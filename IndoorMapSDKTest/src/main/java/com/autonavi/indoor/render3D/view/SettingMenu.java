/**
 * 
 */
package com.autonavi.indoor.render3D.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.im.listener.IMMapLoadListener;
import com.amap.api.im.mapcore.IMFloorInfo;
import com.amap.api.im.util.IMLog;
import com.amap.api.im.view.IMIndoorMapFragment;
import com.autonavi.indoor.render3D.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qingyangli
 *
 */
public class SettingMenu {

	private enum TestInterfaceEnmu {
		CLEAR_ALL_DATA,
		CLEAR_HILIGHT,
		CLEAR_LOCATION_POS,
		CLEAR_ROUTE_RESULT,
		CLEAR_ROUTE_START,
		CLEAR_ROUTE_STOP,
		CLEAR_SEARCH_RESULT,
		CLEAR_SELECTED,

		GET_BUILDING_ID,
		GET_FLOOR_INFO_LIST,
		GET_FLOOR_NO,
		GET_SELECTED_SOURCE_ID,
		GET_ICON_DIR,
		GET_STYLE_DIR,
		GET_MAP_ROTATION,
		GET_SCALE_LEN,
		GET_SCALR_NUM,
		GET_SCALE_UNIT,

		AMAP_LOGO,
		COMPASS_VIEW,
		FLOOR_VIEW,
		SCALE_VIEW,
		ZOOM_VIEW,

		SHOVE,
		MOVE,
		ROTATE,
		SCALE,
		ALL_GESTURE,

		RESET_MAP,
		RESET_COMPASS,
		RESET_MAP_INCLINE,
		RESET_MAP_MOVE,
		RESET_MAP_ROTATE,
		RESET_MAP_SCALE,
		RESET_SCALE,

		SET_DATA_PATH,
		SET_FEATURE_CENTER,
		SET_FEATURE_HILIGHT,
		SET_GESTURE_ENABLE,
		SET_LOC_VIEW_ENABLE,
		SET_SHOVE_ENABLE,
		SET_ROUTE_START,
		SET_ROUTE_STOP,
		SET_SCALE_UNIT,

	};


	public SettingMenu(Context context) {
		this.mContext = context;
		mBuildingMenu = new AlertDialog.Builder(context);
		mInterfaceMenu = new AlertDialog.Builder(context);
		initSettingMenu();
	}

	private static final String TAG = "SettingMenu";

	LinearLayout mLinear;
	List<String> mMenuStringList;

	private Context mContext = null;
	
	private AlertDialog.Builder mBuildingMenu = null;

	private AlertDialog.Builder mInterfaceMenu = null;

	private IMIndoorMapFragment mIndoorMapFragment = null;

	private MainActivity mMainActivity = null;

	private IMMapLoadListener mMapLoadListener = null;

	//private Map

	// 测试列表类
	private List<EnableInfo> mTestInterfaceList = new ArrayList<EnableInfo>();

	//修改列表
	private List<String> mBuildingList = new ArrayList<String>();
	
	private static final int SWITCH_BUILDING = 0;
	private static final int ROUTE_PLANNING = 1;
	private static final int TEST_INTERFACE = 2;
	//private static final int TEST_RANDOM = 3;
	private static final int ABOUT_INDODRRENDER3D = 3;

	private void initSettingMenu() {

		//init Main menu
		mMenuStringList = new ArrayList<String>();
		mMenuStringList.add("切换建筑物");
		mMenuStringList.add("路径规划");
		mMenuStringList.add("指定接口测试");
		//mMenuStringList.add("随机测试");
		mMenuStringList.add("关于定位SDK Demo");

		String[] stringArray = (String[])mMenuStringList.toArray(new String[0]);

		mBuildingMenu.setItems(stringArray, mBuildingMenuOnClickListener);

		initBuildingMenu();
		initTestInterfaceMenu();

	}

	private void initBuildingMenu() {
		//请在线申请建筑物数据，建筑物数据申请详情请参阅：http://lbs.amap.com/console/apply/
		mBuildingList.add("请输入建筑物ID, 建筑物描述");

	}

