package com.vox.personalAssistant;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.voxpersonalassistant.R;

public class App_Settings extends AppCompatActivity {


    ImageButton imageButton;

     AudioManager audioManager;
    SwitchCompat switchCompat;

    private  int brightness;

    private ContentResolver contentResolver;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


// Audio Effect



        setUpToolbar();
















/* Brightness Adjustment */



        final Dialog levelDialog = new Dialog(this);

        WindowManager.LayoutParams settings = getWindow().getAttributes();

        getWindow().setAttributes(settings);





        ImageButton imageButton5 = (ImageButton) findViewById(R.id.Display_btn);
        imageButton5.setOnClickListener(new View.OnClickListener(){
    @Override
    public void onClick(View v) {


        levelDialog.setContentView(R.layout.dialog_brightness);
        final TextView levelTxt = (TextView)levelDialog.findViewById(R.id.level_txt);
        final SeekBar levelSeek = (SeekBar)levelDialog.findViewById(R.id.level_seek);

        contentResolver = getContentResolver();
        window = getWindow();



        levelSeek.setMax(255);
        
        levelSeek.setKeyProgressIncrement(1);

        try {
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            levelSeek.setProgress(brightness);
        }catch(Settings.SettingNotFoundException e){
            e.printStackTrace();
        }





        levelSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //change to progress
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = progress;
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                android.provider.Settings.System.putInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS,brightness);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.screenBrightness = brightness / (float) 300;
                window.setAttributes(layoutParams);


            }
            //methods to implement but not necessary to amend
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });



        Button okBtn = (Button)levelDialog.findViewById(R.id.level_ok);

        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//respond to level
                int chosenLevel = levelSeek.getProgress();
                levelDialog.dismiss();
            }
        });

        levelDialog.show();






    }
});

/* brightness */





/* Rate app Button */


        imageButton = (ImageButton) findViewById(R.id.Rate_app_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App_Settings.this,Rate.class);
                startActivity(intent);
            }
        });


/* Feedback section */

        imageButton = (ImageButton) findViewById(R.id.feedback_forward);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App_Settings.this,Feedback.class);
                startActivity(intent);
            }
        });


        /* Terms  and conditions */

        imageButton = (ImageButton) findViewById(R.id.privacy_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App_Settings.this,Privacy_Policy.class);
                startActivity(intent);
            }
        });



        /* Privacy Policy */

        imageButton = (ImageButton) findViewById(R.id.Terms_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App_Settings.this,Terms_conditions.class);
                startActivity(intent);
            }
        });



  // Audio controller

        final Dialog soundDilog = new Dialog(this);

        WindowManager.LayoutParams settingsSound = getWindow().getAttributes();

        getWindow().setAttributes(settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (Settings.System.canWrite(this)) {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).cancel();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
                startActivity(intent);
            }
        }




        ImageButton SoundButton = (ImageButton) findViewById(R.id.MediaVolume_btn);
        SoundButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                soundDilog.setContentView(R.layout.volume_dialogue);

                final SeekBar seekBarSound = (SeekBar)soundDilog.findViewById(R.id.sound_seeker);




                seekBarSound.setMax(255);
                seekBarSound.setKeyProgressIncrement(1);

                try {
                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
                    audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                    seekBarSound.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                    seekBarSound.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    seekBarSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();

                }

                Button okBtn = (Button)soundDilog.findViewById(R.id.Sound_button);

                okBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
//respond to level
                        int chosenLevel = seekBarSound.getProgress();
                        soundDilog.dismiss();
                    }
                });

                soundDilog.show();






            }
        });




        Audio_effect();
        Data_saver();
        Notification();
        Notification_sound();
        Screen_timeout();
        Auto_Complete();




    }


SwitchCompat Audio_effect;
    public   void Audio_effect(){
        Audio_effect = (SwitchCompat) findViewById(R.id.Audio_effect_btn);
        SharedPreferences sharedPreferences_Audio = getSharedPreferences("save",MODE_PRIVATE);
        Audio_effect.setChecked(sharedPreferences_Audio.getBoolean("value", true));
        Audio_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Audio_effect.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    Audio_effect.setChecked(true);

                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    Audio_effect.setChecked(false);

                }
            }
        });


    }


