package com.autonavi.indoor.render3D.search;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by liqingyang on 16/2/18.
 */
public class IMGridView extends GridView {

    public IMGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IMGridView(Context context) {
        super(context);
    }

    public IMGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
