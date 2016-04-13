package com.nuist.dialog;




import net.simonvt.numberpicker.NumberPicker;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.nuist.dateout.R;

public class SexDialog extends Dialog {

	private Context context;
	private int style;
	public String currentItem;
	
	private static NumberPicker np;
	private static int index = 0;
	
	public SexDialog(Context context) {
		super(context);
		this.context = context;
	}

	public SexDialog(Context context, int style) {
		super(context);
		this.context = context;
		this.style = style;
	}
	public SexDialog(Context context, String currentItem) {
		super(context);
		this.context = context;
		this.currentItem =currentItem;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.vcard_info_sex_dialog);
		np = (NumberPicker) findViewById(R.id.np_sex);
		
        String[] sexsStrings = {"男","女"};  
        np.setDisplayedValues(sexsStrings);
        np.setMinValue(0);  
        np.setMaxValue(sexsStrings.length - 1);
        np.setValue(getItemIndex());
        
		// 设置返回按钮事件和文本
		if (backButtonText != null) {
			Button bckButton = ((Button)findViewById(R.id.btn_sexdialog_cancle));
			bckButton.setText(backButtonText);

			if (backButtonClickListener != null) {
				bckButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						backButtonClickListener.onClick(new SexDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_sexdialog_cancle).setVisibility(View.GONE);
		}

		// 设置确定按钮事件和文本
		if (confirmButtonText != null) {
			Button cfmButton = ((Button)findViewById(R.id.btn_sexdialog_confirm));
			cfmButton.setText(confirmButtonText);

			if (confirmButtonClickListener != null) {
				cfmButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						confirmButtonClickListener.onClick(new SexDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_sexdialog_confirm).setVisibility(View.GONE);
		}
	}

	private String backButtonText; // 对话框返回按钮文本
	private String confirmButtonText; // 对话框确定文本

	// 对话框按钮监听事件
	private DialogInterface.OnClickListener backButtonClickListener,
			confirmButtonClickListener;

	/**
	 * 设置back按钮的事件和文本
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
		if ( np.getValue()==0) {
			return "男";
		}
		return "女";
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}
	
	/**
	 * 通过从Vcard传过来的值判断当前是哪个item
	 * @return  0：男 ； 1：女
	 */
	private int getItemIndex(){
		if (currentItem.equals("男")) {
			return 0;
		}else {
			return 1;
		}
	}
	
}
