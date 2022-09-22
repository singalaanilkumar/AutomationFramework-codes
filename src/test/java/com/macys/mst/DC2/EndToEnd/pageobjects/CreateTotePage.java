package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.execdrivers.ExecutionConfig;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class CreateTotePage extends BasePage {
    private static final String SELECT_CREATE_TOTE = "Create Tote";

    public CreateTotePage(WebDriver driver) {
        super(driver);
    }

    //@FindBy(xpath = "//SELECT[@id='selector']")
    //WebElement selectDropdown;

    @FindBy(xpath = "//li[starts-with(@role,'option')]")
    WebElement selectDropdownOptionValues;

    @FindBy(xpath = "//SPAN[text()='Back']")
    WebElement backButton;

    @FindBy(xpath = "//strong[contains(text(),'Inner Pack Details:')]")
    WebElement InnerPackDetails;

    @FindBy(xpath = "//strong[contains(text(),'No. of Inner Packs:')]")
    WebElement noOfInnerPacks;

    @FindBy(xpath = "//strong[contains(text(),'Tote ID')]")
    WebElement toteIDInnerPack;

    @FindBy(xpath = "//input[@id='entryBox']")
    WebElement entryBoxInnerPack;

    @FindBy(xpath = "//SPAN[text()='Exit']")
    WebElement exitButton;

    @FindBy(xpath = "//strong[contains(text(),'Scan Tote ID')]")
    List<WebElement> scanToteLb2;

    @FindBy(xpath = "//strong[contains(text(),'Scan Tote ID')]")
    WebElement scanToteLbl;

    @FindBy(xpath = "//*[contains(text(),'Single UPC')]")
    WebElement singleUPC;

    @FindBy(xpath = "//b[contains(text(),'Inner Pack')]")
    WebElement innerPack;

    @FindBy(xpath = "//strong[contains(text(),'2. Inner Pack')]")
    WebElement innerPackLbl;

    @FindBy(xpath = "//INPUT[@type='string']")
    WebElement scanToteOptionText;

    @FindBy(xpath = "//INPUT[@type='text']")
    WebElement scanToteInput;

    @FindBy(xpath = "//STRONG[contains(text(),'Scan In-House UPC :')]")
    WebElement scanInHouseUPC;

    @FindBy(xpath = "//INPUT[@type='text']")
    WebElement scanInHouseUPCInput;

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

    @FindBy(xpath = "//STRONG[text()='Locate To :']/following::div[1]")
    WebElement locationsValue;

    @FindBy(xpath = "//INPUT[@type='text']")
    WebElement locationiInput;

    @FindBy(xpath = "//input[@type='text']")
    WebElement userName;

    /**
     * The password
     */
    @FindBy(xpath = "//INPUT[@type='password']")
    WebElement password;

    @FindBy(xpath = "//span[text()='Login']")
    WebElement loginButton;

    @FindBy(xpath = "//*[contains(text(), 'COLUMBUS DC')]")
    WebElement columbusDc;
    // in-case we given space in WebElement it allocted  as error ex:- columbus Dc

    @FindBy(xpath = "//*[contains(text(), 'DC2.0 RF Options')]")
    WebElement rfOptions;

    @FindBy(xpath = "//*[contains(text(), 'Create Tote')]")
    WebElement createTote;

    @FindBy(id = "selector")
    WebElement processAreaDropdown;

    public void navigateToCreateTote() throws Exception {
        log.info("driver = " + driver);

        StepDetail.addDetail(ReadHostConfiguration.RF_MENU_URL.value(), true);
        driver.get(ReadHostConfiguration.RF_MENU_URL.value());
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(20).ignoring(Exception.class).until(visibilityOf(userName));
        userName.clear();
        //userName.sendKeys(ExecutionConfig.appUIUserName);
        userName.sendKeys("");
        getWait(20).ignoring(Exception.class).until(visibilityOf(password));
        password.clear();
        //password.sendKeys(ExecutionConfig.appUIPassword);
        password.sendKeys("");
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(20).ignoring(Exception.class).until(visibilityOf(loginButton));
        loginButton.click();
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());

        getWait(40).ignoring(Exception.class).until(elementToBeClickable(columbusDc));
        Assert.assertEquals(true, columbusDc.isDisplayed());
        if (columbusDc.isDisplayed()) {
            columbusDc.click();
            //getWait(20).ignoring(Exception.class).until(elementToBeClickable(createTote));
            log.info("driver = " + driver.getCurrentUrl());
            log.info("driver = " + driver.getTitle());
            //Assert.assertEquals(true, createTote.isDisplayed());
        }

        getWait(40).ignoring(Exception.class).until(elementToBeClickable(rfOptions));
        Assert.assertEquals(true, rfOptions.isDisplayed());
        if (rfOptions.isDisplayed()) {
            while(!rfOptions.isEnabled()){
                wait(2);
            }
            rfOptions.click();
            getWait(20).ignoring(Exception.class).until(elementToBeClickable(createTote));
            log.info("driver = " + driver.getCurrentUrl());
            log.info("driver = " + driver.getTitle());
            Assert.assertEquals(true, createTote.isDisplayed());
        }
    }

    public void UICacheCleanup() throws Exception {
        try {
            navigateToCreateTote();
            createTote.click();
            getWait(10).ignoring(Exception.class).until(visibilityOf(processAreaDropdown));
            if (!processAreaDropdown.isDisplayed()) {
                String pageTitle = driver.getTitle();
                if (pageTitle.equalsIgnoreCase("CREATE TOTE") && processAreaLbl.isDisplayed()) {
                    exitButton.click();
                }
            }
        } catch (Exception e) {
            log.info("Exception occured while cleaning the UI cache :", e.getMessage());
        }
    }

    public void selectCreateTote() {
        getWait(30).ignoring(Exception.class).until(visibilityOf(createTote));
        if (createTote.isDisplayed()) {
            selectOptionFromMenu(driver, SELECT_CREATE_TOTE);
        }
    }

    public void selectOptionFromMenu(WebDriver driver, String param) {
        switch (param) {
            case SELECT_CREATE_TOTE:
                getWait(5).ignoring(Exception.class).until(visibilityOf(createTote));
                createTote.click();
                break;
        }
    }

    public void clickOnSelectProcessArea() {
        if (scanToteLb2.size() > 0) {
            exitButton.click();
            selectCreateTote();
        }
        getWait(25).ignoring(Exception.class).until(visibilityOf(processAreaDropdown));
        processAreaDropdown.click();
    }

    public void selectProcessArea(String selectProcessAreaString) throws Exception {
        getWait(25).ignoring(Exception.class).until(visibilityOf(processAreaDropdown));

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

    public void validateSingleOrInnerOptionPage() {
        /*getWait(2).until(visibilityOf(singleSKULbl));
        getWait(2).until(visibilityOf(innerPackLbl));
        getWait(2).until(visibilityOf(scanToteOptionText));
        getWait(2).until(visibilityOf(backButton));
        if (singleSKULbl.isDisplayed() && innerPackLbl.isDisplayed() && scanToteOptionText.isDisplayed() && backButton.isDisplayed()) {
            StepDetail.addDetail("Single SKU label, Inner Pack label scanTote Option Textbox and Back button displayed", true);
        }
        Assert.assertTrue("Select Tote Option check: ", scanToteOptionText.isDisplayed());
        Assert.assertTrue("backButton check: ", backButton.isDisplayed());*/

        getWait(2).ignoring(Exception.class).until(visibilityOf(backButton));
        if (backButton.isDisplayed()) {
            Assert.assertTrue("backButton check: ", backButton.isDisplayed());
            List<WebElement> allFields = driver.findElements(By.xpath("//a"));
            long count = allFields.stream().filter(f -> "Single SKU".equalsIgnoreCase(f.getText()) || "Inner Pack".equalsIgnoreCase(f.getText())).count();
            Assert.assertEquals("Select Option Single SKU and Inner Pack displayed", 2, count);
            StepDetail.addDetail("Select Option Single SKU and Inner Pack displayed", true);

        }

    }

    public void selectSingleOrInnerOption(Boolean hasInnerPack) {
        log.info("Tote Option Selected. hasInnerPack:[{}]", hasInnerPack);
        if (hasInnerPack) {
            getWait(25).ignoring(Exception.class).until(visibilityOf(innerPack));
            innerPack.click();
        } else {
            getWait(25).ignoring(Exception.class).until(visibilityOf(singleUPC));
            singleUPC.click();
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
        //getWait(5).until(visibilityOf(processAreaLbl));

        getWait(20).ignoring(Exception.class).until(visibilityOf(backButton));

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
        StepDetail.addDetail("Tote scannned - " + s, true);
        getWait(15).ignoring(Exception.class).until(visibilityOf(scanToteLbl));
        scanToteInput.sendKeys(s);
        scanToteInput.sendKeys(Keys.ENTER);
    }

    public void validateScanInHouseScreen(String tote, String processArea) {
        getWait(5).ignoring(Exception.class).until(visibilityOf(scanInHouseUPC));
        Map<String, String> scanInHouseScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div");

        log.info("scanInHouseScreenElements:::{}", scanInHouseScreenElements);

        if (scanInHouseScreenElements.containsKey("Scan In-House UPC")
                && scanInHouseScreenElements.containsKey("BACK")
                && scanInHouseScreenElements.containsKey("EXIT")) {
            log.info("scan In House UPC, Back button and end tote button displayed");
            StepDetail.addDetail("Scan In House UPC label, Back button and end tote button displayed", true);
            Assert.assertTrue("valid label Process Area", scanInHouseScreenElements.containsKey("Process Area"));
            Assert.assertEquals(processArea, scanInHouseScreenElements.get("Process Area"));

            StepDetail.addDetail("Scan In House UPC Page: Process Area Displayed: " + scanInHouseScreenElements.containsKey("Process Area"), true);
            StepDetail.addDetail("Proceess Area Selected: " + processArea, true);

            Assert.assertTrue("valid label Tote ID", scanInHouseScreenElements.containsKey("Tote ID"));
            Assert.assertEquals(tote, scanInHouseScreenElements.get("Tote ID"));

            StepDetail.addDetail(
                    "Scan In House UPC Page: Tote ID Displayed: " + scanInHouseScreenElements.containsKey("Tote ID"), true);
            StepDetail.addDetail("Tote ID Scanned: " + tote, true);

        } else {
            log.info("Scan In House UPC label or Back button or end tote button is not displayed");
            StepDetail.addDetail("Scan In House UPC label or Back button or end tote button is not displayed", true);
            Assert.assertTrue(false);
        }
    }

    public void scanInHouseUPC(String inHouseUPC) {
        log.info("Scan In House UPC line :[{}]", inHouseUPC);
        getWait(5).ignoring(Exception.class).until(visibilityOf(scanInHouseUPC));
        scanInHouseUPCInput.sendKeys(inHouseUPC);
        scanInHouseUPCInput.sendKeys(Keys.ENTER);

    }

    public void validateInnerPack(String tote) {
        log.info("Validating the InnerPack page");
        Map<String, String> innerPackScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div");
        if (innerPackScreenElements.containsKey("BACK")) {
            Assert.assertEquals(tote, innerPackScreenElements.get("Tote ID"));
            StepDetail.addDetail("Displayed Tote id: " + innerPackScreenElements.get("Tote ID"), true);
            StepDetail.addDetail(" Tote ID Scanned:" + tote, true);
            log.info("tote ID validated", innerPackScreenElements.get("Tote ID"));
        } else {
            log.info("Back button and End Tote button are not displayed in Scan Quantity Page");
            StepDetail.addDetail("Back button and End Tote button are not displayed in Scan Quantity Page", true);
            Assert.assertFalse(true);
        }
        //Assert.assertEquals(tote,TTT);


    }


    public void validateScanQtyPage(String tote, String poLineNumber, Boolean hasInnerPack) {
        log.info("Validating the Scan quantity page. hasInnerPack {}", hasInnerPack);
        getWait(15).ignoring(Exception.class).until(visibilityOf(backButton));

        Map<String, String> scanQtyScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div");
        if (!hasInnerPack) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(endToteButton));

        }
        if (scanQtyScreenElements.containsKey("BACK")) {
            Assert.assertEquals(tote, scanQtyScreenElements.get("Tote ID"));
            StepDetail.addDetail("Displayed Tote id: " + scanQtyScreenElements.get("Tote ID"), true);
            StepDetail.addDetail(" Tote ID Scanned:" + tote, true);
            //TODO
            /*Map<String, String> responseMap = CommonUtils.getMapFromJson(
                    RestUtilities.getRequestResponse(ReadHostConfiguration.FETCH_POLINE_DTLS_URL.value()
                            + ReadHostConfiguration.LOCATION_NUMBER.value() + "/polines/" + poLineNumber));

            CommonUtils.compareValues(getScreenValues(scanQtyScreenElements), responseMap);*/
            StepDetail.addDetail("Validation passed for Scan Quantity screen" + tote, true);

        } else {
            log.info("Back button and End Tote button are not displayed in Scan Quantity Page");
            StepDetail.addDetail("Back button and End Tote button are not displayed in Scan Quantity Page", true);
            Assert.assertFalse(true);
        }

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
            getWait(10).ignoring(Exception.class).until(visibilityOf(entryBoxInnerPack));
            entryBoxInnerPack.sendKeys(quantity);
            entryBoxInnerPack.sendKeys(Keys.ENTER);
        } else {
            getWait(15).ignoring(Exception.class).until(visibilityOf(enterQtyinput));
            enterQtyinput.clear();
            enterQtyinput.sendKeys(quantity);
        }
    }

    public void clickButton(String button) {

        if (button.equalsIgnoreCase("back")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(backButton));
            backButton.click();
        } else if (button.equalsIgnoreCase("exit")) {
            getWait(25).ignoring(Exception.class).until(visibilityOf(exitButton));
            exitButton.click();
        } else if (button.equalsIgnoreCase("End Tote")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(endToteButton));
            endToteButton.click();
        } else if (button.equalsIgnoreCase("exitButton")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(exitButton));
            exitButton.click();
        }
    }

    public void clickButton(String button, WebDriver driver) {
        try {
            TimeUnit.SECONDS.sleep(5);
            List<WebElement> buttons = driver.findElements(By.xpath("//span[contains(@class,'MuiButton')]"));
            for (WebElement buttonValue : buttons) {
                if (buttonValue.getText().equalsIgnoreCase(button)) {
                    Actions action = new Actions(driver);
                    action.moveToElement(buttonValue).click().build().perform();
                    break;
                }
            }
        } catch (Exception e) {
            log.info("Exception occured while clicking the button :", e.getMessage());
        }
    }

    public void validateAndScanStagingLocation(String tote, String poNbr, String param, String stageLocation) {
        StepDetail.addDetail("Validating the select process area screen", true);
        getWait(10).ignoring(Exception.class).until(visibilityOf(stageToteTitleHeader));
        getWait(10).ignoring(Exception.class).until(visibilityOf(exitButton));

        Map<String, String> scanStageLocationScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div");
        //String[] actualToteId = toteIDLabel.getText().split(":");
        //getWait(5).until(visibilityOf(toteIDLabel));
        if (scanStageLocationScreenElements.containsKey("EXIT") && stageToteTitleHeader.isDisplayed()) {
            log.info("Exit button and Stage tote header is displayed in Scan Location Page");
            StepDetail.addDetail("Exit button and Stage tote header is displayed in Scan Location Page", true);

            Assert.assertEquals(tote, scanStageLocationScreenElements.get("Tote ID"));
            StepDetail.addDetail("Displayed Tote id: " + scanStageLocationScreenElements.get("Tote ID"), true);
            StepDetail.addDetail(" Tote ID Scanned:" + tote, true);

            //List<String> locationsAssigned = getLocationsAssignedToPO(poNbr);
            //if (!locationsAssigned.isEmpty()) {
                /*List<String> screenLocations = new ArrayList<>();
                if (locationsValue.getText().contains(",")) {
                    screenLocations = Arrays.asList(locationsValue.getText().replace("+", "").trim().split(","));
                } else {
                    screenLocations = Arrays.asList(locationsValue.getText().trim());
                }

                CommonUtils.validateLists(screenLocations, locationsAssigned);
                StepDetail.addDetail("Locations Displayed  " + screenLocations.toString() + " Locations Assigned to PO"
                        + locationsAssigned.toString(), true);*/

            try {
                // select the valid
                //TODO
                userScansAValidStagingLane(param, stageLocation);

            } catch (Exception e) {
                Assert.fail("Unable to assign location ID");
            }
            //}

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

    public void scanLocationId(String location) {
        StepDetail.addDetail("Scanning the location" + location, true);
        log.info("Scanning the location {}", location);
        getWait(25).ignoring(Exception.class).until(visibilityOf(locationiInput));
        locationiInput.sendKeys(location);
        locationiInput.sendKeys(Keys.ENTER);
    }

    public void userScansAValidStagingLane(String param, String location) throws Exception {
        log.info("scan staging location number :{}, param: {}", location, param);
        switch (param.toLowerCase()) {
            case "empty":
                //new DataCreateModule().deleteInventoryForLocation(location);
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
        JSONArray containerRelationShipArray = (JSONArray) responseObject.getJSONObject("container")
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
            //Assert.assertTrue(parentContainer.equalsIgnoreCase(locationId));
            Assert.assertTrue(childContainer.equalsIgnoreCase(toteId));
        });

    }

    public WebDriverWait getWait(int secs) {
        WebDriverWait wait = new WebDriverWait(driver, secs);
        return wait;
    }

    public Map<String, String> getPoLineDetails(String poLineBarcode) {
        return CommonUtils.getMapFromJson(RestUtilities.getRequestResponse(ReadHostConfiguration.FETCH_POLINE_DTLS_URL.value() + ReadHostConfiguration.LOCATION_NUMBER.value() +
                "/polines/" + poLineBarcode));
    }

}