package com.example.hp.teacher;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {


  // private Dialog dialog;
   private ImageView closePopup;


private TextInputLayout signUpEmail;
private TextInputLayout signUpPassword,signUpConfirmPassword;
private Button createAccount;
    private ProgressDialog progressDialog;
    private ImageButton backSignupButton;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

       // dialog=new Dialog(this);

        //progress bar
        progressDialog=new ProgressDialog(this);

        //firebase
        firebaseAuth=FirebaseAuth.getInstance();



        signUpEmail=(TextInputLayout) findViewById(R.id.sign_up_email);


        signUpPassword=(TextInputLayout) findViewById(R.id.sign_up_password);


        signUpConfirmPassword=(TextInputLayout) findViewById(R.id.sign_up_confirm_password);


        backSignupButton=(ImageButton) findViewById(R.id.backButtonSignup);
        backSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                       onBackPressed();
                        finish();

            }
        });
        createAccount=(Button) findViewById(R.id.create_account_button);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String email=signUpEmail.getEditText().getText().toString().trim();
                final String password=signUpPassword.getEditText().getText().toString().trim();
                final String confirmPassword=signUpConfirmPassword.getEditText().getText().toString().trim();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword))
                {
                    Toast.makeText(SignUpActivity.this,"Please Fill All The Credentials",Toast.LENGTH_LONG).show();
                }
                else {
                    if(password.equals(confirmPassword)) {

                        progressDialog.setTitle("Registering User");
                        progressDialog.setMessage("Please Wait While We Create Your Account");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();


                        registerUser(email,password);
                    }
                    else{
                        signUpConfirmPassword.setError("Confirm Password don't match with Password");
                    }
                }



            }
        });



    }

  /*  private void showpopup() {

        //dialog.setContentView(R.layout.popup_dialog_signup);

      //  studentAccount=(Button) dialog.findViewById(R.id.student_button);
        TeacherAccount=(Button) dialog.findViewById(R.id.teacher_button);

        TeacherAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String email=signUpEmail.getEditText().getText().toString().trim();
                final String password=signUpPassword.getEditText().getText().toString().trim();



                        progressDialog.setTitle("Registering User");
                        progressDialog.setMessage("Please Wait While We Create Your Account");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        registerUser(email, password);


                }


        });

        studentAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                final String email=signUpEmail.getEditText().getText().toString().trim();
                final String password=signUpPassword.getEditText().getText().toString().trim();



                        progressDialog.setTitle("Registering User");
                        progressDialog.setMessage("Please Wait While We Create Your Account");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        registerStudent(email, password);






            }
        });


        closePopup=(ImageView) dialog.findViewById(R.id.close_popup);
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    */


    private void registerUser(String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=currentUser.getUid();
                            String device_token= FirebaseInstanceId.getInstance().getToken();

                            databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);


                           // firebaseFirestore=FirebaseFirestore.getInstance();



                            HashMap<String,String> userdata=new HashMap<>();
                            userdata.put("name","default");
                            userdata.put("image","default");
                            userdata.put("thumb_image","default");
                            userdata.put("device_token",device_token);
                            userdata.put("wall_image","default");
                            userdata.put("country","default");


                            databaseReference.setValue(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        progressDialog.dismiss();

                                        Intent intent=new Intent(SignUpActivity.this,UserNameActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();


                                    }

                                }
                            });

                        } else {

                            progressDialog.hide();
                            // If sign in fails, display a message to the user.
                            Log.w("SignUpActivity", "createUserWithEmail:failure", task.getException());
                            String error_message=task.getException().getMessage();
                            Toast.makeText(SignUpActivity.this, error_message,
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }



}
