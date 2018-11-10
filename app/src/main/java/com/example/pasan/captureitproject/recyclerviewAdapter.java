package com.example.pasan.captureitproject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class recyclerviewAdapter extends RecyclerView.Adapter<recyclerviewAdapter.ViewHolder>{
    public static final String EXTRA_MESSAGE = "hi";
    private static final String TAG = "recyclerviewAdapter";
    private ArrayList<String> imagenames = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> imagesuser = new ArrayList<>();
    private ArrayList<String> professions = new ArrayList<>();
    Dialog dialog;
    Context context;
    Animation downtoTop;
    Animation toptoDown;
    Animation rightToLeft;



    public recyclerviewAdapter(  Context context, ArrayList<String> imagenames, ArrayList<String> images, ArrayList<String> imagesuser, ArrayList<String> profession, ArrayList<String> userIdss) {
        this.imagenames = imagenames;
        this.images = images;
        this.context = context;
        this.imagesuser = imagesuser;
        this.professions = profession;
        this.userIds = userIdss;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int in) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.user_information_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_recyclerview, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        rightToLeft = AnimationUtils.loadAnimation(context, R.anim.rightto_left_feeds);

        holder.userImageLayout.setAnimation(rightToLeft);

        holder.likeButton.setBackgroundResource(R.drawable.camera_white);
        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(context).load(images.get(i)).into(holder.image);
        Glide.with(context).load(imagesuser.get(i)).into(holder.userImage);
        //Glide.with(context).load(imagesuser.get(i)).into(holder.userImage);
        holder.text.setText(imagenames.get(i));
        holder.progressBar.setVisibility(View.GONE);
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downtoTop = AnimationUtils.loadAnimation(context, R.anim.anim3);
                toptoDown = AnimationUtils.loadAnimation(context, R.anim.anim2);

                TextView user_name_dialog = (TextView) dialog.findViewById(R.id.user_name_dialog);
                TextView user_prof_dialog = (TextView) dialog.findViewById(R.id.user_prof_dialog);
                ImageView user_image_dialog = dialog.findViewById(R.id.image_dialog);
                Button chat_button = dialog.findViewById(R.id.chat_dialog);

                Glide.with(context).load(imagesuser.get(i)).into(user_image_dialog);
                user_name_dialog.setText(imagenames.get(i));
                user_prof_dialog.setText(userIds.get(i));
                final String messege;
                messege = userIds.get(i);

                user_image_dialog.setAnimation(toptoDown);
                user_name_dialog.setAnimation(downtoTop);
                user_prof_dialog.setAnimation(downtoTop);
                chat_button.setAnimation(downtoTop);


                user_image_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, userinfo.class);
                        intent.putExtra(EXTRA_MESSAGE, messege);
                        context.startActivity(intent);
                    }
                });

                dialog.show();
            }
        });
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downtoTop = AnimationUtils.loadAnimation(context, R.anim.anim3);
                toptoDown = AnimationUtils.loadAnimation(context, R.anim.anim2);

                TextView user_name_dialog = (TextView) dialog.findViewById(R.id.user_name_dialog);
                TextView user_prof_dialog = (TextView) dialog.findViewById(R.id.user_prof_dialog);
                ImageView user_image_dialog = dialog.findViewById(R.id.image_dialog);
                Button chat_button = dialog.findViewById(R.id.chat_dialog);

                Glide.with(context).load(imagesuser.get(i)).into(user_image_dialog);
                user_name_dialog.setText(imagenames.get(i));
                user_prof_dialog.setText(userIds.get(i));
                final String messege;
                messege = userIds.get(i);

                user_image_dialog.setAnimation(toptoDown);
                user_name_dialog.setAnimation(downtoTop);
                user_prof_dialog.setAnimation(downtoTop);
                chat_button.setAnimation(downtoTop);

                dialog.show();

                user_image_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, userinfo.class);
                        intent.putExtra(EXTRA_MESSAGE, messege);
                        context.startActivity(intent);
                    }
                });
            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.likeButton.setBackgroundResource(R.drawable.camera_green);
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        ImageView image, userImage, likeButton;
        RelativeLayout userImageLayout;
        ProgressBar progressBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.user_name_top);
            image = itemView.findViewById(R.id.imageuser);
            userImage= itemView.findViewById(R.id.profile_image3);
            userImageLayout = itemView.findViewById(R.id.userImageLayout);
            progressBar = itemView.findViewById(R.id.recyclerView_loading);
            likeButton = itemView.findViewById(R.id.likebutton);
        }
    }
}