package net.fengg.app.deafmutism.util.refreshmore.mylistview;

import net.fengg.app.deafmutism.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 下拉刷新的头部
 * 
 * @author 蔡有飞 E-mail: caiyoufei@looip.cn
 * @version 创建时间：2013-12-12 上午11:40:46
 * 
 */
public class MyListViewHeader extends LinearLayout {
	// 整个刷新窗口的布局
	private LinearLayout cLinearLayoutHeader;
	// 下拉刷新的箭头
	private ImageView cImageViewJiantou;
	// 正在刷新的ProgressBar
	private ProgressBar cProgressBar;
	// 刷新的提示
	private TextView cTextViewHint;
	// 刷新的时间
	public TextView cTextViewTime;
	// 箭头刷新的动画，由向下旋转180°向上
	private Animation cAnimationToUp;
	// 箭头刷新的动画，由向上旋转180°向下
	private Animation cAnimationToDown;
	// 箭头旋转动画时间
	private final int ANIMATION_TIME = 180;
	// 下拉窗口显示状态
	private int cState;
	// 未达到刷新距离（释放不会进入刷新状态）
	public final static int STATE_NORMAL = 0;
	// 达到刷新距离（释放会进入刷新状态）
	public final static int STATE_READY = 1;
	// 刷新状态
	public final static int STATE_REFRESHING = 2;

	/**
	 * 构造方法
	 * 
	 * @version 更新时间：2013-12-12 下午1:23:45
	 * @param context
	 */
	public MyListViewHeader(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @time 注释时间：2013-12-12 下午1:24:22
	 * @param context
	 */
	private void init(Context context) {
		// 设置默认显示状态
		cState = STATE_NORMAL;
		// 设置下拉窗口的高度和宽度
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		// 下拉窗口的布局
		cLinearLayoutHeader = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.refresh_header, null);
		// 添加下拉窗口
		addView(cLinearLayoutHeader, lp);
		// 设置加载在整个布局的底部
		setGravity(Gravity.BOTTOM);
		// 刷新的箭头
		cImageViewJiantou = (ImageView) findViewById(R.id.mylistview_header_jiantou);
		// 刷新的提示
		cTextViewHint = (TextView) findViewById(R.id.mylistview_header_hint);
		// 刷新的时间
		cTextViewTime = (TextView) findViewById(R.id.mylistview_header_time);
		// 正在下载的圆形进度条
		cProgressBar = (ProgressBar) findViewById(R.id.mylistview_header_progressbar);
		// 设置由下向上的箭头旋转动画
		cAnimationToUp = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// 设置动画时间
		cAnimationToUp.setDuration(ANIMATION_TIME);
		// 使用animation.setFillAfter(true); view不再返回原位
		cAnimationToUp.setFillAfter(true);
		// 设置由上向下的箭头旋转动画
		cAnimationToDown = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// 设置动画时间
		cAnimationToDown.setDuration(ANIMATION_TIME);
		// 使用animation.setFillAfter(true); view不再返回原位
		cAnimationToDown.setFillAfter(true);
	}

	/**
	 * 设置下拉刷新窗口的显示高度
	 * 
	 * @time 注释时间：2013-12-12 下午1:55:51
	 * @param height
	 */
	public void setVisiableHeight(int height) {
		if (height < 0) {
			height = 0;
		}
		// 获取当前下拉刷新窗口的尺寸
		LinearLayout.LayoutParams lp = (LayoutParams) cLinearLayoutHeader
				.getLayoutParams();
		// 设置窗口高度
		lp.height = height;
		// 设置下拉刷新窗口
		cLinearLayoutHeader.setLayoutParams(lp);
	}

	/**
	 * 获取下拉刷新窗口的高度
	 * 
	 * @time 注释时间：2013-12-12 下午3:15:27
	 */
	public int getVisiableHeight() {
		return cLinearLayoutHeader.getHeight();
	}

	/**
	 * 设置刷新窗口的显示状态
	 * 
	 * @time 注释时间：2013-12-12 下午3:36:18
	 */
	public void setState(int state) {
		// 如果传入的状态和上一个状态一样，则直接返回
		if (cState == state) {
			return;
		}
		// 如果是正在刷新的状态
		if (state == STATE_REFRESHING) {
			// 清除箭头的动画
			cImageViewJiantou.clearAnimation();
			// 隐藏箭头
			cImageViewJiantou.setVisibility(View.GONE);
			// 显示进度条
			cProgressBar.setVisibility(View.VISIBLE);
		} else {
			// 显示箭头
			cImageViewJiantou.setVisibility(View.VISIBLE);
			// 隐藏进度条
			cProgressBar.setVisibility(View.GONE);
		}
		switch (state) {
		// 下拉刷新
		case STATE_NORMAL:
			// 进入释放刷新
			if (cState == STATE_READY) {
				cImageViewJiantou.startAnimation(cAnimationToDown);
			}
			// 进入刷新
			else if (cState == STATE_REFRESHING) {
				cImageViewJiantou.clearAnimation();
			}
			// 设置提示显示
			cTextViewHint.setText("下拉刷新");
			break;
		// 释放刷新
		case STATE_READY:
			if (cState != STATE_READY) {
				cImageViewJiantou.clearAnimation();
				cImageViewJiantou.startAnimation(cAnimationToUp);
				// 设置提示显示
				cTextViewHint.setText("释放刷新");
			}
			break;
		// 刷新中
		case STATE_REFRESHING:
			// 设置提示显示
			cTextViewHint.setText("正在加载...");
			break;

		default:
			break;
		}
		// 记录已执行的状态
		cState = state;
	}
}
