package net.fengg.app.deafmutism.util.refreshmore.adapter;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import net.fengg.app.deafmutism.R;
import net.fengg.app.deafmutism.util.refreshmore.bean.ListItemText;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;


public class MyListViewAdapter extends BaseAdapter {
	// 构造数据源
	private ArrayList<ListItemText> cArrayList;
	// 使用布局填充器将xml转换成View
	private LayoutInflater cLayoutInflater;
	// 删除数据接口
	private DeleteNow cDeleteNow;
	private InputMethodManager imm;
	
	public MyListViewAdapter(ArrayList<ListItemText> list, Context context) {
		super();
		this.cArrayList = list;
		// 设置填充器的上下文环境,获取填充器
		cLayoutInflater = LayoutInflater.from(context);
		imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	/**
	 * 外部设置数据
	 * 
	 * @time 注释时间：2013-12-13 下午6:28:50
	 * @param arrayList
	 */
	public void setData(ArrayList<ListItemText> arrayList) {
		this.cArrayList = arrayList;
	}

	/**
	 * 返回数据源的大小
	 */
	@Override
	public int getCount() {
		return cArrayList.size();
	}

	/**
	 * 返回数据源每一条单独数据的对象
	 */
	@Override
	public Object getItem(int position) {
		return cArrayList.get(position);
	}

	/**
	 * 监听器的最后一个long id参数
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewWwapper cViewWwapper;
		if (convertView == null) {
			cViewWwapper = new ViewWwapper();
			convertView = cViewWwapper.getView();
			// 设个标记Tag,将cViewWwapper暂存在convertView中
			convertView.setTag(cViewWwapper);
		} else {
			cViewWwapper = (ViewWwapper) convertView.getTag();
		}
		// 通过持有者中保管的控件,拿出存在的控件设置数值就可以了
		cViewWwapper.getTvText().setText(
				cArrayList.get(position).getText());
		cViewWwapper.getEtText().setText(
				cArrayList.get(position).getText());
		
		if (cArrayList.get(position).getBeAdd()){
			cViewWwapper.getTvText().setVisibility(View.GONE);
			cViewWwapper.getEtText().setVisibility(View.VISIBLE);
			cViewWwapper.getTvDelete().setVisibility(View.GONE);
			cViewWwapper.getTvEdit().setVisibility(View.VISIBLE);
			cViewWwapper.getTvEdit().setText(R.string.save);
			cViewWwapper.getEtText().setFocusable(true);
			cViewWwapper.getEtText().setFocusableInTouchMode(true);
			cViewWwapper.getEtText().requestFocus();
			imm.showSoftInput(cViewWwapper.getEtText(), InputMethodManager.RESULT_SHOWN);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}else if (cArrayList.get(position).getbeDelete()) {
			cViewWwapper.getTvDelete().setVisibility(View.VISIBLE);
			cViewWwapper.getTvEdit().setVisibility(View.VISIBLE);
		} else {
			cViewWwapper.getTvText().setVisibility(View.VISIBLE);
			cViewWwapper.getEtText().setVisibility(View.GONE);
			cViewWwapper.getTvDelete().setVisibility(View.GONE);
			cViewWwapper.getTvEdit().setVisibility(View.GONE);
		}
		cViewWwapper.getTvDelete().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (cDeleteNow != null) {
							cDeleteNow.delete(position);
						}
					}
				});
		cViewWwapper.getTvEdit().setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if("编辑".equals(cViewWwapper.getTvEdit().getText().toString())) {
							cViewWwapper.getTvText().setVisibility(View.GONE);
							cViewWwapper.getEtText().setVisibility(View.VISIBLE);
							cViewWwapper.getTvDelete().setVisibility(View.GONE);
							cViewWwapper.getTvEdit().setVisibility(View.VISIBLE);
							cViewWwapper.getTvEdit().setText(R.string.save);
							cViewWwapper.getEtText().setFocusable(true);
							cViewWwapper.getEtText().setFocusableInTouchMode(true);
							cViewWwapper.getEtText().requestFocus();
							imm.showSoftInput(cViewWwapper.getEtText(), InputMethodManager.RESULT_SHOWN);
							imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
						}else {
							cViewWwapper.getTvText().setVisibility(View.VISIBLE);
							cViewWwapper.getEtText().setVisibility(View.GONE);
							cViewWwapper.getTvEdit().setVisibility(View.VISIBLE);
							cViewWwapper.getTvEdit().setText(R.string.edit);
							cArrayList.get(position).setText(cViewWwapper.getEtText().getText().toString());
							cArrayList.get(position).setBeDelete(false);
							cArrayList.get(position).setBeAdd(false);
							cViewWwapper.getEtText().clearFocus();
							imm.hideSoftInputFromWindow(cViewWwapper.getEtText().getWindowToken(), 0);
							EventBus.getDefault().post(produceEvent());
						}
					}
				});
		return convertView;
	}

	/**
	 * 外部设置接口
	 * 
	 * @time 注释时间：2013-12-16 下午6:03:50
	 * @param deleteNow
	 */
	public void setDeleteNowListener(DeleteNow deleteNow) {
		this.cDeleteNow = deleteNow;
	}

	/**
	 * 外部删除的接口
	 * 
	 * @author 蔡有飞 E-mail: caiyoufei@looip.cn
	 * @version 创建时间：2013-12-16 下午6:03:03
	 * 
	 */
	public interface DeleteNow {
		public void delete(int position);
	}

	/**
	 * 持有者类，保管我的视图控件
	 * 
	 * @author 蔡有飞 E-mail: caiyoufei@looip.cn
	 * @version 创建时间：2013-12-13 下午3:03:12
	 * 
	 */
	private class ViewWwapper {
		View cView;
		TextView tvText;
		EditText etText;
		TextView tvDelete;
		TextView tvEdit;
		/**
		 * 保管单行布局的View
		 * 
		 * @time 注释时间：2013-12-13 下午3:07:02
		 * @return
		 */
		private View getView() {
			if (cView == null) {
				cView = cLayoutInflater.inflate(R.layout.refreshmore_list_item, null);
			}
			return cView;
		}

		/**
		 * 保管单行中的TextViewName
		 * 
		 * @time 注释时间：2013-12-13 下午3:09:03
		 * @return
		 */
		private TextView getTvText() {
			if (tvText == null) {
				tvText = (TextView) getView().findViewById(
						R.id.tv_word_text);
			}
			return tvText;
		}
		
		private EditText getEtText() {
			if (etText == null) {
				etText = (EditText) getView().findViewById(
						R.id.et_word_text);
			}
			return etText;
		}
		/**
		 * 保管单行中的TextViewAge
		 * 
		 * @time 注释时间：2013-12-13 下午3:10:30
		 * @return
		 */
		private TextView getTvDelete() {
			if (tvDelete == null) {
				tvDelete = (TextView) getView().findViewById(
						R.id.tv_row_listview_age);
			}
			return tvDelete;
		}
		
		private TextView getTvEdit() {
			if (tvEdit == null) {
				tvEdit = (TextView) getView().findViewById(
						R.id.tv_row_listview_edit);
			}
			return tvEdit;
		}
	}

	public class ItemSaveEvent {
		private ArrayList<ListItemText> cArrayList;
		
		public ItemSaveEvent(ArrayList<ListItemText> cArrayList) {
			this.cArrayList = cArrayList;
		}
		
		public ArrayList<ListItemText> getcArrayList() {
			return cArrayList;
		}
	}
	public ItemSaveEvent produceEvent() {
		return new ItemSaveEvent(cArrayList);
	}
	
}
