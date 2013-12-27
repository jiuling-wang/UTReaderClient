package com.example.testutreader.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.data.d;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

public class PullToRefreshListViewSampleAdapter extends SimpleAdapter {
	private List<HashMap<String, Object>> newsList;
	public PullToRefreshListViewSampleAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to
			
			){
		super(context, data, resource, from, to);
		newsList = (List<HashMap<String, Object>>) data;
		
	}
	public void loadData(){
		
		
	}
	
	@Override
    public int getCount() {
            return newsList.size();
    }

    @Override
    public Object getItem(int position) {
            return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
            return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
    	View v = super.getView(position, convertView, parent);
    	return v;
    	
    }

}
