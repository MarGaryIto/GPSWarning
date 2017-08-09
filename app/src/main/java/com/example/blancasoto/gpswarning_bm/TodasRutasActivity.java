package com.example.blancasoto.gpswarning_bm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class TodasRutasActivity extends AppCompatActivity {

    TextView tv_tituloTodasRutas;
    ListView lv_todasRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todas_rutas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_tituloTodasRutas = (TextView)findViewById(R.id.tv_tituloTodasRutas);
        lv_todasRutas = (ListView)findViewById(R.id.lv_todasRutas) ;



        String[] arrayRutas = new String[5];
        SharedPreferences prefs = getSharedPreferences("todosDatosRutas", MODE_PRIVATE);
        arrayRutas[0]="1-"+prefs.getString("nombre1","editar");
        arrayRutas[1]="2-"+prefs.getString("nombre2","editar");
        arrayRutas[2]="3-"+prefs.getString("nombre3","editar");
        arrayRutas[3]="4-"+prefs.getString("nombre4","editar");
        arrayRutas[4]="5-"+prefs.getString("nombre5","editar");



        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_routers,R.id.tv_nombreRuta,arrayRutas);
        lv_todasRutas.setAdapter(adapter);
        lv_todasRutas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup)view;
                TextView vg_tv_nombreRuta = (TextView)vg.findViewById(R.id.tv_nombreRuta);
                String numeroRuta = vg_tv_nombreRuta.getText().toString().substring(0,1);

                SharedPreferences prefs = getSharedPreferences("todosDatosRutas", MODE_PRIVATE);

                String arrayValoresRutanombre[] = new String[8];
                arrayValoresRutanombre[0] = prefs.getString("nombre"+numeroRuta,"ruta "+numeroRuta);
                arrayValoresRutanombre[1] = prefs.getString("origen_n"+numeroRuta,"0.0");
                arrayValoresRutanombre[2] = prefs.getString("origen_s"+numeroRuta,"0.0");
                arrayValoresRutanombre[3] = prefs.getString("destino_n"+numeroRuta,"0.0");
                arrayValoresRutanombre[4] = prefs.getString("destino_s"+numeroRuta,"0.0");
                arrayValoresRutanombre[5] = prefs.getString("avisar_num"+numeroRuta,"1");
                arrayValoresRutanombre[6] = prefs.getString("fk_medida"+numeroRuta,"1");
                arrayValoresRutanombre[7] = prefs.getString("numeroRuta"+numeroRuta,""+numeroRuta);

                abrirLayoutUnicaRuta(arrayValoresRutanombre);
            }
        });
    }

    private void abrirLayoutUnicaRuta(String[] valoresRuta){
        Intent intent = new Intent(this,UnicaRutaActivity.class);
        intent.putExtra("nombre", valoresRuta[0]);
        intent.putExtra("origen_n", valoresRuta[1]);
        intent.putExtra("origen_s", valoresRuta[2]);
        intent.putExtra("destino_n", valoresRuta[3]);
        intent.putExtra("destino_s", valoresRuta[4]);
        intent.putExtra("avisar_num", valoresRuta[5]);
        intent.putExtra("fk_medida", valoresRuta[6]);
        intent.putExtra("numeroRuta", valoresRuta[7]);
        startActivity(intent);
        finish();
    }

}
