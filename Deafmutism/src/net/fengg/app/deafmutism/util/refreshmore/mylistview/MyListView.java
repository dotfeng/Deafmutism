package net.fengg.app.deafmutism.util.refreshmore.mylistview;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import net.fengg.app.deafmutism.R;

/**
 * 自定义ListView
 * 
 * @author 蔡有飞 E-mail: caiyoufei@looip.cn
 * @version 创建时间：2013-12-12 上午11:30:28
 * 
 */
public class MyListView extends ListView implements OnScrollListener {
	// 滑动删除是否是直线
	private boolean cIsDelete = true;
	// 删除某一行的接口
	private Delete cDelete;
	// 判断是否一直在某一行
	private int startItem = -1;
	// 判断是是否是左移
	private int startX = -1;
	// 下拉刷新窗口
	private MyListViewHeader cMyListViewHeader;
	// 加载更多窗口
	public MyListViewFooter cMyListViewFooter;
	// 下拉刷新布局
	private RelativeLayout cRelativeLayoutHeader;
	// 下拉刷新窗口的高度
	private int cHeaderViewHeight;
	// 确保添加“查看更多”只执行一次
	private boolean cIsFooterReady = false;
	// 是否可用进行下拉
	private boolean cEnablePullRefresh = true;
	// 是否可以进行上拉
	private boolean cEnablePullLoad = true;
	// 是否正在进行刷新
	private boolean cIsPullRefreshing = false;
	// 是否正在加载更多
	private boolean cIsPullLoading = false;
	// 外部调用的接口
	private MyListViewListener cMyListViewListener;
	// 加载更多的id
	private int cLoadID;
	// 下拉刷新窗口的高度
	private int cMyListViewHeaderHeight;
	// 用户自定义的Scroller(滑动，自动复位等)
	private Scroller cScroller;
	// 用户自定义的OnScrollListener
	private OnScrollListener cOnScrollListener;
	// 列表当前位置
	private int cScrollerPosition;
	// 列表头部
	private final int LISTVIEW_POSITION_HEADER = 0;
	// 列表底部
	private final int LISTVIEW_POSITION_FOOTER = 1;
	// 滑动条移动时间
	private final int SCROLLER_DURATION_TIME = 400;
	// 当前列表的总数
	private int cTotalItemCount;
	// "加载更多"距离底部的距离标准
	private final static int LOADMORE_BOTTOM_DISTANCE = 50;
	// 点击屏幕获取相对屏幕左上角的Y值
	private float cLastY = -1;
	// 下拉或者上拉，屏幕跟着上下移动的相对比例
	private final static float MOVT_SCALE = 1.8f;
	// 刷新完成
	public final int REFRESH_COMPLETE = 0x00;
	// 加载完成
	public final int DOWNLOAD_COMPLETE = 0x01;
	// 加载结果处理机制
	public Handler cHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_COMPLETE:
				// 停止刷新
				stopRefresh();
				break;
			case DOWNLOAD_COMPLETE:
				// 停止加载更多
				stopLoadMore();
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 构造方法
	 * 
	 * @version 更新时间：2013-12-12 上午11:32:07
	 * @param context
	 */
	public MyListView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @version 更新时间：2013-12-12 上午11:32:17
	 * @param context
	 * @param attrs
	 */
	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @version 更新时间：2013-12-12 上午11:32:23
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @time 注释时间：2013-12-12 上午11:35:24
	 * @param context
	 */
	private void init(Context context) {
		super.setOnScrollListener(this);
		// 自定义滑动，方便执行其他操作
		cScroller = new Scroller(context, new DecelerateInterpolator());
		cMyListViewHeader = new MyListViewHeader(context);
		cMyListViewFooter = new MyListViewFooter(context);
		// 下拉刷新的布局
		cRelativeLayoutHeader = (RelativeLayout) cMyListViewHeader
				.findViewById(R.id.mylistview_header_layout);
		// 添加刷新窗口到顶部
		addHeaderView(cMyListViewHeader);
		// http://blog.sina.com.cn/s/blog_4b93170a0102e2n3.html
		// interface ViewTreeObserver.OnGlobalLayoutListener
		// 当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类
		cMyListViewHeader.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						cMyListViewHeaderHeight = cRelativeLayoutHeader
								.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});

	}

	/**
	 * 填充适配器,加载"加载更多"到listview的底部
	 */
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		// 只执行一次添加"加载更多"到lisview的底部
		if (!cIsFooterReady) {
			cIsFooterReady = true;
			if (cMyListViewFooter != null) {
				addFooterView(cMyListViewFooter);
			}
		}
	}

	/**
	 * 设置是否可用进行下拉
	 * 
	 * @time 注释时间：2013-12-12 下午6:19:42
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		cEnablePullRefresh = enable;
		if (cEnablePullRefresh) {
			cMyListViewHeader.setVisibility(View.VISIBLE);
		} else {
			cMyListViewHeader.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置是否可用进行上拉
	 * 
	 * @time 注释时间：2013-12-12 下午6:22:20
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		cEnablePullLoad = enable;
		if (cEnablePullLoad) {
			cMyListViewFooter.showLoadMore();
			// 设置加载状态false，即未执行加载
			cIsPullLoading = false;
			// 设置为"加载更多"
			cMyListViewFooter.setState(cMyListViewFooter.STATE_NORMAL);
			cMyListViewFooter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 执行加载更多
					startLoadMore();
				}
			});

		} else {
			cMyListViewFooter.hideLoadMore();
			cMyListViewFooter.setOnClickListener(null);
		}
	}

	/**
	 * 开始执行加载更多
	 * 
	 * @time 注释时间：2013-12-13 上午10:16:10
	 */
	public void startLoadMore() {
		// 设置正在加载的状态
		cIsPullLoading = true;
		// 设置正在加载进度条显示
		cMyListViewFooter.setState(cMyListViewFooter.STATE_LOADING);
		if (cMyListViewListener != null) {
			// 执行加载更多
			cMyListViewListener.onLoadMore(cLoadID);
		}
		// "加载更多"设为不可操作
		cMyListViewFooter.setEnabled(false);

	}

	/**
	 * 停止加载更多
	 * 
	 * @time 注释时间：2013-12-13 上午10:30:36
	 */
	public void stopLoadMore() {
		// 如果还处于正在加载状态，则设置为可加载状态
		if (cIsPullLoading) {
			cIsPullLoading = false;
			// 设置窗口显示为：加载更多
			cMyListViewFooter.setState(cMyListViewFooter.STATE_NORMAL);
		}
		// 设置窗口为可操作
		cMyListViewFooter.setEnabled(true);
	}

	/**
	 * 停止刷新
	 * 
	 * @time 注释时间：2013-12-13 上午10:37:38
	 */
	public void stopRefresh() {
		// 如果正在刷新，设置为等待刷新
		if (cIsPullRefreshing) {
			cIsPullRefreshing = false;
			// 重设刷新窗口高度
			resetMyListViewHeaderHeight();
		}
	}

	/**
	 * 重置刷新窗口高度
	 * 
	 * @time 注释时间：2013-12-13 上午10:40:25
	 */
	private void resetMyListViewHeaderHeight() {
		// 获取当前可见高度
		int cCurrentHeight = cMyListViewHeader.getVisiableHeight();
		// 如果不可见，则不进行其他操作
		if (cCurrentHeight <= 0) {
			return;
		}
		// 如果正在刷新而且当前高度小于刷新窗口的高度
		if (cIsPullRefreshing && cCurrentHeight <= cMyListViewHeaderHeight) {
			return;
		}
		// 定义一个高度,用于处理释放后的刷新窗口高度
		int cFinalHeight = 0;
		if (cIsPullRefreshing && cCurrentHeight > cMyListViewHeaderHeight) {
			// 取得刷新窗口的高度
			cFinalHeight = cMyListViewHeaderHeight;
		}
		// 设置标记为列表顶部
		cScrollerPosition = LISTVIEW_POSITION_HEADER;
		// 滑动条自动从当前可见的高度移动到刷新窗口的默认高度,移动时间为SCROLLER_DURATION_TIME
		cScroller.startScroll(0, cCurrentHeight, 0, cFinalHeight
				- cCurrentHeight, SCROLLER_DURATION_TIME);
		// 刷新一下view
		invalidate();
	}

	/**
	 * 重置加载更多窗口的高度
	 * 
	 * @time 注释时间：2013-12-13 下午1:21:52
	 */
	private void resetMyListViewFooterHeight() {
		// 获取当前"加载更多"到底部的距离
		int bottomMargin = cMyListViewFooter.getBottomMargin();
		// 没有处于最底部
		if (bottomMargin > 0) {
			// 设置为底部
			cScrollerPosition = LISTVIEW_POSITION_FOOTER;
			// 滑动条自动从当前可见的高度移动到"加载更多"的默认高度,移动时间为SCROLLER_DURATION_TIME
			cScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLLER_DURATION_TIME);
			// 刷新view
			invalidate();
		}
	}

	/**
	 * 设置刷新时间
	 * 
	 * @time 注释时间：2013-12-13 上午11:08:56
	 */
	public void setRefreshTime() {
		cMyListViewHeader.cTextViewTime.setText(getSystemTime());
	}

	/**
	 * 获取当前系统时间
	 * 
	 * @time 注释时间：2013-12-13 上午11:09:35
	 * @return
	 */
	private String getSystemTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd  HH:mm:ss");
		return dateFormat.format(new Date(System.currentTimeMillis()));

	}

	/**
	 * 更新刷新窗口的高度
	 * 
	 * @time 注释时间：2013-12-13 上午11:52:14
	 */
	private void updateMyListViewHeaderHeight(float addHeight) {
		if (cMyListViewHeader.getVisiableHeight() <= cMyListViewHeaderHeight * 1.5) {
			// 设置下拉窗口的高度为已经显示的高度与传入的高度之和
			cMyListViewHeader.setVisiableHeight((int) addHeight
					+ cMyListViewHeader.getVisiableHeight());
		}
		// 如果是可以下拉状态以及没有处于刷新状态
		if (cEnablePullRefresh && !cIsPullRefreshing) {
			// 如果下拉的高度大于刷新窗口的高度则设置"释放刷新"
			if (cMyListViewHeader.getVisiableHeight() > cMyListViewHeaderHeight) {
				cMyListViewHeader.setState(cMyListViewHeader.STATE_READY);
			}
			// 否则进入到"下拉刷新"
			else {
				cMyListViewHeader.setState(cMyListViewHeader.STATE_NORMAL);
			}
			// 每次下拉之后都恢复到列表的顶部
			setSelection(0);
		}
	}

	/**
	 * 更新加载窗口的高度
	 * 
	 * @time 注释时间：2013-12-13 下午12:01:55
	 */
	private void updateMyListViewFooterHeight(float addHeight) {
		// 设置新的高度为之前的高度与传入高度之和
		int newHeight = cMyListViewFooter.getBottomMargin() + (int) addHeight;
		// 如果可以上拉并且不是处于加载状态
		if (cEnablePullLoad && !cIsPullLoading) {
			// 如果达到加载更多的刷新距离
			if (newHeight >= LOADMORE_BOTTOM_DISTANCE) {
				// 松开载入更多
				cMyListViewFooter.setState(cMyListViewFooter.STATE_READY);
			} else {
				// 查看那多
				cMyListViewFooter.setState(cMyListViewFooter.STATE_NORMAL);
			}
		}
		// 设置"加载更多"窗口的高度
		cMyListViewFooter.setBottomMargin(newHeight);
	}

	/**
	 * 触摸监听事件处理
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (cLastY == -1) {
			// getRawY()是表示相对于屏幕左上角的Y坐标值
			cLastY = ev.getRawY();
		}
		switch (ev.getAction()) {
		// 按下屏幕获取点击点与左上角的Y距离
		case MotionEvent.ACTION_DOWN:
			// 设置为可删除
			cIsDelete = true;
			// 点击屏幕获得点击所在的行
			startItem = pointToPosition((int) ev.getX(), (int) ev.getY());
			// 获取点击时的X坐标
			startX = (int) ev.getX();
			cLastY = ev.getRawY();
			break;
		// 移动的时候做刷新的处理
		case MotionEvent.ACTION_MOVE:
			// 如果在移动过程中移出了点击的那行，则变为不可删除
			if (startItem != pointToPosition((int) ev.getX(), (int) ev.getY())) {
				cIsDelete = false;
			}
			// 定义一个变量，记录按下的点与移动之后的Y轴距离，做上移还是下移的判断
			final float addY = ev.getRawY() - cLastY;
			// 判断之后重新记录上一次移动后的Y位置
			cLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0// 如果处于列表第一个位置
					&& (cMyListViewHeader.getVisiableHeight() > 0// 如果下拉刷新控件高度大于零（即：可见状态）
					|| addY > 0)) // 下拉的距离大于零（即：下拉）
			{
				// 设置下拉窗口显示的相对高度变化
				updateMyListViewHeaderHeight(addY / MOVT_SCALE);
			} else if (getLastVisiblePosition() == cTotalItemCount - 1// 如果处于列表的最后一个位置
					&& (cMyListViewFooter.getBottomMargin() > 0 // 如果"加载更多"控件距离底部的距离大于零（即：可见状态）
					|| addY < 0))// 下拉的距离小于零（即：上拉）
			{
				// 设置下拉窗口显示的相对高度变化
				updateMyListViewFooterHeight(-addY / MOVT_SCALE);
			}
			break;
		default:
			// 满足条件，执行删除
			if (cIsDelete && startX - ev.getX() > 180) {
				int position = pointToPosition((int) ev.getX(), (int) ev.getY());
				if (position >= 1 && cDelete != null) {
					cDelete.deleteRow(position - 1);
				}
			}
			// 移动完成之后恢复默认位置
			cLastY = -1;
			// 如果当前处于列表的最顶部
			if (getFirstVisiblePosition() == 0) {
				// 调用刷新
				if (cEnablePullRefresh// 可下拉状态
						&& cMyListViewHeader.getVisiableHeight() > cMyListViewHeaderHeight// 如果下拉的高度大于刷新窗口的高度
				) {
					// 设置正在刷新
					cIsPullRefreshing = true;
					// 刷新窗口进入刷新状态
					cMyListViewHeader
							.setState(cMyListViewHeader.STATE_REFRESHING);
					// 刷新、加载接口不为空
					if (cMyListViewListener != null) {
						// 根据cLoadID刷新
						cMyListViewListener.onRefresh(cLoadID);
					}
				}
				// 重置刷新窗口高度
				resetMyListViewHeaderHeight();
			}
			// 如果处于列表的最底部
			else if (getLastVisiblePosition() == cTotalItemCount - 1) {
				// 调用加载更多
				if (cEnablePullLoad// 可以上拉
						&& cMyListViewFooter.getBottomMargin() > LOADMORE_BOTTOM_DISTANCE// 达到加载更多的默认距离
				) {
					// 如果加载更多可操作
					if (cMyListViewFooter.isEnabled()) {
						// 执行加载
						startLoadMore();
					}
				}
				// 重置加载窗口高度
				resetMyListViewFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 调用接口并实例化ListView.OnScrollListener
	 * 
	 * @time 注释时间：2013-12-13 上午11:44:37
	 */
	private void invokeOnScrolling() {
		// 如果cOnScrollListener是OnMyListViewScrollListener的一个实例
		if (cOnScrollListener instanceof OnMyListViewScrollListener) {
			OnMyListViewScrollListener lsl = (OnMyListViewScrollListener) cOnScrollListener;
			lsl.onMyListViewScrolling(this);
		}

	}

	/**
	 * 新建一个监听ListView.OnScrollListener，调用OnMyListViewScrollListener做处理
	 * 
	 * @author 蔡有飞 E-mail: caiyoufei@looip.cn
	 * @version 创建时间：2013-12-13 上午11:29:23
	 * 
	 */
	public interface OnMyListViewScrollListener extends OnScrollListener {
		public void onMyListViewScrolling(View view);
	}

	/**
	 * 外部接口
	 * 
	 * @author 蔡有飞 E-mail: caiyoufei@looip.cn
	 * @version 创建时间：2013-12-13 上午10:21:00
	 * 
	 */
	public interface MyListViewListener {
		public void onRefresh(int id);

		public void onLoadMore(int id);
	}

	/**
	 * 外部设置监听和加载的id
	 * 
	 * @time 注释时间：2013-12-13 下午2:27:19
	 */
	public void setMyListViewListenerAndDownloadID(MyListViewListener listener,
			int loadID) {
		cMyListViewListener = listener;
		cLoadID = loadID;
	}

	/**
	 * 设置实现接口的效果: cOnScrollListener设置系统滑动效果
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 该列表的总数
		cTotalItemCount = totalItemCount;
		if (cOnScrollListener != null) {
			// 设置系统滑动效果
			cOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}

	}

	/**
	 * 设置实现接口的效果: cOnScrollListener设置系统滑动效果
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (cOnScrollListener != null) {
			// 当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；
			cOnScrollListener.onScrollStateChanged(view, scrollState);
		}

	}

	/**
	 * 初始化mScrollListener
	 */
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		super.setOnScrollListener(l);
		cOnScrollListener = l;
	}

	/**
	 * computeScroll：主要功能是计算拖动的位移量、更新背景、设置要显示的屏幕
	 * 调用startScroll()是不会有滚动效果的，只有在computeScroll()获取滚动情况，做出滚动的响应
	 * computeScroll在父控件执行drawChild时，会调用这个方法
	 */
	@Override
	public void computeScroll() {
		// http://cache.baiducontent.com/c?m=9f65cb4a8c8507ed4fece76310498a36420ad7743ca0d7162e92ce5f93130a1c187bb5ec66794558c4c50a6604a84356e1e736056d457fa3de95c81cd2eacf776dcf6163671df600439344f1955124b17dc40efeae69a6edb6&p=c36dc54ad5c340fe1dbe9b7c1e5dcd&newp=c2708316d9c10bff57ee947f4b4297231610db2151d1d21635&user=baidu&fm=sc&query=computeScrollOffset&qid=&p1=2
		// 其中Scroller.computeScrollOffset()方法是判断scroller的移动动画是否完成，当你调用
		// startScroll()方法的时候这个方法返回的值一直都为true，如果采用其它方式移动视图比如：scrollTo()或
		// scrollBy时那么这个方法返回false。
		super.computeScroll();
		if (cScroller.computeScrollOffset()) {
			// 如果处于列表顶部
			if (cScrollerPosition == LISTVIEW_POSITION_HEADER) {
				// 设置下拉窗口显示的高度
				cMyListViewHeader.setVisiableHeight(cScroller.getCurrY());
			} else if (cScrollerPosition == LISTVIEW_POSITION_FOOTER) {
				// 设置加载窗口的显示高度
				cMyListViewFooter.setBottomMargin(cScroller.getCurrY());
			}
			// Android中实现view的更新有两组方法，一组是invalidate，另一组是postInvalidate，其中前者是在UI线程自身中使用，而后者在非UI线程中使用。
			postInvalidate();
			// ListView.OnScrollListener抽象类实例化
			invokeOnScrolling();
		}
	}

	/**
	 * 从外部设置监听
	 * 
	 * @time 注释时间：2013-12-16 下午3:42:52
	 * @param delete
	 */
	public void setDeleteListene(Delete delete) {
		this.cDelete = delete;
	};

	/**
	 * 外部重写删除
	 * 
	 * @author 蔡有飞 E-mail: caiyoufei@looip.cn
	 * @version 创建时间：2013-12-16 下午3:38:42
	 * 
	 */
	public interface Delete {
		public void deleteRow(int position);
	}
}
