package com.macys.mst.WMSLite.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

import java.util.HashMap;
import java.util.Map;

public enum ReadHostConfiguration {
    GENERIC_CONFIG_URL("services.hostName", "services.genericConfigUrl"),
    LOCATION_NUMBER("", "services.locationNbr"),
    CREATE_INVENTORY_URL("services.hostName", "services.inventoryService"),
    UPDATE_CONT_STATUS ("services.hostName", "services.ContainerServices.containerCreate"),
    CONTAINER_FORMAT_CONFIG_URL("", "services.ContainerFormatConfig"),
    FETCH_POLINE_DTLS_URL("services.hostName", "services.fetchPOLineDtls"),
    LOCATIONS_ASSIGNED_TO_PO_URL("services.hostName", "services.locationsassignedtoPO"),
    DELETE_INVENTORY_URL("services.hostName", "services.deleteInventory"),
    RF_MENU_URL("", "AppUrls.RFMenuUrl"),
    APP_URL_JWT_PASSWORD("", "AppUrls.jwtPassword"),
    GET_INVENTORY_SERVICE_URL("services.hostName", "services.InventoryServices.inventoryGet"),
    CONFIG_HOST_NAME("", "services.hostNameForConfig"),
    DELETE_CACHE_URL("", "services.deleteCache"),
    GET_MESSAGE_SERVICE_URL("services.hostName", "services.msgServiceGetURL"),
    GET_PO_FOUR_WALLS_HOST_CONFIG("","hostnameConfigForPOFourWalls"),
    SCM_MENU_URL("", "AppUrls.scmurl"),
    WMS_ACTIVITY_URL("", "services.wmsActivityUrl"),
    GET_PACKAWAY_THRESHOLD_LIMIT_URL("","services.ThresholdLimittConfig"),
    GET_INVENTORY_ADJUSTMENT_HISTORY("services.hostName", "services.InventoryServices.inventoryAdjUrl"),
    MANIFEST_URL("","services.manifesturl"),
    LOCATION_SERVICE_HOST("services.hostName","services.locationServiceHost"),
    WMS_LITE_URL("", "AppUrls.wmsLiteUrl");

    private String path;
    private String host;
    private static Map<String, String> urlConfig = new HashMap<>();

    ReadHostConfiguration(String host, String path) {
        this.path = path;
        this.host = host;
    }

    static {
        FileConfig fileConfig = FileConfig.getInstance();
        for (ReadHostConfiguration config : ReadHostConfiguration.values()) {
            String value = (!"".equals(config.host))
                    ? String.format("%1$s%2$s", fileConfig.getStringConfigValue(config.host),
                    fileConfig.getStringConfigValue(config.path))
                    : fileConfig.getStringConfigValue(config.path);

            urlConfig.put(config.name(), value);
        }
    }

    public String value() {
        if (urlConfig.containsKey(this.name())) {
            return urlConfig.get(this.name());
        }
        return null;
    }
}
