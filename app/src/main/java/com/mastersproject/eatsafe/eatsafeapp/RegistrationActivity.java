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
    EditText etUsername;
    EditText etPassword;
    EditText etConfirmPassword;
    String username;
    String password;
    String confirmPassword;
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

                username        = etUsername.getText()+"";
                password        = etPassword.getText()+"";
                confirmPassword = etConfirmPassword.getText()+"";
                _("username:"+username);
                _("password:"+password);
                if (username.length() == 0 || password.length() == 0 || confirmPassword.length() ==0) {
                    Toast.makeText(RegistrationActivity.this, "Username or Password/Confirm Password cannot be blank!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegistrationActivity.this, "Password must be equal to Confirm Password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                ServerConnectivity sr = new ServerConnectivity();
                JSONObject json = sr.getJSON(StringValues.REGISTER_URL, params);
                if (json != null) {
                    try {
                        String jsonString = json.getString("response");
                        if (json.getBoolean("res")) {
                            String token = json.getString("token");
                            String grav = json.getString("grav");
                            SharedPreferences.Editor edit = pref.edit();
                            //Storing Data using SharedPreferences
                            edit.putString("token", token);
                            edit.putString("grav", grav);
                            edit.commit();
                            startActivity(new Intent(RegistrationActivity.this, ProfileActivity.class));
                            finish();
                        }
                        Toast.makeText(getApplication(), jsonString, Toast.LENGTH_LONG).show();
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
        Log.d("Eat Safe Application", "LoginActivity" + "#######" + s);
    }

}
