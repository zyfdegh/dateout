package edu.nuist.dateout.model;

import android.net.Uri;

/**
 * 用户类,服务端数据存放于VCard
 * 
 * @author Veayo
 *
 */
public class UserHead extends User
{
    /**
     * 
     */
    private static final long serialVersionUID = 3311121816055443223L;
    
    private Uri headImageUri;
    
    public UserHead()
    {
        super();
    }

    public UserHead(Uri headImageUri)
    {
        super();
        this.headImageUri = headImageUri;
    }

    public Uri getHeadImageUri()
    {
        return headImageUri;
    }

    public void setHeadImageUri(Uri headImageUri)
    {
        this.headImageUri = headImageUri;
    }
    
}
