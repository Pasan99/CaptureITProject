package com.example.pasan.captureitproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class photoDelete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_delete);

        Intent intent = getIntent();
        String messege = intent.getStringExtra(recyclerviewAdapter.EXTRA_MESSAGE);
        final String imageId = intent.getStringExtra(recyclerviewAdapter.EXTRA_MESSAGE_2);

        PhotoView photoView = findViewById(R.id.photo_view_delete);
        Glide.with(getApplicationContext()).load(messege).into(photoView);

        FloatingActionButton floatingActionButton = findViewById(R.id.delete_photo_button);

        FirebaseAuth moAuth = FirebaseAuth.getInstance();
        final String userID = moAuth.getCurrentUser().getUid();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference imageDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Uploads");
                imageDb.child(imageId).removeValue();
                finish();
            }
        });
    }
}
