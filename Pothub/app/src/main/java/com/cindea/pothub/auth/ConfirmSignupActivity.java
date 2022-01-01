package com.cindea.pothub.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.cindea.pothub.R;
import com.cindea.pothub.auth_util.AWSCognitoAuthentication;

public class ConfirmSignupActivity extends AppCompatActivity {

    private Animation anim_scale_up;
    private Animation anim_scale_down;
    private Button button_confirm;
    private PinView pin_view;
    private String username;
//    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_signup);

        setupAnimations(getApplicationContext());
        button_confirm = findViewById(R.id.activityConfirmSignup_continue);
        pin_view = findViewById(R.id.activityConfirmSignup_code);
        username = getIntent().getStringExtra("username");
        customListeners();

    }

    private void customListeners() {

        button_confirm.setOnClickListener(view -> {

/*            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();*/
            runButtonAnimation(button_confirm);
            String confirmation_code = pin_view.getText().toString();
            AWSCognitoAuthentication auth = new AWSCognitoAuthentication(this);
            auth.initiateConfirmSignUp(username, confirmation_code);
            auth.handleAuthentication(() -> {
                Toast.makeText(getApplicationContext(), "Confirmed", Toast.LENGTH_SHORT).show();
            });

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