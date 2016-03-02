package com.example.mishaberkovich.roomsquad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MyProfileActivity extends AppCompatActivity {


    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase current_user = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid().toString());
    final static ArrayList<String> profile_information = new ArrayList<String>();
    final static int name_loc = 0;//used to track the location of the profile name
    final static int tagline_loc = 1;//used to track location of profile tagline
    final static int birthdate_loc = 2;//used to track the location of the birthdate in the profile_information arraylist
    final static int gender_loc = 3;//used to track the gender

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Firebase.setAndroidContext(this);



        //initialize the arraylist
        for (int i=0; i<10; i++){
            profile_information.add("");
        }

        final TextView profile_name_view_text = (TextView) findViewById(R.id.user_profile_name);//edit text for profile name
        final TextView profile_tagline_view_text = (TextView) findViewById(R.id.user_profile_tagline);//edit text for tagline



        //listens for changes of value to display them to UI
        current_user.child("profile").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if (dataSnapshot.getChildren()!=null) {//if profile is initialized
                    String name = (String) dataSnapshot.child("name").getValue();
                    String tagline = (String) dataSnapshot.child("tagline").getValue();
                    String birthdate = (String) dataSnapshot.child("birthdate").getValue();
                    String gender = (String) dataSnapshot.child("gender").getValue();

                    profile_information.remove(name_loc);
                    profile_information.add(name_loc, name);
                    if (profile_information.get(name_loc)!=null){
                        profile_name_view_text.setText(profile_information.get(name_loc));
                    }
                    profile_information.remove(tagline_loc);
                    profile_information.add(tagline_loc, tagline);
                    if (profile_information.get(tagline_loc)!=null){
                        profile_tagline_view_text.setText(profile_information.get(tagline_loc));
                    }
                    profile_information.remove(birthdate_loc);
                    profile_information.add(birthdate_loc, birthdate);
                    //get age to display
                    if (profile_information.get(birthdate_loc)!=null){
                        displayAge(MyProfileActivity.this);
                    }
                    profile_information.remove(gender_loc);
                    profile_information.add(gender_loc, gender);
                    if (profile_information.get(gender_loc)!=null){
                        displayGender();
                    }
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


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

        Button GoToMainMenuButton = (Button) findViewById(R.id.my_profile_to_main_menu_button);
        GoToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent go_to_main_menu = new Intent(MyProfileActivity.this, MainMenuActivity.class);
                    MyProfileActivity.this.startActivity(go_to_main_menu);
            }
        });
    }

    @Override
    protected void onStart(){
        System.out.println("onStart method for MyProfileActivity being called");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        System.out.println("onRestart method for MyProfileActivity being called");
        super.onRestart();
    }

    @Override
    protected void onPause(){
        System.out.println("onPause method for MyProfileActivity being called");
        super.onPause();
    }

    @Override
    protected void onResume(){
        System.out.println("onResume method for MyProfileActivity being called");
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        System.out.println("onStop method for MyProfileActivity being called");
        super.onStop();

    }

    @Override
    protected void onDestroy(){
        System.out.println("onDestroy method for MyProfileActivity being called");
        super.onDestroy();
    }

    //this method gets the age
    public static void displayAge(Activity activity){

        String birthdate = profile_information.get(birthdate_loc);
        //gets the day month and year into integers
        int day = Integer.parseInt(profile_information.get(birthdate_loc).substring(0, profile_information.get(birthdate_loc).indexOf('/')));
        int month = Integer.parseInt(profile_information.get(birthdate_loc).substring(profile_information.get(birthdate_loc).indexOf('/') + 1, profile_information.get(birthdate_loc).lastIndexOf('/')))-1;
        //due to way it is encoded with january starting at 0, need to subtract 1
        int year = Integer.parseInt(profile_information.get(birthdate_loc).substring(profile_information.get(birthdate_loc).lastIndexOf('/') + 1, profile_information.get(birthdate_loc).length()));

        //adds the age based on the birthdate to show up on the UI
        final Calendar c = Calendar.getInstance();
        int current_year = c.get(Calendar.YEAR);
        int current_month = c.get(Calendar.MONTH);
        int current_day = c.get(Calendar.DAY_OF_MONTH);
        int age;
        if (current_month >= month-1){
            if (current_day >= day) {
                age = current_year - year;
            }else{
                age = current_year - year - 1;
            }
        } else {
            age = current_year - year - 1;
        }
        TextView profile_info = (TextView) activity.findViewById(R.id.my_profile_info);
        String profile_info_text="";
        for (int i=0; i < profile_info.getText().length(); i++){
            profile_info_text = profile_info_text + profile_info.getText().charAt(i);
        }
        int profile_info_age = profile_info_text.indexOf("Age:") + 4;
        int profile_info_gender = profile_info_text.indexOf("Gender:");
        profile_info_text = profile_info_text.substring(0,profile_info_age) +" "+ age +"\n\n"+ profile_info_text.substring(profile_info_gender, profile_info_text.length());
        profile_info.setText(profile_info_text);
    }

    public void displayGender(){
        TextView profile_info = (TextView) findViewById(R.id.my_profile_info);
        String profile_info_text="";
        for (int i=0; i < profile_info.getText().length(); i++){
            profile_info_text = profile_info_text + profile_info.getText().charAt(i);
        }
        int profile_info_gender = profile_info_text.indexOf("Gender:") + 7;
        int profile_info_smoking = profile_info_text.indexOf("Smoking:");
        profile_info_text = profile_info_text.substring(0,profile_info_gender) +" "+ profile_information.get(gender_loc) +"\n\n"+ profile_info_text.substring(profile_info_smoking, profile_info_text.length());
        profile_info.setText(profile_info_text);
    }


}
