package com.videodownloader.whatsappstatussaver.whatsapp_feature;

import android.app.Activity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.videodownloader.whatsappstatussaver.R;


public class WhatsappStatusView extends Activity {

    String path;
    VideoView vv_video;
    String format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatsapp_video_view);
        vv_video = findViewById(R.id.vv_video);
        init();
    }

    private void init() {
        format = getIntent().getStringExtra("format");
        path = getIntent().getStringExtra("path");
        if (format.equals(".mp4")) {
            vv_video.setVideoPath(path);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(vv_video);
            vv_video.setMediaController(mediaController);
            vv_video.start();
        }

    }
}
