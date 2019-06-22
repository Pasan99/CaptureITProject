package com.example.pasan.captureitproject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class search_best_photo_recycleView extends RecyclerView.Adapter<search_best_photo_recycleView.ViewHolder> {
    public static final String EXTRA_MESSAGE = "search_best";
    private ArrayList<String> profile_images_rv = new ArrayList<>();
    private ArrayList<String> user_names_rv = new ArrayList<>();
    private ArrayList<String> ratings_rv = new ArrayList<>();
    private ArrayList<String> userIds_rv = new ArrayList<>();

    private Activity context_rv;
    private Animation bottom_to_top;

    public search_best_photo_recycleView(Activity context, ArrayList<String> profile_images, ArrayList<String> user_names, ArrayList<String> ratings, ArrayList<String> userIds) {
        this.context_rv = context;
        this.profile_images_rv = profile_images;
        this.user_names_rv = user_names;
        this.ratings_rv = ratings;
        this.userIds_rv = userIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_search_best_photo_recycle_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        // Animation
        bottom_to_top = AnimationUtils.loadAnimation(context_rv, R.anim.bottom_to_top_middle );
        viewHolder.mainLayout.setAnimation(bottom_to_top);

        Glide.with(context_rv).load(profile_images_rv.get(i)).into(viewHolder.profileImage_imageView);
        viewHolder.userName_textView.setText(user_names_rv.get(i));
        viewHolder.ratings_textView.setText(ratings_rv.get(i));

        final String messege = userIds_rv.get(i);

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context_rv, "This function is still Under Development", Toast.LENGTH_SHORT).show();
                // create intent object
                Intent intent = new Intent(context_rv, userinfo.class);

                // send message to next activity
                intent.putExtra(EXTRA_MESSAGE, messege);
                // start activity
                context_rv.startActivity(intent);
                context_rv.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                context_rv.finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return user_names_rv.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName_textView, ratings_textView;
        ImageView profileImage_imageView;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName_textView = itemView.findViewById(R.id.user_name_best_photographers);
            ratings_textView = itemView.findViewById(R.id.rating_best_photographer);
            profileImage_imageView = itemView.findViewById(R.id.profile_image_best_photographers);
            mainLayout = itemView.findViewById(R.id.best_photographers_layout);
        }
    }


}
