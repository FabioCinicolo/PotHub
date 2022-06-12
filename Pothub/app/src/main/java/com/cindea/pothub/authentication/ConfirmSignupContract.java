package com.cindea.pothub.authentication;

public interface ConfirmSignupContract {

    interface View {

        void successSignUp();

        void displayError(String message);

    }

    interface Presenter {

        void confirmSignUpClicked(String username, String code);

    }

    interface Model {

        void confirmSignUp(String username, String code, OnFinishListener listener);

        interface OnFinishListener {

            void onSuccess(String message);

            void onError(String message);

        }

    }

}
