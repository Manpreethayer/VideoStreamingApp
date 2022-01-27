package com.example.hp.teacher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hp.teacher.Fragments.AccountFragment;
import com.example.hp.teacher.Fragments.HomeFragment;
import com.example.hp.teacher.Fragments.DiscoverFragment;
import com.example.hp.teacher.Fragments.ChatsFragment;
import com.example.hp.teacher.Fragments.LibraryFragment;
import com.example.hp.teacher.Fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public BottomNavigationView navigation;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_MUST_PERMISSION = 10;

    private boolean mIsPermissionGranted = false;

    private final String[] mPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Class fragment=null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment= HomeFragment.class;
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_explore:

                    fragment= DiscoverFragment.class;
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_account:
                    fragment= AccountFragment.class;
                    loadFragment(fragment);

                    return true;

                case R.id.navigation_library:
                    fragment= LibraryFragment.class;
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_chats:
                    fragment= ChatsFragment.class;
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Class fragmentclass) {
        Fragment fragment1=null;

        try {
            fragment1=(Fragment) fragmentclass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame,fragment1)
                .commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       
        setContentView(R.layout.activity_main);

        mIsPermissionGranted = requestPermissions();








        firebaseAuth=FirebaseAuth.getInstance();


       navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




        if(firebaseAuth.getCurrentUser() != null) {
           // usersDatabse = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
            loadFragment(HomeFragment.class);


        }



    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onStart() {

        super.onStart();



        // Check if user is signed in (non-null)
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();

        if(currentUser==null)
        {
            sendToStart();

        }
        else
        {
            databaseReference.child("online").setValue("true");
        }




    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser=firebaseAuth.getCurrentUser();

        if(currentUser !=null) {

            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }

    private void sendToStart() {
        Intent intent=new Intent(MainActivity.this,StartupActivity.class);
        startActivity(intent);
        finish();
    }


    private boolean requestPermissions() {
        Log.d(TAG, "requestPermissions " + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= M) {
            List<String> deniedPermission = new ArrayList<>();

            for (String permission : mPermissions) {
                if (ActivityCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {

                    Log.e(TAG, "permission " + permission + " is not granted");

                    deniedPermission.add(permission);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        Log.e(TAG, "shouldShowRequestPermissionRationale");
                        return false;
                    }
                }
            }

            if (deniedPermission.size() > 0) {
                String[] targetList = new String[deniedPermission.size()];
                targetList = deniedPermission.toArray(targetList);
                ActivityCompat.requestPermissions(this, targetList, REQUEST_MUST_PERMISSION);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult " + requestCode);
        if (requestCode == REQUEST_MUST_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            mIsPermissionGranted = true;
        }
    }

    public void onClick(View v) {
        if (!mIsPermissionGranted) {
            Toast.makeText(this, "Please check your permission!", Toast.LENGTH_SHORT).show();
        }


    }




}
