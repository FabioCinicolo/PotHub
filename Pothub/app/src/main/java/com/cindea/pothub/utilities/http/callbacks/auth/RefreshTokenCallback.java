package com.cindea.pothub.utilities.http.callbacks.auth;

import android.app.Activity;
import android.content.Intent;

import com.cindea.pothub.MainActivity;
import com.cindea.pothub.home.HomeActivity;
import com.cindea.pothub.utilities.http.callbacks.HTTPCallback;
import com.cindea.pothub.utilities.http.AuthenticationHTTP;
import com.cindea.pothub.utilities.sharedpreferences.TokenSharedPreferences;

public class RefreshTokenCallback implements HTTPCallback {

    private Activity activity;

    public RefreshTokenCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void handleStatus200(String response) {

        TokenSharedPreferences tokenSharedPreferences = new TokenSharedPreferences(activity);
        tokenSharedPreferences.insertIdAndAccessToken(response);

        new AuthenticationHTTP().tokenLogin(tokenSharedPreferences.getIdToken(), new TokenLoginCallback(activity));

    }

    @Override
    public void handleStatus400(String response) {
    }

    @Override
    public void handleStatus401(String response) {
        new TokenSharedPreferences(activity).clear();

        activity.runOnUiThread(() -> {

            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        });

    }

    @Override
    public void handleStatus500(String response) {
    }

    @Override
    public void handleRequestException(String message) {
    }

}