package com.autonavi.indoor.render3D.route;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.im.mapcore.IMFloorInfo;
import com.amap.api.im.util.IMLog;
import com.amap.api.im.view.IMIndoorMapFragment;
import com.autonavi.indoor.render3D.R;
import com.autonavi.indoor.render3D.util.Constant;


public class PoiSelectFragment extends Fragment implements OnClickListener{
	public final static String KEY_POI = "key_SearchFragment_poi";
	public final static int POI_INFO_FROM_KEY = 1;
	public final static int POI_INFO_TO_KEY = 2;

	private PoiInfo mSingleSnapPoi;
	private ListView mList;
	private FloorAdapter mFloorAdapter;
	private TextView mTextPoi;
	public IMFloorInfo mCurrentFloor;

	private IMIndoorMapFragment mIndoorMapFragment = null;
	private PathFragment mPathFragment = null;

	private int mPoiInfoKey = POI_INFO_FROM_KEY;

	public static PoiSelectFragment newInstance(Context context, IMFloorInfo mCurrentFloor)
	{
		PoiSelectFragment retNewFragment = new PoiSelectFragment();
		retNewFragment.mCurrentFloor = mCurrentFloor;
		return retNewFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onStart() {
		super.onStart();


	}
	
	@Override
	public void onStop() {
		super.onStop();
		//getMapInterface().clearMarkers();
	}
	
	private void clearSingleSnapPoi() {
		if (mSingleSnapPoi != null) {
			//getMapInterface().removeMarker(mSingleSnapPoi.cell);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view  = inflater.inflate(R.layout.indoor_route_search, null);
		mList = (ListView)view.findViewById(R.id.list_floors);
		//List<IMFloorInfo> floorInfoList = mIndoorMapFragment.getCurrentFloorInfoList();
		IMLog.logd("#######-------- onCreateView:");
		//mFloorAdapter = new FloorAdapter(floorInfoList, getActivity());
		//mList.setAdapter(mFloorAdapter);
		view.findViewById(R.id.btn_back).setOnClickListener(this);
		view.findViewById(R.id.title).setOnClickListener(this);
		view.findViewById(R.id.btn_sure).setOnClickListener(this);
		//mTextPoi = (TextView)view.findViewById(R.id.text_search);
//		mList.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				FloorInfo data = (FloorInfo) arg1.getTag();
//				mCurrentFloor = data;
//				getMapInterface().loadMapFloor(data.fl_index);
//
//			}
//
//		});
		return view;
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			finish(null);
			break;
		case R.id.title:
			//onSelectFloor();
			break;
		case R.id.btn_sure:
			btnSure();
			break;
		}
	}

	public void onSelectFloor(){
		if (mList.getVisibility() == View.VISIBLE) {
			mList.setVisibility(View.INVISIBLE);
		} else {
			mList.setVisibility(View.VISIBLE);
		}
	}
	
	public void btnSure(){
		//Bundle bundle = new  Bundle();
		//bundle.putSerializable(KEY_POI, mSingleSnapPoi);

		PoiInfo tmpPoiInfo = new PoiInfo();
		tmpPoiInfo.PoiInfoType = Constant.TYPE_ROUTE_PLANNING_POI;
		String curSelectId = mIndoorMapFragment.getCurrentSelectSourceId();
		if (curSelectId == null || curSelectId.equals("")) {
			new AlertDialog.Builder(mIndoorMapFragment.getActivity())
					.setTitle("提示")
					.setMessage("未选择目标位置!")
					.setIcon(
							android.R.drawable.ic_dialog_alert)
					.setPositiveButton("确定", null).show();
			return;
		}
		int floorNo = mIndoorMapFragment.getCurrentFloorNo();
		IMFloorInfo tmpFloorInfo = new IMFloorInfo(floorNo, "");
		PoiMapCell tmpPoiMapCell = new PoiMapCell();
		tmpPoiMapCell.setFloorNo(floorNo);
		tmpPoiMapCell.setPoiId(curSelectId);
		tmpPoiMapCell.setName(curSelectId);
		tmpPoiInfo.cell = tmpPoiMapCell;
		tmpPoiInfo.floor = tmpFloorInfo;

		if (mPoiInfoKey == POI_INFO_FROM_KEY) {
			mPathFragment.setPoiInfoFrom(tmpPoiInfo);
		} else {
			mPathFragment.setPoiInfoTo(tmpPoiInfo);
		}

		finish(null);
	}


	public void finish(Bundle bundle) {
//        BackListener back = getBackListener();
//        if (back != null) {
//            back.onFragmentBackResult(bundle, mRequestCode, this);
//        }
//        popStack();

		FragmentTransaction transcation = getActivity().getSupportFragmentManager().beginTransaction();
		transcation.setCustomAnimations(0, 0, 0,0);
		transcation.hide(mIndoorMapFragment);
		transcation.hide(this);
		transcation.show(mPathFragment);
		transcation.commit();

	}

	public void setIndoorMapFragment(IMIndoorMapFragment indoorMapFragment) {
		this.mIndoorMapFragment = indoorMapFragment;
	}

	public void setPathFragment(PathFragment pathFragment) {
		this.mPathFragment = pathFragment;
	}

	public int getPoiInfoKey() {
		return mPoiInfoKey;
	}

	public void setPoiInfoKey(int poiInfoKey) {
		this.mPoiInfoKey = poiInfoKey;
	}
}
