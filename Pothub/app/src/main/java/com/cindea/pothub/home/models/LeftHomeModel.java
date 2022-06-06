package com.cindea.pothub.home.models;


import static com.cindea.pothub.map.Constants.GET_USER_POTHOLES_BY_DAYS;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.cindea.pothub.CustomThread;
import com.cindea.pothub.authentication.views.fragments.OnHandlerReady;
import com.cindea.pothub.home.LeftHomeContract;


public class LeftHomeModel implements LeftHomeContract.Model, OnHandlerReady {

    private CustomThread thread;
    private Handler handler;
    private String username;
    private int days;
    private OnFinishListener listener;

    @Override
    public void getUserPotholesByDays(String username, int days, OnFinishListener listener) {

        this.username = username;
        this.days = days;
        this.listener = listener;

        thread = new CustomThread(this);

        thread.start();

    }

    @Override
    public void onSuccess(Handler handler) {

        Message message = Message.obtain();

        handler = thread.getHandler();

        message.what = GET_USER_POTHOLES_BY_DAYS;
        message.obj = new CustomMessage(username, days, listener);
        handler.sendMessage(message);

    }

    public class CustomMessage{

        public String username;
        public int days;
        public OnFinishListener listener;

        CustomMessage(String username, int days, OnFinishListener listener){
            this.username = username;
            this.days = days;
            this.listener = listener;
        }

    }

}
