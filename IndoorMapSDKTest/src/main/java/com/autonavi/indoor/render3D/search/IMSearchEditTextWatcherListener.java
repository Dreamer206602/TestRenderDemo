package com.autonavi.indoor.render3D.search;

import android.text.Editable;
import android.view.View;

/**
 * Created by liqingyang on 16/2/17.
 */
public interface IMSearchEditTextWatcherListener {

    public void afterTextChanged(View view, Editable s);

    public void beforeTextChanged(View view, CharSequence s, int start, int count, int after);

    public void onTextChanged(View view, CharSequence s, int start, int before, int count);

}
