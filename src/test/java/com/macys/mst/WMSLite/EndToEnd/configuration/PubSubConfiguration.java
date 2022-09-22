package com.macys.mst.WMSLite.EndToEnd.configuration;

import java.util.HashMap;
import java.util.Map;

public enum PubSubConfiguration {
    GENERIC_CONFIG_URL("services.genericConfigUrl");

    private String topicName;
    private static Map<String, String> urlConfig = new HashMap<>();

    PubSubConfiguration(String topicName) {
        this.topicName = topicName;

    }


}
