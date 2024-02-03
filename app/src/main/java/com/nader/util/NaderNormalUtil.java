package com.nader.util;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import io.github.rosemoe.sora.text.Content;

public class NaderNormalUtil {
    Context context;

    public NaderNormalUtil(Context c){
        this.context =c;
    }
    public void showToast(String s){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
    public static void copyFile(File src, File dst) throws IOException {
        Path sourcePath = src.toPath();
        Path destinationPath = dst.toPath();
        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
