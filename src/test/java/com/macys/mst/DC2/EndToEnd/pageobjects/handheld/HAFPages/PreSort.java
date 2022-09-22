package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.HAFPages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PreSort extends BasePage {

    @FindBy(id = "entryBox")
    WebElement entryBox;

    @FindBy(xpath = "//span[text()='Yes']")
    public WebElement buttonYES;

    @FindBy(xpath = "//span[text()='CLOSE']")
    WebElement buttonCLOSE;

    @FindBy(name = "releaseToteConfirmationButton")
    WebElement releaseToteConfirm;

    @FindBy(xpath = "//span[text()='Release Tote / Bin']")
    WebElement releaseTote;

    @FindBy(xpath = "//button[@aria-label='exit']")
    public WebElement goBackToPresort;

    public void enterScanCase(String Case) throws InterruptedException {
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(entryBox));
        TimeUnit.SECONDS.sleep(2);
        entryBox.sendKeys(Case);
        entryBox.sendKeys(Keys.ENTER);
    }

    public void enterScanLocation(String presrtLcn) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(entryBox));
        try {
            TimeUnit.SECONDS.sleep(3);
            WebElement getLocation1 = driver.findElement(By.id("location1"));
            if (getLocation1.isDisplayed()) {
                entryBox.sendKeys(getLocation1.getText());
            }
        } catch (Exception e) {
            entryBox.sendKeys(presrtLcn);
        }
        entryBox.sendKeys(Keys.ENTER);
    }

    public void scanSku(String sku) throws InterruptedException {
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(entryBox));
        TimeUnit.SECONDS.sleep(2);
        entryBox.sendKeys(sku);
        entryBox.sendKeys(Keys.ENTER);
    }

    public void scanTote(String tote) throws InterruptedException {
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(entryBox));
        TimeUnit.SECONDS.sleep(2);
        entryBox.sendKeys(tote);
        entryBox.sendKeys(Keys.ENTER);
    }

    public void releaseAllTotes(List<String> totes) {
        try {
            pageLoadWait();
            getWait().until(ExpectedConditions.elementToBeClickable(releaseTote));
            releaseTote.click();

            for (String tote : totes) {
                enterScanCase(tote);
                pageLoadWait();
                getWait().until(ExpectedConditions.elementToBeClickable(releaseToteConfirm));
                releaseToteConfirm.click();
                pageLoadWait();
                getWait().until(ExpectedConditions.elementToBeClickable(buttonCLOSE));
                buttonCLOSE.click();
            }
            goBackToPresort.click();
        } catch (Exception ex) {
            Assert.fail("Release Tote interrupted : " + ex.getMessage());
        }
    }
}
