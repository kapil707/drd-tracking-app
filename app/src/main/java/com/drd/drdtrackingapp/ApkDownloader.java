package com.drd.drdtrackingapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class ApkDownloader {

    private Context context;
    private long downloadId;

    public ApkDownloader(Context context) {
        this.context = context;
    }

    public void downloadAndInstallApk(String apkUrl, String title, String description) {
        // Create a download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle(title);
        request.setDescription(description);

        // Set the destination directory for the downloaded file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "1.2-drd-master.apk");

        // Enqueue the download
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadId = downloadManager.enqueue(request);
        }

        // Register a BroadcastReceiver to listen for download completion
        context.registerReceiver(new DownloadReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (receivedDownloadId == downloadId) {
                // The download is complete, install the APK
                installApk(receivedDownloadId);
            }
        }
    }

    private void installApk(long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        Cursor cursor = null;

        try {
            cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String downloadPath = cursor.getString(columnIndex);

                // Check if PackageInstaller is available (Android 21 and higher)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    installApkUsingPackageInstaller(downloadPath);
                } else {
                    // Use alternative installation method for lower versions
                    installApkUsingIntent(downloadPath);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void installApkUsingPackageInstaller(String downloadPath) {
        try {
            File apkFile = new File(Uri.parse(downloadPath).getPath());

            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            Uri apkUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", apkFile);
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                apkUri = Uri.fromFile(apkFile);
            }

            installIntent.setData(apkUri);
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(installIntent);
        } catch (Exception e) {
            Log.e("InstallApk", "Error installing APK1: " + e.getMessage());
            Toast.makeText(context, "Error installing APK "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void installApkUsingIntent(String downloadPath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(downloadPath), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("InstallApk", "Error installing APK2: " + e.getMessage());
            Toast.makeText(context, "Error installing APK" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
