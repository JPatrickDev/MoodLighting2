package com.jdp30.moodlighting2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

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

    public static void setCurrentIP(String newValue, Activity a) {
        SharedPreferences settings = a.getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("moodlighting_IP", newValue);
        editor.apply();
    }

    public static void API_setColor(int color, Activity activity) {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("color", colorIntToString(color));
        makeJSONRequest(activity, "lights/setColor", payload);
    }

    public static void API_startFade(Activity activity, Integer[] colors, double fadeTime, double pauseTime) {
        String[] colorStrings = new String[colors.length];
        for (int i = 0; i != colors.length; i++) {
            colorStrings[i] = colorIntToString(colors[i]);
        }
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("pauseTime", round(pauseTime));
        payload.put("fadeTime", round(fadeTime));
        payload.put("colours", colorStrings);
        makeJSONRequest(activity, "lights/start/fade", payload);
    }

    public static void API_stop(Activity activity) {
        makeGETRequest(activity,"lights/stop");
    }

    public static String colorIntToString(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return red + "," + green + "," + blue;
    }

    public static int colorStringToInt(String color) {
        String[] c = color.split(",");
        int red = Integer.parseInt(c[0]);
        int green = Integer.parseInt(c[1]);
        int blue = Integer.parseInt(c[2]);
        return Color.rgb(red,green,blue);
    }

    private static void makeGETRequestWithResponse(final Activity a, String endpoint, Response.Listener<JSONObject> req){
        String URL = "http://" + getCurrentIP(a) + ":" + port + "/" + endpoint;
        JsonObjectRequest request_json = new JsonObjectRequest(URL, null,
               req, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(a, "There was an error making the request.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        MoodLightingHomeActivity.instance.requestQueue.add(request_json);
    }

    public static void API_toggleLight(final Activity a, final String newColor){
        Response.Listener<JSONObject> infoResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String state = response.getString("type");
                    if (state.equalsIgnoreCase("NONE")) {
                            if(response.has("color")){
                                if(response.getString("color").equals("0,0,0")){
                                    API_setColor(colorStringToInt(newColor),a);
                                }else{
                                    API_setColor(colorStringToInt("0,0,0"),a);
                                }
                            }else{
                                API_setColor(colorStringToInt(newColor),a);
                            }
                    }else{
                        Response.Listener<JSONObject> stopResponse = new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                API_toggleLight(a,newColor);
                            }
                        };
                        makeGETRequestWithResponse(a,"lights/stop",stopResponse);
                    }
                }catch (Exception e){

                }
            }
        };
        makeGETRequestWithResponse(a,"lights/info",infoResponse);
    }

    private static void makeGETRequest(final Activity a, String endpoint) {
        String URL = "http://" + getCurrentIP(a) + ":" + port + "/" + endpoint;
        JsonObjectRequest request_json = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(a, "There was an error making the request.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        MoodLightingHomeActivity.instance.requestQueue.add(request_json);
    }

    private static void makeJSONRequest(final Activity a, String endpoint, HashMap<String, Object> data) {
        String URL = "http://" + getCurrentIP(a) + ":" + port + "/" + endpoint;
        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(a, "There was an error making the request.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        MoodLightingHomeActivity.instance.requestQueue.add(request_json);
    }

    public static double round(double value){
        return (double)Math.round(value * 1000d) / 1000d;
    }
}
