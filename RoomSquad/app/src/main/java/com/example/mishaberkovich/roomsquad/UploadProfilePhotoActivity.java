package com.example.mishaberkovich.roomsquad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.Manifest;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadProfilePhotoActivity extends AppCompatActivity {

    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase current_user = roomsquad_firebase.child("users").child(roomsquad_firebase.getAuth().getUid().toString());

    ImageView targetImage;
    final int external_storage_permission = 0;
    Uri targetUri;
    final ArrayList<String> photo_string_container = new ArrayList<>();
    boolean photo_saved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_photo);
        Firebase.setAndroidContext(this);

        targetImage = (ImageView)findViewById(R.id.targetimage);


        //launch the photo gallery by default in beginning
        Intent photo_gallery_launch = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photo_gallery_launch, 0);

        Button LaunchPhotoGalleryButton = (Button) findViewById(R.id.photo_gallery_button);
        LaunchPhotoGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch the photo gallery
                Intent photo_gallery_launch = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photo_gallery_launch, 0);
            }
        });

        Button SavePhotoButton = (Button) findViewById(R.id.save_photo_button);
        SavePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save changes
                upload_photo_to_firebase();
            }
        });

        Button GoToEditProfileButton = (Button) findViewById(R.id.upload_profile_photo_to_edit_profile_button);
        GoToEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!photo_saved){
                    new AlertDialog.Builder(UploadProfilePhotoActivity.this)
                            .setTitle("Save Changes")
                            .setMessage("Would you like to save your changes?")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue without saving changes
                                    Intent go_to_edit_profile = new Intent(UploadProfilePhotoActivity.this, EditProfileActivity.class);
                                    UploadProfilePhotoActivity.this.startActivity(go_to_edit_profile);
                                }
                            })
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //save changes
                                    upload_photo_to_firebase();
                                    Intent go_to_edit_profile = new Intent(UploadProfilePhotoActivity.this, EditProfileActivity.class);
                                    UploadProfilePhotoActivity.this.startActivity(go_to_edit_profile);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Intent go_to_edit_profile = new Intent(UploadProfilePhotoActivity.this, EditProfileActivity.class);
                    UploadProfilePhotoActivity.this.startActivity(go_to_edit_profile);
                }

            }
        });
    }


    public void upload_photo_to_firebase(){
        photo_saved = true;
        Map<String,Object> profile_details = new HashMap<String,Object>();
        String imageFile = photo_string_container.remove(0);
        profile_details.put("photo", imageFile);
        Firebase user_profile = current_user.child("profile");
        user_profile.updateChildren(profile_details);
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
                displayPhotoFullScreenAndConvertToString();
            }

        }

    }

    public void displayPhotoFullScreenAndConvertToString(){
        photo_saved = false;
        String real_path = getRealPathFromURI(this, targetUri);//get the real uri path
        BitmapFactory.Options smaller_sample = new BitmapFactory.Options();
        smaller_sample.inJustDecodeBounds = true;//dont get the image just its size
        BitmapFactory.decodeFile(real_path, smaller_sample);//decode image without saving it in memory
        int width = smaller_sample.outWidth;
        int height = smaller_sample.outHeight;
        System.out.println("width: " + width + ", height: " + height);
        smaller_sample.inJustDecodeBounds = false;//now we want the real image
        //each pixel is 4 bytes
        int bytes = width*height*4;
        int imageReduction = 1;
        if (bytes>5000000){//5 MB, just in case
            imageReduction = bytes/5000000 + 1;//amount of times smaller you want the image to be
        }
        smaller_sample.inSampleSize = imageReduction;
        Bitmap bitmap = BitmapFactory.decodeFile(real_path, smaller_sample);
        width = smaller_sample.outWidth;
        height = smaller_sample.outHeight;
        System.out.println("width: " + width + ", height: " + height);
        targetImage.setImageBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        System.out.println("bitmap bytes: " + bitmap.getByteCount());
        //bitmap.recycle();//crashes program
        System.out.println("byte length: " + byteArray.length);
        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
        photo_string_container.add(0,imageFile);

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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case external_storage_permission: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    displayPhotoFullScreenAndConvertToString();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


}
