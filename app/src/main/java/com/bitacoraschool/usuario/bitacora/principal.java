package com.bitacoraschool.usuario.bitacora;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class principal extends AppCompatActivity /*implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener*/ {
    //public static final String SEND_DATA_URL = "http://199.89.55.4/ASDE/api/v1/operador/senddata";
    //Bitmap bitmap;
    //RelativeLayout Relative_Actividad_Principal;
    //Button btnEnviar;   // Enviar evento primera vez a webservice
    //ImageView imgFoto;
    /*EditText editNombre;

    EditText editDetalle;
    EditText editDireccion;

    ProgressDialog progressDialog;

    /***************Valores usados en Magic Camera para la obtencion de la foto******/

    /*rivate static String  APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    String imagen64; // imagen en formato string64
    String mPath; // direccion de la imagen en el celular
    String fechaFoto, fechaFotoGmt;

    boolean bandera = true;
    ProgressBar progressBar;



    /*******************Valores Usados en la Localizacion *********************/

    /*private static final String LOGTAG = "android-localizacion";
    private static final int PETICION_PERMISO_LOCALIZACION = 101; //numero para solicitar el permiso de localizacion

    GoogleApiClient mGoogleApiClient = null; //variable para requerir servicios del API Google

    LocationManager locationGPS;

    AlertDialog alert = null;


    /**************** VALORES PRINCIPALES DEL MODELO (Localizacion)***********************/

    /*String Longitud;
    String Latitud;
    String formatted_address;

    String Direccion; // Direccion cuando se obtiene de Geocoder


    // API de GOOGLE para Obtener la Direccion
    String URL_API_GEOCODER = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + Latitud + ","+ Longitud + "&sensor=true";

    /*******************VARIABLES PARA EL ENVIO DE EVENTOS AL SERVER *********************/

   /* Eventos evento;


    /*************************Variable ListView para contener los registros nuevos **********/
    /********************Posiblemente sea sustituido por  la tabla de SQLITE ***************/

    //ArrayList<Eventos> listaEvento = new ArrayList<Eventos>();



    ListView listViewGeneral;
    routes_adapter itemsAdapter;
    ArrayList<rutas_model> rutas;
    ProgressBar pg;
    android.support.design.widget.FloatingActionButton reloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        checkForUsers();
        //DBHandler db = new DBHandler(this);
        //List<rutas_model> asd = db.getAllRutas();
        //validate if user is already logged in, if not, redirect to LogIn page
        listViewGeneral = (ListView)findViewById(R.id.list_itemview_ruta);
        pg = (ProgressBar)findViewById(R.id.principal_pg);
        pg.setVisibility(View.VISIBLE);
        getMyEvents();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);


        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(this,LogIn.class);
                SharedPreferences preferences = getSharedPreferences("CustomPreferences",Context.MODE_PRIVATE);
                preferences.edit().putString("rolID",null).commit();
                preferences.edit().putString("userID",null).commit();
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getMyEvents() {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/getRoutes";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ArrayList<rutas_model> rutas = new ArrayList<rutas_model>();
                            JSONObject j = new JSONObject(response);
                            JSONArray arrayJson = j.getJSONArray("posts");
                            for (int i = 0; i<arrayJson.length();i++){
                                rutas_model rutasArray = new rutas_model();
                                JSONObject obj = arrayJson.getJSONObject(i);
                                rutasArray.setId(obj.getInt("id"));
                                rutasArray.setDescripcion(obj.getString("descripcion"));
                                rutasArray.setLocalidadNombre(obj.getString("nombreLocalidad"));
                                rutasArray.setLocalidad(obj.getString("localidadID"));
                                rutasArray.setTanda(obj.getString("tanda"));
                                rutasArray.setRutaType(obj.getInt("type"));
                                rutas.add(rutasArray);
                            }
                            setArrayToList(rutas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(principal.this, "Tiempo para conexion finalizado, revise su conexion a internet",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(principal.this, "Error en el servidor, Contactese con el suplidor de su aplicacion",Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(principal.this, "Error de coneccion. Revise el estado de su coneccion a internet",Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(principal.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion",Toast.LENGTH_LONG).show();
                        }
                        reloadEvent();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences("CustomPreferences", Context.MODE_PRIVATE);
                Map<String, String> map = new HashMap<String, String>();
                map.put("choferId", preferences.getString("userID",null));
                Calendar c = Calendar.getInstance();
                String curTime = String.format("%02d%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                map.put("time",curTime);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void setArrayToList( ArrayList<rutas_model> e){
        pg = (ProgressBar)findViewById(R.id.principal_pg);
        pg.setVisibility(View.GONE);
        rutas = e;
        itemsAdapter = new routes_adapter(this,0,rutas);
//        listViewGeneral.setAdapter(new ArrayAdapter<String>(principal.this,android.R.layout.simple_list_item_1,arr));
        listViewGeneral.setAdapter(itemsAdapter);
        listViewGeneral.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                rutas_model evento = (rutas_model) arg0.getItemAtPosition(position);
                redirect(evento.getId());
            }
        });

    }
    public void redirect(int ruta){
        Intent intent = new Intent(this,DetalleRuta.class);
        Bundle b = new Bundle();
        b.putInt("rutaID", ruta);
        intent.putExtras(b);
        startActivity(intent);

    }
    public void reload(View view){
        pg = (ProgressBar)findViewById(R.id.principal_pg);
        pg.setVisibility(View.VISIBLE);
        reloadButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.reloadButton);
        reloadButton.setVisibility(View.GONE);
        getMyEvents();
    }
    public void reloadEvent(){
        pg = (ProgressBar)findViewById(R.id.principal_pg);
        pg.setVisibility(View.GONE);
        reloadButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.reloadButton);
        reloadButton.setVisibility(View.VISIBLE);
    }
    private void checkForUsers() {
        SharedPreferences preferences = getSharedPreferences("CustomPreferences",Context.MODE_PRIVATE);
        String rol = preferences.getString("rolID", null);

        try {//in case that preferences trigger a nullPointerExeption error
            if (rol.equals(null)) {
                Intent intent = new Intent(this, LogIn.class);
                finish();
                startActivity(intent);
            }
        }catch(Exception e){
            Intent intent = new Intent(this, LogIn.class);
            finish();
            startActivity(intent);
        }
    }
        /*Relative_Actividad_Principal = (RelativeLayout)findViewById(R.id.activity_principal);

        btnEnviar = (Button)findViewById(R.id.buttonEnviar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imgFoto = (ImageView)findViewById(R.id.imageFoto);
        editNombre = (EditText)findViewById(R.id.editNombre);
        editDetalle = (EditText)findViewById(R.id.editDetalle);
        editDireccion = (EditText)findViewById(R.id.editDireccion);


        locationGPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);// Variable necesaria para preguntar por el GPS

        if (!locationGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertaGPS();
        }



        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

        if(myRequestStoragePermission()){
            imgFoto.setClickable(true);
        }else{
            imgFoto.setClickable(false);
        }




        /****************Click para Obtener Foto (Aqui tambien debemos iniciar el servicio de localizacion o antes) *****************/

       /* imgFoto.setOnClickListener(new View.OnClickListener() { /*Activar con el mismo boton de la CAMARA y Llamada para obtener latiud y longitud y por consiguiente Direccion */
      /*      @Override
            public void onClick(View v) {
                bandera = false;
                openCamara();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() { // Validando formulario
            @Override
            public void onClick(View v) {

                Spinner spiner = (Spinner) findViewById(R.id.spinner_evento);
                String n = spiner.getSelectedItem().toString();
                String d = editDetalle.getText().toString().trim();

                if(TextUtils.isEmpty(n)  || TextUtils.isEmpty(d) || TextUtils.equals(d,null)) {

                    Toast.makeText(principal.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();

                }else if (bandera){

                    Toast.makeText(principal.this,"No puede enviar la misma imagen", Toast.LENGTH_SHORT).show();
                }

                else if (imagen64 != null && mPath != null && Direccion != null){ // Validando datos generados a excepcion de Direccion

                    evento = new Eventos(n,d,Latitud,Longitud,Direccion,imagen64,mPath,fechaFoto,fechaFotoGmt,"1");

                    listaEvento.add(evento);

                    bandera = true;

                    envioEvento(); //Envio del evento al WebService
                }

            }
        });




        /**************************   A CONTINUACION SE IMPLANTA EL LISTVIEW DE LOS EVENTOS GUARDADOS Y SE PASA AL   *****************/
        /*************************************    ACTIVITY QUE LOS MUESTRA (CONTROLA)   ********************************************/



