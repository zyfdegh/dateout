package edu.nuist.dateout.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.nuist.dateout.R;

/**
 * @author Veayo
 *消息通知器
 *用于设置顶部状态栏消息通知
 */
public class MsgNotifier {
	private Context context;
	private int notificationNum;
	
	public MsgNotifier(Context context) {
		this.context=context;
		//this.context=DateoutApp.getInstance().getApplicationContext();
		this.notificationNum=0;
	}
	//状态栏通知
	/**
	 * 用于显示顶部状态消息
	 * @param notifyTickerText 滚动显示的内容
	 * @param notifyTitleText 标题
	 * @param notifyContentText 通知内容
	 * @param clickIntent 点击后的事件
	 */
	public void addMsgNotification(String notifyTickerText,String notifyTitleText,String notifyContentText,Intent clickIntent) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notificationNum++;
		//通知图标
		notification.icon = R.drawable.ic_notification;
		//滚动提示内容
		notification.tickerText = notifyTickerText;
		//设置通知时间
		notification.when = System.currentTimeMillis();  
		notification.flags=Notification.FLAG_AUTO_CANCEL;
		//提示铃声
		notification.defaults=Notification.DEFAULT_ALL;
		//设置通知数目
		notification.number=notificationNum;
		//自定义提示音
		//notification.sound = Uri.parse("file:///sdcard/ringstones/Fire.mp3");
		//铃声效果调节
		notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
		//点击要跳转的下一个Activity
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_ONE_SHOT);
		//显示内容
		notification.setLatestEventInfo(context, notifyTitleText, notifyContentText, pendingIntent);
		manager.notify(R.drawable.ic_notification, notification);
	}
}
