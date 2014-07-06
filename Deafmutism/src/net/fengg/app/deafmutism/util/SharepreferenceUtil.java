package net.fengg.app.deafmutism.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharepreferenceUtil {
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor localEditor;
	
	@SuppressLint("CommitPrefEdits")
	public SharepreferenceUtil(Context context, String shareName){
		sharedPreferences = context.getSharedPreferences(shareName, Context.MODE_PRIVATE);
		localEditor = sharedPreferences.edit();
	}
	
	public String getString(String key){
		String result = null;
		
		//如果不存在该该key键值，那么就返回null
		result = sharedPreferences.getString(key, "");
		return result;
	}
	public String[] getStringArray(String key, String[] defaultValue){
		try {
			String jsonData = "";
			String defaultString = null;
			if(null != defaultValue)
				defaultString = FastJsonUtils.getJsonString(defaultValue);
			jsonData = sharedPreferences.getString(key, defaultString);
			return FastJsonUtils.getStringArray(jsonData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public String[] getStringSet(String key){
		return getStringArray(key, null);
	}
	public int getInt(String key){
		int result = -1;
		result = sharedPreferences.getInt(key, -1);
		return result;
	}
	
	public boolean getBoolean(String key){
		boolean result = false;
		result = sharedPreferences.getBoolean(key, false);
		return result;
	}
	
	public void commitString(String key, String value){
		localEditor.putString(key, value);
		localEditor.commit();
	}
	
	public void commitStringArray(String key, String[] value){
		localEditor.putString(key, FastJsonUtils.getJsonString(value));
		localEditor.commit();
	}
	public void commitInt(String key, int value){
		localEditor.putInt(key, value);
		localEditor.commit();
	}
	
	public void commitBoolean(String key, boolean value){
		localEditor.putBoolean(key, value);
		localEditor.commit();
	}
	//清空
	public void clear(){
		localEditor.clear().commit();
	}
}
