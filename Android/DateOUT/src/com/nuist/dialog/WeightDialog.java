package com.nuist.dialog;




import net.simonvt.numberpicker.NumberPicker;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.nuist.dateout.R;

public class WeightDialog extends Dialog {

	private Context context;
	private int style;
	public String currentItem;
	
	public static int currentweightValue = 60;
	
	private static NumberPicker np;
	private static int index = 0;
	
	public WeightDialog(Context context) {
		super(context);
		this.context = context;
	}

	public WeightDialog(Context context, int style) {
		super(context);
		this.context = context;
		this.style = style;
	}
	public WeightDialog(Context context, String currentItem) {
		super(context);
		this.context = context;
		this.currentItem =currentItem;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.vcard_info_weight_dialog);

		currentweightValue = Integer.parseInt(getItemIndex()[0]);
		
		np = (NumberPicker) findViewById(R.id.np_weight);
        np.setMinValue(30);  
        np.setMaxValue(120);
        np.setValue(currentweightValue);
        
       
		
		// 设置返回按钮事件和文本
		if (backButtonText != null) {
			Button bckButton = ((Button)findViewById(R.id.btn_weightdialog_cancle));
			bckButton.setText(backButtonText);

			if (backButtonClickListener != null) {
				bckButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						backButtonClickListener.onClick(new WeightDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_weightdialog_cancle).setVisibility(View.GONE);
		}

		// 设置确定按钮事件和文本
		if (confirmButtonText != null) {
			Button cfmButton = ((Button)findViewById(R.id.btn_weightdialog_confirm));
			cfmButton.setText(confirmButtonText);

			if (confirmButtonClickListener != null) {
				cfmButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						confirmButtonClickListener.onClick(new WeightDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_weightdialog_confirm).setVisibility(View.GONE);
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
		return np.getValue()+" kg";
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
