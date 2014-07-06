package net.fengg.app.deafmutism.util.refreshmore.mylistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.fengg.app.deafmutism.R;

/**
 * 加载更多窗口
 * 
 * @author 蔡有飞 E-mail: caiyoufei@looip.cn
 * @version 创建时间：2013-12-12 下午4:06:39
 * 
 */
public class MyListViewFooter extends LinearLayout {
	// 加载更多的布局
	private LinearLayout cLinearLayoutFooter;
	// 加载进度显示
	private ProgressBar cProgressBar;
	// 加载更多的提示
	private TextView cTextViewLoadMore;
	// 默认状态
	public final static int STATE_NORMAL = 0;
	// 释放刷新
	public final static int STATE_READY = 1;
	// 加载中
	public final static int STATE_LOADING = 2;

	public MyListViewFooter(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @time 注释时间：2013-12-12 下午4:06:33
	 * @param context
	 */
	private void init(Context context) {
		// 加载更多的布局
		cLinearLayoutFooter = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.refresh_footer, null);
		// 添加view
		addView(cLinearLayoutFooter);
		// 设置窗口大小
		cLinearLayoutFooter.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// 正在加载进度条
		cProgressBar = (ProgressBar) findViewById(R.id.mylistview_footer_progressbar);
		// 加载更多提示语
		cTextViewLoadMore = (TextView) findViewById(R.id.mylistview_footer_hint);
	}

	/**
	 * 显示加载更多窗口
	 * 
	 * @time 注释时间：2013-12-12 下午5:25:45
	 */
	public void showLoadMore() {
		LinearLayout.LayoutParams lp = (LayoutParams) cLinearLayoutFooter
				.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		cLinearLayoutFooter.setLayoutParams(lp);
	}

	/**
	 * 隐藏加载更多窗口
	 * 
	 * @time 注释时间：2013-12-12 下午5:26:18
	 */
	public void hideLoadMore() {
		LinearLayout.LayoutParams lp = (LayoutParams) cLinearLayoutFooter
				.getLayoutParams();
		lp.height = 0;
		cLinearLayoutFooter.setLayoutParams(lp);
	}

	/**
	 * 加载中
	 * 
	 * @time 注释时间：2013-12-12 下午5:30:03
	 */
	public void loading() {
		cProgressBar.setVisibility(View.VISIBLE);
		cTextViewLoadMore.setVisibility(View.GONE);
	}

	/**
	 * 正常状态
	 * 
	 * @time 注释时间：2013-12-12 下午5:31:22
	 */
	public void nomal() {
		cProgressBar.setVisibility(View.GONE);
		cTextViewLoadMore.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置底部间距
	 * 
	 * @time 注释时间：2013-12-12 下午5:36:20
	 */
	public void setBottomMargin(int height) {
		if (height < 0) {
			return;
		}
		LinearLayout.LayoutParams lp = (LayoutParams) cLinearLayoutFooter
				.getLayoutParams();
		lp.bottomMargin = height;
		cLinearLayoutFooter.setLayoutParams(lp);
	}

	/**
	 * 获取底部间距
	 * 
	 * @time 注释时间：2013-12-12 下午5:37:27
	 */
	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LayoutParams) cLinearLayoutFooter
				.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * 设置加载的状态
	 * 
	 * @time 注释时间：2013-12-12 下午5:41:49
	 * @param state
	 */
	public void setState(int state) {
		cProgressBar.setVisibility(View.GONE);
		cTextViewLoadMore.setVisibility(View.GONE);
		if (state == STATE_READY) {
			cTextViewLoadMore.setVisibility(View.VISIBLE);
			cTextViewLoadMore.setText(R.string.add_more);
		} else if (state == STATE_LOADING) {
			cProgressBar.setVisibility(View.VISIBLE);
		} else {
			cTextViewLoadMore.setVisibility(View.VISIBLE);
			cTextViewLoadMore.setText(R.string.add);
		}
	}
}
