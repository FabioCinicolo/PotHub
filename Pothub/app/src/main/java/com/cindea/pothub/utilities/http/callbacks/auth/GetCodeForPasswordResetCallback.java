package com.cindea.pothub.utilities.http.callbacks.auth;

import android.app.Activity;

import com.cindea.pothub.auth.activities.ResetCredentialsActivity;
import com.cindea.pothub.auth.fragments.ResetCRCodeFragment;
import com.cindea.pothub.auth.fragments.ResetCRPasswordFragment;
import com.cindea.pothub.utilities.http.callbacks.HTTPCallback;

public class GetCodeForPasswordResetCallback implements HTTPCallback {

    private Activity activity;
    private String username;

    public GetCodeForPasswordResetCallback(Activity activity, String username) {
        this.activity = activity;
        this.username = username;
    }

    @Override
    public void handleStatus200(String response) {

        activity.runOnUiThread(() -> {

            ((ResetCredentialsActivity)activity).replaceFragment(new ResetCRPasswordFragment());

        });

    }

    @Override
    public void handleStatus400(String response) {

    }

    @Override
    public void handleStatus401(String response) {

    }

    @Override
    public void handleStatus500(String response) {

    }

    @Override
    public void handleRequestException(String message) {

    }
}
