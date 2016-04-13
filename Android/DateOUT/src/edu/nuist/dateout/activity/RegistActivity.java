package edu.nuist.dateout.activity;

import java.io.File;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dateout.R;
import com.nuist.dateout.tab1.activity.FillCardActivity;
import com.nuist.dialog.MyProgressDialog;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.core.XMPPConnectionAssit;
import edu.nuist.dateout.task.UserOnlineStatCheckTask;
import edu.nuist.dateout.util.CacheAssit;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.HeadImageAssit;
import edu.nuist.dateout.util.NetworkAssit;
import edu.nuist.dateout.util.PhotoAssit;
import edu.nuist.dateout.util.RegexAssit;
import edu.nuist.dateout.value.VariableHolder;

/**
 * @author zhang 注册用户
 */
public class RegistActivity extends Activity implements OnClickListener, OnFocusChangeListener
{
	public static RegistActivity instance= null;
	
    private ImageView userHeadImageView;// 图片-选取头像
    
    private EditText nameRegEditText;// 用户名
    
    private EditText passwordRegText;// 密码
    
    private EditText passwordConfirmText;// 确认密码
    
    private Button btnRegistAccount;// 按钮-注册
    
    private TextView checkResultTextView;// 文本框内容校验结果
    
    private ImageView regNameCheckImageView;// 用户名检测结果图标
    
    private ImageView regPwdCheckImageView;// 密码检测结果图标
    
    private ImageView regPwd2CheckImageView;// 第二次密码检测结果图标
    
    private TextView backToLoginActivityTextView;// 返回登录页面
    
    private DateoutApp app;
    
    private Context context;
    
    private Bitmap userHeadBitmap;// 用户头像
    
    private XMPPConnection connection;
    
    private boolean headImageOk = false;// 头像可用
    
    private boolean inputNameOk = false;// 帐号可用
    
    private boolean inputPwdOk = false;// 密码可用
    
