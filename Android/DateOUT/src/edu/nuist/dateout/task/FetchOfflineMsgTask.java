package edu.nuist.dateout.task;

import java.util.Iterator;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.OfflineMessageManager;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.core.MsgUtil;
import edu.nuist.dateout.model.CustomMsgPack;

public class FetchOfflineMsgTask extends AsyncTask<String, String, String>
{
    
    private DateoutApp app;
    
    private Handler msgHandler;
    
    public FetchOfflineMsgTask(Handler msgHandler)
    {
        super();
        app = DateoutApp.getInstance();
        this.msgHandler = msgHandler;
    }
    
    @Override
    protected String doInBackground(String... params)
    {
        // 后台加载离线消息
        fetchOfflineMessage();
        return null;
    }
    
    /**
     * 用户获取离线消息
     */
    private void fetchOfflineMessage()
    {
//        try
//        {
//            OfflineMessageManager offlineManager = new OfflineMessageManager(app.getConnection());
//            Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager.getMessages();
//            Log.i("Dateout", "从服务端获取了" + offlineManager.getMessageCount() + "条离线消息");
//            while (it.hasNext())
//            {
//                org.jivesoftware.smack.packet.Message msg = it.next();
//                // 转换为CustomMsgPack
//                CustomMsgPack myMsgPack = MsgUtil.openfireMsg2CustomMsgPack(msg);
//                
//                // 分派消息包，给Handler处理
//                android.os.Message osMsg = msgHandler.obtainMessage();
//                osMsg.what = 1;
//                osMsg.obj = myMsgPack;
//                osMsg.sendToTarget();
//            }
//            // 告知服务端删除我的离线消息
//            offlineManager.deleteMessages();
            
            // 设置状态为在线
            Presence presence = new Presence(org.jivesoftware.smack.packet.Presence.Type.available);
            presence.setMode(Presence.Mode.chat);
            app.getConnection().sendPacket(presence);
//        }
//        catch (XMPPException e)
//        {
//            e.printStackTrace();
//        }
    }
}
