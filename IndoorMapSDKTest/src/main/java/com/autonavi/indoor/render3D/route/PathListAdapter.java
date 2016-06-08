package com.autonavi.indoor.render3D.route;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PathListAdapter extends BaseAdapter {
	
	ArrayList<PoiInfo> mPoiList;
	private Context mContext;
	
	public PathListAdapter(ArrayList<PoiInfo> poiList,Context context) {
		this.mPoiList = poiList;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mPoiList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mPoiList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v = arg1;
		if(v==null)
		{
			v= new TextView(mContext);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(params);
			v.setPadding(0, 30, 0, 30);
		}
		PoiInfo info = mPoiList.get(arg0);
		TextView text = (TextView)v;
		text.setGravity(Gravity.CENTER);
		text.setTextColor(0xff000000);
		text.setTextSize(20);
		text.setFocusable(false);
		String describe = "";
		if(arg0==0){
			describe = "起点";
		}else
		if(arg0==mPoiList.size()-1){
			describe = "终点";
		}else{
			describe = info.cell.getName();
		}
		
		text.setText(describe);
		v.setTag(info);
		return v;
	}
}
