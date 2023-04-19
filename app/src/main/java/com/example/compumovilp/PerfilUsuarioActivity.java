package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.compumovilp.databinding.ActivityPerfilUsuarioBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private ActivityPerfilUsuarioBinding binding;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilUsuarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();



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
                mAuth.signOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

    }
}