package com.macys.mst.WMSLite.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

public class ConfigurationEndPoint {

    public static final String CONFIG_URL = ReadHostConfiguration.GENERIC_CONFIG_URL.value()+ReadHostConfiguration.LOCATION_NUMBER.value()+"/app/%s/module/%s/%s";

    public static final String configKey =  FileConfig.getInstance().getStringConfigValue("services.hostNameForConfig")+"/"+"%s";

}
