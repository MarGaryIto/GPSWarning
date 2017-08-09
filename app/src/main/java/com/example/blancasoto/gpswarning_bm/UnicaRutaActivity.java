package com.example.blancasoto.gpswarning_bm;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

public class UnicaRutaActivity extends AppCompatActivity {

    EditText et_nombreRuta,et_destinoRuta;
    NumberPicker np_avisarAntesDeNumero,np_avisarAntesDeUnidad;
    Button bt_aceptarRuta,bt_cancelarRuta;
    String numeroRutas,avisarAntesMedida;
    int avisarAntesNumero = 0;
    Switch sw_activaRuta;
    LocationManager locationManager;
    boolean destinoCerca = false;
    final String[] matrizDistancias= {"Mts","Kms"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unica_ruta);

        //instanciar los elementos EditText, NumberPicker, Switch y Buttons
        et_nombreRuta = (EditText) findViewById(R.id.et_nombreRuta);
        et_destinoRuta = (EditText) findViewById(R.id.et_destinoRuta);
        np_avisarAntesDeNumero = (NumberPicker) findViewById(R.id.np_avisarAntesDeNumero);
        np_avisarAntesDeUnidad = (NumberPicker) findViewById(R.id.np_avisarAntesDeUnidad);
        bt_aceptarRuta = (Button) findViewById(R.id.bt_aceptarRuta);
        bt_cancelarRuta = (Button) findViewById(R.id.bt_cancelarRuta);
        sw_activaRuta = (Switch) findViewById(R.id.sw_activaRuta);

        //configurar y asignar valores a los elementos
        et_nombreRuta.setText(getIntent().getExtras().getString("nombre").toString());
        np_avisarAntesDeNumero.setMaxValue(1000);np_avisarAntesDeNumero.setMinValue(10);
        numeroRutas = getIntent().getExtras().getString("numeroRuta");
        np_avisarAntesDeUnidad.setMinValue(0);
        np_avisarAntesDeUnidad.setMaxValue(matrizDistancias.length-1);
        np_avisarAntesDeUnidad.setDisplayedValues(matrizDistancias);
        np_avisarAntesDeUnidad.setWrapSelectorWheel(true);

