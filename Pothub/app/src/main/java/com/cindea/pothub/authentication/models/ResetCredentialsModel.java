package com.cindea.pothub.authentication.models;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.cindea.pothub.MainActivity;
import com.cindea.pothub.authentication.ResetCredentialsContract;
import com.cindea.pothub.cognito.Cognito;

public class ResetCredentialsModel implements ResetCredentialsContract.Model {

    private Cognito cognito;
    private ForgotPasswordContinuation continuation;

    @Override
    public void requestCode(String username, OnFinishListener onFinishListener) {

        cognito = MainActivity.getCognito();
        CognitoUserPool user_pool = cognito.getUser_pool();


        user_pool.getUser(username).forgotPasswordInBackground(new ForgotPasswordHandler() {

            @Override
            public void onSuccess() {
                onFinishListener.onSuccess("1 STEP RESET DONE");
            }

            @Override
            public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuation) {
                continuation = forgotPasswordContinuation;
                onFinishListener.onSuccess("1 STEP RESET DONE");

            }

            @Override
            public void onFailure(Exception e) {

                onFinishListener.onError("Reset credentials failed");

            }
        });

    }

    @Override
    public void resetPassword(String username, String password, String code, OnFinishListener onFinishListener) {

        continuation.setPassword(password);
        continuation.setVerificationCode(code);
        continuation.continueTask();

    }


}
