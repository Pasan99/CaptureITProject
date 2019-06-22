package com.example.pasan.captureitproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class profileSetupTwo extends AppCompatActivity {
    private ImageView profile_image;
    private TextView user_name, email, password,  description,  mobile_no, user_name_log, email_log, password_log, description_log, language_log, mobileno_log;
    private AutoCompleteTextView language;
    private Button confirm_button;
    private Uri profile_image_uri;
    private FirebaseAuth firebase_auth;
    private String user_id;
    private String user_image_url;
    private ProgressDialog progressDialog;
    private boolean result = false;

    private static final String[] LANGUAGES = new String[] {
            "English", "Sinhala", "Tamil"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_two);

        // pop up progress dialog
        progressDialog = new ProgressDialog(this);

        // initialize elements
        profile_image = findViewById(R.id.userImage_profileSetup2);
        user_name = findViewById(R.id.userName_setup2);
        email = findViewById(R.id.email_profileSetup2);
        password = findViewById(R.id.password_profileSetup2);
        description = findViewById(R.id.description_profileSetup2);
        language = (AutoCompleteTextView) findViewById(R.id.language_profileSetup2);
        mobile_no = findViewById(R.id.mobileNo_profileSetup2);
        confirm_button = findViewById(R.id.continueButton_profileSetup2);

        // initialize log element
        user_name_log = findViewById(R.id.user_name_log_setup2);
        description_log = findViewById(R.id.description_log_setup2);
        language_log = findViewById(R.id.language_log_setup2);
        email_log = findViewById(R.id.email_log_profileSetup2);
        mobileno_log = findViewById(R.id.mobileno_log_setup2);
        password_log = findViewById(R.id.password_log_profileSetup2);

        // firebase
        firebase_auth = FirebaseAuth.getInstance();


        // add listners for elements

        // add click listener for image
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        // --------------------------------------------------------------------------------------  // text changed listener start

        // password
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPassword();
                // check other fields are filled or not
                if ( checkDescription() && provideSuggestions() && mobile_no.getText() != null  && mobile_no.getText().length() > 9 && checkPassword() && checkEmail() ){
                    confirm_button.setEnabled(true);
                    confirm_button.setBackgroundResource(R.drawable.profile_setup_button);
                    confirm_button.setTextColor(Color.WHITE);
                }
                else {
                    confirm_button.setEnabled(false);
                    confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                    confirm_button.setTextColor(Color.GRAY);
                }
            }
        });

        // email
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEmail();
                // check other fields are filled or not
                if ( checkDescription() && provideSuggestions() && mobile_no.getText() != null  && mobile_no.getText().length() > 9 && checkPassword() && checkEmail() ){
                    confirm_button.setEnabled(true);
                    confirm_button.setBackgroundResource(R.drawable.profile_setup_button);
                    confirm_button.setTextColor(Color.WHITE);
                }
                else {
                    confirm_button.setEnabled(false);
                    confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                    confirm_button.setTextColor(Color.GRAY);
                }

            }
        });

        // user name
        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkUserName();
                // check other fields are filled or not
                if ( checkDescription() && provideSuggestions() && mobile_no.getText() != null  && mobile_no.getText().length() > 9 && checkPassword() && checkEmail() ){
                    confirm_button.setEnabled(true);
                    confirm_button.setBackgroundResource(R.drawable.profile_setup_button);
                    confirm_button.setTextColor(Color.WHITE);
                }
                else {
                    confirm_button.setEnabled(false);
                    confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                    confirm_button.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // description
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkDescription();
                // check other fields are filled or not
                if ( checkDescription() && provideSuggestions() && mobile_no.getText() != null  && mobile_no.getText().length() > 9 && checkPassword() && checkEmail() ){
                    confirm_button.setEnabled(true);
                    confirm_button.setBackgroundResource(R.drawable.profile_setup_button);
                    confirm_button.setTextColor(Color.WHITE);
                }
                else {
                    confirm_button.setEnabled(false);
                    confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                    confirm_button.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // language
        language.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                provideSuggestions();
                // check other fields are filled or not
                if ( checkDescription() && provideSuggestions() && mobile_no.getText() != null  && mobile_no.getText().length() > 9 && checkPassword() && checkEmail() ){
                    confirm_button.setEnabled(true);
                    confirm_button.setBackgroundResource(R.drawable.profile_setup_button);
                    confirm_button.setTextColor(Color.WHITE);
                }
                else {
                    confirm_button.setEnabled(false);
                    confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                    confirm_button.setTextColor(Color.GRAY);
                }
            }
        });

        // mobile no
        mobile_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                promoteMobileNo();
                // check other fields are filled or not
                if ( checkDescription() && provideSuggestions() && mobile_no.getText() != null  && mobile_no.getText().length() > 9 && checkPassword() && checkEmail() ){
                    confirm_button.setEnabled(true);
                    confirm_button.setBackgroundResource(R.drawable.profile_setup_button);
                    confirm_button.setTextColor(Color.WHITE);
                }
                else {
                    confirm_button.setEnabled(false);
                    confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                    confirm_button.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // ------------------------------------------------------------------------------------------  // text changed listener end


        // if every thing is filled show confirm button
        if ( mobile_no.getText() != null && mobile_no.getText().length() >  8 && checkUserName() && checkDescription() && provideSuggestions() && checkEmail() && checkPassword()){
            confirm_button.setEnabled(true);
            confirm_button.setBackgroundResource(R.drawable.profile_setup_button);
            confirm_button.setTextColor(Color.WHITE);
        }
        else {
            confirm_button.setEnabled(false);
            confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
            confirm_button.setTextColor(Color.GRAY);
        }

        // final task
        // after click confirm button
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Sign Up user");
                progressDialog.setMessage("Please wait.....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                signUpUser();
                progressDialog.dismiss();
            }
        });

    }

    private void doneProfileSetup() {
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(user_id);
        HashMap setup = new HashMap();
        setup.put("Setup", "Two");
        data.updateChildren(setup);
    }


    // end of main ----------------------------------------------------------------------------------------------------------------------------------------  //


    private void signUpUser() {
        // firebase
        final String auth_email = email.getText().toString().trim();
        final String auth_passaword = password.getText().toString().trim();

        progressDialog.show();

        // add user to firebase authentication
        firebase_auth.createUserWithEmailAndPassword(auth_email, auth_passaword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // add details to firebase real time datatabase  | Ex : name, description mobile no, language
                    progressDialog.show();
                    loginUser(auth_email, auth_passaword);
                    addDetails();
                }
                else {
                    Toast.makeText(profileSetupTwo.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void loginUser(String auth_email, String auth_password){
        firebase_auth.signInWithEmailAndPassword(auth_email, auth_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), profileSetup3.class));
                            finish();
                        }

                    }
                });
    }


    // add details to firebase real time datatabase
    private void addDetails(){
        user_id = firebase_auth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(user_id);
        progressDialog.show();

        String userName = user_name.getText().toString();
        String userDescription = description.getText().toString();
        String userLanguage = language.getText().toString();
        String userMobile = mobile_no.getText().toString();


        HashMap userDetails = new HashMap();
        userDetails.put("User Name", userName );
        userDetails.put ("Description", userDescription );
        userDetails.put ("Language", userLanguage );
        userDetails.put("Mobile", userMobile );
        database.updateChildren(userDetails);

        //save profile image
        saveImage();
        doneProfileSetup();
    }


    // save image that you select - start -- must be after saving data, because name of the image is userId ---------------------------------------------- //
    private boolean saveImage(){
        result = false;

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(user_id);

        if ( profile_image_uri != null ){
            // image conversion - start ------------------------------------------------------------------------------------------  //

            progressDialog.show();

            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile Images").child(user_id);
            Bitmap bitmap = null;

            // add image data to bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), profile_image_uri );
            }catch (Exception i ){
                Toast.makeText(this, "Image did'n save successfully ", Toast.LENGTH_SHORT).show();
            }

            // compress bitmap using byte array output stream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

            // add compress array byte array
            byte[] bytes = byteArrayOutputStream.toByteArray();

            // image conversion - end ------------------------------------------------------------------------------------------  //

            // image uploading - start ----------------------------------------------------------------------------------------- //
            // see the upload task
            UploadTask uploadTask = filepath.putBytes(bytes);

            // listener for upload task
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    while (!task.isSuccessful());
                    Uri downloadUri = task.getResult();
                    String imageUrl = String.valueOf(downloadUri);

                    // add the image url to global variable
                    user_image_url = imageUrl;
                    HashMap userDetails = new HashMap();
                    userDetails.put("Profile Image", user_image_url);
                    database.updateChildren(userDetails);

                    if ( imageUrl != null ){
                        startActivity(new Intent(getApplicationContext(), profileSetup3.class));
                        finish();
                    }

                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(profileSetupTwo.this, "Upload Photo failed", Toast.LENGTH_SHORT).show();
                }
            });

            // image uploading - end ----------------------------------------------------------------------------------------- //
        }

        else{
            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    // result from gallery after you picked a image for userProfile
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            // get the uri to global variable - to access that in saving the profile pic to database
            profile_image_uri = imageUri;
            // set the profile pic
            profile_image.setImageURI(imageUri);
        }
    }

     // save image - end ---------------------------------------------------------------------------------------------------------------------------------------- //


    // text change listeners' functions -- start ---------------------------------------------------------------------------------------------------------------- //


    // password validation - start -------------------------------------------------------------------------------------------------------------------------  //
    private boolean checkPassword() {
        boolean result = false;
        String psw = password.getText().toString();
        if (password.getText() != null ){
            if ( psw.length() < 8 ) {
                password_log.setVisibility(View.VISIBLE);
                password_log.setText("Password must contain at least 8 characters");
                confirm_button.setEnabled(false);
                confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                confirm_button.setTextColor(Color.GRAY);
            }
            else {
                password_log.setVisibility(View.GONE);
                result = true;
            }
        }
        if ( password.getText() == null ){
            password_log.setVisibility(View.VISIBLE);
            password_log.setText("Enter Password");
            result = false;
        }
        return result;
    }

    // password validation - end -------------------------------------------------------------------------------------------------------------------------  //


    // email validation - start -------------------------------------------------------------------------------------------------------------------------- //

    private boolean checkEmail() {
        boolean result = false;
        String email1 = email.getText().toString();
        if ( email.getText() != null ){
            if ( email1.contains("@") && email1.contains(".")){
                email_log.setVisibility(View.GONE);
                result = true;
            }
            else {
                email_log.setVisibility(View.VISIBLE);
                email_log.setText("Enter a valid email address");
            }
        }
        if ( email.getText() == null ){
            email_log.setVisibility(View.VISIBLE);
            email_log.setText("Enter Password");
            result = false;
        }
        return result;
    }

    // email validation - end -------------------------------------------------------------------------------------------------------------------------- //

    // promote mobile no give suggestions
    private void promoteMobileNo() {
        if ( mobile_no.getText() != null ){
            mobileno_log.setText("Eg : 077 369 7702");
            if ( mobile_no.getText().length() > 9 ){
                mobileno_log.setVisibility(View.GONE);
            }
            else {
                mobile_no.setVisibility(View.VISIBLE);
                mobileno_log.setText("Eg : 077 369 7702");
            }
        }

    }


    // give suggestions
    private boolean provideSuggestions() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, LANGUAGES);
        language.setAdapter(adapter);

        // check language equals english, sinhala or tamil
        if (language.getText() != null && ( language.getText().toString().equals("English")  || language.getText().toString().equals("Tamil")  || language.getText().toString().equals("Sinhala" ) ) ){
            language_log.setVisibility(View.GONE);
            return true;
        }
        else {
            language_log.setVisibility(View.VISIBLE);
            language_log.setText("Language must be English or Sinhala or Tamil ");
            return false;
        }

    }

    // description validation
    private boolean checkDescription() {
        boolean result = false;

        if (description.getText() != null){
            String descript = description.getText().toString();

            // if description is less than 100 words
            if (descript.length() < 60 ){
                description_log.setVisibility(View.VISIBLE);
                description_log.setText("description must be contain at least 60 characters");
                confirm_button.setEnabled(false);
                confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                confirm_button.setTextColor(Color.GRAY);
                result = false;
            }
            else {
                description_log.setVisibility(View.GONE);
                result = true;

            }

        }
        if ( description.getText() == null ){
            result = false;
        }

        return result;
    }

    // username validation
    private boolean checkUserName() {
        boolean result = false;

        if (user_name.getText() != null ){
            String name = user_name.getText().toString();

            // if username is less than 6 characters
            if (name.length() < 6 ){
                user_name_log.setVisibility(View.VISIBLE);
                user_name_log.setText("User name must be contain at least 6 characters");
                confirm_button.setEnabled(false);
                confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
                confirm_button.setTextColor(Color.GRAY);
                result = false;
            }
            else {
                user_name_log.setVisibility(View.GONE);
                result = true;
            }
        }
        if (user_name.getText() == null ){
            confirm_button.setEnabled(false);
            confirm_button.setBackgroundResource(R.drawable.non_touchable_button);
            confirm_button.setTextColor(Color.GRAY);
            result =false;
        }

        return result;
    }

    // text change listeners' functions -- end ---------------------------------------------------------------------------------------------------------------- //




    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), setupProfileOne.class));
        finish();
    }
}
