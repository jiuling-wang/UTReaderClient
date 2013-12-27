package com.example.testutreader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;








import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.testutreader.model.Comment;
import com.example.testutreader.utility.StoreUtil;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class CommentsActivity extends Activity {

	private int cid;
	private int id;
	private LinearLayout mNewsReplyEditLayout;
	private LinearLayout mNewsReplyImgLayout;
	private ImageButton mNewsReplyImgBtn;
	private EditText mNewsReplyEditText;
	private Button mNewsReplyPost;
	private SimpleAdapter commentsAdapter;
	private ListView commentsList;
	
	List<HashMap<String, Object>> mCommentsData = new ArrayList<HashMap<String,Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comments_layout);
		mNewsReplyEditLayout = (LinearLayout) findViewById(R.id.news_reply_edit_layout);
		mNewsReplyImgLayout = (LinearLayout) findViewById(R.id.news_reply_img_layout);
		mNewsReplyImgBtn = (ImageButton) findViewById(R.id.news_reply_img_btn);
		mNewsReplyEditText = (EditText) findViewById(R.id.news_reply_edittext);
		mNewsReplyPost = (Button) findViewById(R.id.news_reply_post);
		
		NewsCommentsOnClickListener newsCommentsOnClickListener = new NewsCommentsOnClickListener();
		mNewsReplyImgBtn.setOnClickListener(newsCommentsOnClickListener);
		mNewsReplyPost.setOnClickListener(newsCommentsOnClickListener);
		Intent intent = getIntent();
		cid = intent.getIntExtra("cid", 0);
		id = intent.getIntExtra("id", 0);
		mCommentsData = new ArrayList<HashMap<String,Object>>();
		commentsList = (ListView) findViewById(R.id.comment_list);
		
		new GetCommentsAsyncTack().execute();
		
		
		
		
	}
	
	private class GetCommentsAsyncTack extends AsyncTask<Void, Integer, ArrayList<Comment>>{

		
		protected void onPostExecute(ArrayList<Comment> result) {
	         if (result.isEmpty()){
	        	 return;
	         }
	         mCommentsData.clear();
	         
	         for (int i = 0; i < result.size(); i++) {
	        	 	Comment commentsObject = result.get(i);
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("cid", commentsObject.cid);
					hashMap.put("commentator_from",
							commentsObject.name);
					hashMap.put("comment_ptime",
							commentsObject.time);
					hashMap.put("comment_content",
							commentsObject.content.replaceAll("Space", " "));
					mCommentsData.add(hashMap);
				}
	         
	         commentsAdapter = new SimpleAdapter(CommentsActivity.this, mCommentsData
	 				, R.layout.comments_list_item_layout
					, new String[] {"commentator_from", "comment_ptime", "comment_content"}
					, new int[] { R.id.commentator_from, R.id.comment_ptime, R.id.comment_content});
	 		 commentsList.setAdapter(commentsAdapter);
	     }

		@Override
		protected ArrayList<Comment> doInBackground(Void... params) {
			ArrayList<Comment> result = new ArrayList<Comment>();
			String returnedJson = null;
			String url = "http://jiuling-utreader.appspot.com/getComments?cid="+cid+"&id="+id;
			try {
			      returnedJson = Resources.toString( new URL(url), Charsets.UTF_8 );
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			    if (returnedJson != null){
			    	Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			    	Type listType = new TypeToken<List<Comment>>(){}.getType();
			        result = gson.fromJson(returnedJson, listType);
			    }
			return result;
		}
		
    	
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comments, menu);
		return true;
	}
	
	private class PostCommentsAsyncTack extends AsyncTask<String, Integer, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			HttpClient c = new DefaultHttpClient();        
            HttpPost p = new HttpPost(params[0]);        
            try {
				c.execute(p);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return 1;
			
		}
		protected void onPostExecute(Integer res){
			if (res == 1){
				new GetCommentsAsyncTack().execute();
			}
			
		}
    	
    }
	private class NewsCommentsOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			InputMethodManager m = (InputMethodManager) mNewsReplyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			// TODO Auto-generated method stub
			switch(v.getId()){
			//图片按钮
			case R.id.news_reply_img_btn:
				mNewsReplyEditLayout.setVisibility(View.VISIBLE);
				mNewsReplyImgLayout.setVisibility(View.GONE);
				mNewsReplyEditText.requestFocus();
				//显示输入法
				m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
				break;
			//发送按钮
			case R.id.news_reply_post:
				//隐藏输入法
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				String str = mNewsReplyEditText.getText().toString();
				if(str.equals("")){
					Toast.makeText(CommentsActivity.this, "Could not be empty",
							Toast.LENGTH_SHORT).show();
				}
				else {
					String url = "http://jiuling-utreader.appspot.com/postComments";
					String newStr = str.replaceAll(" ", "Space");
					url = url + "?cid=" + cid + "&id="+id+"&name="+StoreUtil.currentUser+"&content="+newStr;
                    new PostCommentsAsyncTack().execute(url);
					mNewsReplyEditLayout.setVisibility(View.GONE);
					mNewsReplyImgLayout.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
		
	}

}
