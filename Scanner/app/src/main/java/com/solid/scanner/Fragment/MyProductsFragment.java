package com.solid.scanner.Fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.solid.scanner.ApiObjects.Product;
import com.solid.scanner.Helpers.ApiAddress;
import com.solid.scanner.Helpers.JsonParser;
import com.solid.scanner.Helpers.SubscriptionManager;
import com.solid.scanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MyProductsFragment extends Fragment {
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        View view = inflater.inflate(R.layout.fragment_all_products, container, false);
        RequestQueue mQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        listView = view.findViewById(R.id.idListView);

        JsonParser jsonParser = new JsonParser(getActivity(), listView, Request.Method.GET, ApiAddress.MY.getUrl().concat(prefs.getString("subkey", "")), "subscribe");
        jsonParser.perform();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Product selectedFromList = (Product) jsonParser.getProductArray().get(position);
            String subkey = prefs.getString("subkey", "");
            if(subkey.isEmpty()) {
                Toast.makeText(getActivity(), "Wprowadź w ustawieniach klucz subskrypcji", Toast.LENGTH_SHORT).show();
            } else {
                SubscriptionManager subscriptionManager = new SubscriptionManager(getActivity(), selectedFromList.getEan(), subkey);
                subscriptionManager.unsubscribe();
            }

        });

        return view;
    }

}
