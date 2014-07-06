package net.fengg.app.deafmutism.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;




import java.util.Set;

import net.fengg.app.deafmutism.util.Constant;
import android.content.Context;
import android.widget.ArrayAdapter;

public class ChattingHotAdapter {

	private ArrayAdapter<String> instance = null;
	private Set<String> hotWords = new HashSet<String>();
	private Context mContext;

	public ChattingHotAdapter(Context context) {
		mContext = context;
		
	}

	public ArrayAdapter<String> getChattingHotAdapter() {
		if (instance == null) {
			instance = new ArrayAdapter<String>(mContext,
					android.R.layout.simple_expandable_list_item_1, getData());
		}
		return instance;
	}

	private List<String> getData() {
		List<String> data = new ArrayList<String>();
		for (String question : Constant.HOT_QUESTIONS) {
			data.add("\""+question+"\"");
		}
		return data;
	}
	
	private void addWord(String word) {
		hotWords.add(word);
	}
}
