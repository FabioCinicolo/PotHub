
package com.cindea.pothub.auth_util;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AWSCognitoAuthentication {

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30,TimeUnit.SECONDS)
            .build();

    private final String URL_POST = "https://ugfmaj5dfb.execute-api.eu-central-1.amazonaws.com/dev/user";

    private Activity activity;


    private String request_body;


    public AWSCognitoAuthentication(Activity activity) {
        this.activity = activity;
    }


    public void initiateSignUp(String username, String email, String password) {
        request_body = "{\"username\":" + username + ",\"email\":" + email + ",\"password\":" + password + ",\"action\":\"SIGNUP\"}";
    }

    public void initiateConfirmSignUp(String username, String confirmation_code) {
        request_body = "{\"username\":" + username + ",\"confirmation_code\":" + confirmation_code + ",\"action\":\"CONFIRM\"}";
    }

    public void initiateSignin(String username, String password) {
        request_body = "{\"username\":" + username + ",\"password\":" + password + ",\"action\":\"SIGNIN\"}";
    }

    public void initiateForgotPassword(String username) {
        request_body = "{\"username\":" + username + ",\"action\":\"FORGOT_PWD\"}";
    }

    public void initiateResetPassword(String username, String password, String confirmation_code) {
        request_body = "{\"username\":" + username + ",\"password\":" + password + ",\"confirmation_code\":" + confirmation_code + ",\"action\":\"RESET_PWD\"}";
    }

    public void handleAuthentication(OnSuccessCallback switcher) {

        RequestBody body = RequestBody.create(request_body, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(URL_POST)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity,
                        e.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                int response_code = response.code();
                if (response_code == 200) {
                    activity.runOnUiThread(() -> {

                        //SUCCESS SIGN UP
                        String body1 = null;
                        try {
                            body1 = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(activity,
                                body1,
                                Toast.LENGTH_SHORT).show();
                        switcher.act();
                    });
                } else if (response_code == 400) {
                    String body = response.body().string();
                    activity.runOnUiThread(() -> {
                        //FAILED SIGN UP
                        Toast.makeText(activity,
                                body,
                                Toast.LENGTH_LONG).show();
                    });
                }
                else if (response_code == 500) {
                    String body = response.body().string();
                    activity.runOnUiThread(() -> {
                        //FAILED SIGN UP
                        Toast.makeText(activity,
                                "INTERNAL SERVER ERROR",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

    }

}