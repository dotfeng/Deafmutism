package net.fengg.app.deafmutism.ui;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import net.fengg.app.deafmutism.R;
import net.fengg.app.deafmutism.ui.base.BaseActivity;
import net.fengg.app.deafmutism.util.SharepreferenceUtil;
import net.fengg.app.deafmutism.util.refreshmore.adapter.MyListViewAdapter;
import net.fengg.app.deafmutism.util.refreshmore.adapter.MyListViewAdapter.DeleteNow;
import net.fengg.app.deafmutism.util.refreshmore.adapter.MyListViewAdapter.ItemSaveEvent;
import net.fengg.app.deafmutism.util.refreshmore.bean.ListItemText;
import net.fengg.app.deafmutism.util.refreshmore.mylistview.MyListView;
import net.fengg.app.deafmutism.util.refreshmore.mylistview.MyListView.Delete;
import net.fengg.app.deafmutism.util.refreshmore.mylistview.MyListView.MyListViewListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

@ContentView(R.layout.activity_hot_words)
public class HotWordsActivity extends BaseActivity implements MyListViewListener,
		Delete, DeleteNow {
	// 自定义的ListView
	@InjectView(R.id.lv_hot_words)
	private MyListView cMyListView;
	@InjectResource(R.array.hot_words)
	private String[] hot_words;
	// MyListView的适配器
	private MyListViewAdapter cMyListViewAdapter;
	// 构造数据源
	private ArrayList<ListItemText> cArrayList;
	// 判断是否进行长按
	private boolean cIsLongClick = false;
	// 记录上一个删除位置
	private int cPosition = -1;
	// 记录上一个是否删除
	private boolean cIsDelete = true;
	private SharepreferenceUtil sharepreference;
	public static String clickedText = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		cancelBaseDialog();
		EventBus.getDefault().register(this);
	}
	@Override
	public void onResume() {
		super.onResume();
		//getBus().register(this);
	}
	/**
	 * 初始化
	 * 
	 * @time 注释时间：2013-12-13 下午2:37:34
	 */
	private void init() {
		cArrayList = new ArrayList<ListItemText>();
		sharepreference = new SharepreferenceUtil(this, "deafmutism");
		// 设置加载窗口是否可以操作
		cMyListView.setPullLoadEnable(true);
		// 设置刷新窗口是否可以操作
		cMyListView.setPullRefreshEnable(false);
		// 设置加载和刷新的监听和id，调用重写的刷新和加载
		cMyListView.setMyListViewListenerAndDownloadID(this, 0);
		// 出现删除接口的重写
		cMyListView.setDeleteListene(this);
		// 设置刷新的时间
		cMyListView.setRefreshTime();
		fillMyListViewAdapter();
		
		// 点击删除接口的重写
		cMyListViewAdapter.setDeleteNowListener(this);
		cMyListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 长按不执行删除
				cIsLongClick = true;
				return false;
			}
		});
		cMyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long arg3) {
				// 点击可执行删除
				cIsLongClick = false;
				if (!cIsDelete && cPosition != -1) {
					cArrayList.get(cPosition).setBeDelete(false);
					cMyListViewAdapter.setData(cArrayList);
					cMyListViewAdapter.notifyDataSetChanged();
				}else {
					clickedText = cArrayList.get(position - 1).getText();
//					使用EventBus传递数据
//					EventBus.getDefault().post(produceEvent());
					Intent it = new Intent();  
	                it.putExtra("clickedText", clickedText);  
	                setResult(Activity.RESULT_OK, it);  
					finish();
				}
			}
		});
	}

	/**
	 * 构造数据源
	 * 
	 * @time 注释时间：2013-12-13 下午3:19:09
	 * @return
	 */
	private ArrayList<ListItemText> getData() {
		String[] hotWords;
		hotWords = sharepreference.getStringArray("hot_words", hot_words);
		
		cArrayList = ListItemText.toList(hotWords);
		
		return cArrayList;
	}

	/**
	 * 填充是适配器
	 * 
	 * @time 注释时间：2013-12-13 下午3:23:38
	 */
	private void fillMyListViewAdapter() {
		cMyListViewAdapter = new MyListViewAdapter(getData(), this);
		cMyListView.setAdapter(cMyListViewAdapter);
		if (cMyListView.getLastVisiblePosition() == cMyListViewAdapter
				.getCount() - 1) {
			cMyListView.cMyListViewFooter.hideLoadMore();
		}
	}

	@Override
	public void onRefresh(int id) {
//		cArrayList.add(0, new ListItemText("新增刷新", false));
//		cMyListViewAdapter.setData(cArrayList);
//		cMyListViewAdapter.notifyDataSetChanged();
//		cMyListView.cHandler.sendEmptyMessage(cMyListView.REFRESH_COMPLETE);
	}

	@Override
	public void onLoadMore(int id) {
		cArrayList.add(new ListItemText("请编辑文字", true, true));
		cMyListViewAdapter.setData(cArrayList);
		cMyListViewAdapter.notifyDataSetChanged();
		cMyListView.cHandler.sendEmptyMessage(cMyListView.DOWNLOAD_COMPLETE);
	}

	/**
	 * MyListView滑动删除接口
	 */
	@Override
	public void deleteRow(int position) {
		if (cArrayList.size() <= 0) {
			return;
		}
		if (!cIsDelete && cPosition != -1) {
			cArrayList.get(cPosition).setBeDelete(false);
		}
		if (!cIsLongClick) {
			cPosition = position;
			cIsDelete = false;
			cArrayList.get(position).setBeDelete(true);
			cMyListViewAdapter.setData(cArrayList);
			cMyListViewAdapter.notifyDataSetChanged();
			
		}
	}

	/**
	 * 适配器的点击删除接口
	 * 
	 * @time 注释时间：2013-12-16 下午6:04:13
	 * @param position
	 */
	@Override
	public void delete(int position) {
		cIsDelete = true;
		cPosition = -1;
		cArrayList.remove(position);
		cMyListViewAdapter.setData(cArrayList);
		cMyListViewAdapter.notifyDataSetChanged();
		hot_words = ListItemText.toStringArray(cArrayList);
		sharepreference.commitStringArray("hot_words", hot_words);
	}
	
	public class ItemClickedEvent {
		public String item;
		
		public ItemClickedEvent(String item) {
			this.item = item;
		}
		
		public String getItem() {
			return item;
		}
	}
	public ItemClickedEvent produceEvent() {
		return new ItemClickedEvent(clickedText);
	}
	
	public void onEvent(ItemSaveEvent itemSaveEvent) {
		cIsDelete = true;
		cPosition = -1;
		cArrayList = itemSaveEvent.getcArrayList();
		cMyListViewAdapter.setData(itemSaveEvent.getcArrayList());
		cMyListViewAdapter.notifyDataSetChanged();
		hot_words = ListItemText.toStringArray(cArrayList);
		sharepreference.commitStringArray("hot_words", hot_words);
	}
	
}
