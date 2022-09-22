package com.macys.mst.DC2.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

public class SortEndPoint {
    public static final String hostname = FileConfig.getInstance().getStringConfigValue("services.hostname");
    public static final String sortLocationGetService= hostname+"/sorting-service/sortlocation/7221/packawaylocation?mode=%s&modeValue=%s&zone=%s&locationType=%s";
    public static final String sortLocationPutService= hostname+"/sorting-service/sortlocation/7221/packawaylocation";
    }
