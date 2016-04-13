package edu.nuist.dateout.core;

import java.sql.Timestamp;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.packet.DelayInformation;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.ChatBubbleItem;
import edu.nuist.dateout.model.CustomMsgPack;
import edu.nuist.dateout.util.TimeAssit;
import edu.nuist.dateout.value.VariableHolder;

public class MsgUtil
{
    public static CustomMsgPack openfireMsg2CustomMsgPack(Message msg)
    {
        DelayInformation delayInfo = (DelayInformation)msg.getExtension("x", "jabber:x:delay");
        String dateAndTime;
        // 如果是离线消息,则时间依据服务端记载的时间为准.否则用本机时间
        if (delayInfo != null)
        {
            Timestamp timestamp = new Timestamp(delayInfo.getStamp().getTime());
            dateAndTime = TimeAssit.timestamp2MyStardFormat(timestamp);
            // dateAndTime = TimeAssit.formatDate(delayInfo.getStamp());
        }
        else
        {
            dateAndTime = TimeAssit.getDate();
        }
        
        String senderId = msg.getFrom().split("@")[0];
        String receiverId = msg.getTo().split("@")[0];
        String msgBody = msg.getBody();
        short msgType = VariableHolder.MSG_TYPE_NOTSET;
        // 设置消息类型
        if (msgBody.indexOf(VariableHolder.MSG_FLAG_IMAGE) == 0)
        {
            msgType = VariableHolder.MSG_TYPE_IMAGE;
        }
        else if (msgBody.indexOf(VariableHolder.MSG_FLAG_AUDIO) == 0)
        {
            msgType = VariableHolder.MSG_TYPE_AUDIO;
        }
        else if (msgBody.indexOf(VariableHolder.MSG_FLAG_FILE) == 0)
        {
            msgType = VariableHolder.MSG_TYPE_FILE;
        }
        else
        {
            msgType = VariableHolder.MSG_TYPE_TEXT;
        }
        System.out.println("==>>收到消息: 来自" + senderId + " ,发给 " + receiverId + ",类型: " + msgType + ",内容:"
            + msg.getBody());
        return new CustomMsgPack(senderId, receiverId, msgBody, dateAndTime, msgType);
    }
    
    public static ChatBubbleItem msgPack2ChatBubbleItem(CustomMsgPack msgPack)
    {
        ChatBubbleItem result = new ChatBubbleItem();
        short msgType = msgPack.getMsgType();
        result.setMsgType(msgType);
        // 消息内容
        if (msgType == VariableHolder.MSG_TYPE_AUDIO || msgType == VariableHolder.MSG_TYPE_IMAGE)
        {
            result.setMsgResUrl(msgPack.getMsgBody());
        }
        else
        {
            result.setMsgText(msgPack.getMsgBody());
        }
        // 消息时间
        result.setMsgDateAndTime(msgPack.getDateAndTime());
        
        String senderId = msgPack.getSenderId();
        DateoutApp app = DateoutApp.getInstance();
        String loginId = app.getLoginUser().getUserId();
        if (senderId.equals(loginId))
        {
            // 消息是自己发送的
            result.setMsgSentByMe(true);
        }
        else
        {
            // 消息是接收到的
            result.setMsgSentByMe(false);
        }
        return result;
    }
}
