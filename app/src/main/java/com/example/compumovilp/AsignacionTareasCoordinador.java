package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.compumovilp.adapters.Tarea;
import com.example.compumovilp.databinding.ActivityAsignacionTareasCoordinadorBinding;
import com.example.compumovilp.databinding.ActivityTramiteSolicitudesEmpleadoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AsignacionTareasCoordinador extends AppCompatActivity {

    private ActivityAsignacionTareasCoordinadorBinding binding;
    private FirebaseAuth mAuth;

    private String empleadoUserId;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAsignacionTareasCoordinadorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        empleadoUserId = getIntent().getStringExtra("USER_ID");
        Log.d("ASIGNACIONCOORDINADORUSER",""+empleadoUserId);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("tareasEmpleado").child(empleadoUserId);
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tarea tarea = new Tarea();
                String tipoTarea = (String) binding.spinnerTarea.getSelectedItem();
                tarea.setTipoTarea(tipoTarea);
                tarea.setDescripcion(binding.descripcionTxt.getText().toString());
                tarea.setEstado(0); //En espera
                tarea.setFechaVencimiento(binding.datePicker.getDayOfMonth() +"/"+ binding.datePicker.getMonth() + "/" +binding.datePicker.getYear() );
                String solicitudId = databaseReference.push().getKey();
                tarea.setId(solicitudId);
                databaseReference.child(solicitudId).setValue(tarea);
                Toast.makeText(getApplicationContext(), "Tarea Asignada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AsignacionTareasCoordinador.this, AsignacionTareasCoordinador.class);
                intent.putExtra("USER_ID",empleadoUserId);
                startActivity(intent);
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