package com.example.pasan.captureitproject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashScreen extends AppCompatActivity {
    private ImageView  camera_logo;
    private ConstraintLayout constraintLayout;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        camera_logo = (ImageView) findViewById(R.id.imageView7);
        constraintLayout = findViewById(R.id.splash_screen_layout);


        Animation myAnim4 = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        Animation camera = AnimationUtils.loadAnimation(this, R.anim.fade);

        constraintLayout.setAnimation(camera);
        camera_logo.setAnimation(camera);


        Thread timer = new Thread(){
          public void run(){

              try{
                  sleep(1000);

              }catch (Exception i){
                  i.printStackTrace();
              }
              finally {

                  user = FirebaseAuth.getInstance().getCurrentUser();
                  if (user != null) {
                      Intent intent = new Intent(getApplicationContext(), BottomNavigation.class);
                      startActivity(intent);
                      finish();
                  }

                  else {
                      startActivity(new Intent(getApplicationContext(), registrationActivity.class));
                      finish();
                  }

              }
          }
        };

        timer.start();
    }
}
