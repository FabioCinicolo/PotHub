package com.cindea.pothub.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cindea.pothub.CustomFragment;
import com.cindea.pothub.R;

public class ResetCRPasswordFragment extends CustomFragment {

    private ResetPwdCallbackListener resetPwdCallbackListener;
    private Button button_continue;
    private TextView text_back;
    private EditText et_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_c_r_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupComponents(view);
        customListeners();

    }

    @Override
    protected void setupComponents(View view) {

        button_continue = view.findViewById(R.id.fragmentCR3_continue);
        resetPwdCallbackListener = (ResetPwdCallbackListener) getActivity();
        et_password = view.findViewById(R.id.fragmentCR3_password);

        setupAnimations(getContext());

    }

    private void customListeners() {

        button_continue.setOnClickListener(view -> {

            runButtonAnimation(button_continue);

            button_handler.postDelayed(() -> resetPwdCallbackListener.switchToResetCRCode(et_password.getText().toString()),170);

        });


    }

}