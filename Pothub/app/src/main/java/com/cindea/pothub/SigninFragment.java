package com.cindea.pothub;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SigninFragment extends Fragment {

    private CallBackListener callBackListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.fragmentSignin_signupbtn);

        callBackListener = (CallBackListener)getActivity();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callBackListener.onSignupPress();

            }
        });


    }

}