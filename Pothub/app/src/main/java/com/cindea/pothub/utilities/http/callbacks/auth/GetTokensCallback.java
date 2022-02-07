package com.cindea.pothub.utilities.http.callbacks.auth;

import android.app.Activity;
import android.content.Intent;

import com.cindea.pothub.auth.activities.ConfirmSignupActivity;
import com.cindea.pothub.home.HomeActivity;
import com.cindea.pothub.utilities.http.callbacks.HTTPCallback;
import com.cindea.pothub.utilities.sharedpreferences.TokenSharedPreferences;


public class GetTokensCallback implements HTTPCallback {

    private Activity activity;
    private String username;

    public GetTokensCallback(Activity activity, String username) {
        this.activity = activity;
        this.username = username;
    }

    @Override
    public void handleStatus200(String response) {

        TokenSharedPreferences tokenSharedPreferences = new TokenSharedPreferences(activity);
        tokenSharedPreferences.insertTokens(response, username);

        activity.runOnUiThread(() -> {

            Intent intent = new Intent(activity, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        });

    }

    @Override
    public void handleStatus400(String response) {
        //TODO: Controllo password (missing o non corretta)
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
