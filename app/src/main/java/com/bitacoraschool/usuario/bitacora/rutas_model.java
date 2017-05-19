package com.bitacoraschool.usuario.bitacora;

/**
 * Created by Octagono on 11/04/2017.
 */

public class rutas_model {

    private int id;
    private String cuentaId;
    private String localidad;
    private String choferId;
    private String descripcion;
    private String tanda;
    private String localidadNombre;
    private int rutaType;

    public int getRutaType() {
        return rutaType;
    }

    public void setRutaType(int rutaType) {
        this.rutaType = rutaType;
    }

    public String getLocalidadNombre() {
        return localidadNombre;
    }

    public void setLocalidadNombre(String localidadNombre) {
        this.localidadNombre = localidadNombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(String cuentaId) {
        this.cuentaId = cuentaId;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getChoferId() {
        return choferId;
    }

    public void setChoferId(String choferId) {
        this.choferId = choferId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTanda() {
        return tanda;
    }

    public void setTanda(String tanda) {
        this.tanda = tanda;
    }
    public rutas_model(){}

    public rutas_model(int id, String cuentaId, String localidad, String choferId, String descripcion, String tanda) {
        this.id = id;
        this.cuentaId = cuentaId;
        this.localidad = localidad;
        this.choferId = choferId;
        this.descripcion = descripcion;
        this.tanda = tanda;
    }
}
