package net.fengg.app.deafmutism;



import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechUser;

import net.fengg.app.deafmutism.ui.HelpLeadUi;
import net.fengg.app.deafmutism.ui.ChatActivity;
import net.fengg.app.deafmutism.ui.base.BaseActivity;
import net.fengg.app.deafmutism.util.SharepreferenceUtil;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

@ContentView(R.layout.activity_start)
public class AppStart extends BaseActivity {

	@InjectView(R.id.image)
	private ImageView image;
	private SharepreferenceUtil sharepreference;
	private boolean Flag = false;// 判断是否是第一次使用,true--不是，false---是

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cancelBaseDialog();
		sharepreference = new SharepreferenceUtil(this, "deafmutism");
		
		//用户登录
		SpeechUser.getUser().login(getApplicationContext(), null, null
						, "appid=" + getString(R.string.app_id), listener);
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(2000);

		image.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				AppStart.this.finish();

				Flag = sharepreference.getBoolean("Flag");
				if (Flag) {
					//非第一次用，直接登录
					openActivity(ChatActivity.class);
				} else {//第一次使用
					openActivity(HelpLeadUi.class);
					Flag = true;
					sharepreference.commitBoolean("Flag", Flag);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}
		});

	}
	/**
	 * 用户登录回调监听器.
	 */
	private SpeechListener listener = new SpeechListener()
	{

		@Override
		public void onData(byte[] arg0) {
		}

		@Override
		public void onCompleted(SpeechError error) {
			if(error != null) {
				Toast.makeText(getApplicationContext(), getString(R.string.text_login_fail)
						, Toast.LENGTH_SHORT).show();
				
			}			
		}

		@Override
		public void onEvent(int arg0, Bundle arg1) {
		}		
	};
}
