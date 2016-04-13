package com.nuist.dateout.tab4.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.nuist.dateout.R;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.task.FetchDownLinkTask;
import edu.nuist.dateout.util.NetworkAssit;
import edu.nuist.dateout.util.TimeAssit;
import edu.nuist.dateout.util.VCardAssit;
import edu.nuist.dateout.value.VariableHolder;

public class PersonalCardActivity extends Activity implements OnClickListener
{
    private DateoutApp app;
    
    private TextView nickNametTextView;// 昵称
    
    private TextView ageHeightWeightTextView;// 年龄身高体重
    
    private TextView moodieTextView;// 个性签名
    
    private TextView cityTextView;// 城市
    
    private ImageView sexImageView;// 性别
    
    private String userId;// 用户ID
    
    private NetworkImageIndicatorView netIndicatorView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_card_activity);
        
        app = DateoutApp.getInstance();
        userId = app.getLoginUser().getUserId();
        
        initView();
        initViewValues();
    }
    
    private void initView()
    {
        netIndicatorView = (NetworkImageIndicatorView)findViewById(R.id.iiv_personal_card_image);// 滑动显示照片的控件
        
        nickNametTextView = (TextView)findViewById(R.id.tv_personal_card_name);// 昵称
        cityTextView = (TextView)findViewById(R.id.tv_personal_card_city);// 城市
        moodieTextView = (TextView)findViewById(R.id.tv_personal_card_moodie);// 个性签名
        ageHeightWeightTextView = (TextView)findViewById(R.id.tv_personal_card_ageHeightWeight);// 年龄身高体重
        sexImageView = (ImageView)findViewById(R.id.iv_personal_card_sex);// 性别
        
        ImageView cardEditImage = (ImageView)findViewById(R.id.iv_personal_card_edit);// 编辑按钮
        cardEditImage.setOnClickListener(this);
    }
    
    private void initViewValues()
    {
        // 加载上部6张图片的下载链接
        String requestStr = VariableHolder.FILE_PREFIX_IMAGE_VCARD + userId;
        if (new NetworkAssit(PersonalCardActivity.this).isNetworkConnected())
        {
            new FetchDownLinkTask(requestStr, urlFetchHandler).execute();
        }
        else
        {
            Toast.makeText(PersonalCardActivity.this, "请先检测网络", Toast.LENGTH_LONG).show();
        }
        
        // 加载个人名片
        CustomVcard myCard;
        if (new NetworkAssit(PersonalCardActivity.this).isNetworkConnected())
        {
            // 加载VCard
            myCard = new VCardAssit(app.getConnection()).loadMyVCard(userId);
        }
        else
        {
            // 读取内存缓存的值
            myCard = app.getLoginUserVcard();
        }
        if (myCard != null)
        {
            // 刷新控件的值
            String nickName = myCard.getNickName();
            String moodie = myCard.getMoodie();
            int age = TimeAssit.birthday2Age(myCard.getBirthDay());
            String height = myCard.getHeight();
            String weight = myCard.getWeight();
            String city = myCard.getBirthPlace();
            String sex = myCard.getGender();
            
            if (nickName.equals(""))
            {
                nickName = userId;
            }
            nickNametTextView.setText(nickName);
            
            if (city.equals(""))
            {
                city = "请设置城市";
            }
            cityTextView.setText(city);
            
            if (moodie.equals(""))
            {
                moodie = "请设置签名";
            }
            moodieTextView.setText(moodie);
            
            String displayStr = "" + age + "岁/" + height + "/" + weight;
            ageHeightWeightTextView.setText(displayStr);
            
            if (sex.equals("男"))
            {
                sexImageView.setImageResource(R.drawable.i_user_sex_man);
            }
            else if (sex.equals("女"))
            {
                sexImageView.setImageResource(R.drawable.i_user_sex_women);
            }
            else
            {
                sexImageView.setImageResource(R.drawable.i_user_sex_notset);
            }
        }
    }
    
    // 处理用户资料卡页面顶部6张图片的下载链接的获取结果
    private Handler urlFetchHandler = new Handler()
    {
        public void handleMessage(android.os.Message osMsg)
        {
            switch (osMsg.what)
            {
                case 1:
                    List<String> downloadLinksList = (List<String>)osMsg.obj;
                    netIndicatorView.setupLayoutByImageUrl(downloadLinksList);
                    netIndicatorView.show();
                    break;
                default:
                    break;
            }
        };
    };
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_personal_card_edit:
                jumpToVCardEditActivity();
                break;
            default:
                break;
        }
    }
    
    /**
     * 跳转到个人信息编辑界面
     */
    private void jumpToVCardEditActivity()
    {
        Intent intent = new Intent(PersonalCardActivity.this, EditVcardActivity.class);
        startActivity(intent);
    }
}
