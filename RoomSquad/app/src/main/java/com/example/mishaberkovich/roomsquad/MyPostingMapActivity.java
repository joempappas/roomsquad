package com.example.mishaberkovich.roomsquad;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyPostingMapActivity extends FragmentActivity implements OnMapReadyCallback{

    private final Context mContext = this;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private GoogleMap mMap;
    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase current_user = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid());
    Firebase Postings = roomsquad_firebase.child("postings");
    ArrayList<String> location_array = new ArrayList<>();
    int array_size = 2;
    int posting_id_loc = 0;
    int location_loc = 1;
    ValueEventListener listenForLocationChanges;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posting_map);


        //initializes arraylist
        for (int i=0; i<array_size; i++){
            location_array.add("");
        }


        String posting_id = getIntent().getExtras().getString("posting_id");//get the posting id to show
        location_array.remove(posting_id_loc);
        location_array.add(posting_id_loc, posting_id);


    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.print("onResume called for MyPostingMapActivity");

        /*
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){

            }
        };
        final MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
        System.out.println();
        System.out.println("myLocation.toString(): " + myLocation.toString());
        System.out.println();
        */

        Firebase.setAndroidContext(this);
        //add value event click listener here for firebase
        listenForLocationChanges = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String curr_location=dataSnapshot.getValue().toString();
                if (curr_location != null){
                    location_array.remove(location_loc);
                    location_array.add(location_loc, curr_location);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        Postings.child(location_array.get(posting_id_loc)).child("location").addValueEventListener(listenForLocationChanges);
        String curr_location = "";
        location_array.remove(location_loc);
        location_array.add(location_loc, curr_location);//myLocation.toString());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        Button go_back_to_posting = (Button) findViewById(R.id.my_posting_map_to_my_posting_button);
        go_back_to_posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_posting = new Intent(MyPostingMapActivity.this, EditMyPostingActivity.class);
                go_to_posting.putExtra("posting_id", location_array.get(posting_id_loc));
                MyPostingMapActivity.this.startActivity(go_to_posting);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Postings.child(location_array.get(posting_id_loc)).child("location").removeEventListener(listenForLocationChanges);
        for(int i=0; i <array_size; i++){
            location_array.remove(0);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = 40;
        double longitude = 40;
        if (location_array.get(location_loc)!=null && location_array.get(location_loc).length() > 0) {
            String location_str = location_array.get(location_loc);
            latitude = Double.parseDouble(location_str.substring(0, location_str.indexOf(",")));
            longitude = Double.parseDouble(location_str.substring(location_str.indexOf(",") + 1, location_str.length()));
        } else {
            System.out.println("IN THE FALLBACK PROVIDER BLOCK");
            Location new_location = new Location("");
            long newTime = System.currentTimeMillis();
            FallbackLocationTracker locationTracker = new FallbackLocationTracker(this);
            locationTracker.start();

            locationTracker.onUpdate(location, locationTracker.lastTime, new_location, newTime);
            locationTracker.getLocation();
            if (locationTracker.hasLocation()) {
                location = locationTracker.lastLoc;
            }
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else {
                System.out.println("LOCATION IS NULL");
            }
            try {
                System.out.println("IN THE TRY BLOCK");
                LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                } else {
                    System.out.println("LOCATION IS NULL");
                }
            } catch (SecurityException ex) {}

            System.out.println("LOCATION FROM GPS: " + latitude + ", " + longitude);
            System.out.println();
            locationTracker.stop();
        }
        LatLng current_location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(current_location).title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));


        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point).title("Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));

                location_array.remove(location_loc);
                String point_str = point.toString();
                location_array.add(location_loc, point_str.substring(point_str.indexOf("(")+1, point_str.indexOf(")")));
                System.out.println("LOCATION: " + location_array.get(location_loc));


                Map<String, Object> posting_details = new HashMap<>();
                posting_details.put("location", location_array.get(location_loc));
                Postings.child(location_array.get(posting_id_loc)).updateChildren(posting_details);

            }
        });



    }

    public interface LocationTracker {
        public interface LocationUpdateListener{
            public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime);
        }

        public void start();
        public void start(LocationUpdateListener update);

        public void stop();

        public boolean hasLocation();

        public boolean hasPossiblyStaleLocation();

        public Location getLocation();

        public Location getPossiblyStaleLocation();

    }

    public enum ProviderType{
        NETWORK,
        GPS
    }

    private class ProviderLocationTracker implements LocationListener, LocationTracker {

        // The minimum distance to change Updates in meters
        private static final long MIN_UPDATE_DISTANCE = 10;

        // The minimum time between updates in milliseconds
        private static final long MIN_UPDATE_TIME = 1000 * 60;

        private LocationManager lm;

        private String provider;

        private Location lastLocation;
        private long lastTime;

        private boolean isRunning;

        private LocationUpdateListener listener;

        public ProviderLocationTracker(Context context, ProviderType type) {
            lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            if(type == ProviderType.NETWORK){
                provider = LocationManager.NETWORK_PROVIDER;
            }
            else{
                provider = LocationManager.GPS_PROVIDER;
            }
        }

        public void start(){
            if(isRunning){
                //Already running, do nothing
                return;
            }

            //The provider is on, so start getting updates.  Update current location
            isRunning = true;
            try {lm.requestLocationUpdates(provider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);} catch (SecurityException ex) {}
            lastLocation = null;
            lastTime = 0;
            return;
        }

        public void start(LocationUpdateListener update) {
            start();
            listener = update;

        }


        public void stop(){
            if(isRunning){
                try {lm.removeUpdates(this);} catch (SecurityException ex) {}
                isRunning = false;
                listener = null;
            }
        }

        public boolean hasLocation(){
            if(lastLocation == null){
                return false;
            }
            if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
                return false; //stale
            }
            return true;
        }

        public boolean hasPossiblyStaleLocation(){
            if(lastLocation != null){
                return true;
            }
            try {return lm.getLastKnownLocation(provider)!= null;} catch (SecurityException ex){
                return true;
            }
        }

        public Location getLocation(){
            if(lastLocation == null){
                try {lastLocation = lm.getLastKnownLocation(provider);} catch(SecurityException ex) {}
            }
            if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
                return null; //stale
            }
            return lastLocation;
        }

        public Location getPossiblyStaleLocation(){
            if(lastLocation != null){
                return lastLocation;
            }
            try {return lm.getLastKnownLocation(provider);}catch (SecurityException ex) {
                return null;
            }
        }

        public void onLocationChanged(Location newLoc) {
            long now = System.currentTimeMillis();
            if(listener != null){
                listener.onUpdate(lastLocation, lastTime, newLoc, now);
            }
            lastLocation = newLoc;
            lastTime = now;
        }

        public void onProviderDisabled(String arg0) {

        }

        public void onProviderEnabled(String arg0) {

        }

        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }
    }


    public class FallbackLocationTracker  implements LocationTracker, LocationTracker.LocationUpdateListener {


        private boolean isRunning;

        private ProviderLocationTracker gps;
        private ProviderLocationTracker net;

        private LocationUpdateListener listener;

        Location lastLoc;
        long lastTime;

        public FallbackLocationTracker(Context context) {
            gps = new ProviderLocationTracker(context, ProviderType.GPS);
            net = new ProviderLocationTracker(context, ProviderType.NETWORK);
        }

        public void start(){
            if(isRunning){
                //Already running, do nothing
                return;
            }

            //Start both
            gps.start(this);
            net.start(this);
            isRunning = true;
        }

        public void start(LocationUpdateListener update) {
            start();
            listener = update;
        }


        public void stop(){
            if(isRunning){
                gps.stop();
                net.stop();
                isRunning = false;
                listener = null;
            }
        }

        public boolean hasLocation(){
            //If either has a location, use it
            return gps.hasLocation() || net.hasLocation();
        }

        public boolean hasPossiblyStaleLocation(){
            //If either has a location, use it
            return gps.hasPossiblyStaleLocation() || net.hasPossiblyStaleLocation();
        }

        public Location getLocation(){
            Location ret = gps.getLocation();
            if(ret == null){
                ret = net.getLocation();
            }
            return ret;
        }

        public Location getPossiblyStaleLocation(){
            Location ret = gps.getPossiblyStaleLocation();
            if(ret == null){
                ret = net.getPossiblyStaleLocation();
            }
            return ret;
        }

        public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
            boolean update = false;

            //We should update only if there is no last location, the provider is the same, or the provider is more accurate, or the old location is stale
            if(lastLoc == null){
                update = true;
            }
            else if(lastLoc != null && lastLoc.getProvider().equals(newLoc.getProvider())){
                update = true;
            }
            else if(newLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
                update = true;
            }
            else if (newTime - lastTime > 5 * 60 * 1000){
                update = true;
            }

            if(update){
                if(listener != null){
                    listener.onUpdate(lastLoc, lastTime, newLoc, newTime);
                }
                lastLoc = newLoc;
                lastTime = newTime;
            }

        }
    }


