package com.cindea.pothub.authentication.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.cindea.pothub.MainActivity;
import com.cindea.pothub.R;
import com.cindea.pothub.authentication.ConfirmSignupContract;
import com.cindea.pothub.authentication.models.ConfirmSignupModel;
import com.cindea.pothub.authentication.presenters.ConfirmSignupPresenter;

public class ConfirmSignupActivity extends AppCompatActivity implements ConfirmSignupContract.View{

    private Animation anim_scale_up;
    private ConfirmSignupContract.Presenter presenter;
    private Animation anim_scale_down;
    private Button button_confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_signup);

        setupComponents();
        customListeners();

    }

    private void setupComponents() {

        setupAnimations(getApplicationContext());
        presenter = new ConfirmSignupPresenter(this, new ConfirmSignupModel());
        button_confirm = findViewById(R.id.activityConfirmSignup_continue);

    }

    private void customListeners() {

        button_confirm.setOnClickListener(view -> {

            runButtonAnimation(button_confirm);
            String confirmation_code = ((PinView)findViewById(R.id.activityConfirmSignup_code)).getText().toString();

            String username = getIntent().getStringExtra("username");
            presenter.confirmSignUpClicked(username, confirmation_code);

        });

    }

    private void setupAnimations(Context context) {

        anim_scale_up = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        anim_scale_down = AnimationUtils.loadAnimation(context, R.anim.scale_down);

    }

    private void runButtonAnimation(Button button) {

        button.startAnimation(anim_scale_up);
        button.startAnimation(anim_scale_down);

    }

    @Override
    public void successSignUp() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public void displayError(String message) {

        runOnUiThread(() -> {

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        });

    }
}