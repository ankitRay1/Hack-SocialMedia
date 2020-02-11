package com.videodownloader.whatsappstatussaver;

import android.util.Patterns;
import android.widget.EditText;

public class WebConnect {
    private EditText textBox;
    private MainActivity activity;

    public WebConnect(EditText textBox, MainActivity activity) {
        this.textBox = textBox;
        this.activity = activity;
    }

    public void connect() {
        String text = textBox.getText().toString();
        if (Patterns.WEB_URL.matcher(text).matches()) {
            if (!text.startsWith("http")) {
                text = "http://" + text;
            }
            activity.getBrowserManager().newWindow(text);
        } else {
            text = "https://google.com/search?q=" + text;
            activity.getBrowserManager().newWindow(text);
        }
    }
}
