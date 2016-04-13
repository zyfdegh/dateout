package com.nuist.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import com.nuist.dateout.R;

public class MyProgressDialog extends Dialog {
	private Context context = null;
	private static MyProgressDialog myProgressDialog=null;
	public MyProgressDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public MyProgressDialog(Context context,int theme){
		super(context,theme);
	}
	/**创建dialog*/
	public static MyProgressDialog createDialog(Context context){
		myProgressDialog = new MyProgressDialog(context,R.style.MyProgressDialog);
		myProgressDialog.setContentView(R.layout.progree_dialog_layout);;
		myProgressDialog.getWindow().getAttributes().gravity=Gravity.CENTER;
		return myProgressDialog; 
	}

	
	 public void onWindowFocusChanged(boolean hasFocus){
	    	if (myProgressDialog == null){
	    		return;
	    	}
	        ImageView imageView = (ImageView) myProgressDialog.findViewById(R.id.iv_dialog_anim);
	        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
	        animationDrawable.start();
	    }
	 /**
	  * 设置标题
	  * @param strTitle
	  * @return
	  */
	 public MyProgressDialog setTitile(String strTitle){
	    	return myProgressDialog;
	    }
	 
	 public MyProgressDialog setMessage(String strMessage){
	    	TextView tvMsg = (TextView)myProgressDialog.findViewById(R.id.tv_dialog_content);
	    	
	    	if (tvMsg != null){
	    		tvMsg.setText(strMessage);
	    	}
	    	
	    	return myProgressDialog;
	    }
}
