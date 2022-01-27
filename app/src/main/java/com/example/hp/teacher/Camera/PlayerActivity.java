package com.example.hp.teacher.Camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.hp.teacher.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class PlayerActivity extends Activity {

    public static String INTENT_URI = "player_uri";

    private VideoView mVideoView;

    private ImageButton sendVideo;
    private Dialog dialog;

    private String videoPath;

    //firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootDatabaseReference,storiesDatabase;
    private StorageReference rootStorageReference,storiesStorage;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

         videoPath = getIntent().getStringExtra(INTENT_URI);

        setContentView(R.layout.activity_player);

        mVideoView = (VideoView) findViewById(R.id.video_view);

        firebaseAuth=FirebaseAuth.getInstance();
        rootStorageReference= FirebaseStorage.getInstance().getReference();
        rootDatabaseReference= FirebaseDatabase.getInstance().getReference();

        dialog=new Dialog(this);


        currentUserId=firebaseAuth.getCurrentUser().getUid();


        //Creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);

        //specify the location of media file
        Uri uri = Uri.parse(videoPath);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        //Setting MediaController and URI, then starting the videoView
        mVideoView.setMediaController(mediaController);
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();


        sendVideo=(ImageButton) findViewById(R.id.send_video);

        sendVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopup();
                saveToStories();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null && !mVideoView.isPlaying())
            mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void saveToStories() {
        storiesDatabase= rootDatabaseReference.child("stories").child(currentUserId).push();
        rootDatabaseReference.keepSynced(true);
        final String pushkey=rootDatabaseReference.push().getKey();

        storiesStorage=rootStorageReference.child("stories").child(currentUserId).child(pushkey+".vid");




        File videoFile=new File(videoPath);

        Uri videoUri=Uri.fromFile(videoFile);



        storiesStorage.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {
                    rootStorageReference.child("stories").child(currentUserId).child(pushkey+".vid").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {

                            final String downloadStoriesUrl = uri.toString();

                            Map updateHashMap=new HashMap<>();
                            updateHashMap.put("imageStory",downloadStoriesUrl);
                            updateHashMap.put("time_ago", ServerValue.TIMESTAMP);
                            updateHashMap.put("type","video");

                            storiesDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {

                                        dialog.dismiss();

                                        Snackbar.make(findViewById(android.R.id.content),"Uploaded Successfully",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                        // Toast.makeText(UserSettingsActivity.this , "Uploaded Succesfully" , Toast.LENGTH_LONG).show();
                                    }

                                    else
                                    {
                                        dialog.dismiss();
                                        String error=task.getException().getMessage();
                                        Toast.makeText(PlayerActivity.this, error,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });




                        }

                    });

                }
                else
                {
                    dialog.dismiss();
                    String error=task.getException().getMessage();
                    Toast.makeText(PlayerActivity.this, error,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showpopup() {



        dialog.setContentView(R.layout.popup_dialog_signup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();



    }

}

