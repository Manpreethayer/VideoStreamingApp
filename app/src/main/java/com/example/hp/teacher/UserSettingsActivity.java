package com.example.hp.teacher;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import id.zelory.compressor.Compressor;


public class UserSettingsActivity extends AppCompatActivity{


     private Dialog dialog;
     private ImageButton change_profile_image;
    private TextView addBio,userBio;




   private static int Image_picker=0;
    private static final int GALERRY_PICK=1;

    private TextView user_name,location;
    private Uri profile_image_uri=null;
    private SimpleDraweeView profile_image;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
   private String user_id;
   private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);


        Fresco.initialize(this);

         dialog=new Dialog(this);

        user_name=(TextView) findViewById(R.id.user_name);
        location=(TextView) findViewById(R.id.user_location);
        userBio=(TextView) findViewById(R.id.user_bio_settings);
        profile_image=(SimpleDraweeView) findViewById(R.id.profile_avatar_user_settings);
        change_profile_image=(ImageButton) findViewById(R.id.update_profile_image);
        addBio=(TextView) findViewById(R.id.add_bio);


        firebaseAuth=FirebaseAuth.getInstance();



        storageReference= FirebaseStorage.getInstance().getReference();

        user_id=firebaseAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name=dataSnapshot.child("name").getValue().toString();
                final String Profileimage=dataSnapshot.child("image").getValue().toString();
                // String display_status=dataSnapshot.child("status").getValue().toString();
              //  String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();

                String country=dataSnapshot.child("country").getValue().toString();

                if(dataSnapshot.hasChild("bio"))
                {
                    String display_bio=dataSnapshot.child("bio").getValue().toString();
                    userBio.setText(display_bio);
                    userBio.setVisibility(View.VISIBLE);
                    addBio.setVisibility(View.GONE);

                }
                else
                {
                    userBio.setVisibility(View.GONE);
                    addBio.setVisibility(View.VISIBLE);
                }


                  user_name.setText(display_name);
                location.setText(country);
                // status.setText(display_status);



                if (!Profileimage.equals("default")) {

                    Uri uriProfile = Uri.parse(Profileimage);
                    profile_image.setImageURI(uriProfile);

                    profile_image.setController(
                            Fresco.newDraweeControllerBuilder()
                                    .setTapToRetryEnabled(true)
                                    .setUri(uriProfile)
                                    .build());
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        change_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
                {

                    if(ContextCompat.checkSelfPermission(UserSettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(UserSettingsActivity.this, "Permission Denied",
                                Toast.LENGTH_SHORT).show();

                        ActivityCompat.requestPermissions(UserSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else
                    {
                        Image_picker=2;
                        // start picker to get image for cropping and then use the image in cropping activity
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.OFF)
                                .setMinCropWindowSize(500,500)
                                .start(UserSettingsActivity.this);


                    }
                }
                else
                {

                    Image_picker=2;

                    Intent gallery_intent=new Intent();
                    gallery_intent.setType("image/*");
                    gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery_intent,"Select Image"),GALERRY_PICK);
                }

            }


        });




        addBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bioIntent=new Intent(UserSettingsActivity.this,BioActivty.class);
                startActivity(bioIntent);
            }
        });





    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==GALERRY_PICK && resultCode==RESULT_OK)
        {
            //for image uri
            Uri imageUri=data.getData();

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {




                if(Image_picker==2)
                {
                    showpopup();
                     profile_image_uri= result.getUri();
                    //  wall_image.setImageURI(wall_image_uri);
                    post_profile_image(profile_image_uri);
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void post_profile_image(final Uri profile_image_uri) {


        File thumb_file_path=new File(profile_image_uri.getPath());



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


       StorageReference image_path=storageReference.child("profile_images").child(user_id +".jpg");
        final StorageReference bitmapFilePath=storageReference.child("profile_images").child("thumbs").child(user_id+".jpg");
        image_path.putFile(profile_image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {

                    storageReference.child("profile_images").child(user_id+ ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {

                            final String downloadProfileUrl = uri.toString();
                            UploadTask uploadTask = bitmapFilePath.putBytes(thumb_bytes);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbtask) {

                                    if(thumbtask.isSuccessful())
                                    {
                                        storageReference.child("profile_images").child("thumbs").child(user_id+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String downloadThumbUrl = uri.toString();

                                                Map updateHashMap=new HashMap<>();
                                                updateHashMap.put("image",downloadProfileUrl);
                                                updateHashMap.put("thumb_image",downloadThumbUrl);

                                                databaseReference.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                        Toast.makeText(UserSettingsActivity.this , "Error Uploading Thumbnail" , Toast.LENGTH_LONG).show();
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
                    Toast.makeText(UserSettingsActivity.this, error,
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
