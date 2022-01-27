package com.example.hp.teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.reactivex.internal.operators.observable.ObservableNever;

public class StartupActivity extends AppCompatActivity {

    private Button startupLoginButton;
    private Button startupSignupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        startupLoginButton=(Button) findViewById(R.id.startupLoginButton);
        startupLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StartupActivity.this,LoginActivity.class);
                startActivity(intent);


            }
        });

        startupSignupButton=(Button) findViewById(R.id.startupSignupButton);
        startupSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(StartupActivity.this,SignUpActivity.class);
                startActivity(intent);


            }
        });
    }
}
