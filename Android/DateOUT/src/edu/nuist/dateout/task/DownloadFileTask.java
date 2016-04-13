package edu.nuist.dateout.task;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import edu.nuist.dateout.net.DownloadUtil;

public class DownloadFileTask extends AsyncTask<String, String, File>
{
    
    private String urlStr;
    
    private Handler handler;
    
    private Context context;
    
    private String fileSavePath;
    
    public DownloadFileTask(String urlStr, Handler handler, String fileSavePath)
    {
        super();
        this.urlStr = urlStr;
        this.handler = handler;
        this.fileSavePath = fileSavePath;
    }
    
    public DownloadFileTask(String urlStr, Handler handler, Context context, String fileSavePath)
    {
        super();
        this.urlStr = urlStr;
        this.handler = handler;
        this.context = context;
        this.fileSavePath = fileSavePath;
    }
    
    @Override
    protected File doInBackground(String... params)
    {
        URL url;
        try
        {
            // string-->url
            url = new URL(urlStr);
            return new DownloadUtil().downloadFileByUrl(url, fileSavePath);
        }
        catch (MalformedURLException e)
        {
            // 链接无效
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(File result)
    {
        super.onPostExecute(result);
        // 传送文件到主线程
        Message osMsg = handler.obtainMessage();
        osMsg.what = 1;
        osMsg.obj = result;
        osMsg.sendToTarget();
    }
}
