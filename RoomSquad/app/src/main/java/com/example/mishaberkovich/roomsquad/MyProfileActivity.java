package com.example.mishaberkovich.roomsquad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.firebase.client.Firebase;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Firebase.setAndroidContext(this);

        Button GoToMyPostings = (Button) findViewById(R.id.my_profile_to_my_postings_button);
        GoToMyPostings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_my_postings = new Intent(MyProfileActivity.this, MyPostingsActivity.class);
                MyProfileActivity.this.startActivity(go_to_my_postings);
            }
        });

        ImageButton GoToEditProfile = (ImageButton) findViewById(R.id.my_profile_to_edit_profile_button);
        GoToEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_edit_profile = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                MyProfileActivity.this.startActivity(go_to_edit_profile);
            }
        });
    }
}
