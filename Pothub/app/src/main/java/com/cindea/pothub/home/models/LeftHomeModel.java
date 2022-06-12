package com.cindea.pothub.home.models;


import static com.cindea.pothub.map.Constants.GET_USER_POTHOLES_BY_DAYS;

import android.os.Handler;
import android.os.Message;

import com.cindea.pothub.CustomThread;
import com.cindea.pothub.OnHandlerReady;
import com.cindea.pothub.home.contracts.LeftHomeContract;


public class LeftHomeModel implements LeftHomeContract.Model, OnHandlerReady {

    private String username;
    private String date;
    private OnFinishListener listener;

    @Override
    public void getUserPotholesByDays(String username, String date, OnFinishListener listener) {

        this.username = username;
        this.date = date;
        this.listener = listener;

        new CustomThread(this).start();

    }

    @Override
    public void onSuccess(Handler handler) {

        Message message = Message.obtain();

        message.what = GET_USER_POTHOLES_BY_DAYS;
        message.obj = new CustomMessage(username, date, listener);
        handler.sendMessage(message);

    }

    public class CustomMessage {

        public String username;
        public String date;
        public OnFinishListener listener;

        CustomMessage(String username, String date, OnFinishListener listener) {
            this.username = username;
            this.date = date;
            this.listener = listener;
        }

    }

}
