package com.autonavi.indoor.render3D.route;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.im.data.IMDataManager;
import com.amap.api.im.data.IMRoutePlanning;
import com.amap.api.im.listener.IMRoutePlanningListener;
import com.amap.api.im.listener.RoutePLanningStatus;
import com.amap.api.im.mapcore.IMFloorInfo;
import com.amap.api.im.mapcore.IMPoint;
import com.amap.api.im.util.IMLog;
import com.amap.api.im.view.IMIndoorMapFragment;
import com.autonavi.indoor.render3D.R;
import com.autonavi.indoor.render3D.util.Constant;

/**
 * 路算
 * @author minghui.wang
 *
 */
public class PathFragment extends Fragment implements OnClickListener{
	private final static int REQUEST_CODE_POIFROM = 1001;
	private final static int REQUEST_CODE_POITO = 1002;
	Button mBtnFrom;
	Button mBtnTo;
	PoiSelectFragment mPoiSelectFragment;
	private IMIndoorMapFragment mIndoorMapFragment = null;
	private EditText mSearchEditText = null;
	private ImageView mLocationView = null;
	PoiInfo mInfoFrom;
	PoiInfo mInfoTo;
	ProgressDialog mProgressDialog;

	private IMPoint mLocationPoint = null;
	private String mLocationBdId = "";

	public static final PathFragment newInstance(Context context, IMIndoorMapFragment indoorMapFragment)
	{
		PathFragment retNewFragment = new PathFragment();
		retNewFragment.mPoiSelectFragment = PoiSelectFragment.newInstance(context,new IMFloorInfo(1, "0",""));
		retNewFragment.mIndoorMapFragment = indoorMapFragment;
		return retNewFragment;
	}

	public void setPoiInfoFrom(){
		setPoiInfoFrom(null);
	}

	public void setPoiInfoFrom(PoiInfo mInfoFrom){
		if(mInfoFrom==null){
			this.mInfoFrom =  loadMylocation();
		}else{
			this.mInfoFrom=mInfoFrom;
		}
		refesh();
	}
	public void setPoiInfoTo(PoiInfo mInfoTo){
		if (mInfoTo == null) {
			return;
		}
		this.mInfoTo = mInfoTo;

		refesh();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mPoiSelectFragment = PoiSelectFragment.newInstance(getActivity(), new IMFloorInfo(1, "0",""));

		View view = View.inflate(getActivity(), R.layout.indoor_route_main, null);
		mBtnFrom = (Button)view.findViewById(R.id.btn_poifrom);
		mBtnTo = (Button)view.findViewById(R.id.btn_poito);
		view.findViewById(R.id.btn_back).setOnClickListener(this);
		view.findViewById(R.id.btn_search).setOnClickListener(this);
		view.findViewById(R.id.btn_mypoifrom).setOnClickListener(this);
		view.findViewById(R.id.btn_mypoito).setOnClickListener(this);
		view.findViewById(R.id.btn_back).setOnClickListener(this);
		mBtnFrom.setOnClickListener(this);
		mBtnTo.setOnClickListener(this);
//		mInfoFrom =  loadMylocation();
		refesh();
		return view;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		clearPath();
	}
	
	private void clearPath(){
//		List<FloorData> list = getAliMapContext().getMapData().floorList;
//		for(FloorData data:list){
//			getMapInterface().setPathData(new ArrayList<PointD>(), data.fl_index);
//		}
	}

	private void refesh(){
		if (mBtnFrom == null && mBtnTo == null) {
			return;
		}

		if(mInfoFrom!=null&& mInfoFrom.cell!=null){
			mBtnFrom.setText("  " + mInfoFrom.cell.getName());
		}
		if(mInfoTo!=null&&mInfoTo.cell!=null){
			mBtnTo.setText("  " + mInfoTo.cell.getName());
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_poifrom:
			btnFrom();
			break;
		case R.id.btn_poito:
			btnTo();
			break;
		case R.id.btn_search:
			btnSearch();
			break;
		case R.id.btn_back:
			btnBack();
			break;
		case R.id.btn_mypoifrom:
			mInfoFrom =  loadMylocation();
			refesh();
			break;
		case R.id.btn_mypoito:
			mInfoTo =  loadMylocation();
			refesh();
			break;	
		}
	}

