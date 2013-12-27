package com.example.testutreader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConstomTextView extends LinearLayout{

	final String TAG = "ConstomTextView";
	private Context mContext;

	private TypedArray mTypedArray;

	private LayoutParams params;
	
	public ConstomTextView(Context context) {
		super(context);
	}
	
	public ConstomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.setOrientation(LinearLayout.VERTICAL);

		mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.constomTextView);
	}
	
	public void setText(ArrayList<HashMap<String, Object>> datas) {
		
		for(HashMap<String, Object> hashMap : datas) {
		
			String type = (String) hashMap.get("type");
			
			
			if(type.equals("image")){
				
				int imagewidth = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_width, 360);
				int imageheight = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_height, 250);
				Log.e(TAG, "width = " + imagewidth + "  height = " + imageheight);
				ImageView imageView = new ImageView(mContext);
				params = new LayoutParams(imagewidth, imageheight);
				params.gravity = Gravity.CENTER_HORIZONTAL;
				imageView.setLayoutParams(params);
				
				imageView.setImageResource(R.drawable.ic_constom);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);

				addView(imageView);
				
				new DownloadPicThread(imageView, hashMap.get("value").toString()).start();
			}
			else {
				float textSize = mTypedArray.getDimension(R.styleable.constomTextView_textSize, 16);
				//int textColor = mTypedArray.getColor(R.styleable.constomTextView_textColor, 0xFF0000FF);
				
				TextView textView = new TextView(mContext);
				textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
				textView.setText(hashMap.get("value").toString().replaceAll("�", " "));
				textView.setTextSize(textSize);
				LayoutParams paramsExample = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1.0f);
				//textView.setTextColor(textColor);
				textView.setTextColor(0xFF00000F);;
				paramsExample.setMargins(40, 20, 40, 20);
				Typeface font= Typeface.createFromAsset(mContext.getAssets(), "Arial.ttf");
				textView.setTypeface(font);
				textView.setLayoutParams(paramsExample);
				addView(textView);
			}
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> hashMap = (HashMap<String, Object>) msg.obj;
			ImageView imageView = (ImageView) hashMap.get("imageView");
			LayoutParams params = new LayoutParams(msg.arg1, msg.arg2);
			params.gravity = Gravity.CENTER_HORIZONTAL;	
			imageView.setLayoutParams(params);
			Drawable drawable = (Drawable) hashMap.get("drawable");
			imageView.setImageDrawable(drawable);		
		};
	};
	

	private class DownloadPicThread extends Thread {
		private ImageView imageView;
		private String mUrl;
		
		
		public DownloadPicThread(ImageView imageView, String mUrl) {
			super();
			this.imageView = imageView;
			this.mUrl = mUrl;
		}


		@Override
		public void run() {
			// TODO Auto-generated method stub
			Drawable drawable = null;
			int newImgWidth = 0;
			int newImgHeight = 0;
			try {
				drawable = Drawable.createFromStream(new URL(mUrl).openStream(), "image");
				//Bitmap b = ((BitmapDrawable)drawable).getBitmap();
				newImgWidth = drawable.getIntrinsicWidth();
				newImgHeight = drawable.getIntrinsicHeight();
//				Log.e(TAG, "original width = " + newImgHeight + " height = " + newImgHeight);
//			    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, newImgWidth*2, newImgHeight*2, false);
//			    drawable =  new BitmapDrawable(bitmapResized);
//				newImgWidth = drawable.getIntrinsicWidth();
//				newImgHeight = drawable.getIntrinsicHeight();
//				Log.e(TAG, "current width = " + newImgHeight + " height = " + newImgHeight);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			SystemClock.sleep(2000);

			Message msg = handler.obtainMessage();
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("imageView", imageView);
			hashMap.put("drawable", drawable);
			msg.obj = hashMap;
			msg.arg1 = newImgWidth;
			msg.arg2 = newImgHeight;
			handler.sendMessage(msg);
		}
	}

}
