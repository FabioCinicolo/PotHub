package com.cindea.pothub.authentication;

public interface ResetCredentialsContract {

    interface View {

        void changeFragmentToResetCRPassword();

        void resetDone(String message);

        void displayUsernameNotFoundError(String message);

        void displayResetError(String message);

    }

    interface Presenter {

        void requestCodeButtonClicked(String username);

        void resetPasswordButtonClicked(String username, String password, String code);

    }

    interface Model {

        void requestCode(String username, Model.OnFinishListener onFinishListener);

        void resetPassword(String username, String password, String code, Model.OnFinishListener onFinishListener);

        interface OnFinishListener {

            void onSuccess(String message);

            void onError(String message);
        }

    }
}
