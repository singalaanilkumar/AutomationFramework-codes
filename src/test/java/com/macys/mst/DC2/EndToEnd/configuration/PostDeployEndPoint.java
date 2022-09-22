package com.macys.mst.DC2.EndToEnd.configuration;

public class PostDeployEndPoint {
	public static final String hostName = "https://msc.gcp.cloudrts.net";
	//FileConfig.getInstance().getStringConfigValue("services.hostName");
    public static final String poLineItemPath = hostName +"/pofourwalls-service/pofourwalls/7221/polines/%s/detail";
    public static final String inventoryByPORPCTPath = hostName+"/inventory-service/inventory/7221/containers/inventory?poNbr=%s&POReceipt=%s";
    public static final String poLineItmPath		 = hostName+"/msp-whm-ordermanagement-service/order/poLineItm/%s";
    public static final String OrderMngt_distro = hostName+"/msp-whm-ordermanagement-service/order/poLineItm/%s";
    public static final String PO4walls_POlineBarcode = hostName+"/pofourwalls-service/pofourwalls/7221/polines/%s";
    public static final String OrderMngt_skudeptNbr = hostName+"/msp-whm-ordermanagement-service/order/poLineItm/skuUpcNbr/%s";
    public static final String PO4walls_distro_inventory = hostName+"/pofourwalls-service/pofourwalls/po/%s?rcptNbr=%s";
    public static final String containerPath = hostName+"/inventory-service/inventory/7221/containers?barcode=%s";
    public static final String PO4walls_reporting = hostName+"/reporting-service/podashboard/7221/poreceipts?poNbr=%s";
    public static final String OrderMngt_InquiryService = hostName+"/msp-whm-ordermanagement-service/order/7221/poinquiry?poNbr=%s";
    public static final String wsmReleaseLaneAcivityPath = hostName+"/wsm-service/wsm/7221/activities?containerType=LANE&type=RELEASE&status=OPEN&limit=1000&sortOrder=desc&sortBy=createdTime&enabled=1";
    public static final String singleSKUDetailsPath = hostName+"/msp-whm-ordermanagement-service/order/7221/polines/%s?poNbr=%s&skuUpc=%s";
    public static final String inventorySnapshotDetailsPath = hostName+"/inventory-service/inventory/7221/containers/inventorySnapshots/items?status=AVL&containerType=BINBOX&barcode=%s";
    public static final String inventoryRelationDetailsPath = hostName+"/inventory-service/inventory/7221/containers/items/relationships/parents?item=%s&itemContainerType=BINBOX";
    public static final String shipInfoPath = hostName+"/messaging-service/message/7221?messageType=SHIPINFO&textFilter=15000";
    public static final String RCPT_DETAIL_BY_REPORT = hostName+"/pofourwalls-service/poscreens/7221/rcptDetailReports/reportId/%s";
    public static final String cartonPath = hostName+"/package-service/7221/packages?barcode=%s&type=CRT";

}
