package com.cindea.pothub.utilities.auth;


import android.util.Log;

import androidx.annotation.NonNull;

import com.cindea.pothub.callbacks.HTTPCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthenticationHTTP {

    //TODO: Inserire URL con modificatore d'accesso FINAL

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static Request request;

    private static void handleAuthentication(HTTPCallback callback) {


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.handleRequestException(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                int response_code = response.code();
                String response_body = response.body().string();
                switch(response_code){
                    case 200 :
                        Log.e("200", response_body);
                        callback.handleStatus200(response_body);
                        break;
                    case 400 :
                        Log.e("400", response_body);
                        callback.handleStatus400(response_body);
                        break;
                    case 401 :
                        Log.e("401", response_body);
                        callback.handleStatus401(response_body);
                        break;
                    case 500 :
                        callback.handleStatus500(response_body);
                        break;
                    default:
                        return;
                }
            }
        });
    }


    public static void signUp(String username, String email, String password, HTTPCallback callback) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users";

        String request_body = "{\"username\":" + username + ",\"email\":" + email + ",\"password\":" + password + "}";

        request = getPostRequest(url, request_body, null);

        handleAuthentication(callback);

    }

    public static void googleSignUp(String username, String email, String id_token, HTTPCallback callback) {


        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/auth/google/users";

        String request_body = "{\"username\":" + username +", \"email\":" + email +"}";

        Headers header = new Headers.Builder().add("Authorization", "\""+id_token+"\"").build();

        request = getPostRequest(url, request_body, header);

        handleAuthentication(callback);

    }

    public static void confirmSignUp(String username, String confirmation_code, HTTPCallback callback) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/confirmation";

        String request_body = "{\"confirmation_code\":" + confirmation_code +"}";

        request = getPostRequest(url, request_body, null);

        handleAuthentication(callback);
    }

    public static void tokenLogin(String id_token, HTTPCallback callback) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/auth/token/validation";

        Headers header = new Headers.Builder().add("Authorization", "\""+id_token+"\"").build();

        request = getGetRequest(url, header);

        handleAuthentication(callback);


    }

    public static void getIdNRefreshTokens(String username, String password, HTTPCallback callback) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/auth/token";

        String request_body = "{\"username\":" + username + ",\"password\":" + password + ",\"grant_type\":\"PASSWORD\"}";

        request = getPostRequest(url, request_body, null);

        handleAuthentication(callback);
    }

    public static void refreshToken(String username, String refresh_token, HTTPCallback callback) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/auth/token";

        String request_body = "{\"username\":" + username + ",\"refresh_token\":"+refresh_token +",\"grant_type\":\"REFRESH_TOKEN\"}";

        request = getPostRequest(url, request_body, null);

        handleAuthentication(callback);
    }

    public static void getCodeForPasswordReset(String username, HTTPCallback callback) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/password/reset-code";

        request = getGetRequest(url, null);

        handleAuthentication(callback);
    }

    public static void resetPassword(String username, String password, String confirmation_code, HTTPCallback callback) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/password";

        String request_body = "{\"password\":" + password + ",\"confirmation_code\":" + confirmation_code + "}";

        request = getPutRequest(url, request_body);

        handleAuthentication(callback);

    }

    public static void changePassword(String username, String old_password, String new_password, String access_token, HTTPCallback callback) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/password";

        String request_body = "{\"old_password\":" + old_password + ",\"password\":" + new_password +",\"confirmation_code\":" + null+ ",\"access_token\":" + access_token + "}";

        request = getPutRequest(url, request_body);

        handleAuthentication(callback);

    }

    public static Request getPostRequest(String url, String request_body, Headers headers) {

        RequestBody body = RequestBody.create(request_body, MediaType.parse("application/json"));

        return headers != null ? new Request.Builder()
                .url(url)
                .post(body).headers(headers)
                .build() : new Request.Builder()
                .url(url)
                .post(body)
                .build();

    }

    public static Request getPutRequest(String url, String request_body) {

        RequestBody body = RequestBody.create(request_body, MediaType.parse("application/json"));

        return new Request.Builder()
                .url(url)
                .put(body)
                .build();

    }

    public static Request getGetRequest(String url, Headers headers) {

        return headers!=null ? new Request.Builder().url(url).headers(headers).build() : new Request.Builder().url(url).build();
    }

}