package edu.nuist.dateout.model;

import java.sql.Timestamp;

/**
 * 用于存储存放在数据库中用户Model类型
 * 
 * @author Veayo
 *
 */
public class UserLoc extends User
{
    
    protected double locWeidu;// 纬度
    
    protected double locJingdu;// 经度
    
    protected Timestamp time;// 时间
    
    public Timestamp getTime()
    {
        return time;
    }
    
    public void setTime(Timestamp time)
    {
        this.time = time;
    }
    
    public UserLoc()
    {
        super();
    }
    
    public UserLoc(String userId,double locWeidu, double locJingdu, Timestamp time)
    {
        super(userId);
        this.locWeidu = locWeidu;
        this.locJingdu = locJingdu;
        this.time = time;
    }
    
    public double getLocWeidu()
    {
        return locWeidu;
    }
    
    public void setLocWeidu(double locWeidu)
    {
        this.locWeidu = locWeidu;
    }
    
    public double getLocJingdu()
    {
        return locJingdu;
    }
    
    public void setLocJingdu(double locJingdu)
    {
        this.locJingdu = locJingdu;
    }
}
