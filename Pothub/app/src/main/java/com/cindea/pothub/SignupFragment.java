package com.cindea.pothub;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupFragment extends CustomFragment {

    private AuthCallbackListener authCallbackListener;
    private Button button_signup;
    private Button button_signin;
    private EditText et_username;
    private EditText et_password;
    private EditText et_email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupComponents(view);
        customListeners();

    }

    @Override
    protected void setupComponents(View view) {

        button_signup = view.findViewById(R.id.fragmentSignup_signupbtn);
        button_signin = view.findViewById(R.id.fragmentSignup_signinbtn);
        authCallbackListener = (AuthCallbackListener)getActivity();
        et_username = view.findViewById(R.id.fragmentSignup_username);
        et_password = view.findViewById(R.id.fragmentSignup_password);
        et_email = view.findViewById(R.id.fragmentSignup_mail);
        setupAnimations(getContext());

    }

    private void customListeners() {

        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runButtonAnimation(button_signin);

                button_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        authCallbackListener.onSigninPress();

                    }
                },170);

            }
        });

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runButtonAnimation(button_signup);

                button_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String username = et_username.getText().toString();
                        String password = et_password.getText().toString();
                        String email = et_email.getText().toString();
                        handleSignUp(username, email, password);
                    }
                },170);

            }
        });


    }

    private void handleSignUp(String username, String email, String password) {


        String URL_POST= "https://eagwqm6kz0.execute-api.eu-central-1.amazonaws.com/dev/user";

        OkHttpClient client = new OkHttpClient();

        String json = "{\"username\":"+username+",\"email\":"+email+",\"password\":"+password+",\"action\":\"SIGNUP\"}";


        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(URL_POST)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        //INSERIRE QUI INTENT CONFIRMATION CODE ACTIVITY
                        Toast.makeText(getActivity(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                int response_code = response.code();
                if(response_code == 200)
                {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            //SUCCESS SIGN UP
                            Toast.makeText(getActivity(),
                                    "SUCCESS",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(response_code == 400){
                    String body = response.body().string();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //FAILED SIGN UP
                            Toast.makeText(getActivity(),
                                    body,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

}