package edu.nuist.dateout.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuist.dateout.R;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.tab.FinderManager;
import edu.nuist.dateout.tab.FriendListManager;
import edu.nuist.dateout.tab.RecentMsgManager;
import edu.nuist.dateout.tab.SettingManager;

/**
 * @author Veayo 程序主框架,包含底部4栏项目 用于切换Fragment,接收与转发聊天消息
 */
public class MainActivity extends FragmentActivity implements OnClickListener
{
    private DateoutApp app;
    
    private FragmentManager fragmentManager;
    
    public static MainActivity instance= null;
    // 4个Fragment布局
    private View view1;
    
    private View view2;
    
    private View view3;
    
    private View view4;
    
    // 4个图片Tab按钮
    private ImageView tabImage1;
    
    private ImageView tabImage2;
    
    private ImageView tabImage3;
    
    private ImageView tabImage4;
    
    // 4个Fragment的管理器
    private FinderManager tabManager1;
    
    private FriendListManager tabManager2;
    
    private RecentMsgManager tabManager3;
    
    private SettingManager tabManager4;
    
  //4个textview，设置字体颜色
//    ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  	private TextView shakeTextView;
  	private TextView addressTextView;
  	private TextView friendTextView;
  	private TextView settingTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_main);
        app=(DateoutApp)this.getApplication();
        
        instance = this;
        fragmentManager = getSupportFragmentManager();
        
        initAllFragments();
        
        initViews();
        // 第一次启动时选中第0个tab
        setTabSelection(1);
        app.setCurrentTabNum(1);
    }
    
    private void initViews()
    {
        
        view1 = findViewById(R.id.tab_1);
        view2 = findViewById(R.id.tab_2);
        view3 = findViewById(R.id.tab_3);
        view4 = findViewById(R.id.tab_4);
        
        tabImage1 = (ImageView)findViewById(R.id.image_weixin);
        tabImage2 = (ImageView)findViewById(R.id.image_address);
        tabImage3 = (ImageView)findViewById(R.id.image_friend);
        tabImage4 = (ImageView)findViewById(R.id.image_setting);
        
        shakeTextView = (TextView) findViewById(R.id.tv_main_shake);
		addressTextView = (TextView) findViewById(R.id.tv_main_address);
		friendTextView = (TextView) findViewById(R.id.tv_main_friend);
		settingTextView = (TextView) findViewById(R.id.tv_main_setting);
		
        view1.setOnClickListener(this);
        view2.setOnClickListener(this);
        view3.setOnClickListener(this);
        view4.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tab_1:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                app.setCurrentTabNum(0);
                tabListenner.clickTab1();
                changeListener.clickTab(0);
                break;
            case R.id.tab_2:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                app.setCurrentTabNum(1);
                changeListener.clickTab(1);
                break;
            case R.id.tab_3:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                app.setCurrentTabNum(2);
                changeListener.clickTab(2);
                break;
            case R.id.tab_4:
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(3);
                app.setCurrentTabNum(3);
                changeListener.clickTab(3);
                break;
            default:
                break;
        }
    }
    
    private void setTabSelection(int index)
    {
        clearSelection();
        FragmentTransaction t = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(t);
        
        switch (index)
        {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                tabImage1.setImageResource(R.drawable.tab1_pressed);
                shakeTextView.setTextColor(getResources().getColor(R.color.item_font_blue));
                if (tabManager1 == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    tabManager1 = new FinderManager();
                    t.add(R.id.content, tabManager1);
                }
                else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    t.show(tabManager1);
                }
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                tabImage2.setImageResource(R.drawable.tab2_pressed);
                addressTextView.setTextColor(getResources().getColor(R.color.item_font_blue));
                if (tabManager2 == null)
                {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    tabManager2 = new FriendListManager();
                    t.add(R.id.content, tabManager2);
                }
                else
                {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    t.show(tabManager2);
                }
                break;
            case 2:
                // 当点击了动态tab时，改变控件的图片和文字颜色
                tabImage3.setImageResource(R.drawable.tab3_pressed);
                friendTextView.setTextColor(getResources().getColor(R.color.item_font_blue));
                if (tabManager3 == null)
                {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    tabManager3 = new RecentMsgManager();
                    t.add(R.id.content, tabManager3);
                }
                else
                {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    t.show(tabManager3);
                }
                break;
            case 3:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                tabImage4.setImageResource(R.drawable.tab4_pressed);
                settingTextView.setTextColor(getResources().getColor(R.color.item_font_blue));
                if (tabManager4 == null)
                {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    tabManager4 = new SettingManager();
                    t.add(R.id.content, tabManager4);
                }
                else
                {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    t.show(tabManager4);
                }
                break;
            default:
                break;
        }
        
        t.commit();
    }
    
    private void hideFragments(FragmentTransaction transaction)
    {
        if (tabManager1 != null)
        {
            transaction.hide(tabManager1);
        }
        if (tabManager2 != null)
        {
            transaction.hide(tabManager2);
        }
        if (tabManager3 != null)
        {
            transaction.hide(tabManager3);
        }
        if (tabManager4 != null)
        {
            transaction.hide(tabManager4);
        }
    }
    
    private void clearSelection()
    {
//      ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	addressTextView.setTextColor(getResources().getColor(R.color.white));
	    shakeTextView.setTextColor(getResources().getColor(R.color.white));
	    friendTextView.setTextColor(getResources().getColor(R.color.white));
	    settingTextView.setTextColor(getResources().getColor(R.color.white));
	    
        tabImage1.setImageResource(R.drawable.tab1_normal);
        tabImage2.setImageResource(R.drawable.tab2_normal);
        tabImage3.setImageResource(R.drawable.tab3_normal);
        tabImage4.setImageResource(R.drawable.tab4_normal);
    }
    
    // 初始化所有4个类
    private void initAllFragments()
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // TODO 将对象初始化动作放到后台线程
        tabManager1 = new FinderManager();
        tabManager2 = new FriendListManager();
        tabManager3 = new RecentMsgManager();
        tabManager4 = new SettingManager();
        
        transaction.add(R.id.content, tabManager1);
        transaction.hide(tabManager1);
        transaction.add(R.id.content, tabManager2);
        transaction.hide(tabManager2);
        transaction.add(R.id.content, tabManager3);
        transaction.hide(tabManager3);
        transaction.add(R.id.content, tabManager4);
        transaction.hide(tabManager4);
        
        transaction.commit();
    }
    
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
    
    /**
     * 按了返回按钮后不退出程序,而是转到后台运行,相当于点击HOME键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(false);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_MENU){
            //自定义菜单按钮点击事件
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**点tab1的监听*/
    public interface DateoutTabListenner{
    	void   clickTab1();
    }
    private DateoutTabListenner tabListenner;
    
    public void setOnDateoutTabListenner(DateoutTabListenner tabListenner2){
    	this.tabListenner = tabListenner2;
    }
    
    /**tab切换监听*/
    public interface TabChangeListener{
    	void clickTab(int tab);
    }
    private TabChangeListener changeListener;
    public void SetOnTabChangeListener(TabChangeListener changeListener){
    	this.changeListener = changeListener;
    }
}
