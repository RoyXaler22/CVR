package com.example.aplicacion_cvr.Ventanas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion_cvr.Conexiones.ListaSQL;
import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.Entidades.Cliente;
import com.example.aplicacion_cvr.Entidades.Factura;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Proveedor_e;
import com.example.aplicacion_cvr.Entidades.Venta;
import com.example.aplicacion_cvr.R;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registros extends AppCompatActivity {
    private List<Factura> listaFacturaLV;
    private ConstraintLayout rootView;
    private Timer timer = new Timer();
    private final long DELAY = 300;

    TextInputEditText txtBuscarRegistro;
    List<Map<String, String>> datosFactura;
    ListView lvFactura;
    SimpleAdapter adapterFactura;
    ImageButton btnIngresarFactura;
    String[] fromFactura = {"factura", "fecha"};
    int[] to = {android.R.id.text1, android.R.id.text2};
    String facturaSeleccionado= "", data = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registros);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtBuscarRegistro = findViewById(R.id.txtEditBuscarRucFactura);
        lvFactura = findViewById(R.id.listFacturas);
        rootView = findViewById(R.id.main);

        rootView.setOnTouchListener((v, event) -> {
            txtBuscarRegistro.clearFocus();
            txtBuscarRegistro.setText("");
            mostrarListaFacturas();
            ocultarTeclado();
            return true;
        });

        txtBuscarRegistro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            busquedaProductosFacturas(s.toString());

                            if(Objects.requireNonNull(txtBuscarRegistro.getText()).toString().trim().isEmpty()){
                                mostrarListaFacturas();
                            }
                        });
                    }
                }, DELAY);
            }
        });

        listaFacturaLV = new ArrayList<>(ListaSQL.LFacturas());
        datosFactura = new ArrayList<>();

        lvFactura.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /** @noinspection unchecked*/
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> itemSeleccionado = (Map<String, String>) parent.getItemAtPosition(position);
                facturaSeleccionado = itemSeleccionado.get("factura");
                Utils.setFacturaSeleccionada(facturaSeleccionado);
                avance();
            }
        });

        adapterFactura = new SimpleAdapter(this, datosFactura,
                android.R.layout.simple_expandable_list_item_2, fromFactura,to);

        lvFactura.setAdapter(adapterFactura);
        mostrarListaFacturas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ejecutarSalida();
    }
    public void avance(){
        Intent intent = new Intent(this, Detalles.class);
        startActivity(intent);
    }

    public void ejecutarSalida(){
        this.finish();
    }

    public void mostrarListaFacturas(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            listaFacturaLV = new ArrayList<>(ListaSQL.LFacturas());

            try {
                runOnUiThread(() -> {
                    datosFactura.clear();
                    adapterFactura.notifyDataSetChanged();

                    runOnUiThread(() -> {
                        for (Factura factura : listaFacturaLV) {
                            Map<String, String> mapa = new HashMap<>();

                            Cliente obj = null;
                            try {
                                obj = Utils.BusquedaClienteConsulta(String.valueOf(factura.getidCli()));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                            mapa.put("factura", String.valueOf(factura.getCodigoFactura()));
                            mapa.put("fecha",  String.valueOf(factura.getFechaEmision()) + " - " +  String.valueOf(Objects.requireNonNull(obj).getRazonSocial()));
                            datosFactura.add(mapa);
                        }
                        adapterFactura.notifyDataSetChanged();
                    });
                });
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR FACTURA", "Excepcion: ", e);
                });
            }
        });
    }

    public void busquedaProductosFacturas(String valor){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            try {
                runOnUiThread(() -> {
                    datosFactura.clear();
                    adapterFactura.notifyDataSetChanged();

                    runOnUiThread(() -> {
                        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(valor)
                                + "\\b", Pattern.CASE_INSENSITIVE);

                        for (Factura factura: listaFacturaLV) {
                            Matcher matcher = pattern.matcher(
                                    String.valueOf(factura.getCodigoFactura()));

                            if (matcher.find() && !valor.trim().isEmpty()) {
                                Map<String, String> mapa = new HashMap<>();
                                mapa.put("factura", String.valueOf(factura.getCodigoFactura()));
                                mapa.put("fecha", String.valueOf(factura.getFechaEmision()));
                                datosFactura.add(mapa);
                            }
                        }
                        adapterFactura.notifyDataSetChanged();
                    });
                });
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR TIENDA", "Excepcion: ", e);
                });
            }
        });
    }

    //Botones
    public void BTN_BUSCAR_REGISTRO(View view) {
    }

    //Funciones Android
    public void lanzarMensaje(String Titulo, String Mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(Titulo)
                .setMessage(Mensaje)
                .setPositiveButton("OK", (dialog, which) -> {
                    //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public void ocultarTeclado(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }
}