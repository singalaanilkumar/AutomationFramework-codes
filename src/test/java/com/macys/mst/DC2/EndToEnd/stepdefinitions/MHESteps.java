package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.configuration.MessagingEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLMessage;
import com.macys.mst.DC2.EndToEnd.model.MessageService;
import com.macys.mst.DC2.EndToEnd.model.Messages;
import com.macys.mst.DC2.EndToEnd.pageobjects.MHEPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.RequestUtil;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class MHESteps {
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
    SeUiContextBase seUiContextBase = new SeUiContextBase();
    POInquiryUISteps poInquirySteps = new POInquiryUISteps(stepsContext);
    private MHEPage mhePage = new MHEPage(driver);
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private CommonUtils commonUtils = new CommonUtils();
    private RequestUtil requestUtil = new RequestUtil();
    String searchMHESql = "";

    public MHESteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String,String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }



    public List<Map<String,String>> fetchMheServiceDetails(String msgtype,String filter){
        List<Map<String,String>> mheDetails = new ArrayList<>();
        log.info("fetchMheServiceDetails inside ::");
        String query = ReadHostConfiguration.GET_MESSAGE_SERVICE_URL.value()+filter+"&MSG_TYP="+msgtype;
        String response = RestUtilities.getRequestResponse(query);
        log.info("fetchMheServiceDetails  ::"+ response);
        response = response.replaceAll("MessageResponseDTO", "messageResponseDTO");
        MessageService messageService = CommonUtils.getClientResponse(response, new TypeReference<MessageService>() {});
        List<Messages> messages = messageService.getMessageResponseDTO().getMessages();
        messages.forEach(msg -> {
            Map<String,String> mheDetailService = new HashMap<>();
            if(null != msg){
                mheDetailService.put("Sequence No#", msg.getSequenceNumber());
                mheDetailService.put("Transaction Name", msg.getTransactionName());
                mheDetailService.put("Message Type", msg.getDestinationId());
                //mheDetailService.put("Status", msg.getTransmissionStatus().getStatus());
                mheDetailService.put("WHM Payload", msg.getIncomingPayload());
                mheDetailService.put("WCS Payload", msg.getOutgoingPayload());
                mheDetailService.put("Client", msg.getClientId());
                mheDetailService.put("Retry Attempts", msg.getRetryAttempts().toString());
                if(null != msg.getCreatedTime() && msg.getCreatedTime().contains("UTC")){
                    String time = msg.getCreatedTime().replace(" UTC", ".000");
                    mheDetailService.put("Created On", time);
                }else{
                    mheDetailService.put("Created On", msg.getCreatedTime());
                }
            }
            mheDetails.add(mheDetailService);
        });
        log.info("MHE details from service:"+mheDetails);
        StepDetail.addDetail("MHE details from service:"+mheDetails, true);

        return mheDetails;
    }

    public List<Map<String,String>> fetchMHEDetailsFromDB(String msgtype,String filter,String fromDate,String toDate, int size) {
        List<Map<String,String>> mheDetails = new ArrayList<>();
        //String query = "";
        String messageGetCallAPI="";
        CommonUtils dateCalculateObject = new CommonUtils();
        if(fromDate.isEmpty()&& toDate.isEmpty()){
            //query = MessageFormat.format(SQLMessage.GET_MESSAGE_DETAILS, msgtype,filter);
            messageGetCallAPI = String.format(MessagingEndPoint.MessagingGetCall,"7221",msgtype,filter,"","",size);
        }else{
            fromDate = dateCalculateObject.getCurrentDateTime(-5);
            toDate = dateCalculateObject.getCurrentDateTime(4);
            seUiContextBase.waitFor(10);
            //query = MessageFormat.format(SQLMessage.GET_MESSAGE_DETAILS_WITH_DATA_RANGE, msgtype,filter,fromDate,toDate);
            messageGetCallAPI = String.format(MessagingEndPoint.MessagingGetCall,"7221",msgtype,filter,fromDate,toDate,size);
        }
        log.info("getMHEDetails endpoint: {}", messageGetCallAPI);
        List<Map<String,String>> outgoingPayloadMapList = new LinkedList<Map<String,String>>();
        String messageServiceGetResponse = CommonUtil.getRequestResponse(messageGetCallAPI);
        if(!StringUtils.isBlank(messageServiceGetResponse)){
            JSONObject parentJson = new JSONObject(messageServiceGetResponse);
            JSONObject messageDTOObject = parentJson.getJSONObject("MessageResponseDTO");
            JSONArray messagesArray = messageDTOObject.getJSONArray("messages");
            for(int i=0;i<messagesArray.length();i++){
                Map<String,String> WCSPayload = new LinkedHashMap<String,String>();
                WCSPayload.put("WCS Payload",messagesArray.getJSONObject(i).getString("outgoingPayload"));
                WCSPayload.put("WCSInPayload",messagesArray.getJSONObject(i).getString("incomingPayload"));

                outgoingPayloadMapList.add(WCSPayload);
            }
        }
		/*List<Map<Object, Object>> dbResults = null;
		try {
			dbResults = DBMethods.getValuesFromDBAsStringList(query,"messaging");
		} catch (Exception e) {
			log.error("Error in fetchMHEDetailsFromDB"+e);
		}
        log.info("GetPOReceiptDetails DB Results: {}", dbResults);
        dbResults.forEach(msg -> {
			Map<String,String> mheDetailService = new HashMap<>();
			if(null != msg){
				mheDetailService.put("Sequence No#", msg.get("sequence_number").toString());
				mheDetailService.put("Transaction Name", msg.get("transaction_name").toString());
				mheDetailService.put("Message Type", msg.get("destination_id").toString());
				mheDetailService.put("Status", msg.get("STATUS").toString());
				mheDetailService.put("WHM Payload", msg.get("incoming_payload").toString());
				mheDetailService.put("WCS Payload", msg.get("outgoing_payload").toString());
				mheDetailService.put("Client", msg.get("client_id").toString());
				mheDetailService.put("Retry Attempts", msg.get("retry_attempts").toString());
				mheDetailService.put("Created On", msg.get("created_time").toString().replace(".0", ".000"));
			}
			mheDetails.add(mheDetailService);
		});
        log.info("MHE details from DB:"+mheDetails);
		StepDetail.addDetail("MHE details from DB:"+mheDetails, true);*/
        return outgoingPayloadMapList;
    }

    @Then("User navigates to MHE and validates STOREALLOC messages for $type $waveCount Wave$values")
    public void thenUservalidatesMHEmessagesforWave(String waveCount, ExamplesTable values) throws InterruptedException {
        Map<String, String> processedGetQP = new HashMap<>();
        String poNbr = null;
        String waveNumber = null;
        if (waveCount.contains("PO")) {
            poNbr = (String) stepsContext.get(Context.PO_NBR.name());
            if (values.getRows().size() == 1) {
                Map<String, String> row = values.getRow(0);
                String GETQueryParams = row.get("GETQueryParams");
                if (GETQueryParams.contains("#PONumber"))
                    GETQueryParams = GETQueryParams.replace("#PONumber", poNbr);
                processedGetQP = requestUtil.getRandomParamsfromMap(GETQueryParams);
            }
        } else {
            waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));
            if (values.getRows().size() == 1) {
                Map<String, String> row = values.getRow(0);
                String GETQueryParams = row.get("GETQueryParams");
                if (GETQueryParams.contains("#waveNumber"))
                    GETQueryParams = GETQueryParams.replace("#waveNumber", waveNumber);
                processedGetQP = requestUtil.getRandomParamsfromMap(GETQueryParams);
            }
        }
        seUiContextBase.waitFor(5);
        mhePage.clickNavOption("MHE");
        mhePage.selectMHESearch();
