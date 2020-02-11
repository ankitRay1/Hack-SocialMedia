package com.videodownloader.whatsappstatussaver.browsing_feature;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.videodownloader.whatsappstatussaver.R;
import com.videodownloader.whatsappstatussaver.VDFragment;
import com.videodownloader.whatsappstatussaver.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BrowserManager extends VDFragment {
    private AdBlocker adBlock;
    private List<BrowserWindow> windows;
    private EditText search_bar;
    private List<String> blockedWebsites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d("debug", "Browser Manager added");
        windows = new ArrayList<>();
        File file = new File(getActivity().getFilesDir(), "ad_filters.dat");
        try {
            if (file.exists()) {
                Log.d("debug", "file exists");
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                adBlock = (AdBlocker) objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            } else {
                adBlock = new AdBlocker();
                Log.d("debug", "file not exists");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(adBlock);
                objectOutputStream.close();
                fileOutputStream.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        blockedWebsites = Arrays.asList(getResources().getStringArray(R.array.blocked_sites));
    }

    public void newWindow(String url) {
        if(blockedWebsites.contains(Utils.getBaseDomain(url))){
            new AlertDialog.Builder(getContext())
                    .setMessage("Youtube is not supported according to google policy.")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create()
                    .show();
        }
        else {
            Bundle data = new Bundle();
            data.putString("url", url);
            BrowserWindow window = new BrowserWindow();
            window.setArguments(data);
            getFragmentManager().beginTransaction()
                    .add(R.id.home_content, window, null)
                    .commit();
            windows.add(window);
            getVDActivity().setOnBackPressedListener(window);
            if (windows.size() > 1) {
                window = windows.get(windows.size() - 2);
                if (window != null && window.getView() != null) {
                    window.getView().setVisibility(View.GONE);
                    window.onPause();
                }
            }
        }
    }

    public void closeWindow(BrowserWindow window) {
        final EditText search_bar = getVDActivity().findViewById(R.id.et_search_bar);
        windows.remove(window);
        getFragmentManager().beginTransaction().remove(window).commit();
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow != null && topWindow.getView() != null) {
                topWindow.onResume();
                topWindow.getView().setVisibility(View.VISIBLE);
            }
            search_bar.setText(topWindow.getUrl());
            search_bar.setSelection(search_bar.getText().length());
            getVDActivity().setOnBackPressedListener(topWindow);
        } else {
            search_bar.getText().clear();
            getVDActivity().showToolbar();
            getVDActivity().setOnBackPressedListener(null);
            getVDActivity().loadInterstitialAd();
        }
    }

    public void closeAllWindow() {
        if(windows.size() > 0){
            for (Iterator<BrowserWindow> iterator = windows.iterator(); iterator.hasNext(); ) {
                BrowserWindow window = iterator.next();
                getFragmentManager().beginTransaction().remove(window).commit();
                iterator.remove();
            }
            getVDActivity().setOnBackPressedListener(null);
        }else {
            getVDActivity().setOnBackPressedListener(null);
        }
    }

    public void hideCurrentWindow() {
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow.getView() != null) {
                topWindow.getView().setVisibility(View.GONE);
            }
        }
    }

    public void unhideCurrentWindow() {
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow.getView() != null) {
                topWindow.getView().setVisibility(View.VISIBLE);
                getVDActivity().setOnBackPressedListener(topWindow);
            }
        } else {
            getVDActivity().setOnBackPressedListener(null);
        }
    }

    public void pauseCurrentWindow() {
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow.getView() != null) {
                topWindow.onPause();
            }
        }
    }

    public void resumeCurrentWindow() {
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow.getView() != null) {
                topWindow.onResume();
                getVDActivity().setOnBackPressedListener(topWindow);
            }
        } else {
            getVDActivity().setOnBackPressedListener(null);
        }
    }

    public void updateAdFilters() {
        adBlock.update(getActivity());

    }

    public boolean checkUrlIfAds(String url) {
        return adBlock.checkThroughFilters(url);
    }
}
