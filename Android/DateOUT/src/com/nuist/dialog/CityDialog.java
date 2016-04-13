package com.nuist.dialog;




import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.simonvt.numberpicker.NumberPicker;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.nuist.dateout.R;
import edu.nuist.dateout.model.CityModel;
import edu.nuist.dateout.model.ProvinceModel;
import edu.nuist.dateout.util.XmlParserHandler;

public class CityDialog extends Dialog {

	private Context context;
	private int style;
	public String currentItem;
	
	private static NumberPicker np1,np2;
	
	private static String[] provinceArrays=null;
	private static String[] cityNames=null;
//	private static int index = 0;
	/**
	 * 解析xml后得到的 省 集合
	 */
	private List<ProvinceModel> provinceList = null;
	
	public CityDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CityDialog(Context context, int style) {
		super(context);
		this.context = context;
		this.style = style;
	}
	public CityDialog(Context context, String currentItem) {
		super(context);
		this.context = context;
		this.currentItem =currentItem;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.vcard_info_city_dialog);

		/**
		 * 初始化时 开始解析xml，获取省份集合
		 */
		provinceList = getXmlData();
		np1 = (NumberPicker) findViewById(R.id.np_city);
		//给省份数组赋值
		provinceArrays = getProvinceArray();
		np1.setDisplayedValues(provinceArrays);
		np1.setMinValue(0);  
		np1.setMaxValue(provinceArrays.length - 1);
		np1.setValue(0);
		
		np2 = (NumberPicker) findViewById(R.id.np_city2);
		//初始化城市numberpicker
		initCityData(0);
		
		np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				initCityData(newVal);
			}
		});
		
//		 设置返回按钮事件和文本
		if (backButtonText != null) {
			Button bckButton = ((Button)findViewById(R.id.btn_citydialog_cancle));
			bckButton.setText(backButtonText);

			if (backButtonClickListener != null) {
				bckButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						backButtonClickListener.onClick(new CityDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_citydialog_cancle).setVisibility(View.GONE);
		}

		// 设置确定按钮事件和文本
		if (confirmButtonText != null) {
			Button cfmButton = ((Button)findViewById(R.id.btn_citydialog_confirm));
			cfmButton.setText(confirmButtonText);

			if (confirmButtonClickListener != null) {
				cfmButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						confirmButtonClickListener.onClick(new CityDialog(getContext()), DialogInterface.BUTTON_NEGATIVE);
						dismiss();
					}
				});
			}
		} else {
			findViewById(R.id.btn_citydialog_confirm).setVisibility(View.GONE);
		}
	}

	  private void initCityData(int provinceId) {
		  List<CityModel> cities= provinceList.get(provinceId).getCityList();
	        if (cities != null && cities.size() > 0) {
	            try {
	                int min = 0;
	                int max = cities.size() -1;
	                
	                cityNames = new String[cities.size()];
	                for (int i = 0; i < cityNames.length; i++) {
	                    cityNames[i] = cities.get(i).getName();
	                }
	                int maxV = np2.getMaxValue();
	                if (max> maxV){ 
	                	np2.setMinValue(min);
	                	np2.setValue(0);
	                	np2.setDisplayedValues(cityNames);
	                	np2.setMaxValue(max);
	                }else{
	                	np2.setMinValue(min);
	                	np2.setValue(0);
	                	np2.setMaxValue(max);
	                	np2.setDisplayedValues(cityNames);
	                }

	            } catch (NumberFormatException e) {
	            	e.printStackTrace();
	            }
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
		return provinceArrays[np1.getValue()]+" "+cityNames[np2.getValue()];
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	
	/**
	 * 解析xml
	 * @return 省份ProvinceModel的list集合
	 */
	public List<ProvinceModel> getXmlData()
	{
		List<ProvinceModel> provinceList = null;
    	AssetManager asset = context.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
        } catch (Throwable e) {  
            e.printStackTrace();  
        } finally {} 
        return provinceList;
	}
	
	/**
	 * 将集合中的省转换为数组
	 */
	public String[] getProvinceArray(){
		String[] provinceArray = new String[provinceList.size()] ;
		for (int i = 0; i < provinceList.size(); i++) {
			provinceArray[i] = provinceList.get(i).getName();
		}
		return provinceArray;
	}
}
