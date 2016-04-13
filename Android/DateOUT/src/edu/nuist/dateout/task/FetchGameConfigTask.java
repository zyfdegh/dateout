package edu.nuist.dateout.task;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.net.HttpUtil;

/**
 * 用于从服务端获取游戏配置信息,如时间、格数、图片下载链接
 * 
 * @author Veayo
 *
 */
public class FetchGameConfigTask extends AsyncTask<String, String, GameConfig>
{
    
    private String userId;
    
    private Handler handler;
    
    private Context context;
    
    private ProgressDialog pd;
    
    public FetchGameConfigTask(String userId, Handler handler, Context context)
    {
        super();
        this.userId = userId;
        this.handler = handler;
        this.context = context;
    }
    
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        // 显示圈圈
        pd = new ProgressDialog(context);
        pd.setTitle("正在加载游戏配置");
        pd.setMessage("请稍候");
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.show();
    }
    
    @Override
    protected GameConfig doInBackground(String... params)
    {
        // 联网获取游戏配置
        try
        {
            return new HttpUtil().fetchGameConfig(userId);
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
    protected void onPostExecute(GameConfig result)
    {
        super.onPostExecute(result);
        pd.dismiss();
        
        // 传送到前端线程
        Message osMsg = handler.obtainMessage();
        osMsg.what = 1;
        osMsg.obj = result;
        osMsg.sendToTarget();
    }
}
