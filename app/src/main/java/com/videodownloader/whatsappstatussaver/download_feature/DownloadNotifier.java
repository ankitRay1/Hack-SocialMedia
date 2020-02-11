package com.videodownloader.whatsappstatussaver.download_feature;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.videodownloader.whatsappstatussaver.R;
import com.videodownloader.whatsappstatussaver.VDApp;

import java.io.File;

public class DownloadNotifier {
    private final int ID = 77777;
    private Intent downloadServiceIntent;
    private Handler handler;
    private NotificationManager notificationManager;
    private DownloadingRunnable downloadingRunnable;
    private boolean sound;
    private boolean vibrate;

    private class DownloadingRunnable implements Runnable {
        @Override
        public void run() {
            String filename = downloadServiceIntent.getStringExtra("name") + "." +
                    downloadServiceIntent.getStringExtra("type");
            Notification.Builder NB;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NB = new Notification.Builder(VDApp.getInstance().getApplicationContext(), "download_01")
                        .setStyle(new Notification.BigTextStyle());
            } else {
                NB = new Notification.Builder(VDApp.getInstance().getApplicationContext());
            }
            NB.setContentTitle("Downloading " + filename)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(VDApp.getInstance()
                            .getApplicationContext().getResources(), R.mipmap.ic_launcher_round))
                    .setOngoing(true);
            if (downloadServiceIntent.getBooleanExtra("chunked", false)) {
                File file = new File(Environment.getExternalStoragePublicDirectory(VDApp.getInstance()
                        .getApplicationContext().getString(R.string.app_name)), filename);
                String downloaded;
                if (file.exists()) {
                    downloaded = android.text.format.Formatter.formatFileSize(VDApp.getInstance
                            ().getApplicationContext(), file.length());
                } else {
                    downloaded = "0KB";
                }
                NB.setProgress(100, 0, true)
                        .setContentText(downloaded);
                notificationManager.notify(ID, NB.build());
                handler.postDelayed(this, 1000);
            } else {
                File file = new File(Environment.getExternalStoragePublicDirectory(VDApp.getInstance()
                        .getApplicationContext().getString(R.string.app_name)), filename);
                String sizeString = downloadServiceIntent.getStringExtra("size");
                int progress = (int) Math.ceil(((double) file.length() / (double) Long.parseLong
                        (sizeString)) * 100);
                progress = progress >= 100 ? 100 : progress;
                String downloaded = android.text.format.Formatter.formatFileSize(VDApp
                        .getInstance().getApplicationContext(), file.length());
                String total = android.text.format.Formatter.formatFileSize(VDApp.getInstance()
                        .getApplicationContext(), Long.parseLong
                        (sizeString));
                NB.setProgress(100, progress, false)
                        .setContentText(downloaded + "/" + total + "   " + progress + "%");
                notificationManager.notify(ID, NB.build());
                handler.postDelayed(this, 1000);
            }
        }
    }

    DownloadNotifier(Intent downloadServiceIntent) {
        SharedPreferences pref = VDApp.getInstance().getSharedPreferences("settings",0);
        this.sound = pref.getBoolean(VDApp.getInstance().getApplicationContext().getString(R.string.soundON),true);
        this.vibrate = pref.getBoolean(VDApp.getInstance().getApplicationContext().getString(R.string.vibrateON),true);
        this.downloadServiceIntent = downloadServiceIntent;
        notificationManager = (NotificationManager) VDApp.getInstance().getApplicationContext().getSystemService
                (Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("download_01",
                    "Download Notification", NotificationManager.IMPORTANCE_LOW));
            if(sound && vibrate){
                NotificationChannel notificationChannel = new NotificationChannel("download_02",
                        "Download Notification", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            else if(sound && !vibrate){
                NotificationChannel notificationChannel = new NotificationChannel("download_03",
                        "Download Notification", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            else if(!sound && vibrate){
                NotificationChannel notificationChannel = new NotificationChannel("download_04",
                        "Download Notification", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            else if(!sound && !vibrate){
                NotificationChannel notificationChannel = new NotificationChannel("download_05",
                        "Download Notification", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }
        HandlerThread thread = new HandlerThread("downloadNotificationThread");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    void notifyDownloading() {
        downloadingRunnable = new DownloadingRunnable();
        downloadingRunnable.run();
    }

    void notifyDownloadFinished() {

        handler.removeCallbacks(downloadingRunnable);
        notificationManager.cancel(ID);
        String filename = downloadServiceIntent.getStringExtra("name") + "." +
                downloadServiceIntent.getStringExtra("type");
        Notification.Builder NB;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_id = "download_02";
            if(sound && vibrate){
                channel_id = "download_02";
            }
            else if(sound && !vibrate){
                channel_id = "download_03";
            }
            else if(!sound && vibrate){
                channel_id = "download_04";
            }
            else if(!sound && !vibrate){
                channel_id = "download_05";
            }
            NB = new Notification.Builder(VDApp.getInstance().getApplicationContext(), channel_id)
                    .setTimeoutAfter(1500)
                    .setContentTitle("Download Finished")
                    .setContentText(filename)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(VDApp.getInstance().getApplicationContext().getResources(),
                            R.mipmap.ic_launcher_round));

            notificationManager.notify(8888, NB.build());

        } else {
            NB = new Notification.Builder(VDApp.getInstance().getApplicationContext())
                    .setTicker("Download Finished")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(VDApp.getInstance().getApplicationContext().getResources(),
                            R.mipmap.ic_launcher_round));
            if(sound)
                NB.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            else
                NB.setSound(null);

            if(vibrate)
                NB.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000});
            else
                NB.setVibrate(new long[] { 0, 0, 0, 0, 0});

            notificationManager.notify(8888, NB.build());
            notificationManager.cancel(8888);
        }
    }

    void cancel() {
        if (downloadingRunnable != null) {
            handler.removeCallbacks(downloadingRunnable);
        }
        notificationManager.cancel(ID);
    }
}
