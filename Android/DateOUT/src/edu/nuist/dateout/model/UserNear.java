package edu.nuist.dateout.model;

import java.io.Serializable;

/**
 * 继承UserLoc,多了一个distance成员
 * 
 * @author Veayo
 *
 */
public class UserNear extends UserLoc implements Serializable
{
    private static final long serialVersionUID = -4108458387946520553L;
    
    private double distance;
    
    private String nickName;
    
    public UserNear()
    {
        super();
    }
    
    public UserNear(double distance, String nickName)
    {
        super();
        this.distance = distance;
        this.nickName = nickName;
    }
    
    public double getDistance()
    {
        return distance;
    }
    
    public void setDistance(double distance)
    {
        this.distance = distance;
    }
    
    public String getNickName()
    {
        return nickName;
    }
    
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }
    
}
