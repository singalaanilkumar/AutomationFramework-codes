package com.macys.mst.DC2.EndToEnd.configuration;

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.foundationalServices.utils.ExpectedDataProperties;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhmTestingService {

    CommonUtils commonUtils = new CommonUtils();

    public Response getPODetailsUsingTemplateName(String template){
        String getUsecaseEndpoint = commonUtils.getUrl("TestingServices.usecase").replace("{template}", template);
        Map<String, String > queryParams = new HashMap<>();
        queryParams.put("whmStatus","OPEN,ACK");
        Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).queryParams(queryParams).log().all().when().contentType(ContentType.JSON).get(getUsecaseEndpoint);
        return response;
    }

    //for multiple payload publishing to same topic
    public List<ApiResponse> publishGivenPayloadsToGivenTopic(String topic, List<String> payloads) {
        ArrayList<ApiResponse> apiResponses = new ArrayList<>();
        for (String payload : payloads) {
            apiResponses.add(publishGivenPayloadToGivenTopic(topic,payload));
        }
        return apiResponses;
    }

    //for single payload publishing
    public ApiResponse publishGivenPayloadToGivenTopic(String topic, String payload){
        String getPublishMessageEndpoint = commonUtils.getUrl("TestingServices.publishMessage").replace("{topicName}", topic);
        return WhmRestCoreAutomationUtils.postRequestResponse(getPublishMessageEndpoint, payload);
    }
}
