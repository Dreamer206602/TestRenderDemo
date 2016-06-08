package com.autonavi.indoor.render3D.route;

import java.io.Serializable;

import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextUtils;

public class PoiMapCell implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6696215682034581104L;
//	public final static int TYPE_VISIABLE = 0;
//	public final static int TYPE_VISIABLE_WITH_SCALE = 1;
	public final static int LEFT_TOP = 0;
	public final static int RIGHT_TOP = 1;
	public final static int LEFT_BOTTOM = 2;
	public final static int RIGHT_BOTTOM = 3;
	public final static int LEFT_CENTER = 4;
	public final static int TOP_CENTER = 5;
	public final static int RIGHT_CENTER = 6;
	public final static int BOTTOM_CENTER = 7;
	public final static int CENTER = 8;
	
	protected RectF rect;
	private int gravity = 0;
    private int type;
    private Point position;
    private double x;
    private double y;
    private int floorNo;
    private double fixHeight = 0;
    private String title;
    private String poiId;
    private int resId;
    private boolean clickable = false;
    private boolean	visible=true;
    public PoiMapCell() {
    	rect = new RectF();
	}
    public PoiMapCell(PoiMapCell cell){
    	this.poiId=cell.poiId;
    	this.rect=cell.rect;
    	this.gravity=cell.gravity;
    	this.type=cell.type;
    	this.position=cell.position;
    	this.x=cell.x;
    	this.y=cell.y;
    	this.floorNo=cell.floorNo;
    	this.fixHeight=cell.fixHeight;
    	this.title=cell.title;
    	this.resId=cell.resId;
    	this.clickable=cell.clickable;
    	this.visible=cell.visible;
    }
    public boolean isVisible() {
		return visible;
	}
    /**
     * 判断当前实例是否相同
     * @param cell
     * @return
     */
	public boolean contains(PoiMapCell cell) {
		if (cell != null) {
//			if(cell==this){
//				return true;
//			}else{
//				return false;
//			}
			if (cell.getX() == this.getX() && cell.getY() == this.getY()) {
				return true;
			} else if (!TextUtils.isEmpty(poiId)) {
				return poiId.equals(cell.getPoiId());
			}
		}
		return false;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setClickable(boolean clickable){
    	this.clickable = clickable;
    }
    
    public boolean isClickable(){
    	return clickable;
    }
    public PoiMapCell(double x, double y, String names) {
        super();
        rect = new RectF();
        this.x = x;
        this.y = y;
        this.title = names;
    }    
    public PoiMapCell(int type, double x, double y, String names) {
        super();
        rect = new RectF();
        this.type = type;
        this.x = x;
        this.y = y;
        this.title = names;
    }
    
    public PoiMapCell(int type, double x, double y,int floorNo, String names,String poiId) {
        super();
        rect = new RectF();
        this.type = type;
        this.x = x;
        this.y = y;
        this.title = names;
        this.floorNo = floorNo;
        this.poiId = poiId;
    }
    
    public PoiMapCell(int type, double x, double y, int floorNo,String strName) {
        super();
        rect = new RectF();
        this.type = type;
        this.x = x;
        this.y = y;
        this.setFloorNo(floorNo);
        this.title = strName;
//        try {
//            this.name = new String(names, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }
    public void Test(int type,double x,double y,int floorNo,String names){
//    	int type=1;
//    	double x=1;
//    	double y=1;
//    	int floorNo=1;
//    	byte[] names=null;
        rect = new RectF();
        this.type = type;
        this.x = x;
        this.y = y;
        this.setFloorNo(floorNo);
        this.title = names;
//        "test 1";//new String(names, "utf-8");
//        try {
//        	if(names!=null)
//			this.name = new String(names,"unicode");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    /**
     * 经纬度
     * @param x
     */
    public double getX() {
        return x;
    }
    /**
     * 经纬度
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }
    /**
     * 经纬度
     * @param x
     */
    public double getY() {
        return y;
    }
    /**
     * 经纬度
     * @param x
     */
    public void setY(double y) {
        this.y = y;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }
    
    @Override
    public int hashCode() {
    	return super.hashCode();
    }

    @Override
    public String toString() {
        return "PoiMapCell{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", name='" + title + '\'' +
                '}';
    }

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
		
		
	}
	public RectF getRect() {
		return rect;
	}

	public void setRect(RectF rect) {
		this.rect = rect;
	}

	public int getFloorNo() {
		return floorNo;
	}

	public void setFloorNo(int floorNo) {
		this.floorNo = floorNo;
	}

	public double getFixHeight() {
		return fixHeight;
	}

	public void setFixHeight(double fixHeight) {
		this.fixHeight = fixHeight;
	}

	/**设置显示方式
	 * 
	 * @param gravity
	 */
	public int getGravity() {
		return gravity;
	}
	/**设置显示方式
	 * 
	 * @param gravity
	 */
	public void setGravity(int gravity) {
		this.gravity = gravity;
	}

	public String getPoiId() {
		return poiId;
	}

	public void setPoiId(String poiId) {
		this.poiId = poiId;
	}
}
