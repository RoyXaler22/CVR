package com.example.aplicacion_cvr.Conexiones;

import android.util.Log;

import com.example.aplicacion_cvr.Entidades.Cliente;
import com.example.aplicacion_cvr.Entidades.Factura;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Proveedor_e;
import com.example.aplicacion_cvr.Entidades.Trabajador;
import com.example.aplicacion_cvr.Entidades.Venta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ListaSQL {

    static Connection Enlace = null;
    String info = "error";

    public static ArrayList<Producto> LProductos(){
        ArrayList<Producto> lista = new ArrayList<>();

        try {
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] SELECT * FROM TB_PRODUCTO";
            Statement variableSql = Enlace.createStatement();
            ResultSet consultaDatos = variableSql.executeQuery(codigoSql);

            Producto obj;

            while(consultaDatos.next()){
                obj = new Producto(consultaDatos.getInt("codPro"),
                        consultaDatos.getString("descripPro").trim(),
                        consultaDatos.getDouble("precioUPro"),
                        consultaDatos.getInt("stockPro"),
                        consultaDatos.getString("tipoPro").trim()
                );
                lista.add(obj);
            }

        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }

        return lista;
    }

    public static ArrayList<Trabajador> LTrabajadores() throws SQLException{
        ArrayList<Trabajador> lista = new ArrayList<>();

        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] SELECT * FROM TB_TRABAJADOR";
            Statement variableSql = Enlace.createStatement();
            ResultSet conts = variableSql.executeQuery(codigoSql);

            Trabajador obj;
            while(conts.next()){
                obj = new Trabajador(conts.getInt("idTbj"),
                        conts.getString("nombreTbj").trim(),
                        conts.getString("usuarioTbj").trim(),
                        conts.getString("contrasenaTbj").trim(),
                        conts.getString("tipoTbj").trim()
                );

                lista.add(obj);
            }
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
        return lista;
    }

    public static ArrayList<Factura> LFacturas(){
        ArrayList<Factura> lista = new ArrayList<>();

        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] SELECT * FROM TB_FACTURA";
            Statement variableSql = Enlace.createStatement();
            ResultSet conts = variableSql.executeQuery(codigoSql);

            Factura obj;
            while(conts.next()){
                obj = new Factura(conts.getInt("codFact"),
                        conts.getString("fechaEFact").trim(),
                        conts.getInt("idClie"),
                        conts.getLong("idTbja")
                );

                lista.add(obj);
            }
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
        return lista;
    }

    public static ArrayList<Proveedor_e> LProveedor(){
        ArrayList<Proveedor_e> lista = new ArrayList<>();

        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] SELECT * FROM TB_PROVEEDOR";
            Statement variableSql = Enlace.createStatement();
            ResultSet conts = variableSql.executeQuery(codigoSql);

            Proveedor_e obj;
            while(conts.next()){
                obj = new Proveedor_e(conts.getLong("codProve"),
                        conts.getString("nombreProve").trim(),
                        conts.getString("tipoProve").trim()
                );

                lista.add(obj);
            }
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
        return lista;
    }

    public static ArrayList<Venta> LVenta() throws SQLException{
        ArrayList<Venta> lista = new ArrayList<>();

        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] SELECT * FROM TB_VENTA_PRODUCTO";
            Statement variableSql = Enlace.createStatement();
            ResultSet conts = variableSql.executeQuery(codigoSql);

            Venta obj;
            while(conts.next()){
                obj = new Venta(conts.getInt("codFac"),
                        conts.getInt("codProd"),
                        conts.getInt("cantidad"),
                        conts.getDouble("precioVenta")
                );

                lista.add(obj);
            }
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method", e);
        }
        return lista;
    }

    public static ArrayList<Cliente> LClientes() throws SQLException{
        ArrayList<Cliente> lista = new ArrayList<>();

        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] SELECT * FROM TB_CLIENTE";
            Statement variableSql = Enlace.createStatement();
            ResultSet conts = variableSql.executeQuery(codigoSql);

            Cliente obj;
            while(conts.next()){
                obj = new Cliente(conts.getLong("rucCli"),
                        conts.getString("nombreCli"),
                        conts.getInt("idCli"),
                        conts.getString("razonCli"),
                        conts.getString("correoCli")
                );

                lista.add(obj);
            }
        } catch (SQLException e) {
            Log.e("ERROR", "Exception in connect method Clientes Lista", e);
        }
        return lista;
    }
}
