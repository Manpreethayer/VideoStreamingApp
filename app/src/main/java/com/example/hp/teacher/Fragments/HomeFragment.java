package com.example.hp.teacher.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hp.teacher.OnlineVideosModel;
import com.example.hp.teacher.R;
import com.example.hp.teacher.SelectVideo;
import com.example.hp.teacher.VideoPlayerActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView videosList;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView sorryText;
    private ProgressBar progressBar;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_blogs, container, false);

        Fresco.initialize(Objects.requireNonNull(getContext()));

        databaseReference= FirebaseDatabase.getInstance().getReference().child("videos").child("allvideos");
        databaseReference.keepSynced(true);
        firebaseAuth=FirebaseAuth.getInstance();

        videosList=(RecyclerView) view.findViewById(R.id.online_videos_list);


        sorryText=(TextView) view.findViewById(R.id.allvideosfragment_sorryText);
        progressBar=(ProgressBar) view.findViewById(R.id.progressBarFragmentAllVideos);
        progressBar.setVisibility(View.VISIBLE);

        LinearLayoutManager listLayoutManager = new LinearLayoutManager(getContext());
        videosList.setHasFixedSize(true);
        videosList.setLayoutManager(listLayoutManager);



        ImageButton videosShow = (ImageButton) view.findViewById(R.id.add_video);

        videosShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent videoSelectIntent=new Intent(getActivity(), SelectVideo.class);
                startActivity(videoSelectIntent);
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    FirebaseRecyclerOptions<OnlineVideosModel> options =
                            new FirebaseRecyclerOptions.Builder<OnlineVideosModel>()
                                    .setQuery(databaseReference, OnlineVideosModel.class)
                                    .build();

                    FirebaseRecyclerAdapter<OnlineVideosModel, HomeFragment.VideosViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<OnlineVideosModel, VideosViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull HomeFragment.VideosViewHolder holder, int position, @NonNull OnlineVideosModel model) {

                            holder.setTitle(model.getTitle());


                            holder.setThumbImage(model.getThumb_image());
                            holder.setChannelName(model.getUploaded_by());



                            final String push_key=getRef(position).getKey();
                            holder.setVideoViews(push_key);

                            String current_user=firebaseAuth.getCurrentUser().getUid();

                            final String user_id=getRef(position).getKey();

                            final String video_uri=model.getVideo_url();
                            final String video_title=model.getTitle();
                            final String videoAbout=model.getVideo_about();
                            final String video_Date=model.getUploaded_date();
                            final String video_thumb=model.getThumb_image();
                            final String uploadedBy=model.getUploaded_by();






                            holder.userView.setOnClickListener(new View.OnClickListener() {
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
                                    videoIntent.putExtra("thumb_image",video_thumb);
                                    startActivity(videoIntent);



                                }
                            });



                        }


                        @NonNull
                        @Override
                        public HomeFragment.VideosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                            View view = LayoutInflater.from(viewGroup.getContext())
                                    .inflate(R.layout.online_videos_layout, viewGroup, false);

                            return new HomeFragment.VideosViewHolder(view);

                        }
                    };
                    firebaseRecyclerAdapter.startListening();
                    progressBar.setVisibility(View.GONE);
                    videosList.setAdapter(firebaseRecyclerAdapter);
                }
                else
                {

                    progressBar.setVisibility(View.GONE);
                    sorryText.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public static class VideosViewHolder extends RecyclerView.ViewHolder
    {
        View userView;


        VideosViewHolder(@NonNull View itemView) {
            super(itemView);

            userView=itemView;
        }

        public void setTitle(String name)
        {
            TextView videoTitle=(TextView) userView.findViewById(R.id.online_video_title);
            videoTitle.setText(name);
        }

         void setChannelName(String channel_id)
        {
            final TextView videoChannel=(TextView) userView.findViewById(R.id.online_video_channel_name);
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(channel_id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    videoChannel.setText(dataSnapshot.child("name").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        void setThumbImage(String image)
        {

            Uri uri = Uri.parse(image);
            SimpleDraweeView videoThumbImage = (SimpleDraweeView) userView.findViewById(R.id.online_video_thumbnail);
            videoThumbImage.setImageURI(uri);

        }


         void setVideoViews(String push_key) {

            final TextView viewsText=(TextView) userView.findViewById(R.id.online_video_views);
            DatabaseReference viewsDatabase=FirebaseDatabase.getInstance().getReference().child("videos").child("allvideos").child(push_key).child("views");
             viewsDatabase.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if(dataSnapshot.exists())
                     {
                       long viewsCount=dataSnapshot.getChildrenCount();
                       String views=viewsCount+" views";
                       viewsText.setText(views);

                     }
                     else
                     {

                         String noViews="0 views";

                         viewsText.setText(noViews);
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });


        }
    }



}
