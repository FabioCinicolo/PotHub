package com.cindea.pothub.auth;

public interface ResetPwdCallbackListener {

    void switchToResetCRPassword(String username);
    void switchToResetCRCode(String password);
    void switchToSignin(String confirmation_code);

}
