package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Splitter;
import com.macys.mst.DC2.EndToEnd.configuration.*;
import com.macys.mst.DC2.EndToEnd.configuration.EndPoints.ConnectShipEndPoint;
import com.macys.mst.DC2.EndToEnd.constants.MHE_MessagingReverseJSON;
import com.macys.mst.DC2.EndToEnd.constants.MHEmessagingServiceURLS;
import com.macys.mst.DC2.EndToEnd.model.Attribute;
import com.macys.mst.DC2.EndToEnd.model.Carton_TrackingNbr;
import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.model.ShipInfoPoObject;
import com.macys.mst.DC2.EndToEnd.model.ShippingRequestDimensionObject;
import com.macys.mst.DC2.EndToEnd.model.ShippingRequestPayloadObject;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.artemis.gcp.pubsub.PubSubUtil;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.serviceobjects.LocationServiceObjects.LocationService;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import com.macys.mst.whm.coreautomation.pojos.EMSConnectionDetails;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import com.macys.mst.whm.coreautomation.utils.EMSMessageUtility;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.RequestUtil;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.ToContext;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

@Slf4j
@Data
public class ShipInfoSteps {

    private static final String SHIP_CONFIRM = "SHIPCONFIRM";
    public long TestNGThreadID = Thread.currentThread().getId();
    ArrayList<String> nonShuttleStores = new ArrayList<String>();
    ArrayList<String> shuttleStores = new ArrayList<String>();
    List<Carton_TrackingNbr> cartonTrackingList = new ArrayList<>();
    CommonUtils commonUtils = new CommonUtils();
    RequestUtil requestUtil = new RequestUtil();
    String FinalShipInfoJson;

    String payloadJson;

    String dimensionJson;

    String weightJson;
    ShipInfoPoObject shpinfoObj = new ShipInfoPoObject();
    ShippingRequestPayloadObject payloadObj = new ShippingRequestPayloadObject();
    ShippingRequestDimensionObject dimensionObj = new ShippingRequestDimensionObject();
    RandomUtil randomUtil = new RandomUtil();
    StepsContext stepsContext;
    WhmTestingService testingService = new WhmTestingService();
    ValidationUtil validationUtils = new ValidationUtil();

    public ShipInfoSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("JSON input with valid ShipInfo request entries")
    public void validShipInfoInputs(@Named("ShipinfoInputs") String ShipinfoInputs, @Named("payload") String payload,
                                    @Named("dimensions") String dimensions) throws JsonGenerationException, JsonMappingException, IOException {

        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
        shpinfoObj = mapper.convertValue(Splitter.on("&").withKeyValueSeparator(":").split(ShipinfoInputs),
                ShipInfoPoObject.class);

        final ObjectMapper mapper1 = new ObjectMapper(); // jackson's objectmapper
        payloadObj = mapper1.convertValue(Splitter.on("&").withKeyValueSeparator(":").split(payload),
                ShippingRequestPayloadObject.class);

        final ObjectMapper mapper2 = new ObjectMapper(); // jackson's objectmapper
        dimensionObj = mapper2.convertValue(Splitter.on("&").withKeyValueSeparator(":").split(dimensions),
                ShippingRequestDimensionObject.class);
        dimensionObj.setUOM("in");
        dimensionObj.setWeightUom("lb");

        payloadObj.setDimension(dimensionObj);

        shpinfoObj.setPayload(payloadObj);

        ObjectWriter ow1 = new ObjectMapper().writer().withDefaultPrettyPrinter();
        FinalShipInfoJson = ow1.writeValueAsString(shpinfoObj);

        log.info("Final Ship request JSON" + FinalShipInfoJson);

        StepDetail.addDetail("ShipInfoService Request JSON" + FinalShipInfoJson, true);
    }

