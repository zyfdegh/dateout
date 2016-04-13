package edu.nuist.dateout.model;

import android.net.Uri;

/**
 * @author Veayo 好友条目类,用户适配从服务端获取的好友信息和页面显示ListView中元素的关系
 */
public class FriendItem
{
    private String userId;
    
    private String remarkName;// 显示的备注名
    
    private String extendString;// 最近消息
    
    private String onlineStatus; // 在线状态
    
    private Uri headImageUri;// 用户头像
    
    private String sortLetters;  //显示数据拼音的首字母
    
	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
    public FriendItem()
    {
        super();
    }
    
    public FriendItem(String userId, String remarkName, String extendString, String onlineStatus, Uri headImageUri)
    {
        super();
        this.userId = userId;
        this.remarkName = remarkName;
        this.extendString = extendString;
        this.onlineStatus = onlineStatus;
        this.headImageUri = headImageUri;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getExtendString()
    {
        return extendString;
    }
    
    public void setExtendString(String extendString)
    {
        this.extendString = extendString;
    }
    
    public String getOnlineStatus()
    {
        return onlineStatus;
    }
    
    public void setOnlineStatus(String onlineStatus)
    {
        this.onlineStatus = onlineStatus;
    }
    
    public Uri getHeadImageUri()
    {
        return headImageUri;
    }
    
    public void setHeadImageUri(Uri headImageUri)
    {
        this.headImageUri = headImageUri;
    }
    
    public String getRemarkName()
    {
        return remarkName;
    }
    
    public void setRemarkName(String remarkName)
    {
        this.remarkName = remarkName;
    }
}
