package com.example.compumovilp;

public class Solicitud {

    private String nombre;
    private String fecha;
    private String horaInicio;
    private String horaFinal;
    private String categoria;
    private String descripcion;

    public int getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(int estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    private int estadoSolicitud; //0 en espera //1 aprobada //2 rechazada

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private String ID;
    // Métodos getter y setter para acceder a los atributos

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
