package edu.nuist.dateout.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import edu.nuist.dateout.config.ServerConfig;

public class OnlineStatusJudge
{
    /**
     * @param strUrl 判断openfire用户的状态 strUrl : url格式 - http://my.openfire.com:9090/plugins/presence
     *            /status?jid=user1@my.openfire.com&type=xml
     * @return 0 - 用户不存在; 1 - 用户在线; 2 -用户离线
     */
    public static short checkOnlineStatus(String strUrl)
    {
        short shOnLineState = 2; // -不存在-
        try
        {
            URL oUrl = new URL(strUrl);
            URLConnection oConn = oUrl.openConnection();
            if (oConn != null)
            {
                BufferedReader oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));
                if (oIn != null)
                {
                    String strFlag = oIn.readLine();
                    oIn.close();
                    if (strFlag.indexOf("type=\"unavailable\"") >= 0 || strFlag.indexOf("Unavailable") >= 0)
                    {
                        shOnLineState = 0;// 离线
                    }
                    else if (strFlag.indexOf("error code=\"403\"") >= 0 || strFlag.indexOf("type=\"error\"") >= 0)
                    {
                        shOnLineState = 2;// 不存在
                    }
                    else if (strFlag.indexOf("priority") >= 0 || strFlag.indexOf("id=\"") >= 0)
                    {
                        shOnLineState = 1;// 在线
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return shOnLineState;
    }
    
    /**
     * 用于判定指定Id的用户在线状态
     * 
     * @param userId 用户Id
     * @return 0-离线,1-在线,2-不存在
     */
    public static short getUserOnlineStatus(String userId)
    {
        // URL如: http://192.168.2.135:9090/plugins/presence/status?jid=user5@lenovo&type=xml
        String url =
            new String("http://" + ServerConfig.SERVER_IP + ":9090/plugins/presence/status?jid=" + userId
                + "@lenovo&type=xml");
        return checkOnlineStatus(url);// 调用该类的方法获取返回值
    }
}
