package com.nuist.picturegame.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nuist.picturegame.util.ImagePiece;
import com.nuist.picturegame.util.ImageSplitterUtil;

import com.nuist.dateout.R;

public class PictureSplitedView extends RelativeLayout {

	/**
	 * 游戏面板的宽度
	 */
	private int mWidth;
	private List<ImagePiece> mItemBitmaps;
	private Bitmap mBitmap;
	private int mColumn = 3;//默认是3
	private int mMargin = 1;
	private int mItemWidth;
	private int mPadding;
	private ImageView[] mGamePintuItems;
	private Bitmap translateBitmap;

	public void setTranslateBitmap(Bitmap translateBitmap) {
		this.translateBitmap = translateBitmap;
	}
	
	public void reSetViewBitmap(Bitmap bitmap){
		translateBitmap = bitmap;
		this.removeAllViews();
		initBitmap();
		initItem();
	}
	
	public void reSetViewColum(int colum){
		mColumn = colum;
		this.removeAllViews();
		initBitmap();
		initItem();
	}
	
	public PictureSplitedView(Context context) {
		super(context);
	}
	public PictureSplitedView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public PictureSplitedView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 取宽和高中的小值
		mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

		// 进行切图，以及排序
		initBitmap();
		// 设置ImageView(Item)的宽高等属性
		initItem();
		// 判断是否开启时间

		setMeasuredDimension(mWidth, mWidth);
	}
	
	
	
	/**
	 * 进行切图，以及排序
	 */
	private void initBitmap()
	{
		if (translateBitmap == null) {
			mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.i3);
		}else {
			mBitmap = translateBitmap;
		}
		mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);
		
	}

	
	private void init()
	{
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				3, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
				getPaddingBottom());
	}
	
	/**
	 * 获取多个参数的最小值
	 */
	private int min(int... params)
	{
		int min = params[0];

		for (int param : params)
		{
			if (param < min)
				min = param;
		}
		return min;
	}

	
	/**
	 * 设置ImageView(Item)的宽高等属性
	 */
	private void initItem()
	{
		
		mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))
				/ mColumn;
		mGamePintuItems = new ImageView[mColumn * mColumn];
		// 生 成我们的Item，设置Rule
		for (int i = 0; i < mGamePintuItems.length; i++)
		{
			ImageView item = new ImageView(getContext());
			
			item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
			
			mGamePintuItems[i] = item;
			item.setId(i + 1);

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					mItemWidth, mItemWidth);

			// 设置Item间横向间隙，通过rightMargin
			// 不是最后一列
			if ((i + 1) % mColumn != 0)
			{
				lp.rightMargin = mMargin;
			}
			// 不是第一列
			if (i % mColumn != 0)
			{
				lp.addRule(RelativeLayout.RIGHT_OF,
						mGamePintuItems[i - 1].getId());
			}
			// 如果不是第一行 , 设置topMargin和rule
			if ((i + 1) > mColumn)
			{
				lp.topMargin = mMargin;
				lp.addRule(RelativeLayout.BELOW,
						mGamePintuItems[i - mColumn].getId());
			}
			addView(item, lp);
		}

	}

}
