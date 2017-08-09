package com.example.blancasoto.gpswarning_bm;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;

public class RutaActivity extends AppCompatActivity {

    TextView tv_latitud,tv_longitud;
    ToggleButton bt_latLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);

        tv_latitud = (TextView) findViewById(R.id.tv_latitud);
        tv_longitud = (TextView) findViewById(R.id.tv_longitud);
        bt_latLong = (ToggleButton) findViewById(R.id.bt_latLong);

    }
    private void updateUI(Location loc) {
        if (loc != null) {
            tv_latitud.setText("Latitud: " + String.valueOf(loc.getLatitude()));
            tv_longitud.setText("Longitud: " + String.valueOf(loc.getLongitude()));
        } else {
            tv_latitud.setText("Latitud: (desconocida)");
            tv_longitud.setText("Longitud: (desconocida)");
        }
    }
}
