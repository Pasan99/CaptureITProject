package com.example.pasan.captureitproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class splashScreen extends AppCompatActivity {
    private ImageView image, location, camera_logo, truck_log;
    String imageUrl = "https://firebasestorage.googleapis.com/v0/b/captureit-b09bc.appspot.com/o/startPage.png?alt=media&token=11908d2a-56b2-4a64-a7f8-1ca632230f29";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        image = (ImageView) findViewById(R.id.imageView7);
        camera_logo = (ImageView) findViewById(R.id.camera);
        truck_log = (ImageView) findViewById(R.id.truck);
        location = (ImageView) findViewById(R.id.location_logo);
        //Glide.with(getApplication()).load("@drawable/startpage").into(image);
        TextView textView = findViewById(R.id.better);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.anim1);
        Animation myAnim2 = AnimationUtils.loadAnimation(this, R.anim.rotate);
        Animation myAnim3 = AnimationUtils.loadAnimation(this, R.anim.rotate2);
        Animation myAnim4 = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        Animation truck = AnimationUtils.loadAnimation(this, R.anim.truck_left_move);
        Animation camera = AnimationUtils.loadAnimation(this, R.anim.camera_move);
        //image.startAnimation(myAnim2);
        image.startAnimation(myAnim);
        location.setAnimation(myAnim2);
        textView.setAnimation(myAnim4);
        truck_log.setAnimation(truck);
        camera_logo.setAnimation(camera);

        Thread timer = new Thread(){
          public void run(){
              try{
                  sleep(2000);
              }catch (Exception i){
                  i.printStackTrace();
              }
              finally {
                  startActivity(new Intent(getApplicationContext(), startPage.class));
                  finish();
              }
          }
        };

        timer.start();
    }
}
