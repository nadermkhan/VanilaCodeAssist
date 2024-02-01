package com.tyron.builder.compiler.aab;

import static com.android.sdklib.build.ApkBuilder.checkFileForPackaging;
import static com.android.sdklib.build.ApkBuilder.checkFolderForPackaging;

import android.util.Log;
import com.android.sdklib.build.DuplicateFileException;
import com.android.sdklib.internal.build.SignedJarBuilder;
import com.tyron.builder.compiler.BuildType;
import com.tyron.builder.compiler.BundleTool;
import com.tyron.builder.compiler.Task;
import com.tyron.builder.compiler.manifest.SdkConstants;
import com.tyron.builder.exception.CompilationFailedException;
import com.tyron.builder.log.ILogger;
import com.tyron.builder.project.Project;
import com.tyron.builder.project.api.AndroidModule;
import com.tyron.common.util.BinaryExecutor;
import com.tyron.common.util.Decompress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import org.apache.commons.io.FileUtils;

public class AabTask extends Task<AndroidModule> {

  public AabTask(Project project, AndroidModule module, ILogger logger) {
    super(project, module, logger);
  }

  private static final String TAG = "bundleRelease";

  private File mBinDir;
  private File base;
  private File manifest;

  private final JavaAndNativeResourceFilter mFilter = new JavaAndNativeResourceFilter();
  private final HashMap<String, File> mAddedFiles = new HashMap<>();

  @Override
  public String getName() {
    return TAG;
  }

  private File mInputApk;
  private File mOutputApk;
  private File mOutputApks;

  @Override
  public void prepare(BuildType type) throws IOException {
    mBinDir = new File(getModule().getBuildDirectory(), "/bin");
    base = new File(mBinDir.getAbsolutePath(), "/base");

    if (!base.exists() && !base.mkdirs()) {
      throw new IOException("Failed to create resource output directory");
    }

    manifest = new File(mBinDir.getAbsolutePath(), "/base/manifest");
    if (!manifest.exists() && !manifest.mkdirs()) {
      throw new IOException("Failed to create resource output directory");
    }

    File dex = new File(mBinDir.getAbsolutePath(), "/base/dex");
    if (!dex.exists() && !dex.mkdirs()) {
      throw new IOException("Failed to create resource output directory");
    }
    mInputApk = new File(mBinDir.getAbsolutePath() + "/app-module.zip");
    mOutputApk = new File(mBinDir.getAbsolutePath() + "/app-module.aab");
    mOutputApks = new File(mBinDir.getAbsolutePath() + "/app.apks");

    mAddedFiles.clear();
  }

  public void run() throws IOException, CompilationFailedException {
    try {
      unZip();
      copyResources();
      copyManifest();
      copyJni();
      copyDexFiles();
      baseZip();
      copyLibraries();
      aab();
    } catch (SignedJarBuilder.IZipEntryFilter.ZipAbortException e) {
      String message = e.getMessage();
      if (e instanceof DuplicateFileException) {
        DuplicateFileException duplicateFileException = (DuplicateFileException) e;
        message +=
            "\n"
                + "file 1: "
                + duplicateFileException.getFile1()
                + "\n"
                + "file 2: "
                + duplicateFileException.getFile2()
                + "\n"
                + "path: "
                + duplicateFileException.getArchivePath();
      }
      throw new CompilationFailedException(message);
    }
  }

  @Override
  protected void clean() {
    try {
      FileUtils.deleteDirectory(base);
    } catch (IOException ignore) {

    }
  }

  private void aab() throws CompilationFailedException {
    Log.d(TAG, "Generating AAB.");
    boolean uncompressed = false;
    BundleTool signer = new BundleTool(mInputApk.getAbsolutePath(), mOutputApk.getAbsolutePath());
    try {

      if (getModule().getUseLegacyPackaging()) {
        uncompressed = true;
        signer.aab(uncompressed);
      } else {
        uncompressed = false;
        signer.aab(uncompressed);
      }
    } catch (Exception e) {
      throw new CompilationFailedException(e);
    }
  }

  private void baseZip() throws IOException {
    Log.d(TAG, "Creating Module Archive");
    String folderToZip = base.getAbsolutePath();
    String zipName = mBinDir.getAbsolutePath() + "/app-module.zip";
    zipFolder(Paths.get(folderToZip), Paths.get(zipName));
  }

