package com.mastersproject.eatsafe.connectivity;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sweny on 3/22/2015.
 */
public class Networking extends AsyncTask {

    public static final int NETWORK_STATE_REGISTER = 1;

    @Override
    protected Object doInBackground(Object[] params) {
        getJSON((String)params[0], (Integer)params[1], (HashMap<String, String>)params[2]);
        return null;
    }

    private void getJSON(String url, int state, HashMap<String, String> valueSet) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        boolean valid = false;
        switch (state) {
            case NETWORK_STATE_REGISTER:

                Set set = valueSet.entrySet();
                Iterator i = set.iterator();
                while(i.hasNext()) {
                    Map.Entry me = (Map.Entry)i.next();
                    postParameters.add(new BasicNameValuePair(me.getKey()+"" , me.getValue()+""));
                }
                valid = true;
                break;
            default:
                _("Warning: Unknown state!");
        }

        if (valid) {
            BufferedReader br;
            StringBuffer stringBuffer = new StringBuffer("");
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
                request.setEntity(entity);
                HttpResponse  response = httpClient.execute(request);
                br = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                String lineSeparator = System.getProperty("line.separator");
                while ((line = br.readLine()) != null) {
                    stringBuffer.append(line+lineSeparator);
                }
                br.close();
            } catch (UnsupportedEncodingException e) {
                _("UnsupportedEncodingException:"+e);
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                _("ClientProtocolException:"+e);
                e.printStackTrace();
            } catch (IOException e) {
                _("IOException:"+e);
                e.printStackTrace();
            }
            _("result:"+stringBuffer);

        } else {
            _("Valid not true");
        }
    }
    private void _(String s){
        Log.d("Eat Safe Application", "LoginActivity" + "#######" + s);
    }
}