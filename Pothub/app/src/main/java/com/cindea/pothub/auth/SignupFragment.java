package com.cindea.pothub.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cindea.pothub.CustomFragment;
import com.cindea.pothub.R;
import com.cindea.pothub.auth.AuthCallbackListener;
import com.cindea.pothub.auth_util.AWSCognitoAuthentication;

public class SignupFragment extends CustomFragment {

    private AuthCallbackListener authCallbackListener;
    private Button button_signup;
    private Button button_signin;
    private Intent intent_confirm;
    private EditText edit_user;
    private EditText edit_email;
    private EditText edit_password;

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
        edit_user = view.findViewById(R.id.fragmentSignup_username);
        edit_email = view.findViewById(R.id.fragmentSignup_mail);
        edit_password = view.findViewById(R.id.fragmentSignup_password);
        authCallbackListener = (AuthCallbackListener)getActivity();

        setupAnimations(getContext());

    }

    private void customListeners() {

        button_signin.setOnClickListener(view -> {

            runButtonAnimation(button_signin);

            button_handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    authCallbackListener.onSigninPress();

                }
            },170);

        });

        button_signup.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), ConfirmSignupActivity.class);

            String username = edit_user.getText().toString();
            String email = edit_email.getText().toString();
            String password = edit_password.getText().toString();

            runButtonAnimation(button_signup);

            intent.putExtra("username", username);
            intent.putExtra("email", email);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            AWSCognitoAuthentication auth = new AWSCognitoAuthentication(getActivity());
            auth.initiateSignUp(username, email, password);
            auth.handleAuthentication(() -> {
                runHandledIntent(intent);
            });

        });


    }

}