package com.wh.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.wh.camapp.App;
import com.wh.camapp.CanvasView;
import com.wh.camapp.Config;
import com.wh.camapp.FileUtils;
import com.wh.camapp.UnzipAssets;
import com.ysten.ai.demo.R;

import java.io.File;
import java.io.IOException;

import static com.wh.camapp.FileUtils.readTxtFile;
import static com.wh.camapp.FileUtils.writeTxtFile;

/**
 * Created by denghui on 2019/4/4.
 */

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private Button btnLocal, btnCloud;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		/* 隐藏状态栏 */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        unzipData();
        initView();
    }

    void unzipData()
    {
        FileUtils fileUtils = new FileUtils();
        String rootDir = fileUtils.getSDPATH() + File.separator + Config.DATA_ROOT;
        // 检查是否一致
        String versionPath = fileUtils.getSDPATH() + File.separator + Config.DATA_VERSION_FILE;
        String version = readTxtFile(versionPath);
        if (version != null && version.trim().equals(Config.DATA_VERSION))
        {
            Log.v(TAG,"data version matches :" + Config.DATA_VERSION);
            return ;
        }
        Log.v(TAG,"current data version:" + Config.DATA_VERSION + ", device version:" + version);
        // 解压数据到该目录
        try
        {
            UnzipAssets.unZip(App.getInstance(), "data.zip", rootDir);
            writeTxtFile(versionPath,Config.DATA_VERSION);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initView(){
        btnLocal = (Button) this.findViewById(R.id.local_button);
        btnCloud = (Button) this.findViewById(R.id.cloud_button);
        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,DemoActivity.class);
                Bundle bundle=new Bundle();
                bundle.putStringArray("list",DemoFactory.getLocalList());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,DemoActivity.class);
                Bundle bundle=new Bundle();
                bundle.putStringArray("list",DemoFactory.getCloudList());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
