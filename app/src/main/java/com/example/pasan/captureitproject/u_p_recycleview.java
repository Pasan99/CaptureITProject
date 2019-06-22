package com.example.pasan.captureitproject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class u_p_recycleview extends RecyclerView.Adapter<u_p_recycleview.ViewHolder>{
    public static final String EXTRA_MESSAGE = "hi";
    public static final String EXTRA_MESSAGE_2 = "ki";
    private static final String TAG = "recyclerviewAdapter";

    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> professions = new ArrayList<>();
    private ArrayList<String> imageIds = new ArrayList<>();

    Dialog dialog;
    Context context;
    Animation downtoTop;
    Animation toptoDown;
    Animation rightToLeft;



    public u_p_recycleview(Context context, ArrayList<String> imagenames, ArrayList<String> images, ArrayList<String> imagesuser, ArrayList<String> profession, ArrayList<String> userIdss, ArrayList<String> imageIdso ) {

        this.images = images;
        this.context = context;
        this.professions = profession;
        this.userIds = userIdss;
        this.imageIds = imageIdso;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int in) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_u_p_recycleview, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        rightToLeft = AnimationUtils.loadAnimation(context, R.anim.rightto_left_feeds);

        holder.image.setVisibility(View.VISIBLE);
        Glide.with(context).load(images.get(i)).into(holder.image);


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make intent object
                Intent intent = new Intent(context, photoDelete.class );

                // all Animations
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String> (holder.image, "mainImage");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity)context, pairs );

                // send image url to the next activity
                intent.putExtra(EXTRA_MESSAGE, images.get(i) );
                intent.putExtra(EXTRA_MESSAGE_2, imageIds.get(i));

                // start new activity
                context.startActivity(intent, activityOptions.toBundle());
            }
        });


    }


    @Override
    public int getItemCount() {
        return images.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text, messege, clickCount;
        ImageView image;
        RelativeLayout userImageLayout;
        ProgressBar progressBar;
        LinearLayout button_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.imageuser);
            userImageLayout = itemView.findViewById(R.id.user_p_bottom);



        }
    }
}
