package edu.nuist.dateout.model;

/**
 * 用于封装聊天气泡内容的模型
 * 
 * @author Veayo
 *
 */
public class ChatBubbleItem
{
    private String msgText;// 文本消息内容
    
    private String msgResUrl;// 消息资源Url(如图片 语音或者文件等类型的下载链接)
    
    private String msgDateAndTime;// 消息日期时间
    
    private short msgType;
    
    private boolean isMsgSentByMe;
    
    public ChatBubbleItem()
    {
        super();
    }
    
    public ChatBubbleItem(String msgText, String msgResUrl, String msgDateAndTime, short msgType, boolean isMsgSentByMe)
    {
        super();
        this.msgText = msgText;
        this.msgResUrl = msgResUrl;
        this.msgDateAndTime = msgDateAndTime;
        this.msgType = msgType;
        this.isMsgSentByMe = isMsgSentByMe;
    }
    
    public String getMsgText()
    {
        return msgText;
    }
    
    public void setMsgText(String msgText)
    {
        this.msgText = msgText;
    }
    
    public String getMsgResUrl()
    {
        return msgResUrl;
    }
    
    public void setMsgResUrl(String msgResUrl)
    {
        this.msgResUrl = msgResUrl;
    }
    
    public String getMsgDateAndTime()
    {
        return msgDateAndTime;
    }
    
    public void setMsgDateAndTime(String msgDateAndTime)
    {
        this.msgDateAndTime = msgDateAndTime;
    }
    
    public short getMsgType()
    {
        return msgType;
    }
    
    public void setMsgType(short msgType)
    {
        this.msgType = msgType;
    }
    
    public boolean isMsgSentByMe()
    {
        return isMsgSentByMe;
    }
    
    public void setMsgSentByMe(boolean isMsgSentByMe)
    {
        this.isMsgSentByMe = isMsgSentByMe;
    }
    
}
