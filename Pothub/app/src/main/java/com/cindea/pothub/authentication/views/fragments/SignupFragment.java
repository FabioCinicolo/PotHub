package com.cindea.pothub.authentication.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cindea.pothub.AuthSwitcher;
import com.cindea.pothub.R;
import com.cindea.pothub.authentication.SignupContract;
import com.cindea.pothub.authentication.models.SignupModel;
import com.cindea.pothub.authentication.presenters.SignupPresenter;
import com.cindea.pothub.authentication.views.ConfirmSignupActivity;

public class SignupFragment extends CustomAuthFragment implements SignupContract.View  {

    private Button button_signup;
    private Button button_signin;
    private String username;
    private SignupContract.Presenter presenter;
    private AuthSwitcher authSwitcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_signup, container, false);

        setupComponents();
        listeners();

        return fragmentView;
    }

    @Override
    protected void setupComponents() {

        button_signup = fragmentView.findViewById(R.id.fragmentSignup_signupbtn);
        button_signin = fragmentView.findViewById(R.id.fragmentSignup_signinbtn);

        presenter = new SignupPresenter(this, new SignupModel());
        authSwitcher = (AuthSwitcher) getActivity();
        setupAnimations(getContext());

    }

    private void listeners() {

        button_signin.setOnClickListener(view -> {

            runButtonAnimation(button_signin);

            button_handler.postDelayed(() -> authSwitcher.onSigninPress(),170);

        });

        button_signup.setOnClickListener(view -> {

            username = ((EditText)fragmentView.findViewById(R.id.fragmentSignup_username)).getText().toString();
            String email = ((EditText)fragmentView.findViewById(R.id.fragmentSignup_mail)).getText().toString();
            String password = ((EditText)fragmentView.findViewById(R.id.fragmentSignup_password)).getText().toString();

            runButtonAnimation(button_signup);

            presenter.signUpButtonPressed(username, email, password);

        });


    }

    @Override
    public void signUpSuccess() {
        Intent intent = new Intent(getActivity(), ConfirmSignupActivity.class);
        intent.putExtra("username", username);
        getActivity().startActivity(intent);
        authSwitcher.onSigninPress();
    }

    @Override
    public void displayError(String message) {
        Log.e("SignUp", message);
    }
}