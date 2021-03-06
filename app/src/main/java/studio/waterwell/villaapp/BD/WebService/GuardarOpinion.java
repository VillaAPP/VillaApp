package studio.waterwell.villaapp.BD.WebService;

import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import studio.waterwell.villaapp.Modelo.MiOpinion;
import studio.waterwell.villaapp.Modelo.Opinion;

/**
 * Created by Efecto Dopler on 11/05/2017.
 */

// Por hacer
public class GuardarOpinion extends AsyncTask<MiOpinion, Void, String> {

    private String userName;

    public GuardarOpinion(String userName){
        this.userName = userName;
    }

    @Override
    protected String doInBackground(MiOpinion... params) {
        MiOpinion opinion = params[0];

        try {
            URL url = new URL("http://www.villaApp.esy.es/set_opinion.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Habilito el poder mandar JSON a la conexión
            connection.setDoInput(true);

            // Habilito el poder recibir JSON a la conexión
            connection.setDoOutput(true);

            // No habilito el uso de caches
            connection.setUseCaches(false);

            // Especifico que mando JSON y algunas propiedades necesarias
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0"
                    + " (Linux, Android 1.5; es-Es) Ejemplo HTTP");


            // Realizo la conexión
            connection.connect();

            JSONObject paramsJSON = new JSONObject();
            paramsJSON.put("userName", this.userName);
            paramsJSON.put("idLugar", opinion.getId());
            paramsJSON.put("rate", opinion.getRate());
            paramsJSON.put("opinion", opinion.getOpinion());

            // Envio el json mediante el método POST
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(paramsJSON.toString());
            writer.flush();
            writer.close();


            int respuesta = connection.getResponseCode();

            if (respuesta == HttpURLConnection.HTTP_OK) {
                // Declaro la lectura
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                // Creo un buffer de lectura
                BufferedReader read = new BufferedReader(in);

                // String donde guardar los resultados
                StringBuilder result = new StringBuilder();

                // String donde ir guardando linea a linea
                String linea = read.readLine();

                while (linea != null) {
                    result.append(linea);
                    linea = read.readLine();
                }

                // Creo un JSON donde almaceno lo guardado en el while
                JSONObject respuestaJSON = new JSONObject(result.toString());

                // Guardo en la variable la respuesta "estado" de la consulta
                String estadoJSON = respuestaJSON.getString("estado");

                if (estadoJSON.equals("1")) {

                }

                connection.disconnect();
            }

        } catch (MalformedURLException e) {
            Log.i("Error", "Fallo en la URL");
        } catch (IOException e) {
            Log.i("Error", "Fallo IOException");
        } catch (JSONException e) {
            Log.i("Error", "Fallo de JSON");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Hola";
    }

}
