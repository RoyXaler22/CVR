package com.example.aplicacion_cvr.Conexiones;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;


import androidx.constraintlayout.widget.ConstraintSet;

import com.example.aplicacion_cvr.Entidades.Cliente;
import com.example.aplicacion_cvr.Entidades.Factura;
import com.example.aplicacion_cvr.Entidades.Producto;
import com.example.aplicacion_cvr.Entidades.Proveedor_e;
import com.example.aplicacion_cvr.Entidades.Trabajador;
import com.example.aplicacion_cvr.Entidades.Venta;
import com.example.aplicacion_cvr.Conexiones.ListaSQL;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Utils {

    private static String usuarioTrabajando;
    private static String tipoTrabajando, facturaSelec;
    private static boolean estadoCambio;
    private static int Factura_Original, ID_Cliente, ID_Original, Item = 0;
    private static  List<Producto> lista_local_statica = null;
    private static String Tipo = "";
    private static long CodigoProv = 0;
    private static ArrayList<Venta> Data_Venta = new ArrayList<>();
    private static Cliente Data_Cliente = null;
    private static Trabajador Data_Trabajador = null;
    private static Factura Data_Factura = null;
    private static final int Clave = 1234;


    /* Datos de Trabajador */
    public static void setDatosTrabajador(String Trabajador, String Tipo){
        usuarioTrabajando = Trabajador;
        tipoTrabajando = Tipo;
    }
    public static String getNombreTrabajador(){
        return usuarioTrabajando;
    }
    public static String getTipoTrabajador(){
        return tipoTrabajando;
    }

    public static boolean getItem() {
        return Item > 0;
    }

    public static void setItem() {
        Item++;
    }

    public static void eraseItem() {
        Item--;
    }

    public static void Clear_Item() {
        Item = 0;
    }

    public static int getFactura_Original() {
        return Factura_Original;
    }

    public static void setFactura_Original() throws SQLException {
        Factura_Original = 1000;
        ArrayList<Factura> lista = ListaSQL.LFacturas();

        if(!lista.isEmpty()){
            for(int i = 0; i<lista.size(); i++){
                Factura_Original++;
            }
        }
    }


    public static int getIdCliente_Original() {
        return ID_Cliente;
    }

    public static void setIdCliente_Original() throws SQLException {
        ID_Cliente = 1;
        ArrayList<Cliente> lista = ListaSQL.LClientes();

        if(!lista.isEmpty()){
            for(int i = 0; i<lista.size(); i++){
                ID_Cliente++;
            }
        }
    }

    public static void eraseFactura_Original() {
        Factura_Original--;
    }

    public static int getID_Original() {
        return ID_Original;
    }

    public static void setID_Original() throws SQLException {
        ID_Original = 4001;

        ArrayList<Trabajador> lista = ListaSQL.LTrabajadores();

        if(!lista.isEmpty()){
            for(int i = 0; i<lista.size(); i++){
                ID_Original++;
            }
        }
    }

    public static boolean isEstadoCambio() {
        return estadoCambio;
    }

    public static void setEstadoCambio(boolean aEstadoCambio) {
        estadoCambio = aEstadoCambio;
    }

    //--------------------------------------------------
    public static ArrayList<Venta> getData_Venta() {
        return Data_Venta;
    }

    public static void setData_Venta(Venta Lista_Venta) {
        Data_Venta.add(Lista_Venta);
    }

    public static void eraseData_Venta(Venta Lista_Venta) {
        Data_Venta.remove(Lista_Venta);
    }

    public static Venta searchData_Venta(int codigo) {
        for(int i = 0; i<Data_Venta.size(); i++){
            if(Data_Venta.get(i).getCodigoPro() == codigo){
                return Data_Venta.get(i);
            }
        }
        return null;
    }

    public static void updateData_Venta(int cantidad, double precioVenta, Venta pos) {
        for(int i = 0; i<Data_Venta.size(); i++){
            if(pos == Data_Venta.get(i)){
                Data_Venta.get(i).setCantidad(Data_Venta.get(i).getCantidad() + cantidad);
                Data_Venta.get(i).setPrecioVenta((double)Data_Venta.get(i).getCantidad() * precioVenta);
            }
        }
    }

    public static void Clear_Data_Venta() {
        Data_Venta.clear();
    }

    //--------------------------------------------------

    public static Cliente getData_Cliente() {
        return Data_Cliente;
    }

    public static void setData_Cliente(Cliente aData_Cliente) {
        Data_Cliente = aData_Cliente;
    }

    public static void Clear_Data_Cliente() {
        Data_Cliente = null;
    }

    //--------------------------------------------------

    public static Trabajador getData_Trabajador() throws SQLException {
        ArrayList<Trabajador> lista = ListaSQL.LTrabajadores();

        for(int i = 0; i < lista.size(); i++){
            if(String.valueOf(lista.get(i).getNombre()).equals(Utils.getNombreTrabajador())){
                Data_Trabajador = lista.get(i);
            }
        }
        return Data_Trabajador;
    }

    public static void Clear_Data_Trabajador() {
        Data_Trabajador = null;
    }

    //--------------------------------------------------

    public static Factura getData_Factura() {
        return Data_Factura;
    }

    public static void setData_Factura(Factura aData_Factura) {
        Data_Factura = aData_Factura;
    }

    public static void Clear_Data_Factura() {
        Data_Factura = null;
    }

    //--------------------------------------------------

    public static List<Producto> getListaLocal(){
        return lista_local_statica;
    }

    public static void setListaLocal(List<Producto> listaLocal){
        lista_local_statica = listaLocal;
    }

    public static void clearListaLocal(){
        lista_local_statica = null;
    }

    //--------------------------------------------------

    public static double gananciasDiarias(String fechaActual) throws SQLException {
        ArrayList<Venta> DataVenta = ListaSQL.LVenta();
        ArrayList<Factura> DataFactura = ListaSQL.LFacturas();
        double ganancia = 0.0;

        for(int i = 0; i < DataFactura.size(); i++){
            if(DataFactura.get(i).getFechaEmision().equals(fechaActual)){
                for(int j = 0; j < DataVenta.size(); j++){
                    if(DataFactura.get(i).getCodigoFactura() == DataVenta.get(j).getNumeroFactura()){
                        ganancia += DataVenta.get(j).getPrecioVenta();
                    }
                }
            }
        }

        return ganancia;
    }

    //--------------------------------------------------

    public static Producto BusquedaProductoConsulta(String Valor) throws SQLException{
        ArrayList<Producto> lista = ListaSQL.LProductos();

        for(int i=0;i<lista.size();i++){
            if(Valor.equals(String.valueOf(lista.get(i).getCodigoProducto()))){
                return lista.get(i);
            }
            else if(Valor.equals(lista.get(i).getDescripcionProducto())){
                return lista.get(i);
            }
        }
        return null;
    }

    //--------------------------------------------------

    public static Cliente BusquedaClienteConsulta(String Valor) throws SQLException{
        ArrayList<Cliente> lista = ListaSQL.LClientes();

        for(int i=0;i<lista.size();i++){
            if(Valor.equals(String.valueOf(lista.get(i).getIdCliente()))){
                return lista.get(i);
            }
        }
        return null;
    }

    //--------------------------------------------------

    public static Proveedor_e BusquedaProveedorConsulta(String Valor) throws SQLException{
        ArrayList<Proveedor_e> lista = ListaSQL.LProveedor();

        for(int i=0;i<lista.size();i++){
            if(Valor.equals(String.valueOf(lista.get(i).getCodigo()))){
                return lista.get(i);
            }

            else if(Valor.equals(lista.get(i).getNombre())){
                return lista.get(i);
            }
        }
        return null;
    }

    //--------------------------------------------------
/*
    public static Trabajador BusquedaTrabajadorConsulta(String Valor) throws SQLException{
        ArrayList<Trabajador> lista = Listas.LTrabajadores();

        for(int i=0;i<lista.size();i++){
            if(Valor.equals(String.valueOf(lista.get(i).getUsuario()))){
                return lista.get(i);
            }
        }
        return null;
    }

    //--------------------------------------------------
*/
    public static void Info_Producto(String tipo, Long Codigo){
        Tipo = tipo;
        CodigoProv = Codigo;
    }

    public static String getTipo() {
        return Tipo;
    }

    public static long getCodigoProv() {
        return CodigoProv;
    }

    public static void Clear_Info_Producto(){
        Tipo = "";
        CodigoProv = 0;
    }



    public static String getFacturaSeleccionada(){
        return facturaSelec;
    }

    public static void setFacturaSeleccionada(String factura){
        facturaSelec = factura;
    }

    public static void Clear_FacturaSeleccionada(){
        facturaSelec = "";
    }


/*
    //--------------------------------------------------

    public static int getClave() {
        return Clave;
    }

    public static void setClave(int aClave) {
        Clave = aClave;
    }

     */

    /* Metodos Extraccion IP Privada */
    public static String getIPAddress(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                return Formatter.formatIpAddress(ipAddress);
            }
        }
        return null; // No conectado a WiFi o sin IP disponible
    }
    public static String getGatewayIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                int gatewayIp = wifiManager.getDhcpInfo().gateway;
                return intToInetAddress(gatewayIp).getHostAddress();
            }
        }
        return null;
    }
    public static String getRouterIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                int gatewayIp = wifiManager.getDhcpInfo().gateway;
                return intToInetAddress(gatewayIp).getHostAddress();
            }
        }
        return null;
    }
    private static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError(e);
        }
    }


}
