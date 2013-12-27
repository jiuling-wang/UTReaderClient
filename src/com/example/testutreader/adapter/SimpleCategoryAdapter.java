package com.example.testutreader.adapter;

import java.util.List;
import java.util.Map;

import com.example.testutreader.R;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SimpleCategoryAdapter extends SimpleAdapter{

	public SimpleCategoryAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = super.getView(position, convertView, parent);
		TextView categoryTitle = (TextView) v;
		categoryTitle.setTextSize(15);
		categoryTitle.setTextColor(0xff000000);
		if(position == 0) {
			
			categoryTitle.setBackgroundResource(R.drawable.indicator);
			//categoryTitle.setTextColor(0xFFFFFFFF);
		}
		return v;
	}

}
