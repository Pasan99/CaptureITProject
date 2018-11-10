package com.example.pasan.captureitproject;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class Feeds extends Fragment {

    private View view;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private ArrayList<String> name_list = new ArrayList<>();
    private ArrayList<String> profile_image_urls = new ArrayList<>();
    private ArrayList<String> profesions = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();

    private String profession;
    private String userId;
    private String profileImageUrl;
    private String userName;
    private String currentUId;

    int check_count = 0;
    private Animation bottomtoTop;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Dialog dialog;
    private LinearLayout loading;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {

            view = inflater.inflate(R.layout.activity_feeds, container, false);
            recyclerView = view.findViewById(R.id.userImages_recyclerView2);

            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.loading_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            loading = dialog.findViewById(R.id.loading_linear_layout);
            ProgressBar progressBar = dialog.findViewById(R.id.spin_kit);
            FadingCircle fadingCircle = new FadingCircle();
            progressBar.setIndeterminateDrawable(fadingCircle);

            loading.getBackground().setAlpha(128);
            dialog.show();

            bottomtoTop = AnimationUtils.loadAnimation(getContext(), R.anim.anim3);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            currentUId = user.getUid();
            countUsers();
            startRecyclerView();
            recyclerView.setAnimation(bottomtoTop);


        } catch (Exception ex) {
            Toast.makeText(getContext(), "ON CREATE VIEW " + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void countUsers() {
        final DatabaseReference user_database = FirebaseDatabase.getInstance().getReference().child("Users");
        imageUrls.add("https://firebasestorage.googleapis.com/v0/b/captureit-b09bc.appspot.com/o/cAPTUREIT.jpg?alt=media&token=1ad1c727-439d-4ab9-bc9b-ea2751e7abfc");
        name_list.add("CaptureIt");
        profile_image_urls.add("https://firebasestorage.googleapis.com/v0/b/captureit-b09bc.appspot.com/o/cAPTUREIT.jpg?alt=media&token=1ad1c727-439d-4ab9-bc9b-ea2751e7abfc");
        profesions.add("None");
        userIds.add("213123ewrwe");


        user_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                startRecyclerView();
                if (dataSnapshot.exists() && !dataSnapshot.child("Connections").child("Nope").hasChild(currentUId) && (!dataSnapshot.getKey().equals(currentUId)) ) {
                    if (dataSnapshot.child("Uploads").exists()) {
                        userName = dataSnapshot.child("Name").getValue().toString();

                        if (dataSnapshot.child("Profession").exists()) {
                            profession = dataSnapshot.child("Profession").getValue().toString();
                        } else {
                            profession = "None";
                        }

                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                        userId = dataSnapshot.getKey();
                        getUserUploads(userId, profileImageUrl, userName);
                        dialog.cancel();
                    }

                }
            }

            private void getUserUploads(final String usersId, final String profileImageUrls, final String userNames) {
                final DatabaseReference uploads_database = FirebaseDatabase.getInstance().getReference().child("Users").child(usersId).child("Uploads");

                uploads_database.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            profile_image_urls.add(profileImageUrls);
                            name_list.add(userNames);
                            profesions.add(profession);
                            userIds.add(usersId);
                            String imageUrl;
                            imageUrl = dataSnapshot.child("Upload").getValue().toString();
                            imageUrls.add(imageUrl);
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


    /*
    public void countUsers() {
        final DatabaseReference user_database = FirebaseDatabase.getInstance().getReference().child("Users");

        try {

            user_database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        //Toast.makeText(getContext(), user.child("Uploads").toString(), Toast.LENGTH_SHORT).show();
                        imageUrls.add("https://firebasestorage.googleapis.com/v0/b/captureit-b09bc.appspot.com/o/cAPTUREIT_feed.jpg?alt=media&token=02505fa6-bd9d-4bf8-aef4-da14a15a1fb3");
                        name_list.add(user.child("Name").getValue().toString());
                        profile_image_urls.add(user.child("profileImageUrl").getValue().toString());
                        profesions.add("FFFFFF");
                        userIds.add(user.getKey());

                    }

                    startRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception ex) {
            Toast.makeText(getContext(), "USER COUNT " + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
*/
    public void startRecyclerView() {
        try {

            RecyclerView feeds_recyclerView = view.findViewById(R.id.userImages_recyclerView2);
            recyclerviewAdapter adapter = new recyclerviewAdapter(getContext(), name_list, imageUrls, profile_image_urls, profesions, userIds);
            feeds_recyclerView.setAdapter(adapter);
            feeds_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        } catch (Exception ex) {
            Toast.makeText(getContext(), "START RECYCLE VIEW " + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
}
