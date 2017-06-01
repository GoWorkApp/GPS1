package com.example.monitor.gps1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference myRef;
    private List<Coordenada> listaUsuarios=new ArrayList<>();
    private DatabaseReference usuario;
    private List<Marker> marcadores=new ArrayList<>();
    private Usuario user = new Usuario();
    private Coordenada x;
    private double lat,lng;
    private String tag="a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        usuario = myRef.push();
        miUbicacion();

    }

    private void miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locListener);
    }



    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng=location.getLongitude();
            x = new Coordenada(lat,lng);
            agregarMarcador();
            usuario.setValue(x);
            try{
                Query qUsuario1 = FirebaseDatabase.getInstance().getReference().child("usuarios").orderByChild("etiqueta").equalTo(tag).limitToFirst(100);

                qUsuario1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listaUsuarios.clear();
                        for(DataSnapshot b : dataSnapshot.getChildren()){
                            Coordenada usuario1 = b.getValue(Coordenada.class);
                            listaUsuarios.add(usuario1);
                        }
                        agregarListaMarcadores();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }catch (Exception e) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaUsuarios.clear();
                        listaUsuarios=new ArrayList<>();
                        for (DataSnapshot a :dataSnapshot.getChildren()) {

                            Coordenada usuario1 = a.getValue(Coordenada.class);

                            listaUsuarios.add(usuario1);
                        }


                        agregarListaMarcadores();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG","Error");
                    }
                });
            }



        }
    }

    private void agregarMarcador() {
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 16);
        if (user.getMarcador() != null) user.getMarcador().remove();
        user.setMarcador(mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title("Mi Posición").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
        mMap.animateCamera(miUbicacion);
    }


    private void agregarListaMarcadores(){
        if (marcadores.size()!=0){
             for (int i=0; i<listaUsuarios.size();i++){
                 marcadores.get(i).remove();
             }
            marcadores.clear();
        }
        for (int i=0; i<listaUsuarios.size(); i++){
            marcadores.add(mMap.addMarker((new MarkerOptions().position(new LatLng(listaUsuarios.get(i).latitud,listaUsuarios.get(i).longitud)))));
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
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
    };

    private void filtroA(View v){

    }
    private  void filtroB(View v){

    }

}


