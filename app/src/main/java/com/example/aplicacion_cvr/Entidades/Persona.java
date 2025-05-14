package com.example.aplicacion_cvr.Entidades;

public class Persona {
    private long Codigo;
    private String nombre;

    public Persona(long Codigo, String nombre){
        this.Codigo = Codigo;
        this.nombre = nombre;
    }

    public long getCodigo() {
        return Codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setCodigo(long Codigo) {
        this.Codigo = Codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
