package com.autonavi.indoor.render3D.route;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName: RoutePathData
 * @Description: 路线规划实体类
 * @author ruimin.cao
 * @date 2014-8-1
 * @version 1.0
 */
public class RoutePathData implements Serializable{
	private static final long serialVersionUID = 1L;

	// 求路结果状态
	public String mResponseStatus;

	// 建筑名称
	public String mBuildingName;

	// 建筑ID
	public String mBuildingId;

	// 规划路径总长度(单位: 米)
	public int mDistance;

	public ArrayList<RoutePathFloor> mFullPath = new ArrayList<RoutePathFloor>();
}
