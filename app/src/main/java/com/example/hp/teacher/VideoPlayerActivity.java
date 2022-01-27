package com.example.hp.teacher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class VideoPlayerActivity extends AppCompatActivity implements PlayerControlView.OnClickListener{

    private PlayerView playerView;
    private SimpleExoPlayer player;
    String video_uri,video_Title,video_About,uploaded_By,push_Key,video_Date,videoThumb;
    Uri url_video;
    private ImageButton closeInfoButton,likeButton,saveVideoButton,closeCommentsButton,postComment;
    private ConstraintLayout commentsLayout;
    private ScrollView infoLayout;
    private TextView videoTitle,videoAbout,videoDate,userName,exoplayerVideoTitle,exoplayerVideoLikes;
    private SimpleDraweeView userImage;
    private TextView playerLikesText,playerViewsText,playerCommentsText,noComments;
    private EditText commentsEditext;
    private String playbackState;

    private FirebaseUser firebaseUser;
    private String currentUserId;
    private boolean videoLiked;
    private boolean videoSaved;
    private Long likesCount,viewsCount,commentsCount;
    private DatabaseReference databaseReference,likesdatabase,viewsDatabase,totalviewsDatabase,playlistDatabase,recentlyPlayedDatabase,likedDatabase,savedDatabase,commentsDatabase;


    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;


    // Saved instance state keys.
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        setContentView(R.layout.activity_video_player);

        Fresco.initialize(getApplicationContext());

        video_uri=getIntent().getStringExtra("video_url");
        url_video=Uri.parse(video_uri);
        video_Title=getIntent().getStringExtra("video_title");
        video_Date=getIntent().getStringExtra("video_date");
        video_About=getIntent().getStringExtra("video_about");
        uploaded_By=getIntent().getStringExtra("user_key");
        push_Key=getIntent().getStringExtra("push_key");
        videoThumb=getIntent().getStringExtra("thumb_image");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }


        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        currentUserId=firebaseUser.getUid();

        playerView=(PlayerView) findViewById(R.id.player_view);
        closeInfoButton=(ImageButton) findViewById(R.id.info_close_button);
        likeButton=(ImageButton) playerView.findViewById(R.id.video_like);
        exoplayerVideoLikes=(TextView) playerView.findViewById(R.id.exoplayer_likes_text);
        exoplayerVideoTitle=(TextView) playerView.findViewById(R.id.video_title_exoplayer);
        saveVideoButton=(ImageButton) playerView.findViewById(R.id.video_save);
        videoTitle=(TextView) findViewById(R.id.video_title_player);
        videoAbout=(TextView) findViewById(R.id.video_about_player);
        videoDate=(TextView) findViewById(R.id.video_date_player);
        userName=(TextView) findViewById(R.id.player_user_name);
        playerLikesText = (TextView) findViewById(R.id.video_likes_player);
        userImage=(SimpleDraweeView) findViewById(R.id.player_user_image);
        playerViewsText=(TextView) findViewById(R.id.video_views_player);
        commentsLayout=(ConstraintLayout) findViewById(R.id.video_player_comments_layout);
        closeCommentsButton=(ImageButton) findViewById(R.id.comments_close_button);
        commentsEditext=(EditText) findViewById(R.id.comments_edit_text);
        postComment=(ImageButton) findViewById(R.id.post_comment);
        playerCommentsText=(TextView) findViewById(R.id.comments_count_text);
        noComments=(TextView)  findViewById(R.id.video_player_no_comments);


        videoTitle.setText(video_Title);
        exoplayerVideoTitle.setText(video_Title);

        viewsDatabase=FirebaseDatabase.getInstance().getReference().child("videos").child("allvideos").child(push_Key).child("views");
        viewsDatabase.keepSynced(true);
        viewsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    viewsCount=dataSnapshot.getChildrenCount();
                    playerViewsText.setText(viewsCount.toString()+" Views");

                }
                else
                {


                    playerViewsText.setText("0 Views");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        likesdatabase= FirebaseDatabase.getInstance().getReference().child("videos").child("allvideos").child(push_Key).child("likes");
        likesdatabase.keepSynced(true);
        likesdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    likesCount=dataSnapshot.getChildrenCount();

                    playerLikesText.setText(likesCount.toString()+" Likes");
                    exoplayerVideoLikes.setText(likesCount.toString());
                    if(dataSnapshot.hasChild(uploaded_By))
                    {
                        videoLiked=true;
                        likeButton.setImageResource(R.drawable.liked_icon);

                    }
                    else
                    {
                        videoLiked=false;
                        likeButton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                    }
                }
                else
                {
                    videoLiked=false;
                    likeButton.setImageResource(R.drawable.ic_thumb_up_black_24dp);

                    playerLikesText.setText("0 Likes");
                    exoplayerVideoLikes.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        commentsDatabase=FirebaseDatabase.getInstance().getReference().child("videos").child("allvideos").child(push_Key).child("comments");
        commentsDatabase.keepSynced(true);
        commentsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    commentsCount=dataSnapshot.getChildrenCount();
                    playerCommentsText.setText(commentsCount.toString());

                }
                else
                {


                    playerCommentsText.setText("0");
                    noComments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uploaded_By);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user_name=dataSnapshot.child("name").getValue().toString();

                final String user_image=dataSnapshot.child("image").getValue().toString();
                userName.setText(user_name);


                Uri uriProfile = Uri.parse(user_image);
                userImage.setImageURI(uriProfile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String uploadedOn="Uploaded on "+video_Date+" at "+video_Date;


        //setters

        videoTitle.setText(video_Title);
        videoAbout.setText(video_About);
        videoDate.setText(uploadedOn);



        infoLayout=(ScrollView) findViewById(R.id.video_info_layout);

        closeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideInfo();

            }
        });


        closeCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideComments();
            }
        });












        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }

        playlistDatabase=FirebaseDatabase.getInstance().getReference().child("videos").child("playlists");
        savedDatabase=playlistDatabase.child(currentUserId).child("saved_videos");
        savedDatabase.child("playlist_videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    if(dataSnapshot.hasChild(push_Key))
                    {
                        videoSaved=true;
                        saveVideoButton.setImageResource(R.drawable.ic_bookmark_black_24dp);

                    }
                    else
                    {
                        videoSaved=false;
                        saveVideoButton.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    }
                }
                else
                {
                    videoSaved=false;
                    saveVideoButton.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             String comment=   commentsEditext.getText().toString().trim();
             commentsEditext.setEnabled(false);

             if(!comment.isEmpty()) {
                 postCommentFunction(comment);
             }
            }
        });


    }

    private void hideInfo() {

        infoLayout.setVisibility(View.GONE);
        playerView.showController();
    }

    private void postCommentFunction(String comment)
    {

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        final String time= DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

        final HashMap<String,String> commentsData=new HashMap<>();
        commentsData.put("comment",comment);
        commentsData.put("comment_date",date);
        commentsData.put("comment_time",time);
        commentsData.put("user_id",currentUserId);


        commentsDatabase.push().setValue(commentsData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                   commentsEditext.setText(null);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void hideComments() {

        commentsLayout.setVisibility(View.GONE);
        playerView.showController();
    }
    @Override
    protected void onStart() {
        super.onStart();

        player= ExoPlayerFactory.newSimpleInstance(this,new DefaultTrackSelector());
        playerView.setPlayer(player);


        DefaultDataSourceFactory defaultDataSourceFactory=new DefaultDataSourceFactory(this,Util.getUserAgent(this,"imagery"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(url_video);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);


           viewsDatabase.push().setValue("viewed");

        totalviewsDatabase=FirebaseDatabase.getInstance().getReference().child("videos").child("total_views").child(uploaded_By);
        totalviewsDatabase.push().setValue("viewed");


        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition);
        }

        final HashMap<String,String> videodata=new HashMap<>();
        videodata.put("title",video_Title);
        videodata.put("thumb_image",videoThumb);
        videodata.put("video_about",video_About);
        videodata.put("video_url",video_uri);
        videodata.put("uploaded_by",uploaded_By);
        videodata.put("uploaded_date",video_Date);

        final Map recentlyPlayedData=new HashMap<>();
        recentlyPlayedData.put("playlist_title","Recently Played");
        recentlyPlayedData.put("playlist_preview",videoThumb);
        recentlyPlayedData.put("playlist_videos/"+push_Key+"/",videodata);


        recentlyPlayedDatabase=playlistDatabase.child(currentUserId).child("recently_played");
        recentlyPlayedDatabase.updateChildren(recentlyPlayedData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {



                if(databaseError != null)
                {


                }


            }
        });




    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        player.release();
        clearStartPosition();
        setIntent(intent);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();
        playerView.setPlayer(null);
        player.release();
        player=null;
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
           case R.id.video_add_to_playlist:
               Toast.makeText(VideoPlayerActivity.this,"Added",Toast.LENGTH_SHORT).show();
               break;
            case R.id.video_like:
                workOnLikes(videoLiked);
                break;
            case R.id.video_info:
                infoLayout.setVisibility(View.VISIBLE);
                playerView.hideController();
                break;
            case R.id.video_save:
                performSaveVideo();
                break;
            case R.id.video_share:
                Toast.makeText(VideoPlayerActivity.this,"shared",Toast.LENGTH_SHORT).show();
                break;
            case R.id.video_comments:
                commentsLayout.setVisibility(View.VISIBLE);
                playerView.hideController();
                break;
            case R.id.closeButtonVideo:
                onBackPressed();
                break;

        }


    }

    private void performSaveVideo() {


        if(videoSaved)
        {
            savedDatabase.child("playlist_videos").child(push_Key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        saveVideoButton.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                    }

                }
            });
        }
        else
        {
            final HashMap<String,String> videodata=new HashMap<>();
                        videodata.put("title",video_Title);
                        videodata.put("thumb_image",videoThumb);
                        videodata.put("video_about",video_About);
                        videodata.put("video_url",video_uri);
                        videodata.put("uploaded_by",uploaded_By);
                        videodata.put("uploaded_date",video_Date);

                        final Map savedData=new HashMap<>();
            savedData.put("playlist_title","Saved");
            savedData.put("playlist_preview",videoThumb);
            savedData.put("playlist_videos/"+push_Key+"/",videodata);



                        savedDatabase.updateChildren(savedData, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {



                                if(databaseError != null)
                                {


                                }
                                else
                                {
                                    saveVideoButton.setImageResource(R.drawable.ic_bookmark_black_24dp);
                                }


                            }
                        });




        }

    }

    private void workOnLikes(final boolean videoLiked) {
        likedDatabase=playlistDatabase.child(currentUserId).child("liked_videos");

        if(videoLiked)
        {
          likesdatabase.child(uploaded_By).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {

                  if (task.isSuccessful())
                  {
                     likeButton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                     likedDatabase.child("playlist_videos").child(push_Key).removeValue();
                  }

              }
          });
        }
        else
        {
            likesdatabase.child(uploaded_By).setValue("liked").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        likeButton.setImageResource(R.drawable.liked_icon);

                        final HashMap<String,String> videodata=new HashMap<>();
                        videodata.put("title",video_Title);
                        videodata.put("thumb_image",videoThumb);
                        videodata.put("video_about",video_About);
                        videodata.put("video_url",video_uri);
                        videodata.put("uploaded_by",uploaded_By);
                        videodata.put("uploaded_date",video_Date);

                        final Map likedData=new HashMap<>();
                        likedData.put("playlist_title","Liked");
                        likedData.put("playlist_preview",videoThumb);
                        likedData.put("playlist_videos/"+push_Key+"/",videodata);



                        likedDatabase.updateChildren(likedData, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {



                                if(databaseError != null)
                                {


                                }


                            }
                        });
                    }

                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }



}