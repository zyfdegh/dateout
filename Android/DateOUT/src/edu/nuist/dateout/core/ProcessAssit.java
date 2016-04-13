package edu.nuist.dateout.core;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;

/**
 * 涉及进程管理的工具类
 * 
 * @author Veayo
 *
 */
public class ProcessAssit
{
    /**
     * 用于判断某个Activity是否在后台
     * 
     * @param context Activity的Content
     * @return true表示在后台运行
     */
    public static boolean isBackground(Context context)
    {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses)
        {
            if (appProcess.processName.equals(context.getPackageName()))
            {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND)
                {
                    Log.i("程序在后台", appProcess.processName);
                    return true;
                }
                else
                {
                    Log.i("程序在前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
    
}
