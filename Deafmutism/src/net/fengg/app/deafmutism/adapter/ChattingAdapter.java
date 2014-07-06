package net.fengg.app.deafmutism.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.fengg.app.deafmutism.R;
import net.fengg.app.deafmutism.model.ChatMessage;

public class ChattingAdapter extends BaseAdapter {
	private Context context;
	private List<ChatMessage> chatMessages;

	public ChattingAdapter(Context context, List<ChatMessage> messages) {
		super();
		this.context = context;
		this.chatMessages = messages;
	}

	public int getCount() {
		return chatMessages.size();
	}

	public Object getItem(int position) {
		return chatMessages.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ChatMessage message = chatMessages.get(position);
		
		if (convertView == null
				|| (holder = (ViewHolder) convertView.getTag()).flag != message.getDirection() 
				|| holder.text == null) {
			holder = new ViewHolder();
			if (message.getDirection() == ChatMessage.MESSAGE_LEFT) {
				holder.flag = ChatMessage.MESSAGE_LEFT;
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from, null);
				holder.text = (TextView) convertView.findViewById(R.id.chatting_content_itv);
			} else {
				holder.flag = ChatMessage.MESSAGE_RIGHT;
					convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to, null);
					holder.text = (TextView) convertView.findViewById(R.id.chatting_content_itv);
			}			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
			holder.text.setText(message.getContent());
		
		return convertView;
	}

	// 优化listview的Adapter
	private final class ViewHolder {
		private TextView text;
		private int flag;
	}
}
