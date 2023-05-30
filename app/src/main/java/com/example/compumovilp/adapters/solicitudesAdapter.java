package com.example.compumovilp.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.compumovilp.OpcionesSeguimiento;
import com.example.compumovilp.R;
import com.example.compumovilp.Solicitud;
import com.example.compumovilp.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class solicitudesAdapter extends ArrayAdapter<Solicitud> {

    private  String userId;
    private Context mContext;
    private int mLayoutResourceId;
    private List<Solicitud> mSolicitudes;
    private FirebaseAuth mAuth;
    private String PATH_SOLICITUDES ="solicitudEmpleado/";
    public solicitudesAdapter(Context context, int layoutResourceId, List<Solicitud> solicitudes,String userId) {
        super(context, layoutResourceId, solicitudes);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mSolicitudes = solicitudes;
        this.userId = userId;

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayoutResourceId, parent, false);
        }

        Solicitud solicitud = mSolicitudes.get(position);
        ImageView profileImageView = convertView.findViewById(R.id.profileImage);

        TextView tipoSolicitud = convertView.findViewById(R.id.tipoSolicitud);
        TextView fechaFinal = convertView.findViewById(R.id.fechaFinal);



        if (solicitud.getCategoria().equals("Vacaciones")) {
            profileImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.vacacionicono, null));
        } else if (solicitud.getCategoria().equals("Licencia médica")) {
            profileImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.medico, null));
        } else if (solicitud.getCategoria().equals("Reembolso de gastos")) {
            profileImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rembolso, null));
        } else if (solicitud.getCategoria().equals("Capacitación y desarrollo")) {
            profileImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.capacitacion, null));
        }



        tipoSolicitud.setText(solicitud.getCategoria());
        fechaFinal.setText("Fecha de solicitud: "+ solicitud.getFecha());

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSolicitudDialog(solicitud);

            }
        });


        return convertView;
    }


    private void showSolicitudDialog(Solicitud solicitud) {
        // Crea un Dialog personalizado para mostrar los detalles de la solicitud
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_solicitud);
        TextView textViewFecha = dialog.findViewById(R.id.textViewFecha);
        TextView textViewCategoria = dialog.findViewById(R.id.textViewCategoria);
        TextView textViewDescripcion = dialog.findViewById(R.id.textViewDescripcion);
        TextView textViewHoraInicio = dialog.findViewById(R.id.textViewHoraInicio);
        TextView textViewHoraFinal = dialog.findViewById(R.id.textViewHoraFinal);
        ImageView imgAceptado = dialog.findViewById(R.id.imgAceptado);
        ImageView imgRechazado = dialog.findViewById(R.id.imgRechazado);

        // Establece los valores de los TextViews con los detalles de la solicitud
        textViewCategoria.setText(solicitud.getCategoria());
        textViewDescripcion.setText("Descripción: "+solicitud.getDescripcion());
        textViewFecha.setText("Fecha: " + solicitud.getFecha());
        textViewHoraInicio.setText("Hora de inicio: " + solicitud.getHoraInicio());
        textViewHoraFinal.setText("Hora de finalización: " + solicitud.getHoraFinal());

        imgAceptado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoSolicitud(solicitud.getID(), 1);
                Toast.makeText(mContext, "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                mSolicitudes.remove(solicitud); // Remove the request from the list
                notifyDataSetChanged(); // Refresh the adapter
            }
        });

        imgRechazado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoSolicitud(solicitud.getID(), 2);
                Toast.makeText(mContext, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                mSolicitudes.remove(solicitud); // Remove the request from the list
                notifyDataSetChanged(); // Refresh the adapter
            }
        });



        dialog.show();
    }

    private void actualizarEstadoSolicitud(String solicitudId, int nuevoEstado) {
        DatabaseReference solicitudRef = FirebaseDatabase.getInstance().getReference(PATH_SOLICITUDES).child(userId).child(solicitudId);

        // Actualiza el valor del estado de la solicitud
        solicitudRef.child("estadoSolicitud").setValue(nuevoEstado)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La actualización se realizó con éxito
                        Log.d("TAG", "Estado de solicitud actualizado correctamente");
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


}
