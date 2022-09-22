package com.macys.mst.WMSLite.EndToEnd.db.app;

public class SQLQueries {

    public static final String GET_OSM_SHP_HDR_DATA = "select * from shp_hdr where fullfillment_nbr='%s'";

    public static final String GET_JOPPA_INPT_PKT_HDR = "SELECT * FROM INPT_PKT_HDR where PKT_CTRL_NBR='%s'";

    public static final String GET_WAVE_STATUS = "SELECT WAVE_STAT_CODE,STATUS,INPT_PKT_HDR_ID,PKT_NBR,PKT_CTRL_NBR FROM INPT_PKT_HDR pkthdr where PKT_NBR='%s'";

    public static final String GET_OSM_SHP_LI_DATA="SELECT RES_NBR,RES_LN_STAT FROM shp_li  where RES_NBR='%s'";

    public static final String GET_INPT_PKT_DTL ="SELECT nil_status, size_desc, CANCEL_QTY, ORIG_ORD_QTY,PKT_CTRL_NBR  FROM INPT_PKT_DTL where PKT_CTRL_NBR='%s'" ;


}
