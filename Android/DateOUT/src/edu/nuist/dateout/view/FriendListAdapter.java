package edu.nuist.dateout.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nuist.dateout.R;

import edu.nuist.dateout.model.FriendItem;

/**
 * 用于显示好友列表的类 Edit by Veayo on 2015/3/25
 */
public class FriendListAdapter extends BaseAdapter implements SectionIndexer
{
    private Context context;
    
    private List<FriendItem> friendList;// 存储所有好友的List
    
    public FriendListAdapter(Context context, List<FriendItem> friendItemList)
    {
        this.context = context;
        this.friendList = friendItemList;
    }
    
    public int getCount()
    {
        return friendList.size();
    }
    
    public Object getItem(int position)
    {
        return friendList.get(position);
    }
    
    public long getItemId(int position)
    {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        ViewHolder listItem = new ViewHolder();
        final FriendItem mContent = friendList.get(position);
        if (convertView == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_friendlist, null);
            listItem.tvLetter = (TextView) view.findViewById(R.id.tv_charater_catalog);
            listItem.headImageImageView = (ImageView)view.findViewById(R.id.headImageView);
            listItem.displayNameTextView = (TextView)view.findViewById(R.id.nameTextView);
            listItem.extendStringTextView = (TextView)view.findViewById(R.id.extendedTextView);
            listItem.onlineStatusTextView = (TextView)view.findViewById(R.id.onlineStatusTextView);
            view.setTag(listItem);
        }
        else
        {
            view = convertView;
            listItem = (ViewHolder)view.getTag();
        }
        
        FriendItem friendItem = friendList.get(position);
        
        listItem.headImageImageView.setImageURI(friendItem.getHeadImageUri());
        String remarkName = friendItem.getRemarkName();
        if (remarkName == null || remarkName.equals(""))
        {
            // 显示为用户Id
            listItem.displayNameTextView.setText(friendItem.getUserId());
        }
        else
        {
            // 显示为用户备注
            listItem.displayNameTextView.setText(friendItem.getRemarkName());
        }
        listItem.extendStringTextView.setText(friendItem.getExtendString());
        listItem.onlineStatusTextView.setText(friendItem.getOnlineStatus());
        
        
        // 根据position获取分类的首字母的Char ascii值
 		int section = getSectionForPosition(position);
 		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
 		if (position == getPositionForSection(section)) {
 			listItem.tvLetter.setVisibility(View.VISIBLE);
 			listItem.tvLetter.setText(mContent.getSortLetters());
 		} else {
 			listItem.tvLetter.setVisibility(View.GONE);
 		}
 		
        
        return view;
    }
    
    public List<FriendItem> getFriendList()
    {
        return friendList;
    }
    
    public void setFriendList(List<FriendItem> friendList)
    {
        this.friendList = friendList;
    }
    
    private class ViewHolder
    {
    	public TextView tvLetter;//字母组名称
    	
        public ImageView headImageImageView;// 用户头像
        
        public TextView displayNameTextView;// 显示名
        
        public TextView extendStringTextView;// 个性签名
        
        public TextView onlineStatusTextView;// 在线状态
    }
    
    
    /**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return friendList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = friendList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
	@Override
	public Object[] getSections() {
		return null;
	}
}
