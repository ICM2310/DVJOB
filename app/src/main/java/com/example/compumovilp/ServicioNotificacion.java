package com.example.compumovilp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

public class ServicioNotificacion extends Service {

    private static final String CHANNEL_ID = "MyApp";
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private NotificationManagerCompat notificationManager;
    private String userId;
    private int notificationId = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        // Recuperar el ID de SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = preferences.getString("USER_ID", null);
        mAuth = FirebaseAuth.getInstance();
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);
        setupDatabaseListener();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get the userId from the intent extras
        //userId = intent.getStringExtra("USER_ID");
        Log.d("funionaputito", userId);
        return super.onStartCommand(intent, flags, startId);
    }


    private void setupDatabaseListener() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("solicitudEmpleado/").child(userId);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Nueva solicitud agregada
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // El valor de una solicitud existente ha cambiado
                Solicitud solicitud = snapshot.getValue(Solicitud.class);
                if (solicitud != null && (solicitud.getEstadoSolicitud() == 1 || solicitud.getEstadoSolicitud() == 2)) {
                    sendNotification(solicitud);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Solicitud eliminada
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Solicitud movida
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error en la operación
            }
        });
    }



    private void sendNotification(Solicitud solicitud) {
        try {
            Context context = this;
            Log.d("FUNCIONANAA", "PerfilUsuario: " + ", Foto de perfil: " );
            String notificationText="Tu solicitud fue procesada";
            if (solicitud.getEstadoSolicitud() == 1) {
                notificationText = "¡Tu solicitud ha sido aprobada!";
            } else if (solicitud.getEstadoSolicitud() == 2) {
                notificationText = "¡Tu solicitud ha sido rechazada!";
            }
            // Construye la notificación utilizando NotificationCompat.Builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notificacion)
                    .setContentTitle("Actualización de solicitud")
                    .setContentText(notificationText)
                    .setAutoCancel(true);

            // Agrega una acción a la notificación (opcional)
            Intent intent = new Intent(context, PerfilUsuario.class);
            intent.putExtra("SOLICITUD_ID", solicitud.getID()); // Pasa el ID de la solicitud a la actividad de detalles
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);

            // Muestra la notificación utilizando NotificationManagerCompat
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            notificationManager.notify(notificationId++, builder.build());
        } catch (Exception e) {
            Log.e("NotificacionJobIntent", "Error al enviar la notificación: " + e.getMessage());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}