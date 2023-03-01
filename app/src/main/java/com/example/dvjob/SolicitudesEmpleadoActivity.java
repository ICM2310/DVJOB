package com.example.dvjob;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.dvjob.databinding.ActivityBusquedaVacanteBinding;
import com.example.dvjob.databinding.ActivitySolicitudesEmpleadoBinding;

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