package edu.nuist.dateout.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.drawable.Drawable;

/**
 * @author Veayo
 * 用于生成MD5码
 */
public class MD5Assit {

	public String getMD5(byte[] byteArray){
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(byteArray);
            return getHashString(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 返回文件的MD5
	 * @param file
	 * @return
	 */
	public String getMD5(File file){
	    if(file!=null&&file.exists()){
	        try
            {
	            FileInputStream in = new FileInputStream(file);
	            FileChannel ch =in.getChannel();
	            MappedByteBuffer byteBuffer =ch.map(FileChannel.MapMode.READ_ONLY, 0,file.length());
	            MessageDigest digest = MessageDigest.getInstance("MD5");
	            digest.update(byteBuffer);
	            in.close();
	            return getHashString(digest);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }catch (NoSuchAlgorithmException e2) {
                e2.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
	}
	
	/**
	 * 用于获取当前时间的MD5,可用作一个随机Md5
	 * @return 返回系统当前日期的Md5
	 */
	public String getRandomMD5(){
	    String now=TimeAssit.getDate();
	    return getMD5(now);
	}
	
    public String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getMD5(Drawable imageDrawable){
    	if (imageDrawable==null) {
			return null;
		} else {
			return getMD5(FormatTools.Drawable2Bytes(imageDrawable));
		}
    }
    
    private String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }

}