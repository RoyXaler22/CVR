package com.example.aplicacion_cvr.Ventanas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.R;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Menu extends AppCompatActivity {

    ConstraintLayout Almacen,Venta,Registro,Proveedor;
    TextView txtUser_info, lbGanancia;

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lbGanancia = findViewById(R.id.lbSoles);

        /* Iteraciones de botones */
        Almacen = findViewById(R.id.ly_btnAlmacen);
        Almacen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Almacen.setBackgroundResource(R.drawable.boton_menu_alter);
                        almacenCarga();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Almacen.setBackgroundResource(R.drawable.boton_menu);
                        return true;
                    default:
                        return false;
                }
            }
        });

        Venta = findViewById(R.id.ly_btnVenta);
        Venta.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Venta.setBackgroundResource(R.drawable.boton_menu_alter);
                        tiendaCarga();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Venta.setBackgroundResource(R.drawable.boton_menu);
                        return true;
                    default:
                        return false;
                }
            }
        });

        Registro = findViewById(R.id.ly_btnRegistros);
        Registro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Registro.setBackgroundResource(R.drawable.boton_menu_alter);
                        registrosCarga();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Registro.setBackgroundResource(R.drawable.boton_menu);
                        return true;
                    default:
                        return false;
                }
            }
        });

        Proveedor = findViewById(R.id.ly_btnProveedor);
        Proveedor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Proveedor.setBackgroundResource(R.drawable.boton_menu_alter);
                        proveedorCarga();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Proveedor.setBackgroundResource(R.drawable.boton_menu);
                        return true;
                    default:
                        return false;
                }
            }
        });

        /* Saludo */
        txtUser_info= findViewById(R.id.lbBienvenida);
        txtUser_info.setText(String.format("Buen Dia,\n%s", Utils.getNombreTrabajador()));

        try {
            LocalDate fechaActual = null;
            String fechaFormateada;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                fechaActual = LocalDate.now();
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yy");
                fechaFormateada = fechaActual.format(formato);
            } else {
                fechaFormateada = "";
            }
            lbGanancia.setText(String.format("S/. %.2f", Utils.gananciasDiarias(fechaFormateada)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void tiendaCarga() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            runOnUiThread(() -> {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Intent intent = new Intent(this, Tienda.class);
                startActivity(intent);
            });
        });
    }

    public void proveedorCarga() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            runOnUiThread(() -> {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Intent intent = new Intent(this, Proveedor.class);
                startActivity(intent);
            });
        });
    }

    public void registrosCarga() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            runOnUiThread(() -> {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Intent intent = new Intent(this, Registros.class);
                startActivity(intent);
            });
        });
    }

    public void almacenCarga() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            runOnUiThread(() -> {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Intent intent = new Intent(this, Almacen.class);
                startActivity(intent);
            });
        });
    }
}