package com.example.ibrakarim.lookaround.ui;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.ibrakarim.lookaround.R;
import com.example.ibrakarim.lookaround.model.NearbyPlaces;
import com.example.ibrakarim.lookaround.model.Results;
import com.example.ibrakarim.lookaround.retrofit.ApiClient;
import com.example.ibrakarim.lookaround.retrofit.ApiInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private LocationRequest mLocationRequest;
    private double latitude,longtude;
    private Marker marker;

    private String placeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // setup google client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bootom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // send location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longtude = location.getLongitude();
        Log.d("yourlocation",latitude+"");

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(latitude, longtude);

        if(marker != null) marker.remove();
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position).title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        marker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }


    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.hospital_nav:
               placeType = "hospital";
               break;
            case R.id.market_nav:
                placeType = "market";
                break;
            case R.id.restaurant_nav:
                placeType = "restaurant";
                break;

            case R.id.school_nav:
                placeType = "school";
                break;
        }


        findNearbyPlaces(placeType);
        return true;
    }

    private void findNearbyPlaces(final String placeType) {
        mMap.clear();
        String url = appendApiUrl(latitude,longtude,placeType);
        Call<NearbyPlaces> call = ApiClient.getApiClient().create(ApiInterface.class).getNearbyPlaces(url);
        call.enqueue(new Callback<NearbyPlaces>() {
            @Override
            public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {
                Log.d(TAG,"inside onResponse");
                if(response.isSuccessful()){
                    Log.d(TAG,"Response is successfull");
                    Log.d(TAG,"status is "+response.body().getStatus());
                    Results[] results = response.body().getResults();
                    for(int i = 0; i < results.length; i++){
                        String placeName = results[i].getName();
                        double latitude =Double.parseDouble(results[i].getGeometry().getLocation().getLat());
                        double longtude = Double.parseDouble(results[i].getGeometry().getLocation().getLng());
                        LatLng position = new LatLng(latitude,longtude);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .title(placeName).position(position);
                        if(placeType.equals("hospital")) markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        else if(placeType.equals("market")) markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                        else if(placeType.equals("school")) markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        else if(placeType.equals("restaurant")) markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    }
                }
            }

            @Override
            public void onFailure(Call<NearbyPlaces> call, Throwable t) {
                Log.d(TAG,"error is : "+t.getLocalizedMessage());
            }
        });
    }

    private String appendApiUrl(double latitude, double longtude, String placeType) {
        StringBuilder builder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        builder.append("location="+latitude+","+longtude+"&type="+placeType+"&radius=5000&sensor=true&key="+
        getString(R.string.place_api_key));
        Log.d(TAG,"url is "+builder.toString());
        return builder.toString();
    }
}
