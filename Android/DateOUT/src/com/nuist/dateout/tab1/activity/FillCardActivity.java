package com.nuist.dateout.tab1.activity;

import org.jivesoftware.smack.XMPPConnection;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dialog.AgeDialog;
import com.nuist.dialog.CityDialog;
import com.nuist.dateout.R;

import edu.nuist.dateout.activity.LoginActivity;
import edu.nuist.dateout.activity.RegistActivity;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.util.VCardAssit;

/**
 * 注册之后完善用户资料页面
 * 
 * @author Xin
 *
 */
public class FillCardActivity extends Activity implements OnClickListener
{
    
    private RadioGroup sexRadioGroup;
    
    private EditText nickNameedEditText;
    
    private TextView ageValuetextTextView;
    
    private TextView cityValueTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fill_card_activity);
        super.onCreate(savedInstanceState);
        
        initViews();
    }
    
    /** 初始化组件 */
    private void initViews()
    {
        nickNameedEditText = (EditText)findViewById(R.id.et_fillvacrd_nickname);
        sexRadioGroup = (RadioGroup)findViewById(R.id.rg_sex);
        
        View ageView = findViewById(R.id.lo_fillvcard_age);
        Button commitBtn = (Button)findViewById(R.id.btn_fill_card_commit);
        Button ignoreBtn = (Button)findViewById(R.id.btn_fill_card_ignore);
        ageValuetextTextView = (TextView)findViewById(R.id.tv_fillvcard_age_value);
        View cityView = findViewById(R.id.lo_fillvcard_city);
        cityValueTextView = (TextView)findViewById(R.id.tv_fillvcard_city_value);
        
        commitBtn.setOnClickListener(this);
        ageView.setOnClickListener(this);
        ignoreBtn.setOnClickListener(this);
        cityView.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_fill_card_commit:
                commitCard();
                break;
            case R.id.lo_fillvcard_age:
                fillAge();
                break;
            case R.id.btn_fill_card_ignore:
                ignore();
                break;
            case R.id.lo_fillvcard_city:
                fillCity();
                break;
            default:
                break;
        }
    }
    
    /** 填写城市信息 */
    private void fillCity()
    {
        editCityValue();
    }
    
    /** 点击忽略按钮后，跳转到登陆界面 */
    private void ignore()
    {
        // 跳转到登录页面
        Intent intent = new Intent();
        intent.setClass(FillCardActivity.this, LoginActivity.class);
        // intent.putExtra("USER_ID_REGIST", username);// 将此处的regName放入intent之中,然后在LoginActivity里面取出来使用
        startActivity(intent);
    }
    
    /** 选择年龄 */
    private void fillAge()
    {
        EditAgeVlaue();
    }
    
    /** 提交信息资料 */
    private void commitCard()
    {
        // 上传用户名片信息
        String sex = "男";
        int sexId = sexRadioGroup.getCheckedRadioButtonId();
        if (sexId == R.id.rb_woman)
        {
            sex = "女";
        }
        String nickname = nickNameedEditText.getText().toString();
        String birthday = ageValuetextTextView.getText().toString();
        String birthplace = cityValueTextView.getText().toString();
        
        DateoutApp app = DateoutApp.getInstance();
        // 上传用户资料
        CustomVcard myCard = new CustomVcard();
        myCard.setNickName(nickname);
        myCard.setGender(sex);
        myCard.setBirthDay(birthday);
        myCard.setBirthPlace(birthplace);
        
        XMPPConnection con = app.getConnection();
        if (con.isConnected() && con.isAuthenticated())
        {
            // 此时的connection对象是在RegistActivity中登录验证过的
            new VCardAssit(app.getConnection()).uploadMyVCard(myCard);
            // 断开连接
            app.getConnection().disconnect();
            //
            Toast.makeText(FillCardActivity.this, "资料上传成功", Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.v("Dateout", "FillCardActivity==>XMPPConnction对象未连接或者未认证");
        }
        
        // 跳转到登录页面
        Intent intent = new Intent();
        intent.setClass(FillCardActivity.this, LoginActivity.class);
        startActivity(intent);// 用户ID信息传递已在RegisterActivity实现
        
        RegistActivity.instance.finish();
        finish();
    }
    
    /** 弹出dialog，编辑用户出生日期 */
    private void EditAgeVlaue()
    {
        String currentValue = ageValuetextTextView.getText().toString();
        if (currentValue == "")
        {
            currentValue = "1993-01-01";
        }
        AgeDialog dialog = new AgeDialog(this, currentValue);
        dialog.setTitle("设置生日");
        dialog.setBackButton("取消", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        dialog.setConfirmButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ageValuetextTextView.setText(AgeDialog.getDate());
                // birthday = ageValuetextTextView.getText().toString();
            }
        });
        dialog.show();
    }
    
    /** 弹出dialog，选择城市 */
    private void editCityValue()
    {
        CityDialog dialog = new CityDialog(this);
        dialog.setTitle("设置城市");
        dialog.setBackButton("取消", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        dialog.setConfirmButton("确定", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                cityValueTextView.setText(CityDialog.getData());
                // birthplace = CityDialog.getData();
            }
        });
        
        dialog.show();
    }
}
