package com.example.aplicacion_cvr.Entidades;

public class Factura {
    private int codigoFactura;
    private String fechaEmision;
    private int idClie;
    private long idTrabajador;

    public Factura(int codigoFactura,String fechaEmision,int idClie,long idTrabajador){
        this.codigoFactura = codigoFactura;
        this.fechaEmision = fechaEmision;
        this.idClie = idClie;
        this.idTrabajador = idTrabajador;
    }

    public int getCodigoFactura() {
        return codigoFactura;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public int getidCli() {
        return idClie;
    }

    public long getIdTrabajador() {
        return idTrabajador;
    }

    public void setCodigoFactura(int codigoFactura) {
        this.codigoFactura = codigoFactura;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public void setidCli(int idClie) {
        this.idClie = idClie;
    }

    public void setIdTrabajador(long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }
}
