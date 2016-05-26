package com.aller.wisdombj.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aller.wisdombj.R;

import android.R.anim;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @ClassName: RefreshListView
 * @Description: 下拉刷新的ListView
 * @author Aller Aller_Dong@163.com
 * @date 2016年5月11日 下午9:23:18
 *
 */
public class RefreshListView extends ListView implements OnScrollListener,android.widget.AdapterView.OnItemClickListener{

	private static final int STATE_PULL_REFRESH = 0;// 下拉刷新
	private static final int STATE_RELEASE_REFRESH = 1;// 松开刷新
	private static final int STATE_REFRESHING = 2;// 正在刷新

	private int startY = -1;// 滑动起点的y坐标

	private int mCurrrentState = STATE_PULL_REFRESH;// 当前状态

	private View refresh_header;
	private TextView tv_title;
	private TextView tv_time;
	private ImageView iv_arr;
	private ProgressBar pb_progress;
	private RotateAnimation animUp;
	private RotateAnimation animDown;
	private int mHeaderViewHeight;

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context) {
		super(context);
		initHeaderView();
		initFooterView();
	}

	/**
     *  初始化头布局
	 *
	 */
	private void initHeaderView() {
		refresh_header = View.inflate(getContext(), R.layout.refresh_header, null);
		this.addHeaderView(refresh_header);

		tv_title = (TextView) refresh_header.findViewById(R.id.tv_title);
		tv_time = (TextView) refresh_header.findViewById(R.id.tv_time);
		iv_arr = (ImageView) refresh_header.findViewById(R.id.iv_arr);
		pb_progress = (ProgressBar) refresh_header.findViewById(R.id.pb_progress);

		refresh_header.measure(0, 0);
		mHeaderViewHeight = refresh_header.getMeasuredHeight();
		refresh_header.setPadding(0, -mHeaderViewHeight, 0, 0);// 隐藏头布局

		initArrowAnim();
		tv_time.setText("最后刷新时间:" + getCurrentTime());
	}
	/**
	* 初始化脚布局
	*
	 */
	public void initFooterView(){
		mFooterView = View.inflate(getContext(), R.layout.refresh_listview_footer, null);
		this.addFooterView(mFooterView);
		
		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		
		this.setOnScrollListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			
			if(startY == -1){//判断下拉距离是否有效
				startY = (int) ev.getRawY();
			}
			
			if(mCurrrentState == STATE_REFRESHING){//正在刷新时不做处理
				break ;
			}
			
			int endY = (int) ev.getRawY();
			int dy = endY - startY;// 移动的距离
			if (dy > 0 && getFirstVisiblePosition() == 0) {
				int padding = dy - mHeaderViewHeight;
				refresh_header.setPadding(0, padding, 0, 0);// 设置下拉刷新显示的高度
				if (padding > 0 && mCurrrentState != STATE_RELEASE_REFRESH) {
					mCurrrentState = STATE_RELEASE_REFRESH;
					refreshState();
				} else if (padding < 0 && mCurrrentState != STATE_PULL_REFRESH) {
					mCurrrentState = STATE_PULL_REFRESH;
					refreshState();
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			startY = -1 ;
			if (mCurrrentState == STATE_RELEASE_REFRESH) {
				mCurrrentState = STATE_REFRESHING ;
				refresh_header.setPadding(0, 0, 0, 0);//显示下拉头部
				refreshState();
			}else if (mCurrrentState == STATE_PULL_REFRESH) {
				refresh_header.setPadding(0, -mHeaderViewHeight, 0, 0);//隐藏
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void refreshState() {
		switch (mCurrrentState) {
		case STATE_PULL_REFRESH:
			tv_title.setText("下拉刷新");
			iv_arr.setVisibility(View.VISIBLE);
			pb_progress.setVisibility(View.INVISIBLE);
			iv_arr.startAnimation(animDown);
			break;
		case STATE_RELEASE_REFRESH:
			tv_title.setText("松开刷新");
			iv_arr.setVisibility(View.VISIBLE);
			pb_progress.setVisibility(View.INVISIBLE);
			iv_arr.startAnimation(animUp);
			break;
		case STATE_REFRESHING:
			tv_title.setText("正在刷新...");
			iv_arr.clearAnimation();// 必须先清除动画,才能隐藏
			iv_arr.setVisibility(View.INVISIBLE);
			pb_progress.setVisibility(View.VISIBLE);
			
			if (mListener != null) {
				mListener.OnRefresh();
			}
			
			break;
		default:
			break;
		}
	}

	// 初始化箭头动画
	private void initArrowAnim() {
		// 向上动画
		animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animUp.setDuration(200);
		animUp.setFillAfter(true);
		// 向下动画
		animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animDown.setDuration(200);
		animDown.setFillAfter(true);
	}
	
	OnRefreshListener mListener ;
	public void setOnRefreshListener(OnRefreshListener listener){
		this.mListener = listener ;
	}
	public interface OnRefreshListener{
		public void OnRefresh();//刷新
		public void OnLoadMore() ;//加载更多
	}
	
	//刷新完成后，收起下拉刷新
	private boolean isLoadingMore;
	private View mFooterView;
	private int mFooterViewHeight;
	public void OnRefreshComplete(boolean success){
		if(isLoadingMore){//加载更多
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
			isLoadingMore =false ;
		}else{
			mCurrrentState = STATE_PULL_REFRESH ;
			tv_title.setText("下拉刷新");
			iv_arr.setVisibility(View.VISIBLE);
			pb_progress.setVisibility(View.INVISIBLE);
			refresh_header.setPadding(0, -mHeaderViewHeight, 0, 0);//隐藏
			
			if(success){
				tv_time.setText("最后刷新时间：" + getCurrentTime());
			}
		}
	}

	/**
	 *  获取系统当前时间
	*
	 */
	public String getCurrentTime(){
		SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
		return mFormat.format(new Date()) ;
	}

	OnItemClickListener mItemClickListener;
	@Override
	public void setOnItemClickListener(android.widget.AdapterView.OnItemClickListener listener) {
		super.setOnItemClickListener(this);
		mItemClickListener = listener ;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(mItemClickListener != null){
			mItemClickListener.onItemClick(parent, view,
					position - getHeaderViewsCount(), id);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING){
			if(getLastVisiblePosition() == getCount() -1 && !isLoadingMore){// 滑动到最后
				mFooterView.setPadding(0, 0, 0, 0);//显示
				setSelection(getCount() - 1);// 改变listview显示位置
				
				isLoadingMore = true ;
				
				if (mListener !=null) {
					mListener.OnLoadMore();
					
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
}
