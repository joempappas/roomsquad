package com.example.mishaberkovich.roomsquad;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ImageButton;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditMyPostingActivity extends AppCompatActivity {

    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase current_user = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid().toString());
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
    boolean changes_made = false;//boolean keeps track if changes made

    //array of photos, since there can be zero or more
    Uri targetUri;//photo uri
    final int external_storage_permission = 0;
    final static ArrayList<String> posting_photos = new ArrayList<>();
    final static ArrayList<String> photo_ids = new ArrayList<>();

    ChildEventListener editMyPostingChildEventListener;

    boolean photo_gallery_stop = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("EditPosting onCreate being called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_posting);

        //to hide keyboard from covering half of screen, initially, then when clicking outside of edit text
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setupUI(findViewById(R.id.edit_my_posting_activity));

        //initializes arraylist
        for (int i=0; i<POSTING_INFO_SIZE; i++){
            posting_information.add("");
        }
        String posting_id = getIntent().getExtras().getString("posting_id");//get the posting id to show

        posting_information.remove(uid_loc);
        posting_information.add(uid_loc, posting_id);
        posting_information.remove(photo_loc);
        posting_information.add(photo_loc, "0");
    }

    @Override
    protected void onResume(){
        System.out.println("EditPosting onResume being called");
        super.onResume();
        Firebase.setAndroidContext(this);


        final TextView top_bar_text = (TextView) findViewById(R.id.edit_my_posting_top_bar_text);
        final EditText posting_name_text = (EditText) findViewById(R.id.edit_my_posting_name);
        final EditText posting_description_text = (EditText) findViewById(R.id.edit_about_my_posting);//edit text for posting name
        final RadioButton roommate_radio_btn = (RadioButton) findViewById(R.id.roommate_radio_button);
        final RadioButton sublet_radio_btn = (RadioButton) findViewById(R.id.sublet_radio_button);
        final RadioButton tenant_radio_btn = (RadioButton) findViewById(R.id.tenant_radio_button);
        final TextView posting_location_text = (TextView) findViewById(R.id.edit_posting_location_text);
        final EditText rm_minprice_range_text = (EditText) findViewById(R.id.from_dollar_input);
        final EditText rm_maxprice_range_text = (EditText) findViewById(R.id.to_dollar_input);
        final CheckBox rm_single = (CheckBox) findViewById(R.id.single_checkbox);
        final CheckBox rm_double = (CheckBox) findViewById(R.id.double_checkbox);
        final CheckBox rm_more = (CheckBox) findViewById(R.id.more_checkbox);


        editMyPostingChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("user")) {
                    String posting_user = dataSnapshot.getValue().toString();
                    posting_information.remove(user_loc);
                    posting_information.add(user_loc, posting_user);
                    if (!posting_user.equals(roomsquad_firebase.getAuth().getUid())) {
                        System.out.println("Error: Not authorized to view this page");
                    }
                } else if (dataSnapshot.getKey().equals("type")) {
                    String posting_type = dataSnapshot.getValue().toString();
                    posting_information.remove(type_loc);
                    posting_information.add(type_loc, posting_type);
                    if (posting_type.equals("Roommate")){
                        roommate_radio_btn.setChecked(true);
                    } else if (posting_type.equals("Sublet")){
                        sublet_radio_btn.setChecked(true);
                    } else if (posting_type.equals("Tenant")){
                        tenant_radio_btn.setChecked(true);
                    }
                } else if (dataSnapshot.getKey().equals("date")) {
                    String posting_date = dataSnapshot.getValue().toString();
                    posting_information.remove(date_loc);
                    posting_information.add(date_loc, posting_date);
                    top_bar_text.setText(posting_date + ": ");
                } else if (dataSnapshot.getKey().equals("name")) {
                    String posting_name = dataSnapshot.getValue().toString();
                    posting_information.remove(name_loc);
                    posting_information.add(name_loc, posting_name);
                    posting_name_text.setText(posting_name);
                    top_bar_text.setText(top_bar_text.getText() + "\"" + posting_name + "\"");
                } else if (dataSnapshot.getKey().equals("description")) {
                    String posting_description = dataSnapshot.getValue().toString();
                    posting_information.remove(description_loc);
                    posting_information.add(description_loc, posting_description);
                    posting_description_text.setText(posting_description);
                } else if (dataSnapshot.getKey().equals("location")) {
                    String posting_location = dataSnapshot.getValue().toString();
                    posting_information.remove(location_loc);
                    posting_information.add(location_loc, posting_location);
                    posting_location_text.setText(posting_location);
                } else if (dataSnapshot.getKey().equals("minprice")) {
                    String posting_minprice = dataSnapshot.getValue().toString();
                    posting_information.remove(minprice_loc);
                    posting_information.add(minprice_loc, posting_minprice);
                    rm_minprice_range_text.setText(posting_minprice);
                } else if (dataSnapshot.getKey().equals("maxprice")) {
                    String posting_maxprice = dataSnapshot.getValue().toString();
                    posting_information.remove(maxprice_loc);
                    posting_information.add(maxprice_loc, posting_maxprice);
                    rm_maxprice_range_text.setText(posting_maxprice);
                } else if (dataSnapshot.getKey().equals("single")) {
                    String single_bool = dataSnapshot.getValue().toString();
                    posting_information.remove(single_loc);
                    posting_information.add(single_loc, single_bool);
                    if (single_bool != null && single_bool.equals("true")) {
                        rm_single.setChecked(true);
                    } else if (single_bool != null) {
                        rm_single.setChecked(false);
                    }
                } else if (dataSnapshot.getKey().equals("double")) {
                    String double_bool = dataSnapshot.getValue().toString();
                    posting_information.remove(double_loc);
                    posting_information.add(double_loc, double_bool);
                    if (double_bool != null && double_bool.equals("true")) {
                        rm_double.setChecked(true);
                    } else if (double_bool != null) {
                        rm_double.setChecked(false);
                    }
                } else if (dataSnapshot.getKey().equals("more")) {
                    String more_bool = dataSnapshot.getValue().toString();
                    posting_information.remove(more_loc);
                    posting_information.add(more_loc, more_bool);
                    if (more_bool != null && more_bool.equals("true")) {
                        rm_more.setChecked(true);
                    } else if (more_bool != null) {
                        rm_more.setChecked(false);
                    }
                } else if (dataSnapshot.getKey().equals("photos")) {
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        if (!d.getValue().toString().equals("deleted")){
                            String photo_file = (String) d.child("photo file").getValue();
                            showPhotoInViewAndSaveToArray(photo_file);
                            photo_ids.add(d.getKey());
                        }

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

        //add listener object to listen for the "posting" children
        Postings.child(posting_information.get(uid_loc)).addChildEventListener(editMyPostingChildEventListener);



        //get the current date
        // Use the current date as the default date in the picker
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        month = month+1;//due to way it is encoded with january starting at 0
        String date = day + "/" + month +"/" + year;
        posting_information.remove(date_loc);
        posting_information.add(date_loc, date);


        //adds user to posting
        posting_information.remove(user_loc);
        posting_information.add(user_loc, roomsquad_firebase.getAuth().getUid().toString());

        //radio buttons to choose posting_type
        final RadioGroup PostingTypeChoice = (RadioGroup) findViewById(R.id.posting_type_radio_buttons);
        PostingTypeChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RelativeLayout RoommateOptions = (RelativeLayout) findViewById(R.id.RoommateOptionsContainer);
                boolean radio_btn_is_checked = false;

                if (checkedId == R.id.roommate_radio_button) {
                    radio_btn_is_checked=true;
                    String prev_type = posting_information.remove(type_loc);
                    posting_information.add(type_loc, "Roommate");
                    if (prev_type == null || !prev_type.equals("Roommate")) {
                        changes_made = true;
                    }
                }
                if (checkedId == R.id.sublet_radio_button) {
                    radio_btn_is_checked=true;
                    String prev_type = posting_information.remove(type_loc);
                    posting_information.add(type_loc, "Sublet");
                    if (prev_type == null || !prev_type.equals("Sublet")){
                        changes_made=true;
                    }

                }
                if (checkedId == R.id.tenant_radio_button) {
                    radio_btn_is_checked=true;
                    String prev_type = posting_information.remove(type_loc);
                    posting_information.add(type_loc, "Tenant");
                    if (prev_type == null || !prev_type.equals("Tenant")) {
                        changes_made = true;
                    }
                }
                if (radio_btn_is_checked){
                    RoommateOptions.setVisibility(View.VISIBLE);
                    final EditText minprice_input = (EditText) findViewById(R.id.from_dollar_input);
                    final EditText maxprice_input = (EditText) findViewById(R.id.to_dollar_input);
                    minprice_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            posting_information.remove(minprice_loc);
                            String minprice = minprice_input.getText().toString();
                            posting_information.add(minprice_loc, minprice);
                            changes_made = true;
                        }
                    });
                    maxprice_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            posting_information.remove(maxprice_loc);
                            String maxprice = maxprice_input.getText().toString();
                            posting_information.add(maxprice_loc, maxprice);
                            changes_made = true;
                        }
                    });
                    final CheckBox single_check = (CheckBox) findViewById(R.id.single_checkbox);
                    final CheckBox double_check = (CheckBox) findViewById(R.id.double_checkbox);
                    final CheckBox more_check = (CheckBox) findViewById(R.id.more_checkbox);
                    single_check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(single_check.isChecked()){
                                posting_information.remove(single_loc);
                                posting_information.add(single_loc,"true");
                            }else{
                                posting_information.remove(single_loc);
                                posting_information.add(single_loc,"false");
                            }
                            changes_made =true;
                        }
                    });
                    double_check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(double_check.isChecked()){
                                posting_information.remove(double_loc);
                                posting_information.add(double_loc,"true");
                            }else{
                                posting_information.remove(double_loc);
                                posting_information.add(double_loc,"false");
                            }
                            changes_made =true;
                        }
                    });
                    more_check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(more_check.isChecked()){
                                posting_information.remove(more_loc);
                                posting_information.add(more_loc,"true");
                            }else{
                                posting_information.remove(more_loc);
                                posting_information.add(more_loc,"false");
                            }
                            changes_made =true;
                        }
                    });
                }

            }
        });


        final EditText posting_name_edit_text = (EditText) findViewById(R.id.edit_my_posting_name);//edit text for posting name
        //get text from edittext with profile tagline
        posting_name_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                posting_information.remove(name_loc);
                String posting_name = posting_name_edit_text.getText().toString();
                posting_information.add(name_loc, posting_name);
                changes_made = true;
            }
        });

        ImageButton AddPhotoButton = (ImageButton) findViewById(R.id.add_photo_button);
        AddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch the photo gallery
                photo_gallery_stop = true;
                save_changes(1);
            }
        });


        final EditText posting_description_edit_text = (EditText) findViewById(R.id.edit_about_my_posting);//edit text for posting name
        //get text from edittext with profile tagline
        posting_description_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                posting_information.remove(description_loc);
                String posting_name = posting_description_edit_text.getText().toString();
                posting_information.add(description_loc, posting_name);
                changes_made = true;
            }
        });

        Button GoToMyPostingButton = (Button) findViewById(R.id.edit_my_posting_to_my_posting_button);
        GoToMyPostingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_changes(0);
            }
        });
    }

    @Override
    protected void onPause() {
        System.out.println("EditPosting onPause being called");
        super.onPause();

    }

    @Override
    protected void onStop(){
        System.out.println("EditPosting onStop being called");
        super.onStop();
        String posting_id = posting_information.get(uid_loc);
        if (!photo_gallery_stop){
            for(int i=0; i <POSTING_INFO_SIZE; i++){
                posting_information.remove(0);
            }
            int photo_amount = posting_photos.size();
            for (int i=0; i < photo_amount; i++){
                posting_photos.remove(0);
            }
            Postings.child(posting_id).removeEventListener(editMyPostingChildEventListener);
            LinearLayout photo_scroll = (LinearLayout) findViewById(R.id.photos_in_scroll);
            photo_scroll.removeAllViewsInLayout();
        }

    }

    //this method uploads the edited changes to firebase to change the internal representation of the user data
    public void modify_firebase_posting_details(){

        Map<String,Object> posting_details = new HashMap<>();
        posting_details.put("user", posting_information.get(user_loc));
        posting_details.put("type", posting_information.get(type_loc));
        posting_details.put("date", posting_information.get(date_loc));
        posting_details.put("name", posting_information.get(name_loc));
        posting_details.put("description", posting_information.get(description_loc));
        posting_details.put("location", posting_information.get(location_loc));
        posting_details.put("minprice", posting_information.get(minprice_loc));
        posting_details.put("maxprice", posting_information.get(maxprice_loc));
        posting_details.put("single", posting_information.get(single_loc));
        posting_details.put("double", posting_information.get(double_loc));
        posting_details.put("more", posting_information.get(more_loc));
        posting_details.put("photos", posting_information.get(more_loc));

        String posting_id = posting_information.get(uid_loc);
        //if it is a new posting
        if(posting_id == null || posting_id.length()==0){
            Firebase new_posting = Postings.push();
            posting_information.remove(uid_loc);
            posting_information.add(uid_loc, new_posting.getKey());
            new_posting.updateChildren(posting_details);
            Map<String, Object> posting_uid = new HashMap<>();
            posting_uid.put(posting_information.get(uid_loc), "true");
            current_user.child("postings").updateChildren(posting_uid);


        }
        //otherwise editing an existing posting
        else {
            posting_details.remove("photos");
            Postings.child(posting_id).updateChildren(posting_details);
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
                    hideSoftKeyboard(EditMyPostingActivity.this);
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

    //the result of the photo gallery activity returns photo picked
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            targetUri = data.getData();
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                System.out.println("No permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, external_storage_permission);
            } else {
                LinearLayout photo_scroll = (LinearLayout) findViewById(R.id.photos_in_scroll);
                photo_scroll.removeAllViewsInLayout();
                processPhoto();

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case external_storage_permission: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    processPhoto();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void processPhoto(){

        String real_path = getRealPathFromURI(this, targetUri);//get the real uri path
        BitmapFactory.Options smaller_sample = new BitmapFactory.Options();
        smaller_sample.inJustDecodeBounds = true;//dont get the image just its size
        BitmapFactory.decodeFile(real_path, smaller_sample);//decode image without saving it in memory
        int width = smaller_sample.outWidth;
        int height = smaller_sample.outHeight;
        smaller_sample.inJustDecodeBounds = false;//now we want the real image
        //each pixel is 4 bytes
        int bytes = width*height*4;
        int imageReduction = 1;
        if (bytes>5000000){//5 MB, just in case
            imageReduction = bytes/5000000 + 1;//amount of times smaller you want the image to be
        }
        smaller_sample.inSampleSize = imageReduction;
        Bitmap bitmap = BitmapFactory.decodeFile(real_path, smaller_sample);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String posting_id = posting_information.get(uid_loc);
        System.out.println("posting id: " + posting_id);
        Map<String,Object> posting_photo_map = new HashMap<>();
        posting_photo_map.put("photo file", imageFile);
        Firebase next_photo_firebase = Postings.child(posting_id).child("photos").push();
        next_photo_firebase.updateChildren(posting_photo_map);


    }

    //show photos
    public void showPhotoInViewAndSaveToArray(String photo_file){
        LinearLayout photo_scroll_container = (LinearLayout) findViewById(R.id.photo_scroll_container);
        final LinearLayout photo_scroll = (LinearLayout) findViewById(R.id.photos_in_scroll);
        ImageView next_photo = new ImageView(EditMyPostingActivity.this);
        byte[] image_in_bytes = Base64.decode(photo_file, Base64.DEFAULT);
        Bitmap pic_bm = BitmapFactory.decodeByteArray(image_in_bytes, 0, image_in_bytes.length);
        next_photo.setImageBitmap(pic_bm);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);//set the params
        next_photo.setLayoutParams(params);
        next_photo.setAdjustViewBounds(true);
        ImageButton DeletePhotoButton = new ImageButton(EditMyPostingActivity.this);
        DeletePhotoButton.setImageResource(R.drawable.delete);
        DeletePhotoButton.setScaleType(ImageView.ScaleType.FIT_XY);
        params = new LinearLayout.LayoutParams(100, 100);//set the params
        DeletePhotoButton.setLayoutParams(params);
        final FrameLayout photo_with_delete = new FrameLayout(EditMyPostingActivity.this);
        photo_with_delete.addView(next_photo);
        photo_with_delete.addView(DeletePhotoButton);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);//set the params
        photo_with_delete.setLayoutParams(params);
        photo_scroll.addView(photo_with_delete);
        final int photo_amount = Integer.parseInt(posting_information.get(photo_loc));
        final int photo_amount_new = photo_amount + 1;
        DeletePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo_scroll.removeView(photo_with_delete);
                String posting_id = posting_information.get(uid_loc);
                Firebase this_photo_firebase = Postings.child(posting_id).child("photos").child(photo_ids.get(photo_amount));
                this_photo_firebase.setValue("deleted");
            }
        });

        posting_information.remove(photo_loc);
        posting_information.add(photo_loc, String.valueOf(photo_amount_new));
        posting_photos.add(photo_file);
        photo_scroll_container.setVisibility(View.VISIBLE);

    }

    //given a URI find a real path
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void save_changes(int intent){

        //name and type must have been entered
        if(posting_information.get(name_loc).length()>0 && posting_information.get(type_loc).length()>0){


                String minprice = posting_information.get(minprice_loc);
                String maxprice = posting_information.get(maxprice_loc);



                if (minprice.length()>0 && maxprice.length()>0 && Integer.parseInt(minprice) > Integer.parseInt(maxprice)){
                    new AlertDialog.Builder(EditMyPostingActivity.this)
                            .setTitle("Minprice > Maxprice")
                            .setMessage("Minimum price must be less than Maximum price. Do you wish to discard these changes, or go back and fix this?")
                            .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //discard draft, go back to all my postings list
                                    photo_gallery_stop=false;
                                    Intent go_to_my_postings = new Intent(EditMyPostingActivity.this, MyPostingsActivity.class);
                                    EditMyPostingActivity.this.startActivity(go_to_my_postings);
                                }
                            })
                            .setNegativeButton("Continue Editing", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                } else {
                    if (changes_made){
                        modify_firebase_posting_details();
                    }
                    if (intent==0){

                        photo_gallery_stop=false;
                        Intent view_posting = new Intent(EditMyPostingActivity.this, MyPostingActivity.class);
                        view_posting.putExtra("posting_id", posting_information.get(uid_loc));
                        EditMyPostingActivity.this.startActivity(view_posting);
                    } else if (intent==1){
                        photo_gallery_stop=true;
                        Intent photo_gallery_launch = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        photo_gallery_launch.putExtra("posting_id", posting_information.get(uid_loc));
                        startActivityForResult(photo_gallery_launch, 0);
                    }

                }



        } else {
            new AlertDialog.Builder(EditMyPostingActivity.this)
                    .setTitle("Name and Type are empty")
                    .setMessage("Posting Name AND Posting Type must be entered! Do you wish to discard this draft or go back and add these parameters?")
                    .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //discard draft, go back to all my postings list
                            Intent go_to_my_postings = new Intent(EditMyPostingActivity.this, MyPostingsActivity.class);
                            EditMyPostingActivity.this.startActivity(go_to_my_postings);
                        }
                    })
                    .setNegativeButton("Continue Editing", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    }
}
