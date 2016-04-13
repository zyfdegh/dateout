package edu.nuist.dateout.tab;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.nuist.dateout.R;

import de.greenrobot.event.EventBus;
import edu.nuist.dateout.activity.ChatActivity;
import edu.nuist.dateout.activity.MainActivity;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.core.MessageAssit;
import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.model.CustomMsgPack;
import edu.nuist.dateout.model.RecentMsgItem;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.HeadImageAssit;
import edu.nuist.dateout.view.ListViewWithDelete;
import edu.nuist.dateout.view.RecentMsgAdapter;

/**
 * @author Veayo 消息回话页面管理器 用于显示最近联系人,聊天消息
 */
public class RecentMsgManager extends Fragment
{
    private DateoutApp app;
    
    private ListViewWithDelete listView;
    
    private Context context;
    
    private RecentMsgAdapter adapter;
    
    private ArrayList<RecentMsgItem> recentMsgList;
    
    private ImageView tab3ButtomReminder;//第三个标签下的未读消息提醒红点
    
    private int unreadMsgNumSum;//未读消息总数
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_3, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        app = DateoutApp.getInstance();
        
        unreadMsgNumSum=0;
        tab3ButtomReminder=(ImageView)getActivity().findViewById(R.id.iv_message_remind);
        tab3ButtomReminder.setVisibility(View.GONE);
        
        listView = (ListViewWithDelete)getActivity().findViewById(R.id.tab3_listview);
        
        /** 删除按钮的回调事件 */
        listView.setDelButtonClickListener(new ListViewWithDelete.DelButtonClickListener()
        {
            
            @Override
            public void clickHappend(int position)
            {
                String slideToDeleteUserId = recentMsgList.get(position).getUserId();
                for (int i = 0; i < recentMsgList.size(); i++)
                {
                    if (recentMsgList.get(i).getUserId().equals(slideToDeleteUserId))
                    {
                        //清除消息数目
                        unreadMsgNumSum-=recentMsgList.get(i).getUnReadMsgNum();
                        checkMsgReminder();
                        
                        // 删除列表中的项目
                        recentMsgList.remove(i);
                        // 更新视图
                        adapter.setRecentMsgList(recentMsgList);
                        adapter.notifyDataSetChanged();
                        
                        //删除数据库中的会话记录
                        DBAssit dbAssit=new DBAssit();
                        if (!dbAssit.isDbConnected())
                        {
                            dbAssit.connectDb();
                        }
                        dbAssit.deleteRecentChatListBetween(app.getLoginUser().getUserId(),slideToDeleteUserId);
                        dbAssit.closeDbConnect();
                    }
                }
                // 从列表上删除该用户
            }
        });
        // 注册tab改变额监听
        initTabChangeListener();
        
        // 注册EventBus事件订阅者
        EventBus.getDefault().register(this);
        
        //从数据库加载最近联系人
        recentMsgList = getRecentMsgList();
        
        adapter = new RecentMsgAdapter(context, recentMsgList);
        
        listView.setAdapter(adapter);
        
        // 注册消息监听器
        MessageAssit msgAssit = new MessageAssit(getActivity());
        msgAssit.work();
        
