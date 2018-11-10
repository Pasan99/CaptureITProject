package com.example.pasan.captureitproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class profilesetupAct extends AppCompatActivity {
    private Button confirm;
    private TextView names;
    private TextView age,mobile;
    private ImageView imageUser;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private ProgressDialog progressDialog;

    private String userId, userName, userAge, userMobile, profileImageUrl;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesetup);
        confirm = (Button) findViewById(R.id.confirm) ;
        imageUser = (ImageView) findViewById(R.id.imageView5) ;
        names = (EditText)findViewById(R.id.user_name);
        age = (EditText)findViewById(R.id.age);
        mobile = (EditText)findViewById(R.id.mobile);
        progressDialog = new ProgressDialog(this);


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getInfo();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                startActivity(new Intent(getApplicationContext(), navigation_interface.class));
                finish();
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

    @Override
    public void onBackPressed() {
        //Execute your code here
        startActivity(new Intent(getApplicationContext(), navigation_interface.class));
        finish();

    }

    private void getInfo() {
        //progressDialog.setMessage("Loading");
        //progressDialog.show();
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0 ){
                    //progressDialog.dismiss();
                    Map < String, Object > map = (Map <String, Object>) dataSnapshot.getValue();
                    if (map.get("Name") != null){
                        userName = map.get("Name").toString();
                        names.setText(userName);

                    }
                    if (map.get("Mobile") != null){
                        userMobile = map.get("Mobile").toString();
                        mobile.setText(userMobile);
                    }
                    if (map.get("Age") != null){
                        userAge = map.get("Age").toString();
                        age.setText(userAge);
                    }

                    if (map.get("profileImageUrl") != null){
                        //profileImageUrl is string
                        profileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(profileImageUrl).into(imageUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInfo() {
        userName = names.getText().toString();
        userAge = age.getText().toString();
        userMobile = mobile.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("Name", userName);
        userInfo.put("Age", userAge);
        userInfo.put("Mobile", userMobile);

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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    final String sdownload_url = String.valueOf(downloadUrl);

                    Log.d("The URL",sdownload_url);
                    //mCustomerDatabase.child("Connection").child("ProfilePic").setValue(sdownload_url);
                    Map userInfo = new HashMap();
                    userInfo.put("profileImageUrl",sdownload_url);
                    mCustomerDatabase.updateChildren(userInfo);
                    finish();
                    return;
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
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            imageUser.setImageURI(resultUri);
        }
    }
}
