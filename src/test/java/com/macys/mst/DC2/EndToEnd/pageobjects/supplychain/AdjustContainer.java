package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.AdjustContainerSteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class AdjustContainer extends BasePage {
    private SeUiContextBase seUiContextBase = new SeUiContextBase();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private static Logger log = Logger.getLogger(AdjustContainerSteps.class.getName());
    @FindBy(xpath = "//STRONG[text()='Scan Container ID :']")
    WebElement scanContainerIdLabel;

    @FindBy(xpath = "//*[*[text()='Exit']]")
    WebElement exit_CntId;

    @FindBy(xpath = "//*[@id='entryBox']")
    WebElement barcode;

    @FindBy(xpath = "//td/span")
    WebElement UPC;

    @FindBy(xpath = "//input[@type='number']")
    private WebElement txtQTY;

    @FindBy(id = "mui-component-select-selected")
    WebElement reasonOption;

    @FindBy(xpath = "//span[text()='Done']")
    WebElement btnDoneAdjReason;

    public WebDriverWait getWait(int waitTime) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        return wait;
    }

    public void validateScanContainerScreen() {
        //title or titleU
        getWait(10).until(ExpectedConditions.visibilityOf(scanContainerIdLabel));
        Assert.assertEquals(true, scanContainerIdLabel.isDisplayed());

        //getWait(10).until(ExpectedConditions.visibilityOf(back_StnId));
        getWait(10).until(ExpectedConditions.visibilityOf(exit_CntId));
        if (exit_CntId.isDisplayed()) {
            log.info("Exit button is displayed on Scan Container Page");
            StepDetail.addDetail("Exit button is displayed on Scan Container Page", true);
        } else {
            log.info("Exit button is displayed on Scan Container Page");
            StepDetail.addDetail("Exit button is displayed on Scan Container Page", true);
            Assert.assertFalse(true);
        }
    }

    public static void sendkeys(WebElement element, String data) {
        try {
            element.clear();
            element.sendKeys(new CharSequence[]{data});
        } catch (Exception var3) {
            log.error(var3);
        }

    }

    public void performAdjustContainer()
    {
        try {
            String Container = (String) dataStorage.getStoredData().get("Containervalue");
            getWait(60).until(ExpectedConditions.visibilityOf(barcode));
            barcode.sendKeys(Container);
            barcode.sendKeys(Keys.ENTER);
            log.info("Selected containerID Value: " + Container);
            StepDetail.addDetail("Selected containerID Value: " + Container, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select containerID Value", false);
            org.testng.Assert.fail("Unable to select containerID Value");
        }
        seUiContextBase.waitFor(2);
        getWait().until(ExpectedConditions.visibilityOf(barcode));
        String UPCValue=getWait().until(ExpectedConditions.visibilityOf(UPC)).getText();
        barcode.sendKeys(UPCValue);
        barcode.sendKeys(Keys.ENTER);
        seUiContextBase.waitFor(2);
        String qty=getWait().until(ExpectedConditions.visibilityOf(txtQTY)).getText();
        if(qty!=null)
        {
            sendkeys(txtQTY, Keys.CONTROL + "a");
            seUiContextBase.waitFor(2);
            txtQTY.sendKeys("2");
            txtQTY.sendKeys(Keys.ENTER);
        }
        seUiContextBase.waitFor(2);
        getWait().until(ExpectedConditions.visibilityOf(reasonOption));
        reasonOption.click();
        seUiContextBase.waitFor(2);
        List<WebElement> options = driver.findElements(By.xpath("//li[@role='option']"));
        options.get(0).click();
        seUiContextBase.waitFor(2);
        btnDoneAdjReason.click();
    }

}
