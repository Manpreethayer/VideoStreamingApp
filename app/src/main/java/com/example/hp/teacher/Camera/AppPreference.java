package com.example.hp.teacher.Camera;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by daewookjung on 14/06/2018.
 */

public class AppPreference {

    private static AppPreference mInstance = null ;
    private static SharedPreferences mPrefs = null;

    private final static String PREF_KEY_FACE_LANDMARK = "prefKeyFaceLandmark";
    private final static String PREF_KEY_FACE_RECT = "prefKeyFaceRect";
    private final static String PREF_KEY_BITRATE = "prefKeyBitrate";
    private final static String PREF_KEY_CAMERA_RATIO = "prefKeyCameraRatio";
    private final static String PREF_KEY_RECORD_VIDEO_ONLY= "prefKeyVideoOnly";

    private AppPreference(Context context) {
        mPrefs = context.getSharedPreferences("com.seerslab.argear.preference", Context.MODE_PRIVATE);
    }

    public static AppPreference getInstance(Context context) {
        if (mInstance == null) {
            synchronized(AppPreference.class){
                if(mInstance == null) {
                    mInstance = new AppPreference(context);
                }
            }
        }
        return mInstance;
    }

    public boolean isCheckedLandmark() {
        return mPrefs.getBoolean(PREF_KEY_FACE_LANDMARK, false);
    }

    public void setLandmark(boolean flag) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(PREF_KEY_FACE_LANDMARK, flag);
        editor.apply();
    }

    public boolean isCheckedFaceRect() {
        return mPrefs.getBoolean(PREF_KEY_FACE_RECT, false);
    }

    public void setFaceRect(boolean flag) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(PREF_KEY_FACE_RECT, flag);
        editor.apply();
    }

    public boolean isCheckedVideoOnly() {
        return mPrefs.getBoolean(PREF_KEY_RECORD_VIDEO_ONLY, false);
    }

    public void setVideoOnly(boolean flag) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(PREF_KEY_RECORD_VIDEO_ONLY, flag);
        editor.apply();
    }

    public int getBitrate() {
        return mPrefs.getInt(PREF_KEY_BITRATE, 3);
    }

    public void setBitrate(int value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREF_KEY_BITRATE, value);
        editor.apply();
    }

    public int getCameraRatio() {
        return mPrefs.getInt(PREF_KEY_CAMERA_RATIO, 1);
    }

    public void setCameraRatio(int value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREF_KEY_CAMERA_RATIO, value);
        editor.apply();
    }
}
