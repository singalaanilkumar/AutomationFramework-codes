package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.Table;
import com.macys.mst.artemis.config.FileConfig;
import org.jbehave.core.steps.context.StepsContext;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.constants.MHE_MessagingReverseJSON;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.OBThroughMergeSQL;
import com.macys.mst.DC2.EndToEnd.model.CartonDetails;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.DockScanPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.*;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import io.restassured.RestAssured;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils.getUTCDatetimeAsString;
import static java.util.stream.Collectors.*;
import static org.testng.Assert.assertEquals;


@Slf4j
public class OBThroughMergeSteps {
    String subscription;
    private static StepsContext stepsContext;
    PubSubUtil pubSubUtil = new PubSubUtil();
    public long TestNGThreadID = Thread.currentThread().getId();
    public static final String SUCCESS_APPT_ASSIGNED_MSG = "Cartons Tied successfully to the Appointment";
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    DockScanPage dockScanPage = PageFactory.initElements(driver, DockScanPage.class);
    StepsDataStore dataStore = StepsDataStore.getInstance();
    public static final String SUCCESS_MSG = "Success";
    public static final String SUCCESS_APPT_Tied_MSG = "Cartons Tied successfully to the Appointment";
    private static final String SHIP_CONFIRM = "SHIPCONFIRM";
    public List<String> cartonsNumber = new ArrayList<>();

    public OBThroughMergeSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    CommonUtils commonUtils = new CommonUtils();
    RequestUtil requestUtil = new RequestUtil();
    RandomUtil randomUtil = new RandomUtil();
    ValidationUtil validationUtils = new ValidationUtil();
    HAFSteps hafSteps = new HAFSteps();
    private String doorNbr;

