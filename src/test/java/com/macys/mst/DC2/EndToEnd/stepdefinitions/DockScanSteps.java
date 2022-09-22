package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import java.util.stream.Collectors;

import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;

import static com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils.getWarehouseLocNbr;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.model.CartonDetails;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.DockScanPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.IncorrectDataException;
import com.macys.mst.DC2.EndToEnd.utilmethods.RequestUtil;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils.getUTCDatetimeAsString;
import static com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils.getWarehouseLocNbr;
import static java.util.stream.Collectors.*;
import static org.testng.Assert.assertEquals;

@Slf4j
public class DockScanSteps {
    CommonUtils commonUtils = new CommonUtils();
    RequestUtil requestUtil = new RequestUtil();
    RandomUtil randomUtil = new RandomUtil();
    HAFSteps hafSteps = new HAFSteps();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    public static final String DOOR_LANE_MAP = "doorLaneMap";
    public static final String SHIP_VIA = "shipVia";
    public static final String LANE_NUMBER = "laneNbr";
    public static final String APPOINTMENT_NUMBER = "apptNbr";
    public static final String CARTON_BARCODES = "cartonBarcodes";
    public static final String CARTON_BARCODES_IDS = "cartonBarcodeIds";
    public static final String DOOR_APPOINTMENT_MAP = "doorAppointmentMap";
    public static final String DOOR_SHIPVIA_MAP = "doorShipviaMap";
    public static final String LOCATION_CARTON_MAP = "locationCartonMap";
    public static final String SHIPVIA_LANE_MAP = "shipviaLaneMap";
    public static final String LAST_SCAN_CARTONS = "lastscanCartons";
    public static final String SUCCESS_APPT_ASSIGNED_MSG = "Cartons Tied successfully to the Appointment";
    public static final String SUCCESS_TIE_APPT_ASSIGNED_MSG = "1 Carton Tied successfully to the Appointment";
    public static final String ERROR_APPT_ALREADY_ASSIGNED_MSG = "Carton is already Tied to this appointment";
    public static final String SUCCESS_UNTIE_APPT_UNASSIGNED_MSG = "1 Carton Untied successfully from the Appointment";
    public static final String ERROR_NO_APPT_EXIT_MSG = "Carton is not Tied to any Appointment. Cannot Untie.";
    public static final String SUCCESS_MSG_TYPE = "Success";
    public static final String ERROR_MSG_TYPE = "Error";
    public static final String LAST_SCAN_FILTER_TYPE = "lastScanFilter";
    public List<String> cartonsNumber = new ArrayList<>();


    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private static StepsContext stepsContext;
    DockScanPage dockScanPage = PageFactory.initElements(driver, DockScanPage.class);
    private ValidationUtil validationUtils = new ValidationUtil();
    StepsDataStore dataStore = StepsDataStore.getInstance();

