package com.cindea.pothub.utilities.sharedpreferences;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.cindea.pothub.entities.Tokens;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringEscapeUtils;

public final class TokenSharedPreferences {

    private Context context;
    private SharedPreferences user_details;
    private Gson gson;
    private Tokens tokens;

    public TokenSharedPreferences(Context context) {
        this.context = context;
        user_details = context.getSharedPreferences("natour_tokens", MODE_PRIVATE);
        gson = new Gson();
    }

    public final void insertTokens(String response, String username){

        tokens = gson.fromJson(removeQuotesAndUnescape(response), Tokens.class);

        SharedPreferences.Editor editor = user_details.edit();

        editor.putString("id_token", tokens.getId_token());
        editor.putString("refresh_token", tokens.getRefresh_token());
        editor.putString("access_token", tokens.getAccess_token());
        editor.putString("username", username);

        editor.commit();

    }

    public final void insertIdAndAccessToken(String response){

        tokens = gson.fromJson(removeQuotesAndUnescape(response), Tokens.class);

        SharedPreferences.Editor editor = user_details.edit();

        editor.putString("id_token", tokens.getId_token());

        editor.putString("access_token", tokens.getAccess_token());

        editor.commit();
    }

    public final String removeQuotesAndUnescape(String uncleanJson) {
        String noQuotes = uncleanJson.replaceAll("^\"|\"$", "");

        return StringEscapeUtils.unescapeJava(noQuotes);
    }

    public final String getIdToken() {

       return user_details.getString("id_token",null);

    }

    public final String getRefreshToken() {

        return user_details.getString("refresh_token",null);

    }

    public String getUsername() {
        return user_details.getString("username", null);
    }

    public final void clear() {
        user_details.edit().clear().commit();
    }

    public final boolean isEmpty() {

        return (user_details.getString("id_token", null)==null) ? true : false;

    }

}
