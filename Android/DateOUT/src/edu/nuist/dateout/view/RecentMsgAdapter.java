package edu.nuist.dateout.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuist.dateout.R;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.model.RecentMsgItem;
import edu.nuist.dateout.util.TimeAssit;
import edu.nuist.dateout.value.VariableHolder;

public class RecentMsgAdapter extends BaseAdapter
{
    private Context context;
    private List<RecentMsgItem> recentMsgList;
    
    public RecentMsgAdapter(Context context, List<RecentMsgItem> recentMsgList)
    {
        this.context = context;
        this.recentMsgList = recentMsgList;
    }
    
    @Override
    public int getCount()
    {
        return recentMsgList.size();
    }
    
    @Override
    public Object getItem(int position)
    {
        return recentMsgList.get(position);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        ViewHolder holder = new ViewHolder();
        if (convertView == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_msglist, null);
            
            holder.userHeadImage = (ImageView)view.findViewById(R.id.ml_headImageView);
            holder.displayUserName = (TextView)view.findViewById(R.id.ml_nameTextView);
            holder.lastMessage = (TextView)view.findViewById(R.id.ml_lastMsg);
            holder.dateOrTime = (TextView)view.findViewById(R.id.ml_dateAndTimeTextView);
            holder.msgTypeImage = (ImageView)view.findViewById(R.id.ml_msgTypeImageView);
            holder.unreadMsgNum = (TextView)view.findViewById(R.id.ml_msgNumTextView);
            
            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }
        
        RecentMsgItem recentMsgItem = recentMsgList.get(position);
        
        holder.userHeadImage.setImageURI(recentMsgItem.getUserHeadUri());
        // 设置用户名
        holder.displayUserName.setText(recentMsgItem.getUserId());
        // 如果消息长于20个字,只保留最近消息的前15位显示到列表中
        String trimedMsgBody;
        if (recentMsgItem.getLastMessage().length() > 10)
        {
            trimedMsgBody = recentMsgItem.getLastMessage().substring(0, 10);
        }
        else
        {
            trimedMsgBody = recentMsgItem.getLastMessage();
        }
        holder.lastMessage.setText(trimedMsgBody);
        
        // 将日期时间转换为用户友好的简略形式
        holder.dateOrTime.setText(TimeAssit.toBriefFormat(recentMsgItem.getMsgDateAndTime()));
        // 未读消息数目设置
        if (recentMsgItem.getUnReadMsgNum() > 0)
        {
            holder.unreadMsgNum.setText(recentMsgItem.getUnReadMsgNum() + "");
            holder.unreadMsgNum.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.unreadMsgNum.setText("");
            holder.unreadMsgNum.setVisibility(View.GONE);
        }
        // 消息类型设置
        String loginId = DateoutApp.getInstance().getLoginUser().getUserId();
        boolean isMsgSentByMe = recentMsgItem.getUserId().equals(loginId) ? true : false;
        switch (recentMsgItem.getMsgType())
        {
            case VariableHolder.MSG_TYPE_TEXT:
                holder.msgTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.msg_text));
                break;
            case VariableHolder.MSG_TYPE_IMAGE:
                if (isMsgSentByMe)
                {
                    holder.lastMessage.setText("发送一张图片");
                    holder.msgTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.msg_image));
                }
                else
                {
                    holder.lastMessage.setText("收到一张图片");
                    holder.msgTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.msg_image));
                }
                break;
            case VariableHolder.MSG_TYPE_AUDIO:
                if (isMsgSentByMe)
                {
                    holder.lastMessage.setText("发出一段语音");
                    holder.msgTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.msg_voice));
                }
                else
                {
                    holder.lastMessage.setText("收到一段语音");
                    holder.msgTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.msg_voice));
                }
                break;
            case VariableHolder.MSG_TYPE_FILE:
                if (isMsgSentByMe)
                {
                    holder.lastMessage.setText("收到一个文件");
                    holder.msgTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.msg_voice));
                }
                else
                {
                    holder.lastMessage.setText("发出一个文件");
                    holder.msgTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.msg_voice));
                }
                break;
            default:
                break;
        }
        return view;
    }
    
    public List<RecentMsgItem> getRecentMsgList()
    {
        return recentMsgList;
    }
    
    public void setRecentMsgList(List<RecentMsgItem> recentMsgList)
    {
        this.recentMsgList = recentMsgList;
    }
    
    private class ViewHolder
    {
        public TextView displayUserName;// 显示用户名
        
        public TextView lastMessage;// 最后一条消息
        
        public TextView dateOrTime;// 消息时间日期
        
        public TextView unreadMsgNum;// 未读消息数目
        
        public ImageView msgTypeImage;// 消息类型,用图标表示
        
        public ImageView userHeadImage;// 用户头像
    }
    
    // 设置事件回调，通知 Mainactivity 消息列表变化
}
