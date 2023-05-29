package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//creditos al chat https://www.youtube.com/watch?v=DFnxY_PEnYY
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.compumovilp.adapters.AdapterMensajes;
import com.example.compumovilp.adapters.MensajeEnviar;
import com.example.compumovilp.adapters.MensajeRecibir;
import com.example.compumovilp.databinding.ActivityChatBinding;
import com.example.compumovilp.databinding.ActivityPerfilCoordinador1Binding;
import com.example.compumovilp.databinding.ActivityPerfilUsuarioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class chat extends AppCompatActivity {

    private ImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;
    private String fotoPerfilCadena="";


    public static final String PATH_USERS="users/";
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    private DatabaseReference myRef;
    private String userId;
    String sala="chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        adapter = new AdapterMensajes(this);
        database = FirebaseDatabase.getInstance();
        fotoPerfil = (ImageView) findViewById(R.id.fotoPerfil);
        nombre = (TextView) findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);


        userId = getIntent().getStringExtra("USER_ID");
        myRef = database.getReference(PATH_USERS + user.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User myUser = dataSnapshot.getValue(User.class);
                    nombre.setText(myUser.getName());
                    fotoPerfilCadena = myUser.getProfileImageURL();
                    Glide.with(getApplicationContext())
                            .load(myUser.getProfileImageURL())
                            .into(fotoPerfil);
                    if(myUser.getRoll()==1){
                        sala = myUser.getUserID()+userId;
                    }else if(myUser.getRoll()==0){
                        sala = userId+myUser.getUserID();
                    }
                } else {
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReference = database.getReference(sala);

        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.push().setValue(new MensajeEnviar(txtMensaje.getText().toString(),nombre.getText().toString(),fotoPerfilCadena,"1", ServerValue.TIMESTAMP));
                txtMensaje.setText("");
            }
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                adapter.addMensaje(m);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }
}