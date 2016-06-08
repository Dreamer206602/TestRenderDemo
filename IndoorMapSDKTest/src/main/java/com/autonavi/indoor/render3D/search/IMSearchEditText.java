package com.autonavi.indoor.render3D.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by liqingyang on 16/2/17.
 */
public class IMSearchEditText extends EditText {

    private IMSearchEditTextWatcherListener mTextWatcherListener = null;

    public void setTextWatcherEventListener(IMSearchEditTextWatcherListener listener) {
        this.mTextWatcherListener = listener;
    }

    public IMSearchEditText(Context context) {
        this(context, null);
    }

    public IMSearchEditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, android.R.attr.autoCompleteTextViewStyle);
    }


    public IMSearchEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);

        setFocusable(true);
        addTextChangedListener(new AutoWatcher(this));
    }

    private class AutoWatcher implements TextWatcher {
        private View view;

        public AutoWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mTextWatcherListener != null) {
                mTextWatcherListener.afterTextChanged(view, s);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (mTextWatcherListener != null) {
                mTextWatcherListener.beforeTextChanged(view, s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mTextWatcherListener != null) {
                mTextWatcherListener.onTextChanged(view, s, start, before, count);
            }
        }
    }

}
