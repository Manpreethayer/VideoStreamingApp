package com.example.hp.teacher;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


public class Offline extends Application {

    private DatabaseReference usersDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Picasso Offline




        firebaseAuth= FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() !=null) {
            usersDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(firebaseAuth.getCurrentUser().getUid());

            usersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    usersDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
