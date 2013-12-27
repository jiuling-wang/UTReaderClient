package com.example.testutreader.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class PostCommentsThread extends Thread {
	private int cid;
	private int id;
	private String time;
	private String name;
	private String content;
	private Context context;
	final private String TAG = "PostCommentsThread";
	public PostCommentsThread(int cid, int id, String name,
			String content, Context context) {
		super();
		Date date = new Date();
		this.time = date.toString();
		this.cid = cid;
		this.id = id;
		this.content = content;
		this.name = name;
		this.context = context;
	}

	@Override
	public void run() {
		String url = "http://jiuling-utreader.appspot.com/postComments";
		url = url + "?cid=" + cid + "&id="+id+"&name="+name+"&content="+content;
		Log.e(TAG,"mark enter thread here: " + url);
		try {
			HttpClient c = new DefaultHttpClient();
			HttpPost p = new HttpPost(url);
			HttpResponse r = c.execute(p);
			Log.e(TAG,"mark after executation: " + url);
			int statusCode = r.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				Toast.makeText(context, "Post successfully", Toast.LENGTH_SHORT)
						.show();
				return;
			}else{
				Toast.makeText(context, "Post failed", Toast.LENGTH_SHORT)
				.show();
				return;
			}
		} catch (Exception e) {
			Log.e(TAG,"mark thread exception: " + e.toString());
			e.printStackTrace();
		}
	}
}