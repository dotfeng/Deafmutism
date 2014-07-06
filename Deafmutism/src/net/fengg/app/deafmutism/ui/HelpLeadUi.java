package net.fengg.app.deafmutism.ui;


import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import net.fengg.app.deafmutism.R;
import net.fengg.app.deafmutism.ui.base.BaseActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;


@ContentView(R.layout.activity_help_lead)
public class HelpLeadUi extends BaseActivity implements OnGestureListener {

	@InjectView(R.id.viewFlipper)
	private ViewFlipper flipper;// 容器
	private GestureDetector detector;// 探测器
	private long lastPauseTime = 0;// 系统时间
	private int mIndexHelpPic = 0;// 当前显示的图片的位置
	private int[] pictures = new int[] { R.drawable.help_1,
			R.drawable.help_2, R.drawable.help_3,
			R.drawable.help_4, };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cancelBaseDialog();
		// 检测器
		detector = new GestureDetector(this);

		for (int i = 0; i < 4; i++) {
			addView(i);
		}
		flipper.setDisplayedChild(0);
		// 标记
	}

	/**
	 * 在flipperview中显示图片
	 */
	private void addView(int mIndexHelpPic) {
		ImageView imageView1 = new ImageView(this);
		imageView1.setScaleType(ScaleType.FIT_XY);
		imageView1.setImageResource(pictures[mIndexHelpPic]);
		flipper.addView(imageView1, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return this.detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		long time = System.currentTimeMillis();
		if (time - lastPauseTime < 500) {
			return false;
		}
		lastPauseTime = time;
		if (3 == mIndexHelpPic) {
			//登录界面
			openActivity(ChatActivity.class);
			this.finish();
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e1.getX() - e2.getX() > 20) {// 右移
			mIndexHelpPic++;
			if (mIndexHelpPic > 3) {
				mIndexHelpPic = 3;
				return false;
			}

			// 添加进的动画
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));

			// 添加出的动画
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));

			this.flipper.showNext();
		}

		if (e1.getX() - e2.getX() < -20) {// 左移
			mIndexHelpPic--;
			if (mIndexHelpPic < 0) {
				mIndexHelpPic = 0;
				return false;
			}

			// 添加出的动画
			flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));

			// 添加进的动画
			flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));

			this.flipper.showPrevious();
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
