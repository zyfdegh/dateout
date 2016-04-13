package edu.nuist.dateout.task;

import java.io.File;
import java.io.IOException;

import android.os.AsyncTask;
import android.os.Handler;
import de.greenrobot.event.EventBus;
import edu.nuist.dateout.model.DownloadResult;
import edu.nuist.dateout.net.DownloadUtil;

/**
 * 用于下载指定文件名的文件并缓存到本地目录 下载服务器端名为fileName的文件,并写入到本地文件filePath 结果Handler 中的osMsg为DownloadResult类型
 * 
 * @author Veayo
 *
 */
public class DownloadAndCacheTask extends AsyncTask<String, Integer, File>
{
    private String fileName;
    
    private String filePath;
    
    private Handler handler;
    
    public DownloadAndCacheTask(String fileName, String filePath, Handler handler)
    {
        this.fileName = fileName;
        this.filePath = filePath;
        this.handler = handler;
    }
    
    @Override
    protected File doInBackground(String... params)
    {
        try
        {
            return new DownloadUtil().downloadFileByName(fileName, filePath);
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
        DownloadResult downloadResult = new DownloadResult();
        
        if (result != null && result.exists() && result.length() > 0)
        {
            // 下载成功
            downloadResult.setDownloadSuccess(true);
            downloadResult.setFilePath(filePath);
        }
        else
        {
            downloadResult.setDownloadSuccess(false);
            downloadResult.setFilePath(filePath);
        }
        
        if (handler == null)
        {
            // 不通知结果
        }
        else
        {
            // 通知UI线程结果
            android.os.Message osMsg = handler.obtainMessage();
            osMsg.what = 1;
            osMsg.obj = downloadResult;
            osMsg.sendToTarget();
        }
        
        // 发送通知到ChatActivity
        EventBus.getDefault().post(downloadResult);
    }
}