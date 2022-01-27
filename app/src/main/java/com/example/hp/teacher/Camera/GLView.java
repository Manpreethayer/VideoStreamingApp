package com.example.hp.teacher.Camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.seerslab.argearsdk.ARGearSDK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class GLView extends GLSurfaceView {

    private final GLRenderer mRenderer;
    private static final String TAG = GLView.class.getSimpleName();

    private int outputTextures[] = new int[2];

    public interface GLViewListener {
        void onGLSurfaceCreated();
    }

    private GLViewListener mListener = null;

    public GLView(Context context) {
        super(context);
        Log.e(TAG, " --- public GLView(Context context) ");
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        mRenderer = new GLRenderer();
        setRenderer(mRenderer);
        setZOrderOnTop(true);

        setRenderMode(RENDERMODE_CONTINUOUSLY);
        setPreserveEGLContextOnPause(false);

        if(ARGearSDK.DEBUGMODE) {
            Log.e(TAG, " ******** public GLView(Context context) ");
        }
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e(TAG, " --- public GLView(Context context, AttributeSet attrs) ");
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        mRenderer = new GLRenderer();
        setRenderer(mRenderer);
        setZOrderOnTop(true);

        setRenderMode(RENDERMODE_CONTINUOUSLY);
        setPreserveEGLContextOnPause(false);

        if(ARGearSDK.DEBUGMODE) {
            Log.e(TAG, " ******** public GLView(Context context, AttributeSet attrs) ");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        if(ARGearSDK.DEBUGMODE) {
            Log.e(TAG, " ******** public void onResume() ");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        if(ARGearSDK.DEBUGMODE) {
            Log.e(TAG, " ******** public void onPause() ");
        }
    }

    public void setGLViewListener(GLViewListener listener) {
        mListener = listener;
    }

    private class GLRenderer implements Renderer {

        private int viewWidth;
        private int viewHeight;

        private FloatBuffer pVertex;
        private FloatBuffer pTexCoord;
        private int hProgram;

        private final String vss =
                "attribute vec2 vPosition;\n" +
                        "attribute vec2 vTexCoord;\n" +
                        "varying vec2 texCoord;\n" +
                        "void main() {\n" +
                        "  texCoord = vTexCoord;\n" +
                        "  gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );\n" +
                        "}";

        private final String fss =
                "precision mediump float;\n" +
                        "uniform sampler2D sTexture;\n" +
                        "varying vec2 texCoord;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = texture2D(sTexture,texCoord);\n" +
                        "}";

        @Override
        public void onDrawFrame(GL10 gl) {
            // ARGearSDK 렌더링
            ARGearSDK.onDrawFrame(gl, outputTextures);

            // ARGearSDK를 통한 결과를 화면에 렌더링
            drawCameraTexture(outputTextures[0]);

            // ARGearSDK를 통한 결과를 raw data로 변환
            //getRawData();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.d(TAG, " --- public void onSurfaceChanged(GL10 gl, int width, int height) : ( "+width+", "+height+")");
            ARGearSDK.onSurfaceChanged(gl, width, height);

            initGL(width,height);
            if(ARGearSDK.DEBUGMODE) {
                Log.d(TAG, " ******** public void onSurfaceChanged(GL10 gl, int width, int height) ");
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.d(TAG, " --- public void onSurfaceCreated(GL10 gl, EGLConfig config) ");
            ARGearSDK.onSurfaceCreated(gl, config);

            if (mListener != null) {
                mListener.onGLSurfaceCreated();
            }

            if(ARGearSDK.DEBUGMODE) {
                Log.d(TAG, " ******** public void onSurfaceCreated(GL10 gl, EGLConfig config) ");
            }
        }

        private void initGL(int width, int height){
            viewWidth = width;
            viewHeight = height;

            float[] vtmp = { 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f };
            //float[] ttmp = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f };
            float[] ttmp = { 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f };

            pVertex = ByteBuffer.allocateDirect(8*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            pVertex.put ( vtmp );
            pVertex.position(0);
            pTexCoord = ByteBuffer.allocateDirect(8*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            pTexCoord.put ( ttmp );
            pTexCoord.position(0);

            hProgram = loadShader ( vss, fss );
        }

        private void drawCameraTexture(int taxID) {
            if(ARGearSDK.DEBUGMODE) {
                Log.d(TAG, " --- out_textureID = " + taxID);
            }

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            GLES20.glViewport( 0, 0, viewWidth, viewHeight );
            GLES20.glUseProgram(hProgram);

            int ph = GLES20.glGetAttribLocation(hProgram, "vPosition");
            int tch = GLES20.glGetAttribLocation ( hProgram, "vTexCoord" );
            int th = GLES20.glGetUniformLocation ( hProgram, "sTexture" );

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, taxID);
            GLES20.glUniform1i(th, 0);

            GLES20.glVertexAttribPointer(ph, 2, GLES20.GL_FLOAT, false, 4*2, pVertex);
            GLES20.glVertexAttribPointer(tch, 2, GLES20.GL_FLOAT, false, 4*2, pTexCoord );
            GLES20.glEnableVertexAttribArray(ph);
            GLES20.glEnableVertexAttribArray(tch);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glFlush();
        }

        private  int loadShader (String vss, String fss ) {
            int vshader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            GLES20.glShaderSource(vshader, vss);
            GLES20.glCompileShader(vshader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(vshader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                if(ARGearSDK.DEBUGMODE) Log.e("Shader", "Could not compile vshader");
                if(ARGearSDK.DEBUGMODE) Log.v("Shader", "Could not compile vshader:"+ GLES20.glGetShaderInfoLog(vshader));
                GLES20.glDeleteShader(vshader);
                vshader = 0;
            }

            int fshader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
            GLES20.glShaderSource(fshader, fss);
            GLES20.glCompileShader(fshader);
            GLES20.glGetShaderiv(fshader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                if(ARGearSDK.DEBUGMODE) Log.e("Shader", "Could not compile fshader");
                if(ARGearSDK.DEBUGMODE) Log.v("Shader", "Could not compile fshader:"+ GLES20.glGetShaderInfoLog(fshader));
                GLES20.glDeleteShader(fshader);
                fshader = 0;
            }

            int program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vshader);
            GLES20.glAttachShader(program, fshader);
            GLES20.glLinkProgram(program);

            return program;
        }

        // Framebuffer에서 RawData 가져오는 방법 예시 함수
        public void getRawData() {

            int width = ARGearSDK.CameraPreviewSize[0];
            int height = ARGearSDK.CameraPreviewSize[1];
            int fboId = ARGearSDK.renderingFboID();

            if(width == 0 || height == 0 || fboId == 0) return;

            // 1. Framebuffer의 RawData 가져옴
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

            ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);


            // 2. RawData를 이용하여 jpg파일로 저장 (확인용)
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);

            Bitmap resize = Bitmap.createScaledBitmap(bitmap, height, width, false);

            File fileCacheItem = new File("/mnt/sdcard/result.jpg");
            OutputStream out = null;

            try {
                fileCacheItem.createNewFile();
                out = new FileOutputStream(fileCacheItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
            resize.compress(Bitmap.CompressFormat.JPEG, 100, out);

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int targetWidth = ((CameraActivity)getContext()).getTargetWidth();
        int targetHeight = ((CameraActivity)getContext()).getTargetHeight();

        Log.d(TAG, "onMeasure " + targetWidth + " " + targetHeight + " " + width + " " + height);

        if(targetWidth > 0 && targetHeight > 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(targetWidth, targetHeight);
            ((CameraActivity)getContext()).setMeasureSurfaceView(this);
        }else{
            setMeasuredDimension(width, height);
        }
    }
}
