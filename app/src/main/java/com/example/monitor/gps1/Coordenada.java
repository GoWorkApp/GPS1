package com.example.monitor.gps1;

/**
 * Created by monitor on 29/05/2017.
 */

public class Coordenada {
    double latitud;
    double longitud;

    public Coordenada() {

    }

    public Coordenada(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }


    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


}
