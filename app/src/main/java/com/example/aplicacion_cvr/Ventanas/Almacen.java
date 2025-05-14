package com.example.aplicacion_cvr.Ventanas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion_cvr.Conexiones.ListaSQL;
import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Proveedor_e;
import com.example.aplicacion_cvr.R;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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

public class Almacen extends AppCompatActivity {
    private ConstraintLayout rootView;
    private List<Producto> listaProductoLV;
    private Timer timer = new Timer();
    private final long DELAY = 300;

    TextView lbStockAlmacen;
    List<Map<String, String>> datosStock;
    SimpleAdapter adapterStock;
    ListView lvProductosAlmacen;
    String productoSeleccionado = "";
    String data = "";


    TextInputEditText txtBuscarAlmacen;
    String[] fromStock = {"descripcion", "id"};
    int[] to = {android.R.id.text1, android.R.id.text2};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_almacen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lbStockAlmacen = findViewById(R.id.lbStockAlmacen);
        txtBuscarAlmacen = findViewById(R.id.txtEditBuscarAlmacen);
        lvProductosAlmacen = findViewById(R.id.listProductosAlmacen);
        rootView = findViewById(R.id.main);

        listaProductoLV = new ArrayList<>(ListaSQL.LProductos());
        datosStock = new ArrayList<>();

        rootView.setOnTouchListener((v, event) -> {
            txtBuscarAlmacen.clearFocus();
            data = "Stock - 0";
            lbStockAlmacen.setText(data);
            mostarProductosAlmacen();
            ocultarTeclado();
            return true;
        });

        txtBuscarAlmacen.addTextChangedListener(new TextWatcher() {
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
                            busquedaProductosAlmacen(s.toString());
                            data = "Stock - 0";
                            lbStockAlmacen.setText(data);

                            if(Objects.requireNonNull(txtBuscarAlmacen.getText()).toString().trim().isEmpty()){
                                mostarProductosAlmacen();
                            }
                        });
                    }
                }, DELAY);
            }
        });

        lvProductosAlmacen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /** @noinspection unchecked*/
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> itemSeleccionado = (Map<String, String>) parent.getItemAtPosition(position);
                productoSeleccionado = itemSeleccionado.get("descripcion");
                seleccionListaProductoAlmacen(productoSeleccionado);
            }
        });

        adapterStock = new SimpleAdapter(this, datosStock,
                android.R.layout.simple_expandable_list_item_2, fromStock,to);

        lvProductosAlmacen.setAdapter(adapterStock);

        mostarProductosAlmacen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    public void mostarProductosAlmacen(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            listaProductoLV = new ArrayList<>(ListaSQL.LProductos());

            try {
                runOnUiThread(() -> {
                    datosStock.clear();
                    adapterStock.notifyDataSetChanged();

                    runOnUiThread(() -> {
                        for (Producto producto : listaProductoLV) {
                            Map<String, String> mapa = new HashMap<>();
                            mapa.put("descripcion", producto.getDescripcionProducto());
                            mapa.put("id", String.valueOf(producto.getCodigoProducto()));
                            datosStock.add(mapa);
                        }
                        adapterStock.notifyDataSetChanged();
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

    public void busquedaProductosAlmacen(String valor){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            try {
                runOnUiThread(() -> {
                    datosStock.clear();
                    adapterStock.notifyDataSetChanged();

                    runOnUiThread(() -> {
                        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(valor)
                                + "\\b", Pattern.CASE_INSENSITIVE);

                        for (Producto producto : listaProductoLV) {
                            Matcher matcher = pattern.matcher(
                                    String.valueOf(producto.getDescripcionProducto()));

                            if (matcher.find() && !valor.trim().isEmpty()) {
                                Map<String, String> mapa = new HashMap<>();
                                mapa.put("descripcion", producto.getDescripcionProducto());
                                mapa.put("id", String.valueOf(producto.getCodigoProducto()));
                                datosStock.add(mapa);
                            }
                        }
                        adapterStock.notifyDataSetChanged();
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

    public void seleccionListaProductoAlmacen(String valor){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                runOnUiThread(() -> {
                    Toast.makeText(this, valor, Toast.LENGTH_SHORT).show();

                    Pattern pattern = Pattern.compile("\\b" + Pattern.quote(valor)
                            + "\\b", Pattern.CASE_INSENSITIVE);

                    for (Producto producto : listaProductoLV) {
                        Matcher matcher = pattern.matcher(
                                String.valueOf(producto.getDescripcionProducto()));

                        if (matcher.find() && !valor.trim().isEmpty()) {
                            data = "Stock - " + producto.getStockProducto();
                            lbStockAlmacen.setText(data);
                        }
                    }
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

    public Producto busquedaProducLocal(String Valor){
        for(Producto producto : listaProductoLV){
            if(Valor.equals(String.valueOf(producto.getCodigoProducto()))){
                return producto;
            }
            else if(Valor.equals(producto.getDescripcionProducto())){
                return producto;
            }
        }
        return null;
    }


    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            String codigo = result.getContents();
            productoSeleccionado = codigo;
            data = "Stock - " + busquedaProducLocal(codigo).getStockProducto();
            lbStockAlmacen.setText(data);
            busquedaProductosAlmacen(busquedaProducLocal(codigo).getDescripcionProducto());
        }
    });

    public void lectorQR(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                runOnUiThread(() -> {
                    ScanOptions options = new ScanOptions();
                    options.setPrompt("Escanear");
                    options.setBeepEnabled(true);
                    options.setOrientationLocked(true);
                    options.setCaptureActivity(CaptureAct.class);
                    barLauncher.launch(options);
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

    public void BTN_QR_ALMACEN(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                lectorQR();
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