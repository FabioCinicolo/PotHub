package com.cindea.pothub.authentication.models;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.cindea.pothub.MainActivity;
import com.cindea.pothub.authentication.ConfirmSignupContract;
import com.cindea.pothub.cognito.Cognito;

public class ConfirmSignupModel implements ConfirmSignupContract.Model{

    @Override
    public void confirmSignUp(String username, String confirmation_code, OnFinishListener listener) {

        Cognito cognito = MainActivity.getCognito();
        CognitoUserPool user_pool = cognito.getUser_pool();
        CognitoUser cognitoUser = user_pool.getUser(username);
        cognitoUser.confirmSignUpInBackground(confirmation_code, false, new GenericHandler() {
            @Override
            public void onSuccess() {
                listener.onSuccess("Utente confermato");
            }

            @Override
            public void onFailure(Exception exception) {
                listener.onError("Utente non confermato");
            }
        });

    }

}
