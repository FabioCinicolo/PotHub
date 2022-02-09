package com.cindea.pothub.changepassword.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cindea.pothub.R;
import com.cindea.pothub.auth.fragments.CustomAuthFragment;
import com.cindea.pothub.utilities.http.AuthenticationHTTP;
import com.cindea.pothub.utilities.http.callbacks.auth.ChangePasswordCallback;
import com.cindea.pothub.utilities.sharedpreferences.TokenSharedPreferences;

public class ChangePasswordFormFragment extends CustomAuthFragment {

    private View view;
    private Button buttonContinue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password_form, container, false);

        setupComponents();
        listeners();

        return view;
    }

    @Override
    protected void setupComponents() {

       buttonContinue = view.findViewById(R.id.fragmentChangeCR_continue);
       setupAnimations(getContext());

    }

    private void listeners() {

        buttonContinue.setOnClickListener(view1 -> {

            String oldpassword = ((EditText)view.findViewById(R.id.fragmentChangeCR_oldpassword)).getText().toString();
            String newpassword = ((EditText)view.findViewById(R.id.fragmentChangeCR_newpassword)).getText().toString();

            runButtonAnimation(buttonContinue);

            button_handler.postDelayed(() -> {
                TokenSharedPreferences tokenSharedPreferences = new TokenSharedPreferences(getActivity());
                new AuthenticationHTTP().changePassword(tokenSharedPreferences.getUsername(), oldpassword,
                        newpassword, tokenSharedPreferences.getAccessToken(), new ChangePasswordCallback(getActivity()));
            }, 170);



        });

    }
}