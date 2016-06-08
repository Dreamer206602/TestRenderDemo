package com.autonavi.indoor.render3D.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.autonavi.indoor.render3D.R;
import com.autonavi.indoor.render3D.listener.BackListener;


public class MapBaseFragment extends Fragment implements BackListener {
	private BackListener mBack;
	private int mRequestCode = 0;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static final MapBaseFragment newInstance(Context context,
													 BackListener from)
	{
		MapBaseFragment retNewFragment = new MapBaseFragment();
		retNewFragment.mContext = context;
		return retNewFragment;
	}


	public BackListener getBackListener() {
		return mBack;
	}

	public Context getAliMapContext() {
		return mContext;
	}

	//protected MapState mMapState;

	@Override
	public void onStop() {
		super.onStop();
		//mMapState = getMapInterface().saveState();
	}

	@Override
	public void onStart() {
		super.onStart();
//		if (mMapState != null) {
//			getMapInterface().restoreState(mMapState);
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private View mView;
	private FrameLayout frame;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (frame != null && mView != null) {
			frame.removeView(mView);
		}
		frame = new FrameLayout(getActivity());
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		frame.setLayoutParams(params);
		if (mView == null) {
			mView = onCreateView(inflater, container);
			if (mView != null) {
				View v = mView.findViewById(R.id.topview);
				if (v != null) {
					v.setOnClickListener(new EmptyClick());
				}
			}
		}
		if (mView != null) {
			frame.addView(mView);
		}
		frame.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return interceptTouch(arg0, arg1);
			}
		});
		return frame;
	}

	public boolean interceptTouch(View arg0, MotionEvent arg1) {
		return false;
	}

	private static class EmptyClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {

		}

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		return null;
	}

	//public IndoorMapView getMapInterface() {
//		return mAliMapContext.getMapInterface();
//	}

//	public void setMapData(IndoorDataManager mapData) {
//		mAliMapContext.setMapData(mapData);
//	}

	public boolean popStack() {
		return getFragmentManager().popBackStackImmediate();
	}

	public boolean onBackPressed() {
		return false;
	}

	protected void showFragment(Fragment fragment, int containerId,
			boolean backstack) {
		showFragment(fragment, 0, 0, 0, 0, containerId, 0, backstack);
	}

	private void setRequestCode(int code) {
		mRequestCode = code;
	}

	protected void showFragment(Fragment fragment, int containerId,
			int requestCode, boolean backstack) {
		showFragment(fragment, 0, 0, 0, 0, containerId, requestCode, backstack);
	}

	public void showFragment(Fragment fragment, int pageEnterAnim,
			int pageExitAnim, int popEnter, int popEixt, int containerId,
			boolean backstack) {
		showFragment(fragment, pageEnterAnim, pageExitAnim, popEnter, popEixt,
				containerId, 0, backstack);
	}

	public void showFragment(Fragment fragment, int pageEnterAnim,
			int pageExitAnim, int popEnter, int popEixt, int containerId,
			int requestCode, boolean backstack) {
		if (fragment instanceof MapBaseFragment) {
			((MapBaseFragment) fragment).setRequestCode(requestCode);
		}
		//getMapInterface().clearMarkers();
		FragmentTransaction transcation = getFragmentManager()
				.beginTransaction();
		transcation.setCustomAnimations(pageEnterAnim, pageExitAnim, popEnter,
				popEixt);
		transcation.replace(containerId, fragment);
		if (backstack) {
			transcation.addToBackStack(null);
		}
		transcation.commit();
	}

	public void finish(Bundle bundle) {
		BackListener back = getBackListener();
		if (back != null) {
			back.onFragmentBackResult(bundle, mRequestCode, this);
		}
		popStack();
	}

	@Override
	public void onFragmentBackResult(Bundle bundle, int requestCode,
			Fragment from) {

	}

}
