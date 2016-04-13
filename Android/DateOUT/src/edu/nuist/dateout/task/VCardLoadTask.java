package edu.nuist.dateout.task;

import java.util.HashMap;

import org.jivesoftware.smack.XMPPConnection;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.util.VCardAssit;

public class VCardLoadTask extends AsyncTask<String, String, CustomVcard>
{
    private String userId;
    
    private XMPPConnection con;
    
    private Handler handler;
    
    public VCardLoadTask(String userId, XMPPConnection con, Handler handler)
    {
        super();
        this.userId = userId;
        this.con = con;
        this.handler = handler;
    }
    
    @Override
    protected CustomVcard doInBackground(String... params)
    {
        return new VCardAssit(con).loadMyVCard(userId);// 获取好友VCard
    }
    
    @Override
    protected void onPostExecute(CustomVcard result)
    {
        super.onPostExecute(result);
        
        HashMap<String, CustomVcard> map=new HashMap<String, CustomVcard>();
        map.put(userId, result);
        
        Message osMsg=handler.obtainMessage();
        osMsg.what=1;
        osMsg.obj=map;
        osMsg.sendToTarget();
    }
}
