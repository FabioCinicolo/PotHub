package com.cindea.pothub.utilities.http.callbacks.auth;

import android.app.Activity;
import android.content.Intent;

import com.cindea.pothub.MainActivity;
import com.cindea.pothub.utilities.http.callbacks.HTTPCallback;
import com.cindea.pothub.utilities.sharedpreferences.TokenSharedPreferences;

public class ChangePasswordCallback implements HTTPCallback {

    Activity activity;

    public ChangePasswordCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void handleStatus200(String response) {

        activity.runOnUiThread(() -> {

            TokenSharedPreferences tokenSharedPreferences = new TokenSharedPreferences(activity);
            tokenSharedPreferences.clear();

            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        });

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