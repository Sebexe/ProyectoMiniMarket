package pagos;

public class Efectivo extends Pago{

    double descuento = 0.9;
    public Efectivo(double valor) {
        super(valor);
        descripcion_metodo = "Efectivo";
    }

    @Override
    public void CalcularCosto() {
        this.precio_final = valor *descuento;
    }
}
