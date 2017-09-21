package cl.inacap.aplicacionseguroauto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //Se crean los objetos a usar relacionados al layout

    Spinner listaMarcas;
    Spinner listaModelos;
    EditText placaPatente;
    EditText anioVehiculo;
    EditText valorUF;
    Button verificarSeguro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se asocian objetos con un elemento de la Interfaz
        placaPatente = (EditText)findViewById(R.id.edt_ingresoPPU);
        anioVehiculo = (EditText)findViewById(R.id.edt_anioVehiculo);
        valorUF = (EditText) findViewById(R.id.edt_valorUF);
        verificarSeguro = (Button) findViewById(R.id.btn_seguro);

        listaMarcas =(Spinner)findViewById(R.id.spin_marcas);
        listaModelos =(Spinner)findViewById(R.id.spin_modelos);

        //Se crean los adaptadores para los spinners

        ArrayAdapter<CharSequence> adaptaMarca = ArrayAdapter.createFromResource(this,R.array.array_Marcas,android.R.layout.simple_spinner_item);

        adaptaMarca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        listaMarcas.setAdapter(adaptaMarca);
        listaMarcas.setOnItemSelectedListener(this);

        verificarSeguro.setOnClickListener(this);


        }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        // Se asocia el spinner de los modelos a la marca de vehiculo

        int[] modelos =  {R.array.array_Chevy,R.array.array_Ford,R.array.array_Honda,R.array.array_Nissan};

        ArrayAdapter<CharSequence> adaptaModelos = ArrayAdapter.createFromResource(this,modelos[position],android.R.layout.simple_spinner_item);
        adaptaModelos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listaModelos.setAdapter(adaptaModelos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        //Se verifica que los datos de imput no este vacío
        if (!placaPatente.getText().toString().equals("") && !anioVehiculo.getText().toString().equals("") &&
                !valorUF.getText().toString().equals("")
                ){

            String patente = placaPatente.getText().toString();
            int anioVeh = Integer.parseInt(anioVehiculo.getText().toString());
            int unidadFomento = Integer.parseInt(valorUF.getText().toString());
            String spinMarca = listaMarcas.getSelectedItem().toString();
            String spinModelo = listaModelos.getSelectedItem().toString();

            //Se verifica patente en caso errado se emite Toast
            if (verificaPPU(patente) == false){
                Toast.makeText(getApplicationContext(),"La patente esta mal ingresada",Toast.LENGTH_SHORT).show();
            }
            //Se verifica año del vehiculo en caso errado se emite Toast
            if (verificaAnio(anioVeh) == false){
                Toast.makeText(getApplicationContext(),"El año no corresponde",Toast.LENGTH_SHORT).show();
            }
            //Se verifica valor unidad de fomento en caso que se 0 se emite Toast
            if (verificaUF(unidadFomento) == false){
                Toast.makeText(getApplicationContext(),"El valor de la Unidad de Fomento no puede ser 0",Toast.LENGTH_SHORT).show();
            }

            //Se verifica patente, la UF y el año de vehiculo sean correctos antes de iniciar nuevo activity
            if (verificaUF(unidadFomento) && verificaPPU(patente) && verificaAnio(anioVeh)){
                Intent intento = new Intent(MainActivity.this, ResultadoSeguro.class);

                intento.putExtra("carroPPU", patente);
                intento.putExtra("carroAnio", anioVeh);
                intento.putExtra("valorUF", unidadFomento);
                intento.putExtra("spinnerMarca", spinMarca);
                intento.putExtra("spinnerModelo", spinModelo);
                startActivity(intento);
            }

        }else{
            // Se solicitan que cumpla con los datos requeridos
            Toast.makeText(getApplicationContext(),"Ingrese los datos requeridos",Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean verificaPPU (String ppu){
        // Se verifica que la patente haya sido ingresada con los valores AAAA55 ó AA5555


        Pattern p = Pattern.compile("[BCDFGHJKLPRSTVWXYZ]{4}[1-9]{1}[0-9]{1}");
        Pattern p2 = Pattern.compile("([ABCEFGHDKLNPRSTUVXYZWM]){1}[ABCDEFGHIJKLNPRSTUVXYZ]{1}[1-9]{1}[0-9]{3}");

        Matcher m = p.matcher(ppu);
        Matcher m2 = p2.matcher(ppu);

        if (m.matches()){
            return true;
        }else{if (m2.matches()){
            return true;
        }else{
            return false;
        }
        }
    }

    public Boolean verificaUF (int uf){
        // Se verifica que el valor de la UF no sea 0

        if (uf > 0){
            return true;
        }else{
            return false;
        }
    }

    public Boolean verificaAnio (int anio){

        Calendar fecha = Calendar.getInstance();
        int anioActual = fecha.get(Calendar.YEAR);
        int anioMax = anioActual+1;


        // Se verifica que el año del vehículo no seA anterior a la construccion del primer auto
        if (anio > anioMax){
            return false;
        }else {
            if (anio > 1984){
                return true;
            }else{
                return false;
            }
        }
    }

}

