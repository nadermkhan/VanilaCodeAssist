package com.tyron.builder.project;

import androidx.annotation.NonNull;
import com.google.common.collect.ImmutableList;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.tyron.builder.model.ProjectSettings;
import com.tyron.builder.project.api.Module;
import com.tyron.builder.project.impl.AndroidModuleImpl;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class Project {

  private final Map<String, Module> mModules;
  private final Module mMainModule;
  private final File mRoot;
  private final String mName;

  private final ProjectSettings mSettings;

  private volatile boolean mCompiling;
  private volatile boolean mIndexing;

  MutableGraph<Module> graph = GraphBuilder.directed().allowsSelfLoops(false).build();

  public Project(File root, String name) {
    mRoot = root;
    mName = name;
    mModules = new LinkedHashMap<>();
    mMainModule = new AndroidModuleImpl(new File(mRoot, mName));

    File codeassist = new File(root, ".idea");
    if (!codeassist.exists()) {
      if (!codeassist.mkdirs()) {}
    }
    mSettings = new ProjectSettings(new File(codeassist, "settings.json"));
  }

  public boolean isCompiling() {
    return mCompiling;
  }

  public void setCompiling(boolean compiling) {
    mCompiling = compiling;
  }

  public void setIndexing(boolean indexing) {
    mIndexing = indexing;
  }

  public boolean isIndexing() {
    return mIndexing;
  }

  public void open() throws IOException {
    mSettings.refresh();
    mMainModule.open();
    ;

    graph.addNode(mMainModule);
    addEdges(graph, mMainModule);
    Set<Module> modules = Graphs.reachableNodes(graph, mMainModule);
    for (Module module : modules) {
      module.open();
      File rootFile = module.getRootFile();
      mModules.put(rootFile.getName(), module);
    }
  }

  public void index() throws IOException {
    Set<Module> modules = Graphs.reachableNodes(graph, mMainModule);
    for (Module module : modules) {
      module.clear();
      module.index();
    }
  }

  /**
   * @return All the modules from the main module, order is not guaranteed
   */
  public Collection<Module> getModules() {
    return mModules.values();
  }

  @NonNull
  public Module getMainModule() {
    return mMainModule;
  }

  public File getRootFile() {
    return mRoot;
  }

  public ProjectSettings getSettings() {
    return mSettings;
  }

  public Module getModule(File file) {
    return getMainModule();
  }

  public List<Module> getDependencies(Module module) {
    return ImmutableList.copyOf(mModules.values()).reverse();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Project project = (Project) o;
    return mRoot.equals(project.mRoot);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mRoot);
  }

  public List<Module> getBuildOrder() throws IOException {
    return getDependencies(mMainModule);
  }

  private void addEdges(MutableGraph<Module> graph, Module module) throws IOException {}
}
