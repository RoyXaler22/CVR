package com.example.aplicacion_cvr.Entidades;

public class Producto {
    private int codigoProducto;
    private String descripcionProducto;
    private double precioUnitario;
    private int stockProducto;
    private String tipoProducto;


    public Producto(int codigoProducto,String descripcionProducto,
                    double precioUnitario,int stockProducto,String tipoProducto){
        this.codigoProducto = codigoProducto;
        this.descripcionProducto = descripcionProducto;
        this.precioUnitario = precioUnitario;
        this.stockProducto = stockProducto;
        this.tipoProducto = tipoProducto;
    }


    public int getCodigoProducto() {
        return codigoProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public int getStockProducto() {
        return stockProducto;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setStockProducto(int stockProducto) {
        this.stockProducto = stockProducto;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public boolean Corroboracion(){
        return stockProducto != 0;
    }
}
