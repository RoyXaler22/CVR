package com.example.aplicacion_cvr.Ventanas;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion_cvr.Conexiones.ListaSQL;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Venta;
import com.example.aplicacion_cvr.R;
import com.google.android.material.textfield.TextInputEditText;
import com.example.aplicacion_cvr.Conexiones.Utils;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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

public class Tienda extends AppCompatActivity {

    private Timer timer = new Timer();
    private final long DELAY = 300;
    private ConstraintLayout rootView;
    private List<Producto> listaProductoLV;
    private List<Venta> listaComprasLV;
    private static final int REQUEST_CODE_SECOND_ACTIVITY = 1;

    List<Map<String, String>> datosLista, datosCompras;
    SimpleAdapter adapterLista, adapterCompras;
    ListView lvProductos, lvCompras;
    TextView lbCosto, lbSubTotal;
    TextInputEditText txtBuscar,txtCantidad;
    ImageButton btnComprar, btnBorrar;
    /* TextInputLayout txtlyBuscar,txtlyCantidad; */

    String[] fromLista = {"descripcion", "id"};
    String[] fromCompras = {"descripcion", "cantidad"};
    int[] to = {android.R.id.text1, android.R.id.text2};

    String productoSeleccionado= "", productoSeleccionadoBorrar = "";
    String data = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SECOND_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                // Cerrar la MainActivity cuando la SegundaActividad devuelve RESULT_OK
                finish();
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tienda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtBuscar = findViewById(R.id.txtEditBuscarProducto);
        txtCantidad = findViewById(R.id.txtEditCantidadProducto);
        lvProductos = findViewById(R.id.listProductos);
        lvCompras = findViewById(R.id.listCompras);
        lbCosto = findViewById(R.id.lbPrecio);
        lbSubTotal = findViewById(R.id.lbSubTotal);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnComprar = findViewById(R.id.btnComprar);
        rootView = findViewById(R.id.main);

        listaProductoLV = new ArrayList<>(ListaSQL.LProductos());
        listaComprasLV = new ArrayList<>(Utils.getData_Venta());

        datosLista = new ArrayList<>();
        datosCompras = new ArrayList<>();

