package com.jdp30.moodlighting2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Objects;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by jackp on 31/05/2018.
 */

public class Util {

    public static int port = 2806;
    public static String getCurrentIP(Activity a) {
        SharedPreferences settings = a.getPreferences(0);
        return settings.getString("moodlighting_IP", "192.168.0.177");
    }

    public static void setCurrentIP(String newValue,Activity a) {
        SharedPreferences settings = a.getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("moodlighting_IP", newValue);
        editor.apply();
    }

    public static void API_setColor(int color,Activity activity){
        Log.d("Moodlighting2","Setting");
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        HashMap<String,Object> payload = new HashMap<>();
        payload.put("color",red + "," + green + "," + blue);
        makeJSONRequest(activity,"lights/setColor",payload);
    }


    public static void makeJSONRequest(Activity a, String endpoint, HashMap<String,Object> data){

        String URL = "http://" + getCurrentIP(a) + ":" + port + "/" + endpoint;
        Log.d("MoodLighting2",URL);
        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error == null || error.networkResponse == null) {
                    Log.d("MoodLighting2",error.getMessage());
                    return;
                }

                String body = "ERROR GETTING ERROR.";
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data,"UTF-8");

                } catch (UnsupportedEncodingException e) {
                    // exception
                }
                Log.d("MoodLighting2",body);
            }
        });
        MoodLightingHomeActivity.instance.requestQueue.add(request_json);
    }

}
