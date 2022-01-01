/*package com.cindea.pothub.auth_util;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class GoogleAuthentication {

    private Intent intent;
    private Fragment signin_fragment;
    private ActivityResultLauncher<Intent> start_activity_for_result;

    public GoogleAuthentication(Fragment signin_fragment) {

        this.signin_fragment = signin_fragment;

        this.start_activity_for_result = signin_fragment.registerForActivityResult(new ActivityResultContracts
                .StartActivityForResult(), result -> {

            if (result.getResultCode() == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignInResult(task);
            }
        });
    }

    public void signIn(Intent intent) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        this.intent = intent;

        GoogleSignInClient googlesignin_client = GoogleSignIn.getClient(signin_fragment.getActivity(), gso);

        Intent signin_intent = googlesignin_client.getSignInIntent();
        start_activity_for_result.launch(signin_intent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {*//**//*
            signin_fragment.getActivity().startActivity(intent);
    }

}*/
