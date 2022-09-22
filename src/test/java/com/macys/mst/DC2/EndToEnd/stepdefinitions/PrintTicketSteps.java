package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.macys.mst.DC2.EndToEnd.configuration.ConfigurationEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.configuration.InventoryEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.PO4WallEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.DC2.EndToEnd.execdrivers.ExecutionConfig;
import com.macys.mst.DC2.EndToEnd.model.Container;
import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.model.LocationDistro;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateTotePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PrintTicketPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData.PoLinebarCode;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.RequestUtil;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
public class PrintTicketSteps {

    private static final String PRINT_DIVERT_LANE = "B120345";

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    PrintTicketPage printTicketPage = PageFactory.initElements(driver, PrintTicketPage.class);
    CreateTotePage createTotePage = PageFactory.initElements(driver, CreateTotePage.class);
    CommonUtils commonUtils = new CommonUtils();
    RandomUtil randomUtil = new RandomUtil();
    private RequestUtil requestUtil = new RequestUtil();
    ValidationUtil validationUtils = new ValidationUtil();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    private StepsContext stepsContext;
    MovePalletSteps movePalletsteps= new MovePalletSteps(stepsContext);

    public long TestNGThreadID = Thread.currentThread().getId();

    @Setter
    @Getter
    private Map<String, String> printTicketConfig;

    @Setter
    @Getter
    private List<String> toteIds;

    public PrintTicketSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("user logged into applications and menu items is displayed")
    public void logintoApplication() throws Exception {
        loginToApplication();
        printTicketPage.validateRFMenu();
    }

    @Given("User logs in to RF")
    public void loginToApplication() throws Exception {

    }

    @When("User selects $param menu")
    public void selectOptionFromMenumenu(String param) {
        printTicketPage.selectOptionFromMenu(param);
    }

    @Then("Print Ticket-Scan Station Screen is displayed")
    public void verifyScanStationScreen() {
        printTicketPage.validateScanStationScreen();
    }

    @When("User Scans a $param StationId $staionNum in the Print Ticket-Scan Station screen")
    public void scanStationId(String param, String stationNum) {

    }

    @Then("Print Ticket-Scan Tote Screen is displayed")
    public void verifyScanToteScreen() {
        printTicketPage.validateScanToteScreen();
    }

    @When("user scans $param Container")
    public void scanTote(String param) {
        printTicketPage.scanTote(param);
    }

    @When("User enters $qty in QTY")
    public void scanQty(String qty) throws InterruptedException {
        printTicketPage.enterqty(qty);
    }

    @Then("Print Ticket-Scan Next Tote Screen is displayed with details")
    public void verifyScanNextToteScreen() {

    }

    @When("User uses $menu for RF Print Ticket transaction $paramtable")
    public void completePrintTicketSteps(String menu, ExamplesTable paramtable) throws Exception {
        logintoApplication();
        selectOptionFromMenumenu(menu);
        List<Map<String, String>> exRows = paramtable.getRows();
        exRows.stream().forEach(m -> {
            m.forEach((k, v) -> {
                if (k.equals("Scan Station") && !v.equalsIgnoreCase("NA")) {
                    verifyScanStationScreen();
                    String[] value = v.split("&");
                    scanStationId(value[0], value[1]);
                } else if (k.equalsIgnoreCase("Scan Tote") && !v.equalsIgnoreCase("NA")) {
                    verifyScanToteScreen();
                    scanTote(v);
                } else if (k.equalsIgnoreCase("Scan Qty") && !v.equalsIgnoreCase("NA")) {
                    try {
                        scanQty(v);
                    } catch (InterruptedException e) {
                        log.info(e.toString());
                    }
                } else if (k.equalsIgnoreCase("Scan Next Tote") && !v.equalsIgnoreCase("NA")) {
                    verifyScanNextToteScreen();
                } else if (k.equalsIgnoreCase("ToteValue") && !v.equalsIgnoreCase("NA")) {
                }
            });
        });

    }

    private WebDriverWait getWait(int secs) {
        WebDriverWait wait = new WebDriverWait(driver, secs);
        return wait;
    }

