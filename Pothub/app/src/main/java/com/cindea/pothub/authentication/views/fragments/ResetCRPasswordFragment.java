package com.cindea.pothub.authentication.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cindea.pothub.R;

public class ResetCRPasswordFragment extends CustomAuthFragment {

    private Button buttonContinue;
    private EditText etPassword;
    private ResetCRFragmentSwitcher resetCRFragmentSwitcher;

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
        etPassword = fragmentView.findViewById(R.id.fragmentCR3_password);

        resetCRFragmentSwitcher = (ResetCRFragmentSwitcher) getActivity();

        setupAnimations(getContext());

    }

    private void listeners() {

        buttonContinue.setOnClickListener(view -> {

            runButtonAnimation(buttonContinue);

            button_handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    resetCRFragmentSwitcher.switchToResetCRCodeFragment(etPassword.getText().toString());

                }
            }, 170);

        });


    }

}