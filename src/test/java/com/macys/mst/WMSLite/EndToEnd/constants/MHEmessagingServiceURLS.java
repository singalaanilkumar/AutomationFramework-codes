package com.macys.mst.WMSLite.EndToEnd.constants;

import com.macys.mst.artemis.config.FileConfig;

public interface MHEmessagingServiceURLS {

    String messagingServiceHostUrl = FileConfig.getInstance().getStringConfigValue("services.hostnameForMessaging");
    String messagingServiceGETAll_URL  = messagingServiceHostUrl+"/messaging-service/message/3?messageType=#messageType#&size=0";
    String pyramidHostUrl = FileConfig.getInstance().getStringConfigValue("services.hostnameForPyramidService");
    String pyramidJSONHostUrl = FileConfig.getInstance().getStringConfigValue("services.hostnameForMessaging");
    String pyramidSimulatorPostReverse_URL = pyramidHostUrl+"/pyramid-simulator-service/msc/v1/messages";
    String pyramidSimulatorPostJSON_URL = pyramidJSONHostUrl+"/messaging-service/message/WESMessaging";

}