	private void initTestInterfaceMenu() {

		mTestInterfaceList.add(new EnableInfo("获得当前建筑物ID", "", TestInterfaceEnmu.GET_BUILDING_ID));
		mTestInterfaceList.add(new EnableInfo("获得楼层信息列表", "", TestInterfaceEnmu.GET_FLOOR_INFO_LIST));
		mTestInterfaceList.add(new EnableInfo("获得当前楼层号", "", TestInterfaceEnmu.GET_FLOOR_NO));
		mTestInterfaceList.add(new EnableInfo("获得已选中的ID", "", TestInterfaceEnmu.GET_SELECTED_SOURCE_ID));
		mTestInterfaceList.add(new EnableInfo("获得旋转角度", "", TestInterfaceEnmu.GET_MAP_ROTATION));
		mTestInterfaceList.add(new EnableInfo("获得缩放长度", "", TestInterfaceEnmu.GET_SCALE_LEN));
		mTestInterfaceList.add(new EnableInfo("获得缩放显示数字", "", TestInterfaceEnmu.GET_SCALR_NUM));
		mTestInterfaceList.add(new EnableInfo("获得缩放单位", "", TestInterfaceEnmu.GET_SCALE_UNIT));

		mTestInterfaceList.add(new EnableInfo("Amap LOGO", true, TestInterfaceEnmu.AMAP_LOGO));
		mTestInterfaceList.add(new EnableInfo("显示罗盘", true, TestInterfaceEnmu.COMPASS_VIEW));
		mTestInterfaceList.add(new EnableInfo("显示楼层控件", true, TestInterfaceEnmu.FLOOR_VIEW));
		mTestInterfaceList.add(new EnableInfo("显示标尺控件", true, TestInterfaceEnmu.SCALE_VIEW));
		mTestInterfaceList.add(new EnableInfo("显示缩放控件", true, TestInterfaceEnmu.ZOOM_VIEW));

		mTestInterfaceList.add(new EnableInfo("平推", true, TestInterfaceEnmu.SHOVE));
		mTestInterfaceList.add(new EnableInfo("移动", true, TestInterfaceEnmu.MOVE));
		mTestInterfaceList.add(new EnableInfo("旋转", true, TestInterfaceEnmu.ROTATE));
		mTestInterfaceList.add(new EnableInfo("缩放", true, TestInterfaceEnmu.SCALE));
		mTestInterfaceList.add(new EnableInfo("所有手势", true, TestInterfaceEnmu.ALL_GESTURE));


		InterfaceAdapter adapter = new InterfaceAdapter(mContext, mTestInterfaceList);
		mInterfaceMenu.setAdapter(adapter, mInterfaceMenuOnClickListener);

	}
	
	private OnClickListener mBuildingMenuOnClickListener = new OnClickListener() {
		
		@Override
	    public void onClick(DialogInterface view, int pos)
	    {
			
	        // TODO 自动生成的方法存根
	        if (pos == SWITCH_BUILDING) {
	        	showCompileSetting(SWITCH_BUILDING, "切换建筑物");

	        } else if (pos == ROUTE_PLANNING) {
				mMainActivity.btnGoHere();

			} else if (pos == TEST_INTERFACE) {
				mInterfaceMenu.show();
			//} else if (pos == TEST_RANDOM) {

			} else if (pos == ABOUT_INDODRRENDER3D) {
	        	AlertDialog.Builder builder2=new AlertDialog.Builder(mContext);
	            builder2.setTitle("关于定位SDK Demo");
	            builder2.setMessage("定位SDK Demo \nversion: " + mIndoorMapFragment.getVersion() +
							"\nsub version: " + mIndoorMapFragment.getSubVersion());
	            builder2.setPositiveButton("确定",new OnClickListener(){

	                public void onClick(DialogInterface dialog, int which)
	                {
	                    // TODO 自动生成的方法存根
	                    dialog.dismiss();
	                    
	                }
	            });
	            builder2.show();
	        }
	        
	        view.dismiss();
	    }
	};


