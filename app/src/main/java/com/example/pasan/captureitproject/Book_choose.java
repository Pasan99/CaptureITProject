package com.example.pasan.captureitproject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.protobuf.StringValue;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Book_choose extends AppCompatActivity {

    private CalendarView calendar;
    private Button time, location_picker, book_button;

    // time_dialog elements
    private Button done;
    private TimePicker timePicker;

    // confirm_dialog_elements
    private TextView dialog_message;
    private Button dialog_order_button, dialog_no_button;

    // value variables
    private int time_hours;
    private int time_minute;
    private int PLACE_PICKER_REQUEST = 1 ;
    private String latitude;
    private String longitude;
    private String location;
    private String[] UN_UI_UPP_OCC_MO_PKT_PR= new String[7];
    private int rn;

    //
    private String customerName;
    private String customerPP;
    private String customerId;
    private String customerMobile;
    Date currentDate;
    String selectedDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_choose);

        Random randomNumber = new Random();
        rn = 1 + randomNumber.nextInt(1000000000);


        // define elements
        time = findViewById(R.id.time_book);
        location_picker = findViewById(R.id.location_book);
        calendar = findViewById(R.id.calendar_book);
        book_button = findViewById(R.id.book_button_bc);

        // define Time dialog and its elements
        final Dialog time_dialog = new Dialog(this);
        time_dialog.setContentView(R.layout.select_time);
        done = time_dialog.findViewById(R.id.done_button_selectTime);
        timePicker = time_dialog.findViewById(R.id.time_timePicker);

        // define confirm dialog and its element
        final Dialog confirm_dialog = new Dialog(this);
        confirm_dialog.setContentView(R.layout.pop_message);
        dialog_message = confirm_dialog.findViewById(R.id.popup_message);
        dialog_order_button = confirm_dialog.findViewById(R.id.popup_yes_button);
        dialog_no_button = confirm_dialog.findViewById(R.id.popup_no_button);
        dialog_message.setText("Do you want to confirm the booking");
        dialog_order_button.setText("Book");
        confirm_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_order_button.setBackgroundResource(R.drawable.rectangle_button);
        confirm_dialog.setCancelable(false);


        // get the message from last activity  message - chose package
        Intent intent = getIntent();
        UN_UI_UPP_OCC_MO_PKT_PR = intent.getStringArrayExtra(BookingOne.EXTRA_MESSAGE);


        // click listeners
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_dialog.show();
            }
        });

        // done button on select time
        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                time_dialog.cancel();
                time_hours = timePicker.getHour();
                time_minute = timePicker.getMinute();
                time.setText(time_hours + ":" + time_minute + " H");
            }
        });

        location_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange( @NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year+"/"+month+1+"/"+dayOfMonth;
            }
        });

        book_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_message.setText("Do you want to confirm the booking\n\n\nDate : "+selectedDate+"\n\nTime : "+time_hours+":"+time_minute+"H" + "\n\n"+location + "\n\nPackage Price\nRs." + UN_UI_UPP_OCC_MO_PKT_PR[6] + ".00");
                confirm_dialog.show();
            }
        });

        // dialog box NO button
        dialog_no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_dialog.cancel();
            }
        });

        // dialog box YES button
        dialog_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });

        getCustomerDetails();

    }

    private void checkFields(){
        if ( selectedDate == null ){
            Toast.makeText(this, "Please select a Date", Toast.LENGTH_SHORT).show();
        }
        else if (location == null ){
            Toast.makeText(this, "Please add a location", Toast.LENGTH_SHORT).show();
        }
        else if (time_hours== 0 && time_minute == 0){
            Toast.makeText(this, "Please select a Time", Toast.LENGTH_SHORT).show();
        }
        else {
            addBookingDetailsOnCustomer();
            addBookingDetailsOnPhotographer();
            startActivity(new Intent(Book_choose.this, BottomNavigation.class));
            finish();
        }
    }

    private void addBookingDetailsOnPhotographer() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(UN_UI_UPP_OCC_MO_PKT_PR[1]).child("Bookings").child("Current").child(Integer.toString(rn));
        HashMap bookingData = new HashMap();
        bookingData.put("Name", customerName);
        bookingData.put("Id", customerId);
        bookingData.put("Profile Image", customerPP);
        bookingData.put("Mobile", customerMobile);
        bookingData.put("Occupation", UN_UI_UPP_OCC_MO_PKT_PR[3]);
        bookingData.put("Package Type", UN_UI_UPP_OCC_MO_PKT_PR[5]);
        bookingData.put("Package Price", UN_UI_UPP_OCC_MO_PKT_PR[6]);
        bookingData.put("Date", selectedDate);
        bookingData.put("Time", time_hours+":"+time_minute+"H");

        database.updateChildren(bookingData);
        addBookingStatus();

    }

    private void addBookingStatus(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Booking Status").child(Integer.toString(rn));
        HashMap status = new HashMap();
        status.put("Status", "Pending");
        database.updateChildren(status);
    }


    private void addBookingDetailsOnCustomer() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String UID = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(UID).child("Bookings").child("Current").child(Integer.toString(rn));
        HashMap bookingData = new HashMap();
        bookingData.put("Name", UN_UI_UPP_OCC_MO_PKT_PR[0]);
        bookingData.put("Id", UN_UI_UPP_OCC_MO_PKT_PR[1]);
        bookingData.put("Profile Image", UN_UI_UPP_OCC_MO_PKT_PR[2]);
        bookingData.put("Mobile", UN_UI_UPP_OCC_MO_PKT_PR[4]);
        bookingData.put("Occupation", UN_UI_UPP_OCC_MO_PKT_PR[3]);
        bookingData.put("Package Type", UN_UI_UPP_OCC_MO_PKT_PR[5]);
        bookingData.put("Package Price", UN_UI_UPP_OCC_MO_PKT_PR[6]);
        bookingData.put("Date", selectedDate);
        bookingData.put("Time", time_hours+":"+time_minute+"H");
        database.updateChildren(bookingData);

    }


    // start activity for choose location
    private void setLocation() {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            startActivityForResult(intentBuilder.build(this),PLACE_PICKER_REQUEST );
        } catch (Exception i){
            Toast.makeText(this, i.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // when you chose location from location choose activity / setLocation()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK){
            Place place = PlacePicker.getPlace(data, this);
            LatLng latLng = place.getLatLng();
            latitude = Double.toString(latLng.latitude);
            longitude = Double.toString(latLng.longitude);
            location = String.format("Place: %s", place.getName());
            location_picker.setText(location);

        }
    }

    private void getCustomerDetails(){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String currentUser = firebaseAuth.getCurrentUser().getUid();
        data.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getKey().equals(currentUser)){
                    if (dataSnapshot.child("User Name").exists()){
                        customerName = dataSnapshot.child("User Name").getValue().toString();
                    }
                        customerId = dataSnapshot.getKey();
                    if (dataSnapshot.child("Profile Image").exists()){
                        customerPP = dataSnapshot.child("Profile Image").getValue().toString();
                    }
                    else {
                        customerPP = "https://4.bp.blogspot.com/-zsbDeAUd8aY/US7F0ta5d9I/AAAAAAAAEKY/UL2AAhHj6J8/s1600/facebook-default-no-profile-pic.jpg";
                    }

                    if (dataSnapshot.child("Mobile").exists()){
                        customerMobile = dataSnapshot.child("Mobile").getValue().toString();
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
