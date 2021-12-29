package com.cindea.pothub;

import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResetCREmailFragment extends CustomFragment {

    private ResetPwdCallbackListener resetPwdCallbackListener;
    private Button button_continue;
    private TextView text_back;
    private EditText et_email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_c_r_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupComponents(view);
        customListeners();

    }

    @Override
    protected void setupComponents(View view) {

        button_continue = view.findViewById(R.id.fragmentCR1_continue);
        text_back = view.findViewById(R.id.fragmentCR1_back);
        resetPwdCallbackListener = (ResetPwdCallbackListener) getActivity();
        et_email = view.findViewById(R.id.fragmentCR1_email);

        setupAnimations(getContext());

    }

    private void customListeners() {

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runButtonAnimation(button_continue);

                button_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        resetPwdCallbackListener.onContinueAfterEmail();

                    }
                },170);

            }
        });

        text_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();

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