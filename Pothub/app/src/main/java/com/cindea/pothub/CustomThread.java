package com.cindea.pothub;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;


public class CustomThread extends HandlerThread {


    private Handler handler;
    private OnHandlerReady onHandlerReady;

    public CustomThread(OnHandlerReady onHandlerReady) {
        super("CustomThread", Process.THREAD_PRIORITY_FOREGROUND);
        this.onHandlerReady = onHandlerReady;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new CustomHandler();
        onHandlerReady.onSuccess(handler);
    }

    public Handler getHandler() {
        return handler;
    }

}
