package net.fengg.app.deafmutism.ui;

import net.fengg.app.deafmutism.R;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * 合成设置界面Activity.
 * 
 * @author iFlytek
 * @since 20120823
 */
public class SettingActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {

	// 地图搜索关键字定义
	public static final String ENGINE_POI = "poi";

	/**
	 * 合成界面入口函数
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// PreferenceManager初始化
		PreferenceManager preferenceManager = getPreferenceManager();
		preferenceManager.setSharedPreferencesName(getPackageName());
		preferenceManager.setSharedPreferencesMode(MODE_PRIVATE);

		addPreferencesFromResource(R.xml.preference_tts);

		// 初始化发音人列表对象.
		ListPreference roleListPreference = (ListPreference) findPreference(getString(R.string.preference_key_tts_role));
		roleListPreference.setOnPreferenceChangeListener(this);
		roleListPreference.setSummary(roleListPreference.getEntry());

		// 初始化语速的拖动条对象.
		SeekBarPreference speedSeekBarPreference = (SeekBarPreference) findPreference(getString(R.string.preference_key_tts_speed));
		speedSeekBarPreference.setOnPreferenceChangeListener(this);
		speedSeekBarPreference.setSummary(String.valueOf(speedSeekBarPreference
				.getProgress()));

		// 初始化音量的拖动条对象.
		SeekBarPreference volumeSeekBarPreference = (SeekBarPreference) findPreference(getString(R.string.preference_key_tts_volume));
		volumeSeekBarPreference.setOnPreferenceChangeListener(this);
		volumeSeekBarPreference.setSummary(String
				.valueOf(volumeSeekBarPreference.getProgress()));
		// 初始化语调的拖动条对象
		SeekBarPreference pitchSeekBarPreference = (SeekBarPreference) findPreference(getString(R.string.preference_key_tts_pitch));
		pitchSeekBarPreference.setOnPreferenceChangeListener(this);
		pitchSeekBarPreference.setSummary(String.valueOf(pitchSeekBarPreference
				.getProgress()));

		// 听写引擎列表.
		ListPreference engineListPreference = (ListPreference) findPreference(getString(R.string.preference_key_iat_engine));
		engineListPreference.setOnPreferenceChangeListener(this);
		engineListPreference.setSummary(engineListPreference.getEntry());

		// 采样率参数列表.
		ListPreference rateListPreference = (ListPreference) findPreference(getString(R.string.preference_key_iat_rate));
		rateListPreference.setOnPreferenceChangeListener(this);
		rateListPreference.setSummary(rateListPreference.getEntry());

	}

	/**
	 * OnPreferenceChangeListener的接口，当设置界面做了修改时被调用.
	 */
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// 如果是弹出列表在ListPreference中查找选中的是哪个元素.
		if (preference instanceof ListPreference) {
			ListPreference listPreference = (ListPreference) preference;

			CharSequence[] entries = listPreference.getEntries();
			int index = listPreference.findIndexOfValue((String) newValue);

			listPreference.setSummary(entries[index]);
			// 如果是seekbar在SeekBarPreference中查找选中的是哪个元素.
		} else if (preference instanceof SeekBarPreference) {
			SeekBarPreference seekBarPreference = (SeekBarPreference) preference;

			seekBarPreference.setSummary(newValue.toString());
		}
		return true;
	}

}
