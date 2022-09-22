package com.macys.mst.DC2.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

public class DistroEndPoint {
    public static final String DISTRO_HOSTNAME= FileConfig.getInstance().getStringConfigValue("services.hostnameForDistro");
    public static final String DISTRO= FileConfig.getInstance().getStringConfigValue("services.distro");
    public static final String POReleaseService = DISTRO_HOSTNAME +DISTRO+ "/receipt/release";
    public static final String PORECEIPT_DASHBOARD= FileConfig.getInstance().getStringConfigValue("services.podashboard");
    public static final String DASHBOARDREPORTINGSERVICE=DISTRO_HOSTNAME +PORECEIPT_DASHBOARD;
    public static final String POINQUIRY= FileConfig.getInstance().getStringConfigValue("services.poInquiry");
    public static final String POINQUIRYREPORTINGSERVICE=DISTRO_HOSTNAME +PORECEIPT_DASHBOARD;  
}
