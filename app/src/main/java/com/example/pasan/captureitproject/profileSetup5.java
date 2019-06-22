package com.example.pasan.captureitproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.protobuf.StringValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class profileSetup5 extends AppCompatActivity {
    private Button confirmButton;
    private TextView bp_photo_count, bp_start_tp, bp_end_tp, bp_diliver_time, bp_description, bp_price;
    private TextView sp_photo_count, sp_start_tp, sp_end_tp, sp_diliver_time, sp_description, sp_price;
    private TextView pp_photo_count, pp_start_tp, pp_end_tp, pp_diliver_time, pp_description, pp_price;

    private Switch bp_action_plan_switch, bp_shedule_posts_switch, bp_smm_switch;
    private Switch sp_action_plan_switch, sp_shedule_posts_switch, sp_smm_switch;
    private Switch pp_action_plan_switch, pp_shedule_posts_switch, pp_smm_switch;

    private ImageView cover_photo;
    private Spinner pack_type;

    private FirebaseAuth firebaseAuth;
    private String UID;
    private int rn;
    private Uri resultUri;
    private String occuptaion;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup5);

        // basic Package
        bp_photo_count = findViewById(R.id.bp_photo_count);
        bp_start_tp = findViewById(R.id.bp_time_period_start);
        bp_end_tp = findViewById(R.id.bp_time_period_end);
        bp_diliver_time = findViewById(R.id.bp_diliver_time);
        bp_description = findViewById(R.id.bp_package_description);
        bp_price = findViewById(R.id.bp_package_price);
        bp_action_plan_switch = findViewById(R.id.bp_action_plan_switch);
        bp_shedule_posts_switch = findViewById(R.id.bp_shedule_post_switch);
        bp_smm_switch = findViewById(R.id.bp_social_media_switch);

        // standard Package
        sp_photo_count = findViewById(R.id.sp_photo_count);
        sp_start_tp = findViewById(R.id.sp_time_period_start);
        sp_end_tp = findViewById(R.id.sp_time_period_end);
        sp_diliver_time = findViewById(R.id.sp_diliver_time);
        sp_description = findViewById(R.id.sp_package_description);
        sp_price = findViewById(R.id.sp_package_price);
        sp_action_plan_switch = findViewById(R.id.sp_action_plan_switch);
        sp_shedule_posts_switch = findViewById(R.id.sp_shedule_post_switch);
        sp_smm_switch = findViewById(R.id.sp_social_media_switch);

        // premium Package
        pp_photo_count = findViewById(R.id.pp_photo_count);
        pp_start_tp = findViewById(R.id.pp_time_period_start);
        pp_end_tp = findViewById(R.id.pp_time_period_end);
        pp_diliver_time = findViewById(R.id.pp_diliver_time);
        pp_description = findViewById(R.id.pp_package_description);
        pp_price = findViewById(R.id.pp_package_price);
        pp_action_plan_switch = findViewById(R.id.pp_action_plan_switch);
        pp_shedule_posts_switch = findViewById(R.id.pp_shedule_post_switch);
        pp_smm_switch = findViewById(R.id.pp_social_media_switch);

        confirmButton = findViewById(R.id.profileSetup5_done_button);
        pack_type = findViewById(R.id.package_occupation);
        cover_photo = findViewById(R.id.coverPhoto_setup5);

        firebaseAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        addItemsToSpinner();

        Random randomNumber = new Random();
        rn = 1 + randomNumber.nextInt(1000000000);

        cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        confirmButton.setEnabled(true);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCompleted();
            }
        });

    }


    private void addItemsToSpinner(){
            final String[] arrayList = new String[]{"Event Photographer", "Wedding Photographer", "Arial Photographer", "Travel Photographer", "Product Photographer", "Food Photographer", "Videographer"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pack_type.setAdapter(adapter);

    }


    private void doneProfileSetup() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(userId);
        HashMap setup = new HashMap();
        setup.put("Setup", "Five");
        data.updateChildren(setup);
    }


    @Override
    public void onBackPressed() {
    }

    private void checkCompleted(){
        if ( pack_type.getSelectedItem() != null ){
            if ( bp_photo_count.getText().toString().equals("") || bp_start_tp.getText().toString().equals("")
                    || bp_end_tp.getText().toString().equals("") || bp_diliver_time.getText().toString().equals("")
                    || bp_description.getText().toString().equals("") || bp_price.getText().toString().equals("")
                    || sp_photo_count.getText().toString().equals("") || sp_start_tp.getText().toString().equals("")
                    || sp_end_tp.getText().toString().equals("") || sp_diliver_time.getText().toString().equals("")
                    || sp_description.getText().toString().equals("") || sp_price.getText().toString().equals("")
                    || pp_photo_count.getText().toString().equals("") || pp_start_tp.getText().toString().equals("")
                    || pp_end_tp.getText().toString().equals("") || pp_diliver_time.getText().toString().equals("")
                    || pp_description.getText().toString().equals("") || pp_price.getText().toString().equals("") ) {
                Toast.makeText(this, "You must enter all the above field to complete package setup", Toast.LENGTH_LONG).show();
            }
            else{
                if (bp_photo_count.getText() == null || bp_start_tp.getText() == null || bp_end_tp.getText() == null
                        || bp_diliver_time.getText() == null || bp_description.getText() == null || bp_price.getText() == null
                        || sp_photo_count.getText() == null || sp_start_tp.getText() == null || sp_end_tp.getText() == null
                        || sp_diliver_time.getText() == null || sp_description.getText() == null || sp_price.getText() == null
                        || pp_photo_count.getText() == null || pp_start_tp.getText() == null || pp_end_tp.getText() == null
                        || pp_diliver_time.getText() == null || pp_description.getText() == null || pp_price.getText() == null){
                    Toast.makeText(this, "You must enter all the above field to complete package setup", Toast.LENGTH_LONG).show();
                }
                else {
                    addPackageDetails();
                    doneProfileSetup();
                    startActivity(new Intent(profileSetup5.this, ProfileSetup6.class));
                    finish();
                }
            }
        }
        else {
            Toast.makeText(this, "Occupation cannot be empty", Toast.LENGTH_LONG).show();
        }

    }

    private void addPackageDetails(){
        occuptaion = pack_type.getSelectedItem().toString();
        database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(UID).child("Packages").child(occuptaion).child(Integer.toString(rn));

        // basic Package
        HashMap basicPack = new HashMap();
        basicPack.put("Photo Count", bp_photo_count.getText().toString().trim());
        basicPack.put("Time Period Start", bp_start_tp.getText().toString().trim());
        basicPack.put("Time Period End", bp_end_tp.getText().toString().trim());
        basicPack.put("Deliver Time", bp_diliver_time.getText().toString().trim());
        basicPack.put("Description", bp_description.getText().toString().trim());
        basicPack.put("Action Plan", bp_action_plan_switch.isChecked());
        basicPack.put("Schedule Posts", bp_shedule_posts_switch.isChecked());
        basicPack.put("SMM", bp_smm_switch.isChecked());
        basicPack.put("Price", bp_price.getText().toString().trim());
        database.child("Basic Package").updateChildren(basicPack);

        // standard Package
        HashMap standardPack = new HashMap();
        standardPack.put("Photo Count", sp_photo_count.getText().toString().trim());
        standardPack.put("Time Period Start", sp_start_tp.getText().toString().trim());
        standardPack.put("Time Period End", sp_end_tp.getText().toString().trim());
        standardPack.put("Deliver Time", sp_diliver_time.getText().toString().trim());
        standardPack.put("Description", sp_description.getText().toString().trim());
        standardPack.put("Action Plan", sp_action_plan_switch.isChecked());
        standardPack.put("Schedule Posts", sp_shedule_posts_switch.isChecked());
        standardPack.put("SMM", sp_smm_switch.isChecked());
        standardPack.put("Price", sp_price.getText().toString().trim());
        database.child("Standard Package").updateChildren(standardPack);

        // premium Package
        HashMap premiumPack = new HashMap();
        premiumPack.put("Photo Count", pp_photo_count.getText().toString().trim());
        premiumPack.put("Time Period Start", pp_start_tp.getText().toString().trim());
        premiumPack.put("Time Period End", pp_end_tp.getText().toString().trim());
        premiumPack.put("Deliver Time", pp_diliver_time.getText().toString().trim());
        premiumPack.put("Description", pp_description.getText().toString().trim());
        premiumPack.put("Action Plan", pp_action_plan_switch.isChecked());
        premiumPack.put("Schedule Posts", pp_shedule_posts_switch.isChecked());
        premiumPack.put("SMM", pp_smm_switch.isChecked());
        premiumPack.put("Price", pp_price.getText().toString().trim());
        database.child("Premium Package").updateChildren(premiumPack);

        uploadPhoto();

    }

    private void uploadPhoto() {
        if ( resultUri != null ){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Packages").child(UID).child(Integer.toString(rn));
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
                    userInfo.put("Cover Photo",sdownload_url);
                    database.updateChildren(userInfo);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            cover_photo.setImageURI(resultUri);
        }
    }
}
