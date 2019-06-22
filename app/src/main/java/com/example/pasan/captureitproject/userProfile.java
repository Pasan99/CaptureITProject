package com.example.pasan.captureitproject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.N)
public class userProfile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "userinfo";
    private ProgressDialog progressDialog;

    // declaration of views
    private ImageView userImage, userImage2;
    private TextView userName, user_name_top, description, language, mobile, occupations;
    private ConstraintLayout linearLayout3;
    private RecyclerView recyclerView;
    private CircleImageView settings_button;
    private ConstraintLayout user_profile_layout;
    private Button upload_button;
    private Button editButton;

    // Firebase
    private DatabaseReference imagedata;
    private FirebaseAuth moAuth;

    // user details
    private String userId;
    private String currentUId;
    private String message;

    public static String user_name = " ";
    public static String user_language = " ";
    public static String user_mobile = " ";
    public static String user_description = " ";


    String userType = "Photographers";
    //
    // private StringJoiner oc = new StringJoiner(" ");

    // Animations
    Animation uptoDown;
    Animation downtoTop;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // definition of all views
        recyclerView = findViewById(R.id.u_p_recycleView);
        linearLayout3 = findViewById(R.id.linearLayout4);
        settings_button = findViewById(R.id.settings_button);
        user_profile_layout = findViewById(R.id.user_profile_layout);
        upload_button = findViewById(R.id.upload_button);
        description = findViewById(R.id.u_p_description);
        language = findViewById(R.id.language);
        mobile = findViewById(R.id.textView18);
        occupations = findViewById(R.id.textView99);
        userImage = findViewById(R.id.u_p_userImage);
        userName = findViewById(R.id.u_p_user_name);
        user_name_top = findViewById(R.id.user_name_top);
        userImage2 = findViewById(R.id.profile_image3);
        editButton = findViewById(R.id.u_p_edit);

        // set the profile image
        Glide.with(userProfile.this).load(Feeds.imageUrl).into(userImage);

        // Firebase
        moAuth = FirebaseAuth.getInstance();
        currentUId = moAuth.getCurrentUser().getUid();

        message = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Animations
        uptoDown = AnimationUtils.loadAnimation(this, R.anim.fade);
        downtoTop = AnimationUtils.loadAnimation(this, R.anim.anim3);
        recyclerView.setAnimation(downtoTop);

        // make progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        checkUserType();

        // choose the firebase instances on selected user
        imagedata = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(message);
        imagedata.keepSynced(true);

        // upload the photo to main image at the top of the intent
        setUserInfo();

        // close progressdialog
        progressDialog.dismiss();

        // listeners
        userImage.setOnClickListener(this);
        editButton.setOnClickListener(this);
        settings_button.setOnClickListener(this);
        upload_button.setOnClickListener(this);

        addOccupations();

    }

    private void checkUserType() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User Type").child("Customers");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if ( dataSnapshot.exists() && Objects.equals(dataSnapshot.getKey(), message)){
                    userType = "Customers";

                    // choose the firebase instances on selected user
                    imagedata = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(message);

                    // upload the photo to main image at the top of the intent
                    setUserInfo();
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addOccupations(){
            //String k = oc.toString();
            //occupations.setText(k);
    }


    public void setUserInfo(){
        imagedata.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ) {
                    userId = dataSnapshot.getKey();
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    assert map != null;
                    if ( map.get("Description") != null){
                        user_description = Objects.requireNonNull(map.get("Description")).toString();
                        description.setText(user_description);
                    }
                    if ( map.get("Mobile") != null){
                        user_mobile = Objects.requireNonNull(map.get("Mobile")).toString();
                        mobile.setText(user_mobile);
                    }
                    if (map.get("User Name") != null){
                        user_name = Objects.requireNonNull(map.get("User Name")).toString();
                        userName.setText(user_name);
                    }
                    if ( dataSnapshot.child("Language").exists()) {
                        if (map.get("Language") != null) {
                            user_language = Objects.requireNonNull(map.get("Language")).toString();
                            language.setText(Objects.requireNonNull(map.get("Language")).toString());
                        }
                    }
                    addOccupations();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if ( v == userImage ){
            Intent intent = new Intent(getApplicationContext(), profilesetupAct.class );


            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair< View, String > (userImage, "profile_photo");
            pairs[1] = new Pair< View, String >(linearLayout3, "gradiant_layout");

            try {
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(userProfile.this , pairs);
                startActivity(intent , activityOptions.toBundle());
            }catch (Exception i ){
                Toast.makeText(getApplicationContext(), "Not Worked" + i, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }

        if ( v == editButton ){
            Intent intent = new Intent(userProfile.this, profilesetupAct.class );


            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair< View, String > (userImage, "profile_photo");
            pairs[1] = new Pair< View, String >(linearLayout3, "gradiant_layout");

            try {
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(userProfile.this , pairs);
                startActivity(intent , activityOptions.toBundle());
            }catch (Exception i ){
                Toast.makeText(getApplicationContext(), "Not Worked" + i, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }

        if ( v == settings_button ){
            Intent intent = new Intent(userProfile.this, settings.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        if ( v == upload_button ){
            Intent intent = new Intent(getApplicationContext(), photoUpload.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
