package com.example.dvjob;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.dvjob.databinding.ActivityRegistroSolicitudBinding;
import com.example.dvjob.databinding.ActivityVisualizarSolicitudesUsuarioBinding;

public class RegistroSolicitud extends AppCompatActivity {
    private ActivityRegistroSolicitudBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroSolicitudBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}