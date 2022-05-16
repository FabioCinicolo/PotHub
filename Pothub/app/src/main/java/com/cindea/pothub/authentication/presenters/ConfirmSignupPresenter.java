package com.cindea.pothub.authentication.presenters;

import com.cindea.pothub.authentication.ConfirmSignupContract;

public class ConfirmSignupPresenter implements ConfirmSignupContract.Presenter, ConfirmSignupContract.Model.OnFinishListener {

    private final ConfirmSignupContract.View view;
    private final ConfirmSignupContract.Model model;

    public ConfirmSignupPresenter(ConfirmSignupContract.View view, ConfirmSignupContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void signUpClicked(String username, String code) {

        model.confirmSignUp(username, code, this);

    }

    @Override
    public void onSuccess(String message) {
        view.successSignUp();
    }

    @Override
    public void onError(String message) {
        view.displayError(message);
    }
}
