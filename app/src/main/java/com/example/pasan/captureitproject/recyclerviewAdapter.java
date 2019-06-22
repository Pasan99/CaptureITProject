package com.example.pasan.captureitproject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Objects;


public class recyclerviewAdapter extends RecyclerView.Adapter<recyclerviewAdapter.ViewHolder>{
    public static final String EXTRA_MESSAGE = "hi";
    static final String EXTRA_MESSAGE_2 ="ki" ;


    // ArrayList
    private ArrayList<String> feeds_userNames;
    private ArrayList<String> feeds_userIds;
    private ArrayList<String> feeds_mainImage;
    private ArrayList<String> feeds_userProfileImage;
    private ArrayList<String> feeds_photo_count;
    private ArrayList<String> feeds_prices;
    private ArrayList<String> feeds_descriptions;
    private ArrayList<String> feeds_occupations;
    private ArrayList<String> feeds_packageTypes;
    private ArrayList<String> feeds_mobiles;
    private ArrayList<String> packageNumbers;
    private String userType;

    // small dialog box
    private Dialog dialog;

    // Activity
    private Context context;

    // Animations
    private Animation downtoTop;
    private Animation toptoDown;


    // get all data and define them
    public recyclerviewAdapter(  Context context, ArrayList<String> userNames, ArrayList<String> coverImage, ArrayList<String> profileImage, ArrayList<String> userIdss, ArrayList<String> photo_count, ArrayList<String> prices, ArrayList<String> descriptions, ArrayList<String> Occupations, ArrayList<String> packageTypes, ArrayList<String> mobiles, String userType, ArrayList<String> packageNumbers) {
        // user names of feeds context
        this.feeds_userNames = userNames;
        // main image of feeds context
        this.feeds_mainImage = coverImage;
        // get context - get Feeds Activity context ( Feeds.class )
        this.context = context;
        // profile photos of feeds context
        this.feeds_userProfileImage = profileImage;
        this.feeds_photo_count = photo_count;
        this.feeds_userIds = userIdss;
        this.feeds_prices = prices;
        this.feeds_descriptions = descriptions;
        this.feeds_occupations = Occupations;
        this.feeds_packageTypes = packageTypes;
        this.feeds_mobiles = mobiles;
        this.userType = userType;
        this.packageNumbers = packageNumbers;

    }


    // when creating each feed context / holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int in) {

