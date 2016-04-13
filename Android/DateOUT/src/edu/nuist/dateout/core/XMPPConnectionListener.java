package edu.nuist.dateout.core;

import org.jivesoftware.smack.ConnectionListener;

import android.util.Log;
import edu.nuist.dateout.app.DateoutApp;

/**
 * 连接监听类
 * 用于掉线的检测与重连
 * @author Administrator
 * 
 */
public class XMPPConnectionListener implements ConnectionListener {
//	private Timer timer;
//	private int reconnectGapTime = 8*1000;//设置重新连接间隔
	private DateoutApp app=DateoutApp.getInstance();
	
	@Override
	public void connectionClosed() {
		Log.i("Dateout", "XMPPConnectionListener==>"+"正常关闭连接");
		// 關閉連接
		app.getConnection().disconnect();
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		Log.v("Dateout", "XMPPConnectionListener==>"+"连接关闭异常");
		// 判斷為帳號已被登錄
		if (e.getMessage().equals("stream:error (conflict)")) {
			//在别的地方登录
		    //TODO 在主线程弹出被挤下线的提示,并询问用户是否重新登录?是否修改密码?是否退出
		}else {
		    // 關閉連接
            app.getConnection().disconnect();
            // 重连服务器
            new XMPPConnectionAssit(app.getServerIpInUse()).reLogin();
        }
	}
	
	@Override
	public void reconnectingIn(int reconnectGapTime) {
		Log.i("Dateout", "XMPPConnectionListener==>"+"reconnectingIn()");
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		Log.i("Dateout", "XMPPConnectionListener==>"+"reconnectionFailed()");
	}

	@Override
	public void reconnectionSuccessful() {
		Log.i("Dateout", "XMPPConnectionListener==>"+"reconnectionSuccessful()");
	}
	
//	class MyTimerTask extends TimerTask {
//		@Override
//		public void run() {
//		    //重新登录
//			new XMPPConnectionAssit(app.getServerIpInUse()).reLogin();
//		}
//	}
}