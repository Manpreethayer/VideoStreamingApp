package com.example.hp.teacher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class UsersProfileActivity  extends AppCompatActivity  {

    private TextView display_name;
    private TextView profileBio,city_country;
    private TextView followers_count;
    private TextView followings_count;
    private SimpleDraweeView profile_image_circle;
    private Button follow_button,messageButton;
    private ImageButton backButton;
    private LinearLayout buttons_layout;

    private Long followers;
    private long followings;

    private AppBarLayout appBarLayout;




    private String current_state;

    private DatabaseReference databaseReference;
    private DatabaseReference followersDatabase;
    private DatabaseReference followingDatabase;


    private FirebaseUser current_user;


    private DatabaseReference notificationsreference;

    private DatabaseReference rootref;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        current_state="not_following";

        Fresco.initialize(this);


        final String profileUser_user_id=getIntent().getStringExtra("user_id");
        //toolbar set
       /* toolbar=(Toolbar) findViewById(R.id.profile_appbar);
        setSupportActionBar(toolbar);*/
        // getActionBar().setDisplayHomeAsUpEnabled(true);

        // firebase database
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(profileUser_user_id);
       followersDatabase=FirebaseDatabase.getInstance().getReference().child("followers");

       followers_count=(TextView) findViewById(R.id.user_profile_followers) ;




       messageButton=(Button) findViewById(R.id.users_profile_message_button);
       buttons_layout=(LinearLayout) findViewById(R.id.users_profile_message_follow_layout);








       /* appBarLayout=(AppBarLayout) findViewById(R.id.users_profile_appbar);
        toolbar=(Toolbar) findViewById(R.id.users_profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });*/

       /* backButton=(ImageButton) findViewById(R.id.users_profile_back_button);*/

        /*appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                                                    @Override
                                                    public void onStateChanged(AppBarLayout appBarLayout, State state) {

                                                        if(state.name().equals("COLLAPSED"))
                                                        {


                                                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
                                                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                                                            backButton.setVisibility(View.GONE);


                                                        }
                                                        if(state.name().equals("EXPANDED"))
                                                        {
                                                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
                                                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                                                            backButton.setVisibility(View.VISIBLE);


                                                        }




                                                    }
                                                });
*/


        notificationsreference=FirebaseDatabase.getInstance().getReference().child("notifications");
        rootref=FirebaseDatabase.getInstance().getReference();

        current_user= FirebaseAuth.getInstance().getCurrentUser();


        String current_user_id=current_user.getUid();

        followersDatabase.child(profileUser_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    followers=dataSnapshot.getChildrenCount();
                    followers_count.setText(followers.toString());
                }
                else
                {
                    followers_count.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        display_name=(TextView) findViewById(R.id.user_name_profile);
        city_country=(TextView) findViewById(R.id.profile_country_city);
        profileBio=(TextView)findViewById(R.id.profile_bio);
        follow_button=(Button)findViewById(R.id.send_request);
        profile_image_circle=(SimpleDraweeView) findViewById(R.id.profile_image_profile);



        if(profileUser_user_id.equals(current_user_id))
        {

            buttons_layout.setVisibility(View.GONE);

        }






        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String user_name=dataSnapshot.child("name").getValue().toString();
                String user_city=dataSnapshot.child("country").getValue().toString();

                final String user_image=dataSnapshot.child("image").getValue().toString();
                display_name.setText(user_name);
               // getSupportActionBar().setTitle(user_name);
                city_country.setText(user_city);
               // profile_status.setText(user_status);

                if(dataSnapshot.hasChild("bio"))
                {
                    String display_bio=dataSnapshot.child("bio").getValue().toString();
                    profileBio.setText(display_bio);
                    profileBio.setVisibility(View.VISIBLE);

                }

                Uri uriProfile = Uri.parse(user_image);
                profile_image_circle.setImageURI(uriProfile);



                  profile_image_circle.setOnClickListener(new View.OnClickListener() {
                     @Override
                  public void onClick(View view) {
                         Intent fullImageIntent=new Intent(UsersProfileActivity.this,FullImageActivity.class);
                         fullImageIntent.putExtra("image_url",user_image);
                         startActivity(fullImageIntent);
                }
                });



                messageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        messageUser(profileUser_user_id,user_name);

                    }
                });

                //-----------Checking follow and Unfollow--------


                followersDatabase.child(profileUser_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        if(dataSnapshot.hasChild(current_user.getUid()))
                        {
                            current_state="following";
                                follow_button.setText("Unfollow");
                        }

                        else
                        {
                            current_state="not_following";

                            follow_button.setText("Follow");

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                follow_button.setEnabled(false);

                //--------------------Not Friends State-------------

                if(current_state.equals("not_following"))
                {
                    DatabaseReference newNotificationsRef=rootref.child("notifications").child(profileUser_user_id).push();
                    String newNotificationId=newNotificationsRef.getKey();

                    HashMap<String,String> noticationsData=new HashMap<>();
                    noticationsData.put("from",current_user.getUid());
                    noticationsData.put("type","follow");

                    Map followmap = new HashMap();
                   // requestmap.put("friend_requests/" + current_user.getUid() + "/" + user_id + "/" + "request_type" ,"sent");
                    followmap.put("followers/" + profileUser_user_id + "/" + current_user.getUid() + "/" + "request_type" ,"followed");
                    followmap.put("following/"+current_user.getUid()+"/"+profileUser_user_id+"/"+"request_type","following");
                    followmap.put("notifications/" + profileUser_user_id + "/" + newNotificationId , noticationsData);

                    rootref.updateChildren(followmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {



                            if(databaseError != null)
                            {

                                Toast.makeText(UsersProfileActivity.this,"There Was Some Error Occured",Toast.LENGTH_LONG).show();
                            }else
                            {
                                follow_button.setEnabled(true);
                                Toast.makeText(UsersProfileActivity.this,"Following",Toast.LENGTH_SHORT).show();
                                current_state="Following";

                                follow_button.setText("Unfollow");
                            }



                        }
                    });
                }


                //--------------------Unfollow-------------


                if(current_state.equals("Following"))
                {
                    followingDatabase.child(current_user.getUid()).child(profileUser_user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    followersDatabase.child(profileUser_user_id).child(current_user.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    follow_button.setEnabled(true);
                                                    current_state="not_following";
                                                    Toast.makeText(UsersProfileActivity.this,"Unfollowed",Toast.LENGTH_SHORT).show();
                                                    follow_button.setText("Follow");

                                                }
                                            });
                                }
                            });
                }




            }
        });

    }

    private void messageUser(String profileUser_user_id, String user_name) {

        Intent chatIntent=new Intent(UsersProfileActivity.this,ChatActivity.class);
        chatIntent.putExtra("user_id", profileUser_user_id);
        chatIntent.putExtra("user_name", user_name);
        startActivity(chatIntent);
    }


}