	/**
	 * 加载指定的位置
	 */
	private PoiInfo loadMylocation() {
		String curBdId = mIndoorMapFragment.getCurrentBuildingId();
		if (mLocationPoint == null || mLocationBdId == null || !mLocationBdId.equals(curBdId)) {
			new AlertDialog.Builder(mIndoorMapFragment.getActivity())
					.setTitle("提示")
					.setMessage("没有定位结果,无法使用定位位置!")
					.setIcon(
							android.R.drawable.ic_dialog_alert)
					.setPositiveButton("确定", null).show();
			return null;
		}

		PoiInfo info = new PoiInfo();
		info.PoiInfoType = Constant.TYPE_ROUTE_PLANNING_LOCATION;
		String namecode="F1";

		info.cell = new PoiMapCell(0 ,mLocationPoint.getX(), mLocationPoint.getY(),
				mLocationPoint.getZ(), "我的位置");
		info.floor = new IMFloorInfo(mLocationPoint.getZ(), namecode, "-1");
		return info;
	}
	
	public void btnFrom(){
		 //showFragment(mPoiSelectFragment, R.id.aliglmap_container,REQUEST_CODE_POIFROM, true);
		mPoiSelectFragment.setPoiInfoKey(PoiSelectFragment.POI_INFO_FROM_KEY);
		btnShowPoiSelectFragment();
	}
	
	
	public void btnTo(){
		//showFragment(mPoiSelectFragment, R.id.aliglmap_container,REQUEST_CODE_POITO,true);
		mPoiSelectFragment.setPoiInfoKey(PoiSelectFragment.POI_INFO_TO_KEY);
		btnShowPoiSelectFragment();
	}

	private void btnShowPoiSelectFragment() {
		FragmentTransaction transcation = getActivity().getSupportFragmentManager().beginTransaction();
		transcation.setCustomAnimations(0, 0, 0,0);

		if (!mPoiSelectFragment.isAdded()) {
			transcation.hide(this).add(R.id.indoor_main_view, mPoiSelectFragment);
			mPoiSelectFragment.setIndoorMapFragment(mIndoorMapFragment);
			mPoiSelectFragment.setPathFragment(this);
		} else {
			transcation.hide(this).show(mPoiSelectFragment);
		}

		transcation.show(mIndoorMapFragment);
		transcation.commit();
	}

