package com.example.pasan.captureitproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class naerbyPhotographerRecycleview extends RecyclerView.Adapter<naerbyPhotographerRecycleview.ViewHolder> {
    private ArrayList <String> userNames = new ArrayList();
    private ArrayList <String> profilePhotos = new ArrayList();
    private ArrayList <String> ratings = new ArrayList();
    private ArrayList <String> distance = new ArrayList();

    Context context;

    public naerbyPhotographerRecycleview(Context context, ArrayList<String> userName, ArrayList<String> profilePhotos, ArrayList<String> ratings, ArrayList<String> distance){
        this.userNames = userName;
        this.profilePhotos = profilePhotos;
        this.ratings = ratings;
        this.distance = distance;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_naerby_photographer_recycleview, viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // Animations
        Animation right_to_left = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
        viewHolder.mainLayout.setAnimation(right_to_left);

        viewHolder.userName_textView.setText(userNames.get(i));
        viewHolder.ratings_textView.setText(ratings.get(i));
        viewHolder.distance_textView.setText(ratings.get(i));
        Glide.with(context).load(profilePhotos.get(i)).into(viewHolder.profileImage_imageView);

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This function is under development", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName_textView, ratings_textView, distance_textView;
        ImageView profileImage_imageView;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName_textView = itemView.findViewById(R.id.nearby_user_name);
            ratings_textView = itemView.findViewById(R.id.nearby_rating);
            profileImage_imageView = itemView.findViewById(R.id.nearby_profile_photo);
            distance_textView = itemView.findViewById(R.id.nearby_distance);
            mainLayout = itemView.findViewById(R.id.nearby_photographer_layout);
        }
    }
}
