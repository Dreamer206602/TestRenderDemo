package com.autonavi.indoor.render3D.route;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autonavi.indoor.render3D.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PathDetailMapFragment extends Fragment implements OnClickListener{
	ArrayList<PoiInfo> mPathList;
	private int mCurrentStep;
	private View btn_prestep,btn_nextstep;
	TextView mTitle;
	TextView mPoiName;
	HashMap<Integer,ArrayList<PoiInfo>> layerPoiMap;


	public static final PathDetailMapFragment newInstance(Context context,
														   HashMap<Integer, ArrayList<PoiInfo>> layerPoiMap,
														   ArrayList<PoiInfo> pathList,
														   int step)
	{
		PathDetailMapFragment retNewFragment = new PathDetailMapFragment();
		retNewFragment.mPathList = pathList;
		retNewFragment.layerPoiMap = layerPoiMap;
		retNewFragment.mCurrentStep = step;
		return retNewFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.indoor_route_detailmap, null);
		view.findViewById(R.id.btn_back).setOnClickListener(this);
		view.findViewById(R.id.btn_prestep).setOnClickListener(this);
		view.findViewById(R.id.btn_nextstep).setOnClickListener(this);
		mTitle = (TextView)view.findViewById(R.id.text_title);
		mPoiName = (TextView)view.findViewById(R.id.text_poiname);
		btn_prestep=view.findViewById(R.id.btn_prestep);
		btn_nextstep=view.findViewById(R.id.btn_nextstep);
		refresh();
		reFreshMap(null,mPathList.get(mCurrentStep));
		return view;
	}
	
	private void refresh(){
		PoiInfo info = mPathList.get(mCurrentStep);
		String mallName = "";
//		IndoorBuilding mapData = getAliMapContext().getMapData().getIndoorBuilding();
//		if(mapData!=null){
//			mallName = mapData.currentMall.getMallName();
//		}
		mTitle.setText(mallName+info.floor.getFloorName());
		String describe = "";
		if(mCurrentStep==0){
			describe = "起点";
			btn_prestep.setEnabled(false);
		}else
		if(mCurrentStep==mPathList.size()-1){
			describe = "终点";
			btn_nextstep.setEnabled(false);
		}else {
			btn_prestep.setEnabled(true);
			btn_nextstep.setEnabled(true);
			if(mCurrentStep+1<mPathList.size()){
				describe = "路过 "+mPathList.get(mCurrentStep+1).cell.getName();
			}else if(mPathList.get(mCurrentStep).floor==mPathList.get(mCurrentStep+1).floor){
				describe = "到达"+mPathList.get(mCurrentStep).floor+"楼";
			}
		}
		mPoiName.setText(describe);
	}
	
	private void reFreshMap(PoiInfo preInfo, PoiInfo info){
		if(preInfo==null||preInfo.floor.getFloorNo()!=info.floor.getFloorNo()){
//			getMapInterface().loadMapFloor(info.floor.fl_index,new OnLoadFloorListener() {
//
//				@Override
//				public void onLoadFloorSuceess() {
//
//				}
//
//				@Override
//				public void onLoadFloorFail() {
//
//				}
//			});
//			ArrayList<PoiInfo> list = layerPoiMap.get(info.floor.fl_index);
//			ArrayList<PointD> targetList = new ArrayList<PointD>();
//			for(PoiInfo p:list){
//				PointD pd = new PointD(p.cell.getX(),p.cell.getY());
//				targetList.add(pd);
//			}
//			getMapInterface().setPathData(info.floor.fl_index,targetList);
		}else{
//			ArrayList<PoiInfo> list = layerPoiMap.get(info.floor.fl_index);
//			int indexPre = list.indexOf(preInfo);
//			int indexCur = list.indexOf(info);
//			int highStart = indexPre>indexCur?indexCur:indexPre;
//			int highEnd = indexPre>indexCur?indexPre:indexCur;
//			ArrayList<PointD> targetList = new ArrayList<PointD>();
//			for(PoiInfo p:list){
//				PointD pd = new PointD(p.cell.getX(),p.cell.getY());
//				targetList.add(pd);
//			}
//			//getMapInterface().setPathData(targetList,highStart,highEnd,info.floor.fl_index);
//			getMapInterface().setPathData(info.floor.fl_index,targetList);
		}
//		getMapInterface().setViewPortToLocation(info.cell.getX(), info.cell.getY());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			btnBack();
			break;
		case R.id.btn_prestep:
			btnPreStep();
			break;
		case R.id.btn_nextstep:
			btnNextStep();
			break;
		}
	}
	
	private void btnBack(){
		//popStack();
	}
	
	private void btnPreStep(){
		int pre = mCurrentStep-1;
		if(pre>=0){
			int preStep = mCurrentStep;
			mCurrentStep = pre;
			refresh();
			reFreshMap(mPathList.get(preStep),mPathList.get(mCurrentStep));
		}
	}
	
	private void btnNextStep(){
		int next = mCurrentStep + 1;
		if(next<mPathList.size()){
			int preStep = mCurrentStep;
			mCurrentStep = next;
			refresh();
			reFreshMap(mPathList.get(preStep),mPathList.get(mCurrentStep));
		}
	}

	public void setPathList(ArrayList<PoiInfo> pathList) {
		this.mPathList = pathList;
	}

	public void setPoiName(TextView poiName) {
		this.mPoiName = poiName;
	}

	public void setLayerPoiMap(HashMap<Integer, ArrayList<PoiInfo>> layerPoiMap) {
		this.layerPoiMap = layerPoiMap;
	}
}
