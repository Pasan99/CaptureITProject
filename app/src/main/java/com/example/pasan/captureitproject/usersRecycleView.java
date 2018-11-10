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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

public class usersRecycleView extends RecyclerView.Adapter<usersRecycleView.ViewHolder>{
    public static final String EXTRA_MESSAGE = "hi";
    private static final String TAG = "recyclerviewAdapter";
    private ArrayList<String> imagenames = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> imagesuser = new ArrayList<>();
    private ArrayList<String> professions = new ArrayList<>();
    private ArrayList<String> imageIds = new ArrayList<>();

    Dialog dialog;
    Context context;
    Animation downtoTop;
    Animation toptoDown;
    Animation rightToLeft;



    public usersRecycleView(  Context context, ArrayList<String> imagenames, ArrayList<String> images, ArrayList<String> imagesuser, ArrayList<String> profession, ArrayList<String> userIdss, ArrayList<String> imageIdso ) {
        this.imagenames = imagenames;
        this.images = images;
        this.context = context;
        this.imagesuser = imagesuser;
        this.professions = profession;
        this.userIds = userIdss;
        this.imageIds = imageIdso;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int in) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.pop_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_recycler_view, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        rightToLeft = AnimationUtils.loadAnimation(context, R.anim.rightto_left_feeds);

        holder.userImageLayout.setAnimation(rightToLeft);
        holder.messege.setText("");
        holder.text.setVisibility(View.VISIBLE);
        holder.image.setVisibility(View.VISIBLE);
        holder.userImage.setVisibility(View.VISIBLE);
        holder.button_layout.setVisibility(View.VISIBLE);
        holder.clickCount.setVisibility(View.VISIBLE);
        holder.likeButton.setBackgroundResource(R.drawable.camera_white);
        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(context).load(images.get(i)).into(holder.image);
        Glide.with(context).load(imagesuser.get(i)).into(holder.userImage);
        //Glide.with(context).load(imagesuser.get(i)).into(holder.userImage);
        holder.text.setText(imagenames.get(i));
        holder.progressBar.setVisibility(View.GONE);

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downtoTop = AnimationUtils.loadAnimation(context, R.anim.anim3);
                toptoDown = AnimationUtils.loadAnimation(context, R.anim.anim2);

                TextView info = (TextView) dialog.findViewById(R.id.popup_message);
                Button yes_button = dialog.findViewById(R.id.popup_yes_button);
                Button no_button = dialog.findViewById(R.id.popup_no_button);

                info.setText("Do you want to delete this Photo");

                dialog.show();

                yes_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference imageDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Uploads");
                        imageDb.child(imageIds.get(i)).removeValue();
                        dialog.cancel();
                        imagenames.remove(i);
                        images.remove(i);
                        userIds.remove(i);
                        imagesuser.remove(i);
                        professions.remove(i);
                        imageIds.remove(i);
                        holder.text.setVisibility(View.GONE);
                        holder.image.setVisibility(View.GONE);
                        holder.userImage.setVisibility(View.GONE);
                        holder.button_layout.setVisibility(View.GONE);
                        holder.clickCount.setVisibility(View.GONE);
                        holder.messege.setText("Item Removed");

                    }
                });


                no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

            }
        });


    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text, messege, clickCount;
        ImageView image, userImage, likeButton;
        RelativeLayout userImageLayout;
        ProgressBar progressBar;
        LinearLayout button_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.user_name_top);
            clickCount = itemView.findViewById(R.id.click_count);
            image = itemView.findViewById(R.id.imageuser);
            userImage= itemView.findViewById(R.id.profile_image3);
            userImageLayout = itemView.findViewById(R.id.userImageLayout);
            progressBar = itemView.findViewById(R.id.recyclerView_loading);
            likeButton = itemView.findViewById(R.id.likebutton);
            messege = itemView.findViewById(R.id.textView10);
            button_layout = itemView.findViewById(R.id.button_layout);
        }
    }
}
