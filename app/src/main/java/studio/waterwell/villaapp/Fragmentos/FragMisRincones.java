package studio.waterwell.villaapp.Fragmentos;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import studio.waterwell.villaapp.Modelo.AdaptadorLista;
import studio.waterwell.villaapp.Modelo.ICambios;
import studio.waterwell.villaapp.Modelo.Lugar;
import studio.waterwell.villaapp.Modelo.MiOpinion;
import studio.waterwell.villaapp.Modelo.Usuario;
import studio.waterwell.villaapp.R;


public class FragMisRincones extends Fragment {
    private final int RUTA = 2;
    private final int OPINION = 2;
    private ListView lista;
    private AdaptadorLista adaptador;
    private Usuario usuario;
    private int posicion;
    private ICambios cambios;

    private ArrayList<Lugar> lugares;

    private ViewGroup container;

    public FragMisRincones() {
        // Required empty public constructor
    }

    public static FragMisRincones newInstance(ArrayList<Lugar> lugares, Usuario usuario) {
        FragMisRincones fragment = new FragMisRincones();
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
            usuario = getArguments().getParcelable("usuario");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        View v = inflater.inflate(R.layout.fragment_rincones, container, false);
        lista = (ListView) v.findViewById(R.id.listas_rincones);

        lugares = getArguments().getParcelableArrayList("lugares");
        ArrayList<Lugar> lugaresVistados = new ArrayList<Lugar>();

        // Recorre la lista de sitios buscando los votados por el usuario
        if(usuario.getOpiniones() != null){
            for(int i = 0; i < usuario.getOpiniones().size(); i++){
                String id = usuario.getOpiniones().get(i).getId();
                boolean encontrado = false;
                int index = 0;
                while(!encontrado){
                    if(lugares.get(index).getId().equals(id)){
                        encontrado = true;
                        lugaresVistados.add(lugares.get(index));
                    }
                    else
                        index++;
                }
            }
        }

        adaptador = new AdaptadorLista(getActivity(), lugaresVistados);
        lista.setAdapter(adaptador);

        // Fijo el evento de que cuando clico en un objeto de la lista devuelvo sus coordenadas para que el mapa se centre en él
        lista.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lugar lugar = (Lugar) parent.getItemAtPosition(position);
                posicion = position;
                Intent i = new Intent();
                i.setAction("android.intent.action.lugar");
                Bundle bundle = new Bundle();
                bundle.putParcelable("usuario", usuario);
                bundle.putParcelable("lugar", lugar);
                // Coordenadas auxiliares ya que no se va a mirar estos parámetros desde aquí
                bundle.putDouble("latitud", 1.0);
                bundle.putDouble("longitud", 1.0);
                i.putExtra("bundle", bundle);
                startActivityForResult(i, OPINION);
            }
        });

        return v;
    }

    // Recoge los datos de la actividad Lugar
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getBundleExtra("bundle");

        if(resultCode == getActivity().RESULT_OK){
            if(requestCode == OPINION){
                int opcion = bundle.getInt("opcion");

                if(opcion == RUTA){
                    Lugar lugar = (Lugar) adaptador.getItem(posicion);
                    cambios.mandarCoordenadas(lugar.obtenerCoordenadas());
                }
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cambios = (ICambios) getActivity();
    }

    // Cuando el usuario ha creado una opinion se llama esto desde la actividad principal
    public void actualizarLugares(Usuario usuario){
        this.usuario = usuario;

        ArrayList<Lugar> lugaresVistados = new ArrayList<Lugar>();
        // Recorre la lista de sitios buscando los votados por el usuario
        if(this.usuario.getOpiniones() != null){
            for(int i = 0; i < this.usuario.getOpiniones().size(); i++){
                String id = this.usuario.getOpiniones().get(i).getId();
                boolean encontrado = false;
                int index = 0;
                while(!encontrado){
                    if(lugares.get(index).getId().equals(id)){
                        encontrado = true;
                        lugaresVistados.add(lugares.get(index));
                    }
                    else
                        index++;
                }
            }
        }
        //lugaresVistados.remove(lugaresVistados.size()-1);
        adaptador.setLista(lugaresVistados);
        adaptador.notifyDataSetChanged();
    }


}
