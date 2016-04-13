package edu.nuist.dateout.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.UserLoc;
import edu.nuist.dateout.model.UserNear;
import edu.nuist.dateout.net.HttpUtil;
import edu.nuist.dateout.value.AppConfig;

/**
 * 用于处理摇一摇之后地理位置的上传,并下载附近的人列表
 * 
 * @author Veayo
 *
 */
public class UploadLocAndGetUsersNearTask extends AsyncTask<String, String, List<UserNear>>
{
    private Handler handler;
    
    private UserLoc userLoc;
    
    private int errorFlag = 0;
    
    private List<String> friendIdList;
    
    DateoutApp app = DateoutApp.getInstance();
    
    public UploadLocAndGetUsersNearTask(UserLoc userLoc, Handler handler)
    {
        super();
        this.handler = handler;
        this.userLoc = userLoc;
    }
    
    @Override
    protected List<UserNear> doInBackground(String... params)
    {
        List<UserNear> usersNearList = new ArrayList<UserNear>();
        // 计算代码段执行开始时间
        long t1 = System.currentTimeMillis();
        // 请求获取附近的人列表
        try
        {
            usersNearList = new HttpUtil().fetchUsersNear(userLoc);
            
            List<String> friendIdList = new ArrayList<String>();
            for (int i = 0; i < app.getFriendList().size(); i++)
            {
                friendIdList.add(app.getFriendList().get(i).getUserId());
            }
            
            // 对附近的人列表进行过滤,只保留陌生人,即不是自己好友
            for (int i = 0; i < usersNearList.size(); i++)
            {
                UserNear userNear = usersNearList.get(i);
                if (friendIdList.contains(userNear.getUserId()))
                {
                    // 移除这个人
                    usersNearList.remove(i);
                }
            }
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
            errorFlag = 2;// 客户端协议异常
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            errorFlag = 3;// 参数异常
        }
        catch (IOException e)
        {
            e.printStackTrace();
            errorFlag = 4;// IO异常
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
            errorFlag = 5;// 超时
        }
        
        long t2 = System.currentTimeMillis(); // 获取结束时间
        long timeGap = (t2 - t1) / 1000;// 代码用时,单位为秒
        if (timeGap < 3)
        {
            sleepSomeSecond(2);
        }
        return usersNearList;
    }
    
    @Override
    protected void onPostExecute(List<UserNear> result)
    {
        super.onPostExecute(result);
        
        if (result == null)
        {
            // 出错
            Message osMsg = handler.obtainMessage();
            osMsg.what = errorFlag;
            osMsg.sendToTarget();
        }
        else
        {
            // 输出附近的人的列表
            Log.v("Dateout", "找到" + result.size() + "位和" + DateoutApp.getInstance().getLoginUser().getUserId() + "距离小于"
                + (int)AppConfig.DISTANCE_NEAR + "米的陌生用户");
            for (int i = 0; i < result.size(); i++)
            {
                Log.v("Dateout", i + 1 + ".用户" + result.get(i).getUserId() + "的距离为:" + result.get(i).getDistance()
                    + "米\t位置最近更新时间:" + result.get(i).getTime().toString());
            }
            
            // 对获取的附近的人列表进行处理
            Message osMsg = handler.obtainMessage();
            osMsg.what = 1;
            osMsg.obj = result;
            osMsg.sendToTarget();
        }
    }
    
    private void sleepSomeSecond(int second)
    {
        // 小于3秒,则延时2秒
        try
        {
            Thread.sleep(second * 1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 用于判断某用户是否已经是自己好友
     * 
     * @param userId 用户名
     * @return true表示userId已经是自己的好友
     */
    boolean isAlreadyMyFriend(String userId)
    {
        // 从静态类中获取好友列表,然后检测用户Id是否在里面
        if (friendIdList.contains(userId))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
