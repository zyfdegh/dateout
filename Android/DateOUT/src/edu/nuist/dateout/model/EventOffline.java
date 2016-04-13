package edu.nuist.dateout.model;

public class EventOffline
{
    private int flag;
    
    private String tag;
    
    public EventOffline()
    {
        super();
    }
    
    public EventOffline(int flag, String tag)
    {
        super();
        this.flag = flag;
        this.tag = tag;
    }
    
    public int getFlag()
    {
        return flag;
    }
    
    public void setFlag(int flag)
    {
        this.flag = flag;
    }
    
    public String getTag()
    {
        return tag;
    }
    
    public void setTag(String tag)
    {
        this.tag = tag;
    }
    
}
