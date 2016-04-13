package edu.nuist.dateout.app;

import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;

import android.content.res.Configuration;

import com.allthelucky.common.view.network.NetworkApp;
import com.nostra13.universalimageloader.core.ImageLoader;

import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.misc.ImageLoaderCfg;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.model.FriendItem;
import edu.nuist.dateout.model.User;
import edu.nuist.dateout.model.UserHead;
import edu.nuist.dateout.util.FileAssit;

/**
 * 单例模式的Application类
 * 
 * @author Veayo
 *
 */
public class DateoutApp extends NetworkApp
{
    // 存储单例对象DateoutApp的引用
    private static DateoutApp instance;
    
    // 用于标记是否进入ChatActivity,在FriendListActivity ChatActivity中被调用
    private boolean chatActivityIsEntered;
    
    // 用于保存静态XMPPConnection对象
    private XMPPConnection connection;
    
    // 用于存储用户设置的的服务机IP
    private String serverIpInUse;
    
    // 用于存储所有好友用户Id列表
    // private List<String> friendIdList;
    
    //储存服务器HostName，如lenovo，通常是计算机名
    private String serviceName="lenovo";
        

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    // 用于标识已登录的用户信息
    private UserHead loginUser = new UserHead();
    
    // 用于存储与我正在聊天的用户
    private UserHead chatUser = new UserHead();
    
    // 用于存储已注册的用户
    private User registUser = new User();
    
    // 存储登录用户的CustomVcard
    private CustomVcard loginUserVcard;
    
    public CustomVcard getLoginUserVcard()
    {
        return loginUserVcard;
    }
    
    public void setLoginUserVcard(CustomVcard loginUserVcard)
    {
        this.loginUserVcard = loginUserVcard;
    }
    
    // 用户列表
    private Roster roster;
    
    // 好友列表，包含头像等信息
    private ArrayList<FriendItem> friendList = new ArrayList<FriendItem>();
    
    // 用于标记当前所在页面Fragment,值在0~3之间,表示当前所在的Tab位置
    private int currentTabNum;
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        // 初始化程序数据库
        DBAssit dbAssit = new DBAssit();
        if (!dbAssit.isDbConnected())
        {
            dbAssit.connectDb();
        }
        dbAssit.initDatabase();
        dbAssit.closeDbConnect();
        // 初始化SD卡目录
        new FileAssit().checkAppDirs();
        // 初始化ImageLoader
        ImageLoader.getInstance().init(new ImageLoaderCfg().getImageLoaderConfig());
        
        // TODO 移除下面的测试代码
        // new FetchUserRandomTask().execute();
        // boolean b=new NetworkAssit(getApplicationContext()).isNetworkConnected();
        // int i=new NetworkAssit(getApplicationContext()).getNetworkType();
        // Log.v("Dateout", b+","+i);
        
        // 存储消息到数据库
//        if (!dbAssit.isDbConnected())
//        {
//            dbAssit.connectDb();
//        }
        // TODO 移除下面测试代码
//         dbAssit.clearTableValues();
        // for (int i = 0; i < 50000; i++)
        // {
        // CustomMsgPack msgPack =
        // new CustomMsgPack("a6", "a3", i + "", TimeAssit.getDate(), (short)VariableHolder.MSG_TYPE_TEXT);
        // dbAssit.saveChatMessage(msgPack);
        // }
//        dbAssit.closeDbConnect();
    }
    
    @Override
    public void onTerminate()
    {
        super.onTerminate();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    
    public int getCurrentTabNum()
    {
        return currentTabNum;
    }
    
    public void setCurrentTabNum(int currentTabNum)
    {
        this.currentTabNum = currentTabNum;
    }
    
    public ArrayList<FriendItem> getFriendList()
    {
        return friendList;
    }
    
    public void setFriendList(ArrayList<FriendItem> friendList)
    {
        this.friendList = friendList;
    }
    
    public Roster getRoster()
    {
        return roster;
    }
    
    public void setRoster(Roster roster)
    {
        this.roster = roster;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        System.gc();
    }
    
    public static DateoutApp getInstance()
    {
        return instance;
    }
    
    public boolean isChatActivityIsEntered()
    {
        return chatActivityIsEntered;
    }
    
    public void setChatActivityIsEntered(boolean chatActivityIsEntered)
    {
        this.chatActivityIsEntered = chatActivityIsEntered;
    }
    
    public XMPPConnection getConnection()
    {
        return connection;
    }
    
    public void setConnection(XMPPConnection connection)
    {
        this.connection = connection;
    }
    
    public String getServerIpInUse()
    {
        return serverIpInUse;
    }
    
    public void setServerIpInUse(String serverIpInUse)
    {
        this.serverIpInUse = serverIpInUse;
    }
    
    public UserHead getLoginUser()
    {
        return loginUser;
    }
    
    public void setLoginUser(UserHead loginUser)
    {
        this.loginUser = loginUser;
    }
    
    public UserHead getChatUser()
    {
        return chatUser;
    }
    
    public void setChatUser(UserHead chatUser)
    {
        this.chatUser = chatUser;
    }
    
    public User getRegistUser()
    {
        return registUser;
    }
    
    public void setRegistUser(User registUser)
    {
        this.registUser = registUser;
    }
    
}
