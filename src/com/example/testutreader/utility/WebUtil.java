package com.example.testutreader.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;
import android.widget.Toast;

import com.example.testutreader.model.Comment;
import com.example.testutreader.model.NewsDetails;
import com.example.testutreader.model.NewsSummary;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class WebUtil {
	final static String TAG = "WebUtil";
	public static ArrayList<NewsSummary> getNewsSummaries(int cid,int startnid){
		String apiUrl = "http://jiuling-utreader.appspot.com/getNewsSummary?cid="+cid+"&sid="+startnid;
		String returnedJson = null;
		ArrayList<NewsSummary> result = new ArrayList<NewsSummary>();

	    try {
	      returnedJson = Resources.toString( new URL(apiUrl), Charsets.UTF_8 );
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    if (returnedJson != null){
	    	Gson gson = new Gson();
	    	Type listType = new TypeToken<List<NewsSummary>>(){}.getType();
	        result = gson.fromJson(returnedJson, listType);
	    }
	    
		return result;
	}
	
	public static ArrayList<NewsDetails> getNewsDetails(int cid,int startnid){
		String apiUrl = "http://jiuling-utreader.appspot.com/getNewsDetails?cid="+cid+"&sid="+startnid;
		String returnedJson = null;
		ArrayList<NewsDetails> result = new ArrayList<NewsDetails>();
		Log.w(TAG,"mark : url is " + apiUrl);
	    try {
	      returnedJson = Resources.toString( new URL(apiUrl), Charsets.UTF_8 );
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    if (returnedJson != null){
	    	Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	    	Type listType = new TypeToken<List<NewsDetails>>(){}.getType();
	        result = gson.fromJson(returnedJson, listType);
	    }
		return result;
	}
	
	public static ArrayList<Comment> getComments(int cid,int id){
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
	public static void makeHTTPPOSTRequest(String apiUrl) {
        try {
            HttpClient c = new DefaultHttpClient();        
            HttpPost p = new HttpPost(apiUrl);        
            HttpResponse r = c.execute(p);
        }
      
        catch(IOException e) {
            System.out.println(e);
        }                        
    }    

	public static void postComments(int cid, int id, String name, String content){
		String url = "http://jiuling-utreader.appspot.com/postComments";
		url = url + "?cid=" + cid + "&id="+id+"&name="+name+"&content="+content;
		Log.e(TAG,"mark enter thread here: " + url);
		makeHTTPPOSTRequest(url);
	}

}
