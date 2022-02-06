package com.cindea.pothub.auth.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cindea.pothub.R;
import com.cindea.pothub.auth.ResetPwdCallbackListener;

public class ResetCRUsernameFragment extends CustomAuthFragment {

    private ResetPwdCallbackListener resetPwdCallbackListener;
    private Button buttonContinue;
    private EditText etUsername;
    private TextView tvBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_reset_c_r_username, container, false);

        setupComponents();
        listeners();
        return fragmentView;
    }

    @Override
    protected void setupComponents() {

        buttonContinue = fragmentView.findViewById(R.id.fragmentCR1_continue);
        tvBack = fragmentView.findViewById(R.id.fragmentCR1_back);
        resetPwdCallbackListener = (ResetPwdCallbackListener) getActivity();
        etUsername = fragmentView.findViewById(R.id.fragmentCR1_username);

        setupAnimations(getContext());

    }

    private void listeners() {

        buttonContinue.setOnClickListener(view -> {

            runButtonAnimation(buttonContinue);

            button_handler.postDelayed(() -> resetPwdCallbackListener.switchToResetCRPassword(etUsername.getText().toString()),170);

        });

        tvBack.setOnClickListener(view -> getActivity().finish());


    }
}