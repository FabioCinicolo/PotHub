package com.cindea.pothub.cognito;


import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class Cognito {

    private String USER_POOL_ID = "";
    private String CLIENT_ID = "";
    private String CLIENT_SECRET = "";
    private Regions AWS_REGION = Regions.EU_CENTRAL_1;

    private CognitoUserPool user_pool;

    public Cognito(Context context) {
        user_pool = new CognitoUserPool(context, this.USER_POOL_ID, this.CLIENT_ID, this.CLIENT_SECRET, this.AWS_REGION);
    }
    public CognitoUserPool getUser_pool() {
        return user_pool;
    }
}




