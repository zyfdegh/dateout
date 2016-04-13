package com.nuist.dateout.media;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuist.dateout.media.RecordManger.SoundAmplitudeListen;

import com.nuist.dateout.R;
import edu.nuist.dateout.task.UploadFileTask;

public class RecordDialog
{
    
    /** 显示录音振幅 */
    private ImageView progress;
    
    // 麦克风按钮
    private ImageView mic_icon;
    
    /** 显示录音振幅的图片缓存 */
    private Drawable[] progressImg = new Drawable[7];
    
    /** 显示计时器 */
    private TextView text_msg;
    
    /** 显示录音振幅 */
    private TextView dialog_title;
    
    /** 录音对话框视图 */
    private View dialog_view;
    
    /** 录音对话框 */
    private AlertDialog dialog;
    
    // 用于处理上传文件结果
    private Handler uploadHandler;
    
    private int countTime = 30;
    
    private boolean isCountDownTime = false;
    
    private RecordManger recordManger;
    
    private Handler handler = new Handler();
    
    private Runnable run = new Runnable()
    {
        
        @Override
        public void run()
        {
            text_msg.setText(countTime + "秒");
            countTime--;
            if (countTime < 0)
            {
                handler.removeCallbacks(run);
                submit();
                return;
            }
            handler.postDelayed(run, 1000);
        }
    };
    
    public RecordDialog(Context context, Handler uploadHandler)
    {
        this.uploadHandler = uploadHandler;
        
        dialog_view = LayoutInflater.from(context).inflate(R.layout.dialog_sound, null);
        
        
        progressImg[0] = context.getResources().getDrawable(R.drawable.mic_1);// 初始化振幅图片
        progressImg[1] = context.getResources().getDrawable(R.drawable.mic_2);// 初始化振幅图片
        progressImg[2] = context.getResources().getDrawable(R.drawable.mic_3);// 初始化振幅图片
        progressImg[3] = context.getResources().getDrawable(R.drawable.mic_4);// 初始化振幅图片
        progressImg[4] = context.getResources().getDrawable(R.drawable.mic_5);// 初始化振幅图片
        progressImg[5] = context.getResources().getDrawable(R.drawable.mic_6);// 初始化振幅图片
        progressImg[6] = context.getResources().getDrawable(R.drawable.mic_7);// 初始化振幅图片
        
        dialog = new AlertDialog.Builder(context).setView(dialog_view).show();
        dialog.hide();
        dialog.setOnDismissListener(onDismissListener);// 设置对话框回退键监听
        
        progress = (ImageView)dialog_view.findViewById(R.id.sound_progress);// 振幅进度条
        mic_icon = (ImageView)dialog.findViewById(R.id.mic);// 状态图标
        dialog_title = (TextView)dialog.findViewById(R.id.title);// 标题
        text_msg = (TextView)dialog.findViewById(R.id.msg);
        
        // 初始化录音管理器
        recordManger = new RecordManger();
        // 设置振幅监听器
        recordManger.setSoundAmplitudeListen(onSoundAmplitudeListen);
    }
    
    public boolean isCountDownTime()
    {
        return isCountDownTime;
    }
    
    public RecordDialog setCountDownTime(boolean isCountDownTime)
    {
        this.isCountDownTime = isCountDownTime;
        return this;
    }
    
    public void showDialog()
    {
        // 开始录音
        startRecord();
        
        dialog.show();// 显示对话框
        if (isCountDownTime)
        {
            handler.post(run);// 显示计时器
        }
    }
    
    /** 回调振幅，根据振幅设置图片 */
    private SoundAmplitudeListen onSoundAmplitudeListen = new SoundAmplitudeListen()
    {
        
        @Override
        public void amplitude(int amplitude, int db, int value)
        {
            if (value >= 6)
            {
                value = 6;
            }
            progress.setBackgroundDrawable(progressImg[value]);// 显示震幅图片
        }
    };
    
    /** 监听器-当对话框提交 */
    public File submit()
    {
        dialog_title.setText("网络处理中");
        dialog.dismiss();
        
        // 停止录音并上传文件
        File file = stopRecordAndUpload();
        
        stopTime();
        if (file != null && file.exists() && file.length() > 0)
        {
            return file;
        }
        else
        {
            return null;
        }
    }
    
    /** 监听器-按下回退键时停止录音 */
    private OnDismissListener onDismissListener = new OnDismissListener()
    {
        
        @Override
        public void onDismiss(DialogInterface arg0)
        {
            recordManger.stopRecord();
            dialog.cancel();
            stopTime();
        }
    };
    
    private void stopTime()
    {
        handler.removeCallbacks(run);// 移除计时器
        text_msg.setText("");
    }
    
    public int getCountTime()
    {
        return countTime;
    }
    
    public RecordDialog setCountTime(int countTime)
    {
        this.countTime = countTime;
        return this;
    }
    
    /** 启动录音不进行网络上传 */
    private void startRecord()
    {
        try
        {
            recordManger.startRecordCreateFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /** 停止录音后上传 */
    private File stopRecordAndUpload()
    {
        File file = recordManger.stopRecord();// 停止录音
        if (file == null || !file.exists() || file.length() == 0)
        {
            // 文件不存在或已经损坏
            return null;
        }
        // TODO 上传文件file
        if (uploadHandler != null)
        {
            new UploadFileTask(file, uploadHandler).execute();
        }
        return recordManger.getFile();
    }
}
