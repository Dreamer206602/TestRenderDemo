package com.autonavi.indoor.render3D.route;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.autonavi.indoor.render3D.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PathDetailListFragment extends Fragment implements OnClickListener{
	ListView mList;
	PathListAdapter mListAdapter;
	ArrayList<PoiInfo> mPathDetailList;
	HashMap<Integer,ArrayList<PoiInfo>> layerPoiArray;
	Context mContext = null;

	public static final PathDetailListFragment newInstance(Context context,
														   ArrayList<PoiInfo> pathDetailList,
														   HashMap<Integer,ArrayList<PoiInfo>> paths)
	{
		PathDetailListFragment retNewFragment = new PathDetailListFragment();
		retNewFragment.layerPoiArray = paths;
		retNewFragment.mContext = context;
		retNewFragment.mergeSamePoi(pathDetailList);
		return retNewFragment;
	}


	public PathDetailListFragment() {
	}

	private void mergeSamePoi(ArrayList<PoiInfo> pathDetailList){
		mPathDetailList = new ArrayList<PoiInfo>();
		PoiInfo cur_index = null;
		for(PoiInfo info:pathDetailList){
			if(cur_index==null){
				mPathDetailList.add(info);
				cur_index = info;
			}else{
				if(!cur_index.cell.getName().equals(info.cell.getName())
						|| cur_index.floor.getFloorNo() != info.floor.getFloorNo()){
					mPathDetailList.add(info);
					cur_index = info;
				}
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(mPathDetailList!=null){
			mListAdapter = new PathListAdapter(mPathDetailList, getActivity());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view  = inflater.inflate(R.layout.indoor_route_searchresult, null);
		mList = (ListView)view.findViewById(R.id.listview);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//showFragment(new PathDetailMapFragment(getAliMapContext(), PathDetailListFragment.this,layerPoiArray, mPathDetailList,arg2), R.id.aliglmap_container, true);
			}
		});
		view.findViewById(R.id.btn_back).setOnClickListener(this);
		view.findViewById(R.id.btn_map).setOnClickListener(this);
		mList.setAdapter(mListAdapter);
		return view;
	}

	@Override
	public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_back:
				btnBack();
				break;
			case R.id.btn_map:
				btnShowMap();
				break;
			}
	}
	
	public void btnBack(){
		//finish(null);
	}
	
	public void btnShowMap(){
		//showFragment(new PathDetailMapFragment(getAliMapContext(), this, layerPoiArray,mPathDetailList, 0), R.id.aliglmap_container, true);
	}

}
