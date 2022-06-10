package com.cindea.pothub.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;

public interface SigninContract {

    interface View {

        void signInCompleted(CognitoUserSession session);
        void displayError(String message);

    }


    interface Presenter {
        void cognitoSignInButtonClicked(String username, String password);
    }

    interface Model {


        interface OnFinishListener {
            void onSuccess(CognitoUserSession session);

            void onError(String message);
        }

        void cognitoSignIn(String username, String password, OnFinishListener listener);

    }

}
