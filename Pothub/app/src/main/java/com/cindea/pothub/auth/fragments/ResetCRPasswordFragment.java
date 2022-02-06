package com.cindea.pothub.auth.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cindea.pothub.R;
import com.cindea.pothub.auth.ResetPwdCallbackListener;

public class ResetCRPasswordFragment extends CustomAuthFragment {

    private ResetPwdCallbackListener resetPwdCallbackListener;
    private Button buttonContinue;
    private EditText etPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_reset_c_r_password, container, false);

        setupComponents();
        listeners();

        return fragmentView;
    }

    @Override
    protected void setupComponents() {

        buttonContinue = fragmentView.findViewById(R.id.fragmentCR3_continue);
        resetPwdCallbackListener = (ResetPwdCallbackListener) getActivity();
        etPassword = fragmentView.findViewById(R.id.fragmentCR3_password);

        setupAnimations(getContext());

    }

    private void listeners() {

        buttonContinue.setOnClickListener(view -> {

            runButtonAnimation(buttonContinue);

            button_handler.postDelayed(() -> resetPwdCallbackListener.switchToResetCRCode(etPassword.getText().toString()),170);

        });


    }

}