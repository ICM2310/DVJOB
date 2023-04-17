package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.compumovilp.databinding.ActivitySolicitudesEmpleadoBinding;


public class SolicitudesEmpleadoActivity extends AppCompatActivity {

    private ActivitySolicitudesEmpleadoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySolicitudesEmpleadoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



    }
}