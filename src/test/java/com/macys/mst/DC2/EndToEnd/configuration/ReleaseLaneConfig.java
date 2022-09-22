package com.macys.mst.DC2.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

public class ReleaseLaneConfig {
    public static final String HOSTNAME = FileConfig.getInstance().getStringConfigValue("services.hostName");
    public static final String CURRENT_USER = FileConfig.getInstance().getStringConfigValue("AppUrls.userName");
    public static final String LOCN_NBR = "7221";
    public static final String WSM_SCHEMA = "wsm";
    public static final String LOCATION_SCHEMA = "location";

    public static final String CREATE_MODIFY_ACTIVITY = HOSTNAME + "/wsm-service/wsm/%s/activities";
    public static final String GET_RL_ACTIVITY_ID = HOSTNAME + "/wsm-service/wsm/%s/activities?id=%s";
    public static final String GET_RL_ACTIVITY_PO_NBR = HOSTNAME + "/wsm-service/wsm/%s/activities?poNbr=%s";
    public static final String GET_RL_ACTIVITY_STATUS_SORTED_COUNT = HOSTNAME + "/wsm-service/wsm/%s/activities?type=RELEASE&status=%s&limit=%s&sortOrder=%s&sortBy=createdTime"; //containerType=LANE&
    public static final String CREATE_TOTE_URI = HOSTNAME + "/inventory-service/inventory/%s/containers";

}
