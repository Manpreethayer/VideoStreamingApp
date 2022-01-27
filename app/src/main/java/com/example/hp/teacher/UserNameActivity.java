package com.example.hp.teacher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class UserNameActivity extends AppCompatActivity {

    private Button next;
    private TextInputLayout username,country;



    //Firebase
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    //
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);




        next=(Button)findViewById(R.id.next_button);
        username=(TextInputLayout)findViewById(R.id.username);
        country=(TextInputLayout) findViewById(R.id.user_country);




        //Firebase
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=firebaseUser.getUid();

        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid=currentUser.getUid();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Progress Dialog
              /*  progressDialog=new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("please wait while we save changes");
                progressDialog.show();
                */





                String name=username.getEditText().getText().toString();
                String Country=country.getEditText().getText().toString();
                if(TextUtils.isEmpty(name)|| TextUtils.isEmpty(Country)) {

                    Toast.makeText(getApplicationContext(), "Empty Credentials", Toast.LENGTH_LONG).show();

                }

                else
                {

                    Map userdata=new HashMap<>();
                    userdata.put("name",name);
                    userdata.put("country",Country);


                    databaseReference.updateChildren(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                Intent intent = new Intent(UserNameActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();


                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Error While Updating", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }
            }
        });
    }
}
