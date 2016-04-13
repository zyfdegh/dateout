package com.nuist.dateout.tab4.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nuist.dateout.R;
import com.nuist.dateout.tab1.activity.FriendCardActivity;
import com.nuist.picturegame.view.GamePintuLayout;
import com.nuist.picturegame.view.GamePintuLayout.GamePintuListener;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.GameConfig;
import edu.nuist.dateout.model.GameConfigInfo;
import edu.nuist.dateout.task.UploadFileTask;
import edu.nuist.dateout.task.UploadGameConfigTask;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.util.FileNameGenTool;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.util.FormatTools;
import edu.nuist.dateout.value.VariableHolder;

public class PictureGameActivity extends Activity implements android.view.View.OnClickListener
{
    
    private GamePintuLayout mGamePintuLayout;
    
    private TextView mTime;
    
    private Animation animation;
    
    private ImageView gameStartImageView;
    
    /** 游戏配置参数 **/
    private int transportColum;// 列数
    
    private int transportTime;// 时间
    
    private int transportTag;// 传过来的标识，标识从哪个activity跳转,tag为1,使设置和重新开始游戏隐藏
    
    private String transportBkgPicUriStr;// 传过来的背景图片Uri
    
    // 暂停或开始的标识位
    public static Boolean isStart = true;
    
    /** 保存当前游戏难度 */
    private int currentColum;
    
    /** 保存当前游戏时间 */
    private int currentTime;
    
    /** 存储是否弹出设置游戏的dialog，true代表弹出，当弹出时，设置返回键无效 */
    private Boolean isShowSettingDialog = false;
    
    private GameConfigInfo gameConfigInfo;
    
