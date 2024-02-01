package com.nader.util;

import static java.lang.String.format;

import android.util.Log;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class NaderZipUtil {

    public static File zipFolder(File toZipFolder) {
        File zipFile = new File(toZipFolder.getParent(), format("%s _VanilaCodeAssistProject.zip", toZipFolder.getName()));
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipSubFolder(out, toZipFolder, toZipFolder.getPath().length());
            Log.i("NaderCAProjectBackupUtility", "Backup File Location: " + zipFile.getAbsolutePath());
            return zipFile;
        } catch (IOException ex) {
            // Handle or log the exception appropriately
            ex.printStackTrace();
            return null;
        }
    }

    private static void zipSubFolder(ZipOutputStream out, File folder, int basePathLength) throws IOException {
        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        try {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    zipSubFolder(out, file, basePathLength);
                } else {
                    byte data[] = new byte[BUFFER];

                    String unmodifiedFilePath = file.getPath();
                    String relativePath = unmodifiedFilePath.substring(basePathLength + 1);

                    try (FileInputStream fi = new FileInputStream(unmodifiedFilePath)) {
                        origin = new BufferedInputStream(fi, BUFFER);

                        ZipEntry entry = new ZipEntry(relativePath);
                        entry.setTime(file.lastModified());
                        out.putNextEntry(entry);

                        int count;
                        while ((count = origin.read(data, 0, BUFFER)) != -1) {
                            out.write(data, 0, count);
                        }
                    } finally {
                        if (origin != null) {
                            origin.close();
                        }
                    }
                    out.closeEntry();
                }
            }
        } finally {
            if (origin != null) {
                origin.close();
            }
        }
    }
}