package com.macys.mst.DC2.EndToEnd.datasetup;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmokeTestData {

    private static Map<String, String> hostMap;
    private static Map<String, String> serviceUrls;
    private static Map<String, String > postServiceUrls;

    public static Map<String, String> getHostMap() {
        if (null == hostMap) {
            setHostMap();
        }
        return hostMap;
    }

    public static Map<String, String> getServiceUrls() {
        if (null == serviceUrls) {
            setServiceUrls();
        }
        return serviceUrls;
    }

    public static Map<String, String> getPOSTServiceUrls(){
        if (null == postServiceUrls) {
            setPOSTServiceUrls();
        }
        return postServiceUrls;
    }


    private static void setHostMap(){
        hostMap = new LinkedHashMap<>();
        hostMap.put("DEV", "https://dev-backstage.devops.fds.com");
        hostMap.put("QA", "https://qa-backstage.devops.fds.com");
        hostMap.put("UAT", "https://uat-backstage.devops.fds.com");
        hostMap.put("PERF", "https://ui-perf.msc.gcp.cloudrts.net");
        hostMap.put("PROD", "https://msc.gcp.cloudrts.net");
    }

    private static void setServiceUrls(){
        serviceUrls = new LinkedHashMap<>();
        serviceUrls.put("loadinquiry","/hh-ddops-service/7221/loadinquiry/door/S201");
        serviceUrls.put("appointments","/appointment-service/appointments/123456");
        serviceUrls.put("networkmap","/cfnetworkmap-service/networkmap/7221/to/6095");
        //serviceUrls.put("sortation","/sortation-service/sortation/7221?barcode=12345");
        serviceUrls.put("accesscontrol","/accesscontrol-service/accesscontrol/roles/1");
        serviceUrls.put("configuration","/configuration-service/configuration/7221/app/pofourwalls/module/pofourwalls/locnconfig");
        serviceUrls.put("handheld","/handheld-service/config/7221/barcodeFormat");
        serviceUrls.put("hh-receiving","/hh-receiving-service/putawayContainer/7221/getStorageLocations");
        /*serviceUrls.put("inbound-dashboard","/inbound-dashboard-service/dashboard/7221?page=0&size=1&sort=ersStatus,asc");*/
        serviceUrls.put("inventory","/inventory-service/attributedef/TOTE");
        serviceUrls.put("location","/location-service/dimension");
        serviceUrls.put("manifest","/manifest-service/manifest/7221/printConfigDetails");
        serviceUrls.put("messaging","/messaging-service/message/7221?size=1&messageType=STOREALLOC");
        serviceUrls.put("orderfulfillment","/msp-whm-orderfulfillment-service/orders?shipOutStartDate=2020-02-18&shipOutEndDate=2020-06-18&orderLimit=1&fulfillmentLocnNbr=7222&flowType=PMR&divNbr=77");
        serviceUrls.put("ordermanagement","/msp-whm-ordermanagement-service/order/postatuses");
        serviceUrls.put("shipment","/msp-whm-shipment-service/shipment/apptstatuses");
        serviceUrls.put("support","/msp-whm-support-service/support/location/7221");
        serviceUrls.put("package","/package-service/7221/packages/statuses");
        serviceUrls.put("pofourwalls","/pofourwalls-service/poscreens/poinquiry-lookup");
        serviceUrls.put("receiving","/receiving-service/eocConfig/7221/slot?slotId=0102-AA-001");
        serviceUrls.put("reporting","/reporting-service/dashboard/7221/?page=0&size=1");
        serviceUrls.put("shipping","/shipping-closeout-service/shipping/7221/manifest/closeout");
        serviceUrls.put("supplychain-composite","/supplychain-composite-service/location/7221/PackAway?StorageType=Half+Pallet");
        serviceUrls.put("wsm","/wsm-service/wsm/7221/activities?limit=1&type=SPLIT");
        serviceUrls.put("waving","/waving-service/waving/7221/waves/");

    }

    private static void setPOSTServiceUrls(){
        postServiceUrls = new LinkedHashMap<>();
        postServiceUrls.put("distroengine","/distroengine-service/distroengine/receipt/release");
        postServiceUrls.put("inventory-messaging","/inventory-messaging-service/platform/v1/actuator/health");
        postServiceUrls.put("shipping","/shipping-service/shipping/labels/7221/");
        postServiceUrls.put("sorting","/sorting-service/sortlocation/7221/packawaylocation");
        postServiceUrls.put("print-adaptor","/print-adaptor-service/nicelabel/7221/prints");
        postServiceUrls.put("pyramid-communication","/pyramid-communication-service/communicate/7221/HEARTBEAT");
    }
}
