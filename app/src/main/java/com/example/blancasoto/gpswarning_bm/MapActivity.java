package com.example.blancasoto.gpswarning_bm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    //declaracion de variables globales
    private GoogleMap mMap;
    Button bt_tipoTerreno,bt_tipoSatelite,bt_tipoHibrido,bt_anadeMarcadorCancelar,bt_anadeMarcadorAceptar;
    Double latitud = -34.0;
    Double longitud = 151.0;
    Marker markerDestino;
    String direccionRuta = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //instanciar los elementos Button
        bt_tipoTerreno = (Button)findViewById(R.id.bt_tipoTerreno);
        bt_tipoSatelite = (Button)findViewById(R.id.bt_tipoSatelite);
        bt_tipoHibrido = (Button)findViewById(R.id.bt_tipoHibrido);
        bt_anadeMarcadorCancelar = (Button)findViewById(R.id.bt_anadeMarcadorCancelar);
        bt_anadeMarcadorAceptar = (Button)findViewById(R.id.bt_anadeMarcadorAceptar);

        //adicion del metodo listener que escucha cuando un Button fue presionado
        bt_tipoTerreno.setOnClickListener(onClickListener);
        bt_tipoSatelite.setOnClickListener(onClickListener);
        bt_tipoHibrido.setOnClickListener(onClickListener);
        bt_anadeMarcadorCancelar.setOnClickListener(onClickListener);
        bt_anadeMarcadorAceptar.setOnClickListener(onClickListener);
    }


    //metodo que escucha elementos tocados
    View.OnClickListener onClickListener = new View.OnClickListener() {

        //metodo de tocar, escucha y almacena en "v" el elemento tocado
        @Override
        public void onClick(View v) {

            //si el elemnto "v" tocado es bt_anadeMarcadorAceptar entonces..
            if (v == bt_anadeMarcadorAceptar) {
                enviarDatosYCerrar();
            } else if (v == bt_tipoTerreno){
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }else if (v == bt_tipoSatelite){
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }else if (v == bt_tipoHibrido){
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }else if (v == bt_anadeMarcadorCancelar){
                finish();
            }
        }
    };//subject: nombre del proyecto dante@utectulancingo.edu.mx

    private void enviarDatosYCerrar() {
        Toast.makeText(getApplicationContext(),""+latitud+" , "+longitud,Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(this,UnicaRutaActivity.class);
        //startActivity(intent);
        /*Intent intent = new Intent(this, UnicaRutaActivity.class);
        //intent.putExtra("latitudDestino", latitud);
        //intent.putExtra("longitudDestino", longitud);
        startActivity(intent);
        finish();*/
    }

    private void anadirMarcadorDestino() {
        //variable de Latitud y Longitud
        LatLng latLngFinal = new LatLng(latitud, longitud + 0.0001);

        //adicion de marcador y sus parametros
        markerDestino = mMap.addMarker(new MarkerOptions()
                .position(latLngFinal)//posicion geografica(...)
                .draggable(true)//desplazable(si)
                .visible(true)//mostrar(si)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_end)));//icono(...)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        latitud = location.getLatitude();
        longitud = location.getLongitude();
        LatLng miUbicacion = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15));

        anadirMarcadorDestino();

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (marker.getId().toString().equals("m0")){
                    Toast.makeText(getApplicationContext()," uno ",Toast.LENGTH_SHORT).show();
                    latitud = marker.getPosition().latitude;
                    longitud = marker.getPosition().longitude;

                }
            }
        });
    }
}
