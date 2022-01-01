package com.cindea.pothub.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cindea.pothub.CustomFragment;
import com.cindea.pothub.R;
import com.cindea.pothub.auth.AuthCallbackListener;
import com.cindea.pothub.auth_util.AWSCognitoAuthentication;

public class SigninFragment extends CustomFragment {

    private AuthCallbackListener authCallbackListener;
    private Button button_signup;
    private Button button_signin;
    private TextView text_forgotpwd;
    private EditText edit_user;
    private EditText edit_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupComponents(view);
        customListeners();


    }

    @Override
    protected void setupComponents(View view) {

        button_signup = view.findViewById(R.id.fragmentSignin_signupbtn);
        button_signin = view.findViewById(R.id.fragmentSignin_signinbtn);
        text_forgotpwd = view.findViewById(R.id.fragmentSignin_forgotpassword);
        edit_user = view.findViewById(R.id.fragmentSignin_username);
        edit_password = view.findViewById(R.id.fragmentSignin_password);

        authCallbackListener = (AuthCallbackListener)getActivity();

        setupAnimations(getContext());

    }

    private void customListeners() {

        button_signin.setOnClickListener(view -> {


            runButtonAnimation(button_signin);

            String username = edit_user.getText().toString();
            String password = edit_password.getText().toString();

//            home_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            AWSCognitoAuthentication auth = new AWSCognitoAuthentication(getActivity());
            auth.initiateSignin(username, password);
            auth.handleAuthentication(() -> {
                Toast.makeText(getContext(), "Fatto", Toast.LENGTH_SHORT).show();
            });

        });

        button_signup.setOnClickListener(view -> {

            runButtonAnimation(button_signup);

            button_handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    authCallbackListener.onSignupPress();

                }
            },170);

        });

        text_forgotpwd.setOnClickListener(view -> authCallbackListener.onResetPassword());


    }


}