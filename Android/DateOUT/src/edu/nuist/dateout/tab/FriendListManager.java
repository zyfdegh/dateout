package edu.nuist.dateout.tab;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dateout.R;
import com.nuist.dateout.tab1.activity.FriendCardDetailActivity;
import com.nuist.dialog.ListLongClickDialog;

import de.greenrobot.event.EventBus;
import edu.nuist.dateout.activity.AddFriendActivity;
import edu.nuist.dateout.activity.ChatActivity;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.model.DownloadResult;
import edu.nuist.dateout.model.EventOffline;
import edu.nuist.dateout.model.FriendItem;
import edu.nuist.dateout.task.DownloadAndCacheTask;
import edu.nuist.dateout.task.FetchFileNameTask;
import edu.nuist.dateout.task.VCardLoadTask;
import edu.nuist.dateout.test.PinyinComparator;
import edu.nuist.dateout.test.SideBar;
import edu.nuist.dateout.test.help;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.HeadImageAssit;
import edu.nuist.dateout.util.NetworkAssit;
import edu.nuist.dateout.util.OnlineStatusAssit;
import edu.nuist.dateout.util.VCardAssit;
import edu.nuist.dateout.value.VariableHolder;
import edu.nuist.dateout.view.DialogCenter;
import edu.nuist.dateout.view.FriendListAdapter;

public class FriendListManager extends Fragment implements SectionIndexer
{
    private DateoutApp app;
    
    private ListView listView;
    
    private Context context;
    
    private FriendListAdapter adapter;
    
    private Roster roster;
    
    private ImageButton addFriendButton;
    
    private SwipeRefreshLayout freshLayout;
    
    private ListLongClickDialog clickDialog;// 长按好友列表弹出dialog
    
    private SideBar sideBar;// 右侧字母选择表
    
    private TextView dialog;// 中间字母dialog
    
    private PinyinComparator pinyinComparator;// 汉字转拼音的类
    
    // private List<String> friendIdList;//存储好友列表中的用户名列表
    // TODO 横屏时候会重新加载Fragment,好友列表初始化的方式是从服务器拉取,所以每次横屏都会非常耗时
    private ArrayList<FriendItem> friendItemList = new ArrayList<FriendItem>();
    
    // 处理好友状态的变更
    private Handler friendStatUpdateHandler;
    
    private PacketListener packetListener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_2, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        
        initSipeRefresh();
        
        app = DateoutApp.getInstance();
        
        listView = (ListView)getActivity().findViewById(R.id.tab2_listview);
        addFriendButton = (ImageButton)getActivity().findViewById(R.id.btn_add_friend);
        
        // EventBus事件监听
        EventBus.getDefault().register(this);
        
        // friendIdList=new ArrayList<String>();//在getData()方法中赋值
        // app.setFriendList(getData());
        adapter = new FriendListAdapter(context, app.getFriendList());
        listView.setAdapter(adapter);
        
        fetchFriendListAndShow(null);
        
        // 注意好友列表更新时候也要更新friendIdList
        // app.setFriendIdList(friendIdList);
        
        // 默认情况下为SubscriptionMode.accept_all
        // TODO 改为自动接受
        Roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
        
