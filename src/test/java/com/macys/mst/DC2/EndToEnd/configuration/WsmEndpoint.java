package com.macys.mst.DC2.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

public class WsmEndpoint {
    public static final String WSM_HOSTNAME= FileConfig.getInstance().getStringConfigValue("services.hostnameForWsm");
    public static final String LOCATION_NBR= FileConfig.getInstance().getStringConfigValue("services.locationNbr");
    public static final String WSM= FileConfig.getInstance().getStringConfigValue("services.wsm");
    public static final String WSM_SERVICE = WSM_HOSTNAME +WSM+ "/"+ LOCATION_NBR+ "/activities";
    public static final String WSM_SERVICE_SEARCH = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?container=%s&containerType=Tote";
    public static final String WSM_ACTIVITY_SEARCH = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?container=%s";
    public static final String WSM_ACTIVITY_SEARCH_By_ID = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?id=%s";
    public static final String WSM_ACTIVITY_SEARCH_RELEASELANE = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?container=%s&type=Release&status=%s";
    public static final String WSM_ACTIVITY_SEARCH_PACKAWAY = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?upc=%s&type=PACKAWAY&status=%s";
    public static final String LOCATION = FileConfig.getInstance().getStringConfigValue("services.location");
    public static final String LOCATION_SEARCH = WSM_HOSTNAME +LOCATION +"/"+LOCATION_NBR +"/barcode/%s";
    public static final String WSM_STS_CARTON_ACTIVITY = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?container=%s&type=STSCARTON&status=%s";
    public static final String WSM_Activities_SEARCH = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?container=%s&containerType=%s&startDate=%s&endDate=%s";
    public static final String WSM_update_Activities = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities";
    public static final String WSM_Activities_SEARCH_PTCRELEASE = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?poNbr=%s&status=%s&container=%s&startDate=%s&endDate=%s";
    public static final String WSM_Activities_SEARCH_ID_STATUS = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?id=%s&status=%s";
    public static final String WSM_Delete_Activities = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities/%s";
    public static final String WSM_ACTIVITY = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?containerType=%s&status=%s";
	public static final String WSM_SPLITFORWAVE_SEARCH = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?container=%s&containerType=BINBOX&type=SPLIT";
    public static final String WSM_ACTIVITY_SEARCH_RELEASELANE_ByContainerId = WSM_HOSTNAME +WSM+"/"+LOCATION_NBR+"/activities?containerType=%s&type=Release&status=%s&containerId=%s";

}
