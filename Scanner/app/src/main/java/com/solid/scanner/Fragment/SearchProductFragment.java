package com.solid.scanner.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.solid.scanner.Helpers.ApiAddress;
import com.solid.scanner.Helpers.JsonParser;
import com.solid.scanner.Helpers.SubscriptionManager;
import com.solid.scanner.R;
import com.solid.scanner.Scanner.Portrait;
import com.solid.scanner.ApiObjects.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class SearchProductFragment extends Fragment {
    private ListView listView;
    private RequestQueue mQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button scanBC = view.findViewById(R.id.scanBarcode);
        scanBC.setOnClickListener(v -> onClick());
        listView = view.findViewById(R.id.idListView);
        mQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

        return view;
    }

    private void onClick(){
        scanNow();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if(result.getContents() == null){
                Toast.makeText(getActivity(), "Nie znaleziono kodu", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
                JsonParser jsonParser = new JsonParser(getActivity(), listView, Request.Method.GET, ApiAddress.SEARCH.getUrl().concat(result.getContents()), "product");
                jsonParser.perform();

                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    Product selectedFromList = jsonParser.getProductArray().get(position);
                    String subkey = prefs.getString("subkey", "");
                    if(subkey.isEmpty()) {
                        Toast.makeText(getActivity(), "Wprowadź w ustawieniach klucz subskrypcji", Toast.LENGTH_SHORT).show();
                    } else {
                        SubscriptionManager subscriptionManager = new SubscriptionManager(getActivity(), selectedFromList.getEan(), subkey);
                        subscriptionManager.subscribe();
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void scanNow(){
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(Portrait.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
        integrator.setPrompt("");
        integrator.initiateScan();
    }
}
