package com.example.dvjob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.dvjob.databinding.ActivityBusquedaVacanteBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;

public class BusquedaVacanteActivity extends AppCompatActivity {

    private ActivityBusquedaVacanteBinding binding;
    ArrayList<String> array = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusquedaVacanteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        JSONObject json = null;
        try {
            json = new JSONObject(loadJSONFromAsset());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JSONArray paisesJsonArray = null;
        try {
            paisesJsonArray = json.getJSONArray("oficinas");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        for(int i=0;i<paisesJsonArray.length();i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = paisesJsonArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                String capital = jsonObject.getString("nombre_oficina");
                array.add(capital);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, array);
        binding.oficinasListView.setAdapter(adapter);



    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("oficinas.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