        //asignar metodos que escuchen los elementos
        np_avisarAntesDeNumero.setOnValueChangedListener(onValueChangeListener);
        np_avisarAntesDeUnidad.setOnValueChangedListener(onValueChangeListener);
        bt_aceptarRuta.setOnClickListener(onClickListener);
        bt_cancelarRuta.setOnClickListener(onClickListener);
        et_destinoRuta.setOnClickListener(onClickListener);

    }

    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if(picker == np_avisarAntesDeNumero){
                avisarAntesNumero = newVal;
                Toast.makeText(getApplicationContext(),"tiempo: "+avisarAntesNumero,Toast.LENGTH_SHORT).show();
            }else if(picker == np_avisarAntesDeUnidad){
                avisarAntesMedida = matrizDistancias[newVal];
                Toast.makeText(getApplicationContext(),"unidad: "+avisarAntesMedida,Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == et_destinoRuta){
                abrirActivityMaps();
            }else if(v == bt_aceptarRuta){
                if (evaluaCampos()){
                    guardarRuta();
                    if(sw_activaRuta.isChecked()){
                        encenderAlarma();
                    }else {
                        apagarServicios();
                    }
                }else{
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.mens_camposIncompletos);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }else if(v == bt_cancelarRuta){
                preguntarSalida();
            }
        }
    };
    private void apagarServicios(){
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }

    private void encenderAlarma(){

        //creacion de procesos en segundo plano tipo Thread
        new Thread(new Runnable() {

            //ejecucion componentes que esten fuera de la aplicacion
            @Override
            public void run() {

                //creacion de segundo plano con metdos que se ejecutan fuera de la Aplicacion
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Estos metodos llaman a cmponentes que esten dentro de la aplicacion
                        String text = getString(R.string.mens_alarmaEncendida);
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                        //mostrarNotificacion();
                        miUbicacion();
                    }
                });
            }
        }).start();//iniciar proceso
    }

    private void mostrarNotificacion(){

        //construccion del notification
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr =(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        //parametros del la notificacion almacenados en variables
        int icono = R.drawable.ic_icon_gpswbm;//icono
        Intent i=new Intent(UnicaRutaActivity.this, MainActivity.class);//donde se ejecuta,a donde lleva
        PendingIntent pendingIntent = PendingIntent.getActivity(UnicaRutaActivity.this, 0, i, 0);//preparacion
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);//sonido

        //asignacion de parametros
        mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(pendingIntent)//actividad a realizar
                .setSmallIcon(icono)//icono
                .setContentTitle(getString(R.string.app_name))//titulo
                .setContentText(getString(R.string.mens_destinoAlcanzado))//mensaje
                .setVibrate(new long[] {100, 250, 100, 500})//vibracion
                .setAutoCancel(false)//cancelar automaticamente(no)
                .setSound(defaultSound);//sonido

        //mostrar notificacion
        mNotifyMgr.notify(1, mBuilder.build());
    }

    private Location createNewLocation(double latitude,double longitude) {
        Location location = new Location("dummyprovider");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    private void actualizarUbicacion(Location location) {
        //preparar archivo temporal prefs
        SharedPreferences prefs = getSharedPreferences("todosDatosRutas",MODE_PRIVATE);

        //almacenamiento de coordenadas en variables
        double latDestino = Double.parseDouble(prefs.getString("destino_n","0.0"));
        double longDestino = Double.parseDouble(prefs.getString("destino_s","0.0"));
        Location misCoordenadas = createNewLocation(latDestino, longDestino);

        //calculo de distancia entre dos coordenadas
        double distance = (int)location.distanceTo(misCoordenadas);

        //almacenamiento de mensaje en variable
        String text = getString(R.string.txt_faltan)+distance+"m";

        //se muestra el mensaje en una notificacion tipo Toast
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

        //ejecucion de tareas si el destino se ha alcanzado
        /*if (distance<=Integer.parseInt(et_avisarNum.getText().toString())){

            //se muestra una notificacion tipo Toast
            Toast.makeText(getApplicationContext(), getString(R.string.mens_destinoAlcanzado), Toast.LENGTH_LONG).show();

            //se almacena en una variable boolean
            destinoCerca = true;

            mostrarNotificacion();
        }*/
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (destinoCerca){
                locationManager.removeUpdates(locationListener);
                locationManager = null;
            }else{
                actualizarUbicacion(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.mens_gpsDesactivado);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    };

    private void miUbicacion() {
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,locationListener);
    }

    private void preguntarSalida(){
        MessageButtonOkCancel(getString(R.string.titulo_mens_salida), getString(R.string.mens_salida), getString(R.string.txt_aceptar), getString(R.string.txt_cancelar));
    }
    public void MessageButtonOkCancel(String title, String message, String aceptText, String cancelText){
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(aceptText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface alertDialog, int id) {
                abrirActivityTodasRutas();
                finish();
            }
        });

        alertDialog.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface alertDialog, int id) {

            }
        });
        alertDialog.show();
    }

    private void guardarRuta(){
        String numeroRuta = getIntent().getExtras().getString("numeroRuta");
        SharedPreferences prefs = getSharedPreferences("todosDatosRutas", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        /*editor.putString("nombre"+numeroRuta, et_nombreRutaNueva.getText().toString());
        editor.putString("origen_n"+numeroRuta, et_origenN.getText().toString());
        editor.putString("origen_s"+numeroRuta, et_origenS.getText().toString());
        editor.putString("destino_n"+numeroRuta, et_destinoN.getText().toString());
        editor.putString("destino_s"+numeroRuta, et_destinoS.getText().toString());
        editor.putString("avisar_num"+numeroRuta, et_avisarNum.getText().toString());*/
        editor.commit();
        abrirActivityTodasRutas();
        finish();
    }
    private Boolean evaluaCampos(){
        if (et_nombreRuta.getText().length()==0){
            return false;
        }else if (et_destinoRuta.getText().length()==0){
            return false;
        }
        return true;
    }

    private void abrirActivityMaps(){
        Intent intent = new Intent(this,MapActivity.class);
        intent.putExtra("numeroDeRuta", getIntent().getExtras().getString("numeroRuta"));
        startActivity(intent);
        finish();
    }

    private void abrirActivityTodasRutas(){
        Intent intent = new Intent(this,TodasRutasActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        preguntarSalida();
    }
}
