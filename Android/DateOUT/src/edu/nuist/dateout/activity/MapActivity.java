package edu.nuist.dateout.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nuist.dateout.R;
import com.nuist.dateout.tab4.activity.GameInfoActivity;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.model.UserNear;
import edu.nuist.dateout.task.FetchDownLinkTask;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.NetworkAssit;
import edu.nuist.dateout.util.TimeAssit;
import edu.nuist.dateout.util.VCardAssit;
import edu.nuist.dateout.value.VariableHolder;

//TODO 修复该Activity在安卓4.4系统上的奔溃问题

public class MapActivity extends Activity implements OnClickListener
{
    private DateoutApp app;
    
    private MapView mMapView;
    
    private BaiduMap mBaiduMap;
    
    private ImageButton locateButton;
    
    // 定位相关
    private LocationClient mLocationClient;
    
    private MyLocationListener mLocationListener;
    
    private MyLocationConfiguration.LocationMode mLocationMode;
    
    private double longitude;// 经度
    
    private double latitude;// 纬度
    
    private float radius;// 定位精度半径，单位是米
    
    private float direction;// 手机方向信息
    
    /** 用来显示好友信息的布局 */
    private View userInfoView;
    
    private ImageView headImageView;
    
    private TextView userNameTextView;
    
    private TextView distancetTextView;
    
    private TextView shakeTimetTextView;
    
    private String userIdClicked;// 存储被点击的用户的ID
    
    private List<UserNear> usersNearlist;
    
    /** 测试数据 **/
    // public static int index = 2;
    
