package com.example.hp.teacher;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UploadMusicActivity extends AppCompatActivity {

   private TextInputLayout songName;
   private Button uploadButton;
   private StorageReference rootStorage,songsStorage;
   private DatabaseReference rootDatabase,songsDatabase;
   String currentUserId;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_music);

        String songNameExtra=getIntent().getStringExtra("songname");
        final String songPathExtra=getIntent().getStringExtra("songUri");

        dialog=new Dialog(this);

        //firebase
        rootStorage=FirebaseStorage.getInstance().getReference();
        rootDatabase= FirebaseDatabase.getInstance().getReference();

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();




        songName=(TextInputLayout) findViewById(R.id.song_name_editext);
        songName.getEditText().setText(songNameExtra);

        uploadButton=(Button) findViewById(R.id.upload_music_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopup();
                try {
                    uploadMusic(songPathExtra);
                } catch (FileNotFoundException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
    }


    private void uploadMusic(String songPath) throws FileNotFoundException {

        songsDatabase = rootDatabase.child("songs").child(currentUserId).push();
        rootDatabase.keepSynced(true);
        String pushkey = songsDatabase.push().getKey();


        InputStream stream = new FileInputStream(new File(songPath));

        songsStorage = rootStorage.child("songs").child(currentUserId + ".mp3").child(pushkey);

        UploadTask uploadTask = songsStorage.putStream(stream);

        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    dialog.dismiss();
                    throw task.getException();

                }

                // Continue with the task to get the download URL
                return songsStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    dialog.dismiss();
                    Uri downloadUri = task.getResult();

                    String downloadUrl=downloadUri.toString();
                } else {

                    dialog.dismiss();
                    // Handle failures
                    // ...
                }
            }
        });


    }


    private void showpopup() {



        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();



    }

}
