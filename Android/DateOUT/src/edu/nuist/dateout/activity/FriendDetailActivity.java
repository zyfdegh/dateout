package edu.nuist.dateout.activity;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dateout.R;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.util.VCardAssit;

public class FriendDetailActivity extends Activity
{
    private TextView idTextView;// 用户Id
    
    private TextView nickNameTextView;// 昵称
    
    private TextView moodieTextView;// 个性签名
    
    private ImageView userHeadImageView;// 图片-选取头像
    
    private TextView distanceTextView;// 距离
    
    private Button btnDeleteFriend;// 删除按钮
    
    private String userId;
    
    private DateoutApp app;
    
    private void initView()
    {
        userHeadImageView = (ImageView)findViewById(R.id.iv_detail_headimage);
        idTextView = (TextView)findViewById(R.id.tv_detail_userid);
        nickNameTextView = (TextView)findViewById(R.id.tv_detail_nickname);
        moodieTextView = (TextView)findViewById(R.id.tv_detail_moodie);
        distanceTextView = (TextView)findViewById(R.id.tv_detail_distance);
        btnDeleteFriend = (Button)findViewById(R.id.btn_detail_deletefriend);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_vcard);
        app = (DateoutApp)this.getApplication();
        
        initView();
        
        userId = this.getIntent().getStringExtra("USER_CHAT");
        // 设置文本信息
        idTextView.setText(userId);
        CustomVcard myCard = new VCardAssit(app.getConnection()).loadMyVCard(userId);
        if (myCard != null)
        {
            nickNameTextView.setText(myCard.getNickName());
            moodieTextView.setText(myCard.getMoodie());
            // TODO 获取指定用户经纬度坐标,并计算距离
            distanceTextView.setText("500m");
        }
        
        // TODO 更改这里的用户头像为最新头像(获取服务端带MD5的文件名,对比本地缓存文件)
        userHeadImageView.setImageResource(R.drawable.default_head);
        btnDeleteFriend.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendDetailActivity.this);
                builder.setTitle("确定要删除该好友？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        // 点击确定后
                        if (app.getRoster() == null)
                        {
                            System.out.println("getRoster返回空");
                        }
                        else
                        {
                            if (app.getChatUser().getUserId() == null)
                            {
                                System.out.println("getUserChatting返回空");
                            }
                            else
                            {
                                deleteFriend(app.getRoster(), app.getChatUser().getUserId());
                                Toast.makeText(FriendDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                Log.v("Dateout", "FriendDetailActivity==>" + "删除了好友" + userId);
                                
                                // 跳转到好友列表
                                Intent intent = new Intent();
                                intent.setClass(FriendDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        // 点击取消后
                        System.out.println("点击了取消");
                    }
                });
                builder.create().show();
            }
        });
    }
    
    private boolean deleteFriend(Roster roster, String userId)
    {
        try
        {
            RosterEntry entry = roster.getEntry(userId + "@" + DateoutApp.getInstance().getServiceName());
            roster.removeEntry(entry);
            return true;
        }
        catch (XMPPException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
