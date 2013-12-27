package com.example.testutreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;







import com.example.testutreader.utility.StoreUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class NewsDetailActivity extends Activity {
	private final String TAG="NewsDetailActivity";
	private final int FINISH = 0;               //代表线程的状态的结束  
    private LayoutInflater mNewsbodyLayoutInflater;  
    private ViewFlipper mNewsBodyFlipper;       //屏幕切换控件  
    private ArrayList<HashMap<String, Object>> mNewsData;  
    private float mStartX;                      //手指按下的开始位置  
    private int mPosition = 0;                  //点击新闻位置          
    private int mCursor = 0;                    //用来标记新闻点击的位置  
    private ConstomTextView mNewsBodyDetail;    //新闻详细内容  
    private LinearLayout mNewsReplyEditLayout;  //新闻回复的布局  
    private LinearLayout mNewsReplyImgLayout;   //新闻图片回复的布局  
    private EditText mNewsReplyEditText;        //新闻回复的文本框  
    private ImageButton mShareNewsButton;       //分享新闻的按钮  
    private ImageButton mFavoritesButton;       //收藏新闻的按钮  
    private boolean keyboardShow;               //键盘是否显示  											
    private Handler mHandler = new Handler() {  
    	  
        @SuppressWarnings("unchecked")  
        @Override  
        public void handleMessage(Message msg) {  
            // TODO Auto-generated method stub  
            switch (msg.arg1) {  
            case FINISH:  
                //把获取到的新闻显示到界面上  
                ArrayList<HashMap<String, Object>> bodyList = (ArrayList<HashMap<String, Object>>) msg.obj;  
                mNewsBodyDetail.setText(bodyList);  
                break;  
            }  
        }  
    };  
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news_detail);
		mNewsReplyEditLayout = (LinearLayout) findViewById(R.id.news_reply_edit_layout);  
        mNewsReplyImgLayout = (LinearLayout) findViewById(R.id.news_reply_img_layout);   
        mNewsReplyEditText = (EditText) findViewById(R.id.news_reply_edittext);  
        mShareNewsButton = (ImageButton) findViewById(R.id.news_share_btn);  
        mFavoritesButton = (ImageButton) findViewById(R.id.news_favorites_btn);  
        
        NewsDetailOnClickListener newsDetailOnClickListener = new NewsDetailOnClickListener();
		mShareNewsButton.setOnClickListener(newsDetailOnClickListener);
		mFavoritesButton.setOnClickListener(newsDetailOnClickListener);
		Button newsReplyPost = (Button) findViewById(R.id.news_reply_post);
		newsReplyPost.setOnClickListener(newsDetailOnClickListener);
		ImageButton newsReplyImgBtn = (ImageButton) findViewById(R.id.news_reply_img_btn);
		newsReplyImgBtn.setOnClickListener(newsDetailOnClickListener);
		
        Intent intent = getIntent();  
        Bundle bundle = intent.getExtras();  
        Serializable serializable = bundle.getSerializable("newsData");  
        mNewsData = (ArrayList<HashMap<String, Object>>) serializable;  
        mCursor = mPosition = bundle.getInt("position"); 
        mNewsBodyFlipper = (ViewFlipper) findViewById(R.id.news_body_flipper);  
        mNewsbodyLayoutInflater = getLayoutInflater();  
        inflateView(0); 
        //new UpdateNewsThread().start();  

		
	}

	private void showPrevious(){
		if(mPosition > 0) {
			mPosition--;
			HashMap<String, Object> hashMap = mNewsData.get(mPosition);
			// if not visited previous page(idx=mPosition)
			if(mCursor > mPosition){
				mCursor = mPosition;
				inflateView(0);
			}
			mNewsBodyFlipper.setInAnimation(this, R.anim.push_right_in);	
			mNewsBodyFlipper.setOutAnimation(this, R.anim.push_right_out);	
			mNewsBodyFlipper.showPrevious();
		}
		else {
			Toast.makeText(NewsDetailActivity.this, "This is the first one", Toast.LENGTH_SHORT).show();
		}
	}

	
	//TODO mNid issue!!!
	private void showNext(){
		Log.w(TAG,"mark : enter show next " + mPosition);
		if(mPosition < mNewsData.size() - 1){  
            
            mNewsBodyFlipper.setInAnimation(this, R.anim.push_left_in);  
            mNewsBodyFlipper.setOutAnimation(this, R.anim.push_left_out);  
            mPosition++;  
            HashMap<String, Object> hashMap = mNewsData.get(mPosition);  
            if(mPosition >= mNewsBodyFlipper.getChildCount()){  
                inflateView(mNewsBodyFlipper.getChildCount());  
            }  
            mNewsBodyFlipper.showNext();  
        } else {  
            Toast.makeText(NewsDetailActivity.this, "This is the last one", Toast.LENGTH_SHORT).show();  
        }  
	}
	private void inflateView(int index) {  

        HashMap<String, Object> hashMap = mNewsData.get(mPosition);  
        View mNewsBodyView = mNewsbodyLayoutInflater.inflate(  
                R.layout.newsbody_layout, null);  
        TextView newsTitle = (TextView) mNewsBodyView  
                .findViewById(R.id.news_body_title);  
        newsTitle.setText(hashMap.get("newslist_item_title").toString());  

        TextView newsPtimeAndSource = (TextView) mNewsBodyView  
                .findViewById(R.id.news_body_ptime_source);  
        newsPtimeAndSource.setText(hashMap.get("newslist_item_source").toString()   
                + "     " + hashMap.get("newslist_item_ptime").toString());  
        mNewsBodyDetail = (ConstomTextView) mNewsBodyView  
                .findViewById(R.id.news_body_details);
        mNewsBodyDetail.setText(getNewsBody()); 
        mNewsBodyFlipper.addView(mNewsBodyView, index);  
        mNewsBodyDetail.setOnTouchListener(new NewsBodyOntouchListener());  
    }  
	
	

    private class NewsDetailOnClickListener implements OnClickListener {  
  
        public void onClick(View v) {  
            InputMethodManager m = (InputMethodManager) mNewsReplyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
            HashMap<String, Object> hashMap = mNewsData.get(mPosition);
            switch (v.getId()) {  
            //跟帖  
 
            case R.id.news_reply_img_btn:  
                mNewsReplyEditLayout.setVisibility(View.VISIBLE);  
                mNewsReplyImgLayout.setVisibility(View.GONE);  
                mNewsReplyEditText.requestFocus();  
                m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);  
                keyboardShow = true;  
                break;  
            case R.id.news_share_btn:  
                Intent shareIntent = new Intent(Intent.ACTION_SEND);   
                shareIntent.setType("text/plain");  
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share");                 
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey u know what ? "+ hashMap.get("newslist_item_title") + ". Come to UTReader and check it!" );  
                startActivity(Intent.createChooser(shareIntent, getTitle()));  
                break;   
            case R.id.news_favorites_btn:  
            	Intent intent = new Intent(NewsDetailActivity.this,  
                        CommentsActivity.class);  
                
                intent.putExtra("cid", (Integer)hashMap.get("newslist_item_cid"));
                intent.putExtra("id", (Integer)hashMap.get("newslist_item_id"));  
                startActivity(intent);  
                break;  
            case R.id.news_reply_post:  
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
                String str = mNewsReplyEditText.getText().toString();  
                if(str.equals("")){  
                    Toast.makeText(NewsDetailActivity.this, "Could not be empty",  
                            Toast.LENGTH_SHORT).show();  
                }  
                else {  
                    int cid = (Integer)hashMap.get("newslist_item_cid");
                    int id = (Integer)hashMap.get("newslist_item_id"); 
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
            return 0;
			
		}
    	
    }
    private class NewsBodyOntouchListener implements OnTouchListener {  
    	  
        @Override  
        public boolean onTouch(View v, MotionEvent event) {  
            // TODO Auto-generated method stub  
            switch (event.getAction()) {   
            case MotionEvent.ACTION_DOWN:  
                if(keyboardShow){  
                    mNewsReplyEditLayout.setVisibility(View.GONE);  
                    mNewsReplyImgLayout.setVisibility(View.VISIBLE);  
                    InputMethodManager m = (InputMethodManager) mNewsReplyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
                    keyboardShow = false;  
                }  

                mStartX = event.getX();  
                break;  
            case MotionEvent.ACTION_UP:  
            	// LEFT
                if (event.getX() < mStartX) {  
                    showNext();  
                }  
                // RIGHT 
                else if (event.getX() > mStartX) {  
                    showPrevious();  
                }  
                break;  
            }  
            return true;  
        }  
    } 
    
    
	 private class UpdateNewsThread extends Thread {  
	        @Override  
	        public void run() {  
	            // TODO Auto-generated method stub  
	            ArrayList<HashMap<String, Object>> newsStr = getNewsBody();  
	            Message msg = mHandler.obtainMessage(); //获取msg  
	            msg.arg1 = FINISH;            
	            msg.obj = newsStr;  
	            mHandler.sendMessage(msg);  //给Handler发送信息  
	        }  
	    }  
    
    private ArrayList<HashMap<String, Object>> getNewsBody(){   
        ArrayList<HashMap<String, Object>> bodylist = new ArrayList<HashMap<String,Object>>();  
        HashMap<String, Object> tmp = mNewsData.get(mPosition);
          
        if (tmp.get("newslist_item_imgurl")!=null){
        	HashMap<String, Object> hashMap = new HashMap<String, Object>();
        	hashMap.put("type", "image");
        	hashMap.put("value", tmp.get("newslist_item_imgurl")); 
        	bodylist.add(hashMap); 
        }
          
        if (tmp.get("newslist_item_article")!=null){
        	HashMap<String, Object> hashMap = new HashMap<String, Object>();
        	hashMap.put("type", "text");
        	hashMap.put("value", tmp.get("newslist_item_article")); 
        	Log.e(TAG, " value = " + hashMap.get("value"));
        	bodylist.add(hashMap);    
        }  
        
        return bodylist;  
    }  
      
    
    
    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news_detail, menu);
		return true;
	}

	
	

}
