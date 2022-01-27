package com.example.hp.teacher;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class FullImageActivity extends AppCompatActivity {
    private SimpleDraweeView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Fresco.initialize(this);

        fullImage=(SimpleDraweeView) findViewById(R.id.full_image);
final String image=getIntent().getStringExtra("image_url");
        Uri uri = Uri.parse(image);
        fullImage.setImageURI(uri);
    }
}
