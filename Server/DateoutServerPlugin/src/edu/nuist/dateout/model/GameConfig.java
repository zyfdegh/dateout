package edu.nuist.dateout.model;

public class GameConfig
{
    private String userId;// 用户ID
    
    private String picUrl;// 背景图片URL
    
    private int timeOut;// 游戏超时
    
    private int difficulty;// 游戏难度,3,4,5
    
    public GameConfig()
    {
        super();
    }
    
    public GameConfig(String userId, String picUrl, int timeOut, int difficulty)
    {
        super();
        this.userId = userId;
        this.picUrl = picUrl;
        this.timeOut = timeOut;
        this.difficulty = difficulty;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getPicUrl()
    {
        return picUrl;
    }
    
    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }
    
    public int getTimeOut()
    {
        return timeOut;
    }
    
    public void setTimeOut(int timeOut)
    {
        this.timeOut = timeOut;
    }
    
    public int getDifficulty()
    {
        return difficulty;
    }
    
    public void setDifficulty(int difficulty)
    {
        this.difficulty = difficulty;
    }
}
