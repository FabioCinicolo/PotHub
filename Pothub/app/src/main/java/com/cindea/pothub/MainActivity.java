package com.cindea.pothub;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.cindea.pothub.authentication.views.ResetCredentialsActivity;
import com.cindea.pothub.authentication.views.fragments.SigninFragment;
import com.cindea.pothub.authentication.views.fragments.SignupFragment;
import com.cindea.pothub.cognito.Cognito;
import com.cindea.pothub.map.LocationService;


public class MainActivity extends AppCompatActivity implements AuthSwitcher {

    Fragment signinFragment = new SigninFragment();
    Fragment signupFragment = new SignupFragment();
    private static Cognito cognito;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cognito = new Cognito(this);

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