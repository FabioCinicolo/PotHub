package com.cindea.pothub.home.contracts;

import com.cindea.pothub.entities.Pothole;

import java.util.List;

public interface RightHomeContract {

    interface View{
        void onPotholesLoaded(List<Pothole> potholes);
        void onError(String message);
    }

    interface Presenter{
        void getPotholesByRange(double meters, double latitude, double longitude);
    }

    interface Model{

        interface OnFinishListener {

            void onPotholesLoaded(List<Pothole> potholes);
            void onError(String message);

        }

        void getPotholesByRange(double meters, double latitude, double longitude, RightHomeContract.Model.OnFinishListener listener);

    }

}
