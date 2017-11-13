package cl.inacap.aplicacionseguroauto;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    //Se crean los objetos a usar relacionados al layout

    Spinner listaMarcas;
    EditText modelos;
    EditText placaPatente;
    EditText anioVehiculo;
    EditText valorUF;
    Button verificarSeguro;
    String marcaElegida;
    String url = "http://portal.unap.cl/kb/aula_virtual/serviciosremotos/datos-uf-dia.php";
    String urlMarcasAutos= "https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json";
    JSONArray arrayMarcas;
    JSONObject objectMarcas;
    ArrayList<String> listadoMarcas;
    ArrayList<MarcasAutos> marcas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se asocian objetos con un elemento de la Interfaz
        placaPatente = (EditText) findViewById(R.id.edt_ingresoPPU);
        anioVehiculo = (EditText) findViewById(R.id.edt_anioVehiculo);
        valorUF = (EditText) findViewById(R.id.edt_valorUF);
        verificarSeguro = (Button) findViewById(R.id.btn_seguro);
        modelos = (EditText) findViewById(R.id.edt_modelos);
        listaMarcas = (Spinner) findViewById(R.id.spin_marcas);

        listaMarcas.setOnItemSelectedListener(this);

        new ProcessJSON().execute(url);
        new marcasJSON().execute(urlMarcasAutos);

        verificarSeguro.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        //Se verifica que los datos de imput no este vacío
        if (!placaPatente.getText().toString().equals("") && !anioVehiculo.getText().toString().equals("") &&
                !valorUF.getText().toString().equals("") && !modelos.getText().toString().equals("")
                ) {

            String patente = placaPatente.getText().toString();
            int anioVeh = Integer.parseInt(anioVehiculo.getText().toString());
            int unidadFomento = Integer.parseInt(valorUF.getText().toString());
            //String spinMarca = listaMarcas.getSelectedItem().toString();
            String spinMarca = marcaElegida;
            String modeloAuto = modelos.getText().toString();

            //Se verifica patente en caso errado se emite Toast
            if (verificaPPU(patente) == false) {
                Toast.makeText(getApplicationContext(), "La patente esta mal ingresada", Toast.LENGTH_SHORT).show();
            }
            //Se verifica año del vehiculo en caso errado se emite Toast
            if (verificaAnio(anioVeh) == false) {
                Toast.makeText(getApplicationContext(), "El año no corresponde", Toast.LENGTH_SHORT).show();
            }

            if(verificaModelo(modeloAuto) == false){
                Toast.makeText(getApplicationContext(), "Debe ingresar un modelo", Toast.LENGTH_SHORT).show();
            }

            //Se verifica patente, el año de vehiculo sean correctos antes de iniciar nuevo activity
            if (verificaPPU(patente) && verificaAnio(anioVeh) && verificaModelo(modeloAuto)) {

                Intent intento = new Intent(MainActivity.this, ResultadoSeguro.class);

                intento.putExtra("carroPPU", patente);
                intento.putExtra("carroAnio", anioVeh);
                intento.putExtra("valorUF", unidadFomento);
                intento.putExtra("spinnerMarca", spinMarca);
                intento.putExtra("modeloAuto", modeloAuto);
                startActivity(intento);
            }

        } else {
            // Se solicitan que cumpla con los datos requeridos
            Toast.makeText(getApplicationContext(), "Ingrese los datos requeridos", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean verificaPPU(String ppu) {
        // Se verifica que la patente haya sido ingresada con los valores AAAA55 ó AA5555


        Pattern p = Pattern.compile("[BCDFGHJKLPRSTVWXYZ]{4}[1-9]{1}[0-9]{1}");
        Pattern p2 = Pattern.compile("([ABCEFGHDKLNPRSTUVXYZWM]){1}[ABCDEFGHIJKLNPRSTUVXYZ]{1}[1-9]{1}[0-9]{3}");

        Matcher m = p.matcher(ppu);
        Matcher m2 = p2.matcher(ppu);

        if (m.matches()) {
            return true;
        } else {
            if (m2.matches()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public Boolean verificaAnio(int anio) {

        Calendar fecha = Calendar.getInstance();
        int anioActual = fecha.get(Calendar.YEAR);
        int mesActual = fecha.get(Calendar.MONTH);
        mesActual = mesActual +1;
        int anioMax = anioActual;

        if(mesActual > 8){
            anioMax = anioActual + 1;
        }

        // Se verifica que el año del vehículo no sea anterior a la puesta en marcha de las nuevas patente AA1000
        if (anio > anioMax) {
            return false;
        } else {
            if (anio > 1984) {
                return true;
            } else {
                return false;
            }
        }
    }

    public Boolean verificaModelo (String modelo){
        String modeloAuto = modelo.trim();

        if(!modeloAuto.equals("")){
            return true;
        }
            return  false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        marcaElegida = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class ProcessJSON extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }

        protected void onPostExecute(String stream) {
            //TextView tv = (TextView) findViewById(R.id.txv_resultado);

            if (stream != null) {
                try {
                    // Obtenemos todos los datos HTTP medinte un objeto JSONObject
                    JSONObject reader = new JSONObject(stream);

                    // Obtenemos uno de los valores que necesitamos
                    String valorApiUF = reader.getString("VALOR_UF");


                    valorUF.setText(valorApiUF);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }




        }

    }

    private class marcasJSON extends  AsyncTask<String, Void, String>{

        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }

        protected void onPostExecute (String stream){
    //
            marcas = new ArrayList<MarcasAutos>();
            listadoMarcas = new ArrayList<String>();


            if (stream != null) {
                try {
                    // Obtenemos todos los datos HTTP medinte un objeto JSONObject
                    objectMarcas = new JSONObject(stream);

                    arrayMarcas = objectMarcas.getJSONArray("Results");

                    for (int i= 0; i < arrayMarcas.length(); i++){
                        objectMarcas = arrayMarcas.getJSONObject(i);

                        MarcasAutos poblarListado = new MarcasAutos();

                        poblarListado.setModeloId(objectMarcas.optString("MakeId"));
                        poblarListado.setNombreModelo(objectMarcas.optString("MakeName"));
                        marcas.add(poblarListado);


                        listadoMarcas.add(objectMarcas.optString("MakeName"));
                    }



                    listaMarcas.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, listadoMarcas));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}

