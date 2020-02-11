package com.videodownloader.whatsappstatussaver.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class CustomText extends TextView {

    public CustomText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setType(context);
    }

    public CustomText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public CustomText(Context context) {
        super(context);
        setType(context);
    }

    private void setType(Context context){
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),
                "fonts/bree_serif.ttf"));
    }
}
