package com.example.hp.teacher;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BioActivty extends AppCompatActivity {


    private Dialog dialog;
    private Button update;
    private Button delete;
    private TextInputLayout statusEditext;



    //Firebase
    private DatabaseReference statusDatabaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_activty);


        dialog=new Dialog(this);
        update=(Button)findViewById(R.id.done);
        delete=(Button) findViewById(R.id.delete_bio) ;
        statusEditext=(TextInputLayout)findViewById(R.id.bio_text);


        //Firebase
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=firebaseUser.getUid();

        statusDatabaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);

        statusDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("bio"))
                {
                    String display_bio=dataSnapshot.child("bio").getValue().toString();
                    statusEditext.getEditText().setText(display_bio);
                    delete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        delete=(Button)  findViewById(R.id.delete_bio);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statusDatabaseReference.child("bio").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        statusEditext.getEditText().setText("");

                        Snackbar.make(findViewById(android.R.id.content),"Deleted",Snackbar.LENGTH_LONG).setAction("Action",null).show();

                    }
                });

            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                    String bioText = statusEditext.getEditText().getText().toString();

                    if(!bioText.isEmpty()) {

                        showpopup();


                        statusDatabaseReference.child("bio").setValue(bioText).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    dialog.dismiss();
                                    Snackbar.make(findViewById(android.R.id.content),"Successfully Updated",Snackbar.LENGTH_LONG).setAction("Action",null).show();


                                    //progressDialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(BioActivty.this, "Error While Updating", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        Snackbar.make(findViewById(android.R.id.content),"Empty Bio",Snackbar.LENGTH_LONG).setAction("Action",null).show();
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
