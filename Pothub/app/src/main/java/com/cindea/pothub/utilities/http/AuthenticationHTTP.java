package com.cindea.pothub.utilities.http;


import com.cindea.pothub.utilities.http.callbacks.HTTPCallback;

import okhttp3.Headers;

public class AuthenticationHTTP extends OkHTTPRequest {

    final String URL = "https://ugfmaj5dfb.execute-api.eu-central-1.amazonaws.com/api/";

    public void signUp(String username, String email, String password, HTTPCallback callback) {

        String url = URL + "users";

        String request_body = "{\"username\":" + username + ",\"email\":" + email + ",\"password\":" + password + "}";

        request = getPostRequest(url, request_body, null);

        startHttpRequest(callback);

    }

    public void confirmSignUp(String username, String confirmation_code, HTTPCallback callback) {

        String url = URL + "users/" + username + "/confirmation";

        String request_body = "{\"confirmation_code\":" + confirmation_code + "}";

        request = getPostRequest(url, request_body, null);

        startHttpRequest(callback);
    }

    public void tokenLogin(String id_token, HTTPCallback callback) {

        String url = URL + "auth/tokens/validation";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        request = getGetRequest(url, header);

        startHttpRequest(callback);


    }

    public void getIdNRefreshTokens(String username, String password, HTTPCallback callback) {

        String url = URL + "auth/tokens";

        String request_body = "{\"username\":" + username + ",\"password\":" + password + ",\"grant_type\":\"PASSWORD\"}";

        request = getPostRequest(url, request_body, null);

        startHttpRequest(callback);
    }

    public void refreshToken(String username, String refresh_token, HTTPCallback callback) {

        String url = URL + "auth/tokens";

        String request_body = "{\"username\":" + username + ",\"refresh_token\":" + refresh_token + ",\"grant_type\":\"REFRESH_TOKEN\"}";

        request = getPostRequest(url, request_body, null);

        startHttpRequest(callback);
    }

    public void getCodeForPasswordReset(String username, HTTPCallback callback) {

        String url = URL + "users/" + username + "/password/reset-code";

        request = getGetRequest(url, null);

        startHttpRequest(callback);
    }

    public void resetPassword(String username, String password, String confirmation_code, HTTPCallback callback) {

        String url = URL + "users/" + username + "/password";

        String request_body = "{\"password\":" + password + ",\"confirmation_code\":" + confirmation_code + "}";

        request = getPutRequest(url, request_body);

        startHttpRequest(callback);

    }

    public void changePassword(String username, String old_password, String new_password, String access_token, HTTPCallback callback) {

        String url = URL + "users/" + username + "/password";

        String request_body = "{\"old_password\":" + old_password + ",\"password\":" + new_password + ",\"confirmation_code\":" + null + ",\"access_token\":" + access_token + "}";

        request = getPutRequest(url, request_body);

        startHttpRequest(callback);

    }
}