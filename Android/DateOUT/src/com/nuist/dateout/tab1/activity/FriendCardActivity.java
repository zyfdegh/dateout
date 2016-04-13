package com.nuist.dateout.tab1.activity;

import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.nuist.dateout.R;

import edu.nuist.dateout.activity.MainActivity;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.task.FetchDownLinkTask;
import edu.nuist.dateout.util.TimeAssit;
import edu.nuist.dateout.util.VCardAssit;
import edu.nuist.dateout.value.VariableHolder;

public class FriendCardActivity extends Activity implements OnClickListener
{
    private String userToAddId;
    
    private DateoutApp app;
    
    private TextView nickNametTextView;// 昵称
    
    private TextView ageHeightWeightTextView;// 年龄身高体重
    
    private TextView moodieTextView;// 个性签名
    
    private TextView cityTextView;// 城市
    
    private ImageView sexImageView;// 性别
    
    private NetworkImageIndicatorView netIndicatorView;
    
    int gameTime;// 游戏用时
    
    int gameRetryTimes;// 游戏重试次数
    
    int gameSteps;// 游戏步数
    
    int gameColumn;// 游戏格数
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_card_activity);
        
        app = DateoutApp.getInstance();
        userToAddId = getIntent().getStringExtra("userid_game");
        gameTime = getIntent().getIntExtra("game_time", 0);
        gameRetryTimes = getIntent().getIntExtra("game_retry_times", 0);
        gameSteps = getIntent().getIntExtra("game_steps", 0);
        gameColumn = getIntent().getIntExtra("game_colum", 0);
        
        initView();
        initViewValues();
    }
    
    private void initView()
    {
        // 加载上部6张图片的下载链接
        String requestStr = VariableHolder.FILE_PREFIX_IMAGE_VCARD + userToAddId;
        new FetchDownLinkTask(requestStr, urlFetchHandler).execute();
        
        netIndicatorView = (NetworkImageIndicatorView)findViewById(R.id.iiv_friend_card_image);// 滑动显示照片的控件
        
        nickNametTextView = (TextView)findViewById(R.id.tv_friend_card_nickname);// 昵称
        cityTextView = (TextView)findViewById(R.id.tv_friend_card_city);// 城市
        moodieTextView = (TextView)findViewById(R.id.tv_friend_card_moodie);// 个性签名
        ageHeightWeightTextView = (TextView)findViewById(R.id.tv_friend_card_ageheightweight);// 年龄身高体重
        sexImageView = (ImageView)findViewById(R.id.iv_friendcard_sex);// 性别
        
        Button btnAddFriend = (Button)findViewById(R.id.btn_friend_card_addfriend);
        btnAddFriend.setOnClickListener(this);
    }
    
    private void initViewValues()
    {
        // 加载VCard
        CustomVcard myCard = new VCardAssit(app.getConnection()).loadMyVCard(userToAddId);
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
        
        if (nickName.equals(""))
        {
            nickName = userToAddId;
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
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_friend_card_addfriend:
                addFriend();
                break;
            default:
                break;
        }
    }
    
    void addFriend()
    {
        // TODO 直接添加为好友,并发送游戏结果信息给对方
        Roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
        
        // 发送好友添加请求
        String senderId = app.getLoginUser().getUserId();
        String serverDomain = DateoutApp.getInstance().getServiceName();
        Presence presence = new Presence(Presence.Type.subscribe);
        presence.setFrom(senderId + "@" + serverDomain);
        presence.setTo(userToAddId + "@" + serverDomain);
        app.getConnection().sendPacket(presence);
        Toast.makeText(FriendCardActivity.this, "已添加" + userToAddId + "为好友", Toast.LENGTH_LONG).show();
        Log.v("Dateout", "AddFriendActivity==>" + "已添加" + userToAddId + "为好友");
        
        // 弹出消息,发送一条消息给对方
        
        ChatManager chatManager = app.getConnection().getChatManager();
        // 消息接收者Jid
        String receiverJid = userToAddId + "@" + DateoutApp.getInstance().getServiceName();
        Chat chat = chatManager.createChat(receiverJid, null);
        
        String autoGenChatMsg =
            "你好呀,我解锁了你设置的" + gameColumn + "*" + gameColumn + "拼图游戏!" + "用时:" + gameTime + "秒,步数:" + gameSteps
                + "步,拼图次数:" + gameRetryTimes + "次。";
        if (gameRetryTimes <= 2)
        {
            // TODO 根据拼图次数不同，设置不同的消息
        }
        else if (gameRetryTimes <= 5)
        {
            
        }
        else if (gameRetryTimes <= 10)
        {
            
        }
        else
        {
            
        }
        
        try
        {
            chat.sendMessage(autoGenChatMsg);
        }
        catch (XMPPException e)
        {
            e.printStackTrace();
        }
        
        Intent intent = new Intent();
        intent.setClass(FriendCardActivity.this, MainActivity.class);
        startActivity(intent);
    }
    
}
