package com.example.mishaberkovich.roomsquad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.ChildEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyPreferencesActivity extends AppCompatActivity {

    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase current_user_prefs = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid().toString()).child("preferences");
    ArrayList<String> pref_array = new ArrayList<>();
    final int pref_size = 15;
    final int from_age_loc = 0;
    final int to_age_loc = 1;
    final int male_loc = 2;
    final int female_loc = 3;
    final int smoking_yes_loc = 4;
    final int smoking_no_loc = 5;
    final int pets_yes_loc = 6;
    final int pets_no_loc = 7;
    final int nightsOut_OneTwo_loc = 8;
    final int nightsOut_threeFour_loc = 9;
    final int nightsOut_fiveSeven_loc = 10;
    final int job_yes_loc = 11;
    final int job_no_loc = 12;
    final int earlyRiser_loc = 13;
    final int lateRiser_loc = 14;
    boolean changes_made = false;
    ChildEventListener pref_value_changed;
    boolean prefs_exist_in_firebase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_preferences);
        //to hide keyboard from covering half of screen, initially, then when clicking outside of edit text
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setupUI(findViewById(R.id.my_preferences_activity));

        //initializes arraylist
        for (int i=0; i<pref_size; i++){
            pref_array.add("");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Firebase.setAndroidContext(this);

        final EditText from_age_edit_text = (EditText) findViewById(R.id.pref_age_from);
        final EditText to_age_edit_text = (EditText) findViewById(R.id.pref_age_to);
        final CheckBox male_check = (CheckBox) findViewById(R.id.male_checkbox);
        final CheckBox female_check = (CheckBox) findViewById(R.id.female_checkbox);
        final CheckBox smoking_yes_check = (CheckBox) findViewById(R.id.smoking_yes_checkbox);
        final CheckBox smoking_no_check = (CheckBox) findViewById(R.id.smoking_no_checkbox);
        final CheckBox pets_yes_check = (CheckBox) findViewById(R.id.okayWithPets_yes_checkbox);
        final CheckBox pets_no_check = (CheckBox) findViewById(R.id.okayWithPets_no_checkbox);
        final CheckBox nights_oneTwo_check = (CheckBox) findViewById(R.id.numberOfNightsOut_oneTwo_checkbox);
        final CheckBox nights_threeFour_check = (CheckBox) findViewById(R.id.numberOfNightsOut_threeFour_checkbox);
        final CheckBox nights_fiveSeven_check = (CheckBox) findViewById(R.id.numberOfNightsOut_fiveSeven_checkbox);
        final CheckBox job_yes_check = (CheckBox) findViewById(R.id.fullTimeJob_yes_checkbox);
        final CheckBox job_no_check = (CheckBox) findViewById(R.id.fullTimeJob_no_checkbox);
        final CheckBox earlyRiser_check = (CheckBox) findViewById(R.id.earlyRiser_checkbox);
        final CheckBox lateRiser_check = (CheckBox) findViewById(R.id.lateRiser_checkbox);


        pref_value_changed = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                prefs_exist_in_firebase=true;
                if(dataSnapshot.getKey().equals("from age")){
                    if (dataSnapshot.getValue()!=null){
                        from_age_edit_text.setText(dataSnapshot.getValue().toString());
                    }
                } else if(dataSnapshot.getKey().equals("to age")){
                    if (dataSnapshot.getValue()!=null){
                        to_age_edit_text.setText(dataSnapshot.getValue().toString());
                    }
                } else if(dataSnapshot.getKey().equals("male")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        male_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("female")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        female_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("smoking yes")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        smoking_yes_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("smoking no")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        smoking_no_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("pets yes")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        pets_yes_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("pets no")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        pets_no_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("nights out 1-2")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        nights_oneTwo_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("nights out 3-4")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        nights_threeFour_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("nights out 5-7")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        nights_fiveSeven_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("job yes")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        job_yes_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("job no")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        job_no_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("early riser")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        earlyRiser_check.setChecked(true);
                    }
                } else if(dataSnapshot.getKey().equals("late riser")){
                    if (dataSnapshot.getValue()!=null && dataSnapshot.getValue().equals("true")){
                        lateRiser_check.setChecked(true);
                    }
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

        current_user_prefs.addChildEventListener(pref_value_changed);



        from_age_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref_array.add(from_age_loc, from_age_edit_text.getText().toString());
                changes_made = true;
            }
        });


        to_age_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref_array.remove(to_age_loc);
                pref_array.add(to_age_loc, to_age_edit_text.getText().toString());
                changes_made = true;

            }
        });


        male_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (male_check.isChecked()) {
                    pref_array.remove(male_loc);
                    pref_array.add(male_loc, "true");
                } else {
                    pref_array.remove(male_loc);
                    pref_array.add(male_loc, "false");
                }
                changes_made = true;
            }
        });


        female_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (female_check.isChecked()) {
                    pref_array.remove(female_loc);
                    pref_array.add(female_loc, "true");
                } else {
                    pref_array.remove(female_loc);
                    pref_array.add(female_loc, "false");
                }
                changes_made = true;
            }
        });

        smoking_yes_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smoking_yes_check.isChecked()) {
                    pref_array.remove(smoking_yes_loc);
                    pref_array.add(smoking_yes_loc, "true");
                } else {
                    pref_array.remove(smoking_yes_loc);
                    pref_array.add(smoking_yes_loc, "false");
                }
                changes_made = true;
            }
        });

        smoking_no_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smoking_no_check.isChecked()) {
                    pref_array.remove(smoking_no_loc);
                    pref_array.add(smoking_no_loc, "true");
                } else {
                    pref_array.remove(smoking_no_loc);
                    pref_array.add(smoking_no_loc, "false");
                }
                changes_made = true;
            }
        });

        pets_yes_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pets_yes_check.isChecked()) {
                    pref_array.remove(pets_yes_loc);
                    pref_array.add(pets_yes_loc, "true");
                } else {
                    pref_array.remove(pets_yes_loc);
                    pref_array.add(pets_yes_loc, "false");
                }
                changes_made = true;
            }
        });

        pets_no_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pets_no_check.isChecked()) {
                    pref_array.remove(pets_no_loc);
                    pref_array.add(pets_no_loc, "true");
                } else {
                    pref_array.remove(pets_no_loc);
                    pref_array.add(pets_no_loc, "false");
                }
                changes_made = true;
            }
        });

        nights_oneTwo_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nights_oneTwo_check.isChecked()) {
                    pref_array.remove(nightsOut_OneTwo_loc);
                    pref_array.add(nightsOut_OneTwo_loc, "true");
                } else {
                    pref_array.remove(nightsOut_OneTwo_loc);
                    pref_array.add(nightsOut_OneTwo_loc, "false");
                }
                changes_made = true;
            }
        });

        nights_threeFour_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nights_threeFour_check.isChecked()) {
                    pref_array.remove(nightsOut_threeFour_loc);
                    pref_array.add(nightsOut_threeFour_loc, "true");
                } else {
                    pref_array.remove(nightsOut_threeFour_loc);
                    pref_array.add(nightsOut_threeFour_loc, "false");
                }
                changes_made = true;
            }
        });

        nights_fiveSeven_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nights_fiveSeven_check.isChecked()) {
                    pref_array.remove(nightsOut_fiveSeven_loc);
                    pref_array.add(nightsOut_fiveSeven_loc, "true");
                } else {
                    pref_array.remove(nightsOut_fiveSeven_loc);
                    pref_array.add(nightsOut_fiveSeven_loc, "false");
                }
                changes_made = true;
            }
        });

        job_yes_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (job_yes_check.isChecked()) {
                    pref_array.remove(job_yes_loc);
                    pref_array.add(job_yes_loc, "true");
                } else {
                    pref_array.remove(job_yes_loc);
                    pref_array.add(job_yes_loc, "false");
                }
                changes_made = true;
            }
        });

        job_no_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (job_no_check.isChecked()) {
                    pref_array.remove(job_no_loc);
                    pref_array.add(job_no_loc, "true");
                } else {
                    pref_array.remove(job_no_loc);
                    pref_array.add(job_no_loc, "false");
                }
                changes_made = true;
            }
        });

        earlyRiser_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (earlyRiser_check.isChecked()) {
                    pref_array.remove(earlyRiser_loc);
                    pref_array.add(earlyRiser_loc, "true");
                } else {
                    pref_array.remove(earlyRiser_loc);
                    pref_array.add(earlyRiser_loc, "false");
                }
                changes_made = true;
            }
        });

        lateRiser_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lateRiser_check.isChecked()) {
                    pref_array.remove(lateRiser_loc);
                    pref_array.add(lateRiser_loc, "true");
                } else {
                    pref_array.remove(lateRiser_loc);
                    pref_array.add(lateRiser_loc, "false");
                }
                changes_made = true;
            }
        });



        //click on bottom bar to return to main menu
        Button GoToMainMenuButton = (Button) findViewById(R.id.my_preferences_to_main_menu_button);
        GoToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (changes_made){
                    new AlertDialog.Builder(MyPreferencesActivity.this)
                            .setTitle("Save Changes")
                            .setMessage("Would you like to save your changes?")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent go_to_main_menu = new Intent(MyPreferencesActivity.this, MainMenuActivity.class);
                                    MyPreferencesActivity.this.startActivity(go_to_main_menu);
                                }
                            })
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (pref_array.get(from_age_loc).length()>0 && pref_array.get(to_age_loc).length()>0){
                                        int from_age_int = Integer.parseInt(pref_array.get(from_age_loc));
                                        int to_age_int = Integer.parseInt(pref_array.get(to_age_loc));
                                        if (from_age_int > to_age_int){
                                            new AlertDialog.Builder(MyPreferencesActivity.this)
                                                    .setTitle("Input Error")
                                                    .setMessage("From Age must be less than or equal to To Age. Would you like to fix this? ")
                                                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent go_to_main_menu = new Intent(MyPreferencesActivity.this, MainMenuActivity.class);
                                                            MyPreferencesActivity.this.startActivity(go_to_main_menu);
                                                        }
                                                    })
                                                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();


                                        } else {
                                            updatePreferencesFirebase();
                                            Intent go_to_main_menu = new Intent(MyPreferencesActivity.this, MainMenuActivity.class);
                                            MyPreferencesActivity.this.startActivity(go_to_main_menu);
                                        }


                                    } else {
                                        updatePreferencesFirebase();
                                        Intent go_to_main_menu = new Intent(MyPreferencesActivity.this, MainMenuActivity.class);
                                        MyPreferencesActivity.this.startActivity(go_to_main_menu);
                                    }


                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Intent go_to_main_menu = new Intent(MyPreferencesActivity.this, MainMenuActivity.class);
                    MyPreferencesActivity.this.startActivity(go_to_main_menu);
                }


            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        for (int i=0; i<pref_size; i++){
            pref_array.remove(0);
        }
        current_user_prefs.removeEventListener(pref_value_changed);
    }

    public void updatePreferencesFirebase(){
        Map<String,Object> pref_details = new HashMap<>();
        pref_details.put("from age", pref_array.get(from_age_loc));
        pref_details.put("to age", pref_array.get(to_age_loc));
        pref_details.put("male", pref_array.get(male_loc));
        pref_details.put("female", pref_array.get(female_loc));
        pref_details.put("smoking yes", pref_array.get(smoking_yes_loc));
        pref_details.put("smoking no", pref_array.get(smoking_no_loc));
        pref_details.put("pets yes", pref_array.get(pets_yes_loc));
        pref_details.put("pets no", pref_array.get(pets_no_loc));
        pref_details.put("nights out 1-2", pref_array.get(nightsOut_OneTwo_loc));
        pref_details.put("nights out 3-4", pref_array.get(nightsOut_threeFour_loc));
        pref_details.put("nights out 5-7", pref_array.get(nightsOut_fiveSeven_loc));
        pref_details.put("job yes", pref_array.get(job_yes_loc));
        pref_details.put("job no", pref_array.get(job_no_loc));
        pref_details.put("early riser", pref_array.get(earlyRiser_loc));
        pref_details.put("late riser", pref_array.get(lateRiser_loc));

        if (prefs_exist_in_firebase){
            current_user_prefs.updateChildren(pref_details);
        }else{
            current_user_prefs.setValue(pref_details);
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
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MyPreferencesActivity.this);
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

}
