package com.example.mishaberkovich.roomsquad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Button ReturnToMainMenu = (Button) findViewById(R.id.my_profile_to_main_menu_button);
        ReturnToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMainMenu = new Intent(MyProfileActivity.this, MainMenuActivity.class);
                MyProfileActivity.this.startActivity(gotoMainMenu);
            }
        });
    }
}
