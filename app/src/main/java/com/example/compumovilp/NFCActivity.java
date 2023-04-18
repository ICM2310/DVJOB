package com.example.compumovilp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



import com.example.compumovilp.databinding.ActivityNfcactivityBinding;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class NFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private String uniqueID;
    private String arrivalTime;
    private TextView tvUniqueID;
    private TextView tvArrivalTime;

    ActivityNfcactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcactivity);
        binding = ActivityNfcactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvUniqueID = binding.identificacion;
        tvArrivalTime = binding.hora;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC no está disponible en este dispositivo.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            Intent intent = new Intent(this, NFCActivity.class);
            intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            IntentFilter[] intentFilters = new IntentFilter[] {
                    new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            };
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                try {
                    ndef.connect();
                    NdefMessage ndefMessage = ndef.getNdefMessage();
                    if (ndefMessage != null) {
                        NdefRecord[] records = ndefMessage.getRecords();
                        for (NdefRecord record : records) {
                            // Procesa el contenido de cada NdefRecord
                            String uniqueID = new String(record.getPayload(), Charset.forName("UTF-8"));

                            // Registra la hora de llegada y realiza acciones adicionales
                            recordArrivalTime(uniqueID);

                            // Muestra un mensaje en la pantalla para confirmar la llegada
                            Toast.makeText(this, "Llegada registrada para el ID: " + uniqueID, Toast.LENGTH_SHORT).show();
                        }
                    }
                    ndef.close();
                } catch (IOException | FormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void recordArrivalTime(String uniqueID) {
        // Guarda el ID único y la hora de llegada en las variables
        this.uniqueID = uniqueID;
        this.arrivalTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Muestra el ID único y la hora de llegada en los elementos TextView
        tvUniqueID.setText("ID: " + uniqueID);
        tvArrivalTime.setText("Hora de llegada: " + arrivalTime);

        // Aquí podemos poner la información en una base de datos o enviarla a un servidor
    }


}


