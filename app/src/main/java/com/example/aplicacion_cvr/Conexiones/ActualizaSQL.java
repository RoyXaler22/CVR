package com.example.aplicacion_cvr.Conexiones;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActualizaSQL {
    static Connection Enlace = null;
    String info = "error";

    public static void UProducto(int codigoProducto, int cantidad){
        try{
            Enlace = ConnectionClass.CONN();
            String codigoSql = "USE [BD_CVR] UPDATE TB_PRODUCTO SET stockPro=" + cantidad + " WHERE codPro=" + codigoProducto;

            PreparedStatement variableSql = Enlace.prepareStatement(codigoSql);
            variableSql.executeUpdate();

        }catch(Exception e){
            Log.e("ERROR", "Exception in connect method", e);
        }
    }
}
