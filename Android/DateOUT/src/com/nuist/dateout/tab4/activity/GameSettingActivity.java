package com.nuist.dateout.tab4.activity;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dateout.listener.MyOnGesTureListener;
import com.nuist.picturegame.dialog.GameSettingDialog;
import com.nuist.picturegame.view.PictureSplitedView;
import com.nuist.dateout.R;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.model.GameConfigInfo;
import edu.nuist.dateout.task.DownloadFileTask;
import edu.nuist.dateout.task.FetchGameConfigTask;
import edu.nuist.dateout.util.CacheAssit;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.PhotoAssit;
import edu.nuist.dateout.value.AppConfig;
import edu.nuist.dateout.value.VariableHolder;

/**
 * 设置游戏图片，以及游戏难度
 * 
 * @author liyuxin
 * 
 */
public class GameSettingActivity extends Activity implements OnClickListener
{
    
    private boolean isImageResetByUser;
    
    private TextView gameTime;
    
    private TextView gameLevel;
    
    /** 记录设置界面里设置的难度 */
    private int level = 3;
    
    /** 记录设置界面里设置的时间 */
    private int time = 30;
    
    private Uri gameBkgPicUri;
    
    private PictureSplitedView pictureSplitedView;
    
    private GameConfigInfo gameConfig;
    
    private Context context;
    
    private String picSavePath;
    
