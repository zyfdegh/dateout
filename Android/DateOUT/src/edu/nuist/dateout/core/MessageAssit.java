package edu.nuist.dateout.core;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import de.greenrobot.event.EventBus;
import edu.nuist.dateout.activity.ChatActivity;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.model.CustomMsgPack;
import edu.nuist.dateout.task.DownloadAndCacheTask;
import edu.nuist.dateout.task.FetchOfflineMsgTask;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.value.VariableHolder;

//TODO 将该类转变为Service
/**
 * @author Veayo 主要用于接收消息，并分派消息到ChatActivity和RecentMsgManager和顶部通知栏
 */
public class MessageAssit
{
    private DateoutApp app;
    
    private Context context;
    
    private DBAssit dbAssit;
    
    // ====处理接收到的消息===
    public Handler msgHandler = new Handler()
    {
        public void handleMessage(android.os.Message osMsg)
        {
            switch (osMsg.what)
            {
                case 1:
                    // 获取MyMessagePack对象
                    CustomMsgPack msgPack = (CustomMsgPack)osMsg.obj;
                    
                    // 得到消息内容
                    String msgBody = msgPack.getMsgBody();
                    // 获取发消息用户的用户名
                    String senderId = msgPack.getSenderId();
                    // 获取当前正在与我聊天的用户
                    String userChatting = app.getChatUser().getUserId();
                    String tickerText;
                    // 设置通知
                    Intent clickIntent = new Intent();
                    clickIntent.setClass(context, ChatActivity.class);
                    // 将此处的userToChat放入intent之中,然后在ChatActivity里面取出来使用
                    clickIntent.putExtra("USER_TO_CHAT", senderId);
                    MsgNotifier notifier = new MsgNotifier(context);
                    // 获得消息类型
                    short msgType = msgPack.getMsgType();
                    switch (msgType)
                    {
                        case VariableHolder.MSG_TYPE_TEXT:
                            // ===通知栏提示信息==
                            // 只在进入ChatActivity发出状态栏提醒,且只提醒不是当前聊天对象的好友的消息
                            /*
                             * 下面几种情况不需要推送通知* 1.RecentMsgListManager在前台时候(MainActivity在前台且当前Tab为消息列表界面)
                             * 2.正在与聊天的用户发送消息时候(ChatActivity在前台且聊天的对象为)
                             */
                            if (app.isChatActivityIsEntered() && (!senderId.equals(userChatting))
                                || ProcessAssit.isBackground(app.getApplicationContext()))
                            {
                                // TODO 将下面break之前的代码移动到这里
                            }
                            tickerText = senderId + ":" + msgBody;// 消息滚动的内容
                            // 设置通知
                            notifier.addMsgNotification(tickerText, senderId, msgBody, clickIntent);
                            // 在下面发送消息
                            break;
                        case VariableHolder.MSG_TYPE_AUDIO:
                            // 解析消息体
                            // 分解msgBody中的内容,根据逗号分割,其中msgBodyParts[0]为消息类型标识,msgBodyParts[1]为文件名称
                            String msgBodyParts[] = msgBody.split(",");
                            String fileName = msgBodyParts[1];
                            
                            // 把文件下载到下面的路径
                            // fileSavePath=Environment.getExternalStorageDirectory()+"/"+VariableHolder.DIR_APP_DATA_AUDIO_RECEIVED+fileName;
                            String fileSavePath = FilePathTool.getAudioReceivedPath(fileName);
                            // 后台下载文件fileName并缓存文件到fileSavePath
                            new DownloadAndCacheTask(fileName, fileSavePath, null).execute();
                            
                            // 设置通知
                            tickerText = senderId + " 发来一段语音";// 消息滚动的内容
                            notifier.addMsgNotification(tickerText, senderId, "发来一段语音", clickIntent);
                            
                            // 将消息体内容替换为该文件的Uri
                            msgPack.setMsgBody(fileSavePath);// 下面存到库中时候用的文件路径Uri
                            // 下载完成之后的后续操作在ChatActivity中进行
                            break;
                        case VariableHolder.MSG_TYPE_IMAGE:
                            // 解析消息体
                            // 分解msgBody中的内容,根据逗号分割,其中msgBodyParts[0]为消息类型标识,msgBodyParts[1]为文件名称
                            msgBodyParts = msgBody.split(",");
                            // fileName = ;
                            
                            String picDownLink = msgBodyParts[1];
                            // 下载文件
                            // 把文件下载到下面的路径
                            // fileSavePath=Environment.getExternalStorageDirectory()+"/"+VariableHolder.DIR_APP_DATA_IMAGE_RECEIVED+fileName;
                            // fileSavePath = FilePathTool.getImageReceivedPath(fileName);
                            // 后台下载文件fileName并缓存文件到fileSavePath
                            // new DownloadAndCacheTask(fileName, fileSavePath, null).execute();
                            
                            // 设置通知
                            tickerText = senderId + " 发来一张图片";// 消息滚动的内容
                            notifier.addMsgNotification(tickerText, senderId, "发来一张图片", clickIntent);
                            
                            // 将消息体内容替换为该文件的Uri
                            msgPack.setMsgBody(picDownLink);// 下面存到库中时候用的文件路径Uri
                            break;
                        case VariableHolder.MSG_TYPE_FILE:
                            tickerText = senderId + " 发来一个文件";// 消息滚动的内容
                            // 设置通知
                            // TODO 将msgBody中的文件信息提取出来
                            notifier.addMsgNotification(tickerText, senderId, "发来文件+文件名", clickIntent);
                            break;
                        default:
                            break;
                    }
                    
                    // 通过Event发布消息,在ChatActivity和RecentMsglistManager那里接收
                    EventBus.getDefault().post(msgPack);// 发送出去为了在文件下载完成之前在视图上面显示一个默认的图片
                    
                    // 存储消息到数据库
                    if (!dbAssit.isDbConnected())
                    {
                        dbAssit.connectDb();
                    }
                    dbAssit.saveChatMessage(msgPack);
                    dbAssit.closeDbConnect();
                    break;
                default:
                    break;
            }
        };
    };
    
    public MessageAssit(Context context)
    {
        this.context = context;
        dbAssit = new DBAssit();
        app = DateoutApp.getInstance();
        // 连接状态监听,用于在掉线后重连
        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        app.getConnection().addConnectionListener(connectionListener);
    }
    
    public void work()
    {
        // 监听消息
        listenningMessage(app.getConnection(), msgHandler);
        // 获取离线消息
        new FetchOfflineMsgTask(msgHandler).execute();
    }
    
    // =================消息监听=================
    public void listenningMessage(XMPPConnection con, final Handler h)
    {
        try
        {
            ChatManager chatManager = con.getChatManager();
            chatManager.addChatListener(new ChatManagerListener()
            {
                @Override
                public void chatCreated(Chat chat, boolean able)
                {
                    chat.addMessageListener(new MessageListener()
                    {
                        @Override
                        public void processMessage(Chat chat, Message msg)
                        {
                            if (msg.getBody() != null)
                            {
                                // 解析消息
                                CustomMsgPack receivedMsgPack = MsgUtil.openfireMsg2CustomMsgPack(msg);
                                
                                // 分派消息包，给Handler处理
                                android.os.Message osMsg = h.obtainMessage();
                                osMsg.what = 1;
                                osMsg.obj = receivedMsgPack;
                                osMsg.sendToTarget();
                            }
                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            Log.e("Dateout", "MessageAssit==>" + "发生异常");
            e.printStackTrace();
        }
    }
}
