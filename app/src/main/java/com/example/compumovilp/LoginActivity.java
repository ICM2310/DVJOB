package com.example.compumovilp;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.compumovilp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "lOGIN";
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    @Override
    protected void onResume() {
        super.onResume();
        mAuth.signOut();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();


        // Configurar BiometricPrompt
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                binding.usuarioIn.setText("andres@gmai.com");
                binding.contraIn.setText("andres");
                sigin();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Autenticación de huella digital fallida", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación de huella digital")
                .setSubtitle("Inicie sesión usando su huella digital")
                .setNegativeButtonText("Cancelar")
                .build();

        // Agregar listener al botón de autenticación de huella digital
        binding.huella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });


        binding.registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RegistroActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
        binding.iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    sigin();
                }
            }
        });

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        };
        SpannableString spannableString = new SpannableString("Registrarse");
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.registro.setText(spannableString);
        binding.registro.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        int roll = user.getRoll();

                        if (roll == 1) {
                            // El usuario es un coordinador
                            Intent intent = new Intent(getBaseContext(), PerfilCoordinador1.class);
                            intent.putExtra("user", currentUser.getDisplayName());
                            startActivity(intent);
                            finish();  // Opcional: para cerrar la actividad actual
                        } else {
                            // El usuario es un empleado
                            Intent intent = new Intent(getBaseContext(), PerfilUsuarioActivity.class);
                            intent.putExtra("user", currentUser.getDisplayName());
                            startActivity(intent);
                            finish();  // Opcional: para cerrar la actividad actual
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar posibles errores
                }
            });
        } else {
            binding.usuarioIn.setText("");
            binding.contraIn.setText("");
        }
    }

    private void sigin(){
        mAuth.signInWithEmailAndPassword(binding.usuarioIn.getText().toString().trim(), binding.contraIn.getText().toString().trim()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }
    private boolean validateForm() {
        boolean valid = true;
        String email =binding.usuarioIn.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.usuarioIn.setError("Required.");
            valid = false;
        } else if (!email.contains("@") || !email.contains(".") || email.length() < 5) {
            valid = false;
        } else {
            binding.usuarioIn.setError(null);
        }
        String password = binding.contraIn.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            binding.contraIn.setError("Required.");
            valid = false;
        } else {
            binding.contraIn.setError(null);
        }
        return valid;
    }
}