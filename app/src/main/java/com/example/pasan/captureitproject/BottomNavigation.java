package com.example.pasan.captureitproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private ProgressDialog progressDialog;
    private ImageView navigation_profileImage;
    private FirebaseUser fbu;
    private String imageUrl;
    private String currentUser;
    private Animation downtoTop;
    private Animation toptoDown, fade;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private ConstraintLayout constraintLayout;
    private DatabaseReference profession;
    private String prof = "";


    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);


        //Determine screen size
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Toast.makeText(this, "Large screen", Toast.LENGTH_LONG).show();
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Toast.makeText(this, "Normal sized screen", Toast.LENGTH_LONG).show();
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Toast.makeText(this, "Small sized screen", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Screen size is neither large, normal or small", Toast.LENGTH_LONG).show();
        }


        relativeLayout = findViewById(R.id.navigation_layout);
        linearLayout = findViewById(R.id.navigation_top);
        constraintLayout = findViewById(R.id.container);

        downtoTop = AnimationUtils.loadAnimation(this, R.anim.main_page_anim_bottom);
        toptoDown = AnimationUtils.loadAnimation(this, R.anim.anim3);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);

        linearLayout.setAnimation(toptoDown);
        relativeLayout.setAnimation(downtoTop);
        constraintLayout.setAnimation(fade);

        fbu = FirebaseAuth.getInstance().getCurrentUser();

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        int i = 0;
        navigation_profileImage = findViewById(R.id.navigation_profileImage);
        profession = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);


        profession.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Profession") != null) {
                        prof = map.get("Profession").toString();
                        Toast.makeText(BottomNavigation.this, "I am a "+ prof, Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (fbu == null) {
            startActivity(new Intent(getApplicationContext(), registrationActivity.class));
            finish();
            progressDialog.dismiss();
        }

        if (fbu != null) {

            final BottomNavigationView navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(this);

            Fragment fragmento = null;
            fragmento = new Feeds();
            loadFragment(fragmento);

            uploadUserImage();

            navigation_profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), navigation_interface.class));
                    finish();
                    finish();
                }
            });

        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_followers:
                fragment = new Swipeeee();
                return loadFragment(fragment);


            case R.id.navigation_map:
                if (prof.equals("CaptureIt Customer") || prof.equals("")) {
                    startActivity(new Intent(getApplicationContext(), CustomerMap.class));
                    return true;
                } else {
                    startActivity(new Intent(getApplicationContext(), photogrpher_map.class));
                    return true;
                }

            case R.id.navigation_home:
                fragment = new Feeds();
                return loadFragment(fragment);

        }

        return false;
    }

    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.navigation_layout, fragment).commit();
        }
        return true;
    }

    private void uploadUserImage() {

        final DatabaseReference imagedata = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        imagedata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("profileImageUrl") != null) {
                        imageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(imageUrl).into(navigation_profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
