package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.HAFPages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import org.junit.Assert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Picking extends BasePage {

    RandomUtil randomUtil = new RandomUtil();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    List<String> pickingPallets = new ArrayList<>();


    @FindBy(xpath = "//INPUT[@type='text']")
    public WebElement inputBox;

    @FindBy(xpath = "//*[contains(text(),'Case')]//parent::div")
    WebElement caseToScan;

    @FindBy(xpath = "//span[text()='SKIP']")
    public WebElement skipButton;

    @FindBy(xpath = "//*[text()='Case Pull  HAF']")
    public WebElement casePullHAFLink;

    @FindBy(xpath = "//span[text()='CLOSE']")
    public WebElement scanZoneCloseButton;

    @FindBy(xpath = "//*[text()='Drop location :']//parent::div")
    public WebElement dropLocationValue;

    @FindBy(xpath = "//span[text()='Exit']")
    public WebElement exitButton;

    @FindBy(xpath = "//strong[text()='Scan Pallet :']")
    public WebElement scanPalletText;

    public void enterScanLocation(String value) throws InterruptedException {
        inputBox.clear();
        inputBox.sendKeys(value + Keys.ENTER);

        try {
            pageLoadWait();
            getWait().until(ExpectedConditions.elementToBeClickable(casePullHAFLink));
        } catch (Exception e) {
            if (scanZoneCloseButton.isDisplayed()) {
                scanZoneCloseButton.click();
                Assert.fail("No casepull activities found");
            }
        }

    }

    public void clickOnActivityLink() {
        casePullHAFLink.click();
        pageLoadWait();
        getWait().until(ExpectedConditions.visibilityOf(inputBox));
    }

    public void scanPallet() throws InterruptedException {
        String palletNumber = randomUtil.getRandomValue("PL-D-18");
        pickingPallets.add(palletNumber);
        dataStorage.getStoredData().put("pickingPallets", pickingPallets);
        inputBox.clear();
        inputBox.sendKeys(palletNumber + Keys.ENTER);
    }

    public void scanCase() throws InterruptedException {
        pageLoadWait();
        getWait().until(ExpectedConditions.visibilityOf(caseToScan));
        String caseNumber = caseToScan.getText().substring(7);
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(inputBox));
        inputBox.clear();
        inputBox.sendKeys(caseNumber);
        inputBox.sendKeys(Keys.ENTER);
        TimeUnit.SECONDS.sleep(2);
        try {
            scanZoneCloseButton.isDisplayed();
            scanZoneCloseButton.click();
            scanDropLocation();
        } catch (Exception ex) {
            try {
                TimeUnit.SECONDS.sleep(2);
                if (dropLocationValue.isDisplayed()) {
                    scanDropLocation();
                }
            } catch (Exception e) {
                scanCase();
            }
        }
    }

    public void scanDropLocation() {
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(inputBox));
        String scanLocation = dropLocationValue.getText().substring(16);
        inputBox.clear();
        inputBox.sendKeys(scanLocation + Keys.ENTER);
        try {
            TimeUnit.SECONDS.sleep(2);
            if (scanPalletText.isDisplayed()) {
                scanPallet();
                scanCase();
            }
        } catch (Exception excp) {
            try {
                if (scanZoneCloseButton.isDisplayed()) {
                    scanZoneCloseButton.click();
                }

                TimeUnit.SECONDS.sleep(2);

                if (exitButton.isDisplayed()) {
                    exitButton.click();
                }
            } catch (Exception exc) {
            }
        }
    }
}
