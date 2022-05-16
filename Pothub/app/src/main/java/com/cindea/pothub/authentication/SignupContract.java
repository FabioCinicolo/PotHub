package com.cindea.pothub.authentication;

public interface SignupContract {

    interface View{
        void signUpSuccess();
        void displayError(String message);
    }

    interface Presenter{
        void signUpButtonPressed(String username, String email, String password);
    }

    interface Model{

        interface OnFinishListener {
            void onSuccess(String message);
            void onError(String message);
        }
        void signUp(String username, String email, String password, OnFinishListener listener);

    }

}
