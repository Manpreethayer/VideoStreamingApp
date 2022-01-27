package com.example.hp.teacher;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.hp.teacher.Songs.SongLoader;
import com.example.hp.teacher.Songs.adapters.SongsAdapter;
import com.example.hp.teacher.Videos.VideosLoader;
import com.example.hp.teacher.Videos.adapters.VideosAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectVideo extends AppCompatActivity {

    private VideosAdapter mAdapter;
    Context context;
    private Toolbar uploadToolbar;

    public static final int RUNTIME_PERMISSION_CODE = 100;

    String[] ListElements = new String[]{};

    RecyclerView videoList;

    List<String> ListElementsArrayList;

    ArrayAdapter<String> adapter;

    Cursor cursor;

    Uri uri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_video);

        videoList = (RecyclerView) findViewById(R.id.videoUploadList);



        context = getApplicationContext();

        uploadToolbar = (Toolbar) findViewById(R.id.upload_music_Toolbar);

        setSupportActionBar(uploadToolbar);

        getSupportActionBar().setTitle("Select Video");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uploadToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));


        // Requesting run time permission for Read External Storage.
        AndroidRuntimePermission();

        videoList.setLayoutManager(new GridLayoutManager(this,2));


        new SelectVideo.loadVideos().execute("");


    }






    // Creating Runtime permission function.
    public void AndroidRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(SelectVideo.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    SelectVideo.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE

                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", null);

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                } else {

                    ActivityCompat.requestPermissions(
                            SelectVideo.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            } else {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case RUNTIME_PERMISSION_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }




    private class loadVideos extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getApplicationContext() != null)
                mAdapter = new VideosAdapter((AppCompatActivity) SelectVideo.this, VideosLoader.getAllVideos(SelectVideo.this), false, false);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            videoList.setAdapter(mAdapter);


        }

        @Override
        protected void onPreExecute() {
        }
    }
}
