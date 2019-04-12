package com.wh.camapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/7/31.
 */

public final class Config {
    private static final String TAG = "Config";

    public static final String DATA_ROOT = "ysten/";
    public static final String GES_DATA_DIR = DATA_ROOT +"ges/res/";
    public static final String FR_DATA_DIR = DATA_ROOT+"facerec";
    // 当前数据模型的版本号
    public static final String DATA_VERSION = "2019.01.23";
    public static final String DATA_VERSION_FILE = DATA_ROOT + "data-version.txt";
    // DIR
    public static float  FPS = 0;
    public static int   FRAME_DELAY = 0;
   // public static float SYS_DELAY = 0;
    //public static float GES_T = 0;

    // 相机事件广播
    public static final String CAMERA_EVENT_BROADCAST = "com.ysten.manager_gesture";
    // CONFIG
    public static int ALG_START = 0;
    public static int ENABLE_FACEREC = 0;
    public static  int ENABLE_EMOTION = 1;
    public static boolean PAUSED = false;

    /** 发给系统，需要开放设备文件 /dev/uinput 写权限 */
    public static final int MOUSE_EVENT_SYSTEM	= 1;
    public static int MOUSE_EVENT = MOUSE_EVENT_SYSTEM;

    //写在/mnt/sdcard/目录下面的文件
    public static void writeFileSdcard(String fileName,String message){
        try{
            FileOutputStream fout = new FileOutputStream(fileName);
            byte [] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private static float mAvgTime = 0;
    public static float updateFps(long dt)
    {
        final int CT = 30;
        if (mAvgTime == 0 )
        {
            mAvgTime = dt;
        }
        else {
            mAvgTime = (mAvgTime * (CT-1) + dt) / CT;
        }

        // update mouse info
        Config.FPS = (1000/mAvgTime);
        Config.FRAME_DELAY = (int)dt;
        return FPS;
    }
}
