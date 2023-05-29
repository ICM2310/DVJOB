package com.example.compumovilp;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.compumovilp.databinding.ActivityPerfilCoordinador1Binding;
import com.example.compumovilp.databinding.ActivityRegistroBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilCoordinador1 extends AppCompatActivity {
    private ActivityPerfilCoordinador1Binding binding;
    public static final String PATH_USERS="users/";

    public static final String PATH_USERSLOCATIONS="ubicacion/";
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private DatabaseReference ubicacionRef;




    private Boolean settingsOK;


    //Ubicacion

    ActivityResultLauncher<String> locationPermission = registerForActivityResult(

            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    initView(result);
                }
            });

    //Encedido programatico localizacion
    ActivityResultLauncher<IntentSenderRequest> getLocationSettings =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            //Log.i(IndexActivity.TAG, "Result from settings: "+result.getResultCode());
                            if (result.getResultCode() == RESULT_OK) {
                                settingsOK = true;
                                startLocationUpdates();
                            } else {
                                Log.e("GPS", "No GPS available");
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilCoordinador1Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Localizacion
        mLocationRequest = createLocationRequest();
        checkLocationSettings();
        //
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Obtener el usuario y la imagen de perfil de la base de datos
        FirebaseUser user = mAuth.getCurrentUser();

        //Loclizacion en segundo plano
        Intent intent = new Intent(PerfilCoordinador1.this, ServicioLocalizacion.class);
        //assert user != null;
        intent.putExtra("USER_ID", user.getUid());
        Log.d("USERID", "PerfilCoordinador: " + user.getUid() + ", Foto de perfil: " );
        startService(intent);
        ////
        DatabaseReference myRef = database.getReference(PATH_USERS + user.getUid());
        ubicacionRef = database.getReference("ubicacion").child(user.getUid());

        //Carga de imagen y clase ubicacion
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
                    ubiClass();
                } else {
                    // El usuario no existe en la base de datos
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ocurrió un error al leer los datos
            }
        });

        binding.listaEmpleadosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), listaEmpleados.class);
                startActivity(intent);
            }
        });
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        verifyPermission();
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

    //Clases ubicacion

    private void ubiClass(){
        FirebaseUser user = mAuth.getCurrentUser();
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setuID(user.getUid());
        ubicacion.setLatitude(0);
        ubicacion.setLongitude(0);
        ubicacion.setRoll(1);
        ubicacionRef.setValue(ubicacion);
    }
    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(30);
        return locationRequest;
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Log.i(IndexActivity.TAG, "GPS is ON");
                settingsOK = true;
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (((ApiException) e).getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest isr = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    getLocationSettings.launch(isr);
                } else {
                    Log.e("GPS","No GPS available");

                }
            }
        });
    }

    private void startLocationUpdates(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            if(settingsOK) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public void verifyPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //If i don´t have the permission
            //Si ya lo habia pedido pero lo nego
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                //Justification
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Nuestra aplicación requiere acceso a la ubicación de su dispositivo para poder proporcionarle información precisa y en tiempo real" )
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                locationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
                            }
                        });
                builder.create().show();
            }else {
                //Pide el permiso pero sin justificación
                locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }

        }else {

            //Si esta aceptadp
            initView(true);

        }
    }

    public void initView(Boolean result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(PerfilCoordinador1.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PerfilCoordinador1.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    //binding.latitud.setText(String.valueOf(location.getLatitude()));
                                    //binding.longitd.setText(String.valueOf(location.getLongitude()));
                                }
                            }
                        });
            }
        }).start();
    }



}