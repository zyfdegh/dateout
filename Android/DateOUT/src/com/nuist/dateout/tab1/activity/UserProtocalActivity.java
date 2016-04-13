package com.nuist.dateout.tab1.activity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nuist.dateout.R;
import com.nuist.dateout.listener.MyOnGesTureListener;
/**
 * 用户协议
 * @author liyuxin
 *
 */
public class UserProtocalActivity extends Activity {

	private ProgressBar progressBar;
	private GestureDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_protocal_activity);
		
		String titleName = getIntent().getExtras().getString("title_name");
		String contentUrl =getIntent().getExtras().getString("content_url");
		
		TextView titleNameTextView = (TextView) findViewById(R.id.tv_title_name);
		titleNameTextView.setText(titleName);
		
		detector = new GestureDetector(this, new MyOnGesTureListener(this));
		
		progressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);
		progressBar.setMax(100);  
		
		ImageButton backButton = (ImageButton) findViewById(R.id.ib_user_protocal_back);
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		WebView webView = (WebView) findViewById(R.id.wv_user_protocal);
		webView.setWebViewClient(new WebViewClient() {
			// 新开页面时用自己定义的webview来显示，不用系统自带的浏览器来显示
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			// 开始加载网页时要做的工作
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
			
			// 加载完成时要做的工作
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
			
			// 加载错误时要做的工作
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
		});
		webView.setWebChromeClient(new MyWebChromeClient());
		
		webView.loadUrl(contentUrl);
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}
	
	private class MyWebChromeClient extends WebChromeClient{

		/**设置progressBar*/
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			progressBar.setProgress(newProgress);  
	        if(newProgress==100){  
	        	progressBar.setVisibility(View.GONE);  
	        }  
			super.onProgressChanged(view, newProgress);
		}
		
	}
}
