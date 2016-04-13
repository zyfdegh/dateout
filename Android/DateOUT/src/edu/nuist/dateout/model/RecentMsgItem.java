package edu.nuist.dateout.model;

import android.net.Uri;

public class RecentMsgItem
{
    private String userId;// 用户Id
    
    private String remarkName;// 显示用户名
    
    private String lastMessage;// 最后一条消息
    
    private String msgDateAndTime;// 消息时间日期
    
    private int unReadMsgNum;// 未读消息数目
    
    private short msgType;// 消息类型
    
    private Uri userHeadUri;// 头像URi
    
    public RecentMsgItem()
    {
        super();
    };
    
    public RecentMsgItem(String userId,String remarkName, String lastMessage, String msgDateAndTime, int unReadMsgNum,
        short msgType, Uri userHeadUri)
    {
        super();
        this.userId=userId;
        this.remarkName = remarkName;
        this.lastMessage = lastMessage;
        this.msgDateAndTime = msgDateAndTime;
        this.unReadMsgNum = unReadMsgNum;
        this.msgType = msgType;
        this.userHeadUri = userHeadUri;
    }
    
    public Uri getUserHeadUri()
    {
        return userHeadUri;
    }
    
    public void setUserHeadUri(Uri userHeadUri)
    {
        this.userHeadUri = userHeadUri;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getRemarkName()
    {
        return remarkName;
    }
    
    public void setRemarkName(String remarkName)
    {
        this.remarkName = remarkName;
    }
    
    public String getLastMessage()
    {
        return lastMessage;
    }
    
    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }
    
    public String getMsgDateAndTime()
    {
        return msgDateAndTime;
    }
    
    public void setMsgDateAndTime(String msgDateAndTime)
    {
        this.msgDateAndTime = msgDateAndTime;
    }
    
    public int getUnReadMsgNum()
    {
        return unReadMsgNum;
    }
    
    public void setUnReadMsgNum(int unReadMsgNum)
    {
        this.unReadMsgNum = unReadMsgNum;
    }
    
    public short getMsgType()
    {
        return msgType;
    }
    
    public void setMsgType(short msgType)
    {
        this.msgType = msgType;
    }
}
