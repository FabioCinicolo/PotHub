package com.cindea.pothub.authentication.models;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.cindea.pothub.MainActivity;
import com.cindea.pothub.authentication.SigninContract;

public class SigninModel implements SigninContract.Model {
    @Override
    public void cognitoSignIn(String username, String password, OnFinishListener listener) {

        CognitoUser user = MainActivity.getCognito().getUser_pool().getUser(username);

            user.getSessionInBackground(new AuthenticationHandler() {
                @Override
                public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                    listener.onSuccess();
                }

                @Override
                public void getAuthenticationDetails(AuthenticationContinuation auth_continuation, String userId) {

                    // The API needs user sign-in credentials to continue
                    AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, password, null);
                    // Pass the user sign-in credentials to the continuation
                    auth_continuation.setAuthenticationDetails(authenticationDetails);
                    // Allow the sign-in to continue
                    auth_continuation.continueTask();
                }

                @Override
                public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

                }

                @Override
                public void authenticationChallenge(ChallengeContinuation continuation) {

                }

                @Override
                public void onFailure(Exception exception) {
                    listener.onError(exception.getMessage() + " \n" + exception.toString());
                }
            });
    }
}
