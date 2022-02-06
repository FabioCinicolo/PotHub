package com.cindea.pothub.callbacks.auth;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import com.cindea.pothub.callbacks.HTTPCallback;
import com.cindea.pothub.utilities.auth.AuthenticationHTTP;
import com.cindea.pothub.entities.Tokens;
import com.cindea.pothub.utilities.sharedpreferences.TokenSharedPreferences;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringEscapeUtils;

public class RefreshTokenCallback implements HTTPCallback {

    private Activity activity;

    public RefreshTokenCallback(Activity activity){
        this.activity = activity;
    }
    @Override
    public void handleStatus200(String response) {

        TokenSharedPreferences tokenSharedPreferences = new TokenSharedPreferences(activity);
        tokenSharedPreferences.insertIdAndAccessToken(response);

        AuthenticationHTTP.tokenLogin(tokenSharedPreferences.getIdToken(), new TokenLoginCallback(activity));

    }

    @Override
    public void handleStatus400(String response) {
    }

    @Override
    public void handleStatus401(String response) {
        new TokenSharedPreferences(activity).clear();
    }

    @Override
    public void handleStatus500(String response) {
    }

    @Override
    public void handleRequestException(String message) {
    }

}