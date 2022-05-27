package com.cindea.pothub;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cindea.pothub.authentication.views.ResetCredentialsActivity;
import com.cindea.pothub.authentication.views.fragments.SigninFragment;
import com.cindea.pothub.authentication.views.fragments.SignupFragment;
import com.cindea.pothub.cognito.Cognito;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements AuthSwitcher {

    Fragment signinFragment = new SigninFragment();
    Fragment signupFragment = new SignupFragment();
    private static Cognito cognito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cognito = new Cognito(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        replaceFragment(signinFragment);
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

    public static Cognito getCognito() {
        return cognito;
    }

}