    @When("Print ticket is done for the totes coming to printing station(s)")
    public void selectPrintTicket() throws Exception {
        createTotePage.navigateToCreateTote();
        getWait(30);
        printTicketPage.selectOptionFromMenu("Print Ticket");
        verifyScanStationScreen();
        String ticketType = getTicketType();
        loadPrintTicketConfig(ticketType);

        if (null != ticketType) {
            String stationId = getPrintTicketConfig().get("stationId");
            if (null != stationId) {
                printTicketPage.scanStationID(stationId);
            } else {
                Assert.fail("Station Id not found.");
            }
        } else {
            Assert.fail("Ticket Type not found.");
        }
        verifyScanToteScreen();
        scanTote();
    }

    @Then("System updates the printed status for the totes")
    public void validatePrintTicketStatus() {
        // only for the ENV UAT update container status to PRT for tote list
        String execenv = ExecutionConfig.getExecEnv();
        if ("UAT".equalsIgnoreCase(execenv)) {
            List<Container> containers = new ArrayList<>();
            getToteIds().forEach(toteid -> {
                Container containerObj = new Container();
                containerObj.setContainerStatusCode("PRT");
                containerObj.setContainerType("TOTE");
                containerObj.setBarCode(toteid);
                containers.add(containerObj);
            });

            JSONArray jsonA = new JSONArray(containers);
            RestUtilities.putRequestResponse(ReadHostConfiguration.UPDATE_CONT_STATUS.value(), jsonA.toString());
        }
        getToteIds().forEach(toteId -> {
            printTicketPage.verifyPrintStatus(toteId);
        });

    }

    @Then("validate number of tickets printed for multiprocess area")
    public void validateNumberOfTicketsmultiprocess() throws InterruptedException {

        ListMultimap<String, String> processAreaToteMap = (ListMultimap<String, String>) stepsContext.get(Context.PROCESS_AREA_TOTE_MAP.name());
        TimeUnit.SECONDS.sleep(20);
        String execenv = ExecutionConfig.getExecEnv();
        if (!CreateToteSteps.hasInnerPack) {

            Multimaps.asMap(processAreaToteMap).forEach((processArea, totelist) -> {
                if (processAreaMHEflag(processArea) && !"UAT".equalsIgnoreCase(execenv)) {
                    totelist.forEach(toteId -> {
                        CommonUtils.verifyMsgServiceResponse("PrintTicket", "CONTROUTE", toteId);
                        validateMHEmessageFromDB("CONTROUTE", toteId);
                    });
                } else
                    log.info("Environment is UAT || NO CONTROUTE due to MHE FLAG TURNED OFF for ProcessArea: [{}]", processArea);

            });


        } else {
            Multimaps.asMap(processAreaToteMap).forEach((processArea, totelist) -> {
                if (processAreaMHEflag(processArea) && !"UAT".equalsIgnoreCase(execenv)) {
                    Set<String> toteSet = new TreeSet<>(getToteIds());
                    toteSet.forEach(toteId -> {
                        CommonUtils.verifyMsgServiceResponse("PrintTicket", "CONTROUTE", toteId);
                        validateMHEmessageFromDB("CONTROUTE", toteId);
                    });
                } else
                    log.info("Environment is UAT || NO CONTROUTE due to MHE FLAG TURNED OFF for ProcessArea: [{}]", processArea);

            });
        }
    }

    @Then("validate number of tickets printed")
    public void validateNumberOfTickets() throws InterruptedException {
        //if env is UAT , then dont do anything ..
        TimeUnit.SECONDS.sleep(20);

        String execenv = ExecutionConfig.getExecEnv();
        if (!CreateToteSteps.hasInnerPack) {
            if (processAreaMHEflag() && !"UAT".equalsIgnoreCase(execenv)) {
                getToteIds().forEach(toteId -> {
                    CommonUtils.verifyMsgServiceResponse("PrintTicket", "CONTROUTE", toteId);
                    validateMHEmessageFromDB("CONTROUTE", toteId);
                });
            } else
                log.info("NO CONTROUTE due to MHE FLAG TURNED OFF || Environment is UAT ");
        } else {
            if (processAreaMHEflag() && !"UAT".equalsIgnoreCase(execenv)) {
                Set<String> toteSet = new TreeSet<>(getToteIds());
                toteSet.forEach(toteId -> {
                    CommonUtils.verifyMsgServiceResponse("PrintTicket", "CONTROUTE", toteId);
                    validateMHEmessageFromDB("CONTROUTE", toteId);
                });
            } else
                log.info("NO CONTROUTE due to MHE FLAG TURNED OFF || Environment is UAT ");
        }
    }