        rootView.setOnTouchListener((v, event) -> {
            txtBuscar.clearFocus();
            txtCantidad.clearFocus();
            ocultarTeclado();
            return true;
        });
        txtBuscar.addTextChangedListener(new TextWatcher() {
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
                            busquedaProductos(s.toString());
                            data = "Precio - S/0.0\nID: 00000";
                            lbCosto.setText(data);
                        });
                    }
                }, DELAY);
            }
        });
        lvProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /** @noinspection unchecked*/
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> itemSeleccionado = (Map<String, String>) parent.getItemAtPosition(position);
                //String idb = itemSeleccionado.get("descripcion");
                String idb = itemSeleccionado.get("id");
                productoSeleccionado = idb;
                seleccionLista(idb);
            }
        });
        lvCompras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /** @noinspection unchecked*/
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> itemSeleccionado = (Map<String, String>) parent.getItemAtPosition(position);
                productoSeleccionadoBorrar = itemSeleccionado.get("descripcion");
                btnBorrar.setVisibility(View.VISIBLE);
            }
        });

        adapterLista = new SimpleAdapter(this, datosLista,
                android.R.layout.simple_expandable_list_item_2, fromLista,to);
        adapterCompras = new SimpleAdapter(this, datosCompras,
                android.R.layout.simple_expandable_list_item_2, fromCompras,to);

        lvProductos.setAdapter(adapterLista);
        lvCompras.setAdapter(adapterCompras);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.Clear_Data_Cliente();
        Utils.Clear_Data_Factura();
        Utils.clearListaLocal();
        ejecutarSalida();
    }

    public void ejecutarSalida(){
        Utils.Clear_Data_Venta();
        Utils.Clear_Item();
        this.finish();
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            String codigo = result.getContents();
            seleccionLista(codigo);
            productoSeleccionado = codigo;
            mostrarTeclado();
            busquedaProductos(busquedaProducLocal(codigo).getDescripcionProducto());
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


    public void actualizarProducLocal(int cantidad, String Valor, boolean SOR){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                runOnUiThread(() -> {
                    for(Producto producto : listaProductoLV){
                        if(Valor.equals(String.valueOf(producto.getCodigoProducto()))){
                            if(!SOR){
                                producto.setStockProducto(producto.getStockProducto() - cantidad);
                            }
                            else {
                                producto.setStockProducto(producto.getStockProducto() + cantidad);
                            }
                        }
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

    public void busquedaProductos(String valor){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            try {
                runOnUiThread(() -> {
                    datosLista.clear();
                    adapterLista.notifyDataSetChanged();

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
                                datosLista.add(mapa);
                            }
                        }
                        adapterLista.notifyDataSetChanged();
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

    public void seleccionLista(String codigo){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                runOnUiThread(() -> {
                    for (int i = 0; i < listaProductoLV.size(); i++) {
                        if (String.valueOf(listaProductoLV.get(i).getCodigoProducto()).equals(codigo)){
                            data = String.valueOf(listaProductoLV.get(i).getPrecioUnitario());
                        }
                    }

                    if(!data.isEmpty()){
                        data = "Precio - S/" + data + "\nID: " + codigo;
                        lbCosto.setText(data);
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

    public void mostrarListaCompras(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                runOnUiThread(() -> {
                    listaComprasLV = new ArrayList<>(Utils.getData_Venta());

                    if(!listaComprasLV.isEmpty()){
                        datosCompras.clear();
                        adapterCompras.notifyDataSetChanged();

                        runOnUiThread(() -> {
                            for (Venta pos : listaComprasLV) {
                                Map<String, String> mapa = new HashMap<>();
                                mapa.put("descripcion",
                                        busquedaProducLocal(String.valueOf(pos.getCodigoPro()))
                                                .getDescripcionProducto());
                                mapa.put("cantidad", "Cant: " + pos.getCantidad() + " - S/" +
                                        pos.getPrecioVenta() + " " + busquedaProducLocal(
                                                String.valueOf(pos.getCodigoPro())).
                                        getStockProducto());
                                datosCompras.add(mapa);
                            }
                            adapterCompras.notifyDataSetChanged();
                        });
                    }
                    else{
                        datosCompras.clear();
                        adapterCompras.notifyDataSetChanged();
                        runOnUiThread(() -> lanzarMensaje("Tienda", "No Hay Compras"));
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

    public double subTotal(){
        double aux, SubTotal = 0;
        List<Venta> listaTemp = new ArrayList<>(Utils.getData_Venta());

        if (!listaTemp.isEmpty()){
            for (Venta obj : listaTemp) {
                aux = obj.getPrecioVenta();
                SubTotal += aux;
            }
            return SubTotal;
        }
        return 0;
    }


    //Botones de la Tienda
    public void BTN_AGREGAR(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                if(!"Precio - S/0.0\nID: 00000".equals(data)){

                    runOnUiThread(() -> {
                        if (Objects.requireNonNull(txtCantidad.getText()).toString()
                                .trim().isEmpty()){
                            lanzarMensaje("Tienda", "Cantidad Vacia");
                        }
                        else{
                            Producto dataInfo = busquedaProducLocal(productoSeleccionado);
                            int Cantidad = Integer.parseInt(String.valueOf(txtCantidad.getText()));
                            double Precio = dataInfo.getPrecioUnitario();

                            runOnUiThread(() -> {
                                if(Cantidad > 0){
                                    if(Cantidad <= dataInfo.getStockProducto()){
                                        runOnUiThread(() -> {
                                            Venta Prueba = Utils.searchData_Venta(
                                                    dataInfo.getCodigoProducto());
                                            if(Prueba != null){
                                                Utils.updateData_Venta(Cantidad, Precio, Prueba);
                                            }
                                            else{
                                                try {
                                                    Utils.setFactura_Original();
                                                } catch (SQLException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                Venta obj = new Venta(
                                                        Utils.getFactura_Original(),
                                                        dataInfo.getCodigoProducto(),
                                                        Cantidad,
                                                        (Precio * Cantidad)
                                                );
                                                Utils.setData_Venta(obj);
                                            }
                                        });

                                        runOnUiThread(() -> actualizarProducLocal(Cantidad,
                                                String.valueOf(dataInfo.getCodigoProducto()),false));

                                        runOnUiThread(this::mostrarListaCompras);
                                        Utils.setItem();
                                        lbSubTotal.setText(String.format("Sub Total - S/%s", subTotal()));

                                        txtBuscar.setText("");
                                        txtCantidad.setText("");
                                        txtCantidad.clearFocus();
                                        txtBuscar.clearFocus();
                                        ocultarTeclado();

                                        if(Utils.getItem()){
                                            btnComprar.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    else{
                                        runOnUiThread(() -> lanzarMensaje("Tienda", "No Hay Stock"));
                                    }
                                }
                                else{
                                    runOnUiThread(() -> lanzarMensaje("Tienda", "Valor Inadecuado"));
                                }
                            });
                        }
                    });
                }
                else{
                    runOnUiThread(() -> lanzarMensaje("Tienda", "Seleccione un Producto"));
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

    public void BTN_BORRAR(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                runOnUiThread(() -> {
                    Utils.eraseItem();
                    Toast.makeText(this, productoSeleccionadoBorrar, Toast.LENGTH_SHORT).show();

                    Producto obj = busquedaProducLocal(productoSeleccionadoBorrar);
                    Venta data_borrar = Utils.searchData_Venta(obj.getCodigoProducto());
                    int cantidadBack = Objects.requireNonNull(data_borrar).getCantidad();

                    runOnUiThread(() -> {
                        actualizarProducLocal(cantidadBack,String.valueOf(obj.getCodigoProducto()),true);
                        Utils.eraseData_Venta(data_borrar);
                     });

                    runOnUiThread(this::mostrarListaCompras);
                    btnBorrar.setVisibility(View.INVISIBLE);
                    lbSubTotal.setText(String.format("Sub Total - S/%s", subTotal()));

                    if(!Utils.getItem()){
                        btnComprar.setVisibility(View.INVISIBLE);
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

    public void BTN_COMPRAR(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> runOnUiThread(() -> {
            try {
                Thread.sleep(100);
                Utils.setListaLocal(listaProductoLV);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Intent intent = new Intent(this, GenerarCompra.class);
            startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY);
//                startActivity(intent);
        }));
    }

    public void BTN_QR(View view) {
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

    public void mostrarTeclado(){
        txtCantidad.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}