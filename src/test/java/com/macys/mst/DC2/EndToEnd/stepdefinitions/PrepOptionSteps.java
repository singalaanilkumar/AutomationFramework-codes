package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.configuration.InventoryEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.DC2.EndToEnd.configuration.WsmEndpoint;
import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLPo4Walls;
import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.model.InventorySnapshot;
import com.macys.mst.DC2.EndToEnd.model.WSMActivity;
import com.macys.mst.DC2.EndToEnd.pageobjects.PrepOptionPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class PrepOptionSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();

    private static final String PUT_TO_STORE_DIVERT = "PH12341";

    PrepOptionPage prepOptionPage = PageFactory.initElements(driver, PrepOptionPage.class);
    private static final String NEW = "N";
    private static final String REGULAR = "R";
    private static final String BIN = "P";
    
    CommonUtils commonUtils = new CommonUtils();
    
    @Getter
    @Setter
    private String prepToteID, splitQuantity, splitToteID, splitSku, splitBinID;
    @Getter
    @Setter
    private int originalQuantity, toteDisplayedunits, binDisplayedunits;
    @Getter
    @Setter
    private Map<String, String> poLineDStringStringMap;

    @Setter
    @Getter
    private List<String> toteIds;
    public long TestNGThreadID = Thread.currentThread().getId();

    private StepsContext stepsContext;
    private StepsDataStore dataStorage = StepsDataStore.getInstance();

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    public PrepOptionSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @Then("Clean the cache")
    public void cleanTheCache() throws InterruptedException {
        prepOptionPage.cleanCache();
    }

    @Given("Get Inventory Details")
    public void getInventoryDetailForPrep() {

        try {
            Map<String, List<String>> barcodeWithTotes = new HashMap<>();
            String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
            String rcptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());

            String query = String.format(SQLPo4Walls.GET_SKU_BARCODE, poNbr, rcptNbr);

            Map<String, String> skuUpcBarcode = DBMethods.getDBValuesInMap(query,"pofourwalls");
            String inventoryResponses = CommonUtils.getRequestResponse(String.format(InventoryEndPoint.GET_INVENTORY_PORECEIPT_PONBR, poNbr, rcptNbr));
            InventoryContainer[] inventoryContainers = CommonUtils.getClientResponse(inventoryResponses, new TypeReference<InventoryContainer[]>() {
            });

            List<InventorySnapshot> inventorySnapshotList = Arrays.asList(inventoryContainers).stream().map(InventoryContainer::getInventorySnapshotList).flatMap(List::stream).collect(Collectors.toList());

            Map<String, List<String>> skuUpcWithTotes = inventorySnapshotList.stream()
                    .collect(Collectors.groupingBy(InventorySnapshot::getItem, Collectors.mapping(InventorySnapshot::getContainer, Collectors.toList())));


            skuUpcWithTotes.forEach((skupc, totes) -> {
                if (skuUpcBarcode.containsKey(skupc)) {
                    barcodeWithTotes.put(skuUpcBarcode.get(skupc), totes);
                } else {
                    Assert.fail("Unable to find the SKU Barcode" + skupc);
                }

            });

            log.info("Prep: SkuBarcode with list of Totes", barcodeWithTotes);
            stepsContext.put(Context.PO_LINES_TOTE_ID.name(), barcodeWithTotes, ToContext.RetentionLevel.SCENARIO);

        } catch (Exception e) {
            log.error("Exception in getInventoryDetailForPrep", e);
            Assert.fail("Exception in getInventoryDetailForPrep", e);
        }

    }


    @When("VAS/PREP is performed for totes in PREP area")
    public void prepOption() throws Exception {
        boolean hasInnerPack = CreateToteSteps.hasInnerPack;
        final boolean hasInnerPackModified = hasInnerPack;
        selectOptionFromMenu();
        Map<String, String> newStoreOldNewToteMap = new HashMap<>(3);
        Map<String, String> regularStoreOldNewToteMap = new HashMap<>(3);
        Map<String, String> packOldNewToteMap = new HashMap<>(3);

        if (hasInnerPack) {
            Map<String, List<String>> barcodeWithTotes = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
            barcodeWithTotes.forEach((poLineBarcode, toteList) -> {
                setPoLineDStringStringMap(getPoLineDetails(poLineBarcode));
            });
            List<String> totes = barcodeWithTotes.values().stream().flatMap(List::stream).collect(Collectors.toList());
            setToteIds(totes);
            log.info("totes :{}", totes);
            Set<String> toteSet = new TreeSet<>(totes);
            log.info("toteSet :{}", toteSet);
            for (String eachtote : toteSet) {
                {
                    // call Inventory service
                    String inventoryResponse = CommonUtils.getRequestResponse(ReadHostConfiguration.GET_INVENTORY_SERVICE_URL.value().replace("{totebarcode}", eachtote));
                    InventoryContainer inventoryContainer = CommonUtils.getClientResponse(inventoryResponse, new TypeReference<InventoryContainer>() {
                    });
                    inventoryContainer.getInventorySnapshotList().forEach(inventory -> {
                        // set original Quantity
                        setOriginalQuantity(inventory.getQuantity());
                    });
                    // set Tote id
                    setPrepToteID(eachtote);

                    // Get WSMActivities based on Tote ID
                    List<WSMActivity> wsmActivities = getWSMActivities(eachtote);
                    boolean binFlag = wsmActivities.stream().filter(f -> BIN.equalsIgnoreCase(f.getSubType())).count() > 0 ? true : false;

                    boolean newFlag = wsmActivities.stream().filter(f -> NEW.equalsIgnoreCase(f.getSubType())).count() > 0 ? true : false;

                    log.info("WSM Activities:{}", wsmActivities);
                    if (!hasInnerPackModified) {
                        validateScanToteScreen("Scan Tote");
                    }

                    scanTote("valid", "Tote", "scan tote");
                    //    validateScanToteScreen("prep");
                    String prepAction = poLineDStringStringMap.get("prep1");
                    if (StringUtils.isNotBlank(prepAction) && !prepAction.contains("*")) {
                        verifyPrepCheckbox("Complete");
                    }
                    clickButton("Complete");

                    if (wsmActivities.size() == 1) {

                        if (BIN.equalsIgnoreCase(wsmActivities.get(0).getSubType())) {
                            setSplitSku(wsmActivities.get(0).getUpc());
                            log.info("Individual BIN Activity::");
                            setBinDisplayedunits(wsmActivities.get(0).getQuantity());
                            validateScanToteScreen("split bin");
                            scanTote("empty", "BINBOX", "split bin");
                            validateScanToteScreen("scan bin sku");
                            scanSku("valid");
                            validateScanToteScreen("bin quantity");
                            scanQuantity("displayed", "Bin");
                            verifyUserMessage("split bin");
                            clickButton("CLOSE");
                            validateInventoryForContainers("created", "split bin");
                            packOldNewToteMap.put(wsmActivities.get(0).getContainerId(), getSplitBinID());
                        } else {
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("original tote");
                            }
                            if (NEW.equalsIgnoreCase(wsmActivities.get(0).getSubType())) {
                                if (!hasInnerPackModified) {
                                    verifyPrepCheckbox("Done");
                                }
                            }
                            prepOptionPage.selectPrepCheckBox();
                            TimeUnit.SECONDS.sleep(5);
                            verifyUserMessage("prep tote");
                            clickButton("CLOSE");

                            if (NEW.equalsIgnoreCase(wsmActivities.get(0).getSubType())) {
                                newStoreOldNewToteMap.put(wsmActivities.get(0).getContainerId(), getPrepToteID());
                            } else if (REGULAR.equalsIgnoreCase(wsmActivities.get(0).getSubType())) {
                                regularStoreOldNewToteMap.put(wsmActivities.get(0).getContainerId(), getPrepToteID());
                            }

                        }
                        continue;
                    }

                    for (WSMActivity activity : wsmActivities) {
                        setSplitSku(activity.getUpc());
                        if (NEW.equalsIgnoreCase(activity.getSubType())) {
                            log.info("::NEW Activity::");
                            setToteDisplayedunits(activity.getQuantity());
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("Split Tote");
                            }
                            scanTote("empty", "Tote", "split tote");
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("Scan tote Sku");
                            }
                            if (!hasInnerPackModified) {
                                scanSku("valid");
                                validateScanToteScreen("tote quantity");
                            }
                            verifyPrepCheckbox("Unit");
                            scanQuantity("displayed", "tote");
                            //validateAlertMessage("Did you complete the prep required for New Store?");
                            //clickButton("OK");
                            verifyUserMessage("split tote");
                            clickButton("CLOSE");
                            if (!hasInnerPackModified) {
                                validateInventoryForContainers("created", "split tote");
                            }
                            newStoreOldNewToteMap.put(activity.getContainerId(), getSplitToteID());
                        } else if (BIN.equalsIgnoreCase(activity.getSubType())) {
                            log.info("::BIN Activity::");
                            setBinDisplayedunits(activity.getQuantity());
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("split bin");
                            }
                            scanTote("empty", "BINBOX", "split bin");
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("scan bin sku");
                            }
                            scanSku("valid");
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("bin quantity");
                            }
                            scanQuantity("displayed", "Bin");
                            verifyUserMessage("split bin");
                            clickButton("CLOSE");
                            if (!hasInnerPackModified) {
                                validateInventoryForContainers("created", "split bin");
                            }
                            packOldNewToteMap.put(activity.getContainerId(), getSplitBinID());
                        } else if (REGULAR.equalsIgnoreCase(activity.getSubType())) {
                            log.info("::REGULAR Activity::");
                            setToteDisplayedunits(activity.getQuantity());
                            if (binFlag) {
                                if (!hasInnerPackModified) {
                                    validateScanToteScreen("split tote regular");
                                }
                                scanTote("empty", "Tote", "split tote");
                                if (!hasInnerPackModified) {
                                    validateScanToteScreen("Scan tote Sku");
                                }
                                scanSku("valid");
                                if (!hasInnerPackModified) {
                                    validateScanToteScreen("tote quantity");
                                }
                                //verifyPrepCheckbox("Unit");
                                scanQuantity("displayed", "tote");
                                verifyUserMessage("split tote");
                                clickButton("CLOSE");
                                if (!hasInnerPackModified) {
                                    validateInventoryForContainers("created", "split tote");
                                }
                                regularStoreOldNewToteMap.put(activity.getContainerId(), getSplitToteID());

                            } else {
                                if (!hasInnerPackModified) {
                                    validateScanToteScreen("original tote");
                                }
                                clickButton("done");
                                verifyUserMessage("prep tote");
                                clickButton("CLOSE");
                                if (!hasInnerPackModified) {
                                    validateInventoryForContainers("created", "prep tote");
                                }
                                regularStoreOldNewToteMap.put(activity.getContainerId(), getPrepToteID());
                            }


                        }
                        setOriginalQuantity(getOriginalQuantity() - activity.getQuantity());
                    }
                    if (binFlag) {
                        if (!hasInnerPackModified) {
                            validateInventoryForContainers("deleted", "prep tote");
                        }
                    }
                }
            }
            clickButton("Exit");

            Map<String, Map<String, String>> prepValues = new HashMap<>(3);
            prepValues.put("NEW", newStoreOldNewToteMap);
            prepValues.put("REGULAR", regularStoreOldNewToteMap);
            log.info("final prep Info: {}", prepValues);

            Map<String, Map<String, String>> binValues = new HashMap<>(3);
            binValues.put("BIN", packOldNewToteMap);
            log.info("final BIN Info: {}", binValues);

            StepDetail.addDetail("Successfully finished NEW and Regular Prep" + prepValues, true);
            StepDetail.addDetail("Successfully finished Packaway Prep" + binValues, true);

            stepsContext.put(Context.PREP_STORE_TOTE_MAP.name(), prepValues, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.BIN_ID_MAP.name(), binValues, ToContext.RetentionLevel.SCENARIO);


        } else {
            Map<String, List<String>> barcodeWithTotes = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
            barcodeWithTotes.forEach((poLineBarcode, toteList) -> {
                setPoLineDStringStringMap(getPoLineDetails(poLineBarcode));
                toteList.forEach(toteId -> {
                    // call Inventory service
                    String inventoryResponse = CommonUtils.getRequestResponse(ReadHostConfiguration.GET_INVENTORY_SERVICE_URL.value().replace("{totebarcode}", toteId));
                    InventoryContainer inventoryContainer = CommonUtils.getClientResponse(inventoryResponse, new TypeReference<InventoryContainer>() {
                    });
                    inventoryContainer.getInventorySnapshotList().forEach(inventory -> {
                        // set original Quantity
                        setOriginalQuantity(inventory.getQuantity());
                    });
                    // set Tote id
                    setPrepToteID(toteId);

                    // Get WSMActivities based on Tote ID
                    List<WSMActivity> wsmActivities = getWSMActivities(toteId);
                    boolean binFlag = wsmActivities.stream().filter(f -> BIN.equalsIgnoreCase(f.getSubType())).count() > 0 ? true : false;

                    boolean newFlag = wsmActivities.stream().filter(f -> NEW.equalsIgnoreCase(f.getSubType())).count() > 0 ? true : false;

                    log.info("WSM Activities:{}", wsmActivities);
                    if (!hasInnerPackModified) {
                        validateScanToteScreen("Scan Tote");
                    }

                    scanTote("valid", "Tote", "scan tote");
                    if (!hasInnerPackModified) {
                        validateScanToteScreen("prep");
                        String prepAction = poLineDStringStringMap.get("prep1");
                        if (StringUtils.isNotBlank(prepAction)) {
                            verifyPrepCheckbox("Complete");
                        }
                    }
                    clickButton("Complete");

                    if (wsmActivities.size() == 1) {

                        if (BIN.equalsIgnoreCase(wsmActivities.get(0).getSubType())) {
                            setSplitSku(wsmActivities.get(0).getUpc());
                            log.info("Individual BIN Activity::");
                            setBinDisplayedunits(wsmActivities.get(0).getQuantity());
                            validateScanToteScreen("split bin");
                            scanTote("empty", "BINBOX", "split bin");
                            validateScanToteScreen("scan bin sku");
                            scanSku("valid");
                            validateScanToteScreen("bin quantity");
                            scanQuantity("displayed", "Bin");
                            verifyUserMessage("split bin");
                            clickButton("CLOSE");
                            validateInventoryForContainers("created", "split bin");
                            packOldNewToteMap.put(wsmActivities.get(0).getContainerId(), getSplitBinID());
                        } else {
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("original tote");
                            }
                            if (NEW.equalsIgnoreCase(wsmActivities.get(0).getSubType())) {
                                if (!hasInnerPackModified) {
                                    verifyPrepCheckbox("Done");
                                }
                            }
                            clickButton("done");
                            verifyUserMessage("prep tote");
                            clickButton("CLOSE");

                            if (NEW.equalsIgnoreCase(wsmActivities.get(0).getSubType())) {
                                newStoreOldNewToteMap.put(wsmActivities.get(0).getContainerId(), getPrepToteID());
                            } else if (REGULAR.equalsIgnoreCase(wsmActivities.get(0).getSubType())) {
                                regularStoreOldNewToteMap.put(wsmActivities.get(0).getContainerId(), getPrepToteID());
                            }

                        }
                        return;

                    }

                    for (WSMActivity activity : wsmActivities) {
                        setSplitSku(activity.getUpc());
                        if (NEW.equalsIgnoreCase(activity.getSubType())) {
                            log.info("::NEW Activity::");
                            setToteDisplayedunits(activity.getQuantity());
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("Split Tote");
                            }
                            scanTote("empty", "Tote", "split tote");
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("Scan tote Sku");
                            }
                            if (!hasInnerPackModified) {
                                scanSku("valid");
                                validateScanToteScreen("tote quantity");
                            }
                            verifyPrepCheckbox("Unit");
                            scanQuantity("displayed", "tote");
                            //validateAlertMessage("Did you complete the prep required for New Store?");
                            //clickButton("OK");
                            verifyUserMessage("split tote");
                            clickButton("CLOSE");
                            if (!hasInnerPackModified) {
                                validateInventoryForContainers("created", "split tote");
                            }
                            newStoreOldNewToteMap.put(activity.getContainerId(), getSplitToteID());
                        } else if (BIN.equalsIgnoreCase(activity.getSubType())) {
                            log.info("::BIN Activity::");
                            setBinDisplayedunits(activity.getQuantity());
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("split bin");
                            }
                            scanTote("empty", "BINBOX", "split bin");
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("scan bin sku");
                            }
                            scanSku("valid");
                            if (!hasInnerPackModified) {
                                validateScanToteScreen("bin quantity");
                            }
                            scanQuantity("displayed", "Bin");
                            verifyUserMessage("split bin");
                            clickButton("CLOSE");
                            if (!hasInnerPackModified) {
                                validateInventoryForContainers("created", "split bin");
                            }
                            packOldNewToteMap.put(activity.getContainerId(), getSplitBinID());
                        } else if (REGULAR.equalsIgnoreCase(activity.getSubType())) {
                            log.info("::REGULAR Activity::");
                            setToteDisplayedunits(activity.getQuantity());
                            if (binFlag) {
                                if (!hasInnerPackModified) {
                                    validateScanToteScreen("split tote regular");
                                }
                                scanTote("empty", "Tote", "split tote");
                                if (!hasInnerPackModified) {
                                    validateScanToteScreen("Scan tote Sku");
                                }
                                scanSku("valid");
                                if (!hasInnerPackModified) {
                                    validateScanToteScreen("tote quantity");
                                }
                                //verifyPrepCheckbox("Unit");
                                scanQuantity("displayed", "tote");
                                verifyUserMessage("split tote");
                                clickButton("CLOSE");
                                if (!hasInnerPackModified) {
                                    validateInventoryForContainers("created", "split tote");
                                }
                                regularStoreOldNewToteMap.put(activity.getContainerId(), getSplitToteID());

                            } else {
                                if (!hasInnerPackModified) {
                                    validateScanToteScreen("original tote");
                                }
                                clickButton("done");
                                verifyUserMessage("prep tote");
                                clickButton("CLOSE");
                                if (!hasInnerPackModified) {
                                    validateInventoryForContainers("created", "prep tote");
                                }
                                regularStoreOldNewToteMap.put(activity.getContainerId(), getPrepToteID());
                            }


                        }
                        setOriginalQuantity(getOriginalQuantity() - activity.getQuantity());
                    }
                    if (binFlag) {
                        if (!hasInnerPackModified) {
                            validateInventoryForContainers("deleted", "prep tote");
                        }
                    }
                });
            });
            clickButton("Exit");

            Map<String, Map<String, String>> prepValues = new HashMap<>(3);
            prepValues.put("NEW", newStoreOldNewToteMap);
            prepValues.put("REGULAR", regularStoreOldNewToteMap);
            log.info("final prep Info: {}", prepValues);

            Map<String, Map<String, String>> binValues = new HashMap<>(3);
            binValues.put("BIN", packOldNewToteMap);
            log.info("final BIN Info: {}", binValues);

            StepDetail.addDetail("Successfully finished NEW and Regular Prep" + prepValues, true);
            StepDetail.addDetail("Successfully finished Packaway Prep" + binValues, true);

            stepsContext.put(Context.PREP_STORE_TOTE_MAP.name(), prepValues, ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.BIN_ID_MAP.name(), binValues, ToContext.RetentionLevel.SCENARIO);
        }

    }
    
    
    @When("VAS/PREP is performed for wave $waveCount in PREP area")
    public void prepOptionForWave(String waveCount) throws Exception {

       selectOptionFromMenu();
       TimeUnit.SECONDS.sleep(5);
       String waveNumber = (String) dataStorage.getStoredData().get(waveCount+"Number");
       

	        Map<String, String> newStoreOldNewToteMap = new HashMap<>(3);
	        Map<String, String> regularStoreOldNewToteMap = new HashMap<>(3);
	        Map<String, String> packOldNewToteMap = new HashMap<>(3);
	
	        List<String> binList = new ArrayList<String>();
			String GETCallEndpoint = commonUtils.getUrl("WSM.getActivities");
			String GETQueryParams = "waveNumber:#waveNbr,type:SPLIT,status:OPEN";
	
			Map<String, String> processedGetQP = commonUtils.getParamsToMap(GETQueryParams.replace("#waveNbr", waveNumber));
		
			Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint,processedGetQP).asResponse();
			if(200==GETResponse.statusCode()){
				JsonPath containers = new JsonPath(GETResponse.asString());
				binList.addAll(containers.getList("containerId",String.class));
			}
			binList = binList.stream().distinct().collect(Collectors.toList());
			log.info("BINBOX "+binList);

	        binList.forEach(binID -> {
	            // call Inventory service
	            String inventoryResponse = CommonUtils.getRequestResponse(ReadHostConfiguration.GET_INVENTORY_SERVICE_URL.value().replace("{totebarcode}", binID));
	            JsonPath inventoryPath = new JsonPath(inventoryResponse);
	            setOriginalQuantity(inventoryPath.get("inventorySnapshotList[0].quantity"));
	            // set BIN ID
	            setPrepToteID(binID);
	            List<WSMActivity> wsmActivities = getWSMActivitiesForBIN(binID);
	            log.info("WSM Activities:{}", wsmActivities);
	            boolean binFlag = wsmActivities.stream().filter(f -> BIN.equalsIgnoreCase(f.getSubType())).count() > 0 ? true : false;
	            
	            validateScanToteScreen("Scan Container");
	            scanContainer("valid", "Tote", "scan container");
	            // Clarity needed when this will be displayed
	            clickButton("Complete");            
	            
	            for (WSMActivity activity : wsmActivities) {
	            	setSplitSku(activity.getUpc());
	            	if(BIN.equalsIgnoreCase(activity.getSubType())) {
	                    log.info("::BIN Activity::");
	                    setBinDisplayedunits(activity.getQuantity());
	                    validateScanToteScreen("split bin wave");
	                    scanTote("empty", "BINBOX", "split bin");
	                    validateScanToteScreen("scan bin sku");
	                    scanSku("valid");
	                    validateScanToteScreen("bin quantity wave");
	                    scanQuantity("displayed", "Bin");
	                    verifyUserMessage("split bin");
	                    clickButton("CLOSE");
	                    validateInventoryForContainers("created", "split bin");
	                    packOldNewToteMap.put(activity.getContainerId(), getSplitBinID());
	            	 } else if (REGULAR.equalsIgnoreCase(activity.getSubType())) {
	                    log.info("::REGULAR Activity::");
	                    setToteDisplayedunits(activity.getQuantity());
	                    if (binFlag) {
	                        validateScanToteScreen("split bin regular");
	                        scanTote("empty", "Tote", "split tote");
	                        validateScanToteScreen("Scan tote Sku");
	                        scanSku("valid");
	                        validateScanToteScreen("tote quantity wave");
	                        //verifyPrepCheckbox("Unit");
	                        scanQuantity("displayed", "tote");
	                        verifyUserMessage("split tote");
	                        clickButton("CLOSE");
	                        validateInventoryForContainers("created", "split tote");
	                        regularStoreOldNewToteMap.put(activity.getContainerId(), getSplitToteID());
	                        
		                  } else {
		                    validateScanToteScreen("original tote");
		                    clickButton("done");
		                    verifyUserMessage("prep tote");
		                    clickButton("CLOSE");
		                    validateInventoryForContainers("created", "prep tote");
		                    regularStoreOldNewToteMap.put(activity.getContainerId(), getPrepToteID());
		                    }
	            	 }
	                 setOriginalQuantity(getOriginalQuantity() - activity.getQuantity());
	            }
	            if (binFlag) {
	                validateInventoryForContainers("deleted", "prep tote");
	            }
	        	
	        });
	            
	 
        clickButton("Exit");

        Map<String, Map<String, String>> prepValues = new HashMap<>(3);
        prepValues.put("NEW", newStoreOldNewToteMap);
        prepValues.put("REGULAR", regularStoreOldNewToteMap);
        log.info("final prep Info: {}", prepValues);

        Map<String, Map<String, String>> binValues = new HashMap<>(3);
        binValues.put("BIN", packOldNewToteMap);
        log.info("final BIN Info: {}", binValues);

        StepDetail.addDetail("Successfully finished NEW and Regular Prep" + prepValues, true);
        StepDetail.addDetail("Successfully finished Packaway Prep" + binValues, true);
        
        dataStorage.getStoredData().put(waveCount+"prepValues", prepValues);
        dataStorage.getStoredData().put(waveCount+"binValues", binValues);

    }


    @Then("TOTECONT message will be sent to Pyramid")
    public void verifyToteContMessage() throws InterruptedException {
        PrintTicketSteps printTicketSteps = new PrintTicketSteps(stepsContext);
        TimeUnit.SECONDS.sleep(20);
        Map<String, Map<String, String>> prepStoreToteMap = (Map<String, Map<String, String>>) stepsContext.get(Context.PREP_STORE_TOTE_MAP.name());
        List<String> toteIds = prepStoreToteMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
        toteIds.forEach(toteId -> {
            CommonUtils.verifyMsgServiceResponse("PrepComplete", "TOTECONT", toteId);
                 printTicketSteps.validateMHEmessageFromDB("TOTECONT", toteId);
        });
    }

    @Then("CONTDIVERT Message route to P2S")
    public void sendContDivertMessage() {

        Map<String, Map<String, String>> prepStoreToteMap = (Map<String, Map<String, String>>) stepsContext.get(Context.PREP_STORE_TOTE_MAP.name());
        List<String> toteIds = prepStoreToteMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
        toteIds.forEach(toteId -> {
            CommonUtils.sendContDivertMessage(toteId, PUT_TO_STORE_DIVERT);
        });

    }

    @Then("WSM activities for the preping are completed")
    public void verifyWSMPrepActivity() {
        Map<String, Map<String, String>> prepStoreToteMap = (Map<String, Map<String, String>>) stepsContext.get(Context.PREP_STORE_TOTE_MAP.name());
        List<String> toteIds = prepStoreToteMap.values().stream().flatMap(m -> m.keySet().stream()).collect(Collectors.toList());
        toteIds.forEach(toteId -> {
            String wsmActivitiesResponse = CommonUtils.getRequestResponse(String.format(WsmEndpoint.WSM_SERVICE_SEARCH, toteId));
            if (StringUtils.isNotBlank(wsmActivitiesResponse)) {
                for (Object wsmActivity : new JSONArray(wsmActivitiesResponse)) {
                    JSONObject activity = (JSONObject) wsmActivity;
                    String containerId = activity.getString("containerId");
                    String status = activity.getString("status");
                    log.info("Tote Id: {} and Status: {}", containerId, status);
                    org.junit.Assert.assertTrue("Prep Activity Completed", StringUtils.equalsIgnoreCase(status, "COMPLETED"));
                }

            } else {
                org.junit.Assert.assertTrue("No WSM Activities for tote" + toteId, false);
            }
        });

    }

    public List<WSMActivity> getWSMActivities(String toteId) {
        List<WSMActivity> wsmActivities = new ArrayList<>(3);
        String wsmActivitiesResponse = CommonUtils.getRequestResponse(String.format(WsmEndpoint.WSM_SERVICE_SEARCH, toteId));
        log.info("wsmActivitiesResponse: {}", wsmActivitiesResponse);
        if (StringUtils.isNotBlank(wsmActivitiesResponse)) {
            for (Object wsmActivity : new JSONArray(wsmActivitiesResponse)) {
                JSONObject activity = (JSONObject) wsmActivity;
                String containerId = activity.getString("containerId");
                String status = activity.getString("status");
                Integer qty = activity.getInt("qty");
                JSONObject attributes = activity.getJSONObject("attributes");
                String subtype = attributes.getString("subType");
                log.info("Activity Tote subtype:[{}], containerId:[{}], status:[{}], Quantity:[{}]",
                        new Object[]{subtype, containerId, status, qty});
                if (StringUtils.equals(containerId, toteId) && StringUtils.equalsIgnoreCase(status, "OPEN")) {
                    wsmActivities.add(new WSMActivity(activity.getBigInteger("id"), subtype, qty, activity.getString("containerType"),
                            containerId, activity.getString("upc"), attributes.getInt("sequence")));
                }
            }

        } else {
            org.junit.Assert.assertTrue("No WSM Activities for tote" + toteId, false);
        }
        wsmActivities.sort(Comparator.comparing(WSMActivity::getSequence));
        return wsmActivities;

    }

    public void verifyPrepCheckbox(String button) {
        prepOptionPage.verifyPrepCheckbox(getPoLineDStringStringMap(), button);
    }

    public void selectOptionFromMenu() {
        prepOptionPage.buttonClick();
    }

    public void scanTote(String param, String container, String screen) {
        try {
            if (screen.equalsIgnoreCase("scan tote")) {
                prepOptionPage.scanToteId(getPrepToteID());
            } else if (screen.equalsIgnoreCase("split tote")) {
                if (param.equalsIgnoreCase("displayed")) {
                    setSplitToteID(getPrepToteID());
                } else {
                    setSplitToteID(new DataCreateModule().generateContainer(param, container));
                }
                prepOptionPage.scanValue(getSplitToteID());
            } else if (screen.equalsIgnoreCase("split bin")) {
                setSplitBinID(new DataCreateModule().generateContainer(param, container));
                prepOptionPage.scanValue(getSplitBinID());
            }
        } catch (Exception e) {
            log.error("Exception in scan tote", e);
            Assert.fail("Exception in scan tote", e);
        }

    }

    @Then("Open a new tab")
    public void openNewTab() {
        prepOptionPage.openNewTab();
    }


    public void validateScanToteScreen(String screen) {
        switch (screen.toLowerCase()) {
            case "scan tote":
                prepOptionPage.validateScanToteScreen();
                break;
            case "scan container":
                prepOptionPage.validateScanContainerScreen();
                break;
            case "prep":
                prepOptionPage.validatePrepScreenPage(getPrepToteID(), getOriginalQuantity(), getPoLineDStringStringMap());
                break;
            case "split tote":
                prepOptionPage.validateSplitToteScreen(getPrepToteID(), "tote", getOriginalQuantity(), getPoLineDStringStringMap());
                break;
            case "split bin":
                prepOptionPage.validateSplitToteScreen(getPrepToteID(), "bin", getOriginalQuantity(), getPoLineDStringStringMap());
                break;
            case "split bin wave":
                prepOptionPage.validateSplitToteScreen(getPrepToteID(), "bin", getOriginalQuantity());
                break;
            case "scan tote sku":
                prepOptionPage.validateSkanSKUScreen(getSplitToteID(), getSplitSku(), "tote");
                break;
            case "scan bin sku":
                prepOptionPage.validateSkanSKUScreen(getSplitBinID(), getSplitSku(), "bin");
                break;
            case "tote quantity":
                prepOptionPage.validateQuantityScreen(getSplitToteID(), getToteDisplayedunits(), getPoLineDStringStringMap(), "tote");
                break;
            case "tote quantity wave":
                prepOptionPage.validateQuantityScreen(getSplitToteID(), getToteDisplayedunits(), "tote");
                break;
            case "bin quantity":
                prepOptionPage.validateQuantityScreen(getSplitBinID(), getBinDisplayedunits(), getPoLineDStringStringMap(), "bin");
                break;
            case "bin quantity wave":
                prepOptionPage.validateQuantityScreen(getSplitBinID(), getBinDisplayedunits(), "bin");
                break;
            case "split tote regular":
                prepOptionPage.validateSplitToteScreen(getPrepToteID(), "regular", getOriginalQuantity(), getPoLineDStringStringMap());
                break;
            case "split bin regular":
                prepOptionPage.validateSplitBinScreen(getPrepToteID(), "regular", getOriginalQuantity());
                break;
            case "original tote":
                prepOptionPage.validateOriginalToteScreen(getPrepToteID(), getOriginalQuantity(), "NonEditableQty");
                break;
        }

    }

    public void scanSku(String param) {
        switch (param.toLowerCase()) {
            case "random":
                prepOptionPage.scanValue("12345");
                break;
            case "null":
                prepOptionPage.scanValue("");
                break;
            case "valid":
                prepOptionPage.scanValue(getSplitSku());
                break;

        }
    }

    public void scanQuantity(String quantity, String param) {
        if (param.equalsIgnoreCase("tote")) {
            if (quantity.equalsIgnoreCase("null")) {
                setSplitQuantity("");
            } else if (quantity.equalsIgnoreCase("greater than displayed")) {
                setSplitQuantity(Integer.toString(1 + getToteDisplayedunits()));
            } else if (quantity.equalsIgnoreCase("less than displayed")) {
                setSplitQuantity(Integer.toString(1 + getToteDisplayedunits()));
            } else if (quantity.equalsIgnoreCase("displayed")) {
                setSplitQuantity(Integer.toString(getToteDisplayedunits()));
            } else {
                setSplitQuantity(quantity);
            }
        } else if (param.equalsIgnoreCase("Bin")) {
            if (quantity.equalsIgnoreCase("null")) {
                setSplitQuantity("");
            } else if (quantity.equalsIgnoreCase("greater than displayed")) {
                setSplitQuantity(Integer.toString(1 + getBinDisplayedunits()));
            } else if (quantity.equalsIgnoreCase("less than displayed")) {
                setSplitQuantity(Integer.toString(1 + getBinDisplayedunits()));
            } else if (quantity.equalsIgnoreCase("displayed")) {
                setSplitQuantity(Integer.toString(getBinDisplayedunits()));
            } else {
                setSplitQuantity(quantity);
            }
        }
        prepOptionPage.scanQuantity(getSplitQuantity());
    }

    private void validateAlertMessage(String message) {
        prepOptionPage.validateAlertMessage(message);
    }

    public void verifyUserMessage(String param) {
        String message = "Put #container# #ToteorBin# on the conveyor";
        if (param.equalsIgnoreCase("split tote")) {
            message = message.replace("#ToteorBin#", getSplitToteID()).replace("#container#", "Tote");
        } else if (param.equalsIgnoreCase("split bin")) {
            message = message.replace("#ToteorBin#", getSplitBinID()).replace("#container#", "Bin");
        } else if (param.equalsIgnoreCase("prep tote")) {
            message = message.replace("#ToteorBin#", getPrepToteID()).replace("#container#", "Tote");
        }
        validateFinalAlertMessage(message);
    }

    private void validateFinalAlertMessage(String message) {
        prepOptionPage.validateFinalAlertMessage(message);
    }

    public void validateInventoryForContainers(String param, String container) {
        String containerId = "";
        int quantity = 0;
        String containerType = "";
        String response = null;

        if (container.equalsIgnoreCase("split tote")) {
            containerId = getSplitToteID();
            quantity = getToteDisplayedunits();
            containerType = "TOTE";
        } else if (container.equalsIgnoreCase("prep tote")) {
            containerId = getPrepToteID();
            quantity = getOriginalQuantity();
            containerType = "TOTE";
        } else if (container.equalsIgnoreCase("split bin")) {
            containerId = getSplitBinID();
            quantity = getBinDisplayedunits();
            containerType = "BINBOX";
        }

        if (param.equalsIgnoreCase("deleted")) {
            response = CommonUtils.getRequestResponse(ReadHostConfiguration.GET_INVENTORY_SERVICE_URL.value().replace("{totebarcode}", containerId));
            Assert.assertTrue(response.isEmpty(), "Inventory is deleted for container :" + container);
        } else {
            response = prepOptionPage.getInventory(containerId);
            if (!response.isEmpty()) {
                JSONObject jsonObject = new JSONObject(response);
                String containerStatus = jsonObject.getJSONObject("container").getString("containerStatusCode");
                log.info("container Status: {}", containerStatus);
                Assert.assertEquals(containerStatus, "VSC", "Container Status:");
                JSONArray inventorySnapshotList = jsonObject.getJSONArray("inventorySnapshotList");
                for (Object object : inventorySnapshotList) {
                    JSONObject inventorySnapshotListObject = (JSONObject) object;
                    if (!CreateToteSteps.hasInnerPack) {
                        Assert.assertEquals(quantity, inventorySnapshotListObject.getInt("quantity"));
                        Assert.assertEquals(containerId, inventorySnapshotListObject.getString("container"));
                        Assert.assertEquals(containerType, inventorySnapshotListObject.getString("containerType"));
                    }
                }

            } else {
                Assert.assertFalse(true, "Inventory response in empty for the conatiner :" + containerId);
            }
        }
    }

    public void clickButton(String button) {
        prepOptionPage.clickButton(button);
    }

    private Map<String, String> getPoLineDetails(String poLineBarcode) {
        return CommonUtils.getMapFromJson(RestUtilities.getRequestResponse(ReadHostConfiguration.FETCH_POLINE_DTLS_URL.value() + ReadHostConfiguration.LOCATION_NUMBER.value() +
                "/polines/" + poLineBarcode));
    }
    
    private List<WSMActivity> getWSMActivitiesForBIN(String containerID) {
        List<WSMActivity> wsmActivities = new ArrayList<>(3);
        String wsmActivitiesResponse = CommonUtils.getRequestResponse(String.format(WsmEndpoint.WSM_SPLITFORWAVE_SEARCH, containerID));
        log.info("wsmActivitiesResponse: {}", wsmActivitiesResponse);
        if (StringUtils.isNotBlank(wsmActivitiesResponse)) {
            for (Object wsmActivity : new JSONArray(wsmActivitiesResponse)) {
                JSONObject activity = (JSONObject) wsmActivity;
                String containerId = activity.getString("containerId");
                String status = activity.getString("status");
                Integer qty = activity.getInt("qty");
                JSONObject attributes = activity.getJSONObject("attributes");
                String subtype = attributes.getString("subType");
                log.info("Activity BINBOX subtype:[{}], containerId:[{}], status:[{}], Quantity:[{}]",
                        new Object[]{subtype, containerId, status, qty});
                if (StringUtils.equals(containerId, containerID) && StringUtils.equalsIgnoreCase(status, "OPEN")) {
                    wsmActivities.add(new WSMActivity(activity.getBigInteger("id"), subtype, qty, activity.getString("containerType"),
                            containerId, activity.getString("upc"), attributes.getInt("sequence")));
                }
            }

        } else {
            org.junit.Assert.assertTrue("No WSM Activities for Container" + containerID, false);
        }
        wsmActivities.sort(Comparator.comparing(WSMActivity::getSequence));
        return wsmActivities;

    }
    
    public void scanContainer(String param, String container, String screen) {
        try {
            if (screen.equalsIgnoreCase("scan container")) {
                prepOptionPage.scanToteId(getPrepToteID());
            } else if (screen.equalsIgnoreCase("split tote")) {
                if (param.equalsIgnoreCase("displayed")) {
                    setSplitToteID(getPrepToteID());
                } else {
                    setSplitToteID(new DataCreateModule().generateContainer(param, container));
                }
                prepOptionPage.scanValue(getSplitToteID());
            } else if (screen.equalsIgnoreCase("split bin")) {
                setSplitBinID(new DataCreateModule().generateContainer(param, container));
                prepOptionPage.scanValue(getSplitBinID());
            }
        } catch (Exception e) {
            log.error("Exception in scan tote", e);
            Assert.fail("Exception in scan tote", e);
        }
    }
}