/*
    public static class MyLocation {
        Timer timer1;
        LocationManager lm;
        LocationResult locationResult;
        boolean gps_enabled=false;
        boolean network_enabled=false;

        public boolean getLocation(Context context, LocationResult result)
        {
            //I use LocationResult callback class to pass location value from MyLocation to user code.
            locationResult=result;
            if(lm==null)
                lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            //exceptions will be thrown if provider is not permitted.
            try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
            try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

            //don't start listeners if no provider is enabled
            if(!gps_enabled && !network_enabled)
                return false;

            if(gps_enabled)
                try{lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);}catch(SecurityException ex){}
            if(network_enabled)
                try{lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);} catch(SecurityException ex) {}
            timer1=new Timer();
            timer1.schedule(new GetLastLocation(), 20000);
            return true;
        }

        LocationListener locationListenerGps = new LocationListener() {
            public void onLocationChanged(Location location) {
                timer1.cancel();
                locationResult.gotLocation(location);
                try{lm.removeUpdates(this);}catch(SecurityException ex) {}
                try{lm.removeUpdates(locationListenerNetwork);} catch(SecurityException ex) {}
            }
            public void onProviderDisabled(String provider) {}
            public void onProviderEnabled(String provider) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };

        LocationListener locationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                timer1.cancel();
                locationResult.gotLocation(location);
                try{lm.removeUpdates(this);}catch(SecurityException ex) {}
                try{lm.removeUpdates(locationListenerGps);} catch(SecurityException ex) {}
            }
            public void onProviderDisabled(String provider) {}
            public void onProviderEnabled(String provider) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };

        class GetLastLocation extends TimerTask {
            @Override
            public void run() {
                try{lm.removeUpdates(locationListenerGps);} catch(SecurityException ex) {}
                try{lm.removeUpdates(locationListenerNetwork);} catch(SecurityException ex) {}

                Location net_loc=null, gps_loc=null;
                if(gps_enabled)
                    try{gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);} catch(SecurityException ex) {}
                if(network_enabled)
                    try{net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);} catch(SecurityException ex) {}

                //if there are both values use the latest one
                if(gps_loc!=null && net_loc!=null){
                    if(gps_loc.getTime()>net_loc.getTime())
                        locationResult.gotLocation(gps_loc);
                    else
                        locationResult.gotLocation(net_loc);
                    return;
                }

                if(gps_loc!=null){
                    locationResult.gotLocation(gps_loc);
                    return;
                }
                if(net_loc!=null){
                    locationResult.gotLocation(net_loc);
                    return;
                }
                locationResult.gotLocation(null);
            }
        }

        public abstract static class LocationResult{
            public abstract void gotLocation(Location location);
        }
    }
    */




}
