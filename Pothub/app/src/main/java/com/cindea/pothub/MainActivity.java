package com.cindea.pothub;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cindea.pothub.authentication.views.ResetCredentialsActivity;
import com.cindea.pothub.authentication.views.fragments.SigninFragment;
import com.cindea.pothub.authentication.views.fragments.SignupFragment;

public class MainActivity extends AppCompatActivity implements AuthSwitcher {

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