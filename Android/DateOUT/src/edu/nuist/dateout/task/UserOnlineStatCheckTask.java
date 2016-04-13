package edu.nuist.dateout.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.util.OnlineStatusAssit;

public class UserOnlineStatCheckTask extends AsyncTask<String, Integer, Integer>
{
    private String userId;
    
    private Handler handler;
    
    public UserOnlineStatCheckTask(String userId, Handler handler)
    {
        super();
        this.userId = userId;
        this.handler = handler;
    }
    
    @Override
    protected Integer doInBackground(String... params)
    {
        String userJid = userId + "@" + DateoutApp.getInstance().getServiceName();
        return Integer.valueOf(new OnlineStatusAssit().getUserOnlineStatus(userJid));
    }
    
    @Override
    protected void onPostExecute(Integer result)
    {
        super.onPostExecute(result);
        if (result != null)
        {
            // 传递消息给Handler处理
            Message osMsg = handler.obtainMessage();
            osMsg.what = 1;
            osMsg.obj = result;
            osMsg.sendToTarget();
        }
    }
}
