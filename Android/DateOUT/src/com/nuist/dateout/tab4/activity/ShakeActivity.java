package com.nuist.dateout.tab4.activity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.nuist.dateout.R;
import com.nuist.dateout.listener.MyOnGesTureListener;
import com.nuist.dateout.tab4.activity.ShakeListener.OnShakeListener;

import edu.nuist.dateout.activity.MapActivity;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.UserLoc;
import edu.nuist.dateout.model.UserNear;
import edu.nuist.dateout.task.UploadLocAndGetUsersNearTask;
import edu.nuist.dateout.value.AppConfig;

public class ShakeActivity extends Activity
{
    private ImageButton shakeTopBtn;
    
    private LocationClient mLocationClient;
    
    private MyLocationListener mLocationListener;
    
    ShakeListener mShakeListener = null;
    
    Vibrator mVibrator;
    
    private RelativeLayout mImgUp;
    
    private RelativeLayout mImgDn;
    
    private GestureDetector detector;
    
    // 用于存储摇动后获取到的地理位置经纬度
    LatLng locLatLng = null;
    
    private ImageView shakeBackgroudImageView;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake_activity);
        
        mVibrator = (Vibrator)getApplication().getSystemService(VIBRATOR_SERVICE);
        
        mImgUp = (RelativeLayout)findViewById(R.id.shakeImgUp);
        mImgDn = (RelativeLayout)findViewById(R.id.shakeImgDown);
        shakeTopBtn = (ImageButton)findViewById(R.id.ib_shake_topBtn);
        shakeBackgroudImageView = (ImageView)findViewById(R.id.iv_shake_bj);
        
        detector = new GestureDetector(this, new MyOnGesTureListener(this));
        
        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new OnShakeListener()
        {
            public void onShake()
            {
                dealShake();
            }
        });
        shakeTopBtn.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                dealShake();
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return detector.onTouchEvent(event);
    }
    
    private void dealShake()
    {
        startAnim(); // 开始 摇一摇手掌动画
        mShakeListener.stop();
        startVibrato(); // 开始 震动
        
        // 这里代码段执行完了之后动画就结束了
        startLocating(getApplicationContext());
    }
    
    public void startAnim()
    { // 定义摇一摇动画动画
    
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -400);
        TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, 400);
        animation.setFillAfter(true);
        animation2.setFillAfter(true);
        
        animation.setDuration(2000);
        animation2.setDuration(2000);
        
        mImgUp.startAnimation(animation);
        mImgDn.startAnimation(animation2);
        
        earthAnim = (AnimationDrawable)shakeBackgroudImageView.getBackground();
        earthAnim.start();
        
    }
    
    public void startVibrato()
    { // 定义震动
        mVibrator.vibrate(new long[] {500, 200, 500, 200}, -1); // 第一个｛｝里面是节奏数组，
                                                                // 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
    }
    
    public void shake_activity_back(View v)
    { // 标题栏 返回按钮
        this.finish();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mShakeListener != null)
        {
            mShakeListener.stop();
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
    
    private void finishAnim()
    {
        TranslateAnimation animation3 = new TranslateAnimation(0, 0, -300, 0);
        TranslateAnimation animation4 = new TranslateAnimation(0, 0, 300, 0);
        animation3.setFillAfter(true);
        animation4.setFillAfter(true);
        
        animation3.setDuration(500);
        animation4.setDuration(500);
        
        mImgUp.startAnimation(animation3);
        mImgDn.startAnimation(animation4);
        earthAnim.stop();
    }
    
    // TODO 设置连接超时
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    // 对附近的人列表进行简单处理
                    List<UserNear> usersNearList = (List<UserNear>)msg.obj;
                    if (usersNearList.size() > 0)
                    {
                        // 附近有人
                        Toast.makeText(ShakeActivity.this, "找到" + usersNearList.size() + "个附近的陌生人", Toast.LENGTH_SHORT)
                            .show();
                        // 启动地图
                        Intent intent = new Intent();
                        intent.setClass(ShakeActivity.this, MapActivity.class);
                        intent.putExtra("shakeLocLat", locLatLng.latitude);
                        intent.putExtra("shakeLocLng", locLatLng.longitude);
                        intent.putExtra("usersNearList", (Serializable)usersNearList);
                        startActivity(intent);
                        // 结束当前Activity
                        finish();
                    }
                    else
                    {
                        // 找不到附近的人
                        Toast.makeText(ShakeActivity.this,
                            "附近" + (int)AppConfig.DISTANCE_NEAR + "米内一个用户都没有=.=",
                            Toast.LENGTH_LONG).show();
                        finishAnim();// 停止动画
                    }
                    break;
                case 5:
                    // 出错
                    Toast.makeText(ShakeActivity.this, "连接超时", Toast.LENGTH_LONG).show();
                    finishAnim();// 停止动画
                    break;
                case 3:
                    // 出错
                    Toast.makeText(ShakeActivity.this, "URL参数异常", Toast.LENGTH_LONG).show();
                    finishAnim();// 停止动画
                    break;
                default:
                    // 出错
                    Toast.makeText(ShakeActivity.this, "出错,其他异常", Toast.LENGTH_LONG).show();
                    finishAnim();// 停止动画
                    break;
            }
            mVibrator.cancel();
            mShakeListener.start();
        }
    };
    
    private AnimationDrawable earthAnim;
    
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
            
            locLatLng = new LatLng(latitude, longitude);
            
            // 建立自身位置模型
            String userId = DateoutApp.getInstance().getLoginUser().getUserId();
            UserLoc user = new UserLoc();
            user.setLocJingdu(longitude);
            user.setLocWeidu(latitude);
            user.setUserId(userId);
            user.setTime(new Timestamp(System.currentTimeMillis()));
            
            Toast.makeText(ShakeActivity.this,
                "经度:" + longitude + ",纬度:" + latitude + " 开始上传位置并获取附近的人",
                Toast.LENGTH_SHORT).show();
            // 上传位置 获取好友列表
            new UploadLocAndGetUsersNearTask(user, handler).execute();
        }
    }
}
