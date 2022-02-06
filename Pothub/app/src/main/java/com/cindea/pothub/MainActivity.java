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

public class MainActivity extends AppCompatActivity implements AuthCallbackListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new SigninFragment());

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityMain_framelayout, fragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onSigninPress() {

        replaceFragment(new SigninFragment());

    }

    @Override
    public void onSignupPress() {

        replaceFragment(new SignupFragment());

    }

    @Override
    public void onResetPassword() {

        Intent intent = new Intent(MainActivity.this, ResetCredentialsActivity.class);
        startActivity(intent);

    }
}