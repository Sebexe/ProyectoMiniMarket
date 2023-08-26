package Ventas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class AlmacenVentas implements Serializable {
    HashMap<Integer,Venta> almacen_ventas;
    int contador;

    public AlmacenVentas(){
        this.almacen_ventas = new HashMap<>();
        contador = 1;
    }

    public int getContador(){
        return contador;
    }

    public void guardarVenta(Venta venta_hecha){
        venta_hecha.setNumero_venta(contador);
        almacen_ventas.put(contador,venta_hecha);
        contador++;
    }

    public ArrayList<Venta> recuperarVentas() {
        return new ArrayList<>(almacen_ventas.values());
    }
}
