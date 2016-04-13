package edu.nuist.dateout.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.nuist.dateout.R;
import edu.nuist.dateout.model.UserRandom;
import edu.nuist.dateout.value.VariableHolder;

/**
 * tab1页面的gridview适配器
 * 
 * @author liyuxin
 */
public class MyGridViewAdapter extends BaseAdapter
{
    
    private List<UserRandom> userRandomList;

    private LayoutInflater inflater;
    
    private DisplayImageOptions options;// 加载图片的模式
    
    private Context context;
    
    public MyGridViewAdapter(Context context, List<UserRandom> list)
    {
        userRandomList = list;
        inflater = LayoutInflater.from(context);
        this.context = context;
        options =
            new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.i1)
                .showImageForEmptyUri(R.drawable.default_head)
                .showImageOnFail(R.drawable.default_head)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
    
    @Override
    public int getCount()
    {
        return userRandomList.size();
    }
    
    public List<UserRandom> getUserRandomList()
    {
        return userRandomList;
    }
    
    public void setUserRandomList(List<UserRandom> userRandomList)
    {
        this.userRandomList = userRandomList;
    }
    
    @Override
    public Object getItem(int position)
    {
        return userRandomList.get(position);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder hodler = new ViewHolder();
        View view = convertView;
        if (view == null)
        {
            view = inflater.inflate(R.layout.commend_friend_item, null);
            hodler.headImageView = (ImageView)view.findViewById(R.id.iv_item_head);
            hodler.onlineStateImage = (ImageView)view.findViewById(R.id.iv_item_state);
            hodler.distanceTextView = (TextView)view.findViewById(R.id.tv_item_distance);
            view.setTag(hodler);
        }
        else
        {
            hodler = (ViewHolder)view.getTag();
        }
        // 图片
        ImageLoader.getInstance()
            .displayImage(userRandomList.get(position).getHeadUrl(), hodler.headImageView, options);
        // 在线状态
        if (userRandomList.get(position).getOnlineStat() == VariableHolder.STAT_ONLINE)
        {
            hodler.onlineStateImage.setImageDrawable(context.getResources().getDrawable(R.drawable.state_point_green));
        }
        else
        {
            hodler.onlineStateImage.setImageDrawable(context.getResources().getDrawable(R.drawable.state_point_red));
        }
        // 距离
        int distance = (int)userRandomList.get(position).getDistance();
        if (distance==-1)
        {
            //-1表示没有数据
            hodler.distanceTextView.setText("无数据");
        }else {
            hodler.distanceTextView.setText(distance + "m");
        }
        return view;
    }
    
    private class ViewHolder
    {
        ImageView headImageView;
        
        ImageView onlineStateImage;
        
        TextView distanceTextView;
    }
}
