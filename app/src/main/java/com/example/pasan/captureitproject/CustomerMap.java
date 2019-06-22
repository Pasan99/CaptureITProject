package com.example.pasan.captureitproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class CustomerMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener {

    int PLACE_PICKER_REQUEST = 1;
    public static final String EXTRA_MESSAGE = "customer_map";
    private static final int REQUEST_CODE = 102;
    private GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private FloatingTextButton captureMeButton, cancelButton;
    private Animation bottomtoUp;
    private Animation fade;
    private LatLng userLoation;
    private String passing_UID;
    private float[] result_distance = new float[10];
    private HashMap users = new HashMap();
    private Dialog dialog;
    private String current_user_distance;
    private TextView searchbar;
    private String mylocation_marker_id;
    private LatLng latLng;
    private Marker onLocationMarker;


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        Toast.makeText(this, "I am CaptureIt customer", Toast.LENGTH_SHORT).show();

        bottomtoUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_page_anim_bottom);
        fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim1);

        captureMeButton = findViewById(R.id.captureMe_button);
        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setVisibility(View.GONE);
        searchbar = findViewById(R.id.map_search);

        searchbar.setInputType(InputType.TYPE_NULL);

        dialog = new Dialog(CustomerMap.this);
        dialog.setContentView(R.layout.user_information_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(200)));
        dialog.getWindow().setDimAmount(30);
        ConstraintLayout dialog_layout = dialog.findViewById(R.id.userInformation);
        dialog_layout.setAnimation(fade);


        // request permission
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // ask permissions here using below code
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // mylocation button
        @SuppressLint("ResourceType") ImageView btnMyLocation = (ImageView) ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(2);
        btnMyLocation.setImageResource(R.drawable.ic_navigation_black_24dp);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                btnMyLocation.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 300, 60, 300);


        captureMeButton.setAnimation(bottomtoUp);
        cancelButton.setAnimation(fade);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference locationDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Location").child("currentLocation");
                locationDb.removeValue();
                cancelButton.setVisibility(View.GONE);
                boolean success = gMap.setMapStyle(new MapStyleOptions(getResources()
                        .getString(R.string.style_json2)));
                gMap.clear();
                onLocationMarker.setVisible(true);
           }
        });

        captureMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference locationDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Location").child("currentLocation");
                addMarkers();
                locationDb.child("Latitude").setValue(mLastLocation.getLatitude());
                locationDb.child("Longitude").setValue(mLastLocation.getLongitude());
                cancelButton.setVisibility(View.VISIBLE);
                onLocationMarker.setVisible(false);
            }
        });

        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPlace();
            }
        });


    }

    private void pickPlace() {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            startActivityForResult(intentBuilder.build(this), PLACE_PICKER_REQUEST);

        }catch (Exception i){
            Toast.makeText(this, i.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST ){
            if (requestCode == RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;
        buildGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (gMap != null) {
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);

        }


    }

    void addMarkers(){
        DatabaseReference location = FirebaseDatabase.getInstance().getReference().child("Available Users");
        location.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.child("Latitude").exists() && dataSnapshot.child("Longitude").exists()) {
                    double lat = Double.parseDouble(dataSnapshot.child("Latitude").getValue().toString());
                    double longi = Double.parseDouble(dataSnapshot.child("Longitude").getValue().toString());

                    try {
                        LatLng sydney = new LatLng(lat, longi);
                        if ( dataSnapshot.child("profileImage").exists() && dataSnapshot.child("userName").exists()) {
                            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),lat, longi, result_distance);
                            String result = String.format("%.02f", result_distance[0]/1000);
                            if ( result_distance[0] < 5000 ) {
                                users.put(dataSnapshot.child("userName").getValue().toString(), dataSnapshot.getKey());
                                gMap.addMarker(new MarkerOptions().position(sydney)
                                        .title(dataSnapshot.child("userName").getValue().toString()).snippet(result + "Km").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_photo))).showInfoWindow();
                            }
                        }

                    }catch (Exception i){
                        Toast.makeText(CustomerMap.this, i.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        gMap.setOnMarkerClickListener(this);

        boolean success = gMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));

        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .radius(5000); // In meters

        // Get back the mutable Circle
        Circle circle = gMap.addCircle(circleOptions);

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private int count = 0;
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

         latLng = new LatLng(location.getLatitude(), location.getLongitude());

        /*
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
*/
        if (count == 0 ) {
            onLocationMarker = gMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.customer_map)));
            count++;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getApplicationContext(), "Bye", Toast.LENGTH_LONG).show();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);

        }catch (Exception i){
            Toast.makeText(this, "Map" + i , Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference locationDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(userId).child("Location");

        GeoFire geoFire = new GeoFire(locationDb);
        geoFire.removeLocation("currentLocation");

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for ( int i = 0; i < users.size(); i++ ){
            if ( users.containsKey(marker.getTitle())){
                passing_UID = (String) users.get(marker.getTitle());
                current_user_distance = marker.getSnippet();
                openDialog();
                dialog.show();
            }
        }

        return false;
    }

    private void openDialog(){
        getUserInfo();
    }

    private void getUserInfo() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && Objects.equals(dataSnapshot.getKey(), passing_UID)){

                    TextView user_name_dialog = dialog.findViewById(R.id.user_name_dialog);
                    TextView distance = dialog.findViewById(R.id.Distance);
                    final ImageView user_image_dialog = dialog.findViewById(R.id.image_dialog);
                    Button see_packages = dialog.findViewById(R.id.chat_dialog);

                    distance.setText(current_user_distance);
                    user_name_dialog.setText(dataSnapshot.child("User Name").getValue().toString());
                    if ( dataSnapshot.child("Profile Image").exists()) {
                        Glide.with(CustomerMap.this).load(dataSnapshot.child("Profile Image").getValue().toString()).into(user_image_dialog);

                }
                see_packages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create intent object
                        Intent intent = new Intent(getApplicationContext(), userinfo.class);

                        // Animations
                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(user_image_dialog, "userImage");
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(CustomerMap.this, pairs);

                        // send message to next activity
                        intent.putExtra(EXTRA_MESSAGE, passing_UID);
                        // start activity
                        startActivity(intent, activityOptions.toBundle());
                    }
                });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(5);
        startActivity(new Intent(CustomerMap.this, BottomNavigation.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
