package com.example.aplicacion_cvr.Conexiones;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {

    static String classes =  "net.sourceforge.jtds.jdbc.Driver";
    protected static String ip = "10.252.9.37";  //10.253.6.65 192.168.18.153
    protected static String port = "1433";
    protected static String db = "BD_CVR";
    protected static String un = "admin_bd";
    protected static String password = "1234";

    public static Connection CONN(){
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        try {
            Class.forName(classes);
            String conUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + db;
            conn = DriverManager.getConnection(conUrl,un,password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public static boolean isConnected() {
        Connection conn = null;
        try {
            conn = CONN();
            if (conn != null && !conn.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            Log.e("ERROR", "Error checking database connection", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Log.e("ERROR", "Error closing database connection", e);
                }
            }
        }
        return false;
    }
}
