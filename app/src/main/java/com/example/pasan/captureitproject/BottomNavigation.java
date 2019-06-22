package com.example.pasan.captureitproject;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
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

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
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

public class BottomNavigation extends AppCompatActivity  {


    private ProgressDialog progressDialog;
    private ImageView navigation_profileImage;
    private FirebaseUser fbu;
    private String imageUrl;
    private String currentUser;
    private Animation downtoTop;
    private Animation toptoDown, fade;
    private RelativeLayout relativeLayout;
    private ConstraintLayout constraintLayout;
    private DatabaseReference profession;
    private String prof = "";
    private FirebaseUser user;
    private String user_profession;
    private String setup;
    private AHBottomNavigation bottomNavigation;
    private int bookingNotifyCount = 0;


    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        relativeLayout = findViewById(R.id.navigation_layout);
        constraintLayout = findViewById(R.id.container);

        downtoTop = AnimationUtils.loadAnimation(this, R.anim.main_page_anim_bottom);
        toptoDown = AnimationUtils.loadAnimation(this, R.anim.anim3);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.navigation);


        //relativeLayout.setAnimation(fade);

        fbu = FirebaseAuth.getInstance().getCurrentUser();

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setBookingNotification();

        int i = 0;
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
            checkProfession();
            setNavBar();



                Fragment fragmento = null;
                fragmento = new Feeds();
                loadFragment(fragmento);



        }


    }

    private void redirect(){
        if ( user_profession.equals("Photographer")){
            switch (setup){
                case "Two":
                    startActivity(new Intent(getApplicationContext(), profileSetup3.class));
                    finish();
                    break;

                case "Three":
                    startActivity(new Intent(getApplicationContext(), profileSetup4.class));
                    finish();
                    break;

                case "Four":
                    startActivity(new Intent(getApplicationContext(), profileSetup5.class));
                    finish();
                    break;

                case "Five":
                    startActivity(new Intent(getApplicationContext(), ProfileSetup6.class));
                    finish();
                    break;

                case "Complete":
                    final BottomNavigationView navigation = findViewById(R.id.navigation);

                    Fragment fragmento = null;
                    fragmento = new Feeds();
                    loadFragment(fragmento);
                    break;
            }
        }
    }

    private void checkProfession() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> datas = ( Map <String,Object> )dataSnapshot.getValue();
                if ( dataSnapshot.exists() && dataSnapshot.getKey().equals(currentUser)) {
                    if (datas.get("Setup") != null ){
                        user_profession = "Photographer";
                        setup = datas.get("Setup").toString();
                        bottomNavigation.disableItemAtPosition(1);
                        bottomNavigation.disableItemAtPosition(3);
                        bottomNavigation.setNotification("", 1);
                        redirect();
                    }
                }
                else {
                    user_profession  = "Customer";
                    bottomNavigation.enableItemAtPosition(1);
                    bottomNavigation.enableItemAtPosition(3);
                    bottomNavigation.setNotification("Nearby", 1);
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

    private void setNavBar(){
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.ic_home_black_24dp, R.color.colorPrimaryDark);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Map" ,R.drawable.map_logo, R.color.colorPrimaryDark);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Booking", R.drawable.ic_payment_black_24dp, R.color.colorPrimaryDark);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Favourites", R.drawable.ic_favorite_black_24dp, R.color.colorPrimaryDark);

        bottomNavigation.setAccentColor(Color.parseColor("#00AEEF"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        bottomNavigation.setForceTint(true);



// Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if ( position == 2 ) {
                    Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(5);
                    Fragment fragment = new Booking();
                    loadFragment(fragment);
                }
                if (position == 1 ){
                    Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(5);
                    Intent intent = new Intent(BottomNavigation.this, CustomerMap.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    finish();
                }
                if (position == 0 ){
                    Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(5);
                    Fragment fragment = new Feeds();
                    loadFragment(fragment);
                }

                if ( position == 3){
                    Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(5);
                    Fragment fragment = new Favourite();
                    loadFragment(fragment);
                }

                return true;
            }
        });
    }

    private void setBookingNotification(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(currentUser).child("Bookings").child("Current");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    bookingNotifyCount++;
                    Toast.makeText(BottomNavigation.this, "Hello", Toast.LENGTH_SHORT).show();
                    bottomNavigation.setNotification(bookingNotifyCount, 2);
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

    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.navigation_layout, fragment).commit();
        }
        return true;
    }


}
