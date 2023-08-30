package pagos;

public class Credito extends Pago{
    int cuotas;
    double c_cuota;
    public Credito(double valor,int cuotas) {
        super(valor);
        this.cuotas = cuotas;
    }


    @Override
    public void CalcularCosto() {
        switch (cuotas) {
            case 2 -> {
                precio_final = valor * 1.06;
                c_cuota = precio_final / 2;
                descripcion_metodo = "Credito 2 cuotas";
            }
            case 3 -> {
                precio_final = valor * 1.12;
                c_cuota = precio_final / 3;
                descripcion_metodo = "Credito 3 cuotas";
            }
            case 6 -> {
                precio_final = valor * 1.20;
                c_cuota = precio_final / 6;
                descripcion_metodo = "Credito 6 cuotas";
            }
        }

        // Redondear a dos decimales
        precio_final = Math.round(precio_final * 100.0) / 100.0;
        c_cuota = Math.round(c_cuota * 100.0) / 100.0;
    }

}
