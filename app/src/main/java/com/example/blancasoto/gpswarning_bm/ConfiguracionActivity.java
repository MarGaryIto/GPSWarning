package com.example.blancasoto.gpswarning_bm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class ConfiguracionActivity extends AppCompatActivity {

    EditText et_nombreUsuario, et_correoUsuario, et_contrasenaUsuario;
    Button bt_aceptarLogUsuario;
    TextView tv_acceder,tv_yaTengoCuenta,tv_mens_sesion,tv_aceptarCuenta;
    private ArrayAdapter adapter;
    private String json_todosUsuarios = "http://gpswbm.esy.es/GPSWBM/php/json_todosUsuarios.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        //solicitud para conectarse a internet
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        //instancia de elementos EditText, Button y TextView
        et_nombreUsuario = (EditText) findViewById(R.id.et_nombreUsuario);
        et_correoUsuario =  (EditText) findViewById(R.id.et_correoUsuario);
        et_contrasenaUsuario = (EditText) findViewById(R.id.et_contrasenaUsuario);
        bt_aceptarLogUsuario = (Button)findViewById(R.id.bt_aceptarLogUsuario);
        tv_acceder = (TextView)findViewById(R.id.tv_acceder);
        tv_yaTengoCuenta = (TextView) findViewById(R.id.tv_yaTengoCuenta);
        tv_mens_sesion = (TextView) findViewById(R.id.tv_mens_sesion);

        //asignar el metodo onClickListener para escuchar si se pulso un Button o un TextView
        bt_aceptarLogUsuario.setOnClickListener(onClickListener);
        tv_acceder.setOnClickListener(onClickListener);

        //elemento adapter para los datos de Json
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        webServiceRest(json_todosUsuarios);

        evaluarSesion();

    }
    View.OnClickListener onClickListener = new  View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == bt_aceptarLogUsuario){
                if(!camposVacios()){
                    if(getString(R.string.txt_crearCuenta)==bt_aceptarLogUsuario.getText().toString()){
                        crearCuenta();
                    }else{
                        iniciarSesion();
                    }
                }
            }else if (v == tv_acceder){

            }
        }
    };

    private void evaluarSesion(){
        //preparar archivo temporal prefs
        SharedPreferences prefs = getSharedPreferences("todosDatosRutas",MODE_PRIVATE);

        //recoger parametros el archivo prefs, sintaxis: ...("parametro","valor default);
        String nombreUsuario = prefs.getString("nombreUsuario","null");
        String correoUsuario = prefs.getString("correoUsuario","null");
        String contrasenaUsuario = prefs.getString("contrasenaUsuario","null");

        //si el usuario NO es null (o sea vacio)..
        if((!Objects.equals(nombreUsuario, "null"))){
            //..entonces,  recargar sesion con los parametros indicados
            recargarSesion(nombreUsuario,correoUsuario,contrasenaUsuario);
        }
    }

    private void recargarSesion(String usuario,String correo,String contrasena){
        //al recargarse sesion, cambiar el contenido de cada TextView y Botones
        tv_acceder.setText(getString(R.string.txt_cerrarSesion));
        tv_yaTengoCuenta.setText(getString(R.string.txt_mens_cerrarCuenta));
        tv_mens_sesion.setText(getString(R.string.txt_miCuenta));
        bt_aceptarLogUsuario.setText(getString(R.string.txt_editar));

        //asigna parametros de usuario a los TextView
        et_nombreUsuario.setText(usuario);
        et_correoUsuario.setText(correo);
        et_contrasenaUsuario.setText(contrasena);
    }

    private void crearCuenta(){
        //obtener datos escritos y almacenarlos en variables
        String nombreUsuario = et_nombreUsuario.getText().toString();
        String usuarioNombre = et_correoUsuario.getText().toString();
        String contrasenaUsuario = et_contrasenaUsuario.getText().toString();

        //preparar archivo temporal prefs
        SharedPreferences prefs = getSharedPreferences("prefs_Usuario",MODE_PRIVATE);

        //habilitar edicion
        SharedPreferences.Editor editor = prefs.edit();

        //pasar datos escritos a los parametros del archivo temporal prefs
        editor.putString("nombreUsuario", nombreUsuario);
        editor.putString("correoUsuario",usuarioNombre);
        editor.putString("contrasenaUsuario",contrasenaUsuario);

        //guardar cambios con un commit
        editor.commit();
        Toast.makeText(getApplicationContext(),getString(R.string.mens_datosGuardados),Toast.LENGTH_SHORT).show();
    }

    private void iniciarSesion(){
        //
    }

    private void webServiceRest(String requestURL){
        try{
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String webServiceResult="";
            while ((line = bufferedReader.readLine()) != null){
                webServiceResult += line;
            }
            bufferedReader.close();
            parseInformation(webServiceResult);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }



    private void parseInformation(String jsonResult){
        JSONArray jsonArray = null;
        String id_usuario;
        String nombre="";
        String email;
        String contrasena;
        String fk_tipo_usuarios;
        try{
            jsonArray = new JSONArray(jsonResult);
        }catch (JSONException e){
            e.printStackTrace();
        }
        for(int i=0;i<jsonArray.length();i++){
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                id_usuario = jsonObject.getString("id_usuario");
                nombre = jsonObject.getString("nombre");
                email = jsonObject.getString("email");
                contrasena = jsonObject.getString("contrasena");
                fk_tipo_usuarios = jsonObject.getString("fk_tipo_usuarios");
            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(getApplicationContext(),nombre,Toast.LENGTH_SHORT).show();
    }

    private boolean camposVacios(){
        //almacena en text el mensaje de campo incompleto
        String text = getString(R.string.mens_camposIncompletos);

        //la linea siguiente significa: (si el EditText.obtenerTexto.numeroCaracteres == o)
        if(et_nombreUsuario.getText().length()==0){
            //entonces muestra un Toast
            Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
            //entonces regresa un positivo, al hacer return, el metodo se cierra
            return true;
        }else if (et_correoUsuario.getText().length()==0){
            Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
            return true;
        }else if (et_contrasenaUsuario.getText().length()==0){
            Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


 /**********************************************************************************************

    private void accederUsuario(){
        guardaUsuario();
    }

    private void prepararAcceder(){
        et_nombreUsuario.setText("-");
        et_nombreUsuario.setEnabled(false);

        bt_aceptarUsuario.setText(getString(R.string.txt_acceder));
        tv_acceder.setText(getString(R.string.txt_toqueAqui));
        tv_yaTengoCuenta.setText(getString(R.string.txt_restaurarContrasena));
    }

    private void recuperarContrasena(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracionActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = ConfiguracionActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_signin, null))
                // Add action buttons
                .setPositiveButton(R.string.txt_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Context context = getApplicationContext();
                        EditText et_cuentaARecuperar = (EditText)findViewById(R.id.et_cuentaARecuperar);
                        CharSequence text = et_cuentaARecuperar.getText();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                })
                .setNegativeButton(R.string.txt_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        builder.create();
    }



    private Boolean evaluaCampos(){
        if(et_nombreUsuario.getText().length()==0){
            return false;
        }else if(et_correoUsuario.getText().length()==0){
            return false;
        }else if(et_contrasenaUsuario.getText().length()==0){
            return false;
        }
        return true;
    }

    private void guardaUsuario(){
        String nombreUsuario = et_nombreUsuario.getText().toString();
        String correoUsuario = et_correoUsuario.getText().toString();
        String contrasenaUsuario = et_contrasenaUsuario.getText().toString();

        SharedPreferences prefs = getSharedPreferences("prefs_Usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombreUsuario",nombreUsuario);
        editor.putString("correoUsuario", correoUsuario);
        editor.putString("contrasenaUsuario", contrasenaUsuario);

        editor.commit();
    }

    private void editarUsuario(){
        guardaUsuario();
        evaluarSesionUsuario();

    }*/
}
