package com.cindea.pothub.cognito;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;

public class Cognito {
    // ############################################################# Information about Cognito Pool
    private String USER_POOL_ID = "eu-central-1_1QYsCdYWB";
    private String CLIENT_ID = "7krigpkagkcuph3r4li6f8qkk2";
    private String CLIENT_SECRET = "10lt8rrlbauglu4cuc2magjp4tpe62ufek7m8bkl98pce09ca5dk";
    private Regions AWS_REGION = Regions.EU_CENTRAL_1;         // Place your Region
    // ############################################################# End of Information about Cognito Pool
    private CognitoUserPool user_pool;
    private Context app_context;
    private CognitoUserAttributes user_attributes;

    private String userPassword;                        // Used for Login

    private ForgotPasswordContinuation continuation;

    public Cognito(Context context) {
        app_context = context;
        user_pool = new CognitoUserPool(context, this.USER_POOL_ID, this.CLIENT_ID, this.CLIENT_SECRET, this.AWS_REGION);
        user_attributes = new CognitoUserAttributes();
    }


    /************SIGN UP***************/
    public void signUpInBackground(String user_id, String password) {
        user_pool.signUpInBackground(user_id, password, user_attributes, null, sign_up_handler);
    }


    SignUpHandler sign_up_handler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful
            Log.d(TAG, "Sign-up success");
            Toast.makeText(app_context, "Sign-up success", Toast.LENGTH_LONG).show();
            // Check if this user (cognitoUser) needs to be confirmed
            if (!userConfirmed) {
                // This user must be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
            } else {
                Toast.makeText(app_context, "Error: User Confirmed before", Toast.LENGTH_LONG).show();
                // The user has already been confirmed
            }
        }

        @Override
        public void onFailure(Exception exception) {
            Toast.makeText(app_context, "Sign-up failed", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Sign-up failed: " + exception);
        }
    };

    /************CONFIRM USER WITH VERIFICATION CODE***************/
    public void confirmUserWithVerificationCode(String user_id, String code) {
        CognitoUser cognitoUser = user_pool.getUser(user_id);
        cognitoUser.confirmSignUpInBackground(code, false, confirmation_handler);
        //cognitoUser.confirmSignUp(code,false, confirmationCallback);
    }

    // Callback handler for confirmSignUp API
    GenericHandler confirmation_handler = new GenericHandler() {

        @Override
        public void onSuccess() {
            // User was successfully confirmed
            Toast.makeText(app_context, "User Confirmed", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onFailure(Exception exception) {
            // User confirmation failed. Check exception for the cause.

        }
    };

    /************LOGIN***************/
    public void signInInBackground(String user_id, String password) {
        CognitoUser cognitoUser = user_pool.getUser(user_id);
        this.userPassword = password;
        cognitoUser.getSessionInBackground(auth_handler);
    }

    // Callback handler for the sign-in process
    AuthenticationHandler auth_handler = new AuthenticationHandler() {
        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }

        @Override
        public void onSuccess(CognitoUserSession user_session, CognitoDevice new_device) {
            Toast.makeText(app_context, "Sign in success", Toast.LENGTH_LONG).show();

        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation auth_continuation, String user_id) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(user_id, userPassword, null);
            // Pass the user sign-in credentials to the continuation
            auth_continuation.setAuthenticationDetails(authenticationDetails);
            // Allow the sign-in to continue
            auth_continuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation mfa_continuation) {
            // Multi-factor authentication is required; get the verification code from user
            //multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
            //multiFactorAuthenticationContinuation.continueTask();
        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-in failed, check exception for the cause
            Toast.makeText(app_context, "Sign in Failure", Toast.LENGTH_LONG).show();
        }


    };


    /************CHANGE PASSWORD***************/
    public void changePassword(String user_id, String old_password, String new_password){
        user_pool.getUser(user_id).changePasswordInBackground(old_password, new_password, confirmation_handler);
    }

    /************FORGOT PASSWORD***************/
    public void forgotPassword(String user_id){

        user_pool.getUser(user_id).forgotPasswordInBackground(forgot_pwd_handler);
    }

    public void confirmForgotPassword(String new_password, String confirmation_code){
        continuation.setPassword(new_password);
        continuation.setVerificationCode(confirmation_code);
        continuation.continueTask();
    }

    ForgotPasswordHandler forgot_pwd_handler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            Log.e(TAG, "Password forgot ok");
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuation) {
            continuation = forgotPasswordContinuation;
        }

        @Override
        public void onFailure(Exception e) {

        }
    };

    public CognitoUserPool getUser_pool() {
        return user_pool;
    }
}




