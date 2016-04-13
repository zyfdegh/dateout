package edu.nuist.dateout.model;

/**
 * 继承UserLoc,多了一个distance成员
 * 
 * @author Veayo
 *
 */
public class UserNear extends UserLoc
{
    private double distance;
    
    public UserNear()
    {
        super();
    }
    
    public UserNear(double distance)
    {
        super();
        this.distance = distance;
    }
    
    public double getDistance()
    {
        return distance;
    }
    
    public void setDistance(double distance)
    {
        this.distance = distance;
    }
}
