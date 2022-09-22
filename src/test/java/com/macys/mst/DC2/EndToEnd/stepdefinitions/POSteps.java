package com.macys.mst.DC2.EndToEnd.stepdefinitions;


import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.configuration.OrderEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.PO4WallEndPoint;
import com.macys.mst.DC2.EndToEnd.db.app.DBInitilizer;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLPo4Walls;
import com.macys.mst.DC2.EndToEnd.db.app.SQLQueriesPO;
import com.macys.mst.DC2.EndToEnd.model.PODataInsert;
import com.macys.mst.DC2.EndToEnd.model.PoLine;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.RequestUtil;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.CreatePOSteps;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.ToContext;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class POSteps {
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
    private CreatePOSteps createPOSteps = new CreatePOSteps();
    private ExpectedDataProperties expectedDataProperties = new ExpectedDataProperties();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private RequestUtil requestUtil = new RequestUtil();
    private CommonUtils commonUtils = new CommonUtils();
    private ValidationUtil validationUtils = new ValidationUtil();

    public POSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("Get PO and details are fetched from test data $param")
    public void getPODetails(ExamplesTable table) throws Exception {
        for (Iterator<Map<String, String>> iterator = table.getRows().iterator(); iterator.hasNext(); ) {
            Map<String, String> exRows = iterator.next();
            String useCaseId = exRows.get("USE_CASE_ID");
            String createUser = exRows.get("CREATE_USER");
            log.info("useCaseId :{}", useCaseId);
            log.info("createUser:{}", createUser);

            String testDataQuery = String.format(SQLQueriesPO.GET_PO_TESTDATA, useCaseId, createUser);
            log.info("Test Data Query : {}", testDataQuery);
            List<Map<?, ?>> dbResults =
                    DBMethods.getValuesFromDBAsList(testDataQuery, "testData");
            //List<Map<Object, Object>> dbResults =DBUtils.getValuesFromDBAsList("testData", testDataQuery);
            log.info(" Test Data DB Results : {}" + dbResults);
            Map<?, ?> randomPORow = dbResults.get(new Random().nextInt(dbResults.size()));

            //Call the db to get poline number and other details
            String poNbr = randomPORow.get("PO_NBR").toString();
            String rcptNbr = randomPORow.get("RCPT_NBR").toString();
            log.info("Test Data PONumber:{} Receipt nbr: {}", poNbr, rcptNbr);

            getPOReceiptDetails(poNbr, rcptNbr);


        }


    }

    @Given("Create PO and details $param")
    public void createPODetails(ExamplesTable table) throws Exception {
        for (Iterator<Map<String, String>> iterator = table.getRows().iterator(); iterator.hasNext(); ) {
            Map<String, String> exRows = iterator.next();
            String poNbr = exRows.get("PO_NBR");
            String rcptNbr = exRows.get("RCPT_NBR");

            log.info("Test Data PONumber:{} Receipt nbr: {}", poNbr, rcptNbr);
            setPOReceiptDetails(poNbr);


        }


    }

    @Given("Get PO line details are fetched from all testdata")
    public void loadAllTestData() {
        Connection con = null;
        try {
            DBInitilizer.cyberarkappid = "WMS_Q";
            DBInitilizer.cyberarksafe = "PSV-FS-WMS-AutoTest-Q";
            con = DBInitilizer.dbConnection("testData");

            List<String> dbResults =
                    // DBUtils.getDBValueInList(con, SQLQueriesPO.GET_ALL_PO_TESTDATA);
                    DBMethods.getDBValueInList(con, SQLQueriesPO.GET_ALL_PO_TESTDATA);
            dbResults.forEach(ponbr -> {
                log.info("Testdata ponbr:[{}]", ponbr);
                try {
                    //List<String> poReceipt = DBUtils.getDBValueInList("pofourwalls", String.format(SQLQueriesPO.GET_PO_RECEPT, ponbr));
                    List<String> poReceipt = DBMethods.getDBValueInList(String.format(SQLQueriesPO.GET_PO_RECEPT, ponbr), "pofourwalls");
                    if (null != poReceipt && poReceipt.isEmpty()) {
                        setPOReceiptDetails(ponbr);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            });
        } catch (Exception e) {
            log.error("Error in loadAllTestData", e);
            org.testng.Assert.fail("Error in loadAllTestData", e);
        } finally {
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setPOReceiptDetails(String poNbr) {
        try {
            String query = String.format(SQLQueriesPO.GET_ORDER_PO_RECEIPT, poNbr);
            log.info("order receipt query: {}", query);
            //List<Map<Object, Object>> dbResults = DBUtils.getValuesFromDBAsList("orders", query);
            List<Map<?, ?>> dbResults = DBMethods.getValuesFromDBAsList(query, "orders");
            log.info("order receipt DB Results : {}" + dbResults);
            Map<String, PODataInsert> rcptNbrMap = new HashMap<>();
            dbResults.forEach((data) -> {
                String rcptNbr = data.get("RCPT_NBR").toString();
                if (!rcptNbrMap.containsKey(rcptNbr)) {
                    PODataInsert requestForDataInsert = new PODataInsert();
                    requestForDataInsert.setPoNbr(Integer.valueOf(data.get("PO_NBR").toString()));
                    requestForDataInsert.setRcptNbr(Integer.valueOf(data.get("RCPT_NBR").toString()));
                    requestForDataInsert.setDeptNbr(Integer.valueOf(data.get("DEPT_NBR").toString()));
                    requestForDataInsert.setStatus("Req");
                    requestForDataInsert.setLocnNbr(Integer.valueOf(data.get("RECV_LOC_NBR").toString()));
                    requestForDataInsert.setReportId(Integer.valueOf(data.get("PO_RCPT_ID").toString()));
                    PoLine poLine = new PoLine();
                    poLine.setInhouseUpc(new BigInteger(data.get("SKU_UPC_NBR").toString()));
                    poLine.setVendorUpc(111111);
                    List<PoLine> poLines = new ArrayList<>(1);
                    poLines.add(poLine);
                    requestForDataInsert.setPoLineItms(poLines);
                    rcptNbrMap.put(rcptNbr, requestForDataInsert);
                } else {
                    PODataInsert requestForDataInsert = rcptNbrMap.get(rcptNbr);
                    PoLine poLine = new PoLine();
                    poLine.setInhouseUpc(new BigInteger(data.get("SKU_UPC_NBR").toString()));
                    poLine.setVendorUpc(111111);
                    List<PoLine> poLines = new ArrayList<>(1);
                    poLines.add(poLine);
                    requestForDataInsert.getPoLineItms().addAll(poLines);
                }

            });

            List<String> jsonMsg = rcptNbrMap.values().stream().map(m -> new JSONObject(m).toString()).collect(Collectors.toList());
            log.info("Pub Sub Message :{}", jsonMsg);
            CommonUtils.publishMessage("mtech-wms-dc2-nonprod", "whm-order-poxref-notification-dev", jsonMsg);

            for (String json : jsonMsg) {
                String PoLineDataInsertResponse = RestUtilities.postRequestResponse(PO4WallEndPoint.PO4WallDataInsert_SERVICE, json, 200);
                log.info("POInsertResponse: {}", PoLineDataInsertResponse);
                StepDetail.addDetail("POInsertResponse: " + PoLineDataInsertResponse, true);
                TimeUnit.SECONDS.sleep(2);
            }


        } catch (Exception e) {
            log.error("Get PO Details", e);
        }
    }

    @Given("Get PO line details are fetched from test data $param")
    public void getPOLineDetails(ExamplesTable table) {
        for (Iterator<Map<String, String>> iterator = table.getRows().iterator(); iterator.hasNext(); ) {
            Map<String, String> exRows = iterator.next();
            String poNbr = exRows.get("PO_NBR");
            String rcptNbr = exRows.get("RCPT_NBR");
            getPOReceiptDetails(poNbr, rcptNbr);
            resetRCPTstatuses(poNbr,rcptNbr);
            dataStorage.getStoredData().put("poNbr", poNbr);
            dataStorage.getStoredData().put("rcptNbr", rcptNbr);
            List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
            for (PoLineBarCodeData.PoLinebarCode poLine : poLinebarCode) {
                String po_report_id = poLine.getReportId();
                dataStorage.getStoredData().put("po_report_id", po_report_id);
                break;
            }
        }
    }

    @Given("PO details fetched from test data for $template with WHM status as $status")
    public void getPO_details_from_Template(String templateName, String status) {
//        System.setProperty("poNbr","4898244");//4693754//4873769
//        System.setProperty("receiptNbr","4578850");//4578369//9624327
        try {
            if (StringUtils.isNotBlank(System.getProperty("poNbr")) && StringUtils.isNotBlank(System.getProperty("receiptNbr"))) {
                String purchaseOrderNbr = System.getProperty("poNbr").trim();
                String purchaseReceiptNbr = System.getProperty("receiptNbr").trim();
                log.info("Prop values are not empty " + purchaseOrderNbr + " ," + purchaseReceiptNbr);
                CommonUtils.doJbehavereportConsolelogAndAssertion("User Provided Purchase order and Receipt Nbr", "PONbr:" + purchaseOrderNbr + " ReceiptNbr : " + purchaseReceiptNbr, true);
                dataStorage.getStoredData().put("poNbr", purchaseOrderNbr);
                dataStorage.getStoredData().put("rcptNbr", purchaseReceiptNbr);
                dataStorage.getStoredData().put("templateName", templateName);
                getPOReceiptDetails(purchaseOrderNbr, purchaseReceiptNbr);
            } else {
                try {
                    List<PoLineBarCodeData.PoLinebarCode> polineData = createPOSteps.getPodetails(templateName,status);
                    if(polineData.size()==0){
                        polineData = createPOSteps.getPoNPoLineItems(templateName,status);
                    }
                    if (polineData.size() <= 0) {
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Purchase order details from template", "The Data from Template is null", true);
                        Map<String, String> POdetailsfromEdp = expectedDataProperties.getPurchaseOrderDetails(templateName);
                        dataStorage.getStoredData().put("poNbr", POdetailsfromEdp.get("PONbr").toString());
                        dataStorage.getStoredData().put("rcptNbr", POdetailsfromEdp.get("RcptNbr").toString());
                        getPOReceiptDetails(POdetailsfromEdp.get("PONbr").toString(), POdetailsfromEdp.get("RcptNbr").toString());
                        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
                        for (PoLineBarCodeData.PoLinebarCode poLine : poLinebarCode) {
                            String po_report_id = poLine.getReportId();
                            dataStorage.getStoredData().put("po_report_id", po_report_id);
                            break;
                        }
                    } else {
                        String poNbr = polineData.get(0).getPoNbr().toString();
                        String rcptNbr = polineData.get(0).getReceiptNbr().toString();
                        String po_reportId = polineData.get(0).getReportId().toString();
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Purchase order details from template", "PONbr:" + poNbr + " ReceiptNbr : " + rcptNbr, true);
                        stepsContext.put(Context.PO_LINES_BARCODE_DATA.name(), polineData, ToContext.RetentionLevel.SCENARIO);
                        stepsContext.put(Context.PO_NBR.name(), poNbr, ToContext.RetentionLevel.SCENARIO);
                        stepsContext.put(Context.PO_RCPT_NBR.name(), rcptNbr, ToContext.RetentionLevel.SCENARIO);
                        dataStorage.getStoredData().put("poNbr", poNbr);
                        dataStorage.getStoredData().put("rcptNbr", rcptNbr);
                        dataStorage.getStoredData().put("po_report_id", po_reportId);

                    }
                } catch (Exception | AssertionError e) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Purchase order details from template", "Exception occured while fetching PO data " + e.getLocalizedMessage(), true);
                    Map<String, String> POdetailsfromEdp = expectedDataProperties.getPurchaseOrderDetails(templateName);
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Purchase order details from template", "PONbr:" + POdetailsfromEdp.get("PONbr").toString() + " ReceiptNbr : " + POdetailsfromEdp.get("RcptNbr").toString(), true);
                    dataStorage.getStoredData().put("poNbr", POdetailsfromEdp.get("PONbr").toString());
                    dataStorage.getStoredData().put("rcptNbr", POdetailsfromEdp.get("RcptNbr").toString());
                    getPOReceiptDetails(POdetailsfromEdp.get("PONbr").toString(), POdetailsfromEdp.get("RcptNbr").toString());
                    List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
                    for (PoLineBarCodeData.PoLinebarCode poLine : poLinebarCode) {
                        String po_report_id = poLine.getReportId();
                        dataStorage.getStoredData().put("po_report_id", po_report_id);
                        break;
                    }
                }
            }

           } catch (Exception e) {
            log.info("Exception occured while fetching the po details :" + e.getMessage());
        }
        if(status.equalsIgnoreCase("CLOSE")){
            setRCPTstatusToClose(stepsContext.get(Context.PO_NBR.name()).toString(),stepsContext.get(Context.PO_RCPT_NBR.name()).toString());
        }else {
            resetRCPTstatuses(stepsContext.get(Context.PO_NBR.name()).toString(),stepsContext.get(Context.PO_RCPT_NBR.name()).toString());
        }

    }

    public void setRCPTstatusToClose(String poNbr, String rcptNbr){
        try {
            String pofourwallsQuery = String.format(SQLPo4Walls.UPDATE_PORECEIPT_STATUS_TO_CLOSE,poNbr,rcptNbr);
            log.info("Orders Rcpt Status Update query {}",pofourwallsQuery);
            DBMethods.deleteOrUpdateDataBase(pofourwallsQuery,"pofourwalls");
        } catch (Exception e) {
            log.info("Exception occurred while setting the PO receipt statuses :" + e.getMessage());
        }
    }
    
    public void resetRCPTstatuses(String poNbr, String rcptNbr){
    	try {
			String pofourwallsQuery = String.format(SQLPo4Walls.UPDATE_PORECEIPT_STATUS,poNbr,rcptNbr);
			log.info("Orders Rcpt Status Update query {}",pofourwallsQuery);
			DBMethods.deleteOrUpdateDataBase(pofourwallsQuery,"pofourwalls");

			String endPoint = commonUtils.getUrl("TestingServices.updateRcptOrders");
			String updateRcptRequest = requestUtil.getRequestBody("#rcptNbr:"+rcptNbr, "updateRcptOrders.json").get(0);
			Response response = WhmRestCoreAutomationUtils.putRequestResponse(endPoint, updateRcptRequest).asResponse();
            CommonUtils.doJbehavereportConsolelogAndAssertion("Update PORcpt",
                    "Update PORcpt Endpoint: " + endPoint + "\n"
                            + "Update PORcpt Request: " + updateRcptRequest + "\n"
                            + "Update PORcpt Response Statuscode: " + response.getStatusCode(),
                    validationUtils.validateResponseStatusCode(response, 200));
		} catch (Exception e) {
			log.info("Exception occured while setting the PO receipt statuses :" + e.getMessage());
		}
    }

    @When("disabled the receipt in QA TransLog table to prevent reuse")
    public void setTheUsedReceiptDisabledInTranslogTable() {
        String url = commonUtils.getUrl("TestingServices.setRcptDisabledInTransLog").replace("#RCPTNBR", (String) dataStorage.getStoredData().get("rcptNbr"));
        Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).when().put(url);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Updated Enabled=0 for the receipt in QA poreceipt_trans_logs table", url, validationUtils.validateResponseStatusCode(response, 200));
    }

    void getPOReceiptDetails(String poNbr, String rcptNbr) {
        List<Map<Object, Object>> dbResults =
                null;
        List<PoLineBarCodeData.PoLinebarCode> polineData = new ArrayList<>();
        try {
            TimeUnit.SECONDS.sleep(5);
            String query = String.format(SQLQueriesPO.GET_PO_DTL, poNbr, rcptNbr);
            log.info("getPOReceiptDetails query: {}", query);
            dbResults = DBMethods.getValuesFromDBAsStringList(query, "pofourwalls");
            log.info("GetPOReceiptDetails DB Results: {}", dbResults);
            Assert.assertTrue("Get PO ReceiptDetails", !dbResults.isEmpty());

            dbResults.forEach(data -> {
                //Call the rest service to get the open qty
                String response = CommonUtils.getRequestResponse(OrderEndPoint.ORDER_SERVICE_PO_SKU.replace("{REPORT_ID}", data.get("REPORT_ID").toString()).replace("{PO_NBR}", data.get("PO_NBR").toString()).replace("{SKU_UPC}", data.get("SKU_UPC").toString()));
                if (StringUtils.isNotBlank(response)) {
                    PoLineBarCodeData.PoLinebarCode poData = new PoLineBarCodeData().new PoLinebarCode();
                    poData.setSKU(data.get("SKU_UPC").toString());
                    poData.setReceiptNbr(data.get("RCPT_NBR").toString());
                    poData.setPoNbr(data.get("PO_NBR").toString());
                    poData.setPoLineBarCode(data.get("BARCODE").toString());
                    poData.setReportId(data.get("REPORT_ID").toString());
                    poData.setLocationNbr(data.get("LOCN_NBR").toString());
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject json = jsonArray.getJSONObject(0);
                    poData.setOpenQty(json.get("openqty").toString());
                    polineData.add(poData);
                }
            });

            stepsContext.put(Context.PO_LINES_BARCODE_DATA.name(), polineData, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.PO_NBR.name(), poNbr, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.PO_RCPT_NBR.name(), rcptNbr, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.REPORT_ID.name(), dbResults.get(0).get("REPORT_ID").toString(), ToContext.RetentionLevel.SCENARIO);
        } catch (Exception e) {
            log.error("Get POReceiptDetails", e);
        }
    }

    void getPOReceiptDetailsAPI(String poNbr, String rcptNbr) {
        Map<String, String> POReceiptDetails_ordMngt = null;
        List<Map<String, String>> poscreens_poDetails = null;
        List<PoLineBarCodeData.PoLinebarCode> polineData = new ArrayList<>();
        String reportId = null;
        try {
            TimeUnit.SECONDS.sleep(5);
            String ordMngt_PODetails = CommonUtils.getRequestResponse(String.format(OrderEndPoint.ORDER_SERVICE_PO_Inquiry, poNbr, rcptNbr));
            log.info("ordMngt_PODetails service response {}", ordMngt_PODetails);

            if (ordMngt_PODetails.isEmpty()) {
                Assert.assertTrue("Unable to access Order Mngt API for PONbr " + poNbr + " And Receipt Nbr " + rcptNbr, false);
            }
            POReceiptDetails_ordMngt = CommonUtils.getMapFromJson(new JSONObject(ordMngt_PODetails).getJSONArray("poInquiry").get(0).toString());
            reportId = String.valueOf(POReceiptDetails_ordMngt.get("reportId"));
            String PODetails_reportID = CommonUtils.getRequestResponse(String.format(PO4WallEndPoint.poscreens_rcptDetailReports, reportId));
            log.info("poscreens_rcptDetailReports service response {}", PODetails_reportID);

            if (PODetails_reportID.isEmpty()) {
                Assert.assertTrue("Unable to access poscreens rcptDetailReports API for reportID " + reportId, false);
            }
            String rcpt_Nbr = new JSONObject(PODetails_reportID).getJSONObject("poDetail").getJSONObject("poDetailHdr").getString("rcptNbr");
            String po_Nbr = new JSONObject(PODetails_reportID).getJSONObject("poDetail").getJSONObject("poDetailHdr").getString("poNbr");
            String report_id = new JSONObject(PODetails_reportID).getJSONObject("poDetail").getJSONObject("poDetailHdr").getString("reportId");
            int loc_Nbr = new JSONObject(PODetails_reportID).getJSONObject("poDetail").getJSONObject("poDetailHdr").getInt("locnNbr");
            String location_nbr = String.valueOf(loc_Nbr);
            poscreens_poDetails = CommonUtils.getListOfMapsFromJsonArray(new JSONObject(PODetails_reportID).getJSONObject("poDetail").getJSONArray("poLineDetails"));
            PoLineBarCodeData.PoLinebarCode poData = new PoLineBarCodeData().new PoLinebarCode();
            for (Map<String, String> eachPOLine : poscreens_poDetails) {
                poData.setSKU(eachPOLine.get("inhouseUpc").toString());
                poData.setReceiptNbr(rcpt_Nbr);
                poData.setPoNbr(po_Nbr);
                poData.setPoLineBarCode(eachPOLine.get("polinebrcd").toString());
                poData.setReportId(report_id);
                poData.setLocationNbr(location_nbr);
                poData.setOpenQty(String.valueOf(eachPOLine.get("expectedUnits")));
                polineData.add(poData);
            }

            stepsContext.put(Context.PO_LINES_BARCODE_DATA.name(), polineData, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.PO_NBR.name(), poNbr, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.PO_RCPT_NBR.name(), rcptNbr, ToContext.RetentionLevel.SCENARIO);


        } catch (Exception e) {
            log.error("Get POReceiptDetails", e);
        }
    }

    @Given("Purchase Order details from storeData")
    public void getPOAndReceiptNbr_From_StoredData() {
        String PONumber = (String) dataStorage.getStoredData().get("poNbr");
        String RcptNumber = (String) dataStorage.getStoredData().get("rcptNbr");

        getPOReceiptDetails(PONumber, RcptNumber);
    }
}
