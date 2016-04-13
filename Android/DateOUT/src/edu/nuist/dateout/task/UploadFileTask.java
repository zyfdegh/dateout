package edu.nuist.dateout.task;

import java.io.File;
import java.io.IOException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.net.UploadUtil;
import edu.nuist.dateout.util.NetworkAssit;
import edu.nuist.dateout.value.AppConfig;
import edu.nuist.dateout.value.VariableHolder;

/** 异步任务-文件上传 */
/**
 * 给定文件,新的文件名,实现上传到服务端指定链接
 * 
 * @author Veayo
 */
public class UploadFileTask extends AsyncTask<String, Integer, Integer>
{
    private File fileToUpload;// 本地文件路径
    
    private Handler handler;
    
    public UploadFileTask(File file, Handler handler)
    {
        this.fileToUpload = file;
        this.handler = handler;
    }
    
    @Override
    protected Integer doInBackground(String... parameters)
    {
        if (new NetworkAssit(DateoutApp.getInstance().getApplicationContext()).isNetworkConnected())
        {
            try
            {
                // 返回0表示成功
                return new UploadUtil().uploadFile(fileToUpload, AppConfig.URL_UPLOAD_SERVLET);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }else {
            //网络未连接
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(Integer result)
    {
        // 使用Handler通知主线程文件传输结果
        Object obj = new String();
        
        if (result == 0)
        {
            Log.v("Dateout", "文件上传成功" + Uri.fromFile(fileToUpload).toString());
            // 上传成功,返回一个String,内容是文件的路径,返回null表示上传失败
            obj = fileToUpload.getName();
            
            // 检测文件是否为聊天图片,如果是,返回图片下载链接
            String fileUploadedName = fileToUpload.getName();
            if (fileUploadedName.startsWith(VariableHolder.FILE_PREFIX_IMAGE_SENT))
            {
                // 返回文件下载链接
                obj = AppConfig.URL_DOWNLOAD_SERVLET + "filename=" + fileUploadedName;
            }
        }
        else
        {
            Log.v("Dateout", "文件上传失败" + Uri.fromFile(fileToUpload).toString());
            obj = null;
        }
        
        if (handler == null)
        {
            // 不处理上传结果
            // handler.sendEmptyMessage(1);
        }
        else
        {
            // 上传成功,就返回文件名
            android.os.Message osMsg = handler.obtainMessage();
            osMsg.what = 1;
            osMsg.obj = obj;
            osMsg.sendToTarget();
        }
    }
}