    @When("Publish and read the message from PubSub for shipinfo service")
    public void pubSubPublish() throws Exception {
        String transcationId = "shipVia";
        String projectId = ConnectShipEndPoint.ProjectID;
        String inboundShipping = ConnectShipEndPoint.Subscriptioninboundshipping;
        String inboundDev = ConnectShipEndPoint.Subscriptionshippinginbounddev;
        String outboundId = ConnectShipEndPoint.SubscriptionOutboundId;
        PubSubUtil pubutil = new PubSubUtil();
        ArrayList<String> jsonList = new ArrayList<>();

        ObjectMapper maper = new ObjectMapper();
        String json = maper.writeValueAsString(shpinfoObj);
        log.info("ShipRequestJSON" + json);

        jsonList.add(json);

        log.info("Project and subscription details" + projectId + inboundShipping);

        pubutil.subscribeProject(projectId, inboundShipping);
        pubutil.publishMessage(projectId, inboundDev, jsonList);

        StepDetail.addDetail("Ship Request is published to inbound topic: " + jsonList, true);

        pubutil.subscribeProject(projectId, outboundId);

        log.info("transcationId" + transcationId);
        pubutil.readMessage(transcationId, 30);

    }

    @Then("SHIPINFO service invoked and validate entry in messaging database")
    public void shipinfoEntry() {
        String requestURL = MHEmessagingServiceURLS.messagingServiceGETAll_URL.replace("#messageType#", "SHIPINFO")
                .replace("3", "9211");
        log.info("Request url: " + requestURL);
        StepDetail.addDetail("Request url: " + requestURL, true);
        String response = RestUtilities.getRequestResponse(requestURL);
        log.info("Response:", response);
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONObject("MessageResponseDTO").getJSONArray("messages");
        boolean flag = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject responseJson = new JSONObject(jsonArray.get(i).toString());
            JSONObject msgPayloadJson = new JSONObject(responseJson.get("incomingPayload").toString());
            if (msgPayloadJson.getJSONObject("payload").getString("lpn")
                    .equalsIgnoreCase(shpinfoObj.getPayload().getPackageNumber())) {
                flag = true;
                log.info("SHIPINFO message is inserted into message DB for given package");
                log.info("SHIPINFO message:" + msgPayloadJson);
                log.info("SHIPINFO message found in message DB for given package: "
                        + msgPayloadJson.getJSONObject("payload").getString("lpn"));
                StepDetail.addDetail("SHIPINFO message:" + msgPayloadJson, true);
                StepDetail.addDetail("SHIPINFO message found in message DB for given package: "
                        + msgPayloadJson.getJSONObject("payload").getString("lpn"), true);
                break;
            }
        }

