package com.example.compumovilp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.compumovilp.adapters.Tarea;
import com.example.compumovilp.databinding.ActivityListaTareasEmpleado1Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class listaTareasEmpleado1 extends AppCompatActivity {

    private ActivityListaTareasEmpleado1Binding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    public static final String PATH_TAREAS="tareasEmpleado/";
    public static final String PATH_USERS="users/";

    //Para manejar los eventos del sensor
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    private String solicitudDialogId;

    // Variables para controlar el tiempo y evitar la duplicación de toasts
    private long lastShakeTime = 0;
    private static final int SHAKE_INTERVAL = 2000; // Intervalo mínimo entre toasts (en milisegundos)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaTareasEmpleado1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //Listener del sensor

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // Obtén los valores de aceleración en los ejes x, y, z
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // Calcula la aceleración resultante
                double acceleration = Math.sqrt(x * x + y * y + z * z);

                // Define el umbral de aceleración para considerar que el dispositivo está siendo agitado
                double shakeThreshold = 15.0;

                // Obtiene el tiempo actual
                long currentTime = System.currentTimeMillis();

                // Si la aceleración es superior al umbral, muestra el Toast
                // Verifica si ha pasado suficiente tiempo desde el último shake para mostrar otro toast
                if (currentTime - lastShakeTime >= SHAKE_INTERVAL) {
                    // Si la aceleración es superior al umbral, muestra el Toast
                    if (acceleration > shakeThreshold) {
                        Toast.makeText(listaTareasEmpleado1.this, "¡Dispositivo agitado!", Toast.LENGTH_SHORT).show();

                        // Actualiza el último tiempo de agite
                        lastShakeTime = currentTime;
                        //Lógica de actualizacion de bd
                        actualizarEstadoTarea();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Método onAccuracyChanged implementado aquí
            }
        };

        initSolicitudes(user.getUid());
    }

    public void initSolicitudes(String uid) {
        database = FirebaseDatabase.getInstance();
        List<Tarea> tareas = new ArrayList<>();
        // Realiza una consulta para obtener las tareas del usuario
        //Solo muestra las tareas en espera
        Query query = database.getReference(PATH_TAREAS).child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Itera sobre los resultados de la consulta
                for (DataSnapshot tareaSnapshot : snapshot.getChildren()) {
                    // Obtiene el objeto Tarea correspondiente a la tarea actual
                    Tarea tarea = tareaSnapshot.getValue(Tarea.class);
                    if (tarea != null) {
                        tareas.add(tarea);
                        Log.d("TAG", "UID de la tarea: " + tarea.getId());
                    }
                }
                // Llamar a un método para procesar la lista de tareas
                procesarTareas(tareas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error de lectura de la base de datos
                Log.w("TAG", "Error al leer los datos.", error.toException());
            }
        });
    }

    public void procesarTareas(List<Tarea> tareas) {

        FirebaseUser user = mAuth.getCurrentUser();
        // Ejemplo: Imprimir las tareas en el log
        for (Tarea tarea : tareas) {
            View tareaView = null;
            if(tarea.getEstado()==0){
                tareaView = getLayoutInflater().inflate(R.layout.item_tarea, binding.linearLayout, false);
            }else{
                tareaView = getLayoutInflater().inflate(R.layout.item_tarea,binding.linearLayout2,false);
            }


            // Obtener referencias a los elementos de la vista
            ImageView imagenTarea = tareaView.findViewById(R.id.imagenTarea);
            TextView tipoTarea = tareaView.findViewById(R.id.tipoTarea);
            TextView fechaVencimiento = tareaView.findViewById(R.id.fechaVencimiento);

            String tipoTareaText = tarea.getTipoTarea();
            if (tipoTareaText.length() > 20) { // Definir el límite de caracteres para aplicar el salto de línea
                tipoTareaText = tipoTareaText.substring(0, 15) + "\n" + tipoTareaText.substring(15);
            }

            // Configurar la información de la tarea en los elementos de la vista
            tipoTarea.setText(tipoTareaText);
            fechaVencimiento.setText( tarea.getFechaVencimiento());
            switch (tarea.getTipoTarea()) {
                case "Verificación de inventario":
                    imagenTarea.setImageResource(R.drawable.inventario);
                    break;
                case "Atención al cliente":
                    imagenTarea.setImageResource(R.drawable.atencioncliente); //
                    break;
                case "Redacción de informes":
                    imagenTarea.setImageResource(R.drawable.informe);
                    break;
                case "Mantenimiento y reparación":
                    imagenTarea.setImageResource(R.drawable.mentenimiento);
                    break;
                default:
                    imagenTarea.setImageResource(R.drawable.capacitacion);

                    break;
            }


            // Agregar la vista de la tarea al LinearLayout
            imagenTarea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear un Intent para iniciar la nueva actividad
                    //Toast.makeText(listaTareasEmpleado1.this, tarea.getDescripcion(), Toast.LENGTH_SHORT).show();
                    showSolicitudDialog(tarea);
                    solicitudDialogId = tarea.getId();

                }
            });
            // Agregar la vista de la tarea al LinearLayout correspondiente
            if (tarea.getEstado() == 0) {
                binding.linearLayout.addView(tareaView);
            } else {
                binding.linearLayout2.addView(tareaView);
            }
        //Para obtener la imágen de perfil del usuario
            DatabaseReference myRef = database.getReference(PATH_USERS + user.getUid());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Verifica si el usuario existe en la base de datos
                    if (dataSnapshot.exists()) {
                        User myUser = dataSnapshot.getValue(User.class);
                        //binding.nombretxt.setText("¡Bienvenido " + myUser.getName()+"!");
                        // Carga la imagen utilizando Glide
                        String nombre = "Hola, " + myUser.getName();
                        binding.nombre.setText(nombre);
                        String email = myUser.getEmail();
                        binding.correo.setText(email);
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
        }
    }

    @SuppressLint("SetTextI18n")
    private void showSolicitudDialog(Tarea tarea) {
        // Crea un Dialog personalizado para mostrar los detalles de la solicitud
        Dialog dialog = new Dialog(listaTareasEmpleado1.this);
        dialog.setContentView(R.layout.dialog_tarea);
        TextView tareaTipo = dialog.findViewById(R.id.tipotarea);
        TextView fechaVencimiento = dialog.findViewById(R.id.fechavencimiento);
        TextView descripcion11 = dialog.findViewById(R.id.descripcion1);

        tareaTipo.setText("Tipo de tarea: "+ tarea.getTipoTarea());
        fechaVencimiento.setText("Fecha vencimiento: "+ tarea.getFechaVencimiento());
        descripcion11.setText("Descripción: "+tarea.getDescripcion());

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // Detiene la escucha del acelerómetro
                sensorManager.unregisterListener(sensorEventListener);
            }
        });

        dialog.show();
        if(tarea.getEstado()==0){
            // Registra el SensorEventListener para el acelerómetro
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }

    }

    private void actualizarEstadoTarea(){
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference solicitudRef = FirebaseDatabase.getInstance().getReference(PATH_TAREAS).child(user.getUid()).child(solicitudDialogId);
        // Actualiza el valor del estado de la solicitud
        solicitudRef.child("estado").setValue(1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La actualización se realizó con éxito
                        Log.d("TAG", "Estado de solicitud actualizado correctamente");
                        actualizarPantalla();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al realizar la actualización
                        Log.e("TAG", "Error al actualizar el estado de la solicitud", e);
                    }
                });
    }

    private void actualizarPantalla() {
        // Limpia las tareas existentes en el LinearLayout
        binding.linearLayout.removeAllViews();

        // Obtiene el usuario actual
        FirebaseUser user = mAuth.getCurrentUser();

        // Realiza una consulta para obtener las tareas del usuario (solo tareas en espera)
        Query query = database.getReference(PATH_TAREAS).child(user.getUid()).orderByChild("estado").equalTo(0);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Tarea> tareas = new ArrayList<>();

                // Itera sobre los resultados de la consulta
                for (DataSnapshot tareaSnapshot : snapshot.getChildren()) {
                    // Obtiene el objeto Tarea correspondiente a la tarea actual
                    Tarea tarea = tareaSnapshot.getValue(Tarea.class);
                    if (tarea != null) {
                        tareas.add(tarea);
                        Log.d("TAG", "UID de la tarea: " + tarea.getId());
                    }
                }

                // Procesa las nuevas tareas en la pantalla
                procesarTareas(tareas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error de lectura de la base de datos
                Log.w("TAG", "Error al leer los datos.", error.toException());
            }
        });
    }



}