package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class SplitAdjustPalletPage extends BasePage {
    CommonUtils commonUtils = new CommonUtils();

    public SplitAdjustPalletPage(WebDriver driver) {
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

    @FindBy(xpath = "//strong[contains(text(), 'Scan Pallet ID : ')]")
    private WebElement scanPalletLabel;

    @FindBy(xpath = "//strong[contains(text(), 'Scan To Pallet ID :')]")
    private WebElement scanToPalletLabel;

    @FindBy(xpath = "//strong[.='Locate To : ']/../following-sibling::div/div")
    private WebElement locateToNumber;

    @FindBy(xpath = "//*[text()='Locate To : ']/parent::div/following-sibling::div/div/button[text()='+']")
    private WebElement plusButton;

    //Yes and No btn avaiable in UAT but not in QA envt. Kept it commented
    @FindBy(xpath = "//span[contains(text(), 'YES')]")
    private WebElement scanPalletStatusYes;

    @FindBy(xpath = "//*[@role='button']")
    private WebElement PIDdropdown;

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

    @FindBy(xpath = "//button/span[contains(text(),'Split')]")
    private WebElement SplitBtn;

    @FindBy(xpath = "//button/span[contains(text(),'Adjust')]")
    private WebElement AdjustBtn;

    @FindBy(xpath = "//input[@class='MuiInputBase-input MuiInput-input']")
    private WebElement locateTxtBox;

    @FindBy(xpath = "//strong[text()='Scan Location ID :']")
    private WebElement scanLocationIdLabel;

    @FindBy(xpath = "//*[text()='Exit']")
    private WebElement exitButton;

    @FindBy(xpath = "//*[text()='Locate']")
    private WebElement LocateButton;

    @FindBy(xpath = "//*[@id='app']/div/div[2]/div/div[2]/div")
    private List<WebElement> splitAdjustScreenElements;

    @FindBy(xpath = "//*[@id='app']/div/div[2]/div/div[2]//*[text()='Num# of Containers : ']")
    private List<WebElement> splitAdjelementswithcontainer;

    @FindBy(xpath = "//*[@role='listbox']/li")
    private List<WebElement> PIDs;

    public void scanBuildPalletReportID(String ReportID) {
        pageLoadWait();
        log.info("Scan Report ID :[{}]", ReportID);
        getWait(10).until(visibilityOf(scanReportIDLabel));
        scanReportIDBox.sendKeys(ReportID);
        scanReportIDBox.sendKeys(Keys.ENTER);
    }

    public void scanPalletNumber(String scanNbr) {
        pageLoadWait();
        getWait(10).until(visibilityOf(scanPalletLabel));
        scanNumberBox.sendKeys(scanNbr);
        scanNumberBox.sendKeys(Keys.ENTER);
    }

    public void scanToPalletNumber(String scanNbr) {
        pageLoadWait();
        getWait(10).until(visibilityOf(scanToPalletLabel));
        scanNumberBox.sendKeys(scanNbr);
        scanNumberBox.sendKeys(Keys.ENTER);
    }

    public void selectPID(String screenType) {
        getWait(5).until(ExpectedConditions.visibilityOf(PIDdropdown));
        PIDdropdown.click();
        String pID = "";
        List<String> PIDs_value = new ArrayList<>();
        List<WebElement> PIDlist = PIDs;
        for (WebElement eachPID : PIDlist) {
            String PID_value = eachPID.getText().toString();
            PIDs_value.add(PID_value);
        }
        if (PIDs_value.size() > 1) {
            if (screenType.equalsIgnoreCase("Split")) {
                PIDs.get(1).click();
                CommonUtils.doJbehavereportConsolelogAndAssertion("PID selected is : ", PIDs.get(1).getText().toString(), true);
            } else if (screenType.equalsIgnoreCase("Adjust")) {
                PIDs.get(0).click();
                CommonUtils.doJbehavereportConsolelogAndAssertion("PID selected is : ", PIDs.get(0).getText().toString(), true);
            }
        } else {
            PIDs.get(0).click();
            CommonUtils.doJbehavereportConsolelogAndAssertion("PID selected is : ", PIDs.get(0).getText().toString(), true);
        }
    }

    public void isPalletEmptyYesBtn() {
        try {
            getWait(5).until(visibilityOf(scanPalletStatusYes));
            scanPalletStatusYes.click();
        } catch (Exception e) {
            log.info("The Entered pallet is not used earlier or its empty");
        }
    }

    public void selectNumOfContainers(String number) {
        pageLoadWait();
        getWait(10).until(visibilityOf(scanNumOfContainersLabel));
        numOfContainers.click();
        numOfContainers.clear();
        numOfContainers.sendKeys(number);
        //   numOfContainers.sendKeys(Keys.ENTER);
    }

    public void scanLocationID(String locationId) {
        pageLoadWait();
        getWait(10).until(visibilityOf(scanLocationIdLabel));
        locateTxtBox.clear();
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
        try {
            getWait(5).until(ExpectedConditions.visibilityOf(plusButton));
            String prePlulatedLocationTxt = locateToNumber.getText().toString();
            return prePlulatedLocationTxt.substring(prePlulatedLocationTxt.length() - 1).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickButton(String button) {
        if (button.equalsIgnoreCase("exit")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(exitButton));
            exitButton.click();
        } else if (button.equalsIgnoreCase("back")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(backBtn));
            backBtn.click();
        } else if (button.equalsIgnoreCase("Split")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(SplitBtn));
            SplitBtn.click();
        } else if (button.equalsIgnoreCase("Locate")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(LocateButton));
            LocateButton.click();
        } else if (button.equalsIgnoreCase("Adjust")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(AdjustBtn));
            AdjustBtn.click();
        }
    }

    public void waitForsplitscreenElemnts() {
        getWait(15).until(ExpectedConditions.visibilityOfAllElements(splitAdjustScreenElements));
    }

    public Map<String, String> getSplitAdjusscreenElements() {
        getWait(15).until(ExpectedConditions.visibilityOfAllElements(splitAdjelementswithcontainer));
        return getScreenData("//*[@id='app']/div/div[2]/div/div[2]/div");
    }
}