    @Then("CONTDIVERT Message route to printticket")
    public void sendContDivertMessage() {

        if (processAreaMHEflag()) {
            getToteIds().forEach(toteId -> {
                CommonUtils.sendContDivertMessage(toteId, PRINT_DIVERT_LANE);
            });
        } else
            log.info("NO CONTDIVERT due to MHE flag TURNED OFF");
    }

    private void scanTote() {
        Map<String, List<String>> barcodeWithTotes = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());

        List<String> totes = barcodeWithTotes.values().stream().flatMap(List::stream).collect(Collectors.toList());
        setToteIds(totes);
        Set<String> toteset = new TreeSet<>(totes);
        if (CreateToteSteps.hasInnerPack) {
            for (String eachTote : toteset) {
                //  InventoryContainer inventoryContainer = CommonUtils.getInventory(eachTote);
                //  List<InventorySnapshot> inventorySnapshot = inventoryContainer.getInventorySnapshotList();
                //  Integer qty = inventorySnapshot.stream().collect(Collectors.summingInt(InventorySnapshot::getQuantity));
                Integer InnerPack_qty = CreateToteSteps.TOTE_MAX_CAP_InnerPack;
                printTicketPage.scanTote(eachTote);
                //	printTicketPage.validatePrintScreenPageForInnerPack(toteId, InnerPack_qty,IsEditable());
                if (IsEditable())
                    printTicketPage.enterQuantity(String.valueOf(InnerPack_qty));
                // Integer totalNbrTickets = Math.multiplyExact(InnerPack_qty, Integer.parseInt(String.valueOf(poLineDetails.get("noOfTickets"))));
                //	printTicketPage.verifyPrintTicketMsgScreen(totalNbrTickets, printTicketConfig.get("printerId"));
            }
            printTicketPage.clickButton("back");
        } else {
            barcodeWithTotes.forEach((poLineBarcode, toteList) -> {
                Map<String, String> poLineDetails = getPoLineDetails(poLineBarcode);
                toteList.forEach(toteId -> {
                    InventoryContainer inventoryContainer = CommonUtils.getInventory(toteId);
                    Integer qty = inventoryContainer.getInventorySnapshotList().get(0).getQuantity();
                    printTicketPage.scanTote(toteId);
                    log.info("IsEditable :{}", IsEditable());

                    try{
                        getWait(15).until(ExpectedConditions.visibilityOf(printTicketPage.reticketConfirmation));
                        printTicketPage.reticketConfirmation.click();
                        printTicketPage.validatePrintScreenPage(poLineDetails, toteId, qty, IsEditable());
                    }catch (Exception ex){
                        printTicketPage.validatePrintScreenPage(poLineDetails, toteId, qty, IsEditable());
                    }

                    if (IsEditable())
                        printTicketPage.enterqty(String.valueOf(qty));
                    Integer totalNbrTickets = Math.multiplyExact(qty, Integer.parseInt(String.valueOf(poLineDetails.get("noOfTickets"))));
                    printTicketPage.verifyPrintTicketMsgScreen(totalNbrTickets, printTicketConfig.get("printerId"));
                    printTicketPage.clickButton("back");

                });
            });
        }
        printTicketPage.clickButton("back");
        printTicketPage.clickButton("exit");
    }

    public boolean IsEditable() {
        String getConfigEndpoint = String.format(ConfigurationEndPoint.configKey, "IsQtyEditable");
        String getConfigResponse = RestUtilities.getRequestResponse(getConfigEndpoint);
        JSONArray jsonArray = new JSONArray(getConfigResponse);
        //JSONObject configValueObject = new JSONObject(jsonArray.getJSONObject(0).getString("configValue"));
        return !(jsonArray.getJSONObject(0).getString("configValue").contains("[{\"isQtyEditable\":false}]"));
        //return configValueObject.getBoolean("isQtyEditable");
    }

    public boolean processAreaMHEflag() {

        String getConfigEndpoint = String.format(ConfigurationEndPoint.configKey, "ProcessAreaConfig");
        String getConfigResponse = RestUtilities.getRequestResponse(getConfigEndpoint);

        String actualProcessArea = (String) stepsContext.get(Context.PO_PROCESS_AREA.name());

        boolean vasMhe = false;
        JSONArray jsonArray = new JSONArray(getConfigResponse);
        String ProcessAreaConfigResponse = jsonArray.getJSONObject(0).getString("configValue").replaceAll("\\\"", "\"");
        JSONArray processAreaJsonArray = new JSONArray(ProcessAreaConfigResponse);
        for (int i = 0; i < processAreaJsonArray.length(); i++) {
            if (processAreaJsonArray.getJSONObject(i).getString("processArea").equalsIgnoreCase(actualProcessArea)) {
                vasMhe = processAreaJsonArray.getJSONObject(i).getBoolean("vasMhe");
            }

        }

        return vasMhe;
    }

    public boolean processAreaMHEflag(String actualProcessArea) {

        String getConfigEndpoint = String.format(ConfigurationEndPoint.configKey, "ProcessAreaConfig");
        String getConfigResponse = RestUtilities.getRequestResponse(getConfigEndpoint);

        boolean vasMhe = false;
        JSONArray jsonArray = new JSONArray(getConfigResponse);
        String ProcessAreaConfigResponse = jsonArray.getJSONObject(0).getString("configValue").replaceAll("\\\"", "\"");
        JSONArray processAreaJsonArray = new JSONArray(ProcessAreaConfigResponse);
        for (int i = 0; i < processAreaJsonArray.length(); i++) {
            if (processAreaJsonArray.getJSONObject(i).getString("processArea").equalsIgnoreCase(actualProcessArea)) {
                vasMhe = processAreaJsonArray.getJSONObject(i).getBoolean("vasMhe");
            }

        }

        return vasMhe;
    }

    private String getTicketType() {
        Map<String, List<String>> barcodeWithTotes = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
        for (String poLineBarCode : barcodeWithTotes.keySet()) {
            String response = CommonUtils.getRequestResponse(String.format(PO4WallEndPoint.PO4WALL_GET_POLINE_DETAILS_ONBARCODE, poLineBarCode));
            JSONObject jsonObj = new JSONObject(response);
            return jsonObj.getString("ticketType");
        }
        return null;
    }

    private void loadPrintTicketConfig(String ticketType) {
        String response = RestUtilities.getRequestResponse(ReadHostConfiguration.CONFIG_HOST_NAME.value() + "/printticketconfig");
        JSONArray jsonArray = new JSONArray(response);
        if (null != jsonArray && jsonArray.length() > 0) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            JSONArray configValueArray = new JSONArray(jsonObject.getString("configValue"));
            setPrintTicketConfig(CommonUtils.getListOfMapsFromJsonArray(configValueArray).stream().filter(filter -> {
                return ticketType.equalsIgnoreCase(filter.get("ticketType")) && "Mcy733".equalsIgnoreCase(filter.get("stationId"));
            }).findAny().orElse(null));
        }
    }


    private Map<String, String> getPoLineDetails(String poLineBarcode) {
        return CommonUtils.getMapFromJson(RestUtilities.getRequestResponse(ReadHostConfiguration.FETCH_POLINE_DTLS_URL.value() + ReadHostConfiguration.LOCATION_NUMBER.value() +
                "/polines/" + poLineBarcode));
    }

    public void validateMHEmessageFromDB(String destID, String filter) {
        int size = 100;
        MHESteps mheSteps = new MHESteps(stepsContext);
        List<PoLinebarCode> poLinebarCode = (List<PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        for (PoLinebarCode poLine : poLinebarCode) {
            Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap = (Map<BigInteger, List<LocationDistro>>) stepsContext.get(Context.SKU_STORE_LOCN_QTY_MAP.name());
            Map<BigInteger, Integer> skuDeptMap = (Map<BigInteger, Integer>) stepsContext.get(Context.SKU_DEPT_NBR_MAP.name());
            List<Map<String, String>> valueFromDB = mheSteps.fetchMHEDetailsFromDB(destID, filter, "", "", size);
            if (destID.equalsIgnoreCase("CONTROUTE")) {
                if (checkCONROUTEMHE(valueFromDB, skuStoreLocnQuantityMap, skuDeptMap, destID, filter)) {
                    log.info(destID + " outgoing payload message is as expected");
                    StepDetail.addDetail(destID + " outgoing payload message is as expected", true);
                    break;
                } else {
                    log.info(destID + " outgoing payload message is not as expected");
                    Assert.assertTrue(destID + " outgoing payload message is not as expected", false);
                }
            }
            if (destID.equalsIgnoreCase("TOTECONT")) {
                if (checkTOTECONTMHE(valueFromDB, skuStoreLocnQuantityMap, skuDeptMap, destID, filter, poLine.getPoLineBarCode(), poLine.getPoNbr(), poLine.getReceiptNbr(), false)) {
                    log.info(destID + " outgoing payload message is as expected");
                    StepDetail.addDetail(destID + " outgoing payload message is as expected", true);
                    break;
                } else {
                    log.info(destID + " outgoing payload message is not as expected");
                    Assert.assertTrue(destID + " outgoing payload message is not as expected", false);
                }

            }

        }
    }

    private Boolean checkCONROUTEMHE(List<Map<String, String>> valueFromDB, Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap, Map<BigInteger, Integer> skuDeptMap, String destID, String filter) {
        Boolean flag = false;
        String expectedMsg = valueFromDB.get(0).get("WCS Payload");
        if(!ExpectedDataProperties.pyramidJsonproperty){
	        String[] values = expectedMsg.split("\\|");
	        if (!(values.length == 7)) {
	            Assert.assertTrue("Message length not as expected", false);
	        }
	        if (!(values[1].equalsIgnoreCase(destID))) {
	            Assert.assertTrue("Message contain :" + values[1] + " but Expected is: " + destID, false);
	        }
	        if (!(values[2].equalsIgnoreCase(filter))) {
	            Assert.assertTrue("Message contain :" + values[2] + " but Expected is: " + filter, false);
	        }
	        flag = checkDeptNbr(skuStoreLocnQuantityMap, skuDeptMap, values[3]);
        }else{
        	JsonPath controute = new JsonPath(expectedMsg);
        	CommonUtils.doJbehavereportConsolelogAndAssertion("CONTROUTE message Destination is as expected", filter, destID.equalsIgnoreCase(controute.getString("messageType")));
        	CommonUtils.doJbehavereportConsolelogAndAssertion("CONTROUTE message Container is as expected", filter, filter.equalsIgnoreCase(controute.getString("container")));
        	flag = checkDeptNbr(skuStoreLocnQuantityMap, skuDeptMap, String.valueOf(controute.getString("dept")));
        }
        return flag;
    }

    private Boolean checkTOTECONTMHE(List<Map<String,String>> valueFromDB,Map<BigInteger,List<LocationDistro>> skuStoreLocnQuantityMap,Map<BigInteger,Integer> skuDeptMap,String destID,String filter,String poLineBrcd,String poNbr,String rcptNbr, boolean prevPut){
    	Boolean flag = false;
        String inventoryBarcodeEndpoint = String.format(InventoryEndPoint.InventoryWithContainerBarcode,"7221",filter);
        List<Map<String,String>> inventoryResults = new LinkedList<>();
        String inventoryForContainerResponse = CommonUtils.getRequestResponse(inventoryBarcodeEndpoint);
        JSONObject containerInventoryJson = new JSONObject(inventoryForContainerResponse);
        JSONArray containerInventoryArray = containerInventoryJson.getJSONArray("inventorySnapshotList");
        for (int i = 0; i < containerInventoryArray.length(); i++) {
            Map<String, String> inventorySnapshotMap = new HashMap<String, String>();
            inventorySnapshotMap.put("ITEM", containerInventoryArray.getJSONObject(i).getString("item"));
            inventorySnapshotMap.put("QUANTITY", String.valueOf(containerInventoryArray.getJSONObject(i).getInt("quantity")));
            inventorySnapshotMap.put("CONTAINER", containerInventoryArray.getJSONObject(i).getString("container"));
            inventoryResults.add(inventorySnapshotMap);
        }
    	String expectedMsg = valueFromDB.get(0).get("WCS Payload");
        ExpectedDataProperties expdataForMessageBasicValidation = new ExpectedDataProperties();
        LinkedHashMap<String, String> actualMessageDataMap = expdataForMessageBasicValidation.expectedMessage("TOTECONT", expectedMsg, prevPut, "PO", CreateToteSteps.hasInnerPack);
    	if(!(actualMessageDataMap.get("containerBarcode").equalsIgnoreCase(filter))){
    		Assert.assertTrue("Message contain :"+actualMessageDataMap.get("containerBarcode")+" but Expected is: "+filter,false);
    	}
    	if(CreateToteSteps.hasInnerPack){    		
    		Map<String, String> expectedSkuQtyMap = getInnerPackForSKUs(poLineBrcd);
    		Map<String, String> actualSkuQtyMap   = new HashMap<>();
    		actualSkuQtyMap.put(actualMessageDataMap.get("upc1"),actualMessageDataMap.get("upc1Qty"));
    		actualSkuQtyMap.put(actualMessageDataMap.get("upc2"),actualMessageDataMap.get("upc2Qty"));
    		actualSkuQtyMap.put(actualMessageDataMap.get("upc3"),actualMessageDataMap.get("upc3Qty"));
            CommonUtils.doJbehavereportConsolelogAndAssertion("SKU List does not match Distro SKU List", "Expected SKU List: "+expectedSkuQtyMap+" Actual SKU List: "+actualSkuQtyMap, expectedSkuQtyMap.equals(actualSkuQtyMap));
    	}else{
    		if(!(inventoryResults.get(0).get("ITEM").toString().equalsIgnoreCase(actualMessageDataMap.get("upc1")))){
        		Assert.assertTrue("Message doesnot contain "+actualMessageDataMap.get("upc1")+" as expected",false);
        	}
    	}
    	flag = checkDeptNbr(skuStoreLocnQuantityMap,skuDeptMap,actualMessageDataMap.get("dept"));
        if (!(actualMessageDataMap.get("poNbr")).equalsIgnoreCase(poNbr)) {
            Assert.assertTrue("Message contain :" + actualMessageDataMap.get("poNbr") + " but Expected is: " + poNbr, false);
        }
        if (!(actualMessageDataMap.get("poReceipt")).equalsIgnoreCase(rcptNbr)) {
            Assert.assertTrue("Message contain :" + actualMessageDataMap.get("poReceipt") + " but Expected is: " + rcptNbr, false);
        }
        return flag;
    }

    private Boolean checkDeptNbr(Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap, Map<BigInteger, Integer> skuDeptMap, String value) {
        Boolean flag = false;
        for (BigInteger sku : skuStoreLocnQuantityMap.keySet()) {
            if (value.equalsIgnoreCase(skuDeptMap.get(sku).toString())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private Map<String,String> getInnerPackForSKUs(String poLineBarcode) {
        Map<String, String> skuQtyMap = new HashMap<>();
        String response = CommonUtils.getRequestResponse(String.format(PO4WallEndPoint.PO4WALL_GET_POLINE_PCK, poLineBarcode));
        if (StringUtils.isNotEmpty(response)) {
            JSONArray jsonArray = new JSONArray(response).getJSONObject(0).getJSONArray("packDetails");
            for (int i = 0; i < jsonArray.length(); i++) {
            	skuQtyMap.put(String.valueOf(jsonArray.getJSONObject(i).get("skuUpc")),String.valueOf(jsonArray.getJSONObject(i).get("qty")));
            }
        }
        return skuQtyMap;
    }

}
