package com.example.hp.teacher.Camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.seerslab.argearsdk.ARGearSDK;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.max;


@SuppressWarnings("deprecation")
public class ReferenceCamera1 extends ReferenceCamera {

    private final String TAG = ReferenceCamera1.class.getSimpleName();

    // 아래의 카메라 ID에 기종별 int 값을 정의하면 됩니다. (현재는 Camera API의 Camera ID 사용하고 있습니다.)
    private static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private Camera mCamera;
    private int NUM_BUFFERS = 5;
    private SurfaceTexture mCameraTexture;

    private final AtomicBoolean mSwitchingCamera = new AtomicBoolean(false);
    private final AtomicBoolean mIsStarted;

    private final List<Integer> mFrontCameraIds = new ArrayList<>();
    private final List<Integer> mRearCameraIds = new ArrayList<>();

    private int mCameraID;
    private final int[] mCameraIdIndices = new int[2];
    private boolean mSupportedFlash = false;

    private int[] mPreviewSize = new int[2];
    private int[] mVideoSize = new int[2];

    private int mCameraOrientation = 0;
    private int mNumCameras = 0;

    private final int mDeviceRotation;

    private boolean mPauseCameraRendering;

    private int mCameraRatio = CAMERA_RATIO_4_3;

    private Context mContext;

    private CameraStateListener mListener = null;

    private FaceDetectionListener mFaceDetectionListener = new FaceDetectionListener() {
        @Override
        public void onFaceDetection(Face[] faces, Camera camera) {
            /*
             * 카메라 HW 에서 전달되는 얼굴 정보를 SDK 에 전달 합니다
             */
            ARGearSDK.setFaceRects(faces);
        }
    };

    private final Camera.PreviewCallback mPreviewCb = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (mPauseCameraRendering) {
                if (mListener != null) {
                    mListener.onReadyCamera(true, mSupportedFlash);
                }
                mPauseCameraRendering = false;
            }

            /*
             * 카메라 HW 에서 획득한 preview frame 정보를 sdk 에 전달합니다
             */
            boolean ret = ARGearSDK.processFrame(data);
            if (mListener != null) {
                mListener.onUpdateFaceFoundState(ret);
            }