//      mhePage.clickSCMmenu();
        mhePage.selectMessageType(processedGetQP.get("#messageType"));
        mhePage.selectTRXDateRange(processedGetQP.get("#startTRXRangeDt"), processedGetQP.get("#endTRXRangeDt"));
        mhePage.selectTextFilter(processedGetQP.get("#textFilter"));
        mhePage.clickSearchButton();
        seUiContextBase.waitFor(15);
        List<Map<String, String>> mheMessagesUI = mhePage.getGridElementsMapAllPages();
        log.info("mheScreenvalues = {}", mheMessagesUI.toString());
        Map<String, Map<String, String>> mheAttributesUIMap = mheMessagesUI.stream().collect(Collectors.toMap(map -> map.get("Sequence No#"), map -> map));
        log.info("mheAttributesUIMap: {}", mheAttributesUIMap);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            String startDate = dateFormat.format(dateFormat.parse(processedGetQP.get("#startTRXRangeDt")));
            String endDate = dateFormat.format(dateFormat.parse(processedGetQP.get("#endTRXRangeDt")));
            List<Map<String, String>> mheFromDB = fetchMHEDetailsFromDBquery(processedGetQP.get("#messageType"), processedGetQP.get("#textFilter"), startDate, endDate);
            log.info("mheDBvalues = {}", mheFromDB.toString());

            Map<String, Map<String, String>> mheAttritbuesFromDBMap = mheFromDB.stream().collect(Collectors.toMap(map -> map.get("Sequence No#"), map -> map));
            log.info("mheFromDBMap: {}", mheAttritbuesFromDBMap);
            if(mheAttributesUIMap.containsKey("")){
                mheAttributesUIMap.remove("");
            }
            CommonUtils.doJbehavereportConsolelogAndAssertion("MHE Screen Count validated",
                    "MHE UI Sequence IDs: " + mheAttributesUIMap.keySet() + " MHE DB Sequence IDs: " + mheAttritbuesFromDBMap.keySet(),
                    mheAttributesUIMap.keySet().equals(mheAttritbuesFromDBMap.keySet()));

            for (String sequenceID_DB : mheAttritbuesFromDBMap.keySet()) {
                mheAttritbuesFromDBMap.get(sequenceID_DB).remove("Created On");
                mheAttributesUIMap.get(sequenceID_DB).remove("Created On");
                mheAttributesUIMap.get(sequenceID_DB).remove("Retry Attempts");
                mheAttritbuesFromDBMap.get(sequenceID_DB).remove("Retry Attempts");
                mheAttributesUIMap.get(sequenceID_DB).remove("Route");
                mheAttritbuesFromDBMap.get(sequenceID_DB).remove("Route");
                CommonUtils.doJbehavereportConsolelogAndAssertion("StoreAlloc Details for ID " + sequenceID_DB,
                        " DB Details: " + mheAttritbuesFromDBMap.get(sequenceID_DB) + " UI Details: " + mheAttributesUIMap.get(sequenceID_DB),
                        mheAttritbuesFromDBMap.get(sequenceID_DB).equals(mheAttributesUIMap.get(sequenceID_DB)));
            }
            String GETCallEndpoint = commonUtils.getUrl("Messaging.getMsgURL");
            Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
            log.info(waveCount + "StoreAllocMsgResponse for WaveNumber " + waveNumber + ": " + GETResponse.asString());
            List<String> mheMsgList = new ArrayList<>();

            if (200 == GETResponse.statusCode()) {
                mheMsgList = getMHEMessagesfromResponse(GETResponse.asString(), waveNumber);
            }
            dataStorage.getStoredData().put(waveCount + "storeAllocMsgList", mheMsgList);
            log.info(waveCount + "storeAllocMsgList: " + mheMsgList);
        } catch (ParseException e) {
            log.info("Unable to validate mhe messages. " + e.getStackTrace());
        }
    }

    private List<Map<String, String>> fetchMHEDetailsFromDBquery(String msgtype, String filter, String
            fromDate, String toDate) {
        List<Map<String, String>> mheDetails = new ArrayList<>();
        String query = "";
        if (fromDate.isEmpty() && toDate.isEmpty()) {
            query = MessageFormat.format(SQLMessage.GET_MESSAGE_DETAILS, msgtype, filter);

        } else {
            StringBuilder fromDateTime = new StringBuilder();
            StringBuilder toDateTime = new StringBuilder();
            fromDateTime.append(fromDate);
            fromDateTime.append(" 00:00:00");
            log.info(fromDateTime.toString());
            toDateTime.append(toDate);
            toDateTime.append(" 23:59:59");
            log.info(toDateTime.toString());
            seUiContextBase.waitFor(10);
            query = MessageFormat.format(SQLMessage.GET_MESSAGE_DETAILS_WITH_DATA_RANGE_MHE, msgtype, filter, fromDateTime.toString(), toDateTime.toString());

        }
        log.info("getMHEDetails query: {}", query);

        List<Map<Object, Object>> dbResults = null;
        try {
            dbResults = DBMethods.getValuesFromDBAsStringList(query, "messaging");
        } catch (Exception e) {
            log.error("Error in fetchMHEDetailsFromDB" + e);
        }
        log.info("GetMheDetails DB Results: {}", dbResults);
        dbResults.forEach(msg -> {
            Map<String, String> mheDetailService = new HashMap<>();
            if (null != msg) {
                mheDetailService.put("Sequence No#", msg.get("sequence_number").toString());
                mheDetailService.put("Transaction Name", msg.get("transaction_name").toString());
                mheDetailService.put("Message Type", msg.get("destination_id").toString());
                mheDetailService.put("Status", msg.get("transmission_status").toString());
                mheDetailService.put("WHM Payload", msg.get("incoming_payload").toString());
                mheDetailService.put("WCS Payload", msg.get("outgoing_payload").toString());
                mheDetailService.put("Client", msg.get("client_id").toString());
                mheDetailService.put("Route", (msg.get("route_id")) != null ? String.valueOf(msg.get("route_id")) : "");
                mheDetailService.put("Retry Attempts", msg.get("retry_attempts").toString());
                mheDetailService.put("Created On", msg.get("created_time").toString().replace(".0", ".000"));
            }
            mheDetails.add(mheDetailService);
        });
        log.info("MHE details from DB:" + mheDetails);
        StepDetail.addDetail("MHE details from DB:" + mheDetails, true);

        return mheDetails;
    }

    @Then("message is updated using Edit and Reprocessing is done")
    public void thenMessageEditAndReprocessed() {
        String selectedSequenceId = mhePage.updateSelectedMessagetoFailed();
        mhePage.clickClearButton();
        mhePage.selectSequenceNum(selectedSequenceId);
        mhePage.clickSearchButton();
        mhePage.selectSequenceandVerifyDetails();
        mhePage.editAndSaveforPayload();
        mhePage.clickSCMmenu();
    }

    private List<String> getMHEMessagesfromResponse(String response, String waveNumber) {

        List<String> mheMsgList = new ArrayList<String>();
        try {
            JSONObject parentJson = new JSONObject(response);
            JSONObject messageDTOObject = parentJson.getJSONObject("MessageResponseDTO");
            JSONArray messagesArray = messageDTOObject.getJSONArray("messages");
            for (int i = 0; i < messagesArray.length(); i++) {
                String mheMsg = messagesArray.getJSONObject(i).getString("outgoingPayload");
                if (mheMsg != null && mheMsg.contains("|W|" + waveNumber + "|"))
                    mheMsgList.add(mheMsg);
            }
        } catch (Exception e) {
            log.info("Unable to extract mhe messages from response. " + e.getStackTrace());
        }

        return mheMsgList;
    }




}
