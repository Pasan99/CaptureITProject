package com.example.pasan.captureitproject;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfileSetup6 extends AppCompatActivity {
    int PLACE_PICKER_REQUEST = 1;
    private Button placePick_button, place_button, done_button;
    private Switch availability_switch;
    private String users_latitude;
    private String users_longitude;
    private String location;
    private String user_name, profile_image;
    private String UID;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup6);

        placePick_button = findViewById(R.id.setup_location_setup6);
        place_button = findViewById(R.id.location_setup6);
        availability_switch = findViewById(R.id.availability_switch_setup6);
        done_button = findViewById(R.id.done_button_setup6);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UID = firebaseAuth.getCurrentUser().getUid();

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetails();
                doneProfileSetup();
                startActivity(new Intent(ProfileSetup6.this, BottomNavigation.class));
                finish();
            }
        });

        placePick_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPlace();

            }
        });

    }

    private void doneProfileSetup() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(userId);
        HashMap setup = new HashMap();
        setup.put("Setup", "Complete");
        data.updateChildren(setup);
    }

    private void addDetails() {
        if (availability_switch.isChecked()){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Available Users").child(UID);
            HashMap data = new HashMap();
            data.put("Availability", true);
            data.put("profileImageUrl" , profile_image );
            data.put("userName" , user_name );
            data.put("Latitude" , users_latitude );
            data.put("Longitude" , users_longitude );
            data.put("Location" , location );
            database.updateChildren(data);
        }
    }

    private void getDetails(){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");
        data.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getKey().equals(UID)){

                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    if (data.get("User Name") != null ){
                        user_name = data.get("User Name").toString();
                    }
                    if (dataSnapshot.child("Profile Image").exists() && data.get("profileImage") != null){
                        profile_image = data.get("Profile Image").toString();
                    }
                    addDetails();
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

    private void pickPlace()  {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (Exception i){
            Toast.makeText(this, i.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ){
            Place place = PlacePicker.getPlace(data, this);
            LatLng latLng = place.getLatLng();
            Double latitude = latLng.latitude;
            Double longitude = latLng.longitude;
            users_latitude = latitude.toString();
            users_longitude = longitude.toString();

            Toast.makeText(this, String.format("Place: %s", place.getName()), Toast.LENGTH_SHORT).show();
            location = String.format("Place: %s", place.getName());
            place_button.setText(location);
        }
    }
}
