package studio.waterwell.villaapp.Actividades;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import studio.waterwell.villaapp.Controlador.Controlador;
import studio.waterwell.villaapp.Controlador.ControladorLugar;
import studio.waterwell.villaapp.Fragmentos.FragInformacion;
import studio.waterwell.villaapp.Fragmentos.FragMapa;
import studio.waterwell.villaapp.Fragmentos.FragMisRincones;
import studio.waterwell.villaapp.Fragmentos.FragRincones;
import studio.waterwell.villaapp.Modelo.ICambios;
import studio.waterwell.villaapp.Modelo.Lugar;
import studio.waterwell.villaapp.Modelo.Usuario;
import studio.waterwell.villaapp.R;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ICambios {

    /* Array de lugares */

    private ArrayList<Lugar> lugares;

    /* Componentes de la vista */

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    /* Componentes de fragmentos */

    // Se encarga de cambiar los fragmentos de la vista
    private FragmentManager fragmentManager;
    private FragMapa fragMapa;
    private FragRincones fragRincones;
    private FragMisRincones fragMisRincones;
    private FragInformacion fragInformacion;
    // Se encarga de ir atrás al pulsar atras o cerrar la app
    private boolean atras;

    /* Atributos necesarios del modelo */

    private Controlador controlador;
    private Usuario usuario;
    private LatLng ubicacion;

    private void cargarDatos() {
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        usuario = bundle.getParcelable("Usuario");
        lugares = bundle.getParcelableArrayList("lugares");
    }

    // Cambia la cabecera de la barra por los valores del usuario logeado
    private void actualizarCabeceraUser(){

        // Obtengo la vista de la cabecera que forma el navigationView
        View auxview = navigationView.getHeaderView(0);

        // Cargo los campos user y email que forma la cabecera y les cambio el valor
        TextView user = (TextView) auxview.findViewById(R.id.nav_usuario);
        user.setText(usuario.getUserName());
    }

    // Crea la barra lateral que se desplaza con el dedo para moverse en opciones
    private void cargarNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        actualizarCabeceraUser();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void cargarFragmentos(){

        // Inicio los fragmentos
        fragmentManager = getSupportFragmentManager();
        fragMapa = FragMapa.newInstance(this.lugares, this.usuario);
        fragRincones = FragRincones.newInstance(this.lugares);
        fragMisRincones = FragMisRincones.newInstance(lugares, usuario);
        fragInformacion = FragInformacion.newInstance();

        // Los coloco en el controlador de fragmentos y oculto todos menos el del mapa
        fragmentManager.beginTransaction()
                .add(R.id.fragmentoPpal, fragMapa)
                .add(R.id.fragmentoPpal, fragRincones)
                .add(R.id.fragmentoPpal, fragMisRincones)
                .add(R.id.fragmentoPpal, fragInformacion)
                .hide(fragRincones)
                .hide(fragMisRincones)
                .hide(fragInformacion)
                .commit();

        // La variable que indica que hay que mover el mapa se pone a falso
        atras = false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        controlador = new Controlador(getApplicationContext());
        cargarDatos();
        cargarNavigationDrawer();
        cargarFragmentos();
        getSupportActionBar().setTitle(getString(R.string.fragmento_mapa));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO: HAY QUE REVISARLO, NO FUNCIONA.
        ControladorLugar controladorLugar = new ControladorLugar(getApplicationContext(), new Lugar());
        controladorLugar.borrarImagenes();
    }

    // Dice que pasa al clicarse una opcion del menú de lateral
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        atras = true;

        if (id == R.id.nav_lista_lugares)
            cambiarFragmento(fragRincones,fragMapa,fragMisRincones, fragInformacion, getString(R.string.fragmento_rincones));

        else if (id == R.id.nav_lista_visitados)
            cambiarFragmento(fragMisRincones,fragMapa,fragRincones, fragInformacion, getString(R.string.fragmento_mis_rincones));

        else if (id == R.id.nav_informacion)
            cambiarFragmento(fragInformacion, fragMapa, fragRincones, fragMisRincones, "Acerca de");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Se encarga de cambiar los fragmentos de la actividad
    private void cambiarFragmento(Fragment mostrar,Fragment ocultar1,Fragment ocultar2, Fragment ocultar3, String nombre){
        fragmentManager.beginTransaction()
                .show(mostrar)
                .hide(ocultar1)
                .hide(ocultar2)
                .hide(ocultar3)
                .commit();
        getSupportActionBar().setTitle(nombre);
    }

    // Controla que sucede cuando se pulsa el boton de atras
    @Override
    public void onBackPressed() {

        // Si estamos viendo el mapa de google la app se minimiza
        if(!atras)
            finish();

        // Si no, se simula ir atras cargando el fragmento del mapa
        else{
            atras = false;
            cambiarFragmento(fragMapa,fragRincones,fragMisRincones, fragInformacion, getString(R.string.fragmento_mapa));
        }
    }

    // Manda las coordenadas desde el fragmento Mapa a principal para trazar una ruta
    @Override
    public void mandarCoordenadas(LatLng latLng) {
        atras = false;
        ubicacion = latLng;
        cambiarFragmento(fragMapa,fragRincones,fragMisRincones, fragInformacion, getString(R.string.fragmento_mapa));
        fragMapa.moverUbicacion(latLng);
        LatLng miUbicacion = fragMapa.obtenerMiUbicacion();
        controlador.obtenerRuta(this, ubicacion, miUbicacion);
    }

    @Override
    public void cambiarCamara(LatLng rincon) {
        fragMapa.moverUbicacion(rincon);
        cambiarFragmento(fragMapa,fragRincones,fragMisRincones, fragInformacion, getString(R.string.fragmento_mapa));
    }

    // Manda desde el AsynTack ObtenerRuta el conjunto de puntos que dibujan en el mapa la ruta desde mi ubicacion hasta el destino
    @Override
    public void obtenerRuta(List<List<HashMap<String, String>>> ruta) {
        fragMapa.pasarRuta(ruta);
    }

    @Override
    public void modificarRuta(LatLng miPosicion) {
        controlador.obtenerRuta(this, ubicacion, miPosicion);
    }

    @Override
    public void modificarLugaresVistiados(Usuario usuario) {
        this.usuario = usuario;
        fragMisRincones.actualizarLugares(usuario);
    }



}
