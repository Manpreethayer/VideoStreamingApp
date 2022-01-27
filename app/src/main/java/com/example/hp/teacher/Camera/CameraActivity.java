package com.example.hp.teacher.Camera;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaActionSound;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.hp.teacher.Camera.CustomCameraButton.CircleProgressBar;
import com.example.hp.teacher.Camera.adapter.FilterListAdapter;
import com.example.hp.teacher.Camera.adapter.StickerCategoryListAdapter;
import com.example.hp.teacher.Camera.adapter.StickerListAdapter;
import com.example.hp.teacher.Camera.model.CategoryModel;
import com.example.hp.teacher.Camera.model.CategoryRespModel;
import com.example.hp.teacher.Camera.model.FilterModel;
import com.example.hp.teacher.Camera.model.ItemModel;
import com.example.hp.teacher.Camera.network.ItemService;
import com.example.hp.teacher.R;
import com.seerslab.argearsdk.ARGearSDK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.hp.teacher.Camera.SLThreadPool.postOnUiThread;

public class CameraActivity extends AppCompatActivity implements
        StickerCategoryListAdapter.Listener, StickerListAdapter.Listener,
        FilterListAdapter.Listener, RadioGroup.OnCheckedChangeListener{

    private static final String TAG = CameraActivity.class.getSimpleName();

    private CircleProgressBar customButton;
    private ImageButton clear;

    private ReferenceCamera1 referenceCamera1;

    int VideoSeconds = 1;
    private Boolean isFlashOn;
    private ImageButton flashButton;

    // camera
    private FrameLayout mCameraLayout;
    private View mViewTopRatio;
    private View mViewBottomRatio;
    private RadioGroup mRadioGroupBitrate;
    private RadioButton mOneMBpsGroupButton;
    private RadioButton mTwoMBpsGroupButton;
    private RadioButton mFourMBpsGroupButton;
    private RadioGroup mRadioGroupCameraRatio;
    private RadioButton mRatio_FullGroupButton;
    private RadioButton mRatio_4_3GroupButton;
    private RadioButton mRatio_1_1GroupButton;

    private CheckBox mCheckboxVideoOnly;
    private TextView mTextViewFaceStatus;
    private TextView mTextViewTriggerStatus;

    // sticker
    private RecyclerView mRecyclerViewStickerCategory;
    private RecyclerView mRecyclerViewSticker;
    private StickerCategoryListAdapter mStickerCategoryListAdapter;
    private StickerListAdapter mStickerListAdapter;
    private RelativeLayout mViewStickers;

    // filter
    private RecyclerView mRecyclerViewFilter;
    private FilterListAdapter mFilterListAdapter;
    private RelativeLayout mViewFilters;


    private CheckBox mCheckDebugLandmark;
    private CheckBox mCheckDebugHWRect;



    private GLView mGlView;
    private ReferenceCamera mCamera;

    private boolean mIsRecording = false;
    private File mRecordingFile;

    private boolean mFilterVignette = false;
    private boolean mFilterBlur = false;
    private int mFilterLevel = 100;
    private String mCurrentFilteritemID = null;
    private String mCurrentStickeritemID = null;
    private boolean mHasTrigger = false;

    private int mDeviceWidth = 0;
    private int mDeviceHeight = 0;
    private int mTargetWidth = 0;
    private int mTargetHeight = 0;

    private boolean mIsInitialize = false;

    private BroadcastReceiver unlockReceiver;
    private IntentFilter unlockFilter;
    private Toast mTriggerToast = null;

    private int progressInt=0;

    private boolean isBulgeBeauty = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, " ******** protected void onCreate(Bundle savedInstanceState)  ");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        isFlashOn=false;

        flashButton=(ImageButton) findViewById(R.id.flash_Button);

        Point realSize = new Point();
        Display display= ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getRealSize(realSize);
        mDeviceWidth = realSize.x;
        mDeviceHeight = realSize.y;
        mTargetWidth = realSize.x;
        mTargetHeight = realSize.y;

        initUI();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);

        requestStickerAPI();
    }

    CountDownTimer VideoCountDown = new CountDownTimer(20000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            VideoSeconds++;
            int VideoSecondsPercentage = VideoSeconds * 5;
            customButton.setProgressWithAnimation(VideoSecondsPercentage);
        }


        @Override
        public void onFinish() {
            // stopRecording();
            stopRecording();
            customButton.setProgress(0);
            VideoSeconds = 0;
        }
    };




    private void runResume() {
        ARGearSDK.createEngine(this, AppConfig.API_KEY, AppConfig.SECRET_KEY);

        initialize();

        if (mIsInitialize) {
            ARGearSDK.resumeEngine();
        }

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, " ******** protected void onResume() ");

        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if(km.inKeyguardRestrictedInputMode()) {
            if (unlockReceiver == null) {
                Log.e(TAG, " ******** onResume() at lockscreen");
                // 전원버튼의 의해 lockscreen 상태로 갔을때, lockscreen 밑에 떠 있는 activity의 resume이 호출되어
                // crash가 발생하는 경우가 있음. lockscreen 상태일때는 unlock event를 등록하고, 실제 unlock event가
                // 호출 되었을때 resume을 타게함.
                unlockFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
                unlockReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                            Log.e(TAG, " ******** receive unlock event ");
                            if (mIsInitialize == false) {
                                runResume();
                            }

                            if (unlockReceiver != null) {
                                unregisterReceiver(unlockReceiver);
                                unlockReceiver = null;
                                unlockFilter = null;
                            }
                        }
                    }
                };
                registerReceiver(unlockReceiver, unlockFilter);
            }
        } else {
            runResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e(TAG, " ******** protected void onPause() ");
        if (mIsInitialize) {
            mIsInitialize = false;

            mCamera.stopCamera();
            mCamera.destroy();

            ARGearSDK.pauseEngine();

            mCameraLayout.removeView(mGlView);
            mGlView = null;

            ARGearSDK.destoryEngine();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, " ******** protected void onDestroy() ");
    }

    private void initialize() {
        mCameraLayout = findViewById(R.id.camera_layout);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mGlView = new GLView(this);
        mGlView.setZOrderMediaOverlay(true);
        initGLView();

        mCameraLayout.addView(mGlView, params);

        initCamera();

        mIsInitialize = true;
    }

    private void initGLView() {
        mGlView.setGLViewListener(new GLView.GLViewListener() {
            @Override
            public void onGLSurfaceCreated() {
                mCamera.startCamera();
            }
        });
    }

    private void initCamera() {

        int ratio = AppPreference.getInstance(CameraActivity.this).getCameraRatio();
        int camera_ratio;
        if(ratio == 1)
            camera_ratio = ReferenceCamera.CAMERA_RATIO_FULL;
        else
            camera_ratio = ReferenceCamera.CAMERA_RATIO_4_3;

        if (AppConfig.USE_CAMERA_API == AppConfig.CAMERA_API_1) {
            mCamera = new ReferenceCamera1(this, getWindowManager().getDefaultDisplay().getRotation(), camera_ratio);
        } else {
            mCamera = new ReferenceCamera2(this, getWindowManager().getDefaultDisplay().getRotation(), camera_ratio);
        }

        if (mCamera.getCameraFacingFrontValue() != -1) {
            mCamera.setFacing(mCamera.getCameraFacingFrontValue());
        } else if (mCamera.getCameraFacingBackValue() != -1) {
            mCamera.setFacing(mCamera.getCameraFacingBackValue());
        } else {
            return;
        }

        mCamera.setCameraStateListener(new ReferenceCamera.CameraStateListener() {
            @Override
            public void onReadyCamera(boolean success, boolean availableFlash) {
                Log.d(TAG, " ###### onReadyCamera " + mCamera.isCameraFacingFront());

                if (success) {
                    ARGearSDK.isCameraFacingFront = mCamera.isCameraFacingFront();
                    ARGearSDK.CameraPreviewSize = mCamera.getPreviewSize();
                    if (!ARGearSDK.isResumed()) {
                        ARGearSDK.resumeEngine();
                    }

                    if (mCurrentFilteritemID != null) {
                        ARGearSDK.setFilter(mCurrentFilteritemID);
                    }
                    if (mCurrentStickeritemID != null) {
                        ARGearSDK.setItem(mCurrentStickeritemID);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initRatioUI();
                        }
                    });


                }
            }

            @Override
            public void onCloseCamera(boolean stopRender) {

            }

            @Override
            public void onUpdateFaceFoundState(final boolean foundFace) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ARGearSDK.isFaceRequired()) {
                            if (foundFace) {
                                mTextViewFaceStatus.setText("Face Found");
                            } else {
                                mTextViewFaceStatus.setText("Face REQUIRED!!");
                            }
                        } else {
                            mTextViewFaceStatus.setText("NO REQUIRED");
                        }

                        // TRIGGER_MOUTH_FLAG   = (1 << 0)
                        // TRIGGER_HEAD_FLAG    = (1 << 1)
                        // TRIGGER_DELAY_FLAG   = (1 << 2)
                        int triggerstatus;
                        triggerstatus = ARGearSDK.getTriggerFlag();
                        mTextViewTriggerStatus.setText(String.format("%d", triggerstatus));
                        onGetTriggerFlag(triggerstatus);
                    }
                });
            }
        });

        mRecordingFile = new File(Environment.getExternalStorageDirectory(), AppConfig.RECORDING_FILE_NAMW);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        mViewTopRatio = findViewById(R.id.top_ratio_view);
        mViewBottomRatio = findViewById(R.id.bottom_ratio_view);


        mViewStickers = (RelativeLayout) findViewById(R.id.stickers_layout);
        mViewFilters = (RelativeLayout) findViewById(R.id.filters_layout);
        clear=(ImageButton) findViewById(R.id.clearButton);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeFilters();
                closeStickers();

            }
        });

        mCheckboxVideoOnly = (CheckBox) findViewById(R.id.video_only_checkbox);
        mRadioGroupBitrate = (RadioGroup) findViewById(R.id.bitrate_radiogroup);

        customButton = (CircleProgressBar) findViewById(R.id.custom_progressBar);

        customButton.setOnTouchListener(new View.OnTouchListener() {

            private Timer timer = new Timer();
            private long LONG_PRESS_TIMEOUT = 1000;
            private boolean wasLong = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getClass().getName(), "touch event: " + event.toString());

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // touch & hold started
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            wasLong = true;
                            // touch & hold was long
                            Log.i("Click", "touch & hold was long");
                            VideoCountDown.start();

                            mIsRecording = true;
                            int targetBitrate = getTargetBitrate();
                            boolean ratio1to1 = is1to1CameraRatio();
                            Log.i(TAG, "startRecording with videoOnly : " + mCheckboxVideoOnly.isChecked() + ", 1to1 : " + ratio1to1 + ", bitrate : " + targetBitrate);
                            startRecording(mRecordingFile, targetBitrate, mCheckboxVideoOnly.isChecked(), ratio1to1);

                        }
                    }, LONG_PRESS_TIMEOUT);
                    return true;
                }


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // touch & hold stopped
                    timer.cancel();
                    if (!wasLong) {
                        // touch & hold was short
                        Log.i("Click", "touch & hold was short");

                        ARGearSDK.takePicture(AppConfig.SAVE_PICTURE_PATH);
                        MediaActionSound sound = new MediaActionSound();
                        sound.play(MediaActionSound.SHUTTER_CLICK);

                        Toast.makeText(CameraActivity.this,AppConfig.SAVE_PICTURE_PATH,Toast.LENGTH_LONG).show();

                       final Intent intent = new Intent(CameraActivity.this, PreviewImageActivity.class);


                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                Bundle b = new Bundle();
                                b.putString("imageBitmap", AppConfig.SAVE_PICTURE_PATH); //Your id
                                intent.putExtras(b);



                                startActivity(intent);

                            }
                        }, 1000);








                    } else {
                        mIsRecording = false;
                        stopRecording();
                        VideoCountDown.cancel();
                        VideoSeconds = 1;
                        customButton.setProgressWithAnimation(0);
                        wasLong = false;
                    }
                    timer = new Timer();
                    return true;
                }

                return false;
            }
        });
        mRadioGroupBitrate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.onembps_radiobutton:
                        AppPreference.getInstance(CameraActivity.this).setBitrate(1);
                        break;
                    case R.id.twombps_radiobutton:
                        AppPreference.getInstance(CameraActivity.this).setBitrate(2);
                        break;
                    case R.id.fourmbps_radiobutton:
                        AppPreference.getInstance(CameraActivity.this).setBitrate(3);
                        break;
                }
            }
        });

        mOneMBpsGroupButton = findViewById(R.id.onembps_radiobutton);
        mTwoMBpsGroupButton = findViewById(R.id.twombps_radiobutton);;
        mFourMBpsGroupButton = findViewById(R.id.fourmbps_radiobutton);;

        mRadioGroupCameraRatio = (RadioGroup) findViewById(R.id.camera_ratio_radiogroup);
        mRadioGroupCameraRatio.setOnCheckedChangeListener(this);

        mRatio_FullGroupButton = findViewById(R.id.ratio_full_radiobutton);
        mRatio_4_3GroupButton = findViewById(R.id.ratio43_radiobugtton);
        mRatio_1_1GroupButton = findViewById(R.id.ratio11_radiobutton);

        // init category list
        mRecyclerViewStickerCategory = (RecyclerView) findViewById(R.id.sticker_category_recyclerview);

        mTextViewFaceStatus = (TextView) findViewById(R.id.face_status_textview);
        mTextViewTriggerStatus = (TextView) findViewById(R.id.trigger_status_textview);

        mRecyclerViewStickerCategory.setHasFixedSize(true);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this);
        categoryLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewStickerCategory.setLayoutManager(categoryLayoutManager);

        mStickerCategoryListAdapter = new StickerCategoryListAdapter(this);
        mRecyclerViewStickerCategory.setAdapter(mStickerCategoryListAdapter);


        // init sticker list
        mRecyclerViewSticker = (RecyclerView) findViewById(R.id.sticker_recyclerview);

        mRecyclerViewSticker.setHasFixedSize(true);
        LinearLayoutManager itemsLayoutManager = new LinearLayoutManager(this);
        itemsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewSticker.setLayoutManager(itemsLayoutManager);

        mStickerListAdapter = new StickerListAdapter(this, this);
        mRecyclerViewSticker.setAdapter(mStickerListAdapter);

        // init filter list
        mRecyclerViewFilter = (RecyclerView) findViewById(R.id.filter_recyclerview);

        mRecyclerViewFilter.setHasFixedSize(true);
        LinearLayoutManager filterLayoutManager = new LinearLayoutManager(this);
        filterLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewFilter.setLayoutManager(filterLayoutManager);

        mFilterListAdapter = new FilterListAdapter(this, this);
        mRecyclerViewFilter.setAdapter(mFilterListAdapter);

        /*mViewBulge = (RelativeLayout) findViewById(R.id.bulge_layout);*/




        mCheckDebugLandmark = findViewById(R.id.debug_landmark_checkbox);
        mCheckDebugHWRect = findViewById(R.id.debug_rect_checkbox);

    }

    private void updateUI() {
        boolean landmark = AppPreference.getInstance(this).isCheckedLandmark();
        boolean faceRect = AppPreference.getInstance(this).isCheckedFaceRect();
        boolean videoOnly = AppPreference.getInstance(this).isCheckedVideoOnly();
        int bitrate = AppPreference.getInstance(this).getBitrate();
        int ratio =  AppPreference.getInstance(this).getCameraRatio();

        setDrawLandmark(landmark, faceRect);
        if (landmark) {
            mCheckDebugLandmark.setChecked(true);
        }
        if (faceRect) {
            mCheckDebugHWRect.setChecked(true);
        }

        if (videoOnly) {
            mCheckboxVideoOnly.setChecked(true);
        }

        if (bitrate == 2) {
            mTwoMBpsGroupButton.setChecked(true);
        } else if (bitrate == 3) {
            mFourMBpsGroupButton.setChecked(true);
        } else {
            mOneMBpsGroupButton.setChecked(true);
        }

        if (ratio == 2) {
            mRatio_4_3GroupButton.setChecked(true);
        } else if (ratio == 3) {
            mRatio_1_1GroupButton.setChecked(true);
        } else {
            mRatio_FullGroupButton.setChecked(true);
        }

    }

    private void initRatioUI() {
        int previewWidth = mCamera.getPreviewSize()[1];
        int previewHeight = mCamera.getPreviewSize()[0];

        int cameraRatioType = mCamera.getCameraRatio();
        if (cameraRatioType == ReferenceCamera.CAMERA_RATIO_FULL) {
            mTargetHeight = mDeviceHeight;
            mTargetWidth = (int) ((float) mDeviceHeight * previewWidth / previewHeight );
        } else {
            mTargetWidth = mDeviceWidth;
            mTargetHeight = (int) ((float) mDeviceWidth * previewHeight / previewWidth);
        }

        if (mGlView != null) {
            mGlView.getHolder().setFixedSize(mTargetWidth, mTargetHeight);
        }

        if (cameraRatioType == ReferenceCamera.CAMERA_RATIO_FULL) {
            mViewTopRatio.setVisibility(View.GONE);
            mViewBottomRatio.setVisibility(View.GONE);
        } else {
            if (is1to1CameraRatio()) {
                int viewTopHight = (mTargetHeight - mTargetWidth) / 2;
                int viewBottomHeight = mDeviceHeight - (viewTopHight + mTargetWidth);
                mViewTopRatio.getLayoutParams().height = viewTopHight;
                mViewBottomRatio.getLayoutParams().height = viewBottomHeight;
                mViewTopRatio.setVisibility(View.VISIBLE);
                mViewBottomRatio.setVisibility(View.VISIBLE);
            } else {
                int viewBottomHeight = mDeviceHeight - mTargetHeight;
                mViewBottomRatio.getLayoutParams().height = viewBottomHeight;
                mViewTopRatio.setVisibility(View.GONE);
                mViewBottomRatio.setVisibility(View.VISIBLE);
            }
        }
    }


    public void onClickButtons(View v) {
        switch (v.getId()){


            case R.id.sticker_button:
                showStickers();
                break;

            case R.id.filter_button:
                showFilters();
                break;





            case R.id.camera_switch_button:
                ARGearSDK.pauseEngine();
                mCamera.changeCameraFacing();
                break;

        }
    }


    private void setDrawLandmark(boolean landmark, boolean faceRect) {

        int flag = 0;

        if(landmark){
            flag |= AppConfig.DLIB_LANDMARK;
            AppPreference.getInstance(this).setLandmark(true);
        }else
            AppPreference.getInstance(this).setLandmark(false);

        if(faceRect){
            flag |= (AppConfig.FACE_RECT_HW | AppConfig.FACE_RECT_SW | AppConfig.FACE_AXIES);
            AppPreference.getInstance(this).setFaceRect(true);
        }else
            AppPreference.getInstance(this).setFaceRect(false);


        ARGearSDK.setDrawLandmarkInfo(flag);
    }

    public void setMeasureSurfaceView(View view) {
        if (view.getParent() instanceof FrameLayout) {
            view.setLayoutParams(new FrameLayout.LayoutParams(mTargetWidth, mTargetHeight));
        }else if(view.getParent() instanceof RelativeLayout) {
            view.setLayoutParams(new RelativeLayout.LayoutParams(mTargetWidth, mTargetHeight));
        }

        /* to align center */
        int cameraRatioType = mCamera.getCameraRatio();
        if ((cameraRatioType == ReferenceCamera.CAMERA_RATIO_FULL) && (mTargetWidth > mDeviceWidth)) {
            view.setX((mDeviceWidth - mTargetWidth) / 2);
        } else {
            view.setX(0);
        }
    }

    public int getTargetWidth() {
        return mTargetWidth;
    }

    public int getTargetHeight() {
        return mTargetHeight;
    }

    private boolean is1to1CameraRatio(){
        return mRadioGroupCameraRatio.getCheckedRadioButtonId() == R.id.ratio11_radiobutton;
    }

    private int getTargetBitrate(){
        int bitRate = 4 * 1000 * 1000;
        switch (mRadioGroupBitrate.getCheckedRadioButtonId()){
            case R.id.onembps_radiobutton:
                bitRate = 1 * 1000 * 1000;
                break;
            case R.id.twombps_radiobutton:
                bitRate = 2 * 1000 * 1000;
                break;
            case R.id.fourmbps_radiobutton:
                bitRate = 4 * 1000 * 1000;
                break;
        }

        return bitRate;
    }

    private void setLastUpdatedAt(long lastUpdatedAt){
        SharedPreferences prefs = getSharedPreferences("sample", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("lastUpdatedAt", lastUpdatedAt);
        editor.apply();
    }

    private long getLastUpdatedAt(){
        SharedPreferences prefs = getSharedPreferences("sample", MODE_PRIVATE);
        return prefs.getLong("lastUpdatedAt", 0);
    }

    private void requestStickerAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ItemService itemService = retrofit.create(ItemService.class);
        Map<String, String> params = new HashMap<>();
        itemService.requestItem(params).enqueue(itemCallBack);
    }

    private Callback<CategoryRespModel> itemCallBack = new Callback<CategoryRespModel>() {
        @Override
        public void onResponse(Call<CategoryRespModel> call, Response<CategoryRespModel> response) {
            if (response.isSuccessful()) {
                final CategoryRespModel respModel = response.body();
                Log.d(TAG, "onResponse : " + respModel.toString());
                if (respModel == null || respModel.categories == null) {
                    Log.w(TAG, "invalid root model.");
                    return;
                }

                Realm rm = Realm.getDefaultInstance();
                rm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // 응답의 아이템이 변경 되었는지 last_updated_at 을 통해 확인 합니다
                        if(getLastUpdatedAt() !=  respModel.last_updated_at) {
                            for( CategoryModel category : respModel.categories) {
                                for(ItemModel item : category.items) {
                                    ItemModel existItem = realm.where(ItemModel.class).equalTo("uuid", item.uuid).findFirst();
                                    if(existItem != null){
                                        // 각 아이템의 updated at 값을 비교 하여 아이템을 재 다운로드 해야 하는지 확인 합니다
                                        if(existItem.updated_at != item.updated_at){
                                            Log.d(TAG, "update item status " + item.uuid + ":" + existItem.downloadStatus + "=>" + ItemModel.STATUS_NEED_DOWNLOAD);
                                            item.downloadStatus = ItemModel.STATUS_NEED_DOWNLOAD;
                                        }else{
                                            // updated at 이 변경 되지 않았으면 기존의 아이템의 상태를 유지 합니다
                                            Log.d(TAG, "exist item status " + item.uuid + ":" + existItem.downloadStatus);
                                            item.downloadStatus = existItem.downloadStatus;
                                        }
                                    }else{
                                        // 새로운 아이템이면 다운로드 필요 상태로 초기화 합니다.
                                        Log.d(TAG, "new item status " + item.uuid + ":" + ItemModel.STATUS_NEED_DOWNLOAD);
                                        item.downloadStatus = ItemModel.STATUS_NEED_DOWNLOAD;
                                    }
                                }
                            }

                            for( FilterModel fiter : respModel.filters) {
                                for(ItemModel item : fiter.items) {
                                    ItemModel existItem = realm.where(ItemModel.class).equalTo("uuid", item.uuid).findFirst();
                                    if(existItem != null){
                                        // 각 아이템의 updated at 값을 비교 하여 아이템을 재 다운로드 해야 하는지 확인 합니다
                                        if(existItem.updated_at != item.updated_at){
                                            Log.d(TAG, "update item status " + item.uuid + ":" + existItem.downloadStatus + "=>" + ItemModel.STATUS_NEED_DOWNLOAD);
                                            item.downloadStatus = ItemModel.STATUS_NEED_DOWNLOAD;
                                        }else{
                                            // updated at 이 변경 되지 않았으면 기존의 아이템의 상태를 유지 합니다
                                            Log.d(TAG, "exist item status " + item.uuid + ":" + existItem.downloadStatus);
                                            item.downloadStatus = existItem.downloadStatus;
                                        }
                                    }else{
                                        // 새로운 아이템이면 다운로드 필요 상태로 초기화 합니다.
                                        Log.d(TAG, "new item status " + item.uuid + ":" + ItemModel.STATUS_NEED_DOWNLOAD);
                                        item.downloadStatus = ItemModel.STATUS_NEED_DOWNLOAD;
                                    }
                                }
                            }

                            // database 를 업데이트 합니다.
                            realm.delete(CategoryModel.class);
                            realm.delete(FilterModel.class);
                            realm.delete(ItemModel.class);
                            realm.copyToRealmOrUpdate(respModel.categories);
                            realm.copyToRealmOrUpdate(respModel.filters);

                            // last updated at 을 업데이트 합니다.
                            setLastUpdatedAt(respModel.last_updated_at);
                        } else {
                            Log.e(TAG, "last sync at is not changed!");
                        }
                    }
                });
            } else {
                Log.e(TAG, "onError : response.isSuccessful() = " + response.isSuccessful());
            }
        }

        @Override
        public void onFailure(Call<CategoryRespModel> call, Throwable t) {
            Log.e(TAG, "onFailure " + t);
        }
    };

    public List<CategoryModel> getStickerCategories(){
        return Realm.getDefaultInstance().where(CategoryModel.class).findAll();
    }

    public List<FilterModel> getFilters(){
        return Realm.getDefaultInstance().where(FilterModel.class).findAll();
    }

    private void showStickers(){
        List<CategoryModel> categories = getStickerCategories();
        mStickerCategoryListAdapter.setData(categories);
        mViewStickers.setVisibility(View.VISIBLE);
        mViewFilters.setVisibility(View.GONE);
    clear.setVisibility(View.VISIBLE);}

    private void closeStickers(){
        mViewStickers.setVisibility(View.GONE);
        clearStickers();
        clear.setVisibility(View.GONE);

    }

    private void clearStickers(){
        ARGearSDK.setItem(null);
        mCurrentStickeritemID = null;
        mHasTrigger = false;
    }

    private void showFilters(){
        List<FilterModel> filters = getFilters();
        mFilterListAdapter.setData(filters);
        mViewFilters.setVisibility(View.VISIBLE);
        mViewStickers.setVisibility(View.GONE);
        clear.setVisibility(View.VISIBLE);


    }

    private void closeFilters(){
        mViewFilters.setVisibility(View.GONE);
        clearFilter();
        clear.setVisibility(View.GONE);
    }

    private void clearFilter(){
        ARGearSDK.setFilter(null);
        mCurrentFilteritemID = null;
    }



    public void FlashControl(View v) {
        Log.i("Flash", "Flash button clicked!");
        boolean hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            AlertDialog alert = new AlertDialog.Builder(CameraActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        } else {

            if (!isFlashOn) {
                isFlashOn = true;
                flashButton.setImageResource(R.mipmap.flash_on);
                Log.i("Flash", "Flash On");

            } else {
                isFlashOn = false;
                flashButton.setImageResource(R.mipmap.flash_off);
                Log.i("Flash", "Flash Off");
            }
        }
    }



    private void startRecording(File recordingFile, int bitrate, boolean videoOnly, boolean ratio1to1){
        ARGearSDK.startRecordVideo(recordingFile, bitrate, videoOnly, ratio1to1);
    }

    private void stopRecording(){
        ARGearSDK.stopRecordVideo();


        final Intent intent = new Intent(this, PlayerActivity.class);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Bundle b = new Bundle();
                b.putString(PlayerActivity.INTENT_URI, mRecordingFile.getAbsolutePath()); //Your id
                intent.putExtras(b);
/*
                Pair[] pair=new Pair[3];
                pair[0]=new Pair<View,String>(cameraButtonProgress,"CameraToVideoTransition");


                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(CameraActivity.this,
                        pair );*/


                startActivity(intent);

            }
        }, 1000);
    }

    @Override
    public void onCategorySelected(CategoryModel category) {
        mStickerListAdapter.setData(category.items);
    }

    @Override
    public void onStickerSelected(int position, ItemModel item) {
        mCurrentStickeritemID = item.uuid;
        mHasTrigger = item.has_trigger;

        if (TextUtils.equals(item.downloadStatus, ItemModel.STATUS_OK)) {
            ARGearSDK.setItem(item.uuid);



        } else {
            new DownloadItemProgressTask(getFilesDir().getAbsolutePath() + "/" + item.uuid, item.zip_file, item.uuid, position, true).execute();
        }
    }

    @Override
    public void onFilterSelected(int position, ItemModel item) {
        mCurrentFilteritemID = item.uuid;

        if (TextUtils.equals(item.downloadStatus, ItemModel.STATUS_OK)) {
            ARGearSDK.setFilter(item.uuid);
        } else {
            new DownloadItemProgressTask(getFilesDir().getAbsolutePath() + "/" + item.uuid, item.zip_file, item.uuid, position, false).execute();
        }
    }

    public void onGetTriggerFlag(final int flag) {
        postOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentStickeritemID != null && mHasTrigger) {
                    // TRIGGER_MOUTH_FLAG   = (1 << 0)
                    // TRIGGER_HEAD_FLAG    = (1 << 1)
                    // TRIGGER_DELAY_FLAG   = (1 << 2)
                    String strTrigger = null;
                    if ((flag & 1) != 0) {
                        strTrigger = "Open your mouth.";
                    } else if ((flag & 2) != 0) {
                        strTrigger = "Move your head side to side.";
                    } else {
                        if (mTriggerToast != null) {
                            mTriggerToast.cancel();
                            mTriggerToast = null;
                        }
                    }

                    if (strTrigger != null) {
                        mTriggerToast = Toast.makeText(CameraActivity.this, strTrigger, Toast.LENGTH_SHORT);
                        mTriggerToast.setGravity(Gravity.CENTER, 0, 0);
                        mTriggerToast.show();
                        mHasTrigger = false;
                    }
                }
            }
        });
    }


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        switch (checkedId) {
            case R.id.ratio_full_radiobutton:
                mCamera.changeCameraRatio(ReferenceCamera.CAMERA_RATIO_FULL);
                AppPreference.getInstance(CameraActivity.this).setCameraRatio(1);
                break;
            case R.id.ratio43_radiobugtton:
                mCamera.changeCameraRatio(ReferenceCamera.CAMERA_RATIO_4_3);
                AppPreference.getInstance(CameraActivity.this).setCameraRatio(2);
                break;
            case R.id.ratio11_radiobutton:
                mCamera.changeCameraRatio(ReferenceCamera.CAMERA_RATIO_4_3);
                AppPreference.getInstance(CameraActivity.this).setCameraRatio(3);
                break;
        }
    }

    public boolean unzipItem(ItemModel item, String targetPath) {
        if(TextUtils.equals(item.downloadStatus, ItemModel.STATUS_OK)){
            return ARGearSDK.unzip(targetPath, item.uuid);
        }else{
            return false;
        }
    }

    public class DownloadItemProgressTask extends AsyncTask<Integer, Integer, Boolean> {
        private ProgressDialog progressDialog;
        private String targetPath;
        private String achiveUrl;
        private String itemId;
        private int itemPosition;
        private boolean isSticker;

        public DownloadItemProgressTask(String targetPath, String url, String itemId, int itemPosition, boolean isSticker) {
            this.targetPath = targetPath;
            this.achiveUrl = url;
            this.itemId = itemId;
            this.itemPosition = itemPosition;
            this.isSticker = isSticker;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CameraActivity.this);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            boolean success = false;
            FileOutputStream fileOutput = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(achiveUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(false);
                urlConnection.connect();

                File file = new File(targetPath);
                file.createNewFile();

                fileOutput = new FileOutputStream(file);

                inputStream = urlConnection.getInputStream();

                Log.d(TAG, "read " + achiveUrl + "=>" + targetPath + " " + urlConnection.getContentLength());

                byte[] buffer = new byte[1024];
                int bufferLength;
                int read = 0;
                int total = urlConnection.getContentLength();
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    read += bufferLength;
                    if (total > 0) {
                        int progress = (int) (100 * (read / (float) total));
                        publishProgress(progress);
                    }
                }

                success = true;
            } catch (IOException e) {
                Log.e(TAG, "" + e);
            } finally {
                if (fileOutput != null) {
                    try {
                        fileOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (success) {
                boolean unzipSuccess = ARGearSDK.unzip(targetPath, itemId);
                success = unzipSuccess;
            }

            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result) {
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // 다운로드가 성공하면 item 상태를 사용 가능한 상태로 변경합니다
                        ItemModel item = realm.where(ItemModel.class).equalTo("uuid", itemId).findFirst();
                        item.downloadStatus = ItemModel.STATUS_OK;
                    }
                }, new Realm.Transaction.OnSuccess(){
                    @Override
                    public void onSuccess() {
                        // 다운로드가 성공하고 item 상태변경이 끝났으면 ui 를 업데이트 합니다.
                        if(isSticker) {
                            mStickerListAdapter.notifyItemChanged(itemPosition);
                        }else{
                            mFilterListAdapter.notifyItemChanged(itemPosition);
                        }
                    }
                });
                Log.d(TAG, "download success!");
            } else {
                Log.e(TAG, "download failed!");
            }
        }
    }
}


