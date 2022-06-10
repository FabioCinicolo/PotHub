package com.cindea.pothub.authentication.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.cindea.pothub.authentication.SigninContract;

public class SigninPresenter implements SigninContract.Presenter, SigninContract.Model.OnFinishListener
{

    private final SigninContract.View view;
    private final SigninContract.Model model;

    public SigninPresenter(SigninContract.View view, SigninContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void cognitoSignInButtonClicked(String username, String password) {
        model.cognitoSignIn(username, password, this);
    }

    @Override
    public void onSuccess(CognitoUserSession session) {
        view.signInCompleted(session);
    }

    @Override
    public void onError(String message) {
        view.displayError(message);
    }
}
