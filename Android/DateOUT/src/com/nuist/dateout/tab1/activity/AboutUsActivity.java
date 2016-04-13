package com.nuist.dateout.tab1.activity;

import com.nuist.dateout.listener.MyOnGesTureListener;

import com.nuist.dateout.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AboutUsActivity extends Activity implements OnClickListener{

	private GestureDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us_activity);
		
		detector = new GestureDetector(this, new MyOnGesTureListener(this));
		initViews();
	}
	
	private void initViews() {
		View uesrProtocalView = findViewById(R.id.lo_user_protocal);
		View introduceFunctionView = findViewById(R.id.lo_introduce_function);
		View helpView = findViewById(R.id.lo_help_and_feedback);
		
		ImageButton backButton = (ImageButton) findViewById(R.id.ib_about_us_back);
		backButton.setOnClickListener(this);
		
		introduceFunctionView.setOnClickListener(this);
		helpView.setOnClickListener(this);
		uesrProtocalView.setOnClickListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lo_user_protocal:
			mystartactivity("用户条款","file:///android_asset/about_us.html");
			break;
		case R.id.lo_introduce_function:
			mystartactivity("功能介绍","http://www.baidu.com");
			break;
		case R.id.lo_help_and_feedback:
			startActivity(new Intent(AboutUsActivity.this, HelpAndFeedBackActivity.class));
			break;
		case R.id.ib_about_us_back:
			finish();
			break;
		default:
			break;
		}
	}
	private void mystartactivity(String titleName,String contentUrl){
		Intent intent = new Intent(AboutUsActivity.this,UserProtocalActivity.class);
		intent.putExtra("title_name", titleName);
		intent.putExtra("content_url", contentUrl);
		startActivity(intent);
	}
	
}
