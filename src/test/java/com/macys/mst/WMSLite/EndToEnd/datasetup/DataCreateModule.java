package com.macys.mst.WMSLite.EndToEnd.datasetup;


import com.macys.mst.WMSLite.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.datasetup.SelectDataModule;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.model.ExamplesTable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

@Slf4j
public class DataCreateModule extends SelectDataModule {


    public static List<String> stnList;
    public static List<String> ticketTypeList;
    public static List<String> printerIdList;

    @SuppressWarnings("rawtypes")
    @Given("Required Parameters are passed to Fetch Configurations $configuration")
    public void fetchConfigurations(ExamplesTable configuration) {
        //@Named("AppName") String appName, @Named("ModuleName") String moduleName, @Named("ConfigKey") String configKey){
    	String response = "";
        Iterator rows = configuration.getRows().iterator();
        while (rows.hasNext()) {
            Map<String, String> row = (Map) rows.next();
            String configKey = (String) row.get("CONFIG_KEY");
            String moduleName = (String) row.get("MODULE_NAME");
            String appName = (String) row.get("APP_NAME");

            String genericConfigUrl = ReadHostConfiguration.GENERIC_CONFIG_URL.value();
            String getGenericConfigURL = genericConfigUrl + ReadHostConfiguration.LOCATION_NUMBER.value() + "/app/" + appName + "/module/" + moduleName + "/" + configKey;

            response = RestUtilities.getRequestResponse(getGenericConfigURL);

            stnList = getParamFromConfiValue(response, (String) row.get("ConfigParam1"));
            ticketTypeList = getParamFromConfiValue(response, (String) row.get("ConfigParam2"));
            printerIdList = getParamFromConfiValue(response, (String) row.get("ConfigParam3"));
        }
    }


    @Given("required Parameters are passed to $param new configuration $table")
    public String addConfigurations(String param, ExamplesTable table) {
        String genericConfigUrl = ReadHostConfiguration.GENERIC_CONFIG_URL.value();

        String response = "";
        Iterator rows = table.getRows().iterator();
        while (rows.hasNext()) {
            Map<String, String> row = (Map) rows.next();
            String configKey = (String) row.get("CONFIG_KEY");
            String configValue = (String) row.get("CONFIG_VALUE");

            if (param.equalsIgnoreCase("update")) {
                log.info("Get generic config url {}/{}", ReadHostConfiguration.CONFIG_HOST_NAME.value(), configKey);
                response = RestUtilities.getRequestResponse(ReadHostConfiguration.CONFIG_HOST_NAME.value() + "/" + configKey);
                Assert.assertTrue("Response from the get serevice" + response, response != null);
                JSONArray array = new JSONArray(response);
                log.info("Updating  the configuration for config key:[{}] with values: [{}] and id of the configuration is {}", new Object[]{configKey, configValue, array.getJSONObject(0).getInt("id")});
                StepDetail.addDetail("Updating  the configuration for config key:" + configKey + "with values: " + configValue + " and id of the configuration is " + array.getJSONObject(0).getInt("id"), true);
                response = RestUtilities.putRequestResponse(genericConfigUrl + array.getJSONObject(0).getInt("id"), configValue);
                CommonUtils.deleteRequest(ReadHostConfiguration.DELETE_CACHE_URL.value(), 308);

            }

        }
        return response;
    }

    public List<String> getParamFromConfiValue(String reponse, String param) {
        log.info("Response value :{}", reponse);

        String paramValues = "";
        List<Map<String, String>> listOfParamConfigs = new ArrayList<>();
        List<String> paramList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(reponse);
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            paramValues = jsonObject.getString("configValue");
            JSONArray configValueArray = new JSONArray(paramValues);
            listOfParamConfigs = CommonUtils.getListOfMapsFromJsonArray(configValueArray);
            for (Map<String, String> configMap : listOfParamConfigs) {
                paramList.add(configMap.get(param));
            }
        }
        log.info("List of {} is {}", param, paramList);
        StepDetail.addDetail("List of " + param + " from the config servcie is: " + paramList, true);
        return paramList;
    }


    @Given("Required Parameters are passed to Fetch Configurations")
    public String fetchConfigurations(@Named("App Name") String appName, @Named("Module Name") String moduleName,
                                      @Named("Configuration") String configuration) {
        StringBuilder sb = new StringBuilder();
        sb.append(ReadHostConfiguration.GENERIC_CONFIG_URL.value());
        sb.append(ReadHostConfiguration.LOCATION_NUMBER.value());
        sb.append("/app/").append(appName);
        sb.append("/module/").append(moduleName);
        sb.append("/");
        sb.append(configuration);

        log.info("Configuration URL:[{}]", sb);
        
        return RestUtilities.getRequestResponse(sb.toString());
    }

    public String getNextContainer(String prefix, String length) throws Exception {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String thirteenDigits = String.valueOf(timestamp.getTime());
        String sevenDigits = String.format("%-7s", prefix).replace(' ', '0');
        String nextContainer = sevenDigits + thirteenDigits;
        nextContainer = nextContainer.substring(0, Integer.parseInt(length));
        return nextContainer;
    }

    public Map<String, String> getPrefixLengthFromConfigusingcntnrtype(String configKey) throws Exception {
        String response = "";
        String configValue = "";
        HashMap<String, String> configvaluesMap = new HashMap<>();
        response = RestUtilities.getRequestResponse(ReadHostConfiguration.CONTAINER_FORMAT_CONFIG_URL.value());

        if (!response.isEmpty()) {
            JSONArray jsnarrinital = new JSONArray(response);
            JSONObject jsonObject = jsnarrinital.getJSONObject(0);
            JSONArray jsnarr = new JSONArray(jsonObject.get("configValue").toString());
            // JSONObject jsonObject= new JSONObject(jsnarr.get(0));
            // configValue=jsonObject.getString("configValue");
            List<Map<String, String>> configvaluesMapList = CommonUtils.getListOfMapsFromJsonArray(jsnarr);
            for (int i = 0; i <= configvaluesMapList.size(); i++) {
                if (configvaluesMapList.get(i).get("type").equalsIgnoreCase(configKey)) {
                    configvaluesMap = (HashMap<String, String>) configvaluesMapList.get(i);
                    break;
                }
            }

            log.info("prefix and length: {}", configvaluesMap);

        } else {
            log.info("Null response from the config Servcie");
            Assert.assertTrue(false);
        }
        return configvaluesMap;
    }

    @Given("Delete the invetory for the container/location $location")
    public void deleteInventoryForLocation(String location) {
        StepDetail.addDetail("Deleting the inventory before scanning the location:" + location, true);
        String sericeUrl = ReadHostConfiguration.DELETE_INVENTORY_URL.value().replace("{container}", location);
        CommonUtils.deleteRequest(sericeUrl, 204);
    }



}