    private boolean inputPwd2Ok = false;// 第二次输入的密码合格
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_regist);
        
        instance =this;
        
        app = (DateoutApp)this.getApplication();
        
        connection = app.getConnection();// 获取XMPPConnect连接对象
        if (connection == null)
        {
            XMPPConnectionAssit connectionAssit = new XMPPConnectionAssit(app.getServerIpInUse());
            connectionAssit.initConnection();
        }
        context = this;
        initView();// 初始化控件
    }
    
    /**
     * 初始化组件
     */
    private void initView()
    {
        userHeadImageView = (ImageView)findViewById(R.id.iv_regist_head);
        nameRegEditText = (EditText)findViewById(R.id.et_regist_regname);
        passwordRegText = (EditText)findViewById(R.id.et_regist_regpwd);
        passwordConfirmText = (EditText)findViewById(R.id.et_regist_regpwd2);
        btnRegistAccount = (Button)findViewById(R.id.btn_regist_regaccount);
        checkResultTextView = (TextView)findViewById(R.id.tv_regist_checkresult);
        regNameCheckImageView = (ImageView)findViewById(R.id.iv_regist_check_name);
        regPwdCheckImageView = (ImageView)findViewById(R.id.iv_regist_check_pw1);
        regPwd2CheckImageView = (ImageView)findViewById(R.id.iv_regist_check_pw2);
        backToLoginActivityTextView = (TextView)findViewById(R.id.tv_register_back);
        
        userHeadImageView.setOnClickListener(this);// 点击头像
        btnRegistAccount.setOnClickListener(this);// 点击注册按钮
        nameRegEditText.setOnFocusChangeListener(this);// 光标状态改变监听
        passwordRegText.setOnFocusChangeListener(this);// 光标状态改变监听
        passwordConfirmText.addTextChangedListener(new MyTextWatcher());// 文本内容改变监听
        backToLoginActivityTextView.setOnClickListener(this);
        backToLoginActivityTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 设置TextView的下划线
        
        checkResultTextView.setVisibility(View.GONE);
        regNameCheckImageView.setVisibility(View.GONE);
        regPwdCheckImageView.setVisibility(View.GONE);
        regPwd2CheckImageView.setVisibility(View.GONE);
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_regist_head:
                // 点击头像按钮
                clickHeadImage();
                break;
            case R.id.btn_regist_regaccount:
                // 点击注册按钮
                clickRegAccoutBtn();
                break;
            case R.id.tv_register_back:
                backToLoginActivity();
            default:
                break;
        }
    }
    
    // 点击头像
    private void clickHeadImage()
    {
        // 弹出对话框,提示相机或者相册
        new PhotoAssit(RegistActivity.this).photoChooseDlg();
    }
    
    // 点击注册按钮
    private void clickRegAccoutBtn()
    {
        String regName = nameRegEditText.getText().toString().trim();
        String regPasswd = passwordRegText.getText().toString().trim();
        
        if (headImageOk && inputNameOk && inputPwdOk && inputPwd2Ok)
        {
            RegistTask registTask = new RegistTask(context, connection, regName, regPasswd, userHeadBitmap);
            registTask.execute();
        }
        else
        {
            if (!headImageOk)
            {
                Toast.makeText(context, "请上传头像", Toast.LENGTH_LONG).show();
            }
            if (!inputNameOk)
            {
                Toast.makeText(context, "检测用户名", Toast.LENGTH_LONG).show();
            }
            if (!inputPwdOk)
            {
                Toast.makeText(context, "检测密码", Toast.LENGTH_LONG).show();
            }
            if (!inputPwd2Ok)
            {
                Toast.makeText(context, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        
        switch (v.getId())
        {
            case R.id.et_regist_regname:
                if (!hasFocus)
                {
                    // 用户名输入框焦点变化
                    checkRegName();
                }
                break;
            case R.id.et_regist_regpwd:
                if (!hasFocus)
                {
                    // 用户名输入框焦点变化
                    checkRegPwd();
                }
                // 密码输入框焦点变化
                break;
            default:
                break;
        }
    }
    
    private Handler onlineStatHandler = new Handler()
    {
        public void handleMessage(android.os.Message osMsg)
        {
            switch (osMsg.what)
            {
                case 1:
                    Integer checkResult = (Integer)osMsg.obj;
                    if (checkResult.equals(Integer.valueOf(VariableHolder.STAT_NOT_EXIST)))
                    {
                        // 用户不存在
                        inputNameOk = true;
                    }
                    else
                    {
                        inputNameOk = false;
                        // 用户名已存在
                        regNameCheckImageView.setImageResource(R.drawable.i_register_no);
                        regNameCheckImageView.setVisibility(View.VISIBLE);
                        checkResultTextView.setText("该用户名已被注册");
                        checkResultTextView.setVisibility(View.VISIBLE);
                    }
                    break;
                
                default:
                    break;
            }
        };
    };
    
    // 检测用户名是否合法
    private void checkRegName()
    {
        // 检测是否合乎正则表达式规则(数字字母下划线)
        String regName = nameRegEditText.getText().toString().trim();
        boolean nameRegexOk = new RegexAssit().checkUserId(regName);
        if (nameRegexOk)
        {
            // 检测长度
            if (regName.length() >= 3 && regName.length() <= 16)
            {
                if (regName.contains("Dateout") || regName.contains("dateout"))
                {
                    // 用户名包含保留字符串
                    regNameCheckImageView.setImageResource(R.drawable.i_register_no);
                    regNameCheckImageView.setVisibility(View.VISIBLE);
                    checkResultTextView.setText("用户名ID中包含了保留字符串dateout字样");
                    checkResultTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    // 检测帐号是否存在,不存在再提醒用户Id无线
                    new UserOnlineStatCheckTask(regName, onlineStatHandler).execute();
                    // OK
                    inputNameOk = true;
                    regNameCheckImageView.setImageResource(R.drawable.i_register_yes);
                    regNameCheckImageView.setVisibility(View.VISIBLE);
                    checkResultTextView.setVisibility(View.GONE);
                }
            }
            else if (regName.length() < 3)
            {
                // 用户ID太短
                regNameCheckImageView.setImageResource(R.drawable.i_register_no);
                regNameCheckImageView.setVisibility(View.VISIBLE);
                checkResultTextView.setText("用户名ID太短");
                checkResultTextView.setVisibility(View.VISIBLE);
                inputNameOk = false;
            }
        }
        else
        {
            // 名称不合法,不全是字母数字下划线
            regNameCheckImageView.setImageResource(R.drawable.i_register_no);
            regNameCheckImageView.setVisibility(View.VISIBLE);
            checkResultTextView.setText("用户名ID格式不合格");
            checkResultTextView.setVisibility(View.VISIBLE);
            inputNameOk = false;
        }
    }
    
    // 检测密码是否合法
    private void checkRegPwd()
    {
        // 检测是否合乎正则表达式规则(数字字母下划线)
        String regPwd = passwordRegText.getText().toString().trim();
        boolean pwdRegexOk = new RegexAssit().checkPwd(regPwd);
        if (pwdRegexOk)
        {
            // 检测长度
            if (regPwd.length() >= 6 && regPwd.length() <= 16)
            {
                // OK
                regPwdCheckImageView.setImageResource(R.drawable.i_register_yes);
                regPwdCheckImageView.setVisibility(View.VISIBLE);
                checkResultTextView.setVisibility(View.GONE);
                inputPwdOk = true;
            }
            else if (regPwd.length() < 6)
            {
                // 用户密码太短
                regPwdCheckImageView.setImageResource(R.drawable.i_register_no);
                regPwdCheckImageView.setVisibility(View.VISIBLE);
                checkResultTextView.setText("密码太短,低于6位");
                checkResultTextView.setVisibility(View.VISIBLE);
                inputPwdOk = false;
            }
        }
        else
        {
            // 名称不合法,不全是字母数字下划线
            regPwdCheckImageView.setImageResource(R.drawable.i_register_no);
            regPwdCheckImageView.setVisibility(View.VISIBLE);
            checkResultTextView.setText("密码格式不合规则");
            checkResultTextView.setVisibility(View.VISIBLE);
            inputPwdOk = false;
        }
    }
    
    private void checkRegPwd2()
    {
        Log.v("Dateout", passwordConfirmText.getText() + "," + passwordRegText.getText());
        String pwd = passwordRegText.getText().toString().trim();
        String pwd2 = passwordConfirmText.getText().toString().trim();
        if (pwd2.equals(pwd))
        {
            // 两次输入的一样
            regPwd2CheckImageView.setImageResource(R.drawable.i_register_yes);
            regPwd2CheckImageView.setVisibility(View.VISIBLE);
            checkResultTextView.setVisibility(View.GONE);
            inputPwd2Ok = true;
        }
        else
        {
            // 两次输入的不一样
            regPwd2CheckImageView.setImageResource(R.drawable.i_register_no);
            regPwd2CheckImageView.setVisibility(View.VISIBLE);
            checkResultTextView.setText("两次输入的密码不一致");
            checkResultTextView.setVisibility(View.VISIBLE);
            inputPwd2Ok = false;
        }
    }
    
    /**
     * 拍照完成或者从相册选取照片后的后续处理工作,主要是裁剪和添加到VCard变量中
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case VariableHolder.REQUEST_CODE_PHOTO_FROM_CAMERA:// 拍照
                if (resultCode == RESULT_OK)
                {
                    // 取出拍完之后缓存的图片
                    File temp = new CacheAssit().getCameraCachedImage();
                    // 进行裁剪
                    new PhotoAssit(RegistActivity.this).startPhotoZoomStyle1(Uri.fromFile(temp));
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_FROM_GALLERY:// 相册
                if (resultCode == RESULT_OK)
                {
                    if (data != null)
                    {
                        Uri picDataUri = data.getData(); // 获取系统返回的照片的Uri
                        if (picDataUri != null)
                        {
                            // 进行裁剪
                            new PhotoAssit(RegistActivity.this).startPhotoZoomStyle1(picDataUri);
                        }
                    }
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_ZOOM_FINISH_SMALL:// 取得裁剪后的图片
                if (data != null)
                {
                    // 显示裁减后用户头像
                    Bundle extras = data.getExtras();
                    userHeadBitmap = extras.getParcelable("data");// 最终得到的头像,类型为Bitmap
                    userHeadImageView.setImageBitmap(userHeadBitmap);
                    headImageOk = true;
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_ZOOM_FINISH_LARGE:
                if (resultCode == RESULT_OK)
                {
                    userHeadBitmap = BitmapFactory.decodeFile(FilePathTool.getCropCachedImagePath());// 最终得到的头像,类型为Bitmap
                    userHeadImageView.setImageBitmap(userHeadBitmap);
                    headImageOk = true;
                }
                break;
            default:
                System.out.println("Error");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        // 跳转到LoginActivity
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivity(intent);
    }
    
    private void backToLoginActivity()
    {
        onBackPressed();// 和返回操作相同
    }
    
    /**
     * 监听Edittext状态的TextWatch
     */
    private class MyTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            // 改变了字符之后
            checkRegPwd2();
        }
        
        @Override
        public void afterTextChanged(Editable s)
        {
            
        }
    }
}

