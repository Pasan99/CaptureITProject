package com.example.pasan.captureitproject;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class Swipeeee extends Fragment {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private cards card_data[];
    private arrayAdapter arrayAdapter;
    private int i;
    private ProgressDialog progressDialog;
    private DatabaseReference userDb;
    private FirebaseUser mAuth;
    String currentUId;
    String carduserName;
    String carduserId;
    private String ImageUrl;
    private Button back, message, followButton, removeButton;
    private String messeage;
    private TextView user_name_bottom, user_text_empty;
    private int name_count = 0;
    private int true_count = 0;
    private ProgressBar progressBar;
    private SwipeFlingAdapterView swipeFlingAdapterView;
    private LinearLayout buttons_layout;
    private String userId;

    ArrayList<String> names = new ArrayList<>(0);
    ArrayList<String> userIds = new ArrayList<>();
    List<cards> rowItems;
    View v;

    private Animation bottomtoTop;
    private Animation fade;
    int count = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_swipecard, container, false);
        bottomtoTop = AnimationUtils.loadAnimation(getContext(), R.anim.anim3);
        fade = AnimationUtils.loadAnimation(getContext(), R.anim.fade);

        swipeFlingAdapterView= v.findViewById(R.id.frame);
        user_name_bottom = v.findViewById(R.id.user_name_bottom);
        user_text_empty = v.findViewById(R.id.user_text_empty);
        buttons_layout = v.findViewById(R.id.swipe_card_buttons_layout);
        followButton = v.findViewById(R.id.followButton);
        removeButton = v.findViewById(R.id.removeButton);

        swipeFlingAdapterView.setAnimation(fade);
        user_name_bottom.setAnimation(bottomtoTop);
        user_text_empty.setAnimation(bottomtoTop);
        buttons_layout.setAnimation(bottomtoTop);

        //progressDialog = new ProgressDialog(getContext());
        progressBar = v.findViewById(R.id.load_swipecards);
        progressBar.setVisibility(View.VISIBLE);




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUId = user.getUid();
        userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        //progressDialog.setMessage("Loading");
        //progressDialog.show();
        checkUser();



        rowItems = new ArrayList<cards>();
        //al.add("Pasan");


        arrayAdapter = new arrayAdapter(getContext(), R.layout.item, rowItems);


        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) v.findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {

                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                if ( names.size() != name_count + 1 ) {
                    name_count++;
                    userId = userIds.get(name_count);
                    user_name_bottom.setText(names.get(name_count));
                    followButton.setBackgroundResource(R.drawable.rectangele);
                    removeButton.setBackgroundResource(R.drawable.rectangele);
                }
                if (names.size() == name_count + 1 ){
                    user_name_bottom.setText("");
                    user_text_empty.setText("User List is Empty");
                    followButton.setVisibility(View.GONE);
                    removeButton.setVisibility(View.GONE);
                }
                /*
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                String userName = obj.getname();
                */
                //userDb.child(userId).child("Connections").child("Nope").child(currentUId).setValue(true);

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                if ( names.size() != name_count + 1 ) {
                    name_count++;
                    userId = userIds.get(name_count);
                    user_name_bottom.setText(names.get(name_count));
                    followButton.setBackgroundResource(R.drawable.rectangele);
                    removeButton.setBackgroundResource(R.drawable.rectangele);
                }
                else if (names.size() == name_count + 1 ){
                    user_name_bottom.setText("");
                    user_text_empty.setText("User List is Empty");
                    followButton.setVisibility(View.GONE);
                    removeButton.setVisibility(View.GONE);
                }
                /*
                cards obj = (cards) dataObject;
                userId = obj.getUserId();
                */
                //userDb.child(userId).child("Connections").child("Yep").child(currentUId).setValue(true);

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

                // Ask for more data here
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                cards obj = (cards) dataObject;
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText( getContext(), names.get(name_count), Toast.LENGTH_SHORT).show();
                userDb.child(userId).child("Connections").child("Yep").child(currentUId).setValue(true);
                followButton.setBackgroundResource(R.drawable.follow_pressed);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText( getContext(), names.get(name_count), Toast.LENGTH_SHORT).show();
                userDb.child(userId).child("Connections").child("Nope").child(currentUId).setValue(true);
                removeButton.setBackgroundResource(R.drawable.remove_pressed);
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    //
    public void checkUser() {

        final DatabaseReference pic_database = FirebaseDatabase.getInstance().getReference().child("Users");
        pic_database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (dataSnapshot.exists() && !dataSnapshot.child("Connections").child("Nope").hasChild(currentUId) && (!dataSnapshot.getKey().equals(currentUId)) && !dataSnapshot.child("Connections").child("Yep").hasChild(currentUId)  ) {
                    if ( dataSnapshot.child("profileImageUrl").exists()) {

                        while ( count < 1 ){
                            userId = dataSnapshot.getKey();
                            count++;
                        }

                        userIds.add(dataSnapshot.getKey());
                        names.add(dataSnapshot.child("Name").getValue().toString());
                        true_count++;
                        while (true_count == 1) {
                            user_name_bottom.setText(dataSnapshot.child("Name").getValue().toString());
                            true_count++;
                        }
                        carduserName = dataSnapshot.child("Name").getValue().toString();
                        carduserId = dataSnapshot.getKey();
                        //checkUploads(carduserId, carduserName);
                        cards item = new cards(dataSnapshot.getKey(), "", dataSnapshot.child("profileImageUrl").getValue().toString());
                        rowItems.add(item);
                        //al.add("Domashi");
                        arrayAdapter.notifyDataSetChanged();
                        //progressDialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                        user_text_empty.setText("");
                    }

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkUploads(final String currentUserId, final String currentUserName) {

        final DatabaseReference picodatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Uploads");
        picodatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (dataSnapshot.exists()) {
                    names.add(currentUserName);
                    //Map<String, Object> map = (Map<String, Object>) dataSnapshot.child("Upload").getValue();
                    ImageUrl = dataSnapshot.child("Upload").getValue().toString();
                    cards item = new cards(currentUserId, "", ImageUrl);
                    rowItems.add(item);
                    //al.add("Domashi");
                    arrayAdapter.notifyDataSetChanged();
                    //progressDialog.dismiss();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
