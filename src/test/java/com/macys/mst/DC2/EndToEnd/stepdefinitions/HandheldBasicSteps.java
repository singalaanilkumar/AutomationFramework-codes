package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.pageobjects.PrintTicketPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.CycleCount;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.DC2SubMenu;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.Putaway;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.HAFPages.HAFSubMenu;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.HAFPages.Picking;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.HAFPages.PreSort;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.Home;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.LocationMenu;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.Menu;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.SubMenu;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.outboundPages.OBSubMenu;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import org.jbehave.core.annotations.Composite;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.macys.mst.DC2.EndToEnd.pageobjects.BasePage.driver;

public class HandheldBasicSteps {
    private ValidationUtil validationUtils = new ValidationUtil();
    Home home = new Home();
    Menu menu;
    LocationMenu locMenu;
    SubMenu subMenu;
    Picking picking = new Picking();
    PreSort presort = new PreSort();
    Putaway putaway = new Putaway();
    CycleCount cycleCount = new CycleCount();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    RandomUtil randomUtil = new RandomUtil();
    List<String> validCycleCountStatuses = Arrays.asList(new String[]{"VSC", "PTW"});
    PrintTicketPage printTicketPage = PageFactory.initElements(driver, PrintTicketPage.class);

    CycleCountSteps cycleCountSteps = new CycleCountSteps();

    @Given("Handheld home page")
    public void logedInHome() {
        home.login();
        menu = new Menu();
        menu.validateRFOption();
        menu.validateHAFOption();
        menu.validateOutboundOption();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Logged in ", "Success", true);      

    }


    @Given("user logged in to select $location")
    public void homePageByLocation(String location) throws InterruptedException {
        home.login();

        locMenu = new LocationMenu();
        TimeUnit.SECONDS.sleep(2);
        locMenu.selectGivenLocation(location);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Location selected ", location, true);
        menu = new Menu();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Location selected ", "Success", true);
    }


    @Then("clean the driver")
    public void cleanDriver() {
        home.cleanUpDriver();
    }

