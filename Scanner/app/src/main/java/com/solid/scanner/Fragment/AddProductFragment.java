package com.solid.scanner.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.solid.scanner.R;
import com.solid.scanner.Scanner.Portrait;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProductFragment extends Fragment {
    private static final String API_URL = "http://pumapp.000webhostapp.com/api.php?action=add";
    private EditText ean, name, quantity;
    private Button submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_addproduct, container, false);
        Button scanBC = view.findViewById(R.id.scanBarcode);
        scanBC.setOnClickListener(v -> onClick());

        submit = view.findViewById(R.id.btnSubmit);
        submit.setOnClickListener(v -> onClickSubmit());

        ean = view.findViewById(R.id.ean13);
        name = view.findViewById(R.id.name);
        quantity = view.findViewById(R.id.quantity);

        return view;
    }

    private void onClick(){
        scanNow();
    }

    private void onClickSubmit() {
        final String nameText = name.getText().toString().trim();
        final String eanText = ean.getText().toString().trim();
        final String quantityText = quantity.getText().toString().trim();

        if (TextUtils.isEmpty(eanText)) {
            ean.setError("Wprowadź EAN");
            ean.requestFocus();
            return;
        }

        if (eanText.length() < 13) {
            ean.setError("EAN musi posiadać 13 znaków");
            ean.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nameText)) {
            name.setError("Wprowadź nazwę");
            name.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityText)) {
            quantity.setError("Wprowadź ilość");
            quantity.requestFocus();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ean", eanText);
            jsonObject.put("name", nameText);
            jsonObject.put("quantity", quantityText);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        final String requestBody = jsonObject.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, response -> {
            try {
                JSONObject jsonObj = new JSONObject(response);
                String message = jsonObj.getString("message");
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                if(message.contains("Pomyślnie")){
                    ean.setText("");
                    name.setText("");
                    quantity.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.i("VOLLEY:", error.toString());
        }){
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if(result.getContents() == null){
                Toast.makeText(getActivity(), "Nie znaleziono kodu", Toast.LENGTH_SHORT).show();
            } else {
                ean.setText(result.getContents());
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
