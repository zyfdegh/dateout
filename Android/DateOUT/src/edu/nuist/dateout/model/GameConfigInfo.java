package edu.nuist.dateout.model;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * 配置游戏的类
 * 
 * @author liyuxin
 *
 */
public class GameConfigInfo implements Serializable
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -1111111L;
    
    /**
     * 实现序列化，以便可以用intent传递
     */
    private int column;// 游戏是columnn*columnn
    
    private int time;
    
    private int tag;// 用于标记上一个Activity
    
    private String bkgPicUriStr;
    
    public GameConfigInfo(int column, int time, int tag, String bkgPicUriStr)
    {
        super();
        this.column = column;
        this.time = time;
        this.tag = tag;
        this.bkgPicUriStr = bkgPicUriStr;
    }
    
    public String getBkgPicUriStr()
    {
        return bkgPicUriStr;
    }
    
    public void setBkgPicUriStr(String bkgPicUriStr)
    {
        this.bkgPicUriStr = bkgPicUriStr;
    }
    
    public GameConfigInfo()
    {
        super();
    }
    
    public int getColumn()
    {
        return column;
    }
    
    public void setColumn(int column)
    {
        this.column = column;
    }
    
    public int getTime()
    {
        return time;
    }
    
    public void setTime(int time)
    {
        this.time = time;
    }
    
    public int getTag()
    {
        return tag;
    }
    
    public void setTag(int tag)
    {
        this.tag = tag;
    }
    
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
    /** 将bitmap转为字节数组 */
    public static byte[] getBytes(Bitmap bitmap)
    {
        // 实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);// 压缩位图
        return baos.toByteArray();// 创建分配字节数组
    }
}