	public void onFragmentBackResult(Bundle bundle,int requestCode,Fragment from) {
		if(bundle!=null){
			PoiInfo  poiInfo = (PoiInfo)bundle.getSerializable(PoiSelectFragment.KEY_POI);
			if(poiInfo!=null){
				if(requestCode==REQUEST_CODE_POIFROM){
					mInfoFrom = poiInfo;
					Log.v("Path", mInfoFrom.toString());
				}else if(requestCode==REQUEST_CODE_POITO){
					mInfoTo = poiInfo;
					Log.v("Path", mInfoTo.toString());
				}
//				Log.v("Path", mInfoFrom.toString());
				refesh();
			}
		}
//		Toast.makeText(getActivity(), "fragment back", Toast.LENGTH_LONG).show();
	}
	
	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		} else {
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
				mProgressDialog.setCancelable(false);
			}
		}
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}
	}
	
	private void reFreshMap(int idx, RoutePathData routeInfoData){
		//getMapInterface().setPathData(routeInfoData);
		mBtnFrom.post(new Runnable() {
			@Override
			public void run() {
				//btnBack();
			}
		});
		
	}
	
	public void btnSearch(){
		if(mInfoFrom==null){
			Toast.makeText(getActivity(), "请选择起始点", Toast.LENGTH_LONG).show();
			return;
		}
		if(mInfoTo==null){
			Toast.makeText(getActivity(), "请选择终点", Toast.LENGTH_LONG).show();
			return;
		}

		IMRoutePlanning routePlanning = new IMRoutePlanning(this.getActivity(),
				mRoutePlanningListener);
		String buildingId = IMDataManager.getInstance().getCurrentBuildingId();

		PoiMapCell fromMapCell = mInfoFrom.cell;
		PoiMapCell toMapCell = mInfoTo.cell;

		IMLog.logd("####### ------ from PoiInfoType:" + mInfoFrom.PoiInfoType);
		IMLog.logd("####### ------ to PoiInfoType:" + mInfoTo.PoiInfoType);

		if (mInfoFrom.PoiInfoType == Constant.TYPE_ROUTE_PLANNING_LOCATION) {
			routePlanning.excutePlanningPointToPoi(buildingId, fromMapCell.getFloorNo(),
					fromMapCell.getX(), fromMapCell.getY(), toMapCell.getPoiId());
			IMLog.logd("####### ------ start Point2Poi");
			return;
		}

		if (mInfoTo.PoiInfoType == Constant.TYPE_ROUTE_PLANNING_LOCATION) {

			routePlanning.excutePlanningPoiToPoint(buildingId, fromMapCell.getPoiId(),
					toMapCell.getFloorNo(), toMapCell.getX(), toMapCell.getY());
			IMLog.logd("####### ------ start Poi2Point");
			return;
		}

		if (mInfoTo.PoiInfoType == Constant.TYPE_ROUTE_PLANNING_POI &&
				mInfoFrom.PoiInfoType == Constant.TYPE_ROUTE_PLANNING_POI) {
			routePlanning.excutePlanningPoiToPoi(buildingId, fromMapCell.getPoiId(),
					toMapCell.getPoiId());
			IMLog.logd("####### ------ start Poi2Poi");
		}



	}


	/**
	 * 路算回调接口
	 */
	private IMRoutePlanningListener mRoutePlanningListener = new IMRoutePlanningListener() {
		@Override
		public void onPlanningSuccess(String routePlanningData) {
			// TODO Auto-generated method stub
			IMLog.logd("#######-------- planning success id:" + Thread.currentThread().getId());
			Toast.makeText(getActivity(), "路算成功", Toast.LENGTH_LONG).show();
			mIndoorMapFragment.clearRouteStart();
			mIndoorMapFragment.clearRouteStop();
			mIndoorMapFragment.clearSelected();
			mIndoorMapFragment.clearHighlight();
			setRouteStartAndStop(routePlanningData);
			mIndoorMapFragment.refreshMap();
			finish(null);

		}

		@Override
		public void onPlanningFailure(RoutePLanningStatus statusCode) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "路算失败,失败码:" + statusCode, Toast.LENGTH_LONG).show();
			IMLog.logd("#######-------- planning failure errorCode:" + statusCode + ", id:" +
					Thread.currentThread().getId());
		}
	};


	public String getFrom()
	{
		return "autonavi";
	}

	public void btnBack(){

		finish(null);
	}

	/**
	 * 设置路径规划开始点和停止点
	 */
	public void setRouteStartAndStop(String routePlanningData) {
		String fromPoiId = mInfoFrom.cell.getPoiId();
		String toPoiId = mInfoTo.cell.getPoiId();
		IMLog.logd("####### ------ from:" + fromPoiId + ", to:" + toPoiId);

		if (fromPoiId != null && !fromPoiId.equals("")) {
			mIndoorMapFragment.setRouteStart(fromPoiId);
		}

		if (toPoiId != null && !toPoiId.equals("")) {
			mIndoorMapFragment.setRouteStop(toPoiId);
		}

		mIndoorMapFragment.setRouteData(routePlanningData);
	}

	/**
	 * 关闭页面
	 * @param bundle
	 */
	public void finish(Bundle bundle) {
//        BackListener back = getBackListener();
//        if (back != null) {
//            back.onFragmentBackResult(bundle, mRequestCode, this);
//        }
//        popStack();

		FragmentTransaction transcation = getActivity().getSupportFragmentManager().beginTransaction();
		transcation.setCustomAnimations(0, 0, 0,0);
		transcation.hide(this).show(mIndoorMapFragment);
		transcation.commit();
		mSearchEditText.setVisibility(View.VISIBLE);
		mSearchEditText.bringToFront();
		mLocationView.setVisibility(View.VISIBLE);
		mLocationView.bringToFront();
	}

	/**
	 * 是否处于路算Poi选择界面下
	 * @return
	 */
	public boolean isPoiSelect() {
		if (mPoiSelectFragment.isVisible()) {
			return true;
		}
		return false;
	}

	public void clear() {
		mInfoFrom = null;
		mInfoTo = null;
		if (mBtnFrom != null) {
			mBtnFrom.setText("   选择起点");
		}

		if (mBtnTo != null) {
			mBtnTo.setText("   选择终点");
		}
	}

	/**
	 * 设置定位位置
	 * @param locationPoint
	 */
	public void setLocationPoint(IMPoint locationPoint) {
		this.mLocationPoint = locationPoint;
	}

	public void setLocationBdId(String locationBdId) {
		this.mLocationBdId = locationBdId;
	}

	public void setSearchEditText(EditText searchEditText) {
		this.mSearchEditText = searchEditText;
	}

	public void setLocationView(ImageView locationView) {
		this.mLocationView = locationView;
	}
}
