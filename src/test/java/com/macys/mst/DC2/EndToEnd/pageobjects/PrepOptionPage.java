package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.CreateToteSteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.selenium.PageObject;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.NoSuchElementException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PrepOptionPage extends PageObject {

    public PrepOptionPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//b[text()='Ticket/Prep']")
    private WebElement prepOption;

    @FindBy(xpath = "//strong[contains(text(),'Scan Container ID :')]")
    WebElement scanToteText;
    
    @FindBy(xpath = "//strong[contains(text(),'Scan Container ID :')]")
    WebElement scanContainerText;

    @FindBy(xpath = "//SPAN[text()='Back']")
    WebElement backButton;

    @FindBy(xpath = "//SPAN[text()='Exit']")
    WebElement exitButton;

    @FindBy(xpath = "//SPAN[text()='CANCEL']")
    WebElement cancelAlertButton;

    @FindBy(xpath = "//SPAN[text()='OK']")
    WebElement okAlertButton;

    @FindBy(xpath = "//SPAN[text()='CLOSE']")
    WebElement closeAlertButton;
    
    @FindBy(xpath = "//*[text()='Info']")
    WebElement alertHeader;

    @FindBy(xpath = "//INPUT[@type='text']")
    WebElement scanToteInput;

    @FindBy(xpath = "//span[text()='Details']")
    WebElement detailsButton;

    @FindBy(xpath = "//span[text()='Complete']")
    WebElement prepCompleteButton;

    @FindBy(xpath = "//strong[text()='Tote ID']/parent::div")
    WebElement ToteIDTxt;

    @FindBy(xpath = "//strong[text()='Tote ID']/parent::div")
    WebElement ToteIDTxt_InnerPack;

    @FindBy(xpath = "//strong[contains(text(),'UPC')]/parent::td")
    WebElement skuLabel;

    @FindBy(xpath = "//strong[contains(text(),'Color')]/parent::td")
    WebElement colorLabel;

    @FindBy(xpath = "//strong[contains(text(),'Size')]/parent::td")
    WebElement sizeLabel;

    @FindBy(xpath = "//strong[contains(text(),'Ticket Type')]/parent::td")
    WebElement ticketTypeLabel;

    @FindBy(xpath = "//strong[text()='Prep']/parent::div")
    WebElement prepLabel;

    @FindBy(xpath = "//strong[contains(text(),'PID')]/parent::td")
    WebElement pID;

    @FindBy(xpath = "//strong[text()='Original Tote']")
    WebElement originalToteText;

    @FindBy(xpath = "//input[@type='text']")
    WebElement enterOption;

    @FindBy(xpath = "//strong[text()='Scan new Tote to split:']")
    WebElement splitScreenScanToteText;

    @FindBy(xpath = "//strong[text()='Scan new Tote for New Store split:']")
    WebElement splitScreenScanNewStoreToteText;

    @FindBy(xpath = "//strong[text()='Scan new Tote for Existing store split:']")
    WebElement splitScreenScanRegularStoreToteText;

    @FindBy(xpath = "//strong[text()='Scan new Bin Box to split:']")
    WebElement scanBinText;

    @FindBy(xpath = "//strong[text()='Original Tote']/parent::div")
    WebElement prepToteID;

    @FindBy(xpath = "//strong[text()='Total Units']/parent::div")
    WebElement totalUnits;

    @FindBy(xpath = "//tbody//td[16]")
    WebElement units;

    @FindBy(xpath = "//tbody//td[1]")
    WebElement skuText;

    @FindBy(xpath = "//tbody//td[2]")
    WebElement colorText;

    @FindBy(xpath = "//tbody//td[3]")
    WebElement sizeText;

    @FindBy(xpath = "//input[@type='text']")
    WebElement scanSku;

    @FindBy(xpath = "//strong[contains(text(), 'New')]/parent::div")
    WebElement splitToteText;

    @FindBy(xpath = "//th[contains(text(),'UPC')]/following::td[1]")
    WebElement screeSku;

    @FindBy(xpath = "//th[contains(text(),'UPC')]/following::td[6]")
    WebElement screeColor;

    @FindBy(xpath = "//th[contains(text(),'UPC')]/following::td[11]")
    WebElement screeSize;

    @FindBy(xpath = "//th[contains(text(),'UPC')]/following::td[6]")
    WebElement unitsToSplit;

    @FindBy(xpath = "//strong[text()='Units']/parent::td")
    WebElement unitsText;

    @FindBy(xpath = "//strong[text()='Original Tote ID']")
    WebElement originalToteIDText;

    @FindBy(xpath = "//span[text()='Done']")
    WebElement doneButton;

    @FindBy(xpath = "//strong[text()='Original Tote ID']/parent::div")
    WebElement originalToteID;

    @FindBy(xpath = "//strong[text()='Qty']/parent::div")
    WebElement qtyValue;

    @FindBy(xpath = "//strong[text()='Color']/parent::td")
    WebElement colorValue;

    @FindBy(xpath = "//strong[text()='UPC']/parent::td")
    WebElement skuValue;

    @FindBy(xpath = "//strong[text()='PID']/parent::td")
    WebElement pidValue;

    @FindBy(xpath = "//strong[text()='Ticket Type']/parent::td")
    WebElement ticketTypeValue;

    @FindBy(xpath = "//strong[text()='Size']/parent::td")
    WebElement sizeValue;

    @FindBy(xpath = "//input[@type='text']")
    WebElement scanQty;

    @FindBy(xpath = "//strong[contains(text(),'Qty')]/parent::td")
    WebElement quantityLabel;

    @FindBy(xpath = "//strong[contains(text(),'Total inner packs')]/parent::div/text()[2]")
    WebElement quantityLabel_Innerpack;

    @FindBy(xpath = "//input")
    WebElement editqtyValue;

    @FindBy(xpath = "//strong[text()='Units']/parent::td")
    WebElement unitsValue;

    @FindBy(xpath = "//span//input[@type='checkbox']")
    WebElement prepCheckbox;

    @FindBy(xpath = "//strong[text()='Packs to split :']/parent::div")
    WebElement packstoSplitqty;

    @FindBy(css = "* /deep/ #clearBrowsingDataConfirm")
    WebElement clearCacheButton;

    public static int newTabcount = 0;

    private SeUiContextBase seUiContextBase = new SeUiContextBase();

    public boolean isElementPresent(By locatorKey) {
        try {
            driver.findElement(locatorKey);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public void verifyPrepCheckbox(Map<String, String> poLineServiceMap, String button) throws NoSuchElementException {

        log.info("verifyPrepCheckbox on {}", button);
        boolean presenceOfCheckBox = isElementPresent(By.xpath("//span//input[@type='checkbox']"));
        if (presenceOfCheckBox) {
            getWait(20);
            log.info("Validated the Prep option checkbox");
            StepDetail.addDetail("Validated the Prep option checkbox", true);
            if ("Complete".equalsIgnoreCase(button)) {
                clickButton("Complete");
            } else if ("Done".equalsIgnoreCase(button)) {
                clickButton("done");
            } else if ("Unit".equalsIgnoreCase(button)) {
                if (CreateToteSteps.hasInnerPack) {
                    log.info("packstoSplitqty.getText().trim() " + packstoSplitqty.getText().trim().split(":")[1].trim());
                    scanQuantity(packstoSplitqty.getText().trim().split(":")[1].trim());
                } else {
                    scanQuantity(unitsValue.getText().trim().split(":")[1].trim());
                }
            }

            validateFinalAlertMessage("Complete the Prep required");
            try {
                TimeUnit.SECONDS.sleep(2);
            }catch (Exception e){
                log.info("Exception occured during wait time : ", e.getMessage());
            }
            clickButton("CLOSE");
            StepDetail.addDetail("Alert Error Message : Complete the Prep required Validated", true);
            List<String> prepOptionsList = extractPrepOptions(poLineServiceMap);
            log.info("Prep List from API: {}", prepOptionsList);
           
            if (!prepCheckbox.isSelected()) {
                prepCheckbox.click();
                log.info("Prep option is Checked");
            }

        } else {
            log.info("Prep option is not available");
        }

    }

    public void buttonClick() {
        getWait(30).until(ExpectedConditions.visibilityOf(prepOption));
        prepOption.click();
    }

    public void openNewTab() {
        driver.get("http://google.com");
        driver.manage().timeouts().pageLoadTimeout(10000, TimeUnit.SECONDS);
    }

    public void cleanCache() throws InterruptedException {
        try {
			driver.manage().deleteAllCookies();
			seUiContextBase.waitFor(20);
			driver.get("chrome://settings/clearBrowserData");
			seUiContextBase.waitFor(20);
			new WebDriverWait(driver, 5).until(ExpectedConditions.elementToBeClickable((WebElement) ((JavascriptExecutor) driver).executeScript("return document.querySelector('settings-ui').shadowRoot.querySelector('settings-main').shadowRoot.querySelector('settings-basic-page').shadowRoot.querySelector('settings-section > settings-privacy-page').shadowRoot.querySelector('settings-clear-browsing-data-dialog').shadowRoot.querySelector('#clearBrowsingDataDialog').querySelector('#clearBrowsingDataConfirm')"))).click();
			seUiContextBase.waitFor(20);
		} catch (Exception e) {
			log.info("Unable to clear browser Data. Possible reasons may be usage of headless browser. "+e.getMessage());
		}
    }

    public WebDriverWait getWait(int waitTime) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        return wait;
    }

    public void validateScanToteScreen() {
        log.info("Validating the Scan tote page in prep option");

        getWait(5).until(ExpectedConditions.visibilityOf(scanToteText));
        getWait(5).until(ExpectedConditions.visibilityOf(exitButton));
        if (exitButton.isDisplayed() && scanToteText.isDisplayed() && scanToteInput.isDisplayed()) {
            log.info("Validated the scan tote Screen in Prep option");
            StepDetail.addDetail("Validated the scan tote Screen in Prep option", true);
            Assert.assertEquals(scanToteInput.getAttribute("value").length(), 0);
        } else {
            log.info("Validation failed for  the scan tote Screen in Prep option");
            StepDetail.addDetail("Validated failed the scan tote Screen in Prep option", true);
            Assert.assertTrue(false);
        }
    }

    public void validatePrepScreenPage(String toteId, int originalQuantity, Map<String, String> poLineServiceMap) {
        log.info("Validating the Prep Screen in Prep option");
        StepDetail.addDetail("Validating the Prep screen after Tote scan ", true);
        getWait(15).until(ExpectedConditions.visibilityOf(detailsButton));
        if (backButton.isDisplayed() && detailsButton.isDisplayed() && prepCompleteButton.isDisplayed()) {
            log.info("All the button displayed");
            if (!CreateToteSteps.hasInnerPack) {
                Assert.assertEquals(toteId, ToteIDTxt.getText().trim().split(":")[1].trim());
                StepDetail.addDetail("Displayed Tote: " + ToteIDTxt.getText().trim().split(":")[1].trim() + " is mathching with scanned tote: " + toteId, true);
                Assert.assertEquals(Integer.toString(originalQuantity), quantityLabel.getText().split(":")[1].trim());
                StepDetail.addDetail("Quantity displayed: " + quantityLabel.getText().split(":")[1].trim() + " is mathching with inventory quantity: " + originalQuantity, true);
                Map<String, String> prepScreenMap = getPrepScreenValues();
                CommonUtils.compareValues(prepScreenMap, poLineServiceMap);
            } else {
            	Map<String, String> prepScreenMap = getPrepScreenValues();
                CommonUtils.compareValues(prepScreenMap, poLineServiceMap);
            }
        } else {
            log.info("One of the button is not displayed");
            Assert.assertTrue(false);
        }

    }


    public void validateSplitToteScreen(String prepTote, String screen, int quantity, Map<String, String> poLineServiceMap) {
        getWait(10).until(ExpectedConditions.visibilityOf(originalToteText));
        getWait(10).until(ExpectedConditions.visibilityOf(enterOption));
        if (screen.equalsIgnoreCase("tote")) {
            getWait(10).until(ExpectedConditions.visibilityOf(splitScreenScanNewStoreToteText));
        } else if (screen.equalsIgnoreCase("regular")) {
            getWait(10).until(ExpectedConditions.visibilityOf(splitScreenScanRegularStoreToteText));
        } else if (screen.equalsIgnoreCase("bin")) {
            getWait(10).until(ExpectedConditions.visibilityOf(scanBinText));
            Assert.assertTrue(scanBinText.isDisplayed());
        }
        Map<String, String> scanSplitToteScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div");
        String toteId = scanSplitToteScreenElements.get("Original Tote");
        Assert.assertEquals(prepTote, toteId);
        StepDetail.addDetail("Displayed Tote: " + toteId + " is mathching with scanned tote: " + prepTote, true);
        Assert.assertEquals(String.valueOf(quantity), units.getText().trim());
        StepDetail.addDetail("Displayed Units: " + units.getText().trim() + " is mathching with inventory quantity: " + String.valueOf(quantity), true);
        CommonUtils.compareValues(getSplitToteScreenMap(), poLineServiceMap);
    }

    public void validateOriginalToteScreen(String prepTote, int quantity, String editable) {
        getWait(5).until(ExpectedConditions.visibilityOf(originalToteIDText));
        getWait(5).until(ExpectedConditions.visibilityOf(doneButton));
        if (doneButton.isDisplayed()) {
            Assert.assertEquals(prepTote, originalToteID.getText().trim().split(":")[1].trim());
            StepDetail.addDetail("Dispalyed Tote: " + originalToteID.getText().trim().split(":")[1].trim() + " is mathching with scanned Tote: " + prepTote, true);
            if (editable.equalsIgnoreCase("EdiatableQty")) {
                Assert.assertEquals(Integer.toString(quantity), editqtyValue.getAttribute("value"));
                StepDetail.addDetail("Dispalyed Quantity: " + editqtyValue.getAttribute("value") + " is matching with inventory units " + quantity, true);

            } else {
                Assert.assertEquals(Integer.toString(quantity), unitsValue.getText().trim().split(":")[1].trim());
                StepDetail.addDetail("Dispalyed Quantity: " + unitsValue.getText().trim().split(":")[1].trim() + " is matching with inventory units " + quantity, true);
            }

        } else {
            log.info("Done button is  displayed");
            Assert.assertFalse("Done button in origianl tote screen is not displayed", true);
        }

    }

    private String getOriginalToteScreenValues() {
        String value = "";
        List<WebElement> elements = driver.findElements(By.xpath("//tbody//tr//td"));
        for (WebElement element : elements) {
            if (element.getText().contains("Prep")) {
                continue;
            }
            value = value + element.getText().trim() + " ";
        }
        log.info(" Page values= " + value);
        return value;
    }

    public void validateSkanSKUScreen(String splitTote, String sku, String screen) {

        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(scanSku));
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(backButton));
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(exitButton));
        if (backButton.isDisplayed() && exitButton.isDisplayed() && scanSku.isDisplayed()) {
            Assert.assertEquals(splitTote, splitToteText.getText().trim().split(":")[1].trim());
            StepDetail.addDetail("Displayed Split container: " + splitToteText.getText().trim().split(":")[1].trim() + " is mathching with scanned split  Container: " + splitTote, true);
            Assert.assertEquals(sku, screeSku.getText().trim());
            StepDetail.addDetail("Displayed UPC : " + screeSku.getText().trim() + " is mathching with inventory UPC : " + sku, true);
            Assert.assertNotNull(unitsToSplit.getText().trim());
        } else {
            log.info("Back button in scan upc scree is not displayed");
            Assert.assertFalse("Back button in skan upc  scree is not displayed", true);
        }
    }

    public void validateQuantityScreen(String splitTote, int units, Map<String, String> poLineServiceMap, String screen) {
        getWait(5).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(enterOption));
        getWait(5).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(backButton));
        getWait(5).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(exitButton));
        if (backButton.isDisplayed() && exitButton.isDisplayed() && enterOption.isDisplayed()) {
            Map<String, String> scanValidateQuantityScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div/div//div//STRONG[text()]/parent::td");
            scanValidateQuantityScreenElements.putAll(CommonUtils.getScreenElementData(driver, "//div//div//div/div//div//STRONG[text()]/parent::div"));

            log.info("scanValidateQuantityScreenElements :  {}", scanValidateQuantityScreenElements);
            String screenToteId = null;
            if ("tote".equalsIgnoreCase(screen)) {
                screenToteId = "New Tote";
            } else if ("bin".equalsIgnoreCase(screen)) {
                screenToteId = "New Bin";
            }

            String toteId = scanValidateQuantityScreenElements.get(screenToteId);
            Assert.assertEquals(splitTote, toteId);
            StepDetail.addDetail("Displayed Container: " + toteId + " is mathching with scanned Container: " + splitTote, true);
            Assert.assertEquals(Integer.toString(units), scanValidateQuantityScreenElements.get("Units"));
            StepDetail.addDetail("Displayed Units: " + scanValidateQuantityScreenElements.get("Units") + " is matching with scan sku screen units" + units, true);

            CommonUtils.compareValues(getQuantityScreenMap(scanValidateQuantityScreenElements), poLineServiceMap);
        } else {
            log.info("Back button in skan sku  scree is not displayed");
            Assert.assertFalse("Back button in skan sku  scree is not displayed", true);
        }

    }


    public void scanValue(String value) {
        try {
            log.info("Scan ID:[{}]", value);
            enterOption.sendKeys(value);
            getWait(30);
            enterOption.sendKeys(Keys.ENTER);
            getWait(30);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void scanQuantity(String value) {
        log.info("scanQuantity:[{}]", value);
        getWait(30);
        scanQty.sendKeys(value);
        getWait(30);
        scanQty.sendKeys(Keys.ENTER);
        getWait(30);

    }

    public void scanToteId(String tote) {
        try {
            log.info("Scanning the tote in Prep option :{}", tote);
            getWait(5).until(ExpectedConditions.visibilityOf(scanToteInput));
            scanToteInput.sendKeys(tote);
            scanToteInput.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }


    private Map<String, String> getPrepScreenValues() {
        Map<String, String> screenValuesMap = new HashMap<>();
        screenValuesMap.put("skuUpc", skuLabel.getText().split(":")[1].trim());
        screenValuesMap.put("colorDesc", colorLabel.getText().split(":")[1].trim());
        screenValuesMap.put("sizeDesc", sizeLabel.getText().split(":")[1].trim());
        screenValuesMap.put("ticketType", ticketTypeLabel.getText().split(":")[1].trim());
        return screenValuesMap;
    }

    private List<String> getPrepLabelValues() {
        return Arrays.asList(prepLabel.getText().split(":")[1].trim().split(","));
    }

    private List<String> extractPrepOptions(Map<String, String> reponseMap) {
        List<String> prepList = new ArrayList<>();
        for (Map.Entry<String, String> entry : reponseMap.entrySet()) {
            if ("prep1".equals(entry.getKey()) || "prep2".equals(entry.getKey()) || "prep3".equals(entry.getKey())) {
                prepList.add(entry.getValue());
            }
        }
        return prepList;
    }

    private Map<String, String> getPrepDetailsScreenValues() {
        Map<String, String> screenValuesMap = getPrepScreenValues();
        screenValuesMap.put("pid", pID.getText().split(":")[1].trim());
        return screenValuesMap;
    }

    private HashMap<String, String> getSplitToteScreenMap() {
        HashMap<String, String> screenValuesMap = new HashMap<>();
        screenValuesMap.put("skuUpc", screeSku.getText().trim());
        screenValuesMap.put("colorDesc", screeColor.getText().trim());
        screenValuesMap.put("sizeDesc", screeSize.getText().trim());
        return screenValuesMap;
    }

    private HashMap<String, String> getQuantityScreenMap(Map<String, String> scanValidateQuantityScreenElements) {
        HashMap<String, String> screenValuesMap = new HashMap<>();
        screenValuesMap.put("skuUpc", scanValidateQuantityScreenElements.get("UPC"));
        screenValuesMap.put("colorDesc", scanValidateQuantityScreenElements.get("Color"));
        screenValuesMap.put("sizeDesc", scanValidateQuantityScreenElements.get("Size"));
        return screenValuesMap;
    }

    private HashMap<String, String> getOriginalToteScreenMap() {
        HashMap<String, String> screenValuesMap = new HashMap<>();
        screenValuesMap.put("skuUpc", skuValue.getText().split(":")[1].trim());
        screenValuesMap.put("colorDesc", colorValue.getText().split(":")[1].trim());
        screenValuesMap.put("sizeDesc", sizeValue.getText().split(":")[1].trim());
        screenValuesMap.put("pidDesc", pidValue.getText().split(":")[1].trim());
        screenValuesMap.put("ticketType", ticketTypeValue.getText().split(":")[1].trim());
        return screenValuesMap;
    }

    public void clickButton(String button) {
        log.info("Clicking on button  :" + button);
        if (button.equalsIgnoreCase("Complete")) {
            getWait(10).until(ExpectedConditions.elementToBeClickable(prepCompleteButton));
            prepCompleteButton.click();
        } else if (button.equalsIgnoreCase("Back")) {
            getWait(10).until(ExpectedConditions.elementToBeClickable(backButton));
            backButton.click();
        } else if (button.equalsIgnoreCase("Details")) {
            getWait(10).until(ExpectedConditions.elementToBeClickable(detailsButton));
            detailsButton.click();
        } else if (button.equalsIgnoreCase("done")) {
            getWait(10).until(ExpectedConditions.elementToBeClickable(doneButton));
            doneButton.click();
        } else if (button.equalsIgnoreCase("OK")) {
            getWait(10).until(ExpectedConditions.elementToBeClickable(okAlertButton));
            okAlertButton.click();
        } else if (button.equalsIgnoreCase("CANCEL")) {
            getWait(10).until(ExpectedConditions.elementToBeClickable(cancelAlertButton));
            cancelAlertButton.click();
        } else if (button.equalsIgnoreCase("CLOSE")) {
            String MainWindow = driver.getWindowHandle();
            driver.switchTo().window(MainWindow);
            closeAlertButton.click();
            String Window = driver.getWindowHandle();
            driver.switchTo().window(Window);
        } else if (button.equalsIgnoreCase("Exit")) {
            getWait(10).until(ExpectedConditions.elementToBeClickable(exitButton));
            exitButton.click();
        }
    }


    public boolean validateAlertMessage(String expMessage) {

        getWait(5).until(ExpectedConditions.visibilityOf(cancelAlertButton));
        getWait(5).until(ExpectedConditions.visibilityOf(okAlertButton));
        if (cancelAlertButton.isDisplayed() && okAlertButton.isDisplayed()) {
            Map<String, String> scanSplitToteScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div");
            StepDetail.addDetail("expMessage: " + expMessage, scanSplitToteScreenElements.containsKey(expMessage));
            Assert.assertTrue(scanSplitToteScreenElements.containsKey(expMessage));
            Assert.assertTrue(scanSplitToteScreenElements.containsKey("Warning"));
            return true;
        }
        return false;
    }

    public void validateFinalAlertMessage(String expMessage) {
        getWait(60).until(ExpectedConditions.elementToBeClickable(closeAlertButton));
        if (closeAlertButton.isDisplayed()) {
            Map<String, String> scanSplitToteScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div");
            StepDetail.addDetail("final Expected Message: " + expMessage, scanSplitToteScreenElements.containsKey(expMessage));
            Assert.assertTrue(scanSplitToteScreenElements.containsKey(expMessage));
        }
    }

    public String getInventory(String conatiner) {
        return RestUtilities.getRequestResponse(ReadHostConfiguration.GET_INVENTORY_SERVICE_URL.value().replace("{totebarcode}", conatiner));
    }
    
    public void selectPrepCheckBox(){
   	 try {
			if (!prepCheckbox.isSelected()) {
			     prepCheckbox.click();
			     log.info("Prep option is Checked");
                clickButton("done");
			 }
		} catch (Exception e) {
			log.info("Exception selection Prep "+e.getLocalizedMessage());
            clickButton("done");
		}
   }    
    
   public void validateScanContainerScreen() {
        log.info("Validating the Scan tote page in prep option");

        getWait(5).until(ExpectedConditions.visibilityOf(scanContainerText));
        getWait(5).until(ExpectedConditions.visibilityOf(exitButton));
        if (exitButton.isDisplayed() && scanContainerText.isDisplayed() && scanToteInput.isDisplayed()) {
            log.info("Validated the scan Container Screen in Prep option");
            StepDetail.addDetail("Validated the scan Container Screen in Prep option", true);
            Assert.assertEquals(scanToteInput.getAttribute("value").length(), 0);
        } else {
            log.info("Validation failed for  the scan tote Screen in Prep option");
            StepDetail.addDetail("Validated failed the scan tote Screen in Prep option", true);
            Assert.assertTrue(false);
        }
   }
   
   public void validateSplitToteScreen(String prepTote, String screen, int quantity) {
       getWait(5).until(ExpectedConditions.visibilityOf(originalToteText));
       getWait(5).until(ExpectedConditions.visibilityOf(enterOption));
       if (screen.equalsIgnoreCase("tote")) {
           getWait(5).until(ExpectedConditions.visibilityOf(splitScreenScanNewStoreToteText));
       } else if (screen.equalsIgnoreCase("regular")) {
           getWait(5).until(ExpectedConditions.visibilityOf(splitScreenScanRegularStoreToteText));
       } else if (screen.equalsIgnoreCase("bin")) {
           getWait(5).until(ExpectedConditions.visibilityOf(scanBinText));
           Assert.assertTrue(scanBinText.isDisplayed());
       }
       Map<String, String> scanSplitToteScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div");
       String toteId = scanSplitToteScreenElements.get("Original Tote");
       Assert.assertEquals(prepTote, toteId);
       StepDetail.addDetail("Displayed Tote: " + toteId + " is mathching with scanned tote: " + prepTote, true);
       Assert.assertEquals(String.valueOf(quantity), units.getText().trim());
       StepDetail.addDetail("Displayed Units: " + units.getText().trim() + " is mathching with inventory quantity: " + String.valueOf(quantity), true);
   }
   
   public void validateQuantityScreen(String splitTote, int units, String screen) {
       getWait(5).until(ExpectedConditions.visibilityOf(enterOption));
       getWait(5).until(ExpectedConditions.visibilityOf(backButton));
       getWait(5).until(ExpectedConditions.visibilityOf(exitButton));
       if (backButton.isDisplayed() && exitButton.isDisplayed() && enterOption.isDisplayed()) {
           Map<String, String> scanValidateQuantityScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div/div//div//STRONG[text()]/parent::td");
           scanValidateQuantityScreenElements.putAll(CommonUtils.getScreenElementData(driver, "//div//div//div/div//div//STRONG[text()]/parent::div"));

           log.info("scanValidateQuantityScreenElements :  {}", scanValidateQuantityScreenElements);
           String screenToteId = null;
           if ("tote".equalsIgnoreCase(screen)) {
               screenToteId = "New Tote";
           } else if ("bin".equalsIgnoreCase(screen)) {
               screenToteId = "New Bin";
           }

           String toteId = scanValidateQuantityScreenElements.get(screenToteId);
           Assert.assertEquals(splitTote, toteId);
           StepDetail.addDetail("Displayed Container: " + toteId + " is mathching with scanned Container: " + splitTote, true);
           Assert.assertEquals(Integer.toString(units), scanValidateQuantityScreenElements.get("Units"));
           StepDetail.addDetail("Displayed Units: " + scanValidateQuantityScreenElements.get("Units") + " is matching with scan sku screen units" + units, true);

       } else {
           log.info("Back button in skan sku  scree is not displayed");
           Assert.assertFalse("Back button in skan sku  scree is not displayed", true);
       }

   }   

   public void validateSplitBinScreen(String prepBIN, String screen, int quantity) {
       getWait(5).until(ExpectedConditions.visibilityOf(originalToteText));
       getWait(5).until(ExpectedConditions.visibilityOf(enterOption));
       if (screen.equalsIgnoreCase("tote")) {
           getWait(5).until(ExpectedConditions.visibilityOf(splitScreenScanNewStoreToteText));
       } else if (screen.equalsIgnoreCase("regular")) {
           getWait(5).until(ExpectedConditions.visibilityOf(splitScreenScanRegularStoreToteText));
       } else if (screen.equalsIgnoreCase("bin")) {
           getWait(5).until(ExpectedConditions.visibilityOf(scanBinText));
           Assert.assertTrue(scanBinText.isDisplayed());
       }
       Map<String, String> scanSplitToteScreenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div");
       String binId = scanSplitToteScreenElements.get("Original Tote");
       Assert.assertEquals(prepBIN, binId);
       StepDetail.addDetail("Displayed BIN: " + binId + " is mathching with scanned BIN: " + prepBIN, true);
       Assert.assertEquals(String.valueOf(quantity), units.getText().trim());
       StepDetail.addDetail("Displayed Units: " + units.getText().trim() + " is mathching with inventory quantity: " + String.valueOf(quantity), true);
       System.out.println(getSplitToteScreenMap());
   }

}