SwitchCompat Screen_Timeout;

    public  void  Screen_timeout(){


        // Data Saver Button
        Screen_Timeout = (SwitchCompat) findViewById(R.id.Screen_timeout_btn);
        SharedPreferences sharedPreferences_screen = getSharedPreferences("save2",MODE_PRIVATE);
        Screen_Timeout.setChecked(sharedPreferences_screen.getBoolean("value2", true));
        Screen_Timeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Screen_Timeout.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save2",MODE_PRIVATE).edit();
                    editor.putBoolean("value2",true);
                    editor.apply();
                    Screen_Timeout.setChecked(true);

                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("save2",MODE_PRIVATE).edit();
                    editor.putBoolean("value2",false);
                    editor.apply();
                    Screen_Timeout.setChecked(false);

                }
            }
        });



    }


SwitchCompat Notification;

    public  void  Notification(){


        // Data Saver Button
        Notification = (SwitchCompat) findViewById(R.id.Notification_btn);
        SharedPreferences sharedPreferences_not = getSharedPreferences("save3",MODE_PRIVATE);
        Notification.setChecked(sharedPreferences_not.getBoolean("value3", true));
         Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Notification.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save3",MODE_PRIVATE).edit();
                    editor.putBoolean("value3",true);
                    editor.apply();
                    Notification.setChecked(true);

                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("save3",MODE_PRIVATE).edit();
                    editor.putBoolean("value3",false);
                    editor.apply();
                     Notification.setChecked(false);

                }
            }
        });



    }



    SwitchCompat Notification_Sound;
    public  void  Notification_sound(){


        // Data Saver Button
        Notification_Sound = (SwitchCompat) findViewById(R.id.Notification_sound_btn);
        SharedPreferences sharedPreferences_Sound = getSharedPreferences("save4",MODE_PRIVATE);
        Notification_Sound.setChecked(sharedPreferences_Sound.getBoolean("value4", true));
        Notification_Sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Notification_Sound.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save4",MODE_PRIVATE).edit();
                    editor.putBoolean("value4",true);
                    editor.apply();
                    Notification_Sound.setChecked(true);

                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("save4",MODE_PRIVATE).edit();
                    editor.putBoolean("value4",false);
                    editor.apply();
                    Notification_Sound.setChecked(false);

                }
            }
        });



    }


    SwitchCompat Auto_complete;

    public  void  Auto_Complete(){


        // Data Saver Button
        Auto_complete = (SwitchCompat) findViewById(R.id.Auto_complete_btn);
        SharedPreferences sharedPreferences_auto = getSharedPreferences("save5",MODE_PRIVATE);
        Auto_complete.setChecked(sharedPreferences_auto.getBoolean("value5", true));
        Auto_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Auto_complete.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save5",MODE_PRIVATE).edit();
                    editor.putBoolean("value5",true);
                    editor.apply();
                    Auto_complete.setChecked(true);

                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("save5",MODE_PRIVATE).edit();
                    editor.putBoolean("value5",false);
                    editor.apply();
                    Auto_complete.setChecked(false);

                }
            }
        });



    }


    public  void  Data_saver(){


        // Data Saver Button
        switchCompat = (SwitchCompat) findViewById(R.id.data_saver_btn);
        SharedPreferences sharedPreferences = getSharedPreferences("save1",MODE_PRIVATE);
        switchCompat.setChecked(sharedPreferences.getBoolean("value1", true));
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompat.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save1",MODE_PRIVATE).edit();
                    editor.putBoolean("value1",true);
                    editor.apply();
                    switchCompat.setChecked(true);

                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("save1",MODE_PRIVATE).edit();
                    editor.putBoolean("value1",false);
                    editor.apply();
                    switchCompat.setChecked(false);

                }
            }
        });



    }

    private void setUpToolbar() {

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

}