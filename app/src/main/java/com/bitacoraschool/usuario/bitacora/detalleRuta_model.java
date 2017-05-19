package com.bitacoraschool.usuario.bitacora;

/**
 * Created by Octagono on 24/04/2017.
 */

public class detalleRuta_model {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEstudianteID() {
        return estudianteID;
    }

    public void setEstudianteID(String estudianteID) {
        this.estudianteID = estudianteID;
    }

    public String getCuentaID() {
        return cuentaID;
    }

    public void setCuentaID(String cuentaID) {
        this.cuentaID = cuentaID;
    }

    public String getLocalidadID() {
        return localidadID;
    }

    public void setLocalidadID(String localidadID) {
        this.localidadID = localidadID;
    }

    public String getRutaID() {
        return rutaID;
    }

    public void setRutaID(String rutaID) {
        this.rutaID = rutaID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNatalidad() {
        return natalidad;
    }

    public void setNatalidad(String natalidad) {
        this.natalidad = natalidad;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public detalleRuta_model(){}

    public detalleRuta_model(int id, String nombre, String rutaID) {
        this.id = id;
        this.rutaID = rutaID;
        this.nombre = nombre;
    }

    public detalleRuta_model(int id, String estudianteID, String cuentaID, String localidadID, String rutaID, String nombre, String natalidad, String latitud, String longitud) {
        this.id = id;
        this.estudianteID = estudianteID;
        this.cuentaID = cuentaID;
        this.localidadID = localidadID;
        this.rutaID = rutaID;
        this.nombre = nombre;
        this.natalidad = natalidad;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    private int id;
    private String estudianteID;
    private String cuentaID;
    private String localidadID;
    private String rutaID;
    private String nombre;
    private String natalidad;
    private String latitud;
    private String longitud;
    private String nombreRuta;
    private int tipoRuta;

    public int getTipoRuta() {
        return tipoRuta;
    }

    public void setTipoRuta(int tipoRuta) {
        this.tipoRuta = tipoRuta;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    private int estatus;

}
