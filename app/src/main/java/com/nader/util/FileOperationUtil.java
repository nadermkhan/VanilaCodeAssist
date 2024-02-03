package com.nader.util;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileOperationUtil {

    private static final int FILE_PICKER_REQUEST_CODE = 1;

    public static void pickAndCopyFile(Activity activity) {
        // Start the file picker activity
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        activity.startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get the selected file URI
            Uri selectedFileUri = data.getData();

            // Start the copy operation in a separate thread
            new Thread(new CopyFileRunnable(activity, selectedFileUri)).start();
        } else {
            // Handle other results or errors
            Toast.makeText(activity.getApplicationContext(), "Faced a problem",Toast.LENGTH_LONG).show();
        }
    }

    private static class CopyFileRunnable implements Runnable {

        private Activity activity;
        private Uri selectedFileUri;

        CopyFileRunnable(Activity activity, Uri uri) {
            this.activity = activity;
            this.selectedFileUri = uri;
        }

        @Override
        public void run() {
            try {
                // Open an input stream from the selected file
                InputStream inputStream = activity.getContentResolver().openInputStream(selectedFileUri);

                // Create the destination file in the app's data folder
                File destinationFile = new File(activity.getFilesDir(), "copied_file.txt");

                // Open an output stream to the destination file
                OutputStream outputStream = new FileOutputStream(destinationFile);

                // Copy the file content
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Close the streams
                inputStream.close();
                outputStream.close();

                // Log the copied file URL
                Log.d("FileHandler", "File copied to: " + destinationFile.getAbsolutePath());

                // Finish the activity on the UI thread if it's not finishing already
                if (!activity.isFinishing()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            activity.finish();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle errors
            }
        }
    }
}
