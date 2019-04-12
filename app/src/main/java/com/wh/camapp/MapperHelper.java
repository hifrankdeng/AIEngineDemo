package com.wh.camapp;

import android.graphics.Rect;
import android.util.Log;
import com.wh.camapp.CoordMapper;

public class MapperHelper {
    private final String TAG = "MapperHelper";
    // 坐标映射关系: 从图像坐标系 -> 屏幕坐标系
    private CoordMapper mapper;
    private boolean isMirror;
    // 屏幕上人脸框位置，屏幕坐标系
    private Rect mROI;
    public CoordMapper setMapper(int screenW, int screenH,
                          int previewWidth, int previewHeight,
                          boolean isMirror)
    {
        mapper = new CoordMapper();
        this.isMirror = isMirror;
        // correct mapper
        if(isMirror) {
            mapper.set(0, 0, screenW, 0, previewWidth, previewHeight, 0, screenH);
        }
        else
        {
            mapper.set(0, 0, 0, 0, previewWidth, previewHeight, screenW, screenH);
        }
        Log.w(TAG, String.format("setMapper[%d, %d] [%d, %d] mirror:%s",
                screenW,  screenH, previewWidth, previewHeight,
                isMirror ? "true" : "false"));
        return mapper;
    }

    public CoordMapper setMapper(int screenW, int screenH,
                          int previewWidth, int previewHeight)
    {
        return setMapper(screenW,  screenH, previewWidth, previewHeight, true);
    }

    public CoordMapper getMapper()
    {
        return mapper;
    }

    public int[] mapScreen2Image(int x, int y, int width, int height)
    {
        // convert to image coordinate
        int x1 = mapper.invX(x);
        int x2 = mapper.invX(x+width);
        int y1 = mapper.invY(y);
        int y2 = mapper.invY(y+height);
        int maxx = Math.max(x1, x2);
        int minx = Math.min(x1, x2);
        int maxy = Math.max(y1, y2);
        int miny = Math.min(y1, y2);
        // 保存屏幕坐标系位置
        int[] rect = new int[]{minx, miny, maxx - minx, maxy-miny};
        return rect;
    }

    /*public void setROI(int x, int y, int width, int height)
    {
        if(mROI != null)
        {
            return;
        }
        // convert to image coordinate
        int x1 = mapper.invX(x);
        int x2 = mapper.invX(x+width);
        int y1 = mapper.invY(y);
        int y2 = mapper.invY(y+height);
        int maxx = Math.max(x1, x2);
        int minx = Math.min(x1, x2);
        int maxy = Math.max(y1, y2);
        int miny = Math.min(y1, y2);
        // 保存屏幕坐标系位置
        mROI = new Rect(x, y, x+width, y+height);
        Log.w(TAG, String.format("setROI[%d, %d, %d, %d]", minx, miny, maxx - minx, maxy-miny));
        Face.get().poseSetFaceROI(minx, miny, maxx - minx, maxy-miny);
    }

    public Rect getROI()
    {
        return mROI;
    }*/
}
