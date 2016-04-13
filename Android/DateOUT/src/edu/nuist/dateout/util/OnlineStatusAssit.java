package edu.nuist.dateout.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.value.VariableHolder;
//TODO 改为Presence机制
/**
 * 警告:该类涉及Http网络访问,不能在UI线程中执行
 * @author Veayo 用于判断用户是否在线,需要Openfire服务端安装Presence插件,并且设置访问权限
 */
public class OnlineStatusAssit {
	private DateoutApp app;
	public OnlineStatusAssit(){
		app=DateoutApp.getInstance();
	}
	/**
	 * @param strUrl
	 *            判断openfire用户的状态 strUrl : url格式 -
	 *            http://my.openfire.com:9090/plugins/presence
	 *            /status?jid=user1@my.openfire.com&type=xml
	 * @return 0 - 用户不存在; 1 - 用户在线; 2 -用户离线
	 */
	public short checkOnlineStatus(String strUrl) {
		short shOnLineState = VariableHolder.STAT_NOT_EXIST; // -不存在-
		try {
			URL oUrl = new URL(strUrl);
			URLConnection oConn = oUrl.openConnection();
			if (oConn != null) {
				BufferedReader oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));
				if (oIn!=null) {
					String strFlag = oIn.readLine();
					oIn.close();
					if (strFlag.indexOf("type=\"unavailable\"") >= 0||strFlag.indexOf("Unavailable") >= 0) {
						shOnLineState = VariableHolder.STAT_OFFLINE;//离线
					}else if (strFlag.indexOf("error code=\"403\"")>=0||strFlag.indexOf("type=\"error\"") >= 0) {
						shOnLineState = VariableHolder.STAT_NOT_EXIST;//不存在
					} else if (strFlag.indexOf("priority") >= 0	|| strFlag.indexOf("id=\"") >= 0) {
						shOnLineState = VariableHolder.STAT_ONLINE;//在线
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return shOnLineState;
	}
	
	/**
	 * 用于判定指定Jid的用户在线状态
	 * @param userJid
	 * @return
	 */
	public short getUserOnlineStatus(String userJid){
		//URL如: http://192.168.2.135:9090/plugins/presence/status?jid=user5@lenovo&type=xml
		String url=new String("http://"+app.getServerIpInUse()+":9090/plugins/presence/status?jid="+userJid+"&type=xml");
		return checkOnlineStatus(url);//调用该类的方法获取返回值
	}
	
	/**
	 * 用于解释和转换用户在线状态
	 * */
	public static String interpretPresenceType(Type onlineType){
		String onlineStatus;
		if(onlineType.equals(Presence.Type.available)){
			onlineStatus="在线";
		}else if(onlineType.equals(Presence.Type.unavailable)){
			onlineStatus="离线";
		}else{
			onlineStatus="其他";
		}
		return onlineStatus;
	}
}
