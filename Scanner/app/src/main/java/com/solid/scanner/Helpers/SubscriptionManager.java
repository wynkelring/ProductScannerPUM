package com.solid.scanner.Helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SubscriptionManager {
    private String ean;
    private String subkey;
    private Context ctx;

    public SubscriptionManager(Context ctx, String ean, String subkey){
        this.ctx = ctx;
        this.ean = ean;
        this.subkey = subkey;
    }

    public void subscribe(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ean", ean);
            jsonObject.put("subKey", subkey);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(ctx));
        final String requestBody = jsonObject.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiAddress.SUBSCRIBE.getUrl(), response -> {
            try {
                JSONObject jsonObj = new JSONObject(response);
                boolean subscribedBefore = jsonObj.getBoolean("subscribedBefore");
                if(subscribedBefore){
                    Toast.makeText(ctx, "Zasubskrybowano wcześniej", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "Zasubskrybowano", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.i("VOLLEY:", error.toString())){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };
        requestQueue.add(stringRequest);
    }

    public void unsubscribe(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ean", ean);
            jsonObject.put("subKey", subkey);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(ctx));
        final String requestBody = jsonObject.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiAddress.UNSUBSCRIBE.getUrl(), response -> {
            try {
                JSONObject jsonObj = new JSONObject(response);
                boolean unsubscribed = jsonObj.getBoolean("unsubscribed");
                if(unsubscribed){
                    Toast.makeText(ctx, "Odsubskrybowałeś", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "Nie miałeś zasubskrybowanego", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.i("VOLLEY:", error.toString())){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };
        requestQueue.add(stringRequest);
    }
}



