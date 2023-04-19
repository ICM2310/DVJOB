package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.compumovilp.databinding.ActivityAtenderSolicitudCoordinadorBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AtenderSolicitudCoordinador extends AppCompatActivity {

    private ActivityAtenderSolicitudCoordinadorBinding binding;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();


        binding = ActivityAtenderSolicitudCoordinadorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.TipoDocumento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipoCedula.setAdapter(adapter);

        binding.buscarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Evidencia_DetallesCoordinador.class);
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