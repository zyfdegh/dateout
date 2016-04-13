package com.nuist.dateout.media;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.widget.ImageView;
import com.nuist.dateout.R;

/**
 * 用于控制语音播放动画的显示
 * @author Veayo
 *
 */
public class VoiceAnimManager{
    
    private Boolean isMe;
    //语音播放动画
    private AnimationDrawable animationDrawable;
    private ImageView voiceImageView;
    
    /**
     * 用于播放voiceImageview的动画
     * @param animationDrawable
     * @param voiceImageview
     */
    public VoiceAnimManager(ImageView voiceImageView,Boolean isMe)
    {
        super();
        this.voiceImageView = voiceImageView;
        this.animationDrawable = (AnimationDrawable)voiceImageView.getDrawable();
        this.isMe = isMe;
    }

    public void playVoiceAnimation(){
        //用于控制播放语音的动画的显示
    	animationDrawable.start();
    }
    public void stopVoiceAnimation(){
    	   animationDrawable.stop();
    	   animationDrawable.selectDrawable(0);
    }
}