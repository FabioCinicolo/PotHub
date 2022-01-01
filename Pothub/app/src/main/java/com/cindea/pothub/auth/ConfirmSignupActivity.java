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

import com.cindea.pothub.R;

public class ConfirmSignupActivity extends AppCompatActivity {

    private Animation anim_scale_up;
    private Animation anim_scale_down;
    private Button button_confirm;
//    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_signup);

        setupAnimations(getApplicationContext());
        button_confirm = findViewById(R.id.activityConfirmSignup_continue);
        customListeners();

    }

    private void customListeners() {

        button_confirm.setOnClickListener(view -> {

/*            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();*/
            runButtonAnimation(button_confirm);

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