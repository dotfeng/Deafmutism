package net.fengg.app.deafmutism.ui;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SynthesizerListener;

import de.greenrobot.event.EventBus;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.fengg.app.deafmutism.R;
import net.fengg.app.deafmutism.adapter.ChattingAdapter;
import net.fengg.app.deafmutism.model.ChatMessage;
import net.fengg.app.deafmutism.ui.HotWordsActivity.ItemClickedEvent;
import net.fengg.app.deafmutism.ui.base.BaseActivity;
import net.fengg.app.deafmutism.util.JsonParser;


/**
 * 实现语音会话功能
 * 
 * @author xuetao
 * 
 */
@ContentView(R.layout.activity_chatting)
public class ChatActivity extends BaseActivity implements OnClickListener, OnItemClickListener,
SynthesizerListener {
	
	/*****************************************************************************
	 * 常量变量定义区*
	 *****************************************************************************/
	
	private ChattingAdapter chatHistoryAdapter;
	private List<ChatMessage> messages = new ArrayList<ChatMessage>();
	
	@InjectView(R.id.lv_chat_history)
	private ListView lv_chat_history;
	@InjectView(R.id.tv_click_chat)
	private TextView tv_click_chat;
	@InjectView(R.id.ll_assistant)
	private LinearLayout ll_assistant; // 弹出的助手界面
	@InjectView(R.id.ll_bottom)
	private LinearLayout ll_bottom; // 初始底部界面
	@InjectView(R.id.ll_bottom_parse)
	private LinearLayout ll_bottom_parse;// 解析的底部界面
	@InjectView(R.id.rl_soft_keyboard)
	private RelativeLayout rl_soft_keyboard;// 调用软键盘的底部界面
	@InjectView(R.id.iv_parse)
	private ImageView iv_parse;// 弹出的助手框的解析动画对应的ImageView
	@InjectView(R.id.tv_parse)
	private TextView tv_parse;// 底部中“识别中”解析动画对应的TextView
	@InjectView(R.id.iv_keyboard)
	private ImageView iv_keyboard; // 软键盘
	@InjectView(R.id.et_nput)
	private EditText et_nput;// 输入框
	@InjectView(R.id.tv_confirm)
	private TextView tv_confirm;// 确定输入
	private boolean isConfirmInput = false;// 是否是在软键盘界面点击“确定”完成问句
	@InjectView(R.id.iv_voice)
	private ImageView iv_voice;// 软键盘界面的麦克风图标
	@InjectView(R.id.tv_cancel)
	private TextView tv_cancel;// 识别过程中的取消按钮
	@InjectView(R.id.tv_listen)
	private TextView tv_listen;// 请说话、倾听中
	@InjectView(R.id.iv_hot_words)
	private ImageView iv_hot_words;// 热词
	@InjectView(R.id.rl_chatting)
	private RelativeLayout rl_chatting;// 聊天界面的parent
	@InjectView(R.id.iv_setting)
	private ImageView iv_setting;//设置界面
	

	private AnimationDrawable animationDrawable;// 弹出的助手框的解析动画
	
	private AnimationDrawable animationDrawable_parse;// 底部中“识别中”解析动画
	
	private String user_question = "";// 用户问的问题
	private InputMethodManager imm;
	private Toast mToast;
	private boolean isStopByUser = false; // 是否是用户主动关闭（即用户点击“点击完成”）
	private boolean isStopParse = false;// 是否是用户取消解析(解析中是点击“取消”按钮)
	
	public static ChatActivity instance = null;

	/** 用户点击“点击说话” **/
	private final int REFRESH_CLICK_CHAT = 0;

	/** 初始化 **/
	private final int REFRESH_INIT = 1;

	/** 显示解析动画 **/
	private final int DISPLAY_PARSE = 2;

	/** 显示助手 **/
	private final int DISPLAY_ASSIS = 3;

	/** 隐藏助手 **/
	private final int HIDE_ASSIS = 4;

	/** listview数据更新 **/
	private final int NOTIFY_DATA_CHANGED = 5;

	/** 语音识别动画初始化 **/
	private final int VOLUME_INIT = 6;

	/** 用户说话后，跳转到指定图片 **/
	private final int VOLUME_CHANGE = 7;

	/** 显示软键盘layout **/
	private final int DISPLAY_KEYBOARD = 8;

	/** "请说话"改为"倾听中" **/
	private final int LISTENING = 9;

	/** 显示或者隐藏热词 **/
	private final int REFRESH_HOT = 10;

	/** 开启语音动画 **/
	private final int SHOW_ANIMATION = 11;

	/** 取消语音动画 **/
	private final int CANCEL_ANIMATION = 12;

	/** 开启解析动画 **/
	private final int SHOW_PARSE_ANIMATION = 13;

	/** 取消解析动画 **/
	private final int CANCEL_PARSE_ANIMATION = 14;

	/** 显示软键盘 arg1**/
	private final int SHOW_KEYBOARD = 0;

	/** 隐藏软键盘arg1 **/
	private final int HIDE_KEYBOARD = 1;
	
	/** 显示热词  arg1**/
	private final int SHOW_HOT = 2;

	/** 隐藏热词  arg1**/
	private final int HIDE_HOT = 3;
	
	private SpeechRecognizer speechRecognizer;
	private SpeechSynthesizer speechSynthesizer;// 读
	
	HotWordsActivity hotWordsActivity = new HotWordsActivity();
	
	
	
	/*****************************************************************************
	 * handler处理区*
	 *****************************************************************************/
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 用户点击“点击说话”
			case REFRESH_CLICK_CHAT:

				tv_click_chat.setText("点击完成");
				break;

			// 初始化
			case REFRESH_INIT:

				ll_bottom.setVisibility(View.VISIBLE);
				ll_bottom_parse.setVisibility(View.INVISIBLE);
				rl_soft_keyboard.setVisibility(View.INVISIBLE);
				// 隐藏热词
//				rl_hot_words.setVisibility(View.GONE);
				rl_chatting.setVisibility(View.VISIBLE);
				tv_click_chat.setText("点击说话");
				tv_listen.setText("请说话");
				ll_assistant.setVisibility(View.GONE);
				break;

			// 显示解析动画
			case DISPLAY_PARSE:

				ll_bottom_parse.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.INVISIBLE);
				rl_soft_keyboard.setVisibility(View.INVISIBLE);
				break;

			// 显示助手
			case DISPLAY_ASSIS:

				ll_assistant.setVisibility(View.VISIBLE);
				break;

			// 隐藏助手
			case HIDE_ASSIS:

				ll_assistant.setVisibility(View.GONE);
				break;

			// listview数据更新
			case NOTIFY_DATA_CHANGED:

				chatHistoryAdapter.notifyDataSetChanged();
				break;

			// 语音识别动画初始化
			case VOLUME_INIT:

				iv_parse.setBackgroundResource(R.anim.chat);
				animationDrawable = (AnimationDrawable) iv_parse
						.getBackground();
				animationDrawable.start();
				break;

			// 用户说话后，跳转到指定图片
			case VOLUME_CHANGE:

				iv_parse.setBackgroundResource(R.drawable.chat_volume);
				break;

			// 显示软键盘layout
			case DISPLAY_KEYBOARD:

				rl_soft_keyboard.setVisibility(View.VISIBLE);
				ll_bottom_parse.setVisibility(View.INVISIBLE);
				ll_bottom.setVisibility(View.INVISIBLE);
				et_nput.setText("");
				// 获取焦点
				et_nput.requestFocus();
				if (msg.arg1 == SHOW_KEYBOARD) {
					// 弹出软键盘
					showKeyBoard();
				}

				break;

			// "请说话"改为"倾听中"
			case LISTENING:

				tv_listen.setText("倾听中");

				break;

			// 显示或者隐藏热词
			case REFRESH_HOT:
				
