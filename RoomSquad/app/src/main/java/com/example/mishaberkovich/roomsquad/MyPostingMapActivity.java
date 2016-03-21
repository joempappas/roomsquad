package com.example.mishaberkovich.roomsquad;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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

public class MyPostingMapActivity extends FragmentActivity implements OnMapReadyCallback{

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
        String curr_location="";
        location_array.remove(location_loc);
        location_array.add(location_loc, curr_location);


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

        if(location_array.get(location_loc)!=null && location_array.get(location_loc).length()>0){
            String location_str = location_array.get(location_loc);
            double latitude = Double.parseDouble(location_str.substring(0, location_str.indexOf(",")));
            double longitude= Double.parseDouble(location_str.substring(location_str.indexOf(",")+1, location_str.length()));

            LatLng current_location = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(current_location).title("Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
        }


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




}
