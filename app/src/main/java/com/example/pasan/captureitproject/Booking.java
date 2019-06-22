
package com.example.pasan.captureitproject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;

public class Booking extends Fragment {
    private View v;
    private String userType = "Photographers";
    private String userId;


    private ArrayList<String> Names = new ArrayList<>();
    private ArrayList<String> Ids = new ArrayList<>();
    private ArrayList<String> ProfileImages  = new ArrayList<>();
    private ArrayList<String> Mobiles = new ArrayList<>();
    private ArrayList<String> Occupations = new ArrayList<>();
    private ArrayList<String> PackageTypes = new ArrayList<>();
    private ArrayList<String> PackagePrices = new ArrayList<>();
    private ArrayList<String> Dates = new ArrayList<>();
    private ArrayList<String> Times = new ArrayList<>();
    private ArrayList<String> Statuse = new ArrayList<>();
    private ArrayList<String> userTypes = new ArrayList<>();
    private ArrayList<String> bookingIds = new ArrayList<>();

    private Dialog dialog;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseAuth firebaseAuth;
        v = inflater.inflate(R.layout.activity_booking, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // define dialog box
        dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.loading_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ProgressBar progressBar = dialog.findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        //loading.getBackground().setAlpha(128);
        dialog.show();
        dialog.setCancelable(false);

        check();
        checkUserType();
        addCurrentBookings();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void checkUserType() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User Type").child("Customers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if ( dataSnapshot.exists() && Objects.equals(dataSnapshot.getKey(), userId)){
                    userType = "Customers";
                    check();
                    addCurrentBookings();

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

    private void check(){
        DatabaseReference bookings = FirebaseDatabase.getInstance().getReference().child("Users").child(userType);
        bookings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).hasChild("Bookings")){
                    // check
                }
                else {
                    dialog.cancel();
                    checkEmpty();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void addCurrentBookings(){
        DatabaseReference bookings = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(userId).child("Bookings").child("Current");
        bookings.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    startRecyclerView();
                    String bookingID = dataSnapshot.getKey();
                    String Name = Objects.requireNonNull(dataSnapshot.child("Name").getValue()).toString();
                    String Id = Objects.requireNonNull(dataSnapshot.child("Id").getValue()).toString();
                    String ProfileImage = Objects.requireNonNull(dataSnapshot.child("Profile Image").getValue()).toString();
                    String Mobile = Objects.requireNonNull(dataSnapshot.child("Mobile").getValue()).toString();
                    String Occupation = Objects.requireNonNull(dataSnapshot.child("Occupation").getValue()).toString();
                    String PackageType = Objects.requireNonNull(dataSnapshot.child("Package Type").getValue()).toString();
                    String PackagePrice = Objects.requireNonNull(dataSnapshot.child("Package Price").getValue()).toString();
                    String Date = Objects.requireNonNull(dataSnapshot.child("Date").getValue()).toString();
                    String Time = Objects.requireNonNull(dataSnapshot.child("Time").getValue()).toString();
                    getStatus(bookingID, Name, Id, ProfileImage, Mobile, Occupation, PackageType, PackagePrice, Date, Time);

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

    public void getStatus(final String bookingID, final String Name, final String Id, final String ProfileImage, final String Mobile, final String Occupation, final String PackageType, final String PackagePrice, final String Date, final String Time){
        final DatabaseReference status = FirebaseDatabase.getInstance().getReference().child("Booking Status");
        status.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && Objects.equals(dataSnapshot.getKey(), bookingID)){
                    dialog.cancel();
                    startRecyclerView();
                    Statuse.add(Objects.requireNonNull(dataSnapshot.child("Status").getValue()).toString());
                    bookingIds.add(bookingID);
                    Names.add(Name);
                    Ids.add(Id);
                    ProfileImages.add(ProfileImage);
                    Mobiles.add(Mobile);
                    Occupations.add(Occupation);
                    PackageTypes.add(PackageType);
                    PackagePrices.add(PackagePrice);
                    Dates.add(Date);
                    Times.add(Time);
                    userTypes.add(userType);
                    startRecyclerView();
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

    public void startRecyclerView() {
            RecyclerView feeds_recyclerView = v.findViewById(R.id.currentBooking_rv);
            BookingElementsAdapter adapter = new BookingElementsAdapter(getContext() , Names , Ids,  ProfileImages ,  Mobiles , Occupations , PackageTypes , PackagePrices , Dates , Times, Statuse, userTypes, bookingIds );
            feeds_recyclerView.setAdapter(adapter);
            feeds_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            //Toast.makeText(getContext(), "START RECYCLE VIEW " + ex.getMessage(), Toast.LENGTH_SHORT).show();



    }
    private void checkEmpty(){

            Vibrator vb = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert vb != null;
        vb.vibrate(8);
            Animation bounce = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
            final Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.popup_messege);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button okay = dialog.findViewById(R.id.button2);
            TextView messege = dialog.findViewById(R.id.popup_messege_text);
            String mes = "You don't have any booking details";
            messege.setText(mes);
            ConstraintLayout loading = dialog.findViewById(R.id.popup_biglayout);
            loading.setAnimation(bounce);
            dialog.show();

            okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

    }

}
