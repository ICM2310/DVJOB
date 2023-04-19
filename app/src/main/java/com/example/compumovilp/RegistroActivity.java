package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
import com.example.compumovilp.databinding.ActivityRegistroBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "Regis";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        binding.reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    if(binding.tyc.isChecked())
                    {
                        regis();
                    }
                    else {
                        Toast.makeText(RegistroActivity.this, "Acepte Terminos y condiciones", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void regis(){
        mAuth.createUserWithEmailAndPassword(binding.emailReg.getText().toString().trim(), binding.contraReg.getText().toString().trim()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null){ //Update user Info
                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(binding.nombreReg.getText().toString()+" "+binding.apellidoReg.getText().toString());
                                upcrb.setPhotoUri(Uri.parse("path/to/pic"));//fake uri, use Firebase Storage
                                user.updateProfile(upcrb.build());
                                updateUI(user);
                            }
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this, "error registrando usuario", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, task.getException().getMessage());
                        }
                    }
                });

    }
    private void updateUI(FirebaseUser currentUser){

        if(currentUser!=null){
            if(binding.checkBox.isChecked()){
                Log.e("Activado", "currentUser es nulo");
                Intent intent = new Intent(getBaseContext(), PerfilCoordinadorActivity.class);
                intent.putExtra("user", currentUser.getDisplayName());
                startActivity(intent);
            }else{
                Log.e("Desactivado", "textobox es nulo");
                Intent intent = new Intent(getBaseContext(), PerfilUsuarioActivity.class);
                intent.putExtra("user", currentUser.getDisplayName());
                startActivity(intent);
            }

        } else {
            binding.emailReg.setText("");
            binding.contraReg.setText("");
        }
    }
    private boolean validateForm() {
        boolean valid = true;
        String email =binding.emailReg.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.emailReg.setError("Required.");
            valid = false;
        } else if (!email.contains("@") || !email.contains(".") || email.length() < 5) {
            valid = false;
        } else {
            binding.emailReg.setError(null);
        }
        String password = binding.contraReg.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            binding.contraReg.setError("Required.");
            valid = false;
        } else {
            binding.contraReg.setError(null);
        }
        return valid;
    }
}