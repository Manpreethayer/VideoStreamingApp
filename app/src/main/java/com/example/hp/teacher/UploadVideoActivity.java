package com.example.hp.teacher;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitHeader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class UploadVideoActivity extends AppCompatActivity {

    private SimpleDraweeView preview1,preview2,preview3,preview4,preview5,preview6,preview7,selectedpreview;
    private TextInputLayout titleText,aboutText,tagsText;
    private Button uploadButton;
    private Bitmap currentPreview;
    private ConstraintLayout progresslayout;
    private ProgressBar progressBar,miniProgress;
    private TextView sizeinMB,uploadedPercentage;
    private Toolbar uploadVideoToolbar;
    private TextView successLabel;
    String channelName;
   // private String videoTime=null;


    private StorageReference rootStorage,videosStorage,videosthumbStorage;
    private DatabaseReference rootDatabase,videosDatabase,allvideosdatabase,tagsDatabase,database;
    String currentUserId;
   // private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        final String videoUri=getIntent().getStringExtra("videoUri");
        final String videoDuration=getIntent().getStringExtra("video_duration");
        /*if(!videoDuration.isEmpty())
            videoTime=VideoTimeLemgth.convertMillis(videoDuration);*/


        uploadVideoToolbar = (Toolbar) findViewById(R.id.upload_video_Toolbar);

        setSupportActionBar(uploadVideoToolbar);

        getSupportActionBar().setTitle("Upload Video");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uploadVideoToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });





        miniProgress=(ProgressBar)findViewById(R.id.min_progress_video_upload);
        preview1=(SimpleDraweeView) findViewById(R.id.videoSelectImage1);
        preview2=(SimpleDraweeView) findViewById(R.id.videoSelectImage2);
        preview3=(SimpleDraweeView) findViewById(R.id.videoSelectImage3);
        preview4=(SimpleDraweeView) findViewById(R.id.videoSelectImage4);
        preview5=(SimpleDraweeView) findViewById(R.id.videoSelectImage5);
        preview6=(SimpleDraweeView) findViewById(R.id.videoSelectImage6);
        preview7=(SimpleDraweeView) findViewById(R.id.videoSelectImage7);
        selectedpreview=(SimpleDraweeView) findViewById(R.id.videoImageUpload);
        currentPreview=null;

        titleText=(TextInputLayout) findViewById(R.id.video_title_upload);
        aboutText=(TextInputLayout) findViewById(R.id.video_about_editext);
        tagsText=(TextInputLayout) findViewById(R.id.video_hashtag_editext);
        uploadedPercentage=(TextView) findViewById(R.id.uploaded_percentage);
        sizeinMB=(TextView) findViewById(R.id.size_of_data);
        successLabel=(TextView) findViewById(R.id.success_label);


        progresslayout=(ConstraintLayout) findViewById(R.id.progress_layout_video);

        uploadButton=(Button) findViewById(R.id.upload_video_button);
        progressBar=(ProgressBar) findViewById(R.id.uploaded_progressbar);


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title=titleText.getEditText().getText().toString().trim();
                final String about=aboutText.getEditText().getText().toString().trim();
                final String tags=tagsText.getEditText().getText().toString().toLowerCase().trim();

                if(TextUtils.isEmpty(title))
                {
                    titleText.setError("this field is required");
                }
                else if(TextUtils.isEmpty(about))
                {
                    aboutText.setError("this field is required");
                }
                else if (currentPreview==null)
                {
                    Toast.makeText(UploadVideoActivity.this,"no preview selected",Toast.LENGTH_SHORT).show();
                }
                else {

                    showpopup();
                    try {
                        titleText.setError(null);
                        aboutText.setError(null);
                        uploadData(title, about, tags,videoUri);
                    } catch (FileNotFoundException e) {
                        closepopup();
                        e.printStackTrace();
                    }

                }

            }
        });


        preview1.setImageBitmap(setpreview1(videoUri));
        preview2.setImageBitmap(setpreview2(videoUri));
        preview3.setImageBitmap(setpreview3(videoUri));
        preview4.setImageBitmap(setpreview4(videoUri));
        preview5.setImageBitmap(setpreview5(videoUri));
        preview6.setImageBitmap(setpreview6(videoUri));
        preview7.setImageBitmap(setpreview7(videoUri));


        preview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPreview=setpreview1(videoUri);
                selectedpreview.setImageBitmap(currentPreview);

            }
        });
        preview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPreview=setpreview2(videoUri);
                selectedpreview.setImageBitmap(currentPreview);

            }
        });
        preview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPreview=setpreview3(videoUri);
                selectedpreview.setImageBitmap(currentPreview);

            }
        });
        preview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPreview=setpreview4(videoUri);
                selectedpreview.setImageBitmap(currentPreview);

            }
        });
        preview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPreview=setpreview5(videoUri);
                selectedpreview.setImageBitmap(currentPreview);

            }
        });
        preview6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPreview=setpreview6(videoUri);
                selectedpreview.setImageBitmap(currentPreview);

            }
        });
        preview7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPreview=setpreview7(videoUri);
                selectedpreview.setImageBitmap(currentPreview);

            }
        });




        //firebase
        rootStorage= FirebaseStorage.getInstance().getReference();
        rootDatabase= FirebaseDatabase.getInstance().getReference();

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        database=FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("name");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                channelName=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    Bitmap setpreview1(String uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        Bitmap thumbframe=mediaMetadataRetriever.getFrameAtTime(3000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        return thumbframe;
    }
    Bitmap setpreview2(String uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        Bitmap thumbframe=mediaMetadataRetriever.getFrameAtTime(6000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        return thumbframe;
    }
    Bitmap setpreview3(String uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        Bitmap thumbframe=mediaMetadataRetriever.getFrameAtTime(9000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        return thumbframe;
    }
    Bitmap setpreview4(String uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        Bitmap thumbframe=mediaMetadataRetriever.getFrameAtTime(12000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        return thumbframe;
    }

    Bitmap setpreview5(String uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        Bitmap thumbframe=mediaMetadataRetriever.getFrameAtTime(60000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        return thumbframe;
    }
    Bitmap setpreview6(String uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        Bitmap thumbframe=mediaMetadataRetriever.getFrameAtTime(90000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        return thumbframe;
    }
    Bitmap setpreview7(String uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        Bitmap thumbframe=mediaMetadataRetriever.getFrameAtTime(120000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        return thumbframe;
    }


    private void uploadData(final String video_title, final String video_about, final String video_tags, String video_uri) throws FileNotFoundException
    {

        videosDatabase = rootDatabase.child("videos").child("users_uploads").child(currentUserId).push();
        rootDatabase.keepSynced(true);
        final String pushkey = videosDatabase.push().getKey();


        Uri file = Uri.fromFile(new File(video_uri));

        videosStorage = rootStorage.child("videos").child(currentUserId + ".mp4").child(pushkey);

        UploadTask uploadTask = videosStorage.putFile(file);

        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
                String progressSize=taskSnapshot.getBytesTransferred()/(1024*1024)+"/"+taskSnapshot.getTotalByteCount()/(1024*1024)+"MB";
              sizeinMB.setText(progressSize);

              String progressPercentage=(int) progress+"%";
              uploadedPercentage.setText(progressPercentage);
               // System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //System.out.println("Upload is paused");
            }
        });




        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    closepopup();
                    throw task.getException();

                }

                // Continue with the task to get the download URL
                return videosStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    String downloadUrlVideo=downloadUri.toString();


                    uploadThumb(downloadUrlVideo,video_title,video_about,pushkey,video_tags);

                } else {

                    closepopup();
                   Toast.makeText(UploadVideoActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



    private void uploadThumb(final String videoDownloadUri, final String title, final String about, final String push_key, final String tags)
    {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        currentPreview.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        videosthumbStorage=rootStorage.child("videos").child("video_thumb").child(push_key);

        UploadTask uploadTaskThumb = videosthumbStorage.putBytes(data);

        Task<Uri> urlTaskThumb = uploadTaskThumb.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    closepopup();
                    throw task.getException();

                }

                // Continue with the task to get the download URL
                return videosthumbStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    closepopup();
                    Uri downloadUriThumb = task.getResult();

                    String downloadUrlThumb=downloadUriThumb.toString();




                    final String date= DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());


                    final HashMap<String,String> videodata=new HashMap<>();
                    videodata.put("title",title);
                    videodata.put("thumb_image",downloadUrlThumb);
                    videodata.put("video_about",about);
                    videodata.put("video_url",videoDownloadUri);
                    videodata.put("uploaded_by",currentUserId);
                    videodata.put("uploaded_date",date);
                    videodata.put("channel_name",channelName);


                    allvideosdatabase=rootDatabase.child("videos").child("allvideos").child(push_key);
                    allvideosdatabase.setValue(videodata).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                videosDatabase.setValue(videodata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful())
                                       {
                                           if(!tags.isEmpty()) {
                                               uploadTags(tags, videodata);
                                           }
                                           else
                                           {
                                               closepopup();
                                               uploadButton.setEnabled(false);
                                           }
                                       }
                                       else
                                       {
                                           closepopup();

                                           Toast.makeText(UploadVideoActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                                       }
                                    }
                                });


                            }
                            else
                            {

                                closepopup();

                                Toast.makeText(UploadVideoActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                } else {

                    closepopup();
                    Toast.makeText(UploadVideoActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void uploadTags(String tags,HashMap videoData)
    {
        if(TextUtils.isEmpty(tags))
        {

            Log.d("UploadVideoActivity","tags are empty");
        }
        else
        {
            String[] tagsArray=tags.split("#");
            for(String x:tagsArray)
            {
               if (!x.isEmpty()) {
                   tagsDatabase = rootDatabase.child("videos").child("tags").child(x).push();
                   tagsDatabase.setValue(videoData).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()) {
                               Toast.makeText(UploadVideoActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                               uploadButton.setEnabled(false);

                           } else {
                               Toast.makeText(UploadVideoActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                           }

                       }
                   });
               }
            }
        }
    }


    private void showpopup()
    {

        uploadButton.setVisibility(View.GONE);
        progresslayout.setVisibility(View.VISIBLE);
        miniProgress.setVisibility(View.VISIBLE);
        titleText.setEnabled(false);
        aboutText.setEnabled(false);
        tagsText.setEnabled(false);
        preview1.setEnabled(false);
        preview2.setEnabled(false);
        preview3.setEnabled(false);
        preview4.setEnabled(false);
        preview5.setEnabled(false);
        preview6.setEnabled(false);
        preview7.setEnabled(false);



    }

    private void closepopup()
    {

        titleText.setEnabled(false);
        aboutText.setEnabled(false);
        tagsText.setEnabled(false);
        preview1.setEnabled(false);
        preview2.setEnabled(false);
        preview3.setEnabled(false);
        preview4.setEnabled(false);
        preview5.setEnabled(false);
        preview6.setEnabled(false);
        preview7.setEnabled(false);

        progresslayout.setVisibility(View.GONE);
        successLabel.setVisibility(View.VISIBLE);
        miniProgress.setVisibility(View.GONE);

    }




}
