package com.example.pasan.captureitproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class photo_regiActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "phot_regiActivity";
    public static final String EXTRA_MESSAGE_2 ="regi" ;
    private ProgressDialog progressDialog;

    // firebase auth class
    private FirebaseAuth mAuth;

    // view component declaration
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText mobile;
    private Button register;

    private Animation left_to_right;
    private Animation right_to_left;
    private Animation bottomtoTop, toptoBottom;

    private TextView popupText;
    private Dialog dialog1;
    private ConstraintLayout relativeLayout;
    private ConstraintLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_regi);



        left_to_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right);
        right_to_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left);
        bottomtoTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_page_anim_bottom);
        toptoBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim2);

        dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.loading_dialog);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loading = dialog1.findViewById(R.id.loading_linear_layout);
        dialog1.setCancelable(false);
        //loading.getBackground().setAlpha(128);

        popupText = dialog1.findViewById(R.id.loading_text);
        popupText.setText("Register User");
        relativeLayout = dialog1.findViewById(R.id.loading_layout);
        relativeLayout.setAnimation(bottomtoTop);

        //create firebase auth object
        mAuth = FirebaseAuth.getInstance();


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


        register.setOnClickListener(this);

    }

    private void doneProfileSetup() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers").child(userId);
        HashMap setup = new HashMap();
        setup.put("Setup", "Complete");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User Type").child("Customers");
        database.child(userId).setValue(true);
    }

    @Override
    public void onBackPressed() {
        //Execute your code here
        startActivity(new Intent(getApplicationContext(), selectProfession.class));
        finish();

    }

    @Override
    public void onClick(View v) {
        if (v == register) {
            registerUser();
        }
    }



    private void registerUser() {
        String emails = email.getText().toString().trim();
        final String usernames = username.getText().toString().trim();
        final String passwords = password.getText().toString().trim();
        final String mobiles = mobile.getText().toString().trim();

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


        dialog1.show();

        mAuth.createUserWithEmailAndPassword(emails, passwords)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String emails = email.getText().toString().trim();
                            //startActivity(new Intent(getApplicationContext(), profilesetupAct.class));
                            Toast.makeText(photo_regiActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference firebaseDbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);
                            Map newPost = new HashMap();
                            newPost.put("User Name", usernames);
                            newPost.put("Mobile", mobiles);
                            firebaseDbUsers.setValue(newPost);
                            dialog1.cancel();
                            loginUser(emails, passwords);

                        }
                        else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                        {
                            //If email already registered.
                            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vb.vibrate(8);
                            Animation bounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                            final Dialog dialog = new Dialog(getApplicationContext());
                            dialog.setContentView(R.layout.popup_messege);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Button okay = dialog.findViewById(R.id.button2);
                            TextView messege = dialog.findViewById(R.id.popup_messege_text);
                            messege.setText("If email already registered.");
                            ConstraintLayout loading = dialog.findViewById(R.id.popup_biglayout);
                            loading.setAnimation(bounce);
                            dialog.show();

                            okay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });

                        }
                        else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            //If email are in incorret  format
                            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vb.vibrate(8);
                            Animation bounce = AnimationUtils.loadAnimation(getApplicationContext() ,R.anim.bounce);
                            final Dialog dialog = new Dialog(getApplicationContext());
                            dialog.setContentView(R.layout.popup_messege);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Button okay = dialog.findViewById(R.id.button2);
                            TextView messege = dialog.findViewById(R.id.popup_messege_text);
                            messege.setText("If email are in incorret  format ");
                            ConstraintLayout loading = dialog.findViewById(R.id.popup_biglayout);
                            loading.setAnimation(bounce);
                            dialog.show();

                            okay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });

                        }
                        else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            //if password not 'stronger'
                            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vb.vibrate(8);
                            Animation bounce = AnimationUtils.loadAnimation(getApplicationContext() ,R.anim.bounce);
                            final Dialog dialog = new Dialog(getApplicationContext());
                            dialog.setContentView(R.layout.popup_messege);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Button okay = dialog.findViewById(R.id.button2);
                            TextView messege = dialog.findViewById(R.id.popup_messege_text);
                            messege.setText("if password not 'stronger'");
                            ConstraintLayout loading = dialog.findViewById(R.id.popup_biglayout);
                            loading.setAnimation(bounce);
                            dialog.show();

                            okay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });
                        }else
                        {
                            //OTHER THING
                            Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vb.vibrate(8);
                            Animation bounce = AnimationUtils.loadAnimation(getApplicationContext() ,R.anim.bounce);
                            final Dialog dialog = new Dialog(getApplicationContext());
                            dialog.setContentView(R.layout.popup_messege);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Button okay = dialog.findViewById(R.id.button2);
                            TextView messege = dialog.findViewById(R.id.popup_messege_text);
                            messege.setText("if password not 'stronger'");
                            ConstraintLayout loading = dialog.findViewById(R.id.popup_biglayout);
                            loading.setAnimation(bounce);
                            dialog.show();

                            okay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });
                        }
                    }

                });


    }

    public void loginUser(String email, String password) {

        //String email = emails.getText().toString().trim();
        //String password = passwords.getText().toString().trim();
        popupText.setText("Log In");
        dialog1.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog1.cancel();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            while ( user == null );
                            if ( user != null ) {
                                doneProfileSetup();
                                Intent intent = new Intent(getApplicationContext(), profilesetupAct.class);
                                intent.putExtra(EXTRA_MESSAGE_2, "Hello");
                                startActivity(new Intent(getApplicationContext(), profilesetupAct.class));
                                finish();

                            }
                            //updateUI(user);
                        } else {
                            dialog1.cancel();
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



}
