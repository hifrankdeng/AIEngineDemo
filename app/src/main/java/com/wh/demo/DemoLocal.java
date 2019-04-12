package com.wh.demo;

import android.util.Log;

import com.wh.camapp.CanvasView;
import com.wh.camapp.Config;
import com.ysten.ai.bean.NuiMessage;
import com.ysten.ai.local.YstenAIEngine;


public class DemoLocal extends WorkerBase
{
	public static final String name = "本地快速模式";
	private static final String TAG = "DemoLocal";
	public static String getName() {
		return "DemoLocal";
	}

	@Override
	public void setup(String dir,int screenW,int screenH)
	{
		super.setup(dir,screenW,screenH);
		String[] nodelist = new String[]{YstenAIEngine.NODE_FACE_DETECT,
				YstenAIEngine.NODE_FACE_LDMARK,
				YstenAIEngine.NODE_FACE_POSE,
				YstenAIEngine.NODE_FACE_ATT};
		YstenAIEngine.get().open(nodelist);
		YstenAIEngine.get().setTrackEnable(true);
		mAvgTime = 0;
	}

	@Override
	public void destroy()
	{
		YstenAIEngine.get().close();
		super.destroy();
	}


	@Override
	public String processFrame(byte[] data, int previewWidth, int previewHeight, CanvasView canvasView)
	{
		// correct mapper
		if(mFlip) {
			mapper.set(0, 0, mScreenW, 0, previewWidth, previewHeight, 0, mScreenH);
		}
		else
		{
			mapper.set(0, 0, 0, 0, previewWidth, previewHeight, mScreenW, mScreenH);
		}

		long startTime = System.currentTimeMillis();  // 開始時間
		String[] nodelist = new String[]{YstenAIEngine.NODE_FACE_DETECT,
				YstenAIEngine.NODE_FACE_LDMARK,
				YstenAIEngine.NODE_FACE_POSE,
				YstenAIEngine.NODE_FACE_ATT};
		String json = YstenAIEngine.get().runFast(nodelist, previewWidth, previewHeight,data);
		long dt = System.currentTimeMillis() - startTime; // 消耗時間
		Config.updateFps(dt);

		// parse
		NuiMessage message = YstenAIEngine.format(json);
		updateResultView(message, canvasView,true);
		Log.i("NUIJSON", String.format("%s",json) );
		return json;
	}
}
