package com.autonavi.indoor.render3D.route;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.im.mapcore.IMFloorInfo;
import com.autonavi.indoor.render3D.R;

import java.util.List;

public class FloorAdapter extends BaseAdapter{
	List<IMFloorInfo> mList;
	Context mContext;
	public FloorAdapter(List<IMFloorInfo> list,Context context) {
		this.mList = list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList==null?null:mList.get(arg0);
	}
	
	public void setFloorList(List<IMFloorInfo> list){
		mList = list;
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View view = arg1;
		if(view==null){
			view = View.inflate(mContext, R.layout.indoor_floor_element, null);
		}
		TextView text =  (TextView)view.findViewById(R.id.text_floor);
		IMFloorInfo data = mList.get(arg0);
		text.setText("" + data.getFloorName());
		view.setTag(mList.get(arg0));
		return view;
	}

}
