package pagos;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Pago implements Serializable {
    public String getDescripcion_metodo() {
        return descripcion_metodo;
    }

    String descripcion_metodo;
    double valor;
    double precio_final;




    public Pago(double valor){
        this.valor = valor;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
    }
    public double getPrecio_final() {
        return precio_final;
    }

    public void setPrecio_final(double precio_final) {
        this.precio_final = precio_final;
    }

    public void CalcularCosto(){
        valor = precio_final;
    }

}
