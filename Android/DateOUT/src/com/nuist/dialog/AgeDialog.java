package com.nuist.dialog;




import net.simonvt.numberpicker.NumberPicker;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.nuist.dateout.R;

public class AgeDialog extends Dialog {

	private Context context;
	private int style;
	
	private NumberPicker np1,np2,np3;
	private static int year =1993;
	private static int month =12;
	private static int day =12;
	
	private String currentItem;
	
	/**
	 * 将出生日期字符串分割成字符串数组
	 * @return
	 */
	private String[] getItemIndex(){
		return currentItem.split("-");
	}
	
	public AgeDialog(Context context) {
		super(context);
		this.context = context;
	}

	public AgeDialog(Context context, int style) {
		super(context);
		this.context = context;
		this.style = style;
	}

	public AgeDialog(Context context, String currentItem) {
		super(context);
		this.context = context;
		this.currentItem =currentItem;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.vcard_info_age_dialog);

		/**
		 * 初始化dialog的时候，将获取edittext的值，准备赋值给numberpcicker
		 */
		String[] dateString= getItemIndex();
		year = Integer.parseInt(dateString[0]);
		month = Integer.parseInt(dateString[1]);
		day = Integer.parseInt(dateString[2]);
		
		np1 = (NumberPicker) findViewById(R.id.np_age_1);
		np2 = (NumberPicker) findViewById(R.id.np_age_2);
		np3 = (NumberPicker) findViewById(R.id.np_age_3);

		np1.setMaxValue(2299);
		np1.setMinValue(1970);
		np1.setValue(year);
		np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				year = np1.getValue();
				if (year % 4 == 0
						&& year % 100 != 0
						|| year % 400 == 0) {
					if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
						np3.setMaxValue(31);
						np3.setMinValue(1);
					}else if(month==4||month==6||month==9||month==11){
						np3.setMaxValue(30);
						np3.setMinValue(1);
					}else{
							np3.setMaxValue(29);
							np3.setMinValue(1);
						}
					
				} else {
					if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
						np3.setMaxValue(31);
						np3.setMinValue(1);
					}else if(month==4||month==6||month==9||month==11){
						np3.setMaxValue(30);
						np3.setMinValue(1);
					}else{
							np3.setMaxValue(28);
							np3.setMinValue(1);
						}
				}

			}
		});

		np2.setMaxValue(12);
		np2.setMinValue(1);
		np2.setValue(month);
		np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				month = np2.getValue();
				if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
					np3.setMaxValue(31);
					np3.setMinValue(1);
				}else if(month==4||month==6||month==9||month==11){
					np3.setMaxValue(30);
					np3.setMinValue(1);
				}else{
					if (year % 4 == 0
							&&year % 100 != 0
							||year % 400 == 0) {
						np3.setMaxValue(29);
						np3.setMinValue(1);
					} else {
						np3.setMaxValue(28);
						np3.setMinValue(1);
					}
				}
			}
		});

		np3.setMaxValue(31);
		np3.setMinValue(1);
		np3.setValue(day);
		np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				day = np3.getValue();
			}
		});
		
		
		
		// 设置返回按钮事件和文本
		if (backButtonText != null) {
			Button bckButton = ((Button)findViewById(R.id.btn_agedialog_cancle));
			bckButton.setText(backButtonText);

			if (backButtonClickListener != null) {
				bckButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						backButtonClickListener.onClick(new AgeDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_agedialog_cancle).setVisibility(View.GONE);
		}

		// 设置确定按钮事件和文本
		if (confirmButtonText != null) {
			Button cfmButton = ((Button)findViewById(R.id.btn_agedialog_confirm));
			cfmButton.setText(confirmButtonText);

			if (confirmButtonClickListener != null) {
				cfmButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						confirmButtonClickListener.onClick(new AgeDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_agedialog_confirm).setVisibility(View.GONE);
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
	
	public static String getDate(){
		return year+"-"+month+"-"+day;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}
}
