package studio.waterwell.villaapp.Modelo;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Efecto Dopler on 07/05/2017.
 */

public interface ICambios {

    // Manda las coordenadas desde el Mapa a principal al haber solicitado trazar una ruta
    public void mandarCoordenadas(LatLng rincon);

    // Manda las coordenadas desde FragRincon a principal para que este se lo mande a Mapa y actualice la camara
    public void cambiarCamara(LatLng rincon);

    // Manda desde el AsynTack ObtenerRuta el conjunto de puntos que dibujan en el mapa la ruta desde mi ubicacion hasta el destino
    public void obtenerRuta(List<List<HashMap<String, String>>> ruta);

    public void modificarRuta(LatLng miPosicion);

    // Cuando un usuario crea una opinion, se modifica la actividad principal
    public void modificarLugaresVistiados(Usuario usuario);
}
