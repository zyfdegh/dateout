package edu.nuist.dateout.task;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import edu.nuist.dateout.net.HttpUtil;

public class FetchFileNameTask extends AsyncTask<String, Integer, String>
{
    
    private String filePrefix;// 文件前缀,如IMAGEHEAD_user2
    
    private Handler handler;
    
    public FetchFileNameTask(String filePrefix, Handler handler)
    {
        this.filePrefix = filePrefix;
        this.handler = handler;
    }
    
    @Override
    protected String doInBackground(String... params)
    {
        // 返回获取到的文件名,null表示不存在文件
        try
        {
            return new HttpUtil().fetchImageHeadFullName(filePrefix);
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        if (result == null || result.equals(""))
        {
            Log.v("Dateout", "FileNameDownTask==>" + "服务端找不到文件" + filePrefix + "...");
        }
        else if (result.startsWith(filePrefix))
        {
            Log.v("Dateout", "FileNameDownTask==>" + "服务端存在头像缓存:" + result);
        }
        else
        {
            Log.v("Dateout", "FileNameDownTask==>" + "出错");
        }
        
        if (handler != null)
        {
            android.os.Message osMsg = handler.obtainMessage();
            osMsg.obj = result;
            osMsg.what = 1;
            osMsg.sendToTarget();
        }
    }
}
