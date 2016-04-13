package edu.nuist.dateout.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import edu.nuist.dateout.net.HttpUtil;
import edu.nuist.dateout.value.VariableHolder;

/**
 * 用于获取特定文件前缀开头的文件下载链接 如filePrefix=="IMAGEVCARD_user2"时,能得到一组IMAGEVCARD_user2开头的所有文件下载链接,并存放到List中,发送到Handler,进行后续处理
 * Handler收到的obj类型 List<String>
 * 
 * @author Veayo
 *
 */
public class FetchDownLinkTask extends AsyncTask<String, Integer, String>
{
    private String filePrefix;// 文件前缀,如IMAGEHEAD_user2
    
    private Handler handler;
    
    public FetchDownLinkTask(String filePrefix, Handler handler)
    {
        super();
        this.filePrefix = filePrefix;
        this.handler = handler;
    }
    
    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return new HttpUtil().fetchDownoadLink(filePrefix);
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
        List<String> downloadLinksList = new ArrayList<String>();
        
        if (result == null || result.equals(""))
        {
            if (filePrefix.startsWith(VariableHolder.CMD_DELETE_ALL_MYVCARD_IMAGES))
            {
                // 是个特殊命令
            }
            else
            {
                Log.v("Dateout", "FetchDownLinkTask==>" + "服务端找不到文件下载链接" + filePrefix + "...");
            }
        }
        else if (result.startsWith("http://"))
        {
            Log.v("Dateout", "FetchDownLinkTask==>" + "找到以" + filePrefix + "开头的文件下载链接:" + result);
            // 对获取到的下载链接进行处理
            String downloadLinks[] = result.split(",");
            for (int i = 0; i < downloadLinks.length; i++)
            {
                downloadLinksList.add(downloadLinks[i]);
            }
        }
        else
        {
            // 出错
        }
        
        // 返回下载链接List类型
        if (handler != null)
        {
            android.os.Message osMsg = handler.obtainMessage();
            osMsg.obj = downloadLinksList;
            osMsg.what = 1;
            osMsg.sendToTarget();
        }
    }
}
