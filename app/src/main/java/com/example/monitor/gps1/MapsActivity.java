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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    private Circle circulo;
    double lat = 0.0;
    double lng =0.0;
    private DatabaseReference myRef;
    private DatabaseReference usuario;
    private HashMap<String,Coordenada> mimapa;
    private List<Coordenada> lista=new ArrayList<>();
    private List<Marker> marcadores=new ArrayList<>();


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

    private void agregarMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) marcador.remove();
        if (circulo != null) circulo.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Mi Posición"));
        CircleOptions OpCirculo = new CircleOptions().center(new LatLng(lat,lng)).radius(100);
        circulo = mMap.addCircle(OpCirculo);
        mMap.animateCamera(miUbicacion);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {

            lat = location.getLatitude();
            lng = location.getLongitude();
            final Coordenada miCoord = new Coordenada(lat,lng);
            usuario.setValue(miCoord);
            myRef.addValueEventListener(new ValueEventListener() {
                     @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         //Object misdatos=dataSnapshot.getValue();
                         for (DataSnapshot a :dataSnapshot.getChildren()){
                           Coordenada coord=a.getValue(Coordenada.class);
                             lista.add(coord);
                         }
                         for (int i=0; i<lista.size(); i++){

                             System.out.println(lista.get(i));
                         }
                         /* if( misdatos instanceof HashMap){
                           mimapa= (HashMap) misdatos;
                          }
                          Collection<Coordenada> c=mimapa.values();
                          Set<String> k=mimapa.keySet();
                         for(Coordenada x: c){
                             System.out.println(x.getLatitud());
                          }
                          //Coordenada coords =  dataSnapshot.getValue(Coordenada.class);
                          /* Collection<Coordenada> es = misdatos.values();
                          for(Coordenada e : es){

                            System.out.print(e.getLatitud());

                         }*/

              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG","Error");
              }
          });

            agregarMarcador(lat, lng);
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

    private void miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locListener);
    }


}



class Coordenada{
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
