package com.cindea.pothub;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ResetCRPasswordFragment extends CustomFragment {

    private ResetPwdCallbackListener resetPwdCallbackListener;
    private Button button_continue;
    private TextView text_back;

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

        setupAnimations(getContext());

    }

    private void customListeners() {

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runButtonAnimation(button_continue);

                button_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // Ciao

                    }
                },170);

            }
        });


    }

}