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

        relativeLayout = findViewById(R.id.navigation_interface_layout);
        relativeLayout.getBackground().setAlpha(250);

        left_to_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right);
        right_to_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left);
        bottomtoTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim3);
        toptoBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim2);
        fade_off = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
        fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popup_message = dialog.findViewById(R.id.popup_message);
        popup_no = dialog.findViewById(R.id.popup_no_button);
        popup_yes = dialog.findViewById(R.id.popup_yes_button);
        linearLayout = dialog.findViewById(R.id.popup_layout);

        moAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        currentUId = moAuth.getCurrentUser().getUid();
        mAuth = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId);

        remove = findViewById(R.id.remove);
        logout = (Button) findViewById(R.id.logout);
        swipe = (Button) findViewById(R.id.swipe);
        check = (Button) findViewById(R.id.check);
        profileImg = (ImageView) findViewById(R.id.imageView4);
        names = (TextView) findViewById(R.id.name);
        passwords = findViewById(R.id.password);

        remove.getBackground().setAlpha(250);
        swipe.getBackground().setAlpha(250);
        check.getBackground().setAlpha(250);
        names.setTextColor(Color.WHITE);
        passwords.setTextColor(Color.WHITE);

        check.setAnimation(left_to_right);
        swipe.setAnimation(right_to_left);
        remove.setAnimation(left_to_right);
        logout.setAnimation(bottomtoTop);
        profileImg.setAnimation(toptoBottom);

        user = FirebaseAuth.getInstance().getCurrentUser();


        // there is no user signed in
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), registrationActivity.class));
        }
        // when user signed in
        if (user != null) {
            //ckeckUser();
            getInfo();

            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.

            String uid = user.getUid();

            //names.setText(uid);
            passwords.setText(email);

        }

        profileImg.setOnClickListener(this);
        logout.setOnClickListener(this);
        swipe.setOnClickListener(this);
        check.setOnClickListener(this);
        names.setOnClickListener(this);
        passwords.setOnClickListener(this);
        remove.setOnClickListener(this);

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
