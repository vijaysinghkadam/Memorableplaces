package com.example.vijay.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class memorablePlace extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,5,locationListener);
                Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location!=null){
                    zoomOnMapLocation(location,"Your location");
                }
            }
        }
    }

    public void zoomOnMapLocation(Location location, String title){
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng,15);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_place);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent=getIntent();
        if (intent.getIntExtra("placeValue",0)==0){
            //zoom in on users location


        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                    zoomOnMapLocation(location,"Your location");
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,5,locationListener);
            Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location!=null){
                zoomOnMapLocation(location,"Your location");
            }
        }}
        else {
            Location location=new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(MainActivity.locations.get(intent.getIntExtra("placeValue",0)-1).latitude);
            location.setLongitude(MainActivity.locations.get(intent.getIntExtra("placeValue",0)-1).longitude);
            zoomOnMapLocation(location,MainActivity.memorablePlaces.get(intent.getIntExtra("placeValue",0)));

        }
}

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent intent=getIntent();
        if (intent.getIntExtra("placeValue",0)==0){
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        String address="";
        try {
            List<Address> memorablePlaces=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (memorablePlaces!=null && memorablePlaces.size()>0){
                if (memorablePlaces.get(0).getThoroughfare()!=null){
                    address+=memorablePlaces.get(0).getThoroughfare();
                    if (memorablePlaces.get(0).getLocality()!=null){
                        address+=memorablePlaces.get(0).getLocality();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address==""){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address=simpleDateFormat.format(new Date());
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        MainActivity.memorablePlaces.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();
    }}
}
