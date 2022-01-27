package com.example.hp.teacher.Camera;


import android.os.Environment;

public class AppConfig {

    public static final String API_URL = "https://api.argear.io";

    // 데모용 API
    public static final String API_KEY = "bf905f01cf1de5a1bbc518e2";
    public static final String SECRET_KEY = "385bac1d1dfdd2da137c8a0d01ae7945300b063ac09863ddcb928c81d2a06d70";


    public static final String imagePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/imagery_user_Pic.jpg";
    public static final String SAVE_PICTURE_PATH =  imagePath;
    public static final String RECORDING_FILE_NAMW = "imagery-video-recording.mp4";

    // camera
    public static final int CAMERA_API_1 = 1;
    public static final int CAMERA_API_2 = 2;

    // sample/build.gradle "minSdkVersion" 설정 변경
    // CAMERA_API_1 -> minSdkVersion 19
    // CAMERA_API_2 -> minSdkVersion 21 (현재 미지원)
    public static final int USE_CAMERA_API  = CAMERA_API_1;

    // bulge beauty type ID
    // param : [0]vLineParam, [1]faceSlimPram, [2]jawPram, [3]chinParam, [4]eyeParam, [5]eyeGapParam,
    //         [6]noseLineParam, [7]noseSideParam, [8]noseLengthParam, [9]mouthSizeParam [10]eyeBackParam */
    public static final int BEAUTY_TYPE_VLINE             = 0;
    public static final int BEAUTY_TYPE_FACE_SLIM         = 1;
    public static final int BEAUTY_TYPE_JAW               = 2;
    public static final int BEAUTY_TYPE_CHIN              = 3;
    public static final int BEAUTY_TYPE_EYE               = 4;
    public static final int BEAUTY_TYPE_EYE_GAP           = 5;
    public static final int BEAUTY_TYPE_NOSE_LINE         = 6;
    public static final int BEAUTY_TYPE_NOSE_SIDE         = 7;
    public static final int BEAUTY_TYPE_NOSE_LENGTH       = 8;
    public static final int BEAUTY_TYPE_MOUTH_SIZE        = 9;
    public static final int BEAUTY_TYPE_EYE_BACK          = 10;
    public static final int BEAUTY_TYPE_EYE_CORNER        = 11;
    public static final int BEAUTY_TYPE_LIP_SIZE          = 12;
    public static final int BEAUTY_TYPE_SKIN_FACE         = 13;
    public static final int BEAUTY_TYPE_SKIN_DARK_CIRCLE  = 14;
    public static final int BEAUTY_TYPE_SKIN_MOUTH_WRINKLE= 15;
    public static final int BEAUTY_ORIGINAL_TYPE_NUM      = 16; // beauty1 타입 총 갯수 //BEAUTY_ORIGINAL_BEAUTY_TYPE 총 갯수
    public static final int BEAUTY_BULGE_TYPE_NUM         = 13; // beauty2 타입 총 갯수 // BEAUTY_BULGE_TYPE 총 갯수


