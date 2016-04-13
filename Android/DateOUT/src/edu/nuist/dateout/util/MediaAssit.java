package edu.nuist.dateout.util;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.util.Log;

public class MediaAssit
{
    /**
     * 播放指定路径的音频文件
     * @param filePath 音频文件绝对完整路径
     */
    public void playAudio(File file,final Handler handler){
        if (file == null || !file.exists() || file.length() == 0) {
            //文件无法播放
            Log.v("Dateout", "ChatActivity==>"+"语音文件无法打开,不存在或者无效");
        }else {
            Log.v("Dateout", "ChatActivity==>"+"语音开始播放");
            
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // TODO Auto-generated method stub
                    mediaPlayer.release();
                    handler.sendEmptyMessage(1000);
                    Log.v("Dateout", "ChatActivity==>"+"语音播放结束");
                }
            });
            try {
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
