package com.example.compumovilp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.compumovilp.databinding.ActivityEvidenciaDetallesCoordinadorBinding;
import com.example.compumovilp.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Evidencia_DetallesCoordinador extends AppCompatActivity {

    private Uri uriCamera;

    private ActivityEvidenciaDetallesCoordinadorBinding binding;

    ActivityResultLauncher<String> mGetContentGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uriLocal) {
                    //Load image on a view...
                    try {
                        loadImage(uriLocal);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


    ActivityResultLauncher<Uri> mGetContentCamera =
            registerForActivityResult(new ActivityResultContracts.TakePicture(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            //Load image on a view
                            try {
                                loadImage(uriCamera);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEvidenciaDetallesCoordinadorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.CajasCompesacion, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.cajaCompensaciponSpinner.setAdapter(adapter);

        binding.btonArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContentGallery.launch("image/*");

            }
        });

        initFile();

        binding.btonCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContentCamera.launch(uriCamera);

            }
        });






    }

    public void loadImage(Uri uriLocal) throws FileNotFoundException {
        final InputStream imageStream = getContentResolver().openInputStream(uriLocal);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        binding.imageView3.setImageBitmap(selectedImage);
    }
    public void initFile(){
        File file = new File(getFilesDir(), "picFromCamera");
        uriCamera = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
    }
}