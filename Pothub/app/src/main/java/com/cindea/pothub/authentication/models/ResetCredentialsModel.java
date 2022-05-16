package com.cindea.pothub.authentication.models;

import com.cindea.pothub.authentication.ResetCredentialsContract;

public class ResetCredentialsModel implements ResetCredentialsContract.Model {

    @Override
    public void requestCode(String username, OnFinishListener onFinishListener) {

    }

    @Override
    public void resetPassword(String username, String password, String code, OnFinishListener onFinishListener) {

    }
}
