package com.mastersproject.eatsafe.connectivity;

import android.util.Log;

/**
 * Created by sweny on 3/22/2015.
 */
public class StringValues {

    private static final String http_url = "http://192.168.0.14:3000";

    public static final String LOGIN_URL = http_url+"/login";
    public static final String REGISTER_URL = http_url+"/register";
    public static final String PASSWORD_RESET_CODE_URL = http_url+"/passwordResetCode";
    public static final String PASSWORD_RESET_URL = http_url+"/passwordReset";
    public static final String GET_ITEM_INFO_URL = http_url+"/decodeUpc";

    public static final String USERNAME_PASS_BLANK_REG = "Username or Password/Confirm Password cannot be blank!";
    public static final String PASS_CONFIRM_EQUAL = "Password must be equal to Confirm Password!";
    public static final String USERNAME_PASS_BLANK = "Username or Password cannot be blank!";
    public static final String fName_lName  = "First or Last name cannot be blank!";



    public static void _(String Activity, String s){
        Log.d("Eat Safe Application: ", Activity + " : -----> :" + s);
    }
}
