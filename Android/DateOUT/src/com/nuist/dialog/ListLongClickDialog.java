package com.nuist.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.nuist.dateout.R;
/**
 * 自定义长按好友列表的item时，弹出的dialog
 * @author liyuxin
 *
 */
public class ListLongClickDialog extends Dialog implements android.view.View.OnClickListener {

	private TextView nameTextView;

	public ListLongClickDialog(Context context, int theme) {
		super(context, theme);
	}

	public ListLongClickDialog(Context context) {
		super(context);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_long_click_dialog);
		
		initViews();
	}

	private void initViews() {
		nameTextView = (TextView) findViewById(R.id.tv_dialog_friend_name);
		TextView showCardTextView = (TextView) findViewById(R.id.tv_dialog_friend_showcard);
		TextView deleteteTextView = (TextView) findViewById(R.id.tv_dialog_friend_delete);
		TextView setNickNameTextView = (TextView) findViewById(R.id.tv_dialog_friend_set_nickname);
		showCardTextView.setOnClickListener(this);
		deleteteTextView.setOnClickListener(this);
		setNickNameTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_dialog_friend_showcard:
			clickListener.onclick(0);
			break;
		case R.id.tv_dialog_friend_delete:
			clickListener.onclick(2);
			break;
		case R.id.tv_dialog_friend_set_nickname:
			clickListener.onclick(1);
		default:
			break;
		}
	}
	/**更改用户名*/
	public void setName(String name){
		nameTextView.setText(name);
	}
	
	/**设置事件回调*/
	private onDialogClickListener clickListener;
	public  void setonDialogClickListener(onDialogClickListener clickListener){
		this.clickListener = clickListener;
	}
	public interface onDialogClickListener{
		void onclick(int index);//0代表显示用户名片，1代表设置备注，2代表删除好友
	}
}
