package edu.nuist.dateout.model;

import java.io.Serializable;

/**
 * 地图上显示好友的信息
 * 
 * @author liyuxin
 */
public class FriendInMapModel extends User implements Serializable
{
    
    private static final long serialVersionUID = -7562700887736976779L;
    
    private String shakeTime;// 时间
    
    private String userNickname; // 昵称
    
    private int distance;// 距离
    
    public FriendInMapModel()
    {
        super();
    }
    
    public FriendInMapModel(String userNickname, int distance, String userId, String shakeTime)
    {
        super();
        this.userNickname = userNickname;
        this.distance = distance;
        this.userId = userId;
        this.shakeTime = shakeTime;
    }
    
    public String getShakeTime()
    {
        return shakeTime;
    }
    
    public void setShakeTime(String shakeTime)
    {
        this.shakeTime = shakeTime;
    }
    
    public String getUserNickname()
    {
        return userNickname;
    }
    
    public void setUserNickname(String userNickname)
    {
        this.userNickname = userNickname;
    }
    
    public int getDistance()
    {
        return distance;
    }
    
    public void setDistance(int distance)
    {
        this.distance = distance;
    }
}
