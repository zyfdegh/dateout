package edu.nuist.dateout.activity;

import java.io.File;
import java.util.concurrent.TimeoutException;

import org.jivesoftware.smack.XMPPConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dateout.R;
import com.nuist.dialog.MyProgressDialog;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.core.XMPPConnectionAssit;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.HeadImageAssit;
import edu.nuist.dateout.util.NetworkAssit;
import edu.nuist.dateout.util.RegexAssit;
import edu.nuist.dateout.value.AppConfig;
import edu.nuist.dateout.value.VariableHolder;

/**
 * 用户登录
 * 
 * @author zhang
 * @date 2015-03-20
 */
public class LoginActivity extends Activity
{
    private DateoutApp app;
    
    private EditText userIdEditText;
    
    private EditText passwdEditText;
    
    private EditText serverIpEditText;
    
    private ImageView showPWImage;// 显示密码
    
    private Button btnLogin;
    
    private TextView regAccountTextView;
    
    private ImageView headImage;// 用户头像
    
    private Uri headImageUri;
    
    private SharedPreferences preferences;
    
    private SharedPreferences.Editor editor;
    
    private String userId, passwd, serverIp;
    
    /**
     * 密码框的状态
     */
    private static boolean PASSWORD_EDITTEXT_STATE = false;
    
    private TextView forgetPWTextView;// 忘记密码
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        
        app = DateoutApp.getInstance();
        
        // 初始化按钮 文本框
        initComponents();
        // 加载用户名密码和服务器IP的值
        initComponentsValue();
        
        refreshHeadImage(userId);
        
