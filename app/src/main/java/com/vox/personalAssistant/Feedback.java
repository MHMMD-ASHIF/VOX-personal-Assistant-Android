package com.vox.personalAssistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voxpersonalassistant.R;

public class Feedback extends AppCompatActivity {

    Button btn;
    EditText editText1;
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setUpToolbar();

        editText1 = (EditText) findViewById(R.id.Name_edit);
        editText2 = (EditText)findViewById(R.id.Feedback_edit);

        btn = (Button) findViewById(R.id.btn_feed);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"infoline.helpzone@gmail.com"});;
                intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback From Vox");
                intent.putExtra(Intent.EXTRA_TEXT,"Name:"+editText1.getText()+"\n Message:"+editText2.getText());
                try {
                    startActivity(Intent.createChooser(intent, "Please Select Email"));
                }
                catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(Feedback.this,"There are no Email Clients",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setUpToolbar() {

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }
}