/*String[] opciones = {
        "Escombros",
        "Publicidad",
        "Basura",
        "Iluminación",
        "Reparación"
};

        Spinner spiner = (Spinner) findViewById(R.id.spinner_evento);
        ArrayAdapter<String> array = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,opciones);
        array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spiner.setAdapter(array);

*/

   // } // Cierre de la funcion onCreate




   /* private void sendingData(){ //Muestra el ProgressDialog
        progressDialog = ProgressDialog.show(this,"","Enviando Datos, Espere....", true);

    }
    private void dataRetrived(){
        progressDialog.dismiss();
    } // Cierra el ProgressDialog



    //function to confirm if an user is already logged in through sharedPreferences file
    private void checkForUsers() {
        SharedPreferences preferences = getSharedPreferences("CustomPreferences",Context.MODE_PRIVATE);
        String rol = preferences.getString("rolID", null);

        try {//in case that preferences trigger a nullPointerExeption error
            if (rol.equals(null)) {
                Intent intent = new Intent(this, LogIn.class);
                Toast.makeText(this, "Log In", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(intent);
            }
        }catch(Exception e){
            Intent intent = new Intent(this, LogIn.class);
            Toast.makeText(this, "Log In", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(intent);
        }
    }

    //inflating menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        SharedPreferences preferences = getSharedPreferences("CustomPreferences",Context.MODE_PRIVATE);
        String rol = preferences.getString("rolID","");
        switch (rol){
            case "2":
                inflater.inflate(R.menu.menuprincipal, menu);
                break;
            case "7":
                inflater.inflate(R.menu.menu_user,menu);
                break;
            default:
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    //selecting the menu to be shown
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.newEventButton:
                intent = new Intent(this, principal.class);
                finish();
                startActivity(intent);
                break;

            case R.id.myEvents:
                intent = new Intent(this, myEvents.class);
                startActivity(intent);
                break;
            case R.id.logout:
                SharedPreferences preferences = getSharedPreferences("CustomPreferences",Context.MODE_PRIVATE);
                preferences.edit().putString("rolID",null).commit();
                intent = new Intent(this, LogIn.class);
                finish();
                startActivity(intent);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

      /***************************************Funciones manejo de Camara************************************************************/

     /* private boolean myRequestStoragePermission(){

          if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
              return true;
          }

          if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)&&
                  (checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)){
              return true;
          }

          if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))||(shouldShowRequestPermissionRationale(CAMERA))){

              Snackbar.make(Relative_Actividad_Principal,"Los Permisos son necesarios para poder usar la aplicación",
                      Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                  @TargetApi(Build.VERSION_CODES.M)
                  @Override
                  public void onClick(View v) {
                      requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, MY_PERMISSIONS);
                  }
              }).show();

          }else{
              requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, MY_PERMISSIONS);
          }


          return false;
      }


    public void openCamara(){

        File file = new File (Environment.getExternalStorageDirectory(),MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated){
            isDirectoryCreated = file.mkdirs();
        }

        if(isDirectoryCreated){

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");


            Calendar c = Calendar.getInstance();

            fechaFoto = df.format(c.getTime()).trim();

            Date fechaDate = null;
            try {
                fechaDate = df.parse(fechaFoto);
            }
            catch (ParseException ex)
            {
                ex.printStackTrace();
            }

            fechaFotoGmt = fechaDate.toString().trim();


            // Long timestamp = System.currentTimeMillis()/1000;

            String imagename = fechaFoto + ".png";
            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + imagename;

            File newfile = new File(mPath);
            Intent intenCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intenCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newfile));
            startActivityForResult(intenCamera,PHOTO_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PHOTO_CODE){

            MediaScannerConnection.scanFile(this,
                    new String[]{mPath}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned" + path + ":");
                            Log.i("ExternalStorage", "-> Uri" + uri);
                        }
                    });


            bitmap = getBitmap(mPath);

            Glide.with(this).load(mPath).into(imgFoto);


            if (bitmap!= null){
                btnEnviar.setEnabled(false);
                btnEnviar.setText("");
                progressBar.setVisibility(View.VISIBLE);
                new ConvertStringImage().execute(bitmap);//Creacion y llamada a la tarea ConvertStringImage
            }else{
                Toast.makeText(this, "Error en la creacion del bitmap. Debe tomar una foto de baja resolución.", Toast.LENGTH_SHORT).show();
            }
            getDireccion("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + Latitud + ","+ Longitud + "&sensor=true");

        }
    }

    public Bitmap getBitmap(String path){
        Bitmap bitmap = null;
        BitmapFactory.Options options;
        try {
            bitmap = BitmapFactory.decodeFile(path);
            return resizePhoto(bitmap,500,true);//Aqui se agrego la funcion resizePhoto
        } catch (OutOfMemoryError e) {
            try {
                options = new BitmapFactory.Options();
                for (options.inSampleSize = 1;options.inSampleSize<=32; options.inSampleSize++){
                    try{
                        bitmap = BitmapFactory.decodeFile(path, options);
                        break;
                    }catch (OutOfMemoryError oom){
                        bitmap = null;
                    }
                }
            } catch(Exception ex) {
                return null;
            }
        }
        Toast.makeText(this, bitmap.getWidth(), Toast.LENGTH_SHORT).show();


        return resizePhoto(bitmap,500,true);

    }

    /**
     * This method resize the photo
     *
     * @param realImage    the bitmap of image
     * @param maxImageSize the max image size percentage
     * @param filter       the filter
     * @return a bitmap of the photo rezise
     */
    /*public static Bitmap resizePhoto(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }





    private class ConvertStringImage extends AsyncTask<Bitmap, Void, Void>{

        @Override
        protected Void doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos); //bm is the bitmap object the 10 is de quality 100 is the maximus
            byte[] b = baos.toByteArray();
            String aux = Base64.encodeToString(b, Base64.DEFAULT);
            imagen64 = aux;
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            btnEnviar.setEnabled(true);
            btnEnviar.setText("Enviar");
            progressBar.setVisibility(View.GONE);

        }

    }



    /********************************* INICIO DE FUNCIONES PARA EL CONTROL DE LATITUD Y LONGITUD Y OBTENER DIRECCION************************/
    /*public void alertaGPS(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, la aplicación requiere su activación para obtener la dirección exacta, desea activarlo? ")
                .setCancelable(false)
                .setPositiveButton("Si",new DialogInterface.OnClickListener(){
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,@SuppressWarnings("unused")final int id){
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));//Este Intent abre la ventana para encender el GPS
                        onStart(); // Agregue esta linea para probar si el GPS entra en funcionamiento inmediatamente despues de encenderlo es necesario realizar mas pruebas
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener(){
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id){
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();

    }

    //Se usa para parar el proceso del servicio google para localizacion
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
    }





    private void updateUI(Location loc) {
        if (loc != null) {
            Latitud = String.valueOf(loc.getLatitude());
            Longitud =  String.valueOf(loc.getLongitude());


        } else {
            Latitud = "18.459892";
            Longitud = "-69.95942";

        }
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);

        }else{
            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            updateUI(lastLocation);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Toast.makeText(getApplicationContext(),"Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show();



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Toast.makeText(this, "Error grave al conectar con Google Play Services", Toast.LENGTH_SHORT).show();

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido
                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Log.e(LOGTAG, "Permiso denegado");
            }
        }

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permisos Aceptados", Toast.LENGTH_SHORT).show();
                imgFoto.setClickable(true);
            }else{
                showExplanation();
            }
        }


    }

    private void showExplanation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(principal.this);
        builder.setTitle("Permisos Denegados");
        builder.setMessage("Para usar la aplicación necesita aceptar los permisos");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
          @Override
            public void onClick(DialogInterface dialog, int which){

             Intent intent =  new Intent();
              intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
              Uri uri = Uri.fromParts("package", getPackageName(), null);
              intent.setData(uri);
              startActivity(intent);
          }

        });
       builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
           @Override
           public void onClick(DialogInterface dialog, int which){
              dialog.dismiss();
               finish();
           }

       });

        builder.show();
    }


    /**************************************************Accedemos a la API de GOOGLE para obtener direccion *********************************/


    /*private void updateDireccion(String cadena){ //Actualizamos la Direccion

        if(cadena != null && !TextUtils.isEmpty(cadena)) {
            editDireccion.setText(cadena);
            Direccion = cadena;

        }else{
            editDireccion.setText("Desconocida");
            Toast.makeText(this, "Debe colocar la dirección manualmente", Toast.LENGTH_LONG).show();
            Direccion = "Desconocida";
        }


    }


    public void getDireccion(String url) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject baseJsonResponse = new JSONObject(response);
                            JSONArray featureArray = baseJsonResponse.getJSONArray("results");

                            // If there are results in the features array
                            if (featureArray.length() > 0) {
                                // Extract out the first feature (which is an earthquake)
                                JSONObject firstFeature = featureArray.getJSONObject(0);
                                formatted_address = firstFeature.getString("formatted_address");
                            }

                            updateDireccion(formatted_address);
                        }



                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(principal.this, "Error voley: "+ error, Toast.LENGTH_SHORT).show();

                    }
                }
        );

        requestQueue.add(stringRequest);



    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



    /**********************************ENVIO DE INFORMACION FINAL (CLASE CLIENTE) A WEB SERVICE *****************************************/


   /* private void envioEvento() {




                try {
                    sendingData();
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_DATA_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    if(response != null){

                                        dataRetrived();
                                        try{
                                            ArrayList<Eventos> eventos = new ArrayList<Eventos>();
                                            JSONObject j = new JSONObject(response);
                                            JSONArray arrayJson = j.getJSONArray("posts");
                                            JSONObject obj = arrayJson.getJSONObject(0);
                                            if (obj.getString("id").equals("Duplicate")){
                                                Toast.makeText(principal.this, "Este Evento Ya Ha Sido Reportado", Toast.LENGTH_SHORT).show();

                                            }
                                            else
                                            {
                                                Toast.makeText(principal.this, "Evento Registrado Exitosamente", Toast.LENGTH_SHORT).show();

                                            }
                                        }catch(Exception e){

                                        }

                                    }


                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dataRetrived();
                                    // Toast.makeText(principal.this, error.toString(), Toast.LENGTH_LONG).show();
                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                        Toast.makeText(principal.this, "Tiempo para conexión finalizado, revise su conexión a internet",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof AuthFailureError) {
                                        Toast.makeText(principal.this, "Usuario o Contraseña Incorrecta, Revise nuevamente su información",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof ServerError) {
                                        Toast.makeText(principal.this, "Error en el servidor, Contacte con el suplidor de su aplicación",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof NetworkError) {
                                        Toast.makeText(principal.this, "Error de conexión. Revise el estado de su conexión a internet",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof ParseError) {
                                        Toast.makeText(principal.this, "Problemas al ejecutar la aplicación, Contacte con el suplidor de su aplicación",Toast.LENGTH_LONG).show();
                                    }

                                }
                            }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("categoria", evento.getCategoria());
                            map.put("detalle", evento.getDetalle());
                            map.put("latitud",evento.getLatitud());
                            map.put("longitud",evento.getLongitud());
                            map.put("direccion",evento.getDireccion());
                            map.put("foto",evento.getFoto());
                            map.put("horaevento",evento.getHoraevento());
                            map.put("estatus",evento.getEstatus());
                            map.put("horaeventogmt",evento.getHoraeventogmt());


                            return map;
                        }
                    };


                    requestQueue.add(stringRequest);

                } catch (Exception e) {
                    dataRetrived();
                    AlertDialog.Builder builderDos = new AlertDialog.Builder(principal.this);
                    builderDos.setMessage("No se ha logrado enviar el evento, revise su conexión a internet")
                            .setNegativeButton("Intente de nuevo", null)
                            .create()
                            .show();
                }

    }*/

}



