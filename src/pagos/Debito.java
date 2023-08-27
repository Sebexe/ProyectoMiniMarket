package pagos;

public class Debito extends Pago{
    public Debito(double valor) {
        super(valor);
        descripcion_metodo = "Debito";
    }
}
