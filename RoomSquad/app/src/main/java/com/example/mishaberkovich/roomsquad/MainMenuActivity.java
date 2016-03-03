package com.example.mishaberkovich.roomsquad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

public class MainMenuActivity extends AppCompatActivity {



    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Firebase.setAndroidContext(this);


        Button MyProfile = (Button) findViewById(R.id.menu_to_profile_button);
        MyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMyProfile = new Intent(MainMenuActivity.this, MyProfileActivity.class);
                MainMenuActivity.this.startActivity(gotoMyProfile);
            }
        });

        Button MyPreferences = (Button) findViewById(R.id.menu_to_preferences_button);
        MyPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMyPreferences = new Intent(MainMenuActivity.this, MyPreferencesActivity.class);
                MainMenuActivity.this.startActivity(gotoMyPreferences);
            }
        });

        Button MyPostings = (Button) findViewById(R.id.menu_to_postings_button);
        MyPostings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMyPostings = new Intent(MainMenuActivity.this, MyPostingsActivity.class);
                MainMenuActivity.this.startActivity(gotoMyPostings);
            }
        });

        Button MyMatches = (Button) findViewById(R.id.menu_to_matches_button);
        MyMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMyMatches = new Intent(MainMenuActivity.this, MyMatchesActivity.class);
                MainMenuActivity.this.startActivity(gotoMyMatches);
            }
        });

        Button MySuggestions = (Button) findViewById(R.id.menu_to_suggestions_button);
        MySuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMySuggestions = new Intent(MainMenuActivity.this, MySuggestionsActivity.class);
                MainMenuActivity.this.startActivity(gotoMySuggestions);
            }
        });

        Button Logout = (Button) findViewById(R.id.logout_button);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                roomsquad_firebase.unauth();
                Intent logout = new Intent(MainMenuActivity.this, LoginActivity.class);
                MainMenuActivity.this.startActivity(logout);
            }
        });

    }

    @Override
    protected void onStart(){
        System.out.println("onStart method for MainMenuActivity being called");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        System.out.println("onRestart method for MainMenuActivity being called");
        super.onRestart();
    }

    @Override
    protected void onPause(){
        System.out.println("onPause method for MainMenuActivity being called");
        super.onPause();
    }

    @Override
    protected void onResume(){
        System.out.println("onResume method for MainMenuActivity being called");
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        System.out.println("onStop method for MainMenuActivity being called");
        super.onStop();

    }

    @Override
    protected void onDestroy(){
        System.out.println("onDestroy method for MainMenuActivity being called");
        super.onDestroy();
    }
}


