package com.example.testutreader;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.example.testutreader.adapter.PullToRefreshListViewSampleAdapter;
import com.example.testutreader.adapter.SimpleCategoryAdapter;
import com.example.testutreader.model.Category;
import com.example.testutreader.model.Comment;
import com.example.testutreader.model.NewsDetails;
import com.example.testutreader.model.NewsSummary;
import com.example.testutreader.utility.DensityUtil;
import com.example.testutreader.utility.StoreUtil;
import com.example.testutreader.utility.StringUtil;
import com.example.testutreader.utility.WebUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.internal.eu;
import com.google.android.gms.plus.PlusClient;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  {

	private final int COLUMNWIDTH_PX = 200; // GridView每个单元格的宽度(像素)
	private final int FLINGVELOCITY_PX = 800; // ViewFilper滑动的距离(像素)
	private final int NEWSCOUNT = 5; // 显示新闻的条数
	private final int SUCCESS = 0; // 加载新闻成功
	private final int NONEWS = 1; // 没有新闻
	private final int NOMORENEWS = 2; // 没有更多新闻
	private final int LOADERROR = 3; // 加载失败



	List<HashMap<String, Category>> categories;
	private long exitTime; // 按返回键退出的时间
	private int mColumnWidth_dip;
	private int mCid; // 新闻编号
	private String mCategoryTitle; // 新闻分类标题
	private PullToRefreshListView  mNewslist; // 新闻列表
	private PullToRefreshListViewSampleAdapter mNewslistAdapter; // 为新闻内容提供需要显示的列表
	private ArrayList<HashMap<String, Object>> mNewsData; // 存储新闻信息的数据集合
	private LayoutInflater mInflater; // 用来动态载入没有loadmore_layout界面

	private Button mLoadmoreButton; // 加载更多按钮

	private LoadNewsAsyncTack mLoadNewsAsyncTack; // 声明LoadNewsAsyncTack引用

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Button mSettingButton = (Button)findViewById(R.id.titlebar_setting);
		mSettingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						UserSettingActivity.class);
				startActivity(intent);
				
			}
		});

		mColumnWidth_dip = DensityUtil.px2dip(this, COLUMNWIDTH_PX);
		mInflater = getLayoutInflater();
		mNewsData = new ArrayList<HashMap<String, Object>>();


		categories = new ArrayList<HashMap<String, Category>>();
		new GetTopicsAsyncTack().execute(StoreUtil.currentUser);
		
		
		// 添加单元格点击事件
		

		//getSpecCatNews(mCid, mNewsData, 0, true);

		
		
//		mNewslistAdapter = new SimpleAdapter(this, mNewsData,
//				R.layout.newslist_item_layout, new String[] {
//						"newslist_item_title", "newslist_item_digest",
//						"newslist_item_source", "newslist_item_ptime" },
//				new int[] { R.id.newslist_item_title,
//						R.id.newslist_item_digest, R.id.newslist_item_source,
//						R.id.newslist_item_ptime });
//		mNewslist = (ListView) findViewById(R.id.news_list);
		
		mNewslistAdapter = new PullToRefreshListViewSampleAdapter(this, mNewsData,
				R.layout.newslist_item_layout, new String[] {
						"newslist_item_title", "newslist_item_digest",
						"newslist_item_source", "newslist_item_ptime" },
				new int[] { R.id.newslist_item_title,
						R.id.newslist_item_digest, R.id.newslist_item_source,
						R.id.newslist_item_ptime });
		
		mNewslist = (PullToRefreshListView) findViewById(R.id.news_list);

		View footerView = mInflater.inflate(R.layout.loadmore_layout, null);
		// 在LiseView下面添加“加载更多”
		mNewslist.addFooterView(footerView);
		// 显示列表
		mNewslist.setAdapter(mNewslistAdapter);
		mNewslist.setOnRefreshListener(new OnRefreshListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mNewslistAdapter.loadData();
				mNewslist.postDelayed(new Runnable() {

					
					@Override
					public void run() {
						mNewslist.onRefreshComplete();
					}
				}, 1000);
				
			}
			
		});
		mNewslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						NewsDetailActivity.class);
				intent.putExtra("categoryTitle", mCategoryTitle);
				intent.putExtra("newsData", mNewsData);
				intent.putExtra("position", position);
				startActivity(intent);
			}

		});

		mLoadmoreButton = (Button) findViewById(R.id.loadmore_btn);
		mLoadmoreButton.setOnClickListener(loadmoreListener);

	}
	
	
