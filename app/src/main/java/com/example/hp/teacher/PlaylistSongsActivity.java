package com.example.hp.teacher;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlaylistSongsActivity extends AppCompatActivity {

    private DatabaseReference playlistVideosDatabase;
    private Toolbar playlistVideosToolbar;
    private RecyclerView playlistVideosList;
    private TextView sorryText;
    private FirebaseAuth firebaseAuth;
    private String playlistTitle,playlistKey,playlistPreview,user_id;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);
        playlistTitle=getIntent().getStringExtra("playlist_title");
        playlistKey=getIntent().getStringExtra("playlist_key");
        playlistPreview=getIntent().getStringExtra("thumb_image");
        user_id=getIntent().getStringExtra("user_id");

        sorryText=(TextView) findViewById(R.id.playlist_videos_sorryText);
        playlistVideosList=(RecyclerView)findViewById(R.id.playlist_videos_list);
        progressBar=(ProgressBar) findViewById(R.id.progressBarPlaylistVideos);
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth= FirebaseAuth.getInstance();
        LinearLayoutManager listLayoutManager = new LinearLayoutManager(getApplicationContext());
        playlistVideosList.setHasFixedSize(true);
        playlistVideosList.setLayoutManager(listLayoutManager);


        playlistVideosToolbar = (Toolbar) findViewById(R.id.playlist_videos_toolbar);

        setSupportActionBar(playlistVideosToolbar);

        getSupportActionBar().setTitle(playlistTitle);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        playlistVideosToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        playlistVideosDatabase= FirebaseDatabase.getInstance().getReference().child("videos").child("playlists").child(user_id).child(playlistKey).child("playlist_videos");
        playlistVideosDatabase.keepSynced(true);
    }


    @Override
    public void onStart() {
        super.onStart();

        playlistVideosDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    FirebaseRecyclerOptions<OnlineVideosModel> options =
                            new FirebaseRecyclerOptions.Builder<OnlineVideosModel>()
                                    .setQuery(playlistVideosDatabase, OnlineVideosModel.class)
                                    .build();

                    FirebaseRecyclerAdapter<OnlineVideosModel, PlaylistSongsActivity.PlaylistSongsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<OnlineVideosModel, PlaylistSongsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull PlaylistSongsActivity.PlaylistSongsViewHolder holder, int position, @NonNull OnlineVideosModel model) {

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
                                    Intent videoIntent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
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
                        public PlaylistSongsActivity.PlaylistSongsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                            View view = LayoutInflater.from(viewGroup.getContext())
                                    .inflate(R.layout.online_videos_layout, viewGroup, false);

                            return new PlaylistSongsActivity.PlaylistSongsViewHolder(view);

                        }
                    };
                    firebaseRecyclerAdapter.startListening();
                    progressBar.setVisibility(View.GONE);
                    playlistVideosList.setAdapter(firebaseRecyclerAdapter);
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


    public static class PlaylistSongsViewHolder extends RecyclerView.ViewHolder
    {
        View userView;


        PlaylistSongsViewHolder(@NonNull View itemView) {
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
