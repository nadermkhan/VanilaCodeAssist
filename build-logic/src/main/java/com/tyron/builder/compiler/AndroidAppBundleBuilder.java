package com.tyron.builder.compiler;

import com.tyron.builder.compiler.aab.AabTask;
import com.tyron.builder.compiler.aab.ExtractApksTask;
import com.tyron.builder.compiler.apk.SignTask;
import com.tyron.builder.compiler.apk.ZipAlignTask;
import com.tyron.builder.compiler.buildconfig.GenerateDebugBuildConfigTask;
import com.tyron.builder.compiler.buildconfig.GenerateReleaseBuildConfigTask;
import com.tyron.builder.compiler.dex.R8Task;
import com.tyron.builder.compiler.firebase.GenerateFirebaseConfigTask;
import com.tyron.builder.compiler.incremental.dex.IncrementalD8Task;
import com.tyron.builder.compiler.incremental.java.IncrementalJavaTask;
import com.tyron.builder.compiler.incremental.kotlin.IncrementalKotlinCompiler;
import com.tyron.builder.compiler.incremental.resource.IncrementalAapt2Task;
import com.tyron.builder.compiler.incremental.resource.IncrementalAssembleLibraryTask;
import com.tyron.builder.compiler.java.CheckLibrariesTask;
import com.tyron.builder.compiler.log.InjectLoggerTask;
import com.tyron.builder.compiler.manifest.ManifestMergeTask;
import com.tyron.builder.compiler.symbol.MergeSymbolsTask;
import com.tyron.builder.compiler.viewbinding.GenerateViewBindingTask;
import com.tyron.builder.crashlytics.CrashlyticsTask;
import com.tyron.builder.log.ILogger;
import com.tyron.builder.project.Project;
import com.tyron.builder.project.api.AndroidModule;
import java.util.ArrayList;
import java.util.List;

public class AndroidAppBundleBuilder extends BuilderImpl<AndroidModule> {

  public AndroidAppBundleBuilder(Project project, AndroidModule module, ILogger logger) {
    super(project, module, logger);
  }

  @Override
  public List<Task<? super AndroidModule>> getTasks(BuildType type) {
    List<Task<? super AndroidModule>> tasks = new ArrayList<>();
    tasks.add(new CleanTask(getProject(), getModule(), getLogger()));
    tasks.add(new CheckLibrariesTask(getProject(), getModule(), getLogger()));
    tasks.add(new IncrementalAssembleLibraryTask(getProject(), getModule(), getLogger()));
    tasks.add(new ManifestMergeTask(getProject(), getModule(), getLogger()));
    if (type == BuildType.DEBUG) {
      tasks.add(new GenerateDebugBuildConfigTask(getProject(), getModule(), getLogger()));
    } else {
      tasks.add(new GenerateReleaseBuildConfigTask(getProject(), getModule(), getLogger()));
    }
    tasks.add(new GenerateFirebaseConfigTask(getProject(), getModule(), getLogger()));
    if (type == BuildType.DEBUG) {
      tasks.add(new InjectLoggerTask(getProject(), getModule(), getLogger()));
    }
    tasks.add(new CrashlyticsTask(getProject(), getModule(), getLogger()));
    tasks.add(new IncrementalAapt2Task(getProject(), getModule(), getLogger(), true));
    tasks.add(new GenerateViewBindingTask(getProject(), getModule(), getLogger(), true));
    tasks.add(new MergeSymbolsTask(getProject(), getModule(), getLogger()));
    tasks.add(new IncrementalKotlinCompiler(getProject(), getModule(), getLogger()));
    tasks.add(new IncrementalJavaTask(getProject(), getModule(), getLogger()));
    if (getModule().getMinifyEnabled() && type == BuildType.RELEASE) {
      tasks.add(new R8Task(getProject(), getModule(), getLogger()));
    } else {
      tasks.add(new IncrementalD8Task(getProject(), getModule(), getLogger()));
    }
    tasks.add(new AabTask(getProject(), getModule(), getLogger()));
    if (getModule().getZipAlignEnabled()) {
      tasks.add(new ZipAlignTask(getProject(), getModule(), getLogger()));
    }
    tasks.add(new SignTask(getProject(), getModule(), getLogger()));
    tasks.add(new ExtractApksTask(getProject(), getModule(), getLogger()));
    return tasks;
  }
}
