package com.example.pasan.captureitproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Map;

import static com.example.pasan.captureitproject.Swipeeee.EXTRA_MESSAGE;

public class userinfo extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "";
    private static final String TAG = "userinfo";
    private ProgressDialog progressDialog;

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

    private String userType = "Photographers";
    private String userId;
    private String profileImageUrl;
    private String userName;
    private String currentUId;
    private String coverPhoto;
    private String mobile;

    private ImageView userImage;
    private TextView userName1;
    private TextView posts;
    private TextView followings;
    private TextView followers;


    private DatabaseReference imagedata;

    private String ImageUrl;
    private String imageUrl;
    private String message;
    private String name;
    private String proffession;
    private String posts_string;
    private String following_count_string;
    private String followers_count_string;
    private String current_user;
    private ConstraintLayout relativeLayout;
    private RecyclerView recyclerView;

    int post_count = 0;
    int following_count = 0;
    int Followers_count = 0;

    Animation uptoDown;
    Animation downtoTop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        // get the selected user from the previous intent
        Intent intent = getIntent();

        recyclerView = findViewById(R.id.userImages_recyclerView);
        relativeLayout = findViewById(R.id.user_info);
        checkUserType();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUid = mAuth.getCurrentUser().getUid();

        if ( intent.getStringExtra(recyclerviewAdapter.EXTRA_MESSAGE) == null ){
            if (intent.getStringExtra(search_best_photo_recycleView.EXTRA_MESSAGE) == null ){
                message = intent.getStringExtra(CustomerMap.EXTRA_MESSAGE);
            }
            else {
                message = intent.getStringExtra(search_best_photo_recycleView.EXTRA_MESSAGE);
            }
        }
        else{
            message = intent.getStringExtra(recyclerviewAdapter.EXTRA_MESSAGE);
        }




        uptoDown = AnimationUtils.loadAnimation(this, R.anim.fade);
        downtoTop = AnimationUtils.loadAnimation(this, R.anim.anim3);

        relativeLayout.setAnimation(uptoDown);
        recyclerView.setAnimation(downtoTop);

        // make progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        // define views
        userName1 = findViewById(R.id.profile_name);
        posts = findViewById(R.id.postCount);
        userImage = findViewById(R.id.profile_image);
        followers = findViewById(R.id.following_count);
        followings = findViewById(R.id.followersCount);

        // choose the firebase instances on selected user
        imagedata = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(message);

        // upload the photo to main image at the top of the intent
        setUserImage();

        // set all the images and data in recycerview
        setImages();

        posts_string = Integer.toString(post_count);
        posts.setText(posts_string);

        // close progressdialog
        progressDialog.dismiss();



    }

    public void setImages ( ){
        searchPhotographers();
        startRecyclerView();
    }



    // execute when back button pressed


    public void searchPhotographers() {
        final DatabaseReference user_database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");


        user_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                startRecyclerView();
                if (dataSnapshot.exists() && (!dataSnapshot.getKey().equals(currentUId)) && dataSnapshot.getKey().equals(message)) {
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

                    searchPackageNumber(profileImageUrl, userName, usersId, Mobile,  occupation, packageId, coverPhoto);
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
                    if (dataSnapshot.child("Photo Count").getValue() != null ) {
                        photo_count.add(dataSnapshot.child("Photo Count").getValue().toString());
                    }
                    if ( dataSnapshot.child("Price").getValue() != null ) {
                        prices.add(dataSnapshot.child("Price").getValue().toString());
                    }
                    if ( dataSnapshot.child("Description").getValue() != null ) {
                        descriptions.add(dataSnapshot.child("Description").getValue().toString());
                    }

                    packageNumbers.add(packageNumber);
                    mobiles.add(mobile);
                    packageTypes.add(packageType);
                    name_list.add(userName);
                    profile_image_urls.add(profileImageUrl);
                    imageUrls.add(coverPhoto);
                    userIds.add(usersId);
                    occupations.add(occupation);
                    startRecyclerView();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserType() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String currentUser = firebaseAuth.getCurrentUser().getUid();
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


    // upload the user's profile photo to main image at the top of the intent
    public void setUserImage(){
        imagedata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ) {
                    userId = dataSnapshot.getKey();
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Profile Image") != null){
                        imageUrl = map.get("Profile Image").toString();
                        //names.setText(userMobile);
                        Glide.with(getApplication()).load(imageUrl).into(userImage);
                    }
                    else {
                        imageUrl = "https://4.bp.blogspot.com/-zsbDeAUd8aY/US7F0ta5d9I/AAAAAAAAEKY/UL2AAhHj6J8/s1600/facebook-default-no-profile-pic.jpg";
                        Glide.with(getApplication()).load(imageUrl).into(userImage);
                    }
                    if (map.get("User Name") != null){
                        name = map.get("User Name").toString();
                        userName1.setText(name);
                        //user_name_top.setText(name);
                    }
                    if ( dataSnapshot.child("Profession").exists()) {
                        if (map.get("Profession") != null) {
                            proffession = map.get("Profession").toString();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void startRecyclerView(){
        Log.d(TAG, "startRecyclerView: hello");
        RecyclerView rcView = findViewById(R.id.userImages_recyclerView);
        recyclerviewAdapter adapter = new recyclerviewAdapter(this, name_list, imageUrls, profile_image_urls , userIds, photo_count, prices, descriptions, occupations, packageTypes, mobiles, userType, packageNumbers);
        rcView.setAdapter(adapter);
        rcView.setLayoutManager(new LinearLayoutManager(this));
    }





}
