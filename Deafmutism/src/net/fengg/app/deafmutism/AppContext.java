/**
 *
 * @author Feng
 */
package net.fengg.app.deafmutism;


import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.widget.Toast;


/**
 * 可用来存储一些需要整个程序生命周期需要的数据
 * 
 * @author Feng
 * 
 */
//ACRA异常处理，也可使用AppExcepitons
/*@ReportsCrashes(formKey="dGVacG0ydVHnaNHjRjVTUTEtb3FPWGc6MQ",
formUri = "http://192.168.1.101/UAControl/bug/post",
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
resNotifTickerText = R.string.crash_notif_ticker_text,
resNotifTitle = R.string.crash_notif_title,
resNotifText = R.string.crash_notif_text,
resNotifIcon = android.R.drawable.stat_notify_error, // optional. default is a warning sign
resDialogText = R.string.crash_dialog_text,
resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)*/
public class AppContext extends Application {

	/**
	 * 用于检测网络状态及网络类型
	 * 
	 * @author YangSen
	 * 
	 */
	public class NetConnectChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			State wifiState = null;
			State mobileState = null;
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getState();
			if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED == mobileState) {
				// 手机网络连接成功
				netState = NET_DATA_STATE;
				judgeShowImg(netState);
			} else if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED != mobileState) {
				// 手机没有任何的网络
				netState = NET_NONE_STATE;
//				showToast("亲，请连接网络后使用。");
			} else if (wifiState != null && State.CONNECTED == wifiState) {
				// 无线网络连接成功
				netState = NET_WIFI_STATE;
				judgeShowImg(netState);
			}

		}

	}
	
	
	/**
	 * 判断是否加载图片
	 */
	private void judgeShowImg(int netState){
		SharedPreferences sharedPreferences = getSharedPreferences("sharedData", Context.MODE_PRIVATE);
		AppContext.CB_PUSH = sharedPreferences.getBoolean("cbPush", true);
		AppContext.CB_WIFI = sharedPreferences.getBoolean("cbWifiShow", true);
		
		if(CB_PUSH) {
		}
		
		if(CB_WIFI){
			if(netState == NET_WIFI_STATE){
				SHOW_IMG = true;
			}else{
				SHOW_IMG = false;
			}
			return;
		}else {
			SHOW_IMG = true;
		}
	}
	

	/**
	 * 设备信息
	 */
	public static final String deviceInfo = "BRAND:" + android.os.Build.BRAND
			+ ";" + "Device:" + android.os.Build.DEVICE + ";" + "MANUFACTURER:"
			+ android.os.Build.MANUFACTURER + ";" + "MODEL:"
			+ android.os.Build.MODEL + ";" + "VERSION:"
			+ android.os.Build.VERSION.SDK_INT + ".";
	public static final int NET_UNKOWN_STATE = -1; // 初始化状态
	public static final int NET_NONE_STATE = 0; // 没有网络
	public static final int NET_WIFI_STATE = 2; // WIFI网络
	public static final int NET_DATA_STATE = 3; // 数据流量
	public static String appName;
	public static String appPath;
	public static PackageInfo info;
	public static Toast toast;
	public static int netState = NET_UNKOWN_STATE;
	//推送
	public static boolean CB_PUSH = false;
	//wifi状态下显示图片
	public static boolean CB_WIFI = false;
	//是否显示图片
	public static boolean SHOW_IMG = false;
	//标记是否是第一次登陆
//	public static int FIRST_FLAG = 0;
//	public static int NETWORK_NUM = 0;
	
	public static int CART_NUM = 0;

	/**
	 * NET_UNKOWN_STATE = -1; // 初始化状态<br/>
	 * NET_NONE_STATE = 0; // 没有网络<br/>
	 * NET_WIFI_STATE = 2; // WIFI网络<br/>
	 * NET_DATA_STATE = 3; // 数据流量<br/>
	 * 
	 * @return
	 */
	public static int getNetState() {
		return netState;
	}

	/**
	 * @param aString
	 */
	private static void showToast(String aString) {
		toast.setText(aString);
		toast.show();
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		if (info == null) {
			info = new PackageInfo();
		}
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		appName = info.packageName;
		return info;
	}

	/**
	 * 
	 */
	private void init() {
		toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
	}

	public static Bitmap bitmap;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 注册App异常崩溃处理器，可不使用，可使用ACRA替代。
//		Thread.setDefaultUncaughtExceptionHandler(AppExceptions
//				.getAppExceptionHandler());
		init();
		getPackageInfo();
		registerNetChangeListener();
		Resources res = getResources();
	    bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);  
	}

	/**
	 * 
	 */
	private void registerNetChangeListener() {
		NetConnectChangeReceiver netConnectChangeReceiver = new NetConnectChangeReceiver();
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(netConnectChangeReceiver, filter);
	}

}
