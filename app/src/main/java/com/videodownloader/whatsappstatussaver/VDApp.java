package com.videodownloader.whatsappstatussaver;

import android.app.Application;
import android.content.Intent;

import com.videodownloader.whatsappstatussaver.download_feature.DownloadManager;

import java.util.ArrayList;

public class VDApp extends Application {
    private static VDApp instance;
    private Intent downloadService;
    private MainActivity.OnBackPressedListener onBackPressedListener;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        downloadService = new Intent(getApplicationContext(), DownloadManager.class);
    }

    public Intent getDownloadService() {
        return downloadService;
    }

    public static VDApp getInstance() {
        return instance;
    }

    public MainActivity.OnBackPressedListener getOnBackPressedListener() {
        return onBackPressedListener;
    }

    public void setOnBackPressedListener(MainActivity.OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }
}
