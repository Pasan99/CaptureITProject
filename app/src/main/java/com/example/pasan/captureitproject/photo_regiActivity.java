package com.example.pasan.captureitproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class photo_regiActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "phot_regiActivity";
    private ProgressDialog progressDialog;

    // firebase auth class
    private FirebaseAuth mAuth;

    // view component declaration
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText mobile;
    private Button register;
    private String item33;

    private Animation left_to_right;
    private Animation right_to_left;
    private Animation bottomtoTop, toptoBottom;

    private TextView popupText;
    private Dialog dialog;
    private RelativeLayout relativeLayout;
    private LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_regi);

        left_to_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right);
        right_to_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left);
        bottomtoTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_page_anim_bottom);
        toptoBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim2);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loading = dialog.findViewById(R.id.loading_linear_layout);
        loading.getBackground().setAlpha(128);

        popupText = dialog.findViewById(R.id.loading_text);
        popupText.setText("Register User");
        relativeLayout = dialog.findViewById(R.id.loading_layout);
        relativeLayout.setAnimation(bottomtoTop);

        //create firebase auth object
        mAuth = FirebaseAuth.getInstance();

        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Select Professions", "Photographer", "CaptureIt Customer"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mobile = (EditText) findViewById(R.id.mobile);
        register = (Button) findViewById(R.id.regiButton);

        username.setAnimation(left_to_right);
        email.setAnimation(right_to_left);
        password.setAnimation(left_to_right);
        mobile.setAnimation(right_to_left);
        register.setAnimation(bottomtoTop);


        dropdown.setOnItemSelectedListener(this);
        register.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        startActivity(new Intent(getApplicationContext(), registrationActivity.class));
        finish();

    }

    @Override
    public void onClick(View v) {
        if (v == register) {
            registerUser();
        }
    }


    public void setup(View view2) {
        // method that we defined bellow
        registerUser();

    }


    private void registerUser() {
        String emails = email.getText().toString().trim();
        final String usernames = username.getText().toString().trim();
        final String passwords = password.getText().toString().trim();
        final String mobiles = mobile.getText().toString().trim();
        final String profession = item33;

        if (TextUtils.isEmpty(emails)) {
            Toast.makeText(this, "please enter a email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(usernames)) {
            Toast.makeText(this, "please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passwords)) {
            Toast.makeText(this, "please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mobiles)) {
            Toast.makeText(this, "please enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (item33 == null || item33 == "Select Professions") {
            Toast.makeText(this, "please enter Profession", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.show();

        mAuth.createUserWithEmailAndPassword(emails, passwords)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String emails = email.getText().toString().trim();
                            //startActivity(new Intent(getApplicationContext(), profilesetupAct.class));
                            Toast.makeText(photo_regiActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference firebaseDbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map newPost = new HashMap();
                            newPost.put("Name", usernames);
                            newPost.put("Mobile", mobiles);
                            newPost.put("Profession", profession);
                            firebaseDbUsers.setValue(newPost);
                            dialog.cancel();
                            loginUser(emails, passwords);

                        }
                    }
                });


    }

    public void loginUser(String email, String password) {

        //String email = emails.getText().toString().trim();
        //String password = passwords.getText().toString().trim();
        popupText.setText("Log In");
        dialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.cancel();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            while ( user == null );
                            if ( user != null ) {
                                startActivity(new Intent(getApplicationContext(), BottomNavigation.class));
                                finish();
                            }
                            //updateUI(user);
                        } else {
                            dialog.cancel();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(photo_regiActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);

                        }

                        // ...
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item33 = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(photo_regiActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }
}
