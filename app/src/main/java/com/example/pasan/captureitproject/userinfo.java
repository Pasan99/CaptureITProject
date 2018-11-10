package com.example.pasan.captureitproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    private ArrayList<String> userNames = new ArrayList<>();
    private ArrayList<String> profileImageUrls = new ArrayList<>();
    private ArrayList<String> proffesions = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();

    private ImageView userImage, userImage2;
    private TextView userName, user_name_top;
    private TextView posts;
    private TextView followings;
    private TextView followers;
    private Button back_button;
    private Button follow_button;

    private DatabaseReference imagedata;

    private String userId;
    private String ImageUrl;
    private String imageUrl;
    private String message;
    private String name;
    private String proffession;
    private String posts_string;
    private String following_count_string;
    private String followers_count_string;
    private String current_user;
    private RelativeLayout relativeLayout;
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
        relativeLayout = (RelativeLayout) findViewById(R.id.user_info);
        back_button = findViewById(R.id.user_info_back);
        follow_button = findViewById(R.id.button);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUid = mAuth.getCurrentUser().getUid();

        message = intent.getStringExtra(recyclerviewAdapter.EXTRA_MESSAGE);

        DatabaseReference followers_database = FirebaseDatabase.getInstance().getReference().child("Users").child(message).child("Connections").child("Yep");

        followers_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getKey().equals(currentUid)){
                        follow_button.setBackgroundResource(R.drawable.follow_pressed);
                        follow_button.setText("Unfollow");
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

        uptoDown = AnimationUtils.loadAnimation(this, R.anim.fade);
        downtoTop = AnimationUtils.loadAnimation(this, R.anim.anim3);

        relativeLayout.setAnimation(uptoDown);
        recyclerView.setAnimation(downtoTop);

        // make progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        // define views
        userName = findViewById(R.id.profile_name);
        user_name_top = findViewById(R.id.user_name_top);
        posts = findViewById(R.id.postCount);
        userImage = findViewById(R.id.profile_image);
        userImage2 = findViewById(R.id.profile_image3);
        followers = findViewById(R.id.following_count);
        followings = findViewById(R.id.followersCount);

        // choose the firebase instances on selected user
        imagedata = FirebaseDatabase.getInstance().getReference().child("Users").child(message);

        // upload the photo to main image at the top of the intent
        setUserImage();

        // set all the images and data in recycerview
        setImages();

        posts_string = Integer.toString(post_count);
        posts.setText(posts_string);

        // get following count
        getFollowings();

        // get followers count
        getFollowers();
        if (Followers_count == 0 ){
            followers_count_string = Integer.toString(Followers_count);
            followers.setText(followers_count_string);
        }
        else {
            Followers_count--;
            followers_count_string = Integer.toString(Followers_count);
            followers.setText(followers_count_string);
        }


        // close progressdialog
        progressDialog.dismiss();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                finish();
            }
        });

    }

    public void setImages ( ){
        // set images on recycler view
        getInfo();
        // start recycler views/ feeds
        startRecyclerView();
    }



    // execute when back button pressed
    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();
        finish();

    }



    // set images on recycler view
    public void getInfo(){
        final DatabaseReference picodatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(message).child("Uploads");

        // add the captureit feed as the first component of recycler view
        imageUrls.add("https://firebasestorage.googleapis.com/v0/b/captureit-b09bc.appspot.com/o/cAPTUREIT.jpg?alt=media&token=1ad1c727-439d-4ab9-bc9b-ea2751e7abfc");
        userNames.add("CaptureIt");
        profileImageUrls.add("https://firebasestorage.googleapis.com/v0/b/captureit-b09bc.appspot.com/o/cAPTUREIT.jpg?alt=media&token=1ad1c727-439d-4ab9-bc9b-ea2751e7abfc");
        proffesions.add("None");
        // get user uploads
        picodatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if ( dataSnapshot.exists() ){
                    post_count++;
                    ImageUrl = dataSnapshot.child("Upload").getValue().toString();
                    imageUrls.add(ImageUrl);
                    userNames.add(name);
                    userIds.add(userId);
                    proffesions.add(proffession);
                    uploadUserImage();
                    profileImageUrls.add(imageUrl);
                    posts_string = Integer.toString(post_count);
                    posts.setText(posts_string);
                }
                else {

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


    // load profile image of every recycler view
    private void uploadUserImage() {
        imagedata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("profileImageUrl") != null){
                        imageUrl = map.get("profileImageUrl").toString();
                    }
                }
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
                    if (map.get("profileImageUrl") != null){
                        imageUrl = map.get("profileImageUrl").toString();
                        //names.setText(userMobile);
                        Glide.with(getApplication()).load(imageUrl).into(userImage);
                        //Glide.with(getApplication()).load(imageUrl).into(userImage2);

                    }
                    if (map.get("Name") != null){
                        name = map.get("Name").toString();
                        userName.setText(name);
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
        recyclerviewAdapter adapter = new recyclerviewAdapter(this, userNames, imageUrls, profileImageUrls, proffesions, userIds);
        rcView.setAdapter(adapter);
        rcView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void getFollowings(){
        final DatabaseReference yep_count = FirebaseDatabase.getInstance().getReference().child("Users").child(message).child("Connections").child("Yep");

        yep_count.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    following_count++;
                    following_count_string = Integer.toString(following_count);
                    followings.setText(following_count_string);
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

    public void getFollowers(){
        final DatabaseReference yep_count = FirebaseDatabase.getInstance().getReference().child("Users");

        yep_count.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() ){
                    current_user = dataSnapshot.getKey();
                    checkforFollowers(current_user);
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


    // check in users for followers
    public void checkforFollowers(String current_user){
        final DatabaseReference check_followers= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user).child("Connections").child("Yep");

        check_followers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && ( dataSnapshot.getKey().equals(message)) ){
                    Followers_count++;
                    followers_count_string = Integer.toString(Followers_count);
                    followers.setText(followers_count_string);
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
