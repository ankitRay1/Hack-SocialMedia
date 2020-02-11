package com.videodownloader.whatsappstatussaver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.EditText;

public abstract class RenameDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private EditText text;
    private Context context;
    private AlertDialog dialog;

    protected RenameDialog(Context context, String hint) {
        this.context = context;
        text = new EditText(context);
        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        text.setHint(hint);
        dialog = new AlertDialog.Builder(context)
                .setView(text).setMessage("Type new name:")
                .setPositiveButton("OK", this)
                .setNegativeButton("CANCEL", this)
                .create();
        dialog.show();
    }

    @Override
    public final void onClick(DialogInterface dialog, int which) {
        Utils.hideSoftKeyboard((Activity) context, text.getWindowToken());
        if (which == DialogInterface.BUTTON_POSITIVE) {
            onOK(text.getText().toString());
        }
    }

    public abstract void onOK(String newName);

    public boolean isActive() {
        return dialog.isShowing();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
