package com.example.dvjob;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.dvjob.databinding.ActivityBusquedaVacanteBinding;
import com.example.dvjob.databinding.ActivitySolicitudUsuarioBinding;

public class SolicitudUsuario extends AppCompatActivity {

    private ActivitySolicitudUsuarioBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySolicitudUsuarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}