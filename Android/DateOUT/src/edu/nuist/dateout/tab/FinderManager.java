package edu.nuist.dateout.tab;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nuist.dateout.tab4.activity.GameInfoActivity;
import com.nuist.dateout.tab4.activity.ShakeActivity;

import com.nuist.dateout.R;
import edu.nuist.dateout.activity.MainActivity;
import edu.nuist.dateout.activity.MainActivity.DateoutTabListenner;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.UserLoc;
import edu.nuist.dateout.model.UserRandom;
import edu.nuist.dateout.task.FetchRandomUsersTask;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.NetworkAssit;
import edu.nuist.dateout.view.MyGridViewAdapter;

public class FinderManager extends Fragment implements OnClickListener
{
    // 定位相关
    private LocationClient mLocationClient;
    
    private MyLocationListener mLocationListener;
    
    // 用于存储摇动后获取到的地理位置经纬度
    // private LatLng locLatLng = null;
    
    // 存储随机的用户
    private List<UserRandom> userRandomList;
    
    private DateoutApp app;
    
    private Context context;
    
    private GridView recommendFriendView;
    
    private ImageButton refreshButton;
    
    private ImageButton shakeButton;
    
    private MyGridViewAdapter adapter;
    
    private SwipeRefreshLayout freshLayout;
    
    private UserLoc userLoc;
    
    private boolean allowRefresh;// 用于标记是否允许用户刷新
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_1, container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        
        app = DateoutApp.getInstance();
        context = getActivity();
        
        allowRefresh = true;// 此时允许刷新
        // 用户位置对象初始化
        userLoc = new UserLoc();
        userLoc.setUserId(app.getLoginUser().getUserId());
        
        // 找控件Id,设置监听
        initComponents();
        
