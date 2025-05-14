package com.example.aplicacion_cvr.Entidades;

public class Trabajador extends Persona {
    private String usuario;
    private String contrasena;
    private String tipo;

    public Trabajador(long Codigo, String nombre,String usuario,
                      String contrasena,String tipo) {
        super(Codigo, nombre);
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.tipo = tipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getTipo() {
        return tipo;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean Permisos(){
        return "Jefe".equals(this.tipo);
    }
}
