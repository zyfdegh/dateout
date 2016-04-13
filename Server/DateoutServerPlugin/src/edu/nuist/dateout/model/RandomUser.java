package edu.nuist.dateout.model;
/**
 * 用于存储第一个页面随机用户的模型
 * @author Veayo
 *
 */
public class RandomUser
{
    private String userId;//用户ID
    private String headImageUrl;//头像下载链接
    private double distance;//距离
    private String onlineStatus;
    
    
    public RandomUser()
    {
        super();
    }
    public RandomUser(String userId, String headImageUrl, double distance, String onlineStatus)
    {
        super();
        this.userId = userId;
        this.headImageUrl = headImageUrl;
        this.distance = distance;
        this.onlineStatus = onlineStatus;
    }
    public String getUserId()
    {
        return userId;
    }
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    public String getHeadImageUrl()
    {
        return headImageUrl;
    }
    public void setHeadImageUrl(String headImageUrl)
    {
        this.headImageUrl = headImageUrl;
    }
    public double getDistance()
    {
        return distance;
    }
    public void setDistance(double distance)
    {
        this.distance = distance;
    }
    public String getOnlineStatus()
    {
        return onlineStatus;
    }
    public void setOnlineStatus(String onlineStatus)
    {
        this.onlineStatus = onlineStatus;
    }
    
    
}
