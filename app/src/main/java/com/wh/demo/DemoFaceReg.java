package com.wh.demo;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.wh.camapp.App;
import com.wh.camapp.CanvasView;
import com.wh.camapp.Config;
import com.ysten.ai.bean.NuiMessage;
import com.ysten.ai.bean.RegMessage;
import com.ysten.ai.cloud.FaceRegHelper;
import com.wh.camapp.MapperHelper;
import com.ysten.ai.cloud.YstenAICloudCallBack;
import com.ysten.ai.demo.R;

public class DemoFaceReg extends WorkerBase
{
    public static final String TAG = "FaceReg";
    public static final String name = "人脸注册";
    private int mState = 0; // 0 none 1 add 2 train

    MapperHelper mMapper = new MapperHelper();
    FaceRegHelper mHelper = new FaceRegHelper();

    int mUserId = -1;
    int mCount = 0;
    Rect mROI = null;
    @Override
    public void setup(String dir,int screenW,int screenH)
    {
        mAvgTime = 0;
        mHelper.init();
        mHelper.setRequestParam(SERVER_URL,APPID);
        super.setup(dir,screenW,screenH);
        mState = 0;
        mCount = 0;
        mHelper.setCloudCallBack(new YstenAICloudCallBack() {
            @Override
            public void onSuccess(String s,NuiMessage nuiMessage) {
                Log.i(TAG,"getValidUserID: " +s);
                mUserId = mHelper.parseUserID(s);
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG,"error:"+ s);
            }
        });
        mHelper.getValidUserID();
    }

    @Override
    public void destroy()
    {
        Log.e("Reg", "destroy");
        mHelper.destroy();
        super.destroy();
    }

    public View getContentView()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(App.getInstance());
        View view = layoutInflater.inflate(R.layout.demo_reg,null);
        // NONE -> SNAP
        Button btnSnap = (Button) view.findViewById(R.id.btnSnap);
        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == 0)
                {
                    mState = 1;
                }
            }
        });
        return view;
    }

    public void mrotate(Point pCenter, float angle, float r, Point p2)
    {
        double tmp = Math.toRadians(angle);
        p2.x = (int)(pCenter.x + r * Math.sin(tmp)) ;
        p2.y = (int)(pCenter.y - r * Math.cos(tmp));
        return;
    }

    public void drawFaceRotation(RegMessage message, CanvasView canvasView)
    {
        float angle = message.rotate_angle;
        float r_ratio = message.rotate_length;
        int center_x = mROI.centerX();//x + w / 2;
        int center_y = mROI.centerY();//y + h / 2;
        int r = (int)(mROI.width()/2 * 0.8);

        Point pcenter = new Point(center_x,center_y);
        Point p2 = new Point(0,0);
        Point p3 = new Point(0,0);
        mrotate(pcenter, angle, r*r_ratio, p3);
        mrotate(pcenter, angle, r, p2);
        canvasView.drawCircle(mapper.mapX(pcenter.x),mapper.mapY(pcenter.y),r,Color.LTGRAY, 5);
        canvasView.drawLine(mapper.mapX(pcenter.x),mapper.mapY(pcenter.y),mapper.mapX(p3.x),mapper.mapY(p3.y),Color.LTGRAY, 5);
    }


    public void drawCanvas(RegMessage message, CanvasView canvasView)
    {
        // draw
        canvasView.paintBegin();
        // draw faces
        if (message != null && message.isvalid > 0)
        {
            //drawFaceInfo(message,canvasView);
            if (message.ldmark_pts != null)
            {
                for(int j=0; j<message.ldmark_pts.size(); j++)
                {
                    int px = mapper.mapX(message.ldmark_pts.get(j).x);
                    int py = mapper.mapY(message.ldmark_pts.get(j).y);
                    canvasView.drawCircle(px,py,1,Color.GREEN,3);
                }
                drawFaceRotation(message,canvasView);
            }
        }
        // draw ROI
        canvasView.drawRect(mROI.left,mROI.top,mROI.right,mROI.bottom, Color.GREEN, 5);
        // draw count
        String tt = String.format("ID:%d #%d", mUserId, mCount);
        canvasView.drawText(mROI.left, mROI.top-mROI.width()/10,tt, Color.RED, mROI.width()/6);
        canvasView.paintEnd();
    }

    @Override
    public String processFrame(byte[] data, int previewWidth, int previewHeight, final CanvasView canvasView)
    {
        // 设置人脸检测位置
        if(mMapper.getMapper() == null)
        {
            mMapper.setMapper(mScreenW, mScreenH, previewWidth, previewHeight, true);
            int w = mScreenH/2;
            int cx = mScreenW/2;
            int cy = mScreenH/2;
            mROI = new Rect(cx - w/2,cy - w/2,cx + w/2,cy + w/2);
            int[] res = mMapper.mapScreen2Image(cx - w/2,cy - w/2, w, w);
            mHelper.setROI(res[0],res[1],res[2], res[3]);
            // 同步映射关系
            mapper = mMapper.getMapper();
        }

        // 得到人脸姿态
        long startTime = System.currentTimeMillis();
        RegMessage message = mHelper.poseEstimate(data, previewWidth, previewHeight);
        long dt = System.currentTimeMillis() - startTime; // 消耗時間
        Config.updateFps(dt);
        drawCanvas(message, canvasView);
        if (1 == mState && message != null)
        {
            mHelper.setCloudCallBack(new YstenAICloudCallBack() {
                @Override
                public void onSuccess(String s,NuiMessage nuiMessage) {
                    Log.i(TAG,"register sucess:" + s);
                    mCount++;
                    mState = 0;
                }

                @Override
                public void onFailure(String s) {
                    Log.e(TAG,"register failed :"+ s);
                }
            });

            mHelper.faceRegister(mUserId, mCount, data, previewWidth, previewHeight, message);
        }

        return "{}";
    }
}
