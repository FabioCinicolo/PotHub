package com.cindea.pothub.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.cindea.pothub.R;
import com.cindea.pothub.auth_util.AWSCognitoAuthentication;

public class ResetCredentialsActivity extends AppCompatActivity implements ResetPwdCallbackListener {

    private AWSCognitoAuthentication auth;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_credentials);

        auth = new AWSCognitoAuthentication(this);

        replaceFragment(new ResetCRUsernameFragment());
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityResetCR_framelayout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void switchToResetCRPassword(String username) {

        this.username = username;
        auth.initiateForgotPassword(username);
        auth.handleAuthentication(() -> {
            replaceFragment(new ResetCRPasswordFragment());
        });

    }

    @Override
    public void switchToResetCRCode(String password) {

        this.password = password;
        replaceFragment(new ResetCRCodeFragment());

    }

    @Override
    public void switchToSignin(String confirmation_code) {

        auth.initiateResetPassword(username, password, confirmation_code);
        auth.handleAuthentication(() -> {
            finish();
        });
    }


}