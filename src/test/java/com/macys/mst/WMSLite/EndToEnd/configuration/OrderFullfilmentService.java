package com.macys.mst.WMSLite.EndToEnd.configuration;

import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.PubSubUtil;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OrderFullfilmentService {

    CommonUtils commonUtils = new CommonUtils();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    PubSubUtil pubSubUtil = new PubSubUtil();
    String projectId = commonUtils.getEnvConfigValue("GCPProjId");
    String subscription;

    public void publishRTF(List<String> listOfPayload) {

        String topic = commonUtils.getEnvConfigValue("pubSub.topics.publishRTF");

        try {
            pubSubUtil.publishMessage(projectId, topic, listOfPayload);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Publish RTF", projectId + "\n" + topic + "\n" + listOfPayload, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Publishing RTF is failed");
        }
    }

    public void validatePublishedRTF() throws InterruptedException {
        //safe delay to integrate the order to backstage
        TimeUnit.SECONDS.sleep(10);

        boolean isValid = false;

        String expectedStatus = "ENR";
        List<String> RTFPayloads = (List) dataStorage.getStoredData().get("RTFPayloads");
        String orderIds = "";

        for (int index = 0; index < RTFPayloads.size(); index++) {
            if (index != RTFPayloads.size() - 1) {
                orderIds += new JSONObject(RTFPayloads.get(index)).getJSONObject("orderHeader").getLong("orderID") + ",";
            } else {
                orderIds += new JSONObject(RTFPayloads.get(index)).getJSONObject("orderHeader").getLong("orderID");
            }
        }
        Map<String, String> queryParamsFilled = new HashMap<>();
        queryParamsFilled.put("orderIds", orderIds);
        ApiResponse response = getOrderDetails(queryParamsFilled);
        dataStorage.getStoredData().put("RTFResponse",response);

        Map<String, Map<String, String>> caseStoreDeptMap = new HashMap<>();

        if (response.getStatusCode() == 200) {
            JSONObject responseInJson = new JSONObject(response.asString());
            JSONArray ordersFetched = responseInJson.getJSONArray("orderDto");
            String[] orders = orderIds.split(",");

            for (int i = 0; i < orders.length; i++) {
                JSONObject orderJson = ordersFetched.getJSONObject(i);
                String storeNumber = String.valueOf(orderJson.getJSONObject("shipment").getInt("shipToLocationNbr"));
                JSONArray lineItemArray = orderJson.getJSONArray("lineItem");

                for (int j = 0; j < lineItemArray.length(); j++) {
                    JSONObject lineItemJSON = lineItemArray.getJSONObject(j);
                    JsonPath lineItemJPath = new JsonPath(lineItemJSON.toString());
                    String deptNbr = lineItemJPath.getString("deptNbr");

                    List<String> caseList = lineItemJPath.getList("source");
                    String[] splitcases = caseList.get(0).split(",");
                    for(String caseBarcode:splitcases){
                        Map<String, String> storeDeptMap = new HashMap<>();
                        storeDeptMap.put(storeNumber,deptNbr);
                        caseStoreDeptMap.put(caseBarcode,storeDeptMap);
                    }
                    isValid = lineItemJPath.getString("status").equalsIgnoreCase(expectedStatus);
                    if (!isValid) {
                        Assert.fail("RTF is not in ENR status");
                    }
                }
            }
            dataStorage.getStoredData().put("caseStoreDeptMap",caseStoreDeptMap);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Validated the order(s) ", ordersFetched.toString(), isValid);
        } else {
            Assert.fail(LocalTime.now() + " : No response found ");
        }
    }

    public ApiResponse getOrderDetails(Map<String, String> queryParams) {
        String endPoint = commonUtils.getEnvConfigValue("services.OrderfulfillmentServices.GetRTF");
        log.info("Request url:{}, QueryParam:{}", endPoint, queryParams);
        return WhmRestCoreAutomationUtils.getRequestResponse(endPoint, queryParams);
    }

    public void createCloseCartonMessageSubscriber(){
        subscription = "pull-whm-orderfulfillment-mms-shipcntr-uate2e-east4";
        try {
            //pubSubUtil.deleteSubscription(subscription);
            String topicId = commonUtils.getEnvConfigValue("pubSub.topics.closeCartonMessageForMMS");
            pubSubUtil.createSubscription(subscription,topicId);
            pubSubUtil.subscribeProject(subscription);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> getCloseCartonJson(List<String> cartonNumbers) {
        List<JSONObject> closeCartonJsons = new ArrayList<>();
        try {
            //allowed delay to get all the closecarton message get published
            TimeUnit.SECONDS.sleep(15);
            for (String carton : cartonNumbers) {
                String messagePulled = pubSubUtil.readMessages("\"orderId\":\"" + carton + "\"", 0);

                if(messagePulled!=null) {
                    closeCartonJsons.add(new JSONObject(messagePulled));
                }else{
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Couldn't find close carton json for carton: ",carton,false);
                }
            }
            pubSubUtil.stopSubscriber();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                pubSubUtil.deleteSubscription(subscription);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return closeCartonJsons;
    }
}
