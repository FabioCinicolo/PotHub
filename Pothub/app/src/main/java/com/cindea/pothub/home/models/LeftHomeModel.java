package com.cindea.pothub.home.models;


import static com.cindea.pothub.map.Constants.GET_USER_POTHOLES_BY_DAYS;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.cindea.pothub.CustomThread;
import com.cindea.pothub.home.LeftHomeContract;


public class LeftHomeModel implements LeftHomeContract.Model{

    private CustomThread thread;
    private Handler handler;

    @Override
    public void getUserPotholesByDays(String username, int days, OnFinishListener listener) {

        Message message = Message.obtain();

        thread = new CustomThread();

        thread.start();

        try{
            synchronized (this){
                wait(1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
