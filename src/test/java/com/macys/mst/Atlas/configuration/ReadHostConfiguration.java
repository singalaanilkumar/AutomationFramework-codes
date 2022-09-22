package com.macys.mst.Atlas.configuration;

import com.macys.mst.artemis.config.FileConfig;

import java.util.HashMap;
import java.util.Map;

public enum ReadHostConfiguration {

    GENERIC_CONFIG_URL("services.hostName", "services.genericConfigUrl"),
    APP_URL_JWT_PASSWORD("", "AppUrls.jwtPassword"),
    LOCATION_NUMBER("", "services.locationNbr"),
    HANDHELD_URL("","AppUrls.handheldUrl"),
    APOLLO_URL("","AppUrls.apolloUrl"),
    MANIFEST_URL("","services.manifesturl");

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
