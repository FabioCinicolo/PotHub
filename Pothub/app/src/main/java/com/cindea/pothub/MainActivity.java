package com.cindea.pothub;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.cindea.pothub.authentication.views.ResetCredentialsActivity;
import com.cindea.pothub.authentication.views.fragments.SigninFragment;
import com.cindea.pothub.authentication.views.fragments.SignupFragment;
import com.cindea.pothub.cognito.Cognito;
import com.cindea.pothub.home.views.HomeActivity;
import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity implements AuthSwitcher {

    private static Cognito cognito;
    Fragment signinFragment = new SigninFragment();
    Fragment signupFragment = new SignupFragment();

    public static Cognito getCognito() {
        return cognito;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cognito = new Cognito(this);
        if (isSessionValid()) {

            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        replaceFragment(signinFragment);
    }

    public boolean isSessionValid() {
        SharedPreferences pref = getSharedPreferences("Cognito", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("CognitoUserSession", null);
        if (json != null) {
            CognitoUserSession session = gson.fromJson(json, CognitoUserSession.class);
            SigninFragment.username = session.getUsername();
            return session.isValid();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityMain_framelayout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onSigninPress() {


        replaceFragment(signinFragment);

    }

    @Override
    public void onSignupPress() {

        replaceFragment(signupFragment);

    }

    @Override
    public void onResetPassword() {

        Intent intent = new Intent(MainActivity.this, ResetCredentialsActivity.class);
        startActivity(intent);

    }

}