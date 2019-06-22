package com.example.pasan.captureitproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class profileSetup4 extends AppCompatActivity {
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup4);

        nextButton = findViewById(R.id.setup4_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneProfileSetup();
                startActivity(new Intent(getApplicationContext(), profileSetup5.class));
                finish();
            }
        });
    }

    private void doneProfileSetup() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(userId);
        HashMap setup = new HashMap();
        setup.put("Setup", "Four");
        data.updateChildren(setup);
    }

    @Override
    public void onBackPressed() {

    }
}
