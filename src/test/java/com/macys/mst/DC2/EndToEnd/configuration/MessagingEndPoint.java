package com.macys.mst.DC2.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

public class MessagingEndPoint {
    public static final String Messaging_HOSTNAME= FileConfig.getInstance().getStringConfigValue("services.hostnameForMessaging");
    public static final String Messaging= FileConfig.getInstance().getStringConfigValue("services.messaging");
    public static final String MessagingGetCall= Messaging_HOSTNAME+Messaging+"/%s?messageType=%s&textFilter=%s&fromDate=%s&toDate=%s&size=%s";


}
