package edu.nuist.dateout.interfc;

import android.view.View;
import edu.nuist.dateout.model.ChatBubbleTag;

/**
 *  自定义接口，用于回调按钮点击事件到ChatActivity
 * @author Veayo
 *
 */
public interface ListItemsCallback {
    void onClick(View v);//用于传送ChatBubble点击事件到ChatActivity
    void onLongClick(View v);
    void transViewTag(ChatBubbleTag tag);
    
}