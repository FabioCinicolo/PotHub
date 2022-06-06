package com.cindea.pothub.authentication.models;

import com.amazonaws.cognito.clientcontext.data.UserContextDataProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.cindea.pothub.MainActivity;
import com.cindea.pothub.authentication.SignupContract;
import com.cindea.pothub.cognito.Cognito;

public class SignupModel implements SignupContract.Model{

    @Override
    public void signUp(String username, String email, String password, OnFinishListener listener) {

        Cognito cognito = MainActivity.getCognito();
        CognitoUserPool user_pool = cognito.getUser_pool();
        CognitoUserAttributes attributes = new CognitoUserAttributes();
        attributes.addAttribute("email", email.replace(" ", ""));
            user_pool.signUpInBackground(username, password, attributes, null, new SignUpHandler() {
                @Override
                public void onSuccess(CognitoUser user, boolean signUpConfirmationState, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                    listener.onSuccess("Sign up success");
                }

                @Override
                public void onFailure(Exception exception) {
                    listener.onError(exception.getMessage());
                }
            });

    }
}
