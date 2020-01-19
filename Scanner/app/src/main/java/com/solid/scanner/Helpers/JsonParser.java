package com.solid.scanner.Helpers;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.solid.scanner.ApiObjects.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class JsonParser {
    private ArrayList<Product> productArray = new ArrayList<>();
    private ArrayList<String> productInfo = new ArrayList<>();
    private int method;
    private String url;
    private String jsonArray;
    private Context ctx;
    private ListView listView;

    public JsonParser(Context ctx, ListView listView, int method, String url, String jsonArray){
        this.method = method;
        this.url = url;
        this.jsonArray = jsonArray;
        this.ctx = ctx;
        this.listView = listView;
    }

    public void perform(){
        RequestQueue mQueue = Volley.newRequestQueue(Objects.requireNonNull(ctx));
        JsonObjectRequest request = new JsonObjectRequest(method, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray(this.jsonArray);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject product = jsonArray.getJSONObject(i);
                            Product prod = new Product(
                                    product.get("ean").toString(),
                                    product.get("name").toString(),
                                    product.get("quantity").toString());

                            productArray.add(prod);
                            productInfo.add(prod.toString());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, productInfo);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        mQueue.add(request);
    }
    public ArrayList<Product> getProductArray(){
        return this.productArray;
    }
}
