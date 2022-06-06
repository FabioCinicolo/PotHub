package com.cindea.pothub.authentication.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chaos.view.PinView;
import com.cindea.pothub.R;

public class ResetCRCodeFragment extends CustomAuthFragment {

    private Button buttonContinue;
    private PinView pinView;
    private ResetCRFragmentSwitcher resetCRFragmentSwitcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_reset_c_r_code, container, false);

        setupComponents();
        listeners();

        return fragmentView;
    }

    @Override
    protected void setupComponents() {

        buttonContinue = fragmentView.findViewById(R.id.fragmentCR2_continue);
        pinView = fragmentView.findViewById(R.id.fragmentCR2_code);

        resetCRFragmentSwitcher = (ResetCRFragmentSwitcher) getActivity();

        setupAnimations(getContext());

    }

    private void listeners() {

        buttonContinue.setOnClickListener(view1 -> {

            runButtonAnimation(buttonContinue);
            Log.e("CODE", pinView.getText().toString());
            button_handler.postDelayed(() -> resetCRFragmentSwitcher.switchToSigninFragment(pinView.getText().toString()), 170);

        });


    }

}