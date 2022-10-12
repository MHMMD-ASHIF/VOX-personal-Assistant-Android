package com.vox.personalAssistant;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.voxpersonalassistant.R;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.scwang.wave.MultiWaveHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.ai_webza_tec.ai_method.checkForPreviousCallList;
import static com.example.ai_webza_tec.ai_method.clearContactListSavedData;
import static com.example.ai_webza_tec.ai_method.getContactList;
import static com.example.ai_webza_tec.ai_method.makeCall;
import static com.example.ai_webza_tec.ai_method.makeCallFromSavedContactList;


public class MainActivity extends AppCompatActivity {


    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ActionBarDrawerToggle actionBarDrawerToggle;



    private SpeechRecognizer recognizer;
    private TextView hello;
    private TextToSpeech tts;
    MultiWaveHeader waveHeader, waveFooter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();





        navigationView = findViewById(R.id.navigation);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {

                    case R.id.Chat:
                        Intent intent1 = new Intent(MainActivity.this , Launch_chat.class);

                        startActivity(intent1);
                        break;
                    case R.id.Settings:
                        Intent intent2 = new Intent(MainActivity.this , App_Settings.class);

                        startActivity(intent2);
                        break;


                    case R.id.app_info:
                        Intent intent4 = new Intent(MainActivity.this , App_Info.class);
                        startActivity(intent4);
                        break;






                    case  R.id.Share:{

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody =  "http://play.google.com/store/apps/detail?id="+getPackageName();
                        String shareSub = "Try now";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));

                    }
                    break;
                }
                return false;
            }
        });


        waveHeader = findViewById(R.id.wave);
        waveFooter = findViewById(R.id.wave2);
        waveHeader.setVelocity(5);
        waveHeader.setProgress(1);
        waveHeader.isRunning();
        waveHeader.setGradientAngle(45);
        waveHeader.setWaveHeight(40);
        waveHeader.setStartColor(Color.RED);
        waveHeader.setCloseColor(Color.CYAN);

        waveFooter.setVelocity(5);
        waveFooter.setProgress(1);
        waveFooter.isRunning();
        waveFooter.setGradientAngle(45);
        waveFooter.setWaveHeight(40);
        waveFooter.setStartColor(Color.MAGENTA);
        waveFooter.setCloseColor(Color.CYAN);





        Dexter.withContext(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        System.exit(0);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


        findById();
        inializeTextToSpeech();
        initializeResult();


    }




    private void setUpToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Nav, R.string.Nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();


    }


    private void inializeTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0) {
                    Toast.makeText(MainActivity.this, "Engine is not available", Toast.LENGTH_SHORT).show();
                    speak("check cellular data");


                } else {
                    String s = Functions.wishMe();
                   speak("I am Vox your personal assistant.." + s);


                }

            }
        });

    }

    private void speak(String msg) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void findById() {

        hello = (TextView) findViewById(R.id.hello);
    }

    private void initializeResult() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {

                    ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    Toast.makeText(MainActivity.this, "" + result.get(0), Toast.LENGTH_SHORT).show();
                    hello.setText(result.get(0));
                    response(result.get(0));

                }


                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }



    private void response(String msg) {


        String msgs = msg.toLowerCase();


            if (msgs.indexOf("hi") != -1) {
                speak("Hello sir how are you ?");

            } else if (msgs.indexOf("hello") != -1) {
                speak("Hello sir how are you ?");
            } else if (msgs.indexOf("fine") != -1) {
                speak("Happy to know that.. how may I help you?");
            } else if (msgs.indexOf("Iam not fine") != -1) {
                speak("please take care");
            } else if (msgs.indexOf("what's") != -1) {
                if (msgs.indexOf("your") != -1) {
                    if (msgs.indexOf("name") != -1) {
                        speak("I am Vox your personal Assistant...");
                    }
                }
                if (msgs.indexOf("time") != -1) {
                    if (msgs.indexOf("now") != -1) {
                        Date date = new Date();
                        String time = DateUtils.formatDateTime(this, date.getTime(), DateUtils.FORMAT_SHOW_TIME);
                        speak("The time now is " + time);
                    }
                }
                if (msgs.indexOf("today") != -1) {
                    if (msgs.indexOf("date") != -1) {
                        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
                        Calendar cal = Calendar.getInstance();
                        String today_date = df.format(cal.getTime());
                        speak("Today's date is " + today_date);
                    }
                }
            }








            else if (msgs.indexOf("open") != -1) {

                if (msgs.indexOf("browser") != -1) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                    startActivity(intent);

                } else if (msgs.indexOf("chrome") != -1) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("com.android.chrome"));
                    startActivity(intent);
                } else if (msgs.indexOf("youtube") != -1) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
                    speak("opening Youtube");
                    startActivity(intent);

                } else if (msgs.indexOf("facebook") != -1) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                    startActivity(intent);
                } else if (msgs.indexOf("instagram") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                    speak("opening instagram");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("whatsapp") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                    speak("opening whatsapp");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("mx player") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.mxtech.videoplayer.ad");
                    speak("opening mx player");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("contacts") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.contacts");
                    speak("opening contacts");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("amazon") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("in.amazon.mShop.android.shopping");
                    speak("opening amazon");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("binance") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.binance.dev");
                    speak("opening binance");
                    ctx.startActivity(i);
                }
                if (msgs.indexOf("calculator") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.calculator");
                    speak("opening calculator");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("calendar") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.calendar");
                    speak("opening calendar");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("camera") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.hmdglobal.camera2");
                    speak("opening camera");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("clock") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.deskclock");
                    speak("opening clock");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("files") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.nbu.files");
                    speak("opening files");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("drive") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.docs");
                    speak("opening drive");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("fm") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.evenwell.fmradio");
                    speak("opening fm");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("gmail") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                    speak("opening gmail");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("news") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.magazines");
                    speak("opening google news");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("google play games") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.play.games");
                    speak("opening google play games");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("play store") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.android.vending");
                    speak("opening play store");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("Google Pay") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.nbu.paisa.user");
                    speak("opening google pay");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("notes") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.keep");
                    speak("opening notes");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("lens") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.ar.lens");
                    speak("opening lens");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("LinkedIn") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.linkedin.android");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("map") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.maps");
                    speak("opening map");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("meet") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.meetings");
                    speak("opening google meet");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("messages") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.messaging");
                    speak("opening messages");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("netflix") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.netflix.mediaclient");
                    speak("opening netflix");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("olx") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.olx.southasia");
                    speak("opening olx");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("phone") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.dialer");
                    speak("opening recent calls");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("phonepe") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.phonepe.app");
                    speak("opening phonepe");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("photos") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.photos");
                    speak("opening photos");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("prime video") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.amazon.avod.thirdpartyclient");
                    speak("opening prime videos");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("telegram") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("org.telegram.messenger");
                    speak("opening telegram");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("settings") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.android.settings");
                    speak("opening system settings");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("wi-fi") != -1) {
                    openWifiSettings();

                } else if (msgs.indexOf("translator") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.translate");
                    speak("opening translator");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("true caller") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.truecaller");
                    speak("opening true caller");
                    ctx.startActivity(i);
                } else if (msgs.indexOf("bluetooth") != -1) {
                    Context ctx = this;
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.android.bluetooth");
                    ctx.startActivity(i);
                } else {
                    String searchQuery = msgs;
                    speak("here your results, lets check ");
                    searchQuery = searchQuery.replace("search", "");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY, searchQuery);
                    startActivity(search);
                }

            } else if (msgs.contains("search")) {

                String searchQuery = msgs;

                speak("here your results");
                searchQuery = searchQuery.replace("search", "");
                Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/#q=" + searchQuery));
                search.putExtra(SearchManager.QUERY, searchQuery);
                startActivity(search);
            } else if (msgs.contains("who is")) {

                String searchQuery = msgs;
                speak("here your results");
                searchQuery = searchQuery.replace("search", "");
                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                search.putExtra(SearchManager.QUERY, searchQuery);
                startActivity(search);
            } else if (msgs.contains("what is")) {

                String searchQuery = msgs;
                speak("here your results");
                searchQuery = searchQuery.replace("search", "");
                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                search.putExtra(SearchManager.QUERY, searchQuery);
                startActivity(search);
            } else if (msgs.contains("which ")) {

                String searchQuery = msgs;
                speak("here your results");
                searchQuery = searchQuery.replace("search", "");
                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                search.putExtra(SearchManager.QUERY, searchQuery);
                startActivity(search);
            } else if (msgs.contains("when ")) {

                String searchQuery = msgs;
                speak("here your results");
                searchQuery = searchQuery.replace("search", "");
                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                search.putExtra(SearchManager.QUERY, searchQuery);
                startActivity(search);
            } else if (msgs.contains("why ")) {

                String searchQuery = msgs;
                speak("here your results");
                searchQuery = searchQuery.replace("search", "");
                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                search.putExtra(SearchManager.QUERY, searchQuery);
                startActivity(search);

            } else if (msgs.contains("play")) {

                String searchQuery = msgs;
                searchQuery = searchQuery.replace("play", "");
                speak("here your result" + searchQuery);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
                intent.putExtra(SearchManager.QUERY, searchQuery);
                startActivity(intent);

            } else if (msgs.indexOf("destroy") != -1) {

                super.onDestroy();

            } else if (msgs.indexOf("alarm") != -1) {

            } else if (msgs.contains("wi-fi")) {
                openWifiSettings();

            } else if (msgs.contains("map") || (msgs.contains("locate"))) {

                String msg1 = extractWordFromString(msgs, "map" + "locate");
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + msg1);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            } else if (msgs.contains("navigate") || (msgs.contains("navigation"))) {

                String msg1 = extractWordFromString(msgs, "navigate" + "navigation");
                Uri uri = Uri.parse("http://www.google.com/#q=" + msg1);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            } else if (msgs.contains("music")) {
                playSearchArtist(extractWordFromString(msgs, "artist"));

            } else if (msgs.contains("dial to")) {
                dialPhoneNumber(extractPhoneNumbersFromString(msgs));

            } else if (msgs.contains("image") || msgs.contains("camera") || msgs.contains("photo")) {
                capturePhoto();

            } else if (msgs.contains("video")) {
                captureVideo();

            } else if (msgs.contains("contact")) {
                insertContact(extractWordFromString(msgs, "name"), extractEmailFromString(msgs));

            } else if (msgs.contains("mail")) {
                composeEmail(extractEmailFromString(msgs), extractWordFromString(msgs, "subject"));

            } else if (msgs.contains("alarm")) {
                int i = extractInt(msgs);
                int min = i % 100;
                int hr = i / 100;
                createAlarm(hr, min, extractWordFromString(msgs, "message"));

            } else if (msgs.contains("timer")) {
                int i = extractInt(msgs);
                startTimer(i, extractWordFromString(msgs, "message"));

            } else if (msgs.indexOf("call") != -1) {
                final String[] listName = {""};
                final String name = Functions.fetchName(msgs);
                Log.d("Name", name);


                Dexter.withContext(this)
                        .withPermissions(
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.CALL_PHONE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (checkForPreviousCallList(MainActivity.this)) {
                                speak(makeCallFromSavedContactList(MainActivity.this, name));
                            } else {
                                HashMap<String, String> list = getContactList(MainActivity.this, name);
                                if (list.size() > 1) {
                                    for (String i : list.keySet()) {
                                        listName[0] = listName[0].concat(".................................!" + i);

                                    }
                                    speak("which one sir ?.. there is " + listName[0]);
                                } else if (list.size() == 1) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        makeCall(MainActivity.this, list.values().stream().findFirst().get());
                                        clearContactListSavedData(MainActivity.this);
                                    }
                                } else {
                                    speak("No contact found");
                                    clearContactListSavedData(MainActivity.this);
                                }

                            }
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
            } else {
                String searchQuery = msgs;
                speak("here your results, lets check together");
                searchQuery = searchQuery.replace("search", "");
                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                search.putExtra(SearchManager.QUERY, searchQuery);
                startActivity(search);
            }
        }




    public void startRecording(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "3000");
        recognizer.startListening(intent);




    }



    private int extractInt(String input) {
        StringBuilder result = new StringBuilder();
        int a = 0;
        for (int i = 0; i < input.length(); i++) {
            Character chars = input.charAt(i);
            if (Character.isDigit(chars)) {
                result.append(chars);
            }
        }
        if (result.length() > 0) {
            a = Integer.parseInt(result.toString());
        }
        return a;
    }



    public void playSearchArtist(String artist) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        if (artist != null) {
            intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
            intent.putExtra(SearchManager.QUERY, artist);
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }



    public void openWifiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public String extractWordFromString(String input, String match) {
        String word = "";
        Scanner s = new Scanner(input);
        while (s.hasNext()) {
            if (s.next().equals(match)) {
                word = s.next();
            }
        }
        return word;
    }


    public String extractPhoneNumbersFromString(String input) {
        String num = "";
        Pattern pattern = Pattern.compile("\\d{4} \\d{3} \\d{3}");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            num = matcher.group(0);
        }
        return num;
    }


    public String extractEmailFromString(String input) {
        String email = "";
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(input);
        while (m.find()) {
            email = m.group();
        }
        return email;
    }


    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        if (phoneNumber != null) {
            intent.setData(Uri.parse("tel:" + phoneNumber));
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void composeEmail(String address, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        if (address != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, address);
        }
        if (subject != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void capturePhoto() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void captureVideo() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void createAlarm(int hour, int minutes, String message) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        if (message != null) {
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, message);
        }
        if (hour > 0) {
            intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        }
        if (minutes > 0) {
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void startTimer(int seconds, String message) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER);
        if (message != null) {
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, message);
        }
        if (seconds > 0) {
            intent.putExtra(AlarmClock.EXTRA_LENGTH, seconds);
        }
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void insertContact(String name, String email) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (name != null) {
            intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        }
        if (email != null) {
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }










}

