package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.Table;
import com.macys.mst.DC2.EndToEnd.configuration.*;
import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLMessage;
import com.macys.mst.DC2.EndToEnd.model.ContainerRelation;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateTotePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PackAwaySortPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PutAwayScanlocationPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.Constants.ContainerType;
import com.macys.mst.DC2.EndToEnd.utilmethods.Constants.PackType;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.macys.mst.DC2.EndToEnd.utilmethods.Constants.SORT_ZONE;
import static com.macys.mst.DC2.EndToEnd.utilmethods.Constants.STORAGE_TYPE;

@Slf4j
public class PackAwaySortSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private PackAwaySortPage packAwaySortPage = PageFactory.initElements(driver, PackAwaySortPage.class);
    private PutAwayScanlocationPage putAwayScanlocationPage = PageFactory.initElements(driver, PutAwayScanlocationPage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
    private DataCreateModule dataModule = new DataCreateModule();
    private WSMServices wsmService = new WSMServices();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();

    public PackAwaySortSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }
    CreateTotePage createTotePage = PageFactory.initElements(driver, CreateTotePage.class);

    @FindBy(xpath = "//*[contains(text(), 'DC2.0 RF Options')]")
    WebElement rfOptions;

    @FindBy(xpath = "//*[contains(text(), 'Pack Away Sorting')]")
    WebElement packAwaySorting;

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    public WebDriverWait getWait(int secs) {
        WebDriverWait wait = new WebDriverWait(driver, secs);
        return wait;
    }

    private String location;
    private String parentContainer;
    private String scannedBarcode;

    public int binSize;

    List<String> sortedBins = new LinkedList<>();
    List<String> unsortedBins = new LinkedList<>();
    private List<String> usedLocations = new LinkedList<>();
    private List<String> usedPallets = new LinkedList<>();
    private List<String> putawayLocations = new LinkedList<>();
    private Map<String, String> binActivity = new HashMap<>();

    @FindBy(xpath = "//strong[contains(text(),'Scan Pallet :')]/parent::div")
    private WebElement palletBinbox;

    @When("Pack away Sort is done for the Bins")
    public void packAwaySorts() throws Exception {
        if (!CreateToteSteps.hasInnerPack) {
            Map<String, Map<String, String>> binIdMap = (Map<String, Map<String, String>>) stepsContext.get(Context.BIN_ID_MAP.name());
            unsortedBins = binIdMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
            log.info("Binbox Ids: " + unsortedBins);
            updateSortingPackawayData("COMPLETED", "NULL", "001");
            getBinIds(unsortedBins);
            packAwaySortPage.buttonClick();
            //for(String eachBin :unsortedBins){
            scanBinsPackAwaySort();
            if (unsortedBins.size() != 0) {
                sortBins(unsortedBins.size());
                packAwaySortPage.clickButton("Exit");
                getLocationType(usedLocations);
                putAwayScanlocationPage.buttonClick();
                putAwayScanlocationPage.selectStorageLcn(driver);
                scanPalletandLocation(usedPallets, putawayLocations);
                putAwayScanlocationPage.clickButton("Exit");
            }
        }
    }

    @When("Pack away Sort is done for the multiple Bins")
    public void packAwaySortforMultipleBin() throws Exception {
        if (!CreateToteSteps.hasInnerPack) {
            Map<String, Map<String, String>> binIdMap = (Map<String, Map<String, String>>) stepsContext.get(Context.BIN_ID_MAP.name());
            unsortedBins = binIdMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
            log.info("Binbox Ids: " + unsortedBins);
            updateSortingPackawayData("COMPLETED", "NULL","001");
            getBinIds(unsortedBins);
            packAwaySortPage.buttonClick();
            scanBinsPackAwaySort();
            if (unsortedBins.size() != 0) {
                sortBinsforMultBins(unsortedBins.size());
                packAwaySortPage.clickButton("Exit");
                getLocationType(usedLocations);
                putAwayScanlocationPage.buttonClick();
                putAwayScanlocationPage.selectStorageLcn(driver);
                scanPalletandLocation(usedPallets, putawayLocations);
                putAwayScanlocationPage.clickButton("Exit");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @When("Pack away Sort is done for the $waveCount Wave Bins")
    public void packAwaySortforWavedBins(String waveCount) throws Exception {
        if (!CreateToteSteps.hasInnerPack) {
            Map<String, String> prepBinBoxMap = ((Map<String, Map<String, String>>) dataStorage.getStoredData().get(waveCount + "binValues")).get("BIN");
            unsortedBins = new ArrayList<>(prepBinBoxMap.values());
            log.info("Binbox Ids: " + unsortedBins);
            updateSortingPackawayData("COMPLETED", "NULL", "001");
            getBinIds(unsortedBins);
            packAwaySortPage.buttonClick();
            scanBinsPackAwaySortForZone("001");
            if (unsortedBins.size() != 0) {
                sortBinsforMultBins(unsortedBins.size());
                packAwaySortPage.clickButton("Exit");
                getLocationType(usedLocations);
                putAwayScanlocationPage.buttonClick();
                putAwayScanlocationPage.selectStorageLcn("Pallet");
                scanPalletandLocation(usedPallets, putawayLocations);
                putAwayScanlocationPage.clickButton("Exit");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @When("Pack away Sort is done for the ICQA multiple Bins")
    public void packAwaySortforMultipleICQABin() throws Exception {
        if (!CreateToteSteps.hasInnerPack) {
            Map<String, Map<String, String>> binIdMap = (Map<String, Map<String, String>>) stepsContext.get(Context.BIN_ID_MAP.name());
            unsortedBins = binIdMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
            log.info("Binbox Ids: " + unsortedBins);
            updateSortingPackawayData("COMPLETED", "NULL", "001");
            getBinIds(unsortedBins);
            createTotePage.navigateToCreateTote();
            packAwaySortPage.buttonClick();
            scanBinsPackAwaySort();
            if (unsortedBins.size() != 0) {
                sortBinsforMultBins(unsortedBins.size());
                packAwaySortPage.clickButton("Exit");
                getLocationType(usedLocations);
            }
        }
    }

    private void scanBinsPackAwaySort() throws Exception {
        validInput(unsortedBins.get(0));
        scanLocation();
        scanPallet();
        packAwaySortPage.validateScreenComponents();
        validateContainerStatus("BinBox", "SRT");
        validateContainerStatus("Pallet", "SIP");
        validateWSMStatus("COMPLETED");
        validatePalletBinContainerRelationship();
        validatePalletLcnContainerRelationship();
    }

    private void scanBinsPackAwaySortForZone(String zone) throws Exception {
        validInput(unsortedBins.get(0));
        scanLocationForZone(zone);
        scanPallet();
        packAwaySortPage.validateScreenComponents();
        validateContainerStatus("BinBox", "SRT");
        validateContainerStatus("Pallet", "SIP");
        validateWSMStatus("COMPLETED");
        validatePalletBinContainerRelationship();
        validatePalletLcnContainerRelationship();
    }

    private void forceCloseAndLocatePallet() throws Exception {
        clickOnClosePallet();
        validateAutoPop("Do you want to close the pallet?", "Yes");
        TimeUnit.SECONDS.sleep(20);
        validateContainerStatus("Pallet", "SRT");
        validateAutoPop("Do you want to locate the Pallet?", "Yes");
        validateAndScanLocnBarcode();
        TimeUnit.SECONDS.sleep(20);
        validateContainerStatus("Pallet", "SPW");
        packAwaySortPage.validateScreenComponents();
    }

    private boolean autocloseAndLocatePallet() throws Exception {
        if (driver.findElements(By.xpath("//h5[text()='" + "Auto closing the Pallet" + "']")).size() != 0) {
            packAwaySortPage.validateErrorPopup("INFO", "Auto closing the Pallet");
            validateContainerStatus("Pallet", "SRT");
            validateAutoPop("Do you want to locate the Pallet?", "Yes");
            validateAndScanLocnBarcode();
            TimeUnit.SECONDS.sleep(20);
            validateContainerStatus("Pallet", "SPW");
            packAwaySortPage.validateScreenComponents();
            return true;
        }
        return false;
    }

    private void getBinIds(List<String> bins) {
        bins.forEach(toteId -> {
            String wsmActivitiesResponse = CommonUtils.getRequestResponse(String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH, toteId));
            if (StringUtils.isNotBlank(wsmActivitiesResponse)) {
                for (Object wsmActivity : new JSONArray(wsmActivitiesResponse)) {
                    JSONObject activity = (JSONObject) wsmActivity;
                    String containerId = activity.getString("containerId");
                    String id = activity.get("id").toString();
                    log.info("Tote Id: {} and id: {}", containerId, id);
                    binActivity.put(containerId, id);
                }
            }
        });
    }

    private void getLocationType(List<String> locations) {
        locations.forEach(loc -> {
            String wsmActivitiesResponse = CommonUtils.getRequestResponse(String.format(WsmEndpoint.LOCATION_SEARCH, loc));
            if (StringUtils.isNotBlank(wsmActivitiesResponse)) {
                JSONArray jArray = new JSONObject(wsmActivitiesResponse).getJSONObject("data").getJSONArray("attributes");
                String storageType = "";
                for (int i = 0; i < jArray.length(); i++) {
                    String key = (String) jArray.getJSONObject(i).get("key");
                    if (key.equalsIgnoreCase("StorageType")) {
                        storageType = jArray.getJSONObject(i).getJSONArray("values").get(0).toString();
                        String locn = getPutAwayLocation(storageType).get(PackAwaySortPage.BARCODE);
                        putawayLocations.add(locn);
                    }
                }
            }
        });
        log.info("Locations: " + putawayLocations);
    }

    public void validInput(String binboxId) {
        String binbox = binboxId;
        packAwaySortPage.scanBinBox(binbox);
        scannedBarcode = binbox;
        StepDetail.addDetail("bin :" + binbox, true);
        log.info(" bin :" + binbox);
    }

    public void scanLocation() {
        location = getLocationForType(packAwaySortPage.getDetailsFromPage().get(PackAwaySortPage.LOCATION_TYPE), PackType.SK).get(PackAwaySortPage.BARCODE);
        log.info("Sorting location :{}", location);
        packAwaySortPage.scanLocationBarcode(location);
    }

    public void scanLocationForZone(String zone) {
        location = getLocationForType(packAwaySortPage.getDetailsFromPage().get(PackAwaySortPage.LOCATION_TYPE), PackType.SK, zone).get(PackAwaySortPage.BARCODE);
        log.info("Sorting location :{}", location);
        packAwaySortPage.scanLocationBarcode(location);
    }


    private Map<String, String> getLocationForType(String type, PackType pack) {
        String zone = new ConfigurationServices().getSortZoneForPack(pack);
        Optional<Map<String, String>> result = ExpectedDataProperties.getLocationsMap().stream().filter(loc -> {
            return type.equalsIgnoreCase(loc.get(STORAGE_TYPE)) && zone.equalsIgnoreCase(loc.get(SORT_ZONE))
                    && !usedLocations.contains(loc.get(PackAwaySortPage.BARCODE));
        }).findAny();
        return result.isPresent() ? result.get() : null;
    }

    private Map<String, String> getLocationForType(String type, PackType pack, String zone) {
        Optional<Map<String, String>> result = ExpectedDataProperties.getLocationsMap().stream().filter(loc -> {
            return type.equalsIgnoreCase(loc.get(STORAGE_TYPE)) && zone.equalsIgnoreCase(loc.get(SORT_ZONE))
                    && !usedLocations.contains(loc.get(PackAwaySortPage.BARCODE));
        }).findAny();
        return result.isPresent() ? result.get() : null;
    }

    private Map<String, String> getPutAwayLocation(String type) {
        Optional<Map<String, String>> result = ExpectedDataProperties.getPutAwayLocationsMap().stream().filter(loc -> {
            return type.equalsIgnoreCase(loc.get(STORAGE_TYPE));
        }).findAny();
        return result.isPresent() ? result.get() : null;
    }

    public void scanPallet() throws Exception {
        parentContainer = dataModule.generateContainer("", ContainerType.PLT.toString());
        packAwaySortPage.scanPalletBarcode(parentContainer);
        updateSortData();
    }

    private void updateSortData() {
        // bin data
        sortedBins.add(scannedBarcode);
        unsortedBins.remove(scannedBarcode);
        // pallet data
        if (parentContainer != null && !usedPallets.contains(parentContainer))
            usedPallets.add(parentContainer);
        // location data
        if (location != null && !usedLocations.contains(location))
            usedLocations.add(location);
    }

    public void validateContainerStatus(String container, String status) {
        String barcode = "";
        if (container.equalsIgnoreCase("binbox"))
            barcode = scannedBarcode != null ? scannedBarcode : unsortedBins.get(0);
        else if (container.equalsIgnoreCase("pallet"))
            barcode = parentContainer;
        try {
        	TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        Map<String, Object> containerDetails = getContainerDetails(barcode);
        Assert.assertEquals(containerDetails.get(RfContainerEnquiryService.Status), status);
        StepDetail.addDetail("Validated " + container + " status to be : " + status, true);
        log.info("Validated " + container + " status to be : " + status);
    }

    public Map<String, Object> getContainerDetails(String barcode) {
        RfContainerEnquiryService rfService = new RfContainerEnquiryService();
        rfService.fetchInventoryServiceDetails(barcode);
        return RfContainerEnquiryService.getContainerInquireDetails();
    }

    public void validateWSMStatus(String expectedStatus) {
        Map<String, String> activity = wsmService.fetchWsmActivitiesById(binActivity.get(scannedBarcode));
        Assert.assertEquals(activity.get("status"), expectedStatus);
        StepDetail.addDetail("Validated WSM activity status updated to : " + expectedStatus, true);
    }

    public void validatePalletBinContainerRelationship() {
        List<ContainerRelation> containerRelationships = (List<ContainerRelation>) (getContainerDetails(
                scannedBarcode).get("containerRealtionshipObjectList"));

        Optional<ContainerRelation> palletRelation = containerRelationships.stream()
                .filter(relation -> relation.getParentContainerType().equals("PLT")).findFirst();

        Assert.assertTrue(palletRelation.isPresent());
        Assert.assertEquals(palletRelation.get().getParentContainer(), parentContainer);
    }

    public void validatePalletLcnContainerRelationship() {
        List<ContainerRelation> containerRelationships = (List<ContainerRelation>) getContainerDetails(
                parentContainer).get("containerRealtionshipObjectList");

        Optional<ContainerRelation> locRelation = containerRelationships.stream()
                .filter(relation -> relation.getParentContainerType().equals("LCN")).findFirst();

        Assert.assertTrue(locRelation.isPresent());
        Assert.assertEquals(locRelation.get().getParentContainer(), location);
    }

    public void sortBins(int unsortedCount) throws Exception {
        for (int i = 0; i < unsortedCount; i++) {
            if (autocloseAndLocatePallet()) {
                if (unsortedBins.size() != 0) {
                    scanBinsPackAwaySort();
                }
            } else {
                if (unsortedBins.size() == 1) {
                    validInput(unsortedBins.get(0));
					/*validateScanPalletScreen();
					packAwaySortPage.ScanPalletWithOutEnter();*/
                    validateScanLocationScreen();
                    packAwaySortPage.ScanLocationWithOutEnter();
                    updateSortData();
                    forceCloseAndLocatePallet();
                    break;
                } else {
                    validInput(unsortedBins.get(0));
					/*validateScanPalletScreen();
					scanDisplayedPallet();*/
                    validateScanLocationScreen();
                    scanDisplayedLocation();
                    validateContainerStatus("BinBox", "SRT");
                    validatePalletBinContainerRelationship();
                    validateWSMStatus("COMPLETED");
                }
            }
        }
    }

    public void validateScanPalletScreen() {
        try {
            packAwaySortPage.NavigateToScanPallet();
        } catch (Exception e) {
            log.info(e.toString());
        }
        log.info("Validated validatePackAwayScanPalletPage");
        StepDetail.addDetail("Validated validatePackAwayScanPalletPage : ", true);
    }

    public void validateScanLocationScreen() {
        try {
            packAwaySortPage.NavigateToScanLocation();
        } catch (Exception e) {
            log.info(e.toString());
        }
        log.info("Validated validatePackAwayScanLocationPage");
        StepDetail.addDetail("Validated validatePackAwayScanLocationPage : ", true);
    }

    public void scanDisplayedPallet() {
        packAwaySortPage.ScanPallet();
        updateSortData();
    }

    public void scanDisplayedLocation() {
        packAwaySortPage.ScanLocation();
        updateSortData();
    }

    public void validateAutoPop(String message, String yes) {
        packAwaySortPage.autoClose(message, yes);
        log.info("Validated Popup and Clicked : " + yes);
        StepDetail.addDetail("Validated Popup and Clicked : " + yes, true);
    }

    public void validateAndScanLocnBarcode() {
        String fromLocation = (String) ((Map<String, Object>) RfContainerEnquiryService.getContainerInquireDetails()).get(RfContainerEnquiryService.Location);
        StepDetail.addDetail("Index: " + fromLocation, true);
        log.info("Location :::" + fromLocation);
        packAwaySortPage.validateAndScanPutAwayLocn(fromLocation);
    }

    public void clickOnClosePallet() {
        log.info("Validated Close Pallet Button is Displayed And Clicked");
        StepDetail.addDetail("Validated Close Pallet Button is Displayed : ", true);
        packAwaySortPage.ClickOnClosePallet();
    }

    public void scanPalletandLocation(List<String> pallets, List<String> loc) throws Exception {
        if (pallets.size() == loc.size()) {
            for (int i = 0; i < loc.size(); i++) {
                putAwayScanlocationPage.scanPallet(pallets.get(i).toString());
                validateContainerStatus("Pallet", "SPW");
                if(dataStorage.getStoredData().get("putawayLocation")!=null) {
                    putAwayScanlocationPage.scanPutAwayLocn((String) dataStorage.getStoredData().get("putawayLocation"));
                }else{
                    putAwayScanlocationPage.scanPutAwayLocn(loc.get(i));
                }
                TimeUnit.SECONDS.sleep(10);
                validateContainerStatus("Pallet", "PTW");
            }
        }
    }

    public void updateSortingPackawayData(String locationStatus, String enabledStatus, String sortZone) {
        try {
            TimeUnit.SECONDS.sleep(5);
            String query = String.format(SQLMessage.UPDDATE_SORTING_PACKAWAY_LOCATION_DATA, locationStatus, enabledStatus, sortZone);
            log.info("getPOReceiptDetails query: {}", query);
            DBMethods.deleteOrUpdateDataBase(query, "sorting");
        } catch (Exception e) {
            log.error("Update Sorting table", e);
        }
    }

    public void verifyPalletType() throws Exception {
        String palletType = "";
        binSize = unsortedBins.size();
        log.info("Size of the bins :" + unsortedBins.size());
        Map<String, String> binboxconfigValuesMap = verifyThresholdLimit("BINBOX");
        String binBoxminCapacity = String.valueOf(binboxconfigValuesMap.get("CapacityMin"));
        String binBoxmaxCapacity = String.valueOf(binboxconfigValuesMap.get("CapacityMax"));
        System.out.println("binBoxminCapacity :" + binBoxminCapacity);
        System.out.println("binBoxmaxCapacity :" + binBoxmaxCapacity);

        Map<String, String> halfPalletconfigValuesMap = verifyThresholdLimit("Half Pallet");
        String halfPalletminCapacity = String.valueOf(halfPalletconfigValuesMap.get("CapacityMin"));
        String halfPalletmaxCapacity = String.valueOf(halfPalletconfigValuesMap.get("CapacityMax"));
        System.out.println("halfPalletminCapacity :" + halfPalletminCapacity);
        System.out.println("halfPalletmaxCapacity :" + halfPalletmaxCapacity);

        Map<String, String> fullPalletconfigValuesMap = verifyThresholdLimit("Full Pallet");
        String fullPalletminCapacity = String.valueOf(fullPalletconfigValuesMap.get("CapacityMin"));
        String fullPalletmaxCapacity = String.valueOf(fullPalletconfigValuesMap.get("CapacityMax"));
        System.out.println("fullPalletminCapacity :" + fullPalletminCapacity);
        System.out.println("fullPalletmaxCapacity :" + fullPalletmaxCapacity);

        if (binSize > Integer.parseInt(binBoxminCapacity) && binSize <= Integer.parseInt(binBoxmaxCapacity)) {
            palletType = "BINBOX";
        } else if (binSize > Integer.parseInt(halfPalletminCapacity) && binSize <= Integer.parseInt(halfPalletmaxCapacity)) {
            palletType = "Half Pallet";
        } else
            palletType = "Full Pallet";

        verifyDisplayedPallet(palletType);

    }


    @Then("WSM activities for the bins after preping are completed")
    public void verifyWSMActivitiesForTheBins() throws Exception {
        Map<String, Map<String, String>> binIdMap = (Map<String, Map<String, String>>) stepsContext.get(Context.BIN_ID_MAP.name());
        List<String> unsortedBins = binIdMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());

        unsortedBins.forEach(bin -> {
            String wsmActivitiesResponse = CommonUtils.getRequestResponse(String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH, bin));
            if (StringUtils.isNotBlank(wsmActivitiesResponse)) {
                for (Object wsmActivity : new JSONArray(wsmActivitiesResponse)) {
                    JSONObject activity = (JSONObject) wsmActivity;
                    String containerId = activity.getString("containerId");
                    String status = activity.getString("status");
                    String id = activity.get("id").toString();
                    log.info("bin Id: {} and id: {}", containerId, id);
                    binActivity.put(containerId, id);
                    org.junit.Assert.assertTrue("Prep Activity Completed", StringUtils.equalsIgnoreCase(status, "OPEN"));
                }
            } else {
                org.junit.Assert.assertTrue("No WSM Activities for bin" + bin, false);
            }
        });
    }


    public Map<String, String> verifyThresholdLimit(String configKey) throws Exception {
        System.out.println("ReadHostConfiguration.CONTAINER_FORMAT_CONFIG_URL.value() :-" + ReadHostConfiguration.GET_PACKAWAY_THRESHOLD_LIMIT_URL.value());
        String response = CommonUtils.getRequestResponse((ReadHostConfiguration.GET_PACKAWAY_THRESHOLD_LIMIT_URL.value()));
        response = '[' + response + ']';
        HashMap<String, String> configvaluesMap = new HashMap<>();
        if (!response.isEmpty()) {
            JSONArray jsnarrinital = new JSONArray(response);
            JSONObject jsonObject = jsnarrinital.getJSONObject(0);
            JSONArray jsnarr = new JSONArray(jsonObject.get("configValue").toString());
            List<Map<String, String>> configvaluesMapList = CommonUtils.getListOfMapsFromJsonArray(jsnarr);
            for (int i = 0; i <= configvaluesMapList.size(); i++) {
                if (configvaluesMapList.get(i).get("StorageType").equalsIgnoreCase(configKey)) {
                    configvaluesMap = (HashMap<String, String>) configvaluesMapList.get(i);
                    break;
                }
            }
            log.info("prefix and length: {}", configvaluesMap);

        } else {
            log.info("Null response from the config Servcie");
            org.junit.Assert.assertTrue(false);
        }
        return configvaluesMap;
    }

    public void sortBinsforMultBins(int unsortedCount) throws Exception {
        for (int i = 0; i < unsortedCount; i++) {
            if (autocloseAndLocatePallet()) {
                if (unsortedBins.size() != 0) {
                    scanBinsPackAwaySort();
                }
            } else {
                if (unsortedBins.size() == 1) {
                    validInput(unsortedBins.get(0));
					validateScanPalletScreen();
					packAwaySortPage.ScanPalletWithOutEnter();
                   /* validateScanLocationScreen();
                    packAwaySortPage.ScanLocationWithOutEnter();*/
                    validateContainerStatus("BinBox", "SRT");
                    validatePalletBinContainerRelationship();
                    updateSortData();
//                    forceCloseAndLocatePallet();
                    break;
                } else {
                    validInput(unsortedBins.get(0));
                    scanDisplayedPallet();
                    TimeUnit.SECONDS.sleep(10);
                    validateContainerStatus("BinBox", "SRT");
                    validatePalletBinContainerRelationship();
                    validateWSMStatus("COMPLETED");

                }
            }
        }
    }

    public void verifyDisplayedPallet(String palletType) {
        String displayedpalletType = packAwaySortPage.getDetailsFromPage().get(PackAwaySortPage.LOCATION_TYPE);
        Assert.assertEquals(displayedpalletType, palletType);
        StepDetail.addDetail("Validated " + displayedpalletType + " status to be : " + palletType, true);

    }

    public void updateSortingLocationData(String zone, String containerType) {

        try {
            String getResponse = WhmRestCoreAutomationUtils.getRequestResponse(String.format(SortEndPoint.sortLocationGetService, "", "", zone, containerType)).asString();
            log.info("Open Sort Location Response: " + getResponse);
            if (StringUtils.isNotBlank(getResponse)) {
                JsonPath sortResponse = new JsonPath(getResponse);
                JSONObject sortCloseRequest = new JSONObject();
                sortCloseRequest.put("id", sortResponse.getInt("id"));
                sortCloseRequest.put("locationStatus", "COMPLETED");
                sortCloseRequest.put("count", 0);
                sortCloseRequest.put("version", 0);
                sortCloseRequest.put("disable", true);
                WhmRestCoreAutomationUtils.putRequestResponse(SortEndPoint.sortLocationPutService, sortCloseRequest.toString());
                log.info("Updated Open Location on Sort Table: " + sortResponse.getString("lcnBrcd"));
            }

        } catch (Exception e) {
            log.info("Unable to update Sort Table", e);
        }
    }

    @When("Pack Away Sorting is performed for created bins and staged to $stageLocn location")
    public void packAwaySorting(String stageLocn) throws Exception {
        CommonUtil.deleteRequest(String.format(InventoryEndPoint.CONSUME_TOTE, "7221", stageLocn, "TestAutomation"), 204);
        Table<String, String, Integer> binMapExpected = (Table<String, String, Integer>) dataStorage.getStoredData().get("ExpectedBinBoxMap");
        packAwaySortPage.selectPackAwaySortingFromMainMenu();
        packAwaySortPage.buttonClick();
        int count = 1;
        for (Table.Cell<String, String, Integer> cell : binMapExpected.cellSet()) {
            log.info("BinBox :", cell.getRowKey());
            packAwaySortPage.scanBinBox(cell.getRowKey());
            if (count == 1) {
                packAwaySortPage.scanLocationBarcode(stageLocn);
                scanPallet();
            } else {
                scanDisplayedPallet();
            }
            count++;
        }

    }
}