        // 列表中的项目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            
            public void onItemClick(AdapterView<?> parent, View rowView, int position, long id)
            {
                
                FriendItem friendListItem = (FriendItem)listView.getItemAtPosition(position);
                String userClickedToChat = friendListItem.getUserId();// 存储好友列表中被点击的用户用户id
                
                // 跳转到ChatActivity
                Intent intent = new Intent();
                intent.setClass(context, ChatActivity.class);
                // 将此处的userToChat放入intent之中,然后在ChatActivity里面取出来使用
                // 设置全局聊天用户的Id和头像,以便ChatActivity中显示
                app.getChatUser().setUserId(userClickedToChat);
                app.getChatUser().setHeadImageUri(friendListItem.getHeadImageUri());
                
                startActivity(intent);
                app.setChatActivityIsEntered(true);
            }
        });
        
        // 列表项的长按事件
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener());
        
        // 添加好友按钮点击事件
        addFriendButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(context, AddFriendActivity.class);
                startActivity(intent);
            }
        });
        
        // 好友状态更新处理
        friendStatUpdateHandler = new Handler()
        {
            public void handleMessage(android.os.Message osMsg)
            {
                switch (osMsg.what)
                {
                // 好友状态变更处理方法
                    case VariableHolder.ROSTER_PRESENCE_CHANGED:
                        Presence presence = (Presence)osMsg.obj;
                        Type presenceType = presence.getType();
                        if (presenceType.equals(Presence.Type.available)
                            || presenceType.equals(Presence.Type.unavailable))
                        {
                            // 类型为用户上线通知
                            // 这里可以添加附加的状态信息,比如离开..正忙..通过presence.getMode()获取,这些都是available,而退出登录则是unavailable
                            String userJid = presence.getFrom();
                            String userId = userJid.substring(0, userJid.indexOf('@'));
                            if (presenceType.equals(Presence.Type.available))
                            {
                                Log.v("Dateout", "FriendListManager==>" + userId + " available");
                            }
                            else
                            {
                                Log.v("Dateout", "FriendListManager==>" + userId + " unavailable");
                            }
                            // 遍历整个好友列表,获取发消息用户所在List中的位置
                            int index = -1;
                            for (int i = 0; i < app.getFriendList().size(); i++)
                            {
                                if (app.getFriendList().get(i).getUserId().equals(userId))
                                {
                                    index = i;
                                    break;
                                }
                            }
                            if (index == -1)
                            {
                                // 添加好友成功后的处理方法
                                // 不在好友列表里的用户也可以发送Presence到当前用户,一般为别的人接收我的好友添加请求
                                // 用户不在列表中
                                
                                // 通过用户名获取用户FriendItem
                                CustomVcard myVCard = new VCardAssit(app.getConnection()).loadMyVCard(userId);
                                RosterEntry entry =
                                    app.getConnection()
                                        .getRoster()
                                        .getEntry(userId + "@" + DateoutApp.getInstance().getServiceName());
                                if (myVCard != null)
                                {
                                    // 新建列表项
                                    FriendItem item = new FriendItem();
                                    item.setUserId(userId);// 用户名
                                    String remarkName = entry.getName();
                                    if (remarkName == null || remarkName.equals(""))
                                    {
                                        item.setRemarkName(userId);// 备注名
                                    }
                                    else
                                    {
                                        item.setRemarkName(remarkName);// 备注名
                                    }
                                    String onlineStatus = OnlineStatusAssit.interpretPresenceType(presenceType);
                                    item.setOnlineStatus("[" + onlineStatus + "]");// 在线状态
                                    item.setExtendString(myVCard.getMoodie());// 签名
                                    
                                    // 先统统设置为缓存的头像
                                    File headImageFile = new HeadImageAssit().getHeadImageFromCache(userId);
                                    if (headImageFile != null)
                                    {
                                        // 加载本地缓存头像
                                        item.setHeadImageUri(Uri.fromFile(headImageFile));
                                    }
                                    else
                                    {
                                        // 加载默认头像
                                        Uri headImageUri =
                                            FormatTools.resId2Uri(getResources(), R.drawable.default_head);
                                        item.setHeadImageUri(headImageUri);
                                    }
                                    
                                    // 后台开始检测更新用户头像
                                    new FetchFileNameTask(VariableHolder.FILE_PREFIX_IMAGE_HEAD + userId,
                                        fileNameFetchHandler).execute();
                                    
                                    friendItemList.add(item);
                                    // 更新全局列表,更新视图
                                    sortList();
                                }
                            }
                            else
                            {
                                // 设置用户状态并更新视图
                                String onlineStatus = OnlineStatusAssit.interpretPresenceType(presence.getType());
                                if (index<0||index>friendItemList.size())
                                {
                                    friendItemList.get(index).setOnlineStatus("[" + onlineStatus + "]");
                                    
                                    // 更新全局变量,更新视图
                                    sortList();
                                    
                                    // 如果聊天页面已进入,且聊天的用户状态改变,则用EventBus发送消息到ChatActivity中
                                    if (app.isChatActivityIsEntered() && app.getChatUser().getUserId().equals(userId))
                                    {
                                        EventBus.getDefault().post(onlineStatus);
                                    }
                                }
                            }
                        }
                        else if (presenceType.equals(Presence.Type.error))
                        {
                            Log.v("Dateout", "FriendListManager==>" + "Presence error");
                        }
                        
                        break;
                    case VariableHolder.ROSTER_ENTRIES_UPDATED:
                        break;
                    case VariableHolder.ROSTER_ENTRIES_ADDED:
                        break;
                    // 删除了好友
                    case VariableHolder.ROSTER_ENTRIES_DELETED:
                        // 更新视图
                        Collection<String> collection = (Collection<String>)osMsg.obj;
                        Iterator<String> it = collection.iterator();
                        while (it.hasNext())
                        {
                            // it.next内容为被删除的用户的Jid
                            String userIdDeleted = it.next().split("@")[0];
                            // 从列表中删除并更新视图
                            for (int i = 0; i < friendItemList.size(); i++)
                            {
                                if (friendItemList.get(i).getUserId().equals(userIdDeleted))
                                {
                                    friendItemList.remove(i);
                                    app.setFriendList(friendItemList);
                                    adapter.setFriendList(friendItemList);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            // 删除与TA的聊天记录
                            DBAssit dbAssit = new DBAssit();
                            if (!dbAssit.isDbConnected())
                            {
                                dbAssit.closeDbConnect();
                            }
                            dbAssit.deleteTheirChatHistory(app.getLoginUser().getUserId(), userIdDeleted);
                            dbAssit.closeDbConnect();
                        }
                        break;
                    case VariableHolder.ROSTER_PRESENCE_SUBSCRIBE:
                        // 处理好友添加请求
                        if (osMsg.obj instanceof Presence)
                        {
                            final Presence precenceSubscribe = (Presence)osMsg.obj;
                            String fromId = precenceSubscribe.getFrom();
                            if (precenceSubscribe.getType().equals(Presence.Type.subscribe))
                            {
                                // 弹出处理好友请求对话框
                                new DialogCenter().popAcceptSubscribeDialog(context, fromId, app.getConnection());
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        
        // 用于监听好友添加请求
        packetListener = new PacketListener()
        {
            @Override
            public void processPacket(Packet packet)
            {
                Log.v("Dateout", "FriendListManager==>" + "有用户要加我");
                String myJid = app.getConnection().getUser();
                if (packet.getFrom().contains(myJid))
                    return;
                if (Roster.getDefaultSubscriptionMode().equals(SubscriptionMode.accept_all))
                {
                    Presence subscription = new Presence(Presence.Type.subscribe);
                    subscription.setTo(packet.getFrom());
                    app.getConnection().sendPacket(subscription);
                    Log.v("Dateout", "FriendListManager==>" + "已自动接受好友添加请求");
                }
                else
                {
                    // 将好友请求传送到前端处理
                    android.os.Message osMsg = friendStatUpdateHandler.obtainMessage();
                    osMsg.what = VariableHolder.ROSTER_PRESENCE_SUBSCRIBE;
                    osMsg.obj = packet;
                    osMsg.sendToTarget();
                }
            }
        };
        
        // Packet消息包过滤器
        PacketFilter filter = new PacketFilter()
        {
            @Override
            public boolean accept(Packet packet)
            {
                if (packet instanceof Presence)
                {
                    Presence presence = (Presence)packet;
                    if (presence.getType().equals(Presence.Type.subscribe))
                    {
                        return true;
                    }
                }
                return false;
            }
        };
        
        // 设置Packet监听
        app.getConnection().addPacketListener(packetListener, filter);
        
        roster.addRosterListener(new RosterListener()
        {
            
            @Override
            public void presenceChanged(Presence presence)
            {
                // 在handler里取出来显示消息
                android.os.Message osMsg = friendStatUpdateHandler.obtainMessage();
                osMsg.what = VariableHolder.ROSTER_PRESENCE_CHANGED;
                osMsg.obj = presence;
                osMsg.sendToTarget();
            }
            
            @Override
            public void entriesUpdated(Collection<String> collection)
            {
                // 有用户删除了我
                android.os.Message osMsg = friendStatUpdateHandler.obtainMessage();
                osMsg.what = VariableHolder.ROSTER_ENTRIES_UPDATED;
                osMsg.obj = collection;
                osMsg.sendToTarget();
                Log.v("Dateout", "FriendListManager==>" + "entriesUpdated");
            }
            
            @Override
            public void entriesDeleted(Collection<String> collection)
            {
                android.os.Message osMsg = friendStatUpdateHandler.obtainMessage();
                osMsg.what = VariableHolder.ROSTER_ENTRIES_DELETED;
                osMsg.obj = collection;
                osMsg.sendToTarget();
                Log.v("Dateout", "FriendListManager==>" + "entriesDeleted");
            }
            
            @Override
            public void entriesAdded(Collection<String> collection)
            {
                // 有用户要添加我
                android.os.Message osMsg = friendStatUpdateHandler.obtainMessage();
                osMsg.what = VariableHolder.ROSTER_ENTRIES_ADDED;
                osMsg.obj = collection;
                osMsg.sendToTarget();
                Log.v("Dateout", "FriendListManager==>" + "entriesAdded");
            }
        });
        initViews();
    }// onActivityCreated()结束
    
    /**
     * /**初始化组件 上次第一个可见元素，用于滚动时记录标识。
     */
    private void initViews()
    {
        sideBar = (SideBar)getActivity().findViewById(R.id.sidrbar);
        dialog = (TextView)getActivity().findViewById(R.id.tv_charater_dialog);
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener()
        {
            @Override
            public void onTouchingLetterChanged(String s)
            {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1)
                {
                    listView.setSelection(position);
                }
            }
        });
    }
    
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    
    private void sortList()
    {
        pinyinComparator = new PinyinComparator();
        friendItemList = (ArrayList<FriendItem>)new help().filledData(friendItemList);
        // 根据a-z进行排序源数据
        Collections.sort(friendItemList, pinyinComparator);
        app.setFriendList(friendItemList);
        adapter.setFriendList(friendItemList);
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public Object[] getSections()
    {
        return null;
    }
    
    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position)
    {
        return friendItemList.get(position).getSortLetters().charAt(0);
    }
    
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section)
    {
        for (int i = 0; i < friendItemList.size(); i++)
        {
            String sortStr = friendItemList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section)
            {
                return i;
            }
        }
        return -1;
    }
    
    // TODO 检测这里的弹出框是否能够弹出
    public void onEvent(EventOffline event)
    {
        if (event.getFlag() == 1)
        {
            // 用户被异地登录
            new DialogCenter().popReloginDialog(context);
        }
        else if (event.getFlag() == 2)
        {
            // 连接断开掉线
            new DialogCenter().popNetworkSettingDialog(context);
        }
        else
        {
            
        }
    }
    
    /**
     * 从服务端获取好友列表,并显示
     */
    //
    private void fetchFriendListAndShow(Handler loadFriendListHandler)
    {
        List<RosterEntry> entriesList = new ArrayList<RosterEntry>();// 存储所有好友列表
        // TODO 处理这里的空指针异常
        if (app.getConnection() != null || roster != null)
        {
            roster = app.getConnection().getRoster();// 从服务器获取好友列表
            app.setRoster(roster);
            Collection<RosterEntry> rosterEntryConnCollection = roster.getEntries();
            
            Iterator<RosterEntry> i = rosterEntryConnCollection.iterator();
            while (i.hasNext())
            {
                entriesList.add(i.next());
            }
            
            for (int j = 0; j < entriesList.size(); j++)
            {
                RosterEntry entry = entriesList.get(j);
                FriendItem friendItem = new FriendItem();
                
                // 设置用户显示名
                // TODO 将用户显示名改为昵称或者备注
                String userJid = entry.getUser();
                String userId = userJid.substring(0, userJid.indexOf("@"));
                // 添加到好友列表中
                friendItem.setUserId(userId);
                String remarkName = entry.getName();// 备注名
                if (remarkName == null || remarkName.equals("") || remarkName.equals("null"))
                {
                    friendItem.setRemarkName(userId);
                }
                else
                {
                    friendItem.setRemarkName(remarkName);
                }
                
                // 设置在线状态 方法1
                Presence presence = roster.getPresence(userJid);
                String onlineStatus = OnlineStatusAssit.interpretPresenceType(presence.getType());
                friendItem.setOnlineStatus("[" + onlineStatus + "]");
                
                friendItem.setExtendString("加载中");// 个性签名
                new VCardLoadTask(userId, app.getConnection(), vcardLoadHandler).execute();
                
                // 先统统设置为缓存的头像
                File headImageFile = new HeadImageAssit().getHeadImageFromCache(userId);
                if (headImageFile != null)
                {
                    // 加载本地缓存头像
                    friendItem.setHeadImageUri(Uri.fromFile(headImageFile));
                }
                else
                {
                    // 加载默认头像
                    Uri headImageUri = FormatTools.resId2Uri(getResources(), R.drawable.default_head);
                    friendItem.setHeadImageUri(headImageUri);
                }
                
                // 后台开始检测更新用户头像
                new FetchFileNameTask(VariableHolder.FILE_PREFIX_IMAGE_HEAD + userId, fileNameFetchHandler).execute();
                
                friendItemList.add(friendItem);
                
                // 边下载边显示
                app.setFriendList(friendItemList);
                adapter.setFriendList(friendItemList);
                adapter.notifyDataSetChanged();
            }
            
            sortList();
            // TODO 好友列表加载完成
            if (loadFriendListHandler != null)
            {
                Message osMsg = loadFriendListHandler.obtainMessage();
                osMsg.what = 1;
                osMsg.sendToTarget();
            }
        }
    }
    
    private FriendItem friendItemLongClicked = null;
    
    private int friendItemLongClickedIndex = -1;
    
    /** 好友列表长按后事件的监听类 */
    private class MyOnItemLongClickListener implements OnItemLongClickListener
    {
        
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
        {
            
            friendItemLongClicked = friendItemList.get(position);
            friendItemLongClickedIndex = position;
            
            clickDialog = new ListLongClickDialog(getActivity(), R.style.mydialog);
            clickDialog.show();
            clickDialog.setName(friendItemLongClicked.getUserId());
            clickDialog.setonDialogClickListener(new MydialogClickListener());
            
            return true;
        }
    }
    
    /** 点击dialog后的事件处理类 */
    private class MydialogClickListener implements ListLongClickDialog.onDialogClickListener
    {
        
        @Override
        public void onclick(int index)
        {
            switch (index)
            {
                case 0:
                    // 好友名片
                    Intent intent = new Intent();
                    intent.setClass(context, FriendCardDetailActivity.class);
                    intent.putExtra("userid_detail", friendItemLongClicked.getUserId());
                    startActivity(intent);
                    clickDialog.dismiss();
                    break;
                case 1:
                    // 修改备注
                    // 获取被长按的用户Id
                    setRemarkname(friendItemLongClicked.getUserId());
                    clickDialog.dismiss();
                    break;
                case 2:
                    // 删除好友
                    new DialogCenter().popDeleteFriendConfirmDialog(friendItemLongClicked.getUserId(), context);
                    clickDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
        
    }
    
    /**
     * 这个一个带有文本编辑框的Dialog 用于给用户userToRemarkId设置备注
     * 
     * @param userToRemarkId 将要备注的用户
     */
    private void setRemarkname(final String userToRemarkId)
    {
        final EditText name_input = new EditText(context);
        name_input.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        name_input.setHint("输入备注");
        new AlertDialog.Builder(context).setTitle("设置备注信息")
            .setView(name_input)
            .setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    String reMarkName = name_input.getText().toString();
                    if (reMarkName == null || reMarkName.equals("") || reMarkName.equals("null"))
                    {
                        // 空
                        Toast.makeText(context, "不能使用null作为备注", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String userJid = userToRemarkId + "@" + DateoutApp.getInstance().getServiceName();
                        RosterEntry entry = app.getConnection().getRoster().getEntry(userJid);
                        if (entry == null)
                        {
                            Toast.makeText(context, "无法设置为用户" + userToRemarkId + "设置备注", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            entry.setName(reMarkName);
                            // 更新列表上的值
                            friendItemLongClicked.setRemarkName(reMarkName);
                            if (friendItemLongClickedIndex >= 0 && friendItemLongClickedIndex <= friendItemList.size())
                            {
                                // 刷新视图
                                friendItemList.set(friendItemLongClickedIndex, friendItemLongClicked);
                                // app.setFriendList(friendItemList);
                                // adapter.setFriendList(friendItemList);
                                // adapter.notifyDataSetChanged();
                                sortList();
                            }
                        }
                    }
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    /** 初始化下拉组件 */
    @SuppressLint("InlinedApi")
    private void initSipeRefresh()
    {
        freshLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.srl_tab2_refresh);
        freshLayout.setColorScheme(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
        freshLayout.setOnRefreshListener(new MyonRefreshListener());
    }
    
    /** 下拉刷新的监听类 */
    private class MyonRefreshListener implements SwipeRefreshLayout.OnRefreshListener
    {
        
        @Override
        public void onRefresh()
        {
            freshLayout.setRefreshing(true);
            friendItemList.clear();
            Handler handler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    switch (msg.what)
                    {
                        case 1:
                            // 列表刷新完成
                            freshLayout.setRefreshing(false);
                            app.setFriendList(friendItemList);
                            break;
                        
                        default:
                            break;
                    }
                };
            };
            // 检测网络
            if (new NetworkAssit(context).isNetworkConnected())
            {
                fetchFriendListAndShow(handler);
            }
            else
            {
                Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                freshLayout.setRefreshing(false);// 停止进度条
            }
        }
    }
    
    // TODO 这样嵌套真的不会有问题么=.=发现安卓搞的UI线程好复杂
    private Handler fileDownHandler = new Handler()
    {
        @Override
        public void handleMessage(Message osMsg)
        {
            super.handleMessage(osMsg);
            switch (osMsg.what)
            {
                case 1:
                    final DownloadResult result = (DownloadResult)osMsg.obj;
                    if (result.isDownloadSuccess())
                    {
                        Log.v("Dateout", "FriendListManager==>" + "头像下载成功");
                        // 下载成功,显示图像
                        // 通过文件路径获取用户Id
                        String fileName = new File(result.getFilePath()).getName();
                        String userId = fileName.split("_")[1];
                        refreshHeadImage(userId, Uri.parse(result.getFilePath()));
                        Log.v("Dateout", "FriendListManager==>" + "使用了服务端头像文件");
                    }
                    else
                    {
                        // 下载失败
                        Log.v("Dateout", "FriendListManager==>" + "头像下载失败");
                    }
                    break;
                
                default:
                    break;
            }
        }
    };
    
    // 用于处理用户头像文件名的下载
    private Handler fileNameFetchHandler = new Handler()
    {
        @Override
        public void handleMessage(Message osMsg)
        {
            super.handleMessage(osMsg);
            switch (osMsg.what)
            {
                case 1:
                    String fileName = (String)osMsg.obj;
                    if (fileName == null || fileName.equals(""))
                    {
                        // 找不到头像
                        Log.v("Dateout", "FriendListManager==>" + "服务器报告不存在用户头像");
                    }
                    else
                    {
                        Log.v("Dateout", "FriendListManager==>" + "找到服务端用户最新头像文件名");
                        // 存在头像
                        // 检测本地缓存
                        String userId = fileName.split("_")[1];
                        File file = new File(FilePathTool.getHeadCachedPath(fileName));
                        // CacheAssit().getLoginHeadImageFromCache(userId);
                        if (file.exists() && file.canRead())
                        {
                            Log.v("Dateout", "FriendListManager==>" + "服务端文件名与本地缓存文件名一致");
                            // 本地缓存已经存在最新头像
                            // 显示为本地缓存
                            refreshHeadImage(userId, Uri.parse(file.getAbsolutePath()));
                            Log.v("Dateout", "FriendListManager==>" + "使用了本地缓存头像");
                        }
                        else
                        {
                            Log.v("Dateout", "FriendListManager==>" + "服务端文件名与本地缓存文件名不一致");
                            // 下载头像并缓存头像
                            new DownloadAndCacheTask(fileName, FilePathTool.getHeadCachedPath(fileName),
                                fileDownHandler).execute();
                        }
                    }
                    break;
                
                default:
                    break;
            }
        }
    };
    
    private Handler vcardLoadHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    HashMap<String, CustomVcard> map = (HashMap<String, CustomVcard>)msg.obj;
                    String userId = (String)map.keySet().toArray()[0];
                    CustomVcard myCard = map.get(userId);
                    if (myCard != null)
                    {
                        String moodie = myCard.getMoodie();
                        // 遍历整个好友列表,获取发消息用户所在List中的位置
                        int index = -1;
                        for (int i = 0; i < app.getFriendList().size(); i++)
                        {
                            if (app.getFriendList().get(i).getUserId().equals(userId))
                            {
                                index = i;
                                break;
                            }
                        }
                        if (index > -1 && index < friendItemList.size())
                        {
                            friendItemList.get(index).setExtendString(moodie);
                        }
                    }
                    break;
                
                default:
                    break;
            }
        };
    };
    
    private void refreshHeadImage(String userId, Uri headImageUri)
    {
        for (int j = 0; j < friendItemList.size(); j++)
        {
            if (friendItemList.get(j).getUserId().equals(userId))
            {
                friendItemList.get(j).setHeadImageUri(headImageUri);
                // 更新视图
                // app.setFriendList(friendItemList);
                // adapter.setFriendList(friendItemList);
                // adapter.notifyDataSetChanged();
                sortList();
            }
        }
    }
    
    @Override
    public void onStart()
    {
        // 从内存中读出好友列表,而不是重新加载网络上的
        adapter.setFriendList(app.getFriendList());
        adapter.notifyDataSetChanged();
        
        super.onStart();
    }
    
    @Override
    public void onResume()
    {
        sortList();
        super.onResume();
    }
}