    private GestureDetector detector;
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return detector.onTouchEvent(event);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_setting_activity);
        // 设置图片下载路径
        picSavePath = new FileAssit().getValidSdCardPath() + "/" + AppConfig.FILE_GAME_CACHE_IMAGE;
        gameConfig = new GameConfigInfo();
        context = this;
        isImageResetByUser = false;
        
        initView();
        // 滑动返回
        detector = new GestureDetector(this, new MyOnGesTureListener(this));
        
        isImageResetByUser = false;
        
        // pictureSplitedView.reSetViewBitmap(BitmapFactory.decodeResource(getResources(),
        // R.drawable.default_game_bkg2));
        // 从网络加载用户的游戏配置
        loadGameConfig();
        // gameBkgPicUri = FormatTools.resId2Uri(getResources(), R.drawable.default_game_bkg);
        // // 格式为android.resource://edu.nuist.dateout/drawable/default_game_bkg这样的
        // pictureSplitedView.reSetViewBitmap(FormatTools.uri2Bitmap(this, gameBkgPicUri));
    }
    
    private void initView()
    {
        ImageView gameSettingImage = (ImageView)findViewById(R.id.iv_game_set_level);
        ImageView gameStartImage = (ImageView)findViewById(R.id.iv_game_practice);
        ImageView choosePictureImage = (ImageView)findViewById(R.id.iv_game_choose_picture);
        
        ImageButton backImageButton = (ImageButton)findViewById(R.id.ib_game_setting_back);
        pictureSplitedView = (PictureSplitedView)findViewById(R.id.iv_game_image);
        
        gameTime = (TextView)findViewById(R.id.tv_game_chooseed_time);
        gameLevel = (TextView)findViewById(R.id.tv_game_chooseed_level);
        gameSettingImage.setOnClickListener(this);
        gameStartImage.setOnClickListener(this);
        choosePictureImage.setOnClickListener(this);
        backImageButton.setOnClickListener(this);
    }
    
    /** 跳转到游戏界面,并传递游戏配置信息 */
    private void jumpToGame()
    {
        if (!isImageResetByUser)
        {
            Toast.makeText(GameSettingActivity.this, "点击左下角更换图片", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // 将裁剪好显示在控件上的Bitmap存储到文件,再读出到Uri
            Intent intent4 = new Intent(GameSettingActivity.this, PictureGameActivity.class);
            
            GameConfigInfo gameConfigInfo = new GameConfigInfo(level, time, 0, gameBkgPicUri.toString());
            Bundle data = new Bundle();
            data.putSerializable("gameConfig", gameConfigInfo);
            intent4.putExtras(data);
            startActivity(intent4);
        }
    }
    
    private void loadGameConfig()
    {
        Handler gameConfigFetchHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 1:
                        GameConfig config = (GameConfig)msg.obj;
                        if (config != null)
                        {
                            // 设置背景图片
                            if (config.getPicUrl() == null || config.getPicUrl().equals(""))
                            {
                                // 使用默认背景
                                Uri defaultBkgPicUri =
                                    FormatTools.resId2Uri(context.getResources(), R.drawable.default_game_bkg2);// 默认背景图片
                                gameConfig.setBkgPicUriStr(defaultBkgPicUri.toString());
                            }
                            else
                            {
                                // 调用下载器,给定链接,下载文件到本地
                                new DownloadFileTask(config.getPicUrl(), bkgPicDownHandler, picSavePath).execute();// 下载图片
                                Uri bkgPicUri =
                                    FormatTools.resId2Uri(context.getResources(), R.drawable.stat_image_loading);// 图片显示为加载中
                                gameConfig.setBkgPicUriStr(bkgPicUri.toString());
                            }
                            // 设置格数
                            if (config.getDifficulty() != 0)
                            {
                                gameConfig.setColumn(config.getDifficulty());
                            }
                            else
                            {
                                gameConfig.setColumn(3);
                            }
                            // 设置游戏超时
                            if (config.getTimeOut() != 0)
                            {
                                gameConfig.setTime(config.getTimeOut());
                            }
                            else
                            {
                                gameConfig.setTime(35);
                            }
                        }
                        else
                        {
                            // 设置为默认值
                            Uri defaultBkgPicUri =
                                FormatTools.resId2Uri(context.getResources(), R.drawable.default_game_bkg2);// 默认背景图片
                            gameConfig.setBkgPicUriStr(defaultBkgPicUri.toString());
                            gameConfig.setColumn(3);
                            gameConfig.setTime(30);
                        }
                        
                        // 设置界面显示
                        pictureSplitedView.reSetViewBitmap(FormatTools.uri2Bitmap(context,
                            Uri.parse(gameConfig.getBkgPicUriStr())));
                        // 格子数
                        int column = gameConfig.getColumn();
                        gameLevel.setText(column + "*" + column);
                        gameTime.setText("" + gameConfig.getTime());
                        break;
                    default:
                        break;
                }
            }
        };
        String loginId = DateoutApp.getInstance().getLoginUser().getUserId();
        new FetchGameConfigTask(loginId, gameConfigFetchHandler, context).execute();
        // TODO 通过用户Id去联系服务器获取该用户的游戏配置,和游戏背景图片
    }
    
    private Handler bkgPicDownHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    // 读取图片下载缓存文件
                    File file = new File(picSavePath);
                    if (file != null && file.exists() && file.length() > 0 && file.canRead())
                    {
                        Uri bkgPicDownloadedUri = Uri.fromFile(file);
                        // 显示图片
                        pictureSplitedView.reSetViewBitmap(FormatTools.uri2Bitmap(context, bkgPicDownloadedUri));
                        pictureSplitedView.reSetViewColum(gameConfig.getColumn());
                        gameConfig.setBkgPicUriStr(bkgPicDownloadedUri.toString());
                        isImageResetByUser=true;
                        gameBkgPicUri=bkgPicDownloadedUri;
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
            case R.id.iv_game_choose_picture:
                loadPhoto();
                break;
            case R.id.iv_game_practice:
                jumpToGame();
                break;
            case R.id.iv_game_set_level:
                settingLevel();
                break;
            case R.id.ib_game_setting_back:
                finish();
                break;
            default:
                break;
        }
    }
    
    /**
     * 设置游戏难度，time and colum
     */
    private void settingLevel()
    {
        GameSettingDialog dialog = new GameSettingDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setTitle("设置游戏");
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
                
                level = GameSettingDialog.getData();
                time = GameSettingDialog.getData1();
                
                gameLevel.setText(level + "*" + level);
                gameTime.setText(time + "s");
                
                pictureSplitedView.reSetViewColum(level);
            }
        });
        dialog.show();
    }
    
    /** 相册点击后处理事件 */
    private void loadPhoto()
    {
        new PhotoAssit(GameSettingActivity.this).photoChooseDlg();
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
                    new PhotoAssit(GameSettingActivity.this).startPhotoZoomStyle3(Uri.fromFile(temp));
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
                            new PhotoAssit(GameSettingActivity.this).startPhotoZoomStyle3(picDataUri);
                        }
                    }
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_ZOOM_FINISH_SMALL:// 取得裁剪后的图片
                if (data != null)
                {
                    // 显示裁减后用户头像
                    Bundle extras = data.getExtras();
                    Bitmap gameBitmap = extras.getParcelable("data");// 最终得到的头像,类型为Bitmap
                    // Bitmap转Uri
                    gameBkgPicUri = FormatTools.bitmap2Uri(GameSettingActivity.this, gameBitmap);
                    setPicToView(gameBkgPicUri);
                    isImageResetByUser = true;
                }
                break;
            case VariableHolder.REQUEST_CODE_PHOTO_ZOOM_FINISH_LARGE:
                if (resultCode == RESULT_OK)
                {
                    gameBkgPicUri = Uri.parse("file://" + FilePathTool.getCropCachedImagePath());
                    setPicToView(gameBkgPicUri);
                    isImageResetByUser = true;
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
    private void setPicToView(Uri uri)
    {
        pictureSplitedView.reSetViewBitmap(FormatTools.uri2Bitmap(GameSettingActivity.this, uri));
    }
}
