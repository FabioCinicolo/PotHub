package com.cindea.pothub.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chaos.view.PinView;
import com.cindea.pothub.CustomFragment;
import com.cindea.pothub.R;

public class ResetCRCodeFragment extends CustomFragment {

    private ResetPwdCallbackListener resetPwdCallbackListener;
    private Button button_continue;
    private PinView pin_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_c_r_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupComponents(view);
        customListeners();

    }

    @Override
    protected void setupComponents(View view) {

        button_continue = view.findViewById(R.id.fragmentCR2_continue);
        resetPwdCallbackListener = (ResetPwdCallbackListener) getActivity();
        pin_view = view.findViewById(R.id.fragmentCR2_code);

        setupAnimations(getContext());

    }

    private void customListeners() {

        button_continue.setOnClickListener(view -> {

            runButtonAnimation(button_continue);

            button_handler.postDelayed(() -> resetPwdCallbackListener.switchToSignin(pin_view.getText().toString()),170);

        });


    }

}