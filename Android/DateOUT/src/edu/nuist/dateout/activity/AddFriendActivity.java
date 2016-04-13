package edu.nuist.dateout.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nuist.dateout.R;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.util.OnlineStatusAssit;
import edu.nuist.dateout.value.VariableHolder;

public class AddFriendActivity extends Activity
{
    DateoutApp app;
    
    private EditText friendIdEditText;
    
    private Button addFriendOkButton;
    
    private List<String> friendIdList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// ***************
        
        setContentView(R.layout.act_addfriend);
        
        app = (DateoutApp)this.getApplication();
        
        if (app.getConnection() == null)
        {
            Log.v("Dateout", "AddFriendActivity==>" + "connection为空");
        }
        
        friendIdList = new ArrayList<String>();
        for (int i = 0; i < app.getFriendList().size(); i++)
        {
            friendIdList.add(app.getFriendList().get(i).getUserId());
        }
        
        friendIdEditText = (EditText)findViewById(R.id.et_addfriend_userid);
        addFriendOkButton = (Button)findViewById(R.id.btn_addfriend_ok);
        
        addFriendOkButton.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                String userIdToAdd = friendIdEditText.getText().toString().trim();
                
                String senderJid = app.getConnection().getUser();
                String senderId = senderJid.substring(0, senderJid.indexOf('@'));
                
                if (userIdToAdd.equals(senderId))
                {
                    // 添加自己为好友
                    Log.v("Dateout", "AddFriendActivity==>" + "用户尝试添加自己为好友");
                    Toast.makeText(AddFriendActivity.this, "你自己就是" + userIdToAdd + "哦~", Toast.LENGTH_LONG).show();
                }
                else if (isAlreadyMyFriend(userIdToAdd))
                {
                    Log.v("Dateout", "AddFriendActivity==>" + "用户已经是自己好友");
                    Toast.makeText(AddFriendActivity.this, "用户" + userIdToAdd + "已经是你的好友了哦~", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // 涉及网络操作,使用异步执行
                    SearchUserTask task = new SearchUserTask(AddFriendActivity.this, userIdToAdd, senderId);
                    task.execute();
                }
            }
        });
    }
    
    /**
     * 添加好友 无分组
     * 
     * @param roster
     * @param userName
     * @param name
     * @return
     */
    public static boolean addUser(Roster roster, String userName, String name)
    {
        try
        {
            roster.createEntry(userName, name, null);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 用于判断某用户是否已经是自己好友
     * 
     * @param userId 用户名
     * @return true表示userId已经是自己的好友
     */
    boolean isAlreadyMyFriend(String userId)
    {
        // 从静态类中获取好友列表,然后检测用户Id是否在里面
        if (friendIdList.contains(userId))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    class SearchUserTask extends AsyncTask<String, Integer, Integer>
    {
        private ProgressDialog pd;
        
        private Context context;
        
        private String userId;
        
        private String senderId;
        
        public SearchUserTask(Context context, String userToAddId, String senderId)
        {
            this.context = context;
            this.userId = userToAddId;
            this.senderId = senderId;
        }
        
        @Override
        protected void onPreExecute()
        {
            // 显示圈圈
            pd = new ProgressDialog(context);
            pd.setTitle("正在查找用户");
            pd.setMessage("请稍候");
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
            pd.show();
            super.onPreExecute();
        }
        
        @Override
        protected Integer doInBackground(String... params)
        {
            return checkUserStatus(userId);
        }
        
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            
        }
        
        protected void onPostExecute(Integer result)
        {
            pd.dismiss();
            switch (result)
            {
                case 0:
                    Log.v("Dateout", "AddFriendActivity==>" + "查找的用户不存在");
                    Toast.makeText(AddFriendActivity.this, "找不到用户" + userId, Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Log.v("Dateout", "AddFriendActivity==>" + "用户存在");
                    // 发送好友添加请求
                    String serverDomain =DateoutApp.getInstance().getServiceName();
                    Presence presence = new Presence(Presence.Type.subscribe);
                    presence.setFrom(senderId + "@" + serverDomain);
                    presence.setTo(userId + "@" + serverDomain);
                    app.getConnection().sendPacket(presence);
                    Toast.makeText(AddFriendActivity.this, "请求已发出", Toast.LENGTH_LONG).show();
                    Log.v("Dateout", "AddFriendActivity==>" + "请求已发出" + userId);
                    finish();
                    break;
                default:
                    break;
            }
        }
        
        /**
         * 查询用户状态,返回在线离线不存在三种结果
         * 
         * @param userId 用户ID
         * @return 用户状态,1表示存在,0表示不存在
         */
        Integer checkUserStatus(String userId)
        {
            // TODO 放到后台线程进行处理
            OnlineStatusAssit judge = new OnlineStatusAssit();
            short status = judge.getUserOnlineStatus(userId + "@" + DateoutApp.getInstance().getServiceName());
            if (status == VariableHolder.STAT_NOT_EXIST)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }
    
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}