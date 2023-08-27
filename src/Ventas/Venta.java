package Ventas;

import pagos.Credito;
import pagos.Pago;
import productos.Producto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Venta implements Serializable {
    ArrayList<ProductoVenta> carrito = new ArrayList<>();
    int total;
    double Precio_final;

    public void setMedio_pago(Pago medio_pago) {
        this.medio_pago = medio_pago;
    }

    public double getPrecio_final() {
        return medio_pago.getPrecio_final();
    }

    public String getDescripcion_metodo(){
        return medio_pago.getDescripcion_metodo();
    }

    Pago medio_pago;

    public int getCuil_cliente() {
        return cuil_cliente;
    }



    public void setCuil_cliente(int cuil_cliente) {
        this.cuil_cliente = cuil_cliente;
    }

    int cuil_cliente;
    public int getNumero_venta() {
        return numero_venta;
    }

    public void setNumero_venta(int numero_venta) {
        this.numero_venta = numero_venta;
    }

    int numero_venta;


    public void agregarCarrito(Producto producto,int cantidad){
        carrito.add(new ProductoVenta(producto,cantidad));
        calcularTotal();
    }

    public void calcularTotal() {
        total = 0;
        for (ProductoVenta producto : carrito){
            total += producto.getTotal();
        }
    }

    public ArrayList<ProductoVenta> getCarrito(){
        return carrito;
    }


    public int getTotal() {
        return total;
    }


}
