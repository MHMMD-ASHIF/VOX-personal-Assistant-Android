package com.vox.personalAssistant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voxpersonalassistant.R;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class Rate extends AppCompatActivity {

    private ReviewInfo reviewInfo;
    private ReviewManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        setUpToolbar();
        activateReviewInfo();
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener((view)->

        {
            startReviewFlow();

        });


    }
    void activateReviewInfo()
    {
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> managerInfoTask = manager.requestReviewFlow();
        managerInfoTask.addOnCompleteListener((task)->
        {
        if (task.isSuccessful()){
            reviewInfo = task.getResult();
            
        }else{

            Toast.makeText(this,"Review Failed to",Toast.LENGTH_SHORT).show();
        }

    });
    }

    void  startReviewFlow()
    {
        if (reviewInfo !=null)
        {
            Task<Void> flow = manager.launchReviewFlow(this,reviewInfo);
            flow.addOnCompleteListener(task -> {

                Toast.makeText(this,"Rating is Complete", Toast.LENGTH_SHORT).show();
            });
        }
    }


        private void setUpToolbar() {

            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Rate App");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



}

}