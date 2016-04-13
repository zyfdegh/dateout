package com.nuist.dateout.tab4.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nuist.dateout.R;
import com.nuist.dialog.AgeDialog;
import com.nuist.dialog.CityDialog;
import com.nuist.dialog.HeightDialog;
import com.nuist.dialog.SexDialog;
import com.nuist.dialog.WeightDialog;

import de.greenrobot.event.EventBus;
import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.CustomVcard;
import edu.nuist.dateout.model.PhotoPack;
import edu.nuist.dateout.task.FetchDownLinkTask;
import edu.nuist.dateout.task.UploadFileTask;
import edu.nuist.dateout.util.CacheAssit;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.util.FileNameGenTool;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.HeadImageAssit;
import edu.nuist.dateout.util.ImageTools;
import edu.nuist.dateout.util.NetworkAssit;
import edu.nuist.dateout.util.PhotoAssit;
import edu.nuist.dateout.util.VCardAssit;
import edu.nuist.dateout.value.VariableHolder;

/**
 * Tab4中的用户资料查看与编辑页面
 * 
 * @author Xin
 *
 */
public class EditVcardActivity extends Activity implements OnClickListener
{
    
    private ImageButton backButton;
    
    private TextView ageValueTextView;
    
    private TextView sexValueTextView;
    
    private TextView heightValueTextView;
    
    private TextView weightValueTextView;
    
    private TextView emotionVlaueTextView;
    
    private TextView cityValueTextView;
    
    private TextView signValueTextView;
    
    private EditText professionTextValue;
    
    private EditText mailTextValue;
    
    private EditText userIdTextView;
    
    private Bitmap userHeadBitmap;
    
    private ImageView headImage;// 头像
    
    private String userId;
    
    private DateoutApp app;
    
    private Context context;
    
    /*** 用来标记第几张照片被点击，由此来确定处理过的头像给谁 */
    private int photoindex = 0;
    
    private PhotoPack[] photos = new PhotoPack[6];
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 去除标题栏
        setContentView(R.layout.vcard_info);
        
        context = this;
        app = DateoutApp.getInstance();
        
