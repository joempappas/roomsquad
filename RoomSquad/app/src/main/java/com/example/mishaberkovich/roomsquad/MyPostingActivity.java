package com.example.mishaberkovich.roomsquad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class MyPostingActivity extends AppCompatActivity {

    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase current_user = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid());
    Firebase Postings = roomsquad_firebase.child("postings");
    final static ArrayList<String> posting_information = new ArrayList<String>();
    final static int POSTING_INFO_SIZE = 13;
    final static int uid_loc = 0;//unique identifier for posting, not displayed to user
    final static int user_loc = 1;//the user who created posting
    final static int type_loc = 2;//the type (roommate, sublet, tenant)
    final static int date_loc = 3;//the date it was created
    final static int name_loc = 4;//used to track the location of the profile name
    final static int description_loc = 5;//used to track the location of the profile name
    final static int location_loc = 6;//the location in the array of the location of the posting
    final static int minprice_loc = 7;
    final static int maxprice_loc = 8;
    final static int single_loc = 9;
    final static int double_loc = 10;
    final static int more_loc = 11;
    final static int photo_loc = 12;


    final static ArrayList<String> posting_photos = new ArrayList<>();

    ChildEventListener myPostingChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posting);
        Firebase.setAndroidContext(this);

        //initializes arraylist
        for (int i=0; i<POSTING_INFO_SIZE; i++){
            posting_information.add("");
        }

        String posting_id = getIntent().getExtras().getString("posting_id");//get the posting id to show
        posting_information.remove(uid_loc);
        posting_information.add(uid_loc, posting_id);
        posting_information.remove(photo_loc);
        posting_information.add(photo_loc, "0");


        final TextView top_bar_text = (TextView) findViewById(R.id.my_posting_top_bar_text);
        final TextView posting_name_text = (TextView) findViewById(R.id.my_posting_name);
        final TextView posting_description_text = (TextView) findViewById(R.id.about_my_posting);//edit text for posting name
        final TextView posting_type_text = (TextView) findViewById(R.id.posting_type_text);
        final TextView posting_date_text = (TextView) findViewById(R.id.posting_date_text);
        final TextView posting_location_text = (TextView) findViewById(R.id.posting_location_text);
        final TextView minprice_range_text = (TextView) findViewById(R.id.minprice_range_text);
        final TextView maxprice_range_text = (TextView) findViewById(R.id.maxprice_range_text);
        final TextView room_amount_text = (TextView) findViewById(R.id.room_amount_text);

        top_bar_text.setVisibility(View.INVISIBLE);
        posting_name_text.setVisibility(View.INVISIBLE);
        posting_description_text.setVisibility(View.INVISIBLE);
        posting_type_text.setVisibility(View.INVISIBLE);
        posting_date_text.setVisibility(View.INVISIBLE);
        posting_location_text.setVisibility(View.INVISIBLE);
        minprice_range_text.setVisibility(View.INVISIBLE);
        maxprice_range_text.setVisibility(View.INVISIBLE);
        room_amount_text.setVisibility(View.INVISIBLE);

        //hide the photo scroll initially, will add later if there are photos to show
        final LinearLayout photo_scroll = (LinearLayout) findViewById(R.id.photo_scroll_container);
        photo_scroll.setVisibility(View.GONE);


        myPostingChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("user")) {
                    String posting_user  = dataSnapshot.getValue().toString();
                    posting_information.remove(user_loc);
                    posting_information.add(user_loc, posting_user);
                    if (!posting_user.equals(roomsquad_firebase.getAuth().getUid())){
                        System.out.println("Error: Not authorized to view this page");
                    }
                } else if (dataSnapshot.getKey().equals("type")) {
                    String posting_type  = dataSnapshot.getValue().toString();
                    posting_information.remove(type_loc);
                    posting_information.add(type_loc, posting_type);
                    posting_type_text.setText(posting_type);
                } else if (dataSnapshot.getKey().equals("date")) {
                    String posting_date = dataSnapshot.getValue().toString();
                    posting_information.remove(date_loc);
                    posting_information.add(date_loc, posting_date);
                    posting_date_text.setText("Last Edited: " + posting_date);
                    top_bar_text.setText(posting_date+": ");
                } else if (dataSnapshot.getKey().equals("name")) {
                    String posting_name  = dataSnapshot.getValue().toString();
                    posting_information.remove(name_loc);
                    posting_information.add(name_loc, posting_name);
                    posting_name_text.setText(posting_name);
                    top_bar_text.setText(top_bar_text.getText() + "\"" +posting_name + "\"");
                } else if (dataSnapshot.getKey().equals("description")) {
                    String posting_description = dataSnapshot.getValue().toString();
                    posting_information.remove(description_loc);
                    posting_information.add(description_loc, posting_description);
                    posting_description_text.setText(posting_description);
                } else if (dataSnapshot.getKey().equals("location")) {
                    String posting_location = dataSnapshot.getValue().toString();
                    posting_information.remove(location_loc);
                    posting_information.add(location_loc, posting_location);
                    if (posting_location.length()>0){
                        posting_location_text.setText(posting_location);
                    }
                } else if (dataSnapshot.getKey().equals("minprice")) {
                    String posting_minprice = dataSnapshot.getValue().toString();
                    posting_information.remove(minprice_loc);
                    posting_information.add(minprice_loc, posting_minprice);
                    minprice_range_text.setText("$"+posting_minprice + " -");
                } else if (dataSnapshot.getKey().equals("maxprice")) {
                    String posting_maxprice = dataSnapshot.getValue().toString();
                    posting_information.remove(maxprice_loc);
                    posting_information.add(maxprice_loc, posting_maxprice);
                    maxprice_range_text.setText("$"+posting_maxprice);
                } else if (dataSnapshot.getKey().equals("single")) {
                    String single_bool = dataSnapshot.getValue().toString();
                    posting_information.remove(single_loc);
                    posting_information.add(single_loc, single_bool);
                    if (single_bool !=null && single_bool.equals("true")){
                        room_amount_text.setText("SINGLE (yes), "+ room_amount_text.getText());
                    } else if (single_bool != null){
                        room_amount_text.setText("SINGLE (no), "+ room_amount_text.getText());
                    }
                } else if (dataSnapshot.getKey().equals("double")) {
                    String double_bool = dataSnapshot.getValue().toString();
                    posting_information.remove(double_loc);
                    posting_information.add(double_loc, double_bool);
                    if (double_bool !=null && double_bool.equals("true")){
                        room_amount_text.setText("DOUBLE (yes), ");
                    } else if (double_bool != null){
                        room_amount_text.setText("DOUBLE (no), ");
                    }
                } else if (dataSnapshot.getKey().equals("more")) {
                    String more_bool = dataSnapshot.getValue().toString();
                    posting_information.remove(more_loc);
                    posting_information.add(more_loc, more_bool);
                    if (more_bool !=null && more_bool.equals("true")){
                        room_amount_text.setText(room_amount_text.getText() + "3+ (yes)");
                    } else if (more_bool != null){
                        room_amount_text.setText(room_amount_text.getText() + "3+ (no)");
                    }
                } else if (dataSnapshot.getKey().equals("photos")) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if (!d.getValue().toString().equals("deleted")){
                            String photo_file = (String) d.child("photo file").getValue();
                            showPhotoInViewAndSaveToArray(photo_file);
                        }
                    }
                }
                top_bar_text.setVisibility(View.VISIBLE);
                posting_name_text.setVisibility(View.VISIBLE);
                posting_description_text.setVisibility(View.VISIBLE);
                posting_type_text.setVisibility(View.VISIBLE);
                posting_date_text.setVisibility(View.VISIBLE);
                posting_location_text.setVisibility(View.VISIBLE);
                minprice_range_text.setVisibility(View.VISIBLE);
                maxprice_range_text.setVisibility(View.VISIBLE);
                room_amount_text.setVisibility(View.VISIBLE);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        //add listener object to listen for the "posting" children
        Postings.child(posting_id).addChildEventListener(myPostingChildEventListener);

        //edit posting
        ImageButton EditPostingButton = (ImageButton) findViewById(R.id.edit_posting_button);
        EditPostingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_edit_posting = new Intent(MyPostingActivity.this, EditMyPostingActivity.class);
                go_to_edit_posting.putExtra("posting_id", posting_information.get(uid_loc));
                MyPostingActivity.this.startActivity(go_to_edit_posting);
            }
        });




        //Go back to my postings
        Button GoToMyPostingsButton = (Button) findViewById(R.id.my_posting_to_my_postings_button);
        GoToMyPostingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_my_postings = new Intent(MyPostingActivity.this, MyPostingsActivity.class);
                MyPostingActivity.this.startActivity(go_to_my_postings);

            }
        });


    }
    @Override
    public void onStop(){
        super.onStop();
        String posting_id = getIntent().getExtras().getString("posting_id");
        Postings.child(posting_id).removeEventListener(myPostingChildEventListener);
        for(int i=0; i <POSTING_INFO_SIZE; i++){
            posting_information.remove(0);
        }
    }

    //show photos
    public void showPhotoInViewAndSaveToArray(final String photo_file){
        LinearLayout photo_scroll_container = (LinearLayout) findViewById(R.id.photo_scroll_container);
        LinearLayout photo_scroll = (LinearLayout) findViewById(R.id.photos_in_scroll);
        final ImageView next_photo = new ImageView(MyPostingActivity.this);
        byte[] image_in_bytes = Base64.decode(photo_file, Base64.DEFAULT);
        final Bitmap pic_bm = BitmapFactory.decodeByteArray(image_in_bytes, 0, image_in_bytes.length);
        next_photo.setImageBitmap(pic_bm);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);//set the params
        next_photo.setLayoutParams(params);
        next_photo.setAdjustViewBounds(true);
        photo_scroll.addView(next_photo);
        next_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout wholescreen = (LinearLayout) findViewById(R.id.content_scroll_container);
                final RelativeLayout photoscreen = (RelativeLayout) findViewById(R.id.fullscreen_photo);
                final ImageView the_photo = new ImageView(MyPostingActivity.this);
                the_photo.setImageBitmap(pic_bm);
                LinearLayout.LayoutParams fs_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);//set the params
                the_photo.setLayoutParams(fs_params);
                the_photo.setAdjustViewBounds(true);
                wholescreen.setVisibility(View.GONE);
                photoscreen.addView(the_photo);
                photoscreen.setVisibility(View.VISIBLE);
                the_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoscreen.setVisibility(View.GONE);
                        wholescreen.setVisibility(View.VISIBLE);
                        photoscreen.removeAllViews();
                    }
                });



            }
        });
        final int photo_amount = Integer.parseInt(posting_information.get(photo_loc));
        final int photo_amount_new = photo_amount + 1;
        posting_information.remove(photo_loc);
        posting_information.add(photo_loc, String.valueOf(photo_amount_new));
        posting_photos.add(photo_file);
        photo_scroll_container.setVisibility(View.VISIBLE);

    }

}