    // /** 游戏难度 */
    // private int level = 3;
    //
    // /** 游戏时间 */
    // private int time = 50;
    //
    // /** 游戏图片 */
    // private Bitmap gameBitmap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        app = DateoutApp.getInstance();
        
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.act_map);
        locateButton = (ImageButton)findViewById(R.id.btn_map_locate);
        locateButton.setOnClickListener(new MyClicklisen());
        initView();
        // 定个位
        // locateMe();
        // 设置用户本人初始的位置
        setMyInitialLocation();
        // 初始化组件
        initComponent();
        // 显示附近的人
        usersNearlist = new ArrayList<UserNear>();
        initUsersNear();
    }
    
    /**
     * 显示附近的人到地图上
     */
    private void initUsersNear()
    {
        usersNearlist = (List<UserNear>)this.getIntent().getSerializableExtra("usersNearList");
        // 收到好友列表
        // 取出用户ID,经纬度,距离,最近定位的时间
        for (int i = 0; i < usersNearlist.size(); i++)
        {
            UserNear userNear = usersNearlist.get(i);
            
            // 查找昵称
            String userId = userNear.getUserId();
            // 通过UserId得到用户Vcard,从而得到用户的昵称(TODO 以后需要改为从服务端填充用户昵称)
            CustomVcard vCard = new VCardAssit(DateoutApp.getInstance().getConnection()).loadMyVCard(userId);
            userNear.setNickName(vCard.getNickName());// 添加用户昵称
            // 把用户显示到地图上
            putUserNearOnMap(userNear);
        }
    }
    
    private void initView()
    {
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mMapView.removeViewAt(1);
        mBaiduMap = mMapView.getMap();
        
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
    }
    
    private void initComponent()
    {
        userInfoView = findViewById(R.id.lo_map_user_info);
        Button hideButton = (Button)findViewById(R.id.btn_map_hide_user_view);
        Button addFriendButton = (Button)findViewById(R.id.btn_map_add_friend);
        headImageView = (ImageView)findViewById(R.id.iv_map_head);
        userNameTextView = (TextView)findViewById(R.id.tv_map_user_name);
        distancetTextView = (TextView)findViewById(R.id.tv_map_user_distance);
        shakeTimetTextView = (TextView)findViewById(R.id.tv_map_user_time);
        
        hideButton.setOnClickListener(this);
        addFriendButton.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_map_hide_user_view:
                hideView();
                break;
            case R.id.btn_map_add_friend:
                GotoGameInfoActivity();
                // GotoGame();
                break;
            default:
                break;
        }
    }
    
    /** 隐藏附近好友信息布局 */
    private void hideView()
    {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.map_card_alpha_hide);
        userInfoView.startAnimation(animation);
        userInfoView.setVisibility(View.GONE);
    }
    
    private void GotoGameInfoActivity()
    {
        Intent intent = new Intent();
        intent.setClass(MapActivity.this, GameInfoActivity.class);
        intent.putExtra("userid_game", userIdClicked);// 传入一个用户Id
        startActivity(intent);
    }
    
    private void putUserNearOnMap(UserNear userNear)
    {
        // 定义Maker坐标点
        Marker marker;
        final LatLng point = new LatLng(userNear.getLocWeidu(), userNear.getLocJingdu());
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        marker = (Marker)mBaiduMap.addOverlay(option);
        // 为marker附加一个标志
        Bundle bundle = new Bundle();
        bundle.putSerializable("user_near", userNear);
        marker.setExtraInfo(bundle);
        
        LatLng llText = point; // 定义文字所显示的坐标点
        String displayName;
        if (userNear.getNickName() == null || userNear.getNickName().equals(""))
        {
            displayName = userNear.getUserId();
        }
        else
        {
            displayName = userNear.getNickName();
        }
        // 构建文字Option对象，用于在地图上添加文字
        OverlayOptions textOption =
            new TextOptions().fontSize(24).fontColor(0xFFFF00FF).text(displayName).position(llText);
        // 在地图上添加该文字对象并显示
        mBaiduMap.addOverlay(textOption);
        // 注册覆盖物监听事件
        mBaiduMap.setOnMarkerClickListener(new MyOnMarkerClickListener());// 点击地图上的人
    }
    
    private void setMyInitialLocation()
    {
        // 获取从ShakeActivity传来的经纬度坐标
        double lat = getIntent().getDoubleExtra("shakeLocLat", 0);// 纬度
        double lng = getIntent().getDoubleExtra("shakeLocLng", 0);// 经度
        if (lat == 0 && lng == 0)
        {
            locateMe();// 重新定位
            mLocationClient.start();
        }
        else
        {
            locateMe();
            // LatLng shakeLocation = new LatLng(lat, lng);
            MyLocationData locData = new MyLocationData.Builder().accuracy(radius)//
                // .direction(0)
                // 方向
                .latitude(lat)
                //
                .longitude(lng)
                .build();
            
            // 显示自己到地图上
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(lat, lng);
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
            // msu = MapStatusUpdateFactory.zoomTo(14.0f);
            mBaiduMap.animateMapStatus(msu);
        }
    }
    
    private void locateMe()
    {
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap.setMyLocationEnabled(true);
        // 1. 初始化LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 2. 声明LocationListener类
        mLocationListener = new MyLocationListener();
        // 3. 注册监听函数
        mLocationClient.registerLocationListener(mLocationListener);
        // 4. 设置参数
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(10 * 000);// 设置发起定位请求的间隔时间,ms
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 设置返回结果包含手机的方向
        mLocationClient.setLocOption(option);
        
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mMapView.onDestroy();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        mMapView.onResume();
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        mMapView.onPause();
    }
    
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
    
    private class MyLocationListener implements BDLocationListener
    {
        // 获取到了位置
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (location == null)
            {
                return;
            }
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (location.hasRadius())
            {// 判断是否有定位精度半径
                radius = location.getRadius();
            }
            MyLocationData locData = new MyLocationData.Builder().accuracy(radius)//
                .direction(direction)
                // 方向
                .latitude(latitude)
                //
                .longitude(longitude)
                .build();
            
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(latitude, longitude);
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
            // msu = MapStatusUpdateFactory.zoomTo(14.0f);
            mBaiduMap.animateMapStatus(msu);
            
            // 停止继续定位
            mLocationClient.stop();
        }
    }
    
    /** marcker的监听类 */
    private class MyOnMarkerClickListener implements OnMarkerClickListener
    {
        
        public boolean onMarkerClick(Marker marker)
        {
            // 封装了附近好友信息
            UserNear userNear = (UserNear)marker.getExtraInfo().get("user_near");
            userIdClicked = userNear.getUserId();
            // 显示昵称
            String displayName;
            if (userNear.getNickName() == null || userNear.getNickName().equals(""))
            {
                displayName = userNear.getUserId();
            }
            else
            {
                displayName = userNear.getNickName();
            }
            userNameTextView.setText(displayName);
            // 通过userId去查找用户头像的下载链接
            // 先给个默认的头像显示着
            Uri defaultHeadImageUri = FormatTools.resId2Uri(getResources(), R.drawable.default_head);
            ImageLoader.getInstance().displayImage(defaultHeadImageUri.toString(), headImageView);
            
            if (new NetworkAssit(getApplicationContext()).isNetworkConnected())
            {
                Handler handler = new Handler()
                {
                    public void handleMessage(android.os.Message osMsg)
                    {
                        switch (osMsg.what)
                        {
                            case 1:
                                List<String> downloadLink = (List<String>)osMsg.obj;
                                if (downloadLink != null)
                                {
                                    if (downloadLink.size() > 0)
                                    {
                                        ImageLoader.getInstance().displayImage(downloadLink.get(0), headImageView);
                                    }
                                }
                                break;
                            
                            default:
                                break;
                        }
                    };
                };
                // 后台联网获取用户头像
                String paramFilePrefix = VariableHolder.FILE_PREFIX_IMAGE_HEAD + userIdClicked;
                new FetchDownLinkTask(paramFilePrefix, handler).execute();
            }
            else
            {
                Toast.makeText(MapActivity.this, "网络不可用", Toast.LENGTH_LONG).show();
            }
            
            distancetTextView.setText("距离:" + (int)userNear.getDistance() + "m");
            // 显示为易读的时间格式
            shakeTimetTextView.setText("最近更新:" + TimeAssit.timestamp2FriendlyFormat(userNear.getTime()));
            userInfoView.setVisibility(View.VISIBLE);
            return false;
        }
    }
    
    private class MyClicklisen implements android.view.View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            locateMe();
            mLocationClient.start();
        }
    }
}