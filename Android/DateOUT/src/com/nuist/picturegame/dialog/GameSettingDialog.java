package com.nuist.picturegame.dialog;




import net.simonvt.numberpicker.NumberPicker;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.nuist.dateout.R;

public class GameSettingDialog extends Dialog {

	private Context context;
	private int style;
	
	private static NumberPicker np1;
	private static NumberPicker np2;
	private String currentItem;
	
	
	public GameSettingDialog(Context context) {
		super(context);
		this.context = context;
	}

	public GameSettingDialog(Context context, int style) {
		super(context);
		this.context = context;
		this.style = style;
	}

	public GameSettingDialog(Context context, String currentItem) {
		super(context);
		this.context = context;
		this.currentItem =currentItem;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.game_setting_dialog);

		
		np1 = (NumberPicker) findViewById(R.id.np_game);
		np2 = (NumberPicker) findViewById(R.id.np_game2);

		String [] gameLevel = {"3*3","4*4","5*5"};
		np1.setDisplayedValues(gameLevel);
		np1.setMaxValue(gameLevel.length-1);
		np1.setMinValue(0);
		np1.setValue(0);

		
		
		np2.setMaxValue(60);
		np2.setMinValue(10);
		np2.setValue(30);
		
		
		np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (newVal==0) {
					
					np2.setMaxValue(60);
					np2.setMinValue(10);
					np2.setValue(30);
				}
				else if (newVal==1) {
					np2.setMaxValue(150);
					np2.setMinValue(30);
					np2.setValue(90);
				}else {
					np2.setMaxValue(300);
					np2.setMinValue(100);
					np2.setValue(200);
				}
			}
		});
		// 设置返回按钮事件和文本
		if (backButtonText != null) {
			Button bckButton = ((Button)findViewById(R.id.btn_gamedialog_cancle));
			bckButton.setText(backButtonText);

			if (backButtonClickListener != null) {
				bckButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						backButtonClickListener.onClick(new GameSettingDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_gamedialog_cancle).setVisibility(View.GONE);
		}

		// 设置确定按钮事件和文本
		if (confirmButtonText != null) {
			Button cfmButton = ((Button)findViewById(R.id.btn_gamedialog_confirm));
			cfmButton.setText(confirmButtonText);

			if (confirmButtonClickListener != null) {
				cfmButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						confirmButtonClickListener.onClick(new GameSettingDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_gamedialog_confirm).setVisibility(View.GONE);
		}
	}

	private String backButtonText; // 对话框返回按钮文本
	private String confirmButtonText; // 对话框确定文本

	// 对话框按钮监听事件
	private DialogInterface.OnClickListener backButtonClickListener,
			confirmButtonClickListener;

	/**
	 * 设置back按钮的事件和文本
	 * 
	 * @param backButtonText
	 * @param listener
	 * @return
	 */
	public void setBackButton(String backButtonText,
			DialogInterface.OnClickListener listener) {
		this.backButtonText = backButtonText;
		this.backButtonClickListener = listener;
	}

	/**
	 * 设置确定按钮事件和文本
	 * 
	 * @param negativeButtonText
	 * @param listener
	 * @return
	 */
	public void setConfirmButton(String confirmButtonText,
			DialogInterface.OnClickListener listener) {
		this.confirmButtonText = confirmButtonText;
		this.confirmButtonClickListener = listener;
	}
	
	public static int getData(){
		
		return np1.getValue()+3;	
				 
	}
	public static int getData1(){
		
		return np2.getValue();	
				 
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}
	
	
	
}
