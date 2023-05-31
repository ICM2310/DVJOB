package com.example.compumovilp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificacionJobIntentService extends JobIntentService {

    private static final String CHANNEL_ID = "MyApp";
    static String userId;
    private NotificationManagerCompat notificationManager;
    private int notificationId = 0;

    private static DatabaseReference databaseReference;

    private FirebaseAuth mAuth;

    public void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificacionJobIntentService.class, 123, work);
        userId = work.getStringExtra("USER_ID");
        Log.d("notificaion", userId);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("solicitudEmpleado/").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Iterar sobre las solicitudes
                for (DataSnapshot solicitudSnapshot : snapshot.getChildren()) {
                    Solicitud solicitud = solicitudSnapshot.getValue(Solicitud.class);
                    if (solicitud != null && (solicitud.getEstadoSolicitud() == 1 || solicitud.getEstadoSolicitud() == 2)) {
                        sendNotification(solicitud);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        userId = intent.getStringExtra("USER_ID");


        // Resto de la lógica de notificación

        // Aquí puedes utilizar el userId para enviar la notificación al usuario correspondiente
    }

    private void sendNotification(Solicitud solicitud) {
        try {

            Context context = this;
            Log.d("FUNCIONANAA", "PerfilUsuario: " + ", Foto de perfil: " );

            // Construye la notificación utilizando NotificationCompat.Builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notificacion)
                    .setContentTitle("Nueva solicitud")
                    .setContentText("¡Tienes una nueva solicitud!")
                    .setAutoCancel(true);

            // Agrega una acción a la notificación (opcional)
            Intent intent = new Intent(context, PerfilUsuario.class);
            intent.putExtra("SOLICITUD_ID", solicitud.getID()); // Pasa el ID de la solicitud a la actividad de detalles
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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

}
