package com.macys.mst.DC2.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

public class PO4WallEndPoint {
    public static final String PO4Wall_HOSTNAME= FileConfig.getInstance().getStringConfigValue("services.hostnameForPO4Wall");
    public static final String PO4Wall= FileConfig.getInstance().getStringConfigValue("services.po4Wall");
    public static final String LOCATION_NUMBER = FileConfig.getInstance().getStringConfigValue("services.locationNbr");
    public static final String PO4WallDataInsert_SERVICE = PO4Wall_HOSTNAME +PO4Wall+ "/test/poreceipt";
    public static final String PO4WallDataCleanUp_SERVICE = PO4Wall_HOSTNAME +PO4Wall+ "/test/receipts/{poRcptNbr}";
    public static final String PO4WALL_GET_POLINE_DETAILS_ONBARCODE = PO4Wall_HOSTNAME +PO4Wall + "/" + LOCATION_NUMBER + "/polines/%s";
    public static final String PO4WALL_GET_POLINE_DETAILS = PO4Wall_HOSTNAME +PO4Wall + "/" + LOCATION_NUMBER + "/polines/";
    public static final String PO4WALL_GET_PO_DETAILS = PO4Wall_HOSTNAME +PO4Wall+"/po/%s?rcptNbr=%s";
    public static final String PO4WALL_GET_POLINE_PCK = PO4Wall_HOSTNAME +PO4Wall + "/" + LOCATION_NUMBER + "/packs?poLineBarcode=%s";
    public static final String POScreen= FileConfig.getInstance().getStringConfigValue("services.poScreen");
    public static final String POSCREEN_GET_PODETAILS = PO4Wall_HOSTNAME +POScreen+"/"+LOCATION_NUMBER+"/podetails/reportid/%s";
    public static final String PO4WALL_GET_LOCATION = PO4Wall_HOSTNAME +PO4Wall + "/locations/%s";
    public static final String POPtcReleaseService = PO4Wall_HOSTNAME + "/pofourwalls-service/ptc/" + LOCATION_NUMBER + "/ptcrelease";
    public static final String poscreens_rcptDetailReports = PO4Wall_HOSTNAME +POScreen + "/" + LOCATION_NUMBER + "/rcptDetailReports/reportId/%s";
    public static final String toteAlloc = PO4Wall_HOSTNAME +PO4Wall + "/" + LOCATION_NUMBER + "/po/poNbr/%s/rcptNbr/%s?skuUpc=%s&toteId=%s";

}
