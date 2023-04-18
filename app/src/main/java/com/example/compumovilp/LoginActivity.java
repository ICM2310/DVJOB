package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.compumovilp.databinding.ActivityLoginBinding;
import com.example.compumovilp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "lOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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
    private void updateUI(FirebaseUser currentUser){
        if(currentUser!=null){
            Intent intent = new Intent(getBaseContext(), PerfilUsuarioActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
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