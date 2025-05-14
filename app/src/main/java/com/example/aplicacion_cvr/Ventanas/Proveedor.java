package com.example.aplicacion_cvr.Ventanas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion_cvr.Conexiones.IngresoSQL;
import com.example.aplicacion_cvr.Conexiones.ListaSQL;
import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Proveedor_e;
import com.example.aplicacion_cvr.R;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proveedor extends AppCompatActivity {
    private ConstraintLayout rootView;
    private List<Proveedor_e> listaProveedorLV;

    List<Map<String, String>> datosProveedor;
    TextInputEditText txtRUC,txtRazon, txtTipo;
    ListView lvProveedor;
    SimpleAdapter adapterProveedor;
    ImageButton btnIngresarProveedor;
    String[] fromProveedor = {"descripcion", "tipo"};
    int[] to = {android.R.id.text1, android.R.id.text2};
    String proveedorSeleccionado= "", data = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_proveedor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtRUC = findViewById(R.id.txtEditRUCProveedor);
        txtRazon = findViewById(R.id.txtEditRazonProveedor);
        txtTipo = findViewById(R.id.txtEditTipo);
        rootView = findViewById(R.id.main);
        lvProveedor = findViewById(R.id.listProveedor);

        listaProveedorLV = new ArrayList<>(ListaSQL.LProveedor());
        datosProveedor = new ArrayList<>();

        rootView.setOnTouchListener((v, event) -> {
            txtRUC.clearFocus();
            txtRazon.clearFocus();
            txtTipo.clearFocus();
            ocultarTeclado();
            return true;
        });

        lvProveedor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /** @noinspection unchecked*/
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> itemSeleccionado = (Map<String, String>) parent.getItemAtPosition(position);
                //String idb = itemSeleccionado.get("descripcion");
                String desc = itemSeleccionado.get("descripcion");
                proveedorSeleccionado = desc;
                seleccionListaProveedor(desc);
            }
        });

        adapterProveedor = new SimpleAdapter(this, datosProveedor,
                android.R.layout.simple_expandable_list_item_2, fromProveedor,to);

        lvProveedor.setAdapter(adapterProveedor);
        mostarProveedores();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.Clear_Info_Producto();
        this.finish();
    }

    public void mostarProveedores(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            listaProveedorLV = new ArrayList<>(ListaSQL.LProveedor());

            try {
                runOnUiThread(() -> {
                    datosProveedor.clear();
                    adapterProveedor.notifyDataSetChanged();

                    runOnUiThread(() -> {
                        for (Proveedor_e proveedor : listaProveedorLV) {
                            Map<String, String> mapa = new HashMap<>();
                            mapa.put("descripcion", proveedor.getNombre());
                            mapa.put("tipo", String.valueOf(proveedor.getCodigo() + " - " + proveedor.getTipoProveedor()));
                            datosProveedor.add(mapa);
                        }
                        adapterProveedor.notifyDataSetChanged();
                    });
                });
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR PROVEEDOR", "Excepcion: ", e);
                });
            }
        });
    }

    public void seleccionListaProveedor(String valor){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                runOnUiThread(() -> {
                    Toast.makeText(this, valor, Toast.LENGTH_SHORT).show();
                });
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR TIENDA", "Exception in connect method", e);
                });
            }
        });
    }


    //Botones
    public void BTN_AGREGAR_PROVEEDOR(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                if(Objects.requireNonNull(txtRazon.getText()).toString()
                        .trim().isEmpty() || Objects.requireNonNull(txtRUC.getText()).toString()
                        .trim().isEmpty() || Objects.requireNonNull(txtTipo.getText()).toString()
                        .trim().isEmpty()){

                    runOnUiThread(() -> {
                        lanzarMensaje("Proveedor","Complete los Datos");
                    });
                }

                else{
                    String rz = String.valueOf(txtRazon.getText());
                    String ruc = String.valueOf(txtRUC.getText());

                    runOnUiThread(() -> {
                        try {
                            Proveedor_e Pr1 = Utils.BusquedaProveedorConsulta(rz);
                            Proveedor_e Pr2 = Utils.BusquedaProveedorConsulta(ruc);

                            if (Pr1 == null && Pr2 == null){
                                IngresoSQL.IProveedor(
                                        Long.parseLong(String.valueOf(txtRUC.getText())),
                                        String.valueOf(txtRazon.getText()),
                                        String.valueOf(txtTipo.getText()));

                                txtRazon.setText("");
                                txtTipo.setText("");
                                txtRUC.setText("");
                                txtRazon.clearFocus();
                                txtTipo.clearFocus();
                                txtRUC.clearFocus();
                                ocultarTeclado();

                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Proveedor Agregado", Toast.LENGTH_SHORT).show();
                                });

                                mostarProveedores();
                            }
                            else{
                                txtRazon.setText("");
                                txtRUC.setText("");
                                txtRazon.clearFocus();
                                txtRUC.clearFocus();
                                txtTipo.clearFocus();
                                ocultarTeclado();

                                runOnUiThread(() -> {
                                    lanzarMensaje("Proveedor","Uno o Mas Datos Ya Estan Registrados");
                                });
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
//                runOnUiThread(() -> {
//
//                });
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR PROVEEDOR", "LISTA", e);
                });
            }
        });
    }

    public void BTN_AGREGAR_PRODUCTO(View view){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                Proveedor_e pro = Utils.BusquedaProveedorConsulta(proveedorSeleccionado);

                if(pro != null){
                    runOnUiThread(() -> {
                        long codigoProve = Objects.requireNonNull(pro).getCodigo();
                        String tipoProve = Objects.requireNonNull(pro).getTipoProveedor();
                        Utils.Info_Producto(tipoProve, codigoProve);

                        Intent intent = new Intent(this, IngresarProductos.class);
                        startActivity(intent);
                    });
                }
                else{
                    runOnUiThread(() -> {
                        lanzarMensaje("Proveedor","Elija un Proveedor");
                    });
                }
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR TIENDA", "LISTA", e);
                });
            }
        });
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