package com.example.mishaberkovich.roomsquad;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.content.DialogInterface;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.View.OnLayoutChangeListener;
import android.view.WindowManager;
import android.view.LayoutInflater;
import android.widget.Button;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.IndexedNode;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {




    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase current_user = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid().toString());
    static boolean changes_saved = true;
    final static ArrayList<String> profile_information = new ArrayList<String>();
    final static int PROF_INFO_SIZE = 5;//will change
    final static int name_loc = 0;//used to track the location of the profile name
    final static int photo_loc = 1;
    final static int tagline_loc = 2;//used to track location of profile tagline
    final static int birthdate_loc = 3;//used to track the location of the birthdate in the profile_information arraylist
    final static int gender_loc = 4;//used to track the gender

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Firebase.setAndroidContext(this);
        //to hide keyboard from covering half of screen, initially, then when clicking outside of edit text
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setupUI(findViewById(R.id.edit_profile_activity));
        //initializes arraylist
        for (int i=0; i<PROF_INFO_SIZE; i++){
            profile_information.add("");
        }


        final EditText profile_name_edit_text = (EditText) findViewById(R.id.edit_profile_name);//edit text for profile name
        final EditText profile_tagline_edit_text = (EditText) findViewById(R.id.edit_profile_tagline);//edit text for tagline
        final RadioGroup genderChoice = (RadioGroup) findViewById(R.id.gender_radio_buttons);//male and female radio button
        final ImageButton profile_pic = (ImageButton) findViewById(R.id.edit_profile_photo);//profile picture


        //listens for changes of value to display them to UI
        current_user.child("profile").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildren() != null) {//if profile is initialized
                    String name = (String) dataSnapshot.child("name").getValue();
                    String tagline = (String) dataSnapshot.child("tagline").getValue();
                    String photo = (String) dataSnapshot.child("photo").getValue();
                    String birthdate = (String) dataSnapshot.child("birthdate").getValue();
                    String gender = (String) dataSnapshot.child("gender").getValue();

                    profile_information.remove(name_loc);
                    profile_information.add(name_loc, name);
                    if (profile_information.get(name_loc) != null) {
                        profile_name_edit_text.setText(profile_information.get(name_loc));
                    }
                    profile_information.remove(tagline_loc);
                    profile_information.add(tagline_loc, tagline);
                    if (profile_information.get(tagline_loc) != null) {
                        profile_tagline_edit_text.setText(profile_information.get(tagline_loc));
                    }
                    profile_information.remove(photo_loc);
                    profile_information.add(photo_loc, photo);
                    if (profile_information.get(photo_loc) != null) {
                        byte[] image_in_bytes = Base64.decode(photo, Base64.DEFAULT);
                        Bitmap pic_bm = BitmapFactory.decodeByteArray(image_in_bytes, 0, image_in_bytes.length);
                        profile_pic.setImageBitmap(pic_bm);
                    }
                    profile_information.remove(birthdate_loc);
                    profile_information.add(birthdate_loc, birthdate);
                    //get age to display
                    if (profile_information.get(birthdate_loc) != null) {
                        displayAge(EditProfileActivity.this);
                    }
                    profile_information.remove(gender_loc);
                    profile_information.add(gender_loc, gender);
                    if (profile_information.get(gender_loc) != null) {
                        displayGender();
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


        //get text from edittext with profile name
        profile_name_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                String last_change = profile_information.remove(name_loc);
                String new_change = profile_name_edit_text.getText().toString();
                if (last_change != null && new_change != null && !last_change.equals(new_change)) {
                    changes_saved = false;
                }

                profile_information.add(name_loc, new_change);

            }
        });


        //get text from edittext with profile tagline
        profile_tagline_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String last_change = profile_information.remove(tagline_loc);
                String new_change = profile_tagline_edit_text.getText().toString();
                if (last_change != null && new_change != null && !last_change.equals(new_change)) {
                    System.out.println("tagline");
                    changes_saved = false;
                }
                profile_information.add(tagline_loc, new_change);
            }
        });

        //go to upload profile picture by clicking on it
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_upload_profile_pic = new Intent(EditProfileActivity.this, UploadProfilePhotoActivity.class);
                EditProfileActivity.this.startActivity(go_to_upload_profile_pic);
            }

        });





        Button ShowDatePickerButton = (Button) findViewById(R.id.age_picker_button);
        ShowDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call showDatePickerDialog to show it
                showDatePickerDialog();
            }
        });




        genderChoice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.male_radio_button) {
                    String prev_gender = profile_information.remove(gender_loc);
                    profile_information.add(gender_loc, "Male");
                    if (prev_gender == null || prev_gender.equals("Female")){
                        changes_saved = false;
                    }
                }
                if (checkedId == R.id.female_radio_button) {
                    String prev_gender = profile_information.remove(gender_loc);
                    profile_information.add(gender_loc, "Female");
                    if (prev_gender == null || prev_gender.equals("Male")){
                        changes_saved = false;
                    }
                }
                displayGender();
            }
        });



        Button SaveChangesButton = (Button) findViewById(R.id.edit_profile_save_changes_button);
        SaveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save the birthdate
                modify_firebase_profile_details();
                Gravity gravity = new Gravity();
                Toast saved_changes_toast = Toast.makeText(EditProfileActivity.this, "Your changes have been saved!!!", Toast.LENGTH_LONG);
                saved_changes_toast.setGravity(gravity.CENTER | gravity.CENTER, 0, 0);
                saved_changes_toast.show();


            }
        });

        Button GoToMyProfileButton = (Button) findViewById(R.id.edit_profile_to_my_profile_button);
        GoToMyProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!changes_saved) {
                    new AlertDialog.Builder(EditProfileActivity.this)
                            .setTitle("Save Changes")
                            .setMessage("Would you like to save your changes?")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue without saving changes
                                    Intent go_to_my_profile = new Intent(EditProfileActivity.this, MyProfileActivity.class);
                                    EditProfileActivity.this.startActivity(go_to_my_profile);
                                }
                            })
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //save changes
                                    modify_firebase_profile_details();
                                    Intent go_to_my_profile = new Intent(EditProfileActivity.this, MyProfileActivity.class);
                                    EditProfileActivity.this.startActivity(go_to_my_profile);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Intent go_to_my_profile = new Intent(EditProfileActivity.this, MyProfileActivity.class);
                    EditProfileActivity.this.startActivity(go_to_my_profile);
                }

            }
        });



    }
    @Override
    protected void onStart(){
        System.out.println("onStart method for EditProfileActivity being called");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        System.out.println("onRestart method for EditProfileActivity being called");
        super.onRestart();
    }

    @Override
    protected void onPause(){
        System.out.println("onPause method for EditProfileActivity being called");
        super.onPause();
    }

    @Override
    protected void onResume(){
        System.out.println("onResume method for EditProfileActivity being called");
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        System.out.println("onStop method for EditProfileActivity being called");
        super.onStop();

    }

    @Override
    protected void onDestroy(){
        System.out.println("onDestroy method for MyProfileActivity being called");
        super.onDestroy();
    }


    //this method uploads the edited changes to firebase to change the internal represenation of the user data
    public void modify_firebase_profile_details(){
        Map<String,Object> profile_details = new HashMap<String,Object>();
        //this map will be used to set the key-value pairs in firebase to their newly edited values
        profile_details.put("name", profile_information.get(name_loc));
        profile_details.put("tagline", profile_information.get(tagline_loc));
        profile_details.put("birthdate", profile_information.get(birthdate_loc));
        profile_details.put("gender", profile_information.get(gender_loc));

        Firebase user_profile = current_user.child("profile");
        user_profile.updateChildren(profile_details);
        changes_saved=true;//sets boolean that changes were saved is true
    }


    //this method shows the date picker dialog
    public void showDatePickerDialog() {
        FragmentManager fm = this.getFragmentManager();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(fm, "datePicker");
    }

    //this method gets the age
    public static void displayAge(Activity activity){

        String birthdate = profile_information.get(birthdate_loc);
        //gets the day month and year into integers
        int day = Integer.parseInt(birthdate.substring(0, birthdate.indexOf('/')));
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
        TextView profile_info = (TextView) activity.findViewById(R.id.edit_profile_info);
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
        TextView profile_info = (TextView) findViewById(R.id.edit_profile_info);
        String profile_info_text="";
        for (int i=0; i < profile_info.getText().length(); i++){
            profile_info_text = profile_info_text + profile_info.getText().charAt(i);
        }
        int profile_info_gender = profile_info_text.indexOf("Gender:") + 7;
        int profile_info_smoking = profile_info_text.indexOf("Smoking:");
        profile_info_text = profile_info_text.substring(0,profile_info_gender) +" "+ profile_information.get(gender_loc) +"\n\n"+ profile_info_text.substring(profile_info_smoking, profile_info_text.length());
        profile_info.setText(profile_info_text);
        //set radio button to be checked to current gender
        RadioGroup genderChoice = (RadioGroup) findViewById(R.id.gender_radio_buttons);//male and female radio button
        if (profile_information.get(gender_loc).equals("Male")){
            genderChoice.check(R.id.male_radio_button);
        } else if (profile_information.get(gender_loc).equals("Female")){
            genderChoice.check(R.id.female_radio_button);
        } else {
        }
    }




    //this method hides the soft keyboard from always covering the screen
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    //this method sets up UI to make sure soft keyboard is hidden when you click out of an edit text
    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(EditProfileActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
        Firebase current_user = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid().toString());


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (profile_information.get(birthdate_loc) != null && profile_information.get(birthdate_loc).length()>1){
                day = Integer.parseInt(profile_information.get(birthdate_loc).substring(0, profile_information.get(birthdate_loc).indexOf('/')));
                month = Integer.parseInt(profile_information.get(birthdate_loc).substring(profile_information.get(birthdate_loc).indexOf('/') + 1, profile_information.get(birthdate_loc).lastIndexOf('/')))-1;
                //due to way it is encoded with january starting at 0, need to subtract 1
                year = Integer.parseInt(profile_information.get(birthdate_loc).substring(profile_information.get(birthdate_loc).lastIndexOf('/') + 1, profile_information.get(birthdate_loc).length()));
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            month = month+1;//due to way it is encoded with january starting at 0
            String birthdate = day + "/" + month +"/" + year;
            String last_date = profile_information.remove(birthdate_loc);
            if (last_date==null || !last_date.equals(birthdate)){//makes changes unsaved if the birthdates were unequal
                changes_saved=false;
            }
            profile_information.add(birthdate_loc, birthdate);

            displayAge(getActivity());



        }




    }
}
