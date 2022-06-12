package com.cindea.pothub.home.presenters;

import com.cindea.pothub.entities.Pothole;
import com.cindea.pothub.home.contracts.RightHomeContract;

import java.util.List;

public class RightHomePresenter implements RightHomeContract.Presenter, RightHomeContract.Model.OnFinishListener {

    private final RightHomeContract.View view;
    private final RightHomeContract.Model model;

    public RightHomePresenter(RightHomeContract.View view, RightHomeContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void getPotholesByRange(double meters, double latitude, double longitude) {
        model.getPotholesByRange(meters, latitude, longitude, this);
    }

    @Override
    public void onPotholesLoaded(List<Pothole> potholes) {

        view.onPotholesLoaded(potholes);

    }

    @Override
    public void onError(String message) {

        view.onError(message);

    }
}
