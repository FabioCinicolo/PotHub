package com.cindea.pothub.authentication;

import android.content.SharedPreferences;

public interface SigninContract {

    interface View {

        void signInCompleted();
        void displayError(String message);

    }


    interface Presenter {
        void cognitoSignInButtonClicked(String username, String password, SharedPreferences cognito_preferences);
    }

    interface Model {


        interface OnFinishListener {
            void onSuccess();

            void onError(String message);
        }

        void cognitoSignIn(String username, String password, SharedPreferences cognito_preferences, OnFinishListener listener);

    }

}
