package com.example.aplicacion_cvr.Entidades;

public class Proveedor_e extends Persona {
    private String tipoProveedor;

    public Proveedor_e(long Codigo, String nombre, String tipoProveedor) {
        super(Codigo, nombre);
        this.tipoProveedor = tipoProveedor;
    }

    public String getTipoProveedor() {
        return tipoProveedor;
    }

    public void setTipoProveedor(String tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }
}
