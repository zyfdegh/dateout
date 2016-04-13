package edu.nuist.dateout.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dateout.R;
import com.nuist.dateout.face.FaceGVAdapter;
import com.nuist.dateout.face.FaceVPAdapter;
import com.nuist.dateout.media.RecordDialog;
import com.nuist.dateout.media.VoiceAnimManager;
import com.nuist.dateout.tab1.activity.FriendCardActivity;
import com.nuist.dateout.tab1.activity.ShowImageActivity;
import com.nuist.dateout.tab4.activity.PersonalCardActivity;

import de.greenrobot.event.EventBus;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.core.MsgUtil;
import edu.nuist.dateout.core.XMPPConnectionAssit;
import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.interfc.ListItemsCallback;
import edu.nuist.dateout.model.ChatBubbleItem;
import edu.nuist.dateout.model.ChatBubbleTag;
import edu.nuist.dateout.model.CustomMsgPack;
import edu.nuist.dateout.model.DownloadResult;
import edu.nuist.dateout.model.RecentMsgItem;
import edu.nuist.dateout.task.UploadFileTask;
import edu.nuist.dateout.util.CacheAssit;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.util.FileNameGenTool;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.util.MediaAssit;
import edu.nuist.dateout.util.OnlineStatusAssit;
import edu.nuist.dateout.util.PhotoAssit;
import edu.nuist.dateout.util.TimeAssit;
import edu.nuist.dateout.value.AppConfig;
import edu.nuist.dateout.value.VariableHolder;
import edu.nuist.dateout.view.ChatBubbleAdapter;

/**
 * @author zhang 聊天窗口
 */
public class ChatActivity extends Activity implements OnClickListener, ListItemsCallback
{
    private DateoutApp app;
    
    // TODO 移动部分不必要的成员变量到方法内部
    private EditText messageEditText;
    
    private ImageButton friendDetailImageButton;
    
    private LinearLayout moreLayout;// 隐藏布局
    
    private LinearLayout voiceLayout;
    
    private TextView friendNameEditText;// 顶部的好友名字
    
    private TextView friendStatEditText;// 顶部的好友在线状态
    
    // 隐藏布局里的的item
    private TextView photoTextView;
    
    private TextView caremaTextView;
    
    private TextView locationTextView;
    
    private ImageView voiceImageview;// 播放语音
    
    private ImageView moreImageView;
    
    private ImageView pressedToSayImageView;// 按住录音
    
    private ListView chatBubbleListView;
    
    private ChatBubbleAdapter adapter;// 用于聊天气泡显示与切换
    
    private List<CustomMsgPack> msgPackList;// 存储消息包的列表
    
    private Chat chat;
    
    private Context context;
    
    private String receiverJid;
    
    private String receiverId;
    
    private String myId;
    
    private DBAssit dbAssit;
    
    private int offset = 0;// 用于标记数据库中消息历史查询的偏移
    
    private boolean VOICE_IMAGE_TAG_STATE = true;// 记录发送按钮 状态的 标识
    
    private RecordDialog recordDialog;
    
    private ImageView showfaceImage; // 用于显示表情布局
    
