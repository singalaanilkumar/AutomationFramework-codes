package com.macys.mst.DC2.EndToEnd.stepdefinitions;
/* @author: B006110
 * @date: 09/26/2019
 * */

import com.macys.mst.DC2.EndToEnd.configuration.OrderFullfilmentService;
import com.macys.mst.DC2.EndToEnd.configuration.WSMServices;
import com.macys.mst.DC2.EndToEnd.configuration.WavingServices;
import com.macys.mst.DC2.EndToEnd.configuration.WhmTestingService;
import com.macys.mst.DC2.EndToEnd.constants.MHE_MessagingReverseJSON;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.HoldAndFlowSQL;
import com.macys.mst.DC2.EndToEnd.model.Activity;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.RequestUtil;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.removeEnd;

@Slf4j
public class HAFSteps {
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    RequestUtil requestUtil = new RequestUtil();
    CommonUtils commonUtils = new CommonUtils();
    ValidationUtil validationUtils = new ValidationUtil();
    RandomUtil randomUtil = new RandomUtil();
    WSMServices wsmServices = new WSMServices();
    WavingServices wavingServices = new WavingServices();
    public long TestNGThreadID = Thread.currentThread().getId();
    OrderFullfilmentService orderFullfilmentService = new OrderFullfilmentService();
    WhmTestingService testingService = new WhmTestingService();
    Boolean isFreshPo = false;
    ExpectedDataProperties expectedDataProperties = new ExpectedDataProperties();

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("HAF PO details from $template")
    @Alias("PO details from $template")
    public void givenPOdetails(String template) {
        //hit the template service and get the ASN PO details
        if (StringUtils.isNotBlank(System.getProperty("poNbr")) && StringUtils.isNotBlank(System.getProperty("receiptNbr"))) {
            String purchaseOrderNbr = System.getProperty("poNbr").trim();
            String purchaseReceiptNbr = System.getProperty("receiptNbr").trim();
            CommonUtils.doJbehavereportConsolelogAndAssertion("User Provided Purchase order and Receipt Nbr", "PONbr:"+purchaseOrderNbr +" ReceiptNbr : "+purchaseReceiptNbr , true);
            dataStorage.getStoredData().put("poNbr", purchaseOrderNbr);
            dataStorage.getStoredData().put("rcptNbr", purchaseReceiptNbr);
        } else {
            Response response = testingService.getPODetailsUsingTemplateName(template);
            if (200 == response.getStatusCode()) {
                isFreshPo = true;
                String poNbr = response.jsonPath().getString("poNbr");
                String rcptNbr = response.jsonPath().getString("rcptNbr");

                dataStorage.getStoredData().put("poNbr", poNbr);
                dataStorage.getStoredData().put("rcptNbr", rcptNbr);

                CommonUtils.doJbehavereportConsolelogAndAssertion("PO,Rcpt,bolNbr = ", poNbr + "," + rcptNbr, validationUtils.validateResponseStatusCode(response, 200));
            } else {
                //do not delete kept as a workaround if usecase service fail for some reason
                Map<String, String> POdetailsfromEdp = expectedDataProperties.getPurchaseOrderDetails(template);
                dataStorage.getStoredData().put("poNbr", POdetailsfromEdp.get("PONbr").toString());
                dataStorage.getStoredData().put("rcptNbr", POdetailsfromEdp.get("RcptNbr").toString());
            }
        }
    }

    @Given("Clean up HAF data")
    public void cleanupHAFData() {
        List<String> activitiesList = new ArrayList<>();
        activitiesList.add("CASEPULL");
        activitiesList.add("PRESORTSPLIT");

        cleanExistingCases();
        cleanOpenedOrAssignedActivities(activitiesList);
        updateCompletedPresortToDeleted();
    }

