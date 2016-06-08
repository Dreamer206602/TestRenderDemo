package com.autonavi.indoor.render3D.route;

import com.amap.api.im.mapcore.IMPoint;

import java.util.ArrayList;

/**
 * @ClassName: RoutePathFloor
 * @Description: 路线规划楼层
 * @author ruimin.cao
 * @date 2014-8-1
 * @version 1.0
 */
public class RoutePathFloor {
	// 楼层号
	public String mFloorNumber;
	// 楼层名字
	public String mFloorName;
	// 规划行为 如走电梯还是楼梯
	public String mAction;
	public String mBuildingId;
	// 本楼层规划距离
	public int mSegDistance;
	// 路径经纬度
	public ArrayList<IMPoint> mPathPointLst = new ArrayList<IMPoint>();
	/**
	 * @Title: startTipsName
	 * @Description: 气泡在起点显示内容
	 * @param startType
	 * @return
	 */
	public String startTipsName(byte startType) {
		String name = "";
		switch (startType) {
			case 0x01:{
				name = "进入建筑物";
				break;
			}
				
			case 0x02:{
				name = "离开建筑物";
				break;
			}
				
			case 0x41:
			case 0x42: {
				name = "进门";
				break;
			}
			
			case 0x03:
			case 0x43: {
				name = "出电梯";
				break;
			}
			
			case 0x04:
			case 0x44: {
				name = "出楼梯";
				break;
			}
			
			case 0x05:
			case 0x45: {
				name = "出扶梯";
				break;
			}
			
			case 0x39: {
				name = "起点";
				break;
			}
		}
		
		return name;
	}

	/**
	 * 气泡在终点显示内容
	 * 
	 * @param endType
	 * @param floorId
	 * @return
	 */
	public String endTipsName(byte endType, String floorName) {
		String name = "";
		String where = "";
		if (floorName != null && !"".equals(floorName)) {
			where = "到" + floorName + "层";
		}

		try {
			switch (endType) {
				case 0x01:
					name = "进入建筑物";
					break;
					
				case 0x02:
					name = "离开建筑物";
					break;
					
				case 0x41:
				case 0x42: {
					name = "出门";
					break;
				}
				
				case 0x03:
				case 0x43: {
					name = "坐电梯" + where;
					break;
				}
				
				case 0X04:
				case 0x44: {
					name = "走楼梯" + where;
					break;
				}
				
				case 0x05:
				case 0x45: {
					name = "坐扶梯" + where;
					break;
				}
				
				case 0x06:
				case 0x40: {
					name = "";
					break;
				}
			
			}
		} catch (Exception e) {

		}
		
		return name;
	}
}
