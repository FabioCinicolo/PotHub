package com.cindea.pothub.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cindea.pothub.CustomFragment;
import com.cindea.pothub.R;
import com.cindea.pothub.auth.AuthCallbackListener;

public class SignupFragment extends CustomFragment {

    private AuthCallbackListener authCallbackListener;
    private Button button_signup;
    private Button button_signin;
    private Intent intent_confirm;

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
        intent_confirm = new Intent(getActivity(), ConfirmSignupActivity.class);
        intent_confirm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

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

            runButtonAnimation(button_signup);

            button_handler.postDelayed(() -> {

                startActivity(intent_confirm);

            },170);

        });


    }

}