private class GetTopicsAsyncTack extends AsyncTask<String, Integer, String>{

		
		protected void onPostExecute(String result) {
			new LoadNewsAsyncTack().execute(0, true);
			for (int i = 0; i < StoreUtil.CATEGORY_NAME.length; i++){
				if (!result.contains(StoreUtil.CATEGORY_NAME[i])) {
					continue;
				}
				Category type = new Category(i,StoreUtil.CATEGORY_NAME[i]);
				HashMap<String, Category> hashMap = new HashMap<String, Category>();
				hashMap.put("category_title", type);
				categories.add(hashMap);
			}
			SimpleCategoryAdapter categoryAdapter = new SimpleCategoryAdapter(MainActivity.this,
					categories, R.layout.category_item_layout,
					new String[] { "category_title" },
					new int[] { R.id.category_title });

			GridView category = new GridView(MainActivity.this);
			category.setSelector(new ColorDrawable(Color.TRANSPARENT));
			category.setColumnWidth(mColumnWidth_dip);
			category.setNumColumns(GridView.AUTO_FIT);
			category.setGravity(Gravity.CENTER);
			int width = mColumnWidth_dip * categories.size();
			LayoutParams params = new LayoutParams(width, LayoutParams.MATCH_PARENT);
			category.setLayoutParams(params);
			category.setAdapter(categoryAdapter);
			LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
			categoryLayout.addView(category);
			category.setOnItemClickListener(new OnItemClickListener() {
				TextView categoryTitle;

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					for (int i = 0; i < parent.getCount(); i++) {
						categoryTitle = (TextView) parent.getChildAt(i);
						categoryTitle.setTextColor(0xff000000);
						categoryTitle.setBackgroundDrawable(null);

					}
					categoryTitle = (TextView) view;
					categoryTitle.setTextColor(0xFF000000);
					categoryTitle
							.setBackgroundResource(R.drawable.indicator);
//					Toast.makeText(MainActivity.this, categoryTitle.getText(),
//							Toast.LENGTH_SHORT).show();
					mCid = categories.get(position).get("category_title").getCid();
					mCategoryTitle = categories.get(position).get("category_title")
							.getTitle();
					mLoadNewsAsyncTack = new LoadNewsAsyncTack();
					mLoadNewsAsyncTack.execute(0, true);
				}

			}); 
	     }

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			String name = params[0];
			if (name.equals("Anonymous")){
				for (int i = 0; i < StoreUtil.CATEGORY_NAME.length; i++){
					result = result + StoreUtil.CATEGORY_NAME[i] + ",";
				}
				return result;
			}
			String url = "http://jiuling-utreader.appspot.com/getTopics?username="+name;
			try {
			      result = Resources.toString( new URL(url), Charsets.UTF_8 );
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			    
			return result;
		}
		
    	
    }



	private int getSpecCatNews(int cid, List<HashMap<String, Object>> newsList,
			int startnid, boolean firstTime) {
		if (firstTime) {
			newsList.clear();
		}
		//ArrayList<NewsSummary> nss = WebUtil.getNewsSummaries(cid, startnid);
		ArrayList<NewsDetails> nds = WebUtil.getNewsDetails(cid, startnid);
		if (!nds.isEmpty()){
			for (NewsDetails nd : nds) {
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("newslist_item_title", nd.title);
				hashMap.put("newslist_item_source", nd.source.replaceAll("�", " "));
				hashMap.put("newslist_item_digest", nd.digest.replaceAll("�", " "));
				hashMap.put("newslist_item_ptime", nd.time);
				hashMap.put("newslist_item_article", nd.article.replaceAll("�", " "));
				hashMap.put("newslist_item_imgurl", nd.imgURL);
				hashMap.put("newslist_item_id", nd.id);
				hashMap.put("newslist_item_cid", nd.cid);
				newsList.add(hashMap);
			}
		}
		
		Collections.sort(newsList,new Comparator<HashMap<String, Object>>() {
			public int compare(HashMap<String, Object> a, HashMap<String, Object> b){
				int aid = (Integer) a.get("newslist_item_id");
				int bid = (Integer) b.get("newslist_item_id");
				if (aid < bid){
					return 1;
				}else{
					return -1;
				}
				
			}
		});

		return SUCCESS;
	}

	/**
	 * 为“加载更多”按钮定义匿名内部类
	 */
	private OnClickListener loadmoreListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mLoadNewsAsyncTack = new LoadNewsAsyncTack();
			switch (v.getId()) {
			// 点击加载更多
			case R.id.loadmore_btn:
				int startID = 0;
				if (!mNewsData.isEmpty()){
					HashMap<String, Object> map = mNewsData.get(mNewsData.size()-1);
					startID = (Integer) map.get("newslist_item_id")-1;
				}
				mLoadNewsAsyncTack.execute(startID, false); // 不是第一次加载新闻里列表
				break;
			// 点击刷新按钮
			case R.id.titlebar_refresh:
				mLoadNewsAsyncTack.execute(0, true);
				break;
			}
		}
	};

	private class LoadNewsAsyncTack extends AsyncTask<Object, Integer, Integer> {

		// 准备运行
		@Override
		protected void onPreExecute() {
			mLoadmoreButton.setText(R.string.loadmore_text);
		}

		// 在后台运行
		@Override
		protected Integer doInBackground(Object... params) {
			return getSpecCatNews(mCid, mNewsData, (Integer) params[0],
					(Boolean) params[1]);
		}

		// 完成后台任务
		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			// 该栏目没有新闻
			case NONEWS:
				Toast.makeText(MainActivity.this, R.string.nonews,
						Toast.LENGTH_SHORT).show();
				break;
			// 该栏目没有更多新闻
			case NOMORENEWS:
				Toast.makeText(MainActivity.this, R.string.nomorenews,
						Toast.LENGTH_SHORT).show();
				break;
			// 加载失败
			case LOADERROR:
				Toast.makeText(MainActivity.this, R.string.loadnewserror,
						Toast.LENGTH_SHORT).show();
				break;
			}
			mLoadmoreButton.setText(R.string.loadmore_btn); // 按钮信息替换为“加载更多”
			mNewslistAdapter.notifyDataSetChanged(); // 通知ListView更新数据
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(1, 1, 1, "Update");
		menu.add(1, 2, 2, "Exit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			// UpdateManager updateManager = new
			// UpdateManager(MainActivity.this);
			// 检测更新
			// updateManager.checkUpdate();
			break;
		case 2:
			finish();
			break;
		}
		return true;
	}

	/**
	 * 按键触发的事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime > 2000)) {
				Toast.makeText(getApplicationContext(), R.string.backcancel,
						Toast.LENGTH_LONG).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
}
