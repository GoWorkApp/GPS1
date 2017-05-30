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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference myRef;
    private List<Coordenada> listaUsuarios=new ArrayList<>();
    private DatabaseReference usuario;
    private List<Marker> marcadores=new ArrayList<>();
    private Usuario user = new Usuario();



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
        myRef = database.getReference("usuario");
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

            user.setMiCoord(new LatLng(location.getLatitude(),location.getLongitude()));
            agregarMarcador();
            usuario.setValue(user.getMiCoord());
            myRef.addValueEventListener(new ValueEventListener() {
                     @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                         listaUsuarios.clear();
                        listaUsuarios=new ArrayList<Coordenada>();
                       for (DataSnapshot a :dataSnapshot.getChildren()) {

                               Coordenada x = a.getValue(Coordenada.class);
                              user.setMapaHash((HashMap<String, Double>) a.getValue());
                                x.setLatitud((Double) user.getMapaHash().get("latitude"));
                                x.setLongitud((Double) user.getMapaHash().get("longitude"));
                               listaUsuarios.add(x);

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

    private void agregarMarcador() {
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(user.getMiCoord(), 16);
        if (user.getMarcador() != null) user.getMarcador().remove();
        user.setMarcador(mMap.addMarker(new MarkerOptions().position(user.getMiCoord()).title("Mi Posición").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
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
            marcadores.add(mMap.addMarker((new MarkerOptions().position(new LatLng(listaUsuarios.get(i).getLatitud(),listaUsuarios.get(i).getLongitud())))));
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

    private void disponible(View v){
        mMap.addMarker(new MarkerOptions().position(user.getMiCoord()).title("Mi Posición").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }
    private  void ocupado(View v){
        mMap.addMarker(new MarkerOptions().position(user.getMiCoord()).title("Mi Posición").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

}


