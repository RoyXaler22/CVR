package com.example.aplicacion_cvr.Conexiones;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IngresoSQL {
    static Connection Enlace = null;
    String info = "error";

    public static void ICliente(int idCliente,long ruc, String nombre, String razon, String correo){
        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] insert into TB_CLIENTE(idCli,rucCli,nombreCli,razonCli"
                    + ",correoCli)values(?,?,?,?,?)";

            PreparedStatement variableSql = Enlace.prepareStatement(codigoSql);

            variableSql.setInt(1, idCliente);
            variableSql.setLong(2, ruc);
            variableSql.setString(3, nombre.trim());
            variableSql.setString(4, razon.trim());
            variableSql.setString(5, correo.trim());

            variableSql.executeUpdate();
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
    }

    public static void IVenta(int factura, int producto, int cantidad, double precio){
        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] insert into TB_VENTA_PRODUCTO(codFac,codProd,cantidad"
                    + ",precioVenta)values(?,?,?,?)";

            PreparedStatement variableSql = Enlace.prepareStatement(codigoSql);

            variableSql.setInt(1, factura);
            variableSql.setInt(2, producto);
            variableSql.setInt(3, cantidad);
            variableSql.setDouble(4, precio);


            variableSql.executeUpdate();
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
    }

    public static void IFactura(int factura, String fechaEmision, int idCliente, long idTrabajador){
        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] insert into TB_FACTURA(codFact,fechaEFact,idClie"
                    + ",idTbja)values(?,?,?,?)";

            PreparedStatement variableSql = Enlace.prepareStatement(codigoSql);

            variableSql.setInt(1, factura);
            variableSql.setString(2, fechaEmision.trim());
            variableSql.setInt(3, idCliente);
            variableSql.setLong(4, idTrabajador);

            variableSql.executeUpdate();
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
    }

    public static void IProveedor(long rucProveedor, String Razon, String Tipo){
        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] insert into TB_PROVEEDOR(codProve,nombreProve,tipoProve"
                    + ")values(?,?,?)";

            PreparedStatement variableSql = Enlace.prepareStatement(codigoSql);

            variableSql.setLong(1, rucProveedor);
            variableSql.setString(2, Razon.trim());
            variableSql.setString(3, Tipo.trim());

            variableSql.executeUpdate();
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
    }

    public static void IProducto(int codigo, String Descripcion, double Precio, int Stock, String Tipo){
        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] insert into TB_PRODUCTO(codPro,descripPro,precioUPro"
                    + ",stockPro,tipoPro)values(?,?,?,?,?)";

            PreparedStatement variableSql = Enlace.prepareStatement(codigoSql);

            variableSql.setInt(1, codigo);
            variableSql.setString(2, Descripcion.trim());
            variableSql.setDouble(3, Precio);
            variableSql.setInt(4, Stock);
            variableSql.setString(5, Tipo.trim());

            variableSql.executeUpdate();
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
    }

    public static void IRegistro_Proveedor_Producto(long codigoProveedor, int codigoProducto, int Cantidad){
        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] insert into TB_PROVEEDOR_PRODUCTO(codProved,codProd,cantidad"
                    + ")values(?,?,?)";

            PreparedStatement variableSql = Enlace.prepareStatement(codigoSql);

            variableSql.setLong(1, codigoProveedor);
            variableSql.setInt(2, codigoProducto);
            variableSql.setInt(3, Cantidad);

            variableSql.executeUpdate();
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
    }

    public static void ITrabajador(int id, String Nombre, String Username, String Contrasena, String Tipo){
        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] insert into TB_TRABAJADOR(idTbj,nombreTbj,usuarioTbj"
                    + ",contrasenaTbj,tipoTbj)values(?,?,?,?,?)";

            PreparedStatement variableSql = Enlace.prepareStatement(codigoSql);

            variableSql.setLong(1, id);
            variableSql.setString(2, Nombre.trim());
            variableSql.setString(3, Username.trim());
            variableSql.setString(4, Contrasena.trim());
            variableSql.setString(5, Tipo.trim());

            variableSql.executeUpdate();
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
    }
}
