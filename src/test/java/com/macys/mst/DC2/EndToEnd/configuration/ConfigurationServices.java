package com.macys.mst.DC2.EndToEnd.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.mst.DC2.EndToEnd.utilmethods.Constants.PackType;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.foundationalServices.StepDefinitions.ConfigurationSteps.ConfigurationSteps;
import com.macys.mst.foundationalServices.serviceobjects.ConfigurationServiceObject.ConfigServiceObject;

public class ConfigurationServices {
    private static List<Map<String, String>> packAwaySortZone;
    
    public static final String APP_NAME = "offpricedc";
    public static final String MODULE_NAME = "handheld";
    
    // PackAwaySortZone configuration keys
    public static final String PACKAWAY_SORT_ZONE = "PackawaySortZone";
    public static final String SORT_ZONE = "SortZone";
    public static final String PACK_TYPE = "PackType";
    
    private ConfigServiceObject serviceObject = new ConfigServiceObject();
    
    public String getSortZoneForPack(PackType pack) {
        Optional<Map<String, String>> config = getPackAwaySortZone().stream()
                .filter(c -> c.get(PACK_TYPE).equalsIgnoreCase(pack.toString())).findFirst();
        return config.isPresent() ? config.get().get(SORT_ZONE) : null;
    }
    
    public List<Map<String, String>> getPackAwaySortZone() {
        if (packAwaySortZone == null)
            fetchPackAwaySortZoneConfig();

        return packAwaySortZone;
    }

    public void fetchPackAwaySortZoneConfig() {
        String configValue = fetchConfig(PACKAWAY_SORT_ZONE).get(0);

        ObjectMapper mapper = new ObjectMapper();
        try {
            ConfigurationServices.packAwaySortZone = mapper.readValue(configValue,
                    new TypeReference<List<Map<String, String>>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
            StepDetail.addDetail("Exception while parsing PackAwaySortZone config response", false);
        }
    }
    
    public List<String> fetchConfig(String configKey) {
        serviceObject.ConfigIdAndValueRetrieval(ReadHostConfiguration.LOCATION_NUMBER.value(), configKey, APP_NAME,
                MODULE_NAME);

        return ConfigurationSteps.getConfigValues();
    }

}
