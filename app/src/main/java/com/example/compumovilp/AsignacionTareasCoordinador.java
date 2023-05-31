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
import com.example.compumovilp.api.EmpleadoApi;
import com.example.compumovilp.databinding.ActivityAsignacionTareasCoordinadorBinding;
import com.example.compumovilp.databinding.ActivityTramiteSolicitudesEmpleadoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsignacionTareasCoordinador extends AppCompatActivity {

    EmpleadoApi empleadoApi = RetrofitClient.getRetrofitInstance().create(EmpleadoApi.class);
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

                // Envía la tarea al servidor REST.
                Call<Tarea> call = empleadoApi.createTask(tarea);
                call.enqueue(new Callback<Tarea>() {
                    @Override
                    public void onResponse(Call<Tarea> call, Response<Tarea> response) {
                        if (!response.isSuccessful()) {
                            Log.e("Error", "Hubo un error en la petición");
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Tarea Asignada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AsignacionTareasCoordinador.this, AsignacionTareasCoordinador.class);
                        intent.putExtra("USER_ID",empleadoUserId);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Tarea> call, Throwable t) {
                        Log.e("Error", "Hubo un error en la petición");
                    }
                });
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