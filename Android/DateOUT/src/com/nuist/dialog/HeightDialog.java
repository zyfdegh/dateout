package com.nuist.dialog;




import net.simonvt.numberpicker.NumberPicker;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.nuist.dateout.R;

public class HeightDialog extends Dialog {

	private Context context;
	private int style;
	public String currentItem;
	
	public static int currentHeightValue = 180;
	
	private static NumberPicker np;
	private static int index = 0;
	
	public HeightDialog(Context context) {
		super(context);
		this.context = context;
	}

	public HeightDialog(Context context, int style) {
		super(context);
		this.context = context;
		this.style = style;
	}
	public HeightDialog(Context context, String currentItem) {
		super(context);
		this.context = context;
		this.currentItem =currentItem;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.vcard_info_height_dialog);

		currentHeightValue = Integer.parseInt(getItemIndex()[0]);
		
		np = (NumberPicker) findViewById(R.id.np_height);
        np.setMinValue(100);  
        np.setMaxValue(250);
        np.setValue(currentHeightValue);
        
       
		
		// 设置返回按钮事件和文本
		if (backButtonText != null) {
			Button bckButton = ((Button)findViewById(R.id.btn_heightdialog_cancle));
			bckButton.setText(backButtonText);

			if (backButtonClickListener != null) {
				bckButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						backButtonClickListener.onClick(new HeightDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_heightdialog_cancle).setVisibility(View.GONE);
		}

		// 设置确定按钮事件和文本
		if (confirmButtonText != null) {
			Button cfmButton = ((Button)findViewById(R.id.btn_heightdialog_confirm));
			cfmButton.setText(confirmButtonText);

			if (confirmButtonClickListener != null) {
				cfmButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						confirmButtonClickListener.onClick(new HeightDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_heightdialog_confirm).setVisibility(View.GONE);
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
	/**
	 * 获取数据
	 * @return
	 */
	public static String getData(){
		return np.getValue()+" cm";
	}
	
	private String[] getItemIndex(){
		return currentItem.split(" ");
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}
}
