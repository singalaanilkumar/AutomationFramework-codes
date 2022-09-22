package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.selenium.PageObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class SortToStorePage extends PageObject {

    private static final String SELECT_SORT_TO_STORE = "SortToStore";
    private static final String SELECT_CREATE_TOTE = "Create Tote";

    public SortToStorePage(WebDriver driver) {
        super(driver);
    }


    @FindBy(xpath = "//*[contains(text(), 'SortToStore')]")
    WebElement sortToStore;

    @FindBy(xpath = "//strong[contains(text(),'Scan Zone')]")
    WebElement scanZoneLbl;

    @FindBy(xpath = "//strong[contains(text(),'Scan Container')]")
    WebElement scanToteLbl;

    @FindBy(xpath = "//SPAN[text()='Exit']")
    WebElement exitButton;

    @FindBy(xpath = "//SPAN[text()='Back']")
    WebElement backButton;

    @FindBy(xpath = "//strong[contains(text(),'Scan Carton : ')]")
    WebElement scanCartonLbl;

    @FindBy(xpath = "//strong[contains(text(),'Location : ')]")
    WebElement locationLbl;

    @FindBy(xpath = "//strong[contains(text(),'Units to put')]//parent::div")
    WebElement units2PutLabel;

    @FindBy(xpath = "//span[contains(text(),'Close Carton')]")
    WebElement closeCartonBtn;

    @FindBy(xpath = "//span[contains(text(),'Yes')]")
    WebElement cartonLocateBtn;

    @FindBy(xpath = "//strong[contains(text(),'Scan Staging Location:')]")
    WebElement scanCartonStageLocLbl;

    @FindBy(xpath = "//strong[contains(text(),'Store : ')]/parent::div")
    WebElement StoreNbr;

    @FindBy(xpath = "//strong[contains(text(),'Container ID :')]")
    WebElement containerIdLbl;

    @FindBy(xpath = "//INPUT[@type='text']")
    WebElement inputText;

    @FindBy(xpath = "//strong[contains(text(),'Scan Location:')]/parent::div")
    WebElement scanLocationLabel;

    @FindBy(xpath = "//input[@id='entryBox']")
    WebElement entryBox;

    @FindBy(xpath = "//strong[text()='Process Area :']/..")
    WebElement processAreaLbl;

    @FindBy(xpath = "//span[text()='EndTote']")
    WebElement endToteButton;

    @FindBy(xpath = "//STRONG[text()='PO']/..")
    WebElement poLabel;

    @FindBy(xpath = "//input[@type='number']")
    WebElement enterQtyinput;

    @FindBy(xpath = "//*[text()='Stage Tote']")
    WebElement stageToteTitleHeader;

    @FindBy(xpath = "//b[contains(text(), 'DC2.0 RF Options')]")
    WebElement rfOptions;

    @FindBy(xpath = "//*[contains(text(), 'Create Tote')]")
    WebElement createTote;

    public static void sortToStore() {
    }

    public void navigateToCreateTote() throws Exception {
        getWait(5).until(elementToBeClickable(rfOptions));

        Assert.assertEquals(true, rfOptions.isDisplayed());

        if (rfOptions.isDisplayed()) {
            rfOptions.click();
            getWait(5).until(elementToBeClickable(sortToStore));

            Assert.assertEquals(true, createTote.isDisplayed());
        }
    }

    public void selectSortToStore() {
        getWait(30).until(visibilityOf(sortToStore));
        if (sortToStore.isDisplayed()) {
            selectOptionFromMenu(driver, SELECT_SORT_TO_STORE);
        }
    }

    public void selectOptionFromMenu(WebDriver driver, String param) {
        switch (param) {
            case SELECT_CREATE_TOTE:
                getWait(5).until(visibilityOf(sortToStore));
                sortToStore.click();
                break;
        }
    }

    public void clickOnSelectProcessArea() {
        WebElement processAreaDropdown = driver.findElement(By.id("selector"));
        getWait(25).until(visibilityOf(processAreaDropdown));
        processAreaDropdown.click();
    }

    public void selectProcessArea(String selectProcessAreaString) throws Exception {
        WebElement processAreaDropdown = driver.findElement(By.id("selector"));
        getWait(25).until(visibilityOf(processAreaDropdown));

        Select selDropdown = new Select(processAreaDropdown);

        List<WebElement> options = selDropdown.getOptions();
        validateDropDown(options);

        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getText().equalsIgnoreCase(selectProcessAreaString)) {
                StepDetail.addDetail("Selecting the process area: " + options.get(i).getText(), true);
                log.info("Selecting the process area :{}", options.get(i).getText());
                options.get(i).click();
                break;
            }
        }

        if ((driver.findElement(By.xpath("//body/div[@id='app']/div/div/div[1]")).getText()).contains("Select Process Area")) {
            Select dropdown = new Select(driver.findElement(By.xpath("//select[@id='selector']")));
            dropdown.selectByValue(selectProcessAreaString);
        }
    }


    private void validateDropDown(List<WebElement> options) {
        List<String> processAreaDropdownList = getProcessAreaDropdownValuesAsList(options);
        DataCreateModule dataCreateModule = new DataCreateModule();
        String response = dataCreateModule.fetchConfigurations("offpricedc", "handheld", "ProcessAreaConfig");
        log.info("Response value :{}", response);
        List<String> processAreaServiceList = getProcessAreaConfigs(response);
        log.info("Process Area config list: {}", processAreaServiceList);
        log.info("Screen ProcessArea list: {}", processAreaDropdownList);
        CommonUtils.validateLists(processAreaDropdownList, processAreaServiceList);
        log.info("Select process area screen validation passed");
        StepDetail.addDetail("Select process area screen validation passed", true);

    }

    public List<String> getProcessAreaDropdownValuesAsList(List<WebElement> options) {
        List<String> optionsList = new ArrayList<>();
        options.forEach(webElement -> {
            optionsList.add(webElement.getText());
        });

        return optionsList;

    }

    public void validateScanTotePage(String selectedProcessArea) {
        log.info("Validating Scan tote screen ...");
        getWait(5).until(visibilityOf(backButton));

        String[] actualProcessArea = processAreaLbl.getText().split(":");
        Assert.assertEquals("Process Area", actualProcessArea[0].trim());
        Assert.assertEquals(selectedProcessArea, actualProcessArea[1].trim());

        StepDetail.addDetail("Screen Elements Visibility Process Area txt--> " + actualProcessArea,
                selectedProcessArea.equalsIgnoreCase(actualProcessArea[1].trim()));

        Assert.assertTrue("exitButton check :", exitButton.isDisplayed());
        Assert.assertTrue("backButton check: ", backButton.isDisplayed());
    }

    public List<String> getProcessAreaConfigs(String reponse) {

        String processAreaValues = "";
        List<String> processAreaList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(reponse);
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            processAreaValues = jsonObject.getString("configValue");
            JSONArray configValueArray = new JSONArray(processAreaValues);
            for (Map<String, String> configMap : CommonUtils.getListOfMapsFromJsonArray(configValueArray)) {
                log.info("Adding the process area:{}", configMap.get("processArea"));
                processAreaList.add(configMap.get("processArea"));
            }
        }

        StepDetail.addDetail("List of Process areas from the config servcie: " + processAreaList, true);
        return processAreaList;

    }

    public void scanToteId(String s) {
        getWait(60).until(visibilityOf(inputText));
        inputText.sendKeys(s);
        inputText.sendKeys(Keys.ENTER);
        getWait(60).until(visibilityOf(entryBox));
        Assert.assertEquals(true, entryBox.isDisplayed());
        getWait(60);
        StepDetail.addDetail("Scanned barcode " + s, true);

    }


    public String getPONbr() {
        if (poLabel.getText().contains(":")) {
            String[] value = poLabel.getText().split(":");
            return value[1].trim();
        }
        return null;
    }

    private HashMap<String, String> getScreenValues(Map<String, String> screenElements) {
        HashMap<String, String> screenValuesMap = new HashMap<>();

        screenValuesMap.put("skuUpc", screenElements.get("SKU"));
        screenValuesMap.put("colorDesc", screenElements.get("Color"));
        screenValuesMap.put("sizeDesc", screenElements.get("Size"));
        screenValuesMap.put("poNbr", screenElements.get("PO"));
        screenValuesMap.put("pid", screenElements.get("PID"));
        return screenValuesMap;
    }

    public void enterQty(String quantity, boolean hasInnerPack) {
        if (hasInnerPack) {
            getWait(10).until(visibilityOf(entryBox));
            entryBox.sendKeys("2");
            entryBox.sendKeys(Keys.ENTER);
        } else {
            getWait(15).until(visibilityOf(enterQtyinput));
            enterQtyinput.clear();
            enterQtyinput.sendKeys(quantity);
        }
    }

    public void clickButton(String button) {

        if (button.equalsIgnoreCase("back")) {
            getWait(5).until(visibilityOf(backButton));
            backButton.click();
        } else if (button.equalsIgnoreCase("exit")) {
            getWait(25).until(visibilityOf(exitButton));
            exitButton.click();
        } else if (button.equalsIgnoreCase("End Tote")) {
            getWait(5).until(visibilityOf(endToteButton));
            endToteButton.click();
        } else if (button.equalsIgnoreCase("exitButton")) {
            getWait(5).until(visibilityOf(exitButton));
            exitButton.click();
        }
    }

    public void clickButton(String button, WebDriver driver) {
        List<WebElement> buttons = driver.findElements(By.xpath("//span[contains(@class,'MuiButton')]"));
        for (WebElement buttonValue : buttons) {
            if (buttonValue.getText().equalsIgnoreCase(button)) {
                buttonValue.click();
                break;
            }
        }
    }

    public void validateAndScanStagingLocation(String tote, String poNbr, String param, String stageLocation) {
        StepDetail.addDetail("Validating the select process area screen", true);
        getWait(5).until(visibilityOf(stageToteTitleHeader));
        getWait(5).until(visibilityOf(exitButton));

        Map<String, String> scanStageLocationScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div");
        if (scanStageLocationScreenElements.containsKey("EXIT") && stageToteTitleHeader.isDisplayed()) {
            log.info("Exit button and Stage tote header is displayed in Scan Location Page");
            StepDetail.addDetail("Exit button and Stage tote header is displayed in Scan Location Page", true);

            Assert.assertEquals(tote, scanStageLocationScreenElements.get("Tote ID"));
            StepDetail.addDetail("Displayed Tote id: " + scanStageLocationScreenElements.get("Tote ID"), true);
            StepDetail.addDetail(" Tote ID Scanned:" + tote, true);

            try {
                userScansAValidStagingLane(param, stageLocation);

            } catch (Exception e) {
                Assert.fail("Unable to assign location ID");
            }

        } else {
            log.info("Exit button or  stage tote header is not displayed in Scan Loccation Page");
            StepDetail.addDetail("Exit button or  stage tote header is not displayed in Scan Loccation Pag", true);
            Assert.assertFalse(true);
        }
    }

    public List<String> getLocationsAssignedToPO(String PONbr) {
        List<String> locationsList = new ArrayList<>();
        String response = CommonUtils.getRequestResponse(ReadHostConfiguration.LOCATIONS_ASSIGNED_TO_PO_URL.value()
                .replace("{locnNbr}", ReadHostConfiguration.LOCATION_NUMBER.value()).replace("{PO}", PONbr));
        if (!response.isEmpty()) {
            response = response.replace("[", "").replace("]", "").replaceAll("\"", "");
            locationsList = Arrays.asList(response.trim().split(","));
        }
        log.info("getLocationsAssignedToPO: {}", locationsList);
        return locationsList;
    }

    public void scanLocationId(String proclocation) {
        StepDetail.addDetail("Scanning the location" + proclocation, true);
        log.info("Scanning the location {}", proclocation);
        getWait(30);
        getWait(10).until(visibilityOf(entryBox));
        Assert.assertEquals(true, entryBox.isDisplayed());
        Assert.assertEquals(true, entryBox.isDisplayed());
        getWait(10);
        Assert.assertEquals(true, exitButton.isDisplayed());
        Assert.assertEquals(true, backButton.isDisplayed());
        getWait(10);
        getWait(30);
        entryBox.sendKeys(proclocation);
        entryBox.sendKeys(Keys.ENTER);
        StepDetail.addDetail("Scanned Pallet barcode " + proclocation, true);
    }

    public void valiatePracessingLocation(String proclocation) {
        log.info("Scanning the location {}", proclocation);
        getWait(10).until(visibilityOf(entryBox));
        String locFromUI = entryBox.getText();
        Assert.assertEquals("MLS Processing Location", proclocation, locFromUI);
        StepDetail.addDetail("MLS Processing Location" + proclocation, true);

    }


    public void userScansAValidStagingLane(String param, String location) throws Exception {
        log.info("scan staging location number :{}, param: {}", location, param);
        switch (param.toLowerCase()) {
            case "empty":
                scanLocationId(location);
                break;
            case "location assigned to differnt po":
                location = ExpectedDataProperties.getLocations()
                        .get(CommonUtils.getRandomNumber(ExpectedDataProperties.getLocations().size()));
                new DataCreateModule().deleteInventoryForLocation(location);
                new DataCreateModule().createInventoryForLocation(location, "TOTE", CommonUtils.getRandomItem("10"));
                scanLocationId(location);
                break;
        }
    }

    public void validateTheStagingLaneAssignedToCurrentPO(String toteId) {
        String response = RestUtilities.getRequestResponse(
                ReadHostConfiguration.GET_INVENTORY_SERVICE_URL.value().replace("{totebarcode}", toteId));
        StepDetail.addDetail(" Inventory details after scanning location " + response, response != null);
        log.info("Inventory details after scanning location :{}", response);
        Assert.assertTrue("Inventory details after scanning location " + response, response != null);
        JSONObject responseObject = new JSONObject(response);
        JSONArray containerRelationShipArray = responseObject.getJSONObject("container")
                .getJSONArray("containerRelationshipList");
        containerRelationShipArray.forEach(value -> {
            String parentContainer = "";
            String childContainer = "";
            JSONObject containerRealtionshipObject = (JSONObject) value;
            StepDetail.addDetail(" Container relation ship object " + containerRealtionshipObject.toString(),
                    containerRealtionshipObject != null);
            log.info("Container relation ship object: {}", containerRealtionshipObject.toString());
            parentContainer = containerRealtionshipObject.getString("parentContainer");
            childContainer = containerRealtionshipObject.getString("childContainer");
            Assert.assertTrue(childContainer.equalsIgnoreCase(toteId));
        });

    }

    private WebDriverWait getWait(int secs) {
        WebDriverWait wait = new WebDriverWait(driver, secs);
        return wait;
    }

    public Map<String, String> getPoLineDetails(String poLineBarcode) {
        return CommonUtils.getMapFromJson(RestUtilities.getRequestResponse(ReadHostConfiguration.FETCH_POLINE_DTLS_URL.value() + ReadHostConfiguration.LOCATION_NUMBER.value() +
                "/polines/" + poLineBarcode));
    }


    public void selectOptionFromMenu(String param) {
        StepDetail.addDetail("Menu option selected" + param, true);
        switch (param) {
            case "SortToStore":
                getWait(10).until(ExpectedConditions.visibilityOf(sortToStore));
                sortToStore.click();
                break;
        }
    }


    public void validateScanZoneScreen() {
        getWait(10).until(ExpectedConditions.visibilityOf(scanZoneLbl));
        Assert.assertEquals(true, scanZoneLbl.isDisplayed());
        getWait(10).until(ExpectedConditions.visibilityOf(exitButton));
        if (exitButton.isDisplayed()) {
            log.info("Exit button are displayed in Scan Zone Page");
            StepDetail.addDetail("Exit button is displayed in Scan Zone Page", true);
        } else {
            log.info("Exit button is displayed in Scan Station Page");
            StepDetail.addDetail("Exit button is displayed in Scan Zone Page", true);
            Assert.assertFalse(true);
        }
    }


    public void validateScanToteScreen() {
        getWait(10).until(ExpectedConditions.visibilityOf(scanToteLbl));
        Assert.assertEquals(true, scanToteLbl.isDisplayed());
        getWait(10).until(ExpectedConditions.visibilityOf(backButton));
        getWait(10).until(ExpectedConditions.visibilityOf(exitButton));
        if (scanToteLbl.isDisplayed()) {
            log.info("Scan Tote label are displayed in Scan Tote Page");
            StepDetail.addDetail("Scan Tote label are displayed in Scan Tote Page", true);
        } else {
            log.info("Scan Tote label is not displayed in Scan Tote Page");
            StepDetail.addDetail("Scan Tote label are not displayed in Scan Tote Page", true);
            Assert.assertFalse(true);
        }
    }

    public void validateScanLocationScreen() {
        getWait(10).until(ExpectedConditions.visibilityOf(scanLocationLabel));
        Assert.assertEquals(true, scanLocationLabel.isDisplayed());
        getWait(10).until(ExpectedConditions.visibilityOf(backButton));
        getWait(10).until(ExpectedConditions.visibilityOf(exitButton));
        if (scanLocationLabel.isDisplayed()) {
            log.info("Scan Location label are displayed in Scan Tote Page");
            StepDetail.addDetail("Scan Location  label are displayed in Scan Tote Page", true);
        } else {
            log.info("Scan Location label are not displayed in Scan Tote Page");
            StepDetail.addDetail("Scan Location  label are not displayed in Scan Tote Page", true);
            Assert.assertFalse(true);
        }
    }

    public void validateCartonScreen() {
        getWait(10).until(ExpectedConditions.visibilityOf(containerIdLbl));
        Assert.assertEquals(true, containerIdLbl.isDisplayed());
        getWait(10).until(ExpectedConditions.visibilityOf(scanCartonStageLocLbl));
        getWait(10).until(ExpectedConditions.visibilityOf(backButton));
        getWait(10).until(ExpectedConditions.visibilityOf(exitButton));
        getWait(30);
        if (scanCartonStageLocLbl.isDisplayed()) {
            log.info("Scan Stage Location label are displayed in Scan Carton Page");
            StepDetail.addDetail("Scan Location  label are displayed in Scan Tote Page", true);
        } else {
            log.info("Scan Stage Location label  is not displayed in Scan Carton Page");
            StepDetail.addDetail("Scan Location  label are not displayed in Scan Tote Page", true);
            Assert.assertFalse(true);
        }
    }

    public void scanZoneBox(String zone) {
        try {
            TimeUnit.SECONDS.sleep(30);
            entryBox.sendKeys(zone);
            log.info("Scanned zone {} ", zone);
            TimeUnit.SECONDS.sleep(10);
            entryBox.sendKeys(Keys.ENTER);
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void scanCartonId(String cartonId) {
        getWait(30);
        getWait(30).until(visibilityOf(scanCartonLbl));
        Assert.assertEquals(true, scanCartonLbl.isDisplayed());
        Assert.assertEquals(true, exitButton.isDisplayed());
        Assert.assertEquals(true, backButton.isDisplayed());
        Assert.assertEquals(true, locationLbl.isDisplayed());
        inputText.sendKeys(cartonId);
        inputText.sendKeys(Keys.ENTER);

        StepDetail.addDetail("Scanned Pallet barcode " + cartonId, true);

    }

    public String getQty() {
        String cartonLabel = units2PutLabel.getText();
        String[] qty = cartonLabel.split(":");
        log.info("Quantity of carton to be entered :",qty[1]);
        return qty[1].trim();
    }


    public void enterCartonQty(String quantity) {
        getWait(10);
        entryBox.clear();
        entryBox.sendKeys(quantity);
        closeCartonBtn.click();
        getWait(10).until(ExpectedConditions.visibilityOf(cartonLocateBtn));
        cartonLocateBtn.click();
    }


    public void scanStageId(String stageId) {
        getWait(10).until(
                ExpectedConditions.and((
                                ExpectedConditions.visibilityOf(scanCartonStageLocLbl)),
                        ExpectedConditions.visibilityOf(containerIdLbl)));
        inputText.sendKeys(stageId);
        inputText.sendKeys(Keys.ENTER);
    }

    public String getStrNbr() {
        getWait(10).until(ExpectedConditions.visibilityOf(StoreNbr));
        String strnbrwithTxt = StoreNbr.getText();
        log.info("strnbrwithTxt {}", strnbrwithTxt);
        String[] strdtl = strnbrwithTxt.split(":");
        log.info("strdtl {}", strdtl.length);
        String strnbr = strdtl[1].trim();
        return strnbr;
    }

}