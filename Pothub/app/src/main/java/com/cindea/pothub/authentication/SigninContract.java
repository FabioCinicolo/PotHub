package com.cindea.pothub.authentication;

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


        void cognitoSignIn(String username, String password, OnFinishListener listener);

        interface OnFinishListener {
            void onSuccess(CognitoUserSession session);

            void onError(String message);
        }

    }

}
