package com.example.compumovilp.api;

import com.example.compumovilp.User;
import com.example.compumovilp.adapters.Tarea;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EmpleadoApi {
    @GET("empleados/")
    Call<List<User>> getAllEmpleados();

    @GET("empleados/{id}")
    Call<User> getEmpleadoById(@Path("id") Long id);

    @POST("empleados/")
    Call<User> createEmpleado(@Body User newEmpleado);

    @PUT("empleados/{id}")
    Call<User> updateEmpleado(@Path("id") Long id, @Body User empleado);

    @DELETE("empleados/{id}")
    Call<Void> deleteEmpleado(@Path("id") Long id);

    @POST("tareas/")
    Call<Tarea> createTask(@Body Tarea tarea);
}
