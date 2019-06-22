package com.example.pasan.captureitproject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class settings extends AppCompatActivity implements View.OnClickListener {
    int PLACE_PICKER_REQUEST = 1;
    private Button done_button;
    private LinearLayout availability_layout, location_layout, logout_layout, remove_account_layout, location_showing_layout;
    private Animation bottom_to_top;
    private Switch availability_switch;
    private FirebaseAuth firebaseAuth;
    private boolean availability;
    private String currentUId;
    private Dialog dialog;
    private TextView popup_message;
    private Button popup_yes, popup_no;
    private LinearLayout linearLayout;
    private TextView place_text;
    private LatLng LatLng;
    private String users_latitude;
    private String users_longitude;
    private String location;
    private String users_profile_image;
    private String user_name;
    private String userType = "Photographers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getUserDetails();

        // animations
        bottom_to_top = AnimationUtils.loadAnimation(this, R.anim.anim3);

        // define views
        availability_switch = findViewById(R.id.availability_switch);
        availability_layout = findViewById(R.id.linearLayout5);
        location_layout = findViewById(R.id.linearLayout7);
        logout_layout = findViewById(R.id.linearLayout8);
        remove_account_layout = findViewById(R.id.linearLayout9);
        location_showing_layout = findViewById(R.id.linearLayout10);
        done_button = findViewById(R.id.settings_done_button);
        place_text = findViewById(R.id.place_text);

        // dialog box
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popup_message = dialog.findViewById(R.id.popup_message);
        popup_no = dialog.findViewById(R.id.popup_no_button);
        popup_yes = dialog.findViewById(R.id.popup_yes_button);
        linearLayout = dialog.findViewById(R.id.popup_layout);

        // animations
        availability_layout.setAnimation(bottom_to_top);
        location_layout.setAnimation(bottom_to_top);
        logout_layout.setAnimation(bottom_to_top);
        remove_account_layout.setAnimation(bottom_to_top);

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUId = firebaseAuth.getCurrentUser().getUid();

        // set the availability
        getAvailability();

        checkUserType();


        remove_account_layout.setOnClickListener(this);
        logout_layout.setOnClickListener(this);
        done_button.setOnClickListener(this);
        location_layout.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    void getAvailability(){
        final DatabaseReference imagedata = FirebaseDatabase.getInstance().getReference().child("Available Users");
        imagedata.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getKey().equals(currentUId)){
                    availability_switch.setChecked(true);
                    availability_layout.setBackgroundResource(R.drawable.follow_pressed);
                    if ( dataSnapshot.child("Location").exists()) {
                        String locate = dataSnapshot.child("Location").getValue().toString();
                        place_text.setText(locate);
                        location_showing_layout.setBackgroundResource(R.drawable.follow_pressed);
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
    }

    private void checkUserType() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User Type").child("Customers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if ( dataSnapshot.exists() && dataSnapshot.getKey().equals(currentUId)){
                    userType = "Customers";
                    availability_switch.setClickable(false);
                    location_layout.setClickable(false);
                    availability_layout.setVisibility(View.GONE);
                    location_showing_layout.setVisibility(View.GONE);
                    location_layout.setVisibility(View.GONE);
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



    public void removeUser() {
        dialog.show();
        popup_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_message.setText("Deleting Account...");
                final DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userType);
                usersDatabase.child(currentUId).removeValue();
                FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();

                users.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "User account deleted.", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user == null) {
                                        dialog.cancel();
                                        startActivity(new Intent(getApplicationContext(), registrationActivity.class));
                                        finish();
                                    }
                                }
                            }
                        });
            }
        });

        popup_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_message.setText("Canceling Removing Account Successful");
                Toast.makeText(getApplicationContext(), "Canceling Removing Account Successful", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });


    }

    @Override
    public void onClick(View v) {
        // when you click logout button
        if ( v == logout_layout){
            popup_message.setText("Do you want to Log Out");
            dialog.show();
            popup_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        startActivity(new Intent(getApplicationContext(), registrationActivity.class));
                        finish();
                    }
                }
            });
            popup_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }

        // when you pressed done button
        if ( v == done_button ){
            availability = availability_switch.isChecked();
            if (availability == true ){
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Available Users");
                Map map = new HashMap();
                map.put("Availability", availability);
                map.put("profileImage", users_profile_image);
                map.put("userName", user_name);
                saveLocation();
                firebaseDatabase.child(currentUId).updateChildren(map);

            }
            else {
                final DatabaseReference dataBase = FirebaseDatabase.getInstance().getReference().child("Available Users");
                dataBase.child(currentUId).removeValue();
            }

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        // when you pressed remove account
        if ( v == remove_account_layout ){
            removeUser();
        }

        if ( v == location_layout){
            getPlace();
        }
    }


    // get place
    private void getPlace(){
        try{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (Exception i){
            Toast.makeText(this, "Not worked", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng = place.getLatLng();
                Double latitude = LatLng.latitude;
                Double longitude = LatLng.longitude;
                users_latitude = latitude.toString();
                users_longitude = longitude.toString();

                location = String.format("Place: %s", place.getName());
                place_text.setText(location);
            }
        }
    }

    private void saveLocation(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Available Users").child(currentUId);
        Map map = new HashMap();
        map.put("Latitude", users_latitude);
        map.put("Longitude", users_longitude);
        map.put("Location", location);
        database.updateChildren(map);
    }

    private  void getUserDetails(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(userId);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( dataSnapshot.exists() ){
                    if (dataSnapshot.child("Profile Image").getValue() != null ) {
                        users_profile_image = dataSnapshot.child("Profile Image").getValue().toString();
                    }
                    user_name = dataSnapshot.child("User Name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
