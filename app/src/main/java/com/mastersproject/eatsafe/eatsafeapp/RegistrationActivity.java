package com.mastersproject.eatsafe.eatsafeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mastersproject.eatsafe.connectivity.ServerConnectivity;
import com.mastersproject.eatsafe.connectivity.StringValues;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RegistrationActivity extends ActionBarActivity {

    ImageButton ibRegister;
    EditText etUsername, etPassword, etConfirmPassword, etFname, etLname;
    String username, password, confirmPassword, fName, lName;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        setIbRegister();
    }

    private void setIbRegister(){
        ibRegister = (ImageButton) findViewById(R.id.idRegister);
        ibRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFname     = (EditText)    findViewById(R.id.etFname);
                etLname     = (EditText)    findViewById(R.id.etLname);
                etUsername  = (EditText)    findViewById(R.id.etUsername);
                etPassword  = (EditText)    findViewById(R.id.etPassword);
                etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
                username        = etUsername.getText()+"";
                password        = etPassword.getText()+"";
                fName           = etFname.getText()+"";
                lName           = etLname.getText()+"";
                confirmPassword = etConfirmPassword.getText()+"";

                if (username.length() == 0 || password.length() == 0 || confirmPassword.length() ==0) {
                    Toast.makeText(RegistrationActivity.this, StringValues.USERNAME_PASS_BLANK_REG, Toast.LENGTH_SHORT).show();
                    return;
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegistrationActivity.this, StringValues.PASS_CONFIRM_EQUAL, Toast.LENGTH_SHORT).show();
                    return;
                } else if (fName.length() == 0 || lName.length() == 0) {
                    Toast.makeText(RegistrationActivity.this, StringValues.fName_lName, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("fName", fName));
                params.add(new BasicNameValuePair("lName", lName));
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                ServerConnectivity sr = new ServerConnectivity();
                JSONObject json = sr.getJSON(StringValues.REGISTER_URL, params);
                if (json != null) {
                    try {
                        String jsonString = json.getString("response");
                        if (json.getBoolean("res")) {
                            /*String token = json.getString("token");
                            String grav = json.getString("grav");
                            SharedPreferences.Editor edit = pref.edit();
                            //Storing Data using SharedPreferences
                            edit.putString("token", token);
                            edit.putString("grav", grav);
                            edit.commit();*/
                            startActivity(new Intent(RegistrationActivity.this, ScanActivity.class));
                            finish();
                        }
                        Toast.makeText(RegistrationActivity.this, jsonString, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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
        Log.d("Eat Safe Application", "RegistrationActivity" + "#######" + s);
    }
}
