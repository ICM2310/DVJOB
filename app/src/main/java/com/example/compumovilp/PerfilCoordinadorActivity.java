package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.compumovilp.databinding.ActivityLoginBinding;
import com.example.compumovilp.databinding.ActivityPerfilCoordinadorBinding;

public class PerfilCoordinadorActivity extends AppCompatActivity {

    private ActivityPerfilCoordinadorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilCoordinadorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.solicitudFincaUsuarioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SolicitudFincaCoordinador.class);
                startActivity(intent);
            }
        });


        binding.atenderSolicitudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AtenderSolicitudCoordinador.class);
                startActivity(intent);
            }
        });

    }
}