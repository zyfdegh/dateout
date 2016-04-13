package edu.nuist.dateout.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.XMPPError;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.value.AppConfig;
import edu.nuist.dateout.value.VariableHolder;

/**
 * 主要用于初始化XMPPConnection对象
 * 
 * @author Veayo
 *
 */
public class XMPPConnectionAssit
{
    private String serverIp;
    
    private int openfirePort;
    
    private DateoutApp app = DateoutApp.getInstance();
    
    public XMPPConnectionAssit()
    {
        super();
        this.openfirePort = AppConfig.DEFAULT_OPENFIRE_PORT;
        String ip = app.getServerIpInUse();
        if (ip != null && !ip.equals(""))
        {
            this.serverIp = ip;
        }
    }
    
    public XMPPConnectionAssit(String serverIp)
    {
        this.openfirePort = AppConfig.DEFAULT_OPENFIRE_PORT;
        this.serverIp = serverIp;
    }
    
    /**
     * 用于初始化Dateout里的XMPPConnection对象
     */
    public void initConnection()
    {
        ConnectionConfiguration conConfig;
        conConfig = new ConnectionConfiguration(serverIp, openfirePort);
        conConfig.setSendPresence(false);// 设置不允许发送状态信息，即不告知服务端用户上线(available)
        /** 是否启用安全验证 */
        conConfig.setSASLAuthenticationEnabled(false);
        conConfig.setTruststorePath("/system/etc/security/cacerts.bks");
        conConfig.setTruststorePassword("changeit");
        conConfig.setTruststoreType("bks");
        /** 是否启用调试 */
        conConfig.setDebuggerEnabled(true);
        /** 创建connection链接 */
        // 设置全局连接
        app.setConnection(new XMPPConnection(conConfig));
        app.setServiceName((app.getConnection().getServiceName()));
    }
    
    /**
     * 断开连接
     */
    public void disConnect()
    {
        Presence presence = new Presence(Presence.Type.unavailable);
        DateoutApp.getInstance().getConnection().disconnect(presence);
    }
    
