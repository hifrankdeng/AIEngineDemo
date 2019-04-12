package com.wh.demo;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.Log;
import android.view.View;

import com.wh.camapp.CamWorker;
import com.wh.camapp.CanvasView;
import com.wh.camapp.CoordMapper;
import com.ysten.ai.bean.FaceMessage;
import com.ysten.ai.bean.NuiMessage;

public class WorkerBase extends CamWorker
{
	public static String SERVER_URL ="http://api.ai.ysten.com:8099";
	public static String APPID ="4b20c38d-5413-3d27-9c41-3c5c980eef8f";

	public View getContentView()
	{
		return null;
	}

	@Override
	public void destroy()
	{
		super.destroy();
	}

	private static final String TAG = "WorkerBase";
	public CoordMapper mapper = new CoordMapper();
	// 保持屏幕分辨率
    public int mScreenW,mScreenH;
    public boolean mFlip = true;
	public float mAvgTime = 0;
	@Override
	public void setup(String dir,int screenW,int screenH)
	{
		mAvgTime = 0;
		mScreenH = screenH;
		mScreenW = screenW;
	}

	public void drawFaceInfo(FaceMessage face,CanvasView canvasView,boolean isShowPts)
	{
		int lightBlueColor = Color.argb(200, 51, 181, 255);
		int yellowColor = Color.argb(255, 233, 183, 0);
		Rect rect = canvasView.drawRect(mapper.mapX(face.x),
				mapper.mapY(face.y),
				mapper.mapX(face.x + face.w),
				mapper.mapY(face.y + face.h),
				lightBlueColor, 4);
		float conf = (1-2*face.dist);
		conf = Math.max(0,conf);
		// head ID-P-Gender-Age
		String text = face.desc();
		canvasView.drawText(rect.left,rect.top - 10 ,text ,yellowColor, 30);
		//landmark
		//写入关键点
		if (isShowPts && face.ldmark_pts != null)
		{
			for(int j=0; j<face.ldmark_pts.size(); j++) {
				int px = mapper.mapX(face.ldmark_pts.get(j).x);
				int py = mapper.mapY(face.ldmark_pts.get(j).y);
				canvasView.drawCircle(px,py,1,Color.GREEN,6);
			}
		}

		// blood bar
		int barHeight = (int) (rect.width() * 0.05);
		barHeight = Math.max(barHeight, 5);
		int blue = Color.argb(255, 23, 150, 255);
		canvasView.drawRect(rect.left, (int)(rect.top),(int)(rect.left+rect.width()*conf), rect.top+barHeight,blue, 0);
		// emotion
		text = face.strEmotion();
		canvasView.drawText(rect.left,rect.bottom + 30 ,text ,Color.argb(255, 233, 183, 0), 30);
	}

	public void updateResultView(NuiMessage message, CanvasView canvasView,boolean isShowPts)
	{
		// draw
		canvasView.paintBegin();
		if (message != null && message.faceList != null && message.faceList.size() > 0)
		{
			for (int i = 0; i < message.faceList.size(); i++)
			{
				FaceMessage face = message.faceList.get(i);
				drawFaceInfo(face,canvasView,isShowPts);
			}
		}
		canvasView.paintEnd();
	}

}
