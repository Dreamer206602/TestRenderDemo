package com.autonavi.indoor.render3D.search;

/**
 * Created by liqingyang on 16/2/18.
 */
public class IMSearchResult {

    // ft_sourceid
    public String strSID = "";
    //ft_autonavi_pid
    public String strPID = "";
    // ft_name_dp
    public String strNameDp = "";
    // ft_name_cn
    public String strNameCn = "";
    // ft_name_en
    public String strNameEn = "";
    // ft_typecode: AABBCC: AA大类别，BB:中类, CC: 子类别定义
    public int nTypeCode = 0;
    // ft_shoptype
    public int nShopType = 0;
    // 楼层 Index
    public int nFloorIndex = 0;
    // 中心点坐标
    public double nCenterX = 0;
    public double nCenterY = 0;


}
