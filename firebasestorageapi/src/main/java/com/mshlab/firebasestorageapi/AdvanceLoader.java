package com.mshlab.firebasestorageapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AdvanceLoader {


    private final Activity visibleActivity;
    private final String loadingMessage;
    private ProgressDialog progressDialog;


    public AdvanceLoader(Activity visibleActivity, String loadingMessage) {
        this.visibleActivity = visibleActivity;
        this.loadingMessage = loadingMessage;
        this.progressDialog = new ProgressDialog(visibleActivity);
    }


    public void updateProgress(double val, String title, String msg) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setProgress((int) val);
    }


    public void show() {
        showProgress(null);

    }

    public void show( DialogInterface.OnClickListener onCancelClicked) {
        showProgress(onCancelClicked);

    }

    private void showProgress(DialogInterface.OnClickListener onCancelClicked) {
        this.progressDialog = new ProgressDialog(visibleActivity);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(loadingMessage);
        progressDialog.setMessage("");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100); // Progress Dialog Max Value

        if (onCancelClicked != null) {
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", onCancelClicked);

        }
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        progressDialog.show();

    }

    public void hide() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    public void showSimple() {
        this.progressDialog = new ProgressDialog(visibleActivity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(loadingMessage);
        progressDialog.show();

    }


}
