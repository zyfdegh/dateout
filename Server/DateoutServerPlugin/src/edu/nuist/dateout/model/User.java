package edu.nuist.dateout.model;

/**
 * 用户模型
 * 
 * @author Veayo
 *
 */
public class User
{
    protected String userId;
    
    public User()
    {
        super();
    }
    
    public User(String userId)
    {
        super();
        this.userId = userId;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