    @Given("$noOfCartons HAF CFCcartons in manifested status $examplesTable")
    public void createCFCCartonsInPackage(int noOfCartons, ExamplesTable examplesTable) throws InterruptedException {
        List<String> cartonsList = new ArrayList<>();
        if (noOfCartons > 0 && examplesTable != null && examplesTable.getRowCount() == 1) {
            for (int i = 0; i < noOfCartons; i++) {
                Map<String, String> row = examplesTable.getRows().get(0);
                String endPoint = FileConfig.getInstance().getStringConfigValue("services." + row.get("requestUrl").replace("#locNbr", "4420"));
                endPoint = endPoint.replace("{topicName}", commonUtils.getEnvConfigValue(row.get("topicName")));
                String createPackageRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
                createPackageRequest = createPackageRequest.replace("#ShipNbr", randomUtil.getRandomValue("RTF-D-6")).replace("#wgt", randomUtil.getRandomValue("D-2")).replace("#len", randomUtil.getRandomValue("D-2"));
                createPackageRequest = createPackageRequest.replace("#wdt", randomUtil.getRandomValue("D-2")).replace("#hgt", randomUtil.getRandomValue("D-2")).replace("#disc", randomUtil.getRandomValue("D-1"));
                createPackageRequest = createPackageRequest.replace("#base", randomUtil.getRandomValue("D-1")).replace("#fuel", randomUtil.getRandomValue("D-2")).replace("#total", randomUtil.getRandomValue("D-3"));
                createPackageRequest = createPackageRequest.replace("#spcl", randomUtil.getRandomValue("D-1")).replace("#transit", randomUtil.getRandomValue("D-1")).replace("#divertshipSeq", 1234567 + "" + i);
                cartonsList.add(requestUtil.getRandomeValueMaps().get(0).get("#barcode"));

                Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createPackageRequest).asResponse();

                CommonUtils.doJbehavereportConsolelogAndAssertion("publish carton event",
                        "pubsub Endpoint: " + endPoint + "\n"
                                + "pubsub Request: " + createPackageRequest + "\n"
                                + "pubsub Response Statuscode: " + response.getStatusCode(),
                        validationUtils.validateResponseStatusCode(response, 200));
            }
            dataStore.getStoredData().put("cfcCartonsList", cartonsList);
            dataStore.getStoredData().put("Legacy", true);
            log.info("List of cartons:{}", dataStore.getStoredData().get("cfcCartonsList"));
            TimeUnit.SECONDS.sleep(15);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Cartons are in manifested status in package domain", cartonsList.toString(), areContainersInExpStatus(cartonsList, "MFT", true));
            saveCartonsDetails(cartonsList, true);
        }
    }

    @Then("create an appointment in Loading status $examplesTable")
    public void createAppointment(ExamplesTable examplesTable) throws InterruptedException {

        if (examplesTable != null && examplesTable.getRowCount() == 1) {

            Map<String, String> row = examplesTable.getRows().get(0);
            String endPoint = commonUtils.getUrl(row.get("requestUrl"));
            endPoint = endPoint.replace("{topicName}", commonUtils.getEnvConfigValue(row.get("topicName")));
            String createAppointmentRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
            JSONObject requestJson = new JSONObject(createAppointmentRequest);
            dataStore.getStoredData().put("doorNbr", requestJson.getString("doorNbr"));
            doorNbr = requestJson.getString("doorNbr");
            //if there is any appointment is already present for the same door in LOADING move to LoadComplete
            updateApptAsLoadComplete();
            log.info("Url {} Appointment filled {}", endPoint, createAppointmentRequest);

            WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createAppointmentRequest);
            TimeUnit.SECONDS.sleep(10);
            CommonUtils.doJbehavereportConsolelogAndAssertion("publish create appointment event",
                    "pubsub Endpoint: " + endPoint + "\n"
                            + "pubsub Request: " + createAppointmentRequest, validateApptIsInExpectedStatus(Integer.toString(requestJson.getInt("apptNbr")), "LOADING"));
            dataStore.getStoredData().put("appointmentNbr", requestJson.getInt("apptNbr"));

        }

    }

    @When("update the appointment as loadComplete")
    public void updateApptAsLoadComplete() throws InterruptedException {
        String getDoorDetailUrl;
        String doorNbr = dataStore.getStoredData().get("doorNbr").toString();
        String flag = dataStore.getStoredData().get("Legacy").toString();
        if (flag.equalsIgnoreCase("true")) {
            getDoorDetailUrl = String.format(FileConfig.getInstance().getStringConfigValue("services." + "ddopsService.scanDoor").replace("#locNbr", "4420"), doorNbr);
        } else {
            getDoorDetailUrl = String.format(commonUtils.getUrl("ddopsService.scanDoor"), doorNbr);
        }
        Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).get(getDoorDetailUrl);
        TimeUnit.SECONDS.sleep(5);
        if (null != response && 200 == response.statusCode()) {
            JSONObject repObj = new JSONObject(response.asString());
            Integer appointmentNbr = repObj.getInt("apptNbr");
            if ("LOADING".equals(repObj.getString("apptStatDesc"))) ;

            String sql = OBThroughMergeSQL.UPDATE_APPT_LOADCOMPLETE.replace("#apptNbr", appointmentNbr.toString());
            DBMethods.deleteOrUpdateDataBase(sql, "appointment");
            Boolean validationStatus = validateApptIsInExpectedStatus(appointmentNbr.toString(), "LOAD COMPLETE");
            TimeUnit.SECONDS.sleep(5);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Appointment is updated as Load complete : ", validationStatus.toString(), validationStatus);
            validateApptIsInExpectedStatus(appointmentNbr.toString(), "LOAD COMPLETE");
        }
    }

    public boolean validateApptIsInExpectedStatus(String apptNbr, String expectedStatus) throws InterruptedException {
        String getDoorDetailUrl = String.format(commonUtils.getUrl("appointmentService.getApptDetailsByApptNbr"), apptNbr);
        Response response = WhmRestCoreAutomationUtils.getRequestResponse(getDoorDetailUrl).asResponse();
        if (null != response && 200 == response.statusCode()) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("get Appointment details for given door",
                    "getDoorDetails URL: " + getDoorDetailUrl + "\n"
                            + "response " + response, true);
            TimeUnit.SECONDS.sleep(10);
            return expectedStatus.equalsIgnoreCase(response.jsonPath().getString("apptStatDesc"));

        } else {
            return false;
        }
    }

    @Then("scan door $doorNumber and select $option")
    @Composite(steps = {"When scan door $doorNumber number",
            "When User selects $option"})
    public void scanDoorAndSelectLastScan(String doorNumber, String option) {
    }

    @When("validate $flag cartons are tied to the appointment")
    public void validateCartonsTiedToAppointment(String flag) throws InterruptedException {
        List<String> cartonList = (List<String>) dataStore.getStoredData().get("cartonsList");
        String endPoint;
        String lLoc = flag;
        String appointNumber = dataStore.getStoredData().get("appointmentNbr").toString();
        if (lLoc.equals("legacy")) {
            endPoint = FileConfig.getInstance().getStringConfigValue("services." + "packageService.getPackageByAppointment").replace("#locNbr", "4420");
        } else
            endPoint = new CommonUtils().getUrl("packageService.getPackageByAppointment");
        Map<String, String> getCallQueryParams = new HashMap<>();
        getCallQueryParams.put("apptNbr", appointNumber);
        Response response = WhmRestCoreAutomationUtils.getRequestResponse(endPoint, getCallQueryParams).asResponse();
        List<String> cartonListAPI = response.jsonPath().getList("content.barcode");
        Boolean validationStatus = validateCartonsAfterTieing(cartonListAPI);
        TimeUnit.SECONDS.sleep(3);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Cartons are in manifested status in package domain for appointment no:", appointNumber, validationStatus);
    }

    public boolean validateCartonsAfterTieing(List<String> cartonListAPI) {
        List<String> cartonList = (List<String>) dataStore.getStoredData().get("cfcCartonsList");
        log.info("Actual Carton Barcode {} ", cartonListAPI);
        Collections.sort(cartonListAPI);
        Collections.sort(cartonList);
        for (int i = 0; i < cartonListAPI.size(); i++) {
            String expBarcode = cartonList.get(i);
            String actualBarCode = cartonListAPI.get(i);
            if (expBarcode.equals(actualBarCode)) {
                CommonUtils.doJbehavereportConsolelogAndAssertion("Expected and actual Barcodes match for carton " + i + ": ", actualBarCode, true);
            } else {
                CommonUtils.doJbehavereportConsolelogAndAssertion("Expected and actual Barcodes dosen't match for carton " + i + ": ", actualBarCode, false);
                return false;
            }
        }

        return true;
    }

    @When("Do Last scan on last carton, on that door $doorNbr")
    public void performLastScan(String doorNbr) throws InterruptedException {
        log.info("performLastScan: option: {}, Door Nbr:{}", doorNbr);
        String barcode;
        int size;
        List<String> cartonList = (List<String>) dataStore.getStoredData().get("cfcCartonsList");
        log.info("performLastScan: Door Nbr:{}", doorNbr);
        size = cartonList.size();
        barcode = cartonList.get(size - 1);
        log.info("Scan Last Carton barcode:{} ", barcode);
        dockScanPage.scanCartonNumber(barcode);
        dockScanPage.validateWarningPopup(String.format("Total %d cartons available to Tie. Do you want to proceed?", size), "yes");
        dockScanPage.validateInlineMessage(size + " " + SUCCESS_APPT_Tied_MSG, SUCCESS_MSG);
        TimeUnit.SECONDS.sleep(3);
    }

    @Then("divert the package through Pyramid to $location")
    public void diverPackage(String location) throws Exception {
        Map<String, List<String>> cartonIdToteMap = (Map<String, List<String>>) stepsContext.get(Context.CARTON_TOTE_MAP.name());
        cartonIdToteMap.keySet().forEach(cartonId -> {
            String seqId = randomUtil.getRandomValue("40000-D-4");
            String jsonResult = MHE_MessagingReverseJSON.SHIP_CONFIRM_MESSAGE;
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jsonResult = jsonResult
                    .replace("#sequenceno#", seqId)
                    .replace("#barCode#", cartonId)
                    .replace("#shipLane#", location);
            log.info("SHIPCONFIRM message: {}", jsonResult);
            StepDetail.addDetail("Divertship Payload:" + seqId + "|" + "SHIPCONFIRM" + "|" + cartonId + "|" + location, true);
            CommonUtils.pyramidResponseValidation(jsonResult);
            StepDetail.addDetail("Successfully sent Ship Confirm Message", true);
            String trasName = String.format("%s:%s", seqId, SHIP_CONFIRM);
            CommonUtils.verifyPutToStoreMsgResponse(trasName, SHIP_CONFIRM, cartonId);

            CommonUtils.waitSec(5);

            String response = commonUtils.getContainerDetailsbyBarcode(cartonId);
            if (null != response) {
                JsonPath cartondetail = new JsonPath(response);
                if (CommonUtils.packageFlag) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Status validated as SHR for Carton:", cartonId, "SHR".equals(cartondetail.getString("[0].status")));
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: ShipDivert Location validated as " + location, cartonId, location.equals(cartondetail.getString("[0].location")));
                    Boolean StatusDivertshipSeq = !(cartondetail.getString("[0].divertshipSeq").isEmpty());
                    Boolean StatusDivertshipTS = !(cartondetail.getString("[0].divertshipTs").isEmpty());
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: ShipDivert for divertshipSeq:" + " for Carton ID", cartonId, StatusDivertshipSeq);
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: ShipDivert for divertship TimeStamp" + " for Carton ID", cartonId, StatusDivertshipTS);
                }
            } else {
                CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Response is NULL for Divertship ", cartonId, false);
            }
        });
    }

    public void saveCartonsDetails(List<String> cartons, Boolean flag) {
        List<CartonDetails> cartonDetails = new ArrayList<>();
        cartons.forEach(carton -> {
            String packageResponse = getPackagesDetail(carton, flag);
            JsonPath jpath = new JsonPath(packageResponse);
            String barCode = jpath.getString("content.barcode[0]");
            cartonDetails.add(new CartonDetails(String.valueOf(jpath.getInt("content.id[0]")), barCode, 0, jpath.getString("content.shipVia[0]"), String.valueOf(jpath.getInt("content.storeLocnNbr[0]")), ""));
        });
        stepsContext.put(Context.CARTON_DETAILS.name(), cartonDetails, ToContext.RetentionLevel.SCENARIO);

        Map<String, List<String>> cartonIdToteMap = cartonDetails.stream().collect(groupingBy(CartonDetails::getCartonId, mapping(CartonDetails::getToteId, toList())));
        log.info("cartonIdToteMap : {}", cartonIdToteMap);
        stepsContext.put(Context.CARTON_TOTE_MAP.name(), cartonIdToteMap, ToContext.RetentionLevel.SCENARIO);
    }

    public String getPackagesDetail(String barcode, Boolean flag) {
        String endPoint;
        if (flag == true) {
            endPoint = String.format(FileConfig.getInstance().getStringConfigValue("services." + "packageService.searchPackage").replace("#locNbr", "4420"), barcode);
        } else endPoint = String.format(commonUtils.getUrl("packageService.searchPackage"), barcode);

        Response response = WhmRestCoreAutomationUtils.getRequestResponse(endPoint).asResponse();
        if (200 != response.statusCode()) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("No Package available",
                    "NA",
                    validationUtils.validateResponseStatusCode(response, 204));
        }
        return response.asString();
    }

    @Then("create an legacy appointment in Loading status $examplesTable")
    public void createLegacyAppointment(ExamplesTable examplesTable) throws InterruptedException {

        if (examplesTable != null && examplesTable.getRowCount() == 1) {

            Map<String, String> row = examplesTable.getRows().get(0);
            String endPoint = FileConfig.getInstance().getStringConfigValue("services." + row.get("requestUrl")).replace("#locNbr", "4420");
            endPoint = endPoint.replace("{topicName}", commonUtils.getEnvConfigValue(row.get("topicName")));
            String createAppointmentRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
            JSONObject requestJson = new JSONObject(createAppointmentRequest);
            dataStore.getStoredData().put("doorNbr", requestJson.getString("doorNbr"));
            doorNbr = requestJson.getString("doorNbr");
            //if there is any appointment is already present for the same door in LOADING move to LoadComplete
            updateApptAsLoadComplete();
            log.info("Url {} Appointment filled {}", endPoint, createAppointmentRequest);

            WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createAppointmentRequest);
            TimeUnit.SECONDS.sleep(10);
            CommonUtils.doJbehavereportConsolelogAndAssertion("publish create appointment event",
                    "pubsub Endpoint: " + endPoint + "\n"
                            + "pubsub Request: " + createAppointmentRequest, validateApptIsInExpectedStatus(Integer.toString(requestJson.getInt("apptNbr")), "LOADING"));
            dataStore.getStoredData().put("appointmentNbr", requestJson.getInt("apptNbr"));
        }

    }

    @Given("update Package $value")
    public void updatePackage(ExamplesTable values) {
        Map<String, String> updateParam;
        String requestUrl;
        if (values.getRowCount() > 0) {
            Map<String, String> row = values.getRows().get(0);
            List<Map<String, String>> updateParams = requestUtil.getParmasToListOfMaps(row.get("updateParam"));
            requestUrl = row.get("requestUrl");
            updateParam = updateParams.get(0);
        } else {
            throw new IncorrectDataException("Require atleast one row of data");
        }
        List<CartonDetails> cartonDetails = (List<CartonDetails>) stepsContext.get(Context.CARTON_DETAILS.name());
        String appointNumber = dataStore.getStoredData().get("appointmentNbr").toString();
//        String shipVia = dataStore.getStoredData().get("shipVia").toString();
        JSONArray jsonArray = new JSONArray();

        int count = 0;
        for (CartonDetails cartonDetail : cartonDetails) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cartonDetail.getSequenceId());
            jsonObject.put("barcode", cartonDetail.getCartonId());
            if (null != updateParam.get("apptNbr")) {
                jsonObject.put("apptNbr", appointNumber);
            }
            if (null != updateParam.get("divertshipSeq") && Boolean.valueOf(updateParam.get("divertshipSeq"))) {
                jsonObject.put("divertshipSeq", ++count);
                jsonObject.put("divertshipTs", getUTCDatetimeAsString());
            }
            jsonArray.put(jsonObject);
        }
        WhmRestCoreAutomationUtils.putRequestResponse(commonUtils.getUrl(requestUrl), jsonArray.toString());
    }

    @Given("$noOfCartons BACKSTAGE package in PCK status $examplesTable")
    public void createBackStagePackage(int noOfCartons, ExamplesTable examplesTable) throws InterruptedException {
        List<String> cartonsList = new ArrayList<>();
        for (int i = 0; i < noOfCartons; i++) {
            Map<String, String> row = examplesTable.getRows().get(0);
            String endPoint = commonUtils.getUrl(row.get("requestUrl"));
            String createPackageRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
            cartonsList.add(requestUtil.getRandomeValueMaps().get(0).get("#barcode"));

            Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createPackageRequest).asResponse();
            CommonUtils.doJbehavereportConsolelogAndAssertion("Created Package",
                    "Create Package Endpoint: " + endPoint + "\n"
                            + "Create Package Request: " + createPackageRequest + "\n"
                            + "Create Package Response Statuscode: " + response.getStatusCode(),
                    validationUtils.validateResponseStatusCode(response, 201));
        }
        saveCartonsDetails(cartonsList, false);
        log.info("Cartons Information:{}", cartonsList);
        dataStore.getStoredData().put("cfcCartonsList", cartonsList);
        dataStore.getStoredData().put("cartonList", cartonsList);
        dataStore.getStoredData().put("Legacy", false);
    }

    public Boolean areContainersInExpStatus(List<String> containers, String expectedStatus, Boolean flag) {
        List<Boolean> status = new ArrayList<>();
        //the delay is intentional to make sure all the messages/updates are processed
        try {
            TimeUnit.SECONDS.sleep(3);
            for (String container : containers) {
                status.add(isContainerInExpStatus(container, expectedStatus, flag));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !status.contains(false);
    }

    public Boolean isContainerInExpStatus(String container, String expectedStatus, Boolean flag) throws InterruptedException {
        String response = getContainerDetailsbyBarcode(container, flag);
        JsonPath containerDetail = new JsonPath(response);
        if (null != response && !response.isEmpty()) {
            if (flag == true) {
                CommonUtils.doJbehavereportConsolelogAndAssertion("Container Status validated as " + expectedStatus + " for Carton: Carton ID", container, expectedStatus.equals(containerDetail.getString("[0].status")));
            } else {
                CommonUtils.doJbehavereportConsolelogAndAssertion("Container Status validated as " + expectedStatus + " for Carton: Carton ID", container, expectedStatus.equals(containerDetail.getString("container.containerStatusCode")));
            }
            return true;
        } else {
            return false;
        }
    }

    public String getContainerDetailsbyBarcode(String barcode, Boolean flag) throws InterruptedException {
        TimeUnit.SECONDS.sleep(15);
        String endpoint;
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("barcode", barcode);
        if (flag == true) {
            endpoint = FileConfig.getInstance().getStringConfigValue("services." + "packageService.createPackage").replace("#locNbr", "4420");
        } else
            endpoint = commonUtils.getUrl("packageService.createPackage");

        Response response = WhmRestCoreAutomationUtils.getRequestResponse(endpoint, queryParams).asResponse();
        return response.asString();
    }

    @When("MMS CLSCTN message $subscription subscriber is available")
    public void createMMSSubscriberForClosecartonMessage(String subscription) {
        try {
            pubSubUtil.deleteSubscription(subscription);
            String topicId = commonUtils.getEnvConfigValue("pubSub.topics.closeCartonMessageForMMS");
            pubSubUtil.createSubscription(subscription, topicId);
            pubSubUtil.subscribeProject(subscription);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("################################ Subscriber started and running ################################");
    }

    @Then("validate MMS CLSCTN messages for $source are published and valid")
    public void validateMMSClSCTN(String source) {

        String shipnmbr = null;
        String orderId = null;
        List<String> cartonList = new ArrayList<>();
        if (source.equalsIgnoreCase("PO")) {
            cartonList = (List<String>) dataStore.getStoredData().get("cartonDetailsList");

        } else if (source.equalsIgnoreCase("Wave")) {
            cartonList = (List<String>) dataStore.getStoredData().get("wave1cartonDetailsList");
        }
        log.info(cartonList.toString());
        for (int i = 0; i < cartonList.size(); i++) {
            Map<String, String> expctValue = new HashMap<>();
            expctValue = exptValue(source);
            JSONObject jb = new JSONObject();
            List<JSONObject> closeCartonJsons = new ArrayList<>();
            String carton = (String.valueOf(cartonList.get(i)).split("=")[2].replace(", totalQuantity", ""));
            String val[] = String.valueOf(cartonList.get(i)).replaceAll("CartonDetails\\p{P}", "").split(",");
            for (String data : val) {
                String details[] = data.split("=");
                jb.put(details[0].trim(), details[1]);
                if ("skuupc".equalsIgnoreCase(details[0].trim())) {
                    expctValue.put(details[0].trim(), details[1].substring(0, 12));
                } else {
                    expctValue.put(details[0].trim(), details[1]);
                }
            }
            // if (source.equalsIgnoreCase("PO")) {
            expctValue.remove("toteId");
            expctValue.remove("storeLocationNumber");
            expctValue.remove("sequenceId");

            //}
            //only for wave count
            if (source.equalsIgnoreCase("Wave")) {
                Table<String, String, String> rtfsToVerify = (Table<String, String, String>) dataStore.getStoredData().get("publishedRTFs");
                for (String key : rtfsToVerify.rowKeySet()) {
                    for (Map.Entry<String, String> row : rtfsToVerify.row(key).entrySet()) {
                        /*   System.out.println(row.getKey()+"-----------------------"+row.getValue());*/
                        boolean st = row.getValue().contains(jb.get("skuupc").toString().substring(0, 12)) && row.getValue().contains(jb.getString("storeLocationNumber"));
                        if (st) {
                            JSONObject rtf = new JSONObject(row.getValue());
                            shipnmbr = rtf.getJSONObject("shipment").get("orderShipmentNbr").toString();
                            orderId = rtf.getJSONObject("orderHeader").get("orderID").toString();
                            expctValue.put("orderID", orderId);
                            expctValue.put("shipmentNmbr", shipnmbr);
                            break;
                        }
                    }
                    log.info("ExpectedValue: " + expctValue.toString());
                }
            }

            try {

                //allowed delay to get all the closecarton message get published
                TimeUnit.SECONDS.sleep(30);
                String messagePulled = pubSubUtil.readMessage("\"orderId\":\"" + carton + "\"", 0);
                log.info("Message pulled: " + messagePulled);
                if (messagePulled != null) {
                    closeCartonJsons.add(new JSONObject(messagePulled));
                    for (JSONObject jobj : closeCartonJsons) {
                        String actualvalue = null;
                        //iterating expected map
                        Map<String, String> actualValue = actualVal(jobj, source, shipnmbr, jb.get("skuupc").toString().substring(0, 12));
                        log.info("Expected Value: " + expctValue.toString());
                        log.info("Actual Value: " + actualValue);
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Exp Vs Act carton map comparision:", String.valueOf(closeCartonJsons.size()), actualValue.equals(expctValue));
                        CommonUtils.doJbehavereportConsolelogAndAssertion("close carton json for carton is found and validated: ", carton, true);

                    }
                } else {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Couldn't find close carton json for carton: ", carton, false);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }//for end
        pubSubUtil.stopSubscriber();
        try {
            pubSubUtil.deleteSubscription(subscription);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Map<String, String> actualVal(JSONObject jobj, String source, String expectedShipnmbr, String expskuupc) {
        Map<String, String> actval = new HashMap<>();
        actval.put("cartonId", jobj.getJSONObject("fulfillmentOrder").getJSONObject("orderHeader").get("orderId").toString());
        actval.put("orderType", jobj.getJSONObject("fulfillmentOrder").getJSONObject("orderHeader").get("orderType").toString());
        actval.put("sourceApplicationId", jobj.getJSONObject("fulfillmentOrder").getJSONObject("orderHeader").get("sourceApplicationId").toString());
        actval.put("eventType", jobj.getJSONObject("fulfillmentOrder").getJSONObject("orderHeader").get("eventType").toString());
        actval.put("shipmentorderType", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("orderType").toString());
        if (jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).getJSONObject("cartonContentList").getJSONArray("cartonContent").getJSONObject(0).get("itemSkuUpc").toString().equals(expskuupc.substring(0, 12))) {
            actval.put("totalQuantity", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).getJSONObject("cartonContentList").getJSONArray("cartonContent").getJSONObject(0).get("itemPackedQuantity").toString());
            actval.put("skuupc", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).getJSONObject("cartonContentList").getJSONArray("cartonContent").getJSONObject(0).get("itemSkuUpc").toString());
        } else {
            actval.put("totalQuantity", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(1).getJSONObject("cartonContentList").getJSONArray("cartonContent").getJSONObject(0).get("itemPackedQuantity").toString());
            actval.put("skuupc", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(1).getJSONObject("cartonContentList").getJSONArray("cartonContent").getJSONObject(0).get("itemSkuUpc").toString());
        }
        if (source != null && source.equalsIgnoreCase("PO")) {
            actval.put("orderSubType", jobj.getJSONObject("fulfillmentOrder").getJSONObject("orderHeader").get("orderSubType").toString());
            actval.put("ShipmentList ordertype", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("orderSubType").toString());
            actval.put("LocationNbr", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("fulfillmentLocationNbr").toString());
            actval.put("PO", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("poNumber").toString());
            actval.put("poReceipt", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("poReceipt").toString());
        } else if (source != null && source.equalsIgnoreCase("Wave")) {
            actval.put("orderSubType", jobj.getJSONObject("fulfillmentOrder").getJSONObject("orderHeader").get("orderSubType").toString());
            actval.put("ShipmentList ordertype", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("orderSubType").toString());
            actval.put("LocationNbr", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("fulfillmentLocationNbr").toString());
            actval.put("orderID", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("orderNumber").toString());
            if (jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("orderShipmentNbr").equals(expectedShipnmbr)) {
                actval.put("shipmentNmbr", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(0).get("orderShipmentNbr").toString());
            } else {
                actval.put("shipmentNmbr", jobj.getJSONObject("fulfillmentOrder").getJSONObject("shipmentList").getJSONArray("shipment").getJSONObject(1).get("orderShipmentNbr").toString());
            }
        }
        log.info("actualValue: " + actval.toString());
        return actval;
    }


    public Map<String, String> exptValue(String source) {
        Map<String, String> expValue = new HashMap<>();
        expValue.put("orderType", "Movement");
        expValue.put("sourceApplicationId", "Backstage");
        expValue.put("eventType", "SHPCONFIRM");
        expValue.put("shipmentorderType", "Movement");
        if (source.equalsIgnoreCase("wave")) {
            expValue.put("orderSubType", "TRQ");
            expValue.put("ShipmentList ordertype", "TRQ");
            expValue.put("LocationNbr", "7222");
        } else {
            expValue.put("PO", dataStore.getStoredData().get("poNbr").toString());
            expValue.put("poReceipt", dataStore.getStoredData().get("receiptNbr").toString());
            expValue.put("orderSubType", "PO");
            expValue.put("ShipmentList ordertype", "PO");
            expValue.put("LocationNbr", "7221");
        }
        log.info("ExplValue: " + expValue.toString());
        return expValue;
    }
}