        // make new dialog box
        dialog = new Dialog(context);
        // set dialog context to related layout
        dialog.setContentView(R.layout.user_information_dialog);
        // make dialog background transparent
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // inflate holders with related layout / make dummy holders, there are no values inserted only the layout
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_recyclerview, viewGroup, false);

        return new ViewHolder(view);
    }


    // full fill dummy holders with values/data
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        checkLike(packageNumbers.get(i), holder);
        // Animation Definition
        Animation top_to_bottom = AnimationUtils.loadAnimation(context, R.anim.bottom_to_top_middle);
        // Set animation
        holder.userImageLayout.setAnimation(top_to_bottom);

        // starting point of load data
        holder.progressBar.setVisibility(View.VISIBLE);

        // adding values to dummy holder
        Glide.with(context).load(feeds_mainImage.get(i)).into(holder.mainImage_imageView);
        Glide.with(context).load(feeds_userProfileImage.get(i)).into(holder.userProfileImage_imageView);
        holder.userName_textView.setText(feeds_userNames.get(i));
        holder.profession_textView.setText(feeds_occupations.get(i));
        String price = feeds_photo_count.get(i) + " Photos / Rs." + feeds_prices.get(i) +".00";
        holder.price_photos_textview.setText(price);
        holder.description_textview.setText(feeds_descriptions.get(i));
        holder.pakageType_textview.setText(feeds_packageTypes.get(i));

        // like button
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                assert vb != null;
                vb.vibrate(10);
                addLike(packageNumbers.get(i));

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                assert vb != null;
                vb.vibrate(3);
                removeLike(packageNumbers.get(i));
            }
        });

        // to make user notify, when successfully load the details
        holder.progressBar.setVisibility(View.GONE);

        // when pressed book
        holder.orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( userType.equals("Customers")) {
                    String[] UN_UI_UPP_OCC = new String[6];
                    UN_UI_UPP_OCC[0] = feeds_userNames.get(i);
                    UN_UI_UPP_OCC[1] = feeds_userIds.get(i);
                    UN_UI_UPP_OCC[2] = feeds_userProfileImage.get(i);
                    UN_UI_UPP_OCC[3] = feeds_occupations.get(i);
                    UN_UI_UPP_OCC[4] = feeds_mobiles.get(i);
                    UN_UI_UPP_OCC[5] = packageNumbers.get(i);

                    Intent intent = new Intent(context, BookingOne.class);
                    intent.putExtra(EXTRA_MESSAGE, UN_UI_UPP_OCC);
                    context.startActivity(intent);
                }
                else {
                    Toast.makeText(context, "Photographers cannot make Bookings", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // when you pressed the main photo in feeds content / holder
        holder.mainImage_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make intent object
                Intent intent = new Intent(context, photoView.class );

                // all Animations
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String> (holder.mainImage_imageView, "mainImage");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity)context, pairs );

                // send image url to the next activity
                intent.putExtra(EXTRA_MESSAGE, feeds_mainImage.get(i) );

                // start new activity
                context.startActivity(intent, activityOptions.toBundle());
            }
        });

        // when you pressed the user's profile photo in feeds content / holder
        holder.userProfileImage_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animation Definition
                downtoTop = AnimationUtils.loadAnimation(context, R.anim.anim3);
                toptoDown = AnimationUtils.loadAnimation(context, R.anim.anim2);

                // Dialog box layout context Declaration and Definition
                TextView user_name_dialog = (TextView) dialog.findViewById(R.id.user_name_dialog);
                TextView user_prof_dialog = (TextView) dialog.findViewById(R.id.user_prof_dialog);
                final ImageView user_image_dialog = dialog.findViewById(R.id.image_dialog);
                Button chat_button = dialog.findViewById(R.id.chat_dialog);

                // Dialog box layout context add values
                Glide.with(context).load(feeds_userProfileImage.get(i)).into(user_image_dialog);
                user_name_dialog.setText(feeds_userNames.get(i));

                final String messege = feeds_userIds.get(i);

                // Animations
                user_image_dialog.setAnimation(toptoDown);
                user_name_dialog.setAnimation(downtoTop);
                user_prof_dialog.setAnimation(downtoTop);
                chat_button.setAnimation(downtoTop);

                chat_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create intent object
                        Intent intent = new Intent(context, userinfo.class);

                        // Animations
                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(user_image_dialog, "userImage");
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        // send message to next activity
                        intent.putExtra(EXTRA_MESSAGE, messege);
                        // start activity
                        context.startActivity(intent, activityOptions.toBundle());
                    }
                });

                // When you pressed the profile Image in pop up dialog box
                user_image_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create intent object
                        Intent intent = new Intent(context, userinfo.class);

                        // Animations
                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(user_image_dialog, "userImage");
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        // send message to next activity
                        intent.putExtra(EXTRA_MESSAGE, messege);
                        // start activity
                        context.startActivity(intent, activityOptions.toBundle());
                    }
                });

                dialog.show();
            }
        });



        // when you pressed the user's name in feeds content / holder
        holder.userName_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, userinfo.class);

                final String messege = feeds_userIds.get(i);
                // Animations
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(holder.userProfileImage_imageView, "userImage");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                // send message to next activity
                intent.putExtra(EXTRA_MESSAGE, messege);
                // start activity
                context.startActivity(intent, activityOptions.toBundle());
                /*
                // Animation Definition
                downtoTop = AnimationUtils.loadAnimation(context, R.anim.anim3);
                toptoDown = AnimationUtils.loadAnimation(context, R.anim.anim2);

                // Dialog box layout context declaration and definition
                TextView user_name_dialog = (TextView) dialog.findViewById(R.id.user_name_dialog);
                TextView user_prof_dialog = (TextView) dialog.findViewById(R.id.user_prof_dialog);
                final ImageView user_image_dialog = dialog.findViewById(R.id.image_dialog);
                Button chat_button = dialog.findViewById(R.id.chat_dialog);

                // fill Dialog box layout context
                Glide.with(context).load(feeds_userProfileImage.get(i)).into(user_image_dialog);
                user_name_dialog.setText(feeds_userNames.get(i));


                final String messege = feeds_userIds.get(i);

                // Animations
                user_image_dialog.setAnimation(toptoDown);
                user_name_dialog.setAnimation(downtoTop);
                user_prof_dialog.setAnimation(downtoTop);
                chat_button.setAnimation(downtoTop);

                dialog.show();

                chat_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create intent object
                        Intent intent = new Intent(context, userinfo.class);

                        // Animations
                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(user_image_dialog, "userImage");
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        // send message to next activity
                        intent.putExtra(EXTRA_MESSAGE, messege);
                        // start activity
                        context.startActivity(intent, activityOptions.toBundle());
                    }
                });

                // When you pressed the profile Image in pop up dialog box
                user_image_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create new intent object
                        Intent intent = new Intent(context, userinfo.class);

                        // Animation
                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(user_image_dialog, "userImage");
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        // send message to next intent
                        intent.putExtra(EXTRA_MESSAGE, messege);
                        context.startActivity(intent, activityOptions.toBundle());
                    }
                });
                */
            }

        });


    }

    private void addLike(String packageNumber) {
        DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("Likes");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        like.child(packageNumber).child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).setValue("like");
    }

    private void removeLike(String packageNumber) {
        DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("Likes");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        like.child(packageNumber).child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).removeValue();
    }

    private void checkLike(final String packageNumber, final ViewHolder holder){
        DatabaseReference like = FirebaseDatabase.getInstance().getReference().child("Likes");
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        like.keepSynced(true);
        like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( dataSnapshot.child(packageNumber).hasChild(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())){
                    holder.likeButton.setLiked(true);
                }
                else {
                    holder.likeButton.setLiked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return feeds_prices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName_textView, profession_textView, description_textview, price_photos_textview, pakageType_textview;
        ImageView mainImage_imageView, userProfileImage_imageView;
        ConstraintLayout userImageLayout;
        ProgressBar progressBar;
        LikeButton likeButton;
        Button orderButton;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderButton = itemView.findViewById(R.id.order_button_feeds);
            price_photos_textview = itemView.findViewById(R.id.textView68);
            description_textview = itemView.findViewById(R.id.textView21);
            profession_textView = itemView.findViewById(R.id.profession_feeds);
            userName_textView = itemView.findViewById(R.id.user_name_top);
            mainImage_imageView = itemView.findViewById(R.id.imageuser);
            userProfileImage_imageView = itemView.findViewById(R.id.profile_image3);
            userImageLayout = itemView.findViewById(R.id.userImageLayout);
            progressBar = itemView.findViewById(R.id.recyclerView_loading);
            likeButton = itemView.findViewById(R.id.likebutton);
            pakageType_textview = itemView.findViewById(R.id.feeds_package_type);
        }
    }



}