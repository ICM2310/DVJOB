package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.compumovilp.databinding.ActivityEvidenciaDetallesCoordinadorBinding;
import com.example.compumovilp.databinding.ActivityMainBinding;

public class Evidencia_DetallesCoordinador extends AppCompatActivity {

    private ActivityEvidenciaDetallesCoordinadorBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEvidenciaDetallesCoordinadorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.CajasCompesacion, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.cajaCompensaciponSpinner.setAdapter(adapter);


    }
}