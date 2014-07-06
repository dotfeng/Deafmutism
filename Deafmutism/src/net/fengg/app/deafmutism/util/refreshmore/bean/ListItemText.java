package net.fengg.app.deafmutism.util.refreshmore.bean;

import java.util.ArrayList;
import java.util.List;

public class ListItemText {
	private String text;
	private boolean beDelete;
	private boolean beAdd;
	
	public ListItemText(String text) {
		this.text = text;
		this.beDelete = false;
		this.beAdd = false;
	}
	
	public ListItemText(String text, boolean beDelete, boolean beAdd) {
		this.text = text;
		this.beDelete = beDelete;
		this.beAdd = beAdd;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean getbeDelete() {
		return beDelete;
	}

	public void setBeDelete(boolean beDelete) {
		this.beDelete = beDelete;
	}

	@Override
	public String toString() {
		return "MyInformation [name=" + text + ", delete=" + beDelete + "]";
	}
	public static ArrayList<String> toStringList(List<ListItemText> items) {
		ArrayList<String> list = new ArrayList<String>();
		for(ListItemText item : items) {
			list.add(item.getText());
		}
		return list;
	}
	public static String[] toStringArray(List<ListItemText> items) {
		String[] list = new String[items.size()];
		for(int i =0; i < items.size(); i++) {
			list[i] = items.get(i).getText();
		}
		return list;
	}
	
	public static ArrayList<ListItemText> toList(String[] items) {
		return toList(items, false, false);
	}
	public static ArrayList<ListItemText> toList(String[] items, boolean beDelete, boolean beAdd) {
		ArrayList<ListItemText> list = new ArrayList<ListItemText>();
		for(String item : items) {
			list.add(new ListItemText(item, beDelete, beAdd));
		}
		return list;
	}

	public boolean getBeAdd() {
		return beAdd;
	}

	public void setBeAdd(boolean beAdd) {
		this.beAdd = beAdd;
	}
}
