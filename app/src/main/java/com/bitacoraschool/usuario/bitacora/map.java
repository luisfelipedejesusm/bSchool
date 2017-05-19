package com.bitacoraschool.usuario.bitacora;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class map extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    NavigationView navigationView;
    private GoogleMap mMap;
    LocationManager locationGPS;
    public  ArrayList<LatLng> listaFinal;
    AlertDialog alert;
    GoogleApiClient mGoogleApiClient;
    String Latitud;
    String Longitud;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final String LOGTAG = "android-localizacion";
    private final int MY_PERMISSIONS = 100;
    private LatLng navigationCurrentPoint;
    ProgressBar map_pg;
    Dialog settingsDialog;
    ProgressDialog dialog;
    String selectedEstudianteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        locationGPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertaGPS();
        }
        
        try {

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .enableAutoManage(this, this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

            }
        }catch (Exception e)
        {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

        getMyEvents();
        getMyPoints();

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
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Toast.makeText(this, "location changed", Toast.LENGTH_SHORT).show();
// Do clever stuff here
    }
    private void getMyEvents() {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/getInfo";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j = new JSONObject(response);
                            JSONArray arrayJson = j.getJSONArray("posts");
                            Menu menu = navigationView.getMenu();
                            for (int i = 0; i < arrayJson.length(); i++) {
                                JSONObject obj = arrayJson.getJSONObject(i);
                                menu.add(0, obj.getInt("estudianteID"), 0, obj.getString("nombre"));
                            }
                            map_pg = (ProgressBar)findViewById(R.id.map_pg);
                            map_pg.setVisibility(View.GONE);
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
                            Toast.makeText(map.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(map.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(map.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(map.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mMap.setMyLocationEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(-0.183161, -78.470376))// Sets the center of the map to location user
                .zoom(13










                )                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                settingsDialog = new Dialog(map.this);
                settingsDialog.setTitle(marker.getTitle());
                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.button_marker_layout
                        , null));
                settingsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        navigationCurrentPoint = marker.getPosition();
                    }
                });
                settingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        navigationCurrentPoint = null;
                    }
                });
                selectedEstudianteID = marker.getSnippet();
                settingsDialog.show();
                navigationCurrentPoint = marker.getPosition();
                int dividerId = settingsDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                View divider = settingsDialog.findViewById(dividerId);
                if(divider!=null) {
                    divider.setBackgroundColor(ContextCompat.getColor(map.this, R.color.colorPrimary));
                }
                int textViewId = settingsDialog.getContext().getResources().getIdentifier("android:id/title", null, null);
                TextView tv = (TextView) settingsDialog.findViewById(textViewId);
                if (tv!=null) {
                    tv.setTextColor(ContextCompat.getColor(map.this, R.color.colorPrimary));
                }


                return true;
            }
        });

    }
    public void buttonStartNavigation(View view){

        String baseUri = "http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s";

        String uri = String.format(baseUri, Latitud, Longitud, navigationCurrentPoint.latitude, navigationCurrentPoint.longitude);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
    public void buttonMarkerMensaje(View view){
        String smsNumber = "8098285215"; //without '+'
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch(Exception e) {
            Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void buttonMarkerEntregado(View view){
        settingsDialog.dismiss();
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        Bundle b = getIntent().getExtras();
        updateStatus(selectedEstudianteID,0,b.getInt("rutaID"));
    }
    public void buttonMarkerRecogido(View view){
        settingsDialog.dismiss();
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        Bundle b = getIntent().getExtras();
        updateStatus(selectedEstudianteID,1,b.getInt("rutaID"));
    }
    public void updateStatus(final String estudianteID, final int type, final int rutaID){
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/updateEstatus";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(map.this, "Realizado", Toast.LENGTH_SHORT).show();
                        getMyPoints();
                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(login.this, error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(map.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(map.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(map.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(map.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
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
                map.put("estudianteID",estudianteID);
                map.put("rutaID", String.valueOf(rutaID));
                map.put("time", timeF);
                map.put("type",String.valueOf(type));
                map.put("from","map");
                return map;
            }
        };
        requestQueue.add(stringRequest);
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
                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()){
            case R.id.mostrarTodo:
                mMap.clear();
                getMyPoints();
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();

        addMarker(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void addMarker(final int kidID) {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/getInfoKid";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j = new JSONObject(response);
                            JSONArray arrayJson = j.getJSONArray("posts");
                            JSONObject obj = arrayJson.getJSONObject(0);
                            addGoogleMarker(obj.getString("latitud"), obj.getString("longitud"), obj.getString("nombre"), obj.getString("en_escuela"));
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
                            Toast.makeText(map.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(map.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(map.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(map.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                Bundle b = getIntent().getExtras();
                map.put("rutaID", String.valueOf(b.getInt("rutaID")));
                map.put("kidID", String.valueOf(kidID));
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void addGoogleMarker(String lat, String lng, String nombre, String enEscuela) {
        mMap.clear();
        LatLng marker = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));
        if(enEscuela.equals("1")){
            mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(nombre)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.recogido)));
        }else{
            mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(nombre)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person_pin_circle_black_24dp)));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        double latitude = Float.parseFloat(Latitud);
        double longitude = Float.parseFloat(Longitud);
        LatLng position = new LatLng(latitude,longitude);
        String url = getUrl(position, marker);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }
    private String getUrl(LatLng origin, LatLng dest){

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Toast.makeText(this, "Error grave al conectar con Google Play Services", Toast.LENGTH_SHORT).show();

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
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Toast.makeText(getApplicationContext(),"Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show();

    }
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
    public void addmarkers(ArrayList<LatLng> lista){
        int x =1;
        ArrayList listUrl = new ArrayList();
        while(x<lista.size()) {
            x--;
            ArrayList<LatLng> listaParceable = new ArrayList<LatLng>();
            int y = 0;
            while (x<lista.size() && y < 9) {
                try {
                    listaParceable.add(lista.get(x));
                }catch (Exception e){
                    y=23;
                }
                x++;
                y++;
            }
            ArrayList arreglourl = new ArrayList();
            for(int z = 1; z<listaParceable.size()-1;z++){
                arreglourl.add(listaParceable.get(z));
            }
            String url = getDirectionsUrl(listaParceable.get(0), listaParceable.get(listaParceable.size()-1), arreglourl);
            listUrl.add(url);
        }



        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(listUrl);
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest, ArrayList<LatLng> markers) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String str_waypoints = "waypoints=";
        for (LatLng puntos:markers) {
            str_waypoints = str_waypoints + puntos.latitude + "," + puntos.longitude + "|";
        }
        str_waypoints = str_waypoints.substring(0, str_waypoints.length() - 1);
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + str_waypoints + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }
    private void recogido_entregado(final String type) {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/recogido_entregado";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       if(type.equals("recogido")){

                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(login.this, error.toString(), Toast.LENGTH_LONG).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(map.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(map.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(map.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(map.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                    map.put("type", type);
                    map.put("type", type);
                    return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    private class DownloadTask extends AsyncTask<ArrayList, Void, ArrayList>{

        // Downloading data in non-ui thread
        @Override
        protected ArrayList doInBackground(ArrayList... urls) {

            // For storing data from web service
            ArrayList data = new ArrayList();


            try{
                for (int x = 0; x<urls[0].size(); x++){
                    data.add(downloadUrl(urls[0].get(x).toString()));

                }

                // Fetching the data from web service
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);

            ParserTaskMulti parserTask = new ParserTaskMulti();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }
    private class ParserTaskMulti extends AsyncTask<ArrayList, Integer, ArrayList<List<List<HashMap<String, String>>>>>{

        // Parsing the data in non-ui thread
        @Override
        protected ArrayList<List<List<HashMap<String, String>>>> doInBackground(ArrayList... jsonData) {

            JSONObject jObject;
            ArrayList<List<List<HashMap<String, String>>>> routes = new ArrayList<>();

            for(int x = 0; x<jsonData[0].size();x++){
                try{
                    jObject = new JSONObject(jsonData[0].get(x).toString());
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    List<List<HashMap<String, String>>> parseado = parser.parse(jObject);
                    // Starts parsing data
                    routes.add(parseado);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(ArrayList<List<List<HashMap<String, String>>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            for (int counter1 = 0; counter1<result.size();counter1++){
                // Traversing through all the routes
                for(int i=0;i<result.get(counter1).size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(counter1).get(i);

                    // Fetching all the points in i-th route
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);
                }

                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }

        }
    }
    private void getMyPoints() {
        final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/getKidPoints";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ArrayList<rutas_model> rutas = new ArrayList<rutas_model>();
                            JSONObject j = new JSONObject(response);
                            JSONArray arrayJson = j.getJSONArray("posts");
                            ArrayList<LatLng> lista = new ArrayList<>();
                            for (int i = 0; i < arrayJson.length(); i++) {
                                JSONObject obj = arrayJson.getJSONObject(i);
                                LatLng point = new LatLng(Float.parseFloat(obj.getString("latitud")),Float.parseFloat(obj.getString("longitud")));
                                lista.add(point);
                                MarkerOptions options = new MarkerOptions();
                                options.position(point)
                                        .title(obj.getString("nombre"))
                                        .snippet(String.valueOf(obj.getInt("id")));
                                if(obj.getString("estatus").equals("1")){
                                    options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.recogido));
                                }else if (obj.getString("estatus").equals("0")){
                                    options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person_pin_circle_black_24dp));

                                }else if (obj.getString("estatus").equals("2")){
                                    options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.enescuela));

                                }

                                mMap.addMarker(options);
                            }
                            setLista(lista);
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
                            Toast.makeText(map.this, "Tiempo para conexion finalizado, revise su conexion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(map.this, "Error en el servidor, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(map.this, "Error de coneccion. Revise el estado de su coneccion a internet", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(map.this, "Problemas al ejecutar la aplicacion, Contactese con el suplidor de su aplicacion", Toast.LENGTH_LONG).show();
                        }
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
    public void setLista(ArrayList<LatLng> list){
        listaFinal = list;
        double latitude = Float.parseFloat(((Latitud == null)? "0" :  Latitud));
        double longitude = Float.parseFloat(((Latitud == null)? "0" :  Longitud));
        if (latitude == 0 && longitude == 0){

        }else {
            LatLng position = new LatLng(latitude, longitude);
            listaFinal.add(0, position);
        }
        try {
            addmarkers(listaFinal);

        }catch(Exception e){

        }
    }

}
