package com.cindea.pothub.auth.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.cindea.pothub.R;

public class ConfirmSignupActivity extends AppCompatActivity {

    private Animation anim_scale_up;
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
        button_confirm = findViewById(R.id.activityConfirmSignup_continue);

    }

    private void customListeners() {

        button_confirm.setOnClickListener(view -> {

            runButtonAnimation(button_confirm);
            String confirmation_code = ((PinView)findViewById(R.id.activityConfirmSignup_code)).getText().toString();

            String username = getIntent().getStringExtra("username");

            new AuthenticationHTTP().confirmSignUp(username, confirmation_code, new ConfirmSignupCallback(this));

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

}