        // 使用默认列表
        loadDefaultUserList();
        adapter = new MyGridViewAdapter(getActivity(), userRandomList);
        recommendFriendView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        // 定位
        startLocating(getActivity().getApplicationContext());
    }
    
    private void initComponents()
    {
        recommendFriendView = (GridView)getActivity().findViewById(R.id.gv_finder_recommend);
        refreshButton = (ImageButton)getActivity().findViewById(R.id.ib_tab1_refresh);
        shakeButton = (ImageButton)getActivity().findViewById(R.id.ib_tab1_shake);
        freshLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.srl_tab1_refresh);
        freshLayout.setColorScheme(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
        
        recommendFriendView.setOnItemClickListener(new MyOnitemClickListener());
        freshLayout.setOnRefreshListener(new MyonRefreshListener());
        refreshButton.setOnClickListener(this);
        shakeButton.setOnClickListener(this);
        // 点击Tab1监听
        MainActivity.instance.setOnDateoutTabListenner(new DateoutTabListenner()
        {
            @Override
            public void clickTab1()
            {
                // 点击Tab1,响应事件，开始动画
                handler.sendEmptyMessageDelayed(SRART_ANIM, 3000);
            }
        });
    }
    
    /** 下拉刷新的监听类 */
    private class MyonRefreshListener implements SwipeRefreshLayout.OnRefreshListener
    {
        @Override
        public void onRefresh()
        {
            if (allowRefresh)
            {
                // 顶部进度条动起来
                freshLayout.setRefreshing(true);
                userLoc.setTime(new Timestamp(System.currentTimeMillis()));
                /*
                 * 如果需要每次刷新都重新定位,则反注释这条语句 startLocating(getActivity().getApplicationContext());
                 */
                refreshRandomUsersList(userLoc);// 加载网络列表
            }
            else
            {
                Toast.makeText(getActivity(), "正在处理上一次请求 =.=", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    public void onResume()
    {
        super.onResume();
        System.out.println("onResume");
        if (!this.isHidden())
        {
            handler.sendEmptyMessageDelayed(SRART_ANIM, 2000);// 获得焦点时播放动画
        }
    };
    
    public void onPause()
    {
        handler.removeMessages(SRART_ANIM);// 获得焦点时播放动画
        super.onPause();
    };
    
    /** 控制动画播放的handler */
    public static final int SRART_ANIM = 0x18;
    
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == SRART_ANIM)
            {
                
                System.out.println("handler");
                final int max = userRandomList.size();
                if (max > 0)
                {
                    Random random = new Random();
                    int randomItem = random.nextInt(max);
                    if (randomItem >= 0 && randomItem <= 23)
                    {
                        startAnim(randomItem);
                    }
                    
                    if (FinderManager.this.isHidden())
                    {
                        System.out.println("stop" + DateoutApp.getInstance().getCurrentTabNum());
                        handler.removeMessages(SRART_ANIM);
                    }
                    else
                    {
                        System.out.println("keepstart" + DateoutApp.getInstance().getCurrentTabNum());
                        handler.sendEmptyMessageDelayed(SRART_ANIM, 1500);
                    }
                }
            }
        }
    };
    
    /** 开始动画 */
    public void startAnim(int resId)
    {
        AlphaAnimation alphaAnimation = new AlphaAnimation((float)1, (float)0.2);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        
        View itemView = recommendFriendView.getChildAt(resId);
        if (itemView == null)
        {
            // 防止空指针异常
        }
        else
        {
            itemView.startAnimation(alphaAnimation);
        }
    }
    
    // 随机加载用户列表
    private void refreshRandomUsersList(UserLoc userLoc)
    {
        allowRefresh = false;// 开始加载数据,不再允许下拉刷新
        Handler fetchTaskHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case 1:
                        if (msg.obj != null)
                        {
                            // 加载好友
                            userRandomList = (List<UserRandom>)msg.obj;
                            // 更新列表
                            adapter.setUserRandomList(userRandomList);
                            adapter.notifyDataSetChanged();
                            freshLayout.setRefreshing(false);// 停止进度条
                            allowRefresh = true;// 只有数据加载完成并显示之后才允许再次刷新
                        }
                        break;
                    case 2:
                        // 列表为空
                        Toast.makeText(context, "找不到用户", Toast.LENGTH_SHORT).show();
                        freshLayout.setRefreshing(false);// 停止进度条
                        allowRefresh = true;// 只有数据加载完成并显示之后才允许再次刷新
                        break;
                    case 3:
                        // 出错或者连接超时
                        Toast.makeText(context, "服务端未启动或者IP配置出错", Toast.LENGTH_SHORT).show();
                        freshLayout.setRefreshing(false);// 停止进度条
                        allowRefresh = true;// 只有数据加载完成并显示之后才允许再次刷新
                        break;
                    default:
                        break;
                }
            };
        };
        
        // 检测网络
        NetworkAssit netAssit = new NetworkAssit(context);
        if (netAssit.isNetworkConnected())
        {
            new FetchRandomUsersTask(userLoc, fetchTaskHandler).execute();
        }
        else
        {
            Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
            freshLayout.setRefreshing(false);// 停止进度条
        }
    }
    
    // 在加载完随机用户列表之前先使用默认的一批照片
    private void loadDefaultUserList()
    {
        ArrayList<UserRandom> defaultUserList = new ArrayList<UserRandom>();
        for (int i = 0; i < 24; i++)
        {
            UserRandom user = new UserRandom();
            user.setDistance(500);
            Uri defaultHead = FormatTools.resId2Uri(getResources(), R.drawable.default_head);
            user.setHeadUrl(defaultHead.toString());
            user.setOnlineStat((short)1);
            user.setUserId(null);
            
            defaultUserList.add(user);
        }
        this.userRandomList = defaultUserList;
    }
    
    /**
     * GridView监听器
     * 
     * @author liyuxin
     *
     */
    class MyOnitemClickListener implements OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // Toast.makeText(getActivity(), "点击了第" + position+"个", Toast.LENGTH_SHORT).show();
            
            String userIdClicked = userRandomList.get(position).getUserId();
            if (userIdClicked == null || userIdClicked.equals(""))
            {
                Toast.makeText(getActivity(), "用户无效,请等待加载完成", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), "点击了用户: " + userIdClicked + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(), GameInfoActivity.class);
                intent.putExtra("userid_game", userIdClicked);
                startActivity(intent);
            }
        }
    }
    
    private void startLocating(Context context)
    {
        // mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        // mBaiduMap.setMyLocationEnabled(true);
        // 1. 初始化LocationClient类
        mLocationClient = new LocationClient(context);
        // 2. 声明LocationListener类
        mLocationListener = new MyLocationListener();
        // 3. 注册监听函数
        mLocationClient.registerLocationListener(mLocationListener);
        // 4. 设置参数
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间,ms
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 设置返回结果包含手机的方向
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
    
    private class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (location == null)
            {
                return;
            }
            // 获取到了位置
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            mLocationClient.stop();
            
            // 设置当前用户位置信息
            userLoc.setLocJingdu(longitude);
            userLoc.setLocWeidu(latitude);
            userLoc.setTime(new Timestamp(System.currentTimeMillis()));
            
            // 顶部进度条动起来
            freshLayout.setRefreshing(true);
            refreshRandomUsersList(userLoc);// 加载网络列表
        }
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ib_tab1_refresh:
                // 点击刷新按钮
                // 顶部进度条动起来
                freshLayout.setRefreshing(true);
                userLoc.setTime(new Timestamp(System.currentTimeMillis()));
                refreshRandomUsersList(userLoc);
                break;
            case R.id.ib_tab1_shake:
                // 点击摇一摇按钮
                Intent intent = new Intent(getActivity(), ShakeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
