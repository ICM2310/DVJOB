package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.compumovilp.databinding.ActivityOpcionesSeguimientoBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OpcionesSeguimiento extends AppCompatActivity {

    private ActivityOpcionesSeguimientoBinding binding;
    private FirebaseAuth mAuth;

    DatabaseReference ref;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpcionesSeguimientoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = getIntent().getStringExtra("USER_ID");
        Log.d("UserID", "ID: " + userId  );
        mAuth = FirebaseAuth.getInstance();
        if (userId != null) {
            ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            // rest of the code that uses the ref object
        } else {
            Log.e("OpcionesSeguimiento", "USER_ID extra not provided in intent");
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User myUser = dataSnapshot.getValue(User.class);
                    binding.nombretxt.setText("Seguimiento a " + myUser.getName());

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
                // Manejar errores de Firebase
            }
        });

        binding.chatcord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), chat.class);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("real", userId);
                intent.putExtra("COORD_ID", user.getUid());
                startActivity(intent);
            }
        });

    }
}