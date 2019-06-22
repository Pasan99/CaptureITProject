package com.example.pasan.captureitproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Map;

public class navigation_interface extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "navigation_interface";
    private Button logout, remove;
    private Button swipe, check;
    private StorageReference mStorageRef;
    DatabaseReference mAuth;
    private FirebaseAuth moAuth;
    private String currentUId;
    private String profileImageUrl;
    private ImageView profileImg;
    private String userName;
    private TextView names, passwords;
    private ProgressDialog progressDialog;
    private Animation left_to_right;
    private Animation right_to_left;
    private Animation bottomtoTop, toptoBottom, fade_off, fade;
    private FirebaseUser user;
    String email, password;
    private RelativeLayout relativeLayout;
    Dialog dialog;
    TextView popup_message;
    Button popup_yes, popup_no;
    LinearLayout linearLayout;

    //private DatabaseReference mCustomerDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_interface);
        //showToast();


        currentUId = moAuth.getCurrentUser().getUid();
        mAuth = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId);





        user = FirebaseAuth.getInstance().getCurrentUser();


        // there is no user signed in
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), registrationActivity.class));
        }
        // when user signed in
        if (user != null) {
            //ckeckUser();

            // Name, email address, and profile photo Url


            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.

            String uid = user.getUid();

            //names.setText(uid);
            passwords.setText(email);

        }


    }

    @Override
    public void onBackPressed() {
        relativeLayout.setAnimation(fade_off);
        startActivity(new Intent(getApplicationContext(), BottomNavigation.class));
        finish();

    }


    private void getInfo() {
        progressDialog.setMessage("Loading");
        progressDialog.show();
        mAuth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("profileImageUrl") != null) {
                        profileImageUrl = map.get("profileImageUrl").toString();
                        //names.setText(userMobile);
                        Glide.with(getApplication()).load(profileImageUrl).into(profileImg);
                        progressDialog.dismiss();

                    }
                    if (map.get("Name") != null) {
                        userName = map.get("Name").toString();
                        names.setText(userName);
                        progressDialog.dismiss();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == logout) {
            popup_message.setText("Do you want to Log Out");
            dialog.show();
            popup_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        relativeLayout.setAnimation(fade_off);
                        startActivity(new Intent(getApplicationContext(), registrationActivity.class));
                        finish();
                    }
                }
            });
            popup_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

        }
        if (v == swipe) {
            startActivity(new Intent(getApplicationContext(), userProfile.class));
            finish();

        }
        if (v == check) {
            startActivity(new Intent(getApplicationContext(), photoUpload.class));
            finish();

        }
        if (v == profileImg || v == names || v == passwords) {
            finish();
            startActivity(new Intent(getApplicationContext(), profilesetupAct.class));
        }

        if (v == remove) {
            popup_message.setText("Do you really want to delete your account");
            removeUser();
        }
    }


    public void removeUser() {
        dialog.show();
        popup_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_message.setText("Deleting Account...");
                final DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                usersDatabase.child(currentUId).removeValue();
                FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();

                users.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(navigation_interface.this, "User account deleted.", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user == null) {
                                        dialog.cancel();
                                        relativeLayout.setAnimation(fade_off);
                                        startActivity(new Intent(getApplicationContext(), registrationActivity.class));
                                        finish();
                                    }
                                }
                            }
                        });
            }
        });

        popup_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_message.setText("Canceling Removing Account Successful");
                Toast.makeText(navigation_interface.this, "Canceling Removing Account Successful", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });


    }


}
