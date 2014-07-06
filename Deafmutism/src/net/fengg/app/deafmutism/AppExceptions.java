/**
 *
 * @author Feng
 */
package net.fengg.app.deafmutism;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;


import org.apache.http.HttpException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


/**
 * 
 * @author Feng
 * 
 */
public class AppExceptions extends Exception implements
		UncaughtExceptionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8821946901284487917L;

	private final static boolean Debug = false;// 是否保存错误日志

	/** 定义异常类型 */
	public final static byte TYPE_NETWORK = 0x01;
	public final static byte TYPE_SOCKET = 0x02;
	public final static byte TYPE_HTTP_CODE = 0x03;
	public final static byte TYPE_HTTP_ERROR = 0x04;
	public final static byte TYPE_XML = 0x05;
	public final static byte TYPE_IO = 0x06;
	public final static byte TYPE_RUN = 0x07;

	private byte type;
	private int code;
	public static final String TAG = "mDefaultHandler"; 
	private UncaughtExceptionHandler mDefaultHandler;

	/**
	 * @return the type
	 */
	public byte getType() {

		return type;
	}

	/**
	 * @return the code
	 */
	public int getCode() {

		return code;
	}

	private AppExceptions() {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	public AppExceptions(byte type, int code, Exception exception) {

		super(exception);
		this.type = type;
		this.code = code;
		if (Debug) {
			this.saveErrorLog(exception);
		}

	}

	Context context;

	public void makeToast(Context context) {
		this.context = context;
		switch (this.getType()) {
		case TYPE_HTTP_CODE:
			String err = context.getString(R.string.http_status_code_error,
					this.getCode());
			Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_HTTP_ERROR:
			Toast.makeText(context, R.string.http_exception_error,
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_SOCKET:
			Toast.makeText(context, R.string.socket_exception_error,
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_NETWORK:
			Toast.makeText(context, R.string.network_not_connected,
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_XML:
			Toast.makeText(context, R.string.xml_parser_failed,
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_IO:
			Toast.makeText(context, R.string.io_exception_error,
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_RUN:
			Toast.makeText(context, R.string.app_run_code_error,
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	/**
	 * 保存异常信息到SD卡
	 * 
	 * @param exception
	 */
	private void saveErrorLog(Exception exception) {

		String logName = "errorlog.txt";
		String savePath = "";
		String filePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		String sdstate = Environment.getExternalStorageState();
		if (sdstate == Environment.MEDIA_MOUNTED) {
			savePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + R.string.app_name+"/Log/";
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} else {
			File file = context.getCacheDir();
			savePath = file.toString() + File.separator;
		}
		filePath = savePath + logName;
		if (savePath.equals("")) {
			return;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();

				fw = new FileWriter(file, true);
				pw = new PrintWriter(fw);
				exception.printStackTrace(pw);
				pw.close();
				fw.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (pw != null)
					pw.close();
				if (fw != null)
					try {
						fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			}
		}

	}

	public static AppExceptions http(int code) {

		return new AppExceptions(TYPE_HTTP_CODE, code, null);
	}

	public static AppExceptions http(Exception e) {

		return new AppExceptions(TYPE_HTTP_ERROR, 0, e);
	}

	public static AppExceptions socket(Exception e) {

		return new AppExceptions(TYPE_SOCKET, 0, e);
	}

	public static AppExceptions io(Exception e) {

		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppExceptions(TYPE_NETWORK, 0, e);
		} else if (e instanceof IOException) {
			return new AppExceptions(TYPE_IO, 0, e);
		}
		return run(e);
	}

	public static AppExceptions xml(Exception e) {

		return new AppExceptions(TYPE_XML, 0, e);
	}

	public static AppExceptions network(Exception e) {

		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppExceptions(TYPE_NETWORK, 0, e);
		} else if (e instanceof HttpException) {
			return http(e);
		} else if (e instanceof SocketException) {
			return socket(e);
		}
		return http(e);
	}

	public static AppExceptions run(Exception e) {

		return new AppExceptions(TYPE_RUN, 0, e);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}

	}

	/**
	 * 自定义异常处理:收集错误信息&发送错误报告
	 * 
	 * @param ex
	 * @return true:处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {

		if (ex == null) {
			return false;
		}

		final Context context = AppManager.getAppManager().getCurrentActivity();

		if (context == null) {
			return false;
		}

		final String crashReport = getCrashReport(context, ex);
		// 显示异常信息&发送报告
		new Thread() {

			public void run() {
				Looper.prepare();
				makeToast(context);
				sendAppCrashReport(context, crashReport);
				Looper.loop();
			}

			/**
			 * 发送异常信息到服务器
			 * 
			 * @param context
			 * @param crashReport
			 */
			private void sendAppCrashReport(Context context, String crashReport) {
				Log.d(TAG, "an error occured when collect crash info");
			}

		}.start();
		return true;
	}

	/**
	 * 获取APP崩溃异常报告
	 * 
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context, Throwable ex) {

		PackageInfo pinfo = ((AppContext) context.getApplicationContext())
				.getPackageInfo();
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "("
				+ pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE
				+ "(" + android.os.Build.MODEL + ")\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		return exceptionStr.toString();
	}

	/**
	 * 获取APP异常崩溃处理对象
	 * 
	 * @param context
	 * @return
	 */
	public static AppExceptions getAppExceptionHandler() {
		return new AppExceptions();
	}
}
