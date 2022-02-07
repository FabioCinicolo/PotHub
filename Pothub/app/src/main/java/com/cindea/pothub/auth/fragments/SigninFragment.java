package com.cindea.pothub.auth.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cindea.pothub.R;
import com.cindea.pothub.auth.AuthCallbackListener;
import com.cindea.pothub.home.HomeActivity;
import com.cindea.pothub.utilities.http.AuthenticationHTTP;
import com.cindea.pothub.utilities.http.callbacks.auth.GetTokensCallback;

public final class SigninFragment extends CustomAuthFragment {

    private AuthCallbackListener authCallbackListener;
    private Button button_signup;
    private Button button_signin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_signin, container, false);

        setupComponents();
        customListeners();

        return fragmentView;
    }

    @Override
    protected void setupComponents() {

        button_signin = fragmentView.findViewById(R.id.fragmentSignin_signinbtn);
        button_signup = fragmentView.findViewById(R.id.fragmentSignin_signupbtn);

        authCallbackListener = (AuthCallbackListener)getActivity();
        setupAnimations(getContext());

    }

    private void customListeners() {

        button_signin.setOnClickListener(view -> {

            runButtonAnimation(button_signin);

            String username = ((EditText)fragmentView.findViewById(R.id.fragmentSignin_username)).getText().toString();
            String password = ((EditText)fragmentView.findViewById(R.id.fragmentSignin_password)).getText().toString();


            new AuthenticationHTTP().getIdNRefreshTokens(username, password, new GetTokensCallback(getActivity(), username));

        });

        button_signup.setOnClickListener(view -> {

            runButtonAnimation(button_signup);
            button_handler.postDelayed(() -> authCallbackListener.onSignupPress(),170);

        });

        ((TextView)fragmentView.findViewById(R.id.fragmentSignin_forgotpassword)).setOnClickListener(
                view -> authCallbackListener.onResetPassword()
        );

    }


}