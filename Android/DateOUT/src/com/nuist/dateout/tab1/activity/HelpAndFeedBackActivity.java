package com.nuist.dateout.tab1.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.nuist.dateout.R;
import com.nuist.dateout.listener.MyOnGesTureListener;

public class HelpAndFeedBackActivity extends Activity implements OnClickListener{
	private GestureDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_feedback_activity);
		
		initViews();
	}

	private void initViews() {
		ImageButton backButton = (ImageButton) findViewById(R.id.ib_help_feedback_back);
		backButton.setOnClickListener(this);
		
		detector = new GestureDetector(this, new MyOnGesTureListener(this));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_help_feedback_back:
			finish();
			break;

		default:
			break;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}
}
