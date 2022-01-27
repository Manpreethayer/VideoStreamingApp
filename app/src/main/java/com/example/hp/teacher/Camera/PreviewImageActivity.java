package com.example.hp.teacher.Camera;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hp.teacher.R;
import com.example.hp.teacher.UserSettingsActivity;
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

public class PreviewImageActivity extends AppCompatActivity {

    ImageView previewImage;
    private ImageButton sendImage;
    private Dialog dialog;

   private String imagePath;

    //firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootDatabaseReference,storiesDatabase;
    private StorageReference rootStorageReference,storiesStorage;
   private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imagePath=getIntent().getStringExtra("imageBitmap");
        setContentView(R.layout.activity_preview_image);

        firebaseAuth=FirebaseAuth.getInstance();
        rootStorageReference= FirebaseStorage.getInstance().getReference();
        rootDatabaseReference=FirebaseDatabase.getInstance().getReference();

        dialog=new Dialog(this);


        currentUserId=firebaseAuth.getCurrentUser().getUid();

        previewImage=(ImageView) findViewById(R.id.preview_image);

        Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
        previewImage.setImageBitmap(bitmap);

        sendImage=(ImageButton) findViewById(R.id.send_image);
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopup();
                saveToStories();
            }
        });

    }

    private void saveToStories() {
        storiesDatabase= rootDatabaseReference.child("stories").child(currentUserId).push();
        rootDatabaseReference.keepSynced(true);
        final String pushkey=rootDatabaseReference.push().getKey();

        storiesStorage=rootStorageReference.child("stories").child(currentUserId).child(pushkey+".jpg");

        final StorageReference thumbStoriesReference=rootStorageReference.child("stories").child("stories_thumbs").child(pushkey+".jpg");



        File thumb_file_path=new File(imagePath);

        Uri imageUri=Uri.fromFile(thumb_file_path);



        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        //compressing image with compressor library
        try {
            Bitmap thumbBitMap= new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(100)
                    .compressToBitmap(thumb_file_path);


            thumbBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);


        } catch (IOException e) {
            e.printStackTrace();
        }

        final byte[] thumb_bytes  = baos.toByteArray();

        storiesStorage.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {
                    rootStorageReference.child("stories").child(currentUserId).child(pushkey+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {

                            final String downloadStoriesUrl = uri.toString();
                            UploadTask uploadTask = thumbStoriesReference.putBytes(thumb_bytes);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbtask) {

                                    if(thumbtask.isSuccessful())
                                    {
                                        rootStorageReference.child("stories").child("stories_thumbs").child(pushkey+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String downloadThumbUrl = uri.toString();

                                                Map updateHashMap=new HashMap<>();
                                                updateHashMap.put("imageStory",downloadStoriesUrl);
                                                updateHashMap.put("thumb_imageStory",downloadThumbUrl);
                                                updateHashMap.put("time_ago", ServerValue.TIMESTAMP);
                                                updateHashMap.put("type","image");

                                                storiesDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful())
                                                        {

                                                           dialog.dismiss();

                                                            Snackbar.make(findViewById(android.R.id.content),"Uploaded Successfully",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                                            // Toast.makeText(UserSettingsActivity.this , "Uploaded Succesfully" , Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });

                                            }
                                        });
                                    }
                                    else
                                    {
                                        dialog.dismiss();
                                        Toast.makeText(PreviewImageActivity.this , "Error Uploading Thumbnail" , Toast.LENGTH_LONG).show();
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
                    Toast.makeText(PreviewImageActivity.this, error,
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

