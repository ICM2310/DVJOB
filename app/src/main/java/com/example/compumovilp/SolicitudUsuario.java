package com.example.compumovilp;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.compumovilp.databinding.ActivitySolicitudUsuarioBinding;


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