    @When("user click on main menu $mainMenu")
    public void clickOnMainMenu(String mainMenu) throws InterruptedException {
        menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.Outbound));
        boolean isValid = false;
        if ("DC2.0".equalsIgnoreCase(mainMenu)) {
            menu.RFOption.click();
            menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.createTote));
            isValid = menu.createTote.isDisplayed() && menu.createTote.isEnabled();
            subMenu = new DC2SubMenu();
        } else if ("HAF".equalsIgnoreCase(mainMenu)) {
            menu.HAFOption.click();
            menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.associateASN));
            isValid = menu.associateASN.isDisplayed() && menu.associateASN.isEnabled();
            subMenu = new HAFSubMenu();
        } else if ("Outbound".equalsIgnoreCase(mainMenu)) {
            menu.Outbound.click();
            menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.dockScan));
            isValid = menu.dockScan.isDisplayed() && menu.dockScan.isEnabled();
            subMenu = new OBSubMenu();
        } else {
            Assert.fail("No such option available in main menu");
        }
        CommonUtils.doJbehavereportConsolelogAndAssertion("Main menu selected", mainMenu, isValid);
    }

    @Given("user click on sub menu $selectedSubMenu")
    @When("user click on sub menu $selectedSubMenu")
    public void clickOnSubMenu(String selectedSubMenu) {
        subMenu.clickOnGivenSubMenu(selectedSubMenu);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Submenu selected", selectedSubMenu, true);
    }

    @Then("presort cases to $location and validate")
    public void presortCases(String location) {
        clickOnSubMenu("PreSort");
        Map<String, Map<Long, Integer>> caseSkuQtyMap = (Map<String, Map<Long, Integer>>) dataStorage.getStoredData().get("CaseSkuQtyMap");
        Map<String, String> caseToToteRelation = new HashMap<>();
        List<String> listOfTotes = new ArrayList<>();
        dataStorage.getStoredData().put("toteAfterPresort", listOfTotes);
        try {
            for (String barcode : caseSkuQtyMap.keySet()) {
                HAFSteps.clearPresortAisles(location);
                presort.pageLoadWait();
                TimeUnit.SECONDS.sleep(2);
                presort.enterScanCase(barcode);
                presort.pageLoadWait();
                TimeUnit.SECONDS.sleep(2);
                presort.enterScanLocation(location);
                String tote = randomUtil.getRandomValue("50-D-18");
                listOfTotes.add(tote);

                Map<Long, Integer> skuQtyMap = caseSkuQtyMap.get(barcode);
                int skuQty = 0;
                for (Long sku : skuQtyMap.keySet()) {
                    skuQty += skuQtyMap.get(sku);

                    for (int size = 0; size < skuQty; size++) {
                        presort.pageLoadWait();
                        TimeUnit.SECONDS.sleep(1);
                        presort.scanSku(sku.toString());

                        presort.pageLoadWait();
                        TimeUnit.SECONDS.sleep(1);
                        presort.scanTote(tote);

                        if (size == 0) {
                            presort.pageLoadWait();
                            TimeUnit.SECONDS.sleep(1);
                            presort.enterScanLocation(location);
                        }

                        try {
                            if (size == skuQty - 1) {
                                presort.pageLoadWait();
                                TimeUnit.SECONDS.sleep(2);
                                presort.buttonYES.click();
                            }
                        } catch (Exception e) {
                            Assert.fail("Is case empty alert didn't appear as expected");
                        }
                    }
                }
                caseToToteRelation.put(tote, barcode);
            }
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
        dataStorage.getStoredData().put("caseToToteRelation", caseToToteRelation);
        presort.goBackToPresort.click();
    }

    @Then("release all the totes")
    public void closeAllTotes() {
        clickOnSubMenu("PreSort");
        presort.releaseAllTotes((List<String>) dataStorage.getStoredData().get("toteAfterPresort"));
    }

    /* user can send value in below format and this method will process it
     $putawayloc = PA10M040:1,PA10M038:1,PA10M036:1*/
    @Then("perform putaway as $putawayloc and validate")
    public void scanContainerputAway(String putawayloc) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        putaway.selectPutawaySubMenu("Case");
        putaway.scanContainerputAway(putawayloc);
        putaway.backButton.click();
    }

    @Then("perform putaway on created ICQA binboxes as $putawayloc and validate")
    public void scanBinboxputAway(String putawayloc) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        printTicketPage.selectOptionFromMenu("Putaway Pallet");
        putaway.selectPutawaySubMenu("Binbox");
        putaway.scanContainerputAway(putawayloc);
        putaway.backButton.click();
    }

    @Then("pick and stage all cases")
    public void doPicking() throws InterruptedException {

        clickOnSubMenu("Picking");

        Map<String, String> locationToScan = (Map) dataStorage.getStoredData().get("pickLocationAndNumberOfCases");
        List<String> pickLocs = new ArrayList<>();
        for (String loc : locationToScan.keySet()) {
            pickLocs.add(loc);
        }

        //allowed delay time to get all the activities created
        TimeUnit.SECONDS.sleep(2);

        if (!pickLocs.isEmpty()) {
            picking.enterScanLocation(pickLocs.get(0));
            picking.clickOnActivityLink();
            picking.scanPallet();
            picking.scanCase();
        }
    }

    @Given("user signed in and selected $mainMenu")
    @Then("user signed in and selected $mainMenu")
    @Composite(steps = {"Given Handheld home page",
            "When user click on main menu $mainMenu"})
    public void loginAndSelectMainOption(String mainMenu) {
    }

    @Given("user loged in and selected $subMenu of $mainMenu")
    @Composite(steps = {"Given user signed in and selected $mainMenu",
            "When user click on sub menu $selectedSubMenu"})
    public void loginSelectMenuAndSubmenu(String selectedSubMenu, String mainMenu) {
    }

    @Given("user logs in and selected $selectedSubMenu of $mainMenu for $location")
    @Composite(steps = {"Given user logged in to select location",
            "When user click on main menu $mainMenu",
            "When user click on sub menu $selectedSubMenu"})
    public void loginSelectMenuAndSubmenuByLocation(String location, String selectedSubMenu, String mainMenu) {
    }

    @Then("perform cycle count of all visible containers and validate")
    public void doCyclecount() throws InterruptedException {
        cycleCount.clickNavOption("Cycle Count");
        doCycelCountOfValidInventories();
        doCycelCountOfContainersFromDifferentLocation();
        doCycelCountOfNonLocatedInventories();
        doLWCycelCountOfLocatedInventories();
        doCycelCountOfSystemicallyNotExistingInventories();
        doCycelCountOfNotSupportedContainers();
        cycleCount.back();
    }

    @Then("perform cycle count for $scenario")
    public void doCyclecount(String scenario) throws InterruptedException {
        cycleCount.clickNavOption("Cycle Count");
        switch (scenario) {
            case "ValidInventorywithLocation":
                doCycelCountOfValidInventories();
                break;
            case "ValidInventorywithIncorrectLocation":
                doCycelCountOfContainersFromDifferentLocation();
                break;
            case "ValidInventorywithNoLocation":
                doCycelCountOfNonLocatedInventories();
                break;
            case "ValidSystemInventoryLW":
                doLWCycelCountOfLocatedInventories();
                break;
            case "SystemicallyNotExistingInventory":
                doCycelCountOfSystemicallyNotExistingInventories();
                break;
            case "NotSupportedContainers":
                doCycelCountOfNotSupportedContainers();
                break;
        }
        cycleCount.back();
    }

    private void doCycelCountOfValidInventories() throws InterruptedException {

        List<String> validInventoryList = (List<String>) dataStorage.getStoredData().get("inventoryListFromValidLocation");
        List<InventoryContainer> containers = getInventoryContainerListForBarcodes(validInventoryList);
        List<InventoryContainer> containersToBeCorrected = new ArrayList<>();
        List<InventoryContainer> containersNotToBeCorrected = new ArrayList<>();

        for (InventoryContainer container : containers) {
            if (validCycleCountStatuses.contains(container.getContainer().getContainerStatusCode())) {
                containersToBeCorrected.add(container);
            } else {
                containersNotToBeCorrected.add(container);
            }
        }

        if (null != validInventoryList) {
            String scannedLocation = containers.get(0).getContainer().getContainerRelationshipList().get(0).getParentContainer();
            if (null != scannedLocation && !scannedLocation.isEmpty()) {
                cycleCount.scanLocation(scannedLocation);
                try {
                    if (cycleCount.totalContainersScanned.isDisplayed()) {
                        cycleCount.endLocation();
                        cycleCount.scanLocation(scannedLocation);
                        scanBarcodesAndEndLocation(validInventoryList, scannedLocation);
                    }
                } catch (NoSuchElementException exc) {
                    scanBarcodesAndEndLocation(validInventoryList, scannedLocation);
                }
                TimeUnit.SECONDS.sleep(5);
                cycleCountSteps.validateUpdatedContainersAfterCC(containersToBeCorrected, scannedLocation);
                cycleCountSteps.validateUnmodifiedContainersAfterCC(containersNotToBeCorrected, scannedLocation);
            }
        }
    }

    private void doCycelCountOfContainersFromDifferentLocation() throws InterruptedException {
        List<String> inventoryListFromIncorrectLocation = (List<String>) dataStorage.getStoredData().get("inventoryListFromIncorrectLocation");

        List<InventoryContainer> containers = getInventoryContainerListForBarcodes(inventoryListFromIncorrectLocation);
        List<InventoryContainer> containersToBeCorrected = new ArrayList<>();
        List<InventoryContainer> containersNotToBeCorrected = new ArrayList<>();

        for (InventoryContainer container : containers) {
            if (validCycleCountStatuses.contains(container.getContainer().getContainerStatusCode())) {
                containersToBeCorrected.add(container);
            } else {
                containersNotToBeCorrected.add(container);
            }
        }

        if (null != inventoryListFromIncorrectLocation) {
            String scannedLocation = "PR01A001";             //loc type packaway stage
            TimeUnit.SECONDS.sleep(1);
            cycleCount.scanLocation(scannedLocation);
            try {
                if (cycleCount.totalContainersScanned.isDisplayed()) {
                    cycleCount.endLocation();
                    cycleCount.scanLocation(scannedLocation);
                    scanBarcodesAndEndLocation(inventoryListFromIncorrectLocation, scannedLocation);
                }
            } catch (NoSuchElementException exc) {
                scanBarcodesAndEndLocation(inventoryListFromIncorrectLocation, scannedLocation);
            }
            TimeUnit.SECONDS.sleep(5);
            cycleCountSteps.validateUpdatedContainersAfterCC(containersToBeCorrected, scannedLocation);
            cycleCountSteps.validateUnmodifiedContainersAfterCC(containersNotToBeCorrected, scannedLocation);
        }
    }

    private void doCycelCountOfNonLocatedInventories() throws InterruptedException {
        List<String> inventoryListWithoutLocation = (List<String>) dataStorage.getStoredData().get("inventoryListWithoutLocation");

        List<InventoryContainer> containers = getInventoryContainerListForBarcodes(inventoryListWithoutLocation);
        List<InventoryContainer> containersToBeCorrected = new ArrayList<>();
        List<InventoryContainer> containersNotToBeCorrected = new ArrayList<>();

        for (InventoryContainer container : containers) {
            if (validCycleCountStatuses.contains(container.getContainer().getContainerStatusCode())) {
                containersToBeCorrected.add(container);
            } else {
                containersNotToBeCorrected.add(container);
            }
        }

        if (null != inventoryListWithoutLocation) {
            String scannedLocation = "JD03A002";             //loc type drop location
            TimeUnit.SECONDS.sleep(1);
            cycleCount.scanLocation(scannedLocation);
            try {
                if (cycleCount.totalContainersScanned.isDisplayed()) {
                    cycleCount.endLocation();
                    cycleCount.scanLocation(scannedLocation);
                    scanBarcodesAndEndLocation(inventoryListWithoutLocation, scannedLocation);
                }
            } catch (NoSuchElementException exc) {
                scanBarcodesAndEndLocation(inventoryListWithoutLocation, scannedLocation);
            }
            TimeUnit.SECONDS.sleep(5);
            cycleCountSteps.validateUpdatedContainersAfterCC(containersToBeCorrected, scannedLocation);
            cycleCountSteps.validateUnmodifiedContainersAfterCC(containersNotToBeCorrected, scannedLocation);
        }
    }

    private void doLWCycelCountOfLocatedInventories() throws InterruptedException {
        List<String> inventoryListForPartialCycleCount = (List<String>) dataStorage.getStoredData().get("inventoryListForPartialCycleCount");

        List<InventoryContainer> containers = getInventoryContainerListForBarcodes(inventoryListForPartialCycleCount);
        if (null != inventoryListForPartialCycleCount) {
            String scannedLocation = containers.get(0).getContainer().getContainerRelationshipList().get(0).getParentContainer();                //loc type flow stage
            TimeUnit.SECONDS.sleep(1);
            try {
                if (cycleCount.totalContainersScanned.isDisplayed()) {
                    cycleCount.endLocation();
                    cycleCount.scanLocation(scannedLocation);
                    cycleCount.endLocation();
                }
            } catch (NoSuchElementException exc) {
                cycleCount.scanLocation(scannedLocation);
                cycleCount.endLocation();
            }
            TimeUnit.SECONDS.sleep(5);
            cycleCountSteps.validateLWContainersAfterCC(containers, scannedLocation);
        }
    }

    private void doCycelCountOfSystemicallyNotExistingInventories() throws InterruptedException {
        List<String> randomBarcodeswithValidPrefix = (List<String>) dataStorage.getStoredData().get("randomBarcodeswithValidPrefix");
        if (null != randomBarcodeswithValidPrefix) {
            //loc type flow stage
            String locationNumber = "KS01A007";
            TimeUnit.SECONDS.sleep(1);
            cycleCount.scanLocation(locationNumber);
            try {
                if (cycleCount.totalContainersScanned.isDisplayed()) {
                    cycleCount.endLocation();
                    cycleCount.scanLocation(locationNumber);
                    scanBarcodesAndEndLocation(randomBarcodeswithValidPrefix, locationNumber);
                }
            } catch (NoSuchElementException exc) {
                scanBarcodesAndEndLocation(randomBarcodeswithValidPrefix, locationNumber);
            }
            TimeUnit.SECONDS.sleep(5);
            cycleCountSteps.validateContainersNotInventoried(randomBarcodeswithValidPrefix, locationNumber);
        }
    }

    private void doCycelCountOfNotSupportedContainers() throws InterruptedException {
        List<String> randomInvWithInvalidBarcode = (List<String>) dataStorage.getStoredData().get("randomInvWithInvalidBarcode");
        if (null != randomInvWithInvalidBarcode) {
            //loc type processing stage
            String locationNumber = "CS02A007";
            TimeUnit.SECONDS.sleep(1);
            cycleCount.scanLocation(locationNumber);
            try {
                if (cycleCount.totalContainersScanned.isDisplayed()) {
                    cycleCount.endLocation();
                    cycleCount.scanLocation(locationNumber);
                    for (String barcode : randomInvWithInvalidBarcode) {
                        TimeUnit.SECONDS.sleep(2);
                        cycleCount.scanContainer(barcode);
                        TimeUnit.SECONDS.sleep(1);
                        cycleCount.closeButton();
                    }
                }
            } catch (NoSuchElementException exc) {
                for (String barcode : randomInvWithInvalidBarcode) {
                    TimeUnit.SECONDS.sleep(2);
                    cycleCount.scanContainer(barcode);
                    TimeUnit.SECONDS.sleep(1);
                    cycleCount.closeButton();
                }
            }
            CommonUtils.doJbehavereportConsolelogAndAssertion("Page is not allowing to scan non-supported containers : ", randomInvWithInvalidBarcode.toString(), cycleCount.dataTableAndCountFieldAreNotPresent());
        }
    }

    public List<InventoryContainer> getInventoryContainerListForBarcodes(List<String> barcodeList) {
        List<InventoryContainer> containersObjectList = new ArrayList<>();
        for (String barcode : barcodeList) {
            containersObjectList.add(CommonUtils.getInventory(barcode));
        }
        return containersObjectList;
    }

    public void scanBarcodesAndEndLocation(List<String> barcodeList, String scannedLocation) throws InterruptedException {
        for (String barcode : barcodeList) {
            //safe delay to get the barcode added properly to UI and DB table
            TimeUnit.SECONDS.sleep(2);
            cycleCount.scanContainer(barcode);
        }
        TimeUnit.SECONDS.sleep(2); //wait time to read the page content        
        CommonUtils.doJbehavereportConsolelogAndAssertion("Page successfully scanned supported containers : ", barcodeList.toString(), validateUIPageData(barcodeList));
        TimeUnit.SECONDS.sleep(1);
        cycleCount.endLocation();
        TimeUnit.SECONDS.sleep(5);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Page successfully scanned supported containers & performed end location : ", barcodeList.toString(), true);
        TimeUnit.SECONDS.sleep(5);
    }

    public boolean validateUIPageData(List<String> barcodeList) throws InterruptedException {
        try {
            int totalContScanned = cycleCount.getTotalContainersScanned();
            boolean barcodeListSizeVStotalContScan = barcodeList.size() == totalContScanned;
            boolean totalContScannedVsActualCountInUI = totalContScanned == cycleCount.getActualCountOfRowsInUITable();
            boolean barcodeListVsUItableList = barcodeList.equals(cycleCount.getBarcodeListFromUITable());

            boolean finalValidationResult = barcodeListSizeVStotalContScan && totalContScannedVsActualCountInUI && barcodeListVsUItableList;

            if (!finalValidationResult) {
                cycleCount.endLocation();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Endedn location successfully", "END LOCATION CLICKED", true);
            }

            CommonUtils.doJbehavereportConsolelogAndAssertion("Expected data vs data presnt in UI barcodeListSizeVStotalContScan, totalContScannedVsActualCountInUI, barcodeListVsUItableList", barcodeListSizeVStotalContScan + "," + totalContScannedVsActualCountInUI + "," + barcodeListVsUItableList, finalValidationResult);
            return finalValidationResult;
        } catch (Exception ex) {
            cycleCount.endLocation();
            CommonUtils.doJbehavereportConsolelogAndAssertion("Exception occurred in Validation,", "But performed END LOCATION", false);
        }
        return false;
    }


}
