package com.example.pasan.captureitproject;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class registrationActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "registrationActivity";
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private EditText emails;
    private EditText passwords;
    private Button login;
    private TextView registration;
    private Animation left_to_right;
    private Animation right_to_left;
    private Animation bottomtoTop, toptoBottom, fade_off, fade;
    private ImageView image;
    private ConstraintLayout constraintLayout;
    private Dialog dialog;
    private TextView popupText;
    private RelativeLayout relativeLayout;
    private LinearLayout loading;
    //String imageUrl = "https://firebasestorage.googleapis.com/v0/b/captureit-b09bc.appspot.com/o/startPage.png?alt=media&token=11908d2a-56b2-4a64-a7f8-1ca632230f29";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();

        left_to_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right);
        right_to_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left);
        bottomtoTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_page_anim_bottom);
        toptoBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim2);
        fade_off = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
        fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loading = dialog.findViewById(R.id.loading_linear_layout);
        loading.getBackground().setAlpha(128);

        popupText = dialog.findViewById(R.id.loading_text);
        popupText.setText("Log In");
        relativeLayout = dialog.findViewById(R.id.loading_layout);
        relativeLayout.setAnimation(bottomtoTop);

        constraintLayout = findViewById(R.id.registration_layout);
        emails = (EditText) findViewById(R.id.logEmail);
        passwords = (EditText) findViewById(R.id.logPassword);
        progressDialog = new ProgressDialog(this);
        login = (Button) findViewById(R.id.Loginbutton);
        registration = (TextView) findViewById(R.id.textView3);
        image = (ImageView) findViewById(R.id.imageView3);

        constraintLayout.setAnimation(fade);
        emails.setAnimation(left_to_right);
        passwords.setAnimation(right_to_left);
        login.setAnimation(bottomtoTop);
        registration.setAnimation(bottomtoTop);
        image.setAnimation(toptoBottom);




        //Glide.with(getApplication()).load(imageUrl).into(image);
        registration.setOnClickListener(this);
        login.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

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
            startActivity(new Intent(getApplicationContext(), navigation_interface.class));
            finish();
        }
    }

    public void navigation(View view){
        Intent intent = new Intent(this,navigation_interface.class);
        startActivity(intent);
    }

    public void loginUser (){

        String email = emails.getText().toString().trim();
        String password = passwords.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            constraintLayout.setAnimation(fade_off);
                            dialog.cancel();
                            startActivity(new Intent(getApplicationContext(), BottomNavigation.class));
                            finish();
                            //updateUI(user);
                        } else {
                            dialog.cancel();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(registrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);

                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
        finish();
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if ( v == login ){
            loginUser();
        }
        if ( v == registration ){
            Intent intent = new Intent(getApplicationContext(), photo_regiActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(registrationActivity.this, Pair.<View, String>create(image, "captureit"));
            constraintLayout.setAnimation(fade_off);
            startActivity(intent);
            finish();
        }
    }
}
