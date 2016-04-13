package edu.nuist.dateout.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用于存储存放在数据库中用户Model类型
 * 
 * @author Veayo
 *
 */
public class UserLoc extends User implements Serializable
{
    private static final long serialVersionUID = -3293552981015944472L;
    
    protected double locJingdu;// 经度
    
    protected double locWeidu;// 纬度
    
    protected Timestamp time;// 时间
    
    public UserLoc()
    {
        super();
    }
    
    public UserLoc(String userId, double locJingdu, double locWeidu, Timestamp time)
    {
        super(userId);
        this.locJingdu = locJingdu;
        this.locWeidu = locWeidu;
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
    
    public Timestamp getTime()
    {
        return time;
    }
    
    public void setTime(Timestamp time)
    {
        this.time = time;
    }
    
}
