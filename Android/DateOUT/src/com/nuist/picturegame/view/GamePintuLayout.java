package com.nuist.picturegame.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nuist.picturegame.util.ImagePiece;
import com.nuist.picturegame.util.ImageSplitterUtil;

import com.nuist.dateout.R;

public class GamePintuLayout extends RelativeLayout implements OnClickListener
{


	private int mColumn;
	/**
	 * 容器的内边距
	 */
	private int mPadding;
	/**
	 * 每张小图之间的距离（横、纵） dp
	 */
	private int mMargin = 1;
	private int mItemWidth;

	private ImageView[] mGamePintuItems;


	public int getSuccess_time() {
		return success_time;
	}
	public int getSuccess_step() {
		return success_step;
	}
	public int getSuccess_number() {
		return success_number;
	}

	/**完成游戏所剩的时间*/
    private int success_time = 10;
    /**完成游戏的步数*/
    private int success_step=0;
    /**完成游戏所有的次数*/
    private int success_number=1;
    
	/**
	 * 游戏的图片
	 */
	private Bitmap mBitmap;

	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}

	private List<ImagePiece> mItemBitmaps;

	private boolean once;

	/**
	 * 游戏面板的宽度
	 */
	private int mWidth;

	private boolean isGameSuccess;

	public interface GamePintuListener
	{
		void gameSuccess();

		void timechanged(int currentTime);

		void gameover();
	}

	public GamePintuListener mListener;

	/**
	 * 设置接口回调
	 * 
	 * @param mListener
	 */
	public void setOnGamePintuListener(GamePintuListener mListener)
	{
		this.mListener = mListener;
	}

	private static final int TIME_CHANGED = 0x110;
	private static final int GAME_SUCCESS = 0x111;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case TIME_CHANGED:
				if (isGameSuccess|| isPause)
					return;
				if (mListener != null)
				{
					mListener.timechanged(mTime);
				}
				if (mTime == 0)
				{
//					isGameOver = true;
					mListener.gameover();
					return;
				}
				mTime--;
				mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);

				break;
			case GAME_SUCCESS:
				if (mListener != null)
				{
					mListener.gameSuccess();
					
				} else
				{
					gameSuccess();
				}
				break;

			}
		};
	};
	private boolean isTimeEnabled = false;
	private int mTime;

	/**获取当前剩余时间*/
	public int getmTime(){
		return mTime;
	}
	private void checkTimeEnable()
	{
		if (isTimeEnabled)
		{
			// 根据当前等级设置时间
			mTime = settingTime;
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
	}

	public void countTimeBaseLevel(int time)
	{
		settingTime = time;
		mTime = time;
	}
	
	public void setCurrentColum(int colum)
	{
		mColumn = colum;
	}
	///????????????????????????????????????????????????????????
	public void restart(Boolean tag)
	{
		
		success_number++;//玩游戏次数+1
		
		this.removeAllViews();
		mAnimLayout = null;
		isGameSuccess = false;
		mHandler.removeMessages(TIME_CHANGED);
		mTime = settingTime;
		if (tag) {
			checkTimeEnable();
		}
		initBitmap();
		initItem();
	}
	
	public void GameOverThenRestart(){
		restart(false);
//		isGameOver = false;
	}
	
	private boolean isPause ; 
	
	public void pause()
	{
		isPause = true ; 
		mHandler.removeMessages(TIME_CHANGED);
	}
	
	public void resume()
	{
		if(isPause)
		{
			isPause = false ;
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
	}

	public void gameSuccess()
	{
//		this.removeAllViews();
//		mAnimLayout = null;
//		mColumn++;
//		isGameSuccess = false;
//		checkTimeEnable();
//		initBitmap();
//		initItem();
	}
	/**
	 * 设置是否开启时间
	 * @param isTimeEnabled
	 */
	public void setTimeEnabled(boolean isTimeEnabled)
	{
		this.isTimeEnabled = isTimeEnabled;
	}

	public GamePintuLayout(Context context)
	{
		this(context, null);
	}

	public GamePintuLayout(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public GamePintuLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				3, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
				getPaddingBottom());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 取宽和高中的小值
		mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

		if (!once)
		{
			// 进行切图，以及排序
			initBitmap();
			// 设置ImageView(Item)的宽高等属性
			initItem();
			// 判断是否开启时间
			checkTimeEnable();

			once = true;
		}

		setMeasuredDimension(mWidth, mWidth);

	}

	
	/**
	 * 进行切图，以及排序
	 */
	private void initBitmap()
	{
		if (mBitmap == null)
		{
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.i3);
		}
		mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);

		// 使用sort完成我们的乱序
		Collections.sort(mItemBitmaps, new Comparator<ImagePiece>()
		{
			@Override
			public int compare(ImagePiece a, ImagePiece b)
			{
				return Math.random() > 0.5 ? 1 : -1;
			}
		});

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
		// 生成我们的Item，设置Rule
		for (int i = 0; i < mGamePintuItems.length; i++)
		{
			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			item.setImageBitmap(mItemBitmaps.get(i).getBitmap());

			mGamePintuItems[i] = item;
			item.setId(i + 1);

			// 在Item的tag中存储了index
			item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

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
	
	private ImageView mFirst;
	private ImageView mSecond;

	@Override
	public void onClick(View v)
	{
		if (isAniming)
			return;

		// 两次点击同一个Item
		if (mFirst == v)
		{
			mFirst.setColorFilter(null);
			mFirst = null;
			return;
		}
		if (mFirst == null)
		{
			if (mAnimLayout!=null) {
				mAnimLayout.removeAllViews();
			}
			mFirst = (ImageView) v;
			mFirst.setColorFilter(Color.parseColor("#55FF0000"));
		} else
		{
			mSecond = (ImageView) v;
			// 交换我们的Item
			exchangeView();
		}

	}

	/**
	 * 动画层
	 */
	private RelativeLayout mAnimLayout;
	private boolean isAniming;
	private int settingTime;

	/**
	 * 交换我们的Item
	 */
	private void exchangeView()
	{
		mFirst.setColorFilter(null);
		// 构造我们的动画层
		setUpAnimLayout();

		ImageView first = new ImageView(getContext());
		final Bitmap firstBitmap = mItemBitmaps.get(
				getImageIdByTag((String) mFirst.getTag())).getBitmap();
		first.setImageBitmap(firstBitmap);
		LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
		lp.leftMargin = mFirst.getLeft() - mPadding;
		lp.topMargin = mFirst.getTop() - mPadding;
		first.setLayoutParams(lp);
		mAnimLayout.addView(first);

		ImageView second = new ImageView(getContext());
		final Bitmap secondBitmap = mItemBitmaps.get(
				getImageIdByTag((String) mSecond.getTag())).getBitmap();
		second.setImageBitmap(secondBitmap);
		LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
		lp2.leftMargin = mSecond.getLeft() - mPadding;
		lp2.topMargin = mSecond.getTop() - mPadding;
		second.setLayoutParams(lp2);
		mAnimLayout.addView(second);

		// 设置动画
		TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft()
				- mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
		anim.setDuration(300);
		anim.setFillAfter(true);
		first.startAnimation(anim);

		TranslateAnimation animSecond = new TranslateAnimation(0,
				-mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop()
						+ mFirst.getTop());
		animSecond.setDuration(300);
		animSecond.setFillAfter(true);
		second.startAnimation(animSecond);

		// 监听动画
		anim.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
				mFirst.setVisibility(View.INVISIBLE);
				mSecond.setVisibility(View.INVISIBLE);

				isAniming = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				success_step++;//玩游戏步数+1

				String firstTag = (String) mFirst.getTag();
				String secondTag = (String) mSecond.getTag();

				mFirst.setImageBitmap(secondBitmap);
				mSecond.setImageBitmap(firstBitmap);

				mFirst.setTag(secondTag);
				mSecond.setTag(firstTag);

				mFirst.setVisibility(View.VISIBLE);
				mSecond.setVisibility(View.VISIBLE);

				mFirst = mSecond = null;
				// 判断用户游戏是否成功
				checkSuccess();
				isAniming = false;
			}
		});

	}

	/**
	 * 判断用户游戏是否成功
	 */
	private void checkSuccess()
	{
		boolean isSuccess = true;

		for (int i = 0; i < mGamePintuItems.length; i++)
		{
			ImageView imageView = mGamePintuItems[i];
			if (getImageIndexByTag((String) imageView.getTag()) != i)
			{
				isSuccess = false;
			}
		}

		if (isSuccess)
		{
			success_time = mTime;//把游戏剩余时间赋给success_time
			isGameSuccess = true;
			mHandler.removeMessages(TIME_CHANGED);
			mHandler.sendEmptyMessage(GAME_SUCCESS);
		}

	}

	/**
	 * 根据tag获取Id
	 * 
	 * @param tag
	 * @return
	 */
	public int getImageIdByTag(String tag)
	{
		String[] split = tag.split("_");
		return Integer.parseInt(split[0]);
	}

	public int getImageIndexByTag(String tag)
	{
		String[] split = tag.split("_");
		return Integer.parseInt(split[1]);
	}

	/**
	 * 构造我们的动画层
	 */
	private void setUpAnimLayout()
	{
		if (mAnimLayout == null)
		{
			mAnimLayout = new RelativeLayout(getContext());
			addView(mAnimLayout);
		}
	}

}
