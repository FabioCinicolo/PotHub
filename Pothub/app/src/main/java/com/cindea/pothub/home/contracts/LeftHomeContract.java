package com.cindea.pothub.home.contracts;


import com.cindea.pothub.entities.Pothole;

import java.util.List;

public interface LeftHomeContract {

    interface View {
        void onPotholesLoaded(List<Pothole> potholes);

        void onError(String message);
    }

    interface Presenter {
        void getUserPotholesByDays(String username, String date);
    }

    interface Model {

        void getUserPotholesByDays(String username, String date, OnFinishListener listener);

        interface OnFinishListener {
            void onPotholesLoaded(List<Pothole> potholes);

            void onError(String message);
        }

    }

}
