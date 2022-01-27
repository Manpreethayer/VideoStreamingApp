package com.example.hp.teacher;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangeUsernameActivity extends AppCompatActivity {

    private Dialog dialog;
    private Button save;
    private TextInputLayout username;



    //Firebase
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);


        dialog=new Dialog(this);
        save=(Button)findViewById(R.id.save_username);
        username=(TextInputLayout)findViewById(R.id.username_changes);





        //Firebase
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=firebaseUser.getUid();

        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid=currentUser.getUid();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("name");

       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String user_name=dataSnapshot.getValue().toString();
               username.getEditText().setText(user_name);


           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Progress Dialog
              /*  progressDialog=new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("please wait while we save changes");
                progressDialog.show();
                */

showpopup();



                String name=username.getEditText().getText().toString();

                if(TextUtils.isEmpty(name)) {

                    dialog.dismiss();
                    username.setErrorEnabled(true);
                    username.setError("Empty Username");


                }

                else
                {




                    databaseReference.setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                dialog.dismiss();
                                username.setErrorEnabled(false);
                                Snackbar.make(findViewById(android.R.id.content),"Successfully Updated",Snackbar.LENGTH_LONG).setAction("Action",null).show();


                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error While Updating", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

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
