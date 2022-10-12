package com.vox.personalAssistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.voxpersonalassistant.R;

public class Launch_chat extends AppCompatActivity {


    private  static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_chat);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(Launch_chat.this, Chat.class);
                startActivity(homeIntent);
                finish();
            }


        },SPLASH_TIME_OUT);
    }
}
