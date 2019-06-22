package com.example.pasan.captureitproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class profilesetupAct extends AppCompatActivity {
    private TextView names;
    private TextView language,mobile, description;
    private ImageView imageUser;
    private DatabaseReference mCustomerDatabase;
    private String userType = "Photographers";
    private String message;


    private String userId;
    private Uri resultUri;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesetup);
        Button confirm = findViewById(R.id.confirm);
        imageUser = findViewById(R.id.imageView5) ;
        names = findViewById(R.id.user_name);
        language = findViewById(R.id.age);
        mobile = findViewById(R.id.mobile);
        description = findViewById(R.id.description_profile_setup);

        Intent intent = getIntent();
        message = intent.getStringExtra(photo_regiActivity.EXTRA_MESSAGE_2);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        checkUserType();

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(userId);

        getInfo();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message == null ) {
                    saveUserInfo();
                    finish();
                }
                else if (message.equals("Hello")){
                    startActivity(new Intent(getApplicationContext(), BottomNavigation.class));
                    finish();
                }
            }
        });

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });


        //myRef.setValue("Hello, World!");
    }

    private void checkUserType() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User Type").child("Customers");
        database.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if ( dataSnapshot.exists() && Objects.equals(dataSnapshot.getKey(), userId)){
                    userType = "Customers";
                    mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(userId);

                    getInfo();
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



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getInfo() {
        // set the profile image
        Glide.with(profilesetupAct.this).load(Feeds.imageUrl).into(imageUser);

        // other details
        language.setText(userProfile.user_language);
        names.setText(userProfile.user_name);
        mobile.setText(userProfile.user_mobile);
        description.setText(userProfile.user_description);
    }

    private void saveUserInfo() {
        String userName = names.getText().toString();
        String language1 = language.getText().toString();
        String userMobile = mobile.getText().toString();
        String description1 = description.getText().toString();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("User Name", userName);
        userInfo.put("Language", language1);
        userInfo.put("Mobile", userMobile);
        userInfo.put("Description", description1);

        //mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mCustomerDatabase.updateChildren(userInfo);

        if ( resultUri != null ){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile Images").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()){
                        Toast.makeText(profilesetupAct.this, "Loading", Toast.LENGTH_SHORT).show();
                    }
                    Uri downloadUrl = urlTask.getResult();
                    final String sdownload_url = String.valueOf(downloadUrl);

                    Log.d("The URL",sdownload_url);
                    //mCustomerDatabase.child("Connection").child("ProfilePic").setValue(sdownload_url);
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("Profile Image",sdownload_url);
                    mCustomerDatabase.updateChildren(userInfo);
                    finish();
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
        }
        else {
            finish();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            assert data != null;
            resultUri = data.getData();
            imageUser.setImageURI(resultUri);
        }
    }
}