        /**
         * 用于监听文本框userIdEditText的焦点变化
         */
        userIdEditText.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    // 光标移入
                }
                else
                {
                    // 光标离开 更新头像
                    String userIdNew = userIdEditText.getText().toString().trim();
                    refreshHeadImage(userIdNew);
                }
            }
        });
        
        // 点击登录
        btnLogin.setOnClickListener(new OnClickListener()
        {
            // =================用户登录=================
            @Override
            public void onClick(View v)
            {
                userId = userIdEditText.getText().toString().trim();
                passwd = passwdEditText.getText().toString().trim();
                serverIp = serverIpEditText.getText().toString().trim();
                
                // dealFocusView(false);// 使组件失去焦点
                hideSoftInputView();// 隐藏软键盘
                
                // 设置全局IP
                app.setServerIpInUse(serverIp);
                // 用户名密码非空检测
                if (userId.equals("") || passwd.equals(""))
                {
                    Toast.makeText(LoginActivity.this, "用户名或密码为空", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // 检测IP有效性检测
                    if (new RegexAssit().checkIp(serverIp))
                    {
                        // 初始化连接
                        XMPPConnectionAssit connectionAssit = new XMPPConnectionAssit(serverIp);
                        connectionAssit.initConnection();

                        // 超时控制
                        LoginTask loginTask = new LoginTask(LoginActivity.this, app.getConnection(), userId, passwd);
                        loginTask.execute();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "IP非法", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        
        // 点击注册
        regAccountTextView.setOnClickListener(new OnClickListener()
        {
            // =================注册用户=================
            @Override
            public void onClick(View v)
            {
                // 存储IP
                String serverIp = serverIpEditText.getText().toString().trim();
                editor.putString("serverip", serverIp);
                editor.commit();
                
                // 设置全局IP
                app.setServerIpInUse(serverIp);
                // 设置全局连接
                // 初始化连接
                XMPPConnectionAssit connectionAssit = new XMPPConnectionAssit(serverIp);
                connectionAssit.initConnection();
                
                // 跳转到RegistActivity
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
                
                finish();
            }
        });
        
        /**
         * 设置密码显示或隐藏
         */
        showPWImage.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PASSWORD_EDITTEXT_STATE = !PASSWORD_EDITTEXT_STATE;
                if (PASSWORD_EDITTEXT_STATE)
                {
                    // 设置密码框内容显示
                    showPWImage.setImageResource(R.drawable.i_login_showpassword_selecteed);
                    passwdEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    // 设置密码框内容隐藏
                    showPWImage.setImageDrawable(getResources().getDrawable(R.drawable.i_login_showpassword_normal));
                    passwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }
    
    /**
     * 初始化组件按钮 文本框
     */
    void initComponents()
    {
        userIdEditText = (EditText)findViewById(R.id.et_login_name);
        passwdEditText = (EditText)findViewById(R.id.et_login_password);
        btnLogin = (Button)findViewById(R.id.btn_loginview_login);
        regAccountTextView = (TextView)findViewById(R.id.tv_loginview_register);
        serverIpEditText = (EditText)findViewById(R.id.serverIpTextId);
        headImage = (ImageView)findViewById(R.id.iv_login_head);
        showPWImage = (ImageView)findViewById(R.id.iv_loginview_showpasswprd);
        forgetPWTextView = (TextView)findViewById(R.id.tv_loginview_forget_pw);
        
        regAccountTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 设置TextView的下划线
        forgetPWTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }
    
    /**
     * 初始化用户名 密码 服务器IP的值
     */
    void initComponentsValue()
    {
        preferences = getSharedPreferences("dateout", MODE_PRIVATE);
        editor = preferences.edit();
        // 读取保存的ServerIP信息
        String serverIpFromPref = preferences.getString("serverip", null);// 不存在就返回null
        if (serverIpFromPref == null)
        {
            // 使用默认IP
            serverIpEditText.setText(AppConfig.DEFAULT_SERVER_IP);
        }
        else
        {
            // 使用读取到的IP
            serverIpEditText.setText(serverIpFromPref);
        }
        // 取出注册了的用户Id
        String userIdRegistId = app.getRegistUser().getUserId();
        
        if (userIdRegistId != null)
        {
            userIdEditText.setText(userIdRegistId);
            passwdEditText.setText("");
            userId = userIdRegistId;
        }
        else
        {
            userId = preferences.getString("username", null);
            if (userId != null)
            {
                // 设置用户名
                userIdEditText.setText(userId);
            }
            else
            {
                // TODO 移除这里的else语句(为了方便快速调试)
                userId = "user1";
                userIdEditText.setText("user1");
            }
            passwd = preferences.getString("passwd", null);
            if (passwd != null)
            {
                // 设置密码
                passwdEditText.setText(passwd);
            }
            else
            {
                // TODO 移除这里的else语句(为了方便快速调试)
                passwd = "111111";
                passwdEditText.setText("111111");
            }
        }
    }
    
    private void refreshHeadImage(String userIdParam)
    {
        if (userIdParam == null || userIdParam.equals(""))
        {
            // 无效用户名
            useDefaultHeadImage();
            Log.v("Dateout", "LoginActivity==>" + "用户名无效,使用默认头像" + userIdParam);
        }
        else
        {
            // 在本地缓存中查找头像
            File headImageFile = new HeadImageAssit().getHeadImageFromCache(userIdParam);
            if (headImageFile != null && headImageFile.exists() && headImageFile.canRead())
            {
                // 缓存存在
                // 加载本地缓存头像
                headImageUri = Uri.parse(headImageFile.getAbsolutePath());
                headImage.setImageURI(headImageUri);
                app.getLoginUser().setHeadImageUri(headImageUri);
                Log.v("Dateout", "LoginActivity==>" + "使用本地缓存的头像" + userIdParam);
            }
            else
            {
                useDefaultHeadImage();
                Log.v("Dateout", "LoginActivity==>" + "本地缓存找不到用户" + userIdParam + "的头像");
            }
        }
    }
    
    private void useDefaultHeadImage()
    {
        Uri headImageUri = FormatTools.resId2Uri(getResources(), R.drawable.default_head);
        headImage.setImageURI(headImageUri);
        app.getLoginUser().setHeadImageUri(headImageUri);
    }
    
    /** 批量设置失去焦点或者获得焦点 */
    // private void dealFocusView(Boolean isFocus)
    // {
    // if (isFocus)
    // {
    // userIdEditText.setFocusableInTouchMode(isFocus);
    // passwdEditText.setFocusableInTouchMode(isFocus);
    // }
    // else
    // {
    // userIdEditText.setFocusable(isFocus);
    // passwdEditText.setFocusable(isFocus);
    // }
    // btnLogin.setClickable(isFocus);
    // regAccountTextView.setClickable(isFocus);
    // }
    
    /** 隐藏软键盘 */
    private void hideSoftInputView()
    {
        InputMethodManager manager = ((InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    // 登录帐号异步任务
    class LoginTask extends AsyncTask<String, Integer, Integer>
    {
        private Context context;
        
        private XMPPConnection con;
        
        private String userId;
        
        private String passwd;
        
        private MyProgressDialog progressDialog = null;
        
        /** 开启dialog */
        private void startProgressDialog()
        {
            if (progressDialog == null)
            {
                progressDialog = MyProgressDialog.createDialog(context);
                progressDialog.setMessage("正在登录...");
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.show();
        }
        
        /** 退出dialog */
        private void stopProgressDialog()
        {
            if (progressDialog != null)
            {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
        
        public LoginTask(Context context, XMPPConnection con, String userId, String passwd)
        {
            this.context = context;
            this.con = con;
            this.userId = userId;
            this.passwd = passwd;
        }
        
        @Override
        protected void onPreExecute()
        {
            // handler.sendEmptyMessage(TIME_CHANGE);
            super.onPreExecute();
            // 显示自定义圈圈
            startProgressDialog();
        }
        
        @Override
        protected Integer doInBackground(String... params)
        {
            // 后台进行登录
            try
            {
                return new XMPPConnectionAssit().loginWithResult(con, userId, passwd);
            }
            catch (TimeoutException e)
            {
                e.printStackTrace();
                return VariableHolder.LOGIN_TIME_OUT;
            }
        }
        
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            
        }
        
        protected void onPostExecute(Integer result)
        {
            stopProgressDialog();
            switch (result)
            {
                case VariableHolder.LOGIN_SECCESS: // 登录成功
                    // 提示登录成功
                    Log.v("Dateout", "LoginActivity==>" + "登录成功");
                    // 保存用户名密码服务器Ip
                    editor.putString("serverip", serverIp);
                    editor.putString("username", this.userId);
                    editor.putString("passwd", this.passwd);
                    editor.commit();
                    
                    // 设置全局变量
                    app.setConnection(con);
                    app.getLoginUser().setUserId(userId);
                    app.getLoginUser().setHeadImageUri(headImageUri);
                    
                    // 跳转到MainActivity
                    Intent intent = new Intent();
                    intent.setClass(context, MainActivity.class);
                    context.startActivity(intent);
                    
                    finish();
                    break;
                case VariableHolder.LOGIN_ERROR_ACCOUNT_NOTPASS:
                    Toast.makeText(context, "用户名或密码出错", Toast.LENGTH_LONG).show();
                    break;
                case VariableHolder.LOGIN_ERROR_SERVER_UNAVAILABLE:
                    // 检测网络
                    Toast.makeText(context, "无法连接到服务器", Toast.LENGTH_SHORT).show();
                    new NetworkAssit(context).checkNetworkAndPopDialog();
                    break;
                case VariableHolder.LOGIN_TIME_OUT:
                    Toast.makeText(context, "登录超时!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(context, "暂时无法登录", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
