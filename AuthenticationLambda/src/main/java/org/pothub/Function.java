package org.pothub;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.pothub.exceptions.CognitoException;
import org.pothub.idps.Cognito;

public class Function implements RequestHandler<Request, String> {
    @Override
    public String handleRequest(Request request, Context context) {

        Cognito cognito = new Cognito();

        String action = request.getAction();
        switch (action) {
            case "SIGNUP":
                try {
                    cognito.signUpUser(request.getUser());
                    return "User signed up successfully";
                } catch (CognitoException e) {
                    throw new RuntimeException(e.getMessage());
                }
            case "CONFIRM":
                try {
                    cognito.confirmUser(request.getUser().getUsername(), request.getConfirmation_code());
                    return "User confirmed";
                } catch (CognitoException e) {
                    throw new RuntimeException(e.getMessage());
                }
            case "SIGNIN":
                try {
                    cognito.signInUser(request.getUser().getUsername(), request.getUser().getPassword());
                    return "User signed in successfully";
                } catch (CognitoException e) {
                    throw new RuntimeException(e.getMessage());
                }
            case "FORGOT_PWD":
                try {
                    cognito.initiateForgotPassword(request.getUser().getUsername());
                    return "A code has been sent to your mail";
                } catch (CognitoException e) {
                    throw new RuntimeException(e.getMessage());
                }
            case "RESET_PWD":
                try {
                    cognito.resetPassword(request.getUser().getUsername(), request.getUser().getPassword(), request.getConfirmation_code());
                    return "Password changed successfully";
                } catch (CognitoException e) {
                    throw new RuntimeException(e.getMessage());
                }
            default:
                throw new RuntimeException("Wrong action");
        }
    }
}