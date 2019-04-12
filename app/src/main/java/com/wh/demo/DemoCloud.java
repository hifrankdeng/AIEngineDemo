package com.wh.demo;

import android.util.Log;
import android.view.View;

import com.wh.camapp.CanvasView;
import com.wh.camapp.Config;
import com.ysten.ai.bean.NuiMessage;
import com.ysten.ai.cloud.YstenAICloud;
import com.ysten.ai.cloud.YstenAICloudCallBack;

public class DemoCloud extends WorkerBase
{
    public static final String name = "云端人脸识别";
    public View getContentView()
    {
        return null;
    }
    private YstenAICloud aiCloud = new YstenAICloud();

    @Override
    public void setup(String dir,int screenW,int screenH)
    {
        super.setup(dir,screenW,screenH);
        aiCloud.setRequestParam(SERVER_URL,APPID);
        mAvgTime = 0;
    }


    @Override
    public String processFrame(byte[] data, int previewWidth, int previewHeight, final CanvasView canvasView)
    {
        // correct mapper
        if(mFlip) {
            mapper.set(0, 0, mScreenW, 0, previewWidth, previewHeight, 0, mScreenH);
        }
        else
        {
            mapper.set(0, 0, 0, 0, previewWidth, previewHeight, mScreenW, mScreenH);
        }
        //face rec
        aiCloud.setCloudCallBack(new YstenAICloudCallBack() {
            @Override
            public void onSuccess(String s, NuiMessage nuiMessage) {
                Log.i("DemoCloud",s);
                updateResultView(nuiMessage, canvasView,false);
            }

            @Override
            public void onFailure(String s) {
                Log.e("DemoCloud",s);
            }
        });
        long startTime = System.currentTimeMillis();  // 開始時間

        String[] nodes = {YstenAICloud.NODE_FACE_REC};
        aiCloud.run(nodes, previewWidth, previewHeight, data);
        long dt = System.currentTimeMillis() - startTime; // 消耗時間
        Config.updateFps(dt);
        return null;
    }
}
