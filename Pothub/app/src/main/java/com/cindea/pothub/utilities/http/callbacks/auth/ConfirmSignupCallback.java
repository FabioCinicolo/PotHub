package com.cindea.pothub.utilities.http.callbacks.auth;

import android.app.Activity;
import android.content.Intent;

import com.cindea.pothub.MainActivity;
import com.cindea.pothub.auth.activities.ConfirmSignupActivity;
import com.cindea.pothub.utilities.http.callbacks.HTTPCallback;

public class ConfirmSignupCallback implements HTTPCallback {

    private Activity activity;

    public ConfirmSignupCallback(Activity activity) {

        this.activity = activity;
    }

    @Override
    public void handleStatus200(String response) {

        activity.runOnUiThread(() -> {

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
