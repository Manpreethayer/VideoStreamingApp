package com.example.hp.teacher;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity {


private TextView forgotPassword;
private TextInputLayout loginEmail;
private TextInputLayout loginPassword;
private Button loginButton;
private ImageButton backButtonLogin;
private FirebaseAuth firebaseAuth;
private DatabaseReference databaseReference;
    private ProgressBar progressBarLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();

        //progress bar
       // progressDialog=new ProgressDialog(this);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");


        loginEmail=(TextInputLayout) findViewById(R.id.login_email);
        progressBarLogin=(ProgressBar) findViewById(R.id.progressBarLogin);

        loginButton=(Button) findViewById(R.id.login_button);
        backButtonLogin=(ImageButton) findViewById(R.id.backButtonLogin);

        loginPassword=(TextInputLayout) findViewById(R.id.login_password);

        forgotPassword=(TextView) findViewById(R.id.forgot_password_button);
        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        backButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailAddress=loginEmail.getEditText().getText().toString().trim();

                if(TextUtils.isEmpty(emailAddress))
                {

                    Toast.makeText(LoginActivity.this, "Please enter email and then proceed", Toast.LENGTH_LONG).show();
                }
                else {

                    firebaseAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(LoginActivity.this, "Password reset link sent, check your mail", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                }
            }
        });




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=loginEmail.getEditText().getText().toString().trim();
                String password=loginPassword.getEditText().getText().toString().trim();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this,"Enter Credentials",Toast.LENGTH_LONG).show();
                }
                else
                {

                    progressBarLogin.setVisibility(View.VISIBLE);
                   /* progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("Please Wait While We Log You In");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();*/

                    updateUI(email,password);
                }
            }
        });

    }


    private void updateUI(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressBarLogin.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information



                            String current_user_id=firebaseAuth.getCurrentUser().getUid();
                            String device_token= FirebaseInstanceId.getInstance().getToken();

                            databaseReference.child(current_user_id).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {



                                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                    Log.d("Loin Activity", "signInWithEmail:success");
                                }
                            });



                        }

                        else
                        {
                            progressBarLogin.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            String error_message=task.getException().getMessage();
                            Log.w("Login Activity", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, error_message,
                                    Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });
    }
}
