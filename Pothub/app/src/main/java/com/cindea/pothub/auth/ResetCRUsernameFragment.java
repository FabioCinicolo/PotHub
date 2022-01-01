package com.cindea.pothub.auth;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cindea.pothub.CustomFragment;
import com.cindea.pothub.R;

public class ResetCRUsernameFragment extends CustomFragment {

    private ResetPwdCallbackListener resetPwdCallbackListener;
    private Button button_continue;
    private EditText et_username;
    private TextView text_back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_c_r_username, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupComponents(view);
        customListeners();

    }

    @Override
    protected void setupComponents(View view) {

        button_continue = view.findViewById(R.id.fragmentCR1_continue);
        text_back = view.findViewById(R.id.fragmentCR1_back);
        resetPwdCallbackListener = (ResetPwdCallbackListener) getActivity();
        et_username = view.findViewById(R.id.fragmentCR1_username);

        setupAnimations(getContext());

    }

    private void customListeners() {

        button_continue.setOnClickListener(view -> {

            runButtonAnimation(button_continue);

            button_handler.postDelayed(() -> resetPwdCallbackListener.switchToResetCRPassword(et_username.getText().toString()),170);

        });

        text_back.setOnClickListener(view -> getActivity().finish());


    }
}