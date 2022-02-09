package org.pothub;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.pothub.exceptions.CognitoException;
import org.pothub.idps.Cognito;

public class Function implements RequestHandler<Event, String> {

    //Object declared outside of handleRequest are reused
    Cognito cognito = new Cognito();

    @Override
    public String handleRequest(Event event, Context context) {


        String action = event.getAction();
        switch (action) {

            //Pure Cognito User Registration
            case "REGISTER":
                try {

                    cognito.signUpUser(event.getUser());

                    return "A confirmation code has been sent to your email address";

                } catch (CognitoException e) {

                    throw new RuntimeException(e.getMessage());

                }
                //Confirm signup with confirmation code sent via email
            case "CONFIRM":
                try {
                    cognito.confirmUser(event.getUser().getUsername(), event.getConfirmation_code());

                    return "Successfully signed up";

                } catch (CognitoException e) {
                    throw new RuntimeException(e.getMessage());
                }

                //Cognito Token Validation
            case "TOKEN_LOGIN":

                cognito.verifyIdToken(event.getId_token());

                return "Successfully signed in";

            //Get cognito id and refresh tokens by username and password
            case "PASSWORD":
                try {
                    String json_tokens = cognito.signInUserAndGetTokens(event.getUser().getUsername(), event.getUser().getPassword());

                    return json_tokens;

                } catch (CognitoException e) {

                    throw new RuntimeException(e.getMessage());

                }

                //Get new cognito id token and new access token by refresh token
            case "REFRESH_TOKEN":
                try {
                    String new_tokens = cognito.getNewIdAndAccessTokens(event.getRefresh_token(), event.getUser().getUsername());

                    return new_tokens;

                } catch (CognitoException e) {

                    throw new RuntimeException(e.getMessage());
                }
                //Initiate forgot password flow, an email will eventually be sent to the user
            case "FORGOT_PWD":
                try {
                    cognito.initiateForgotPassword(event.getUser().getUsername());

                    return "A code has been sent to your mail";

                } catch (CognitoException e) {

                    throw new RuntimeException(e.getMessage());

                }
                //Password reset via confirmation code sent previously via email
            case "RESET_PWD":
                try {
                    if (event.getConfirmation_code() != null) {
                        cognito.resetPassword(event.getUser().getUsername(), event.getUser().getPassword(), event.getConfirmation_code());
                    } else {
                        cognito.changePassword(event.getOld_password(), event.getUser().getPassword(), event.getAccess_token());
                    }
                    return "Password changed successfully";
                } catch (CognitoException e) {
                    throw new RuntimeException(e.getMessage());
                }
                //Changes user password (he must be logged in already)
            case "CHANGE_PASSWORD":
                try {
                    cognito.changePassword(event.getOld_password(), event.getUser().getPassword(), event.getAccess_token());
                    return "Password changed successully";

                } catch (CognitoException e) {

                    throw new RuntimeException(e.getMessage());

                }
            default:
                throw new RuntimeException("Wrong action");

        }
    }
}