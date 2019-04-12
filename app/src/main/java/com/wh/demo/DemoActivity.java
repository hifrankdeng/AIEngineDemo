package com.wh.demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wh.camapp.CanvasView;
import com.wh.camapp.Config;
import com.ysten.ai.demo.R;
import java.util.List;


public class DemoActivity extends Activity implements SurfaceHolder.Callback
{
	// 720P
	final int P720[] = {1280, 720};
	final int P1080[] = {1920, 1080};
	final int PreviewSize[] = P1080;
	private final int OptFrameWidth = PreviewSize[0];
	private final int OptFrameHeight = PreviewSize[1];
	private static final String TAG = "CamApp";
	private static Context context = null;
	private CanvasView mRectView = null;
	private SurfaceView mSurfaceview = null;
	private SurfaceHolder mSurfaceHolder = null;
	private Camera mCamera = null;
	private boolean bIfPreview = false;
	private int mPreviewHeight = 0;
	private int mPreviewWidth = 0;
	WorkerBase mCurDemo = null;
	String mCurDemoName = "";
	void switchToDemo(WorkerBase demo)
	{
		if (mCurDemo != null)
		{
			mCurDemo.destroy();
			mCurDemo = null;
		}
		mCurDemo = demo;
		// setup
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mCurDemo.setup("",metric.widthPixels, metric.heightPixels);
		// setView
		ViewGroup container = (ViewGroup)findViewById(R.id.div_container);
		container.removeAllViews();
		View demoView = mCurDemo.getContentView();
		if (demoView != null)
		{
			container.addView(demoView);
		}
	}

	void switchDemo(String demoName)
	{
		WorkerBase demo = DemoFactory.createDemo(demoName);
		if (demo != null)
		{
			switchToDemo(demo);
			mCurDemoName = demoName;
            RadioGroup layout = (RadioGroup)findViewById(R.id.demoList);
			Log.i(TAG,"" + DemoFactory.getSubIndex(demoName));
            RadioButton rd = (RadioButton) layout.getChildAt(DemoFactory.getSubIndex(demoName));
            if (!rd.isChecked()) {
                rd.setChecked(true);
            }
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/* 隐藏状态栏 */
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_demo);
		context = this;

		String[] tags = DemoFactory.getList();
		String defaultDemo = DemoFactory.getDefaultDemo();
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null)  //this line is necessary for getting any value
		{
			tags = getIntent().getStringArrayExtra("list");
			defaultDemo = tags[0];
		}
		initView(tags);
		switchDemo(defaultDemo);
	}

	protected void onDestroy()
	{
		super.onDestroy();
		if (mCurDemo != null)
		{
			mCurDemo.destroy();
		}
	}

	// InitSurfaceView
	private void initView(String[] tags)
	{
		mSurfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
		mSurfaceHolder = mSurfaceview.getHolder();
		mSurfaceHolder.addCallback(DemoActivity.this);
		//mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mRectView = (CanvasView) this.findViewById(R.id.rectview);
		// Add demo buttons
		RadioGroup layout = (RadioGroup)findViewById(R.id.demoList);
		//String[] tags = DemoFactory.getList();
		for (int i = 0;i < tags.length;i++)
		{
			RadioButton button = (RadioButton) getLayoutInflater().inflate(R.layout.demo_title, null);
			button.setText(tags[i]);
			layout.addView(button);
			button.setTag(tags[i]);
		}
		layout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				for (int j = 0; j < radioGroup.getChildCount(); j++) {
					RadioButton rd = (RadioButton) radioGroup.getChildAt(j);
					if (rd.isChecked()) {
						String tag = (String)rd.getTag();
						if (tag != null && mCurDemoName != tag)
						{
							switchDemo(tag);
						}break;
					}
				}
			}
		});
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		mCamera = Camera.open(0);
		try
		{
			Log.i(TAG, "SurfaceHolder.Callback：surface Created");
			mCamera.setPreviewDisplay(mSurfaceHolder);
		}
		catch (Exception ex)
		{
			if (null != mCamera)
			{
				mCamera.release();
				mCamera = null;
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Log.i(TAG, "SurfaceHolder.Callback：Surface Changed");
		initCamera();
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.i(TAG, "SurfaceHolder.Callback：Surface Destroyed");
		if (null != mCamera)
		{
			mCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
			mCamera.stopPreview();
			bIfPreview = false;
			mCamera.release();
			mCamera = null;
		}
	}

	private void initCamera()
	{
		Log.i(TAG, "going into initCamera");
		if (bIfPreview)
		{
			mCamera.stopPreview();// stopCamera();
		}
		if (null != mCamera)
		{
			try
			{
				/* Camera Service settings */
				Camera.Parameters parameters = mCamera.getParameters();
				List<Camera.Size> mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
				Camera.Size optsize = getOptimalPreviewSize(mSupportedPreviewSizes, OptFrameWidth, OptFrameHeight);
				parameters.setPreviewSize(optsize.width, optsize.height);
				//parameters.setPreviewFormat(ImageFormat.YV12);
				// parameters.setPictureFormat(ImageFormat.YV12);
				mPreviewWidth = parameters.getPreviewSize().width;
				mPreviewHeight = parameters.getPreviewSize().height;
				// 横竖屏镜头自动调整
				if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
				{
					parameters.set("orientation", "portrait"); //
					parameters.set("rotation", 90); // 镜头角度转90度（默认摄像头是横拍）
					mCamera.setDisplayOrientation(90); // 在2.2以上可以使用
				}
				else
				// 如果是横屏
				{
					parameters.set("orientation", "landscape"); //
					mCamera.setDisplayOrientation(0); // 在2.2以上可以使用
				}
				mCamera.setPreviewCallback(mYUVPreviewCallback);
				mCamera.setParameters(parameters); // 将Camera.Parameters设定予Camera
				mCamera.startPreview(); // 打开预览画面
				//mRectView.setPreviewSize(mPreviewWidth, mPreviewHeight);
				bIfPreview = true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h)
	{
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Camera.Size size : sizes)
		{
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff)
			{
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null)
		{
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes)
			{
				if (Math.abs(size.height - targetHeight) < minDiff)
				{
					if ((size.height - targetHeight) < 0)
					{
						optimalSize = size;
						minDiff = Math.abs(size.height - targetHeight);
					}
				}
			}
		}
		return optimalSize;
	}

	private void updateFps()
	{
		TextView tv = (TextView)findViewById(R.id.fps);
		String text = String.format("FPS:%.1f  %.1fms", Config.FPS, 1000/Config.FPS);//1000/(Config.FPS+1));
		tv.setText(text);
	}

	private Camera.PreviewCallback mYUVPreviewCallback = new Camera.PreviewCallback()
	{
		public void onPreviewFrame(byte[] data, Camera arg1)
		{
			if (mCurDemo != null)
			{
				mCurDemo.processFrame(data, mPreviewWidth, mPreviewHeight, mRectView);
				updateFps();
			}
		}
	};

}
