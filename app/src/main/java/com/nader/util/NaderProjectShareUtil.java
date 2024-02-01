package com.nader.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class NaderProjectShareUtil {

    public static void shareBackupFile(Context context, File backupFile) {
        if (backupFile != null && backupFile.exists()) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/zip");

            Uri fileUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above, use FileProvider
                fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", backupFile);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                // For Android versions below 10, use Uri.fromFile
                fileUri = Uri.fromFile(backupFile);
            }

            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

            try {
                context.startActivity(Intent.createChooser(shareIntent, "Share Backup File"));
            } catch (Exception e) {
                Log.e("NaderCAProjectBackupUtility", "Error sharing backup file", e);
            }
        } else {
            Log.e("NaderCAProjectBackupUtility", "Backup file is null or doesn't exist");
        }
    }
}