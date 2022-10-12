package com.vox.personalAssistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voxpersonalassistant.R;

public class Terms_conditions extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        setUpToolbar();

        btn = (Button) findViewById(R.id.btn_term);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(Terms_conditions.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setUpToolbar() {

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Terms&service");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

}