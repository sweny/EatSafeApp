package com.mastersproject.eatsafe.eatsafeapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mastersproject.eatsafe.connectivity.ServerConnectivity;
import com.mastersproject.eatsafe.connectivity.StringValues;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ActionBarActivity {

    EditText etUsername, etPassword, code, newPass, res_email;
    ImageButton idLogin;
    TextView tvSignUp;
    TextView tvForgotPass;
    String username, password, email_res_txt,jsonString;
    Dialog reset;
    Button cont, cont_code, cancel, cancel1;
    List<NameValuePair> params;
    SharedPreferences pref;
    ServerConnectivity sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _("Login Activity started!");
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        //getActionBar().hide();
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        sr = new ServerConnectivity();
        setContentView(R.layout.activity_login);
        setLoginButton();
        setTvSignUp();
        setTvForgotPass();
   }


    private void setLoginButton() {
        etUsername  = (EditText)    findViewById(R.id.etUsername);
        etPassword  = (EditText)    findViewById(R.id.etPassword);
        idLogin     = (ImageButton) findViewById(R.id.idLogin);
        idLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _("Login button clicked!");

                username = etUsername.getText() + "";
                password = etPassword.getText() + "";
                _("username:" + username);
                _("password:" + password);
                if (username.length() == 0 || password.length() == 0) {
                    Toast.makeText(LoginActivity.this, StringValues.USERNAME_PASS_BLANK, Toast.LENGTH_SHORT).show();
                    return;
                }
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));


                JSONObject json = sr.getJSON(StringValues.LOGIN_URL, params);
                if (json != null) {
                    try {
                        jsonString = json.getString("response");
                        if (json.getBoolean("res")) {
                            /*String token = json.getString("token");
                            String grav = json.getString("grav");
                            SharedPreferences.Editor edit = pref.edit();
                            //Storing Data using SharedPreferences
                            edit.putString("token", token);
                            edit.putString("grav", grav);
                            edit.commit();*/
                            startActivity(new Intent(LoginActivity.this, ScanActivity.class));
                            finish();
                        }
                        Toast.makeText(LoginActivity.this, jsonString, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setTvSignUp() {
        tvSignUp    = (TextView)    findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        });
    }

  private void setTvForgotPass() {
        tvForgotPass    = (TextView)    findViewById(R.id.tvforgotPass);
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset = new Dialog(LoginActivity.this);
                reset.setTitle("Reset Password");
                reset.setContentView(R.layout.reset_pass_init);
                cont = (Button)reset.findViewById(R.id.resbtn);
                cancel = (Button)reset.findViewById(R.id.cancelbtn);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reset.dismiss();
                    }
                });
                res_email = (EditText)reset.findViewById(R.id.resetEmail);

                cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        email_res_txt = res_email.getText().toString();
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("email", email_res_txt));

                        JSONObject json = sr.getJSON(StringValues.PASSWORD_RESET_CODE_URL, params);

                        if (json != null) {
                            try {
                                jsonString = json.getString("response");
                                if(json.getBoolean("res")){
                                    Log.e("JSON", jsonString);
                                    Toast.makeText(getApplication(), jsonString, Toast.LENGTH_LONG).show();
                                    reset.setContentView(R.layout.reset_pass_code);
                                    cont_code = (Button)reset.findViewById(R.id.btnContinue);
                                    code = (EditText)reset.findViewById(R.id.etCode);
                                    newPass = (EditText)reset.findViewById(R.id.etNewPass);
                                    cancel1 = (Button)reset.findViewById(R.id.btnCancel);
                                    cancel1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            reset.dismiss();
                                        }
                                    });
                                    cont_code.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String passCode = code.getText().toString();
                                            String newPassword = newPass.getText().toString();
                                            Log.e("Code",passCode);
                                            Log.e("New pass",newPassword);
                                            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                                            params.add(new BasicNameValuePair("email", email_res_txt));
                                            params.add(new BasicNameValuePair("code", passCode));
                                            params.add(new BasicNameValuePair("newPass", newPassword));
                                            JSONObject json = sr.getJSON(StringValues.PASSWORD_RESET_URL, params);

                                            if (json != null) {
                                                try {

                                                    jsonString = json.getString("response");
                                                    if(json.getBoolean("res")){
                                                        reset.dismiss();
                                                        Toast.makeText(getApplication(),jsonString,Toast.LENGTH_LONG).show();

                                                    }else{
                                                        Toast.makeText(getApplication(),jsonString,Toast.LENGTH_LONG).show();

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }else{

                                    Toast.makeText(getApplication(), jsonString, Toast.LENGTH_LONG).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

              reset.show();
            }
        });
    }

     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void _(String s){
        Log.d("Eat Safe Application","LoginActivity"+"#######"+s);
    }

}
