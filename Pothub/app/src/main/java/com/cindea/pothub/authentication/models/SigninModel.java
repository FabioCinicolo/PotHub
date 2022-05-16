package com.cindea.pothub.authentication.models;

import android.content.SharedPreferences;

import com.cindea.pothub.authentication.SigninContract;

public class SigninModel implements SigninContract.Model {
    @Override
    public void cognitoSignIn(String username, String password, SharedPreferences cognito_preferences, OnFinishListener listener) {

    }
}
