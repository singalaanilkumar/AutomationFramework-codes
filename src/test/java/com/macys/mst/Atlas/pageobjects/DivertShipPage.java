package com.macys.mst.Atlas.pageobjects;

import com.google.common.collect.Table;
import com.macys.mst.Atlas.execdrivers.ExecutionConfig;
import com.macys.mst.Atlas.utilmethods.CommonUtils;
import com.macys.mst.Atlas.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.model.ExamplesTable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DivertShipPage extends BasePage {

    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private CommonUtils commonUtils = new CommonUtils();

    public DivertShipPage(WebDriver driver) {
        super(driver);
    }

    public void performDivertShipUsingAPI(ExamplesTable values) {
        boolean logRequest = true;
//        String packageNo = "10000000000043236391";
//        String actualWeight = "2";
        String packageNo = dataStorage.getStoredData().get("package_number").toString();
        String actualWeight = dataStorage.getStoredData().get("estimated_weight").toString();
        String userId = ExecutionConfig.appUIUserName;
        if (1 == values.getRows().size()) {
            Map<String, String> row = values.getRow(0);
            Map<String, String> queryParams = commonUtils.getParamsToMap(row.get("queryParams"));
            queryParams.put("actualWeight", actualWeight);
            queryParams.put("userId", userId);
            String endPoint = commonUtils.getUrl(row.get("requestUrl")).replace("#packageNo", packageNo);
            Response response = logRequest ? (Response) RestAssured.given().queryParams(queryParams).contentType(ContentType.JSON).log().method().log().uri().log().params().post(endPoint, new Object[0]) : (Response) RestAssured.given().queryParams(queryParams).contentType(ContentType.JSON).post(endPoint, new Object[0]);
            if (response.getStatusCode() == 200) {
                    Assert.assertTrue("DIVERTSHIP SUCCESSFULLY COMPLETED".equals(response.jsonPath().getString("returnMessage")));
                    StepDetail.addDetail(response.jsonPath().getString("returnMessage"),true);
                }
        }
    }
}


