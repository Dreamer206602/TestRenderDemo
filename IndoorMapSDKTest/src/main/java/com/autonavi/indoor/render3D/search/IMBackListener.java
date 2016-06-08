package com.autonavi.indoor.render3D.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by liqingyang on 16/2/17.
 */
public interface IMBackListener {
    public void onFragmentBackResult(Bundle bundle, int requstCode, Fragment from);
}
