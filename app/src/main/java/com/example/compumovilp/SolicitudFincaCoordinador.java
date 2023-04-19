package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.compumovilp.databinding.ActivitySolicitudFincaCoordinadorBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SolicitudFincaCoordinador extends AppCompatActivity {

    private ActivitySolicitudFincaCoordinadorBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        binding = ActivitySolicitudFincaCoordinadorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.verSolicitudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.rutaSedebton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapActivity.class);
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