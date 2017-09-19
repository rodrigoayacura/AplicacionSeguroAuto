package cl.inacap.aplicacionseguroauto;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Calendar;


public class ResultadoSeguro extends AppCompatActivity {

    //Se crean los objetos a usar relacionados al layout

    TextView placaPatente;
    TextView anioCarro;
    TextView montoUF;
    TextView marcaCarro;
    TextView modeloCarro;
    TextView antiguo;
    TextView seguro;
    TextView montoSeguro;
    ImageView imagenSeguro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_seguro);

        //Se asocian objetos con un elemento de la Interfaz

        placaPatente = (TextView) findViewById(R.id.rTxt_ppu);
        anioCarro = (TextView) findViewById(R.id.rTxt_anioVeh);
        montoUF = (TextView) findViewById(R.id.rTxt_valorUF);
        marcaCarro = (TextView) findViewById(R.id.rTxt_marca);
        modeloCarro = (TextView) findViewById(R.id.rTxt_modelo);
        antiguo = (TextView) findViewById(R.id.txt_antiguedad);
        seguro = (TextView) findViewById(R.id.txt_asegurable);
        montoSeguro = (TextView) findViewById(R.id.txt_valorSeguro);
        imagenSeguro = (ImageView) findViewById(R.id.img_seguro);

        //Se crea metodo Intent para recibir los datos del Main Activity
        Intent intento = getIntent();
        Intent extras = getIntent();

        Bundle datosSeguro= intento.getExtras();

        // Se asocian los datos del Main Activity a variable del Activity Resultado
        String patente = datosSeguro.getString("carroPPU");
        int anioVeh = datosSeguro.getInt("carroAnio");
        int valorFomento = datosSeguro.getInt("valorUF");
        String marcaVeh =extras.getStringExtra("spinnerMarca");
        String modeloVeh =extras.getStringExtra("spinnerModelo");
        String antiguedadVeh;
        String asegurable;

        // Uso de metodos para detenminar las variables
        antiguedadVeh = calculaAntiguedad(diferenciaAnios(anioVeh));
        asegurable = seguroValidoo(diferenciaAnios(anioVeh));


        placaPatente.setText("PPU del Vehículo: "+patente);
        anioCarro.setText("Año del Vehículo: "+anioVeh);
        montoUF.setText("Valor de la UF: "+valorFomento+" pesos");
        marcaCarro.setText("Marca del Vehículo: "+marcaVeh);
        modeloCarro.setText("Modelo del Vehículo: "+modeloVeh);
        antiguo.setText("El Vehículo tiene una Antigüedad de: "+antiguedadVeh);
        seguro.setText("El Vehículo "+asegurable);
        montoSeguro.setText(valorSeguro(diferenciaAnios(anioVeh),valorFomento));
        muestraImagen(asegurable);

    }

    public int diferenciaAnios (int anioRegistro){
        //Método para calcular la diferencia entre el año actual y el año ingresado por el usuario

        int diferenciaAnio;
        Calendar fecha = Calendar.getInstance();
        int anioActual = fecha.get(Calendar.YEAR);
        diferenciaAnio = anioActual - anioRegistro;

        if (diferenciaAnio <1){
            diferenciaAnio = 1;
        }

        return diferenciaAnio;
    }

    public String calculaAntiguedad (int diferencia){
        //Método para determinar la antigüedad del vehículo

        String tiempo;

        if (diferencia == 1){
            tiempo = diferencia + " Año";
        }else {
            tiempo = diferencia + " Años";
        }

        return tiempo;

    }

    public String seguroValidoo (int dif){
        //Método para determinar si un es asegurable o no

        String validez;

        if (dif <= 10){
            validez = "es Asegurable";
        }else {
            validez = "No es Asegurable";
        }

        return validez;

    }

    public String valorSeguro (int dif, int uf){
        //Método para calcular el valor a pagar por el seguro del vehículo

        String valorPago;

        float montoSeguro = (float) (uf *.1);

        DecimalFormat df =new DecimalFormat("0.00");

        if (dif > 10){
            valorPago = "NO corresponde seguro";
        }else {
            valorPago = "Valor Seguro: $"+df.format(montoSeguro*dif)+" pesos.";
            }


        return valorPago;
    }

    public void muestraImagen (String seguro){
        //Método para mostrar imagen de acuerdo a si es no asegurable el vehículo
        if (seguro == "es Asegurable"){
            imagenSeguro.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.conseguro));
        }else {
            imagenSeguro.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.sinseguro));
        }


    }


}
