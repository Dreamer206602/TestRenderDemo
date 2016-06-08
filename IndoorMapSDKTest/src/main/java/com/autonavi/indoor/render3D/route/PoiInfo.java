package com.autonavi.indoor.render3D.route;

import com.amap.api.im.mapcore.IMFloorInfo;

import java.io.Serializable;

public class PoiInfo implements Serializable{
	private static final long serialVersionUID = 8589268912354164692L;
	public PoiMapCell cell;
	public IMFloorInfo floor;
	public String des;
	public int PoiInfoType;
}
