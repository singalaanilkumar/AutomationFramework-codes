package com.macys.mst.DC2.EndToEnd.stepdefinitions;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.macys.mst.DC2.EndToEnd.configuration.*;
import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.db.app.SQLPackage;
import com.macys.mst.DC2.EndToEnd.execdrivers.ExecutionConfig;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateInnerPacksPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateTotePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODashboardReleasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData.PoLinebarCode;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import com.macys.mst.foundationalServices.utils.TearDownMethods;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class CreateToteSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private CommonUtils commonUtils = new CommonUtils();
    CreateTotePage createTotePage = PageFactory.initElements(driver, CreateTotePage.class);
    CreateInnerPacksPage createInnerPacksPage = PageFactory.initElements(driver, CreateInnerPacksPage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    public static List<String> totes = new ArrayList<String>();
    private final Integer TOTE_MAX_CAP = 50;
    public static final Integer TOTE_MAX_CAP_InnerPack = 6;
    public static String eachInnerpackQty = "";
    public static boolean hasInnerPack = false;
    private Integer OVERAGE_QTY = 10;
    private Integer SHORTAGE_QTY = 10;
    private StepsContext stepsContext;
    public static int tempQty = 0;
    StepsDataStore dataStorage = StepsDataStore.getInstance();
/*    @Getter
    @Setter
    public static boolean innerpack;*/

    //public static int createToteLoopingcount=0;
    public CreateToteSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    private void cleanUpAnyExistingTotes() {
        TearDownMethods tearDownMethods = new TearDownMethods();
        List<PoLinebarCode> poLinebarCode = (List<PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        poLinebarCode.forEach(value -> {
            //Below commented line of code is an alternate method to clean the existing totes.. Pls dont delete
           //    tearDownMethods.tearDownForEndToEnd(value.getPoNbr(), value.getReceiptNbr());
            tearDownMethods.cleanUpAnyExistingTotes(value.getPoNbr(), value.getReceiptNbr());
            tearDownMethods.POFourWallDataCleanup(value.getReceiptNbr());
        });
    }

    @Given("User logs in to RF application, selects DC2.0 RF Options")
    public void loginCreateTote() throws Exception {
        createTotePage.navigateToCreateTote();
    }

    @Given("Required Parameters are passed to update new configuration $exampleTable")
    public String addConfigurations(String param, ExamplesTable table) {
        String genericConfigUrl = ReadHostConfiguration.GENERIC_CONFIG_URL.value();

        String response = "";
        Iterator rows = table.getRows().iterator();
        while (rows.hasNext()) {
            Map<String, String> row = (Map) rows.next();
            String configKey = (String) row.get("CONFIG_KEY");
            String configValue = (String) row.get("CONFIG_VALUE");

            if (param.equalsIgnoreCase("update")) {
                String configHostName = String.format("%1$s%2$s%3$s", ReadHostConfiguration.CONFIG_HOST_NAME.value(), "/", configKey);
                log.info("Get generic config url :{}", configHostName);
                response = RestUtilities.getRequestResponse(configHostName);
                Assert.assertTrue("Response from the get serevice" + response, response != null);
                JSONArray array = new JSONArray(response);
                log.info("Updating  the configuration for config key:[{}] with values: [{}] and id of the configuration is [{}]", new Object[]{configKey, configValue, array.getJSONObject(0).getInt("id")});

                StepDetail.addDetail("Updating  the configuration for config key:" + configKey + "with values: "
                        + configValue + " and id of the configuration is " + array.getJSONObject(0).getInt("id"), true);
                response = RestUtilities.putRequestResponse(genericConfigUrl + array.getJSONObject(0).getInt("id"),
                        configValue);
                CommonUtils.deleteRequest(ReadHostConfiguration.DELETE_CACHE_URL.value(), 308);

            }
        }
        return response;
    }

    @Given("Reset scenario")
    public void stepContextReset() {
        stepsContext.resetScenario();
    }

    public String generateRangeFromLocation(String barcode) {
        StringBuffer sb = new StringBuffer();
        sb.append(barcode.charAt(0) + "-" + barcode.charAt(0));
        sb.append(";");
        sb.append(barcode.charAt(1) + "-" + barcode.charAt(1));
        sb.append(";");
        sb.append(barcode.charAt(2) + "" + barcode.charAt(3) + "-" + barcode.charAt(2) + barcode.charAt(3));
        sb.append(";");
        sb.append(barcode.charAt(4) + "-" + barcode.charAt(4));
        sb.append(";");
        sb.append(barcode.substring(sb.length() - 13, barcode.length()) + "-" + barcode.substring(sb.length() - 13, barcode.length()));
        log.info(sb.toString());
        return sb.toString();
    }

    @When("ProcessArea is performed and totes are created for $full release and staged to $locationBarcode staging location $param")
    public void createTote(String full, String locationBarcode, ExamplesTable table) throws Exception {
        hasInnerPack = false;
        createTotePage.UICacheCleanup();
        loginCreateTote();
        String overage_shortage_flag = "";
        if (full.contains("partial") || full.equals("full")) {
            cleanUpAnyExistingTotes();
        }
        CommonUtil.deleteRequest(String.format(InventoryEndPoint.CONSUME_TOTE, "7221", locationBarcode, "TestAutomation"), 204);
        String stageLocation = locationBarcode;
        int createToteLoopingcount = 0;
        boolean testflg = false;
        Set<String> totelst = new HashSet<>();
        ListMultimap<String, String> stageLocationToteMap = ArrayListMultimap.create();
        ListMultimap<String, String> processAreaToteMap = ArrayListMultimap.create();
        Map<String, List<String>> polines = new LinkedHashMap<>();
        Map<PoLinebarCode, String> innerPack_SKU_qty = new LinkedHashMap<>();
        String distroType = "";
        String receivedqty = "";
        int rcvqty = 0;

        List<PoLinebarCode> poLinebarCode = (List<PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        if (full.contains("full")) {
            for (PoLinebarCode eachItem : poLinebarCode) {
                innerPack_SKU_qty.put(eachItem, null);
            }
        }
        log.info("List Size: " + poLinebarCode.size());
        if (full.contains("partial")) {
            LinkedList<Long> UpcsToRelease = PODashboardReleasePage.UPCsForRelease;
            log.info("UPCs to be Released {}", UpcsToRelease);
            int noOfRow = 0;
            List<Map<String, String>> tableRows = new LinkedList<Map<String, String>>();
            for (int i = 0; i < UpcsToRelease.size(); i++) {
                Map<String, String> rowValues = new LinkedHashMap<String, String>();
                String UPC = String.valueOf(UpcsToRelease.get(i));
                rowValues.put("SKUUPC", String.valueOf(UpcsToRelease.get(i)));
                for (PoLinebarCode poLine : poLinebarCode) {
                    if (poLine.getSKU().equalsIgnoreCase(UPC)) {
                        if (full.charAt(full.length() - 1) == '1') {
                            rowValues.put("Quantity", String.valueOf(Integer.valueOf(poLine.getOpenQty()) / 2));
                            tempQty = Integer.valueOf(poLine.getOpenQty()) / 2;
                        } else if (full.charAt(full.length() - 1) == '2') {
                            if (i != 0) {
                                rowValues.put("Quantity", String.valueOf(Integer.valueOf(poLine.getOpenQty())));
                            } else
                                rowValues.put("Quantity", String.valueOf(Integer.valueOf(poLine.getOpenQty()) - tempQty));
                        } else if (full.charAt(full.length() - 1) == '3') {
                            if (i != 0 && i != 1)
                                rowValues.put("Quantity", String.valueOf(Integer.valueOf(poLine.getOpenQty()) / 2));
                            else
                                rowValues.put("Quantity", String.valueOf(Integer.valueOf(poLine.getOpenQty()) + 1));
                        }
                    }
                }
                rowValues.put("StageLocation", "ABCD");
                if (table.getHeaders().contains("Rerelease")) {
                    rowValues.put("Rerelease", "1");
                }
                tableRows.add(i, rowValues);
                noOfRow = noOfRow + 1;
            }
            table = table.withRows(tableRows);
        }
        log.info("Example Table row size ::{}", table.getRows().size());
        // custom PO lines
        if (table.getRows().size() > 0) {
            log.info("Custom PO Line");
            Map<PoLinebarCode, String> ip_SKU_qty = new LinkedHashMap<>();
            for (Iterator<Map<String, String>> iterator = table.getRows().iterator(); iterator.hasNext(); ) {
                Map<String, String> exRows = iterator.next();
                String skuupc = exRows.get("SKUUPC");
                overage_shortage_flag = exRows.get("TypeFlag");
                String innerpackqty = exRows.get("InnerPackQty");
                if (!testflg) {
                    eachInnerpackQty = innerpackqty;
                    testflg = true;
                }
                String innerpackFlg = exRows.get("InnerPackFlg");
                if (innerpackFlg != null && innerpackFlg.equalsIgnoreCase("Y")) {
                    hasInnerPack = true;
                } else {
                    hasInnerPack = false;
                }
                for (PoLinebarCode customPoLinebarCode : poLinebarCode) {
                    if (hasInnerPack) {
                        innerPack_SKU_qty.put(customPoLinebarCode, innerpackqty);
                    } else {
                        innerPack_SKU_qty.put(customPoLinebarCode, null);
                    }
                }
                if (null != overage_shortage_flag) {
                    distroType = exRows.get("DistroType");

                }

            }
        }
        Set multiProcessArea = new HashSet();
        for (Map.Entry<PoLinebarCode, String> entry : innerPack_SKU_qty.entrySet()) {
            log.info("Scan PO Number: [{}]", entry.getKey().getPoNbr());
            log.info("Scan PO Line: [{}]", entry.getKey().getPoLineBarCode());
            log.info("Quantity: [{}]", entry.getKey().getOpenQty());
            String processArea = createTotePage.getPoLineDetails(entry.getKey().getPoLineBarCode()).get("processArea");
            log.info("ProcessArea: [{}]", processArea);
            multiProcessArea.add(processArea);

            if (table.getHeaders().contains("Rerelease")) {
                log.info("Rereleasing");
            } else if (!table.getHeaders().contains("Operation") && createToteLoopingcount == 0) {
                stepsContext.put(Context.PO_PROCESS_AREA.name(), processArea, ToContext.RetentionLevel.SCENARIO);
            }

            if (multiProcessArea.size() > 1) {
                createTotePage.clickButton("exit");
                createToteLoopingcount = 0;
            }

            boolean customToteQuantity = entry.getKey().getOpenQty().contains(",");
            stageLocation = customToteQuantity ? entry.getKey().getLocationNbr() : stageLocation;
            List<String> totes = new ArrayList<>(5);
            int open_qty = 0;
            log.info("has Inner Pack ? [{}]", hasInnerPack);
            if (hasInnerPack) {
                createInnerPacksPage.createInnerPacks(innerPack_SKU_qty);
            }
            boolean flag = false;
            List<String> qty = null;
            if (overage_shortage_flag == null) {
                overage_shortage_flag = "";
            }
            if (hasInnerPack) {
                qty = getSplitToteByQuantity_InnerPack(entry.getKey().getOpenQty(), customToteQuantity, entry.getValue());
            } else if (overage_shortage_flag.equalsIgnoreCase("Overage") || overage_shortage_flag.equalsIgnoreCase("Shortage")) {
                open_qty = Integer.parseInt(entry.getKey().getOpenQty());
                if (overage_shortage_flag.equalsIgnoreCase("Overage")) {
                    open_qty = open_qty + OVERAGE_QTY;
                } else if (overage_shortage_flag.equalsIgnoreCase("Shortage")) {
                    open_qty = open_qty - SHORTAGE_QTY;
                }
                qty = getSplitToteByQuantity(String.valueOf(open_qty), customToteQuantity);
            } else {
                qty = getSplitToteByQuantity(entry.getKey().getOpenQty(), customToteQuantity);
            }
            for (String quantity : qty) {
                String.format("PO Number:[%s], PO Line:[%s], ProcessArea:[%s], Quantity: [%s], stageLocation:[%s]", entry.getKey().getPoNbr(), entry.getKey().getPoLineBarCode(), processArea, quantity, stageLocation);
                log.info("Scan Quantity [{}]", quantity);

                if (!flag) {
                    if (createToteLoopingcount == 0) {
                        createTotePage.selectCreateTote();
                        validateToteProcessArea(processArea);
                        //createTotePage.validateScanTotePage(processArea);
                        //Inner pack or Single sku validation starts here
                        createTotePage.selectSingleOrInnerOption(hasInnerPack);
                    }
                    createTotePage.validateScanTotePage(processArea);
                    flag = true;
                }
                // create Tote
                String toteId = createToteId();
                log.info("Tote ID :[{}]", toteId);
                createTotePage.scanToteId(toteId);
                // validate Scan in house UPC
                createTotePage.validateScanInHouseScreen(toteId, processArea);
                createTotePage.scanInHouseUPC(entry.getKey().getPoLineBarCode());
                //createTotePage.validateScanQtyPage(toteId, poLine.getPoLineBarCode(), hasInnerPack);
                createTotePage.enterQty(quantity, hasInnerPack);
                if (!hasInnerPack) {

                    clickButton("End Tote", "Scan Qty");
                }
                log.info("Staging location: " + stageLocation);
                dataStorage.getStoredData().put("stageLocation", stageLocation);
                //Validate the stage Tote and Select location
                if (processArea.equalsIgnoreCase("OSC") || processArea.equalsIgnoreCase("PTC")) {
                    createTotePage.validateAndScanStagingLocation(toteId, entry.getKey().getPoNbr(), "empty", stageLocation);
                }

                totes.add(toteId);
                stageLocationToteMap.put(stageLocation, toteId);
                processAreaToteMap.put(processArea, toteId);
            }
            rcvqty = rcvqty + open_qty;

            receivedqty = String.valueOf(rcvqty);

            totes.sort(Comparator.naturalOrder());

            polines.put(entry.getKey().getPoLineBarCode(), totes);
            createToteLoopingcount = createToteLoopingcount + 1;
            settote(totes);
            if (hasInnerPack) {
                for (Map.Entry<PoLinebarCode, String> toteSKUentry : innerPack_SKU_qty.entrySet()) {
                    polines.put(toteSKUentry.getKey().getPoLineBarCode(), totes);
                }
                break;
            } else {
                totes.sort(Comparator.naturalOrder());
                polines.put(entry.getKey().getPoLineBarCode(), totes);
                createToteLoopingcount = createToteLoopingcount + 1;
            }

            totelst.addAll(totes);
        }
        createTotePage.waitForHeader("CREATE TOTE");
        createTotePage.clickButton("exit");
        log.info("final polines::    [{}]", polines);
        log.info("StageLocation Totes Map::    [{}]", stageLocationToteMap);
        if (table.getHeaders().contains("Rerelease")) {
            stepsContext.put(Context.PO_LINES_TOTE_ID_RERELEASE.name(), polines, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.STAGING_LOCATION_TOTE_MAP_RERELEASE.name(), stageLocationToteMap, ToContext.RetentionLevel.SCENARIO);


        } else if (!table.getHeaders().contains("Rerelease") && !table.getHeaders().contains("Operation")) {
            stepsContext.put(Context.PO_LINES_TOTE_ID.name(), polines, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.STAGING_LOCATION_TOTE_MAP.name(), stageLocationToteMap, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.DISTRO_TYPE.name(), distroType, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.OVERAGE_SHORTAGE_FLAG.name(), overage_shortage_flag, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.RECEIVED_QTY.name(), receivedqty, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.MULTI_PROCESS_AREA.name(), multiProcessArea, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.PROCESS_AREA_TOTE_MAP.name(), processAreaToteMap, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.Tote_List.name(), totelst, ToContext.RetentionLevel.SCENARIO);
        }


        StepDetail.addDetail(" Final PO Lines with POLine Barcode and corresponding Totes:" + polines, true);
        StepDetail.addDetail(" Final Stage Location with corresponding Totes:" + stageLocationToteMap, true);
    }

    /**
     * Create the Tote Id
     *
     * @throws Exception
     */
    private String createToteId() throws Exception {
        return new DataCreateModule().generateContainer("empty", "Tote");
    }

    private void validateToteProcessArea(String processArea) throws Exception {
        createTotePage.clickOnSelectProcessArea();
        createTotePage.selectProcessArea(processArea);
    }

    private List<String> getSplitToteByQuantity(String quantity, Boolean custom) {

        List<String> splitToteQuantity = new ArrayList<>();

        if (!custom) {
            Integer totalQuantity = new Integer(quantity);
            int remainingQ = totalQuantity % TOTE_MAX_CAP;
            if (remainingQ != 0) {
                splitToteQuantity.add(String.valueOf(remainingQ));
            }
            for (int i = 0; i < totalQuantity / TOTE_MAX_CAP; i++) {
                splitToteQuantity.add(String.valueOf(TOTE_MAX_CAP));
            }
        } else {
            splitToteQuantity = Splitter.on(",").splitToList(quantity);

        }

        log.info("Initiating Totes count:[{}] and Quantity of each totes [{}]", splitToteQuantity.size(), splitToteQuantity);
        StepDetail.addDetail("Initiating Totes count:" + splitToteQuantity.size() + " and Quantity of each totes :" + splitToteQuantity, true);
        return splitToteQuantity;
    }

    private List<String> getSplitToteByQuantity_InnerPack(String quantity, Boolean custom, String toteQty) {

        List<String> splitToteQuantity = new ArrayList<>();

        if (!custom) {
            Integer totalQuantity = new Integer(quantity);
            Integer tote_qty = new Integer(toteQty);
            totalQuantity = totalQuantity / tote_qty;

            int remainingQ = totalQuantity % TOTE_MAX_CAP_InnerPack;
            if (remainingQ != 0) {
                splitToteQuantity.add(String.valueOf(remainingQ));
            }
            for (int i = 0; i < totalQuantity / TOTE_MAX_CAP_InnerPack; i++) {
                splitToteQuantity.add(String.valueOf(TOTE_MAX_CAP_InnerPack));
            }
        } else {
            splitToteQuantity = Splitter.on(",").splitToList(quantity);

        }

        log.info("Initiating Totes count:[{}] and Quantity of each totes [{}]", splitToteQuantity.size(), splitToteQuantity);
        StepDetail.addDetail("Initiating Totes count:" + splitToteQuantity.size() + " and Quantity of each totes :" + splitToteQuantity, true);
        return splitToteQuantity;
    }

    @When("user clicks on $button button on $screen screen")
    public void clickButton(String button, String screen) {
        switch (screen) {
            case "Process Area":
                createTotePage.clickButton(button);
                break;
            case "Scan Tote":
                createTotePage.clickButton(button, driver);
                break;
            case "Scan Qty":
                createTotePage.clickButton(button);
                break;
            case "Scan PO Line":
                createTotePage.clickButton(button);
                break;
            case "Stage Tote":
                createTotePage.clickButton(button);
                break;
            case "Stage Pallet":
                createTotePage.clickButton(button);
                break;
        }

    }

    @When("Clean the inventory and activity of the $param location")
    public void cleanUpLocationInventory(String param) throws Exception {
        log.info("Delete Totes For PO: " + String.format(InventoryEndPoint.CONSUME_TOTE, new Object[]{"7221", param, "TestAutomation"}));
        Response response = (Response) RestAssured.given().headers(com.macys.mst.foundationalServices.utils.ExpectedDataProperties.getHeaderProps()).contentType(ContentType.JSON).when().delete(String.format(InventoryEndPoint.CONSUME_TOTE, new Object[]{"7221", param, "TestAutomation"}), new Object[0]);
        log.info("status of the inventory deletion API :" + response.getStatusCode());
        if (200 != response.getStatusCode() && 204 != response.getStatusCode()) {
            Assert.assertTrue(false);
        }
        ActivityDeletion("container", param, "ASSIGNED,OPEN");
        if(CommonUtils.packageFlag)
        	PackageDeletionForthePO();
        else
        	CartonDeletionForthePO();
    }

    @When("Clean all the Open Lane Activities for $parm location")
    public void clean_OpenAndInProgress_Lane_Activities(String param){
        if ("UAT".equalsIgnoreCase(ExecutionConfig.getExecEnv())) {
            ActivityDeletion("LANE", param, "ASSIGNED,OPEN");
        }
    }

    @When("Reset status to $status of container $containerType $barcode")
    public void resetStatusOfContainer(String status, String containerType, String barcode) throws Exception {
        String sql = String.format(SQLResearchInventory.Enable_Container, barcode);
        DBMethods.deleteOrUpdateDataBase(sql, "inventory");

        String url = FileConfig.getInstance().getStringConfigValue("services.hostname")
                + "/inventory-service/containers/7221";
        log.info("Update inventory url: " + url);
        String body = "[{\"barCode\":\"" + barcode + "\",\"containerType\":\"" + containerType
                + "\",\"containerStatusCode\":\"" + status + "\"}]";
        log.info("Update inventory body: " + body);
        CommonUtil.putRequestResponse(url, body);

    }

    @When("Message is parsed")
    public void messageIsParsed() {
        ExpectedDataProperties expdata = new ExpectedDataProperties();
        String messaege = "\\u0002000000365|STOREALLOC|6330|492608024463|1|||||||||||||||||||||||||||||830|40|0|0|N000|0|P|4726842|4519230|||\\u0003";
        String toteContMessage = "\\u0002000000389|TOTECONT|50000022000077000860|492608024432|1|||||||||||||||||||||||||||||830|50|0|N000|P|4726842|4519230|||\\u0003";
        LinkedHashMap<String, String> storeAllocMap = expdata.expectedMessage("STOREALLOC", messaege, false, "PO", false);
        log.info("storeallocmap : " + storeAllocMap.toString());
        LinkedHashMap<String, String> toteContMap = expdata.expectedMessage("TOTECONT", toteContMessage, false, "PO", false);
        log.info("toteContmap : " + toteContMap.toString());
    }

    public void CartonDeletionForthePO() throws Exception {
        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        String cartonInventoryCleanUpSql = String.format(SQLResearchInventory.Cartons_Attribute_Disabled_Status, poLinebarCode.stream().findFirst().orElse(null).getPoNbr());
        List<String> cartons = DBMethods.getDBValueInList(cartonInventoryCleanUpSql, "inventory");
        log.info("cartons list to be deleted: " + cartons.toString());

        if (!cartons.isEmpty()) {
            for (int i = 0; i < cartons.size(); i++) {
                log.info("Delete path: " + String.format(InventoryEndPoint.CONSUME_TOTE, new Object[]{"7221", cartons.get(i), "TestAutomation"}));
                Response response = (Response) RestAssured.given().headers(com.macys.mst.foundationalServices.utils.ExpectedDataProperties.getHeaderProps()).contentType(ContentType.JSON).when().delete(String.format(InventoryEndPoint.CONSUME_TOTE, new Object[]{"7221", cartons.get(i), "TestAutomation"}), new Object[0]);
                log.info("status of the inventory deletion API :" + response.getStatusCode());
                if (200 != response.getStatusCode() && 204 != response.getStatusCode()) {
                    Assert.assertTrue(false);
                }
            }
        }
    }
    
    public void PackageDeletionForthePO() throws Exception {
        String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
        String poRcptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());
        String packageCleanUpSql = String.format(SQLPackage.PackagesForPO_SQL, poNbr,poRcptNbr);
        List<String> packageIDs = DBMethods.getDBValueInList(packageCleanUpSql, "inventory");
        log.info("PackageID list to be deleted: " + packageIDs.toString());

        if (!packageIDs.isEmpty()) {
            for (int i = 0; i < packageIDs.size(); i++) {
             //   String consume_Pkg = commonUtils.getEnvConfigValue("packageService.deletePackage");
                String CONSUME_PACKAGE_endpoint = commonUtils.getUrl("packageService.deletePackage").replace("#packageID", packageIDs.get(i));
            //    log.info("Delete path: " + String.format(PackageEndPoint.CONSUME_PACKAGE, new Object[]{"7221", packageIDs.get(i), "TestAutomation"}));
                log.info("Delete path: ", CONSUME_PACKAGE_endpoint);
            //    Response response = (Response) RestAssured.given().headers(com.macys.mst.foundationalServices.utils.ExpectedDataProperties.getHeaderProps()).contentType(ContentType.JSON).when().delete(String.format(PackageEndPoint.CONSUME_PACKAGE, new Object[]{"7221", packageIDs.get(i), "TestAutomation"}), new Object[0]);
                Response response = (Response) RestAssured.given().headers(com.macys.mst.foundationalServices.utils.ExpectedDataProperties.getHeaderProps()).contentType(ContentType.JSON).when().delete(CONSUME_PACKAGE_endpoint);
                log.info("status of the inventory deletion API :" + response.getStatusCode());
                if (204 != response.getStatusCode()) {
                    Assert.assertTrue(false);
                }
            }
        }
    }

    public void ActivityDeletion(String content, String paramaterValue, String ActivityStatus) {
        String containerGetActivity = "";
        String[] Statuses = ActivityStatus.split(",");
        if (content.equals("container")) {
            containerGetActivity = String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH_RELEASELANE, paramaterValue, Statuses[0]);
        }
        if (content.equals("upc")) {
            containerGetActivity = String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH_PACKAWAY, paramaterValue, Statuses[0]);
        }
        if (content.equalsIgnoreCase("LANE")) {
            containerGetActivity = String.format(WsmEndpoint.WSM_ACTIVITY, content, Statuses[0]);
        }
        log.info("GetAssignedActivitiesEndpoint :" + containerGetActivity);
        String AssignedActivitiesJson = CommonUtil.getRequestResponse(containerGetActivity);
        List<Integer> assignedActivityIdList = new ArrayList<Integer>();
        if (!StringUtils.isBlank(AssignedActivitiesJson)) {
            JSONArray ActivitiesArray = new JSONArray(AssignedActivitiesJson);
            for (int i = 0; i < ActivitiesArray.length(); i++) {
                assignedActivityIdList.add(ActivitiesArray.getJSONObject(i).getInt("id"));
            }

            JSONArray jsonArrayUpdateActivity = new JSONArray();
            for (int j = 0; j < assignedActivityIdList.size(); j++) {
                JSONObject ActivityJson = new JSONObject();
                ActivityJson.put("id", assignedActivityIdList.get(j));
                ActivityJson.put("status", "OPEN");
                jsonArrayUpdateActivity.put(j, ActivityJson);
            }
            log.info("UpdateEndpoint :" + WsmEndpoint.WSM_SERVICE);
            log.info("UpdateBody :" + jsonArrayUpdateActivity.toString());
            CommonUtil.putRequestResponse(WsmEndpoint.WSM_SERVICE, jsonArrayUpdateActivity.toString());

        }
        if (content.equals("container")) {
            containerGetActivity = String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH_RELEASELANE, paramaterValue, Statuses[1]);
        }
        if (content.equals("upc")) {
            containerGetActivity = String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH_PACKAWAY, paramaterValue, Statuses[1]);
        }
        if (content.equalsIgnoreCase("LANE")) {
            containerGetActivity = String.format(WsmEndpoint.WSM_ACTIVITY, content, Statuses[1]);
        }
        log.info("GetAssignedActivitiesEndpoint :" + containerGetActivity);
        String OpenActivitiesJson = CommonUtil.getRequestResponse(containerGetActivity);
        List<Integer> openActivityIdList = new ArrayList<Integer>();
        if (!StringUtils.isBlank(OpenActivitiesJson)) {
            JSONArray ActivitiesArray = new JSONArray(OpenActivitiesJson);
            for (int i = 0; i < ActivitiesArray.length(); i++) {
                openActivityIdList.add(ActivitiesArray.getJSONObject(i).getInt("id"));
            }
            log.info("deleteRequest :" + WsmEndpoint.WSM_SERVICE + "/" + CommonUtil.getValuesFromList(openActivityIdList));
            Response response = (Response) RestAssured.given().contentType(ContentType.JSON).headers(ExpectedDataProperties.getHeaderProps()).when().delete(WsmEndpoint.WSM_SERVICE + "/" + CommonUtil.getValuesFromList(openActivityIdList));
        }
    }


    @Then("validate inventory is created and lane is associated with all these totes")
    public void validateTheStagingLaneAssignedToCurrentPO() {
        Map<String, List<String>> poLinesToteId = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
        if (!poLinesToteId.isEmpty()) {
            List<String> toteIds = poLinesToteId.values().stream().flatMap(List::stream).collect(Collectors.toList());
            toteIds.forEach(toteId -> {
                log.info("validate inventory is created and lane is associated with all these totes: [{}]", toteId);
                createTotePage.validateTheStagingLaneAssignedToCurrentPO(toteId);
            });

        } else {
            Assert.fail("Get PoLines ToteIds is empty");
        }


    }

    public static Boolean hasInnerPack(String poLineBarcode) {
        String response = CommonUtils.getRequestResponse(String.format(PO4WallEndPoint.PO4WALL_GET_POLINE_PCK, poLineBarcode));
        if (StringUtils.isNotEmpty(response)) {
            JSONArray jsonArray = new JSONArray(response);
            String packType = (String) ((JSONObject) jsonArray.get(0)).get("packType");
            return "IP".equalsIgnoreCase(packType);
        }
        return false;
    }

    @When("Cleaning up the activity for the $param packawaySort")
    public void cleanWsmActivities(String param) {
        List<PoLinebarCode> poLinebarCode = (List<PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        for (PoLinebarCode poLine : poLinebarCode) {
            ActivityDeletion("upc", poLine.getSKU(), "ASSIGNED,OPEN");
        }
    }

    public List<String> gettote() {
        return this.totes;
    }

    public void settote(List tote) {
        this.totes = tote;
    }

    @When("cleanup exisiting Totes")
    public void cleanexistingTotes(){
        cleanUpAnyExistingTotes();
    }

    @When("totes are created without staging location")
    public void createTotes_without_stagingLocation() throws Exception {
        List<String> totes = new ArrayList<>();
        List<PoLinebarCode> poLinebarCode = (List<PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        loginCreateTote();
        createTotePage.selectCreateTote();
        for (PoLinebarCode poLine : poLinebarCode) {
            String processArea = createTotePage.getPoLineDetails(poLine.getPoLineBarCode()).get("processArea");
            validateToteProcessArea(processArea);
            createTotePage.selectSingleOrInnerOption(hasInnerPack);
            String toteId = createToteId();
            log.info("Tote ID :[{}]", toteId);
            createTotePage.scanToteId(toteId);
            createTotePage.scanInHouseUPC(poLine.getPoLineBarCode());
            boolean customToteQuantity =poLine.getOpenQty().contains(",");
            List<String> qty = getSplitToteByQuantity(poLine.getOpenQty(), customToteQuantity);
            for (String quantity : qty) {
                createTotePage.enterQty(quantity, hasInnerPack);
                break;
            }
            totes.add(toteId);
            stepsContext.put(Context.Tote_List.name(), totes, ToContext.RetentionLevel.SCENARIO);
            break;
        }
        createTotePage.clickButton("End Tote");
        createTotePage.clickButton("exit");
    }



}