            camera.addCallbackBuffer(data);
        }
    };

    private final Camera.ErrorCallback mErrorCb = new Camera.ErrorCallback() {
        @Override
        public void onError(int error, Camera camera) {
            Log.e(TAG, "camera error - " + error);
            stopCamera();
            startCamera(mCameraID, mCameraIdIndices[mCameraID]);
        }
    };


    public ReferenceCamera1(Context context, int deviceRotation) {
        mDeviceRotation = deviceRotation;
        mIsStarted = new AtomicBoolean(false);
        mPauseCameraRendering = false;

        mNumCameras = Camera.getNumberOfCameras();
        mContext = context;

        initializeCameraIds();
    }

    public ReferenceCamera1(Context context, int deviceRotation, int ratio) {
        mDeviceRotation = deviceRotation;
        mIsStarted = new AtomicBoolean(false);
        mPauseCameraRendering = false;

        mNumCameras = Camera.getNumberOfCameras();
        mContext = context;

        mCameraRatio = ratio;

        initializeCameraIds();
    }

    @Override
    public void setCameraStateListener(CameraStateListener listener) {
        mListener = listener;
    }

    @Override
    protected int getCameraFacingFront() {
        if (mFrontCameraIds.size() == 0) {
            return -1;
        }
        return CAMERA_FACING_FRONT;
    }

    @Override
    protected int getCameraFacingBack() {
        if (mRearCameraIds.size() == 0) {
            return -1;
        }
        return CAMERA_FACING_BACK;
    }

    @Override
    public void setFacing(int CameraFacing) {
        mCameraID = CameraFacing;
    }

    @Override
    public void startCamera() {
        mCameraTexture = ARGearSDK.getCameraSurfaceTexture();
        startCamera(mCameraID, mCameraIdIndices[mCameraID]);
    }

    @Override
    public void stopCamera() {
        Log.d(TAG , "stopCamera");
        synchronized (mIsStarted) {
            if (mIsStarted.compareAndSet(true, false)) {
                try {
                    mCamera.stopPreview();
                    releaseCamera(mCamera);
                    mCamera = null;
                } catch (NullPointerException e) {
                    Log.e(TAG, "Error Stopping camera - NullPointerException: ", e);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Error Stopping camera - RuntimeException: ", e);
                }
            }

            if (mListener != null) {
                mListener.onCloseCamera(false);
            }
        }
    }






    @Override
    public void destroy() {}

    @Override
    public boolean isCameraFacingFront(){
        return mCameraID == CAMERA_FACING_FRONT;
    }

    @Override
    public boolean changeCameraRatio(int ratio) {
        mCameraRatio = ratio;
        if (mSwitchingCamera.compareAndSet(false, true)) {
            changeCamera(mCameraID, mCameraIdIndices[mCameraID]);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getCameraRatio() {
        return mCameraRatio;
    }




    @Override
    public boolean changeCameraFacing() {
        if (mFrontCameraIds.size() == 0 || mRearCameraIds.size() == 0) {
            return false;
        }

        if (mSwitchingCamera.compareAndSet(false, true)) {
            if (mCameraID == CAMERA_FACING_BACK) {
                mCameraID = CAMERA_FACING_FRONT;
            } else if (mCameraID == CAMERA_FACING_FRONT) {
                mCameraID = CAMERA_FACING_BACK;
            }
            changeCamera(mCameraID, mCameraIdIndices[mCameraID]);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int[] getPreviewSize() {
        return mPreviewSize;
    }

    public boolean isRunning() {
        return mIsStarted.get();
    }

    public int[] getVideoSize() {
        return mVideoSize;
    }

    public int getOrientation() {
        return mCameraOrientation;
    }

    private int getCameraId(int facing, int cameraIdIndex) {
        int cameraId = -1;
        if (facing == CAMERA_FACING_FRONT
                && mFrontCameraIds.size() > cameraIdIndex) {
            cameraId = mFrontCameraIds.get(cameraIdIndex);
        } else if (facing == CAMERA_FACING_BACK
                && mRearCameraIds.size() > cameraIdIndex) {
            cameraId = mRearCameraIds.get(cameraIdIndex);
        }
        return cameraId;
    }

    private void initializeCameraIds() {
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < mNumCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CAMERA_FACING_FRONT) {
                    mFrontCameraIds.add(i);
                } else if (cameraInfo.facing == CAMERA_FACING_BACK) {
                    mRearCameraIds.add(i);
                }
            }
            mCameraIdIndices[0] = 0;
            mCameraIdIndices[1] = 0;
        } catch (RuntimeException e) {
            Log.e(TAG, "Getting camera IDs failed.", e);
            mFrontCameraIds.clear();
            mRearCameraIds.clear();
            mCameraIdIndices[0] = 0;
            mCameraIdIndices[1] = 0;
        }
    }

    private Camera openCamera(int cameraId) throws RuntimeException {
        Camera ret = null;
        if (cameraId >= 0 && cameraId < mNumCameras) {
            ret = Camera.open(cameraId);
        }
        return ret;
    }

    private void initCamera(int facing) throws IOException {

        mCamera.setPreviewTexture(mCameraTexture);

        mCameraOrientation = setCameraDisplayOrientation(facing, mDeviceRotation);

        Log.d(TAG, "initCamera " + mDeviceRotation + " " + mCameraOrientation);
    }

    private void initCameraParameter(@NonNull Parameters camParams) {
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        printSizes(camParams.getSupportedPreviewSizes(), "preview");
        printSizes(camParams.getSupportedVideoSizes(), "video");

        Size previewSize;
        Size videoSize;

        Point realSize = new Point();
        display.getRealSize(realSize);
        int width = realSize.x;
        int height = realSize.y;

        if (mCameraRatio == CAMERA_RATIO_FULL) {
            previewSize = getOptimalSize(camParams.getSupportedPreviewSizes(), width, height, "preview");
            videoSize = getOptimalSize(camParams.getSupportedVideoSizes(), previewSize.height, previewSize.width, "video");
        } else {
            previewSize = getFittedPreviewSize(camParams.getSupportedPreviewSizes(), 0.75f);
            videoSize = getOptimalSize(camParams.getSupportedVideoSizes(), previewSize.height, previewSize.width, "video");
        }

        if (videoSize == null) {
            videoSize = previewSize;
        }

        if (previewSize != null) {
            camParams.setPreviewSize(previewSize.width, previewSize.height);
        } else {
            previewSize = camParams.getPreviewSize();
        }

        Log.d(TAG , "displayMetrics w = " + width + "  h = " + height);
        Log.d(TAG , " ==== previewSize w = " + previewSize.width + "  h = " + previewSize.height);
        Log.d(TAG , "videoSize w = " + videoSize.width + "  h = " + videoSize.height);

        mPreviewSize[0] = previewSize.width;
        mPreviewSize[1] = previewSize.height;
        //mPreviewSize[1] = (int)((float)previewSize.width * screenRatio); //previewSize.height;

        mVideoSize[0] = videoSize.width;
        mVideoSize[1] = videoSize.height;

        // 플래시.
        List<String> supportedFlashModes = camParams.getSupportedFlashModes();
        mSupportedFlash = supportedFlashModes != null && supportedFlashModes.contains(Parameters.FLASH_MODE_TORCH);

        // Auto-focus
        if (camParams.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            camParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (camParams.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_AUTO)) {
            camParams.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        }

        final int previewWidth = camParams.getPreviewSize().width;
        final int previewHeight = camParams.getPreviewSize().height;

        final float horizontalFov = camParams.getVerticalViewAngle();
        Log.i(TAG, String.format("Horizontal FOV:%.2f", horizontalFov));

        //  Detection FPS: Frequency of facial detector when faces are missing.
        //  0.0f means that it finds faces ASAP when missing.
        final float detectionFps = 0.0f;

        final boolean isFrontFacing = isCameraFacingFront();

        int[] frameRateRange = new int[2];
        camParams.getPreviewFpsRange(frameRateRange);
        final float fps = frameRateRange[1] / 1000.0f;
        Log.i(TAG, String.format("FPS:%.2f", fps));


        ARGearSDK.setTrackerConfig(previewWidth,
                previewHeight,
                horizontalFov,
                detectionFps,
                mCameraOrientation,
                isFrontFacing,
                fps);
    }

    private void startCamera(final int facing, final int cameraIdIndex) {
        Log.d(TAG , "startCamera " + mIsStarted + " " + mCameraTexture);
        
        synchronized (mIsStarted) {
            if (mIsStarted.compareAndSet(false, true)) {
                if (mFrontCameraIds.size() == 0 && mRearCameraIds.size() == 0) {
                    mIsStarted.set(false);
                    mListener.onReadyCamera(false, false);
                    return;
                }

                int cameraId = getCameraId(facing, cameraIdIndex);
                if (cameraId < 0) {
                    mIsStarted.set(false);
                    mListener.onReadyCamera(false, false);
                    return;
                }

                if (mCameraTexture != null) {
                    try {
                        mCamera = openCamera(cameraId);

                        initCamera(facing);

                        Parameters camParams = mCamera.getParameters();
                        initCameraParameter(camParams);
                        mCamera.setErrorCallback(mErrorCb);
                        mCamera.setParameters(camParams);
                        mCamera.setPreviewCallbackWithBuffer(mPreviewCb);

                        int size = mPreviewSize[0] * mPreviewSize[1] * 3 / 2;
                        for (int i=0; i<NUM_BUFFERS; i++)
                            mCamera.addCallbackBuffer(new byte[size]);

                        mCamera.startPreview();

                        if (mListener != null) {
                            mListener.onReadyCamera(true, mSupportedFlash);
                        }

                        int maxAmountOffaces = mCamera.getParameters().getMaxNumDetectedFaces();
                        if(maxAmountOffaces > 0){
                            mCamera.setFaceDetectionListener(mFaceDetectionListener);
                            mCamera.startFaceDetection();
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Error opening camera - NullPointerException: ", e);
                        mIsStarted.set(false);
                        if (mListener != null) {
                            mListener.onReadyCamera(false, false);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "SetPreviewTexture failed.", e);
                        if (mCamera != null) {
                            releaseCamera(mCamera);
                            mCamera = null;
                        }
                        mIsStarted.set(false);
                        if (mListener != null) {
                            mListener.onReadyCamera(false, false);
                        }
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Error opening camera - RuntimeException: ", e);
                        if (mCamera != null) {
                            releaseCamera(mCamera);
                            mCamera = null;
                        }
                        mIsStarted.set(false);
                        if (mListener != null) {
                            mListener.onReadyCamera(false, false);
                        }
                    }
                } else {
                    mIsStarted.set(false);
                }
            }
        }

    }

    private void changeCamera(final int cameraFacing, final int cameraIdIndex) {

        if (mIsStarted.compareAndSet(true, false)) {
            try {
                mCamera.stopPreview();
                releaseCamera(mCamera);
                mCamera = null;
                mPauseCameraRendering = true;
            } catch (NullPointerException e) {
                Log.e(TAG, "Error Stopping camera - NullPointerException: ", e);
            } catch (RuntimeException e) {
                Log.e(TAG, "Error Stopping camera - RuntimeException: ", e);
            }

            if (mListener != null) {
                mListener.onCloseCamera(true);
            }

            int cameraId = getCameraId(cameraFacing, cameraIdIndex);

            if (mIsStarted.compareAndSet(false, true)) {
                if ((mCameraTexture != null) && cameraId >= 0) {
                    try {
                        mCamera = openCamera(cameraId);
                        initCamera(cameraFacing);

                        Parameters camParams = mCamera.getParameters();
                        initCameraParameter(camParams);
                        mCamera.setErrorCallback(mErrorCb);
                        mCamera.setParameters(camParams);
                        mCamera.setPreviewCallbackWithBuffer(mPreviewCb);

                        int size = mPreviewSize[0] * mPreviewSize[1] * 3 / 2;
                        for (int i=0; i<NUM_BUFFERS; i++)
                            mCamera.addCallbackBuffer(new byte[size]);
                        mCamera.startPreview();

                        int maxAmountOffaces = mCamera.getParameters().getMaxNumDetectedFaces();
                        if(maxAmountOffaces > 0){
                            mCamera.setFaceDetectionListener(mFaceDetectionListener);
                            mCamera.startFaceDetection();
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Error opening camera - NullPointerException: ", e);
                        mIsStarted.set(false);
                        if (mListener != null) {
                            mListener.onReadyCamera(false, false);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "SetPreviewTexture failed.", e);
                        if (mCamera != null) {
                            releaseCamera(mCamera);
                            mCamera = null;
                        }
                        mIsStarted.set(false);
                        if (mListener != null) {
                            mListener.onReadyCamera(false, false);
                        }
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Error opening camera - RuntimeException: ", e);
                        if (mCamera != null) {
                            releaseCamera(mCamera);
                            mCamera = null;
                        }
                        mIsStarted.set(false);
                        if (mListener != null) {
                            mListener.onReadyCamera(false, false);
                        }
                    }
                }
            }
        }


        mSwitchingCamera.set(false);
    }

    private void releaseCamera(@NonNull Camera camera) {
        try {
            camera.setErrorCallback(null);
//            mCamera.setPreviewCallback(null);
            camera.setPreviewTexture(null);
            camera.setPreviewCallbackWithBuffer(null);
            camera.setFaceDetectionListener(null);
            camera.addCallbackBuffer(null);
            camera.release();
        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "Release camera error.", e);
        }
    }

    private Size getFittedPreviewSize(List<Size> supportedPreviewSize, float previewRatio) {
        Size retSize = null;
        for (Size size : supportedPreviewSize) {
            float ratio = size.height / (float) size.width;
            float EPSILON = (float) 1e-4;
            if (Math.abs(ratio - previewRatio) < EPSILON && max(size.width, size.height) <= 1024 ) {
                if (retSize == null || retSize.width < size.width) {
                    retSize = size;
                }
            }
        }
        return retSize;
    }

    private Size getOptimalSize(List<Size> sizes, int w, int h, String where) {

        Log.d(TAG, "getOptimalSize " + w + " " + h + " " + where + " " +  sizes);

        if (sizes == null) {
            return null;
        }

        double targetRatio = (double) h / w;
        Size optimalSize = null;
        double ASPECT_TOLERANCE = 0.0;

        // 1. find exactly matched
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            int maxSize = Math.max(size.width , size.height);
            Log.d(TAG, "optimal size (exactly) " + size.width + " " + size.height + " " +
                    ratio + " " + targetRatio + " " + Math.abs(ratio - targetRatio));

            boolean isCorrectkSize;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                isCorrectkSize = (size.height <= w);
            } else {
                isCorrectkSize = (maxSize <= 1280 && size.height <= w);
            }

            if (Math.abs(ratio - targetRatio) == ASPECT_TOLERANCE && isCorrectkSize) {
                optimalSize = size;
                break;
            }
        }

        if (optimalSize == null) {
            double minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                double ratio = (double) size.width / size.height;
                int maxSize = Math.max(size.width , size.height);
                Log.d(TAG, "optimal size (step 2) " + size.width + " " + size.height + " "
                        + targetRatio + " " + minDiff);

                boolean isCorrectkSize;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    isCorrectkSize = (size.height <= w);
                } else {
                    isCorrectkSize = (maxSize <= 1280 && size.height <= w);
                }

                if (Math.abs(targetRatio - ratio) < minDiff && isCorrectkSize) {
                    optimalSize = size;
                    minDiff = Math.abs(targetRatio - ratio);
                }
            }
        }

        if (optimalSize != null) {
            Log.d(TAG, "result size " + targetRatio + " " + ((double)optimalSize.width / (double)optimalSize.height) + " " + optimalSize.width + " " + optimalSize.height);
        } else {
            Log.d(TAG, "could not find optimal size " + targetRatio);
        }

        return optimalSize;
    }

    private int setCameraDisplayOrientation(int cameraId, int deviceRotation) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        Log.d(TAG, "setCameraDisplayOrientation " + deviceRotation + " " + info.orientation);

        int degrees = 0;
        switch (deviceRotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(result);

        return result;
    }

    private void printSizes(List<Size> sizes, String title){
        if(sizes != null) {
            for (Size size : sizes) {
                double ratio = (double) size.width / size.height;
                Log.d(TAG, size.width + " " + size.height + " " + ratio);
            }
        }
    }
}
