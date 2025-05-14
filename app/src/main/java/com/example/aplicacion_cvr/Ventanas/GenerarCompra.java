package com.example.aplicacion_cvr.Ventanas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion_cvr.Api.ApiSunat;
import com.example.aplicacion_cvr.Api.ConfiguracionApiSunat;
import com.example.aplicacion_cvr.Conexiones.ActualizaSQL;
import com.example.aplicacion_cvr.Conexiones.IngresoSQL;
import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.Entidades.Cliente;
import com.example.aplicacion_cvr.Entidades.Factura;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Trabajador;
import com.example.aplicacion_cvr.Entidades.Venta;
import com.example.aplicacion_cvr.Entidades_Api.Empresa;
import com.example.aplicacion_cvr.R;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLException;
import java.sql.Struct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GenerarCompra extends AppCompatActivity {

    private ConstraintLayout rootView;

    String RazonSocial_GN = "";

    TextInputEditText txtRUC,txtRazon, txtNombre, txtCorreo;
    ImageButton btnCliente, btnGenerarCompra, btnAtras, btnManual;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generar_compra);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rootView = findViewById(R.id.main);
        btnCliente = findViewById(R.id.btnCliente);
        btnGenerarCompra = findViewById(R.id.btnGenerarCompra);
        btnAtras = findViewById(R.id.btnAtrass);
        txtRUC = findViewById(R.id.txtEditRUC);
        txtRazon = findViewById(R.id.txtEditRazon);
        txtNombre = findViewById(R.id.txtEditNombre);
        txtCorreo = findViewById(R.id.txtEditCorreo);
        btnManual = findViewById(R.id.btnManual);


        rootView.setOnTouchListener((v, event) -> {
            txtRUC.clearFocus();
            txtRazon.clearFocus();
            txtNombre.clearFocus();
            txtCorreo.clearFocus();
            ocultarTeclado();
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.eraseFactura_Original();
        ejecutarSalida();
    }

    public void ejecutarSalida(){
        Utils.Clear_Data_Cliente();
        this.finish();
    }

    public void Reset(){
        Utils.Clear_Data_Venta();
        Utils.Clear_Data_Cliente();
        Utils.Clear_Data_Factura();
        Utils.Clear_Item();
        Utils.clearListaLocal();
    }

    public void cargaCliente() throws SQLException {
        LocalDate fechaActual = null;
        String fechaFormateada;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            fechaActual = LocalDate.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yy");
            fechaFormateada = fechaActual.format(formato);
        } else {
            fechaFormateada = "";
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            try{
                runOnUiThread(() -> {
                    Trabajador trabajadorData = null;
                    try {
                        Utils.setIdCliente_Original();
                        trabajadorData = Utils.getData_Trabajador();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    int codigoFactura = Utils.getFactura_Original();
                    int idCliente = Utils.getIdCliente_Original();

                    Cliente obj = new Cliente(
                            Long.parseLong(String.valueOf(txtRUC.getText())),
                            String.valueOf(txtNombre.getText()),
                            idCliente,
                            String.valueOf(txtRazon.getText()),
                            String.valueOf(txtCorreo.getText())
                    );

                    Factura fact = new Factura(
                            codigoFactura,
                            fechaFormateada,
                            idCliente,
                            trabajadorData.getCodigo()
                    );

                    Utils.setData_Cliente(obj);
                    Utils.setData_Factura(fact);
                });
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR TIENDA", "LISTA", e);
                });
            }
        });
    }

    public void GetApiSunat(String ruc){
        ApiSunat apiService = ConfiguracionApiSunat.getRetrofitInstance().create(ApiSunat.class);
        //ruc = "20109072177";  // ID del producto a buscar 20109072177
        Call<Empresa> call = apiService.getDetallesConRuc(ruc);
        call.enqueue(new Callback<Empresa>() {

            @Override
            public void onResponse(Call<Empresa> call, Response<Empresa> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Empresa producto = response.body();

                    if (producto.getRazonSocial() != null){
                        txtRazon.setText(String.valueOf(producto.getRazonSocial()));
                        txtRazon.setEnabled(false);
                    }
                    else{
                        lanzarMensaje("SIN REGISTROS", "INGRESE LA RAZON SOCIAL");
                        txtRazon.setEnabled(true);
                        txtRazon.setText("");
                    }
                }
            }
            @Override
            public void onFailure(Call<Empresa> call, Throwable t) {
                //System.out.println("Error: " + t.getMessage());
            }
        });

    }

    public void BTN_MANUAL(View view) {
        GetApiSunat(String.valueOf(txtRUC.getText()));
    }

    public void BTN_GENERAR_COMPRAR(View view) throws SQLException {
        setResult(RESULT_OK);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                runOnUiThread(() -> {

                    Cliente cli = Utils.getData_Cliente();
                    Factura fact = Utils.getData_Factura();
                    ArrayList<Venta> listaCompra = Utils.getData_Venta();

                    runOnUiThread(() -> {
                        IngresoSQL.ICliente(cli.getIdCliente(), cli.getCodigo(),cli.getNombre(), cli.getRazonSocial(), cli.getCorreo());
                        IngresoSQL.IFactura(fact.getCodigoFactura(),fact.getFechaEmision(),fact.getidCli(),fact.getIdTrabajador());
                    });

                    runOnUiThread(() -> {
                        for(int i = 0; i < listaCompra.size(); i++){
                            IngresoSQL.IVenta(
                                    listaCompra.get(i).getNumeroFactura(),
                                    listaCompra.get(i).getCodigoPro(),
                                    listaCompra.get(i).getCantidad(),
                                    listaCompra.get(i).getPrecioVenta()
                            );
                        }
                    });

                    runOnUiThread(() -> {
                        List<Producto> local = Utils.getListaLocal();

                        runOnUiThread(() -> {
                            for(Producto obj : local){
                                ActualizaSQL.UProducto(
                                        obj.getCodigoProducto(),
                                        obj.getStockProducto()
                                );
                            }
                        });
                    });

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Venta Completada", Toast.LENGTH_SHORT).show();
                        Reset();
                    });
                });

                this.finish();
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR TIENDA", "LISTA", e);
                });
            }
        });
    }

    public void BTN_CLIENTE(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                runOnUiThread(() -> {
                    try {
                        if (!Objects.requireNonNull(txtRUC.getText()).toString().trim().isEmpty()){
                            if (!Objects.requireNonNull(txtNombre.getText()).toString().trim().isEmpty()) {
                                if (!Objects.requireNonNull(txtRazon.getText()).toString().trim().isEmpty()) {
                                    if (!Objects.requireNonNull(txtCorreo.getText()).toString().trim().isEmpty()) {

                                        cargaCliente();
                                        btnCliente.setVisibility(View.INVISIBLE);
                                        btnGenerarCompra.setVisibility(View.VISIBLE);
                                        btnAtras.setVisibility(View.VISIBLE);
                                        runOnUiThread(() -> {
                                            Toast.makeText(this, "Cliente Registrado", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    else{
                                        lanzarMensaje("VENTA", "Correo Invalido Invalido");
                                    }
                                }
                                else{
                                    lanzarMensaje("VENTA", "Razon Social Invalida");
                                }
                            }
                            else{
                                lanzarMensaje("VENTA", "Nombre Invalido");
                            }
                        }
                        else{
                            lanzarMensaje("VENTA", "RUC Invalido");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR TIENDA", "LISTA", e);
                });
            }
        });
    }

    public void BTN_ATRAS(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                runOnUiThread(() -> {
                    Utils.Clear_Data_Cliente();
                    Utils.Clear_Data_Factura();
                    btnCliente.setVisibility(View.VISIBLE);
                    btnGenerarCompra.setVisibility(View.INVISIBLE);
                    btnAtras.setVisibility(View.INVISIBLE);
                });
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