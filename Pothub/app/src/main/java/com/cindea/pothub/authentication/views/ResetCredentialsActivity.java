package com.cindea.pothub.authentication.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cindea.pothub.MainActivity;
import com.cindea.pothub.R;
import com.cindea.pothub.authentication.ResetCredentialsContract;
import com.cindea.pothub.authentication.models.ResetCredentialsModel;
import com.cindea.pothub.authentication.presenters.ResetCredentialsPresenter;
import com.cindea.pothub.authentication.views.fragments.ResetCRCodeFragment;
import com.cindea.pothub.authentication.views.fragments.ResetCRFragmentSwitcher;
import com.cindea.pothub.authentication.views.fragments.ResetCRPasswordFragment;
import com.cindea.pothub.authentication.views.fragments.ResetCRUsernameFragment;


public class ResetCredentialsActivity extends AppCompatActivity implements ResetCRFragmentSwitcher, ResetCredentialsContract.View {

    private String username;
    private String password;
    private ResetCredentialsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_credentials);

        presenter = new ResetCredentialsPresenter(this, new ResetCredentialsModel());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityResetCR_framelayout, new ResetCRUsernameFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void switchToResetCRPasswordFragment(String username) {

        this.username = username;
        presenter.requestCodeButtonClicked(username);
    }

    @Override
    public void switchToSigninFragment(String code) {

        presenter.resetPasswordButtonClicked(username, password, code);

    }

    @Override
    public void switchToResetCRCodeFragment(String password) {

        this.password = password;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityResetCR_framelayout, new ResetCRCodeFragment());
        fragmentTransaction.commit();

    }


    @Override
    public void changeFragmentToResetCRPassword() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityResetCR_framelayout, new ResetCRPasswordFragment());
        fragmentTransaction.commit();

    }

    @Override
    public void resetDone(String message) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public void displayUsernameNotFoundError(String message) {

        runOnUiThread(() -> {

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        });

    }

    @Override
    public void displayResetError(String message) {
        runOnUiThread(() -> {

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        });
    }
}