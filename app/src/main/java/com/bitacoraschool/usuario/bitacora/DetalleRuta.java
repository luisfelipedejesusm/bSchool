package com.bitacoraschool.usuario.bitacora;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetalleRuta extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    ArrayList<detalleRuta_model> detallerutas;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private detalle_rutas_adapter itemsAdapter;
    ListView listViewGeneral;
    private Object parent;
    int parentposition;
    Dialog settingsDialog;
    detalleRuta_model selectedOption;
    ProgressDialog dialog;
    ProgressBar loaderPG;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationManager locationGPS;
    AlertDialog alert;
    private String Latitud;
    private String Longitud;
    private static final String LOGTAG = "android-localizacion";
    private final int MY_PERMISSIONS = 100;
    FloatingActionButton reloadButton;
    private FloatingActionButton llegofloatbtn;
    Dialog mensajeDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);
        listViewGeneral = (ListView) findViewById(R.id.ruta_detalle_listview);

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

        getMyEvents();
    }

    public void alertaGPS(){

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

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void updateUI(Location loc) {
        if (loc != null) {
            Latitud = String.valueOf(loc.getLatitude());
            Longitud =  String.valueOf(loc.getLongitude());


        } else {
            Latitud = "-1";
            Longitud = "-1";

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // menu.findItem(R.id.mapa).setEnabled(true);

        return true;
    }

    @Override


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapa:
                Intent intent = new Intent(this, map.class);
                Bundle b = new Bundle();
                Bundle c = getIntent().getExtras();
                b.putInt("rutaID", c.getInt("rutaID"));
                intent.putExtras(b);
                startActivity(intent);
                return true;
            case R.id.atras:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getMyEvents() {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/getInfo";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ArrayList<detalleRuta_model> detalle_rutas = new ArrayList<detalleRuta_model>();
                            JSONObject j = new JSONObject(response);
                            JSONArray arrayJson = j.getJSONArray("posts");
                            for (int i = 0; i < arrayJson.length(); i++) {
                                detalleRuta_model rutaDetalleArray = new detalleRuta_model();
                                JSONObject obj = arrayJson.getJSONObject(i);
                                rutaDetalleArray.setId(obj.getInt("id"));
                                rutaDetalleArray.setNombreRuta(obj.getString("descripcion"));
                                rutaDetalleArray.setNombre(obj.getString("nombre"));
                                rutaDetalleArray.setRutaID(obj.getString("rutaID"));
                                rutaDetalleArray.setEstudianteID(obj.getString("estudianteID"));
                                rutaDetalleArray.setEstatus(obj.getInt("estatus"));
                                rutaDetalleArray.setTipoRuta(obj.getInt("type"));
                                detalle_rutas.add(rutaDetalleArray);
                            }
                            setArrayToList(detalle_rutas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(login.this, error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(DetalleRuta.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(DetalleRuta.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(DetalleRuta.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(DetalleRuta.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        }
                        reloadEvent();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                Bundle b = getIntent().getExtras();

                map.put("rutaID", String.valueOf(b.getInt("rutaID")));
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void reloadEvent(){
        loaderPG = (ProgressBar)findViewById(R.id.detalleRutaPG);
        loaderPG.setVisibility(View.GONE);
        reloadButton = (FloatingActionButton) findViewById(R.id.reloadButtonDetalleRuta);
        reloadButton.setVisibility(View.VISIBLE);
    }
    public void reload(View view){
        loaderPG = (ProgressBar)findViewById(R.id.detalleRutaPG);
        loaderPG.setVisibility(View.VISIBLE);
        reloadButton = (FloatingActionButton) findViewById(R.id.reloadButtonDetalleRuta);
        reloadButton.setVisibility(View.GONE);
        getMyEvents();
    }

    public void buttonMarkerRecogido(View view) {
        settingsDialog.dismiss();
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        updateStatus(selectedOption, 1, parentposition);
    }

    public void buttonMarkerEntregado(View view) {
        settingsDialog.dismiss();
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        updateStatus(selectedOption, 0, parentposition);
    }

    public void Llego(View view) {
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        updateRouteStatus();
    }

    public void buttonMarkerMensaje(View view) {

        mensajeDialog = new Dialog(DetalleRuta.this);
        mensajeDialog.setTitle("mensaje");
        mensajeDialog.setContentView(getLayoutInflater().inflate(R.layout.button_list_mensaje
                , null));
        mensajeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

            }
        });
        mensajeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        mensajeDialog.show();
        int dividerId = mensajeDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = mensajeDialog.findViewById(dividerId);
        if(divider!=null) {
            divider.setBackgroundColor(ContextCompat.getColor(DetalleRuta.this, R.color.colorPrimary));
        }
        int textViewId = mensajeDialog.getContext().getResources().getIdentifier("android:id/title", null, null);
        TextView tv = (TextView) mensajeDialog.findViewById(textViewId);
        if (tv!=null) {
            tv.setTextColor(ContextCompat.getColor(DetalleRuta.this, R.color.colorPrimary));
        }
        settingsDialog.dismiss();
    }

   public void mensajecod1 (View view){
       dialog = ProgressDialog.show(this, "",
               "Loading. Please wait...", true);
       sendMessage(selectedOption,1);
   }

    public void mensajecod2 (View view){
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        sendMessage(selectedOption,2);
    }

    public void mensajecod3 (View view){
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        sendMessage(selectedOption,3);
    }
    public void sendMessage(final detalleRuta_model model, final int type) {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/sendMessage";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DetalleRuta.this, "Mensaje Enviado Exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(login.this, error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(DetalleRuta.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(DetalleRuta.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(DetalleRuta.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(DetalleRuta.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                Calendar c = Calendar.getInstance();
                Date time = c.getTime();
                String timeF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
                map.put("estudianteID", model.getEstudianteID());
                map.put("rutaID", model.getRutaID());
                map.put("type", String.valueOf(type));
                map.put("time", timeF);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void updateRouteStatus() {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/hasArrived";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DetalleRuta.this, "Realizado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        DetalleRuta.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(login.this, error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(DetalleRuta.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(DetalleRuta.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(DetalleRuta.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(DetalleRuta.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                Calendar c = Calendar.getInstance();
                Date time = c.getTime();
                String timeF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
                Bundle b = getIntent().getExtras();
                map.put("rutaID", String.valueOf(b.getInt("rutaID")));
                map.put("time", timeF);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void updateStatus(final detalleRuta_model model, final int type, final int parentposition2) {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/updateEstatus";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DetalleRuta.this, "Realizado", Toast.LENGTH_SHORT).show();

                            getMyEvents();
                            dialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(login.this, error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(DetalleRuta.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(DetalleRuta.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(DetalleRuta.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(DetalleRuta.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                Calendar c = Calendar.getInstance();
                Date time = c.getTime();
                String timeF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
                map.put("estudianteID", model.getEstudianteID());
                map.put("rutaID", model.getRutaID());
                map.put("time", timeF);
                map.put("type", String.valueOf(type));
                map.put("latitud",Latitud);
                map.put("longitud",Longitud);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void setArrayToList(ArrayList<detalleRuta_model> e) {
        //pg = (ProgressBar)findViewById(R.id.principal_pg);
        //pg.setVisibility(View.GONE);
        detallerutas = e;
        itemsAdapter = new detalle_rutas_adapter(this, 0, detallerutas);
//        listViewGeneral.setAdapter(new ArrayAdapter<String>(principal.this,android.R.layout.simple_list_item_1,arr));
        listViewGeneral.setAdapter(itemsAdapter);
        listViewGeneral.setTextFilterEnabled(true);
        listViewGeneral.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                parent = arg0;
                selectedOption = (detalleRuta_model) arg0.getItemAtPosition(position);
                if (selectedOption.getTipoRuta()==1){
                    if (selectedOption.getEstatus() == 2){
                        Toast.makeText(DetalleRuta.this, "Esta persona ya ha llegado a su destino", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (selectedOption.getTipoRuta()==2){
                    if (selectedOption.getEstatus() == 0){
                        Toast.makeText(DetalleRuta.this, "Esta persona ya ha llegado a su destino", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                    parentposition = position;
                    detalleRuta_model detalleRuta = (detalleRuta_model) arg0.getItemAtPosition(position);
                    settingsDialog = new Dialog(DetalleRuta.this);
                    settingsDialog.setTitle(detalleRuta.getNombre());
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.button_list
                            , null));
                    settingsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {

                        }
                    });
                    settingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    settingsDialog.show();
                    int dividerId = settingsDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                    View divider = settingsDialog.findViewById(dividerId);
                    if(divider!=null) {
                        divider.setBackgroundColor(ContextCompat.getColor(DetalleRuta.this, R.color.colorPrimary));
                    }
                    int textViewId = settingsDialog.getContext().getResources().getIdentifier("android:id/title", null, null);
                    TextView tv = (TextView) settingsDialog.findViewById(textViewId);
                    if (tv!=null) {
                        tv.setTextColor(ContextCompat.getColor(DetalleRuta.this, R.color.colorPrimary));
                    }

            }
        });
        loaderPG = (ProgressBar) findViewById(R.id.detalleRutaPG);
        loaderPG.setVisibility(View.GONE);
        llegofloatbtn = (FloatingActionButton) findViewById(R.id.llegofloatbtn);
        llegofloatbtn.setVisibility(View.VISIBLE);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);

        } else {
            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            updateUI(lastLocation);
        }
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

    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){

        }
    }
}
