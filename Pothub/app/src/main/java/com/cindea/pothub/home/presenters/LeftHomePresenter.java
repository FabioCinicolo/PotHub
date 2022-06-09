package com.cindea.pothub.home.presenters;

import com.cindea.pothub.entities.Pothole;
import com.cindea.pothub.home.contracts.LeftHomeContract;

import java.util.List;

public class LeftHomePresenter implements LeftHomeContract.Presenter, LeftHomeContract.Model.OnFinishListener {


    private final LeftHomeContract.View view;
    private final LeftHomeContract.Model model;

    public LeftHomePresenter(LeftHomeContract.View view, LeftHomeContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void getUserPotholesByDays(String username, String date) {
        model.getUserPotholesByDays(username, date, this);
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
