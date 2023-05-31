package com.example.compumovilp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    //El uso de 10.0.2.2 como dirección IP es para acceder a tu máquina local desde el emulador de Android.
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}