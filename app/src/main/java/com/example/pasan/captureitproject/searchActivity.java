package com.example.pasan.captureitproject;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class searchActivity extends AppCompatActivity {

    private ArrayList<String> user_names = new ArrayList<>();
    private ArrayList<String> profile_images = new ArrayList<>();
    private ArrayList<String> ratings = new ArrayList<>();
    private ArrayList<String> distances = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();

    private TextView search_bar;
    private String keyword;
    private String profileImage;

    private RecyclerView rv_photographers;
    private ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // initialize elements
        search_bar = findViewById(R.id.search_bar_search);
        rv_photographers = findViewById(R.id.best_photographer_recycleview);
        constraintLayout = findViewById(R.id.constraintLayout);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(search_bar, InputMethodManager.SHOW_IMPLICIT);

        // get user details
        getBestPhotographersDetail();

        // add data to recycleview
        startRecycleview();

        // add data to nearby recycleview
        startRecycleViewNearby();

        // when text changed in search bar
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchResult();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        rv_photographers.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rv_photographers.setVisibility(View.GONE);
    }

    public void getBestPhotographersDetail() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                startRecycleview();
                if (dataSnapshot.exists() && dataSnapshot.child("User Name").exists() ) {

                    try {
                        String userName = dataSnapshot.child("User Name").getValue().toString();
                        if (dataSnapshot.child("Profile Image").exists()){
                            profileImage = dataSnapshot.child("Profile Image").getValue().toString();
                        }
                        else {
                            profileImage = "https://4.bp.blogspot.com/-zsbDeAUd8aY/US7F0ta5d9I/AAAAAAAAEKY/UL2AAhHj6J8/s1600/facebook-default-no-profile-pic.jpg";
                        }

                        String userId = dataSnapshot.getKey();
                        userIds.add(userId);
                        user_names.add(userName);
                        profile_images.add(profileImage);
                        ratings.add("Rating : Still Processing");
                        distances.add("Distance : still Processing");

                    } catch (Exception i) {
                        Toast.makeText(searchActivity.this, "This" + i, Toast.LENGTH_SHORT).show();
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


    public void startRecycleview() {
        RecyclerView recyclerView = findViewById(R.id.best_photographer_recycleview);
        search_best_photo_recycleView adapter = new search_best_photo_recycleView(searchActivity.this, profile_images, user_names, ratings, userIds);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void startRecycleViewNearby() {
        RecyclerView recyclerView = findViewById(R.id.nearbyPhotographer_recycleview);
        naerbyPhotographerRecycleview adapter = new naerbyPhotographerRecycleview(searchActivity.this, user_names, profile_images, ratings, distances);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    public void searchResult() {
        if (search_bar.getText() != null) {
            keyword = search_bar.getText().toString();
            userIds.clear();
            user_names.clear();
            profile_images.clear();
            ratings.clear();
            distances.clear();
            getsearchDetails();
            startRecycleview();
            startRecycleViewNearby();

        }
    }

    public void getsearchDetails() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                startRecycleview();
                startRecycleViewNearby();
                if (dataSnapshot.exists() && dataSnapshot.child("User Name").exists() ) {
                    String userName = dataSnapshot.child("User Name").getValue().toString();
                    String word1 = userName.toLowerCase();
                    String word2 = keyword.toLowerCase();
                    if ( word1.contains(word2) ) {
                        if (dataSnapshot.child("Profile Image").exists()) {
                            profileImage = dataSnapshot.child("Profile Image").getValue().toString();
                        }
                        else {
                            profileImage = "https://4.bp.blogspot.com/-zsbDeAUd8aY/US7F0ta5d9I/AAAAAAAAEKY/UL2AAhHj6J8/s1600/facebook-default-no-profile-pic.jpg";
                        }
                        String userId = dataSnapshot.getKey();
                        userIds.add(userId);
                        user_names.add(userName);
                        profile_images.add(profileImage);
                        ratings.add("Rating : Still Processing");
                        distances.add("Distance : still Processing");
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


}
