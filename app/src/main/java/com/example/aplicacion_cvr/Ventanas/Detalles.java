package com.example.aplicacion_cvr.Ventanas;

import static com.example.aplicacion_cvr.Conexiones.Utils.BusquedaProductoConsulta;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

import com.example.aplicacion_cvr.Conexiones.ListaSQL;
import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.Entidades.Cliente;
import com.example.aplicacion_cvr.Entidades.Factura;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Proveedor_e;
import com.example.aplicacion_cvr.Entidades.Venta;
import com.example.aplicacion_cvr.R;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Detalles extends AppCompatActivity {
    private List<Factura> listaFacturaLV;
    private List<Venta> listaVentaLV;
    private List<Cliente> listaClienteLV;
    double sub = 0;
    double igv =0;
    double total =0;
    String datos = "";

    TextView lbClienteRUC, lbFechaFactura, lbNumFactura, lbClienteFactura, lbTotalFactura, lbClienteRazon;

    List<Map<String, String>> datosCompras;

    ListView lvComprasReg;

    SimpleAdapter adapterComprasReg;

    String[] fromComprasReg = {"producto", "resumen"};

    int[] to = {android.R.id.text1, android.R.id.text2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lbClienteFactura = findViewById(R.id.lbClienteFactura);
        lbClienteRUC = findViewById(R.id.lbClienteRUC);
        lbFechaFactura = findViewById(R.id.lbFechaFactura);
        lbNumFactura = findViewById(R.id.lbNumFactura);
        lbTotalFactura = findViewById(R.id.lbProductosFactura);
        lvComprasReg = findViewById(R.id.listComprasReg);
        lbClienteRazon = findViewById(R.id.lbClienteRazonSoc);

        listaFacturaLV = new ArrayList<>(ListaSQL.LFacturas());
        datosCompras = new ArrayList<>();
        try {
            listaVentaLV = new ArrayList<>(ListaSQL.LVenta());
            listaClienteLV = new ArrayList<>(ListaSQL.LClientes());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        adapterComprasReg = new SimpleAdapter(this, datosCompras,
                android.R.layout.simple_expandable_list_item_2, fromComprasReg,to);

        lvComprasReg.setAdapter(adapterComprasReg);
        mostrarDatos();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void mostrarDatos(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{

                runOnUiThread(() -> {
                    String facturaCode = Utils.getFacturaSeleccionada();
                    for(Factura factura : listaFacturaLV){
                        if(factura.getCodigoFactura() == Integer.parseInt(facturaCode)){

                            lbFechaFactura.setText(String.valueOf("Fecha: " + factura.getFechaEmision()));
                            lbNumFactura.setText(String.valueOf("N° " + factura.getCodigoFactura()));

                            runOnUiThread(() -> {
                                for(Cliente cliente : listaClienteLV){
                                    if(cliente.getIdCliente() == factura.getidCli()){
                                        lbClienteFactura.setText("Cliente: "+ cliente.getNombre());
                                        lbClienteRazon.setText("Razon: "+ cliente.getRazonSocial());
                                        lbClienteRUC.setText(String.valueOf("RUC: " + cliente.getCodigo()));
                                    }
                                }
                            });
                        }
                    }


                    try {
                        runOnUiThread(() -> {
                            datosCompras.clear();
                            adapterComprasReg.notifyDataSetChanged();

                            runOnUiThread(() -> {
                                for (Venta venta : listaVentaLV) {
                                    if(venta.getNumeroFactura() == Integer.parseInt(facturaCode)){
                                        Map<String, String> mapa = new HashMap<>();

                                        Producto obj = null;
                                        try {
                                            obj = Utils.BusquedaProductoConsulta(String.valueOf(venta.getCodigoPro()));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        sub = sub + venta.getPrecioVenta();

                                        mapa.put("producto", Objects.requireNonNull(obj).getDescripcionProducto());
                                        mapa.put("resumen",  "Cant. " + venta.getCantidad() +
                                                " |  S/." + venta.getPrecioVenta());
                                        datosCompras.add(mapa);

                                    }
                                }
                                adapterComprasReg.notifyDataSetChanged();

                                igv = sub * 0.18;
                                total = sub + igv;
                                //DecimalFormat df = new DecimalFormat("#.##");


                                datos += "-----------------------------------------\n";
                                datos += String.format("%-10s %s %.2f%n", "SubTotal", "| s/.", sub);
                                datos += String.format("%-15s %s %.2f%n", "IGV", "| s/.", igv);
                                datos += String.format("%-14s %s %.2f%n", "Total", "| s/.", total);
                                lbTotalFactura.setText(datos);
                            });
                        });
                    }
                    catch (Exception e){
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                            Log.e("ERROR FACTURA", "Excepcion: ", e);
                        });
                    }


//                    for(Venta venta : listaVentaLV){
//                        if(venta.getNumeroFactura() == Integer.parseInt(facturaCode)){
//
//                            Producto obj = null;
//                            try {
//                                obj = Utils.BusquedaProductoConsulta(String.valueOf(venta.getCodigoPro()));
//                            } catch (SQLException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                            sub = sub + venta.getPrecioVenta();
//                            datos += Objects.requireNonNull(obj).getDescripcionProducto() + "\nCant. " + venta.getCantidad() +
//                                    " - S/-" + venta.getPrecioVenta() + "\n\n";
//                        }
//                    }
//
//                    igv = sub * 0.18;
//                    total = sub + igv;
//
//                    datos += "\nSubTotal - " + sub;
//                    datos += "\nIGV - " + igv;
//                    datos += "\nTotal - " + total;
//
//                    lbTotalFactura.setText(datos);
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