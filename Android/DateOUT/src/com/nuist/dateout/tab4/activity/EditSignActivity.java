package com.nuist.dateout.tab4.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.nuist.dateout.R;

public class EditSignActivity extends Activity implements OnClickListener{
	

	private EditText signTextView;
	private Intent intent;
	private String signValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vcard_info_sign_activity);
		
		initView();
	}

	private void initView() {
		intent = getIntent();
		signTextView = (EditText) findViewById(R.id.et_vcard_sign);
		signValue = intent.getExtras().get("sign").toString();
		if (signValue!=null)
        {
		    signTextView.setText(signValue);
        }
		
		TextView completeView =  (TextView) findViewById(R.id.btn_vacard_sign_complete);
		completeView.setOnClickListener(this);
		TextView cancleView  = (TextView) findViewById(R.id.tv_vacard_sign_back);
		cancleView.setOnClickListener(this);
	}
	
	@Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_vacard_sign_complete) {
            //activity结束时回传数据
            intent.putExtra("editsign", signTextView.getText().toString());
            setResult(200, intent);
            finish();//返回EditVcardActivity
        }
        if (v.getId()==R.id.tv_vacard_sign_back) {
            intent.putExtra("editsign", signValue);
            setResult(201, intent);
            finish();
        }
    }
	
	@Override
	public void onBackPressed()
	{
	    super.onBackPressed();
	    intent.putExtra("editsign", signValue);
        setResult(201, intent);
        finish();
	}
}
