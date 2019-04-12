package com.wh.demo;

import android.util.Log;
import android.view.View;

import com.wh.camapp.CanvasView;
import com.wh.camapp.Config;
import com.ysten.ai.bean.FaceMessage;
import com.ysten.ai.bean.LandmarkPoint;
import com.ysten.ai.bean.NuiMessage;
import com.ysten.ai.cloud.YstenAICloud;
import com.ysten.ai.cloud.YstenAICloudCallBack;
import com.ysten.ai.local.YstenAIEngine;

public class DemoCloudAndLocal extends WorkerBase
{
    public static final String name = "云端识别";
    public View getContentView()
    {
        return null;
    }
    private YstenAICloud aiCloud = new YstenAICloud();
    private NuiMessage mShowMessage = null;

    @Override
    public void setup(String dir,int screenW,int screenH)
    {
        super.setup(dir,screenW,screenH);
        YstenAIEngine.get().open(YstenAIEngine.NODE_FACE_DETECT);
        YstenAIEngine.get().setTrackEnable(true);
        aiCloud.setRequestParam(SERVER_URL,APPID);
        mAvgTime = 0;
    }

    @Override
    public void destroy()
    {
        YstenAIEngine.get().close();
        super.destroy();
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
        //face detect client + other cloud
        long startTime = System.currentTimeMillis();  // 開始時間
        String[] nodelist = new String[]{YstenAIEngine.NODE_FACE_DETECT};
        String json = YstenAIEngine.get().runFast(nodelist,previewWidth, previewHeight,data);
        long dt = System.currentTimeMillis() - startTime; // 消耗時間
        Config.updateFps(dt);
        if(json.isEmpty())
        {
            return "{}";
        }
        NuiMessage trackMessage = YstenAIEngine.format(json);
        if (trackMessage.faceList.size() < 1)//no face
        {
            return json;
        }
        mShowMessage = updataShowMessage(trackMessage);
        updateResultView(mShowMessage, canvasView,true);
        //face rec
        aiCloud.setCloudCallBack(new YstenAICloudCallBack() {
            @Override
            public void onSuccess(String s, NuiMessage nuiMessage) {
                Log.i("DemoCloud",s);
                if (nuiMessage != null) {
                    mShowMessage = nuiMessage;
                }
            }

            @Override
            public void onFailure(String s) {
                Log.e("DemoCloud",s);
            }
        });

        String[] nodes = {YstenAICloud.NODE_FACE_REC,YstenAICloud.NODE_FACE_ATT};
        aiCloud.runForFace(nodes, previewWidth, previewHeight, data,trackMessage);
        return null;
    }

    NuiMessage updataShowMessage(NuiMessage trackMessage){
        if(mShowMessage == null) {  //首次检测到
            return trackMessage;
        }
       // return mShowMessage;
        NuiMessage res =trackMessage;
        for (int i = 0 ; i < trackMessage.faceList.size(); i++){
            FaceMessage faceMessage = trackMessage.faceList.get(i);
            boolean exist = false;
            for (int j = 0 ; j < mShowMessage.faceList.size();j++){
                FaceMessage showMessage = mShowMessage.faceList.get(j);
                if (faceMessage.rectId == showMessage.rectId){//track face  update attribute
                    int dx = faceMessage.x - showMessage.x;
                    int dy = faceMessage.y - showMessage.y;
                    showMessage.x = faceMessage.x;
                    showMessage.y = faceMessage.y;
                    showMessage.w = faceMessage.w;
                    showMessage.h = faceMessage.h;
                    if (showMessage.ldmark_pts != null){
                        for (int k = 0; k <showMessage.ldmark_pts.size() ; k++) {
                            showMessage.ldmark_pts.get(k).x += dx;
                            showMessage.ldmark_pts.get(k).y += dy;
                        }
                    }
                    exist = true;
                    res.faceList.set(i,showMessage);
                    break;
                }
            }
            if (!exist){   //new face
                res.faceList.set(i,faceMessage);
            }
        }
        return res;
    }
}
