package com.cindea.pothub.home;


import com.cindea.pothub.entities.Pothole;
import java.util.List;

public interface LeftHomeContract {

    interface View{
        void onPotholesLoaded(List<Pothole> potholes);
        void onError(String message);
    }

    interface Presenter{
        void getUserPotholesByDays(String username, int days);
    }

    interface Model{

        interface OnFinishListener {
            void onPotholesLoaded(List<Pothole> potholes);
            void onError(String message);
        }

        void getUserPotholesByDays(String username, int days, OnFinishListener listener);

    }

}
