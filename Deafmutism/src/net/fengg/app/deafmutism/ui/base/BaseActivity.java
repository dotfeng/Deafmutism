/**
 * 
 */
package net.fengg.app.deafmutism.ui.base;


import roboguice.activity.RoboActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import net.fengg.app.deafmutism.AppManager;
import net.fengg.app.deafmutism.R;
import net.fengg.app.deafmutism.view.LoadingDialog;


/**
 * @author YangSen
 * 
 */
public class BaseActivity extends RoboActivity {
	protected LoadingDialog baseDialog;
	protected SharedPreferences mSharedPreferences;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		AppManager.getAppManager().addActivity(this);
		showBaseDialog();
		//初始化缓存对象.
		mSharedPreferences = getSharedPreferences(getPackageName(),
						MODE_PRIVATE);
	}


	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelBaseDialog();
		AppManager.getAppManager().finishActivity(this);

	}

	@Override
	public void onPause() {
		super.onPause();
//		bus.unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
//		bus.register(this);
	}
	/**
	 * 进入activity显示加载进度
	 */
	protected void showBaseDialog() {
		baseDialog = new LoadingDialog(BaseActivity.this, R.style.dialog);
		baseDialog.show();
	}

	/**
	 * 消除baseDialog
	 */
	protected void cancelBaseDialog() {
		if (baseDialog.isShowing()) {
			baseDialog.dismiss();
		}
	}

	protected void openActivity(Class<?> activity) {

		openActivity(activity, null);
	}

	/**
	 * @param activity
	 * @param bundle
	 */
	protected void openActivity(Class<?> activity, Bundle bundle) {

		Intent intent = new Intent();
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		intent.setClass(getApplicationContext(), activity);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	protected void openActivityForResult(Class<?> activity){
		openActivityForResult(activity, -1);
	}
	
	protected void openActivityForResult(Class<?> activity, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), activity);
		startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}
}
