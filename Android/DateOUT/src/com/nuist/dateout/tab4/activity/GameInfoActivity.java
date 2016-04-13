package com.nuist.dateout.tab4.activity;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dateout.R;
import com.nuist.dateout.listener.MyOnGesTureListener;
import com.nuist.picturegame.view.PictureSplitedView;

import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.model.GameConfigInfo;
import edu.nuist.dateout.task.DownloadFileTask;
import edu.nuist.dateout.task.FetchGameConfigTask;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.util.NetworkAssit;

public class GameInfoActivity extends Activity
{
    private PictureSplitedView gameImageView;// 游戏图片
    
    private TextView gameLevelTextView;// 游戏级别
    
    private TextView gameTimeTextView;// 游戏时间
    
    private Button gameStartButton;
    
    private String userToAddId;// 要添加的用户Id
    
    private String picSavePath;
    
    private Context context;
    
    private boolean canPlayGame;
    
    private GameConfigInfo gameConfig;
    
    private GestureDetector detector;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_info_activity);
        
        canPlayGame = false;
        
        gameConfig = new GameConfigInfo();
        gameConfig.setTag(1);
        context = this;
        // 设置图片下载路径
        picSavePath = FilePathTool.getDownloadCachedImagePath();
        
        // 获取Intent传过来的userId
        userToAddId = getIntent().getStringExtra("userid_game");
        
        // 加载该用户的游戏配置信息
        loadGameConfig();
        
        initViews();
        gameStartButton.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (canPlayGame)
                {
                    // 点击之后启动游戏,参数为gameConfig
                    Intent intent = new Intent();
                    intent.setClass(GameInfoActivity.this, PictureGameActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("gameConfig", gameConfig);
                    intent.putExtras(data);
                    intent.putExtra("userid_game", userToAddId);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(context, "暂时不能开始解锁游戏=.=", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return detector.onTouchEvent(event);
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
                            if (!config.getPicUrl().equals(""))
                            {
                                canPlayGame = false;
                                // 调用下载器,给定链接,下载文件到本地
                                new DownloadFileTask(config.getPicUrl(), bkgPicDownHandler, picSavePath).execute();// 下载图片
                                Uri bkgPicUri =
                                    FormatTools.resId2Uri(context.getResources(), R.drawable.default_game_bkg2);// 图片显示为加载中
                                gameConfig.setBkgPicUriStr(bkgPicUri.toString());
                                gameConfig.setColumn(1);
                            }
                            else
                            {
                                canPlayGame = false;
                                // 使用默认背景
                                Uri defaultBkgPicUri =
                                    FormatTools.resId2Uri(context.getResources(), R.drawable.default_game_bkg2);// 默认背景图片
                                gameConfig.setBkgPicUriStr(defaultBkgPicUri.toString());
                                gameConfig.setColumn(1);
                            }
                            // 设置格数
                            if (config.getDifficulty() != 0)
                            {
                                gameConfig.setColumn(config.getDifficulty());
                            }
                            else
                            {
                                gameConfig.setColumn(1);
                            }
                            // 设置游戏超时
                            if (config.getTimeOut() != 0)
                            {
                                gameConfig.setTime(config.getTimeOut());
                            }
                            else
                            {
                                gameConfig.setTime(0);
                            }
                        }
                        else
                        {
                            // 设置为默认值
                            Uri defaultBkgPicUri =
                                FormatTools.resId2Uri(context.getResources(), R.drawable.default_game_bkg2);// 默认背景图片
                            gameConfig.setBkgPicUriStr(defaultBkgPicUri.toString());
                            gameConfig.setColumn(1);
                            gameConfig.setTime(0);
                        }
                        
                        // 设置界面显示
                        gameImageView.reSetViewBitmap(FormatTools.uri2Bitmap(context,
                            Uri.parse(gameConfig.getBkgPicUriStr())));
                        // 格子数
                        int column = gameConfig.getColumn();
                        gameLevelTextView.setText(column + "*" + column);
                        gameTimeTextView.setText("" + gameConfig.getTime());
                        break;
                    default:
                        break;
                }
            }
        };
        
        if (new NetworkAssit(GameInfoActivity.this).isNetworkConnected())
        {
            // 通过用户Id去联系服务器获取该用户的游戏配置,和游戏背景图片
            new FetchGameConfigTask(userToAddId, gameConfigFetchHandler, GameInfoActivity.this).execute();
        }
        else
        {
            Toast.makeText(context, "网络不可用,无法加载头像", Toast.LENGTH_LONG).show();
        }
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
                    if (file != null && file.exists())
                    {
                        Uri bkgPicDownloadedUri = Uri.fromFile(file);
                        // 显示图片
                        gameImageView.reSetViewBitmap(FormatTools.uri2Bitmap(context, bkgPicDownloadedUri));
                        gameImageView.reSetViewColum(gameConfig.getColumn());
                        gameConfig.setBkgPicUriStr(bkgPicDownloadedUri.toString());
                        canPlayGame = true;
                    }
                    break;
                
                default:
                    break;
            }
        };
    };
    
    private void initViews()
    {
        gameImageView = (PictureSplitedView)findViewById(R.id.iv_game_info_image);
        gameLevelTextView = (TextView)findViewById(R.id.tv_game_info_level);
        gameTimeTextView = (TextView)findViewById(R.id.tv_game_info_time);
        gameStartButton = (Button)findViewById(R.id.btn_gameinfo_start);
        ImageButton backButton = (ImageButton)findViewById(R.id.ib_game_info_back);
        backButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        detector = new GestureDetector(this, new MyOnGesTureListener(this));
    }
    
}
