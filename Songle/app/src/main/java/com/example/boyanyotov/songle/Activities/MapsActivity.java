package com.example.boyanyotov.songle.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boyanyotov.songle.DataStructures.IconStyles;
import com.example.boyanyotov.songle.DataStructures.Placemark;
import com.example.boyanyotov.songle.Generators.HintGenerator;
import com.example.boyanyotov.songle.LevelData;
import com.example.boyanyotov.songle.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    static private String TAG = MapsActivity.class.getSimpleName();
    private static SharedPreferences settingsPreferences;
    private static SharedPreferences progressPreferences;
    private static SharedPreferences levelPreferences;
    private static SharedPreferences.Editor editor;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    //////////////////////////////////////////////////////////////
    /*                  GOOGLE MAPS API                         */
    //////////////////////////////////////////////////////////////
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = false;
    private Location mLastLocation;
    SupportMapFragment mapFragment;
    FrameLayout fr_maps;


    //////////////////////////////////////////////////////////////
    /*                          Resources                       */
    //////////////////////////////////////////////////////////////
    private String themeStr;
    private int rid;
    private String btnStr = "@drawable/btn_";


    //////////////////////////////////////////////////////////////
    /*                          Parameters                      */
    //////////////////////////////////////////////////////////////
    // Level Maps Stuff
    private List<Placemark> placemarks;
    private Map<String,IconStyles> iconStylesMap;

    // Level stats
    private int difficulty;
    private int hint_points;
    private int hint_cost;
    private boolean hint_bought;

    // Current Song
    private String songTitle;
    private String songArtist;
    private String songNumber;
    private String songLink;

    // Current song lyrics
    private int lyrics_rows;
    private HashMap<Integer,Integer> lyrics_row_words;
    private Map<String,String> lyricsMap;
    private Map<String,Boolean> lyricsFound;
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsPreferences = this.getSharedPreferences("Settings",MODE_PRIVATE);
        progressPreferences = this.getSharedPreferences("Progress",MODE_PRIVATE);
        levelPreferences    = this.getSharedPreferences("Level",MODE_PRIVATE);

        hint_bought = false;
        hint_points = 0;

        getParams();
        setViews();
        connectGUI();
        HintGenerator.getInstance().setGeneratorParams(difficulty,songTitle,songArtist,songLink);
        setLevelTxt();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        LocationSettingsRequest. Builder builder = new LocationSettingsRequest.Builder ()
                .addLocationRequest(mLocationRequest);



    }

    @Override
    protected void onStart() {
        super.onStart();
        applyTheme();
        setHintsOn();
        generateLyricsView();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    ///////////////////////////////////////////////////////////////////////
    /*                       General Functions                           */
    ///////////////////////////////////////////////////////////////////////
    Button openMapsBtn;
    Button openLyricsBtn;
    Button openLevelMenuBtn;
    Button backBtn;
    LinearLayout ll_maps_menu;
    LinearLayout ll_maps_activity;

    protected void setViews(){
        // General GUI Views
        setContentView(R.layout.activity_maps);
        backBtn = findViewById(R.id.backBtn);
        openLevelMenuBtn = findViewById(R.id.openLevelMenuBtn);
        openLyricsBtn= findViewById(R.id.openLyricsBtn);
        openMapsBtn  = findViewById(R.id.openMapsBtn);
        ll_maps_menu = findViewById(R.id.ll_maps_menu);
        ll_maps_activity = findViewById(R.id.ll_MapsActivity);

        // Level Maps GUI
        fr_maps = findViewById(R.id.fr_maps);

        // Level Menu GUI Views
        cl_levelmenu = findViewById(R.id.cl_levelmenu);
        levelArtistsTxt = findViewById(R.id.levelArtistTxt);
        levelNameTxt = findViewById(R.id.levelNameTxt);
        levelTitleTxt = findViewById(R.id.levelTitleTxt);
        guessBtn = findViewById(R.id.guessBtn);
        guessTxt = findViewById(R.id.guessTxt);
        hintPointsTxt = findViewById(R.id.hintPointsTxt);
        hintTypeTxt = findViewById(R.id.hintTypeTxt);
        hintCostTxt = findViewById(R.id.hintCostTxt);
        buyHintBtn = findViewById(R.id.buyBtn);


        // Level Lyrics GUI View
        ll_lyricsmenu = findViewById(R.id.ll_lyricsmenu);
        ll_lyrics = findViewById(R.id.ll_lyrics);
    }

    protected void connectGUI(){
        // Connect : General
        openLevelMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLevel();
            }
        });
        openMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps();
            }
        });
        openLyricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLyrics();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        // Connect : Level Menu
        guessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGuess();
            }
        });
        buyHintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyHint();
            }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void applyTheme(){
        // Apply theme: General
        if(settingsPreferences.getBoolean("DarkTheme",false)){
            ll_maps_activity.setBackgroundColor(Color.parseColor("#111111"));
            cl_levelmenu.setBackgroundColor(Color.parseColor("#111111"));
            ll_maps_menu.setBackgroundColor(Color.parseColor("#000000"));
            themeStr = "_dark";
        }
        else {
            ll_maps_activity.setBackgroundColor(Color.parseColor("#89bff0"));
            cl_levelmenu.setBackgroundColor(Color.parseColor("#89bff0"));
            ll_maps_menu.setBackgroundColor(Color.parseColor("#89bff0"));
            themeStr = "_light";
        }
        rid = getResources().getIdentifier( btnStr + "levels" + themeStr,null,getPackageName());
        backBtn.setBackgroundResource(rid);
        rid = getResources().getIdentifier( btnStr + "menu" + themeStr,null,getPackageName());
        openLevelMenuBtn.setBackgroundResource(rid);
        rid = getResources().getIdentifier( btnStr + "lyrics" + themeStr,null,getPackageName());
        openLyricsBtn.setBackgroundResource(rid);
        rid = getResources().getIdentifier( btnStr + "map" + themeStr,null,getPackageName());
        openMapsBtn.setBackgroundResource(rid);

        // Apply theme: Level menu
        rid = getResources().getIdentifier( btnStr + "guess" + themeStr,null,getPackageName());
        guessBtn.setBackgroundResource(rid);
        rid = getResources().getIdentifier( btnStr + "buy_hint" + themeStr,null,getPackageName());
        buyHintBtn.setBackgroundResource(rid);

    }

    private void getParams(){
        difficulty = progressPreferences.getInt("Difficulty",0);
        if(difficulty == 0) Log.e(TAG,"No param: difficulty");

        hint_points = progressPreferences.getInt("HintPoints",0);

        songNumber = levelPreferences.getString("LevelSongNumber",null);
        if(songNumber == null) Log.e(TAG,"No param: song number");

        songTitle  = levelPreferences.getString("LevelSongTitle",null);
        if(songTitle == null) Log.e(TAG,"No param: song name");

        songArtist = levelPreferences.getString("LevelSongArtist",null);
        if(songArtist == null) Log.e(TAG,"No param: song artist");

        songLink   = levelPreferences.getString("LevelSongLink",null);
        if(songLink == null) Log.e(TAG,"No param: song link");

        lyricsMap = LevelData.getInstance().getLyricsMap();
        lyrics_rows = LevelData.getInstance().getLyricsRows();
        lyrics_row_words = LevelData.getInstance().getLyricsCount();
        lyricsFound = new HashMap<>();

        placemarks = LevelData.getInstance().getPlacemarks();
        iconStylesMap = LevelData.getInstance().getIconStylesMap();

    }

    private void goBack(){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Return to levels");
        alertDialogBuilder.setMessage("Are you sure you want to return to levels. " +
                "All data progress for this level will be lost. " +
                "Maybe you wanted to go to the level menu to guess the song? ");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void openLevel(){
        fr_maps.setVisibility(View.GONE);
        ll_lyricsmenu.setVisibility(View.GONE);
        cl_levelmenu.setVisibility(View.VISIBLE);
        setLevelTxt();
    }

    private void openLyrics(){
        cl_levelmenu.setVisibility(View.GONE);
        fr_maps.setVisibility(View.GONE);
        ll_lyricsmenu.setVisibility(View.VISIBLE);
        generateLyricsView();
    }

    private void openMaps(){
        cl_levelmenu.setVisibility(View.GONE);
        ll_lyricsmenu.setVisibility(View.GONE);
        fr_maps.setVisibility(View.VISIBLE);
    }

    ///////////////////////////////////////////////////////////////////////
    /*                       Level Menu Func                             */
    ///////////////////////////////////////////////////////////////////////
    ConstraintLayout cl_levelmenu;
    TextView levelTitleTxt;
    TextView levelNameTxt;
    TextView levelArtistsTxt;
    EditText guessTxt;
    Button guessBtn;
    TextView hintPointsTxt;
    TextView hintTypeTxt;
    TextView hintCostTxt;
    Button buyHintBtn;

    private void setLevelTxt(){
        levelNameTxt.setText("LEVEL: SONG " + songNumber);
        if(hint_bought && difficulty == 2) levelTitleTxt.setText(songTitle);
        else levelTitleTxt.setText("Unknown");
        if(hint_bought && (difficulty == 3 || difficulty == 4))levelArtistsTxt.setText(songArtist);
        else levelArtistsTxt.setText("Unknown");

        hintTypeTxt.setText(HintGenerator.getInstance().generateType());
        hintCostTxt.setText(Integer.toString(HintGenerator.getInstance().generateHintCost()));
        hintPointsTxt.setText(Integer.toString(hint_points));
    }

    private void setHintsOn (){
        if (difficulty == 5) {
            hintPointsTxt.setVisibility(View.GONE);
            hintCostTxt.setVisibility(View.GONE);
            hintTypeTxt.setVisibility(View.GONE);
            buyHintBtn.setVisibility(View.GONE);
        }
        else {
            hintPointsTxt.setVisibility(View.VISIBLE);
            hintCostTxt.setVisibility(View.VISIBLE);
            hintTypeTxt.setVisibility(View.VISIBLE);
            buyHintBtn.setVisibility(View.VISIBLE);
        }
    }

    private void checkGuess(){
        String guessStr = guessTxt.getText().toString();
        Log.w(TAG,"guessStr = " + guessStr);
        Log.w(TAG,"title of song = " + songTitle);
        if ( guessStr.toLowerCase().equals(songTitle.toLowerCase())){
            Log.w(TAG,"SONG GUESSED");
            songGuessed();
        }
        else songNotGuessed();
    }

    private void songGuessed(){

        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Song guessed!");
        alertDialogBuilder.setMessage("Congrats! You guessed the song! " +
                "You earn 1 Hint Point for the correct answer." +
                "You can now continue to play other levels");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hint_points++;
                editor = progressPreferences.edit();
                editor.putInt("HintPoints",hint_points);
                editor.putBoolean("Song_" + Integer.parseInt(songNumber) + "_Passed",true);
                Log.e(TAG,"Song num = " + songNumber);
                editor.commit();
                onBackPressed();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void songNotGuessed(){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Wrong guess!");
        alertDialogBuilder.setMessage("Sorry, fella, your guess was wrong " +
                "Check for missing characters or the full name of the song (online). " +
                "Fetch more lyrics! You can do it! ");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void buyHint(){
        final int hint_cost = HintGenerator.getInstance().generateHintCost();
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Buy hint");
        if ( hint_points >= hint_cost) {

            alertDialogBuilder.setMessage("You currently have " + Integer.toString(hint_points) +
                    " Hint Points. Buying the hint will leave you with " +
                    Integer.toString(hint_points - hint_cost) +
                    " Hint Points. Are you sure you want to buy the hint?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    hint_bought = true;
                    hint_points = hint_points - hint_cost;
                    editor = progressPreferences.edit();
                    editor.putInt("HintPoints",hint_points);
                    editor.commit();
                    setLevelTxt();
                    showHint();
                    dialogInterface.cancel();
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }
        else {
            alertDialogBuilder.setMessage("You currently have " + Integer.toString(hint_points) +
                    " Hint Points. You cannot buy the hint!");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showHint() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("HINT");
        alertDialogBuilder.setMessage("The " + HintGenerator.getInstance().generateType() +
                " is " + HintGenerator.getInstance().generateHint());
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    ///////////////////////////////////////////////////////////////////////
    /*                           Lyrics Func                             */
    ///////////////////////////////////////////////////////////////////////
    LinearLayout ll_lyricsmenu;
    LinearLayout ll_lyrics;

    private void generateLyricsView(){
        ll_lyrics.removeAllViews();
        for(int i = 1; i< lyrics_rows; i++){
            String rowLyrics = "";

            for(int j = 1; j < lyrics_row_words.get(i);j++){
                String key = i + ":" + j;
                if(lyricsMap.get(key).equals("")){
                    continue;
                } else if(lyricsFound.containsKey(key)){
                    rowLyrics = rowLyrics + lyricsMap.get(key);
                } else rowLyrics += " _ ";
            }

            TextView rowTxt = new TextView(this);
            int id = i*i+i;
            rowTxt.setId(id);
            rowTxt.setTag(id);
            rowTxt.setTextColor(Color.parseColor("#ffffff"));
            rowTxt.setText(rowLyrics);
            rowTxt.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            ll_lyrics.addView(rowTxt);
        }
    }




    ///////////////////////////////////////////////////////////////////////
    /*                              Map Func                             */
    ///////////////////////////////////////////////////////////////////////


    protected void createLocationRequest() {
        // Set the parameters for the location request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); // preferably every 5 seconds
        mLocationRequest.setFastestInterval(1000); // at most every second
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Can we access the user’s current location?
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try { createLocationRequest(); }
        catch (java.lang.IllegalStateException ise) {
            System.out.println("IllegalStateException thrown [onConnected]");
        }
        // Can we access the user’s current location?
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    @Override
    public void onLocationChanged(Location current) {
        System.out.println(
                " [onLocationChanged] Lat/long now (" +
                        String.valueOf(current.getLatitude()) + "," +
                        String.valueOf(current.getLongitude()) + ")"
        );
        mLastLocation = current;
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(current.getLatitude(),current.getLongitude())));
    }

    @Override
    public void onConnectionSuspended(int flag) {
        System.out.println(" >>>> onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
            Toast.makeText(MapsActivity.this, "Connection failed. Please enable internet.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setMaxZoomPreference(21);
        mMap.setMinZoomPreference(19);

        try {
            // Visualise current position with a small blue circle
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {
            System.out.println("Security exception thrown [onMapReady]");
        }
            // Add ‘‘My location’’ button to the user interface
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        putMarkers();
    }

    private void putMarkers(){
        for(Placemark placemark : placemarks){

            String path = iconStylesMap.get(placemark.getDescription()).getLink();
            String str = "@drawable/" + placemark.getDescription();
            int rid = getResources().getIdentifier(str,null,getPackageName());
            if(mMap == null) Log.e(TAG,"MAP IS NULLLLL");
            mMap.addMarker(new MarkerOptions()
                    .position(placemark.getCoordinates())
                    .title(placemark.getName())
                    .snippet(placemark.getDescription())
                    .icon(BitmapDescriptorFactory.fromResource(rid)));

        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.i(TAG, "Marker clicked");

        LatLng myLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        Double distance = CalculationByDistance(myLocation, marker.getPosition());
        Log.i(TAG,"Distance = " + Double.toString(distance));


        if (distance <= 0.02) {
            // COLLECT MARKER
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder.setMessage("You collected the lyric: " + lyricsMap.get(marker.getTitle()));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Awesome",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            lyricsFound.put(marker.getTitle(), true);
                            marker.setVisible(false);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    return true;
    }

    private double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

}
