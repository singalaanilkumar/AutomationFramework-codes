package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.macys.mst.DC2.EndToEnd.configuration.*;
import com.macys.mst.DC2.EndToEnd.constants.MHE_MessagingReverseJSON;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLOrderFullFilment;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.model.*;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateBinPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PackAwayPullPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.RunnerPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.BuildPalletPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.*;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.removeEnd;

@Slf4j
public class PackawayPullSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private static final String UNIT_PUT = "UNITPUT";
    private static final String CONT_CLOSED = "CONTCLOSED";
    private static final String NEW_STORE = "N000";
    private static String packawayActivityTypeConfigValue = "";
    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(BuildPalletPage.class.getName());
    private PackAwayPullPage packAwayPullPage = PageFactory.initElements(driver, PackAwayPullPage.class);
    private RunnerPage runnerPage = PageFactory.initElements(driver, RunnerPage.class);
    private WSMServices wsmService = new WSMServices();
    private RequestUtil requestUtil = new RequestUtil();
    private CommonUtils commonUtils = new CommonUtils();
    private ValidationUtil validationUtils = new ValidationUtil();
    private RandomUtil randomUtil = new RandomUtil();
    private WavingServices wavingServices = new WavingServices();
    private WhmTestingService testingService = new WhmTestingService();
    public long TestNGThreadID = Thread.currentThread().getId();
    private ExpectedDataProperties expdataForMessageBasicValidation = new ExpectedDataProperties();
    private StepsContext stepsContext;
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    public List<String> listOfcasesAndBinboxes;
    CreateBinPage createBinPage = PageFactory.initElements(driver, CreateBinPage.class);
    public PackawayPullSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    // Order Steps

    @Then("Publish Multiple RTF message $values")
    public void publishMultipleRTFMessage(ExamplesTable values) {
        List<String> rtfList = new ArrayList<String>();
        Table<String, String, Map<String, String>> rtfRequestValues = HashBasedTable.create();
        Table<String, String, String> rtfRequests = HashBasedTable.create();

        String orderID = null;

        if (1 <= values.getRowCount()) {
            for (int i = 0; i < values.getRowCount(); i++) {
                Map<String, String> row = values.getRow(i);

                List<String> messageBody = requestUtil.getRequestBody(row.get("orderParams"), row.get("templateName"));
                Map<String, String> RTFFilledRandomValue = requestUtil.getRandomeValueMaps().get(0);

                String lineItemSnipBody = requestUtil.getRequestBodyFromFile("src/test/resources/RequestTemplates/" + row.get("lineItemTemplate"));
                String lineItemJSON = null;
                int lineNbr = 1;
                for (String lineItem : StringUtils.split(row.get("lineParams"), "{(.*?)}")) {
                    String lineItemBodyTemp = lineItemSnipBody;
                    String[] lineItemParams = StringUtils.split(lineItem, ",");
                    for (String lineItemParam : lineItemParams) {
                        String[] keyValue = lineItemParam.split(":");
                        lineItemBodyTemp = lineItemBodyTemp.replace(keyValue[0], keyValue[1]);
                    }
                    lineItemBodyTemp = lineItemBodyTemp.replace("#lnNbr", String.valueOf(lineNbr++));

                    if (StringUtils.isEmpty(lineItemJSON)) {
                        lineItemJSON = lineItemBodyTemp;
                    } else {
                        lineItemJSON = lineItemJSON + "," + lineItemBodyTemp;
                    }
                }

                String rtfRequest = messageBody.get(0).replace("#lineItem", lineItemJSON);
                // Replace orderID with existing order ID if not generated newly
                if (RTFFilledRandomValue.containsKey("#orderID")) {
                    orderID = RTFFilledRandomValue.get("#orderID");
                } else {
                    RTFFilledRandomValue.put("#orderID", orderID);
                }
                rtfRequestValues.put(RTFFilledRandomValue.get("#orderID"), RTFFilledRandomValue.get("#shipNbr"), RTFFilledRandomValue);

                // Replace generated values across lineItem elements
                for (String key : RTFFilledRandomValue.keySet()) {
                    rtfRequest = rtfRequest.replaceAll(key, RTFFilledRandomValue.get(key));
                }
                rtfList.add(rtfRequest);
                rtfRequests.put(RTFFilledRandomValue.get("#orderID"), RTFFilledRandomValue.get("#shipNbr"), rtfRequest);
            }
            dataStorage.getStoredData().put("rtfRequestValues", rtfRequestValues);

            //Publish RTFs
            String topic = commonUtils.getEnvConfigValue(values.getRow(0).get("topic"));
            try {
                List<ApiResponse> responses = testingService.publishGivenPayloadsToGivenTopic(topic, rtfList);
                for (ApiResponse ApiResponse : responses) {
                    validationUtils.validateResponseStatusCode(ApiResponse.asResponse(), 200);
                }
                dataStorage.getStoredData().put("publishedRTFs", rtfRequests);
                CommonUtils.doJbehavereportConsolelogAndAssertion("Publish RTF Success",
                        "Topic: " + topic + "\n" +
                                "RTF requests: " + rtfList, true);
            } catch (Exception e) {
                e.printStackTrace();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Publish RTF Success",
                        "Topic: " + topic + "\n" +
                                "RTF requests: " + rtfList, false);
            }
        } else {
            throw new IncorrectDataException("Requires atleast one row of data");
        }

    }

    @SuppressWarnings("unchecked")
    @Then("validate Published RTF $waveCount wave$values")
    @Alias("validate RTF order status for $waveCount wave$values")
    public void validatePublishedRTF(String waveCount, ExamplesTable values) throws InterruptedException {
        //safe delay to integrate the order to backstage
        TimeUnit.SECONDS.sleep(10);
        Table<String, String, String> rtfsToVerify;
        Table<String, String, String> verifiedRTFs = HashBasedTable.create();

        if ("before".equalsIgnoreCase(waveCount)) {
            rtfsToVerify = (Table<String, String, String>) dataStorage.getStoredData().get("publishedRTFs");
        } else {
            rtfsToVerify = (Table<String, String, String>) dataStorage.getStoredData().get(waveCount + "RTFs");
        }

        if (1 == values.getRows().size()) {

            Map<String, String> row = values.getRow(0);
            String endPoint = commonUtils.getUrl(row.get("requestUrl"));
            String expectedStatus = row.get("expectedStatus");

            Set<String> orderIDs = rtfsToVerify.rowKeySet();
            Map<String, String> queryParamsSplit = commonUtils.getParamsToMap(row.get("queryParams"));
            Map<String, String> queryParamsFilled = new HashMap<>();

            for (String key : queryParamsSplit.keySet()) {
                if (key.equalsIgnoreCase("orderIds") && row.get("queryParams").contains("PREVSTEP")) {
                    queryParamsFilled.put(key, String.join(",", orderIDs));
                } else if (key.equals("orderID")) {
                    queryParamsFilled.put(key, queryParamsSplit.get("#orderID"));
                } else {
                    queryParamsFilled.put(key, randomUtil.getRandomValue(queryParamsSplit.get(key)));
                }
            }

            Response response = WhmRestCoreAutomationUtils.getRequestResponse(endPoint, queryParamsFilled).asResponse();
            if (response.getStatusCode() == 200) {
                JSONArray rtfsFetched = new JSONObject(response.asString()).getJSONArray("orderDto");

                for (Cell<String, String, String> cell : rtfsToVerify.cellSet()) {
                    String rtfPublished = rtfsToVerify.get(cell.getRowKey(), cell.getColumnKey());
                    String rtfFetched = "";
                    for (int i = 0; i < rtfsFetched.length(); i++) {
                        rtfFetched = "";
                        JsonPath rtfFetchedPath = new JsonPath(rtfsFetched.getJSONObject(i).toString());
                        String orderID = rtfFetchedPath.getString("orderHeader.orderID");
                        String shipmentID = rtfFetchedPath.getString("shipment.orderShipmentNbr");
                        if (cell.getRowKey().equals(orderID) && cell.getColumnKey().equals(shipmentID)) {
                            rtfFetched = rtfsFetched.getJSONObject(i).toString();
                            break;
                        }
                    }

                    if (StringUtils.isNotBlank(rtfFetched)) {
                        Map<String, String> shipmentPublished = CommonUtils.getMapFromJson(new JSONObject(rtfPublished).getJSONObject("shipment").toString());
                        Map<String, String> shipmentFetched = CommonUtils.getMapFromJson(new JSONObject(rtfFetched).getJSONObject("shipment").toString());
                        Assert.assertEquals(shipmentPublished, shipmentFetched, "Shipment Details are not as published");

                        Map<String, String> orderPublished = CommonUtils.getMapFromJson(new JSONObject(rtfPublished).getJSONObject("orderHeader").toString());
                        orderPublished.remove("orderConfirmTimeStamp");
                        orderPublished.remove("eventTimestamp");

                        Map<String, String> orderFetched = CommonUtils.getMapFromJson(new JSONObject(rtfFetched).getJSONObject("orderHeader").toString());
                        orderFetched.remove("orderConfirmTimeStamp");
                        orderFetched.remove("eventTimestamp");
                        Assert.assertEquals(orderPublished, orderFetched, "Order Header details are not as published");

                        JSONArray lineItemsArray;
                        if ("before".equalsIgnoreCase(waveCount)) {
                            lineItemsArray = new JSONObject(rtfPublished).getJSONObject("lineItemList").getJSONArray("lineItem");
                        } else {
                            lineItemsArray = new JSONObject(rtfPublished).getJSONArray("lineItem");
                        }
                        JSONArray lineItemsFetched = new JSONObject(rtfFetched).getJSONArray("lineItem");
                        for (int i = 0; i < lineItemsArray.length(); i++) {
                            boolean lineItemFound = false;
                            JsonPath lineItemPublished = new JsonPath(lineItemsArray.get(i).toString());
                            for (int j = 0; j < lineItemsFetched.length(); j++) {
                                JsonPath lineItemFetched = new JsonPath(lineItemsFetched.get(j).toString());
                                if (lineItemPublished.getInt("orderLineItemNbr") == lineItemFetched.getInt("orderLineItemNbr")) {
                                    lineItemFound = true;
                                    boolean isLineItemValid = lineItemPublished.getDouble("itemSkuUpc") == lineItemFetched.getDouble("itemSkuUpc") &&
                                            lineItemPublished.getInt("itemQuantity") == lineItemFetched.getInt("itemQuantity") &&
                                            lineItemPublished.getInt("orderShipmentNbr") == lineItemFetched.getInt("orderShipmentNbr") &&
                                            lineItemFetched.getString("status").equals(expectedStatus);

                                    Assert.assertTrue(isLineItemValid, "LineItem Mismatch. Published: " + lineItemPublished + " Fetched: " + lineItemFetched);
                                    break;
                                }
                            }
                            Assert.assertTrue(lineItemFound, "LineItem Not found " + lineItemPublished.toString());
                        }
                        verifiedRTFs.put(cell.getRowKey(), cell.getColumnKey(), rtfFetched);
                    } else {
                        Assert.assertNotEquals("RTF not found for OrderID: " + cell.getRowKey() + " ,ShipmentID: " + cell.getColumnKey(), "", rtfFetched);
                    }

                    if ("before".equalsIgnoreCase(waveCount)) {
                        CommonUtils.doJbehavereportConsolelogAndAssertion("RTF Message Validated.\n",
                                "RTF Published as expected: " + rtfPublished + "\n"
                                        + "RTF Status as expected: " + expectedStatus + "\n", true);


                    } else {
                        CommonUtils.doJbehavereportConsolelogAndAssertion("RTF Status Validated.\n",
                                "RTF Published as expected: " + rtfPublished + "\n"
                                        + "RTF Status as expected: " + expectedStatus + "\n", true);
                    }

                }
                if ("before".equalsIgnoreCase(waveCount)) {
                    dataStorage.getStoredData().put("verifiedRTFs", verifiedRTFs);
                }


            } else {
                Assert.assertTrue(false, "Unable to get RTF respose.Error Statuscode: " + response.getStatusCode());
            }
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    // Inventory Steps
    @Given("Inventory Created $values")
    public void createInv(ExamplesTable values) {
        if (values.getRowCount() > 0) {
            Map<String, String> containerbarcode_Template = new HashMap<>();
            List<String> createInventoryRequests = new ArrayList<>();
            listOfcasesAndBinboxes = new ArrayList<>();
            for (Map<String, String> row : values.getRows()) {
                String endPoint = commonUtils.getUrl(row.get("requestUrl"));
                String createInventoryRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
                //to get only the generated barcode of the inventory - as of now takes only the first one,
                // if necessary code can change to support multiple request of the same row
                JSONObject jsonObject = new JSONObject(createInventoryRequest);
                listOfcasesAndBinboxes.add(jsonObject.getJSONObject("container").getString("barCode"));

                String createInventoryTemplate = row.get("templateName");
                createInventoryRequests.add(createInventoryRequest);
                containerbarcode_Template.put(createInventoryRequest, createInventoryTemplate);
                Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createInventoryRequest).asResponse();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Created Inventory",
                        "Create Inventory Endpoint: " + endPoint + "\n"
                                + "Create Inventory Request: " + createInventoryRequest + "\n"
                                + "Create Inventory Response Statuscode: " + response.getStatusCode(),
                        validationUtils.validateResponseStatusCode(response, 201));
            }
            dataStorage.getStoredData().put("inventoryContainerList", listOfcasesAndBinboxes);
            dataStorage.getStoredData().put("createInventoryRequests", createInventoryRequests);
        } else {
            throw new IncorrectDataException("Require atleast one row of data");
        }
    }

    @Given("Inventory created with valid location $values")
    public void createValidInventoryForCycleCount(ExamplesTable values){
        createInv(values);
        dataStorage.getStoredData().put("inventoryListFromValidLocation",listOfcasesAndBinboxes);

    }

    @Given("Inventory created for LWContainers Cycle Count $values")
    public void createValidInventoryForPartialCycleCount(ExamplesTable values){
        createInv(values);
        dataStorage.getStoredData().put("inventoryListForPartialCycleCount",listOfcasesAndBinboxes);

    }

    @Given("Containers created with VSC Status for Cycle Count $values")
    public void createVSCBinBox(ExamplesTable values){
        createInv(values);
        dataStorage.getStoredData().put("inventoryListPrepedBINs",listOfcasesAndBinboxes);

    }

    @Given("Containers created with LW Status for Cycle Count $values")
    public void createLWConatiners(ExamplesTable values){
        createInv(values);
        dataStorage.getStoredData().put("inventoryLost",listOfcasesAndBinboxes);

    }

    @Given("Inventory located at incorrect location $values")
    public void createAndLocateInventoryAtIncorrectLocation(ExamplesTable values){
        createInv(values);
        dataStorage.getStoredData().put("inventoryListFromIncorrectLocation",listOfcasesAndBinboxes);

    }

    @Given("Inventory created without location $values")
    public void createInventoryWithoutLocation(ExamplesTable values){
        createInv(values);
        dataStorage.getStoredData().put("inventoryListWithoutLocation",listOfcasesAndBinboxes);

    }

    @Given("Random Inventory with Valid Barcode Created")
    public void randomInvWithValidBarcode() {
        List<String> randomBarcodeswithValidPrefix = new ArrayList<>();
        randomBarcodeswithValidPrefix.add(randomUtil.getRandomValue("95-D-18"));
        randomBarcodeswithValidPrefix.add(randomUtil.getRandomValue("000-D-17"));
        randomBarcodeswithValidPrefix.add(randomUtil.getRandomValue("063-D-17"));
        dataStorage.getStoredData().put("randomBarcodeswithValidPrefix",randomBarcodeswithValidPrefix);
    }

    @Given("Random Inventory with InValid Barcode Created")
    public void randomInvWithInvalidBarcode() {
        List<String> randomBarcodeswithInValidPrefix = new ArrayList<>();
        randomBarcodeswithInValidPrefix.add(randomUtil.getRandomValue("50-D-18"));
        randomBarcodeswithInValidPrefix.add(randomUtil.getRandomValue("15-D-18"));
        randomBarcodeswithInValidPrefix.add(randomUtil.getRandomValue("12-D-18"));
        dataStorage.getStoredData().put("randomInvWithInvalidBarcode",randomBarcodeswithInValidPrefix);
    }

    @SuppressWarnings("unchecked")
    @Then("validate the inventory using GET service $values")
    @Alias("validate and Store the inventory using GET service $values")
    public void getInventory(ExamplesTable values) {
        if (1 == values.getRowCount()) {
            Map<String, String> row = values.getRow(0);
            String businessFn = row.get("businessFn");
            String endPoint = commonUtils.getUrl(row.get("requestUrl"));
            Map<String, String> userPassedQueryParams = Splitter.on(",").withKeyValueSeparator(":").split(row.get("queryParams"));
            List<String> inventoryResponses = new ArrayList<>();
            List<String> createInventoryRequests = (List<String>) dataStorage.getStoredData().get("createInventoryRequests");

            for (String request : createInventoryRequests) {
                JsonPath jpath = new JsonPath(request);
                Map<String, String> queryParams = new HashMap<>();
                for (String key : userPassedQueryParams.keySet()) {
                    queryParams.put(key, jpath.get(userPassedQueryParams.get(key)));
                }
                Response response = WhmRestCoreAutomationUtils.getRequestResponse(endPoint, queryParams).asResponse();
                inventoryResponses.add(response.asString());
                if (!"HAF".equalsIgnoreCase(businessFn)) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("GET Response", endPoint + "\n" + response.asString(), validationUtils.validateResponseStatusCode(response, 200));
                    validationUtils.validateResponseBody(request, response);
                }
            }
            dataStorage.getStoredData().put("GetInventoryResponses", inventoryResponses);
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @SuppressWarnings("unchecked")
    @Then("create parent containers $values")
    public void createParentPallet(ExamplesTable values) {
        List<String> createContainerRequests = new ArrayList<String>();
        if (1 == values.getRowCount()) {

            Map<String, String> row = values.getRow(0);
            List<String> createInventoryRequests = (List<String>) dataStorage.getStoredData().get("createInventoryRequests");
            String endPoint = commonUtils.getUrl(row.get("requestUrl"));

            if (row.get("requestParams").contains("PREVSTEP")) {
                // loop and generate parent container requests for all existing child containers
                for (String inventoryRequest : createInventoryRequests) {
                    JsonPath inventoryPath = new JsonPath(inventoryRequest);
                    String barcode = (String) inventoryPath.get("container.containerRelationshipList.findAll {containerRelationshipList -> containerRelationshipList.parentContainerType=='PLT'}.parentContainer[0]");
                    if (StringUtils.isNotEmpty(barcode)) {
                        String createContainerRequest = requestUtil.getRequestBody(row.get("requestParams").replace("PREVSTEP", barcode), row.get("templateName")).get(0);
                        createContainerRequests.add(createContainerRequest);
                    }
                }
            } else {
                String createContainerRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
                createContainerRequests.add(createContainerRequest);
            }
            dataStorage.getStoredData().put("createContainerRequests", createContainerRequests);
            for (String createContainerRequest : createContainerRequests) {
                Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createContainerRequest).asResponse();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Created Container",
                        "Create Container Endpoint: " + endPoint + "\n"
                                + "Create Container Request: " + createContainerRequest + "\n"
                                + "Create Container Response Statuscode: " + response.getStatusCode(),
                        validationUtils.validateResponseStatusCode(response, 201));
            }
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @SuppressWarnings("unchecked")
    @Then("locate pallet to packaway location $values")
    public void locateContainer(ExamplesTable values) {

        if (1 == values.getRowCount()) {
            List<String> createContainerRequests = (List<String>) dataStorage.getStoredData().get("createContainerRequests");
            Map<String, String> row = values.getRow(0);

            String endPoint = commonUtils.getUrl(row.get("requestUrl"));
            String locateContainerRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);

            JsonPath jsonPath = new JsonPath(locateContainerRequest);
            String ptwLoc = jsonPath.getString("container.containerRelationshipList[0].parentContainer");
            dataStorage.getStoredData().put("putawayLocation",ptwLoc);

            for (String containerRequest : createContainerRequests) {
                JsonPath containerPath = new JsonPath(containerRequest);
                String sourceBarcode = (String) containerPath.get("barCode");
                Response response = WhmRestCoreAutomationUtils.putRequestResponse(endPoint.replace("#sourceBarcode", sourceBarcode), locateContainerRequest).asResponse();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Located Container",
                        "Locate Container Endpoint: " + endPoint.replace("#sourceBarcode", sourceBarcode) + "\n"
                                + "Locate Container Request: " + locateContainerRequest + "\n"
                                + "Locate Container Response Statuscode: " + response.getStatusCode(),
                        validationUtils.validateResponseStatusCode(response, 201));
            }
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @SuppressWarnings("unchecked")
    @Then("locate BinBox to packaway location $values")
    public void locateBinBoxes(ExamplesTable values) {

        if (1 == values.getRowCount()) {
            List<String> createInventoryRequests = (List<String>) dataStorage.getStoredData().get("createInventoryRequests");
            Map<String, String> row = values.getRow(0);
            String endPoint = commonUtils.getUrl(row.get("requestUrl"));
            String locateContainerRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);

            for (String inventoryRequest : createInventoryRequests) {
                JsonPath inventoryContainerPath = new JsonPath(inventoryRequest);
                String parentContainer = (String) inventoryContainerPath.get("container.containerRelationshipList[0].parentContainer");
                //if parent container is null, locate binbox to location
                if (parentContainer == null) {
                    String sourceBarcode = (String) inventoryContainerPath.get("container.barCode");
                    Response response = WhmRestCoreAutomationUtils.putRequestResponse(endPoint.replace("#sourceBarcode", sourceBarcode), locateContainerRequest).asResponse();
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Located Container",
                            "Locate Container Endpoint: " + endPoint.replace("#sourceBarcode", sourceBarcode) + "\n"
                                    + "Locate Container Request: " + locateContainerRequest + "\n"
                                    + "Locate Container Response Statuscode: " + response.getStatusCode(),
                            validationUtils.validateResponseStatusCode(response, 201));
                }
            }
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @Given("inventory is cleared for the given SKUs$values")
    @When("there is no any inventory available for the given sku$values")
    public void findAndDeleteInventory(ExamplesTable values) {
        if (values.getRows().size() > 0) {
            for (Map<String, String> row : values.getRows()) {
                String GETCallEndpoint = commonUtils.getUrl(row.get("getRequestUrl"));
                String DELETECallEndpoint = commonUtils.getUrl(row.get("deleteRequestUrl"));
                Map<String, String> processedGetQP = commonUtils.getParamsToMap(row.get("GETQueryParams"));
                Map<String, String> processedDeleteQP = commonUtils.getParamsToMap(row.get("DELETEQueryParams"));

                Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                if (200 == GETResponse.statusCode()) {
                    List<String> listOfContainerToDelete = new JsonPath(GETResponse.asString()).getList("container");

                    for (String barcode : listOfContainerToDelete) {
                        Response deleteResponse = WhmRestCoreAutomationUtils.deleteRequestResponse(DELETECallEndpoint.replace("#sourceBarcode", barcode), processedDeleteQP).asResponse();
                        Assert.assertTrue(validationUtils.validateResponseStatusCode(deleteResponse, 200) || validationUtils.validateResponseStatusCode(deleteResponse, 204), barcode + " not DELETED");
                    }

                    Response getRespAfterDelete = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Deleted all existing Inventory ",
                            listOfContainerToDelete.toString(),
                            validationUtils.validateResponseStatusCode(getRespAfterDelete, 204));
                } else {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("No Inventory available or Unable to get inventory",
                            "NA",
                            validationUtils.validateResponseStatusCode(GETResponse, 204));
                }
            }
        } else {
            throw new IncorrectDataException("Require atleast row of data");
        }
    }

    @Given("Get RTF Details are cleared for SKUs$value")
    public void clearRTFDetails(ExamplesTable value) {
        if (1 == value.getRows().size()) {
            Map<String, String> row = value.getRow(0);
            for (String key : row.keySet()) {
                row.put(key, randomUtil.getRandomValue(row.get(key)));
            }

            String listOfHoldDates = joinList(getListofDates(row.get("effectiveStartDate"), row.get("effectiveEndDate")));
            String listOfShipDates = joinList(getListofDates(row.get("shipOutStartDate"), row.get("shipOutEndDate")));

            String query = SQLOrderFullFilment.RTFUpdateQuery;

            query = query.replace("#listOfHoldDates", listOfHoldDates);
            query = query.replace("#listOfShipDates", listOfShipDates);
            query = query.replace("#statusLists", row.get("statusLists"));
            query = query.replace("#deptNumbers", row.get("deptNumbers"));

            DBMethods.deleteOrUpdateDataBase(query, "orderfulfillment");
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @Given("RTF Details are cleared for SKUs$value")
    public void clearRTFDetailsAPI(ExamplesTable value) {
        Map<String, String> lineItemValues = new HashMap<String, String>();

        JSONObject lineStatusdto = new JSONObject();
        JSONArray lineStatus = new JSONArray();

        if (1 == value.getRows().size()) {
            Map<String, String> row = value.getRow(0);
            String GETCallEndpoint = commonUtils.getUrl(row.get("getRequestUrl"));
            String GETQueryParams = row.get("GETQueryParams");
            Map<String, String> processedGetQP = requestUtil.getRandomParamsfromMap(GETQueryParams);
            Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
            log.info("Open Order Response: {}", GETResponse.asString());
            if (200 == GETResponse.statusCode()) {
                JSONArray rtfsFetched = new JSONObject(GETResponse.asString()).getJSONArray("orderDto");
                for (int i = 0; i < rtfsFetched.length(); i++) {
                    JsonPath rtfFetchedPath = new JsonPath(rtfsFetched.getJSONObject(i).toString());
                    String orderID = rtfFetchedPath.getString("orderHeader.orderID");
                    String shipmentID = rtfFetchedPath.getString("shipment.orderShipmentNbr");

                    JSONArray lineItemArray = new JSONArray();
                    JSONArray lineitem = rtfsFetched.getJSONObject(i).getJSONArray("lineItem");
                    for (int m = 0; m < lineitem.length(); m++) {
                        JsonPath lineitemPath = new JsonPath(lineitem.getJSONObject(m).toString());
                        lineItemValues.put("itemSkuUpc", lineitemPath.getString("itemSkuUpc"));
                        lineItemValues.put("allocatedQty", lineitemPath.getString("itemQuantity"));
                        lineItemValues.put("status", "ALC");
                        lineItemValues.put("waveId", "999");
                        lineItemValues.put("waveNbr", "999");
                        lineItemArray.put(lineItemValues);
                    }

                    JSONObject linestatusJson = new JSONObject();
                    linestatusJson.put("orderId", orderID);
                    linestatusJson.put("orderShipmentNbr", shipmentID);
                    linestatusJson.put("orderLines", lineItemArray);
                    lineStatus.put(linestatusJson);
                }
                lineStatusdto.put("lineStatusDto", lineStatus);
                log.info("Update RTF request", lineStatusdto.toString());

                String endPoint = commonUtils.getUrl(row.get("getUpdateRequestUrl"));
                Response response = WhmRestCoreAutomationUtils.putRequestResponse(endPoint, lineStatusdto.toString()).asResponse();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Update RTF Request published as expected.\n",
                        "Update Endpoint: " + endPoint + "\n"
                                + "Update Request: " + lineStatusdto.toString() + "\n"
                                + "Update Response StatusCode: " + response.getStatusCode(), validationUtils.validateResponseStatusCode(response, 200));

                GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                CommonUtils.doJbehavereportConsolelogAndAssertion("No Open RTF found for required dates.\n",
                        "Get Endpoint: " + endPoint + "\n"
                                + "Get Response: " + GETResponse.asString() + "\n"
                                + "Get Response StatusCode: " + GETResponse.getStatusCode(), validationUtils.validateResponseStatusCode(GETResponse, 204));
            } else {
                log.info("********************Unable to get RTFs or No RTFs Found************************** " + GETResponse.statusCode());
            }
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }

    }

    @When("PackawayPull activities are completed for $waveCount wave$values")
    public void completeBinPullAndBinPullSplitActivities(String waveCount, ExamplesTable values) throws Exception {

        String getBinPullSplitQueryParams = "waveNumber:#waveNumber,status:OPEN";

        ArrayList<String> altLocations = new ArrayList<String>();
        HashMap<String, String> skuAction = new HashMap<String, String>();
        HashMap<String, String> skuAltLocation = new HashMap<String, String>();
        List<String> binBoxList = new ArrayList<String>();
        if (0 < values.getRowCount()) {
            for (Map<String, String> row : values.getRows()) {
                skuAction.put(row.get("SKU"), row.get("ACTION"));
                if ("ALT".equals(row.get("ACTION"))) {
                    skuAltLocation.put(row.get("SKU"), row.get("LOCATION"));
                }
            }
        }

        log.info("Activities will be completed with SKU Action " + skuAction);

        String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");

        String palletBarCode;
        String GETCallEndpoint = commonUtils.getUrl("WSM.getActivities");
        Map<String, String> processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
        Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
        JSONArray waveActivities = new JSONArray(GETResponse.asString());
        log.info("Get Activity Response for Wave: " + waveNumber + "\n" + GETResponse.asString());

        if ((200 == GETResponse.statusCode())) {
            Map<String, Set<String>> locationActivityDescMap = getLocationActivityTypeDescForWave(waveActivities);

            packAwayPullPage.navigatetoPackAwayPull();

            for (String location : locationActivityDescMap.keySet()) {

                packAwayPullPage.enterPackAwayLocation(location);

                for (String activityProcesAreaDesc : locationActivityDescMap.get(location)) {
                    String activityType = getActivityTypeByDesc(activityProcesAreaDesc);
                    String activityDesc = activityProcesAreaDesc.substring(0, activityProcesAreaDesc.length() - 4);

                    processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
                    processedGetQP.put("processArea", activityProcesAreaDesc.substring(activityProcesAreaDesc.length() - 3));
                    processedGetQP.put("locationNbr", location);
                    processedGetQP.put("type", activityType);
                    GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                    JSONArray wsmActivities = new JSONArray(GETResponse.asString());

                    packAwayPullPage.selectActivityProcessAreaDesc(activityProcesAreaDesc);

                    /*if (activityProcesAreaDesc.contains("Bin Pull Split")) {
                        packAwayPullPage.verifyPackAwayLocation(location);
                    }*/

                    palletBarCode = packAwayPullPage.scanRandomPallet();

                    for (int i = 0; i < wsmActivities.length(); i++) {
                        JsonPath wsmActivity = new JsonPath(wsmActivities.get(i).toString());
                        String activityID = wsmActivity.getString("id");
                        String binboxBarcode = wsmActivity.getString("containerId");
                        String upc = wsmActivity.getString("upc");

                        if (("NIL".equals(skuAction.get(upc)))) {
                            packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                            packAwayPullPage.scanBinBox(binboxBarcode);
                            validateWSMStatus(activityID, "COMPLETED");
                            if ("BINPULL".equals(activityType)) {
                                binBoxList.add(binboxBarcode);
                            }
                        } else if (("SUB".equals(skuAction.get(upc)))) {
                            packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                            String substituteBINBOX = createSubstituteBinBox(binboxBarcode, location);
                            log.info(substituteBINBOX);
                            packAwayPullPage.selectSubstituteBinBox(substituteBINBOX);
                            validateWSMStatus(activityID, "CANCELLED");
                            HashMap<String, String> activityAttributes = new HashMap<String, String>();
                            activityAttributes.put("container", substituteBINBOX);
                            activityAttributes.put("type", activityType);
                            activityAttributes.put("waveNumber", waveNumber);
                            validateWSMStatus(activityAttributes, "COMPLETED");
                            if ("BINPULL".equals(activityType)) {
                                binBoxList.add(substituteBINBOX);
                            }
                        } else if (("ALT".equals(skuAction.get(upc)))) {
                            packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                            String alternateBINBOX = createSubstituteBinBox(binboxBarcode, skuAltLocation.get(upc));
                            log.info(alternateBINBOX);
                            packAwayPullPage.selectAlternateBinBox(alternateBINBOX);
                            validateWSMStatus(activityID, "CANCELLED");
                            HashMap<String, String> activityAttributes = new HashMap<String, String>();
                            activityAttributes.put("container", alternateBINBOX);
                            activityAttributes.put("type", activityType);
                            activityAttributes.put("waveNumber", waveNumber);
                            validateWSMStatus(activityAttributes, "OPEN");
                            altLocations.add(skuAltLocation.get(upc));
                        } else {
                            packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                            packAwayPullPage.scanBinBox(binboxBarcode);
                            validateWSMStatus(activityID, "COMPLETED");
                            if ("BINPULL".equals(activityType)) {
                                binBoxList.add(binboxBarcode);
                            }
                        }
                    }
                    packAwayPullPage.validateActivityAlertMessage();
                    packAwayPullPage.enterDropLocation();
                }

                packAwayPullPage.validateZoneAlertMessage();
            }

            // Scan Alternate Locations and Complete activities - Need separate for block to avoid endless loop

            for (String location : altLocations) {

                processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
                processedGetQP.put("locationNbr", location);
                GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                JSONArray altActivities = new JSONArray(GETResponse.asString());
                Set<String> locationActivityDescSet = getLocationActivityTypeDescForWave(altActivities).get(location);

                packAwayPullPage.enterPackAwayLocation(location);

                for (String activityProcesAreaDesc : locationActivityDescSet) {
                    String activityType = getActivityTypeByDesc(activityProcesAreaDesc);
                    String activityDesc = activityProcesAreaDesc.substring(0, activityProcesAreaDesc.length() - 4);

                    processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
                    processedGetQP.put("processArea", activityProcesAreaDesc.substring(activityProcesAreaDesc.length() - 3));
                    processedGetQP.put("locationNbr", location);
                    processedGetQP.put("type", activityType);

                    GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                    JSONArray wsmActivities = new JSONArray(GETResponse.asString());

                    packAwayPullPage.selectActivityProcessAreaDesc(activityProcesAreaDesc);
                    if (activityProcesAreaDesc.contains("Bin Pull Split")) {
                        packAwayPullPage.verifyPackAwayLocation(location);
                    }

                    palletBarCode = packAwayPullPage.scanRandomPallet();

                    for (int i = 0; i < wsmActivities.length(); i++) {
                        JsonPath wsmActivity = new JsonPath(wsmActivities.get(i).toString());
                        String activityID = wsmActivity.getString("id");
                        String binboxBarcode = wsmActivity.getString("containerId");
                        if ("BINPULL".equals(activityType)) {
                            binBoxList.add(binboxBarcode);
                        }

                        packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                        packAwayPullPage.scanBinBox(binboxBarcode);
                        validateWSMStatus(activityID, "COMPLETED");
                    }
                    packAwayPullPage.validateActivityAlertMessage();
                    packAwayPullPage.enterDropLocation();
                }

                packAwayPullPage.validateZoneAlertMessage();

            }
            packAwayPullPage.clickBackButton();
            dataStorage.getStoredData().put(waveCount + "binBoxList", binBoxList);

        } else {
            log.info("Unable to get wsmActivties or no Activities exist for Wave Number " + waveNumber + ". WSM Response Code " + GETResponse.statusCode());
        }
    }

    @SuppressWarnings("unchecked")
    @Then("preview and Run the PCKPULL $waveCount wave and validate$value")
    public void previewAndRunWave(String waveCount, ExamplesTable value) {
        if (1 == value.getRows().size()) {
            Map<String, String> row = value.getRow(0);

            Table<String, String, String> rtfsPublished = (Table<String, String, String>) dataStorage.getStoredData().get("verifiedRTFs");
            Table<String, String, String> toBeWavedRTFs = HashBasedTable.create();

            String filledPayload = requestUtil.getRequestBody(row.get("requestParams"), "Wave_PCK.json").get(0);

            String listOfHoldDates = joinList(getListofDates(requestUtil.getRandomeValueMaps().get(0).get("#efctStartDt"), requestUtil.getRandomeValueMaps().get(0).get("#efctEndDt")));
            String listOfShipDates = joinList(getListofDates(requestUtil.getRandomeValueMaps().get(0).get("#startShpDt"), requestUtil.getRandomeValueMaps().get(0).get("#endShpDt")));

            for (Cell<String, String, String> cell : rtfsPublished.cellSet()) {
                JsonPath rtfPath = new JsonPath(cell.getValue());
                if (listOfHoldDates.contains(rtfPath.getString("shipment.holdDate")) && listOfShipDates.contains(rtfPath.getString("shipment.expectedShipDate")))
                    toBeWavedRTFs.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
            }

            dataStorage.getStoredData().put(waveCount + "RTFs", toBeWavedRTFs);

            wavingServices.previewWave(filledPayload);
            String waveNumber = wavingServices.runWave();
            dataStorage.getStoredData().put(waveCount + "Number", waveNumber);
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @Then("Release $waveCount wave")
    public void releaseWaveonUI(String waveCount) throws Exception {
        String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");
        Response rlsResponse = wavingServices.updateWaveLifecyleToGivenStatus(waveNumber, "RLS");
        CommonUtils.doJbehavereportConsolelogAndAssertion("Release Wave successful. WaveNumber:", waveNumber, validationUtils.validateResponseStatusCode(rlsResponse, 200));
    }


    @When("WSM activities are cleared$values")
    public void cleanUpPackawayPullActivity(ExamplesTable values) throws Exception {
        if (values.getRows().size() > 0) {
            for (Map<String, String> row : values.getRows()) {
                String GETCallEndpoint = commonUtils.getUrl(row.get("getRequestUrl"));
                String DELETECallEndpoint = commonUtils.getUrl(row.get("deleteRequestUrl"));
                String GETQueryParams = row.get("GETQueryParams");

                Map<String, String> processedGetQP = commonUtils.getParamsToMap(GETQueryParams);

                Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                List<String> listOfActivityToDelete = new ArrayList<>();
                JSONArray listOfActivityToUnassign = new JSONArray();

                if (200 == GETResponse.statusCode()) {
                    JSONArray jsonArray = new JSONArray(GETResponse.asString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        listOfActivityToDelete.add(String.valueOf(jsonObject.get("id")));
                        if ("ASSIGNED".equals(jsonObject.getString("status"))) {
                            jsonObject.remove("actor");
                            jsonObject.remove("status");
                            jsonObject.put("status", "OPEN");
                            listOfActivityToUnassign.put(jsonObject);
                        }
                    }
                }

                if (listOfActivityToUnassign.length() != 0) {
                    Response putResponse = WhmRestCoreAutomationUtils.putRequestResponse(GETCallEndpoint, listOfActivityToUnassign.toString()).asResponse();
                    if (putResponse != null) {
                        Assert.assertTrue(validationUtils.validateResponseStatusCode(putResponse, 201), "Activities " + listOfActivityToUnassign + " not Unassigned");
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Activities unassigned ",
                                new JsonPath(listOfActivityToUnassign.toString()).getList("id", Integer.class).toString(),
                                validationUtils.validateResponseStatusCode(putResponse, 201));
                    }
                }

                String ProcessedDELETECallEndpoint = DELETECallEndpoint;
                if (!listOfActivityToDelete.isEmpty()) {
                    Response deleteResponse = WhmRestCoreAutomationUtils.deleteRequestResponse(ProcessedDELETECallEndpoint.replace("#activityIDList", String.join(",", listOfActivityToDelete))).asResponse();
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Deleted Open Activities ",
                            String.join(",", listOfActivityToDelete),
                            validationUtils.validateResponseStatusCode(deleteResponse, 204));

                    Response getAfterDelete = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Deleted Open Activities ",
                            String.join(",", listOfActivityToDelete),
                            validationUtils.validateResponseStatusCode(getAfterDelete, 204));
                } else {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("No Open Activity ", "NA", true);
                }
            }
        } else {
            throw new IncorrectDataException("Require atleast row of data");
        }
    }

    @SuppressWarnings("unchecked")
    @Then("Validate WSM Activities for $waveCount wave in $waveStatus Status$values")
    public void validateWSMActivities(String waveCount, String waveStatus, ExamplesTable values) throws Exception {
        TimeUnit.SECONDS.sleep(10);
        Table<String, String, Integer> skuActivityMapExpected = HashBasedTable.create();
        if (values.getRows().size() >= 0) {
            for (int i = 0; i < values.getRows().size(); i++) {
                skuActivityMapExpected.put(values.getRow(i).get("SKU"), values.getRow(i).get("ActivityType"), Integer.valueOf(values.getRow(i).get("Count")));
            }
        }

        String waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));
        Table<String, String, String> rtfRequests = (Table<String, String, String>) dataStorage.getStoredData().get(waveCount + "RTFs");

        Map<String, Integer> skuQTYMapE = new HashMap<String, Integer>();

        for (Cell<String, String, String> cell : rtfRequests.cellSet()) {
            JSONArray lineItemArray = new JSONObject(cell.getValue()).getJSONArray("lineItem");
            for (int j = 0; j < lineItemArray.length(); j++) {
                JsonPath lineItemPath = new JsonPath(lineItemArray.get(j).toString());
                if (!skuQTYMapE.containsKey(lineItemPath.getString("itemSkuUpc")))
                    skuQTYMapE.put(lineItemPath.getString("itemSkuUpc"), lineItemPath.getInt("openQuantity"));
                else
                    skuQTYMapE.put(lineItemPath.getString("itemSkuUpc"), skuQTYMapE.get(lineItemPath.getString("itemSkuUpc")) + lineItemPath.getInt("openQuantity"));
            }
        }

        Map<String, String> processedGetQP = new HashMap<String, String>();
        processedGetQP.put("waveNumber", waveNumber);
        processedGetQP.put("status", waveStatus);
        Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(commonUtils.getUrl("WSM.getActivities"), processedGetQP).asResponse();

        Table<String, String, Integer> skuActivityMapActual = HashBasedTable.create();
        Map<String, Integer> skuQTYMapA = new HashMap<String, Integer>();
        Table<String, String, String> splitActivities = HashBasedTable.create();
        Table<String, String, String> pckpullActivities = HashBasedTable.create();

        JSONArray waveActivities = new JSONArray(GETResponse.asString());
        if (waveActivities.length() != 0) {
            for (int i = 0; i < waveActivities.length(); i++) {
                JsonPath waveActivity = new JsonPath(waveActivities.get(i).toString());
                if ("BINPULLSPLIT".equals(waveActivity.getString("type"))) {
                    pckpullActivities.put(waveActivity.getString("containerId"), "BINPULLSPLIT", waveActivities.get(i).toString());
                } else if ("BINPULL".equals(waveActivity.getString("type"))) {
                    pckpullActivities.put(waveActivity.getString("containerId"), "BINPULL", waveActivities.get(i).toString());
                } else if ("SPLIT".equals(waveActivity.getString("type"))) {
                    splitActivities.put(waveActivity.getString("containerId"), waveActivity.getString("attributes.subType"), waveActivities.get(i).toString());
                }

                if (skuActivityMapActual.containsRow(waveActivity.getString("upc")) && skuActivityMapActual.row(waveActivity.getString("upc")).containsKey(waveActivity.getString("type"))) {
                    skuActivityMapActual.put(waveActivity.getString("upc"), waveActivity.getString("type"), skuActivityMapActual.get(waveActivity.getString("upc"), waveActivity.getString("type")) + 1);
                } else if (skuActivityMapActual.containsRow(waveActivity.getString("upc")) && !skuActivityMapActual.row(waveActivity.getString("upc")).containsKey(waveActivity.getString("type"))) {
                    skuActivityMapActual.put(waveActivity.getString("upc"), waveActivity.getString("type"), 1);
                } else if (!skuActivityMapActual.containsRow(waveActivity.getString("upc")) && !skuActivityMapActual.row(waveActivity.getString("upc")).containsKey(waveActivity.getString("type"))) {
                    skuActivityMapActual.put(waveActivity.getString("upc"), waveActivity.getString("type"), 1);
                }
            }
        }

        for (Cell<String, String, String> cell : pckpullActivities.cellSet()) {
            JsonPath activity = new JsonPath(cell.getValue());
            if (skuQTYMapA.containsKey(activity.getDouble("upc")) && "BINPULL".equals(cell.getColumnKey())) {
                skuQTYMapA.put(activity.get("upc"), skuQTYMapA.get(activity.getDouble("upc")) + activity.getInt("qty"));
            } else if ((!skuQTYMapA.containsKey(activity.getDouble("upc"))) && "BINPULL".equals(cell.getColumnKey())) {
                skuQTYMapA.put(activity.get("upc"), activity.getInt("qty"));
            } else if (skuQTYMapA.containsKey(activity.getDouble("upc")) && "BINPULLSPLIT".equals(cell.getColumnKey())) {
                Integer regQTY = new JsonPath(splitActivities.get(cell.getRowKey(), "R")).getInt("qty");
                Integer pckQTY = new JsonPath(splitActivities.get(cell.getRowKey(), "P")).getInt("qty");
                Integer totQTY = activity.getInt("qty");
                Assert.assertEquals((regQTY + pckQTY), totQTY.intValue(), "Split QTY doesnot match for BIN: " + cell.getRowKey());
                skuQTYMapA.put(activity.get("upc"), skuQTYMapA.get(activity.getDouble("upc")) + regQTY);
            } else if ((!skuQTYMapA.containsKey(activity.getDouble("upc"))) && "BINPULLSPLIT".equals(cell.getColumnKey())) {
                Integer regQTY = new JsonPath(splitActivities.get(cell.getRowKey(), "R")).getInt("qty");
                Integer pckQTY = new JsonPath(splitActivities.get(cell.getRowKey(), "P")).getInt("qty");
                Integer totQTY = activity.getInt("qty");
                Assert.assertEquals((regQTY + pckQTY), totQTY.intValue(), "Split QTY doesnot match for BIN: " + cell.getRowKey());
                skuQTYMapA.put(activity.get("upc"), regQTY);
            }
        }

        for (Cell<String, String, Integer> cell : skuActivityMapExpected.cellSet()) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Activity Count as expected",
                    "SKU: " + cell.getRowKey() + " and ActivityType: " + cell.getColumnKey(),
                    cell.getValue() == skuActivityMapActual.get(cell.getRowKey(), cell.getColumnKey()));
        }

        for (String sku : skuQTYMapE.keySet()) {
            Integer expectedQTY = skuQTYMapE.get(sku);
            Integer reservedQTY = ((skuQTYMapA.get(sku) == null) ? 0 : skuQTYMapA.get(sku));
            CommonUtils.doJbehavereportConsolelogAndAssertion("Activity SKU QTY as expected",
                    "SKU: " + sku,
                    reservedQTY == expectedQTY);
        }

    }

    @Then("Validate WSM Activities after Undo $waveCount wave")
    public void validateWSMActivitiesForUndoWave(String waveCount) {
        String waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));

        String GETCallEndpoint = commonUtils.getUrl("WSM.getActivities");
        String getBinPullSplitQueryParams = "waveNumber:#waveNumber";
        Map<String, String> processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
        JSONArray waveActivityList = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asJSONArray();

        for (int i = 0; i < waveActivityList.length(); i++) {
            JSONObject waveActivity = waveActivityList.getJSONObject(i);
            switch (waveActivity.getString("status").toUpperCase()) {
                case "CANCELLED":
                    CommonUtils.doJbehavereportConsolelogAndAssertion("ActivityID: " + waveActivity.getInt("id") + " cancelled", "WaveNumber: " + waveNumber, true);
                    break;
                case "COMPLETED":
                    CommonUtils.doJbehavereportConsolelogAndAssertion("ActivityID: " + waveActivity.getInt("id") + " completed", "WaveNumber: " + waveNumber, true);
                    break;
                default:
                    CommonUtils.doJbehavereportConsolelogAndAssertion("ActivityID: " + waveActivity.getInt("id") + " not cancelled", "WaveNumber: " + waveNumber, false);
                    break;
            }
        }
        CommonUtils.doJbehavereportConsolelogAndAssertion("All non-complete Activities cancelled for WaveNumber: ", waveNumber, true);
    }


    @SuppressWarnings("unchecked")
    @Then("STOREALLOC message Validated for $type $waveCount Wave$values")
    public void validateStoreAllocMessage(String type, String waveCount, ExamplesTable values) throws Exception {
        TimeUnit.SECONDS.sleep(20);
        if (values.getRows().size() == 1) {
            String waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));
            Table<String, String, String> waveRTFs = (Table<String, String, String>) dataStorage.getStoredData().get(waveCount + "RTFs");

            Map<String, String> row = values.getRow(0);
            String GETCallEndpoint = commonUtils.getUrl(row.get("getRequestUrl"));
            String GETQueryParams = row.get("GETQueryParams");
            if (GETQueryParams.contains("#waveNumber"))
                GETQueryParams = GETQueryParams.replace("#waveNumber", waveNumber);
            Map<String, String> processedGetQP = commonUtils.getParamsToMap(GETQueryParams);

            Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
            log.info(waveCount + "StoreAllocMsgResponse for WaveNumber " + waveNumber + ": " + GETResponse.asString());
            List<String> mheMsgList = new ArrayList<>();

            if (200 == GETResponse.statusCode()) {
                mheMsgList = getMHEMessagesfromResponse(GETResponse.asString(), waveNumber);
            }
            dataStorage.getStoredData().put(waveCount + "storeAllocMsgList", mheMsgList);
            log.info(waveCount + "storeAllocMsgList: " + mheMsgList);
            Table<String, String, String> storeSKULineItemmap = HashBasedTable.create();
            for (Cell<String, String, String> cell : waveRTFs.cellSet()) {
                JsonPath rtfPath = new JsonPath(cell.getValue());
                JSONArray lineItemArray = new JSONObject(cell.getValue()).getJSONArray("lineItem");
                for (int j = 0; j < lineItemArray.length(); j++) {
                    JsonPath lineItemPath = new JsonPath(lineItemArray.get(j).toString());
                    storeSKULineItemmap.put(rtfPath.getString("shipment.shipToLocationNbr"), lineItemPath.getString("itemSkuUpc"), lineItemArray.get(j).toString());
                }
            }

            Table<String, String, Map<String, String>> runMheMsgTable = HashBasedTable.create();
            Table<String, String, Map<String, String>> undoMheMsgTable = HashBasedTable.create();

            if (mheMsgList.size() == (storeSKULineItemmap.size() * ((type.equals("Undo")) ? 2 : 1))) {
                log.info("No of STOREALLOC messages are as expected");
                for (int i = 0; i < mheMsgList.size(); i++) {
                    LinkedHashMap<String, String> actualMessageDataMap = expdataForMessageBasicValidation.expectedMessage("STOREALLOC", mheMsgList.get(i), false, "WAVE", false);
                    runMheMsgTable.put(actualMessageDataMap.get("store"), actualMessageDataMap.get("upc1"), actualMessageDataMap);
                    if ("Undo".equalsIgnoreCase(type) && "0".equals(actualMessageDataMap.get("totalQty")))
                        undoMheMsgTable.put(actualMessageDataMap.get("store"), actualMessageDataMap.get("upc1"), actualMessageDataMap);
                }
            } else {
                Assert.assertTrue(false, "No of STOREALLOC messages are not as expected");
            }

            for (Cell<String, String, String> cell : storeSKULineItemmap.cellSet()) {
                if ("Undo".equalsIgnoreCase(type)) {
                    HashMap<String, String> mheDataMap = (HashMap<String, String>) undoMheMsgTable.get(cell.getRowKey(), cell.getColumnKey());
                    if (mheDataMap != null) {
                        validateMHEMessage(mheDataMap, "STOREALLOC", storeSKULineItemmap.get(cell.getRowKey(), cell.getColumnKey()), waveNumber, cell.getRowKey(), true);
                    } else {
                        log.info("No StoreAlloc Message found for Store: " + cell.getRowKey() + " ,SKU: " + cell.getColumnKey() + " Combination");
                        StepDetail.addDetail("No StoreAlloc Message found for Store: " + cell.getRowKey() + " ,SKU: " + cell.getColumnKey() + " Combination", false);
                        Assert.assertTrue(false, "No StoreAlloc Message found for Store: " + cell.getRowKey() + " ,SKU: " + cell.getColumnKey() + " Combination");
                    }
                    log.info("Details of Undo STOREALLOC messages are as expected for Store: " + cell.getRowKey() + " ,SKU: " + cell.getColumnKey() + " Combination");
                    StepDetail.addDetail("Details of STOREALLOC messages are as expected for Store: " + cell.getRowKey() + " ,SKU: " + cell.getColumnKey() + " Combination", true);
                } else if ("Run".equalsIgnoreCase(type)) {
                    HashMap<String, String> mheDataMap = (HashMap<String, String>) runMheMsgTable.get(cell.getRowKey(), cell.getColumnKey());
                    if (mheDataMap != null) {
                        validateMHEMessage(mheDataMap, "STOREALLOC", storeSKULineItemmap.get(cell.getRowKey(), cell.getColumnKey()), waveNumber, cell.getRowKey(), false);
                    } else {
                        Assert.assertTrue(false, "No StoreAlloc Message found for Store: " + cell.getRowKey() + " ,SKU: " + cell.getColumnKey() + " Combination");
                    }
                    log.info("Details of Wave STOREALLOC messages are as expected for Store: " + cell.getRowKey() + " ,SKU: " + cell.getColumnKey() + " Combination");
                    StepDetail.addDetail("Details of STOREALLOC messages are as expected for Store: " + cell.getRowKey() + " ,SKU: " + cell.getColumnKey() + " Combination", true);
                }
            }

        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }


    @SuppressWarnings("unchecked")
    @Then("System sends TOTECONT message for $waveCount Wave$values")
    public void validateTOTECONTMessage(String waveCount, ExamplesTable values) throws Exception {
        TimeUnit.SECONDS.sleep(10);
        if (values.getRows().size() == 1) {

            String waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));

            Map<String, String> row = values.getRow(0);
            String GETCallEndpoint = commonUtils.getUrl(row.get("getRequestUrl"));
            String GETQueryParams = row.get("GETQueryParams");
            if (GETQueryParams.contains("#waveNumber"))
                GETQueryParams = GETQueryParams.replace("#waveNumber", waveNumber);
            Map<String, String> processedGetQP = commonUtils.getParamsToMap(GETQueryParams);

            Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
            List<String> mheMsgList = new ArrayList<>();

            if (200 == GETResponse.statusCode()) {
                mheMsgList = getMHEMessagesfromResponse(GETResponse.asString(), waveNumber);
            }
            dataStorage.getStoredData().put(waveCount + "toteContMsgList", mheMsgList);
            log.info(waveCount + "toteContMsgList", mheMsgList);
            List<String> containerList = new ArrayList<String>();

            // Add TOTEs Created From BINBOXSPLIT Activity
            Map<String, Map<String, String>> prepBinBoxToteMap = (Map<String, Map<String, String>>) dataStorage.getStoredData().get(waveCount + "prepValues");
            if (prepBinBoxToteMap != null)
                containerList.addAll(prepBinBoxToteMap.get("REGULAR").values());

            // Add BINBOXs Pulled via BINPULL Activity
            List<String> binBoxList = (List<String>) dataStorage.getStoredData().get(waveCount + "binBoxList");
            if (binBoxList != null)
                containerList.addAll(binBoxList);

            Map<String, Map<String, String>> mheMsgMap = new HashMap<String, Map<String, String>>();
            if (mheMsgList.size() == containerList.size()) {
                log.info("No of TOTECONT messages are as expected");
                for (int i = 0; i < mheMsgList.size(); i++) {
                    LinkedHashMap<String, String> actualMessageDataMap = expdataForMessageBasicValidation.expectedMessage("TOTECONT", mheMsgList.get(i), false, "WAVE", false);
                    mheMsgMap.put(actualMessageDataMap.get("containerBarcode"), actualMessageDataMap);
                }
            } else {
                Assert.fail("Incorrect Size of TOTECONT messages, Expected: "+containerList.size()+" Actual: "+mheMsgList.size());
            }

            // Validate if TOTECONT present and valid for SPLIT TOTES and PULLED BINBOXs - IGNORES earlier TOTECONT Activity
            for (String container : containerList) {
                HashMap<String, String> mheDataMap = (HashMap<String, String>) mheMsgMap.get(container);
                if (mheDataMap != null) {
                    validateMHEMessage(mheDataMap, "TOTECONT", getContainerDetailsbyBarcode(container), waveNumber, "", false);
                    log.info("Details of TOTECONT message are as expected for Container: " + container);
                    StepDetail.addDetail("Details of TOTECONT message are as expected for Container: " + container, true);
                } else {
                    Assert.assertTrue(false, "No TOTECONT Message found for Container: " + container);
                    log.info("No TOTECONT Message found for Container: " + container);
                    StepDetail.addDetail("No TOTECONT Message found for Container: " + container, false);
                }

            }

        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @When("UNITPUT message for $waveCount Wave is published by Pyramid for moving inventory to carton on $location")
    public void putToStoreSimulation(String waveCount, String location) throws Exception {

        Map<String, String> cartonConfig = CommonUtils.loadConfig("Carton", "Attributes", "Carton_Dimensions", "Carton", "P2C");
        String prefix = cartonConfig.get("StartsWith");
        Map<String, String> storeLocationCartonIds = new HashMap<>();
        List<CartonDetails> cartonDetailsList = new ArrayList<>(5);

        String waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));
        List<Unitput> unitPutList = getUNITUTDetailsForWave(waveCount);
        if (!unitPutList.isEmpty()) {
            for (Unitput unitPut : unitPutList) {
                String storeLocNbr = String.valueOf(unitPut.getStoreLocationNbr());
                String carton;

                if (storeLocationCartonIds.containsKey(storeLocNbr)) {
                    carton = storeLocationCartonIds.get(storeLocNbr);
                } else {
                    storeLocationCartonIds.put(storeLocNbr, CommonUtils.getRandomCartonNumber(prefix, "20"));
                    carton = storeLocationCartonIds.get(storeLocNbr);
                }
                String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
                String sourceContainerID = unitPut.getContainerId();
                String quantity = String.valueOf(unitPut.getQuantity());

                String upc = unitPut.getSkuDetails().stream().map(m -> {
                    return String.format("%s|%s", m.getSku(), m.getQuantity());
                }).collect(Collectors.joining("|"));

                if (!ExpectedDataProperties.pyramidJsonproperty) {
                    List<String> messageData = new LinkedList<>();
                    messageData.add(seqId);
                    messageData.add("UNITPUT");
                    messageData.add(sourceContainerID);
                    messageData.add(carton);

                    for (SKUDetails detail : unitPut.getSkuDetails()) {
                        messageData.add(detail.getSku().toString());
                        messageData.add(String.valueOf(detail.getQuantity()));
                    }
                    StringBuilder message = new StringBuilder("\u0002");

                    message.append(StringUtils.join(messageData, "|"));
                    message.append(addPipe(messageData.size()));

                    messageData = new LinkedList<>();
                    messageData.add(storeLocNbr);
                    messageData.add(String.valueOf(unitPut.getDeptNbr()));
                    messageData.add(quantity);
                    messageData.add("0");
                    messageData.add(unitPut.getCasePack());
                    messageData.add("W");
                    messageData.add(waveNumber);
                    messageData.add("");
                    messageData.add(location);

                    message.append(StringUtils.join(messageData, "|")).append("||\u0003");

                    cartonDetailsList.add(new CartonDetails(seqId, carton, Integer.valueOf(quantity), upc, storeLocNbr, sourceContainerID));

                    log.info("Unit put message: {}", message);
                    CommonUtils.pyramidResponseValidation(message.toString());
                    String trasName = String.format("%s:%s", seqId, UNIT_PUT);
                    CommonUtils.verifyPutToStoreMsgResponse(trasName, UNIT_PUT, sourceContainerID);
                    StepDetail.addDetail("UNITPUT published for Carton: " + carton, true);
                    dataStorage.getStoredData().put(waveCount + "cartonDetailsList", cartonDetailsList);
                }
                else{
                    String lineItmRqstParams = "";
                    String finallineItmRqstParams = "";
                    List<String> lstlineItmRqstParams = new ArrayList<>();
                    for (SKUDetails detail : unitPut.getSkuDetails()) {
                        String SkuNbr = detail.getSku().toString();
                        String Skuqty = detail.getQuantity().toString();
                        lineItmRqstParams = "{#skuNbr:" + SkuNbr + ",#skuQty:" + Skuqty + "}";
                        List<String> messageBody = requestUtil.getRequestBody(lineItmRqstParams, "UNITPUTItemList.json");
                        lstlineItmRqstParams.addAll(messageBody);
                    }
                    for (String eachLineItemLst : lstlineItmRqstParams) {
                        finallineItmRqstParams = (finallineItmRqstParams + eachLineItemLst).trim() + ",";
                    }
                    finallineItmRqstParams = "["+removeEnd(finallineItmRqstParams, ",")+"]";
                    log.info("finallineItmRqstParams: {}", finallineItmRqstParams);
                    String requestParams = "{#sequenceno:D-9,#sourceContainer:" + sourceContainerID + ",#targetContainer:" + carton + ",#StrNbr:"+storeLocNbr+",#DeptNbr:"+String.valueOf(unitPut.getDeptNbr())+",#SKUqty:"+quantity+",#CasePack:"+unitPut.getCasePack()+",#PONbr:"+waveNumber+",#orderSource:W"+",#RcptNbr:"+"}";
                    List<String> messageBody = requestUtil.getRequestBody(requestParams, "UNITPUT.json");
                    log.info("Unitput input JSON payload: {}", messageBody);
                    for (String eachMessageBody : messageBody) {
                        eachMessageBody = eachMessageBody.replace("\"#lineItem\"",finallineItmRqstParams);
                        log.info("Unitput input JSON payload with filled values: {}", eachMessageBody);
                        CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "UNITPUT");
                        JSONObject json = new JSONObject(eachMessageBody);
                        seqId = json.getJSONObject("payload").getString("sequenceNo");
                    }
                    cartonDetailsList.add(new CartonDetails(seqId, carton, Integer.valueOf(quantity), upc, storeLocNbr, sourceContainerID));
                    String trasName = UNIT_PUT;
                    CommonUtils.verifyPutToStoreMsgResponse(trasName, UNIT_PUT, sourceContainerID);
                    StepDetail.addDetail("UNITPUT published for Carton: " + carton, true);
                    dataStorage.getStoredData().put(waveCount + "cartonDetailsList", cartonDetailsList);
                }

            }
        } else {
            Assert.fail("Unitput message is empty");
        }
    }

    @Then("validate the inventory for wave1 $waveCount after undo")
    public void AfterUndoWavevalidateInventory(String waveCount, ExamplesTable values) throws InterruptedException {
        // Delay to wave Update Status
        TimeUnit.SECONDS.sleep(10);
        if (1 == values.getRowCount()) {
            Map<String, String> row = values.getRow(0);

            String waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));

            String invEndPoint = commonUtils.getUrl(row.get("invRequestUrl"));
            Map<String, String> processedGetQP = commonUtils.getParamsToMap(row.get("invQueryParams"));
            Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(invEndPoint, processedGetQP).asResponse();
            List<String> listOfRSVContainers = null;
            List<String> listOfWaveContainers = new ArrayList<String>();
            if (204 == GETResponse.statusCode()) {
                log.info("Inventory not reserved for wave: " + waveNumber);
            } else if (200 == GETResponse.statusCode()) {
                JsonPath containers = new JsonPath(GETResponse.asString());
                listOfRSVContainers = containers.getList("container");

                for (String containerBarCode : listOfRSVContainers) {
                    String response = getContainerDetailsbyBarcode(containerBarCode);
                    if (StringUtils.isNotBlank(response)) {
                        JsonPath rPath = new JsonPath(response);
                        String waveNumberAttribute = rPath.getString("inventorySnapshotList[0].attributeList.findAll {attributeList -> attributeList.key=='WaveNumber'}[0].values[0]");
                        if (waveNumber.equals(waveNumberAttribute)) {
                            listOfWaveContainers.add((containerBarCode));
                        }
                    }
                }

                /// Add Validation
            }
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @SuppressWarnings("unchecked")
    @Then("validate the inventory for $waveCount wave using GET service$values")
    public void validateInventoryForWave(String waveCount, ExamplesTable values) throws InterruptedException {
        // Delay to wave Update Status
        TimeUnit.SECONDS.sleep(10);
        if (1 == values.getRowCount()) {
            Map<String, String> row = values.getRow(0);

            String waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));
            Table<String, String, String> rtfRequests = (Table<String, String, String>) dataStorage.getStoredData().get(waveCount + "RTFs");

            HashMap<String, HashMap<String, Integer>> storeSKUQTYmap = new HashMap<String, HashMap<String, Integer>>();
            Set<String> skuItems = new HashSet<String>();

            for (Cell<String, String, String> cell : rtfRequests.cellSet()) {
                HashMap<String, Integer> skuQTYMap = new HashMap<String, Integer>();
                JsonPath rtfPath = new JsonPath(cell.getValue().toString());
                JSONArray lineItemArray = new JSONObject(cell.getValue()).getJSONArray("lineItem");
                for (int j = 0; j < lineItemArray.length(); j++) {
                    JsonPath lineItemPath = new JsonPath(lineItemArray.get(j).toString());
                    skuQTYMap.put(lineItemPath.getString("itemSkuUpc"), lineItemPath.getInt("openQuantity"));
                    skuItems.add(lineItemPath.getString("itemSkuUpc"));
                }
                storeSKUQTYmap.put(rtfPath.getString("shipment.shipToLocationNbr"), skuQTYMap);
            }

            String invEndPoint = commonUtils.getUrl(row.get("invRequestUrl"));

            Map<String, String> processedGetQP = commonUtils.getParamsToMap(row.get("invQueryParams"));
            Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(invEndPoint, processedGetQP).asResponse();
            List<String> listOfRSVContainers = null;

            if (200 == GETResponse.statusCode()) {
                JsonPath containers = new JsonPath(GETResponse.asString());
                listOfRSVContainers = containers.getList("container");
            } else {
                Assert.assertTrue(false, "Inventory not reserved for wave: " + waveNumber);
            }

            List<String> rsvInventoryResponses = new ArrayList<>();

            HashMap<String, Integer> skuQTYRSV = new HashMap<String, Integer>();
            HashMap<String, HashSet<String>> skuBINRSV = new HashMap<String, HashSet<String>>();

            for (String containerBarCode : listOfRSVContainers) {
                String response = getContainerDetailsbyBarcode(containerBarCode);
                if (StringUtils.isNotBlank(response)) {
                    JsonPath rPath = new JsonPath(response);
                    String containerStatusCode = rPath.getString("container.containerStatusCode");
                    String inventoryStatusCode = rPath.getString("inventorySnapshotList[0].statusCode");
                    String skuItem = rPath.getString("inventorySnapshotList[0].item");
                    Integer skuQuantity = rPath.getInt("inventorySnapshotList[0].quantity");
                    String waveNumberAttribute = rPath.getString("inventorySnapshotList[0].attributeList.findAll {attributeList -> attributeList.key=='WaveNumber'}[0].values[0]");

                    if ("RSV".equals(containerStatusCode) && "RSV".equals(inventoryStatusCode) && waveNumber.equals(waveNumberAttribute)) {
                        rsvInventoryResponses.add("");
                        if (skuQTYRSV.containsKey(skuItem)) {
                            skuQTYRSV.put(skuItem, skuQTYRSV.get(skuItem) + skuQuantity);
                            skuBINRSV.get(skuItem).add(rPath.getString("container.barCode"));
                        } else {
                            skuQTYRSV.put(skuItem, skuQuantity);
                            skuBINRSV.put(skuItem, new HashSet<String>(Arrays.asList(rPath.getString("container.barCode"))));
                        }
                    }
                }
            }

            dataStorage.getStoredData().put(waveCount + "Inventory",skuBINRSV);

            for (String sku : skuItems) {
                Integer expectedQTY = 0;
                for (String store : storeSKUQTYmap.keySet()) {
                    expectedQTY = expectedQTY + ((storeSKUQTYmap.get(store).get(sku) == null) ? 0 : storeSKUQTYmap.get(store).get(sku));
                }
                Integer reservedQTY = ((skuQTYRSV.get(sku) == null) ? 0 : skuQTYRSV.get(sku));
                Assert.assertTrue(reservedQTY >= expectedQTY, "Excpected QTY not reserved for SKU " + sku + ". Expected QTY: " + expectedQTY + " Reserver QTY: " + reservedQTY);
                log.info("BINBOXs " + skuBINRSV.get(sku) + " reserved as expected for SKU: " + sku + ". Expected QTY: " + expectedQTY + " Reserver QTY: " + reservedQTY);
                StepDetail.addDetail("BINBOXs " + skuBINRSV.get(sku) + " reserved as expected for SKU: " + sku + ". Expected QTY: " + expectedQTY + " Reserver QTY: " + reservedQTY, true);
            }

            log.info("Inventory reserved as expected");
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @SuppressWarnings("unchecked")
    @Then("Inventory is created for outbound cartons and decreased from original Containers for $waveCount wave")
    public void verifyCartons(String waveCount) {
        List<CartonDetails> cartonDetailsList = (List<CartonDetails>) dataStorage.getStoredData().get(waveCount + "cartonDetailsList");
        Map<String, List<String>> cartonIdToteMap = cartonDetailsList.stream().collect(groupingBy(CartonDetails::getCartonId, mapping(CartonDetails::getToteId, toList())));
        dataStorage.getStoredData().put(waveCount + "cartonIdToteMap", cartonIdToteMap);
        stepsContext.resetScenario();
        stepsContext.put(Context.CARTON_TOTE_MAP.name(), cartonIdToteMap, ToContext.RetentionLevel.SCENARIO);
        log.info("cartonIdToteMap : {}", cartonIdToteMap);
        cartonIdToteMap.forEach((cartonId, toteIds) -> {
            String response = commonUtils.getContainerDetailsbyBarcode(cartonId);
            try {
                if (null != response) {
                    JsonPath cartondetail = new JsonPath(response);
                    if(CommonUtils.packageFlag){
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as IPK for Carton: Carton ID", cartonId, "IPK".equals(cartondetail.getString("[0].status")));
                    }else{
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as IPK for Carton: Carton ID", cartonId, "IPK".equals(cartondetail.getString("container.containerStatusCode")));
                    }
                } else {
                    log.info("P2S: Unable to create Carton: {}", cartonId);
                    Assert.assertTrue(false, "P2S: Unable to create Carton:" + cartonId);
                    StepDetail.addDetail(String.format("P2S: Unable to create Carton: %s", cartonId), true);
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            toteIds.forEach(toteId -> {
                InventoryContainer toteDetail = CommonUtils.getInventory(toteId);
                log.info("ToteDetail : {}", toteDetail);
                if (null != toteDetail) {
                    log.info("Tote is partially moved. Tote Id: {}", toteId);
                } else {
                    log.info("P2S: Tote [{}] is successfully moved into carton [{}]", toteId, cartonId);
                    StepDetail.addDetail(String.format("P2S: Container [%s] is successfully moved into carton [%s]", toteId, cartonId), true);
                }
            });
        });
    }

    @When("Runner activities are completed for $waveCount Wave$values")
    public void completeBinRunnerActivities(String waveCount, ExamplesTable values) throws Exception {
        if (0 < values.getRowCount()) {
            runnerPage.navigatetoRunner();
            for (int i = 0; i < values.getRowCount(); i++) {
                Map<String, String> row = values.getRow(i);
                String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");
                String GETCallEndpoint = commonUtils.getUrl(row.get("getRequestUrl"));

                Map<String, String> processedGetQP = commonUtils.getParamsToMap(row.get("getQueryParams").replace("#waveNumber", waveNumber));
                Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                log.info("Get Activity Response for Wave: " + waveNumber + "\n" + GETResponse.asString());
                String activityType = processedGetQP.get("type");
                if ((200 == GETResponse.statusCode())) {
                    JSONArray wsmActivities = new JSONArray(GETResponse.asString());
                    for (int j = 0; j < wsmActivities.length(); j++) {
                        JsonPath wsmActivity = new JsonPath(wsmActivities.get(j).toString());
                        String palletBarcode = wsmActivity.getString("containerId");
                        String activityID = wsmActivity.getString("id");
                        log.info("Pallet Barcode :{}", palletBarcode);
                        runnerPage.enterPalletNbr(palletBarcode);
                        if ("BINRUNNER".equals(activityType)) {
                            try {
                                JSONArray containerRelationList = new JSONObject(getContainerDetailsbyBarcode(palletBarcode)).getJSONObject("container").getJSONArray("containerRelationshipList");
                                Integer binSize = 0;
                                for (int k = 0; k < containerRelationList.length(); k++) {
                                    if ("BINBOX".equals(containerRelationList.getJSONObject(k).getString("childContainerType")))
                                        binSize++;
                                }
                                runnerPage.scanNumberOfBins(binSize.toString());
                            } catch (Exception e) {
                                log.info("No of BINs Page not dispayed: " + e.getMessage());
                            }
                        }
                        runnerPage.enterDropLocation();
                        validateWSMStatus(activityID, "COMPLETED");
                    }
                }
            }
            runnerPage.clickExitButton();
        } else {
            throw new IncorrectDataException("Require atleast one row of data");
        }
    }

    @SuppressWarnings("unchecked")
    @When("CONTCLOSE message is simulated to Pyramid for $waveCount wave")
    public void verifyCartonCloseMessage(String waveCount) {

        List<CartonDetails> cartonDetailsList = (List<CartonDetails>) dataStorage.getStoredData().get(waveCount + "cartonDetailsList");
        Map<String, String> cartonIdStoreLocationMap = cartonDetailsList.stream().collect(
                Collectors.toMap(CartonDetails::getCartonId, CartonDetails::getStoreLocationNumber,
                        (storeLocationNumber1, storeLocationNumber2) -> {
                            if(storeLocationNumber1.equals(storeLocationNumber2))
                                return storeLocationNumber1;
                            else
                                return storeLocationNumber1+","+storeLocationNumber2;
                        }));
        stepsContext.put(Context.CARTON_STORE_MAP.name(), cartonIdStoreLocationMap, ToContext.RetentionLevel.SCENARIO);
        log.info("cartonIdStoreLocationMap  : {}", cartonDetailsList);
        cartonDetailsList.forEach((cartonDetails) -> {
            String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String jsonResult = MHE_MessagingReverseJSON.CONT_CLOSE_MESSAGE;
                jsonResult = jsonResult
                        .replace("#sequenceno#", seqId)
                        .replace("#cartonNumber#", cartonDetails.getCartonId())
                        .replace("#storeLocNbr#", cartonDetails.getStoreLocationNumber());
                log.info("Cont close message :{}", jsonResult);
                CommonUtils.pyramidResponseValidation(jsonResult);
                String trasName = String.format("%s:%s", seqId, CONT_CLOSED);
                CommonUtils.verifyPutToStoreMsgResponse(trasName, CONT_CLOSED, cartonDetails.getCartonId());
            }else {
                String requestParams = "{#sequenceno:D-9,#container:" + cartonDetails.getCartonId() + ",#storeNbr:" + cartonDetails.getStoreLocationNumber() + "}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "CONTCLOSED.json");
                log.info("CONTCLOSED input JSON payload: {}", messageBody);
                for (String eachMessageBody : messageBody) {
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "CONTCLOSED");
                    JSONObject json = new JSONObject(eachMessageBody);
                    seqId = json.getJSONObject("payload").getString("sequenceNo");
                }
                String trasName = CONT_CLOSED;
                CommonUtils.verifyPutToStoreMsgResponse(trasName, CONT_CLOSED, cartonDetails.getCartonId());
            }
        });
    }

    @SuppressWarnings("unchecked")
    @When("SCANWEIGH is generated by pyramid SHIPREQUEST event is sent to shipping service for $waveCount wave")
    public void scanWeighSimulation(String waveCount) {
        List<CartonDetails> cartonDetailsList = (List<CartonDetails>) dataStorage.getStoredData().get(waveCount + "cartonDetailsList");
        cartonDetailsList.forEach(CartonDetails -> {
            String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String jsonResult = MHE_MessagingReverseJSON.SCAN_WEIGH_MESSAGE;
                jsonResult = jsonResult
                        .replace("#sequenceno#", seqId)
                        .replace("#barCode#", CartonDetails.getCartonId())
                        .replace("#weight#", "2.4");

                log.info("Scan Weigh message: {}", jsonResult);
                CommonUtils.pyramidResponseValidation(jsonResult);
            }else{
                String requestParams = "{#sequenceno:D-9,#carton:"+CartonDetails.getCartonId()+",#weight:D-2"+"}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "SCANWEIGH.json");
                log.info("SCANWEIGH input JSON payload: {}", messageBody);
                for (String eachMessageBody : messageBody) {
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "SCANWEIGH");
                }
            }

        });
        StepDetail.addDetail("Successfully sent Scan Weigh Message", true);
    }

    // Private methods

    private List<String> getMHEMessagesfromResponse(String response, String waveNumber) {

        List<String> mheMsgList = new ArrayList<String>();
        try {
            JSONObject parentJson = new JSONObject(response);
            JSONObject messageDTOObject = parentJson.getJSONObject("MessageResponseDTO");
            JSONArray messagesArray = messageDTOObject.getJSONArray("messages");
            for (int i = 0; i < messagesArray.length(); i++) {
                String mheMsg = messagesArray.getJSONObject(i).getString("outgoingPayload");
                if (mheMsg != null && mheMsg.contains(waveNumber))
                    mheMsgList.add(mheMsg);
            }
        } catch (Exception e) {
            log.info("Unable to extract mhe messages from response. " + e.getStackTrace());
        }

        return mheMsgList;
    }

    private void validateMHEMessage(Map<String, String> actualMHEDataMap, String msgType, String response, String waveNumber, String storeNumber, boolean undoFlag) {
        JsonPath responsePath = new JsonPath(response);
        if ("STOREALLOC".equals(msgType)) {
            Assert.assertEquals("STOREALLOC", actualMHEDataMap.get("messageType"), "Incorrect MHE Messagetype");
            Assert.assertEquals("0", actualMHEDataMap.get("storeType"), "Incorrect UPC QTY on STOREALLOC message");
            Assert.assertEquals("1", actualMHEDataMap.get("upc1Qty"), "Incorrect UPC QTY on STOREALLOC message");
            Assert.assertEquals(NEW_STORE, actualMHEDataMap.get("innerPackPrefix"), "Incorrect UPC QTY on STOREALLOC message");
            Assert.assertEquals("", actualMHEDataMap.get("waveNbrNextElement"), "Incorrect WaveNbrNextElement on STOREALLOC message");
            Assert.assertEquals("W", actualMHEDataMap.get("orderType"), "Incorrect OrderType on STOREALLOC message");

            Assert.assertEquals(storeNumber, actualMHEDataMap.get("store"), "Incorrect Store on STOREALLOC message");
            Assert.assertEquals(responsePath.get("itemSkuUpc").toString(), actualMHEDataMap.get("upc1"), "Incorrect UPC on STOREALLOC message");
            if (!undoFlag)
                Assert.assertEquals(responsePath.get("itemQuantity").toString(), actualMHEDataMap.get("totalQty"), "Incorrect TotalQty on STOREALLOC message");
            else
                Assert.assertEquals("0", actualMHEDataMap.get("totalQty"), "Incorrect TotalQty on STOREALLOC message");
            Assert.assertEquals(responsePath.get("deptNbr").toString(), actualMHEDataMap.get("dept"), "Incorrect Dept on STOREALLOC message");
            Assert.assertEquals(waveNumber, actualMHEDataMap.get("waveNbr"), "Incorrect WaveNumber on STOREALLOC message");

        } else if ("TOTECONT".equals(msgType)) {
            Assert.assertEquals("TOTECONT", actualMHEDataMap.get("messageType"), "Incorrect MHE Messagetype");
            Assert.assertEquals("0", actualMHEDataMap.get("storeType"), "Incorrect UPC QTY on TOTECONT message");
            Assert.assertEquals("1", actualMHEDataMap.get("upc1Qty"), "Incorrect UPC QTY on TOTECONT message");
            Assert.assertEquals(NEW_STORE, actualMHEDataMap.get("innerPackPrefix"), "Incorrect UPC QTY on TOTECONT message");
            Assert.assertEquals("", actualMHEDataMap.get("waveNbrNextElement"), "Receipt Number not expected on WAVE TOTECONT message");
            Assert.assertEquals("W", actualMHEDataMap.get("orderType"), "Incorrect OrderType on TOTECONT message");

            Assert.assertEquals(responsePath.get("inventorySnapshotList[0].item").toString(), actualMHEDataMap.get("upc1"), "Incorrect UPC on TOTECONT message");
            Assert.assertEquals(responsePath.get("inventorySnapshotList[0].quantity").toString(), actualMHEDataMap.get("totalQty"), "Incorrect TotalQty on TOTECONT message");
            Assert.assertEquals(responsePath.get("inventorySnapshotList[0].attributeList.findAll {attributeList -> attributeList.key=='Department'}[0].values[0]").toString(), actualMHEDataMap.get("dept"), "Incorrect Dept on TOTECONT message");
            Assert.assertEquals(responsePath.get("inventorySnapshotList[0].container").toString(), actualMHEDataMap.get("containerBarcode"), "Incorrect Dept on TOTECONT message");
            Assert.assertEquals(waveNumber, actualMHEDataMap.get("waveNbr"), "Incorrect WaveNumber on TOTECONT message");
        }
    }


    private List<Unitput> getUNITUTDetailsForWave(String waveCount) {
        List<Unitput> unitputList = new LinkedList<Unitput>();
        try {
            LinkedList<StoreAlloc> storeAllocMsgList = getStoreAllocList(waveCount);
            LinkedList<ToteCont> toteContMsgList = getToteContList(waveCount);
            for (StoreAlloc storeAllocMsg : storeAllocMsgList) {
                Integer store = Integer.valueOf(storeAllocMsg.getStoreNum());
                Integer dept = Integer.valueOf(storeAllocMsg.getDeptNum());

                Long skuUpcNum = Long.valueOf(storeAllocMsg.getSkuUpcNum());
                Integer expectedQty = storeAllocMsg.getAllocatedQuantity();
                List<SKUDetails> skuDetails = new ArrayList<>(2);
                skuDetails.add(new SKUDetails(BigInteger.valueOf(skuUpcNum), 1));
                for (ToteCont toteContMsg : toteContMsgList) {
                    if (String.valueOf(skuUpcNum).equals(toteContMsg.getSkuUpcNum())) {
                        if (toteContMsg.getAllocatedQuantity() == 0) {
                            //Skip
                        } else if (toteContMsg.getAllocatedQuantity() >= expectedQty) {
                            unitputList.add(new Unitput(toteContMsg.getContainerId(), store, expectedQty, skuDetails, dept.toString(), NEW_STORE));
                            toteContMsg.setAllocatedQuantity(toteContMsg.getAllocatedQuantity() - expectedQty);
                            expectedQty = 0;
                            break;
                        } else if (toteContMsg.getAllocatedQuantity() < expectedQty) {
                            unitputList.add(new Unitput(toteContMsg.getContainerId(), store, toteContMsg.getAllocatedQuantity(), skuDetails, dept.toString(), NEW_STORE));
                            expectedQty = expectedQty - toteContMsg.getAllocatedQuantity();
                            toteContMsg.setAllocatedQuantity(0);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error in creating unitput message", e);
            Assert.fail("Error in creating unitput message. " + e);
        }

        return unitputList;
    }

    @SuppressWarnings("unchecked")
    private LinkedList<StoreAlloc> getStoreAllocList(String waveCount) {
        LinkedList<StoreAlloc> storeAllocList = new LinkedList<StoreAlloc>();
        ArrayList<String> storeAllocMsgList = (ArrayList<String>) dataStorage.getStoredData().get(waveCount + "storeAllocMsgList");
        for (String storeAllocMsg : storeAllocMsgList) {
            LinkedHashMap<String, String> storeAllocMsgDataMap = expdataForMessageBasicValidation.expectedMessage("STOREALLOC", storeAllocMsg, false, "WAVE", false);
            storeAllocList.add(new StoreAlloc(storeAllocMsgDataMap.get("store"), Integer.valueOf(storeAllocMsgDataMap.get("totalQty")), storeAllocMsgDataMap.get("upc1"), storeAllocMsgDataMap.get("dept")));
        }
        return storeAllocList;
    }

    @SuppressWarnings("unchecked")
    private LinkedList<ToteCont> getToteContList(String waveCount) {
        LinkedList<ToteCont> toteContList = new LinkedList<ToteCont>();
        ArrayList<String> toteContMsgList = (ArrayList<String>) dataStorage.getStoredData().get(waveCount + "toteContMsgList");
        for (String toteContMsg : toteContMsgList) {
            LinkedHashMap<String, String> toteContMsgDataMap = expdataForMessageBasicValidation.expectedMessage("TOTECONT", toteContMsg, false, "WAVE", false);
            toteContList.add(new ToteCont(toteContMsgDataMap.get("containerBarcode"), Integer.valueOf(toteContMsgDataMap.get("totalQty")), toteContMsgDataMap.get("upc1")));
        }
        return toteContList;
    }


    private void validateWSMStatus(String activityID, String expectedStatus) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, String> activity = wsmService.fetchWsmActivitiesById(activityID);
        Assert.assertEquals(activity.get("status"), expectedStatus);
        StepDetail.addDetail("Validated WSM activity status updated to : " + expectedStatus, true);
    }


    private void validateWSMStatus(Map<String, String> activityAttributes, String expectedStatus) {
        Map<String, String> activity = wsmService.fetchWsmActivities(activityAttributes);
        Assert.assertEquals(activity.get("status"), expectedStatus);
        StepDetail.addDetail("Validated WSM activity status updated to : " + expectedStatus, true);
    }


    @SuppressWarnings("unchecked")
    private String createSubstituteBinBox(String barCode, String locationBarCode) {

        try {
            List<String> createInventoryRequests = (List<String>) dataStorage.getStoredData().get("createInventoryRequests");
            String createReferenceRequest = "";
            for (String createInventoryRequest : createInventoryRequests) {
                if (createInventoryRequest.contains(barCode)) {
                    createReferenceRequest = createInventoryRequest;
                    break;
                }
            }
            String newBinBoxBarCode = randomUtil.getRandomValue("95-D-18");
            String newPalletBarCode = randomUtil.getRandomValue("PLT-D-17");
            createReferenceRequest = createReferenceRequest.replaceAll(barCode, newBinBoxBarCode);
            JsonPath createRequestPath = new JsonPath(createReferenceRequest);
            String palletBarCode = createRequestPath.getString("container.containerRelationshipList.findAll {containerRelationshipList -> containerRelationshipList.parentContainerType=='PLT'}.parentContainer[0]");

            createReferenceRequest = createReferenceRequest.replaceAll(palletBarCode, newPalletBarCode);

            //Create Inventory
            Response response = WhmRestCoreAutomationUtils.postRequestResponse(commonUtils.getUrl("InventoryServices.CreateInventory"), createReferenceRequest).asResponse();
            CommonUtils.doJbehavereportConsolelogAndAssertion("Create Inventory", commonUtils.getUrl("InventoryServices.CreateInventory") + "\n" + createReferenceRequest + "\n" + response.getStatusCode(), validationUtils.validateResponseStatusCode(response, 201));

            //Create Pallet
            String requestParamsCreate = "{#contStatus:PTW,#contTyp:PLT,#barCode:PREVSTEP}";
            String createContainerRequest = requestUtil.getRequestBody(requestParamsCreate.replace("PREVSTEP", newPalletBarCode), "CreatePallet.json").get(0);
            Response response1 = WhmRestCoreAutomationUtils.postRequestResponse(commonUtils.getUrl("InventoryServices.CreateContainer"), createContainerRequest).asResponse();
            CommonUtils.doJbehavereportConsolelogAndAssertion("Create Container", commonUtils.getUrl("InventoryServices.CreateContainer") + "\n" + createContainerRequest + "\n" + response1.getStatusCode(), validationUtils.validateResponseStatusCode(response1, 201));

            //Locate Pallet
            String requestParamsLocate = "{#parentConTyp:LCN,#parentBarcode:PREVSTEP";
            String locateContainerRequest = requestUtil.getRequestBody(requestParamsLocate.replace("PREVSTEP", locationBarCode), "LocatePallet.json").get(0);
            Response response2 = WhmRestCoreAutomationUtils.putRequestResponse(commonUtils.getUrl("InventoryServices.LocateContainer").replace("#sourceBarcode", newPalletBarCode), locateContainerRequest).asResponse();
            CommonUtils.doJbehavereportConsolelogAndAssertion("Locate Container", commonUtils.getUrl("InventoryServices.LocateContainer").replace("#sourceBarcode", newPalletBarCode) + "\n" + locateContainerRequest + "\n" + response2.getStatusCode(), validationUtils.validateResponseStatusCode(response2, 201));

            return newBinBoxBarCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private Map<String, Set<String>> getLocationActivityTypeDescForWave(JSONArray wsmActivities) {
        if (StringUtils.isBlank(packawayActivityTypeConfigValue)) {
            String ConfigCallEndpoint = commonUtils.getUrl("configurationServices.packawayActivityTypes");
            Response ConfigResponse = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).get(ConfigCallEndpoint);
            log.info("Get PackawayActivityTypes Config Response" + "\n" + ConfigResponse.asString());
            packawayActivityTypeConfigValue = String.valueOf(new JSONArray(ConfigResponse.asString()).getJSONObject(0).get("configValue"));
        }

        JSONArray ConfigArray = new JSONArray(packawayActivityTypeConfigValue);
        Map<String, Set<String>> locationActivityDescMap = new HashMap<String, Set<String>>();
        for (int i = 0; i < wsmActivities.length(); i++) {
            JsonPath wsmPath = new JsonPath(wsmActivities.get(i).toString());
            String processArea = wsmPath.getString("attributes.processArea");
            String activityType = wsmPath.getString("type");
            String locationNbr = wsmPath.getString("attributes.locationNbr");
            String activityTypeDesc = "";

            for (int j = 0; j < ConfigArray.length(); j++) {
                activityTypeDesc = "";
                JsonPath configPath = new JsonPath(ConfigArray.getJSONObject(j).toString());
                if (configPath.getString("activityType").equals(activityType) && configPath.getString("processArea").equals(processArea)) {
                    activityTypeDesc = configPath.getString("activityTypeDesc");
                    if (locationActivityDescMap.containsKey(locationNbr)) {
                        locationActivityDescMap.get(locationNbr).add(StringUtils.normalizeSpace(activityTypeDesc + " " + processArea));
                    } else {
                        locationActivityDescMap.put(locationNbr, new HashSet<String>(Arrays.asList(StringUtils.normalizeSpace(activityTypeDesc + " " + processArea))));
                    }
                    break;
                }
            }
        }
        return locationActivityDescMap;
    }


    private String getActivityTypeByDesc(String activityDesc) {
        if (StringUtils.isBlank(packawayActivityTypeConfigValue)) {
            String ConfigCallEndpoint = commonUtils.getUrl("configurationServices.packawayActivityTypes");
            Response ConfigResponse = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).get(ConfigCallEndpoint);
            log.info("Get PackawayActivityTypes Config Response" + "\n" + ConfigResponse.asString());
            packawayActivityTypeConfigValue = String.valueOf(new JSONArray(ConfigResponse.asString()).getJSONObject(0).get("configValue"));
        }

        JSONArray ConfigArray = new JSONArray(packawayActivityTypeConfigValue);

        for (int j = 0; j < ConfigArray.length(); j++) {
            JsonPath configPath = new JsonPath(ConfigArray.getJSONObject(j).toString());
            if (StringUtils.normalizeSpace(configPath.getString("activityTypeDesc")).equals(activityDesc.substring(0, activityDesc.length() - 4)) && StringUtils.normalizeSpace(configPath.getString("processArea")).equals(activityDesc.substring(activityDesc.length() - 3))) {
                return configPath.getString("activityType");
            }
        }
        return "";
    }

    private ArrayList<String> getListofDates(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        ArrayList<String> totalDates = new ArrayList<>();
        while (!start.isAfter(end)) {
            totalDates.add(start.toString());
            start = start.plusDays(1);
        }
        return totalDates;
    }

    private String joinList(List<String> listofStrings) {
        return String.join(",", listofStrings.stream().map(stringValue -> ("'" + stringValue + "'")).collect(Collectors.toList()));
    }

    private String getContainerDetailsbyBarcode(String barcode) {
        try {
            Map<String, String> queryParams = new HashMap<String, String>();
            queryParams.put("barcode", barcode);
            String endpoint = commonUtils.getUrl("InventoryServices.CreateInventory");
            Response response = WhmRestCoreAutomationUtils.getRequestResponse(endpoint, queryParams).asResponse();
            return response.asString();
        } catch (Exception e) {
            log.info("unable to get inventory for barCode: " + barcode + " with error " + e.getMessage());
            return "";
        }
    }

    private String addPipe(int size) {
        return String.format("%" + (35 - size) + "s", " ").replace(" ", "|");
    }

    @Then("Validate Inventory and PACKAWAY Activities for $param Bins with containerStatusCode $containerStatusCode and statusCode $statusCode in $activityStatus Status")
    public void validateCreateBinPackawayActivities(String activityStatus, String param,String containerStatusCode, String statusCode) throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        Table<String, String, Integer> binMapExpected = (Table<String, String, Integer>) dataStorage.getStoredData().get("ExpectedBinBoxMap");
        for (Table.Cell<String, String, Integer> cell : binMapExpected.cellSet()) {
            String binBox = cell.getRowKey();
            Map<String, String> processedGetQP = new HashMap<String, String>();
            processedGetQP.put("type", "PACKAWAY");
            processedGetQP.put("status", activityStatus);
            processedGetQP.put("container", binBox);
            Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(commonUtils.getUrl("WSM.getActivities"), processedGetQP).asResponse();
            Assert.assertTrue(GETResponse.getStatusCode()==200);
            StepDetail.addDetail("BinBox:"+cell.getRowKey() +";JSON Response: "+GETResponse.asString(), true);
            JSONArray activityDtl = new JSONArray(GETResponse.asString());
            if (activityDtl.length() != 0) {
                JsonPath waveActivity = new JsonPath(activityDtl.get(0).toString());
                Assert.assertTrue(waveActivity.getString("upc").equals(cell.getColumnKey()));
                Assert.assertTrue(Integer.valueOf(waveActivity.getString("totalQty"))==cell.getValue());
            }
            //Add step to validate containerStatus, inventoryStatus, ReasonCode, Sku, PoLineBrcd, Qty, Po, Rcpt
            //validateContainerStatus("BinBox", "SRT");
            String url = ReadHostConfiguration.getCreateInventoryURL() + "7221/containers";
            Response response = RestAssured.given()
                    .headers(ExpectedDataProperties.getHeaderProps()).queryParam("barcode", binBox).log()
                    .all().when().contentType(ContentType.JSON).get(url, new Object[0]);
            if (param.equalsIgnoreCase("not created") || param.equalsIgnoreCase("deleted")) {
                StepDetail.addDetail("Response is empty", response.asString().length() == 0);
                org.junit.Assert.assertTrue("Response is empty", response.asString().length() == 0);
            }else if (!response.asString().isEmpty() && param.equalsIgnoreCase("created")) {

                JSONObject responseObject = new JSONObject(response.asString());
                LOGGER.info("Inventory object: " + responseObject);
                JSONObject containerObject = responseObject.getJSONObject("container");
                JSONArray inventorySnapshotList = responseObject.getJSONArray("inventorySnapshotList");
                org.junit.Assert.assertEquals(binBox, containerObject.getString("barCode"));
                org.junit.Assert.assertEquals("BINBOX", containerObject.getString("containerType"));
                org.junit.Assert.assertEquals(containerStatusCode, containerObject.getString("containerStatusCode"));

                inventorySnapshotList.forEach(value -> {
                    JSONObject inventorySnapshot = (JSONObject) value;
                    String containerType="BINBOX";
                    if (containerType.equalsIgnoreCase("BINBOX")) {
                        org.junit.Assert.assertEquals(binBox, inventorySnapshot.getString("container"));
                        org.junit.Assert.assertEquals(statusCode, inventorySnapshot.getString("statusCode"));
                        LOGGER.info("Create Bin is created and validated the inventory details");
                        StepDetail.addDetail("Create Bin is created and validated the inventory details", true);
                    }
                });
            }
        }
    }

    @Then("Publish RTF message $values")
    public void publishMultipleRTFMessageForGivenQty(ExamplesTable values) {
        List<String> rtfList = new ArrayList<String>();
        Table<String, String, Map<String, String>> rtfRequestValues = HashBasedTable.create();
        Table<String, String, String> rtfRequests = HashBasedTable.create();

        String orderID = null;

        if (1 <= values.getRowCount()) {
            for (int i = 0; i < values.getRowCount(); i++) {
                Map<String, String> row = values.getRow(i);

                List<String> messageBody = requestUtil.getRequestBody(row.get("orderParams"), row.get("templateName"));
                Map<String, String> RTFFilledRandomValue = requestUtil.getRandomeValueMaps().get(0);

                String lineItemSnipBody = requestUtil.getRequestBodyFromFile("src/test/resources/RequestTemplates/" + row.get("lineItemTemplate"));
                String lineItemJSON = null;
                int lineNbr = 1;
                for (String lineItem : StringUtils.split(row.get("lineParams"), "{(.*?)}")) {
                    String lineItemBodyTemp = lineItemSnipBody;
                    String[] lineItemParams = StringUtils.split(lineItem, ",");
                    for (String lineItemParam : lineItemParams) {
                        String[] keyValue = lineItemParam.split(":");
                        if (keyValue[0].equalsIgnoreCase("#QTY")) {
                            //keyValue[1] = keyValue[1].replace(keyValue[1], dataStorage.getStoredData().get("RTF" + lineNbr + "_QTY").toString());
                            lineItemBodyTemp = lineItemBodyTemp.replace("#QTY", keyValue[1]);
                        } else {
                            lineItemBodyTemp = lineItemBodyTemp.replace(keyValue[0], keyValue[1]);
                        }
                    }
                    lineItemBodyTemp = lineItemBodyTemp.replace("#lnNbr", String.valueOf(lineNbr++));

                    if (StringUtils.isEmpty(lineItemJSON)) {
                        lineItemJSON = lineItemBodyTemp;
                    } else {
                        lineItemJSON = lineItemJSON + "," + lineItemBodyTemp;
                    }
                }

                String rtfRequest = messageBody.get(0).replace("#lineItem", lineItemJSON);
                // Replace orderID with existing order ID if not generated newly
                if (RTFFilledRandomValue.containsKey("#orderID")) {
                    orderID = RTFFilledRandomValue.get("#orderID");
                } else {
                    RTFFilledRandomValue.put("#orderID", orderID);
                }
                rtfRequestValues.put(RTFFilledRandomValue.get("#orderID"), RTFFilledRandomValue.get("#shipNbr"), RTFFilledRandomValue);

                // Replace generated values across lineItem elements
                for (String key : RTFFilledRandomValue.keySet()) {
                    rtfRequest = rtfRequest.replaceAll(key, RTFFilledRandomValue.get(key));
                }
                rtfList.add(rtfRequest);
                rtfRequests.put(RTFFilledRandomValue.get("#orderID"), RTFFilledRandomValue.get("#shipNbr"), rtfRequest);
            }
            dataStorage.getStoredData().put("rtfRequestValues", rtfRequestValues);

            //Publish RTFs
            String topic = commonUtils.getEnvConfigValue(values.getRow(0).get("topic"));
            try {
                List<ApiResponse> responses = testingService.publishGivenPayloadsToGivenTopic(topic, rtfList);
                for (ApiResponse ApiResponse : responses) {
                    validationUtils.validateResponseStatusCode(ApiResponse.asResponse(), 200);
                }
                dataStorage.getStoredData().put("publishedRTFs", rtfRequests);
                CommonUtils.doJbehavereportConsolelogAndAssertion("Publish RTF Success",
                        "Topic: " + topic + "\n" +
                                "RTF requests: " + rtfList, true);
            } catch (Exception e) {
                e.printStackTrace();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Publish RTF Success",
                        "Topic: " + topic + "\n" +
                                "RTF requests: " + rtfList, false);
            }
        } else {
            throw new IncorrectDataException("Requires atleast one row of data");
        }

    }

    @Then("validate the bin location is updated to the ICQA location$values")
    public void verifyParentLocationForCancelledBin(ExamplesTable values) {
        Map<String, String> row = values.getRow(0);
        String ICQALocation = null;
        String getConfigUrl = commonUtils.getUrl("configurationServices.GetRestrictedERSStatus");
        Response configResponse = WhmRestCoreAutomationUtils.getRequestResponse(getConfigUrl.replace("{AppName}", row.get("appName")).replace("{MName}", row.get("moduleName")).replace("{CName}", row.get("configKey"))).asResponse();
        if (200 == configResponse.statusCode()) {
            List<String> configValues = new JsonPath(configResponse.asString()).getList("configValue");
            ICQALocation = configValues.get(0);
        } else {
            CommonUtils.doJbehavereportConsolelogAndAssertion("No configuration available",
                    "NA",
                    validationUtils.validateResponseStatusCode(configResponse, 204));
        }

        String barcode = dataStorage.getStoredData().get("binboxBarcode").toString();
        StepDetail.addDetail("Cancelled Bin barcode "+barcode, true);
        StepDetail.addDetail("ICQA LOcation value "+ICQALocation, true);
        String parentContainer = dataStorage.getStoredData().get("parentContainer").toString();
        String parentEnquiryQuery = String.format(SQLResearchInventory.ParentEnquiry,barcode);
        String parentLocation = DBMethods.getDBValueInString(parentEnquiryQuery, "inventory_ng");
        if(!ICQALocation.equals(" ")) {
            Assert.assertEquals(parentLocation, ICQALocation);
            StepDetail.addDetail("Parent location of bin" + parentLocation, true);
        }
        else{
            Assert.assertEquals(parentLocation, parentContainer);
            StepDetail.addDetail("Parent location  of bin" + parentLocation, true);
        }

    }

    @Given("Multiple Inventory Created $values")
    public void createMultipleInv(ExamplesTable values) {
        if (values.getRowCount() > 0) {
            Map<String, String> containerbarcode_Template = new HashMap<>();
            List<String> createInventoryRequests = new ArrayList<>();
            listOfcasesAndBinboxes = new ArrayList<>();
            int i = 1;
            for (Map<String, String> row : values.getRows()) {
                String endPoint = commonUtils.getUrl(row.get("requestUrl"));
                String createInventoryRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
                //to get only the generated barcode of the inventory - as of now takes only the first one,
                // if necessary code can change to support multiple request of the same row
                String lineItemJSON = null;
                int lineNbr = 1;
                for (String lineItem : StringUtils.split(row.get("requestParams"), "{(.*?)}")) {
                    String lineItemBodyTemp = createInventoryRequest;
                    String[] lineItemParams = StringUtils.split(lineItem, ",");
                    for (String lineItemParam : lineItemParams) {
                        String[] keyValue = lineItemParam.split(":");
                        if (keyValue[0].contains("#qty")) {
                            lineItemBodyTemp = lineItemBodyTemp.replace("QTY", dataStorage.getStoredData().get("BIN".concat(Integer.toString(i)).concat("_QTY")).toString());
                        } else if (keyValue[0].contains("#parentBarcode")) {
                            lineItemBodyTemp = lineItemBodyTemp.replace("PARENTBARCODE", dataStorage.getStoredData().get("BIN".concat(Integer.toString(i)).concat("_LOC")).toString());
                        }
                    }
                    lineItemBodyTemp = lineItemBodyTemp.replace("#lnNbr", String.valueOf(lineNbr++));

                    if (StringUtils.isEmpty(lineItemJSON)) {
                        lineItemJSON = lineItemBodyTemp;
                    } else {
                        lineItemJSON = lineItemJSON + "," + lineItemBodyTemp;
                    }
                    createInventoryRequest = lineItemJSON;
                }

                JSONObject jsonObject = new JSONObject(createInventoryRequest);
                listOfcasesAndBinboxes.add(jsonObject.getJSONObject("container").getString("barCode"));

                String createInventoryTemplate = row.get("templateName");
                createInventoryRequests.add(createInventoryRequest);
                containerbarcode_Template.put(createInventoryRequest, createInventoryTemplate);
                Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createInventoryRequest).asResponse();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Created Inventory",
                        "Create Inventory Endpoint: " + endPoint + "\n"
                                + "Create Inventory Request: " + createInventoryRequest + "\n"
                                + "Create Inventory Response Statuscode: " + response.getStatusCode(),
                        validationUtils.validateResponseStatusCode(response, 201));
                i++; //added for count of rows
            }
            dataStorage.getStoredData().put("inventoryContainerList", listOfcasesAndBinboxes);
            dataStorage.getStoredData().put("createInventoryRequests", createInventoryRequests);
        } else {
            throw new IncorrectDataException("Require atleast one row of data");
        }
    }

    private void unassignActivity(JSONArray waveActivities, String reqStatus){
        for (int i = 0; i < waveActivities.length(); i++) {
            JsonPath wsmPath = new JsonPath(waveActivities.get(i).toString());
            String status = wsmPath.getString("status");
            String id = wsmPath.getString("id");
            String locnNbr = wsmPath.getString("locnNbr");
            if(status.equalsIgnoreCase("ASSIGNED")){
                String updateWsmActivityEndpoint = String.format(WsmEndpoint.WSM_update_Activities, locnNbr, status);
                String payloadParams = "";
                payloadParams += "{#id:" + id + ",#status:" + reqStatus + "}";
                List<String> filledActivityUpdateList = requestUtil.getRequestBody(payloadParams.trim(), "WSMActivityUpdateStatus.json");
                Response updateResponse = WhmRestCoreAutomationUtils.putRequestResponse(updateWsmActivityEndpoint, filledActivityUpdateList.toString()).asResponse();
                log.info("Updating the store activities to COMPLETE :", updateResponse.getStatusCode());
            }
        }
    }

    @When("packaway pull activities are completed for $waveCount wave for given actions$values")
    public void completeBinPullAndBinPullSplitActivitiesForGivenActions(String waveCount, ExamplesTable values) throws Exception {

        String getBinPullSplitQueryParams = "waveNumber:#waveNumber,status:OPEN";
        ArrayList<String> altLocations = new ArrayList<String>();
        HashMap<String, String> skuAction = new HashMap<String, String>();
        HashMap<String, String> skuAltLocation = new HashMap<String, String>();
        List<String> binBoxList = new ArrayList<String>();
        if (0 < values.getRowCount()) {
            for (Map<String, String> row : values.getRows()) {
                skuAction.put(row.get("SKU"), row.get("ACTION"));
                if ("ALT".equals(row.get("ACTION"))) {
                    skuAltLocation.put(row.get("SKU"), row.get("LOCATION"));
                }

                log.info("Activities will be completed with SKU Action " + skuAction);

                String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");
                String palletBarCode;

                String GETCallEndpoint = commonUtils.getUrl("WSM.getActivities");
                Map<String, String> processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
                //Here it will search for activities with status given in story file.
                processedGetQP.put("status", row.get("STATUS"));
                Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                JSONArray waveActivities = new JSONArray(GETResponse.asString());
                log.info("Get Activity Response for Wave: " + waveNumber + "\n" + GETResponse.asString());
                unassignActivity(waveActivities,"OPEN");

                if ((200 == GETResponse.statusCode())) {
                    Map<String, Set<String>> locationActivityDescMap = getLocationActivityTypeDescForWave(waveActivities);

                    packAwayPullPage.navigatetoPackAwayPull();

                    for (String location : locationActivityDescMap.keySet()) {

                        packAwayPullPage.enterPackAwayLocation(location);

                        for (String activityProcesAreaDesc : locationActivityDescMap.get(location)) {
                            String activityType = getActivityTypeByDesc(activityProcesAreaDesc);
                            String activityDesc = activityProcesAreaDesc.substring(0, activityProcesAreaDesc.length() - 4);

                            processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
                            processedGetQP.put("processArea", activityProcesAreaDesc.substring(activityProcesAreaDesc.length() - 3));
                            processedGetQP.put("locationNbr", location);
                            processedGetQP.put("type", activityType);
                            GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                            JSONArray wsmActivities = new JSONArray(GETResponse.asString());

                            packAwayPullPage.selectActivityProcessAreaDesc(activityProcesAreaDesc);

                            palletBarCode = packAwayPullPage.scanRandomPallet();

                            for (int i = 0; i < wsmActivities.length(); i++) {
                                JsonPath wsmActivity = new JsonPath(wsmActivities.get(i).toString());
                                String activityID = wsmActivity.getString("id");
                                String binboxBarcode = wsmActivity.getString("containerId");
                                String upc = wsmActivity.getString("upc");

                                if (("NIL".equals(skuAction.get(upc)))) {
                                    packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                                    packAwayPullPage.clickNotInLocation();
                                    packAwayPullPage.clickCloseButton();
                                    validateWSMStatus(activityID, "CANCELLED");
                                    dataStorage.getStoredData().put("binboxBarcode",binboxBarcode);
                                    log.info("binboxBarcode of cancelled bin: "+binboxBarcode);
                                    if ("BINPULL".equals(activityType)) {
                                        binBoxList.add(binboxBarcode);
                                    }
                                } else if (("SUB".equals(skuAction.get(upc)))) {
                                    packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                                    String substituteBINBOX = createSubstituteBinBox(binboxBarcode, location);
                                    log.info(substituteBINBOX);
                                    packAwayPullPage.selectSubstituteBinBox(substituteBINBOX);
                                    validateWSMStatus(activityID, "CANCELLED");
                                    HashMap<String, String> activityAttributes = new HashMap<String, String>();
                                    activityAttributes.put("container", substituteBINBOX);
                                    activityAttributes.put("type", activityType);
                                    activityAttributes.put("waveNumber", waveNumber);
                                    validateWSMStatus(activityAttributes, "COMPLETED");
                                    if ("BINPULL".equals(activityType)) {
                                        binBoxList.add(substituteBINBOX);
                                    }
                                } else if (("ALT".equals(skuAction.get(upc)))) {
                                    packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                                    String alternateBINBOX = createSubstituteBinBox(binboxBarcode, skuAltLocation.get(upc));
                                    log.info(alternateBINBOX);
                                    packAwayPullPage.selectAlternateBinBox(alternateBINBOX);
                                    validateWSMStatus(activityID, "CANCELLED");
                                    HashMap<String, String> activityAttributes = new HashMap<String, String>();
                                    activityAttributes.put("container", alternateBINBOX);
                                    activityAttributes.put("type", activityType);
                                    activityAttributes.put("waveNumber", waveNumber);
                                    validateWSMStatus(activityAttributes, "OPEN");
                                    altLocations.add(skuAltLocation.get(upc));
                                } else if (("COMPLETE".equals(skuAction.get(upc)))){
                                    packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                                    packAwayPullPage.scanBinBox(binboxBarcode);
                                    packAwayPullPage.clickCloseButton();
                                    validateWSMStatus(activityID, "COMPLETED");
                                    if ("BINPULL".equals(activityType)) {
                                        binBoxList.add(binboxBarcode);
                                    }
                                }
                                else {
                                    packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                                    validateWSMStatus(activityID, "ASSIGNED");
                                    if ("BINPULL".equals(activityType)) {
                                        binBoxList.add(binboxBarcode);
                                    }
                                }
                            }
                        }
                    }

                    // Scan Alternate Locations and Complete activities - Need separate for block to avoid endless loop

                    for (String location : altLocations) {

                        processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
                        processedGetQP.put("locationNbr", location);
                        GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                        JSONArray altActivities = new JSONArray(GETResponse.asString());
                        Set<String> locationActivityDescSet = getLocationActivityTypeDescForWave(altActivities).get(location);

                        packAwayPullPage.enterPackAwayLocation(location);

                        for (String activityProcesAreaDesc : locationActivityDescSet) {
                            String activityType = getActivityTypeByDesc(activityProcesAreaDesc);
                            String activityDesc = activityProcesAreaDesc.substring(0, activityProcesAreaDesc.length() - 4);

                            processedGetQP = commonUtils.getParamsToMap(getBinPullSplitQueryParams.replace("#waveNumber", waveNumber));
                            processedGetQP.put("processArea", activityProcesAreaDesc.substring(activityProcesAreaDesc.length() - 3));
                            processedGetQP.put("locationNbr", location);
                            processedGetQP.put("type", activityType);

                            GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                            JSONArray wsmActivities = new JSONArray(GETResponse.asString());

                            packAwayPullPage.selectActivityProcessAreaDesc(activityProcesAreaDesc);
                            if (activityProcesAreaDesc.contains("Bin Pull Split")) {
                                packAwayPullPage.verifyPackAwayLocation(location);
                            }

                            palletBarCode = packAwayPullPage.scanRandomPallet();

                            for (int i = 0; i < wsmActivities.length(); i++) {
                                JsonPath wsmActivity = new JsonPath(wsmActivities.get(i).toString());
                                String activityID = wsmActivity.getString("id");
                                String binboxBarcode = wsmActivity.getString("containerId");
                                if ("BINPULL".equals(activityType)) {
                                    binBoxList.add(binboxBarcode);
                                }

                                packAwayPullPage.validateActivity(wsmActivities.getJSONObject(i), activityDesc, location, palletBarCode);
                                packAwayPullPage.scanBinBox(binboxBarcode);
                                validateWSMStatus(activityID, "COMPLETED");
                            }
                            packAwayPullPage.validateActivityAlertMessage();
                            packAwayPullPage.enterDropLocation();
                        }

                        packAwayPullPage.validateZoneAlertMessage();

                    }
                    packAwayPullPage.goToPreviousPage();
                    dataStorage.getStoredData().put(waveCount + "binBoxList", binBoxList);

                } else {
                    log.info("Unable to get wsmActivties or no Activities exist for Wave Number " + waveNumber + ". WSM Response Code " + GETResponse.statusCode());
                }
            }
        }
    }

    @Then("validate the inventory attribute values for the cancelled bin$values")
    public void validateConditionCode(ExamplesTable values) throws InterruptedException {
        boolean flag = false;
        Map<String, String> row = values.getRow(0);
        String endPoint = commonUtils.getUrl(row.get("requestUrl"));
        String[] concode = row.get("targetCondCode").split(",");
        String barcode = dataStorage.getStoredData().get("binBoxBarcode").toString();
        Map<String, String> getcontainerQueryParams = Splitter.on(",").withKeyValueSeparator(":").split(row.get("queryParams").replace("container.barCode", barcode));
        Response response = WhmRestCoreAutomationUtils.getRequestResponse(endPoint, getcontainerQueryParams).asResponse();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Container Details : ", response.asString(), validationUtils.validateResponseStatusCode(response, 200));
        JsonPath jpath = new JsonPath(response.asString());
        String statusCode = "", conditionCodeList = "", containerStatusCode = "";
        StepDetail.addDetail("containerId : " + barcode, true);
        if (jpath.get("container.containerStatusCode") != null) {
            containerStatusCode = jpath.get("container.containerStatusCode").toString();
            Assert.assertEquals(containerStatusCode, concode[0]);
            StepDetail.addDetail("containerStatusCode : " + containerStatusCode, true);
        }
        if (jpath.get("inventorySnapshotList[0].statusCode") != null) {
            statusCode = jpath.get("inventorySnapshotList[0].statusCode").toString();
            Assert.assertEquals(statusCode, concode[1]);
            StepDetail.addDetail("statusCode : " + statusCode, true);
        }
        if (jpath.get("inventorySnapshotList[0].conditionCodeList") != null) {
            conditionCodeList = jpath.get("inventorySnapshotList[0].conditionCodeList[0]").toString();
            Assert.assertEquals(conditionCodeList, concode[2]);
            StepDetail.addDetail("conditionCodeList : " + conditionCodeList, true);
        }

        JSONObject jsonObject = new JSONObject(response.asString());
        JSONArray attributeList = jsonObject.getJSONArray("inventorySnapshotList").getJSONObject(0).getJSONArray("attributeList");
        for (int i = 0; i < attributeList.length(); i++) {
            String attributeListtype = attributeList.getJSONObject(i).getString("key");
            if (attributeListtype.equalsIgnoreCase("WaveNumber")) {
                flag = true;
            }
        }
        Assert.assertFalse(flag,"Attribute list contains attribute WaveNumber");
        StepDetail.addDetail("Attribute List does not contain WaveNumber attribute", true);
    }

    @Then("locate BinBox to processing stage location for create bin ICQA $values")
    public void locateBinsForCtreateBinICQA(ExamplesTable values) {
        if (1 == values.getRowCount()) {
            List<String> binboxList = (List<String>) dataStorage.getStoredData().get("BinBoxMap");
            Map<String, String> row = values.getRow(0);
            String endPoint = commonUtils.getUrl(row.get("requestUrl"));
            String locateContainerRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
            for (String inventoryRequest : binboxList) {
                    Response response = WhmRestCoreAutomationUtils.putRequestResponse(endPoint.replace("#sourceBarcode", inventoryRequest), locateContainerRequest).asResponse();
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Located Container",
                            "Locate Container Endpoint: " + endPoint.replace("#sourceBarcode", inventoryRequest) + "\n"
                                    + "Locate Container Request: " + locateContainerRequest + "\n"
                                    + "Locate Container Response Statuscode: " + response.getStatusCode(),
                            validationUtils.validateResponseStatusCode(response, 201));
            }
        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }
}
   

