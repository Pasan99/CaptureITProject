package com.example.pasan.captureitproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class photoView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Intent intent = getIntent();
        String messege = intent.getStringExtra(recyclerviewAdapter.EXTRA_MESSAGE);

        PhotoView photoView = findViewById(R.id.photo_view);
        Glide.with(getApplicationContext()).load(messege).into(photoView);

    }
}
