package com.cindea.pothub.authentication;

public interface ConfirmSignupContract {

    interface View {

        void successSignUp();
        void displayError(String message);

    }

    interface Presenter {

        void signUpClicked(String username, String password);

    }

    interface Model {

        interface OnFinishListener {

            void onSuccess(String message);
            void onError(String message);

        }

        void confirmSignUp(String username, String code, OnFinishListener listener);

    }

}
