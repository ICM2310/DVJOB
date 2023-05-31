package com.example.compumovilp.adapters;

public class Tarea {
    private String tipoTarea;
    private String fechaVencimiento;
    private String descripcion;

    public String getId() {
        return id;
    }

    private String id;


    public void setId(String id) {
        this.id = id;
    }

    public int getEstado() {
        return estado;
    }

    private int estado; //0: en espera //1 completado


    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(String tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}




