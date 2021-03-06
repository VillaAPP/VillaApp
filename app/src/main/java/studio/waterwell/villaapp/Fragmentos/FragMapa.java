package studio.waterwell.villaapp.Fragmentos;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import studio.waterwell.villaapp.Modelo.ICambios;
import studio.waterwell.villaapp.Modelo.Lugar;
import studio.waterwell.villaapp.Modelo.Usuario;
import studio.waterwell.villaapp.R;

public class FragMapa extends Fragment {

    private Mapa mapa;
    private Button boton;
    private Button boton2;
    private ICambios cambios;
    private ViewGroup container;


    public FragMapa() {
        // Required empty public constructor
    }

    public static FragMapa newInstance(ArrayList<Lugar> lugares, Usuario usuario) {
        FragMapa fragment = new FragMapa();
        Bundle args = new Bundle();

        args.putParcelableArrayList("lugares", lugares);
        args.putParcelable("usuario", usuario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.container = container;
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mapa2, container, false);

        ArrayList<Lugar>  lugares = getArguments().getParcelableArrayList("lugares");
        Usuario usuario = getArguments().getParcelable("usuario");
        mapa = Mapa.newInstance(lugares, usuario);
        getFragmentManager().beginTransaction()
                .add(R.id.frag_mapa, mapa)
                .commit();

        boton = (Button) v.findViewById(R.id.btn_tmapa);

        boton2 = (Button) v.findViewById(R.id.btn_ruta);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapa.cambiarMapa();
            }
        });

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapa.borraRuta();
            }
        });

        return v;
    }

    // Devuelve las coordenadas actuales del usuario obtenidas desde el mapa
    public LatLng obtenerMiUbicacion(){
        return mapa.getMisCoordenadas();
    }

    // Recibe desde principal el conjunto de puntos que forman la ruta para que el mapa lo dibuje
    public void pasarRuta(List<List<HashMap<String, String>>> ruta){
        mapa.setRuta(ruta);
    }


    // Recibe desde principal las coordenadas donde el mapa debe poner la camara
    public void moverUbicacion(LatLng latLng){
        mapa.ubicarse(latLng);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cambios = (ICambios) getActivity();
    }

}
