package com.example.pasan.captureitproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class profileSetup3 extends AppCompatActivity {
    private Switch eventPhSwitsh, weddingPhSwitch, arialPhSwitch, travelPhSwitch, productPhSwitch, foodPhSwitch, videographerSwitch;
    private Button confirmButton;
    private FirebaseAuth firebaseAuth;
    private String currentUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup3);

        eventPhSwitsh = findViewById(R.id.eventPhotographer_switch);
        weddingPhSwitch = findViewById(R.id.weddingPhotographerSwitch);
        arialPhSwitch = findViewById(R.id.arialPhotographerSwitch);
        travelPhSwitch = findViewById(R.id.travelPhotographySwitch);
        productPhSwitch = findViewById(R.id.productPhotographerSwitch);
        foodPhSwitch = findViewById(R.id.foodPhotographerSwitch);
        videographerSwitch = findViewById(R.id.videographerSwitch);
        confirmButton = findViewById(R.id.profileSetup3_nextButton);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfession();
                doneProfileSetup();
                startActivity(new Intent(profileSetup3.this, profileSetup4.class));
                finish();
            }
        });


    }

    private void doneProfileSetup() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(userId);
        HashMap setup = new HashMap();
        setup.put("Setup", "Three");
        data.updateChildren(setup);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User Type").child("Photographers");
        database.child(userId).setValue(true);
    }

    private void addProfession() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(currentUID).child("Professions");
        HashMap professions = new HashMap();

        if (eventPhSwitsh.isChecked()){
            professions.put("Event Photographer", true);
        }

        if (weddingPhSwitch.isChecked()){
            professions.put("Wedding Photographer", true);
        }

        if (arialPhSwitch.isChecked()){
            professions.put("Arial Photographer", true);
        }

        if (travelPhSwitch.isChecked()){
            professions.put("Travel Photographer", true);
        }

        if (productPhSwitch.isChecked()){
            professions.put("Product Photographer", true);
        }

        if (foodPhSwitch.isChecked()){
            professions.put("Food Photographer", true);
        }

        if (videographerSwitch.isChecked()){
            professions.put("Videographer", true);
        }

        database.updateChildren(professions);

    }
}
