package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.compumovilp.databinding.ActivityVisualizarSolicitudesUsuarioBinding;


public class VisualizarSolicitudesUsuario extends AppCompatActivity {

    private ActivityVisualizarSolicitudesUsuarioBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisualizarSolicitudesUsuarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);    }
}