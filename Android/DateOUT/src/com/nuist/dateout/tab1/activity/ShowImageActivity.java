package com.nuist.dateout.tab1.activity;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nuist.dateout.R;

import edu.nuist.dateout.model.DownloadResult;
import edu.nuist.dateout.task.DownloadAndCacheTask;
import edu.nuist.dateout.util.FilePathTool;
import edu.nuist.dateout.value.VariableHolder;
import edu.nuist.dateout.view.DialogCenter;
import edu.nuist.dateout.view.ZoomImageView;

public class ShowImageActivity extends Activity
{
    
    private ZoomImageView showImageView;
    
    private String picUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_activity);
        showImageView = (ZoomImageView)findViewById(R.id.ziv_show_image);
        picUrl = (String)getIntent().getExtras().get("imageUrl");
        
        // 通过文件名找缓存,找不到就去下载高清原图并显示
        String fileName = picUrl.substring(picUrl.lastIndexOf(VariableHolder.FILE_PREFIX_IMAGE_SENT));
        String fileSavePath = FilePathTool.getImageReceivedPath(fileName);
        File cacheFile = new File(fileSavePath);
        if (cacheFile.exists() && cacheFile.canRead())
        {
            // 显示图片
            showImageView.setImageURI(Uri.parse(fileSavePath));
        }
        else
        {
            // 再去发送的图片文件夹找
            fileSavePath = FilePathTool.getImageSentPath(fileName);
            cacheFile = new File(fileSavePath);
            if (cacheFile.exists() && cacheFile.canRead())
            {
                // 显示图片
                showImageView.setImageURI(Uri.parse(fileSavePath));
            }
            else
            {
                // 先显示默认图片
                showImageView.setImageResource(R.drawable.default_received_image);
                // 下载文件并拷贝到程序目录
                new DownloadAndCacheTask(fileName, fileSavePath, handler).execute();
            }
        }
        
        ImageLoader.getInstance().displayImage(picUrl, showImageView);
        
        showImageView.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        showImageView.setOnLongClickListener(new OnLongClickListener()
        {
            
            @Override
            public boolean onLongClick(View v)
            {
                new DialogCenter().popSaveImageDialog(ShowImageActivity.this);
                
                return false;
            }
        });
    }
    
    private Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    DownloadResult result = (DownloadResult)msg.obj;
                    if (result.isDownloadSuccess())
                    {
                        // 显示图片
                        showImageView.setImageURI(Uri.parse(result.getFilePath()));
                    }
                    else
                    {
                        ImageLoader.getInstance().displayImage(picUrl, showImageView);
                    }
                    break;
                
                default:
                    break;
            }
        };
    };
}
