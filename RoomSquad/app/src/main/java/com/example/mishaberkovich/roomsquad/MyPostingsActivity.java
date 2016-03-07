package com.example.mishaberkovich.roomsquad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class MyPostingsActivity extends AppCompatActivity {

    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase current_user_postings = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid().toString()).child("postings");
    Firebase Postings = roomsquad_firebase.child("postings");
    final ArrayList<String> MyPostingsArray = new ArrayList<>();

    ChildEventListener postingsChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_postings);
        Firebase.setAndroidContext(this);

        System.out.println("Creating My Postings Activity");

        //add a new posting button
        ImageButton AddNewPostingButton = (ImageButton) findViewById(R.id.new_posting_button);
        AddNewPostingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_edit_posting = new Intent(MyPostingsActivity.this, EditMyPostingActivity.class);
                go_to_edit_posting.putExtra("posting_id", "");
                MyPostingsActivity.this.startActivity(go_to_edit_posting);
            }
        });



        final LinearLayout my_postings_scroll = (LinearLayout) findViewById(R.id.my_postings_inside_scroll);

        postingsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                //if posting belongs to current user
                //not so efficient cuz looks through all postings and not user postings
                //in future might have to change this when there are a lot of postings
                //but user only has link to posting not the data...
                if (!dataSnapshot.getValue().equals("deleted") && dataSnapshot.child("user").getValue().equals(roomsquad_firebase.getAuth().getUid())) {
                    final String posting_id = dataSnapshot.getKey();
                    String date_name = dataSnapshot.child("date").getValue().toString() + ": " + dataSnapshot.child("name").getValue().toString();
                    MyPostingsArray.add(date_name);

                    //dynamically add buttons for each posting
                    final LinearLayout BtnContainerWithSpace = new LinearLayout(MyPostingsActivity.this);
                    final LinearLayout ButtonsContainer = new LinearLayout(MyPostingsActivity.this);
                    ButtonsContainer.setOrientation(LinearLayout.HORIZONTAL);
                    Button My_Posting_Link = new Button(MyPostingsActivity.this);
                    Button Delete_Posting = new Button(MyPostingsActivity.this);
                    //set the text and colorings
                    My_Posting_Link.setText(MyPostingsArray.get(MyPostingsArray.size() - 1));
                    My_Posting_Link.setTextColor(Color.WHITE);
                    My_Posting_Link.setTextSize(15);
                    My_Posting_Link.setBackgroundResource(R.drawable.button_pressed);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(760, LayoutParams.MATCH_PARENT);//set the params
                    ButtonsContainer.addView(My_Posting_Link, params);
                    Delete_Posting.setText("Delete posting");
                    Delete_Posting.setTextColor(Color.WHITE);
                    Delete_Posting.setTextSize(10);
                    Delete_Posting.setBackgroundResource(R.drawable.button_pressed);
                    params = new LinearLayout.LayoutParams(320, LayoutParams.MATCH_PARENT);//set the params
                    //deletes the posting when button is clicked
                    ButtonsContainer.addView(Delete_Posting, params);
                    int button_height = 140;
                    if (date_name.length()<24){//otherwise the text in button will go overboard and not be seen
                        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 140);//set the params
                    } else if (date_name.length()<48){//otherwise the text in button will go overboard and not be seen
                        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 180);//set the params
                        button_height=180;
                    } else if (date_name.length()<72){//otherwise the text in button will go overboard and not be seen
                        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 220);//set the params
                        button_height=220;
                    }

                    BtnContainerWithSpace.addView(ButtonsContainer, params);
                    params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, button_height+40);//set the params
                    my_postings_scroll.addView(BtnContainerWithSpace, params);
                    //click on the posting to go to it
                    My_Posting_Link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent view_posting = new Intent(MyPostingsActivity.this, MyPostingActivity.class);
                            view_posting.putExtra("posting_id", posting_id);
                            startActivity(view_posting);
                        }
                    });
                    Delete_Posting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(MyPostingsActivity.this)
                                    .setTitle("Delete Posting")
                                    .setMessage("Are you sure you want to delete this posting?")
                                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //do nothing
                                        }
                                    })
                                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //delete these things from firebase
                                            Postings.child(posting_id).setValue("deleted");
                                            current_user_postings.child(posting_id).setValue("deleted");
                                            BtnContainerWithSpace.setVisibility(View.GONE);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();


                        }
                    });
                }
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
        Postings.addChildEventListener(postingsChildEventListener);






        //bottom bar to go back to main menu
        Button GoToMainMenuButton = (Button) findViewById(R.id.my_postings_to_main_menu_button);
        GoToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_main_menu = new Intent(MyPostingsActivity.this, MainMenuActivity.class);
                MyPostingsActivity.this.startActivity(go_to_main_menu);
                }
            });
    }

    @Override
    protected void onResume() {
        System.out.println("Resuming My Postings Activity");
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Postings.removeEventListener(postingsChildEventListener);
    }





}
