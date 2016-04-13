package com.nuist.dateout.listener;

import com.baidu.platform.comapi.map.x;

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
/**
 * 向右快速滑动，finish掉activity的监听类
 * @author liyuxin
 *
 */
public class MyOnGesTureListener implements OnGestureListener {

	
	private  Activity activity;
	public MyOnGesTureListener(Activity activity) {
		super();
		this.activity =activity;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX>1000) {
			activity.finish();
		}
		return false;
	}

}
