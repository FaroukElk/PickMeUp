package com.example.farouk.pickmeup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private RecyclerView mRecyclerView;
    private EventAdapter eAdapter;
    private ArrayList<newEvent> eventList = new ArrayList<>();
    public static GoogleMap mMap;
    public MapFragment map;
    public static List<newEvent> filteredList = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationManager locationManager;
    public FloatingActionButton createEventButton;
    public FloatingActionButton mapsizeButton;
    public Button filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        createEventButton = (FloatingActionButton) findViewById(R.id.createEventButton);
        mapsizeButton = (FloatingActionButton) findViewById(R.id.mapsizeButton);
        filterButton = (Button) findViewById(R.id.filterButton);
        eAdapter = new EventAdapter(eventList);
        if (eventList.size() == 0){
            filterButton.setVisibility(View.GONE);
        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(eAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);
        locationManager =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled()){
            showAlert();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(3600000).setFastestInterval(1800000);
        map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        adjustMapHeight();
    }

    protected void onResume(){
        super.onResume();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.i(TAG, "OnResume, Connected back!");
        }
        if (eventList.size() != 0){
            filterButton.setVisibility(View.VISIBLE);
        }
    }

    protected void onPause(){
        super.onPause();
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        adjustMapHeight();
    }

    private boolean isLocationEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location").setMessage("This app uses your location to find games near you.\nPlease enable location access to use this app").setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt){
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }

            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }


    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }
    public void createEvent(View view){ //Starts the createEvent activity
        Intent intentEvent = new Intent(this, createEvent.class);
        startActivityForResult(intentEvent, 0);
    }

    public void makeMapFull(View view) { //Makes map fragment fullscreen instead of taking up halfscreen
        ViewGroup.LayoutParams params = map.getView().getLayoutParams();
        if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            params.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics()));
            map.getView().setLayoutParams(params);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mapsizeButton.setVisibility(View.VISIBLE);
            createEventButton.setVisibility(View.VISIBLE);
            if (eventList.size() != 0){
                filterButton.setVisibility(View.VISIBLE);
            }
            adjustMapHeight();
            
        } else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            map.getView().setLayoutParams(params);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mapsizeButton.setVisibility(View.GONE);
            createEventButton.setVisibility(View.GONE);
            filterButton.setVisibility(View.GONE);
        }
    }

    public void filterResults(View view){ //Starts filter activity
        Intent filterIntent = new Intent(this, filterResults.class);
        startActivityForResult(filterIntent, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                makeMapFull(this.findViewById(android.R.id.content).getRootView());
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) { //Receives information from create event activity and adds to recyclerview
                mRecyclerView.setAdapter(eAdapter);
                resetMap();
                String sport = data.getStringExtra("sport");
                String skill = data.getStringExtra("skill");
                String location = data.getStringExtra("location");
                String date = data.getStringExtra("date");
                String hour = data.getStringExtra("hour");
                String min = data.getStringExtra("min");
                if (Integer.valueOf(min) < 10){
                    min = "0" + min;
                }
                int hr = Integer.valueOf(hour);
                String time;
                if (hr > 11 && hr != 12){
                    time = String.valueOf(hr%12) + ":" + min + " PM";
                }
                else if (hr == 12){
                    time = "12:" + min + " PM";
                }
                else{
                    if (hr == 0){
                        time = "12:" + min + " AM";
                    }
                    else{
                        time = hour + ":" + min + " AM";
                    }
                }
                double lat = Double.parseDouble(data.getStringExtra("lat"));
                double lng = Double.parseDouble(data.getStringExtra("lng"));
                LatLng latLng = new LatLng(lat, lng);
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(sport).snippet(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                markers.add(marker);
                marker.showInfoWindow();
                newEvent event = new newEvent(sport, skill, location, date, time, marker);
                eventList.add(event);
                eAdapter.notifyDataSetChanged();
                if (eventList.size() > 1) {
                    showAllMarkers(markers);
                }
                else{
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                }
            }
        }
        else { //Applies the filter information from the filter results activity
            if (resultCode == RESULT_OK) {
                initializeFilteredList();
                int i = 0;
                int filtered = 0;
                String sportFilter = data.getStringExtra("sportFilter");
                String distanceFilter = data.getStringExtra("distanceFilter");
                String skillFilter = data.getStringExtra("skillFilter");
                String dateFilter = data.getStringExtra("dateFilter");
                if (!sportFilter.equals("Any")) {
                    for (Iterator<newEvent> iterator = filteredList.iterator(); iterator.hasNext(); i++) {
                        newEvent event = iterator.next();
                        if (sportFilter.contains(event.getSport())) {
                            markers.remove(event.getMarker());
                            event.getMarker().remove();
                            iterator.remove();
                            filtered++;
                        }
                    }
                }
                if (!skillFilter.equals("Any")) {
                    for (Iterator<newEvent> iterator = filteredList.iterator(); iterator.hasNext(); ) {
                        newEvent event = iterator.next();
                        if (!event.getSkill().equals(skillFilter)) {
                            markers.remove(event.getMarker());
                            event.getMarker().remove();
                            iterator.remove();
                            filtered++;
                        }
                    }
                }
                if (!distanceFilter.equals("Any")) {
                    float distance = Float.parseFloat(distanceFilter) * (float) 1609.34;
                    Location currLocation = new Location("currLocation");
                    currLocation.setLatitude(mLastLocation.getLatitude());
                    currLocation.setLongitude(mLastLocation.getLongitude());
                    for (Iterator<newEvent> iterator = filteredList.iterator(); iterator.hasNext(); ) {
                        newEvent event = iterator.next();
                        Marker toMarker = event.getMarker();
                        Location toLocation = new Location("toLocation");
                        toLocation.setLatitude(toMarker.getPosition().latitude);
                        toLocation.setLongitude(toMarker.getPosition().longitude);
                        if (currLocation.distanceTo(toLocation) > distance) {
                            markers.remove(toMarker);
                            toMarker.remove();
                            iterator.remove();
                            filtered++;
                        }
                    }
                }
                if (!dateFilter.equals("Any")){
                    for (Iterator<newEvent> iterator = filteredList.iterator(); iterator.hasNext(); ){
                        newEvent event = iterator.next();
                        if (!event.getDate().equals(dateFilter)){
                            markers.remove(event.getMarker());
                            event.getMarker().remove();
                            iterator.remove();
                            filtered++;
                        }
                    }
                }
                if (filtered == 1){
                    Toast.makeText(this, "1 event filtered from results.", Toast.LENGTH_SHORT).show();
                }
                else if (filtered > 1){
                    Toast.makeText(this, filtered + " events filtered from results.", Toast.LENGTH_SHORT).show();
                }
                EventAdapter eAdapter2 = new EventAdapter(filteredList);
                mRecyclerView.setAdapter(eAdapter2);
                eAdapter2.notifyDataSetChanged();
                if (filteredList.size() > 1) {
                    showAllMarkers(markers);
                }
                else if (filteredList.size() == 1){
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(filteredList.get(0).getMarker().getPosition()));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }
            catch (IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        }
        else{
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null){
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else{
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                          String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void showAllMarkers(List<Marker> markers){ //Adjusts map zoom to show all event markers on map
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker: markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 20;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    public void initializeFilteredList(){ //When applying a new filter, this function resets the filtered list with all events
        filteredList.clear();
        filteredList = new ArrayList<>(eventList);
        resetMap();
    }

    public void resetMap(){ //Adds all markers that were filtered out back to map
        for (Iterator<newEvent> iterator = eventList.iterator(); iterator.hasNext(); ) {
            newEvent event = iterator.next();
            Marker currMarker = event.getMarker();
            if (!isMarkerOnMap(markers, currMarker)){
                Marker addMarker = mMap.addMarker(new MarkerOptions().position(currMarker.getPosition()).title(event.getSport()).snippet(event.getLocation()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                event.setMarker(addMarker);
                markers.add(addMarker);
            }
        }
    }
    public boolean isMarkerOnMap(List<Marker> markers, Marker checkMarker){ //Checks if marker is currently shown on map
        for (int i = 0; i < markers.size(); i++){
            Marker onMap = markers.get(i);
            if ((onMap.getPosition().latitude == checkMarker.getPosition().latitude) && (onMap.getPosition().longitude == checkMarker.getPosition().longitude) ){
                return true;
            }
        }
        return false;
    }

    public void adjustMapHeight(){
        ViewGroup.LayoutParams params = map.getView().getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            params.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
            map.getView().setLayoutParams(params);
        }
        else{
            params.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics()));
            map.getView().setLayoutParams(params);
        }
    }
}
