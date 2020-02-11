package com.videodownloader.whatsappstatussaver.whatsapp_feature;

import android.app.Activity;
import android.graphics.Bitmap;

public class WhatsappStatusItem extends Activity
{
    String str_path;
    Bitmap str_thumb;
    String format;
    boolean boolean_selected;

    public String getFormat() { return format; }

    public void setFormat(String format) { this.format = format; }

    public String getStr_path() { return str_path; }

    public void setStr_path(String str_path) { this.str_path = str_path; }

    public Bitmap getStr_thumb() { return str_thumb; }

    public void setStr_thumb(Bitmap str_thumb) { this.str_thumb = str_thumb; }

    public boolean isBoolean_selected() { return boolean_selected; }

    public void setBoolean_selected(boolean boolean_selected) { this.boolean_selected = boolean_selected; }
}