package edu.nuist.dateout.model;

/**
 * Tab1随机用户模型
 * @author Veayo
 *
 */
public class UserRandom extends User
{
    private String headUrl;//头像下载链接
    private double distance;//距离
    private short onlineStat;//在线状态
    
    
    public UserRandom()
    {
        super();
    }
    public UserRandom(String headUrl, double distance, short onlineStat)
    {
        super();
        this.headUrl = headUrl;
        this.distance = distance;
        this.onlineStat = onlineStat;
    }
    public String getHeadUrl()
    {
        return headUrl;
    }
    public void setHeadUrl(String headUrl)
    {
        this.headUrl = headUrl;
    }
    public double getDistance()
    {
        return distance;
    }
    public void setDistance(double distance)
    {
        this.distance = distance;
    }
    public short getOnlineStat()
    {
        return onlineStat;
    }
    public void setOnlineStat(short onlineStat)
    {
        this.onlineStat = onlineStat;
    }
    
    
}
