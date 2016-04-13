package com.nuist.dateout.tab1.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nuist.dateout.R;
import com.nuist.dateout.listener.MyOnGesTureListener;

import edu.nuist.dateout.db.DBAssit;
import edu.nuist.dateout.util.CacheAssit;

public class ClearTrashActivity extends Activity implements OnClickListener
{
    private GestureDetector detector;
    
    ProgressDialog pd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.clear_trash_activity);
        detector = new GestureDetector(this, new MyOnGesTureListener(this));
        initViews();
    }
    
    private void initViews()
    {
        View clearRecordView = findViewById(R.id.lo_trash_chat_record);
        View clearPictureView = findViewById(R.id.lo_trash_picture);
        View clearVoiceView = findViewById(R.id.lo_trash_voice);
        View clearAllView = findViewById(R.id.lo_trash_all);
        ImageButton backButton = (ImageButton)findViewById(R.id.ib_clear_trash_back);
        pd = new ProgressDialog(this);
        
        backButton.setOnClickListener(this);
        clearRecordView.setOnClickListener(this);
        clearPictureView.setOnClickListener(this);
        clearVoiceView.setOnClickListener(this);
        clearAllView.setOnClickListener(this);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return detector.onTouchEvent(event);
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.lo_trash_chat_record:
                clearRecord();
                break;
            case R.id.lo_trash_picture:
                clearPicture();
                break;
            case R.id.lo_trash_voice:
                clearVoice();
                break;
            case R.id.lo_trash_all:
                clearAl();
                break;
            case R.id.ib_clear_trash_back:
                finish();
                break;
            default:
                break;
        }
    }
    
    private void clearAl()
    {
        new CacheAssit().clearAudioCache();
        new CacheAssit().clearPictureCache();
        DBAssit dbAssit = new DBAssit();
        if (!dbAssit.isDbConnected())
        {
            dbAssit.connectDb();
        }
        dbAssit.deleteAllFromTableChatHistory();
        dbAssit.deleteAllFromTableMsgList();
        dbAssit.closeDbConnect();
        // 显示圈圈
        pd.setTitle("请稍候");
        pd.setMessage("正在清空数据");
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.show();
        
        new Handler().postDelayed(new Runnable()
        {
            
            @Override
            public void run()
            {
                pd.dismiss();
                Toast.makeText(ClearTrashActivity.this, "清理完成", Toast.LENGTH_LONG).show();
            }
        }, 5000);
    }
    
    private void clearVoice()
    {
        new CacheAssit().clearAudioCache();
        // 显示圈圈
        pd.setTitle("请稍候");
        pd.setMessage("正在清理语音缓存");
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.show();
        
        new Handler().postDelayed(new Runnable()
        {
            
            @Override
            public void run()
            {
                pd.dismiss();
                Toast.makeText(ClearTrashActivity.this, "清理完成", Toast.LENGTH_LONG).show();
            }
        }, 1000);
    }
    
    private void clearPicture()
    {
        new CacheAssit().clearPictureCache();
        // 显示圈圈
        pd.setTitle("请稍候");
        pd.setMessage("正在清理图片缓存");
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.show();
        
        new Handler().postDelayed(new Runnable()
        {
            
            @Override
            public void run()
            {
                pd.dismiss();
                Toast.makeText(ClearTrashActivity.this, "清理完成", Toast.LENGTH_LONG).show();
            }
        }, 2000);
    }
    
    private void clearRecord()
    {
        DBAssit dbAssit = new DBAssit();
        if (!dbAssit.isDbConnected())
        {
            dbAssit.connectDb();
        }
        dbAssit.deleteAllFromTableChatHistory();
        dbAssit.deleteAllFromTableMsgList();
        dbAssit.closeDbConnect();
        // 显示圈圈
        pd.setTitle("请稍候");
        pd.setMessage("正在删除消息历史");
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.show();
        
        new Handler().postDelayed(new Runnable()
        {
            
            @Override
            public void run()
            {
                pd.dismiss();
                Toast.makeText(ClearTrashActivity.this, "清理完成", Toast.LENGTH_LONG).show();
            }
        }, 1500);
    }
}
