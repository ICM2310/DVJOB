package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.compumovilp.databinding.ActivityListaCordinadoresBinding;
import com.example.compumovilp.databinding.ActivityTramiteSolicitudesEmpleadoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TramiteSolicitudesEmpleado extends AppCompatActivity {
    private ActivityTramiteSolicitudesEmpleadoBinding binding;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTramiteSolicitudesEmpleadoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("solicitudEmpleado").child(user.getUid());

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Solicitud solicitud = new Solicitud();
                solicitud.setNombre(String.valueOf(binding.nombrePersona.getText()));
                solicitud.setFecha(binding.datePicker.getDayOfMonth() +"/"+ binding.datePicker.getMonth() + "/" +binding.datePicker.getYear() );
                solicitud.setHoraInicio(binding.horaInicio.getText().toString());
                solicitud.setHoraFinal(binding.horaFinal.getText().toString());
                solicitud.setEstadoSolicitud(0); //En espera
                String categoriaSeleccionada = (String) binding.spinnerCategory.getSelectedItem();
                solicitud.setCategoria(categoriaSeleccionada);
                solicitud.setDescripcion(binding.descripcionTxt.getText().toString());
                String solicitudId = databaseReference.push().getKey();
                solicitud.setID(solicitudId);
                databaseReference.child(solicitudId).setValue(solicitud);
                Toast.makeText(getApplicationContext(), "Solicitud enviada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TramiteSolicitudesEmpleado.this, TramiteSolicitudesEmpleado.class);
                startActivity(intent);

                /*binding.nombrePersona.setText(" ");
                binding.descripcionTxt.setText(" ");
                binding.horaFinal.setText(" ");
                binding.horaInicio.setText(" ");
                binding.spinnerCategory.setSelection(0);*/
            }
        });

    }

    //Menu
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogOut:
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}