//				if (msg.arg1 == SHOW_HOT) {
//					rl_hot_words.setVisibility(View.VISIBLE);
//					rl_chatting.setVisibility(View.GONE);
//				} else if (msg.arg1 == HIDE_HOT){
//					rl_hot_words.setVisibility(View.GONE);
//					rl_chatting.setVisibility(View.VISIBLE);
//				} 			

				break;

			// 开启语音动画
			case SHOW_ANIMATION:

				if (!animationDrawable.isRunning()) {
					animationDrawable.start();
				}

				break;

			// 取消语音动画
			case CANCEL_ANIMATION:

				if (animationDrawable.isRunning()) {
					animationDrawable.stop();
				}

				break;

			// 开启解析动画
			case SHOW_PARSE_ANIMATION:

				if (!animationDrawable_parse.isRunning()) {
					animationDrawable_parse.start();
				}

				break;

			// 取消解析动画
			case CANCEL_PARSE_ANIMATION:

				animationDrawable_parse.stop();

				break;

			default:
				break;
			}
		};
	};
	

	
	
	/**************************************************************************
	 * Activity生命周期处理区*
	 **************************************************************************/
	
	@SuppressLint("ShowToast")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initListener();
		
		initAdapter();
		
		animationDrawable = (AnimationDrawable) iv_parse.getBackground();
		animationDrawable_parse = (AnimationDrawable) tv_parse.getBackground();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		//创建识别对象
		speechRecognizer = SpeechRecognizer.createRecognizer(this);
		//初始化合成对象.
		speechSynthesizer = SpeechSynthesizer.createSynthesizer(this);
				
		mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		instance = this;
		cancelBaseDialog();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onStop() {
		mToast.cancel();
		if (null != speechRecognizer) {
			speechRecognizer.cancel();
		}
		if (null != speechSynthesizer) {
			speechSynthesizer.stopSpeaking();
		}
		super.onStop();
	}
	
	/**************************************************************************
	 * 初始化区*
	 **************************************************************************/

	/**
	 * 初始化组件
	 */
	private void initListener() {
		lv_chat_history.setOnItemClickListener(this);
		tv_click_chat.setOnClickListener(this);
		iv_keyboard.setOnClickListener(this);
		tv_confirm.setOnClickListener(this);
		iv_voice.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		iv_hot_words.setOnClickListener(this);
		iv_setting.setOnClickListener(this);
	}
	
	
	
	/**
     * 识别回调。
     */
	RecognizerListener recognizerListener = new RecognizerListener() {

		/**
		 * 音量变化回调。 说明： 1、录音数据回调，可以使用volume值更新录音状态。 参数： volume - 录音音量值，范围0-30 抛出：
		 * android.os.RemoteException
		 */
		@Override
		public void onVolumeChanged(int volume) {

			if (volume != 0) {
				/**
				 * 语音图片(随着用户音量的高低)刷新
				 */
				// 图片切换成用于当前音量大小对应的图片
				handler.sendEmptyMessage(VOLUME_CHANGE);
				// 图片初始化
				handler.sendEmptyMessageDelayed(VOLUME_INIT, 200);
				if ("请说话".equals(tv_listen.getText().toString().trim())) {
					handler.sendEmptyMessage(LISTENING);
				}
			}

		}

		/**
		 * 1、识别引擎采用流式传输的方式，可能会多次返回结果。 参数： recognizerresult -
		 * 识别结果，请参考RecognizerResult定义 islast - true表示最后一次结果，false表示结果未取完
		 */
		@Override
		public void onResult(final RecognizerResult recognizerresult,
				boolean islast) {
			Log.e("xuetao", "onResult isLast = " + islast + ", isStopByUser = "+isStopByUser);
			/**
			 * 加上下面的处理，是解决两个问题：
			 * 1、如果是用户手动停止的(手动停止是用户点击了”说完了“按钮)，则需要走后面的流程
			 * 2、如果不是用户手动停止的，并且是islast，则需要return，不能走后面的流程，因为如果走了后面的流程，
			 * 当用户语音输入了 123456，则最后只显示最后的标点符号
			 */
			if (islast && !isStopByUser) {
				if (recognizerresult == null) { // 如果用户没有说话，则进行初始化
					Log.e("xuetao",
							"-----------recognizerresult == null------11111-----------");
					handler.sendEmptyMessage(REFRESH_INIT);
					handler.sendEmptyMessage(CANCEL_ANIMATION);
				} else {
					Log.e("xuetao",
							"-----------recognizerresult != null------22222---getAns--------");
					showResult();
				}
				return;
			}

			runOnUiThread(new Runnable() {
				public void run() {
					if (recognizerresult != null) {
						handler.sendEmptyMessage(CANCEL_ANIMATION);
						handler.sendEmptyMessage(HIDE_ASSIS);
						System.out.println(recognizerresult.getResultString());

						user_question = user_question + JsonParser.parseIatResult(recognizerresult.getResultString());
						if (isStopByUser) {
							Log.e("xuetao", "-----------33333---getAns--------");
							showResult();
							isStopByUser = false;
						}

					} else {
						handler.sendEmptyMessage(REFRESH_INIT);
						handler.sendEmptyMessage(CANCEL_ANIMATION);
					}

				}
			});

		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, String msg) {

		}
		@Override
		public void onError(SpeechError err) {
			showTip(err.getPlainDescription(true));
			handler.sendEmptyMessage(CANCEL_ANIMATION);
			handler.sendEmptyMessage(REFRESH_INIT);
		}

		@Override
		public void onEndOfSpeech() {
			Log.e("xuetao", "___________onEndOfSpeech________");
		}

		@Override
		public void onBeginOfSpeech() {
			Log.e("xuetao", "__________onBeginOfSpeech_______");
		}
	};

	/**************************************************************************
	 * 点击事件处理区*
	 **************************************************************************/
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_click_chat:
			if ("点击说话".equals(tv_click_chat.getText().toString().trim())) {
				handler.sendEmptyMessage(DISPLAY_ASSIS);
				handler.sendEmptyMessage(REFRESH_CLICK_CHAT);
				handler.sendEmptyMessage(SHOW_ANIMATION);
				recognize();
			} else if ("点击完成".equals(tv_click_chat.getText().toString().trim())) {
				isStopByUser = true;
				speechRecognizer.stopListening();
				handler.sendEmptyMessage(CANCEL_ANIMATION);
			}

			break;

		case R.id.iv_keyboard:

			Message message = handler.obtainMessage();
			message.what = DISPLAY_KEYBOARD;
			message.arg1 = SHOW_KEYBOARD;
			handler.sendMessage(message);
			break;

		case R.id.tv_confirm:
			String question = et_nput.getText().toString();
			Log.e("xuetao", "-------------question------" + question);
			if (question == null || "".equals(question)) {
				Toast.makeText(getApplicationContext(), "您没有输入任何内容",
						Toast.LENGTH_SHORT).show();
			} else {
				isConfirmInput = true;
				Message msg = handler.obtainMessage();
				msg.what = REFRESH_HOT;
				msg.arg1 = HIDE_HOT;
				handler.sendMessage(msg);
				
				handler.sendEmptyMessage(DISPLAY_PARSE);
				handler.sendEmptyMessage(SHOW_PARSE_ANIMATION);
				
				refreshUI(question, true, ChatMessage.MESSAGE_LEFT);
				
				closeKeyBoard();
			}

			break;

		case R.id.iv_voice:
			handler.sendEmptyMessage(REFRESH_INIT);
			closeKeyBoard();
			break;

		case R.id.tv_cancel:
			isStopParse = true;
			messages.add(new ChatMessage(ChatMessage.MESSAGE_LEFT, "识别已取消"));
			handler.sendEmptyMessage(NOTIFY_DATA_CHANGED);
			handler.sendEmptyMessage(REFRESH_INIT);
			break;

		case R.id.iv_hot_words:
			openActivity(HotWordsActivity.class);
