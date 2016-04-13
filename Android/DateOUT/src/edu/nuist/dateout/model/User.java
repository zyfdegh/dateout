package edu.nuist.dateout.model;

import java.io.Serializable;

/**
 * 用户模型
 * 
 * @author Veayo
 *
 */
public class User implements Serializable
{
    private static final long serialVersionUID = -7306807977034019183L;
    
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
