package com.macys.mst.DC2.EndToEnd.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.mst.DC2.EndToEnd.model.Activity;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.Constants;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
public class WSMServices {

    public static final String ATTRIBUTES = "attributes";
    private static Logger logger = Logger.getLogger(WSMServices.class.getName());

    CommonUtils commonUtils = new CommonUtils();
    StepsDataStore dataStore = StepsDataStore.getInstance();
    public static final String ACTIVITY_TYPE = "type";
    public static final String CONTAINER_TYPE = "containerType";
    public static final String ATTRIBUTE_KEY_PO_NBR = "poNbr";
    public static final String STATUS = "status";
    public static final String PID = "pid";
    public static final String UPC = "upc";
    public static final String ID = "id";


    public Map<String, String> fetchWsmActivitiesById(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);

        return fetchWsmActivities(params);
    }

    public Map<String, String> fetchWsmActivities(Map<String, String> params) {
        Map<String, String> wsmActivity = new HashMap<>();
        String url = ReadHostConfiguration.WMS_ACTIVITY_URL.value();
        String activityResponse = CommonUtils.getRequestResponse(url + CommonUtils.getQueryString(params));
        log.info("Get WSM activities response : " + activityResponse);
        if (!activityResponse.isEmpty()) {
            Activity[] activities = CommonUtils.getClientResponse(activityResponse, new TypeReference<Activity[]>() {
            });

            List<Activity> act = Arrays.asList(activities);
            dataStore.getStoredData().put("activitiesList", act);
            act.forEach(activity -> {
                if (!StringUtils.isEmpty(activity.getId()))
                    wsmActivity.put("id", activity.getId());
                if (!StringUtils.isEmpty(activity.getType()))
                    wsmActivity.put("type", activity.getType());
                if (!StringUtils.isEmpty(activity.getQty()))
                    wsmActivity.put("qty", activity.getQty());
                if (!StringUtils.isEmpty(activity.getStatus()))
                    wsmActivity.put("status", activity.getStatus());
                if (!StringUtils.isEmpty(activity.getAttributes().getPoNbr()))
                    wsmActivity.put("poNbr", activity.getAttributes().getPoNbr());
                if (!StringUtils.isEmpty(activity.getAttributes().getPid()))
                    wsmActivity.put("pid", activity.getAttributes().getPid());
                if (!StringUtils.isEmpty(activity.getContainerId()))
                    wsmActivity.put("containerId", activity.getContainerId());
                if (!StringUtils.isEmpty(activity.getContainerType()))
                    wsmActivity.put("containerType", activity.getContainerType());
                if (!StringUtils.isEmpty(activity.getActor()))
                    wsmActivity.put("actor", activity.getActor());
                if (!StringUtils.isEmpty(activity.getLocnNbr()))
                    wsmActivity.put("locnNbr", activity.getLocnNbr());
                if (!StringUtils.isEmpty(activity.getUpc()))
                    wsmActivity.put("upc", activity.getUpc());
                if (!StringUtils.isEmpty(activity.getTotalQty()))
                    wsmActivity.put("totalQty", activity.getTotalQty());
            });
        }
        return wsmActivity;
    }

    public Response updateWSMActivities(String payload) {
        return WhmRestCoreAutomationUtils.putRequestResponse(commonUtils.getUrl("wsmServices.updateActivities"), payload).asResponse();
    }

    public Response deleteActivitiesWithId(String activityId) {
        return WhmRestCoreAutomationUtils.deleteRequestResponse(commonUtils.getUrl("wsmServices.deleteActivityOnId").replace("{activityId}", activityId), new HashMap<>()).asResponse();
    }

    public ApiResponse getActivities(Map<String, String> queryParams) {
        return WhmRestCoreAutomationUtils.getRequestResponse(commonUtils.getUrl("wsmServices.getActivities"), queryParams);
    }

    public List<Map<String, String>> fetchOpenAndAssignedWsmActivitiesForMode(Constants.SortMode mode, String modeValue) {
        Map<String, String> params = new HashMap<>();
        params.put(CONTAINER_TYPE, "BinBox");
        params.put(STATUS, "OPEN");
        params.put(ACTIVITY_TYPE, "Packaway");

        switch (mode) {
            case SKU:
                params.put(UPC, modeValue);
                break;
            case PO:
                params.put(ATTRIBUTE_KEY_PO_NBR, modeValue);
                break;
            case PID:
                params.put(PID, modeValue);
                break;
            default:
                return new ArrayList<>();
        }

        // fetch open activities
        List<Map<String, String>> activities = fetchedWsmActivities(params);
        // fetch assigned activites
        params.put(STATUS, "ASSIGNED");
        activities.addAll(fetchedWsmActivities(params));
        return activities;
    }

    public List<Map<String, String>> fetchedWsmActivities(Map<String, String> params) {
        String url = ReadHostConfiguration.getWmsActivityUrl() + CommonUtils.getQueryString(params);

        logger.info("request path : " + url);
        Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps())
                .contentType(ContentType.JSON).get(url, new Object[0]);

        StepDetail.addDetail("request path :  " + url, true);
        logger.info("Get WSM activities response : " + response.asString());

        if (response == null || response.getStatusCode() != 200 && response.getStatusCode() != 201)
            return new ArrayList<>();

        try {
            return parseWsmActivities(response.asString());
        } catch (IOException e) {
            e.printStackTrace();
            StepDetail.addDetail("Exception while parsing WSM activities in sku mode", false);
            return new ArrayList<>();
        }
    }

    private List<Map<String, String>> parseWsmActivities(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> parsedActivities = mapper.readValue(response,
                new TypeReference<List<Map<String, Object>>>() {
                });

        List<Map<String, String>> activities = new ArrayList<>();

        parsedActivities.forEach(act -> {
            Map<String, String> activity = new HashMap<>();
            act.forEach((key, value) -> {
                if (key.equals(ATTRIBUTES))
                    ((Map<String, Object>) value)
                            .forEach((attrKey, attrValue) -> activity.put(attrKey, String.valueOf(attrValue)));
                else
                    activity.put(key, String.valueOf(value));
            });
            activities.add(activity);
        });
        return activities;
    }
}
