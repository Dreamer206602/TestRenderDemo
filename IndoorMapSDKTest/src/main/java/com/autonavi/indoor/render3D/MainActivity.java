package com.autonavi.indoor.render3D;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.im.data.IMDataManager;
import com.amap.api.im.listener.DownloadStatusCode;
import com.amap.api.im.listener.IMDataDownloadListener;
import com.amap.api.im.listener.IMMapEventListener;
import com.amap.api.im.listener.IMMapLoadListener;
import com.amap.api.im.listener.IMRoutePlanningListener;
import com.amap.api.im.listener.MapLoadStatus;
import com.amap.api.im.listener.RoutePLanningStatus;
import com.amap.api.im.mapcore.IMFloorInfo;
import com.amap.api.im.mapcore.IMPoint;
import com.amap.api.im.util.IMLog;
import com.amap.api.im.view.IMIndoorGLMapView;
import com.amap.api.im.view.IMIndoorMapFragment;
import com.autonavi.indoor.constant.Configuration;
import com.autonavi.indoor.constant.MessageCode;
import com.autonavi.indoor.entity.LocationResult;
import com.autonavi.indoor.location.ILocationManager;
import com.autonavi.indoor.onlinelocation.OnlineLocator;
import com.autonavi.indoor.render3D.route.PathFragment;
import com.autonavi.indoor.render3D.route.PoiInfo;
import com.autonavi.indoor.render3D.route.PoiMapCell;
import com.autonavi.indoor.render3D.search.IMSearchFragment;
import com.autonavi.indoor.render3D.util.Constant;
import com.autonavi.indoor.render3D.view.SettingMenu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private IMIndoorGLMapView mIndoorGLMapView;
    private boolean rendererSet;

    private IMIndoorMapFragment mIndoorMapFragment = null;
    private IMSearchFragment mSearchFragment = null;
    private PathFragment mPathFragment = null;

    private EditText mSearchEditText = null;

    private IMDataManager mDataManager = null;

    private SettingMenu mSettingMenu = null;

    private Context mContext = null;

    private boolean mLocationStatus = false;                // 定位按钮状态


    private IMPoint mLastPoint = null;

    private String mLastSelectedPoiId = null;

    private ImageView mLocationView = null;

    private RelativeLayout mLocatingLayout = null;

    private Button mSettingButton = null;

    // 去这里Tips变量
    private View mBottomViewDetail = null;
    private TextView mTextPoi;
    private TextView mTextPoiDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this.getApplicationContext();
        IMLog.setDebug(true);

        IMLog.logd("#######  --------------------------   App start   ---------------------------  #######");

        final boolean supportsEs2 = IMIndoorMapFragment.isSupportsOpenGlEs2(this);

        //IMIndoorMapFragment.setStyleDirectory("im_style");
        //IMIndoorMapFragment.setIconDirectory("im_icon");

        if (supportsEs2) {

            setContentView(R.layout.activity_main);
            mIndoorMapFragment =  (IMIndoorMapFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.indoor_main_map_view);

            mSettingMenu = new SettingMenu(this);
            mSettingMenu.setIndoorMapFragment(mIndoorMapFragment);
            mSettingMenu.setMainActivity(this);
            mSettingMenu.setMapLoadListener(mMapLoadListener);


        } else {
            // Should never be seen in production, since the manifest filters
            // unsupported devices.
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        initLocationButton();

        initOtherUI();

        initSettingButton();

        initRoutePlanningFragment();

        L.d("##start!");
        // 渲染数据下载
//        //IMLog.setDebug(true);
        mDataManager = IMDataManager.getInstance();
//        mDataManager.setDataPath(Environment.getExternalStorageDirectory() + "/test_data");
//        mDataManager.downloadBuildingData(mBuildingId, mDataDownloadListener);

        //mDataManager.setDataPath(Environment.getExternalStorageDirectory() + "/test_data");

        mIndoorMapFragment.setDataPath(Environment.getExternalStorageDirectory() + "/test_data");

        //请在线申请建筑物数据，建筑物数据申请详情请参阅：http://lbs.amap.com/console/apply/
        mIndoorMapFragment.loadMap("请输入建筑物ID", mMapLoadListener);


//        IMLog.logd("#######-------- onClick version:" + mIndoorMapFragment.getVersion()
//                + " , subVersion:" + mIndoorMapFragment.getSubVersion());

        mIndoorMapFragment.setMapEventListener(mMapEventListener);



    }


    /**
     * init UI
     */
    private void initOtherUI() {

        // init Search editText
        mSearchEditText = (EditText) findViewById(R.id.indoor_search_edittext);
        mSearchEditText.setBackgroundColor(Color.WHITE);
        mSearchEditText.getBackground().setAlpha(150);
        mSearchEditText.setText("  请输入要搜索的内容...");
        mSearchEditText.setTextColor(Color.GRAY);
        mSearchEditText.setFocusable(false);
        mSearchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mIndoorMapFragment.getActivity(), "选中搜索框",
//                        Toast.LENGTH_LONG).show();
                if (mSearchFragment == null) {
                    mSearchFragment = IMSearchFragment.newInstance(
                            MainActivity.this, mIndoorMapFragment, null);
                    mSearchFragment.setDataManager(mDataManager);
                    mSearchFragment.setMainSearchEditText(mSearchEditText);
                    mSearchFragment.setSettingButton(mSettingButton);
                    mSearchFragment.setMainLocationView(mLocationView);
                }

                FragmentTransaction transcation = getSupportFragmentManager().beginTransaction();

                transcation.setCustomAnimations(0, 0, 0,0);

                if (!mSearchFragment.isAdded()) {
                    transcation.hide(mIndoorMapFragment).add(R.id.indoor_main_view, mSearchFragment);
                } else {
                    transcation.hide(mIndoorMapFragment).show(mSearchFragment);
                    mSearchFragment.refreshGridView();
                }
                transcation.commit();

                mSearchEditText.setVisibility(View.GONE);
                mSettingButton.setVisibility(View.GONE);
                mLocationView.setVisibility(View.GONE);
            }
        });


        // init Path fragment
        mPathFragment = PathFragment.newInstance(this, mIndoorMapFragment);
        mPathFragment.setSearchEditText(mSearchEditText);
        mPathFragment.setLocationView(mLocationView);
    }


    /**
     * 初始化定位按钮
     */
    private void initLocationButton() {

        mLocationView = (ImageView)findViewById(R.id.locating_btn);

        mLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMLog.logd("#######-------- onClick ");

                if (!mLocationStatus) {         // 处理开始定位
                    startLocating();
                    mLocationView.setImageResource(R.drawable.indoor_gps_locked);
                    mLocationStatus = true;
                    mFirstCenter = false;
                } else {                    // 处理结束定位
                    stopLocating();
                    mLocationView.setImageResource(R.drawable.indoor_gps_unlocked);
                    mIndoorMapFragment.clearLocatingPosition();
                    mIndoorMapFragment.clearLocationOnFloorView();
                    mIndoorMapFragment.refreshMap();
                    mLocationStatus = false;
                }

            }
        });
    }

    /**
     * 初始化设置按钮
     */
    private void initSettingButton() {
        mSettingButton = (Button)findViewById(R.id.route_btn);

        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingMenu.show();


            }
        });
    }

    /**
     * 初始化路算试图
     */
    private void initRoutePlanningFragment() {

        mBottomViewDetail = findViewById(R.id.bottom_view_detail);
        mTextPoi = (TextView) findViewById(R.id.text_poi);
        mTextPoiDetail = (TextView) findViewById(R.id.text_poi_detail);
        Button btnGohere = (Button) findViewById(R.id.btn_gohere);
        btnGohere.setOnClickListener(this);
    }

    /**
     * 点击去这里按钮
     */
    public void btnGoHere() {

//        if (mLastSelectedPoiId == null) {
//            new AlertDialog.Builder(mIndoorMapFragment.getActivity())
//                    .setTitle("提示")
//                    .setMessage("未选择要到达的点!")
//                    .setIcon(
//                            android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton("确定", null).show();
//            return;
//        }

        mPathFragment.clear();
        PoiInfo mInfoTo = new PoiInfo();
        mInfoTo.PoiInfoType = Constant.TYPE_ROUTE_PLANNING_POI;
        PoiMapCell tmpPoiMapCell = new PoiMapCell();
        if (mLastSelectedPoiId != null && !mLastSelectedPoiId.equals("")) {
            tmpPoiMapCell.setPoiId(mLastSelectedPoiId);
            tmpPoiMapCell.setName(mLastSelectedPoiId);
        } else {
            tmpPoiMapCell.setPoiId(mLastSelectedPoiId);
            tmpPoiMapCell.setName("选择终点");
        }
        mInfoTo.cell = tmpPoiMapCell;
        mInfoTo.floor = new IMFloorInfo(mIndoorMapFragment.getCurrentFloorNo(), "", "0");
        mPathFragment.setPoiInfoFrom();
        mPathFragment.setPoiInfoTo(mInfoTo);

        mSearchEditText.setVisibility(View.GONE);
        mLocationView.setVisibility(View.GONE);

        FragmentTransaction transcation = getSupportFragmentManager().beginTransaction();

        transcation.setCustomAnimations(0, 0, 0,0);

        if (!mPathFragment.isAdded()) {
            transcation.hide(mIndoorMapFragment).add(R.id.indoor_main_view, mPathFragment)
                    .commit();
        } else {
            transcation.hide(mIndoorMapFragment).show(mPathFragment)
                    .commit();
        }


    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 到这里按键
            case R.id.btn_gohere:
                //IMLog.logd("#######-------- onClick ");
                btnGoHere();
                mBottomViewDetail.setVisibility(View.GONE);
                mSearchEditText.setVisibility(View.GONE);
                break;
            default:

                break;
        }

    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        // 获取手机当前音量值
        switch (keyCode) {
            // 音量减小
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                //IMLog.logd("#######-------- onClick ");
                mSettingMenu.show();
                return true;
            // 音量增大
            case KeyEvent.KEYCODE_VOLUME_UP:
                mSettingMenu.show();
                return true;
            case KeyEvent.KEYCODE_BACK:
                exit();
                return true;
        }
        return super.onKeyDown (keyCode, event);
    }

    /**
     * 地图加载回调接口
     */
    private IMMapLoadListener mMapLoadListener = new IMMapLoadListener() {

        @Override
        public void onMapLoadSuccess() {
            Toast.makeText(mIndoorMapFragment.getActivity(), "地图加载完毕",
                    Toast.LENGTH_LONG).show();

            initLocating(Configuration.LocationProvider.WIFI);

        }

        @Override
        public void onMapLoadFailure(MapLoadStatus mapLoadStatus) {
            IMLog.logd("#######-------- onMapLoadFailure:" + mapLoadStatus + ", id:" + Thread.currentThread().getId());
            Toast.makeText(mIndoorMapFragment.getActivity(), "地图加载失败,失败状态:" + mapLoadStatus,
                    Toast.LENGTH_LONG).show();
        }

    };

    /**
     * 下载回调接口
     */
    private IMDataDownloadListener mDataDownloadListener = new IMDataDownloadListener() {

        @Override
        public void onDownloadSuccess(String buildingId) {
            // TODO Auto-generated method stub
            IMLog.logd("#######-------- download success:" + buildingId + ", id:" + Thread.currentThread().getId());
            //mDataManager.loadBuildingData();

        }

        @Override
        public void onDownloadFailure(String buildingId, DownloadStatusCode statusCode) {
            // TODO Auto-generated method stub
            IMLog.logd("#######-------- download failure:" + buildingId + ", errorCode:" +
                    statusCode + ", id:" + Thread.currentThread().getId());
        }

        @Override
        public void onDownloadProgress(String buildingId, float progress) {
            // TODO Auto-generated method stub
            IMLog.logd("####### building:" + buildingId + ", progress:" + progress + ", id:" + Thread.currentThread().getId());

        }

    };

    /**
     * 路算回调接口
     */
    private IMRoutePlanningListener mRoutePlanningListener = new IMRoutePlanningListener() {
        @Override
        public void onPlanningSuccess(String routePlanningData) {
            // TODO Auto-generated method stub
            IMLog.logd("#######-------- planning success id:" + Thread.currentThread().getId());

            mIndoorMapFragment.refreshMap();

        }

        @Override
        public void onPlanningFailure(RoutePLanningStatus statusCode) {
            // TODO Auto-generated method stub
            IMLog.logd("#######-------- planning failure errorCode:" + statusCode + ", id:" +
                    Thread.currentThread().getId());
        }
    };

    private IMMapEventListener mMapEventListener = new IMMapEventListener() {
        @Override
        public void onFloorChange(int floorNo) {
            ///IMLog.logd("#######-------- onFloorChange id:" + Thread.currentThread().getId());
        }

        @Override
        public void onSelectedPoi(String poiId) {

            IMLog.logd("#######-------- onSelectedShop:" + poiId + " id:" + Thread.currentThread().getId());

            if (poiId != null && !poiId.equals("")) {
                mLastSelectedPoiId = poiId;

                List<String> list = new ArrayList<String>();
                list.add(poiId);

                mIndoorMapFragment.selectFeature(poiId);
                mIndoorMapFragment.setFeatureHighlight(poiId);
                //mIndoorMapFragment.setFeatureCenter(poiId);

                Toast.makeText(mContext, "PoiId:" + poiId , Toast.LENGTH_SHORT).show();
            } else {
                // 取消选择
//                mIndoorMapFragment.clearSelected();
//                mIndoorMapFragment.clearHighlight();
                mLastSelectedPoiId = "";
            }

//            // 再路算选点界面下,不显示去这里按钮
//            if (mPathFragment == null || !mPathFragment.isPoiSelect()) {
//                mBottomViewDetail.setVisibility(View.VISIBLE);
//                mTextPoi.setText(poiId);
//            }
        }

        @Override
        public void onSingleTap(double lng, double lat) {
            IMLog.logd("#######-------- onSingleTap lng:" + lng + ", lat:" + lat
                    + " id:" + Thread.currentThread().getId());
            IMPoint cvtPoint = mIndoorMapFragment.convertCoordinateToScreen(lng, lat);
            IMLog.logd("#######-------- onSingleTap posX:" + cvtPoint.getX() + ", poxY:" + cvtPoint.getY()
                    + " id:" + Thread.currentThread().getId());
        }

        @Override
        public void onDoubleTap() {
            //IMLog.logd("#######-------- onDoubleTap id:" + Thread.currentThread().getId());
            mIndoorMapFragment.zoomIn();
        }

        @Override
        public void onLongPress() {
            //IMLog.logd("#######-------- onLongPress id:" + Thread.currentThread().getId());
        }

        @Override
        public void onInclineBegin() {
            //IMLog.logd("#######-------- onShoveBegin id:" + Thread.currentThread().getId());
        }

        @Override
        public void onIncline(float centerX, float centerY, float shoveAngle) {
//            IMLog.logd("#######-------- onShove x:" + centerX + ", y:" + centerY + ", value:"
//                    + shoveAngle);
        }

        @Override
        public void onInclineEnd() {
            //IMLog.logd("#######-------- onShoveEnd id:" + Thread.currentThread().getId());
        }

        @Override
        public void onScaleBegin() {
            //IMLog.logd("#######-------- onScaleBegin id:" + Thread.currentThread().getId());
        }

        @Override
        public void onScale(float focusX, float focusY, float scaleValue) {
//            IMLog.logd("#######-------- onRotate x:" + focusX + ", y:" + focusY + ", value:"
//                    + scaleValue);
        }

        @Override
        public void onScaleEnd() {
            //IMLog.logd("#######-------- onScaleEnd id:" + Thread.currentThread().getId());
        }

        @Override
        public void onTranslateBegin() {
            //IMLog.logd("#######-------- onTranslateBegin id:" + Thread.currentThread().getId());
        }

        @Override
        public void onTranslate(float transX, float transY) {
//            IMLog.logd("#######-------- onTranslate x:" + transX + ", y:" + transY);
        }

        @Override
        public void onTranslateEnd() {
            //IMLog.logd("#######-------- onTranslateEnd id:" + Thread.currentThread().getId());
        }

        @Override
        public void onRotateBegin() {
            //IMLog.logd("#######-------- onRotateBegin id:" + Thread.currentThread().getId());
        }

        @Override
        public void onRotate(float centerX, float centerY, float rotateAngle) {
//            IMLog.logd("#######-------- onRotate x:" + centerX + ", y:" + centerY + ", value:"
//                    + rotateAngle);
        }

        @Override
        public void onRotateEnd() {
            //IMLog.logd("#######-------- onRotateEnd id:" + Thread.currentThread().getId());
        }
    };



    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet) {
            mIndoorGLMapView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rendererSet) {
            mIndoorGLMapView.onResume();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public Context getContext() {
        return mContext;
    }

    private boolean isExit = false;
    private void exit() {
        if(!isExit) {
            isExit = true;
            Toast.makeText(this, "在按一次退出程序", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }


    // ---------------------       定位SDK调用       -----------------------------
    boolean mIsManagerInited = false;
    boolean mIsLocating = false;
    SDKInitHandler mSDKInitHandler = new SDKInitHandler(this);;
    com.autonavi.indoor.constant.Configuration.Builder mConfigBuilder = null;
    private final InnerHandler mInnerHandler = new InnerHandler(this);
    public int mLastLocatedFloorNO = -99999;
    private ILocationManager mLocationManager = null;
    //ILocationManager mLocationManager;
    private static long mLastLocationTime = 0;
    private static int mLocationIntervalTime = 1000;
    private static boolean mLocationIntervalFlag = true;
    private boolean mFirstCenter = false;                       // 第一次定位居中

    public void initLocating(Configuration.LocationProvider provider) {
        IMLog.logd("#######init " + provider);

        //VERSION 5.5
        mConfigBuilder = new Configuration.Builder(mContext);
        String key = "";
        try {
            ApplicationInfo appInfo = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);

            key = appInfo.metaData.getString("indoormap3d_key");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "定位Lbs Key错误", Toast.LENGTH_SHORT).show();
        }

        mConfigBuilder.setLBSParam(key);
        //mConfigBuilder.setSqlitePath(Environment.getExternalStorageDirectory() + "/autonavi/indoor/indoor_db.db");

//        //离线定位
//        mLocationManager = com.autonavi.indoor.location.LocationManager.getInstance();			//混合定位
//        //mConfigBuilder.setServer(Configuration.ServerType.SERVER_AOS, "");
//        mConfigBuilder.setLocationMode(Configuration.LocationMode.OFFLINE);

        //在线定位
		mLocationManager = OnlineLocator.getInstance();				//在线定位

		//mConfigBuilder.setServer(Configuration.ServerType.SERVER_AOS, "");
		//mConfigBuilder.setLocationMode(Configuration.LocationMode.AUTO);

        mSDKInitHandler = new SDKInitHandler(this);
        mConfigBuilder.setLocationProvider(provider);		//Configuration.LocationProvider.WIFI

        mLocationManager.init("",
                mConfigBuilder.build(), mSDKInitHandler);

//        mLocationManager.init(mIndoorMapFragment.getCurrentBuildingId(),
//                mConfigBuilder.build(), mSDKInitHandler);

    }


    private void destroyLocating(){
        IMLog.logd("#######destroy");
        if (mLocationManager != null) {
            IMLog.logd("#######destroy in");
            mLocationManager.destroy();
            mIsManagerInited = false;
            mLocationManager = null;
        }
    }

    void startLocating(){
        IMLog.logd("#######start");
        if (mIsManagerInited && !mLocationStatus && mLocationManager != null) {
            IMLog.logd("#######start in");

            //try {
            mLocationManager.requestLocationUpdates(mInnerHandler);
            //} catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            //	e.printStackTrace();
            //}

            //mIsLocating = true;
        }
    }
    void stopLocating(){
        IMLog.logd("#######stop");
        if (mIsManagerInited && mLocationManager != null) {
            IMLog.logd("#######stop in");
            mLocationManager.removeUpdates(mInnerHandler);
            mIsLocating = false;
        }
    }

    private static class SDKInitHandler extends Handler {
        private final WeakReference<MainActivity> mParent;
        public SDKInitHandler(MainActivity parent) {
            mParent = new WeakReference<MainActivity>(parent);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity parent = mParent.get();
            if (parent == null ) {
                IMLog.logd("#######parent == null");
                return;
            }
//			//parent.mIsLocating = false;
//			L.d("mIsLocating = false");
            if (msg.what == MessageCode.MSG_THREAD_PREPARED){
                IMLog.logd("#######Initialize LocationManager with Configuration");
                if (parent.mLocationManager == null) {
                    return;
                }

                parent.mIsManagerInited = true;
                //parent.mLocationManager.requestLocationUpdates(parent.mInnerHandler);
                parent.mIsLocating = true;
                //parent.start();
                IMLog.logd("#######mIsLocating = true");
            }
            else if (msg.what == MessageCode.MSG_WIFI_NOT_ENABLED ){
                Toast.makeText(parent.getContext(), "请先打开wifi", Toast.LENGTH_SHORT).show();
            }else if (msg.what == MessageCode.MSG_WIFI_NOT_PERMITTED ){
                Toast.makeText(parent.getContext(), "wifi没有授权", Toast.LENGTH_SHORT).show();
            }else if (msg.what == MessageCode.MSG_BLE_NOT_PERMITTED ){
                Toast.makeText(parent.getContext(), "BLE没有授权", Toast.LENGTH_SHORT).show();
            } else if (msg.what == MessageCode.MSG_BLE_NOT_ENABLED ){
                Toast.makeText(parent.getContext(), "请先打开BLE", Toast.LENGTH_SHORT).show();
            }else if (msg.what == MessageCode.MSG_SENSOR_MISSING ){
                Toast.makeText(parent.getContext(), "手机缺少步导需要的传感器：加速度、磁力计、重力计等", Toast.LENGTH_SHORT).show();
            }else if (msg.what == MessageCode.MSG_NETWORK_ERROR ){
                Toast.makeText(parent.getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }else if (msg.what == MessageCode.MSG_NETWORK_NOT_SATISFY){
                Toast.makeText(parent.getContext(), "当前网络和用户设置的不符，不能下载数据", Toast.LENGTH_SHORT).show();
            }else if (msg.what == MessageCode.MSG_SERVER_ERROR){
                Toast.makeText(parent.getContext(), "服务器端错误", Toast.LENGTH_SHORT).show();
            } else {
                IMLog.logd("#######error!");
            }
        }
    };

    private static class InnerHandler extends Handler{
        private final WeakReference<MainActivity> mParent;
        public InnerHandler(MainActivity parent) {
            mParent = new WeakReference<MainActivity>(parent);
        }
        @Override
        public void handleMessage(Message msg)
        {
            MainActivity mParent = this.mParent.get();
            if (mParent == null) {
                IMLog.logd("#######2.00 parent is NULL");
                return;
            }
            switch (msg.what) {
                case -1: {
                    IMLog.logd("#######2.00");
                    break;
                }
                case MessageCode.MSG_REPORT_ONLINE_LOCATION: {
                    onLocated(msg, true);
                    break;
                }
                case MessageCode.MSG_REPORT_LOCATION: {
                    onLocated(msg, false);
                    break;
                }
                case MessageCode.MSG_SENSOR_MISSING: {
                    Toast.makeText(mParent.getContext(), "手机缺少步导需要的传感器：加速度、磁力计、重力计等", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MessageCode.MSG_BLE_NO_SCAN: {
                    Toast.makeText(mParent.getContext(), "一段时间内没有蓝牙扫描", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MessageCode.MSG_WIFI_NO_SCAN: {
                    Toast.makeText(mParent.getContext(), "一段时间内没有WIFI扫描", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MessageCode.MSG_NETWORK_ERROR: {
                    Toast.makeText(mParent.getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MessageCode.MSG_NETWORK_NOT_SATISFY: {
                    Toast.makeText(mParent.getContext(), "当前网络和用户设置的不符，不能下载数据", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MessageCode.MSG_SERVER_ERROR: {
                    Toast.makeText(mParent.getContext(), "服务器端错误", Toast.LENGTH_SHORT).show();
                    break;
                }case MessageCode.MSG_PRESSURE_CHANGED: {
                    //Toast.makeText(mParent.getContext(), "气压值改变异常", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MessageCode.MSG_REPORT_PED: {
                    break;
                }
            }
        }
        void onLocated(Message msg, boolean isOnline){
            MainActivity parent = mParent.get();
            if (parent == null)
                return;
            LocationResult result = (LocationResult)msg.obj;
            IMLog.logd("#######客户端收到定位结果：isOnline="+ isOnline + ", ("+result.x + ", " +result.y +","+ result.z +"),"+result.a);
            if (result.x == 0 && result.y == 0 && result.z == -99){
                IMLog.logd("#######无法定位 -99");
                return ;
            }
            if (result.x == -10000 || result.y == -10000 ){
                IMLog.logd("#######无法定位 -1000");
                return ;
            }
            if (result.x == 0 && result.y == 0 && result.z == -127){
                IMLog.logd("#######无法定位  -127");
                return ;
            }

            long curTime = System.currentTimeMillis();

            // 跳点模式
//            if (mLocationIntervalFlag && curTime - mLastLocationTime < mLocationIntervalTime) {
//                return;
//            }

            mLastLocationTime = curTime;

            IMLog.logd("#######lng:" + result.x + ", lat:"
                    + result.y + ", floor:"
                    + result.z + ", angel:"
                    + result.a + ", accuracy:"
                    + result.r);

            if (!result.bid.equals(parent.mIndoorMapFragment.getCurrentBuildingId())) {
                Toast.makeText(parent.getContext(), "定位结果不在当前建筑物内!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 显示定位点
            parent.mIndoorMapFragment.setLocatingPosition(result.x, result.y, result.z,
                    result.a, result.r);

            if (parent.mIndoorMapFragment.getCurrentFloorNo() != result.z) {
                parent.mIndoorMapFragment.switchFloorByFloorNo(result.z);
            }

            if (!parent.mFirstCenter) {
                parent.mIndoorMapFragment.setCoordinateCenter(result.x, result.y, (int) result.z);
                parent.mIndoorMapFragment.setCoordinateDirect(result.a);
                parent.mIndoorMapFragment.setMapIncline(-45);
                //parent.mFirstCenter = true;
            }

            // 搜索定位周围的点
//            List<IMSearchResult> searchResultList = IMDataManager
//                    .getInstance().searchByDistance(result.x, result.y, result.z, 100, 20);
//
//            List<String> featureList = new ArrayList<String>();
//            for (IMSearchResult sr: searchResultList) {
//                    featureList.add(sr.getId());
//            }
//            parent.mIndoorMapFragment.selectSearchResultList(featureList);

            // 记录定位点
            IMPoint tmpPoint = new IMPoint(result.x, result.y, result.z);
            parent.mLastPoint = tmpPoint;
            parent.mPathFragment.setLocationPoint(tmpPoint);
            parent.mPathFragment.setLocationBdId(result.bid);

            // 切层
            parent.mIndoorMapFragment.showLocationOnFloorView(result.z);

        }
    };




}
