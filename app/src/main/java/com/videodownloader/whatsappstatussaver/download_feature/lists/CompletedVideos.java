package com.videodownloader.whatsappstatussaver.download_feature.lists;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompletedVideos implements Serializable {
    private List<String> videos;

    public CompletedVideos() {
        videos = new ArrayList<>();
    }

    public void addVideo(Context context, String name) {
        videos.add(0, name);
        save(context);
    }

    public List<String> getVideos() {
        return videos;
    }

    public static CompletedVideos load(Context context) {
        CompletedVideos completedVideos = new CompletedVideos();
        File file = new File(context.getFilesDir(), "completed.dat");
        Log.d("surabhi", "load: " + context.getFilesDir());
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                completedVideos = (CompletedVideos) objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
        return completedVideos;
    }

    public void save(Context context) {
        try {
            File file = new File(context.getFilesDir(), "completed.dat");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