    /**
     * 重新连接
     * 
     * @param connection
     * @return
     */
    public boolean reConnect(XMPPConnection connection)
    {
        try
        {
            connection.connect();
            if (connection.isConnected())
            {
                Presence presence = new Presence(Presence.Type.available);
                connection.sendPacket(presence);
                // Toast.makeText(context, "用户已上线!", Toast.LENGTH_LONG).show();
                Log.w("Dateout", "XMPPConnectionAssit==>" + "重连成功");
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (XMPPException e)
        {
            Log.v("ERROR", "XMPPConnectionAssit==>" + "XMPP连接失败!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 带有返回值的登录方法
     * 
     * @param con
     * @param id
     * @param ps
     * @return
     */
    // public int loginWithResult(XMPPConnection con, String id, String ps)
    // {
    // try
    // {
    // // 登录
    // con.connect();
    // con.login(id, ps);
    // return VariableHolder.LOGIN_SECCESS;// 登录成功
    // }
    // catch (Exception e)
    // {
    // if (e instanceof XMPPException)
    // {
    // XMPPException xe = (XMPPException)e;
    // final XMPPError error = xe.getXMPPError();
    // int errorCode = error.getCode();
    // if (errorCode == 401)
    // {
    // return VariableHolder.LOGIN_ERROR_ACCOUNT_NOTPASS;// 帐号或密码错误
    // }
    // else if (errorCode == 403)
    // {
    // return VariableHolder.LOGIN_ERROR_ACCOUNT_NOTPASS;// 帐号或密码错误
    // }
    // else
    // {
    // return VariableHolder.LOGIN_ERROR_SERVER_UNAVAILABLE;// 服务器无法连接
    // }
    // }
    // else
    // {
    // return VariableHolder.LOGIN_ERROR_OTHER;// 其他原因错误
    // }
    // }
    // }
    
    /**
     * 循环重新登录,每隔5秒执行一次登录操作
     */
    public void reLogin()
    {
        // 读取保存的账户密码
        Context context = app.getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences("dateout", Context.MODE_PRIVATE);
        
        String userId = preferences.getString("username", null);
        String passwd = preferences.getString("passwd", null);
        
        // 尝试重新连接
        // 初始化连接
        // 重新连接
        if (userId != null && passwd != null)
        {
            Log.i("Dateout", "XMPPConnectionAssit==>" + "正在尝试重新登录");
            // 尝试重新登录
            int loginResult = -1;
            try
            {
                loginResult = loginWithResult(app.getConnection(), userId, passwd);
            }
            catch (TimeoutException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (loginResult != VariableHolder.LOGIN_SECCESS)
            {
                Log.i("Dateout", "XMPPConnectionAssit==>" + "重新登录失败!");
                try
                {
                    Thread.sleep(5 * 1000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                XMPPConnectionAssit connectionAssit = new XMPPConnectionAssit(app.getServerIpInUse());
                connectionAssit.initConnection();
                connectionAssit.reConnect(app.getConnection());
                try
                {
                    loginResult = loginWithResult(app.getConnection(), userId, passwd);
                }
                catch (TimeoutException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Log.i("Dateout", "XMPPConnectionAssit==>" + "重新登录成功!");
            // 设置连接状态监听
            // 连接状态监听,用于在掉线后重连
            XMPPConnectionListener connectionListener = new XMPPConnectionListener();
            app.getConnection().addConnectionListener(connectionListener);
        }
    }
    
    /**
     * @author xin编写 zhang修改
     * @param connection 前面建立的XMPPConnection对象
     * @param account 注册用户名
     * @param password 注册密码
     * @return 注册结果
     */
    public Integer registAccount(XMPPConnection connection, String username, String password)
    {
        try
        {
            connection.connect();
        }
        catch (XMPPException e)
        {
            e.printStackTrace();
            return VariableHolder.REGIST_ERR_CONNECT_FAILURE;
        }
        Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(DateoutApp.getInstance().getServiceName());
        reg.setUsername(username);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
        reg.setPassword(password);
        reg.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
        PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
        PacketCollector collector = connection.createPacketCollector(filter);
        connection.sendPacket(reg);// 发送注册请求包
        IQ result = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        // Stop queuing results
        collector.cancel();// 停止请求results（是否成功的结果）
        if (result == null)
        {
            Log.v("Dateout", "RegistrationAssit==>" + "注册失败,服务器无响应");
            return VariableHolder.REGIST_ERR_SERVER_NO_RESPONSE;
        }
        else if (result.getType() == IQ.Type.RESULT)
        {
            Log.v("Dateout", "RegistrationAssit==>" + "注册成功");
            return VariableHolder.REGIST_SUCCESS;
        }
        else
        { // if (result.getType() == IQ.Type.ERROR)
            if (result.getError().toString().equalsIgnoreCase("conflict(409)"))
            {
                Log.v("Dateout", "RegistrationAssit==>注册失败,用户已存在" + result.getError().toString());
                return VariableHolder.REGIST_ERR_ACCOUNT_EXIST;
            }
            else
            {
                Log.v("Dateout", "RegistrationAssit==>注册失败" + result.getError().toString());
                return VariableHolder.REGIST_ERR_UNKNOWN_FAILURE;
            }
        }
    }
    
    public Integer loginWithResult(final XMPPConnection con, final String id, final String ps)
        throws TimeoutException
    {
        Integer result = VariableHolder.LOGIN_ERROR_OTHER;
        // 超时控制
        final ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<String> call = new Callable<String>()
        {
            public String call()
                throws Exception
            {
                // 开始执行耗时操作
                try
                {
                    // ====登录====
                    con.connect();
                    con.login(id, ps);
                    return VariableHolder.LOGIN_SECCESS + "";// 登录成功
                }
                catch (Exception e)
                {
                    if (e instanceof XMPPException)
                    {
                        XMPPException xe = (XMPPException)e;
                        final XMPPError error = xe.getXMPPError();
                        int errorCode = error.getCode();
                        if (errorCode == 401)
                        {
                            return VariableHolder.LOGIN_ERROR_ACCOUNT_NOTPASS + "";// 帐号或密码错误
                        }
                        else if (errorCode == 403)
                        {
                            return VariableHolder.LOGIN_ERROR_ACCOUNT_NOTPASS + "";// 帐号或密码错误
                        }
                        else
                        {
                            return VariableHolder.LOGIN_ERROR_SERVER_UNAVAILABLE + "";// 服务器无法连接
                        }
                    }
                    else
                    {
                        return VariableHolder.LOGIN_ERROR_OTHER + "";// 其他原因错误
                    }
                }
            }
        };
        
        Future<String> future = exec.submit(call);
        // 超时设置
        String strResult = null;
        try
        {
            strResult = future.get(1000 * 30, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        } // 任务处理超时时间设为 30秒
        result = Integer.parseInt(strResult);// 这个才是登录操作真正的返回值哦
        // 好蛋疼,这个call对象只能返回String,需要把登录结果转换为String,再转为int
        exec.shutdown();// 关闭线程池
        return result;
    }
    
    public void changePassword()
    {
        
    }
    
    /**
     * 删除好友
     * 
     * @param roster
     * @param userId 待删除的用户id
     * @return true表示删除成功
     */
    public boolean deleteFriend(Roster roster, String userId)
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
    
    /**
     * 给定用户Id,返回该用户的备注名
     * 
     * @param userId 需要转换的用户Id
     * @param con 有效的XMPPConnnection对象
     * @return 返回用户备注名,null表示不存在备注
     */
    public String userId2RemarkName(XMPPConnection con, String userId)
    {
        RosterEntry entry = con.getRoster().getEntry(userId + "@" + DateoutApp.getInstance().getServiceName());
        if (entry != null)
        {
            String remarkName = entry.getName();
            if (remarkName == null || remarkName.equals(""))
            {
                return userId;
            }
            else
            {
                return remarkName;
            }
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 用户将用户Id转换为JabberID(jid),一般情况下,jid=userId@serverhost/Smack这样的形式
     * 
     * @param userId 用户Id
     * @return 返回用户Jid
     */
    public String userId2UserJid(String userId)
    {
        return userId + "@" + DateoutApp.getInstance().getServiceName();
    }
    
    public String userJid2UserId(String userJid)
    {
        return userJid.split("@")[0];// 即@符号之前的部分
    }
}
