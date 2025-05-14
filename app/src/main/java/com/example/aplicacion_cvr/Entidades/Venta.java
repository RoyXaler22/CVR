package com.example.aplicacion_cvr.Entidades;

public class Venta {
    private int cantidad;
    private double precioVenta;
    private int numeroFactura;
    private int codigoPro;

    public Venta(int numeroFactura,int codigoPro,int cantidad,double precioVenta){
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
        this.numeroFactura = numeroFactura;
        this.codigoPro = codigoPro;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public int getNumeroFactura() {
        return numeroFactura;
    }

    public int getCodigoPro() {
        return codigoPro;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public void setNumeroFactura(int numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public void setCodigoPro(int codigoPro) {
        this.codigoPro = codigoPro;
    }
}