    private View faceLayout;// 表情布局
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);
        
        app = DateoutApp.getInstance();
        
        context = this;
        initComponents();
        
        // 初始化表情
        initStaticFaces();
        initViews();
        InitViewPager();
        
        ChatManager chatManager = app.getConnection().getChatManager();
        // 消息接收者用户名
        receiverId = app.getChatUser().getUserId();
        // 将被点击好友用户名保存到DateoutApp中
        app.getChatUser().setUserId(receiverId);
        // 消息接收者Jid
        receiverJid = receiverId + "@" + DateoutApp.getInstance().getServiceName();
        chat = chatManager.createChat(receiverJid, null);
        
        // 消息发送者Id
        myId = app.getLoginUser().getUserId();
        // 顶部用户名和在线状态的设置
        String remarkName = new XMPPConnectionAssit().userId2RemarkName(app.getConnection(), receiverId);
        friendNameEditText.setText(remarkName);
        // 动态更新此处的用户状态
        Presence presence = app.getRoster().getPresence(receiverJid);
        String onlineStatus = OnlineStatusAssit.interpretPresenceType(presence.getType());
        friendStatEditText.setText("(" + onlineStatus + ")");
        
        msgPackList = getMoreChatHistoryFromDb(myId, receiverId, offset);
        // 先是使用一个空的列表填充
        adapter = new ChatBubbleAdapter(this, this, new ArrayList<ChatBubbleItem>());
        chatBubbleListView.setAdapter(adapter);
        refreshChatBubbleList();
        
        // 注册EventBus消息订阅者
        EventBus.getDefault().register(this);
    }
    
    /**
     * 从数据库中获取一批聊天历史记录
     * 
     * @param offset 消息表查询偏移
     * @return 返回从offset开始,后面的20条消息记录(依照时间倒序)
     */
    private ArrayList<CustomMsgPack> getMoreChatHistoryFromDb(String userId1, String userId2, int offset)
    {
        // 获取数据库中的历史消息记录
        dbAssit = new DBAssit();
        
        if (!dbAssit.isDbConnected())
        {
            dbAssit.connectDb();
        }
        // 获取消息历史中最近20条消息记录
        ArrayList<CustomMsgPack> msgList = dbAssit.getChatMessageHistory(userId1, userId2, offset);
        dbAssit.closeDbConnect();
        
        if (msgList == null)
        {
            msgList = new ArrayList<CustomMsgPack>();
        }
        return msgList;
    }
    
    /**
     * 点击发送按钮发送消息
     */
    private void sendMessageBtnClicked()
    {
        if (!app.getConnection().isConnected())
        {
            Toast.makeText(context, "连接已断开,无法发送消息", Toast.LENGTH_SHORT).show();
            Log.w("Dateout", "ChatActivity==>" + "连接已断开,无法发送消息");
            // TODO 重新连接
        }
        else
        {
            String msgToSend = messageEditText.getText().toString();
            if (msgToSend.length() > 0 && msgToSend.length() < 1024)
            {
                CustomMsgPack msgPack =
                    new CustomMsgPack(myId, receiverId, msgToSend, TimeAssit.getDate(), VariableHolder.MSG_TYPE_TEXT);
                checkThenAddMsgPackToList(msgPack);
                refreshChatBubbleList();
                try
                {
                    // 发送消息
                    chat.sendMessage(msgToSend);
                    System.out.println("发出消息：" + msgToSend);
                    // 存储消息到数据库
                    if (!dbAssit.isDbConnected())
                    {
                        dbAssit.connectDb();
                    }
                    dbAssit.saveChatMessage(msgPack);
                    dbAssit.closeDbConnect();
                }
                catch (XMPPException e)
                {
                    e.printStackTrace();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            else if (msgToSend.length() <= 0)
            {
                Toast.makeText(context, "消息为空", Toast.LENGTH_SHORT).show();
            }
            else if (msgToSend.length() >= 1024)
            {
                Toast.makeText(context, "消息太长啦=.=", Toast.LENGTH_SHORT).show();
            }
            messageEditText.setText("");
        }
    }
    
    /**
     * 初始化组件
     */
    private void initComponents()
    {
        messageEditText = (EditText)findViewById(R.id.et_chat_msgtosend);
        voiceImageview = (ImageView)findViewById(R.id.iv_chatview_voice);
        friendDetailImageButton = (ImageButton)findViewById(R.id.btn_chat_frienddetail);
        moreLayout = (LinearLayout)findViewById(R.id.lo_chatview_more);
        voiceLayout = (LinearLayout)findViewById(R.id.lo_chatview_voice);
        photoTextView = (TextView)findViewById(R.id.tv_more_photo);
        caremaTextView = (TextView)findViewById(R.id.tv_more_carema);
        locationTextView = (TextView)findViewById(R.id.tv_more_location);
        moreImageView = (ImageView)findViewById(R.id.iv_chat_viewmore);
        pressedToSayImageView = (ImageView)findViewById(R.id.iv_pressed_to_say);
        chatBubbleListView = (ListView)findViewById(R.id.lv_chat_msglist);
        friendNameEditText = (TextView)findViewById(R.id.tv_chat_username);
        friendStatEditText = (TextView)findViewById(R.id.tv_chat_userstat);
        
        messageEditText.setOnClickListener(this);
        messageEditText.addTextChangedListener(new MyTextWatcher());
        voiceImageview.setOnClickListener(this);
        friendDetailImageButton.setOnClickListener(this);
        moreImageView.setOnClickListener(this);
        photoTextView.setOnClickListener(this);
        caremaTextView.setOnClickListener(this);
        locationTextView.setOnClickListener(this);
        chatBubbleListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        pressedToSayImageView.setOnClickListener(this);
        pressedToSayImageView.setOnTouchListener(new MyontouchListener());
        
        // 展开表情布局的按钮
        showfaceImage = (ImageView)findViewById(R.id.iv_chat_face);
        showfaceImage.setOnClickListener(this);
        faceLayout = findViewById(R.id.lo_chatview_face);
    }
    
    /**
     * 用于检测msgPackList长度,根据长度来增删msgPackList元素
     * 
     * @param msgPack
     */
    private void checkThenAddMsgPackToList(CustomMsgPack msgPack)
    {
        int listItemCount = msgPackList.size();
        if (listItemCount <= 20)
        {
            msgPackList.add(msgPack);
        }
        else
        {
            msgPackList.remove(0);
            msgPackList.add(msgPack);
        }
    }
    
    /**
     * 使用onEvent来接收从MessageAssit类中传来的EventBus事件
     * 
     * @param msgPack 收到的消息包
     */
    public void onEvent(CustomMsgPack msgPack)
    {
        // event.getReceiverId()是接收到的消息包中的收方,也就是聊天窗口的发方
        // 只接收正在聊天的用户发给我自己的消息
        if (msgPack.getReceiverId().equals(myId) && app.getChatUser().getUserId().equals(msgPack.getSenderId()))
        {
            processMessageRecevied(msgPack);
        }
    }
    
    /**
     * 用于更新顶部好友在线状态 使用onEvent来接收从FriendListManaget类中传来的EventBus事件
     * 
     * @param newOnlineState
     */
    public void onEvent(String newOnlineState)
    {
        // 更新页面在线情况
        friendStatEditText.setText(newOnlineState);
    }
    
    /**
     * 用于接收来自DownloadAndCacheTask发来的文件下载结果
     * 
     * @param event
     */
    public void onEvent(DownloadResult event)
    {
        
        int lastIndex = msgPackList.size() - 1;
        if (event.isDownloadSuccess())
        {
            Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
            msgPackList.get(lastIndex).setMsgBody(event.getFilePath());
            refreshChatBubbleList();// 刷新视图
        }
        else
        {
            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 用于处理接收到的消息（显示到聊天气泡上）
     * 
     * @param msgPack
     */
    private void processMessageRecevied(CustomMsgPack msgPack)
    {
        // ChatBubbleItem bubbleItem=msgPack2ChatBubbleItem(msgPack);
        // 将消息加入气泡列表
        checkThenAddMsgPackToList(msgPack);
        refreshChatBubbleList();
    }
    
    // TODO 封装到类转换工具中
    /**
     * 退出ChatActivity之后,从数据库取出最近一条消息,封装成RecentMsgItem,并传递给RecentMsgManager
     */
    private void deliverLastMsgToRecentMsgManager()
    {
        if (!dbAssit.isDbConnected())
        {
            dbAssit.connectDb();
        }
        CustomMsgPack lastMsgPack = dbAssit.getLastChatMsg(myId, receiverId);
        dbAssit.closeDbConnect();
        
        if (lastMsgPack != null)
        {
            RecentMsgItem recentMsgItem = new RecentMsgItem();
            recentMsgItem.setUserHeadUri(app.getChatUser().getHeadImageUri());
            recentMsgItem.setUserId(receiverId);
            recentMsgItem.setLastMessage(lastMsgPack.getMsgBody());
            recentMsgItem.setMsgDateAndTime(lastMsgPack.getDateAndTime());
            recentMsgItem.setUnReadMsgNum(0);
            recentMsgItem.setMsgType(lastMsgPack.getMsgType());// 根据消息类型设置MsgType
            // 发送到RecentMsgManager中
            EventBus.getDefault().post(recentMsgItem);
        }
    }
    
    /**
     * 当编辑消息的文本框被点击时调用
     */
    private void hideAllOtherView()
    {
        moreLayout.setVisibility(View.GONE);
        voiceLayout.setVisibility(View.GONE);
        faceLayout.setVisibility(View.GONE);
    }
    
    /**
     * 点击发送语音时调用的方法
     */
    private void showChatWithFriendVoiceView()
    {
        if (VOICE_IMAGE_TAG_STATE)
        {
            hideSoftInputView();
            if (faceLayout.getVisibility() == View.VISIBLE)
            {
                faceLayout.setVisibility(View.GONE);
            }
            if (moreLayout.getVisibility() == View.VISIBLE)
            {
                moreLayout.setVisibility(View.GONE);
            }
            if (voiceLayout.getVisibility() == View.GONE)
            {
                voiceLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                voiceLayout.setVisibility(View.GONE);
            }
        }
        else
        {
            sendMessageBtnClicked();
        }
    }
    
    /**
     * 点击more按钮，执行的方法
     */
    private void showChatWithFriendMoreView()
    {
        hideSoftInputView();
        if (voiceLayout.getVisibility() == View.VISIBLE)
        {
            voiceLayout.setVisibility(View.GONE);
        }
        
        if (faceLayout.getVisibility() == View.VISIBLE)
        {
            faceLayout.setVisibility(View.GONE);
        }
        if (moreLayout.getVisibility() == View.GONE)
        {
            moreLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            moreLayout.setVisibility(View.GONE);
        }
    }
    
    /** 显示表情布局 */
    private void showFaceLayout()
    {
        hideSoftInputView();
        voiceLayout.setVisibility(View.GONE);
        moreLayout.setVisibility(View.GONE);
        // faceLayout.setVisibility(View.VISIBLE);
        if (faceLayout.getVisibility() == View.GONE)
        {
            faceLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            faceLayout.setVisibility(View.GONE);
        }
    }
    
    /**
     * 点击好友详情按钮,执行的方法
     */
    private void onClickBtnFriendDetail()
    {
        Intent intent = new Intent();
        intent.putExtra("userid_detail", receiverId);
        intent.setClass(context, FriendCardActivity.class);
        startActivity(intent);
    }
    
    /**
     * 隐藏软键盘
     */
    private void hideSoftInputView()
    {
        InputMethodManager manager = ((InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    /**
     * 按了手机返回按钮
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        // 退出ChatActivity之后,更改标记值
        app.setChatActivityIsEntered(false);
        EventBus.getDefault().unregister(this);// 反注册EventBus
        
        // 取出最近一条消息内容,转换为RecentMsgItem,并发出去
        // TODO 优化此处代码,防止退出时候掉帧卡顿
        deliverLastMsgToRecentMsgManager();
    }
    
    /**
     * 响应事件集中处理
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        // 点击我的头像
            case R.id.iv_chat_myhead:
                Intent intent = new Intent();
                intent.setClass(context, PersonalCardActivity.class);
                startActivity(intent);
                break;
            // 点击好友头像
            case R.id.iv_chat_friendhead:
                onClickBtnFriendDetail();
                break;
            case R.id.iv_chat_image:
                // Toast.makeText(context, "点击了图片", Toast.LENGTH_SHORT).show();
                break;
            // TODO 将消息点击触发事件更改为长按
            case R.id.tv_chat_text:
                // Toast.makeText(context, "点击了文本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_chat_voice:
                // Toast.makeText(context, "点击了播放", Toast.LENGTH_SHORT).show();
                break;
            // 点击加号
            case R.id.iv_chat_viewmore:
                showChatWithFriendMoreView();
                break;
            // 加号里的相册
            case R.id.tv_more_photo:
                PhotoAssit photoAssit = new PhotoAssit(ChatActivity.this);
                photoAssit.startGallery();
                break;
            // 加号里的相机
            case R.id.tv_more_carema:
                PhotoAssit photoAssit2 = new PhotoAssit(ChatActivity.this);
                photoAssit2.startCamera();
                break;
            // 加号里的地图
            case R.id.tv_more_location:
                break;
            // 语音按钮或发送按钮(可切换)
            case R.id.iv_chatview_voice:
                showChatWithFriendVoiceView();
                break;
            // 点击按住说话
            // case R.id.iv_pressed_to_say:
            // Toast.makeText(context, "长按开始录音", Toast.LENGTH_SHORT).show();
            // break;
            case R.id.et_chat_msgtosend:
                hideAllOtherView();
                break;
            case R.id.btn_chat_frienddetail:
                onClickBtnFriendDetail();
                break;
            case R.id.iv_chat_face:
                // 点击显示表情
                showFaceLayout();
                break;
            default:
                break;
        }
    }
    
    /**
     * 用于处理点击拍照或者从相册选取照片之后,照片的处理,包括显示到控件和发送图片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        
        // CustomMsgPack msgPack;//待发送的消息包(上传图片后应当用消息通知对方)
        String dstFileName;// 在程序目录下的文件缓存名
        File srcFile = new File("");// 拷贝前的文件
        File dstFile = new File("");// 拷贝后的文件
        String dstFilePath;// 拷贝后的文件路径
        
        // 用于处理图片上传完成后的结果，通知到主线程
        Handler handler = new Handler()
        {
            public void handleMessage(android.os.Message osMsg)
            {
                switch (osMsg.what)
                {
                    case 1:
                        // 这里的obj可以是文件名,也可以是图片下载链接(聊天发送的图片)
                        String resultObj = (String)osMsg.obj;
                        if (resultObj != null)
                        {
                            Toast.makeText(context, "图片上传成功", Toast.LENGTH_SHORT).show();
                            // 发送消息通知对方
                            try
                            {
                                // 发送图片时,只发送消息标识和图片下载链接,不发送文件本身
                                chat.sendMessage(VariableHolder.MSG_FLAG_IMAGE + "," + resultObj);
                            }
                            catch (XMPPException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        
        switch (requestCode)
        {
            case VariableHolder.REQUEST_CODE_PHOTO_FROM_CAMERA:
                if (resultCode == RESULT_OK)
                {
                    // 取出拍完之后缓存的图片
                    srcFile = new CacheAssit().getCameraCachedImage();
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_FROM_GALLERY:
                if (resultCode == RESULT_OK)
                {
                    if (data != null)
                    {
                        Uri picDataUri = data.getData(); // 获取系统返回的照片的Uri
                        if (picDataUri != null)
                        {
                            // 获取图片的绝对路径
                            String picAbsuluteUriStr = new FileAssit().getImageAbsolutePath(context, picDataUri);
                            // 得到原图像文件
                            srcFile = new File(picAbsuluteUriStr);
                        }
                    }
                }
                break;
            default:
                break;
        }
        
        if (srcFile.getName() == null || srcFile.getName().equals(""))
        {
            // 没有选择图片,从相册返回,或者取消拍照
        }
        else
        {
            // ====显示和上传图像====
            // 拷贝后的文件名
            dstFileName = new FileNameGenTool().generateSentImageName(srcFile);
            // 拷贝后的文件路径
            dstFilePath = FilePathTool.getImageSentPath(dstFileName);// 新的文件路径
            dstFile = new File(dstFilePath);
            // 拷贝文件srcFile到新的文件dstFilePath,返回值结果
            short copyResult = new FileAssit().copyFile(srcFile, dstFile);
            // 拷贝没有失败
            if (copyResult != VariableHolder.COPY_RESULT_FAIL)
            {
                String dstUriStr = Uri.fromFile(dstFile).toString();
                CustomMsgPack msgPackForView =
                    new CustomMsgPack(myId, receiverId, dstUriStr, TimeAssit.getDate(), VariableHolder.MSG_TYPE_IMAGE);
                
                // 显示图片到聊天窗口
                checkThenAddMsgPackToList(msgPackForView);
                refreshChatBubbleList();
                
                String picDownLink = AppConfig.URL_DOWNLOAD_SERVLET + "filename=" + dstFileName;
                CustomMsgPack msgPackForDb =
                    new CustomMsgPack(myId, receiverId, picDownLink, TimeAssit.getDate(), VariableHolder.MSG_TYPE_IMAGE);
                // 存储消息到数据库
                if (!dbAssit.isDbConnected())
                {
                    dbAssit.connectDb();
                }
                dbAssit.saveChatMessage(msgPackForDb);
                dbAssit.closeDbConnect();
                
                new UploadFileTask(dstFile, handler).execute();// 调用异步任务上传
                
                // 消息在图片上传成功之后,才发送给对方
            }
        }
    }
    
    // 从UploadFileTask里面传过来osMsg
    private Handler uploadHandler = new Handler()
    {
        public void handleMessage(android.os.Message osMsg)
        {
            switch (osMsg.what)
            {
                case 1:
                    String fileUploadedName = (String)osMsg.obj;
                    if (fileUploadedName == null)
                    {
                        // 上传失败
                        Toast.makeText(context, "语音上传失败", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context, "语音上传成功", Toast.LENGTH_SHORT).show();
                        // 发送消息通知对方发了语音
                        try
                        {
                            chat.sendMessage(VariableHolder.MSG_FLAG_AUDIO + "," + fileUploadedName);
                        }
                        catch (XMPPException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    
    /**
     * 按钮长按按住监听,松开按钮监听
     */
    private class MyontouchListener implements OnTouchListener
    {
        
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            // 按下录音按钮
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                pressedToSayImageView.setImageResource(R.drawable.i_voice_select);
                recordDialog = new RecordDialog(context, uploadHandler).setCountTime(60).setCountDownTime(true);
                recordDialog.showDialog();
            }
            
            // 松开录音按钮
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                // Toast.makeText(context, "送开了按钮", Toast.LENGTH_SHORT).show();
                
                pressedToSayImageView.setImageResource(R.drawable.i_voice_normal);
                File recordFile = recordDialog.submit();
                
                if (recordFile != null && recordFile.exists() && recordFile.length() > 0)
                {
                    // TODO 在此检测文件长度,如果大于4096字节,则上传并显示,否则提示录音太短
                    if (recordFile.length() > 4 * 1024)
                    {
                        Toast.makeText(context, "开始上传录音", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context, "录音时间太短", Toast.LENGTH_SHORT).show();
                    }
                    // String
                    // dstFileUriStr=Environment.getExternalStorageDirectory()+"/"+VariableHolder.DIR_APP_DATA_AUDIO_SENT+recordFile.getName();
                    String dstFileUriStr = FilePathTool.getAudioSentPath(recordFile.getName());
                    Toast.makeText(context, dstFileUriStr, Toast.LENGTH_SHORT).show();
                    CustomMsgPack msgPack =
                        new CustomMsgPack(myId, receiverId, dstFileUriStr, TimeAssit.getDate(),
                            VariableHolder.MSG_TYPE_AUDIO);
                    // 显示语音消息到气泡
                    checkThenAddMsgPackToList(msgPack);
                    
                    // 存储消息到数据库
                    if (!dbAssit.isDbConnected())
                    {
                        dbAssit.connectDb();
                    }
                    dbAssit.saveChatMessage(msgPack);
                    dbAssit.closeDbConnect();
                }
            }
            
            return false;
        }
    }
    
    // 刷新视图
    private void refreshChatBubbleList()
    {
        List<ChatBubbleItem> chatBubbleItemList = new ArrayList<ChatBubbleItem>();
        for (int i = 0; i < msgPackList.size(); i++)
        {
            // 转换CustonMsgPack为ChatBubbleItem
            chatBubbleItemList.add(MsgUtil.msgPack2ChatBubbleItem(msgPackList.get(i)));
        }
        adapter.setChatBubbleItemList(chatBubbleItemList);
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public void transViewTag(ChatBubbleTag tag)
    {
        if (tag != null)
        {
            // 通过msgPack类型,显示图片或者播放语音,或者询问下载文件
            switch (tag.getMsgType())
            {
                case VariableHolder.MSG_TYPE_AUDIO:
                    animManager = new VoiceAnimManager((ImageView)tag.getView(), tag.isMsgSentByMe());
                    animManager.playVoiceAnimation();
                    File audioFile = new File(tag.getResUrl());
                    new MediaAssit().playAudio(audioFile, animHandler);
                    break;
                case VariableHolder.MSG_TYPE_IMAGE:
                    // 获取图片Uri
                    String imageUrl = tag.getResUrl();
                    if (imageUrl == null || imageUrl.equals(""))
                    {
                    }
                    else
                    {
                        Intent intent = new Intent(ChatActivity.this, ShowImageActivity.class);
                        intent.putExtra("imageUrl", imageUrl);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    
    Handler animHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if (msg.what == 1000)
            {
                animManager.stopVoiceAnimation();
            }
            
        };
    };
    
    @Override
    public void onLongClick(View v)
    {
        Toast.makeText(context, "长按了", Toast.LENGTH_SHORT).show();
    }
    
    // *********************************表情*****************************************************
    private List<View> views = new ArrayList<View>();
    
    private ViewPager mViewPager;
    
    private List<String> staticFacesList;
    
    private int columns = 6;
    
    private int rows = 4;
    
    /** 代表着页数的小白点 */
    private LinearLayout mDotsLayout;
    
    private SwipeRefreshLayout refreshlayout;
    
    private VoiceAnimManager animManager;// 播放动画的控制类
    
    // private EditText input;
    
    @SuppressLint("InlinedApi")
    private void initViews()
    {
        mViewPager = (ViewPager)findViewById(R.id.face_viewpager);
        mViewPager.setOnPageChangeListener(new PageChange());
        // 表情下小圆点
        mDotsLayout = (LinearLayout)findViewById(R.id.face_dots_container);
        // input = (EditText) findViewById(R.id.input_sms);
        
        refreshlayout = (SwipeRefreshLayout)findViewById(R.id.srl_chat_refresh);
        refreshlayout.setColorScheme(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
        refreshlayout.setOnRefreshListener(new ChatRefreshListener());
    }
    
    /** 下拉刷新的监听类 */
    private class ChatRefreshListener implements OnRefreshListener
    {
        
        @Override
        public void onRefresh()
        {
            // 顶部进度条动起来
            refreshlayout.setRefreshing(true);
            // 下拉刷新消息记录
            offset += 20;// 继续查询下20条记录
            ArrayList<CustomMsgPack> nextMsgPackList = getMoreChatHistoryFromDb(myId, receiverId, offset);
            if (nextMsgPackList != null)
            {
                int listSize = nextMsgPackList.size();
                for (int i = 0; i < listSize; i++)
                {
                    // 加入消息列表
                    // msgPackList.add(nextMsgPackList.get(i));
                    msgPackList.add(0, nextMsgPackList.get(listSize - i - 1));
                }
                chatBubbleListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                // chatBubbleListView.setSelection(3);
                // refreshChatBubbleList();
                List<ChatBubbleItem> chatBubbleItemList = new ArrayList<ChatBubbleItem>();
                for (int i = 0; i < msgPackList.size(); i++)
                {
                    // 转换CustonMsgPack为ChatBubbleItem
                    chatBubbleItemList.add(MsgUtil.msgPack2ChatBubbleItem(msgPackList.get(i)));
                }
                adapter.setChatBubbleItemList(chatBubbleItemList);
                adapter.notifyDataSetChanged();
                
                // 用于控制气泡连续显示连续
                if (listSize <= 0)
                {
                    chatBubbleListView.setSelection(0);
                }
                else
                {
                    chatBubbleListView.setSelection(listSize - 1);
                }
            }
            refreshlayout.setRefreshing(false);
        }
        
    }
    
    /*
     * 初始表情 *
     */
    private void InitViewPager()
    {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++)
        {
            views.add(viewPagerItem(i));
            LayoutParams params = new LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }
    
    /** 获取小白点下标的imageview */
    private ImageView dotsItem(int position)
    {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView)layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }
    
    /**
     * 表情页改变时，dots效果也要跟着改变
     * */
    class PageChange implements OnPageChangeListener
    {
        @Override
        public void onPageScrollStateChanged(int arg0)
        {
        }
        
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
        }
        
        @Override
        public void onPageSelected(int arg0)
        {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++)
            {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }
        
    }
    
    private View viewPagerItem(int position)
    {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.face_gridview, null);// 表情布局
        GridView gridview = (GridView)layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList.subList(position * (columns * rows - 1),
            (columns * rows - 1) * (position + 1) > staticFacesList.size() ? staticFacesList.size()
                : (columns * rows - 1) * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                try
                {
                    String png = ((TextView)((LinearLayout)view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal"))
                    {// 如果不是删除图标
                        insert(getFace(png));
                    }
                    else
                    {
                        delete();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        return gridview;
    }
    
    /**
     * 删除图标执行事件 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     * */
    private void delete()
    {
        if (messageEditText.getText().length() != 0)
        {
            int iCursorEnd = Selection.getSelectionEnd(messageEditText.getText());
            int iCursorStart = Selection.getSelectionStart(messageEditText.getText());
            if (iCursorEnd > 0)
            {
                if (iCursorEnd == iCursorStart)
                {
                    if (isDeletePng(iCursorEnd))
                    {
                        String st = "#[face/png/f_static_000.png]#";
                        ((Editable)messageEditText.getText()).delete(iCursorEnd - st.length(), iCursorEnd);
                    }
                    else
                    {
                        ((Editable)messageEditText.getText()).delete(iCursorEnd - 1, iCursorEnd);
                    }
                }
                else
                {
                    ((Editable)messageEditText.getText()).delete(iCursorStart, iCursorEnd);
                }
            }
        }
    }
    
    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     * **/
    private boolean isDeletePng(int cursor)
    {
        String st = "#[face/png/f_static_000.png]#";
        String content = messageEditText.getText().toString().substring(0, cursor);
        if (content.length() >= st.length())
        {
            String checkStr = content.substring(content.length() - st.length(), content.length());
            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }
    
    /**
     * 向输入框里添加表情
     * */
    private void insert(CharSequence text)
    {
        int iCursorStart = Selection.getSelectionStart((messageEditText.getText()));
        int iCursorEnd = Selection.getSelectionEnd((messageEditText.getText()));
        if (iCursorStart != iCursorEnd)
        {
            ((Editable)messageEditText.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((messageEditText.getText()));
        ((Editable)messageEditText.getText()).insert(iCursor, text);
    }
    
    /**
     * 将 表情字符串转换 为可显示在输入框的表情
     * 
     * @param png
     * @return
     */
    private SpannableStringBuilder getFace(String png)
    {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try
        {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(new ImageSpan(ChatActivity.this, BitmapFactory.decodeStream(getAssets().open(png))), sb.length()
                - tempText.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return sb;
    }
    
    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     * 
     * @return
     */
    private int getPagerCount()
    {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1) : count / (columns * rows - 1) + 1;
    }
    
    /**
     * 初始化表情列表staticFacesList,从asset中读取表情
     */
    private void initStaticFaces()
    {
        try
        {
            staticFacesList = new ArrayList<String>();
            String[] faces = getAssets().list("face/png");
            // 将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++)
            {
                staticFacesList.add(faces[i]);
            }
            // 去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 监听Edittext状态的TextWatch
     */
    private class MyTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            // TODO Auto-generated method stub
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void afterTextChanged(Editable s)
        {
            int stringLength = s.toString().length();
            if (stringLength == 0)
            {
                voiceImageview.setImageDrawable(getResources().getDrawable(R.drawable.chat_voice_icon_selector));
                VOICE_IMAGE_TAG_STATE = true;
            }
            else if (VOICE_IMAGE_TAG_STATE)
            {
                voiceImageview.setImageDrawable(getResources().getDrawable(R.drawable.i_send_message));
                VOICE_IMAGE_TAG_STATE = false;
            }
        }
    }
}