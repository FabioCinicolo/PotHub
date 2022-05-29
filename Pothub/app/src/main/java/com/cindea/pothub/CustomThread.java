package com.cindea.pothub;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;



public class CustomThread extends HandlerThread {


    private Handler handler;

    public CustomThread() {
        super("CustomThread", Process.THREAD_PRIORITY_FOREGROUND);
    }

    @Override
    protected void onLooperPrepared() {
        handler = new CustomHandler();
    }

    public Handler getHandler() {
        return handler;
    }

}
