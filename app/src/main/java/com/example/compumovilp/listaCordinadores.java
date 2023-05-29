package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.compumovilp.adapters.cordinadoresAdapter;
import com.example.compumovilp.adapters.usuariosAdapter;
import com.example.compumovilp.databinding.ActivityListaCordinadoresBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listaCordinadores extends AppCompatActivity{
    private ActivityListaCordinadoresBinding binding;
    private FirebaseAuth mAuth;

    FirebaseDatabase database;
    public static final String PATH_USERS="users/";

    private cordinadoresAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        binding = ActivityListaCordinadoresBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Manejo de usuarios
        mAuth = FirebaseAuth.getInstance();
        initUsers();
        List<User> users = new ArrayList<>();
        mUserAdapter = new cordinadoresAdapter(this, R.layout.contactsrow, users);
        binding.listDisponibles.setAdapter(mUserAdapter);

    }

    public void initUsers(){
        database = FirebaseDatabase.getInstance();
        List<User> users = new ArrayList<>();
        // Realiza una consulta para obtener los usuarios disponibles
        Query query = database.getReference(PATH_USERS).orderByChild("roll").equalTo(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Itera sobre los resultados de la consulta
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Obtiene el objeto User correspondiente al usuario actual
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        // Obtiene el nombre y la foto de perfil del usuario actual
                        String name = user.getName();
                        user.setUserID(userSnapshot.getKey());
                        //String photoUrl = user.getPhotoUrl();
                        // Aquí puedes hacer lo que necesites con el nombre y la foto del usuario actual
                        Log.d("DATAUSERAVALIABLE", "Nombre: " + name + ", Foto de perfil: " );
                        users.add(user);
                    }
                }
                mUserAdapter.clear();
                mUserAdapter.addAll(users);
                mUserAdapter.notifyDataSetChanged();
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