    @Then("pyramid publishes CONTRECEIVE messages for all cases and validate")
    public void publishContreceiveAndValidate() throws InterruptedException {
        List<String> caseBarcodeList = (List) dataStorage.getStoredData().get("caseBarcodeList");
        String Rcpt = (String) dataStorage.getStoredData().get("rcptNbr");
        String containerBarcode="";
        //validateReceiptStatus(10);
        for (int size = 0; size < caseBarcodeList.size(); size++) {
            if (!isFreshPo) {
                if (size == 0) {
                    String sql = HoldAndFlowSQL.updateRcptBeforeFirstScan.replace("#RCPTNBR", Rcpt);
                    DBMethods.deleteOrUpdateDataBase(sql, "orders");
                } else {
                    String sql = HoldAndFlowSQL.updateRcptAfterFirstScan.replace("#RCPTNBR", Rcpt);
                    DBMethods.deleteOrUpdateDataBase(sql, "orders");
                }
            }
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String contReceiveTemplate = MHE_MessagingReverseJSON.CONT_RECEIVE;
                contReceiveTemplate = contReceiveTemplate.replace("#sequenceno#", randomUtil.getRandomValue("D-9"));
                contReceiveTemplate = contReceiveTemplate.replace("#ASN#", (String) dataStorage.getStoredData().get("ASN"));
                contReceiveTemplate = contReceiveTemplate.replace("#PO#", (String) dataStorage.getStoredData().get("poNbr"));
                contReceiveTemplate = contReceiveTemplate.replace("#Rcpt#", Rcpt);
                contReceiveTemplate = contReceiveTemplate.replace("#divertLane#", randomUtil.getRandomValue("D-7"));
                containerBarcode = caseBarcodeList.get(size);
                String messageToPublish = contReceiveTemplate;
                messageToPublish = messageToPublish.replace("#conatinerBarcode#", containerBarcode);
                CommonUtils.pyramidResponseValidation(messageToPublish);
            } else {
                containerBarcode = caseBarcodeList.get(size);
                String requestParams = "{#sequenceno:D-9,#lpnNumber:" + containerBarcode + ",#shipmentId:" + (String) dataStorage.getStoredData().get("ASN") + ",#orderNumber:"+(String) dataStorage.getStoredData().get("poNbr")+",#receiptNumber:"+Rcpt+",#divertLaneId:D-7"+"}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "CONTRECEIVE.json");
                for (String eachMessageBody : messageBody) {
                    log.info("CONTRECEIVE input JSON payload with filled values: {}", eachMessageBody);
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "UNITPUT");
                    JSONObject json = new JSONObject(eachMessageBody);
                }

            }

        }
        //after finishing publish of all the contreceive, check for the case status
        //allowed wait time to get all the cases updated
        TimeUnit.SECONDS.sleep(10);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Cases are moved to CRE status", caseBarcodeList.toString(), areContainersInExpectedStatus(caseBarcodeList, "CRE"));
    }

    @When("received RTFs for the cases and validated $values")
    public void publishRTFandValidate(ExamplesTable values) throws InterruptedException {
        Map<String, Map<Long, Integer>> caseSkuQtyMap = (Map<String, Map<Long, Integer>>) dataStorage.getStoredData().get("CaseSkuQtyMap");
        Map<String, String> row = values.getRow(0);
        String requestParams = row.get("requestParams");
        List<String> messageBody = requestUtil.getRequestBody(requestParams, row.get("templateName"));
        List<Map<String, String>> paramsToMap = requestUtil.getParmasToListOfMaps(requestParams);
        List<String> caseBarcodeList = (List) dataStorage.getStoredData().get("caseBarcodeList");

        List<String> finalRTFPayloadList = new ArrayList<>();
        int caseStartIndex = 0;
        if (messageBody.size() == paramsToMap.size()) {
            for (int index = 0; index < messageBody.size(); index++) {
                String messagePayload = messageBody.get(index);
                JSONObject messageJson = new JSONObject(messagePayload);
                String shipmentNbr = String.valueOf(messageJson.getJSONObject("shipment").getLong("orderShipmentNbr"));

                Map param = paramsToMap.get(index);
                Integer noOfCases = Integer.parseInt((String) param.get("NumberOfcases"));

                int caseEndIndex = caseStartIndex + noOfCases - 1;
                String lineItmRqstParams = "";
                Map<Long, String> skuBarcodeMap = new HashMap<>();
                Map<Long, Integer> finalSkuQtyMap = new HashMap<>();

                for (; caseStartIndex < caseBarcodeList.size(); caseStartIndex++) {
                    String caseBarcode = caseBarcodeList.get(caseStartIndex);
                    Map<Long, Integer> skuQtyMap = caseSkuQtyMap.get(caseBarcode);

                    for (Long sku : skuQtyMap.keySet()) {

                        //contruct the SkuToQty relation
                        if (finalSkuQtyMap.containsKey(sku)) {
                            finalSkuQtyMap.put(sku, finalSkuQtyMap.get(sku) + skuQtyMap.get(sku));
                        } else {
                            finalSkuQtyMap.put(sku, skuQtyMap.get(sku));
                        }

                        //construct skuToBarcode relation
                        if (skuBarcodeMap.containsKey(sku)) {
                            skuBarcodeMap.put(sku, skuBarcodeMap.get(sku) + ";" + caseBarcode);
                        } else {
                            skuBarcodeMap.put(sku, caseBarcode);
                        }
                    }

                    if (caseStartIndex == caseEndIndex) {
                        if (skuBarcodeMap.size() == finalSkuQtyMap.size()) {
                            int lineItemNumber = 1;
                            for (Long sku : skuBarcodeMap.keySet()) {
                                lineItmRqstParams += "{#shipNbr:" + shipmentNbr + ",#lnNbr:" + lineItemNumber + ",#SKU:" + sku + ",#qty:" + finalSkuQtyMap.get(sku) + ",#caseBarcode:" + skuBarcodeMap.get(sku) + "},";
                                lineItemNumber++;
                            }
                        }
                        ++caseStartIndex;
                        break;
                    }
                }

                List<String> filledLineItemList = requestUtil.getRequestBody(lineItmRqstParams, "RTFLineItem.json");
                String finalfilledLineItems = filledLineItemList.toString().replace(";", ",");
                messagePayload = messagePayload.replace("\"#lineItem\"", finalfilledLineItems);
                finalRTFPayloadList.add(messagePayload);
            }
        }
        dataStorage.getStoredData().put("RTFPayloads", finalRTFPayloadList);

        //if testing service is down comment below 5 lines of code and enable the alternate method and run
        String topicName = commonUtils.getEnvConfigValue("pubSub.topics.publishRTF");
        List<ApiResponse> responses = testingService.publishGivenPayloadsToGivenTopic(topicName, finalRTFPayloadList);
        for (ApiResponse ApiResponse : responses) {
            validationUtils.validateResponseStatusCode(ApiResponse.asResponse(), 200);
        }

        //orderFullfilmentService.publishRTF(finalRTFPayloadList); - if testing service is down use this method
        orderFullfilmentService.validatePublishedRTF();
    }

    @Then("preview and Run the wave and validate $value")
    public void previewAndRunWave(ExamplesTable value) throws InterruptedException {
        if (null != value) {
            Map<String, String> row = value.getRow(0);
            List<String> filledPayloads = requestUtil.getRequestBody(row.get("requestParams"), "Wave.json");
            List<String> waveIdsList = new ArrayList<>();
            List<String> waveNumbers = new ArrayList<>();
            for (String payload : filledPayloads) {
                wavingServices.previewWave(payload);
                waveIdsList.add((String) dataStorage.getStoredData().get("waveId"));
                TimeUnit.SECONDS.sleep(2);
                waveNumbers.add(wavingServices.runWave());
            }
            dataStorage.getStoredData().put("waveIdsList", waveIdsList);
            dataStorage.getStoredData().put("waveNumbersList", waveNumbers);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Activities & Cases are valid for waves after Waving : ", waveNumbers.toString(), validateActivitiesAndCasesAfterWaving("READY") && areWavesInExpectedStatus("RLS"));


        }
    }

    public boolean validateActivitiesAndCasesAfterWaving(String status) {
        List<String> barcodeList = (List) dataStorage.getStoredData().get("caseBarcodeList");

        return validateActivitiesByWave(barcodeList, "CASEPULL", status) &&
                areContainersInExpectedStatus(barcodeList, "RSV");
    }

    @Then("validate activities and case after picking")
    public boolean validateActivitiesAndCasesAfterPicking() {
        List<String> barcodeList = (List) dataStorage.getStoredData().get("caseBarcodeList");

        return validateActivitiesByWave(barcodeList, "CASEPULL", "COMPLETED") &&
                areContainersInExpectedStatus(barcodeList, "SPK") && areWavesInExpectedStatus("PIKIP");
    }

    public boolean validateActivitiesByWave(List<String> barcodeList, String activity, String status) {
        List<String> waveNumbers = (List) dataStorage.getStoredData().get("waveNumbersList");
        Map<String, String> caseToWaveRelation = new HashMap<>();
        boolean isValid = false;
        for (String waveNum : waveNumbers) {
            Map<String, String> getActQP = new HashMap<>();
            getActQP.put("waveNumber", waveNum);
            ApiResponse response = wsmServices.getActivities(getActQP);
            if (200 == response.getStatusCode()) {
                response.asJSONArray().length();
                for (int size = 0; size < response.asJSONArray().length(); size++) {
                    String caseBarcodeRetrieved = response.getString("[" + size + "].containerId");
                    caseToWaveRelation.put(caseBarcodeRetrieved, waveNum);
                    isValid = activity.equals(response.getString("[" + size + "].type")) &&
                            status.equals(response.getString("[" + size + "].status")) &&
                            barcodeList.contains(caseBarcodeRetrieved);
                    if (!isValid) {
                        Assert.fail("Activities are not in expected status");
                    }
                }
            }

        }
        dataStorage.getStoredData().put("caseToWaveRelation", caseToWaveRelation);
        return isValid;
    }

    @When("waves are $waveCycle and validated")
    public void wavesAreReleasedToPresort(String waveCycle) {
        List<String> waveNumbers = (List) dataStorage.getStoredData().get("waveNumbersList");

        String status = null;

        if ("releaseToPresort".equals(waveCycle)) {
            status = "PSR";
        } else if ("releaseToPTS".equals(waveCycle)) {
            status = "PTSR";
        } else if ("releaseToPick".equals(waveCycle)) {
            status = "RLS";
        } else {
            Assert.fail("Given wave lifecycle status is not supported");
        }

        for (String waveNumber : waveNumbers) {
            Response response = wavingServices.updateWaveLifecyleToGivenStatus(waveNumber, status);
            validationUtils.validateResponseStatusCode(response, 200);
        }

        areWavesInExpectedStatus(status);
        if ("releaseToPick".equals(waveCycle)) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Activities & Cases are valid for waves after Release: ", waveNumbers.toString(), validateActivitiesAndCasesAfterWaving("OPEN"));
        }
    }

    public boolean areWavesInExpectedStatus(String expectedStatus) {
        List<String> waveIds = (List) dataStorage.getStoredData().get("waveIdsList");
        boolean asExpected = false;
        for (String waveId : waveIds) {
            asExpected = wavingServices.isWaveLifeCycleAsExpected(waveId, expectedStatus);
            if (!asExpected) {
                break;
            }
        }
        return asExpected;
    }

    @When("totealloc messages and totes are validated")
    public void validateAfterPresortAndToteRelease() {
        List<String> toteBarcodeList = (List<String>) dataStorage.getStoredData().get("toteAfterPresort");
        areContainersInExpectedStatus(toteBarcodeList, "PTS");
        validateTOTEALLOCmessages();
    }

    public void cleanExistingCases() {

        String PONbr = (String) dataStorage.getStoredData().get("poNbr");
        String PORcptNbr = (String) dataStorage.getStoredData().get("rcptNbr");

        String GETASNDetailsEndpoint = commonUtils.getUrl("orderManagement.getASNDetails");
        Response response = WhmRestCoreAutomationUtils.getRequestResponse(GETASNDetailsEndpoint.replace("{PO}", PONbr).replace("{Rcpt}", PORcptNbr), new HashMap<>()).asResponse();

        //collects all the containers related to the given PO
        if (validationUtils.validateResponseStatusCode(response, 200)) {
            Map<String, Map<Long, Integer>> caseSkuQtyMap = new HashMap<>();
            String postInvEndpoint = commonUtils.getUrl("InventoryServices.createAndGetInventory");
            JSONObject responseJson = new JSONObject(response.asString());
            String ASN = String.valueOf(responseJson.getJSONObject("AsnDTO").get("poShpmtNbr"));
            dataStorage.getStoredData().put("ASN", ASN);
            JSONArray containers = responseJson.getJSONObject("AsnDTO").getJSONArray("containers");
            List<String> caseBarcodeList = new ArrayList<>();

            //performing restoration of the cases
            for (int index = 0; index < containers.length(); index++) {

                JSONObject containerJson = containers.getJSONObject(index);
                String containerBarcode = containerJson.getString("cntrNbr");
                String requestParams = "{#PO:" + PONbr + ",#Rcpt:" + PORcptNbr + ",#ASN:" + ASN + ",#barCode:" + containerBarcode + "}";
                caseBarcodeList.add(containerBarcode);
                List<String> createContainerPayload = requestUtil.getRequestBody(requestParams, "CaseForHAF.json");
                JSONArray skus = containerJson.getJSONArray("skus");

                //constructing the inventorySnapShotList
                String payloadParams = "";
                Map<Long, Integer> skuQtyMap = new HashMap<>();
                //String futureUseParamPayload ="";
                for (int skusize = 0; skusize < skus.length(); skusize++) {

                    JSONObject skuJson = skus.getJSONObject(skusize);
                    Long sku = skuJson.getLong("sku");
                    int qty = skuJson.getInt("quantity");
                    skuQtyMap.put(sku, qty);
                    payloadParams += "{#SKU:" + sku + ",#qty:" + qty + ",#ASN:" + ASN + "},";

                }
                caseSkuQtyMap.put(containerBarcode, skuQtyMap);

                List<String> filledInvSnapshotList = requestUtil.getRequestBody(payloadParams, "CaseInvSnapAttribList.json");

                String finalPayload = createContainerPayload.get(0).replace("#invSnapshot", filledInvSnapshotList.toString());

                Response getServiceResponse = getInventoryDetailsWithBarcode(containerBarcode).asResponse();

                if (validationUtils.validateResponseStatusCode(getServiceResponse, 200)) {
                    //deleting the inventory before the resurrection
                    String DELETECallEndpoint = commonUtils.getUrl("InventoryServices.deleteInventory").replace("{containerBarcode}", containerBarcode).replace("#reasonCode", "RR");
                    WhmRestCoreAutomationUtils.deleteRequestResponse(DELETECallEndpoint);
                }
                //post call to resurrect the case
                Response createInvResponse = WhmRestCoreAutomationUtils.postRequestResponse(postInvEndpoint, finalPayload).asResponse();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Case resurrected successfully :" + containerBarcode, finalPayload, validationUtils.validateResponseStatusCode(createInvResponse, 201));
            }
            dataStorage.getStoredData().put("caseBarcodeList", caseBarcodeList);
            dataStorage.getStoredData().put("CaseSkuQtyMap", caseSkuQtyMap);
            log.info("Container barcode associated with the given PO & PORcpt= " + caseBarcodeList.toString());
        } else {
            Assert.fail("No Data available for given PO & PORcpt " + PONbr + " , " + PORcptNbr);
        }
    }

    @Then("publish UNITPUT for all cartons")
    public void publishUnitpuForAllCartons() throws Exception {
        Map<String, Map<Long, Integer>> toteToSkuQtyMap = (Map<String, Map<Long, Integer>>) dataStorage.getStoredData().get("toteToSkuQtyMap");
        Map<String, String> caseToToteRelation = (Map<String, String>) dataStorage.getStoredData().get("caseToToteRelation");
        Map<String, Map<String, String>> caseStoreDeptMap = (Map<String, Map<String, String>>) dataStorage.getStoredData().get("caseStoreDeptMap");
        Map<String, String> caseToWaveRelation = (Map) dataStorage.getStoredData().get("caseToWaveRelation");
        Long skuNbr = 0L;
        Integer quantity = 0;
        String store = null;
        String dept = null;

        Map<String, String> cartonToStoreReleation = new HashMap<>();
        List<String> cartonList = new ArrayList<>();

        for (String tote : caseToToteRelation.keySet()) {
            String seqId= randomUtil.getRandomValue("D-9");
            String caseNbr = caseToToteRelation.get(tote);
            String waveNbr = caseToWaveRelation.get(caseNbr);
            String cartonNbr = randomUtil.getRandomValue("150003-D-14");
            Map<Long, Integer> skuQtyMap = toteToSkuQtyMap.get(tote);
            for (Long sku : skuQtyMap.keySet()) {
                skuNbr = sku;
                quantity = skuQtyMap.get(sku);
            }

            Map<String, String> StoreDeptMap = caseStoreDeptMap.get(caseNbr);
            for (String str : StoreDeptMap.keySet()) {
                store = str;
                dept = StoreDeptMap.get(str);
            }

            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String unitputTemplate = MHE_MessagingReverseJSON.UNITPUT;
                cartonList.add(cartonNbr);
                unitputTemplate = unitputTemplate.replace("#sequenceno#", seqId);
                unitputTemplate = unitputTemplate.replace("#conatinerBarcode#", tote);
                unitputTemplate = unitputTemplate.replace("#cartonBarcode#", cartonNbr);
                unitputTemplate = unitputTemplate.replace("#SKU#", skuNbr.toString());
                unitputTemplate = unitputTemplate.replace("#store#", store);
                unitputTemplate = unitputTemplate.replace("#dept#", dept);
                unitputTemplate = unitputTemplate.replace("#qty#", quantity.toString());
                unitputTemplate = unitputTemplate.replace("#waveNbr#", waveNbr);
                CommonUtils.doJbehavereportConsolelogAndAssertion("Generated UNITPUT messages: ", unitputTemplate, true);
                cartonToStoreReleation.put(cartonNbr, store);
                CommonUtils.pyramidResponseValidation(unitputTemplate);
            }else{
                String lineItmRqstParams = "";
                String finallineItmRqstParams = "";
                List<String> lstlineItmRqstParams = new ArrayList<>();
                lineItmRqstParams = "{#skuNbr:" + skuNbr.toString() + ",#skuQty:1}";
                List<String> messageBody = requestUtil.getRequestBody(lineItmRqstParams, "UNITPUTItemList.json");
                lstlineItmRqstParams.addAll(messageBody);
                for (String eachLineItemLst : lstlineItmRqstParams) {
                    finallineItmRqstParams = (finallineItmRqstParams + eachLineItemLst).trim() + ",";
                }
                finallineItmRqstParams = "["+removeEnd(finallineItmRqstParams, ",")+"]";
                log.info("finallineItmRqstParams: {}", finallineItmRqstParams);
                String requestParams = "{#sequenceno:"+seqId+",#sourceContainer:" + tote + ",#targetContainer:" + cartonNbr + ",#StrNbr:"+store+",#DeptNbr:"+dept+",#SKUqty:"+quantity.toString()+",#CasePack:N"+",#PONbr:"+waveNbr+",#orderSource:W"+",#RcptNbr:"+"}";
                List<String> HAFPayloads = requestUtil.getRequestBody(requestParams, "UNITPUT.json");
                for (String eachMessageBody : HAFPayloads) {
                    eachMessageBody = eachMessageBody.replace("\"#lineItem\"",finallineItmRqstParams);
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Generated HAFUNITPUT JSON messages : ", eachMessageBody, true);
                    cartonToStoreReleation.put(cartonNbr, store);
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "UNITPUT");
                }

            }
        }
        //wait time to complete all the unitput message processing
        TimeUnit.SECONDS.sleep(5);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Cartons created after UNITPUT and Valid: ", cartonList.toString(), areContainersInExpectedStatus(cartonList, "IPK"));

        dataStorage.getStoredData().put("cartonToStoreReleation", cartonToStoreReleation);
        dataStorage.getStoredData().put("cartonList", cartonList);
    }

    @Then("close all cartons")
    public void closeAllCartons() throws Exception {
        Map<String, String> cartonToStoreRelation = (Map<String, String>) dataStorage.getStoredData().get("cartonToStoreReleation");
        List<String> cartonList = (List<String>) dataStorage.getStoredData().get("cartonList");
        for (String carton : cartonToStoreRelation.keySet()) {
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String contCloseTemplate = MHE_MessagingReverseJSON.CONT_CLOSE_MESSAGE;
                contCloseTemplate = contCloseTemplate.replace("#sequenceno#", randomUtil.getRandomValue("D-9"));
                contCloseTemplate = contCloseTemplate.replace("#cartonNumber#", carton);
                contCloseTemplate = contCloseTemplate.replace("#storeLocNbr#", cartonToStoreRelation.get(carton));
                CommonUtils.doJbehavereportConsolelogAndAssertion("Generated CONTCLOSE messages: ", contCloseTemplate, true);
                CommonUtils.pyramidResponseValidation(contCloseTemplate);
            }
            else {
                String requestParams = "{#sequenceno:D-9,#container:"+carton+",#storeNbr:"+cartonToStoreRelation.get(carton)+"}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "CONTCLOSED.json");
                log.info("CONTCLOSED input JSON payload: {}", messageBody);
                for (String eachMessageBody : messageBody) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("CONTCLOSE JSON messages: ", eachMessageBody, true);
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "CONTCLOSED");
                }
            }
        }
        //wait time to complete all the contclose message processing
        TimeUnit.SECONDS.sleep(5);
        CommonUtils.doJbehavereportConsolelogAndAssertion("All cartons are closed and validated PCK : ", cartonList.toString(), areContainersInExpectedStatus(cartonList, "PCK"));
    }

    @Then("weigh and manifest all cartons")
    public void weighAllCartons() throws InterruptedException {
        List<String> cartonList = (List<String>) dataStorage.getStoredData().get("cartonList");
        for (String carton : cartonList) {
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String scanweighTemplate = MHE_MessagingReverseJSON.SCAN_WEIGH_MESSAGE;
                scanweighTemplate = scanweighTemplate.replace("#sequenceno#", randomUtil.getRandomValue("D-9"));
                scanweighTemplate = scanweighTemplate.replace("#barCode#", carton);
                scanweighTemplate = scanweighTemplate.replace("#weight#", randomUtil.getRandomValue("D-2") + "." + randomUtil.getRandomValue("D-2"));
                CommonUtils.doJbehavereportConsolelogAndAssertion("Generated SCANWEIGH messages: ", scanweighTemplate, true);
                CommonUtils.pyramidResponseValidation(scanweighTemplate);
            }else {
                String requestParams = "{#sequenceno:D-9,#carton:" + carton + ",#weight:" + randomUtil.getRandomValue("D-2") + "." + randomUtil.getRandomValue("D-2") + "}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "SCANWEIGH.json");
                for (String eachMessageBody : messageBody) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("SCANWEIGH JSON messages: ", eachMessageBody, true);
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "SCANWEIGH");
                }
            }
        }
        //given wait to process all the messages to MFT status
        TimeUnit.MINUTES.sleep(1);
        CommonUtils.doJbehavereportConsolelogAndAssertion("All cartons are Manifested and validated MFT : ", cartonList.toString(), areContainersInExpectedStatus(cartonList, "MFT"));
    }

    @Then("make all cartons shipReady")
    public void shipAllCartons() throws Exception {
        List<String> cartonList = (List<String>) dataStorage.getStoredData().get("cartonList");
        for (String carton : cartonList) {
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String shipconfirmTemplate = MHE_MessagingReverseJSON.SHIP_CONFIRM_MESSAGE;
                shipconfirmTemplate = shipconfirmTemplate.replace("#sequenceno#", randomUtil.getRandomValue("D-9"));
                shipconfirmTemplate = shipconfirmTemplate.replace("#barCode#", carton);
                shipconfirmTemplate = shipconfirmTemplate.replace("#shipLane#", randomUtil.getRandomValue("D-7"));
                CommonUtils.doJbehavereportConsolelogAndAssertion("Generated SHIPCONFIRM messages: ", shipconfirmTemplate, true);
                CommonUtils.pyramidResponseValidation(shipconfirmTemplate);
            }else {
                String requestParams = "{#sequenceno:D-9,#carton:"+carton+",#carrierLane:D-7"+"}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "SHIPCONFIRM.json");
                for (String eachMessageBody : messageBody) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("SHIPCONFIRM JSON messages: ", eachMessageBody, true);
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "SHIPCONFIRM");
                    StepDetail.addDetail("Successfully sent Ship Confirm Message", true);
                }
            }
        }
        //wait time to complete all the contclose message processing
        TimeUnit.SECONDS.sleep(5);
        CommonUtils.doJbehavereportConsolelogAndAssertion("All cartons are shipReady SHR : ", cartonList.toString(), areContainersInExpectedStatus(cartonList, "SHR"));
    }

    @Then("validate cases and totes after Presort")
    public void validateTotesAgainstCaseAfterPresort() {
        Map<String, Map<Long, Integer>> caseSkuQtyMap = (Map<String, Map<Long, Integer>>) dataStorage.getStoredData().get("CaseSkuQtyMap");
        Map<String, Map<Long, Integer>> caseToSkuQtyMap = new HashMap<>();
        Map<String, Map<Long, Integer>> toteToSkuQtyMap = new HashMap<>();
        List<String> listOfTotes = (List) dataStorage.getStoredData().get("toteAfterPresort");
        Map<String, String> caseToToteRelation = (Map<String, String>) dataStorage.getStoredData().get("caseToToteRelation");
        boolean areWavesInExpectedStatus = areWavesInExpectedStatus("PSIP");
        for (String tote : listOfTotes) {
            ApiResponse response = getInventoryDetailsWithBarcode(tote);
            if (200 == response.getStatusCode()) {
                response.equalTo("container.containerStatusCode", "SIP");
                Long sku = Long.parseLong(response.getString("inventorySnapshotList[0].item"));
                int qty = response.getInt("inventorySnapshotList[0].quantity");
                HashMap<Long, Integer> skuQtymap = new HashMap<>();
                skuQtymap.put(sku, qty);
                caseToSkuQtyMap.put(caseToToteRelation.get(tote), skuQtymap);
                toteToSkuQtyMap.put(tote, skuQtymap);
            }
        }
        boolean isCaseToToteCreationSuccess = caseSkuQtyMap.equals(caseToSkuQtyMap);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Totes and Wavelifecycle looks good", "SIP & PSIP", areWavesInExpectedStatus && isCaseToToteCreationSuccess);
        validateCaseAfterPresort();
        dataStorage.getStoredData().put("toteToSkuQtyMap", toteToSkuQtyMap);
    }


    public void cleanOpenedOrAssignedActivities(List<String> activities) {

        Map<String, String> getCallQuweryParams = new HashMap<>();
        getCallQuweryParams.put("status", "ASSIGNED,OPEN");

        List<Activity> activitiesList = new ArrayList<>();
        List<String> updateActivities = new ArrayList<>();

        List<String> typeOfActivities = new ArrayList<>();
        typeOfActivities.addAll(activities);

        for (String activityType : typeOfActivities) {
            getCallQuweryParams.put("type", activityType);
            wsmServices.fetchWsmActivities(getCallQuweryParams);

            if (dataStorage.getStoredData().get("activitiesList") != null) {
                activitiesList.addAll((List<Activity>) dataStorage.getStoredData().get("activitiesList"));
                dataStorage.getStoredData().put("activitiesList", null);
            }
        }

        if (activitiesList.size() > 0) {
            for (Activity activity : activitiesList) {
                if ("OPEN".equals(activity.getStatus())) {
                    String activityId = activity.getId();
                    Response deleteResponse = wsmServices.deleteActivitiesWithId(activityId);
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Deleted activity", activityId, validationUtils.validateResponseStatusCode(deleteResponse, 204));
                } else if ("ASSIGNED".equals(activity.getStatus())) {
                    updateActivities.add(String.valueOf(activity.getId()));
                } else {
                    Assert.fail("Activities returned are not in required status");
                }
            }
        }

        if (updateActivities.size() > 0) {
            String payloadParams = "";
            for (String id : updateActivities) {
                payloadParams += "{#id:" + id + "},";
            }
            List<String> filledActivityUpdateList = requestUtil.getRequestBody(payloadParams.trim(), "WSMActivityUpdate.json");
            Response updateResponse = wsmServices.updateWSMActivities(filledActivityUpdateList.toString());
            CommonUtils.doJbehavereportConsolelogAndAssertion("Updated the following activities to COMPLETED", updateActivities.toString(), validationUtils.validateResponseStatusCode(updateResponse, 201));
        }
    }

    public void updateCompletedPresortToDeleted() {
        List<String> caseBarcodeList = (List) dataStorage.getStoredData().get("caseBarcodeList");
        String sql = HoldAndFlowSQL.UpdateCompletedPresortSplitAsDeleted.replace("#containers", caseBarcodeList.toString()).replace("[", "").replace("]", "");
        log.info("Framed query to Update COMPLETED PRESORTSPLIT to DELETED : " + sql);
        DBMethods.deleteOrUpdateDataBase(sql, "wsm");
    }

    public static void clearPresortAisles(@NotNull String presortLoc) throws Exception {
        String aisle = presortLoc.substring(2, 4);
        String schema = "sorting";

        String getToteAllocIDSQL = HoldAndFlowSQL.selectPresortToteAllocIDs.replace("#aisle", aisle);
        String getQDIDSQL = HoldAndFlowSQL.selectPresortQDAisleIDs.replace("#aisle", aisle);

        List<String> toteAllocListOfIDs = DBMethods.getDBValueInList(getToteAllocIDSQL, schema);
        List<String> QDListOfIDs = DBMethods.getDBValueInList(getQDIDSQL, schema);

        if (toteAllocListOfIDs.size() > 0) {
            String updateToteAllocAisleSQL = HoldAndFlowSQL.updateGivenIDsToAisleZero.replace("#IDs", toteAllocListOfIDs.toString()).replace("[", "").replace("]", "");
            DBMethods.deleteOrUpdateDataBase(updateToteAllocAisleSQL, schema);
        }

        if (QDListOfIDs.size() > 0) {
            String updateQDAisleSQL = HoldAndFlowSQL.updateGivenIDsToAisleZeroInQD.replace("#IDs", QDListOfIDs.toString()).replace("[", "").replace("]", "");
            DBMethods.deleteOrUpdateDataBase(updateQDAisleSQL, schema);
        }
    }

    public static ApiResponse getInventoryDetailsWithBarcode(String containerBarcode) {
        String getCallEndPoint = new CommonUtils().getUrl("InventoryServices.createAndGetInventory");
        Map<String, String> getCallqueryParams = new HashMap<>();
        getCallqueryParams.put("barcode", containerBarcode);
        return WhmRestCoreAutomationUtils.getRequestResponse(getCallEndPoint, getCallqueryParams);
    }

    public void validateCaseAfterPresort() {
        List<String> caseBarcodeList = (List) dataStorage.getStoredData().get("caseBarcodeList");
        for (String barcode : caseBarcodeList) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Case is deleted after presorting: ", barcode, validationUtils.validateResponseStatusCode(getInventoryDetailsWithBarcode(barcode).asResponse(), 204));
        }
    }

    public Boolean areContainersInExpectedStatus(List<String> containers, String expectedStatus) {
        List<Boolean> status = new ArrayList<>();
        //the delay is intentional to make sure all the messages/updates are processed
        try {
            TimeUnit.SECONDS.sleep(3);
            for (String container : containers) {
                status.add(isContainerInExpectedStatus(container, expectedStatus));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !status.contains(false);
    }

    public void validateTOTEALLOCmessages() {

        Map<String, Map<Long, Integer>> toteToSkuQtyMap = (Map<String, Map<Long, Integer>>) dataStorage.getStoredData().get("toteToSkuQtyMap");
        Map<String, String> caseToToteRelation = (Map<String, String>) dataStorage.getStoredData().get("caseToToteRelation");
        Map<String, Map<String, String>> caseStoreDeptMap = (Map<String, Map<String, String>>) dataStorage.getStoredData().get("caseStoreDeptMap");
        Map<String, String> caseToWaveRelation = (Map) dataStorage.getStoredData().get("caseToWaveRelation");

        List<String> waveNumbers = (List<String>) dataStorage.getStoredData().get("waveNumbersList");

        List<String> incomingPayloadList = new ArrayList<>();
        for (String waveNbr : waveNumbers) {
            Map<String, String> filterQP = new HashMap<>();
            filterQP.put("messageType", "TOTEALLOC");
            filterQP.put("textFilter", waveNbr);
            String endPoint = commonUtils.getUrl("messagingService.getMessagesWithGivenFilters");
            ApiResponse response = WhmRestCoreAutomationUtils.getRequestResponse(endPoint, filterQP);

            if (200 == response.getStatusCode()) {
                JSONObject jsonObject = response.asJSONObject();
                JSONArray jsonArray = jsonObject.getJSONObject("MessageResponseDTO").getJSONArray("messages");

                for (int size = 0; size < jsonArray.length(); size++) {
                    incomingPayloadList.add(jsonArray.getJSONObject(size).getString("incomingPayload"));
                }
            }
        }

        if (toteToSkuQtyMap.size() == incomingPayloadList.size()) {
            boolean isValid = false;
            for (String expTote : toteToSkuQtyMap.keySet()) {
                String originalCase = caseToToteRelation.get(expTote);
                for (int size = 0; size < incomingPayloadList.size(); size++) {
                    String payload = incomingPayloadList.get(size);

                    if (payload.contains(expTote)) {
                        incomingPayloadList.remove(size);
                        Map<Long, Integer> skuQtyMap = toteToSkuQtyMap.get(expTote);
                        Map<String, String> storeDeptMap = caseStoreDeptMap.get(originalCase);
                        String waveNbr = caseToWaveRelation.get(originalCase);

                        for (Long sku : skuQtyMap.keySet()) {
                            isValid = payload.contains(sku.toString()) && payload.contains("\"quantity\":" + skuQtyMap.get(sku)) && payload.contains(waveNbr);
                        }

                        if (isValid) {
                            for (String store : storeDeptMap.keySet()) {
                                isValid = payload.contains(store) && payload.contains(storeDeptMap.get(store));
                                CommonUtils.doJbehavereportConsolelogAndAssertion("TOTEALLOC", payload, isValid);
                            }
                        } else {
                            Assert.fail("Totealloc doesn't have expected data");
                        }
                        break;
                    }
                }
            }

        } else {
            Assert.fail("Generated Totealloc count is not as expected");
        }

    }

    public Boolean isContainerInExpectedStatus(String container, String expectedStatus) {
        String response =  commonUtils.getContainerDetailsbyBarcode(container);
        JsonPath containerDetail = new JsonPath(response);
        if (null!= response && !response.isEmpty()) {
            if(CommonUtils.packageFlag){
                CommonUtils.doJbehavereportConsolelogAndAssertion("Container Status validated as "+expectedStatus+" for Carton: Carton ID", container, expectedStatus.equals(containerDetail.getString("[0].status")));
            }else{
                CommonUtils.doJbehavereportConsolelogAndAssertion("Container Status validated as "+expectedStatus+" for Carton: Carton ID", container, expectedStatus.equals(containerDetail.getString("container.containerStatusCode")));
            }
            return true;
        } else {
            return false;
        }
    }

    @When("closecarton message subscriber is available")
    public void createAndSubscribeClosecartonMessageSubscriber() {
        orderFullfilmentService.createCloseCartonMessageSubscriber();
        log.info("################################ Subscriber started and running ################################");
    }

    @Then("validate closecarton messages are published and valid")
    public void subscriberIntialize() {
        List<String> cartonList = (List<String>) dataStorage.getStoredData().get("cartonList");
        List<JSONObject> closecartonMessages = orderFullfilmentService.getCloseCartonJson(cartonList);
        validateHAFClosecartonMMSMessages(closecartonMessages);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Close carton messages are published and count is :", String.valueOf(closecartonMessages.size()), closecartonMessages.size() == cartonList.size());
    }

    private void validateHAFClosecartonMMSMessages(List<JSONObject> closecartonMessages) {

        List<String> cartonList = (List<String>) dataStorage.getStoredData().get("cartonList");
        List<Map<String, String>> expectedDataList = new ArrayList<>();
        List<Map<String, String>> actualDataList = new ArrayList<>();

        if (null != closecartonMessages && closecartonMessages.size() > 0) {
            for (JSONObject closecartonJson : closecartonMessages) {
                Map<String, String> actualDataMap = new HashMap<>();
                JSONObject orderHeader = closecartonJson.getJSONObject("fulfillmentOrder").getJSONObject("orderHeader");
                JSONObject shipmentJson = closecartonJson.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0);
                JSONObject cartonContentJson = shipmentJson.getJSONObject("cartonContentList").getJSONArray("cartonContent").getJSONObject(0);

                actualDataMap.put("orderId", orderHeader.getString("orderId"));
                actualDataMap.put("orderType", orderHeader.getString("orderType"));
                actualDataMap.put("orderSubType", orderHeader.getString("orderSubType"));
                actualDataMap.put("sourceApplicationId", orderHeader.getString("sourceApplicationId"));
                actualDataMap.put("eventType", orderHeader.getString("eventType"));
                actualDataMap.put("fulfillmentLocationNbr", String.valueOf(shipmentJson.getInt("fulfillmentLocationNbr")));
                actualDataMap.put("itemSkuUpc", String.valueOf(cartonContentJson.getLong("itemSkuUpc")));
                actualDataMap.put("itemPackedQuantity", String.valueOf(cartonContentJson.getInt("itemPackedQuantity")));
                actualDataList.add(actualDataMap);
            }
        } else {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Messages didin't find or couldn't read : ", "Please validate manually in google console", false);
        }

        for (String carton : cartonList) {
            ApiResponse response = getInventoryDetailsWithBarcode(carton);
            Map<String, String> expectedDataMap = new HashMap<>();
            expectedDataMap.put("orderId", carton);
            expectedDataMap.put("orderType", "Movement");
            expectedDataMap.put("orderSubType", "TRQ");
            expectedDataMap.put("sourceApplicationId", "Backstage");
            expectedDataMap.put("eventType", "SHPCONFIRM");
            expectedDataMap.put("fulfillmentLocationNbr", "7254");
            expectedDataMap.put("itemSkuUpc", response.getString("inventorySnapshotList[0].item"));
            expectedDataMap.put("itemPackedQuantity", String.valueOf(response.getInt("inventorySnapshotList[0].quantity")));
            expectedDataList.add(expectedDataMap);
        }

        Assert.assertEquals(expectedDataList, actualDataList);
    }

    public static ApiResponse getlocationDetails(String locationNbr) {
        String getCallEndPoint = new CommonUtils().getUrl("CycleCountServices.location");
        getCallEndPoint = getCallEndPoint.replace("#locationNbr", locationNbr);
        return WhmRestCoreAutomationUtils.getRequestResponse(getCallEndPoint);
    }

    public void validateReceiptStatus(int expectedStatus) {
        String GETPODetailsEndpoint = commonUtils.getUrl("orderManagement.getPODetails");
        String poNbr = (String) dataStorage.getStoredData().get("poNbr");
        String rcpt = (String) dataStorage.getStoredData().get("rcptNbr");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("poNbr", poNbr);
        ApiResponse response = WhmRestCoreAutomationUtils.getRequestResponse(GETPODetailsEndpoint, queryParams);

        if (200 == response.getStatusCode()) {
            JSONObject responseJson = response.asJSONObject();
            JSONArray poInquiry = responseJson.getJSONArray("poInquiry");
            for (int size = 0; size < poInquiry.length(); size++) {
                JSONObject jsonPoDtl = poInquiry.getJSONObject(size);
                if (rcpt.equals(String.valueOf(jsonPoDtl.getLong("receiptNbr")))) {
                    int statusCode = jsonPoDtl.getInt("rcptStatNbr");
                    String rcptStatus = jsonPoDtl.getString("rcptStatus");
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Rcpt code & status is Expected = " + expectedStatus + " Actual", statusCode + "," + rcptStatus, expectedStatus == statusCode);
                    break;
                }
            }
        } else {
            CommonUtils.doJbehavereportConsolelogAndAssertion("ordermanagement-service response status ", response.getStatusCode() + " : PO details not found in Order schema please check in DB", false);
        }
    }
}
