package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.compumovilp.databinding.ActivityPerfilUsuarioBinding;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private ActivityPerfilUsuarioBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilUsuarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.solicitudFincaUsuarioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SolicitarUsuario.class);
                startActivity(intent);
            }
        });


        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}