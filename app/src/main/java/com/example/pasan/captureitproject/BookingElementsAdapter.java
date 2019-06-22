package com.example.pasan.captureitproject;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingElementsAdapter extends RecyclerView.Adapter<BookingElementsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> Names = new ArrayList<String>();
    private ArrayList<String> Ids = new ArrayList<String>();
    private ArrayList<String> ProfileImages = new ArrayList<String>();
    private ArrayList<String> Mobiles = new ArrayList<String>();
    private ArrayList<String> Occupations = new ArrayList<String>();
    private ArrayList<String> PackageTypes = new ArrayList<String>();
    private ArrayList<String> PackagePrices = new ArrayList<String>();
    private ArrayList<String> Dates = new ArrayList<String>();
    private ArrayList<String> Times = new ArrayList<String>();
    private ArrayList<String> Status = new ArrayList<String>();
    private ArrayList<String> UserTypes = new ArrayList<String>();
    private ArrayList<String> bookingIds = new ArrayList<String>();


    public BookingElementsAdapter(Context context, ArrayList<String> Names, ArrayList<String> Ids, ArrayList<String> ProfileImages, ArrayList<String> Mobiles, ArrayList<String> Occupations, ArrayList<String> PackageTypes, ArrayList<String> PackagePrices, ArrayList<String> Dates, ArrayList<String> Times, ArrayList<String> Status, ArrayList<String> UserTypes, ArrayList<String> bookingIds) {
        this.Names = Names;
        this.Ids = Ids;
        this.ProfileImages = ProfileImages;
        this.Mobiles = Mobiles;
        this.Occupations = Occupations;
        this.PackageTypes = PackageTypes;
        this.PackagePrices = PackagePrices;
        this.Dates = Dates;
        this.Times = Times;
        this.context = context;
        this.Status = Status;
        this.UserTypes = UserTypes;
        this.bookingIds = bookingIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // inflate holders with related layout / make dummy holders, there are no values inserted only the layout
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.booking_element_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        if (Status.get(i).equals("Pending")) {
            viewHolder.status_textview.setTextColor(Color.rgb(254, 176, 59));
        }
        if (Status.get(i).equals("Canceled")) {
            viewHolder.status_textview.setTextColor(Color.RED);
        }
        if (Status.get(i).equals("Booked")) {
            viewHolder.status_textview.setTextColor(Color.rgb(5, 152, 0));
        }

        viewHolder.status_textview.setText(Status.get(i));
        viewHolder.name_textview.setText(Names.get(i));
        viewHolder.date_textview.setText(Dates.get(i) + "\t " + Times.get(i));
        viewHolder.occupation_textview.setText(Occupations.get(i));
        viewHolder.packageType_textview.setText(PackageTypes.get(i));
        viewHolder.price_textview.setText("Rs." + PackagePrices.get(i) + ".00");
        Glide.with(context).load(ProfileImages.get(i)).into(viewHolder.profilePhoto_imageview);

        if (Status.get(i).equals("Pending") && UserTypes.get(i).equals("Photographers")) {
            viewHolder.cancle_button.setVisibility(View.VISIBLE);
            viewHolder.confirm_button.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cancle_button.setVisibility(View.GONE);
            viewHolder.confirm_button.setVisibility(View.GONE);
        }

        viewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = Mobiles.get(i);
                Intent i = new Intent(Intent.ACTION_DIAL);
                String p = "tel:" + mobile;
                i.setData(Uri.parse(p));
                context.startActivity(i);
            }
        });

        viewHolder.messegeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // define dialog box
                Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(8);
                Animation bounce = AnimationUtils.loadAnimation(context, R.anim.bounce);
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_messege);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button okay = dialog.findViewById(R.id.button2);
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
        });

        viewHolder.confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.cancle_button.setVisibility(View.GONE);
                viewHolder.confirm_button.setVisibility(View.GONE);
                viewHolder.status_textview.setText("Booked");
                viewHolder.status_textview.setTextColor(Color.rgb(5,152,0));
                addPackageStatus(bookingIds.get(i));
            }
        });

        viewHolder.cancle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.cancle_button.setVisibility(View.GONE);
                viewHolder.confirm_button.setVisibility(View.GONE);
                viewHolder.status_textview.setTextColor(Color.RED);
                viewHolder.status_textview.setText("Canceled");
                addPackageStatusAsCanceled(bookingIds.get(i));
            }
        });
    }

    private void addPackageStatus(String BookingId){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Booking Status").child(BookingId);
        HashMap status = new HashMap();
        status.put("Status", "Booked");
        database.updateChildren(status);
    }

    private void addPackageStatusAsCanceled(String BookingId){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Booking Status").child(BookingId);
        HashMap status = new HashMap();
        status.put("Status", "Canceled");
        database.updateChildren(status);
    }


    @Override
    public int getItemCount() {
        return Ids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView date_textview, name_textview, occupation_textview, price_textview, packageType_textview, status_textview;
        CircleImageView profilePhoto_imageview;
        Button confirm_button, cancle_button, callButton, messegeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            callButton = itemView.findViewById(R.id.be_callButton);
            messegeButton = itemView.findViewById(R.id.be_messageButton);
            confirm_button = itemView.findViewById(R.id.be_confirm_button);
            cancle_button = itemView.findViewById(R.id.be_cancel_button);
            status_textview = itemView.findViewById(R.id.textView86);
            date_textview = itemView.findViewById(R.id.be_date);
            name_textview = itemView.findViewById(R.id.be_customerName);
            occupation_textview = itemView.findViewById(R.id.be_occupation);
            price_textview = itemView.findViewById(R.id.be_price);
            packageType_textview = itemView.findViewById(R.id.be_packageType);
            profilePhoto_imageview = itemView.findViewById(R.id.Booking_profile_image);
        }
    }


}
