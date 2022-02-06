package com.cindea.pothub.callbacks.auth;

import android.app.Activity;

import com.cindea.pothub.callbacks.HTTPCallback;

public class TokenLoginCallback implements HTTPCallback {

    private Activity activity;

    public TokenLoginCallback(Activity activity){
        this.activity = activity;
    }
    @Override
    public void handleStatus200(String response) {

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
