package com.example.pasan.captureitproject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class photoUpload extends AppCompatActivity implements View.OnClickListener {

    private Button uploadButton;
    private Uri uril;
    private String userId;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    int i = 1;
    private String number;
    private int rn;
    private int imageCOunt;
    private String randNumber;
    private Button back;
    private TextView textView, upload;

    private Uri[] list = new Uri[12];
    private ImageView[] imageViews = new ImageView[12];
    private ProgressBar progressBar;
    int successCount = 0;
    int whileSuccessCount = 0;
    private  int precentage = 0;
    private Animation top_off, bottom_off, fade_off, fade, bottom, top;
    private ConstraintLayout constraintLayout;
    private ProgressBar uploading;
    private Dialog dialog;
    private UploadTask[] uploadTasks = new UploadTask[12];
    private TextView popupMessage;
    private int Add;
    LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        Random randomNumber = new Random();
        rn = 1 + randomNumber.nextInt(100000000);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        top_off = AnimationUtils.loadAnimation(this, R.anim.top_off);
        bottom_off = AnimationUtils.loadAnimation(this, R.anim.bottom_off);
        fade_off = AnimationUtils.loadAnimation(this, R.anim.fade_off);
        top = AnimationUtils.loadAnimation(this, R.anim.anim2);
        bottom = AnimationUtils.loadAnimation(this, R.anim.anim3);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.upload_photos);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loading = dialog.findViewById(R.id.loading_linear_layout);
        loading.getBackground().setAlpha(190);

        popupMessage = dialog.findViewById(R.id.popup_message);
        uploading = dialog.findViewById(R.id.upload_bar);
        uploading.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        textView = findViewById(R.id.savingText);
        upload = findViewById(R.id.textView5);
        constraintLayout = findViewById(R.id.uploadlayout);


        imageViews[0] = (ImageView) findViewById(R.id.image1);
        imageViews[1] = (ImageView) findViewById(R.id.image2);
        imageViews[2] = (ImageView) findViewById(R.id.image3);
        imageViews[3] = (ImageView) findViewById(R.id.image4);
        imageViews[4] = (ImageView) findViewById(R.id.image5);
        imageViews[5] = (ImageView) findViewById(R.id.image6);
        imageViews[6] = (ImageView) findViewById(R.id.image7);
        imageViews[7] = (ImageView) findViewById(R.id.image8);
        imageViews[8] = (ImageView) findViewById(R.id.image9);
        imageViews[9] = (ImageView) findViewById(R.id.image10);
        imageViews[10] = (ImageView) findViewById(R.id.image11);
        imageViews[11] = (ImageView) findViewById(R.id.image12);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        back = (Button) findViewById(R.id.buttonBak);

        constraintLayout.setAnimation(fade);
        uploadButton.setAnimation(bottom);
        upload.setAnimation(top);

        for (int i = 0; i < 12; i++) {
            imageViews[i].setOnClickListener(this);
        }
        uploadButton.setOnClickListener(this);
        back.setOnClickListener(this);


    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        uploadButton.setAnimation(bottom_off);
        upload.setAnimation(top_off);
        constraintLayout.setAnimation(fade_off);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public void onClick(View v) {

        for (int i = 0; i < 12; i++) {
            if (v == imageViews[i]) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, i + 1);
            }
        }
        if (v == uploadButton) {
            dialog.show();
            uploading.setVisibility(View.VISIBLE);
            textView.setText("Saving");
            imageCOunt = countImages();
            if (imageCOunt == 0 ){
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(100);
                textView.setText("Done");
                uploadButton.setAnimation(bottom_off);
                upload.setAnimation(top_off);
                constraintLayout.setAnimation(fade_off);
                dialog.cancel();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            if (imageCOunt < 6 ){
                Add = 16;
            }
            else {
                Add = 5;
            }
            uploadPhotos();
        }
        if (v == back) {
            uploadButton.setAnimation(bottom_off);
            upload.setAnimation(top_off);
            constraintLayout.setAnimation(fade_off);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    private void uploadPhotos()  {
        for ( int i = 0; i < 12; i++ ){
            if (list[i] != null ){
                precentage = 2;
                uploading.setProgress(precentage);
                rn++;
                randNumber = Integer.toString(rn);
                StorageReference uploadPhotos = FirebaseStorage.getInstance().getReference().child("Uploads_New").child(randNumber);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), list[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20 ,byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                uploadTasks[i] = uploadPhotos.putBytes(bytes);
                uploadTasks[i].addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                        while (!url.isSuccessful());
                        uploading.setProgress(precentage);
                        precentage = precentage + Add;
                        rn++;
                        randNumber = Integer.toString(rn);
                        whileSuccessCount++;
                        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Uploads").child(randNumber);
                        Uri downloadUrl = url.getResult();
                        String dwnUrl = String.valueOf(downloadUrl);
                        Map userInfo = new HashMap();
                        userInfo.put("Upload", dwnUrl);
                        mCustomerDatabase.updateChildren(userInfo);

                        if (imageCOunt==whileSuccessCount) {
                            //popupMessage.setText("Done");
                            textView.setText("Done");
                            uploading.setProgress(100);
                            uploadButton.setAnimation(bottom_off);
                            upload.setAnimation(top_off);
                            constraintLayout.setAnimation(fade_off);
                            dialog.cancel();
                            finish();
                        }
                    }
                });
                uploadTasks[i].addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(photoUpload.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (int i = 0; i < 12; i++) {
            if (requestCode == i + 1 && resultCode == Activity.RESULT_OK) {
                final Uri imageUri = data.getData();
                list[i] = imageUri;
                imageViews[i].setImageURI(list[i]);
            }
        }
    }


    public int countImages(){
        int count = 0;
        for (int i = 0; i < 12; i++ ){
            if ( list[i] != null ){
                count++;
            }
        }
        return count;
    }




}
