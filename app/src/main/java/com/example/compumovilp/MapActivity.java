package com.example.compumovilp;

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
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compumovilp.databinding.ActivityMapBinding;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;


public class MapActivity extends AppCompatActivity {

    MapView map;
    GeoPoint startPoint;
    private ActivityMapBinding binding;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Boolean settingsOK;

    Marker longPressedMarker;

    RoadManager roadManager;

    Polyline roadOverlay;


    int cont = 0;



    ActivityResultLauncher<IntentSenderRequest> getLocationSettings =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            //Log.i(IndexActivity.TAG, "Result from settings: "+result.getResultCode());
                            if(result.getResultCode() == RESULT_OK){
                                settingsOK = true;
                                startLocationUpdates();
                            }else{
                                //binding.textView5.setText("GPS is off");
                            }
                        }
                    });



    ActivityResultLauncher<String> locationPermission = registerForActivityResult(

            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    initView(result);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        verifyPermission();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();

        Spinner spinner = findViewById(R.id.spinnerMap);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Sedes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        checkLocationSettings();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
            }
        };

        //OMS
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setContentView(R.layout.activity_map);
        map =findViewById(R.id.osmMap);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);


        //Obtener la dirección
        Geocoder mGeocoder = new Geocoder(getBaseContext());


        binding.spinnerMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String addressString = adapterView.getItemAtPosition(i).toString();

                if (!addressString.isEmpty()) {
                    try {
                        List<Address> addresses = mGeocoder.getFromLocationName(addressString, 2);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address addressResult = addresses.get(0);
                            LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                            if (map != null) {
                                GeoPoint markerPoint = new GeoPoint(addressResult.getLatitude(), addressResult.getLongitude());
                                Marker marker = new Marker(map);
                                marker.setTitle("Mi Marcador");
                                @SuppressLint("UseCompatLoadingForDrawables") Drawable myIcon = getResources().getDrawable(R.drawable.pin);
                                marker.setIcon(myIcon);
                                marker.setPosition(markerPoint);
                                marker.setAnchor(Marker.ANCHOR_CENTER,
                                        Marker.ANCHOR_BOTTOM);
                                map.getOverlays().add(marker);
                                map.invalidate();
                                IMapController mapController = map.getController();
                                mapController.setZoom(18.0);
                                if(cont ==0){
                                    mapController.setCenter(startPoint);
                                }else{
                                    mapController.setCenter(markerPoint);
                                    cont =1;
                                }
                                //drawRoute(startPoint,  markerPoint);
                                if (startPoint != null) {
                                    drawRoute(startPoint, markerPoint);
                                }
                            }
                        } else {
                            Toast.makeText(MapActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MapActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //LonClick

        //RUTA BONUS PACK
        roadManager = new OSRMRoadManager(this, "ANDROID");
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }


    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        IMapController mapController = map.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(this.startPoint);
        Marker marker = new Marker(map);
        marker.setTitle("Mi Marcador");
        @SuppressLint("UseCompatLoadingForDrawables") Drawable myIcon = getResources().getDrawable(R.drawable.pin);
        marker.setIcon(myIcon);
        //marker.setPosition(this.startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        //mapController.animateTo(bogota)
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        stopLocationUpdates();

    }

    //LonClick




    private Marker createMarker(GeoPoint p, String title, String desc, int iconID){
        Marker marker = null;
        if(map!=null) {
            marker = new Marker(map);
            if (title != null) marker.setTitle(title);
            if (desc != null) marker.setSubDescription(desc);
            if (iconID != 0) {
                Drawable myIcon = getResources().getDrawable(iconID, this.getTheme());
                marker.setIcon(myIcon);
            }
            marker.setPosition(p);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        }
        return marker;
    }



    //RUTA COMO OMS

    private void drawRoute(GeoPoint start, GeoPoint finish){
        ArrayList<GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(start);
        routePoints.add(finish);
        Road road = roadManager.getRoad(routePoints);
        //Log.i(IndexActivity.TAG, "Duration: "+road.mDuration/60+" min");
        if(map!=null){
            if(roadOverlay!=null){
                map.getOverlays().remove(roadOverlay);
            }
            roadOverlay = RoadManager.buildRoadOverlay(road);
            roadOverlay.getOutlinePaint().setColor(Color.RED);
            roadOverlay.getOutlinePaint().setStrokeWidth(10);
            map.getOverlays().add(roadOverlay);
        }

        Toast toast = Toast.makeText(this, "Route length: "+road.mLength+" klm" + "Duration:"+road.mDuration/60+" min", Toast.LENGTH_LONG);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }



    public void verifyPermission(){

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                        }
                    }
                });
    }


    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(30);
        return locationRequest;
    }
    private void checkLocationSettings(){
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
                if(((ApiException) e).getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED){
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest isr = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    getLocationSettings.launch(isr);
                }else {
                    //binding.textView5.setText("No GPS available");
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



}