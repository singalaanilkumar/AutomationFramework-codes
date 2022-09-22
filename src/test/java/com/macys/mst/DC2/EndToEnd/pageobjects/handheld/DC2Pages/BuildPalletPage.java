package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class BuildPalletPage extends BasePage {

    public BuildPalletPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//a[@href='/buildPallet']")
    WebElement clickBuildPalletLink;

    @FindBy(xpath = "//strong[contains(text(),'Scan Report ID :')]")
    WebElement scanReportIDLabel;

    @FindBy(xpath = "//input[@class='MuiInputBase-input MuiInput-input']")
    WebElement scanReportIDBox;

    @FindBy(xpath = "//input[@id='entryBox']")
    WebElement scanNumberBox;

    @FindBy(xpath = "//strong[contains(text(), 'Scan Pallet :')]")
    private WebElement scanNumberLabel;

    @FindBy(xpath = "//*[text()='Locate To : ']/parent::div/following-sibling::div")
    private WebElement locatetNumber;

    @FindBy(xpath = "//*[text()='Locate To : ']/parent::div/following-sibling::div/div/button[text()='+']")
    private WebElement plusButton;

    //Yes and No btn avaiable in UAT but not in QA envt. Kept it commented
    @FindBy(xpath = "//span[contains(text(), 'YES')]")
    private WebElement scanPalletStatusYes;

    @FindBy(xpath = "//span[contains(text(), 'NO')]")
    private WebElement scanPalletStatusNO;

    @FindBy(xpath = "//*[@id='entryBox']")
    private WebElement numOfContainers;

    @FindBy(xpath = "//div[contains(text(),'Num# of Containers :')]")
    private WebElement scanNumOfContainersLabel;

    @FindBy(xpath = "//button/span[contains(text(),'End Pallet')]")
    private WebElement endPalletBtn;

    @FindBy(xpath = "//button/span[contains(text(),'Locate')]")
    private WebElement locateBtn;

    @FindBy(xpath = "//button/span[contains(text(),'Back')]")
    private WebElement backBtn;

    @FindBy(xpath = "//input[@class='MuiInputBase-input MuiInput-input']")
    private WebElement locateTxtBox;

    @FindBy(xpath = "//strong[text()='Scan Location ID :']")
    private WebElement scanLocationIdLabel;

    @FindBy(xpath = "//*[text()='Exit']")
    private WebElement exitButton;

    public void scanBuildPalletReportID(String ReportID) {
        pageLoadWait();
        log.info("Scan Report ID :[{}]", ReportID);
        getWait(10).until(visibilityOf(scanReportIDLabel));
        scanReportIDBox.sendKeys(ReportID);
        scanReportIDBox.sendKeys(Keys.ENTER);
    }

    public void scanPalletNumber(String scanNbr) {
        pageLoadWait();
        getWait(10).until(visibilityOf(scanNumberLabel));
        scanNumberBox.sendKeys(scanNbr);
        scanNumberBox.sendKeys(Keys.ENTER);
    }

    public void isPalletEmptyYesBtn() {
        try {
            getWait(5).until(visibilityOf(scanPalletStatusYes));
            scanPalletStatusYes.click();
        }catch(Exception e){
            log.info("The Entered pallet is not used earlier or its empty");
        }
    }

    public void selectNumOfContainers(String number) {
        pageLoadWait();
        getWait(10).until(visibilityOf(scanNumOfContainersLabel));
        numOfContainers.sendKeys(number);
    }

    public void scanLocationID(String locationId) {
        pageLoadWait();
        getWait(10).until(visibilityOf(scanLocationIdLabel));
        locateTxtBox.sendKeys(locationId);
        locateTxtBox.sendKeys(Keys.ENTER);
        log.info("Selected Location Id: " + locationId);
    }

    public void LocatePallet() {
        pageLoadWait();
        getWait(10).until(ExpectedConditions.elementToBeClickable(locateBtn));
        locateBtn.click();
    }

    public void endPallet() {
        pageLoadWait();
        getWait(10).until(ExpectedConditions.elementToBeClickable(endPalletBtn));
        endPalletBtn.click();
    }

    public void backBtn() {
        pageLoadWait();
        getWait(10).until(ExpectedConditions.elementToBeClickable(backBtn));
        backBtn.click();
    }

    public String getLocateIdOnScreen() {
        try{
            getWait(5).until(ExpectedConditions.visibilityOf(plusButton));
            String prePlulatedLocationTxt = locatetNumber.getText().toString();
            return prePlulatedLocationTxt.substring(0,prePlulatedLocationTxt.trim().length()-1).trim();
        }
        catch (Exception e){
            return "";
        }
    }

    public void clickButton(String button) {
        if (button.equalsIgnoreCase("exit")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(exitButton));
            exitButton.click();
            getWait(2);
        }
        else if (button.equalsIgnoreCase("back")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(backBtn));
            backBtn.click();
            getWait(2);
        }
        else if(button.equalsIgnoreCase("Enter")) {
            numOfContainers.sendKeys(Keys.ENTER);
        }
    }

}