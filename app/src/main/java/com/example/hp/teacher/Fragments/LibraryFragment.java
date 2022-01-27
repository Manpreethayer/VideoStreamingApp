package com.example.hp.teacher.Fragments;


import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hp.teacher.OnlineVideosModel;
import com.example.hp.teacher.PlaylistModel;
import com.example.hp.teacher.PlaylistSongsActivity;
import com.example.hp.teacher.SelectMusicActivity;
import com.example.hp.teacher.R;
import com.example.hp.teacher.VideoPlayerActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {

    private ImageButton songsShow;
    private RecyclerView libraryList;
    private DatabaseReference playlistsDatabase;
    private FirebaseUser firebaseUser;
   private String userId;
    private ProgressBar progressBar;
    private TextView noPlaylistMessage;
    private String playlistTitle,previewImage,playlistPath;

    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_tube, container, false);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        userId=firebaseUser.getUid();
        Fresco.initialize(Objects.requireNonNull(getContext()));

        playlistsDatabase= FirebaseDatabase.getInstance().getReference().child("videos").child("playlists").child(userId);
        playlistsDatabase.keepSynced(true);

        libraryList=(RecyclerView) view.findViewById(R.id.playlist_list);

        noPlaylistMessage=(TextView) view.findViewById(R.id.no_playlist_message) ;

        progressBar=(ProgressBar) view.findViewById(R.id.progressBarLibraryFragment);
        progressBar.setVisibility(View.VISIBLE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),   2);
        libraryList.setHasFixedSize(true);
        libraryList.setLayoutManager(gridLayoutManager);

        songsShow=(ImageButton)view.findViewById(R.id.add_music);

        songsShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SelectMusicActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        playlistsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    FirebaseRecyclerOptions<PlaylistModel> options =
                            new FirebaseRecyclerOptions.Builder<PlaylistModel>()
                                    .setQuery(playlistsDatabase, PlaylistModel.class)
                                    .build();

                    FirebaseRecyclerAdapter<PlaylistModel, LibraryFragment.PlaylistViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PlaylistModel, PlaylistViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull LibraryFragment.PlaylistViewHolder holder, int position, @NonNull PlaylistModel model) {

                            holder.setTitle(model.getPlaylist_title());


                            holder.setThumbImage(model.getPlaylist_preview());

                            playlistTitle=model.getPlaylist_title();
                            previewImage=model.getPlaylist_preview();




                            final String push_key=getRef(position).getKey();

                            holder.setVideosCount(push_key,userId);


                            holder.userView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                        //go to chat activity
                        Intent playlistIntent = new Intent(getContext(), PlaylistSongsActivity.class);
                                    playlistIntent.putExtra("playlist_title",playlistTitle);
                                    playlistIntent.putExtra("playlist_key",push_key);
                                    playlistIntent.putExtra("user_id",userId);
                                    playlistIntent.putExtra("thumb_image",previewImage);
                        startActivity(playlistIntent);



                                }
                            });



                        }


                        @NonNull
                        @Override
                        public LibraryFragment.PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                            View view = LayoutInflater.from(viewGroup.getContext())
                                    .inflate(R.layout.playlist_layout, viewGroup, false);

                            return new LibraryFragment.PlaylistViewHolder(view);

                        }
                    };
                    firebaseRecyclerAdapter.startListening();
                    progressBar.setVisibility(View.GONE);
                    libraryList.setAdapter(firebaseRecyclerAdapter);
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    noPlaylistMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public static class PlaylistViewHolder extends RecyclerView.ViewHolder
    {
        View userView;


        PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            userView=itemView;
        }

        public void setTitle(String title)
        {
            TextView playlistTitle=(TextView) userView.findViewById(R.id.playlist_title);
            playlistTitle.setText(title);
        }


        void setThumbImage(String image)
        {

            Uri uri = Uri.parse(image);
            SimpleDraweeView playlistPreviewImage = (SimpleDraweeView) userView.findViewById(R.id.playlist_preview_image);
            playlistPreviewImage.setImageURI(uri);

        }


         void setVideosCount(String playlist_key,String user_id) {

             final TextView playlistVideosCount=(TextView) userView.findViewById(R.id.playlist_videos_count);
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("videos").child("playlists").child(user_id).child(playlist_key).child("playlist_videos");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        long videosCount=dataSnapshot.getChildrenCount();
                        String videos=videosCount+" videos";
                        playlistVideosCount.setText(videos);

                    }
                    else
                    {

                        String noVideos="0 videos";

                        playlistVideosCount.setText(noVideos);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}
