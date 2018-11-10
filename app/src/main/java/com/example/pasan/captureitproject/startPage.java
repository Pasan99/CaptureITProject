package com.example.pasan.captureitproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class startPage extends AppCompatActivity {
    private Button start;
    private ImageView image;
    private FirebaseUser user;

    String imageUrl = "https://firebasestorage.googleapis.com/v0/b/captureit-b09bc.appspot.com/o/startPage.png?alt=media&token=11908d2a-56b2-4a64-a7f8-1ca632230f29";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), BottomNavigation.class));
            this.finish();
        }

        else {
            startActivity(new Intent(getApplicationContext(), registrationActivity.class));
            this.finish();
        }


    }

}
