package com.autonavi.indoor.render3D.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.im.data.IMDataManager;
import com.amap.api.im.mapcore.IMSearchResult;
import com.amap.api.im.util.IMUtils;
import com.amap.api.im.view.IMIndoorMapFragment;
import com.autonavi.indoor.render3D.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liqingyang on 16/2/18.
 */
public class IMSearchFragment extends Fragment implements OnClickListener,
        OnTouchListener {

    public final static String KEY_TYPE = "key_Search_type";
    public final static String KEY_TYPE_PUB = "key_Search_pub";
    public final static String KEY_TYPE_KEY = "key_Search_key";
    public final static String KEY_REQUEST = "key_Search_request";

    private final static String TYPE_KEY_ICON_ID = "icon_id";
    private final static String TYPE_KEY_ICON = "icon";
    private final static String TYPE_KEY_NAME = "name";

    private Context mContext;
    private ProgressDialog mLoadingDialog;
    private View mSearchView;
    private ImageButton mSearchBack;
    private TextView mSearchText;

    private LinearLayout mBussinessLayout;
    private TextView mBussinessTextView;
    private IMGridView mBussinessGridView;
    private LinearLayout mPubLayout;
    private IMGridView mServiceGridView;
    private View mSearchResultView;
    private View view;
    // 搜索结果列表
    private ImageButton mSearchResultBack;
    private IMSearchEditText mSearchEditText;
    private ImageButton mSearchClearBtn;
    private TextView mSearchResultTips;
    private ListView mSearchListView;
    private IMSearchListAdapter mListViewAdapter;
;
    private IMIndoorMapFragment mMapFragment;
    private IMDataManager mDataManager = null;
    private List<String> mShopTypes=new ArrayList<String>();

    private EditText mMainSearchEditText;
    private ImageView mMainLocationView = null;

    private Button mSettingButton = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                initGridView();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        view = View.inflate(getActivity(), R.layout.activity_indoor_search, null);
        mContext = getActivity();

        // 搜索分类
        initSearchTopBar();
        // 搜索结果列表
        initSearchResultView();
        initGridView();
        return view;

    }

    public static final IMSearchFragment newInstance(Context context,
                                                     IMIndoorMapFragment fragment,
                                                     Bundle bundle)
    {
        IMSearchFragment retNewFragment = new IMSearchFragment();
        retNewFragment.mMapFragment = fragment;
        //fragment.setArguments(bundle);
        return retNewFragment;
    }


    public IMSearchFragment() {

    }

    /**
     * @Title: initSearchTopBar
     * @Description: 初始化室内搜索顶部Bar
     */
    private void initSearchTopBar() {
        mSearchView = view.findViewById(R.id.indoor_search_view);

        mSearchBack = (ImageButton) mSearchView
                .findViewById(R.id.indoor_search_category_btn_back);
        mSearchBack.setOnClickListener(this);

        mSearchText = (TextView) mSearchView
                .findViewById(R.id.indoor_search_category_textview);
        mSearchText.setOnClickListener(this);
    }

    /**
     * @Title: initGridView
     * @Description:初始化室内GridView
     */
    private void initGridView() {
        dismissLoadingDialog();
//        mShopTypes.clear();
//        pubTypes.clear();
        // 初始化精品商家
        mBussinessLayout = (LinearLayout) mSearchView
                .findViewById(R.id.indoor_bussiness_layout);

        mBussinessTextView = (TextView) mSearchView
                .findViewById(R.id.indoor_bussiness_textview);

        mBussinessGridView = (IMGridView) mSearchView
                .findViewById(R.id.indoor_business_gridview);

        refreshGridView();

    }

    public void refreshGridView() {

        List<String> allSearchTypeList = mDataManager.getAllSearchType();
        for (String type : allSearchTypeList) {
            mShopTypes.add(type);
        }

        if (null != mShopTypes && mShopTypes.size() > 0) {
            List<HashMap<String, Object>> mShopTypeList = new ArrayList<HashMap<String, Object>>();
            for (String type : allSearchTypeList) {
                String iconName = type.toLowerCase();
                String typeName = type;
                int iconId = this.getResources()
                        .getIdentifier("indoor_" + iconName, "drawable",
                                getActivity().getPackageName());
                if (iconId > 0) {
                    HashMap<String, Object> resHashMap = new HashMap<String, Object>();
                    resHashMap.put(TYPE_KEY_ICON_ID, iconId);
                    resHashMap.put(TYPE_KEY_ICON, iconName);
                    resHashMap.put(TYPE_KEY_NAME, typeName);
                    mShopTypeList.add(resHashMap);
                }
            }

            SimpleAdapter bussinessAdapter = new SimpleAdapter(mContext,
                    mShopTypeList, R.layout.indoor_gridview_item, new String[]{
                    TYPE_KEY_ICON_ID, TYPE_KEY_ICON}, new int[]{
                    R.id.gridview_item_image, R.id.gridview_item_text});

            mBussinessGridView.setAdapter(bussinessAdapter);
            mBussinessGridView
                    .setOnItemClickListener(mBussinessItemClickListener);

            // if (mDataManager.getIndoorBuilding().mAutoNaviType / 10000 == 6)
            // {// 购物类建筑
            // mBussinessTextView.setText("精品商家");
            // } else {
            // mBussinessTextView.setText("服务分类");
            // }
            mBussinessTextView.setText("服务分类");
            mBussinessLayout.setVisibility(View.VISIBLE);
        } else {
            mBussinessLayout.setVisibility(View.GONE);
        }
    }

    /**
     * @Title: initSearchResultView
     * @Description: 初始化室内搜索结果列表视图
     */
    private void initSearchResultView() {
        mSearchResultView = view.findViewById(R.id.indoor_search_result_view);

        mSearchResultBack = (ImageButton) mSearchResultView
                .findViewById(R.id.indoor_search_result_btn_back);

        mSearchResultBack.setOnClickListener(this);

        mSearchEditText = (IMSearchEditText) mSearchResultView
                .findViewById(R.id.indoor_search_edittext);

        setEditTextEventListener();

        mSearchClearBtn = (ImageButton) mSearchResultView
                .findViewById(R.id.indoor_search_edittext_clear_btn);
        mSearchClearBtn.setOnClickListener(this);

        mSearchResultTips = (TextView) mSearchResultView
                .findViewById(R.id.indoor_search_result_tip);
        mSearchListView = (ListView) mSearchResultView
                .findViewById(R.id.indoor_search_result_list);
        mSearchListView.setOnTouchListener(this);
        mSearchListView.setOnItemClickListener(mSearchListItemClickListener);

        mListViewAdapter = new IMSearchListAdapter(mContext, true);
        mSearchListView.setAdapter(mListViewAdapter);
    }


    /**
     * 点击商家监听事件:跳到搜索结果列表显示
     */
    private OnItemClickListener mBussinessItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            HashMap<String, Object> itemMap = (HashMap<String, Object>) parent
                    .getAdapter().getItem(position);
            if (null != itemMap && itemMap.get(TYPE_KEY_NAME) != null) {
                String type = itemMap.get(TYPE_KEY_NAME).toString();
                //ClassInfo shopType = (ClassInfo) itemMap.get("obj");
                //mSearchResultView.setVisibility(View.VISIBLE);

                List<String> featureList = new ArrayList<String>();

                List<IMSearchResult> srList = mDataManager.searchByType(type);
                for (IMSearchResult sr: srList) {
                    if (sr.getFloorNo() == mMapFragment.getCurrentFloorNo()) {
                        featureList.add(sr.getId());
                    }
                }

                if (featureList.size() == 0) {
                    Toast.makeText(getContext(), "当前楼层没有类型为" + type + "的商铺", Toast.LENGTH_SHORT).show();
                    mMapFragment.clearSearchResult();
                } else {
                    mMapFragment.selectSearchResultList(featureList);
                    mMapFragment.setFeatureCenter(featureList);
                }

                finish(null);
                mMapFragment.refreshMapAnimated();

            }
        }
    };

    /**
     * 点击公共设施监听事件:直接回到地图高亮显示
     */
    private OnItemClickListener mServiceItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            HashMap<String, Object> itemMap = (HashMap<String, Object>) parent
                    .getAdapter().getItem(position);
            if (itemMap == null) {
                return;
            }
        }
    };

    /**
     * 搜索结果列表每项点击事件：回到地图高亮显示
     */
    private OnItemClickListener mSearchListItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            IMSearchResult result = mListViewAdapter.getItem(position);

            int curFloorNo = mMapFragment.getCurrentFloorNo();

            // 不在当前层,则切换楼层
            if (curFloorNo != result.getFloorNo()) {
                mMapFragment.switchFloorByFloorNo(result.getFloorNo());

            }

            mMapFragment.selectSearchResult(result.getId());

            mMapFragment.refreshMapAnimated();

            finish(null);

        }
    };

    /**
     * @param keywords
     * @Title: loadSearchListByKeywords
     * @Description: 加载搜索关键字列表
     */
    private void loadSearchListByKeywords(final String keywords) {


        List<IMSearchResult> srList = IMDataManager.getInstance()
                .search(keywords);
        if (null != srList) {
            mListViewAdapter.notifyListByKeywords(keywords, srList);
            if (srList.size() == 0) {
                mSearchResultTips.setVisibility(View.VISIBLE);
                mSearchListView.setVisibility(View.GONE);
            } else {
                mSearchResultTips.setVisibility(View.GONE);
                mSearchListView.setVisibility(View.VISIBLE);
            }
        } else {
            mSearchResultTips.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @Title: showSoftInput
     * @Description: 显示输入键盘
     */
    private void showSoftInput() {
        mSearchEditText.setFocusable(true);
        mSearchEditText.setFocusableInTouchMode(true);
        mSearchEditText.requestFocus();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mSearchEditText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mSearchEditText, 0);
            }
        }, 100);
    }

    /**
     * @Title: setEditTextEventListener
     * @Description: 设置EditText监听事件
     */
    private void setEditTextEventListener() {
        mSearchEditText
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            String keywords = mSearchEditText.getText()
                                    .toString().trim();
                            if (!TextUtils.isEmpty(keywords)) {
                                loadSearchListByKeywords(mSearchEditText
                                        .getText().toString().trim());
                                IMUtils.hideInputMethod(mContext, v);
                            }

                            return true;
                        }

                        return false;
                    }
                });

        mSearchEditText
                .setTextWatcherEventListener(new IMSearchEditTextWatcherListener() {
                    private int selectionStart;
                    private int selectionEnd;

                    @Override
                    public void afterTextChanged(View view, Editable s) {
                        selectionStart = mSearchEditText.getSelectionStart();
                        selectionEnd = mSearchEditText.getSelectionEnd();

                        if (s.length() > 0) {
                            mSearchClearBtn.setVisibility(View.VISIBLE);
                        } else if (s.length() == 0) {
                            mSearchClearBtn.setVisibility(View.GONE);
                        } else if (s.length() > 20) {
                            s.delete(selectionStart - 1, selectionEnd);
                            int tempSelection = selectionEnd;
                            mSearchEditText.setText(s);
                            mSearchEditText.setSelection(tempSelection);// 设置光标在最后
                        }


                    }

                    @Override
                    public void beforeTextChanged(View view, CharSequence s,
                                                  int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(View view, CharSequence s,
                                              int start, int before, int count) {

                        final String keywords = s.toString().trim();
                        if ("".equals(keywords)) {
                            if (mSearchEditText.isFocused()) {
                                mSearchListView.setVisibility(View.GONE);
                            }
                        } else {
                            if (mSearchEditText.isFocused()) {
                                loadSearchListByKeywords(keywords);
                            }
                        }


                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.indoor_search_category_btn_back:// 分类搜索返回按钮
                finish(null);
                break;

            case R.id.indoor_search_category_textview:// 点击顶部搜索TextView

                mSearchResultView.setVisibility(View.VISIBLE);
                mSearchListView.setVisibility(View.GONE);
                showSoftInput();

                break;

            case R.id.indoor_search_result_btn_back:// 搜索结果列表返回按钮

                mSearchEditText.setText("");
                IMUtils.hideInputMethod(mContext, mSearchEditText);
                mSearchResultView.setVisibility(View.GONE);
                mSearchResultTips.setVisibility(View.GONE);

                break;

            case R.id.indoor_search_edittext_clear_btn:// EditText清空按钮
                mSearchEditText.setText("");
                mSearchClearBtn.setVisibility(View.GONE);

                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.indoor_search_result_list:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    IMUtils.hideInputMethod(mContext, view);
                }

                return false;
        }

        return false;
    }

    //@Override
    public boolean onBackPressed() {
        // getMapInterface().onDestroy();
        // manager.free();

        // }
        // @Override
        // public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        // {
        if (mSearchResultView.getVisibility() == View.VISIBLE) {
            if (mListViewAdapter != null) {
                mListViewAdapter.clearData();
            }

            mSearchEditText.setText("");
            IMUtils.hideInputMethod(mContext, mSearchEditText);
            mSearchResultView.setVisibility(View.GONE);
            mSearchResultTips.setVisibility(View.GONE);
            return true;
        }

        //return super.onBackPressed();
        return true;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshGridView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//
//		this.destroySearchResource();
//	}

    private void destroySearchResource() {
        if (mListViewAdapter != null) {
            mListViewAdapter.clearData();
        }
    }

    private void dismissLoadingDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }


    public void finish(Bundle bundle) {
//        BackListener back = getBackListener();
//        if (back != null) {
//            back.onFragmentBackResult(bundle, mRequestCode, this);
//        }
//        popStack();

        FragmentTransaction transcation = getActivity().getSupportFragmentManager().beginTransaction();
        transcation.setCustomAnimations(0, 0, 0,0);

        transcation.hide(this).show(mMapFragment)
                .commit();
        mMainSearchEditText.setVisibility(View.VISIBLE);
        mSettingButton.setVisibility(View.VISIBLE);
        mMainLocationView.setVisibility(View.VISIBLE);

    }

    public void setDataManager(IMDataManager dataManager) {
        this.mDataManager = dataManager;
    }

    public void setMainSearchEditText(EditText searchEditText) {
        this.mMainSearchEditText = searchEditText;
    }

    public void setSettingButton(Button settingButton) {
        this.mSettingButton = settingButton;
    }

    public void setMainLocationView(ImageView mainLocationView) {
        this.mMainLocationView = mainLocationView;
    }
}
