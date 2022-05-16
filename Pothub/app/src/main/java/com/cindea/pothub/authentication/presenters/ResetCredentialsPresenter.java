package com.cindea.pothub.authentication.presenters;
import com.cindea.pothub.authentication.ResetCredentialsContract;

public class ResetCredentialsPresenter implements ResetCredentialsContract.Presenter {

    private final ResetCredentialsContract.View view;
    private final ResetCredentialsContract.Model model;

    public ResetCredentialsPresenter(ResetCredentialsContract.View view, ResetCredentialsContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void requestCodeButtonClicked(String username) {
        model.requestCode(username, new ResetCredentialsContract.Model.OnFinishListener() {
            @Override
            public void onSuccess(String message) {
                view.changeFragmentToResetCRPassword();
            }

            @Override
            public void onError(String message) {
                view.displayUsernameNotFoundError(message);
            }
        });
    }

    @Override
    public void resetPasswordButtonClicked(String username, String password, String code) {

        model.resetPassword(username, password, code, new ResetCredentialsContract.Model.OnFinishListener() {

            @Override
            public void onSuccess(String message) {
                view.resetDone(message);
            }

            @Override
            public void onError(String message) {
                view.displayResetError(message);
            }

        });

    }
}
