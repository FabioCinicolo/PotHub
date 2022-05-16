package com.cindea.pothub.authentication.presenters;

import com.cindea.pothub.authentication.SignupContract;

public class SignupPresenter implements SignupContract.Presenter, SignupContract.Model.OnFinishListener {

    private final SignupContract.View view;
    private final SignupContract.Model model;

    public SignupPresenter(SignupContract.View view, SignupContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void signUpButtonPressed(String username, String email, String password) {
        model.signUp(username, email, password, this);
    }

    @Override
    public void onSuccess(String message) {
        view.signUpSuccess();
    }

    @Override
    public void onError(String message) {
        view.displayError(message);
    }
}
