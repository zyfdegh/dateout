package edu.nuist.dateout.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nuist.dateout.R;
import com.nuist.dateout.face.AnimatedGifDrawable;
import com.nuist.dateout.face.AnimatedImageSpan;
import com.nuist.dateout.media.VoiceAnimManager;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.interfc.ListItemsCallback;
import edu.nuist.dateout.misc.ImageLoaderCfg;
import edu.nuist.dateout.model.ChatBubbleItem;
import edu.nuist.dateout.model.ChatBubbleTag;
import edu.nuist.dateout.util.TimeAssit;
import edu.nuist.dateout.value.VariableHolder;

/**
 * @author Veayo 用于切换视图,以显示发方和收方气泡 能够根据消息类型切换至图片视图或者语音视图
 */
public class ChatBubbleAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener
{
    private Context context;
    
    private LayoutInflater inflater;
    
    private List<ChatBubbleItem> chatBubbleItemList;// 消息气泡列表
    
    private ListItemsCallback mCallback;// 列表中项目点击事件回调方法接口,在ChatActivity中进行处理
    
    /** 弹出的更多选择框 */
    private PopupWindow popupWindow;
    
    /** 复制，删除 */
    private TextView copy;
    
    public ChatBubbleAdapter(Context context, ListItemsCallback callback, List<ChatBubbleItem> chatBubbleItemList)
    {
        this.context = context;
        this.mCallback = callback;
        this.chatBubbleItemList = chatBubbleItemList;
        
        this.inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initPopWindow();
    }
    
    @Override
    public int getCount()
    {
        return chatBubbleItemList.size();
    }
    
