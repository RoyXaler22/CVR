package com.example.aplicacion_cvr.Ventanas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion_cvr.Conexiones.ConnectionClass;
import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private ConstraintLayout rootView;
    Connection con = null;
    TextInputLayout txtUsuarioly, txtContrasenaly;
    TextInputEditText txtUsuarioa, txtContrasenaa;
    TextView txtResultado;
    String info = "error";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rootView = findViewById(R.id.main);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                txtUsuarioa.clearFocus();
                txtContrasenaa.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                return true;
            }
        });

        // Verificar la conexión a Internet
        if (estadoInternet()) {
            cargaInicial();
        } else {
            advertenciaInternet();
        }
    }

    /**
     * Inicializando Variables "cargaInicial()"
     */

    public void cargaInicial(){
        con = ConnectionClass.CONN();
        pruebaConexion();

        txtResultado = findViewById(R.id.lbTitulo_2);
        txtUsuarioly = findViewById(R.id.tilyUsuario);
        txtUsuarioa = findViewById(R.id.txtEditUsuario);
        txtContrasenaly = findViewById(R.id.tilyContrasena);
        txtContrasenaa = findViewById(R.id.txtEditContrasena);
    }

    /**
     * Boton de Login "BTN_LOGIN()"
     */

    public void BTN_LOGIN(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            if (con == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                });
            } else {
                try {
                    String codigoSql = "USE [BD_CVR] Select * from TB_TRABAJADOR where usuarioTbj=? and contrasenaTbj=?";
                    PreparedStatement variableSql = con.prepareStatement(codigoSql);
                    variableSql.setString(1, Objects.requireNonNull(txtUsuarioa.getText()).toString());
                    variableSql.setString(2, Objects.requireNonNull(txtContrasenaa.getText()).toString());

                    ResultSet consultaDatos = variableSql.executeQuery();

                    runOnUiThread(() -> {
                        try {
                            Thread.sleep(10);
                            if(consultaDatos.next()){
                                //Guardando Datos Consultados
                                Utils.setDatosTrabajador(consultaDatos.getString("nombreTbj"),
                                        consultaDatos.getString("tipoTbj"));

                                /* Lanzar Nueva Ventana */
                                Intent intent = new Intent(this, Bienvenida.class);
                                startActivity(intent);
                                //finish();
                            }
                            else{
                                //Lanzar de Error
                                txtUsuarioa.setText("");
                                txtContrasenaa.setText("");
                                txtUsuarioa.clearFocus();
                                txtContrasenaa.clearFocus();
                                Toast.makeText(this, "Datos Incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (InterruptedException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (SQLException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error en la consulta", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    /**
     * Pruebas de Conexion
     */
    public void pruebaConexion() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                if (con != null) info = "Conectado";
            } catch (Exception e) {
                Log.e("ERROR", "Exception in connect method", e);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
                Log.e("ERROR", "info");
            });
        });
    }
    public boolean estadoInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void advertenciaInternet() {
        new AlertDialog.Builder(this)
                .setTitle("Sin conexion a internet")
                .setMessage("Porfavor Revisa Tu Conexion A Internet.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}