package com.macys.mst.DC2.EndToEnd.configuration;
/*
@Author: B006110
@Date: 10/10/19
 */

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.response.Response;
import org.junit.Assert;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class WavingServices {

    StepsDataStore dataStorage = StepsDataStore.getInstance();
    CommonUtils commonUtils = new CommonUtils();
    ValidationUtil validationUtils = new ValidationUtil();

    public void previewWave(String payload) {
        String endPoint = commonUtils.getUrl("WavingServices.previewWave");
        Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, payload).asResponse();
        if (validationUtils.validateResponseStatusCode(response, 201)) {
            String waveId = response.jsonPath().getString("id");
            dataStorage.getStoredData().put("waveId", waveId);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Preview wave successful & waveID :" + waveId, response.asString(), true);
        } else {
            Assert.fail(LocalTime.now() + " : Wave preview failed for given payload - " + payload);
        }
    }

    public String runWave() {
        String waveId = (String) dataStorage.getStoredData().get("waveId");
        String endPoint = commonUtils.getUrl("WavingServices.runWave").replace("#id", waveId);
        Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, "{}").asResponse();
        String waveNumber = null;
        if (validationUtils.validateResponseStatusCode(response, 201)) {
            waveNumber = response.jsonPath().getString("wave.waveNbr");
            CommonUtils.doJbehavereportConsolelogAndAssertion("Run wave successful & waveNumber :" + waveNumber, response.asString(), true);
        } else {
            Assert.fail(LocalTime.now() + " : Wave run failed for given waveID: " + waveId);
        }
        return waveNumber;
    }

    public Response updateWaveLifecyleToGivenStatus(String waveNbr, String status) {
        String endPoint = commonUtils.getUrl("WavingServices.updateWaveLifeCycle").replace("#waveNbr", waveNbr).replace("#status", status);
        return WhmRestCoreAutomationUtils.putRequestResponse(endPoint, "{}").asResponse();
    }

    public boolean isWaveLifeCycleAsExpected(String waveId, String expectedLifeCycle) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("enabled", "1");
        ApiResponse response = getWaveLifeCycles(waveId, queryParams);
        //expectation is at a time wave has only one active cycle
        response.equalTo("[0].status", expectedLifeCycle);
        return true;
    }

    public ApiResponse getWaveLifeCycles(String id, Map<String, String> queryParams) {
        String endpoint = commonUtils.getUrl("WavingServices.getWaveLifeCycle").replace("#id", id);
        if (null != queryParams) {
            return WhmRestCoreAutomationUtils.getRequestResponse(endpoint, queryParams);
        } else {
            return WhmRestCoreAutomationUtils.getRequestResponse(endpoint, new HashMap<>());
        }
    }
}