        Assert.assertTrue(flag);

    }

    @When("Carton is weighed, SCANWEIGH by pyramid, container dimensions are updated and SHIPREQUEST event is sent to shipping service")
    public void scanWeighSimulation() {
        Map<String, List<String>> cartonIdToteMap = (Map<String, List<String>>) stepsContext.get(Context.CARTON_TOTE_MAP.name());
        cartonIdToteMap.forEach((cartonId, toteIds) -> {
            String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String jsonResult = MHE_MessagingReverseJSON.SCAN_WEIGH_MESSAGE;
                jsonResult = jsonResult
                        .replace("#sequenceno#", seqId)
                        .replace("#barCode#", cartonId)
                        .replace("#weight#", "2.4");
                log.info("Scan Weigh message: {}", jsonResult);
                CommonUtils.pyramidResponseValidation(jsonResult);
            } else {
                String requestParams = "{#sequenceno:D-9,#carton:" + cartonId + ",#weight:D-2" + "}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "SCANWEIGH.json");
                log.info("SCANWEIGH input JSON payload: {}", messageBody);
                for (String eachMessageBody : messageBody) {
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "SCANWEIGH");
                }
            }
        });
        StepDetail.addDetail("Successfully sent Scan Weigh Message", true);
    }

    public void storeRouteclassification() throws Exception {
        String ConfigurationValueEndpoint = String.format(ConfigurationEndPoint.CONFIG_URL, "pofourwalls", "pofourwalls", "locnconfig");
        String ConfigurationResponse = CommonUtil.getRequestResponse(ConfigurationValueEndpoint);
        JSONArray ConfigValueArray = new JSONArray(ConfigurationResponse);
        String configValue = ConfigValueArray.getJSONObject(0).getString("configValue").replace("\\\"", "\"");
        JSONArray locnconfig = new JSONArray(configValue);

        /*String configurationValueSql = String.format(SQLConfiguration.ConfigurationValue, "7221", "pofourwalls", "pofourwalls", "locnconfig", "1");
        //String locnConfigValue = DBUtils.getDBValueInString("configuration",configurationValueSql);
        String locnConfigValue = DBMethods.getDBValueInString(configurationValueSql,"configuration");
        JSONArray locnconfig = new JSONArray(locnConfigValue);*/
        ArrayList<String> nonShuttleStores = new ArrayList<String>();
        ArrayList<String> shuttleStores = new ArrayList<String>();
        for (int k = 0; k < locnconfig.length(); k++)
            if (locnconfig.getJSONObject(k).getBoolean("routeFlag") == false)
                nonShuttleStores.add(locnconfig.getJSONObject(k).getString("locnNbr"));
            else
                shuttleStores.add(locnconfig.getJSONObject(k).getString("locnNbr"));
        setNonShuttleStores(nonShuttleStores);
        setShuttleStores(shuttleStores);
    }

    @SuppressWarnings("unchecked")
    @Then("SHIPINFO message is sent to PYramid and Cartons are diverted to Shipping area, shipping labels are printed")
    public void verifyShipInfoMessage() throws Exception {
        Carton_TrackingNbr carton_TrackingNbr = new Carton_TrackingNbr();
        TimeUnit.SECONDS.sleep(30);
        storeRouteclassification();
        Map<String, List<String>> cartonIdToteMap = (Map<String, List<String>>) stepsContext.get(Context.CARTON_TOTE_MAP.name());
        Map<String, String> cartonIdStoreLocationMap = (Map<String, String>) stepsContext.get(Context.CARTON_STORE_MAP.name());

        cartonIdToteMap.keySet().forEach(cartonId -> {
            String response = commonUtils.getContainerDetailsbyBarcode(cartonId);
            if (null != response) {
                JsonPath cartondetail = new JsonPath(response);

                if (CommonUtils.packageFlag) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as MFT for Carton: Carton ID", cartonId, "MFT".equals(cartondetail.getString("[0].status")));
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Store validated as " + cartonIdStoreLocationMap.get(cartonId) + " for Carton: Carton ID", cartonId, cartonIdStoreLocationMap.get(cartonId).equals(cartondetail.getString("[0].storeLocnNbr")));
                } else {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as MFT for Carton: Carton ID", cartonId, "MFT".equals(cartondetail.getString("container.containerStatusCode")));
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Store validated as " + cartonIdStoreLocationMap.get(cartonId) + " for Carton: Carton ID", cartonId, cartonIdStoreLocationMap.get(cartonId).equals(cartondetail.getString("container.attributeList.findAll {attributeList -> attributeList.key=='Store'}[0].values[0]")));
                }

                JSONObject jsonObject = CommonUtils.verifyPutToStoreMsgResponse("SCANWEIGH", "SHIPINFO", cartonId);
                JSONObject payload = new JSONObject(jsonObject.getString("incomingPayload")).getJSONObject("payload");
                assertEquals(payload.getString("lpn"), cartonId);
                carton_TrackingNbr.setCartonNbr(payload.getString("lpn"));
                carton_TrackingNbr.setTrackingNbr(payload.getString("trackingNumber"));
                cartonTrackingList.add(carton_TrackingNbr);
                log.info("Tracking Number : {}", payload.getString("trackingNumber"));
                log.info("shipVia : {}", payload.getString("shipVia"));
                log.info("shipToZipCode : {}", payload.getString("shipToZipCode"));
                log.info("hazMat : {}", payload.getString("hazMat"));
                log.info("shipLabel : {}", CommonUtils.decryptBase64(payload.getString("shipLabel")));
                validateMHEmessageFormat("SHIPINFO", cartonId);
            } else {
                log.info("Shipping: Unable to retrieve Carton: {}", cartonId);
                StepDetail.addDetail(String.format("Shipping: Unable to retrieve Carton: %s", cartonId), false);
                org.testng.Assert.assertTrue(false, "Shipping: Unable to retrieve Carton:" + cartonId);
            }
        });
        stepsContext.put(Context.cartonTrackingMap.name(), cartonTrackingList, ToContext.RetentionLevel.SCENARIO);
    }

    @Then("SHIPCONFIRM message is sent by Pyramid after store package is shipped")
    public void shipconfirmSimulation() throws Exception {
        LocationService locationService = new LocationService();

        locationService.stubCreateLocations("Divert", "7221", "AREA,ZONE,AISLE,LEVEL,POSITION", "1-1;0-0;10-10;1-1;5-5", "NONE", "Divert Type", "P", "H,in,30,0,20&L,in,60,0,20&V,cuin,60,0,20&W,in,10,0,20&WT,lb,60,0,20&MV,cuin,10,0,20", "BH04632");

        String shipDivertLocation = "101015";
        Map<String, List<String>> cartonIdToteMap = (Map<String, List<String>>) stepsContext.get(Context.CARTON_TOTE_MAP.name());

        final String finalShipDivertLocation = shipDivertLocation;
        cartonIdToteMap.keySet().forEach(cartonId -> {
            String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String jsonResult = MHE_MessagingReverseJSON.SHIP_CONFIRM_MESSAGE;
                jsonResult = jsonResult
                        .replace("#sequenceno#", seqId)
                        .replace("#barCode#", cartonId)
                        .replace("#shipLane#", finalShipDivertLocation);

                log.info("SHIPCONFIRM message: {}", jsonResult);
                CommonUtils.pyramidResponseValidation(jsonResult);
                StepDetail.addDetail("Successfully sent Ship Confirm Message", true);
                String trasName = String.format("%s:%s", seqId, SHIP_CONFIRM);
                CommonUtils.verifyPutToStoreMsgResponse(trasName, SHIP_CONFIRM, cartonId);
            } else {
                String requestParams = "{#sequenceno:D-9,#carton:" + cartonId + ",#carrierLane:" + finalShipDivertLocation + "}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "SHIPCONFIRM.json");
                log.info("SHIPCONFIRM input JSON payload: {}", messageBody);
                for (String eachMessageBody : messageBody) {
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "SHIPCONFIRM");
                    StepDetail.addDetail("Successfully sent Ship Confirm Message", true);
                    JSONObject json = new JSONObject(eachMessageBody);
                    seqId = json.getJSONObject("payload").getString("sequenceNo");
                    String trasName = SHIP_CONFIRM;
                    CommonUtils.verifyPutToStoreMsgResponse(trasName, SHIP_CONFIRM, cartonId);
                }
            }

            CommonUtils.waitSec(5);

            String response = commonUtils.getContainerDetailsbyBarcode(cartonId);
            if (null != response) {
                JsonPath cartondetail = new JsonPath(response);
                if (CommonUtils.packageFlag) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as SHR for Carton ID", cartonId, "SHR".equals(cartondetail.getString("[0].status")));
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh ShipDivertLocation validated as " + finalShipDivertLocation + " for Carton ID", cartonId, finalShipDivertLocation.equals(cartondetail.getString("[0].location")));
                } else {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as SHR for Carton ID", cartonId, "SHR".equals(cartondetail.getString("container.containerStatusCode")));
                    JSONArray containerRelation = new JSONObject(response).getJSONObject("container").getJSONArray("containerRelationshipList");
                    for (int i = 0; i < containerRelation.length(); i++) {
                        assertEquals(String.valueOf(containerRelation.getJSONObject(i).get("parentContainer")), finalShipDivertLocation);
                        assertEquals(String.valueOf(containerRelation.getJSONObject(i).get("childContainer")), cartonId);
                        assertEquals(String.valueOf(containerRelation.getJSONObject(i).get("childContainerType")), "CRT");
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh ShipDivertLocation validated as " + finalShipDivertLocation + " for Carton ID", cartonId, true);
                    }
                }
            } else {
                CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Unable to get details for Carton ID", cartonId, false);
            }
        });
    }

    @Then("Cartons are moved to Shipped status after publishing LOGD via $method")
    public void publishingLogDAndValidatingCartonStatus(String method) throws Exception {
        TimeUnit.SECONDS.sleep(10);
        String trackNbr;
        List<Carton_TrackingNbr> cartonTrackinglist = (List<Carton_TrackingNbr>) stepsContext.get(Context.cartonTrackingMap.name());
        commonUtils.doJbehavereportConsolelogAndAssertion("cartonTrackinglist", cartonTrackinglist.toString(), true);

        for (Carton_TrackingNbr ct : cartonTrackinglist) {
            trackNbr = ct.getTrackingNbr();
            if (StringUtils.isBlank(trackNbr)) {
                trackNbr = randomUtil.getRandomValue("1S-D-18");
            }

            String cartonNumber = ct.getCartonNbr();

            if ("DC20_PUBSUB".equalsIgnoreCase(method)) {
                publishLogDToDC2OPubSub(cartonNumber, trackNbr);
            } else if ("ENROUTE".equalsIgnoreCase(method)) {
                publishLogDToEnroute(cartonNumber, trackNbr);
            } else if ("MONARCH".equalsIgnoreCase(method)) {
                publishLogDToMonarch(cartonNumber, trackNbr);
            } else {
                Assert.fail("Can't publish the LogD to provided method : " + method);
            }

            validateCartonDetailsAfterLogD(cartonNumber);
        }
    }

    private void publishLogDToDC2OPubSub(String cartonNumber, String trackNbr) {
        String reqParam = "{#CartonNbr:" + cartonNumber + ",#TrackingNbr:" + trackNbr + "}";
        List<String> filledRequest = requestUtil.getRequestBody(reqParam, "LOGD.json");
        StepDetail.addDetail(String.format("The request posted is: " + filledRequest), true);
        String topic = CommonUtils.packageFlag ? commonUtils.getEnvConfigValue("pubSub.topics.packageLogD") : commonUtils.getEnvConfigValue("pubSub.topics.LogD");

        List<ApiResponse> responses = testingService.publishGivenPayloadsToGivenTopic(topic, filledRequest);
        for (ApiResponse ApiResponse : responses) {
            validationUtils.validateResponseStatusCode(ApiResponse.asResponse(), 200);
        }
    }

    private void publishLogDToEnroute(String cartonNumber, String trackNbr) {

        EMSConnectionDetails emsConnectionDetails = new EMSConnectionDetails(
                commonUtils.getEnvConfigValue("ems.enroute.server"),
                commonUtils.getEnvConfigValue("ems.enroute.user"),
                commonUtils.getEnvConfigValue("ems.enroute.user"),
                'T', commonUtils.getEnvConfigValue("ems.enroute.topic")
        );


        String ENROUTE_MSG_BODY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                "<CarrierDetails xmlns:ns0=\"http://www.mst.macys.com/lgs/rcv/schemas/CommonCarrierSvc/CommonCarrier_v1.0.0.0\">\r\n" +
                "   <DelivCarrier>UPSN</DelivCarrier>\r\n" +
                "   <DelivCarrierType>3RD PARTY</DelivCarrierType>\r\n" +
                "   <ShpDT>#shipDate</ShpDT>\r\n" +
                "   <OriginDT>#originDate</OriginDT>\r\n" +
                "</CarrierDetails>".replace("#shipDate", randomUtil.getRandomValue("CAL"))
                        .replace("#originDate", randomUtil.getRandomValue("CAL"));

        Map<String, String> messageProperties = new HashMap<>();
        messageProperties.put("AppId", "ERA");
        messageProperties.put("Carton_nbr", cartonNumber);
        messageProperties.put("EventType", "CartonDetail");
        messageProperties.put("EventTypeID", "LOGD");
        messageProperties.put("EventVersion", "1.0.0.0");
        messageProperties.put("Third_Party_Tracking_Lbl", trackNbr);
        messageProperties.put("Target", "JC");
        messageProperties.put("FrmDivision", "77");
        messageProperties.put("ToDivision", "77");
        messageProperties.put("Type_Code", "827");

        try {
            EMSMessageUtility.sendMessageToDestination(emsConnectionDetails, messageProperties, ENROUTE_MSG_BODY);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void publishLogDToMonarch(String cartonNumber, String trackNbr) {
        EMSConnectionDetails emsConnectionDetails = new EMSConnectionDetails(
                commonUtils.getEnvConfigValue("ems.monarch.server"),
                commonUtils.getEnvConfigValue("ems.monarch.user"),
                commonUtils.getEnvConfigValue("ems.monarch.user"),
                'T', commonUtils.getEnvConfigValue("ems.monarch.topic")
        );

        String MONARCH_MSG_BODY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                "<MerchandiseLineItems xmlns:ns0=\"http://www.mst.macys.com/d2c/trs/schemas/MerchandiseEventSvc/MerchandiseLineItems_v1.0.0.0\">\r\n" +
                "    <ReferenceSeqNbr>1</ReferenceSeqNbr>\r\n" +
                "    <ScannedDt>2020-06-07</ScannedDt>\r\n" +
                "    <SchedArrvDt>2020-06-08</SchedArrvDt>\r\n" +
                "    <FrmDivLocNbr>6315</FrmDivLocNbr>\r\n" +
                "    <FrmLocNbr>7221</FrmLocNbr>\r\n" +
                "    <ToDivLocNbr>6315</ToDivLocNbr>\r\n" +
                "    <ToLocNbr>6778</ToLocNbr>\r\n" +
                "    <RSNCd>37</RSNCd>\r\n" +
                "    <VndNbr>921</VndNbr>\r\n" +
                "    <GMMId>1</GMMId>\r\n" +
                "    <DivManID>16</DivManID>\r\n" +
                "    <GMMDesc>CENTER CORE</GMMDesc>\r\n" +
                "    <DivManDesc>WOMENS SHOES</DivManDesc>\r\n" +
                "    <MkstNbr>85441</MkstNbr>\r\n" +
                "    <Color>0</Color>\r\n" +
                "    <Size>375</Size>\r\n" +
                "    <UccID>492609</UccID>\r\n" +
                "    <PID>BRIANA-85441</PID>\r\n" +
                "    <PIDDesc>BRIANA BLACK FLAT SANDAL</PIDDesc>\r\n" +
                "    <FrmZlClassNbr>23</FrmZlClassNbr>\r\n" +
                "    <FrmOwnRTLAmt>14.99</FrmOwnRTLAmt>\r\n" +
                "    <ToOwnRTLAmt>14.99</ToOwnRTLAmt>\r\n" +
                "    <UpcQty>2</UpcQty>\r\n" +
                "    <UserId>TRS_MERCHANDISE_EVENT</UserId>\r\n" +
                "</MerchandiseLineItems>\r\n".replace("#scanDate", randomUtil.getRandomValue("DATE"))
                        .replace("#schedArrvDt", randomUtil.getRandomValue("DATE+3"));

        Map<String, String> messageProperties = new HashMap<>();
        messageProperties.put("AppId", "ERA");
        messageProperties.put("Carton_nbr", cartonNumber);
        messageProperties.put("EventTypeID", "LOGD");
        messageProperties.put("Third_Party_Tracking_Lbl", trackNbr);
        messageProperties.put("FrmDivision", "77");
        messageProperties.put("ToDivision", "77");

        try {
            EMSMessageUtility.sendMessageToDestination(emsConnectionDetails, messageProperties, MONARCH_MSG_BODY);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void validateCartonDetailsAfterLogD(String cartonNumber) throws InterruptedException {
        String response = commonUtils.getContainerDetailsbyBarcode(cartonNumber);
        if (null != response) {
            JsonPath cartondetail = new JsonPath(response);
            if (CommonUtils.packageFlag) {
                if (!("SHP".equals(cartondetail.getString("[0].status")))) {
                    TimeUnit.SECONDS.sleep(50);
                    response = commonUtils.getContainerDetailsbyBarcode(cartonNumber);
                    cartondetail = new JsonPath(response);
                }
                CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: LogD validated as SHP for Carton: Carton ID", cartonNumber, "SHP".equals(cartondetail.getString("[0].status")));
            } else {
                if (!("SHP".equals(cartondetail.getString("container.containerStatusCode")))) {
                    TimeUnit.SECONDS.sleep(50);
                    response = commonUtils.getContainerDetailsbyBarcode(cartonNumber);
                    cartondetail = new JsonPath(response);
                }
                CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: LogD validated as SHP for Carton: Carton ID", cartonNumber, "SHP".equals(cartondetail.getString("container.containerStatusCode")));
            }
        } else {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Unable to get details for Carton ID", cartonNumber, false);
        }
    }

    private void validateMHEmessageFormat(String destId, String filter) {
        int size = 100;
        MHESteps mheSteps = new MHESteps(stepsContext);
        List<Map<String, String>> valueFromDB = mheSteps.fetchMHEDetailsFromDB(destId, filter, "", "", size);
        String outgoingPayload = valueFromDB.get(0).get("WCS Payload");
        if (!ExpectedDataProperties.pyramidJsonproperty) {
            String[] values = outgoingPayload.split("\\|");
            if (!(values.length == 10)) {
                Assert.assertTrue("Message length not as expected", false);
            }
            if (!(values[1].equalsIgnoreCase(destId))) {
                Assert.assertTrue("Message contain :" + values[1] + " but Expected is: " + destId, false);
            }
            if (!(values[2].equalsIgnoreCase(filter))) {
                Assert.assertTrue("Message contain :" + values[2] + " but Expected is: " + filter, false);
            }
            if (StringUtils.isEmpty(values[5]) && !(Integer.parseInt(values[5]) > 0)) {
                Assert.assertTrue("Message doesnot contain ZipCode", false);
            }
            if (StringUtils.isEmpty(values[9])) {
                Assert.assertTrue("Message doesnot contain shiplabel", false);
            }
            if (validateShipViaAndTrackingNbr(filter, values[4], values[6], values[3])) {
                log.info(destId + " outgoing payload message is as expected");
            }
        } else {
            JsonPath shipInfo = new JsonPath(valueFromDB.get(0).get("WCSInPayload"));
            log.info("WCSInPayload:{}", shipInfo);
            if (!(destId.equalsIgnoreCase(shipInfo.getString("messageType")))) {
                Assert.assertTrue("Message contain :" + shipInfo.getString("messageType") + " but Expected is: " + destId, false);
            }
            if (!(filter.equalsIgnoreCase(shipInfo.getString("payload.lpn")))) {
                Assert.assertTrue("Message contain :" + shipInfo.getString("payload.lpn") + " but Expected is: " + filter, false);
            }
            if (StringUtils.isEmpty(shipInfo.getString("payload.shipToZipCode")) && !(Integer.parseInt(shipInfo.getString("payload.shipToZipCode")) > 0)) {
                Assert.assertTrue("Message doesnot contain ZipCode", false);
            }
            if (StringUtils.isEmpty(shipInfo.getString("payload.shipLabel"))) {
                Assert.assertTrue("Message doesnot contain shiplabel", false);
            }
            if (validateShipViaAndTrackingNbr(filter, shipInfo.getString("payload.shipVia"), shipInfo.getString("payload.hazMat"), shipInfo.getString("payload.trackingNumber"))) {
                log.info(destId + " outgoing payload message is as expected");
            }

        }
    }

    public Boolean validateShipViaAndTrackingNbr(String cartonID, String actualShipVia, String actualHazmat, String trackingNbr) {
        Boolean flag = false;
        String storeNbr = "";
        Boolean hazmat = false;
        String expectedShipVia = "";

        if (CommonUtils.packageFlag) {
            String response = commonUtils.getContainerDetailsbyBarcode(cartonID);
            if (StringUtils.isNotBlank(response)) {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                storeNbr = String.valueOf(jsonObject.getInt("storeLocnNbr"));
                String haz = jsonObject.getString("hazmatIndicator");
                hazmat = Boolean.getBoolean(haz == "Y" ? "true" : "false");
            } else {
                Assert.fail("Carton details not found in package domain for : " + cartonID);
            }
        } else {
            InventoryContainer inventoryContainer = CommonUtils.getInventory(cartonID);
            List<Attribute> attribute = inventoryContainer.getContainer().getAttributeList();
            for (Attribute attr : attribute) {
                if ("Store".equalsIgnoreCase(attr.getKey())) {
                    storeNbr = attr.getValues().get(0);
                }
                if ("Instruction".equalsIgnoreCase(attr.getKey())) {
                    if (attr.getValues().get(0).equalsIgnoreCase("HAZ"))
                        hazmat = true;
                }
            }
        }
        String response = CommonUtils.getRequestResponse(String.format(PO4WallEndPoint.PO4WALL_GET_LOCATION, storeNbr));
        Boolean routeFlag = null;
        if (StringUtils.isNotEmpty(response)) {
            JSONObject jsonObject = new JSONObject(response);
            routeFlag = (Boolean) jsonObject.getJSONObject("LocationDto").get("routeAsNewStore");
        }
        String endpointFlag = commonUtils.getUrl("NetworkMap.networkMapFlag");
        String networkMapFlagResponse = CommonUtils.getRequestResponse(endpointFlag);
        JsonPath networkMapFlag = new JsonPath(networkMapFlagResponse);
        String networkMapFlagval = networkMapFlag.get("configValue").toString().split(":")[1];
        networkMapFlagval = networkMapFlagval.substring(0, networkMapFlagval.length() - 3);
        log.info("NetworkMapFlag " + ":- " + networkMapFlagval);
        if (networkMapFlagval.equalsIgnoreCase("true") && !actualShipVia.equals("UGC") && !actualShipVia.equals("UGC/U3") && !actualShipVia.equals("SHL")) {
            String shipViaResponse = CommonUtils.getRequestResponse(String.format(commonUtils.getUrl("NetworkMap.networkMapShipViaService"), storeNbr));
            JsonPath shipViadetails = new JsonPath(shipViaResponse);
            expectedShipVia = shipViadetails.getString("shipVia");
            log.info("The ShipVia is :- " + expectedShipVia);
        } else {
            if (routeFlag) {
                expectedShipVia = "SHL";
            } else {
                if (hazmat) {
                    expectedShipVia = "UGC";
                } else {
                    expectedShipVia = "UGC/U3";
                }
            }
        }
        if (!(expectedShipVia.contains(actualShipVia))) {
            Assert.assertTrue("Message contain :" + actualShipVia + " but Expected is: " + expectedShipVia, false);
        }
        if (actualHazmat.equalsIgnoreCase("N")) {
            actualHazmat = "false";
        }
        flag = hazmat.toString().equalsIgnoreCase(actualHazmat);
        if (!flag) {
            Assert.assertTrue("Message contain :" + hazmat + " but Expected is: " + actualHazmat, false);
        }
        if (routeFlag) {
            if (!StringUtils.isEmpty(trackingNbr)) {
                Assert.assertTrue("Message contains Tracking Number", false);
            }
        } else {
            if (StringUtils.isEmpty(trackingNbr)) {
                Assert.assertTrue("Message does not have Tracking Number", true);
            }
        }
        return flag;
    }

}