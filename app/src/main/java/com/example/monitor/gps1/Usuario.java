package com.example.monitor.gps1;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


import java.util.HashMap;


/**
 * Created by monitor on 29/05/2017.
 */

public class Usuario {
    private String etiqueta="fds";
    private Marker marcador;
    private LatLng miCoord;
    private HashMap<String,Double> mapaHash;





    public Usuario() {
    }




    public HashMap getMapaHash() {
        return mapaHash;
    }


    public void setMapaHash(HashMap<String, Double> mapaHash) {
        this.mapaHash = mapaHash;
    }

    public Usuario(LatLng miCoord, Marker marcador) {
        this.miCoord = miCoord;
        this.marcador = marcador;
    }

    public LatLng getMiCoord() {
        return miCoord;
    }

    public void setMiCoord(LatLng miCoord) {
        this.miCoord = miCoord;
    }

    public Marker getMarcador() {
        return marcador;
    }

    public void setMarcador(Marker marcador) {
        this.marcador = marcador;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}

