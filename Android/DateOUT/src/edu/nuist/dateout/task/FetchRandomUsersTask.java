package edu.nuist.dateout.task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import edu.nuist.dateout.model.UserLoc;
import edu.nuist.dateout.model.UserRandom;
import edu.nuist.dateout.net.HttpUtil;

public class FetchRandomUsersTask extends AsyncTask<String, String, List<UserRandom>>
{
    
    private UserLoc userLoc;// 包含位置信息的用户
    
    private Handler Handler;// 处理获取到列表之后的动作
    
    public FetchRandomUsersTask(UserLoc userLoc, android.os.Handler handler)
    {
        super();
        this.userLoc = userLoc;
        Handler = handler;
    }
    
    @Override
    protected List<UserRandom> doInBackground(String... params)
    {
        // TODO 设置超时
        try
        {
            return new HttpUtil().fetchUsersRandom(userLoc);
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
            // 连接超时
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(List<UserRandom> result)
    {
        super.onPostExecute(result);
        
        if (result == null)
        {
            // 连接故障
            Message osMsg = Handler.obtainMessage();
            osMsg.what = 3;
            osMsg.sendToTarget();
        }
        else
        {
            if (result.size() > 0)
            {
                // 让Handler对结果进行后续处理
                Message osMsg = Handler.obtainMessage();
                osMsg.what = 1;
                osMsg.obj = result;
                osMsg.sendToTarget();
            }
            else
            {
                // 加载的列表为空
                Message osMsg = Handler.obtainMessage();
                osMsg.what = 2;
                osMsg.sendToTarget();
            }
        }
    }
}
