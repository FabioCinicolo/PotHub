package com.cindea.pothub;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SignupFragment extends Fragment {

    CallBackListener callBackListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.fragmentSignup_signinbtn);

        callBackListener = (CallBackListener)getActivity();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callBackListener.onSigninPress();

            }
        });


    }

}