package com.example.hp.teacher.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hp.teacher.FragmentProfileVideosModel;
import com.example.hp.teacher.OnlineVideosModel;
import com.example.hp.teacher.R;
import com.example.hp.teacher.VideoPlayerActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView display_name;
    private TextView profileBio, city_country;
    private TextView followers_count;
    private TextView uploads_count;
    private TextView views_count;
    private SimpleDraweeView user_profile_image;
    private ImageButton profile_settings;

    private Long followers, uploads, views;

    private DatabaseReference usersDatabase, followersDatabase, uploadsDatabase, totalViewsDatabase;
    private FirebaseAuth firebaseAuth;
    private RecyclerView profileUploadsList;
    String userId;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        display_name = (TextView) view.findViewById(R.id.fragment_profile_user_name);
        profileBio = (TextView) view.findViewById(R.id.fragment_profile_bio);
        followers_count = (TextView) view.findViewById(R.id.fragment_followers_count);
        uploads_count = (TextView) view.findViewById(R.id.fragment_uploads_count);
        views_count = (TextView) view.findViewById(R.id.fragment_views_count);
        user_profile_image = (SimpleDraweeView) view.findViewById(R.id.fragment_profile_user_image);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        usersDatabase.keepSynced(true);

        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                final String displayName = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                //  String display_status=dataSnapshot.child("status").getValue().toString();
                // String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();


                if (dataSnapshot.hasChild("bio")) {
                    String display_bio = dataSnapshot.child("bio").getValue().toString();
                    profileBio.setText(display_bio);
                    profileBio.setVisibility(View.VISIBLE);

                }


                display_name.setText(displayName);
                //   accountStatus.setText(display_status);

                if (!image.equals("default")) {

                       /* Picasso.with(UserSettingsActivity.this)
                                .load(image)
                                .placeholder(R.mipmap.profile_avatar)
                                .into(user_image);
                                */

                    Uri uri = Uri.parse(image);
                    user_profile_image.setImageURI(uri);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followersDatabase = FirebaseDatabase.getInstance().getReference().child("followers");
        followersDatabase.keepSynced(true);

        followersDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    followers = dataSnapshot.getChildrenCount();
                    followers_count.setText(followers.toString());
                } else {
                    followers_count.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uploadsDatabase = FirebaseDatabase.getInstance().getReference().child("videos").child("users_uploads").child(userId);
        uploadsDatabase.keepSynced(true);

        uploadsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    uploads = dataSnapshot.getChildrenCount();
                    uploads_count.setText(uploads.toString());
                } else {
                    uploads_count.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        totalViewsDatabase = FirebaseDatabase.getInstance().getReference().child("videos").child("total_views").child(userId);
        totalViewsDatabase.keepSynced(true);

        totalViewsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    views = dataSnapshot.getChildrenCount();
                    views_count.setText(views.toString());
                } else {
                    views_count.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileUploadsList = (RecyclerView) view.findViewById(R.id.profile_uploads_list);


        LinearLayoutManager listLayoutManager = new LinearLayoutManager(getContext());
        profileUploadsList.setHasFixedSize(true);
        profileUploadsList.setLayoutManager(listLayoutManager);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<FragmentProfileVideosModel> options =
                new FirebaseRecyclerOptions.Builder<FragmentProfileVideosModel>()
                        .setQuery(uploadsDatabase, FragmentProfileVideosModel.class)
                        .build();

        FirebaseRecyclerAdapter<FragmentProfileVideosModel, ProfileFragment.ProfileVideosViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FragmentProfileVideosModel, ProfileVideosViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProfileFragment.ProfileVideosViewHolder holder, int position, @NonNull FragmentProfileVideosModel model) {

                holder.setTitleProfileVideo(model.getTitle());

                holder.setProfileVideoThumbImage(model.getThumb_image());


                final String push_key = getRef(position).getKey();

                // String current_user=firebaseAuth.getCurrentUser().getUid();

                final String user_id = getRef(position).getKey();

                final String video_uri = model.getVideo_url();
                final String video_title = model.getTitle();
                final String videoAbout = model.getVideo_about();
                final String video_Date = model.getUploaded_date();
                //final String videoTime=model.getUploadedTime();
                final String uploadedBy = model.getUploaded_by();






             /*   holder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        //go to chat activity
                        Intent videoIntent = new Intent(getContext(), VideoPlayerActivity.class);
                        videoIntent.putExtra("video_url",video_uri );
                        videoIntent.putExtra("video_title",video_title);
                        videoIntent.putExtra("video_about",videoAbout);
                        videoIntent.putExtra("video_date",video_Date);
                        videoIntent.putExtra("push_key",push_key);
                        videoIntent.putExtra("user_key",uploadedBy);
                        startActivity(videoIntent);



                    }
                });*/

             holder.userView.findViewById(R.id.more_button_profile_fragment).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                                     }
             });


            }


            @NonNull
            @Override
            public ProfileFragment.ProfileVideosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                final View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.fragment_profile_videos_layout, viewGroup, false);
                return new ProfileFragment.ProfileVideosViewHolder(view);

            }
        };
        firebaseRecyclerAdapter.startListening();
        profileUploadsList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class ProfileVideosViewHolder extends RecyclerView.ViewHolder {
        View userView;


        ProfileVideosViewHolder(@NonNull View itemView) {
            super(itemView);

            userView = itemView;
        }

        void setTitleProfileVideo(String name) {
            TextView videoTitle = (TextView) userView.findViewById(R.id.profile_fragment_video_title);
            videoTitle.setText(name);
        }


        void setProfileVideoThumbImage(String image) {

            Uri uri = Uri.parse(image);
            SimpleDraweeView videoThumbImage = (SimpleDraweeView) userView.findViewById(R.id.profile_fragment_video_image);
            videoThumbImage.setImageURI(uri);

        }




    }


}