        userId = app.getLoginUser().getUserId();
        initView();// 初始化控件
        initViewValues();// 初始化控件的值
        refreshViewValues();
    }
    
    /** 照片栏的监听器 */
    class PhotoOnClickListener implements OnClickListener
    {
        
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.iv_vcard_photo1:
                    LoadPhoto(0);
                    break;
                case R.id.iv_vcard_photo2:
                    LoadPhoto(1);
                    break;
                case R.id.iv_vcard_photo3:
                    LoadPhoto(2);
                    break;
                case R.id.iv_vcard_photo4:
                    LoadPhoto(3);
                    break;
                case R.id.iv_vcard_photo5:
                    LoadPhoto(4);
                    break;
                case R.id.iv_vcard_photo6:
                    LoadPhoto(5);
                    break;
                default:
                    break;
            }
        }
    }
    
    /** 相册点击后处理事件 */
    private void LoadPhoto(int index)
    {
        new PhotoAssit(EditVcardActivity.this).photoChooseDlg();
        /** 标识哪个控件被点击 */
        photoindex = index;
    }
    
    /**
     * 拍照完成或者从相册选取照片后的后续处理工作,主要是裁剪和添加到VCard变量中
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case 100:
                if (resultCode == 200)
                {
                    String editedSign = data.getStringExtra("editsign");
                    if (editedSign != null)
                    {
                        signValueTextView.setText(editedSign);
                    }
                }
                else if (resultCode == 201)
                {
                    // 点击了取消或者按了返回键
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_FROM_CAMERA:// 拍照
                if (resultCode == RESULT_OK)
                {
                    // 取出拍完之后缓存的图片
                    File temp = new CacheAssit().getCameraCachedImage();
                    // 进行裁剪
                    new PhotoAssit(EditVcardActivity.this).startPhotoZoomStyle3(Uri.fromFile(temp));
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
                            new PhotoAssit(EditVcardActivity.this).startPhotoZoomStyle3(picDataUri);
                        }
                    }
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_ZOOM_FINISH_SMALL:// 取得裁剪后的图片
                if (data != null)
                {
                    // TODO 裁剪完成之后保存图像到文件,上传时候从文件读取,解决从控件读取图片引起的图像质量下降问题
                    // 显示裁减后用户头像
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = extras.getParcelable("data");// 最终得到的头像,类型为Bitmap
                    setPicToView(bitmap);
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_ZOOM_FINISH_LARGE:
                if (resultCode == RESULT_OK)
                {
                    Bitmap bitmap=BitmapFactory.decodeFile(FilePathTool.getCropCachedImagePath());
                    setPicToView(bitmap);
                }
                break;
            default:
                System.out.println("Error");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 将裁减后的图片显示到控件上
     * 
     * @param picdata
     */
    private void setPicToView(Bitmap image)
    {
        if (photoindex == 10)
        {
            userHeadBitmap = image;
            Bitmap dealedBitmap = ImageTools.toRoundCorner(image, image.getHeight() / 2);
            headImage.setImageBitmap(dealedBitmap);
        }
        else
        {
            Drawable drawableHeadPic = new BitmapDrawable(image);
            photos[photoindex].getImageView().setImageDrawable(drawableHeadPic);
            photos[photoindex].setIsDefaultImage(false);
        }
    }
    
    /**
     * 初始化组件
     */
    private void initView()
    {
        /** 初始化上面的6张照片照片 */
        ImageView photo0 = (ImageView)findViewById(R.id.iv_vcard_photo1);
        ImageView photo1 = (ImageView)findViewById(R.id.iv_vcard_photo2);
        ImageView photo2 = (ImageView)findViewById(R.id.iv_vcard_photo3);
        ImageView photo3 = (ImageView)findViewById(R.id.iv_vcard_photo4);
        ImageView photo4 = (ImageView)findViewById(R.id.iv_vcard_photo5);
        ImageView photo5 = (ImageView)findViewById(R.id.iv_vcard_photo6);
        photos[0] = new PhotoPack(photo0, true);
        photos[1] = new PhotoPack(photo1, true);
        photos[2] = new PhotoPack(photo2, true);
        photos[3] = new PhotoPack(photo3, true);
        photos[4] = new PhotoPack(photo4, true);
        photos[5] = new PhotoPack(photo5, true);
        
        // 为每一张照片设置监听事件
        for (int i = 0; i < photos.length; i++)
        {
            photos[i].getImageView().setOnClickListener(new PhotoOnClickListener());
        }
        
        backButton = (ImageButton)findViewById(R.id.ib_vacard_back);
        backButton.setOnClickListener(this);
        /** 提交按钮 */
        TextView commitTextView = (TextView)findViewById(R.id.tv_vacard_commit);
        commitTextView.setOnClickListener(this);
        
        professionTextValue = (EditText)findViewById(R.id.et_vcard_profession_value);// 职业
        mailTextValue = (EditText)findViewById(R.id.et_vcard_mail_value);
        userIdTextView = (EditText)findViewById(R.id.et_vcard_userid);
        ageValueTextView = (TextView)findViewById(R.id.tv_vcard_age_value);
        sexValueTextView = (TextView)findViewById(R.id.tv_vacard_sex_value);
        heightValueTextView = (TextView)findViewById(R.id.tv_vcard_height_value);
        weightValueTextView = (TextView)findViewById(R.id.tv_vcard_weight_value);
        emotionVlaueTextView = (TextView)findViewById(R.id.tv_vcard_emotion_value);
        cityValueTextView = (TextView)findViewById(R.id.tv_vcard_city_value);
        
        View sexView = findViewById(R.id.lo_vcard_sex);
        View ageView = findViewById(R.id.lo_vcard_age);
        View heightView = findViewById(R.id.lo_vcard_height);
        View weightView = findViewById(R.id.lo_vcard_weight);
        View cityView = findViewById(R.id.lo_vcard_city);
        View emotionView = findViewById(R.id.lo_vcard_emotion);
        View interestView = findViewById(R.id.lo_vcard_interest);
        
        sexView.setOnClickListener(this);
        ageView.setOnClickListener(this);
        heightView.setOnClickListener(this);
        weightView.setOnClickListener(this);
        cityView.setOnClickListener(this);
        emotionView.setOnClickListener(this);
        interestView.setOnClickListener(this);
        
        View signView = findViewById(R.id.lo_vcard_sign);// 个性签名
        signView.setOnClickListener(this);
        signValueTextView = (TextView)findViewById(R.id.tv_vacard_sign_value);
        
        headImage = (ImageView)findViewById(R.id.iv_vcard_head);
        headImage.setOnClickListener(this);
    }
    
    // 初始化控件值
    private void initViewValues()
    {
        // 设置顶部6张照片初始值
        for (int i = 0; i < photos.length; i++)
        {
            photos[i].getImageView().setImageResource(R.drawable.default_received_image);
            photos[i].getImageView().setOnClickListener(new PhotoOnClickListener());
        }
        // 设置用户头像为默认值
        Bitmap headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_head);
        Bitmap dealedBitmap = ImageTools.toRoundCorner(headBitmap, headBitmap.getHeight() / 2);
        headImage.setImageBitmap(dealedBitmap);
        // 设置下面的控件为默认值
        professionTextValue.setText("加载中");// 职业
        mailTextValue.setText("加载中");// 邮箱
        userIdTextView.setText("加载中");// 昵称
        ageValueTextView.setText("加载中");// 出生年月
        sexValueTextView.setText("加载中");// 性别
        heightValueTextView.setText("加载中");// 身高
        weightValueTextView.setText("加载中");// 体重
        emotionVlaueTextView.setText("加载中");// 情感状态
        cityValueTextView.setText("加载中");// 城市
        signValueTextView.setText("加载中");// 个性签名
    }
    
    private void refreshViewValues()
    {
        // 设置用户头像
        Uri headImageUri = app.getLoginUser().getHeadImageUri();// 存储着登录用户的账户与头像
        if (headImageUri == null)
        {
            // 使用默认头像
            headImage.setImageResource(R.drawable.default_head);
        }
        else
        {
            // 将头像转圆角并显示
            Bitmap headBitmap = BitmapFactory.decodeFile(headImageUri.toString());
            Bitmap dealedBitmap = ImageTools.toRoundCorner(headBitmap, headBitmap.getHeight() / 2);
            headImage.setImageBitmap(dealedBitmap);
        }
        
        if (!new NetworkAssit(context).isNetworkConnected())
        {
            Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
        }
        else
        {
            CustomVcard myVcard = new CustomVcard();
            myVcard = new VCardAssit(app.getConnection()).loadMyVCard(userId);
            app.setLoginUserVcard(myVcard);// 设置到全局
            if (myVcard != null)
            {
                // 设置下面的控件的值
                professionTextValue.setText(myVcard.getJob());// 职业
                mailTextValue.setText(myVcard.getEmail());// 邮箱
                userIdTextView.setText(myVcard.getNickName());// 昵称
                ageValueTextView.setText(myVcard.getBirthDay());// 出生年月
                sexValueTextView.setText(myVcard.getGender());// 性别
                heightValueTextView.setText(myVcard.getHeight());// 身高
                weightValueTextView.setText(myVcard.getWeight());// 体重
                emotionVlaueTextView.setText(myVcard.getSingleState());// 情感状态
                cityValueTextView.setText(myVcard.getBirthPlace());// 城市
                signValueTextView.setText(myVcard.getMoodie());// 个性签名
            }
            // 加载上部6张图片的下载链接
            String requestStr = VariableHolder.FILE_PREFIX_IMAGE_VCARD + userId;
            new FetchDownLinkTask(requestStr, urlFetchHandler).execute();
        }
    }
    
    // 处理用户资料卡页面顶部6张图片的下载链接的获取结果
    private Handler urlFetchHandler = new Handler()
    {
        public void handleMessage(android.os.Message osMsg)
        {
            switch (osMsg.what)
            {
                case 1:
                    List<String> downloadLinksList = (List<String>)osMsg.obj;
                    if (downloadLinksList.size() <= 6)
                    {
                        for (int i = 0; i < downloadLinksList.size(); i++)
                        {
                            // 使用ImageLoader下载图片,显示图片,缓存图片
                            ImageLoader.getInstance().displayImage(downloadLinksList.get(i), photos[i].getImageView());
                            photos[i].setIsDefaultImage(false);
                            
                        }
                    }
                    break;
                
                default:
                    break;
            }
        };
    };
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ib_vacard_back:
                finish();
                break;
            case R.id.lo_vcard_city:
                editCityValue();
                break;
            case R.id.lo_vcard_age:
                EditAgeVlaue();
                break;
            case R.id.lo_vcard_sex:
                EditSex();
                break;
            case R.id.lo_vcard_height:
                EditHeight();
                break;
            case R.id.lo_vcard_weight:
                editWeight();
                break;
            case R.id.lo_vcard_emotion:
                editEmotion();
                break;
            case R.id.lo_vcard_interest:
                editInterest();
                break;
            case R.id.lo_vcard_sign:
                editSign();
                break;
            case R.id.tv_vacard_commit:
                commitEdit();// 提交vacrd的更改
                break;
            case R.id.iv_vcard_head:
                editHead();
                break;
            default:
                break;
        }
    }
    
    /** 编辑头像 */
    private void editHead()
    {
        LoadPhoto(10);
    }
    
    private short photoCount = 0;
    
    /***
     * 提交vcard编辑
     */
    private void commitEdit()
    {
        if (!new NetworkAssit(context).isNetworkConnected())
        {
            Toast.makeText(context, "请先检测网络", Toast.LENGTH_LONG).show();
        }
        else
        {
            String user = userIdTextView.getText().toString();
            String sign = signValueTextView.getText().toString();
            String sex = sexValueTextView.getText().toString();
            String age = ageValueTextView.getText().toString();
            String height = heightValueTextView.getText().toString();
            String weight = weightValueTextView.getText().toString();
            String city = cityValueTextView.getText().toString();
            String profession = professionTextValue.getText().toString();
            String mail = mailTextValue.getText().toString();
            String emotion = emotionVlaueTextView.getText().toString();
            
            CustomVcard card = new CustomVcard();
            card.setNickName(user);
            card.setMoodie(sign);
            card.setGender(sex);
            card.setBirthDay(age);
            card.setHeight(height);
            card.setWeight(weight);
            
            card.setBirthPlace(city);
            card.setJob(profession);
            card.setEmail(mail);// TODO 设置邮箱地址规则检测器
            
            card.setSingleState(emotion);
            card.setInterest(" ");
            
            // ==上传用户VCard
            boolean uploadSuccess = new VCardAssit(app.getConnection()).uploadMyVCard(card);
            if (uploadSuccess)
            {
                Toast.makeText(this, "资料上传成功", Toast.LENGTH_LONG).show();
                app.setLoginUserVcard(card);
            }
            else
            {
                Toast.makeText(this, "资料上传失败", Toast.LENGTH_LONG).show();
            }
            // ==上传用户头像
            // 用于处理上传结果的Handler,从UploadFileTask里面传过来osMsg
            final Uri headImageUri = FormatTools.bitmap2Uri(context, userHeadBitmap);
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
                                // 更新全局的用户头像Uri
                                app.getLoginUser().setHeadImageUri(headImageUri);
                                // 通知SettingManager更新头像
                                EventBus.getDefault().post(headImageUri);
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
            
            if (headImageUri == null)
            {
                // 无法生成文件
                // Toast.makeText(context, "无法上传头像,原因是把Bitmap转换为Uri,检测SD卡是否正确挂载", Toast.LENGTH_LONG).show();
                // Toast.makeText(context, "没有更换头像", Toast.LENGTH_LONG).show();
            }
            else
            {
                // 缓存到本地,并上传用户头像
                new HeadImageAssit().cacheAndUploadHeadImage(userId, context, headImageUri, uploadHandler);
            }
            
            // 用于处理上传头像之后对结果的后续处理
            Handler uploadHandler2 = new Handler()
            {
                public void handleMessage(android.os.Message osMsg)
                {
                    switch (osMsg.what)
                    {
                        case 1:
                            String result = (String)osMsg.obj;
                            photoCount++;
                            if (result != null)
                            {
                                Toast.makeText(EditVcardActivity.this, "上传第" + photoCount + "张图片成功", Toast.LENGTH_SHORT)
                                    .show();
                            }
                            else
                            {
                                Toast.makeText(EditVcardActivity.this, "上传第" + photoCount + "张图片失败", Toast.LENGTH_SHORT)
                                    .show();
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
            
            // 在上传之前发送指令给服务端,让它删掉之前的旧的图片
            // 利用这个类里面的特殊参数能够删除服务端指定用户的所有VCARD照片
            String cmdPrefix = VariableHolder.CMD_DELETE_ALL_MYVCARD_IMAGES + userId;
            new FetchDownLinkTask(cmdPrefix, null).execute();
            // 上传用户照片
            for (int i = 0; i < 6; i++)
            {
                // 如果不是默认的头像,就上传
                if (!photos[i].getIsDefaultImage())
                {
                    // 图片源文件,也就是裁减后保存在Android文件夹的图片
                    // TODO 这里从图片控件获取图片质量会相当差
                    String srcFilePath =
                        FormatTools.uri2Path(context,
                            FormatTools.drawable2Uri(context, photos[i].getImageView().getDrawable()));// 把ImageView控件上显示的图片转换为Uri
                    // 不是默认的头像,也就是被修改过
                    // 异步上传头像
                    File srcFile = new File(srcFilePath);
                    if (srcFile != null && srcFile.exists())
                    {
                        // 先拷贝文件到应用缓存,并重命名
                        // 拷贝后的文件路径
                        String dstFileName = new FileNameGenTool().generateVCardImageName(srcFile, userId);
                        String dstFilePath = FilePathTool.getVCardImagePath(dstFileName);// 新的文件路径
                        File dstFile = new File(dstFilePath);
                        // 拷贝文件srcFile到新的文件dstFilePath,返回值结果
                        short copyResult = new FileAssit().copyFile(srcFile, dstFile);
                        // 拷贝没有失败
                        if (copyResult != VariableHolder.COPY_RESULT_FAIL)
                        {
                            Log.v("Dateout", "EditVcardActivity==>" + "开始上传用户" + userId + "资料卡中第" + (i + 1) + "张照片");
                            new UploadFileTask(dstFile, uploadHandler2).execute();
                        }
                    }
                }
            }
//            finish();
        }
        // 返回到SettingManager
        // finish();
    }
    
    // 处理上传结果
    
    /**
     * 编辑个性签名
     */
    private void editSign()
    {
        Intent intent = new Intent(context, EditSignActivity.class);
        intent.putExtra("sign", signValueTextView.getText());
        startActivityForResult(intent, 100);
    }
    
    /**
     * 编辑个人兴趣
     */
    private void editInterest()
    {
        
    }
    
    private void editCityValue()
    {
        CityDialog dialog = new CityDialog(this);
        dialog.setTitle("设置城市");
        dialog.setBackButton("取消", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
        });
        dialog.setConfirmButton("确定", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                cityValueTextView.setText(CityDialog.getData());
            }
        });
        
        dialog.show();
    }
    
    private void editEmotion()
    {
        
        final String[] mItems = {"保密", "单身", "恋爱中", "已婚", "同性"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("情感状态");
        builder.setItems(mItems, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // System.out.println("你选择的id为" + which + " , " + mItems[which]);
                emotionVlaueTextView.setText(mItems[which]);
            }
        });
        builder.create().show();
    }
    
    /**
     * 弹出修改体重的dialog
     */
    private void editWeight()
    {
        String currentValue = weightValueTextView.getText().toString();
        if (currentValue.equals("") || currentValue.equals("未设置"))
        {
            currentValue = "60 kg";
        }
        WeightDialog dialog = new WeightDialog(this, currentValue);
        dialog.setTitle("设置体重");
        dialog.setBackButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
        });
        dialog.setConfirmButton("确定", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                weightValueTextView.setText(WeightDialog.getData());
            }
        });
        
        dialog.show();
    }
    
    /**
     * 弹出修改身高的dialog
     */
    private void EditHeight()
    {
        
        String currentValue = heightValueTextView.getText().toString();
        if (currentValue.equals("") || currentValue.equals("未设置"))
        {
            currentValue = "180 cm";
        }
        
        HeightDialog dialog = new HeightDialog(this, currentValue);
        dialog.setTitle("设置身高");
        dialog.setBackButton("取消", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
        });
        dialog.setConfirmButton("确定", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                heightValueTextView.setText(HeightDialog.getData());
            }
        });
        
        dialog.show();
    }
    
    /**
     * 弹出修改性别的dialog
     */
    private void EditSex()
    {
        String currentValue = sexValueTextView.getText().toString();
        if (currentValue.equals("") || currentValue.equals("未设置"))
        {
            currentValue = "男";
        }
        SexDialog dialog = new SexDialog(this, currentValue);
        dialog.setTitle("设置性别");
        dialog.setBackButton("取消", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        dialog.setConfirmButton("确定", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                sexValueTextView.setText(SexDialog.getData());
                
            }
        });
        
        dialog.show();
    }
    
    /**
     * 修改年龄
     */
    private void EditAgeVlaue()
    {
        String currentValue = ageValueTextView.getText().toString();
        if (currentValue.equals("") || currentValue.equals("未设置"))
        {
            currentValue = "1993-01-01";
        }
        AgeDialog dialog = new AgeDialog(this, currentValue);
        dialog.setTitle("设置生日");
        dialog.setBackButton("取消", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        dialog.setConfirmButton("确定", new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ageValueTextView.setText(AgeDialog.getDate());
            }
        });
        
        dialog.show();
    }
}