        // 列表中的项目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View rowView, int position, long id)
            {
                // 先获取被点击的用户名
                RecentMsgItem recentMsgItem = (RecentMsgItem)listView.getItemAtPosition(position);
                String userIdClickedToChat = recentMsgItem.getUserId();// 存储好友列表中被点击的用户用户id
                
                //更新未读消息总数
                unreadMsgNumSum-=recentMsgList.get(position).getUnReadMsgNum();
                checkMsgReminder();
                
                // 将该用户未读消息数目置为空
                recentMsgList.get(position).setUnReadMsgNum(0);
                // 刷新列表
                adapter.setRecentMsgList(recentMsgList);
                adapter.notifyDataSetChanged();
                // 插入记录到数据库
                insertRecentMsgItemToDb(recentMsgList.get(position));
                
                // 设置点击的用户Id和头像到全局变量,以便ChatActivity提取
                app.getChatUser().setUserId(userIdClickedToChat);
                app.getChatUser().setHeadImageUri(recentMsgItem.getUserHeadUri());
                
                // 跳转到ChatActivity
                Intent intent = new Intent();
                intent.setClass(context, ChatActivity.class);
                startActivity(intent);
                app.setChatActivityIsEntered(true);
            }
        });
    }
    
    private void checkMsgReminder(){
        if (unreadMsgNumSum>0)
        {
            tab3ButtomReminder.setVisibility(View.VISIBLE);
        }else {
            tab3ButtomReminder.setVisibility(View.GONE);
        }
    }
    
    private void initTabChangeListener()
    {
        MainActivity.instance.SetOnTabChangeListener(new MainActivity.TabChangeListener()
        {
            
            @Override
            public void clickTab(int tab)
            {
                if (tab == 2)
                {
                    return;
                }
                else
                {
                    listView.dismissPopWindow();
                }
            }
        });
    }
    
    /**
     * EventBus消息订阅者 接收来自MessageAssit发来的消息包
     * 
     * @param msgPack event
     */
    public void onEvent(CustomMsgPack msgPack)
    {
        refreshMsgListView(msgPack);
    }
    
    /**
     * EventBus消息订阅者 接收来自ChatActivity发来的消息包
     * 
     * @param recentMsgItem event
     */
    public void onEvent(RecentMsgItem recentMsgItem)
    {
        refreshMsgListView(recentMsgItem);
    }
    
    private void refreshMsgListView(RecentMsgItem recentMsgItem)
    {
        
        // 遍历整个好友列表,获取发消息用户所在List中的位置
        int index = -1;
        for (int i = 0; i < recentMsgList.size(); i++)
        {
            if (recentMsgList.get(i).getUserId().equals(recentMsgItem.getUserId()))
            {
                index = i;
                break;
            }
        }
        
        // 如果最近联系人列表中不存在该这个人,则新建
        if (index == -1)
        {
            // 将新用户加到列表最前面
            recentMsgList.add(0, recentMsgItem);
            adapter.setRecentMsgList(recentMsgList);
            adapter.notifyDataSetChanged();
            // 插入记录到数据库
            insertRecentMsgItemToDb(recentMsgItem);
            index = 0;
        }
        // 重新设置消息数目
        recentMsgList.set(index, recentMsgItem);
        adapter.setRecentMsgList(recentMsgList);
        adapter.notifyDataSetChanged();
        // 插入记录到数据库
        insertRecentMsgItemToDb(recentMsgItem);
    }
    
    private void refreshMsgListView(CustomMsgPack msgPack)
    {
        // 获取发消息用户的用户名
        String senderId = msgPack.getSenderId();
        String msgBody = msgPack.getMsgBody();
        String dateAndTime = msgPack.getDateAndTime();
        short msgType = msgPack.getMsgType();
        
        // 遍历整个好友列表,获取发消息用户所在List中的位置
        int index = -1;
        for (int i = 0; i < recentMsgList.size(); i++)
        {
            if (recentMsgList.get(i).getUserId().equals(senderId))
            {
                index = i;
                break;
            }
        }
        if (index == -1)
        {
            // 最近消息列表中不存在该用户,则新建Item,添加到列表并更新视图
            RecentMsgItem recentMsgItem = new RecentMsgItem();
            recentMsgItem.setUserId(senderId);// 将用户Id作为显示名
            recentMsgItem.setRemarkName(senderId);// TODO 将Id名改为用户备注名
            recentMsgItem.setLastMessage(msgBody);
            recentMsgItem.setMsgDateAndTime(dateAndTime);
            recentMsgItem.setMsgType(msgType);
            recentMsgItem.setUnReadMsgNum(0);
            
            // 先使用默认头像
            recentMsgItem.setUserHeadUri(FormatTools.resId2Uri(getResources(), R.drawable.default_head));
            // 读取缓存头像
            File headImageFile = new HeadImageAssit().getHeadImageFromCache(senderId);
            if (headImageFile != null && headImageFile.exists() && headImageFile.canRead())
            {
                // 使用缓存头像
                recentMsgItem.setUserHeadUri(Uri.fromFile(headImageFile));
            }
            else
            {
                //TODO 从服务端加载头像
            }
            recentMsgList.add(0, recentMsgItem);// 将新用户加到列表最前面
            adapter.setRecentMsgList(recentMsgList);
            adapter.notifyDataSetChanged();
            // 插入记录到数据库
            insertRecentMsgItemToDb(recentMsgItem);
            index = 0;
        }
        
        // ===消息未读数目设置
        // 如果是在ChatActivity之后就不更新未读消息数目了
        // TODO 这里貌似除了当前其他未在聊天的用户也不显示未读消息数目了
        if (!app.isChatActivityIsEntered())
        {
            // 获取该用户项目上当前未读消息数目,字符串型
            int currentUnreadMsgNum = recentMsgList.get(index).getUnReadMsgNum();
            // 重新设置消息数目
            recentMsgList.get(index).setUnReadMsgNum(++currentUnreadMsgNum);
            
            ++unreadMsgNumSum;
            checkMsgReminder();
        }
        
        // 设置astMessage为该用户发的消息
        recentMsgList.get(index).setLastMessage(msgBody);
        // 设置消息日期
        recentMsgList.get(index).setMsgDateAndTime(dateAndTime);
        // 设置消息类型图标
        recentMsgList.get(index).setMsgType(msgType);
        adapter.setRecentMsgList(recentMsgList);
        adapter.notifyDataSetChanged();
        // 插入记录到数据库
        insertRecentMsgItemToDb(recentMsgList.get(index));
    }
    
    private void insertRecentMsgItemToDb(RecentMsgItem item)
    {
        String loginId = app.getLoginUser().getUserId();
        DBAssit dbAssit = new DBAssit();
        if (!dbAssit.isDbConnected())
        {
            dbAssit.connectDb();
        }
        dbAssit.saveRecentMsgList(loginId, item);// 存储数据
        dbAssit.closeDbConnect();
    }
    
    /**
     * 从数据库获取最近联系人列表
     * @return
     */
    private ArrayList<RecentMsgItem> getRecentMsgList()
    {
        String loginId = app.getLoginUser().getUserId();
        ArrayList<RecentMsgItem> recentMsgItemList = new ArrayList<RecentMsgItem>();
        DBAssit dbAssit = new DBAssit();
        if (!dbAssit.isDbConnected())
        {
            dbAssit.connectDb();
        }
        recentMsgItemList = dbAssit.getRecentMsgList(loginId);// 存储数据
        // 设置用户头像Uri(并没有把用户头像Uri存储到数据库)
        for (int i = 0; i < recentMsgItemList.size(); i++)
        {
            // 到缓存查找用户头像
            File file = new HeadImageAssit().getHeadImageFromCache(recentMsgItemList.get(i).getUserId());
            if (file != null && file.exists() && file.canRead())
            {
                recentMsgItemList.get(i).setUserHeadUri(Uri.fromFile(file));
            }
            else
            {
                // 找不到头像,使用默认头像
                Uri defaultHeadUri = FormatTools.resId2Uri(getResources(), R.drawable.default_head);
                recentMsgItemList.get(i).setUserHeadUri(defaultHeadUri);
            }
            
            //顺便设置一下未读消息总数
            unreadMsgNumSum+=recentMsgItemList.get(i).getUnReadMsgNum();
        }
        checkMsgReminder();
        
        dbAssit.closeDbConnect();
        return recentMsgItemList;
    }
    
}
