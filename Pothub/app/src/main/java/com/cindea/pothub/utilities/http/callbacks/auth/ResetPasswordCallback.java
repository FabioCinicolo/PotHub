package com.cindea.pothub.utilities.http.callbacks.auth;

import android.app.Activity;

import com.cindea.pothub.utilities.http.callbacks.HTTPCallback;

public class ResetPasswordCallback implements HTTPCallback {

    private Activity activity;

    public ResetPasswordCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void handleStatus200(String response) {

        activity.runOnUiThread(() -> activity.finish());

    }

    @Override
    public void handleStatus400(String response) {

    }

    @Override
    public void handleStatus401(String response) {

    }

    @Override
    public void handleStatus500(String response) {

    }

    @Override
    public void handleRequestException(String message) {

    }


}
