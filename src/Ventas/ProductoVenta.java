package Ventas;

import productos.Producto;

public class ProductoVenta extends Producto {

    int cantidad;
    int total;
    public ProductoVenta(Producto producto,int cantidad) {
        super(producto.getCodigo(), producto.getDescripcion(), producto.getPrecio_unitario(), producto.getStock_actual(), producto.getStock_minimo());
        this.cantidad = cantidad;
        this.CalcularTotal();
    }

    public void CalcularTotal() {
        total = cantidad * this.getPrecio_unitario();
    }

    public int getTotal() {
        return total;
    }

    public int getCantidad() {
        return cantidad;
    }
}
