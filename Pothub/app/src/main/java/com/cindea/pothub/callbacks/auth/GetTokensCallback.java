package com.cindea.pothub.callbacks.auth;

import android.app.Activity;
import android.content.SharedPreferences;

import com.cindea.pothub.callbacks.HTTPCallback;
import com.cindea.pothub.entities.Tokens;
import com.cindea.pothub.utilities.sharedpreferences.TokenSharedPreferences;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringEscapeUtils;


public class GetTokensCallback implements HTTPCallback {

    private Activity activity;

    public GetTokensCallback(Activity activity, String username){
        this.activity = activity;
    }

    @Override
    public void handleStatus200(String response) {

        TokenSharedPreferences tokenSharedPreferences = new TokenSharedPreferences(activity);
        tokenSharedPreferences.insertTokens(response);

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
