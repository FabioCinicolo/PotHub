package com.cindea.pothub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class ResetCredentialsActivity extends AppCompatActivity implements ResetPwdCallbackListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_credentials);

        replaceFragment(new ResetCREmailFragment());
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityResetCR_framelayout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onContinueAfterEmail() {

        replaceFragment(new ResetCRCodeFragment());

    }

    @Override
    public void onContinueAfterPin() {

        replaceFragment(new ResetCRPasswordFragment());

    }
}