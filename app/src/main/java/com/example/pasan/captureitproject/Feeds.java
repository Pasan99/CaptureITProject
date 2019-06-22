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
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Pair;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.Wave;
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
import java.util.Objects;
import java.util.StringJoiner;

public class Feeds extends Fragment implements View.OnClickListener {

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

    int check_count = 0;
    private Animation bottomtoTop;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Dialog dialog;
    private ConstraintLayout loading;
    private ImageView profileImage;
    public static String imageUrl = null;
    private String currentUser;
    private ConstraintLayout constraintLayout;
    private EditText searchbar;
    private String Like = "false";

    // search bar buttons
    private Button btn_all;
    private Button btn_basic;
    private Button btn_standard;
    private Button btn_premium;

    // search bar button states
    private boolean btn_all_state = true;
    private boolean btn_basic_state = false;
    private boolean btn_standard_state = false;
    private boolean btn_premium_state = false;
    private String selected_type = "all";

    private String userType = "Photographers";

    int pages = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {

            view = inflater.inflate(R.layout.activity_feeds, container, false);
            recyclerView = view.findViewById(R.id.userImages_recyclerView2);


            // layout elements
            searchbar = view.findViewById(R.id.search_bar);
            constraintLayout = view.findViewById(R.id.top_bar_main);
            profileImage = view.findViewById(R.id.navigation_profileImage);
            // search bar buttons
            btn_all = view.findViewById(R.id.btn_feeds_all);
            btn_basic = view.findViewById(R.id.btn_feeds_basic);
            btn_standard = view.findViewById(R.id.btn_feeds_standard);
            btn_premium = view.findViewById(R.id.btn_feeds_premium);

            // define dialog box
            dialog = new Dialog(Objects.requireNonNull(getContext()));
            dialog.setContentView(R.layout.loading_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loading = dialog.findViewById(R.id.loading_linear_layout);
            ProgressBar progressBar = dialog.findViewById(R.id.spin_kit);
            FadingCircle fadingCircle = new FadingCircle();
            progressBar.setIndeterminateDrawable(fadingCircle);
            //loading.getBackground().setAlpha(128);
            dialog.show();
            dialog.setCancelable(false);

            // Animation
            bottomtoTop = AnimationUtils.loadAnimation(getContext(), R.anim.anim3);
            recyclerView.setAnimation(bottomtoTop);


            // Firebase
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            currentUId = user.getUid();
            currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            checkUserType();


            // get user details
            searchPhotographers();

            if (imageUrl != null ) {
                Glide.with(Feeds.this).load(Feeds.imageUrl).into(profileImage);
            }
            else {
                // add profile image to top imageView in feeds activity
                uploadUserImage();
            }


            // add details to recycleview
            startRecyclerView();



            // Click Listners

            // search bar buttons
            btn_all.setOnClickListener(this);
            btn_basic.setOnClickListener(this);
            btn_standard.setOnClickListener(this);
            btn_premium.setOnClickListener(this);

            // others
            profileImage.setOnClickListener(this);
            searchbar.setOnClickListener(this);

            searchbar.setInputType(InputType.TYPE_NULL);



        } catch (Exception ex) {
            Toast.makeText(getContext(), "ON CREATE VIEW " + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void searchPhotographers() {
        final DatabaseReference user_database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");


        user_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //startRecyclerView();
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
                        //startRecyclerView();
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
                    assert packageType != null;
                    if (packageType.equals(selected_type)) {
                        searchPackageType(profileImageUrl, userName, usersId, Mobile, occupation, packageType, packageId, coverPhoto);
                    }
                    if (selected_type.equals("all")){
                        searchPackageType(profileImageUrl, userName, usersId, Mobile, occupation, packageType, packageId, coverPhoto);
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

    private void searchPackageType(final String profileImageUrl, final String userName, final String usersId, final String Mobile, final String occupation, final String packageType , final String packageNumber , final String coverPhoto){
        final DatabaseReference package_type_database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(usersId).child("Packages").child(occupation).child(packageNumber).child(packageType);

        package_type_database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals("Cover Photo")){
                    dialog.cancel();
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




    public void startRecyclerView() {
        try {

            RecyclerView feeds_recyclerView = view.findViewById(R.id.userImages_recyclerView2);
            recyclerviewAdapter adapter = new recyclerviewAdapter(getContext(), name_list, imageUrls, profile_image_urls , userIds, photo_count, prices, descriptions, occupations, packageTypes, mobiles, userType, packageNumbers);
            feeds_recyclerView.setAdapter(adapter);
            feeds_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        } catch (Exception ex) {
            Toast.makeText(getContext(), "START RECYCLE VIEW " + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    private void uploadUserImage() {

        final DatabaseReference imagedata = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(currentUser);
        imagedata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Profile Image") != null) {
                        imageUrl = map.get("Profile Image").toString();
                        Glide.with(Feeds.this).load(imageUrl).into(profileImage);
                        startRecyclerView();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserType() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User Type").child("Customers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if ( dataSnapshot.exists() && dataSnapshot.getKey().equals(currentUser)){
                    userType = "Customers";

                    // add profile image to top imageView in feeds activity
                    uploadUserImage();
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


    @Override
    public void onClick(View v) {
        if (v == profileImage){
            Intent intent = new Intent(getContext(), userProfile.class);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(profileImage, "profile_photo");
            pairs[1] = new Pair<View, String>(constraintLayout, "gradiant_layout");
            try {
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
                startActivity(intent, activityOptions.toBundle());
            } catch (Exception i) {
                Toast.makeText(getContext(), "Not Worked" + i, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }

        if (v == searchbar){
            Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(10);
            Intent intent = new Intent(getContext(), searchActivity.class);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(constraintLayout, "gradiant_layout");
            pairs[1] = new Pair<View, String>(searchbar, "search_bar");

            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
            startActivity(intent, activityOptions.toBundle());
        }


        //search bar buttons
        if (v == btn_all ){
            moveToFirstPage();
        }
        if (v == btn_basic){
            moveToSecondPage();
        }
        if (v == btn_standard){
            moveToThirdPage();
        }
        if (v == btn_premium){
            moveToFourthPage();
        }


    }


    private void moveToFirstPage(){
        btn_all_state = true;
        btn_basic_state = false;
        btn_standard_state = false;
        btn_premium_state = false;
        selected_type = "all";
        btn_basic.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_standard.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_premium.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_all.setBackgroundColor(getResources().getColor(R.color.search_bar_button_orange_color));
        imageUrls.clear();
        name_list.clear();
        profile_image_urls.clear();
        photo_count.clear();
        userIds.clear();
        prices.clear();
        descriptions.clear();
        occupations.clear();
        packageTypes.clear();
        mobiles.clear();
        packageNumbers.clear();
        searchPhotographers();
    }

    private void moveToSecondPage(){
        btn_all_state = false;
        btn_basic_state = true;
        btn_standard_state = false;
        btn_premium_state = false;
        selected_type = "Basic Package";
        btn_basic.setBackgroundColor(getResources().getColor(R.color.search_bar_button_orange_color));
        btn_standard.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_premium.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_all.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        imageUrls.clear();
        name_list.clear();
        profile_image_urls.clear();
        photo_count.clear();
        userIds.clear();
        prices.clear();
        descriptions.clear();
        occupations.clear();
        packageTypes.clear();
        mobiles.clear();
        packageNumbers.clear();
        searchPhotographers();
    }

    private void moveToThirdPage(){
        btn_all_state = false;
        btn_basic_state = false;
        btn_standard_state = true;
        btn_premium_state = false;
        selected_type = "Standard Package";
        btn_basic.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_standard.setBackgroundColor(getResources().getColor(R.color.search_bar_button_orange_color));
        btn_premium.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_all.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        imageUrls.clear();
        name_list.clear();
        profile_image_urls.clear();
        photo_count.clear();
        userIds.clear();
        prices.clear();
        descriptions.clear();
        occupations.clear();
        packageTypes.clear();
        mobiles.clear();
        packageNumbers.clear();
        searchPhotographers();
    }

    private void moveToFourthPage(){
        btn_all_state = false;
        btn_basic_state = false;
        btn_standard_state = false;
        btn_premium_state = true;
        selected_type = "Premium Package";
        btn_basic.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_standard.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        btn_premium.setBackgroundColor(getResources().getColor(R.color.search_bar_button_orange_color));
        btn_all.setBackgroundColor(getResources().getColor(R.color.search_bar_button_ash_color));
        imageUrls.clear();
        name_list.clear();
        profile_image_urls.clear();
        photo_count.clear();
        userIds.clear();
        prices.clear();
        descriptions.clear();
        occupations.clear();
        packageTypes.clear();
        mobiles.clear();
        packageNumbers.clear();
        searchPhotographers();
    }

}


