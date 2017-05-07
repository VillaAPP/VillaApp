package studio.waterwell.villaapp.Actividades;



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

import studio.waterwell.villaapp.Controlador.Controlador;
import studio.waterwell.villaapp.Fragmentos.FragMapa;
import studio.waterwell.villaapp.Fragmentos.FragMisRincones;
import studio.waterwell.villaapp.Fragmentos.FragRincones;
import studio.waterwell.villaapp.Fragmentos.ICambios;
import studio.waterwell.villaapp.Modelo.Usuario;
import studio.waterwell.villaapp.R;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ICambios {

    // Componentes de la vista
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    // Componentes de gragmentos
    private FragmentManager fragmentManager;  // Se encarga de cambiar los fragmentos de la vista
    private FragMapa fragMapa;
    private FragRincones fragRincones;
    private FragMisRincones fragMisRincones;


    // Atributos necesarios
    private Usuario usuario;
    private boolean moverUbicacion;
    private LatLng ubicacion;

    // TODO: Coger del bundle mandado por CargaDatosLogin el ArrayList de ubicaciones de toda la app
    private void cargarDatos() {
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        usuario = bundle.getParcelable("Usuario");

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
        fragMapa = FragMapa.newInstance();
        fragRincones = FragRincones.newInstance();
        fragMisRincones= FragMisRincones.newInstance();

        // Los coloco en el controlador de fragmentos y oculto todos menos el del mapa
        fragmentManager.beginTransaction()
                .add(R.id.fragmentoPpal, fragMapa)
                .add(R.id.fragmentoPpal, fragRincones)
                .hide(fragRincones)
                .add(R.id.fragmentoPpal, fragMisRincones)
                .hide(fragMisRincones)
                .commit();

        // La variable que indica que hay que mover el mapa se pone a falso
        moverUbicacion = false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        cargarDatos();
        cargarNavigationDrawer();

        cargarFragmentos();
        getSupportActionBar().setTitle("Mapa de la Villa");
    }

    // Dice que pasa al clicarse una opcion del menú de lateral
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mapa) {
            cambiarFragmento(fragMapa,fragRincones,fragMisRincones, "Mapa de la Villa");

            // TODO: LA IDEA ES QUE ESTO CAMBIE LA POSICION DE LA CAMARA DEL MAPA CUANDO SELECCIONE VER UN SITIO DESDE fragRincones
            if(moverUbicacion){
                // Ubicacion de prueba
                fragMapa.moverUbicacion(ubicacion);
            }
        }
        else if (id == R.id.nav_lista_lugares) {
            // TODO: Este boolean cambiará con un evento del fragmento que reciba los datos pertinentes y será modificado desde un metodo de la interfaz de principal
            moverUbicacion = true;
            ubicacion = new LatLng(40.39991817, -3.6941729);
            cambiarFragmento(fragRincones,fragMapa,fragMisRincones, "Rincones de la Villa");
        }
        else if (id == R.id.nav_lista_visitados) {
            cambiarFragmento(fragMisRincones,fragMapa,fragRincones, "Rincones visitados");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Se encarga de cambiar los fragmentos de la actividad
    private void cambiarFragmento(Fragment mostrar,Fragment ocultar1,Fragment ocultar2, String nombre){
        fragmentManager.beginTransaction()
                .show(mostrar)
                .hide(ocultar1)
                .hide(ocultar2)
                .commit();
        getSupportActionBar().setTitle(nombre);
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void obtenerUbicacion(LatLng latLng) {
        Controlador controlador = new Controlador(getApplicationContext());
        controlador.ObtenerUbicacion(latLng);
    }
}
