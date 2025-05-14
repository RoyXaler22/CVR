package com.example.aplicacion_cvr.Ventanas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import com.example.aplicacion_cvr.Conexiones.IngresoSQL;
import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Proveedor_e;
import com.example.aplicacion_cvr.R;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngresarProductos extends AppCompatActivity {
    private ConstraintLayout rootView;

    TextInputEditText txtCodigo, txtDescripcion, txtPrecio,txtCantidad;
    TextView lbProveedor;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingresar_productos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCodigo = findViewById(R.id.txtEditCodigoProducto);
        txtDescripcion = findViewById(R.id.txtEditDescripcionProducto);
        txtPrecio = findViewById(R.id.txtEditPrecioProducto);
        txtCantidad = findViewById(R.id.txtEditCantProducto);
        lbProveedor = findViewById(R.id.lbDescripcionProveedor);
        rootView = findViewById(R.id.main);

        rootView.setOnTouchListener((v, event) -> {
            txtCodigo.clearFocus();
            txtDescripcion.clearFocus();
            txtPrecio.clearFocus();
            txtCantidad.clearFocus();
            ocultarTeclado();
            return true;
        });

        entradaInicial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.Clear_Info_Producto();
        this.finish();
    }

    public void entradaInicial(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                Proveedor_e pro = Utils.BusquedaProveedorConsulta(String.valueOf(Utils.getCodigoProv()));

                runOnUiThread(() -> {
                    lbProveedor.setText(Objects.requireNonNull(pro).getNombre());
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

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            String codigo = result.getContents();
            txtCodigo.setText(codigo);
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


    //Botones
    public void BTN_QR_PRODUCTO(View view) {
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

    public void BTN_CARGAR_PRODUCTO(View view) {
        if(Objects.requireNonNull(txtCodigo.getText()).toString()
                .trim().isEmpty() || Objects.requireNonNull(txtDescripcion.getText()).toString()
                .trim().isEmpty() || Objects.requireNonNull(txtPrecio.getText()).toString()
                .trim().isEmpty() || Objects.requireNonNull(txtCantidad.getText()).toString()
                .trim().isEmpty()){

            runOnUiThread(() -> {
                lanzarMensaje("Producto","Complete los Datos");
            });
        }
        else{
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try{
                    runOnUiThread(() -> {
                        try {
                            Producto Prueba_1 = Utils.BusquedaProductoConsulta(String.valueOf(txtCodigo.getText()));
                            Producto Prueba_2 = Utils.BusquedaProductoConsulta(String.valueOf(txtDescripcion.getText()));

                            if(Prueba_1 == null && Prueba_2 == null){
                                runOnUiThread(() -> {
                                    if(Double.parseDouble(String.valueOf(txtPrecio.getText())) > 0
                                            && Integer.parseInt(String.valueOf(txtCantidad.getText())) > 0){

                                        runOnUiThread(() -> {

                                            IngresoSQL.IProducto(
                                                    Integer.parseInt(String.valueOf(txtCodigo.getText())),
                                                    String.valueOf(txtDescripcion.getText()),
                                                    Double.parseDouble(String.valueOf(txtPrecio.getText())),
                                                    Integer.parseInt(String.valueOf(txtCantidad.getText())),
                                                    Utils.getTipo()
                                            );

                                            IngresoSQL.IRegistro_Proveedor_Producto(
                                                    Utils.getCodigoProv(),
                                                    Integer.parseInt(String.valueOf(txtCodigo.getText())),
                                                    Integer.parseInt(String.valueOf(txtCantidad.getText()))
                                            );

                                            runOnUiThread(() -> {
                                                Toast.makeText(this, "Producto Agregado", Toast.LENGTH_SHORT).show();
                                            });

                                            txtCodigo.setText("");
                                            txtDescripcion.setText("");
                                            txtPrecio.setText("");
                                            txtCantidad.setText("");
                                            txtCodigo.clearFocus();
                                            txtDescripcion.clearFocus();
                                            txtPrecio.clearFocus();
                                            txtCantidad.clearFocus();
                                            ocultarTeclado();
                                        });
                                    }
                                    else{
                                        txtPrecio.setText("");
                                        txtCantidad.setText("");
                                        txtPrecio.clearFocus();
                                        txtCantidad.clearFocus();
                                        ocultarTeclado();

                                        runOnUiThread(() -> {
                                            lanzarMensaje("Producto","Valores Inadecuados en Precio y/o Stock");
                                        });
                                    }
                                });
                            }
                            else{
                                txtCodigo.setText("");
                                txtDescripcion.setText("");
                                txtCodigo.clearFocus();
                                txtDescripcion.clearFocus();
                                ocultarTeclado();

                                runOnUiThread(() -> {
                                    lanzarMensaje("Producto","Uno o Mas Datos Ya Estan Registrados");
                                });
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    runOnUiThread(() -> {

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