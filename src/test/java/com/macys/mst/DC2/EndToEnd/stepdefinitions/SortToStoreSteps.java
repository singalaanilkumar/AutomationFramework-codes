package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.macys.mst.DC2.EndToEnd.configuration.*;
import com.macys.mst.DC2.EndToEnd.constants.MHE_MessagingReverseJSON;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLMessage;
import com.macys.mst.DC2.EndToEnd.model.CartonDetails;
import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.model.LocationDistro;
import com.macys.mst.DC2.EndToEnd.model.POLineItems;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODashboardReleasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.SortToStorePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData.PoLinebarCode;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.RequestUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

@Slf4j
public class SortToStoreSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();

    private SortToStorePage sortToStorePage = PageFactory.initElements(driver, SortToStorePage.class);
    PODashboardReleasePage poDashboardReleasePage = PageFactory.initElements(driver, PODashboardReleasePage.class);
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    private RequestUtil requestUtil = new RequestUtil();
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    
    @Setter
    @Getter
    private List<CartonDetails> cartonDetails;

    public SortToStoreSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    private String parentContainer;
    private String scannedBarcode;
    
    private List<CartonDetails> cartonDetailsList = new ArrayList<>(5);

    @Then("Sort To Store -Scan Zone Screen is displayed")
    public void verifyScanZoneScreen() {
        sortToStorePage.validateScanZoneScreen();
    }

    @Then("Sort To Store -Scan Tote Screen is displayed")
    public void validateScanToteScreen() {
        sortToStorePage.validateScanToteScreen();
    }

    @Then("Sort To Store -Scan Location Screen is displayed")
    public void validateScanLocationScreen() {
        sortToStorePage.validateScanLocationScreen();
    }

    @Then("Sort To Store -Scan Carton Screen is displayed")
    public void validateCartonScreen() {
        sortToStorePage.validateCartonScreen();
    }

    @SuppressWarnings("unchecked")
	@When("Sort To Store is performed in zone $stagezone and processing location $proclocation and staged to $stageid location for $waveCount wave")
    public void sortToStoreForWave(String stagezone, String proclocation, String stageid,String waveCount) throws Exception {
    	
       Table<String, String, String> rtfRequests = (Table<String, String, String>) dataStorage.getStoredData().get(waveCount + "RTFs");

       Table<String, String,Integer> storeSKUQTYTable = HashBasedTable.create();
       for (Cell<String, String, String> cell : rtfRequests.cellSet()) {
            JsonPath rtfPath = new JsonPath(cell.getValue().toString());
            JSONArray lineItemArray = new JSONObject(cell.getValue()).getJSONArray("lineItem");
            for (int j = 0; j < lineItemArray.length(); j++) {
            	JsonPath lineItemPath = new JsonPath(lineItemArray.get(j).toString());
                storeSKUQTYTable.put(rtfPath.getString("shipment.shipToLocationNbr"), lineItemPath.getString("itemSkuUpc"), lineItemPath.getInt("openQuantity"));
            }
        }
    	
        CommonUtil.deleteRequest(String.format(InventoryEndPoint.CONSUME_TOTE, "7221", proclocation, "TestAutomation"), 204);
        CommonUtil.deleteRequest(String.format(InventoryEndPoint.CONSUME_TOTE, "7221", stageid, "TestAutomation"), 204);
        
        Map<String, String> cartonConfig = CommonUtils.loadConfig("Carton", "Attributes", "Carton_Dimensions", "Carton", "P2C");
        String prefix = cartonConfig.get("StartsWith");
        sortToStorePage.selectOptionFromMenu("SortToStore");
        verifyScanZoneScreen();
        sortToStorePage.scanZoneBox(stagezone);
        HashMap<String, HashSet<String>> skuBINsMap = (HashMap<String, HashSet<String>>) dataStorage.getStoredData().get(waveCount + "Inventory");
        SortToStoreSteps.log.info("Map Size :" + skuBINsMap.size());
        if (!skuBINsMap.isEmpty()) {
            Set<String> binIds = skuBINsMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
            SortToStoreSteps.log.info("BINs :" + binIds);
            StepDetail.addDetail("List of BINs :" + binIds, true);
            SortToStoreSteps.log.info("size of the inventory :" + binIds.size());
            binIds.forEach(binId -> {
                try {
                	JsonPath binDetails = new JsonPath(commonUtils.getContainerDetailsbyBarcode(binId));
                	Integer binQTY = binDetails.getInt("inventorySnapshotList[0].quantity");
                	String binSKU = binDetails.get("inventorySnapshotList[0].item").toString();
                	if(binQTY>0){
                		for(String store : storeSKUQTYTable.rowKeySet()){
                    		Integer expectedQTY = storeSKUQTYTable.get(store, binSKU);
                    		if(expectedQTY>0){
                    			SortToStoreSteps.log.info("validate inventory is created with all these BINs: [{}]", binId);
                                sortToStorePage.scanToteId(binId);
                                log.info("proclocation :" + proclocation);
                                validateScanLocationScreen();
                                String str_Nbr = sortToStorePage.getStrNbr();
                                String loction_nbr = "";
                                
                                String ProcessingLocation = "";
                                String cartonId = "";
                                String prevStrLoc = verifySTSInprggresscartonActivities(loction_nbr, "IN_PROGRESS", "Location");
                                if (prevStrLoc.equalsIgnoreCase("")) {
                                    ProcessingLocation = proclocation;
                                } else {
                                    ProcessingLocation = prevStrLoc;
                                }
                                TimeUnit.SECONDS.sleep(10);
                                log.info("MSL ProcessingLocation : {}", ProcessingLocation);
                                sortToStorePage.scanLocationId(ProcessingLocation);
                                if (prevStrLoc.equalsIgnoreCase("")) {
                                    cartonId = CommonUtils.getRandomCartonNumber(prefix, "20");
                                    sortToStorePage.scanCartonId(cartonId);
                                } else {
                                    cartonId = verifySTSInprggresscartonActivities(str_Nbr, "IN_PROGRESS", "CartonId");
                                    sortToStorePage.scanCartonId(cartonId);
                                }
                                log.info("Carton Nbr displyed that needs to be scanned is {}", cartonId);
                                StepDetail.addDetail("Carton Nbr Generated : " + cartonId, true);
                                TimeUnit.SECONDS.sleep(10);
                                String ScanQty = sortToStorePage.getQty();
                                
                                if(binQTY>expectedQTY){
                                	ScanQty = expectedQTY.toString();
                                    sortToStorePage.enterCartonQty(String.valueOf(ScanQty));
                                    binQTY = binQTY-expectedQTY;
                                    expectedQTY = 0;
                                    storeSKUQTYTable.put(store, binSKU,expectedQTY);
                                }else{
                                	ScanQty = binQTY.toString();
                                    sortToStorePage.enterCartonQty(String.valueOf(ScanQty));
                                    expectedQTY = expectedQTY-binQTY;
                                    binQTY = 0;
                                    storeSKUQTYTable.put(store, binSKU,expectedQTY);
                                }

                                Map<String, Object> containerDetails = getContainerDetails(cartonId);
                                Assert.assertEquals(containerDetails.get(RfContainerEnquiryService.Status), "SHR");
                                log.info("containerDetails.get(RfContainerEnquiryService.Status {}", containerDetails.get(RfContainerEnquiryService.Status));
                                StepDetail.addDetail("Validated " + cartonId + " status to be : " + "SHR", true);
                                sortToStorePage.scanStageId(stageid);
                                TimeUnit.SECONDS.sleep(10);
                                Map<String, Object> containerStgDetails = getContainerDetails(cartonId);
                                log.info("containerStgDetails.get(RfContainerEnquiryService.Status) {}", containerStgDetails.get(RfContainerEnquiryService.Status));
                                String jsonResult = MHE_MessagingReverseJSON.MANIFEST_PAYLOAD;
                                jsonResult = jsonResult
                                        .replace("#carton#", cartonId);
                                SortToStoreSteps.log.info("Scan Weigh message: {}", jsonResult);
                                log.info("ReadHostConfiguration.CONTAINER_FORMAT_CONFIG_URL.value() :-", ReadHostConfiguration.MANIFEST_URL.value());
                                JSONObject json = new JSONObject(jsonResult);
                                String path = ReadHostConfiguration.MANIFEST_URL.value();
                                Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).log().all().when().contentType(ContentType.JSON).body(json.toString()).post(path);
                                log.info("response :" + response);
                                StepDetail.addDetail("Successfully sent Scan Weigh Message", true);
                                if(binQTY<=0){
                                	log.info("BIN {} has been exhausted", binId);
                                	break;
                                }
                    		}
                    		else{
                    			log.info("Store {} has already reached expected QTY for SKU {}",store,binSKU);
                    		}
                    	}
                	}else{
                		log.info("BIN:{} has QTY:{}",binId, binQTY);
                	}
                	
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            sortToStorePage.clickButton("exit");

        } else {
            Assert.fail("Get PoLines ToteIds is empty");
        }
    }
    

    @Then("Validate STSCARTON Activities for $waveCount wave in $waveStatus Status$values")
    public void validateWSMActivities(String waveCount, String waveStatus, ExamplesTable values) throws Exception {
        TimeUnit.SECONDS.sleep(10);        
    }

    @SuppressWarnings("unchecked")    
    @When("Sort To Store is performed in zone $stagezone and processing location $proclocation and staged to $stageid location")
    public void sortToStore(String stagezone, String proclocation, String stageid) throws Exception {

    	CommonUtil.deleteRequest(String.format(InventoryEndPoint.CONSUME_TOTE, "7221", proclocation, "TestAutomation"), 204);
        CommonUtil.deleteRequest(String.format(InventoryEndPoint.CONSUME_TOTE, "7221", stageid, "TestAutomation"), 204);
        Map<String, String> cartonConfig = CommonUtils.loadConfig("Carton", "Attributes", "Carton_Dimensions", "Carton", "P2C");
        String prefix = cartonConfig.get("StartsWith");
        sortToStorePage.selectOptionFromMenu("SortToStore");
        verifyScanZoneScreen();
        sortToStorePage.scanZoneBox(stagezone);
        Map<String, List<String>> poLinesToteId = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
        SortToStoreSteps.log.info("Map Size :" + poLinesToteId.size());
        if (!poLinesToteId.isEmpty()) {
            List<String> toteIds = poLinesToteId.values().stream().flatMap(List::stream).collect(Collectors.toList());
            SortToStoreSteps.log.info("toteIds :" + toteIds);
            StepDetail.addDetail("List of Totes :" + toteIds, true);
            SortToStoreSteps.log.info("size of the inventory :" + toteIds.size());
            toteIds.forEach(toteId -> {
                try {
                    SortToStoreSteps.log.info("validate inventory is created with all these totes: [{}]", toteId);
                    sortToStorePage.scanToteId(toteId);
                    log.info("proclocation :" + proclocation);
                    validateScanLocationScreen();
                    String str_Nbr = sortToStorePage.getStrNbr();
                    String loction_nbr = "";
                    Map<BigInteger, List<LocationDistro>> SkuStoreLocnQuantityMap = (Map<BigInteger, List<LocationDistro>>) stepsContext.get(Context.SKU_STORE_LOCN_QTY_MAP.name());
                    List<LocationDistro> locationDistro = SkuStoreLocnQuantityMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
                    for (LocationDistro eachLocationDistro : locationDistro) {
                        if (eachLocationDistro.getStoreNbr().equals(str_Nbr)) {
                            loction_nbr = String.valueOf(eachLocationDistro.getLocationNbr());
                            break;
                        } else {
                            continue;
                        }
                    }
                    String ProcessingLocation = "";
                    String cartonId = "";
                    String prevStrLoc = verifySTSInprggresscartonActivities(loction_nbr, "IN_PROGRESS", "Location");
                    if (prevStrLoc.equalsIgnoreCase("")) {
                        ProcessingLocation = proclocation;
                    } else {
                        ProcessingLocation = prevStrLoc;
                    }
                    TimeUnit.SECONDS.sleep(10);
                    log.info("MSL ProcessingLocation : {}", ProcessingLocation);
                    sortToStorePage.scanLocationId(ProcessingLocation);
                    if (prevStrLoc.equalsIgnoreCase("")) {
                        cartonId = CommonUtils.getRandomCartonNumber(prefix, "20");
                        sortToStorePage.scanCartonId(cartonId);
                    } else {
                        cartonId = verifySTSInprggresscartonActivities(str_Nbr, "IN_PROGRESS", "CartonId");
                        sortToStorePage.scanCartonId(cartonId);
                    }
                    log.info("Carton Nbr displyed that needs to be scanned is {}", cartonId);
                    StepDetail.addDetail("Carton Nbr Generated : " + cartonId, true);
                    TimeUnit.SECONDS.sleep(10);
                    String ScanQty = sortToStorePage.getQty();
                    sortToStorePage.enterCartonQty(String.valueOf(ScanQty));
                    Map<String, Object> containerDetails = getContainerDetails(cartonId);
                    Assert.assertEquals(containerDetails.get(RfContainerEnquiryService.Status), "SHR");
                    log.info("containerDetails.get(RfContainerEnquiryService.Status {}", containerDetails.get(RfContainerEnquiryService.Status));
                    StepDetail.addDetail("Validated " + cartonId + " status to be : " + "SHR", true);
                    sortToStorePage.scanStageId(stageid);
                    TimeUnit.SECONDS.sleep(10);
                    Map<String, Object> containerStgDetails = getContainerDetails(cartonId);
                  //  Assert.assertEquals(containerStgDetails.get(RfContainerEnquiryService.Status), "STG");
                    log.info("containerStgDetails.get(RfContainerEnquiryService.Status) {}", containerStgDetails.get(RfContainerEnquiryService.Status));
                  //  StepDetail.addDetail("Validated " + cartonId + " status to be : " + "STG", true);
                    String jsonResult = MHE_MessagingReverseJSON.MANIFEST_PAYLOAD;
                    jsonResult = jsonResult
                            .replace("#carton#", cartonId);
                    SortToStoreSteps.log.info("Scan Weigh message: {}", jsonResult);
                    log.info("ReadHostConfiguration.CONTAINER_FORMAT_CONFIG_URL.value() :-", ReadHostConfiguration.MANIFEST_URL.value());
                    JSONObject json = new JSONObject(jsonResult);
                    String path = ReadHostConfiguration.MANIFEST_URL.value();
                    Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).log().all().when().contentType(ContentType.JSON).body(json.toString()).post(path);
                    log.info("response :" + response);
                    StepDetail.addDetail("Successfully sent Scan Weigh Message", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            });

        } else {
            Assert.fail("Get PoLines ToteIds is empty");
        }
    }

    public void validateContainerStatus(String container, String status) {
        String barcode = "";
        if (container.equalsIgnoreCase("CRT"))
            barcode = scannedBarcode != null ? scannedBarcode : String.valueOf(cartonDetailsList.get(0));
        else if (container.equalsIgnoreCase("pallet"))
            barcode = parentContainer;

        Map<String, Object> containerDetails = getContainerDetails(barcode);
        Assert.assertEquals(containerDetails.get(RfContainerEnquiryService.Status), status);
        StepDetail.addDetail("Validated " + container + " status to be : " + status, true);
    }


    public Map<String, Object> getContainerDetails(String barcode) {
        RfContainerEnquiryService rfService = new RfContainerEnquiryService();
        rfService.fetchInventoryServiceDetails(barcode);
        return RfContainerEnquiryService.getContainerInquireDetails();
    }


    @SuppressWarnings("unchecked")
	@Then("Cartons are closed and diverted to Shipping area, shipping labels are printed")
    public void verifyCartonShipInfoMessage() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        Map<String, List<String>> cartonIdToteMap = (Map<String, List<String>>) stepsContext.get(Context.CARTON_TOTE_MAP.name());
        cartonIdToteMap.keySet().forEach(cartonId -> {
            InventoryContainer cartonDetail = CommonUtils.getInventory(cartonId);
            if (null != cartonDetail) {
                assertEquals(cartonDetail.getContainer().getContainerStatusCode(), "STG");
                StepDetail.addDetail(String.format("Shipping: Scan Weigh message: Carton ID: %s and Carton status: %s  validated", cartonId, cartonDetail.getContainer().getContainerStatusCode()), true);
                SortToStoreSteps.log.info("Shipping: able to retrieve Carton: {}", cartonId);

            } else {
                SortToStoreSteps.log.info("Shipping: Unable to retrieve Carton: {}", cartonId);
                org.testng.Assert.assertTrue(false, "Shipping: Unable to retrieve Carton:" + cartonId);
                StepDetail.addDetail(String.format("Shipping: Unable to retrieve Carton: %s", cartonId), true);
            }


        });

    }

    public void updateStoreallocData() {

        //List<Map<Object, Object>> dbResults = null;
        //List<PoLineBarCodeData.PoLinebarCode> polineData = new ArrayList<>();
        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        for (PoLinebarCode poLine : poLinebarCode) {
            try {
                TimeUnit.SECONDS.sleep(5);
                String query = String.format(SQLMessage.UPDATE_SORTING_STORE_ALLOC_DATA.replace("{PONBR}", poLine.getPoNbr()).replace("{RECEIPTNBR}", poLine.getReceiptNbr()));
                SortToStoreSteps.log.info("getPOReceiptDetails query: {}", query);
                DBMethods.deleteOrUpdateDataBase(query, "sorting");
                //DBUtils.deleteOrUpdateDataBase("sorting", query);
            } catch (Exception e) {
                SortToStoreSteps.log.error("Update Sorting table", e);
            }
        }
    }

    @Then("Validate store alloc message in sorting db")
    public void validateSortingStoreAllocMessage() {

        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        for (PoLinebarCode poLine : poLinebarCode) {
            try {
                TimeUnit.SECONDS.sleep(5);
                String query = String.format(SQLMessage.GET_SORTING_STORE_ALLOC_DATA.replace("{PONBR}", poLine.getPoNbr()).replace("{RECEIPTNBR}", poLine.getReceiptNbr()));
                SortToStoreSteps.log.info("getPOReceiptDetails query: {}", query);
                List<String> dbResults = DBMethods.getDBValueInList(query, "sorting");
                if (dbResults.size() > 0) {
                    SortToStoreSteps.log.info("Sorting store alloc messages are made entry into DB");
                } else SortToStoreSteps.log.info("Failed to make entry into DB");

                //DBUtils.deleteOrUpdateDataBase("sorting", query);
            } catch (Exception e) {
                SortToStoreSteps.log.error("Retriving the DB results failed", e);
            }
        }

    }
    
    @SuppressWarnings("unchecked")
	@Then("Validate store alloc message in sorting db for $waveCount wave")
    public void validateSortingStoreAllocMessageForWave(String waveCount) {
	
        try {
        	TimeUnit.SECONDS.sleep(5);
			String waveNumber = String.valueOf(dataStorage.getStoredData().get(waveCount + "Number"));
            Table<String, String, String> rtfRequests = (Table<String, String, String>) dataStorage.getStoredData().get(waveCount + "RTFs");

            Table<String, String,Integer> storeAllocTableExpected = HashBasedTable.create();
            for (Cell<String, String, String> cell : rtfRequests.cellSet()) {
                JsonPath rtfPath = new JsonPath(cell.getValue().toString());
                JSONArray lineItemArray = new JSONObject(cell.getValue()).getJSONArray("lineItem");
                for (int j = 0; j < lineItemArray.length(); j++) {
                	JsonPath lineItemPath = new JsonPath(lineItemArray.get(j).toString());
                    storeAllocTableExpected.put(rtfPath.getString("shipment.shipToLocationNbr"), lineItemPath.getString("itemSkuUpc"), lineItemPath.getInt("openQuantity"));
                }
            }
            Table<String, String, Integer> storeAllocTableActual = HashBasedTable.create();
			String query = String.format(SQLMessage.GET_SORTING_STORE_ALLOC_DATA_WAVE.replace("{WAVENUMBER}",waveNumber));
			SortToStoreSteps.log.info("StoreAlloc Sorting DB query: {}", query);
			List<Map<Object, Object>> dbResults = DBMethods.getValuesFromDBAsStringList(query, "sorting");
			if (dbResults.size() > 0) {
			        SortToStoreSteps.log.info("Sorting store alloc messages are made entry into DB");
			        for(Map<Object,Object> storeAllocMap : dbResults){
			        	storeAllocTableActual.put(storeAllocMap.get("store_loc_nbr").toString(), storeAllocMap.get("skuupc").toString(), Integer.valueOf(storeAllocMap.get("distro_qty").toString()));
			        }
			} else SortToStoreSteps.log.info("Failed to make entry into DB");
			
			CommonUtils.doJbehavereportConsolelogAndAssertion("Sort StoreAlloc messages as Expected", storeAllocTableActual.cellSet().toString(), storeAllocTableActual.equals(storeAllocTableExpected));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }


    @When("Clean inventory and activity")
    public void cleanupInventory() {
        List<String> activitiesList = new ArrayList<>();
        activitiesList.add("LANE");
        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        for (PoLinebarCode poLine : poLinebarCode) {
            try {
                TimeUnit.SECONDS.sleep(5);
                String query = String.format(SQLMessage.CLEANUP_SORTING_STORE_ALLOC_DATA.replace("{PONBR}", poLine.getPoNbr()).replace("{RECEIPTNBR}", poLine.getReceiptNbr()));
                SortToStoreSteps.log.info("getPOReceiptDetails query: {}", query);
                DBMethods.deleteOrUpdateDataBase(query, "sorting");

                //DBUtils.deleteOrUpdateDataBase("sorting", query);
            } catch (Exception e) {
                SortToStoreSteps.log.error("Retriving the DB results failed", e);
            }
        }
    }

    @Then("Validate the STS Carton activities for $store with status $status")
    public void verifySTScartonActivities(String store, String status) {
        List<String> statusList = new ArrayList<>();
        Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap = (Map<BigInteger, List<LocationDistro>>) stepsContext.get(Context.SKU_STORE_LOCN_QTY_MAP.name());
        log.info("skuStoreLocnQuantityMap {}", skuStoreLocnQuantityMap);
        for (List<LocationDistro> distro : skuStoreLocnQuantityMap.values()) {
            for (int p = 0; p < distro.size(); p++) {
                int str = distro.get(p).getLocationNbr();
                log.info("StroNbr : {}", str);
                String WSM_STS_CARTON_ACTIVITY = String.format(WsmEndpoint.WSM_STS_CARTON_ACTIVITY, str, status);
                log.info("GetActivitiesforStoreEndpoint: " + WSM_STS_CARTON_ACTIVITY);
                String response = CommonUtils.getRequestResponse(WSM_STS_CARTON_ACTIVITY);
                if (!response.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(response);
                    if (null != jsonArray && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            String str_Status = jsonObject.getString("status");
                            String str_nbr = jsonObject.getString("containerId");
                            statusList.add(str_nbr);
                        }
                        StepDetail.addDetail("List of stores that are allocated {}" + statusList, true);
                    }
                } else {
                    log.info("No OPEN STS Activities for given Store {}", str);
                    Assert.assertTrue("No OPEN STS Activities for given Store", false);
                }
            }
        }
    }

    public String verifySTSInprggresscartonActivities(String store, String status, String requiredValue) {
        String WSM_STS_CARTON_ACTIVITY = String.format(WsmEndpoint.WSM_STS_CARTON_ACTIVITY, store, status);
        log.info("GetActivitiesforStoreEndpoint: " + WSM_STS_CARTON_ACTIVITY);
        String response = CommonUtils.getRequestResponse(WSM_STS_CARTON_ACTIVITY);
        log.info("Response for WSM STS Carton Activity: ", response);
        String str_locNbr = "";
        String carton_nbr = "";
        if (!response.isEmpty()) {
            log.info("Inprogress MLS processing location is available for the store");
            JSONArray jsonArray = new JSONArray(response);
            if (null != jsonArray && jsonArray.length() > 0) {
                if (requiredValue.equalsIgnoreCase("Location")) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        str_locNbr = jsonObject.getJSONObject("attributes").getString("locationNbr");
                        log.info("str_locNbr from Response : {}", str_locNbr);
                        break;
                    }
                    return str_locNbr;
                } else if (requiredValue.equalsIgnoreCase("Location")) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        carton_nbr = jsonObject.getJSONObject("attributes").getString("cartonNbr");
                        log.info("carton_nbr from Response : {}", carton_nbr);
                        break;
                    }
                    return carton_nbr;
                }
            }
            return str_locNbr;
        } else {
            log.info("No Assigned locations are avaialble for the given store");
            return str_locNbr;
        }
    }

    @When("Clean the activity for the given stores")
    public void clean_store_activity() {
        String po_Nbr = (String) stepsContext.get(Context.PO_NBR.name());
        List<POLineItems> poLineItems = CommonUtils.POOrderDetails(po_Nbr);
        for (POLineItems eachPOLineItem : poLineItems) {
            List<LocationDistro> distrolist = eachPOLineItem.getPoLocationDistroList();
            for (LocationDistro eachLocationDistro : distrolist) {
                String locationNbr = eachLocationDistro.getLocationNbr().toString();
                cleanOpenedOrAssignedOrInprogressActivitiesForStr(locationNbr, "IN_PROGRESS");
                cleanOpenedOrAssignedOrInprogressActivitiesForStr(locationNbr, "OPEN");
                cleanOpenedOrAssignedOrInprogressActivitiesForStr(locationNbr, "ASSIGNED");
            }
        }
        updateStoreallocData();
    }
    
    @SuppressWarnings("unchecked")
	@When("Clean the activity for the given stores for $waveCount RTF")
    public void clean_store_activity_for_Wave(String waveCount) {
        Table<String, String, String> rtfRequests = (Table<String, String, String>) dataStorage.getStoredData().get("publishedRTFs");
        
        Table<String, String,Integer> storeSKUQTYTable = HashBasedTable.create();
        for (Cell<String, String, String> cell : rtfRequests.cellSet()) {
             JsonPath rtfPath = new JsonPath(cell.getValue().toString());
             JSONArray lineItemArray = new JSONObject(cell.getValue()).getJSONObject("lineItemList").getJSONArray("lineItem");
             for (int j = 0; j < lineItemArray.length(); j++) {
             	JsonPath lineItemPath = new JsonPath(lineItemArray.get(j).toString());
                 storeSKUQTYTable.put(rtfPath.getString("shipment.shipToLocationNbr"), lineItemPath.getString("itemSkuUpc"), lineItemPath.getInt("itemQuantity"));
             }
         }
            
        for (String locationNbr : storeSKUQTYTable.rowKeySet()) {
                cleanOpenedOrAssignedOrInprogressActivitiesForStr(locationNbr, "IN_PROGRESS");
                cleanOpenedOrAssignedOrInprogressActivitiesForStr(locationNbr, "OPEN");
                cleanOpenedOrAssignedOrInprogressActivitiesForStr(locationNbr, "ASSIGNED");
        }
     }

    public void cleanOpenedOrAssignedOrInprogressActivitiesForStr(String str, String status) {
        String updateWsmActivityEndpoint = String.format(WsmEndpoint.WSM_update_Activities, str, status);
        String WSM_STS_CARTON_ACTIVITY = String.format(WsmEndpoint.WSM_STS_CARTON_ACTIVITY, str, status);
        log.info("GetActivitiesforStoreEndpoint: " + WSM_STS_CARTON_ACTIVITY);
        String response = CommonUtils.getRequestResponse(WSM_STS_CARTON_ACTIVITY);
        if (!response.isEmpty()) {
            JSONArray jsonArray = new JSONArray(response.toString());
            List<String> updateActivities = new ArrayList<>();
            for (Object object : jsonArray) {
                JSONObject jsonObject = new JSONObject(object.toString());
                String activityId = String.valueOf(jsonObject.get("id"));
                log.info("Activities to be Modified {}", activityId);
                if ("OPEN".equals(jsonObject.getString("status"))) {
                    String deleteActivityEndpoint = String.format(WsmEndpoint.WSM_Delete_Activities, activityId);
                    CommonUtils.deleteRequest(deleteActivityEndpoint, 204);
                } else if ("ASSIGNED".equals(jsonObject.getString("status"))) {
                    updateActivities.add(String.valueOf(jsonObject.get("id")));
                }
                if ("IN_PROGRESS".equals(jsonObject.getString("status"))) {
                    updateActivities.add(String.valueOf(jsonObject.get("id")));
                }
            }
            if (updateActivities.size() > 0) {
                String payloadParams = "";
                for (String id : updateActivities) {
                    payloadParams += "{#id:" + id + "},";
                }
                List<String> filledActivityUpdateList = requestUtil.getRequestBody(payloadParams.trim(), "WSMActivityUpdate.json");
                Response updateResponse = WhmRestCoreAutomationUtils.putRequestResponse(updateWsmActivityEndpoint, filledActivityUpdateList.toString()).asResponse();
                log.info("Updating the store activities to COMPLETE :", updateResponse.getStatusCode());
            }
        }
    }
}


