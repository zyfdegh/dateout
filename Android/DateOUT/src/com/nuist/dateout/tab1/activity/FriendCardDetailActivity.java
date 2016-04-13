package com.nuist.dateout.tab1.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.nuist.dateout.R;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.task.FetchDownLinkTask;
import edu.nuist.dateout.util.TimeAssit;
import edu.nuist.dateout.util.VCardAssit;
import edu.nuist.dateout.value.VariableHolder;

public class FriendCardDetailActivity extends Activity
{
    
    private String userDetailId;
    
    private NetworkImageIndicatorView netIndicatorView;
    
    private TextView nickNametTextView;// 昵称
    
    private TextView ageHeightWeightTextView;// 年龄身高体重
    
    private TextView moodieTextView;// 个性签名
    
    private TextView cityTextView;// 城市
    
    private ImageView sexImageView;// 性别
    
    private TextView emotionVlaueTextView;// 情感状态
    
    private TextView mailTextValue;// 邮箱
    
    private TextView professionTextValue;// 职业
    
    private DateoutApp app;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_card_detail_activity);
        
        app = DateoutApp.getInstance();
        userDetailId = getIntent().getStringExtra("userid_detail");
        
        initView();
        
        if (userDetailId!=null)
        {
            initViewValues();
        }
    }
    
    private void initView()
    {
        // 加载上部6张图片的下载链接
        netIndicatorView = (NetworkImageIndicatorView)findViewById(R.id.iiv_friend_card_image);// 滑动显示照片的控件
        nickNametTextView = (TextView)findViewById(R.id.tv_friend_card_nickname);// 昵称
        cityTextView = (TextView)findViewById(R.id.tv_friend_card_city);// 城市
        moodieTextView = (TextView)findViewById(R.id.tv_friend_card_moodie);// 个性签名
        ageHeightWeightTextView = (TextView)findViewById(R.id.tv_friend_card_ageheightweight);// 年龄身高体重
        sexImageView = (ImageView)findViewById(R.id.iv_friendcard_sex);// 性别
        emotionVlaueTextView = (TextView)findViewById(R.id.tv_vcard_emotion_value);// 情感状态
        professionTextValue = (TextView)findViewById(R.id.tv_vcard_profession_value);// 职业
        mailTextValue = (TextView)findViewById(R.id.tv_vcard_mail_value);// 邮箱
        
        Button moreInfoButton = (Button)findViewById(R.id.btn_friend_card_addfriend);
        moreInfoButton.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                findViewById(R.id.lo_more_info).setVisibility(View.VISIBLE);
            }
        });
    }
    
    private void initViewValues()
    {
        // 加载上部6张图片的下载链接
        String requestStr = VariableHolder.FILE_PREFIX_IMAGE_VCARD + userDetailId;
        new FetchDownLinkTask(requestStr, urlFetchHandler).execute();
        
        // 加载VCard
        CustomVcard myCard = new VCardAssit(app.getConnection()).loadMyVCard(userDetailId);
        if (myCard == null)
        {
            // 读取本地缓存的值
            myCard = app.getLoginUserVcard();
        }
        // 刷新控件的值
        String nickName = myCard.getNickName();
        String moodie = myCard.getMoodie();
        int age = TimeAssit.birthday2Age(myCard.getBirthDay());
        String height = myCard.getHeight();
        String weight = myCard.getWeight();
        String city = myCard.getBirthPlace();
        String sex = myCard.getGender();
        String singleStatus = myCard.getSingleState();
        String email = myCard.getEmail();
        String job = myCard.getJob();
        
        if (nickName == null || nickName.equals(""))
        {
            nickName = userDetailId;
        }
        nickNametTextView.setText(nickName);
        
        if (city == null || city.equals(""))
        {
            city = "城市";
        }
        cityTextView.setText(city);
        
        if (moodie == null || moodie.equals(""))
        {
            moodie = "TA没有写个性签名";
        }
        moodieTextView.setText(moodie);
        
        if (singleStatus == null || singleStatus.equals(""))
        {
            singleStatus = "未设置";
        }
        emotionVlaueTextView.setText(singleStatus);
        
        if (job == null || job.equals(""))
        {
            job = "未设置";
        }
        professionTextValue.setText(job);
        
        if (email == null || email.equals(""))
        {
            email = "未设置";
        }
        mailTextValue.setText(email);
        
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
            sexImageView.setImageResource(R.drawable.i_user_sex_man);
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
}
