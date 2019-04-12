package com.wh.camapp;

import android.app.Application;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by Soul on 2017/9/14.
 */

public class App extends Application {
    static final String TAG = "App";
    private static App instance;
    private static Camera mCamera;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public static App getInstance() {
        return instance;
    }

    public static Camera getCamera() {
        if (mCamera != null) {
//            Log.e(TAG, "mCamera!=null");
            return mCamera;
        } else {
            Log.e(TAG, "mCamera==null");
            FrontCamera mFrontCamera = new FrontCamera();
            mFrontCamera.setCamera(mCamera);
            mCamera = mFrontCamera.initCamera();
            if (mCamera != null) {
                Log.e(TAG, "mCamera!=null");
            }
            return mCamera;
        }
    }

    public static void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

}
