package com.macys.mst.DC2.EndToEnd.configuration;
import com.macys.mst.artemis.config.FileConfig;
public class OrderEndPoint {
    public static final String ORDER_HOSTNAME= FileConfig.getInstance().getStringConfigValue("services.hostnameForOrder");
    public static final String ORDER= FileConfig.getInstance().getStringConfigValue("services.order");
    public static final String LOCATION_NBR= FileConfig.getInstance().getStringConfigValue("services.locationNbr");
    public static final String ORDER_SERVICE = ORDER_HOSTNAME +ORDER+ "/poLineItm";
    public static final String ORDER_SERVICE_PO_SKU = ORDER_HOSTNAME +ORDER+"/"+LOCATION_NBR+"/polines/{REPORT_ID}?poNbr={PO_NBR}&skuUpc={SKU_UPC}";
    public static final String ORDER_SERVICE_DRR_PO_DETAIL_HEADER = ORDER_HOSTNAME +ORDER+"/"+LOCATION_NBR+"/rcptDetailReports/reportId/";
    public static final String ORDER_SERVICE_DRR_PO_STATUS = ORDER_HOSTNAME +ORDER+"/"+LOCATION_NBR+"/poinquiry?reportId=";
    public static final String ORDER_SERVICE_PO_LINE = ORDER_HOSTNAME +ORDER+"/"+LOCATION_NBR+"/poinquiry?reportId=";
    public static final String ORDER_SERVICE_PO_Inquiry = ORDER_HOSTNAME +ORDER+"/"+LOCATION_NBR+"/poinquiry?poNbr=%s&rcptNbr=%s";

}
