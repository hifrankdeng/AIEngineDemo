package com.wh.demo;

import android.util.Log;

public class DemoFactory {
    private static String[] mList = {
            DemoLocal.name,
            DemoDetect.name,
            DemoCloudAndLocal.name,
            //DemoCloud.name,
            DemoFaceReg.name
    };

    private static  String[] mLocallist = {
            DemoLocal.name,
            DemoDetect.name,
    };

    private static String[] mCloudlist = {
            DemoCloudAndLocal.name,
            DemoFaceReg.name
    };

    public static WorkerBase createDemo(String demoName)
    {
        switch (demoName)
        {
            case DemoLocal.name: return new DemoLocal();
            case DemoDetect.name: return new DemoDetect();
            case DemoCloud.name: return new DemoCloud();
            case DemoCloudAndLocal.name: return new DemoCloudAndLocal();
            case DemoFaceReg.name: return new DemoFaceReg();
        }
        return null;
    }

    public static String getDefaultDemo()
    {
        return DemoFaceReg.name;
    }

    public static String[] getList()
    {
        return mList;
    }
    public static String[] getLocalList()
    {
        return mLocallist;
    }

    public static String[] getCloudList()
    {
        return mCloudlist;
    }

    public static int getIndex(String demoName)
    {
        for (int i = 0;i < mList.length;i++)
        {
            if (demoName.equals(mList[i]))
            {
                return i;
            }
        }
        return -1;
    }

    public static int getSubIndex(String demoName)
    {
        for (int i = 0;i < mLocallist.length;i++)
        {
            if (demoName.equals(mLocallist[i]))
            {
                return i;
            }
        }
        for (int i = 0;i < mCloudlist.length;i++)
        {
            if (demoName.equals(mCloudlist[i]))
            {
                return i;
            }
        }
        return -1;
    }

}
