package edu.nuist.dateout.task;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.net.HttpUtil;
import edu.nuist.dateout.value.AppConfig;

public class UploadGameConfigTask extends AsyncTask<String, String, String>
{
    
    private GameConfig config;
    
    private Handler handler;
    
    public UploadGameConfigTask(GameConfig config, Handler handler)
    {
        super();
        this.config = config;
        this.handler = handler;
    }
    
    @Override
    protected String doInBackground(String... params)
    {
        String requestParam =
            "userId=" + config.getUserId() + "&picName=" + config.getPicUrl() + "&time=" + config.getTimeOut()
                + "&difficulty=" + config.getDifficulty();
        String url = AppConfig.URL_GAMECONFIG_SAVE_SERVLET + requestParam;
        try
        {
            return new HttpUtil().getHttpPostResult(url);
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
        
        if (result.equals("Done!"))
        {
            // 保存成功
            // handler.sendEmptyMessage(0);
            Message osMsg = handler.obtainMessage();
            osMsg.what = 1;
            osMsg.sendToTarget();
        }
        else
        {
            // 保存失败
            // handler.sendEmptyMessage(1);
            Message osMsg = handler.obtainMessage();
            osMsg.what = 2;
            osMsg.sendToTarget();
        }
    }
}
