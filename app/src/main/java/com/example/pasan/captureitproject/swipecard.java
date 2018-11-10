package com.example.pasan.captureitproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.Map;



    public class swipecard extends AppCompatActivity  {

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
        private Button back, message;
        private String messeage;

        String[] names = new String[45];
        ListView listView;
        List<cards> rowItems;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            progressDialog = new ProgressDialog(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_swipecard);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            currentUId = user.getUid();
            userDb = FirebaseDatabase.getInstance().getReference().child("Users");
            progressDialog.setMessage("Loading");
            progressDialog.show();
            checkUser();

            rowItems = new ArrayList<cards>();
            //al.add("Pasan");


            arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);


            SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

            flingContainer.setAdapter(arrayAdapter);
            progressDialog.dismiss();


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
                    //Do something on the left!
                    //You also have access to the original object.
                    //If you want to use it just cast it (String) dataObject
                    cards obj = (cards) dataObject;
                    String userId = obj.getUserId();
                    String userName = obj.getname();
                    userDb.child(userId).child("Connections").child("Nope").child(currentUId).setValue(true);

                    Toast.makeText(swipecard.this, "You removed " + userName + " from the List", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    cards obj = (cards) dataObject;
                    String userId = obj.getUserId();
                    userDb.child(userId).child("Connections").child("Yep").child(currentUId).setValue(true);

                    Toast.makeText(swipecard.this, "Added to the List", Toast.LENGTH_SHORT).show();
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
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    cards obj = (cards) dataObject;
                    messeage = obj.getUserId();
                    //Toast.makeText(swipecard.this, "LoveIt",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), userinfo.class);
                    intent.putExtra(EXTRA_MESSAGE, messeage);
                    startActivity(intent);
                    finish();
                }
            });

        }


        @Override
        public void onBackPressed() {
            //Execute your code here
            startActivity(new Intent(getApplicationContext(), navigation_interface.class));
            finish();

        }

        //
        public void checkUser() {


            final DatabaseReference pic_database = FirebaseDatabase.getInstance().getReference().child("Users");
            pic_database.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (dataSnapshot.exists() && !dataSnapshot.child("Connections").child("Nope").hasChild(currentUId) && (!dataSnapshot.getKey().equals(currentUId))) {
                        carduserName = dataSnapshot.child("Name").getValue().toString();
                        carduserId = dataSnapshot.getKey();
                        //checkUploads(carduserId, carduserName);
                        cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue().toString(), dataSnapshot.child("profileImageUrl").getValue().toString());
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

        public void checkUploads(final String currentUserId, final String currentUserName) {

            final DatabaseReference picodatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Uploads");
            picodatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (dataSnapshot.exists()) {

                        //Map<String, Object> map = (Map<String, Object>) dataSnapshot.child("Upload").getValue();
                        ImageUrl = dataSnapshot.child("Upload").getValue().toString();
                        cards item = new cards(currentUserId, currentUserName, ImageUrl);
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

        private void showToast() {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.lovetoast, null);
            Toast toast = new Toast(this);
            toast.setView(view);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }



    }
