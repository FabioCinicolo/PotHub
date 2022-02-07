package com.cindea.pothub.utilities.http.callbacks.auth;

import android.app.Activity;
import android.content.Intent;

import com.cindea.pothub.MainActivity;
import com.cindea.pothub.home.HomeActivity;
import com.cindea.pothub.utilities.http.AuthenticationHTTP;
import com.cindea.pothub.utilities.http.callbacks.HTTPCallback;
import com.cindea.pothub.utilities.sharedpreferences.TokenSharedPreferences;

public class TokenLoginCallback implements HTTPCallback {

    private Activity activity;

    public TokenLoginCallback(Activity activity){
        this.activity = activity;
    }
    @Override
    public void handleStatus200(String response) {
        activity.runOnUiThread(() -> {

            Intent intent = new Intent(activity, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        });
    }

    @Override
    public void handleStatus400(String response) {

    }

    @Override
    public void handleStatus401(String response) {

        //Se il token è scaduto allora uso il refresh token per ottenere un nuovo id token
        TokenSharedPreferences tokenSharedPreferences = new TokenSharedPreferences(activity);

        if (response.contains("expired")) {
            new AuthenticationHTTP().refreshToken(tokenSharedPreferences.getUsername(), tokenSharedPreferences.getRefreshToken(), new RefreshTokenCallback(activity));
        }else {

            tokenSharedPreferences.clear();
            activity.runOnUiThread(() -> {

                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);

            });

        }

    }

    @Override
    public void handleStatus500(String response) {

    }

    @Override
    public void handleRequestException(String message) {

    }

}
