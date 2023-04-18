package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.compumovilp.databinding.ActivityAtenderSolicitudCoordinadorBinding;
import com.example.compumovilp.databinding.ActivityMainBinding;

public class AtenderSolicitudCoordinador extends AppCompatActivity {

    private ActivityAtenderSolicitudCoordinadorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }
}