/**
 * @author xin,zhang
 * @date 2015-03-19 用于注册帐号的类
 */
class RegistTask extends AsyncTask<String, Integer, Integer>
{
    private DateoutApp app = DateoutApp.getInstance();
    
    private String username;
    
    private String password;
    
    private Bitmap userHeadBitmap;
    
    private Context context;
    
    private XMPPConnection con;
    
    private MyProgressDialog progressDialog = null;
    
    /** 开启dialog */
    private void startProgressDialog()
    {
        if (progressDialog == null)
        {
            progressDialog = MyProgressDialog.createDialog(context);
            progressDialog.setMessage("注册中...");
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
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public RegistTask(Context context, XMPPConnection con, String userId, String passwd, Bitmap userHeadBitmap)
    {
        this.username = userId;
        this.password = passwd;
        this.userHeadBitmap = userHeadBitmap;
        this.context = context;
        this.con = con;
    }
    
    @Override
    protected void onPreExecute()
    {
        startProgressDialog();
        super.onPreExecute();
    }
    
    @Override
    protected void onProgressUpdate(Integer... values)
    {
        
    }
    
    @Override
    protected Integer doInBackground(String... params)
    {
        return new XMPPConnectionAssit().registAccount(con, username, password);
    }
    
    protected void onPostExecute(Integer result)
    {
        stopProgressDialog();
        
        switch (result)
        {
        // 1.注册成功 2.这个账号已经存在 3.注册失败 4.连接失败
            case VariableHolder.REGIST_SUCCESS:
                Toast.makeText(context, "注册成功", Toast.LENGTH_LONG).show();
                
                // 登录一次以验证账户
                verifiyAccount();
                
                // 上传用户头像
                // 用于处理上传结果的Handler,从UploadFileTask里面传过来osMsg
                Handler uploadHandler = new Handler()
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
                                    Toast.makeText(context, "头像上传失败", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(context, "头像上传成功", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                };
                
                Uri uri = FormatTools.bitmap2Uri(context, userHeadBitmap);
                if (uri == null)
                {
                    // 转换失败
                    Toast.makeText(context, "无法上传头像,原因是把Bitmap转换为Uri出错,检测SD卡是否正确挂载", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // 缓存头像到本地,并上传用户头像
                    new HeadImageAssit().cacheAndUploadHeadImage(username, context, uri, uploadHandler);
                }
                // 跳转到完善资料页面
                Intent intent = new Intent();
                intent.setClass(context, FillCardActivity.class);
                context.startActivity(intent);
                
                // 将注册用户的信息保存到全局变量
                app.getRegistUser().setUserId(username);
                
                // TODO 释放当前Activity
                // RegistActivity.this.finish();
                // DateoutApp.getInstance().removeActivity(RegistActivity.this);
                break;
            case VariableHolder.REGIST_ERR_ACCOUNT_EXIST:
                Toast.makeText(context, "帐号已存在", Toast.LENGTH_LONG).show();
                break;
            case VariableHolder.REGIST_ERR_CONNECT_FAILURE:
                Toast.makeText(context, "连接失败", Toast.LENGTH_LONG).show();
                new NetworkAssit(context).checkNetworkAndPopDialog();
                break;
            default:
                Toast.makeText(context, "注册失败，未知错误", Toast.LENGTH_LONG).show();
                break;
        }
    }
    
    /**
     * 用于上传用户VCard
     */
    private void verifiyAccount()
    {
        // 登录一次帐号,使得当前Activity中的XMPPConnection对象有效,目的是为了下面能够上传用户头像和资料
        try
        {
            con.login(username, password);
            app.setConnection(con);// 设置全局Connection对象
        }
        catch (XMPPException e)
        {
            // 注册都能成功那登录应该不成什么问题吧,除外关掉网络之类的
            e.printStackTrace();
        }
    }
}
