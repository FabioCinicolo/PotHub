package com.cindea.pothub.authentication.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.cindea.pothub.AuthSwitcher;
import com.cindea.pothub.R;
import com.cindea.pothub.authentication.SigninContract;
import com.cindea.pothub.authentication.models.SigninModel;
import com.cindea.pothub.authentication.presenters.SigninPresenter;
import com.cindea.pothub.authentication.views.ResetCredentialsActivity;
import com.cindea.pothub.home.views.HomeActivity;
import com.google.gson.Gson;

public final class SigninFragment extends CustomAuthFragment implements SigninContract.View {

    public static String username;
    private Button button_signup;
    private Button button_signin;
    private SigninContract.Presenter presenter;
    private AuthSwitcher authSwitcher;

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

        presenter = new SigninPresenter(this, new SigninModel());
        authSwitcher = (AuthSwitcher) getActivity();
        setupAnimations(getContext());

    }

    private void customListeners() {

        button_signin.setOnClickListener(view -> {

            runButtonAnimation(button_signin);

            username = ((EditText) fragmentView.findViewById(R.id.fragmentSignin_username)).getText().toString();
            String password = ((EditText) fragmentView.findViewById(R.id.fragmentSignin_password)).getText().toString();

            presenter.cognitoSignInButtonClicked(username, password);

        });

        button_signup.setOnClickListener(view -> {

            runButtonAnimation(button_signup);
            button_handler.postDelayed(() -> authSwitcher.onSignupPress(), 170);

        });

        ((TextView) fragmentView.findViewById(R.id.fragmentSignin_forgotpassword)).setOnClickListener(view -> {

            getActivity().startActivity(new Intent(getActivity(), ResetCredentialsActivity.class));

        });

    }


    @Override
    public void signInCompleted(CognitoUserSession session) {

        SharedPreferences pref = getActivity().getSharedPreferences("Cognito", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(session);
        editor.putString("CognitoUserSession", json);
        editor.commit();

        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void displayError(String message) {

        getActivity().runOnUiThread(() -> {

            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

        });

    }
}