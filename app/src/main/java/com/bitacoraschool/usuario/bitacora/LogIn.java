package com.bitacoraschool.usuario.bitacora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity{
    public static final String LOGIN_URL = "http://162.248.55.25/API/api/v1/operador/login";
    public static final String KEY_CORREO = "correo";
    public static final String KEY_PASSWORD = "clave";
    private EditText editTextCorreo;
    private EditText editTextPassword;
    private Button buttonLogin;
    private String correo;
    private String clave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        editTextCorreo = (EditText) findViewById(R.id.editTextCorreo);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                correo = editTextCorreo.getText().toString().trim();
                clave = editTextPassword.getText().toString().trim();

                if(TextUtils.isEmpty(correo)||TextUtils.isEmpty(clave)){

                    Toast.makeText(LogIn.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }else {

                    userLogin();
                }
            }
        });
    }
    private void userLogin() {
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    String id = "";
                                    String rol = "";
                                    try {
                                        JSONObject j = new JSONObject(response);
                                        JSONArray arrayJson = j.getJSONArray("posts");
                                        JSONObject reader = arrayJson.getJSONObject(0);
                                        id = arrayJson.getJSONObject(0).getString("id");
                                        rol = arrayJson.getJSONObject(0).getString("rolID");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (!id.equals("not found")) {
                                        Log.v("Response:", response);
                                        SharedPreferences sharedpreferences = getSharedPreferences("CustomPreferences", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("rolID", rol);
                                        editor.putString("userID", id);
                                        editor.commit();
                                        getPrincipal();
                                    } else {
                                        Toast.makeText(LogIn.this, "Error en Usuario o Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                        Toast.makeText(LogIn.this, "Tiempo para conexión finalizado, revise su conexión a internet",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof AuthFailureError) {
                                        Toast.makeText(LogIn.this, "Usuario o Contraseña Incorrecta, Revise nuevamente su información",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof ServerError) {
                                        Toast.makeText(LogIn.this, "Error en el servidor, Contacte con el suplidor de su aplicación",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof NetworkError) {
                                        Toast.makeText(LogIn.this, "Error de conexión. Revise el estado de su conexión a internet",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof ParseError) {
                                        Toast.makeText(LogIn.this, "Problemas al ejecutar la aplicación, Contacte con el suplidor de su aplicación",Toast.LENGTH_LONG).show();
                                    }

                                }
                            }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put(KEY_CORREO, correo);
                            map.put(KEY_PASSWORD, clave);
                            return map;
                        }
                    };
                    requestQueue.add(stringRequest);
                } catch (Exception e) {
                    AlertDialog.Builder builderDos = new AlertDialog.Builder(LogIn.this);
                    builderDos.setMessage("Por favor, revise su conexión a internet")
                            .setNegativeButton("Intente de nuevo", null)
                            .create()
                            .show();
                }

    }
    private void getPrincipal() {
        Intent intent = new Intent(LogIn.this, principal.class);
        startActivity(intent);
        finish();
    }

}