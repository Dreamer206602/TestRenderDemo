package com.autonavi.indoor.render3D.listener;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public interface BackListener {
	public void onFragmentBackResult(Bundle bundle, int requstCode, Fragment from);
}
