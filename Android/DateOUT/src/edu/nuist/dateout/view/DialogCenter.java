package edu.nuist.dateout.view;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.core.XMPPConnectionAssit;

public class DialogCenter
{
    /**
     * 弹出设置网络对话框
     * 
     * @param context
     */
    public void popNetworkSettingDialog(final Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络未连接");
        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setPositiveButton("设置", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // 跳转到网络设置页面
                Intent intent =  new Intent(Settings.ACTION_SETTINGS);  
                context.startActivity(intent);
                
//                Intent intent = null;
//                /**
//                 * 判断手机系统的版本！如果API大于10 就是3.0+ 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
//                 */
//                if (android.os.Build.VERSION.SDK_INT > 10)
//                {
//                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
//                }
//                else
//                {
//                    intent = new Intent();
//                    ComponentName component =
//                        new ComponentName("com.android.phone", "com.android.phone.WirelessSettings");
//                    intent.setComponent(component);
//                    intent.setAction("android.intent.action.VIEW");
//                }
//                context.startActivity(intent);
            }
        });
        
        builder.setNegativeButton("取消", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // 取消设置
            }
        });
        builder.create();
        builder.show();
    }
    
    /**
     * 弹出重新登录对话框
     * 
     * @param context
     */
    public void popReloginDialog(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("连接已断开!");
        builder.setMessage("检测到帐号在另一台设备登录,请选择操作");
        builder.setPositiveButton("修改密码", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO 跳转到修改密码页面
                
            }
        });
        
        builder.setNegativeButton("重新登录", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO 注销登录,返回登录页面
                
            }
        });
        
        builder.setNeutralButton("取消", new OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
                // TODO 销毁对话框
            }
            
        });
        builder.create();
        builder.show();
    }
    
    /**
     * 弹出处理好友添加请求对话框,用在FriendListManager中
     * 
     * @param context 上下文
     * @param fromId 要加我为好友的用户的Id
     * @param conn 有效的XMPPConnection对象
     */
    public void popAcceptSubscribeDialog(Context context, final String fromId, final XMPPConnection conn)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(fromId + "想要添加你为好友");
        builder.setPositiveButton("接受", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                // 点击确定后
                // subscribed
                Presence presenceAccept = new Presence(Presence.Type.subscribed);
                presenceAccept.setFrom(conn.getUser());
                presenceAccept.setTo(fromId);
                conn.sendPacket(presenceAccept);
                
                // 添加好友
                Presence presenceAdd = new Presence(Presence.Type.subscribe);
                presenceAdd.setFrom(conn.getUser());
                presenceAdd.setTo(fromId);
                conn.sendPacket(presenceAdd);
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                // 点击取消后
                Presence presenceDecline = new Presence(Presence.Type.unsubscribed);
                presenceDecline.setFrom(conn.getUser());
                presenceDecline.setTo(fromId);
                conn.sendPacket(presenceDecline);
            }
        });
        builder.create().show();
    }
    
    public void popDeleteFriendConfirmDialog(final String userToDeleteId, final Context context)
    {
        final DateoutApp app = DateoutApp.getInstance();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("确定要删除该用户？");
        builder.setMessage("删除之后将无法恢复,您与用户"+userToDeleteId+"的聊天记录也将被删除");
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
                    if (userToDeleteId == null)
                    {
                        System.out.println("userToDeleteId为空");
                    }
                    else
                    {
                        boolean deleteSuccess = new XMPPConnectionAssit().deleteFriend(app.getRoster(), userToDeleteId);
                        if (deleteSuccess)
                        {
                            Toast.makeText(context, "成功删除用户" + userToDeleteId, Toast.LENGTH_SHORT).show();
                            Log.v("Dateout", "FriendDetailActivity==>" + "删除了用户" + userToDeleteId);
                        }
                        else
                        {
                            Toast.makeText(context, "暂时无法删除用户" + userToDeleteId, Toast.LENGTH_SHORT).show();
                            Log.v("Dateout", "FriendDetailActivity==>" + "无法删除用户" + userToDeleteId);
                        }
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
    
    public void popSaveImageDialog(final Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("保存图片");
        builder.setMessage("是否想要存储聊天图片");
        
        builder.setPositiveButton("保存", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
        });
        
        builder.setNeutralButton("取消", new OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
            
        });
        builder.create();
        builder.show();
    }
    
    public void popClearAllCacheDialog(final Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("清理所有缓存");
        builder.setMessage("是否想要清空所有数据");
        
        builder.setPositiveButton("确定", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
        });
        
        builder.setNeutralButton("取消", new OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
            
        });
        builder.create();
        builder.show();
    }
}
