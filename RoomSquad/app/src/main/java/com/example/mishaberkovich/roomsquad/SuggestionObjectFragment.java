package com.example.mishaberkovich.roomsquad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ChildEventListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SuggestionObjectFragment extends Fragment {

    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.

        Bundle args = getArguments();
        final String uid = args.getString("user id");



        if(!uid.equals("null")){
            final View rootView = inflater.inflate(R.layout.suggestion, container, false);
            final TextView profile_name_view_text = (TextView) rootView.findViewById(R.id.profile_name);//edit text for profile name
            final TextView profile_tagline_view_text = (TextView) rootView.findViewById(R.id.profile_tagline);//edit text for tagline
            final ImageView profile_pic = (ImageView) rootView.findViewById(R.id.profile_photo);//profile picture
            profile_name_view_text.setVisibility(View.INVISIBLE);
            profile_tagline_view_text.setVisibility(View.INVISIBLE);
            profile_pic.setVisibility(View.INVISIBLE);//don't show the default photo right away



            Firebase user_profile = roomsquad_firebase.child("users").child(uid).child("profile");


            ValueEventListener profile_event_listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildren()!=null) {//if profile is initialized
                        String name = (String) dataSnapshot.child("name").getValue();
                        String tagline = (String) dataSnapshot.child("tagline").getValue();
                        String photo = (String) dataSnapshot.child("photo").getValue();
                        String birthdate = (String) dataSnapshot.child("birthdate").getValue();
                        String gender = (String) dataSnapshot.child("gender").getValue();

                        System.out.println(dataSnapshot.getKey());
                        System.out.println(name);

                        if (name!=null){
                            profile_name_view_text.setText(name);
                            profile_name_view_text.setVisibility(View.VISIBLE);
                        }
                        if (tagline!=null){
                            profile_tagline_view_text.setText(tagline);
                            profile_tagline_view_text.setVisibility(View.VISIBLE);
                        }
                        if (photo!=null){
                            byte[] image_in_bytes = Base64.decode(photo, Base64.DEFAULT);
                            Bitmap pic_bm = BitmapFactory.decodeByteArray(image_in_bytes, 0, image_in_bytes.length);
                            profile_pic.setImageBitmap(pic_bm);
                            profile_pic.setVisibility(View.VISIBLE);
                        }
                        if(birthdate!=null){
                            displayAge(rootView, birthdate);
                        }
                        if (gender!=null){
                            displayGender(rootView, gender);
                        }


                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            user_profile.addValueEventListener(profile_event_listener);

            Button view_postings = (Button) rootView.findViewById(R.id.profile_postings_button);
            view_postings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent go_to_my_postings = new Intent(getActivity(), MyPostingsActivity.class);
                    go_to_my_postings.putExtra("user_id", uid);
                    getActivity().startActivity(go_to_my_postings);
                }
            });

            return rootView;
        } else {
            final View rootView = inflater.inflate(R.layout.suggestion, container, false);
            return rootView;
        }


    }

    public void displayAge(View view, String birthdate){
        //gets the day month and year into integers
        int day = Integer.parseInt(birthdate.substring(0,birthdate.indexOf('/')));
        int month = Integer.parseInt(birthdate.substring(birthdate.indexOf('/') + 1, birthdate.lastIndexOf('/')))-1;
        //due to way it is encoded with january starting at 0, need to subtract 1
        int year = Integer.parseInt(birthdate.substring(birthdate.lastIndexOf('/') + 1, birthdate.length()));

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
        TextView profile_info = (TextView) view.findViewById(R.id.profile_info);
        String profile_info_text="";
        for (int i=0; i < profile_info.getText().length(); i++){
            profile_info_text = profile_info_text + profile_info.getText().charAt(i);
        }
        int profile_info_age = profile_info_text.indexOf("Age:") + 4;
        int profile_info_gender = profile_info_text.indexOf("Gender:");
        profile_info_text = profile_info_text.substring(0,profile_info_age) +" "+ age +"\n\n"+ profile_info_text.substring(profile_info_gender, profile_info_text.length());
        profile_info.setText(profile_info_text);
    }

    public void displayGender(View view, String gender){
        TextView profile_info = (TextView) view.findViewById(R.id.profile_info);
        String profile_info_text="";
        for (int i=0; i < profile_info.getText().length(); i++){
            profile_info_text = profile_info_text + profile_info.getText().charAt(i);
        }
        int profile_info_gender = profile_info_text.indexOf("Gender:") + 7;
        int profile_info_smoking = profile_info_text.indexOf("Smoking:");
        profile_info_text = profile_info_text.substring(0,profile_info_gender) +" "+ gender +"\n\n"+ profile_info_text.substring(profile_info_smoking, profile_info_text.length());
        profile_info.setText(profile_info_text);
    }


}