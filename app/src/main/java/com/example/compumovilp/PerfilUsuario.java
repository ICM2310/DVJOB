package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.compumovilp.databinding.ActivityPerfilUsuarioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilUsuario extends AppCompatActivity {

    private ActivityPerfilUsuarioBinding binding;
    private FirebaseAuth mAuth;
    public static final String PATH_USERS="users/";
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilUsuarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        //Obtener el usuario y la imagen de perfil de la base de datos

        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference(PATH_USERS + user.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Verifica si el usuario existe en la base de datos
                if (dataSnapshot.exists()) {
                    User myUser = dataSnapshot.getValue(User.class);
                    binding.nombretxt.setText("¡Bienvenido " + myUser.getName()+"!");

                    // Carga la imagen utilizando Glide
                    Glide.with(getApplicationContext())
                            .load(myUser.getProfileImageURL())
                            .into(binding.profileImage);
                } else {
                    // El usuario no existe en la base de datos
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ocurrió un error al leer los datos
            }
        });

        binding.chatemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), listaCordinadores.class);
                startActivity(intent);
            }
        });

    }



    //Inflate del menú
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