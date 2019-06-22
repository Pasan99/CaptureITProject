package com.example.pasan.captureitproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookingOne extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "BookingOne";
    private Button bp_book, sp_book, pp_book;
    private TextView bp_photo_count, bp_time_period, bp_diliver_time, bp_price, sp_photo_count, sp_time_period, sp_diliver_time, sp_price, pp_photo_count, pp_time_period, pp_diliver_time, pp_price;
    private Switch bp_action_plan, bp_schedule_post, bp_social_media, sp_action_plan, sp_schedule_post, sp_social_media, pp_action_plan, pp_schedule_post, pp_social_media;
    private String[] UN_UI_UPP_OCC_MO_PKT_PR = new String[7];
    private String message[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_one);

        Intent intent = getIntent();
        message = intent.getStringArrayExtra(recyclerviewAdapter.EXTRA_MESSAGE);
        // add details to another array
        addToAnotherArray(message);

        bp_book = findViewById(R.id.bp_book_button);
        sp_book = findViewById(R.id.sp_book_button);
        pp_book = findViewById(R.id.pp_book_button);

        // basic package
        bp_photo_count = findViewById(R.id.bp_photo_count_book) ;
        bp_time_period = findViewById(R.id.bp_time_period_book);
        bp_diliver_time = findViewById(R.id.bp_diliver_time_book);
        bp_price = findViewById(R.id.bp_price_book);
        bp_action_plan = findViewById(R.id.bp_action_plan_switch_book);
        bp_schedule_post = findViewById(R.id.bp_schedul_posts_book);
        bp_social_media = findViewById(R.id.bp_socila_media_management_book);

        // standard package
        sp_photo_count = findViewById(R.id.sp_photo_count_book) ;
        sp_time_period = findViewById(R.id.sp_time_period_book);
        sp_diliver_time = findViewById(R.id.sp_diliver_time_book);
        sp_price = findViewById(R.id.sp_price_book);
        sp_action_plan = findViewById(R.id.sp_action_plan_switch_book);
        sp_schedule_post = findViewById(R.id.sp_schedule_posts_switch_book);
        sp_social_media = findViewById(R.id.sp_social_media_management_switch_book);

        // premium package
        pp_photo_count = findViewById(R.id.pp_photo_count_book) ;
        pp_time_period = findViewById(R.id.pp_time_period_book);
        pp_diliver_time = findViewById(R.id.pp_diliver_time_book);
        pp_price = findViewById(R.id.pp_price_book);
        pp_action_plan = findViewById(R.id.pp_action_plan_book);
        pp_schedule_post = findViewById(R.id.pp_schedule_posts_book);
        pp_social_media = findViewById(R.id.pp_social_media_management_book);

        getDetails();

        bp_action_plan.setClickable(false);
        bp_schedule_post.setClickable(false);
        bp_social_media.setClickable(false);
        sp_action_plan.setClickable(false);
        sp_schedule_post.setClickable(false);
        sp_social_media.setClickable(false);
        pp_action_plan.setClickable(false);
        pp_schedule_post.setClickable(false);
        pp_social_media.setClickable(false);

        bp_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UN_UI_UPP_OCC_MO_PKT_PR[5] = "Basic Package";
                UN_UI_UPP_OCC_MO_PKT_PR[6] = "50000";
                Intent intent = new Intent(getApplicationContext(), Book_choose.class);
                intent.putExtra(EXTRA_MESSAGE, UN_UI_UPP_OCC_MO_PKT_PR);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        sp_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UN_UI_UPP_OCC_MO_PKT_PR[5] = "Standard Package";
                UN_UI_UPP_OCC_MO_PKT_PR[6] = "50000";
                Intent intent = new Intent(getApplicationContext(), Book_choose.class);
                intent.putExtra(EXTRA_MESSAGE, UN_UI_UPP_OCC_MO_PKT_PR);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        pp_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UN_UI_UPP_OCC_MO_PKT_PR[5] = "Premium Package";
                UN_UI_UPP_OCC_MO_PKT_PR[6] = "50000";
                Intent intent = new Intent(getApplicationContext(), Book_choose.class);
                intent.putExtra(EXTRA_MESSAGE, UN_UI_UPP_OCC_MO_PKT_PR);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    private void addToAnotherArray(String[] message){
        for (int i = 0; i < 5; i++ ){
            UN_UI_UPP_OCC_MO_PKT_PR[i] = message[i];
        }
    }

    private void getDetails(){
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(UN_UI_UPP_OCC_MO_PKT_PR[1]).child("Packages").child(UN_UI_UPP_OCC_MO_PKT_PR[3]).child(message[5]);
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getKey().equals("Basic Package")){
                        bp_diliver_time.setText(dataSnapshot.child("Deliver Time").getValue().toString());
                        bp_photo_count.setText(dataSnapshot.child("Photo Count").getValue().toString());
                        bp_price.setText(dataSnapshot.child("Price").getValue().toString());

                        // Action plan
                        if (dataSnapshot.child("Action Plan").getValue().toString().equals("true")){
                            bp_action_plan.setChecked(true);
                        }
                        else {
                            bp_action_plan.setChecked(false);
                        }

                        // Social media Management
                        if (dataSnapshot.child("SMM").getValue().toString().equals("true")){
                            bp_social_media.setChecked(true);
                        }
                        else {
                            bp_social_media.setChecked(false);
                        }

                        // Schedule Posts
                        if (dataSnapshot.child("Schedule Posts").getValue().toString().equals("true")){
                            bp_schedule_post.setChecked(true);
                        }
                        else {
                            bp_schedule_post.setChecked(false);
                        }
                    }

                    if (dataSnapshot.getKey().equals("Standard Package")){
                        sp_diliver_time.setText(dataSnapshot.child("Deliver Time").getValue().toString());
                        sp_photo_count.setText(dataSnapshot.child("Photo Count").getValue().toString());
                        sp_price.setText(dataSnapshot.child("Price").getValue().toString());

                        // Action plan
                        if (dataSnapshot.child("Action Plan").getValue().toString().equals("true")){
                            sp_action_plan.setChecked(true);
                        }
                        else {
                            sp_action_plan.setChecked(false);
                        }

                        // Social media Management
                        if (dataSnapshot.child("SMM").getValue().toString().equals("true")){
                            sp_social_media.setChecked(true);
                        }
                        else {
                            sp_social_media.setChecked(false);
                        }

                        // Schedule Posts
                        if (dataSnapshot.child("Schedule Posts").getValue().toString().equals("true")){
                            sp_schedule_post.setChecked(true);
                        }
                        else {
                            sp_schedule_post.setChecked(false);
                        }
                    }

                    if (dataSnapshot.getKey().equals("Premium Package")){
                        pp_diliver_time.setText(dataSnapshot.child("Deliver Time").getValue().toString());
                        pp_photo_count.setText(dataSnapshot.child("Photo Count").getValue().toString());
                        pp_price.setText(dataSnapshot.child("Price").getValue().toString());

                        // Action plan
                        if (dataSnapshot.child("Action Plan").getValue().toString().equals("true")){
                            pp_action_plan.setChecked(true);
                        }
                        else {
                            pp_action_plan.setChecked(false);
                        }

                        // Social media Management
                        if (dataSnapshot.child("SMM").getValue().toString().equals("true")){
                            pp_social_media.setChecked(true);
                        }
                        else {
                            pp_social_media.setChecked(false);
                        }

                        // Schedule Posts
                        if (dataSnapshot.child("Schedule Posts").getValue().toString().equals("true")){
                            pp_schedule_post.setChecked(true);
                        }
                        else {
                            pp_schedule_post.setChecked(false);
                        }
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


