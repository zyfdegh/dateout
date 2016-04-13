package com.nuist.dateout.tab1.activity;

import com.nuist.dateout.listener.MyOnGesTureListener;
import com.nuist.dateout.R;
import com.nuist.dialog.MyProgressDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class SetAppActivity extends Activity implements OnClickListener{

	private GestureDetector detector;
	private MyProgressDialog progressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_app_activity);
		detector = new GestureDetector(this, new MyOnGesTureListener(this));
		initViews();
	}
	
	private void initViews() {
		ImageButton backButton = (ImageButton) findViewById(R.id.ib_set_app_back);
		backButton.setOnClickListener(this);
		
		View checkUpdateView =  findViewById(R.id.lo_check_update);//检测更新
		checkUpdateView.setOnClickListener(this);
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_set_app_back:
			finish();
			break;
		case R.id.lo_check_update:
			checkUpdate();
		default:
			break;
		}
	}

	/**检测更新*/
	private void checkUpdate() {
		startProgressDialog();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				stopProgressDialog();
				Toast.makeText(SetAppActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
			}
		}, 5000);
	}
	
	 
     
	 
     /** 开启dialog */
     private void startProgressDialog()
     {
         if (progressDialog == null)
         {
             progressDialog = MyProgressDialog.createDialog(this);
             progressDialog.setMessage("检测中...");
             progressDialog.setCanceledOnTouchOutside(false);
         }
         progressDialog.show();
     }
     
     /** 退出dialog */
     private void stopProgressDialog()
     {
         if (progressDialog != null)
         {
             progressDialog.dismiss();
             progressDialog = null;
         }
     }
}
