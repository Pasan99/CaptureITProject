package com.example.pasan.captureitproject;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Favourite extends Fragment {
    private View view;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private ArrayList<String> name_list = new ArrayList<>();
    private ArrayList<String> profile_image_urls = new ArrayList<>();
    private ArrayList<String> photo_count = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<String> prices = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> occupations = new ArrayList<>();
    private ArrayList<String> packageTypes = new ArrayList<>();
    private ArrayList<String> mobiles = new ArrayList<>();
    private ArrayList<String> packageNumbers = new ArrayList<>();

    private String profession;
    private String userId;
    private String profileImageUrl;
    private String userName;
    private String currentUId;
    private String coverPhoto;
    private String mobile;

    private Animation fade;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Dialog dialog;
    private ConstraintLayout loading;
    private ImageView profileImage;
    private String imageUrl;
    private String currentUser;

    private String userType = "Photographers";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {

            view = inflater.inflate(R.layout.activity_favourite, container, false);
            recyclerView = view.findViewById(R.id.favourite_packages_recycleview);

            // define dialog box
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.loading_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loading = dialog.findViewById(R.id.loading_linear_layout);
            ProgressBar progressBar = dialog.findViewById(R.id.spin_kit);
            FadingCircle fadingCircle = new FadingCircle();
            progressBar.setIndeterminateDrawable(fadingCircle);
            //loading.getBackground().setAlpha(128);
            dialog.show();
            dialog.setCancelable(false);

            // Animation
            fade = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            recyclerView.setAnimation(fade);


            // Firebase
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            currentUId = user.getUid();
            currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            checkUserType();

            // get user details
            searchPhotographers();


            // add details to recycleview
            startRecyclerView();



        } catch (Exception ex) {
            Toast.makeText(getContext(), "ON CREATE VIEW " + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void searchPhotographers() {
        final DatabaseReference user_database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");


        user_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                startRecyclerView();
                if (dataSnapshot.exists() && (!dataSnapshot.getKey().equals(currentUId))) {
                    if (dataSnapshot.child("Packages").exists()) {
                        userName = dataSnapshot.child("User Name").getValue().toString();
                        if (dataSnapshot.child("Profile Image").exists()) {
                            profileImageUrl = dataSnapshot.child("Profile Image").getValue().toString();
                        }
                        else {
                            profileImageUrl = "https://4.bp.blogspot.com/-zsbDeAUd8aY/US7F0ta5d9I/AAAAAAAAEKY/UL2AAhHj6J8/s1600/facebook-default-no-profile-pic.jpg";
                        }

                        if(dataSnapshot.child("Mobile").exists()){
                            mobile = dataSnapshot.child("Mobile").getValue().toString();
                        }
                        userId = dataSnapshot.getKey();
                        searchOccupations(userId, profileImageUrl, userName, mobile);
                        dialog.cancel();
                        startRecyclerView();
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

    private void searchOccupations(final String usersId, final String profileImageUrls, final String userNames, final String Mobile) {
        final DatabaseReference uploads_database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(usersId).child("Packages");

        uploads_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String occupation = dataSnapshot.getKey();
                    searchPackages(profileImageUrls, userNames, usersId, Mobile,  occupation);

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

    private void searchPackages(final String profileImageUrl, final String userName, final String usersId, final String Mobile,  final String occupation){
        final DatabaseReference packages_database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(usersId).child("Packages").child(occupation);
        packages_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    String packageId = dataSnapshot.getKey();
                    if (dataSnapshot.child("Cover Photo").exists()){
                        coverPhoto = dataSnapshot.child("Cover Photo").getValue().toString();
                    }
                    else {
                        coverPhoto = "https://blog.iron.io/wp-content/uploads/2017/11/LinkedIn-Cover.png";
                    }

                    searchForFavourite(profileImageUrl, userName, usersId, Mobile,  occupation, packageId, coverPhoto);
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

    private void searchForFavourite(final String profileImageUrl, final String userName, final String usersId, final String Mobile,  final String occupation, final String packageId, final String coverPhoto) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Likes");
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        database.keepSynced(true);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startRecyclerView();
                if (dataSnapshot.child(packageId).hasChild(firebaseAuth.getCurrentUser().getUid())){
                    searchPackageNumber(profileImageUrl, userName, usersId, Mobile,  occupation, packageId, coverPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchPackageNumber(final String profileImageUrl, final String userName, final String usersId, final String Mobile,  final String occupation, final String packageId, final String coverPhoto){
        final DatabaseReference package_Number_database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(usersId).child("Packages").child(occupation).child(packageId);
        package_Number_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    String packageType = dataSnapshot.getKey();
                    searchPackageType(profileImageUrl, userName, usersId, Mobile, occupation, packageType, packageId, coverPhoto);
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

    private void searchPackageType(final String profileImageUrl, final String userName, final String usersId, final String Mobile, final String occupation, final String packageType , final String packageNumber , final String coverPhoto){
        final DatabaseReference package_type_database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(usersId).child("Packages").child(occupation).child(packageNumber).child(packageType);

        package_type_database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals("Cover Photo")){
                    startRecyclerView();
                    if (dataSnapshot.child("Photo Count").getValue() != null ) {
                        photo_count.add(dataSnapshot.child("Photo Count").getValue().toString());
                    }
                    if ( dataSnapshot.child("Price").getValue() != null ) {
                        prices.add(dataSnapshot.child("Price").getValue().toString());
                    }
                    if ( dataSnapshot.child("Description").getValue() != null ) {
                        descriptions.add(dataSnapshot.child("Description").getValue().toString());
                    }

                    dialog.cancel();
                    packageNumbers.add(packageNumber);
                    mobiles.add(mobile);
                    packageTypes.add(packageType);
                    name_list.add(userName);
                    profile_image_urls.add(profileImageUrl);
                    imageUrls.add(coverPhoto);
                    userIds.add(usersId);
                    occupations.add(occupation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void startRecyclerView() {
        try {

            dialog.cancel();
            RecyclerView feeds_recyclerView = view.findViewById(R.id.favourite_packages_recycleview);
            recyclerviewAdapter adapter = new recyclerviewAdapter(getContext(), name_list, imageUrls, profile_image_urls , userIds, photo_count, prices, descriptions, occupations, packageTypes, mobiles, userType, packageNumbers);
            feeds_recyclerView.setAdapter(adapter);
            feeds_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        } catch (Exception ex) {
            Toast.makeText(getContext(), "START RECYCLE VIEW " + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }


    private void checkUserType() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User Type").child("Customers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if ( dataSnapshot.exists() && dataSnapshot.getKey().equals(currentUser)){
                    userType = "Customers";
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
