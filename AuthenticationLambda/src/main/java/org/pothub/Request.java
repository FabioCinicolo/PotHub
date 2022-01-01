package org.pothub;

import org.pothub.entities.User;

public class Request {

    private User user;

    private String confirmation_code;

    private String action;

    public Request(){}

    public Request(User user, String confirmation_code, String action) {
        this.user = user;
        this.confirmation_code = confirmation_code;
        this.action = action;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfirmation_code() {
        return confirmation_code;
    }

    public void setConfirmation_code(String confirmation_code) {
        this.confirmation_code = confirmation_code;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
