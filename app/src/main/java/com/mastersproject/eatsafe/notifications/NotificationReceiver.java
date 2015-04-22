package com.mastersproject.eatsafe.notifications;

/**
 * Created by Nikita on 4/19/2015.
 */
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.mastersproject.eatsafe.eatsafeapp.R;

public class NotificationReceiver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView tv = new TextView(this);
        tv.setText("High Fructose Corn Sugar is not good for  health!");

        setContentView(tv);
    }
}