	private OnClickListener mInterfaceMenuOnClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface view, int pos)
		{
			EnableInfo enableInfo = mTestInterfaceList.get(pos);

			// TODO 自动生成的方法存根
			if (enableInfo.type == TestInterfaceEnmu.SHOVE) {
				mIndoorMapFragment.setMapInclineEnable(enableInfo.getReverseEnable());
				enableInfo.makeToast();

			} else if (enableInfo.type == TestInterfaceEnmu.MOVE) {
				mIndoorMapFragment.setMapTranslateEnable(enableInfo.getReverseEnable());
				enableInfo.makeToast();
			} else if (enableInfo.type == TestInterfaceEnmu.ROTATE) {
				mIndoorMapFragment.setMapRotateEnable(enableInfo.getReverseEnable());
				enableInfo.makeToast();
			} else if (enableInfo.type == TestInterfaceEnmu.SCALE) {
				mIndoorMapFragment.setMapScaleEnable(enableInfo.getReverseEnable());
				enableInfo.makeToast();
			} else if (enableInfo.type == TestInterfaceEnmu.ALL_GESTURE) {
				mIndoorMapFragment.setGestureEnable(enableInfo.getReverseEnable());
				enableInfo.makeToast();
			} else if (enableInfo.type == TestInterfaceEnmu.AMAP_LOGO) {
				boolean isShow = enableInfo.getReverseEnable();
				if (isShow) {
					mIndoorMapFragment.showAmapLogo();
				} else {
					mIndoorMapFragment.hideAmapLogo();
				}
				enableInfo.makeToast();
			} else if (enableInfo.type == TestInterfaceEnmu.COMPASS_VIEW) {
				boolean isShow = enableInfo.getReverseEnable();
				if (isShow) {
					mIndoorMapFragment.showCompassView();
				} else {
					mIndoorMapFragment.hideCompassView();
				}
				enableInfo.makeToast();
			} else if (enableInfo.type == TestInterfaceEnmu.FLOOR_VIEW) {
				boolean isShow = enableInfo.getReverseEnable();
				if (isShow) {
					mIndoorMapFragment.showFloorView();
				} else {
					mIndoorMapFragment.hideFloorView();
				}
				enableInfo.makeToast();
			} else if (enableInfo.type == TestInterfaceEnmu.SCALE_VIEW) {
				boolean isShow = enableInfo.getReverseEnable();
				if (isShow) {
					mIndoorMapFragment.showPlottingScale();
				} else {
					mIndoorMapFragment.hidePlottingScale();
				}
				enableInfo.makeToast();
			} else if (enableInfo.type == TestInterfaceEnmu.ZOOM_VIEW) {
				boolean isShow = enableInfo.getReverseEnable();
				if (isShow) {
					mIndoorMapFragment.showZoomView();
				} else {
					mIndoorMapFragment.hideZoomView();
				}
				enableInfo.makeToast();

			} else if (enableInfo.type == TestInterfaceEnmu.GET_BUILDING_ID) {
				Toast.makeText(mIndoorMapFragment.getActivity(), "建筑物ID:" +
						mIndoorMapFragment.getCurrentBuildingId(),
						Toast.LENGTH_SHORT).show();
			} else if (enableInfo.type == TestInterfaceEnmu.GET_FLOOR_INFO_LIST) {
				List<IMFloorInfo> floorList = mIndoorMapFragment.getCurrentFloorInfoList();
				String showStr = "";
				for (IMFloorInfo tmpInfo: floorList) {
					showStr += tmpInfo.getFloorNo() + ", ";
					showStr += tmpInfo.getFloorNona() + ", ";
					showStr += tmpInfo.getFloorName() + "\n";
				}

				Toast.makeText(mIndoorMapFragment.getActivity(), showStr,
						Toast.LENGTH_LONG).show();
			} else if (enableInfo.type == TestInterfaceEnmu.GET_FLOOR_NO) {
				Toast.makeText(mIndoorMapFragment.getActivity(), "floor no:" + mIndoorMapFragment.getCurrentFloorNo(),
						Toast.LENGTH_SHORT).show();
			} else if (enableInfo.type == TestInterfaceEnmu.GET_SELECTED_SOURCE_ID) {
				Toast.makeText(mIndoorMapFragment.getActivity(), "选中SourceID:" +
						mIndoorMapFragment.getCurrentSelectSourceId(),
						Toast.LENGTH_SHORT).show();
			} else if (enableInfo.type == TestInterfaceEnmu.GET_MAP_ROTATION) {
				Toast.makeText(mIndoorMapFragment.getActivity(), "旋转角度:" +
								mIndoorMapFragment.getMapRotation(),
						Toast.LENGTH_SHORT).show();
			} else if (enableInfo.type == TestInterfaceEnmu.GET_SCALE_LEN) {
				Toast.makeText(mIndoorMapFragment.getActivity(), "标尺长度单元:" +
								mIndoorMapFragment.getPlottingScaleLength(),
						Toast.LENGTH_SHORT).show();
			} else if (enableInfo.type == TestInterfaceEnmu.GET_SCALR_NUM) {
				Toast.makeText(mIndoorMapFragment.getActivity(), "标尺显示数字:" +
								mIndoorMapFragment.getPlottingScaleNumber(),
						Toast.LENGTH_SHORT).show();
			} else if (enableInfo.type == TestInterfaceEnmu.GET_SCALE_UNIT) {
				Toast.makeText(mIndoorMapFragment.getActivity(), "标尺显示单位:" +
								mIndoorMapFragment.getPlottingScaleUnit(),
						Toast.LENGTH_SHORT).show();


			//} else if (enableInfo.type == TestInterfaceEnmu.SCALE) {

			}

			view.dismiss();
		}
	};

	/**
	 * 显示Wifi编译设置
	 */
	private void showCompileSetting(final int compileType, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //builder.setIcon(R.drawable.collapsed);
        builder.setTitle(title);

		String[] stringArray = (String[])mBuildingList.toArray(new String[0]);

		builder.setItems(stringArray, mSwitchFloorOnClickListener);

        builder.show();
	}

	private OnClickListener mSwitchFloorOnClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface view, int pos)
		{

			IMLog.logd("#######-------- onClick :" + mBuildingList.get(pos)
					+ ", id:" + Thread.currentThread().getId());

			String buildingId = mBuildingList.get(pos).split(",")[0];

			boolean isStartLoad = mIndoorMapFragment.loadMap(buildingId, mMapLoadListener);

			if (!isStartLoad) {
				Toast.makeText(mIndoorMapFragment.getActivity(), "已有地图正在加载中,加载失败!",
						Toast.LENGTH_LONG).show();
			}

			view.dismiss();
		}
	};


	
	public AlertDialog show() {
		return mBuildingMenu.show();
	}

	public void setIndoorMapFragment(IMIndoorMapFragment indoorMapFragment) {
		this.mIndoorMapFragment = indoorMapFragment;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mMainActivity = mainActivity;
	}

	/**
	 * 字符串后加开启状态
	 * @param oriStr
	 * @param enable
	 * @return
	 */
	private String addEnableText(String oriStr, boolean enable) {
		String enableEndStr;

		if (enable) {
			enableEndStr = "(启用)";
		} else {
			enableEndStr = "(停用)";
		}
		return oriStr + enableEndStr;
	}

	public class InterfaceAdapter extends BaseAdapter
	{
		private Activity context = null;
		public List<Application> list = null;

		private Context mContext;
		private LayoutInflater mInflater;
		private List<EnableInfo> mInterfaceList;

		public InterfaceAdapter(Context context, List<EnableInfo> interfaceList) {
			mContext = context;
			mInterfaceList = interfaceList;
		}

		public List<EnableInfo> getInterfaceList() {
			return mInterfaceList;
		}

		@Override
		public int getCount() {
			return mInterfaceList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				TextView floorNameText = new TextView(mContext);

				convertView = floorNameText;
			}

			TextView mFloorNameText=(TextView)convertView;
			EnableInfo editInfo = mInterfaceList.get(position);
			String showText;
			if (editInfo.value == null) {
				showText = addEnableText(editInfo.text, editInfo.enable);
			} else {
				showText = editInfo.text;
			}

			mFloorNameText.setText(showText);

			return convertView;
		}

	}


	class EnableInfo {

		public EnableInfo() {
		}

		public EnableInfo(String text, boolean enable, TestInterfaceEnmu type) {
			this.text = text;
			this.enable = enable;
			this.type = type;
		}

		public EnableInfo(String text, String value, TestInterfaceEnmu type) {
			this.text = text;
			this.value = value;
			this.type = type;
		}

		public String text;
		public boolean enable;
		public String value = null;
		public boolean input;
		public TestInterfaceEnmu type;

		public boolean getReverseEnable() {
			enable = !enable;
			return enable;
		}

		public void makeToast() {
			Toast.makeText(mIndoorMapFragment.getActivity(), text + ":" + enable,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void setMapLoadListener(IMMapLoadListener mapLoadListener) {
		this.mMapLoadListener = mapLoadListener;
	}
}
