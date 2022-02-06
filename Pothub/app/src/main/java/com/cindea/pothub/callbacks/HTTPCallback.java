package com.cindea.pothub.callbacks;

public interface HTTPCallback {

    void handleStatus200(String response);

    void handleStatus400(String response);

    void handleStatus401(String response);

    void handleStatus500(String response);

    void handleRequestException(String message);

}