    public DockScanSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }


    @Given("Get configuration $values")
    public void getConfiguration(ExamplesTable values) {
        Map<String, String> row = values.getRow(0);
        String getConfigUrl = commonUtils.getUrl("configurationServices.GetRestrictedERSStatus");
        Response configResponse = WhmRestCoreAutomationUtils.getRequestResponse(getConfigUrl.replace("{AppName}", row.get("appName")).replace("{MName}", row.get("moduleName")).replace("{CName}", row.get("configKey"))).asResponse();
        Response dockScanTechConfiguration = WhmRestCoreAutomationUtils.getRequestResponse(getConfigUrl.replace("{AppName}", row.get("appName")).replace("{MName}", row.get("moduleName")).replace("{CName}", "dockScanTechConfiguration")).asResponse();
        if (200 == dockScanTechConfiguration.statusCode()) {

            List<String> configValues = new JsonPath(dockScanTechConfiguration.asString()).getList("configValue");
            log.info("configValues:::{}", configValues);
            stepsContext.put(LAST_SCAN_FILTER_TYPE, configValues.get(0).contains("SEQUENCE"), ToContext.RetentionLevel.SCENARIO);

        }
        if (200 == configResponse.statusCode()) {
            List<String> configValues = new JsonPath(configResponse.asString()).getList("configValue");
            JSONArray jsonArray = new JSONArray(configValues.get(0));
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            String door = jsonObject.getString("Door");
            String lane = jsonObject.getString("Lane");
            Map<String, String> dockDoorLaneMap = new HashMap<>(3);
            dockDoorLaneMap.put(door, lane);
            stepsContext.put(DOOR_LANE_MAP, dockDoorLaneMap, ToContext.RetentionLevel.SCENARIO);
            log.info("dockDoorLaneMap:{}", dockDoorLaneMap);
        } else {
            CommonUtils.doJbehavereportConsolelogAndAssertion("No configuration available",
                    "NA",
                    validationUtils.validateResponseStatusCode(configResponse, 204));
        }
    }

    @Given("Get Door Details $doorNumber")
    public void getDoorDetails(String doorNumber) {
        String getDoorDetailUrl = String.format(commonUtils.getUrl("ddopsService.scanDoor"), doorNumber);
        Response response = WhmRestCoreAutomationUtils.getRequestResponse(getDoorDetailUrl).asResponse();

        if (200 == response.statusCode()) {
            JSONObject repObj = new JSONObject(response.asString());
            try {
                Map<String, Integer> dockDoorAppointmentMap = (Map<String, Integer>) stepsContext.get(DOOR_APPOINTMENT_MAP);
                Map<String, Integer> dockDoorShipViaMap = (Map<String, Integer>) stepsContext.get(DOOR_SHIPVIA_MAP);
                Map<Integer, String> shipViaLaneMap = (Map<Integer, String>) stepsContext.get(SHIPVIA_LANE_MAP);

                dockDoorAppointmentMap.putIfAbsent(doorNumber, repObj.getInt(APPOINTMENT_NUMBER));
                dockDoorShipViaMap.putIfAbsent(doorNumber, repObj.getInt(SHIP_VIA));
                shipViaLaneMap.putIfAbsent(repObj.getInt(SHIP_VIA), repObj.getString(LANE_NUMBER));
            } catch (StepsContext.ObjectNotStoredException e) {
                Map<String, Integer> dockDoorAppointmentMap = new HashMap<>(1);
                Map<String, Integer> dockDoorShipViaMap = new HashMap<>(1);
                Map<Integer, String> shipViaLaneMap = new HashMap<>(1);

                dockDoorAppointmentMap.put(doorNumber, repObj.getInt(APPOINTMENT_NUMBER));
                dockDoorShipViaMap.put(doorNumber, repObj.getInt(SHIP_VIA));
                shipViaLaneMap.put(repObj.getInt(SHIP_VIA), repObj.getString(LANE_NUMBER));

                stepsContext.put(DOOR_APPOINTMENT_MAP, dockDoorAppointmentMap, ToContext.RetentionLevel.SCENARIO);
                stepsContext.put(DOOR_SHIPVIA_MAP, dockDoorShipViaMap, ToContext.RetentionLevel.SCENARIO);
                stepsContext.put(SHIPVIA_LANE_MAP, shipViaLaneMap, ToContext.RetentionLevel.SCENARIO);
            }


            log.info("DOOR_APPOINTMENT_MAP:{}", stepsContext.get(DOOR_APPOINTMENT_MAP));
            log.info("DOOR_SHIPVIA_MAP:{}", stepsContext.get(DOOR_SHIPVIA_MAP));
            log.info("SHIPVIA_LANE_MAP:{}", stepsContext.get(SHIPVIA_LANE_MAP));


        } else {
            CommonUtils.doJbehavereportConsolelogAndAssertion("given door details not available:" + doorNumber,
                    "NA",
                    false);
        }
    }

    @Given("Create Package $values")
    public void createPackage(ExamplesTable values) {
        if (values.getRowCount() > 0) {
            List<String> cartons = new ArrayList<>();
            Map<String, Integer> dockDoorShipViaMap = (Map<String, Integer>) stepsContext.get(DOOR_SHIPVIA_MAP);
            for (Map<String, String> row : values.getRows()) {
                String endPoint = commonUtils.getUrl(row.get("requestUrl"));
                String doorNumber = row.get("doorNumber");
                String createPackageRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
                JsonPath jpath = new JsonPath(createPackageRequest);
                cartons.add(jpath.getString("barcode"));
                String shipVia = jpath.getString("shipVia");
                if (null != shipVia && shipVia.contains("#shipVia")) {
                    if (dockDoorShipViaMap.containsKey(doorNumber)) {
                        createPackageRequest = createPackageRequest.replace("#shipVia", String.valueOf(dockDoorShipViaMap.get(doorNumber)));
                    } else {
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Invalid shipVia. Door Nbr:", doorNumber, false);
                    }
                }

                Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createPackageRequest).asResponse();

                CommonUtils.doJbehavereportConsolelogAndAssertion("Created Package",
                        "Create Package Endpoint: " + endPoint + "\n"
                                + "Create Package Request: " + createPackageRequest + "\n"
                                + "Create Package Response Statuscode: " + response.getStatusCode(),
                        validationUtils.validateResponseStatusCode(response, 201));
            }
            stepsContext.put(CARTON_BARCODES, cartons, ToContext.RetentionLevel.SCENARIO);
            log.info("List of cartons:{}", cartons);
            cartonsNumber = cartons;
            saveCartonDetails(cartons);


        } else {
            throw new IncorrectDataException("Require atleast one row of data");
        }

    }

    public void saveCartonDetails(List<String> cartons) {
        List<CartonDetails> cartonDetails = new ArrayList<>();
        cartons.forEach(carton -> {
            String packageResponse = getPackageDetail(carton);
            JsonPath jpath = new JsonPath(packageResponse);
            String barCode = jpath.getString("content.barcode[0]");
            cartonDetails.add(new CartonDetails(String.valueOf(jpath.getInt("content.id[0]")), barCode, 0, jpath.getString("content.shipVia[0]"), String.valueOf(jpath.getInt("content.storeLocnNbr[0]")), ""));
        });
        stepsContext.put(Context.CARTON_DETAILS.name(), cartonDetails, ToContext.RetentionLevel.SCENARIO);

        Map<String, List<String>> cartonIdToteMap = cartonDetails.stream().collect(groupingBy(CartonDetails::getCartonId, mapping(CartonDetails::getToteId, toList())));
        log.info("cartonIdToteMap : {}", cartonIdToteMap);
        stepsContext.put(Context.CARTON_TOTE_MAP.name(), cartonIdToteMap, ToContext.RetentionLevel.SCENARIO);
    }

    @Given("Publish Carton Event $values")
    public void publishCartonEvent(ExamplesTable values) {
        if (values.getRowCount() > 0) {
            List<String> cartons = new ArrayList<>();
            Map<String, Integer> dockDoorShipViaMap = (Map<String, Integer>) stepsContext.get(DOOR_SHIPVIA_MAP);
            Map<Integer, String> shipViaLaneMap = (Map<Integer, String>) stepsContext.get(SHIPVIA_LANE_MAP);
            for (Map<String, String> row : values.getRows()) {
                String endPoint = commonUtils.getUrl(row.get("requestUrl"));
                endPoint = endPoint.replace("{topicName}", commonUtils.getEnvConfigValue(row.get("topicName")));
                String createPackageRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);
                String doorNumber = row.get("doorNumber");

                String dataJson = new JsonPath(createPackageRequest).getString("data");
                JsonPath jpath = new JsonPath(dataJson);
                cartons.add(jpath.getString("barcode"));

                String shipVia = jpath.getString("shipVia");
                String location = jpath.getString("location");

                createPackageRequest = createPackageRequest.replace("#divertshipTs", commonUtils.getUTCDatetimeAsString());

                if (null != shipVia && shipVia.contains("#shipVia")) {
                    if (dockDoorShipViaMap.containsKey(doorNumber)) {
                        createPackageRequest = createPackageRequest.replace("#shipVia", String.valueOf(dockDoorShipViaMap.get(doorNumber)));
                    } else {
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Invalid shipVia. Door Nbr:", doorNumber, false);
                    }
                }

                if (null != location && location.contains("#location")) {
                    if (shipViaLaneMap.containsKey(dockDoorShipViaMap.get(doorNumber))) {
                        createPackageRequest = createPackageRequest.replace("#location", shipViaLaneMap.get(dockDoorShipViaMap.get(doorNumber)));
                    } else {
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Invalid location. Door Nbr:", doorNumber, false);
                    }
                }

                Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createPackageRequest).asResponse();

                CommonUtils.doJbehavereportConsolelogAndAssertion("publish carton event",
                        "pubsub Endpoint: " + endPoint + "\n"
                                + "pubsub Request: " + createPackageRequest + "\n"
                                + "pubsub Response Statuscode: " + response.getStatusCode(),
                        validationUtils.validateResponseStatusCode(response, 200));


            }
            stepsContext.put(CARTON_BARCODES, cartons, ToContext.RetentionLevel.SCENARIO);
            log.info("List of cartons:{}", cartons);
            commonUtils.waitSec(10);
            saveCartonDetails(cartons);
        } else {
            throw new IncorrectDataException("Require atleast one row of data");
        }
    }

    public String getPackageDetail(String barcode) {
        String endPoint = String.format(commonUtils.getUrl("packageService.searchPackage"), barcode);
        Response response = WhmRestCoreAutomationUtils.getRequestResponse(endPoint).asResponse();
        if (200 != response.statusCode()) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("No Package available",
                    "NA",
                    validationUtils.validateResponseStatusCode(response, 204));
        }
        return response.asString();
    }

    @Given("Update Package $values")
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
        Map<String, Integer> dockDoorAppointmentMap = (Map<String, Integer>) stepsContext.get(DOOR_APPOINTMENT_MAP);
        List<CartonDetails> cartonDetails = (List<CartonDetails>) stepsContext.get(Context.CARTON_DETAILS.name());
        Map<Integer, String> shipViaLaneMap = (Map<Integer, String>) stepsContext.get(SHIPVIA_LANE_MAP);
        JSONArray jsonArray = new JSONArray();

        int count = 0;
        for (CartonDetails cartonDetail : cartonDetails) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cartonDetail.getSequenceId());
            jsonObject.put("barcode", cartonDetail.getCartonId());
            jsonObject.put("status", updateParam.get("cartonStatus"));
            if (null != updateParam.get("apptNbr")) {
                jsonObject.put("apptNbr", dockDoorAppointmentMap.get(updateParam.get("apptNbr")));
            }
            if (null != updateParam.get("location")) {
                jsonObject.put("location", updateParam.get("location"));
            } else {
                // using Skuupc field as shipvia
                jsonObject.put("location", shipViaLaneMap.get(Integer.valueOf(cartonDetail.getSkuupc())));
            }
            if (null != updateParam.get("divertshipSeq") && Boolean.valueOf(updateParam.get("divertshipSeq"))) {
                jsonObject.put("divertshipSeq", ++count);
                jsonObject.put("divertshipTs", getUTCDatetimeAsString());
            }
            jsonArray.put(jsonObject);
        }
        WhmRestCoreAutomationUtils.putRequestResponse(commonUtils.getUrl(requestUrl), jsonArray.toString());
    }

    @Then("Validate door $doorNbr with package status $status on dock option $option")
    public void validatePackageStatus(String doorNbr, String status, String option) {
        if ("lastScan".equalsIgnoreCase(option)) {
            List<String> cartons = (List<String>) stepsContext.get(LAST_SCAN_CARTONS);
            cartons.forEach(carton -> {
                String response = getPackageDetail(carton);
                JsonPath jpath = new JsonPath(response);
                //CommonUtils.doJbehavereportConsolelogAndAssertion("Validated Package Status", status, status.equalsIgnoreCase(jpath.getString("content.status[0]")));
                Map<String, Integer> dockDoorAppointmentMap = (Map<String, Integer>) stepsContext.get(DOOR_APPOINTMENT_MAP);
                CommonUtils.doJbehavereportConsolelogAndAssertion("Validated Appointment number", jpath.getString("content.apptNbr[0]"),
                        String.valueOf(dockDoorAppointmentMap.get(doorNbr)).equalsIgnoreCase(jpath.getString("content.apptNbr[0]")));
            });
        } else if ("tie".equalsIgnoreCase(option)) {
            Map<String, Integer> doorShipViaMap = (Map<String, Integer>) stepsContext.get(DOOR_SHIPVIA_MAP);
            Integer shipVia = doorShipViaMap.get(doorNbr);

            List<CartonDetails> cartonDetails = (List<CartonDetails>) stepsContext.get(Context.CARTON_DETAILS.name());
            cartonDetails = cartonDetails.stream().filter(f -> f.getSkuupc().equals(String.valueOf(shipVia))).collect(Collectors.toList());

            cartonDetails.forEach(cartonDetail -> {
                String response = getPackageDetail(cartonDetail.getCartonId());
                JsonPath jpath = new JsonPath(response);
                CommonUtils.doJbehavereportConsolelogAndAssertion(String.format("Expected Package Status:%s and actual Status", status), jpath.getString("content.status[0]"), status.equalsIgnoreCase(jpath.getString("content.status[0]")));
                Map<String, Integer> dockDoorAppointmentMap = (Map<String, Integer>) stepsContext.get(DOOR_APPOINTMENT_MAP);
                CommonUtils.doJbehavereportConsolelogAndAssertion("Validated Appointment number", jpath.getString("content.apptNbr[0]"),
                        String.valueOf(dockDoorAppointmentMap.get(doorNbr)).equalsIgnoreCase(jpath.getString("content.apptNbr[0]")));

            });
        } else {

            Map<String, Integer> doorShipViaMap = (Map<String, Integer>) stepsContext.get(DOOR_SHIPVIA_MAP);
            Integer shipVia = doorShipViaMap.get(doorNbr);

            List<CartonDetails> cartonDetails = (List<CartonDetails>) stepsContext.get(Context.CARTON_DETAILS.name());
            cartonDetails = cartonDetails.stream().filter(f -> f.getSkuupc().equals(String.valueOf(shipVia))).collect(Collectors.toList());

            cartonDetails.forEach(carton -> {
                String response = getPackageDetail(carton.getCartonId());
                JsonPath jpath = new JsonPath(response);
                CommonUtils.doJbehavereportConsolelogAndAssertion(String.format("Expected Package Status:%s and actual Status", status), jpath.getString("content.status[0]"), status.equalsIgnoreCase(jpath.getString("content.status[0]")));
                CommonUtils.doJbehavereportConsolelogAndAssertion("Validated Appointment number", jpath.getString("content.apptNbr[0]"), StringUtils.isBlank(jpath.getString("content.apptNbr[0]")));
            });
        }

    }

    @When("scan door $doorNumber number")
    public void scanDoorNumber(String doorNumber) {
        dockScanPage.scanDoorNumber(doorNumber);
    }

    @Then("Validate Load Inquiry details for $details")
    public void validateLoadInquiryDetails(String details) {
        dockScanPage.validateLoadInquiryDetails(details, cartonsNumber);
    }

    @Then("Validate Dock Scan Screen")
    public void validateDoorScanScreen() {
        dockScanPage.validateDoorScanScreen();
    }

    @Then("Validate Dock Options Screen")
    public void validateDockOptionScreen() {
        dockScanPage.validateDockOptionScreen();
    }

    @When("User selects $option")
    public void selectDockOption(String option) throws InterruptedException {
        dockScanPage.selectOption(option);
    }

    @Then("Validate door $doorNbr with $option Dock option")
    public void validateSelectedDockOptionScreen(String doorNbr, String option) {
        Map<String, Integer> dockDoorAppointmentMap = (Map<String, Integer>) stepsContext.get(DOOR_APPOINTMENT_MAP);
        dockScanPage.validateSelectedDockOptionScreen(option, doorNbr,
                dockDoorAppointmentMap.get(doorNbr));
    }

    @When("Perform Last scan on carton $nbr, on that door $doorNbr")
    public void performLastScan(Integer nbr, String doorNbr) {
        log.info("performLastScan: option: {}, Door Nbr:{}", nbr, doorNbr);
        Map<String, Integer> doorShipViaMap = (Map<String, Integer>) stepsContext.get(DOOR_SHIPVIA_MAP);
        Integer shipVia = doorShipViaMap.get(doorNbr);

        List<CartonDetails> cartonDetails = (List<CartonDetails>) stepsContext.get(Context.CARTON_DETAILS.name());
        cartonDetails = cartonDetails.stream().filter(f -> f.getSkuupc().equals(String.valueOf(shipVia))).collect(Collectors.toList());

        String barcode = cartonDetails.get(nbr - 1).getCartonId();

        Integer totalLastScanCount = getTotalCountLastScanCartons(doorNbr, shipVia, barcode);
        log.info("Scan Carton:{}, totalLastScanCount:{}", barcode, totalLastScanCount);
        dockScanPage.scanCartonNumber(barcode);
        validateWarningPopup(String.format("Total %d cartons available to Tie. Do you want to proceed?", totalLastScanCount), "yes");
        validateInlineMessage(totalLastScanCount + " " + SUCCESS_APPT_ASSIGNED_MSG, SUCCESS_MSG_TYPE);
    }

    @When("scan carton number with $option option on that door $doorNbr")
    public void scanCartonNumber(String doorNbr, String option) {
        log.info("scanCartonNumber: option: {}, Door Nbr:{}", option, doorNbr);
        Map<String, Integer> doorShipViaMap = (Map<String, Integer>) stepsContext.get(DOOR_SHIPVIA_MAP);
        Integer shipVia = doorShipViaMap.get(doorNbr);

        List<CartonDetails> cartonDetails = (List<CartonDetails>) stepsContext.get(Context.CARTON_DETAILS.name());
        cartonDetails = cartonDetails.stream().filter(f -> f.getSkuupc().equals(String.valueOf(shipVia))).collect(Collectors.toList());

        if ("Invalid".equalsIgnoreCase(option)) {
            log.info("Carton nbr:{}, shipVia: {}", cartonDetails.get(0).getCartonId(), shipVia);
            dockScanPage.scanCartonNumber(cartonDetails.get(0).getCartonId());

        } else {
            cartonDetails.forEach(cartonDetail -> {
                dockScanPage.scanCartonNumber(cartonDetail.getCartonId());

                if ("tie".equalsIgnoreCase(option)) {
                    validateInlineMessage(SUCCESS_TIE_APPT_ASSIGNED_MSG, SUCCESS_MSG_TYPE);
                } else if ("retie".equalsIgnoreCase(option)) {
                    validateInlineMessage(ERROR_APPT_ALREADY_ASSIGNED_MSG, ERROR_MSG_TYPE);
                } else if ("untie".equalsIgnoreCase(option)) {
                    validateInlineMessage(SUCCESS_UNTIE_APPT_UNASSIGNED_MSG, SUCCESS_MSG_TYPE);
                } else if ("reUntie".equalsIgnoreCase(option)) {
                    validateInlineMessage(ERROR_NO_APPT_EXIT_MSG, ERROR_MSG_TYPE);
                }
            });

        }

    }

    @When("scan $cartonNumber carton number")
    public void scanCartonNumber(String cartonNumber) {
        log.info("Scan selected Carton:{}", cartonNumber);
        dockScanPage.scanCartonNumber(cartonNumber);
    }


    public Integer getTotalCountLastScanCartons(String doorNumber, Integer shipVia, String carton) {
        Map<Integer, String> shipViaLaneMap = (Map<Integer, String>) stepsContext.get(SHIPVIA_LANE_MAP);

        List<String> cartons = getLastScanCartons(carton, shipViaLaneMap.get(shipVia), shipVia, Integer.valueOf(getWarehouseLocNbr()));

        log.info("LastScan Cartons:{}", cartons);

        try {
            List<String> lastScanCartons = (List<String>) stepsContext.get(LAST_SCAN_CARTONS);
            lastScanCartons.clear();
            lastScanCartons.addAll(cartons);
        } catch (StepsContext.ObjectNotStoredException e) {
            stepsContext.put(LAST_SCAN_CARTONS, cartons, ToContext.RetentionLevel.SCENARIO);
        }

        return cartons.size();

    }

    @Then("Validate override appointment Message $expMessage and click $yesNo for door $doorNo")
    public void validateApptOverridePopup(String expMessage, String yesNo, String doorNo) {
        Map<String, Integer> dockDoorAppointmentMap = (Map<String, Integer>) stepsContext.get(DOOR_APPOINTMENT_MAP);
        Integer apptNo = dockDoorAppointmentMap.get(doorNo);
        expMessage = expMessage.replace("#appt", String.valueOf(apptNo));
        dockScanPage.validateWarningPopup(expMessage, yesNo);
    }

    @Then("Validate alert Message $expMessage and click $yesNo")
    public void validateWarningPopup(String expMessage, String yesNo) {
        dockScanPage.validateWarningPopup(expMessage, yesNo);
    }

    @Then("Validate Inline Message $msg and Message type $msgType")
    public void validateInlineMessage(String msg, String msgType) {
        dockScanPage.validateInlineMessage(msg, msgType);
    }

    private List<String> getLastScanCartons(String cntrNbr, String carrierLane, Integer shipVia, Integer locNbr) {

        StringBuilder query = new StringBuilder("SELECT barcode from package.package where ");
        if ((Boolean) stepsContext.get(LAST_SCAN_FILTER_TYPE)) {
            query.append("divertship_seq <= (select divertship_seq from package.package where barcode='").append(cntrNbr).append("')");
        } else {
            query.append("divertship_ts <= (select divertship_ts from package.package where barcode='").append(cntrNbr).append("')");
        }
        query.append(" and locn_nbr=").append(locNbr).append(" and location ='").append(carrierLane).append("' and ship_via=").append(shipVia);
        query.append(" and (appt_nbr = '' or appt_nbr is null)");

        try {
            return DBMethods.getDBValueInList(query.toString(), "package");
        } catch (Exception e) {
            log.error("unable to retrieve LastScanCartons data.", e);
        }
        return null;
    }

    @Then("scan door $doorNumber and select $option")
    @Composite(steps = {"When scan door $doorNumber number",
            "When User selects $option"})
    public void scanDoorAndSelectLastScan(String doorNumber, String option) {

    }

    @Given("$noOfCartons HAF CFC cartons in manifested status $examplesTable")
    public void createCFCCartonsInPackage(int noOfCartons, ExamplesTable examplesTable) throws InterruptedException {
        String randomSeq = randomUtil.getRandomValue("D-7");
        List<String> cartonsList = new ArrayList<>();
        if (noOfCartons > 0 && examplesTable != null && examplesTable.getRowCount() == 1) {
            for (int i = 0; i < noOfCartons; i++) {
                Map<String, String> row = examplesTable.getRows().get(0);
                String endPoint = commonUtils.getUrl(row.get("requestUrl"));
                endPoint = endPoint.replace("{topicName}", commonUtils.getEnvConfigValue(row.get("topicName")));
                String createPackageRequest = requestUtil.getRequestBody(row.get("requestParams"), row.get("templateName")).get(0);

                createPackageRequest = createPackageRequest.replace("#ShipNbr", randomUtil.getRandomValue("RTF-D-6")).replace("#wgt", randomUtil.getRandomValue("D-2")).replace("#len", randomUtil.getRandomValue("D-2"));
                createPackageRequest = createPackageRequest.replace("#wdt", randomUtil.getRandomValue("D-2")).replace("#hgt", randomUtil.getRandomValue("D-2")).replace("#disc", randomUtil.getRandomValue("D-1"));
                createPackageRequest = createPackageRequest.replace("#base", randomUtil.getRandomValue("D-1")).replace("#fuel", randomUtil.getRandomValue("D-2")).replace("#total", randomUtil.getRandomValue("D-3"));
                createPackageRequest = createPackageRequest.replace("#spcl", randomUtil.getRandomValue("D-1")).replace("#transit", randomUtil.getRandomValue("D-1")).replace("#divertshipSeq", randomSeq + "" + i);
                cartonsList.add(requestUtil.getRandomeValueMaps().get(0).get("#barcode"));
                Response response = WhmRestCoreAutomationUtils.postRequestResponse(endPoint, createPackageRequest).asResponse();
                CommonUtils.doJbehavereportConsolelogAndAssertion("publish carton event",
                        "pubsub Endpoint: " + endPoint + "\n"
                                + "pubsub Request: " + createPackageRequest + "\n"
                                + "pubsub Response Statuscode: " + response.getStatusCode(),
                        validationUtils.validateResponseStatusCode(response, 200));
            }
            dataStore.getStoredData().put("cfcCartonsList", cartonsList);
            log.info("List of cartons:{}", dataStore.getStoredData().get("cfcCartonsList"));
            TimeUnit.SECONDS.sleep(15);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Cartons are in manifested status in package domain", cartonsList.toString(), hafSteps.areContainersInExpectedStatus(cartonsList, "MFT"));
            saveCartonDetails(cartonsList);
        }
    }

    @Then("validate carton Ship via details")
    public void validateShipViaDetails() {
        List<String> cartonList = (List<String>) dataStorage.getStoredData().get("cartonList");
        for (String carton : cartonList) {
            String response = commonUtils.getPackageDetailByBarcode(carton);
            JsonPath jpath = new JsonPath(response);
            String shipVia = jpath.getString("[0].shipVia");
            log.info("Carton " + carton + " Ship via " + shipVia);
            String shipViaResponse = CommonUtils.getRequestResponse(String.format(commonUtils.getUrl("NetworkMap.networkMapShipViaService"), jpath.getString("[0].storeLocnNbr")));
            JsonPath shipViadetails = new JsonPath(shipViaResponse);
            String expectedShipVia = shipViadetails.getString("shipVia");
            assertEquals(shipVia, expectedShipVia, "ShipVia is incorrect");
        }
    }
    @Then("validate the Ship info $examplesTable")
    public void validateShipInfoDetails(ExamplesTable values) throws InterruptedException {
        TimeUnit.SECONDS.sleep(20);
        List<String> cartonList = (List<String>) dataStorage.getStoredData().get("cartonList");
        for (String carton : cartonList) {
            if (values.getRows().size() == 1) {
                Map<String, String> row = values.getRow(0);
                String GETCallEndpoint = commonUtils.getUrl(row.get("getRequestUrl"));
                String GETQueryParams = row.get("GETQueryParams");
                Map<String, String> processedGetQP = commonUtils.getParamsToMap(GETQueryParams);
                String strfromdate = randomUtil.getRandomValue(processedGetQP.get("fromDate"));
                String strTodate = randomUtil.getRandomValue(processedGetQP.get("toDate"));
                strfromdate = strfromdate.split("T")[0].concat(" 00:00:00");
                strTodate = strTodate.split("T")[0].concat(" 00:00:00");

                processedGetQP = commonUtils.getParamsToMap(GETQueryParams.replace("#carton", carton));
                processedGetQP.put("fromDate", strfromdate);
                processedGetQP.put("toDate", strTodate);

                Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                log.info(GETResponse.toString());
                String incomingPayload = GETResponse.jsonPath().get("MessageResponseDTO.messages[0].incomingPayload");
                log.info("incomingPayload: " + incomingPayload);
                JSONObject jsonObj = new JSONObject(incomingPayload);
                JSONObject payload = jsonObj.getJSONObject("payload");
                log.info("payload " + payload.toString());
                assertEquals(carton, payload.getString("lpn"));
                if (payload.getString("shipLabel").isEmpty()) {
                    Assert.fail("Ship label is empty");
                }
            }
        }
    }
}