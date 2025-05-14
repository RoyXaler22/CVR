package com.example.aplicacion_cvr.Entidades;

public class Cliente extends Persona{
    private String razonSocial;
    private String Correo;
    private int IdCliente;

    public Cliente(long Codigo, String nombre, int IdCliente, String razonSocial, String Correo) {
        super(Codigo, nombre);
        this.razonSocial = razonSocial;
        this.Correo = Correo;
        this.IdCliente = IdCliente;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public void setCorreo(String Correo) {
        this.Correo = Correo;
    }

    public int getIdCliente() {
        return IdCliente;
    }

    public void setIdCliente(int idCliente) {
        IdCliente = idCliente;
    }
}