    @Override
    public Object getItem(int position)
    {
        return chatBubbleItemList.get(position);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    public List<ChatBubbleItem> getChatBubbleItemList()
    {
        return chatBubbleItemList;
    }
    
    public void setChatBubbleItemList(List<ChatBubbleItem> chatBubbleItemList)
    {
        this.chatBubbleItemList = chatBubbleItemList;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        
        View view = null;
        ViewHolder holder = new ViewHolder();
        // 初始化popwindow
        
        ChatBubbleItem bubbleItem = chatBubbleItemList.get(position);
        boolean isMsgSentByMe = bubbleItem.isMsgSentByMe();
        
        if (isMsgSentByMe)
        {
            // 根据消息流向设置视图
            view = this.inflater.inflate(R.layout.item_chat_me, null);
            holder.userHeadImage = (ImageView)view.findViewById(R.id.iv_chat_myhead);
        }
        else
        {
            view = this.inflater.inflate(R.layout.item_chat_friend, null);
            holder.userHeadImage = (ImageView)view.findViewById(R.id.iv_chat_friendhead);
        }
        
        // 相同名称控件
        holder.chatVoice = (ImageView)view.findViewById(R.id.iv_chat_voice);
        holder.chatImage = (ImageView)view.findViewById(R.id.iv_chat_image);
        holder.chatText = (TextView)view.findViewById(R.id.tv_chat_text);
        holder.dateAndTime = (TextView)view.findViewById(R.id.tv_chat_msgdate);
        
        view.setTag(holder);
        
        // 设置监听
        holder.userHeadImage.setOnClickListener(this);
        holder.chatImage.setOnClickListener(this);
        holder.chatVoice.setOnClickListener(this);
        holder.chatText.setOnLongClickListener(new popAction(convertView, position));
        
        // 显示内容
        if (isMsgSentByMe)
        {
            String myheadImagePath = "file://" + DateoutApp.getInstance().getLoginUser().getHeadImageUri().toString();
            
            ImageLoader.getInstance().displayImage(myheadImagePath, holder.userHeadImage, ImageLoaderCfg.optionHead);
        }
        else
        {
            String friendheadImagePath =
                "file://" + DateoutApp.getInstance().getChatUser().getHeadImageUri().toString();
            ImageLoader.getInstance()
                .displayImage(friendheadImagePath, holder.userHeadImage, ImageLoaderCfg.optionHead);
        }
        
        if (bubbleItem.getMsgDateAndTime() != null)
        {
            holder.dateAndTime.setText(TimeAssit.toBriefFormatStyle2(bubbleItem.getMsgDateAndTime()));
        }
        
        // 检测消息类型
        switch (bubbleItem.getMsgType())
        {
            case VariableHolder.MSG_TYPE_TEXT:
                // 消息为文本
                holder.chatText.setVisibility(View.VISIBLE);
                if (bubbleItem.getMsgText() != null)
                {
                    // 对文本里的特殊内容(如表情)做处理
                    SpannableStringBuilder sb = textToSpannableStr(context, holder.chatText, bubbleItem.getMsgText());
                    holder.chatText.setText(sb);
                    holder.chatText.setAutoLinkMask(Linkify.ALL);// 设置自动显示超链接,电话号码
                    holder.chatText.setMovementMethod(LinkMovementMethod.getInstance());// 设置超链接点击跳转
                }
                break;
            case VariableHolder.MSG_TYPE_AUDIO:
                // 消息为语音
                holder.chatVoice.setVisibility(View.VISIBLE);
                if (bubbleItem.getMsgResUrl() != null)
                {
                    holder.chatVoice.setTag(new ChatBubbleTag(bubbleItem.getMsgType(), bubbleItem.getMsgResUrl(),
                        isMsgSentByMe));
                }
                break;
            case VariableHolder.MSG_TYPE_IMAGE:
                // 消息为图片
                holder.chatImage.setVisibility(View.VISIBLE);
                if (bubbleItem.getMsgResUrl() != null)
                {
                    ImageLoader.getInstance().displayImage(bubbleItem.getMsgResUrl(),
                        holder.chatImage,
                        ImageLoaderCfg.optionChat);
                    holder.chatImage.setTag(new ChatBubbleTag(bubbleItem.getMsgType(), bubbleItem.getMsgResUrl(),
                        isMsgSentByMe));
                }
                break;
            default:
                break;
        }
        return view;
    }
    
    private class ViewHolder
    {
        public ImageView userHeadImage;// 用户头像
        
        public TextView dateAndTime;// 消息时间日期
        
        public TextView chatText;// 文本消息
        
        public ImageView chatImage;// 图片消息
        
        public ImageView chatVoice;// 语音消息
    }
    
    /**
     * 响应点击事件,调用子定义接口，并传入View
     */
    @Override
    public void onClick(View v)
    {
        ChatBubbleTag tag = (ChatBubbleTag)v.getTag();
        switch (v.getId())
        {
        // 点击的是用户头像
            case R.id.iv_chat_myhead:
                mCallback.onClick(v);// 回调到ChatActivity进行处理
                break;
            // 点击的是好友的头像
            case R.id.iv_chat_friendhead:
                mCallback.onClick(v);// 回调到ChatActivity进行处理
                break;
            // 点击的是聊天发送的图片
            case R.id.iv_chat_image:
                mCallback.onClick(v);// 回调到ChatActivity进行处理
                mCallback.transViewTag(tag);
                break;
            // 点击的是聊天发送的语音
            case R.id.iv_chat_voice:
                // 播放语音动画
                //mCallback.onClick(v);
            	tag.setView(v);
                mCallback.transViewTag(tag);
                break;
            default:
                break;
        }
    }
    
    @Override
    public boolean onLongClick(View v)
    {
        mCallback.onLongClick(v);
        return false;
    }
    
    /********************************* 添加pop，实现复制文本功能 *******************************************/
    /**
     * 每个ITEM中more按钮对应的点击动作
     * */
    public class popAction implements OnLongClickListener
    {
        int position;
        
        View view;
        
        public popAction(View view, int position)
        {
            this.position = position;
            this.view = view;
        }
        
        @Override
        public boolean onLongClick(View v)
        {
            int[] arrayOfInt = new int[2];
            // 获取点击按钮的坐标
            v.getLocationOnScreen(arrayOfInt);
            int x = arrayOfInt[0];
            int y = arrayOfInt[1];
            showPop(v, x, y, view, position);
            return true;
        }
    }
    
    /**
     * 显示popWindow
     * */
    public void showPop(View parent, int x, int y, final View view, final int position)
    {
        // 设置popwindow显示位置
        if (y > 20)
        {
            y = y - 20;
        }
        popupWindow.showAtLocation(parent, 0, x, y);
        
        // 为按钮绑定事件
        // 复制
        copy.setOnClickListener(new View.OnClickListener()
        {
            
            @SuppressLint("NewApi")
            @Override
            // ??????????
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                if (popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                }
                // 获取剪贴板管理服务
                ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本数据复制到剪贴板
                cm.setText(chatBubbleItemList.get(position).getMsgText());// ?????
            }
        });
    }
    
    /**
     * 对文本内容进行处理，显示表情
     * 
     * @param gifTextView
     * @param content
     * @return
     */
    public SpannableStringBuilder textToSpannableStr(final Context context, final TextView gifTextView, String content)
    {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find())
        {
            String tempText = m.group();
            try
            {
                String num = tempText.substring("#[face/png/f_static_".length(), tempText.length() - ".png]#".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif 否则说明gif找不到，则显示png
                 * */
                InputStream is = context.getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is, new AnimatedGifDrawable.UpdateListener()
                {
                    @Override
                    public void update()
                    {
                        gifTextView.postInvalidate();
                    }
                })), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            }
            catch (Exception e)
            {
                String png = tempText.substring("#[".length(), tempText.length() - "]#".length());
                try
                {
                    sb.setSpan(new ImageSpan(context, BitmapFactory.decodeStream(context.getAssets().open(png))),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                catch (IOException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return sb;
    }
    
    /**
     * 初始化弹出的pop
     * */
    private void initPopWindow()
    {
        View popView = inflater.inflate(R.layout.chat_item_copy_menu, null);
        copy = (TextView)popView.findViewById(R.id.chat_copy_menu);
        popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        // 获取popwindow焦点
        popupWindow.setFocusable(true);
        // 设置popwindow如果点击外面区域，便关闭。
        popupWindow.setOutsideTouchable(true);
    }
}