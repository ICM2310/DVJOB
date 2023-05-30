package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.compumovilp.adapters.solicitudesAdapter;
import com.example.compumovilp.adapters.usuariosAdapter;
import com.example.compumovilp.databinding.ActivityListaEmpleadosBinding;
import com.example.compumovilp.databinding.ActivityUbicacionTiempoRealEmpleadoBinding;
import com.example.compumovilp.databinding.ActivityValidarSolicitudesCoordinadorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ValidarSolicitudesCoordinador extends AppCompatActivity {
    String userId;
    String name;
    private ActivityValidarSolicitudesCoordinadorBinding binding;

    private FirebaseAuth mAuth;

    FirebaseDatabase database;

    public static final String PATH_SOLICITUDES="solicitudEmpleado/";

    private solicitudesAdapter mSolicitudAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityValidarSolicitudesCoordinadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra("USER_ID");
        Log.d("USERKD","WWW"+userId);

        mAuth = FirebaseAuth.getInstance();
        List<Solicitud> solicitudes = new ArrayList<>();
        mSolicitudAdapter = new solicitudesAdapter(this, R.layout.solicitudrow, solicitudes,userId);
        binding.listSolicitudes.setAdapter(mSolicitudAdapter);
        initSolicitudes();
    }
    public void initSolicitudes() {
        database = FirebaseDatabase.getInstance();
        List<Solicitud> solicitudes = new ArrayList<>();
        // Realiza una consulta para obtener las solicitudes del usuario
        Query query = database.getReference(PATH_SOLICITUDES).child(userId).orderByChild("estadoSolicitud").equalTo(0);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mSolicitudAdapter.clear();  // Borra la lista antes de agregar nuevas solicitudes
                // Itera sobre los resultados de la consulta
                for (DataSnapshot solicitudSnapshot : snapshot.getChildren()) {
                    // Obtiene el objeto Solicitud correspondiente a la solicitud actual
                    Solicitud solicitud = solicitudSnapshot.getValue(Solicitud.class);
                    if (solicitud != null) {
                        solicitudes.add(solicitud);
                    }
                }

                mSolicitudAdapter.addAll(solicitudes);  // Agrega todas las solicitudes
                mSolicitudAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error de lectura de la base de datos
                Log.w("TAG", "Error al leer los datos.", error.toException());
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