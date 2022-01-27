package com.example.hp.teacher.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hp.teacher.BioActivty;
import com.example.hp.teacher.R;
import com.example.hp.teacher.StartupActivity;
import com.example.hp.teacher.UserNameActivity;
import com.example.hp.teacher.UserSettingsActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private CardView logout;
    private CardView accountUserInfo,changeUsername,changeBio;
    private SimpleDraweeView accountUserImage;
    private TextView accountDisplayName;
    private TextView userLoggedPhone;

    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private DatabaseReference databaseReference;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account, container, false);

        Fresco.initialize(getContext());

        accountUserInfo=(CardView) view.findViewById(R.id.account_user_info);
        accountUserImage=(SimpleDraweeView) view.findViewById(R.id.account_profile_image);
        accountDisplayName=(TextView) view.findViewById(R.id.account_user_name);




        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();


        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        databaseReference.keepSynced(true);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name=dataSnapshot.child("name").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
              //  String display_status=dataSnapshot.child("status").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();




               accountDisplayName.setText(display_name);
             //   accountStatus.setText(display_status);

                if (!image.equals("default")) {

                       /* Picasso.with(UserSettingsActivity.this)
                                .load(image)
                                .placeholder(R.mipmap.profile_avatar)
                                .into(user_image);
                                */

                    Uri uri = Uri.parse(thumb_image);
                    accountUserImage.setImageURI(uri);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        accountUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getContext(),UserSettingsActivity.class);
                startActivity(intent);

            }
        });



        changeUsername=(CardView) view.findViewById(R.id.account_username_change);
        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getContext(),UserNameActivity.class);
                startActivity(intent);

            }
        });

        changeBio=(CardView) view.findViewById(R.id.account_bio_change);
        changeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getContext(),BioActivty.class);
                startActivity(intent);

            }
        });

        logout=(CardView) view.findViewById(R.id.account_user_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getActivity(),StartupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;


    }

}