    private String userToAddId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.picturegame_activity);
        
        userToAddId = getIntent().getStringExtra("userid_game");
        // 获取游戏配置
        Intent intent = getIntent();
        gameConfigInfo = (GameConfigInfo)intent.getSerializableExtra("gameConfig");
        transportColum = gameConfigInfo.getColumn();
        transportTime = gameConfigInfo.getTime();
        transportTag = gameConfigInfo.getTag();
        transportBkgPicUriStr = gameConfigInfo.getBkgPicUriStr();
        // 检测配置
        currentColum = transportColum;
        currentTime = transportTime;
        mTime = (TextView)findViewById(R.id.tv_game_time);
        
        // 暂停、开始按钮
        gameStartImageView = (ImageView)findViewById(R.id.iv_game_start);
        gameStartImageView.setOnClickListener(this);
        // 设置按钮
        ImageView gameSettingImageView = (ImageView)findViewById(R.id.iv_game_setting);
        gameSettingImageView.setOnClickListener(this);
        // 重新开始按钮
        ImageView gameRestartImageView = (ImageView)findViewById(R.id.iv_game_restart);
        gameRestartImageView.setOnClickListener(this);
        
        mGamePintuLayout = (GamePintuLayout)findViewById(R.id.id_gamepintu);
        
        // 判断从哪个acticity过来，使控件隐藏
        if (transportTag == 1)
        {
            gameSettingImageView.setVisibility(View.GONE);
            gameRestartImageView.setVisibility(View.GONE);
        }
        
        // 从Uri读取Bitmap
        Bitmap bkgPicBitmap = FormatTools.uri2Bitmap(PictureGameActivity.this, Uri.parse(transportBkgPicUriStr));
        mGamePintuLayout.setmBitmap(bkgPicBitmap);
        mGamePintuLayout.countTimeBaseLevel(transportTime);
        mGamePintuLayout.setCurrentColum(transportColum);
        mGamePintuLayout.setTimeEnabled(true);
        // 设置显示时间的textview的动画
        animation = AnimationUtils.loadAnimation(this, R.anim.game_timer_anim);
        
        mGamePintuLayout.setOnGamePintuListener(new GamePintuListener()
        {
            @Override
            public void timechanged(int currentTime)
            {
                if (currentTime < 10)
                {
                    mTime.setText("0" + currentTime);
                }
                else
                {
                    mTime.setText("" + currentTime);
                }
                if (currentTime != 0)
                {
                    mTime.startAnimation(animation);
                }
            }
            
            @Override
            public void gameSuccess()
            {
                if (transportTag == 1)
                {
                    new AlertDialog.Builder(PictureGameActivity.this).setTitle("完成游戏!")
                        .setMessage("您通过了对方设置的解锁游戏!")
                        .setCancelable(false)
                        .setPositiveButton("查看TA的更多信息", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent intent = new Intent(PictureGameActivity.this, FriendCardActivity.class);
                                intent.putExtra("userid_game", userToAddId);
                                intent.putExtra("game_time",
                                    gameConfigInfo.getTime() - mGamePintuLayout.getSuccess_time());
                                intent.putExtra("game_steps", mGamePintuLayout.getSuccess_step());
                                intent.putExtra("game_retry_times", mGamePintuLayout.getSuccess_number());
                                intent.putExtra("game_colum", gameConfigInfo.getColumn());
                                startActivity(intent);
                            }
                        })
                        .show();
                }
                else
                {
                    new AlertDialog.Builder(PictureGameActivity.this).setTitle("完成游戏配置预览")
                        .setMessage("陌生人通过您设置的游戏后,便能够添加您为好友,是否需要重新配置游戏?")
                        .setCancelable(false)
                        .setNeutralButton("重新配置", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                finish();
                            }
                        })
                        .setPositiveButton("完成配置", new OnClickListener()
                        {
                            
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                uploadGameConfig();
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new OnClickListener()
                        {
                            
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
//                                restartGame();
                            }
                        })
                        .show();
                }
            }
            
            @Override
            public void gameover()
            {
                new AlertDialog.Builder(PictureGameActivity.this).setTitle("游戏结束")
                    .setMessage("请选择重新再来或者直接退出")
                    .setCancelable(false)
                    .setPositiveButton("重新开始", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mGamePintuLayout.setCurrentColum(currentColum);
                            mGamePintuLayout.countTimeBaseLevel(currentTime);
                            
                            mGamePintuLayout.restart(true);
                        }
                    })
                    .setNegativeButton("退出", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }
                    })
                    .show();
            }
        });
        
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_game_start:
                gamePause(true);
                break;
            case R.id.iv_game_setting:
                // 上传游戏配置
                uploadGameConfig();
                break;
            case R.id.iv_game_restart:
                restartGame();
                break;
            default:
                break;
        }
    }
    
    private void uploadGameConfig()
    {
        // 先上传游戏背景图片
        String srcFilePath = FormatTools.uri2Path(PictureGameActivity.this, Uri.parse(transportBkgPicUriStr));
        File srcFile = new File(srcFilePath);
        // 拷贝文件到游戏目录,并重命名
        final String userId = DateoutApp.getInstance().getLoginUser().getUserId();
        final String dstFileName = new FileNameGenTool().generateGameImageName(srcFile, userId);
        String dstFilePath = FilePathTool.getGameImagePath(dstFileName);
        File dstFile = new File(dstFilePath);
        // 拷贝文件
        short copyResult = new FileAssit().copyFile(srcFile, dstFile);
        
        final Handler uploadConfigHandler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                switch (msg.what)
                {
                    case 1:
                        // 保存游戏配置成功
                        Toast.makeText(PictureGameActivity.this, "保存游戏配置成功", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(PictureGameActivity.this, "保存游戏配置失败", Toast.LENGTH_LONG).show();
                        // 保存游戏配置失败
                        break;
                    default:
                        break;
                }
            };
        };
        
        Handler uploadImageHandler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                switch (msg.what)
                {
                    case 1:
                        // 上传背景图片成功
                        if (msg.obj == null)
                        {
                            // 上传背景图片失败
                            Toast.makeText(PictureGameActivity.this, "上传游戏图片失败", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(PictureGameActivity.this, "上传游戏图片成功", Toast.LENGTH_SHORT).show();
                            // 先获取游戏配置
                            GameConfig config = new GameConfig();
                            config.setDifficulty(transportColum);
                            config.setTimeOut(transportTime);
                            config.setUserId(userId);
                            config.setPicUrl(dstFileName);// 上传的时候只需要给一个文件名
                            // 上传游戏配置
                            new UploadGameConfigTask(config, uploadConfigHandler).execute();
                        }
                        break;
                    
                    default:
                        break;
                }
            };
        };
        
        if (copyResult == VariableHolder.COPY_RESULT_SUCCESS)
        {
            // 上传文件dstFile
            new UploadFileTask(dstFile, uploadImageHandler).execute();
        }
    }
    
    /**
     * 重新开始游戏
     */
    private void restartGame()
    {
        if (isStart)
        {
            mGamePintuLayout.restart(true);
        }
    }
    
    /**
     * 切换开始 和 暂停
     */
    private void gamePause(Boolean tag)
    {
        isStart = !isStart;
        if (isStart)
        {
            gameStartImageView.setImageDrawable(getResources().getDrawable(R.drawable.i_game_start));
            mGamePintuLayout.resume();
        }
        else
        {
            gameStartImageView.setImageDrawable(getResources().getDrawable(R.drawable.i_game_pause));
            mGamePintuLayout.pause();
            if (tag)
            {
                new AlertDialog.Builder(PictureGameActivity.this).setTitle("游戏暂停")
                    .setMessage("准备好请点击开始\n预祝你成功")
                    .setCancelable(false)
                    .setPositiveButton("开始", new OnClickListener()
                    
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            gamePause(false);
                        }
                    })
                    .show();
            }
            
        }
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        isStart = false;
        mGamePintuLayout.pause();
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        mGamePintuLayout.resume();
        isStart = true;
    }
}
