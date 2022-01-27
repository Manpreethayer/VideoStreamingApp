package com.example.hp.teacher.Camera;

public abstract class ReferenceCamera {

    public static final int CAMERA_RATIO_FULL = 0;
    public static final int CAMERA_RATIO_4_3 = 1;

    public interface CameraStateListener {
        void onReadyCamera(boolean success, boolean availableFlash);
        void onCloseCamera(boolean stopRender);
        void onUpdateFaceFoundState(boolean foundFace);
    }

    protected abstract int getCameraFacingFront();
    protected abstract int getCameraFacingBack();

    public abstract void setFacing(int CameraFacing);
    public abstract void setCameraStateListener(CameraStateListener listener);
    public abstract boolean isCameraFacingFront();
    public abstract int[] getPreviewSize();
    public abstract void startCamera();
    public abstract void stopCamera();


    public abstract void destroy();
    public abstract boolean changeCameraFacing();
    public abstract int getCameraRatio();
    public abstract boolean changeCameraRatio(int ratio);

    public int getCameraFacingFrontValue() {
        return getCameraFacingFront();
    }

    public int getCameraFacingBackValue() {
        return getCameraFacingBack();
    }
}