package com.autonavi.indoor.render3D.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.im.mapcore.IMSearchResult;
import com.autonavi.indoor.render3D.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liqingyang on 16/2/18.
 */
public class IMSearchListAdapter extends BaseAdapter {

    Context mContext;

    private String mKeywords = "";
    private final String htmlStart = "<font color='#4c90f9'>";
    private final String htmlEnd = "</font>";

    private List<IMSearchResult> mSearchResultList = null;
    private LayoutInflater mInflater;
    public boolean isShowIcon = true;
    private int mSelectedTypeIcon = -1;

    // 室内品牌图标集合
    private Map<String, Bitmap> mBrandBitmaps;

    public IMSearchListAdapter(Context context, boolean isShowIcon) {
        mContext = context;
        this.isShowIcon = isShowIcon;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null == mBrandBitmaps) {
            mBrandBitmaps = new HashMap<String, Bitmap>();
        }
    }

    public void clearData() {
        if (mSearchResultList != null) {
            mSearchResultList.clear();
        }

        if (null != mBrandBitmaps) {
            mBrandBitmaps.clear();
        }
    }

    public synchronized void notifyListByType(int typeIcon, List<IMSearchResult> mSearchResultList) {
        this.isShowIcon = false;
        mSelectedTypeIcon = typeIcon;
        this.mSearchResultList = mSearchResultList;
        this.notifyDataSetChanged();
    }

    public synchronized void notifyListByKeywords(String keywords,
                                                  List<IMSearchResult> mSearchResultList) {
        this.isShowIcon = true;
        this.mKeywords = keywords;
        this.mSearchResultList = mSearchResultList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == mSearchResultList)
            return 0;

        return mSearchResultList.size();
    }

    @Override
    public IMSearchResult getItem(int position) {
        if (position >= 0 && position < mSearchResultList.size()) {
            return mSearchResultList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == mSearchResultList || mSearchResultList.size() == 0)
            return null;

        ViewHolder holder = null;
        IMSearchResult indoorPoi = mSearchResultList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.indoor_view_search_listview_item, null);

            holder.mPosName = (TextView) convertView
                    .findViewById(R.id.indoor_pos_name);

            holder.mPosAddress = (TextView) convertView
                    .findViewById(R.id.indoor_pos_address);

            holder.mTypeImgView = (ImageView) convertView
                    .findViewById(R.id.indoor_pos_imageview);

            holder.mPosDistance = (TextView) convertView
                    .findViewById(R.id.indoor_pos_distance);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTypeImgView.setVisibility(View.VISIBLE);
        if (isShowIcon) {
            holder.mTypeImgView
                    .setImageResource(R.drawable.indoor_search_input_icon);
        } else {
//			if(!TextUtils.isEmpty(indoorPoi.mBrandId)){
//				Bitmap bitmap = null;
//				bitmap = mBrandBitmaps.get(indoorPoi.mBrandId);
//				if(null == bitmap){
//					bitmap = IndoorCache.getBrandIconFromCache(indoorPoi.mBrandId);
//				}
//
//				if(null != bitmap){
//					if(!mBrandBitmaps.containsKey(indoorPoi.mBrandId))
//						mBrandBitmaps.put(indoorPoi.mBrandId, bitmap);
//
//					holder.mTypeImgView.setImageBitmap(bitmap);
//				}else{
//					holder.mTypeImgView.setImageResource(mSelectedTypeIcon);
//				}
//			}else{
//				holder.mTypeImgView.setImageResource(mSelectedTypeIcon);
//			}
        }

        holder.mPosDistance.setVisibility(View.GONE);

        holder.mPosName.setText(Html.fromHtml(getHighLightString(
                indoorPoi.getName(), this.mKeywords)));

        holder.mPosAddress.setVisibility(View.VISIBLE);
        holder.mPosAddress.setText(indoorPoi.getFloorNo() + "层");

        return convertView;
    }

    class ViewHolder {
        public TextView mPosName;
        public TextView mPosAddress;
        public ImageView mTypeImgView;
        public TextView mPosDistance;
    }

    private String getHighLightString(String name, String keywords) {
        if (name == null)
            name = "";

        if (name.length() == 0 || keywords == null || keywords.length() == 0)
            return name;

        StringBuffer nameHtml = new StringBuffer(name);
        int index = nameHtml.toString().toLowerCase()
                .indexOf(keywords.toLowerCase());

        if (index > -1) {
            nameHtml.insert(index + keywords.length(), htmlEnd);
            nameHtml.insert(index, htmlStart);
        }

        return nameHtml.toString();
    }

    public SpannableString getMarkColorString(String result, String keyword) {
        SpannableString sp = new SpannableString(result);
        try {
            if (keyword != null && result.contains(keyword)) {
                int sIndex = result.indexOf(keyword);
                int eIndex = sIndex + keyword.length();
                if (sIndex >= 0 && eIndex > 0 && sIndex < eIndex) {
                    sp.setSpan(
                            new ForegroundColorSpan(Color.argb(255, 76, 144,
                                    249)), // 将关键字颜色标记为蓝色
                            sIndex, eIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sp;
    }

}
