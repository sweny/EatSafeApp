package com.mastersproject.eatsafe.connectivity;

import android.util.Log;

/**
 * Created by sweny on 3/22/2015.
 */
public class StringValues {

    private static final String http_url = "http://0.0.0.0:8081";

    public static final String LOGIN_URL = http_url+"/login";
    public static final String REGISTER_URL = http_url+"/register";
    public static final String PASSWORD_RESET_CODE_URL = http_url+"/passwordResetCode";
    public static final String PASSWORD_RESET_URL = http_url+"/passwordReset";

    public static final String USERNAME_PASS_BLANK_REG = "Username or Password/Confirm Password cannot be blank!";
    public static final String PASS_CONFIRM_EQUAL = "Password must be equal to Confirm Password!";
    public static final String USERNAME_PASS_BLANK = "Username or Password cannot be blank!";



    public static void _(String Activity, String s){
        Log.d("Eat Safe Application: ", Activity + " : -----> :" + s);
    }
}
