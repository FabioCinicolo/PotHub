package com.cindea.pothub.auth;

public interface ResetPwdCallbackListener {

    void switchToResetCRPassword();
    void switchToResetCRCode();
    void switchToSignin();

}