//			Message msg = handler.obtainMessage();
//			msg.what = REFRESH_HOT;
//
//			if (rl_hot_words.getVisibility() == View.VISIBLE) {
//				msg.arg1 = HIDE_HOT;
//			} else {
//				msg.arg1 = SHOW_HOT;
//			}
//			handler.sendMessage(msg);
			break;
			
		case R.id.iv_setting:
			openActivity(SettingActivity.class);
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode) {
		case -1:
			if(resultCode==Activity.RESULT_OK)
			//do something
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View itemView, int position, long id) {
		synthetizeInSilence(messages.get(position).getContent());
	}
	
	
	/**************************************************************************
	 * 方法定义区*
	 **************************************************************************/

	/**
	 * 设置adapter
	 */
	private void initAdapter() {

		chatHistoryAdapter = new ChattingAdapter(this, messages);
		lv_chat_history.setAdapter(chatHistoryAdapter);

//		lv_hot_words.setAdapter(new ArrayAdapter<String>(this,
//				android.R.layout.simple_expandable_list_item_1, Constant.HOT_QUESTIONS));
	}
	
	/**
	 * 显示软键盘
	 */
	private void showKeyBoard() {		
		imm.showSoftInput(et_nput, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/**
	 * 关闭软键盘
	 */
	private void closeKeyBoard() {
		imm.hideSoftInputFromWindow(et_nput.getWindowToken(), 0);
	}
	
	/**
	 * 进行语音识别
	 */
	private void recognize() {
		// 获取引擎参数
		String engine = mSharedPreferences.getString(
				getString(R.string.preference_key_iat_engine),
				getString(R.string.preference_default_iat_engine));
		// 清空Grammar_ID，防止识别后进行听写时Grammar_ID的干扰
		speechRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
		// 设置听写引擎
		speechRecognizer.setParameter(SpeechConstant.DOMAIN, engine);
		// 设置采样率参数，支持8K和16K

		// 设置采样率参数，支持8K和16K
		String rate = mSharedPreferences.getString(
				getString(R.string.preference_key_iat_rate),
				getString(R.string.preference_default_iat_rate));

		if (rate.equals("rate8k")) {
			speechRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
		} else {
			speechRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
		}
		// 当DOMAIN设置为POI搜索时,获取area参数
		if (SettingActivity.ENGINE_POI.equals(engine)) {
			String province = mSharedPreferences.getString(
					getString(R.string.preference_key_poi_province),
					getString(R.string.preference_default_poi_province));
			String city = mSharedPreferences.getString(
					getString(R.string.preference_key_poi_city),
					getString(R.string.preference_default_poi_city));

			speechRecognizer.setParameter(SpeechConstant.SEARCH_AREA, province
					+ city);
		}
		// 启动语义理解
		speechRecognizer.startListening(recognizerListener);

	}
	

	/**
	 * 显示toast
	 * 
	 * @param str
	 */
	private void showTip(final String str) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}
	

	/**
	 * 获取答案** 
	 */
	public void showResult() {
		// 由于handler.sendEmptyMessageDelayed(VOLUME_INIT, 200),所以需要取消这个消息
		handler.removeMessages(VOLUME_INIT);
		handler.sendEmptyMessage(DISPLAY_PARSE);
		handler.sendEmptyMessage(SHOW_PARSE_ANIMATION);
		refreshUI(user_question, false, ChatMessage.MESSAGE_RIGHT);
		user_question = "";// 初始化
	}
	

	
	/**
	 * 刷新聊天的UI
	 * @param answer
	 */
	private void refreshUI(String answer, boolean beRead, int direction) {
		handler.sendEmptyMessage(CANCEL_PARSE_ANIMATION);
		// 如果用户没有点击取消
		if (!isStopParse) {
			Log.e("xuetao",	"user--not cancel --------send------- answer = " + answer);

			messages.add(new ChatMessage(direction, answer));

			handler.sendEmptyMessage(NOTIFY_DATA_CHANGED);
			if (!isConfirmInput) {
				// 如果不是在软键盘界面点击“确认”
				handler.sendEmptyMessage(REFRESH_INIT);
			} else {
				// 如果是在软键盘界面点击“确认”
				isConfirmInput = false; // 初始化
				Message message = handler.obtainMessage();
				message.what = DISPLAY_KEYBOARD;
				message.arg1 = HIDE_KEYBOARD;
				handler.sendMessage(message);
			}
			if(beRead) {
				synthetizeInSilence(answer);
			}
		} else {
			// 如果用户点击了取消，则不发送解析后的答案，并且初始化标志位
			Log.e("xuetao", "user--cancel ---------not send");
			isStopParse = false;
		}
	}
	
	/**
	 * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
	 * @param
	 */
	private void synthetizeInSilence(String source) {
		if (null == speechSynthesizer) {
			// 创建合成对象.
			speechSynthesizer = SpeechSynthesizer.createSynthesizer(this);
		}
		// 设置合成发音人.
		String role = mSharedPreferences.getString(
				getString(R.string.preference_key_tts_role),
				getString(R.string.preference_default_tts_role));

		// 设置发音人
		speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, role);

		// 获取语速
		int speed = mSharedPreferences.getInt(
				getString(R.string.preference_key_tts_speed), 50);
		// 设置语速
		speechSynthesizer.setParameter(SpeechConstant.SPEED, "" + speed);
		// 获取音量.
		int volume = mSharedPreferences.getInt(
				getString(R.string.preference_key_tts_volume), 50);
		// 设置音量
		speechSynthesizer.setParameter(SpeechConstant.VOLUME, "" + volume);
		// 获取语调
		int pitch = mSharedPreferences.getInt(
				getString(R.string.preference_key_tts_pitch), 50);
		// 设置语调
		speechSynthesizer.setParameter(SpeechConstant.PITCH, "" + pitch);
		// 进行语音合成.
		speechSynthesizer.startSpeaking(source, this);
	}
	
	/**
	 * 按键处理
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// event.getRepeatCount() == 0这是重复次数。
		// 点后退键的时候，为了防止点得过快，触发两次后退事件，故做此设置。
		// 建议保留这个判断，增强程序健壮性。
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
//				&& rl_hot_words.getVisibility() == View.VISIBLE) {
//
//			rl_hot_words.setVisibility(View.GONE);
//			rl_chatting.setVisibility(View.VISIBLE);
//			return true;
//
//		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 设置用户问题
	 * 
	 * @param user_question
	 *            用户问题
	 */
	public void setUser_question(String user_question) {
		this.user_question = user_question;
	}

	@Override
	public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompleted(SpeechError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeakBegin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeakPaused() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeakProgress(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeakResumed() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
		//EventBus.getDefault().unregister(this);
	}
	
	public void onEvent(ItemClickedEvent event) {
		refreshUI(event.getItem(), true, ChatMessage.MESSAGE_LEFT);
	}
}
