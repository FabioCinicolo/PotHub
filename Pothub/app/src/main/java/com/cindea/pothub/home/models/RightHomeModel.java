package com.cindea.pothub.home.models;

import static com.cindea.pothub.map.Constants.GET_POTHOLES_BY_RANGE;

import android.os.Handler;
import android.os.Message;

import com.cindea.pothub.CustomThread;
import com.cindea.pothub.OnHandlerReady;
import com.cindea.pothub.home.contracts.RightHomeContract;

public class RightHomeModel implements RightHomeContract.Model, OnHandlerReady {

    private double meters;
    private double latitude;
    private double longitude;
    private OnFinishListener listener;

    @Override
    public void getPotholesByRange(double meters, double latitude, double longitude, OnFinishListener listener) {

        this.meters = meters;
        this.latitude = latitude;
        this.longitude = longitude;
        this.listener = listener;

        new CustomThread(this).start();

    }

    @Override
    public void onSuccess(Handler handler) {

        Message message = Message.obtain();

        message.what = GET_POTHOLES_BY_RANGE;
        message.obj = new RightHomeModel.CustomMessage(meters, latitude, longitude, listener);
        handler.sendMessage(message);

    }

    public class CustomMessage{

        public double meters;
        public double latitude;
        public double longitude;
        public RightHomeContract.Model.OnFinishListener listener;

        CustomMessage(double meters, double latitude, double longitude, RightHomeContract.Model.OnFinishListener listener) {
            this.meters = meters;
            this.latitude = latitude;
            this.longitude = longitude;
            this.listener = listener;

        }

    }

}
