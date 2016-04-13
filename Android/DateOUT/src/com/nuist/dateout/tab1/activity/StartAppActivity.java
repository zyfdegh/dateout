package com.nuist.dateout.tab1.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.nuist.dateout.R;
import edu.nuist.dateout.activity.LoginActivity;
/***
 * 开启app时的闪屏页面
 * @author liyuxin
 *
 */
public class StartAppActivity extends Activity implements AnimationListener{
	
	private ImageView imageBackgroud;
	private ImageView logoImage;
	private Animation logoAnimation;
	private Animation animation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_app_activity);
		imageBackgroud = (ImageView) findViewById(R.id.iv_start_app_bj); 
		logoImage = (ImageView) findViewById(R.id.iv_dateout_logo);
		
		logoAnimation = AnimationUtils.loadAnimation(this, R.anim.start_app_logo_anim);
		animation = AnimationUtils.loadAnimation(this, R.anim.start_app_anim);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				logoImage.startAnimation(logoAnimation);
				imageBackgroud.startAnimation(animation);
				imageBackgroud.setVisibility(View.VISIBLE);
			}
		}, 1000);
		animation.setAnimationListener(this);//为动画设置监听
	
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		Intent intent = new Intent(StartAppActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}
}
