package com.mastersproject.eatsafe.connectivity;

import android.util.Log;

/**
 * Created by sweny on 3/22/2015.
 */
public class StringValues {

    private static final String http_url = "http://";

    public static final String LOGIN_URL = http_url+"/login";
    public static final String REGISTER_URL = http_url+"/register";
    public static final String PASSWORD_RESET_CODE_URL = http_url+"/passwordResetCode";
    public static final String PASSWORD_RESET_URL = http_url+"/passwordReset";



    public static void _(String Activity, String s){
        Log.d("Eat Safe Application: ", Activity + " : -----> :" + s);
    }
}
