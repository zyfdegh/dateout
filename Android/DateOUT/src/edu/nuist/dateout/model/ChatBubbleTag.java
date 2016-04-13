package edu.nuist.dateout.model;

import android.view.View;

public class ChatBubbleTag
{
    private short msgType;
    
    private String resUrl;
    
    private boolean isMsgSentByMe;
    
    private View view ;
    
    public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public ChatBubbleTag(short msgType, String resUrl, boolean isMsgSentByMe)
    {
        super();
        this.msgType = msgType;
        this.resUrl = resUrl;
        this.isMsgSentByMe = isMsgSentByMe;
    }
    
    public ChatBubbleTag()
    {
        super();
    }
    
    public boolean isMsgSentByMe()
    {
        return isMsgSentByMe;
    }
    
    public void setMsgSentByMe(boolean isMsgSentByMe)
    {
        this.isMsgSentByMe = isMsgSentByMe;
    }
    
    public short getMsgType()
    {
        return msgType;
    }
    
    public void setMsgType(short msgType)
    {
        this.msgType = msgType;
    }
    
    public String getResUrl()
    {
        return resUrl;
    }
    
    public void setResUrl(String resUrl)
    {
        this.resUrl = resUrl;
    }
}
