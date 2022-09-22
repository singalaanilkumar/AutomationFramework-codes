package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class PackAwayPullPage extends BasePage {

    private final String ACTIVITIESZONE_ALERTMSG = "Activities exhausted for the scanned zone";
    private final String ACTIVITIESCOMP_ALERTMSG = "All activities exhausted for the activity type";

    private RandomUtil randomUtil = new RandomUtil();
    
    public PackAwayPullPage(WebDriver driver) {
        super(driver);
    }
    SeUiContextBase seUiContextBase = new SeUiContextBase();
    
    // Button Elements
    @FindBy(xpath = "//*[text()='Back']")
    private WebElement backButton;
      
    @FindBy(xpath = "//*[text()='Exit']")
    private WebElement exitButton;
    
    @FindBy(xpath = "//*[text()='CLOSE']")
    private WebElement closeButton;
    
    @FindBy(xpath = "//*[text()='Find Substitute']")
    private WebElement substituteButton;

    @FindBy(xpath = "//*[text()='Find Alternate']")
    WebElement alternateButton;
    
    // Label Elements    
    @FindBy(xpath = "//*[contains(text(),'Scan Pallet')]")
    private WebElement scanPalletLabel;

    @FindBy(xpath = "//*[text()='Scan Location :']")
    private WebElement scanLocationText;
    
    @FindBy(xpath = "//*[text()='Scan Location to Verify : ']")
    private WebElement verifyLocationText;

    @FindBy(xpath = "//*[*[text()='Activity Id :']]")
    private WebElement activity_Id;

    @FindBy(xpath = "//*[*[text()='Activity Desc :']]")
    private WebElement activity_Desc;

    @FindBy(xpath = "//*[*[text()='Pallet :']]")
    private WebElement palletBarcode;

    @FindBy(xpath = "//*[*[text()='Location :']]")
    private WebElement locationBarcode;

    @FindBy(xpath = "//*[*[text()='Bin :']]")
    private WebElement binboxBarcode;
    
    @FindBy(xpath = "//*[*[text()='Scan Substitute Bin :']]")
    private WebElement subBinboxBarcode;

    @FindBy(xpath = "//*[contains(text(), 'Pack Away Pull')]")
    private WebElement packAwayPull;
    
    // Text Box Elements   
    @FindBy(xpath = "//input[@type='number']")
    WebElement enterInputNbr;

    @FindBy(xpath = "//input[@type='text']")
    WebElement enterInputTxt;

    @FindBy(xpath = "//span[contains(text(),'Not In Location')]")
    WebElement notInLocation;

    @FindBy(xpath = "//span[contains(text(),'Confirm')]")
    WebElement confirm;

    public void clickBackButton(){
    	  getWait(20).ignoring(Exception.class).until(elementToBeClickable(backButton));
          backButton.click();
    }
    
    public void clickExitButton(){
  	  getWait(20).ignoring(Exception.class).until(elementToBeClickable(exitButton));
  	  exitButton.click();
  }

    public void navigatetoPackAwayPull() throws Exception {
        getWait(20).ignoring(Exception.class).until(elementToBeClickable(packAwayPull));
        Assert.assertEquals(true, packAwayPull.isDisplayed());
        packAwayPull.click();
    }

    public void enterPackAwayLocation(String packawayLoc) {
        getWait(15).ignoring(Exception.class).until(visibilityOf(enterInputTxt));
        valiateScanLocationScreen();
        enterInputTxt.clear();
        enterInputTxt.sendKeys(packawayLoc);
        enterInputTxt.sendKeys(Keys.ENTER);
    }
    
    public void verifyPackAwayLocation(String packawayLoc) {
        getWait(15).ignoring(Exception.class).until(visibilityOf(enterInputTxt));
        //validateVerifyLocationScreen();
        enterInputTxt.clear();
        enterInputTxt.sendKeys(packawayLoc);
        enterInputTxt.sendKeys(Keys.ENTER);
    }

    public void scanToteId(String s) {
        getWait(15).ignoring(Exception.class).until(visibilityOf(enterInputTxt));
        enterInputTxt.clear();
        enterInputTxt.click();
        enterInputTxt.sendKeys(s);
        enterInputTxt.sendKeys(Keys.ENTER);
    }

    public void selectSubstituteBinBox(String binboxBarcode){
    	getWait(10).ignoring(Exception.class).until(visibilityOf(substituteButton));
    	substituteButton.click();
    	getWait(10).ignoring(Exception.class).until(visibilityOf(subBinboxBarcode));
    	enterInputTxt.sendKeys(binboxBarcode);
    	enterInputTxt.sendKeys(Keys.ENTER);
    }
    
    public void selectAlternateBinBox(String binboxBarcode){
    	getWait(10).ignoring(Exception.class).until(visibilityOf(alternateButton));
    	alternateButton.click();
    }

    public void scanLocationId(String location) {
        StepDetail.addDetail("Scanning the location" + location, true);
        log.info("Scanning the location {}", location);
        getWait(25).ignoring(Exception.class).until(visibilityOf(enterInputTxt));
        enterInputTxt.sendKeys(location);
        enterInputTxt.sendKeys(Keys.ENTER);
    }

    public void valiateScanLocationScreen() {
        log.info("Validating the Scan location in PackAway Pull Page");

        getWait(5).until(ExpectedConditions.visibilityOf(scanLocationText));
        getWait(5).until(ExpectedConditions.visibilityOf(exitButton));
        if (exitButton.isDisplayed() && scanLocationText.isDisplayed() && enterInputTxt.isDisplayed() && backButton.isDisplayed()) {
            log.info("Validating the Scan location in PackAway Pull Page");
            StepDetail.addDetail("Validating the Scan location in PackAway Pull Page", true);
            Assert.assertEquals(enterInputTxt.getAttribute("value").length(), 0);
        } else {
            log.info("Validating the Scan location in PackAway Pull Page");
            StepDetail.addDetail("Validating the Scan location in PackAway Pull Page", false);
            Assert.assertTrue(false);
        }
    }
    
    
    public void validateVerifyLocationScreen() {
        log.info("Validating the Scan location in PackAway Pull Page");

        getWait(5).until(ExpectedConditions.visibilityOf(verifyLocationText));
        getWait(5).until(ExpectedConditions.visibilityOf(exitButton));
        if (exitButton.isDisplayed() && verifyLocationText.isDisplayed() && enterInputTxt.isDisplayed() && backButton.isDisplayed()) {
            log.info("Validating the Verify location in PackAway Pull Page");
            StepDetail.addDetail("Validating the Verify location in PackAway Pull Page", true);
        } else {
            log.info("Validating the Scan location in PackAway Pull Page");
            StepDetail.addDetail("Validating the Verify location in PackAway Pull Page", true);
            Assert.assertTrue(false);
        }
    }

    public void validateActivity(JSONObject activityJSON, String activityDescE, String scanlocationE, String palletBarCodeE) {
        log.info("Validating Activities got displayed");
        getWait(10).ignoring(Exception.class).until(visibilityOf(activity_Id));
        getWait(10).ignoring(Exception.class).until(visibilityOf(activity_Desc));
        getWait(10).ignoring(Exception.class).until(visibilityOf(palletBarcode));
        getWait(10).ignoring(Exception.class).until(visibilityOf(locationBarcode));
        getWait(10).ignoring(Exception.class).until(visibilityOf(binboxBarcode));

        String activityIDE = String.valueOf(activityJSON.get("id"));
        String binboxBarcodeE = String.valueOf(activityJSON.get("containerId"));

        Assert.assertEquals("Activtiy ID not displayed", activityIDE, activity_Id.getText().replace("Activity Id : ", ""));
        Assert.assertEquals("Activtiy Desc not displayed", activityDescE, activity_Desc.getText().replace("Activity Desc : ", ""));
        Assert.assertEquals("Location Barcode not displayed", scanlocationE, locationBarcode.getText().replace("Location : ", ""));
        Assert.assertEquals("BINBOX Barcode not displayed", binboxBarcodeE, binboxBarcode.getText().replace("Bin : ", ""));

        log.info("Validated Activities for activityID " + activityIDE);
        StepDetail.addDetail("Validated Activities for activityID " + activityIDE, true);
    }

    public void selectActivityProcessAreaDesc(String activityDesc) {
    	seUiContextBase.waitFor(10);
    	getWait(20).ignoring(Exception.class).until(visibilityOf(backButton));
    	List<WebElement> wbElements = driver.findElements(By.xpath("//b"));
        for(WebElement node : wbElements){
        	if(StringUtils.normalizeSpace(activityDesc).equalsIgnoreCase(StringUtils.normalizeSpace(node.getText()))){
        		node.click();
        		break;
           	}
        }
        log.info("Selected Activity Desc: "+StringUtils.normalizeSpace(activityDesc));
        StepDetail.addDetail("Selected Activity Desc: " + activityDesc, true);
    }

    public String scanRandomPallet() throws Exception {
        String palletContainerBarCode = randomUtil.getRandomValue("PLT-D-17");
        getWait(10).until(ExpectedConditions.visibilityOf(scanPalletLabel));
        enterInputTxt.clear();
        enterInputTxt.sendKeys(palletContainerBarCode);
        enterInputTxt.sendKeys(Keys.ENTER);
        StepDetail.addDetail("Scanned Pallet barcode " + palletContainerBarCode, true);
        return palletContainerBarCode;
    }

    public void scanBinBox(String barCode) {
        getWait(10).until(ExpectedConditions.visibilityOf(enterInputTxt));
        enterInputTxt.clear();
        enterInputTxt.sendKeys(barCode);
        enterInputTxt.sendKeys(Keys.ENTER);
        StepDetail.addDetail("Scanned BINBOX barcode " + barCode, true);
    }

    public void validateActivityAlertMessage() {
        valiateExhaustedPopUp(ACTIVITIESCOMP_ALERTMSG);
        closeButton.click();
        StepDetail.addDetail("WSM activites are completed", true);
    }

    public void validateZoneAlertMessage() {
        valiateExhaustedPopUp(ACTIVITIESZONE_ALERTMSG);
        closeButton.click();
        StepDetail.addDetail("First Drop Location is completed", true);
    }
    
    public void clickCloseButton() {
        try {
			getWait(20).until(ExpectedConditions.visibilityOf(closeButton));
			closeButton.click();
			StepDetail.addDetail("Close Button is clicked", true);
		} catch (Exception e) {
			log.debug("Unable to click close button", e);
		}
    }

    public void valiateExhaustedPopUp(String expMessage) {
        getWait(20).until(ExpectedConditions.visibilityOf(closeButton));
        if (closeButton.isDisplayed()) {
            Map<String, String> alertMsgScreenElements = CommonUtils.getScreenElementData(driver, "/html/body/div[2]/div[3]/div");
            StepDetail.addDetail("final Expected Message: " + expMessage, alertMsgScreenElements.containsKey(expMessage));
            Assert.assertTrue(alertMsgScreenElements.containsKey(expMessage));
        }
    }

    public void enterDropLocation() throws Exception {
        getWait(10).ignoring(Exception.class).until(visibilityOf(enterInputTxt));
        Map<String, String> scanDropLocationScreenElements = CommonUtils.getScreenElementData(driver, "//*[@id='app']/div/div[2]/div/div[2]/div");
        seUiContextBase.waitFor(10);
        log.info("DropLocationScreenElements:{}", scanDropLocationScreenElements);
        String drop_location = scanDropLocationScreenElements.get("Drop location").toString().trim();
        log.info("Drop Location from UI :{}", drop_location);
        if (scanDropLocationScreenElements.containsKey("Scan Drop Location")
                && scanDropLocationScreenElements.containsKey("Pallet")
                && scanDropLocationScreenElements.containsKey("Drop location")) {
            log.info("scan Drop Location Screen Elements are displayed");
            StepDetail.addDetail("scan Drop Location Screen Elements are displayed", true);
        } else {
            StepDetail.addDetail("Sscan Drop Location Screen Elements are NOT displayed", true);
            Assert.assertTrue(false);
        }
        enterInputTxt.sendKeys(drop_location);
        enterInputTxt.sendKeys(Keys.ENTER);
    }
    public void exitPage() throws Exception {
        getWait(20).ignoring(Exception.class).until(elementToBeClickable(exitButton));
        Assert.assertEquals(true, exitButton.isDisplayed());
        exitButton.click();
    }

    public void goToPreviousPage() throws Exception {
        driver.navigate().back();
        getWait(20).ignoring(Exception.class).until(elementToBeClickable(packAwayPull));
        Assert.assertEquals(true, packAwayPull.isDisplayed());
    }

    public void clickNotInLocation() throws InterruptedException {
        getWait(10).until(ExpectedConditions.elementToBeClickable(notInLocation));
        notInLocation.click();
        StepDetail.addDetail("Clicked on Not In Location ", true);
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h5[text()='Not in Location ?']")));
        confirm.click();
        StepDetail.addDetail("Clicked on Confirm ", true);

        try {
            getWait(5).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h5[text()='All activities exhausted for the activity type']")));
            closeButton.click();
        } catch (TimeoutException e1) {

        }
        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
        }
    }
}
