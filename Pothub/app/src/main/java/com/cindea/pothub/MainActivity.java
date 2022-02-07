package com.cindea.pothub;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cindea.pothub.auth.AuthCallbackListener;
import com.cindea.pothub.auth.activities.ResetCredentialsActivity;
import com.cindea.pothub.auth.fragments.SigninFragment;
import com.cindea.pothub.auth.fragments.SignupFragment;
import com.cindea.pothub.utilities.http.AuthenticationHTTP;
import com.cindea.pothub.utilities.http.callbacks.auth.TokenLoginCallback;
import com.cindea.pothub.utilities.sharedpreferences.TokenSharedPreferences;

public class MainActivity extends AppCompatActivity implements AuthCallbackListener {

    Fragment signinFragment = new SigninFragment();
    Fragment signupFragment = new SignupFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(signinFragment);

    }

    @Override
    protected void onResume() {
        super.onResume();

        TokenSharedPreferences tokenSharedPreferences = new TokenSharedPreferences(this);

        if(!tokenSharedPreferences.isEmpty())
            new AuthenticationHTTP().tokenLogin(tokenSharedPreferences.getIdToken(), new TokenLoginCallback(this));

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