  // Uses java.util.zip to create zip file
  private void zipFolder(final Path sourceFolderPath, Path zipPath) throws IOException {
    final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
    Files.walkFileTree(
        sourceFolderPath,
        new SimpleFileVisitor<Path>() {
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
            Files.copy(file, zos);
            zos.closeEntry();
            return FileVisitResult.CONTINUE;
          }
        });
    zos.close();
  }

  private void copyManifest() throws CompilationFailedException {
    Log.d(TAG, "Copying Manifest.");

    List<String> args = new ArrayList<>();
    args.add("mv");
    args.add(base.getAbsolutePath() + "/AndroidManifest.xml");
    args.add(manifest.getAbsolutePath() + "");
    BinaryExecutor executor = new BinaryExecutor();
    executor.setCommands(args);
    if (!executor.execute().isEmpty()) {
      throw new CompilationFailedException(executor.getLog());
    }
  }

  private void copyDexFiles() throws IOException {
    File[] dexFiles = mBinDir.listFiles(c -> c.isFile() && c.getName().endsWith(".dex"));

    File dexOutput = new File(mBinDir, "base/dex");
    if (dexFiles != null) {
      for (File dexFile : dexFiles) {
        FileUtils.copyFileToDirectory(dexFile, dexOutput);
      }
    }
  }

  private void copyJni() throws IOException {
    Log.d(TAG, "Coping JniLibs.");
    String fromDirectory = getModule().getNativeLibrariesDirectory().getAbsolutePath();
    String toToDirectory = base.getAbsolutePath() + "/lib";
    copyDirectoryFileVisitor(fromDirectory, toToDirectory);

    List<File> libraries = getModule().getLibraries();
    for (File library : libraries) {
      File parent = library.getParentFile();
      if (parent == null) {
        continue;
      }

      File jniDir = new File(parent, "jni");
      if (jniDir.exists()) {
        fromDirectory = jniDir.getAbsolutePath();
        copyDirectoryFileVisitor(fromDirectory, toToDirectory);
      }
    }
  }

  private void copyResources()
      throws IOException, SignedJarBuilder.IZipEntryFilter.ZipAbortException {
    File resourcesDir = getModule().getResourcesDir();
    File[] files = resourcesDir.listFiles();
    if (files != null) {
      for (File file : files) {
        copyResources(file, "root");
      }
    }
  }

  private void copyResources(File file, String path) throws IOException {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File child : files) {
          copyResources(child, path + "/" + file.getName());
        }
      }
    } else {
      File directory = new File(base, path);
      if (!directory.exists() && !directory.mkdirs()) {
        throw new IOException("Failed to create directory " + directory);
      }

      FileUtils.copyFileToDirectory(file, directory);
    }
  }

  private void copyLibraries()
      throws IOException, SignedJarBuilder.IZipEntryFilter.ZipAbortException {
    List<File> libraryJars = getModule().getLibraries();
    File originalFile = new File(mBinDir, "app-module.zip");
    try (ZipFile zipFile = new ZipFile(originalFile)) {
      for (File libraryJar : libraryJars) {
        if (!libraryJar.exists()) {
          continue;
        }

        mFilter.reset(libraryJar);
        try (JarFile jarFile = new JarFile(libraryJar)) {
          Enumeration<JarEntry> entries = jarFile.entries();
          jar:
          while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            String name = "root/" + jarEntry.getName();
            String[] names = name.split("/");
            if (names.length == 0) {
              continue;
            }
            for (String s : names) {
              boolean checkFolder = checkFolderForPackaging(s);
              if (!checkFolder) {
                continue jar;
              }
            }

            if (jarEntry.isDirectory() || jarEntry.getName().startsWith("META-INF")) {
              continue;
            }

            boolean b = mFilter.checkEntry(name);
            if (!b) {
              continue;
            }
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setFileNameInZip(name);
            zipParameters.setCompressionLevel(CompressionLevel.FASTEST);
            zipFile.addStream(inputStream, zipParameters);
          }
        } catch (ZipException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void copyDirectoryFileVisitor(String source, String target) throws IOException {
    TreeCopyFileVisitor fileVisitor = new TreeCopyFileVisitor(source, target);
    Files.walkFileTree(Paths.get(source), fileVisitor);
  }

  private void unZip() {
    Log.d(TAG, "Unzipping proto format.");
    String zipFilePath = mBinDir.getAbsolutePath() + "/proto-format.zip";
    String destDir = base.getAbsolutePath() + "";
    Decompress.unzip(zipFilePath, destDir);
  }

  /**
   * Custom {@link SignedJarBuilder.IZipEntryFilter} to filter out everything that is not a standard
   * java resources, and also record whether the zip file contains native libraries.
   *
   * <p>Used in {@link SignedJarBuilder#writeZip(java.io.InputStream,
   * SignedJarBuilder.IZipEntryFilter)} when we only want the java resources from external jars.
   */
  private final class JavaAndNativeResourceFilter implements SignedJarBuilder.IZipEntryFilter {
    private final List<String> mNativeLibs = new ArrayList<String>();
    private boolean mNativeLibsConflict = false;
    private File mInputFile;

    @Override
    public boolean checkEntry(String archivePath) throws ZipAbortException {
      // split the path into segments.
      String[] segments = archivePath.split("/");

      // empty path? skip to next entry.
      if (segments.length == 0) {
        return false;
      }

      // Check each folders to make sure they should be included.
      // Folders like CVS, .svn, etc.. should already have been excluded from the
      // jar file, but we need to exclude some other folder (like /META-INF) so
      // we check anyway.
      for (int i = 0; i < segments.length - 1; i++) {
        if (!checkFolderForPackaging(segments[i])) {
          return false;
        }
      }

      // get the file name from the path
      String fileName = segments[segments.length - 1];

      boolean check = checkFileForPackaging(fileName);

      // only do additional checks if the file passes the default checks.
      if (check) {
        File duplicate = checkFileForDuplicate(archivePath);
        if (duplicate != null) {
          throw new DuplicateFileException(archivePath, duplicate, mInputFile);
        } else {
          mAddedFiles.put(archivePath, mInputFile);
        }

        if (archivePath.endsWith(".so") || archivePath.endsWith(".bc")) {
          mNativeLibs.add(archivePath);

          // only .so located in lib/ will interfere with the installation
          if (archivePath.startsWith(SdkConstants.FD_APK_NATIVE_LIBS + "/")) {
            mNativeLibsConflict = true;
          }
        } else if (archivePath.endsWith(".jnilib")) {
          mNativeLibs.add(archivePath);
        }
      }

      return check;
    }

    List<String> getNativeLibs() {
      return mNativeLibs;
    }

    boolean getNativeLibsConflict() {
      return mNativeLibsConflict;
    }

    void reset(File inputFile) {
      mInputFile = inputFile;
      mNativeLibs.clear();
      mNativeLibsConflict = false;
    }
  }

  private File checkFileForDuplicate(String archivePath) {
    return mAddedFiles.get(archivePath);
  }
}