    public static final int[] BEAUTY_TYPE_MAX_VALUE = {
            0,   //BEAUTY_TYPE_VLINE
            0,   //BEAUTY_TYPE_FACE_SLIM
            0,   //BEAUTY_TYPE_JAW
            100, //BEAUTY_TYPE_CHIN
            0,   //BEAUTY_TYPE_EYE
            100, //BEAUTY_TYPE_EYE_GAP
            0,   //BEAUTY_TYPE_NOSE_LINE
            0,   //BEAUTY_TYPE_NOSE_SIDE
            100, //BEAUTY_TYPE_NOSE_LENGTH
            100, //BEAUTY_TYPE_MOUTH_SIZE
            0,   //BEAUTY_TYPE_EYE_BACK
            100, //BEAUTY_TYPE_EYE_CORNER
            100, //BEAUTY_TYPE_LIP_SIZE
            0,   //BEAUTY_TYPE_SKIN
            0,   //BEAUTY_TYPE_DARK_CIRCLE
            0,   //BEAUTY_TYPE_MOUTH_WRINKLE
    };
    public static final int[] BEAUTY_ORIGINAL_TYPE_INIT_VALUE = {
            10, //브이라인   //BEAUTY_TYPE_VLINE
            90, //슬림하게   //BEAUTY_TYPE_FACE_SLIM
            55, //얼굴길이   //BEAUTY_TYPE_JAW
            -50,//턱       //BEAUTY_TYPE_CHIN
            5,  //눈크기    //BEAUTY_TYPE_EYE
            -10,//눈간격    //BEAUTY_TYPE_EYE_GAP
            0,  //코좁게    //BEAUTY_TYPE_NOSE_LINE
            35, //콧볼     //BEAUTY_TYPE_NOSE_SIDE
            30, //코길이    //BEAUTY_TYPE_NOSE_LENGTH
            -35,//입크기    //BEAUTY_TYPE_MOUTH_SIZE
            0,  //뒤트임    //BEAUTY_TYPE_EYE_BACK
            0,  //눈꼬리    //BEAUTY_TYPE_EYE_CORNER
            0,  //입술     //BEAUTY_TYPE_LIP_SIZE
            50, //피부     //BEAUTY_TYPE_SKIN
            0,  //다크서클   //BEAUTY_TYPE_DARK_CIRCLE
            0,  //팔자주름   //BEAUTY_TYPE_MOUTH_WRINKLE
    };

    public static final int[] BEAUTY_BULGE_TYPE_INIT_VALUE = {
            7, //BEAUTY_TYPE_VLINE
            7, //BEAUTY_TYPE_FACE_SLIM
           18, //BEAUTY_TYPE_JAW
            0, //BEAUTY_TYPE_CHIN
            7, //BEAUTY_TYPE_EYE
            0, //BEAUTY_TYPE_EYE_GAP
            0, //BEAUTY_TYPE_NOSE_LINE
            7, //BEAUTY_TYPE_NOSE_SIDE
           10, //BEAUTY_TYPE_NOSE_LENGTH
           20, //BEAUTY_TYPE_MOUTH_SIZE
           -5, //BEAUTY_TYPE_EYE_BACK
            0, //BEAUTY_TYPE_EYE_CORNER
            0, //BEAUTY_TYPE_LIP_SIZE
            50, //BEAUTY_TYPE_SKIN
    };


    public static final int BASIC_BEAUTY_TYPE_1 = 100;
    public static final int BASIC_BEAUTY_TYPE_2 = 200;
    public static final int BASIC_BEAUTY_TYPE_3 = 300;

    public static final float[] BASIC_BEAUTY_1 = {
            20,//브이라인
            10, //슬림하게
            45,//얼굴길이
            45,//턱
            5,//눈크기
            -10, //눈간격
            40,//코좁게
            20, //콧볼
            15, //코길이
            0, //입크기
            0, //뒤트임
            0, //눈꼬리
            0, //입술
            50,//피부
            0,
            0,
    };

    public static final float[] BASIC_BEAUTY_2 = {
            10,//브이라인
            90, //슬림하게
            55,//얼굴길이
            -50, //턱
            5,//눈크기
            -10, //눈간격
            0,//코좁게
            35, //콧볼
            30, //코길이
            -35, //입크기
            0, //뒤트임
            0, //눈꼬리
            0, //입술
            50,//피부
            0,
            0,
    };

    public static final float[] BASIC_BEAUTY_3 = {
            25,    //브이라인
            20,    //슬림하게
            50,    //얼굴길이
            -25,   //턱
            25,    //눈크기
            -10,   //눈간격
            30,    //코좁게
            40,    //콧볼
            90,    //코길이
            0,     //입크기
            0,     //뒤트임
            0,     //눈꼬리
            0,     //입술
            50,    //피부
            0,
            0,
    };


    public static final boolean BULGE_MODE_BEAUTY = true;    // 벌지 타입 : Beauty
    public static final boolean BULGE_MODE_FUN = false;      // 벌지 타입 : Fun

    // debug landmark
    public static final int FACE_RECT_HW    = (1 << 0);  // HW Rect 그리기
    public static final int FACE_RECT_SW    = (1 << 1);  // 결과 Rect 그리기
    public static final int DLIB_LANDMARK   = (1 << 2);  // 68 pt 표시
    public static final int FACE_AXIES      = (1 << 4);  // 기즈모 표시
}
