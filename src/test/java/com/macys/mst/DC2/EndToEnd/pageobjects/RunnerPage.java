package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Map;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class RunnerPage extends BasePage {
    public RunnerPage(WebDriver driver) {
        super(driver);
    }
    
    @FindBy(xpath = "//*[text()='Runner']")
    private WebElement runner;
    
    // Label Elements
    @FindBy(xpath = "//*[text()='Scan Drop Location/Pallet :']")
    private WebElement scanloctionAndPalletText;
    
    @FindBy(xpath = "//*[*[text()='Number of Bins :']]")
    private WebElement numberOfBinsDisplayed;
    
    // Text Box Elements   
    @FindBy(xpath = "//input[@type='number']")
    private WebElement enterInputNbr;

    @FindBy(xpath = "//input[@type='text']")
    private WebElement enterInputTxt;

    // Button Elements
    @FindBy(xpath = "//*[text()='Exit']")
    private WebElement exitButton;
    
    private SeUiContextBase seUiContextBase = new SeUiContextBase();

    public void navigatetoRunner() throws Exception {
        getWait(20).ignoring(Exception.class).until(elementToBeClickable(runner));
        Assert.assertEquals(true, runner.isDisplayed());
        runner.click();
    }

    public void enterPalletNbr(String palletNbr) {
        getWait(15).ignoring(Exception.class).until(visibilityOf(enterInputTxt));
        valiatedropLocationScreen();
        enterInputTxt.clear();
        enterInputTxt.sendKeys(palletNbr);
        enterInputTxt.sendKeys(Keys.ENTER);
    }

    public void valiatedropLocationScreen() {
        log.info("Validating the Drop location/Pallet in PackAway Pull Runner Page");
        getWait(5).until(ExpectedConditions.visibilityOf(enterInputTxt));
        getWait(5).until(ExpectedConditions.visibilityOf(exitButton));
        if (exitButton.isDisplayed() && scanloctionAndPalletText.isDisplayed() && enterInputTxt.isDisplayed()) {
            log.info("Validating the Scan drop location/Pallet in PackAway Pull Runner Page");
            StepDetail.addDetail("Validating the Scan drop location/Pallet in PackAway Pull Runner Page", true);
            Assert.assertEquals(enterInputTxt.getAttribute("value").length(), 0);
        } else {
            log.info("Validating the Scan drop location/Pallet in PackAway Pull Runner Page");
            StepDetail.addDetail("Validating the Scan drop location/Pallet in PackAway Pull Runner Page", false);
            Assert.assertTrue(false);
        }
    }
    
    public void scanNumberOfBins(String nbrOfBins) {
        getWait(10).until(ExpectedConditions.visibilityOf(enterInputNbr));
        enterInputNbr.sendKeys(nbrOfBins);
        enterInputNbr.sendKeys(Keys.ENTER);
    }

    public void enterDropLocation() throws Exception {
    	seUiContextBase.waitFor(10);
        getWait(20).ignoring(Exception.class).until(visibilityOf(enterInputTxt));
        Map<String, String> scanDropLocationScreenElements = CommonUtils.getScreenElementData(driver, "//*[@id=\"app\"]/div/div[2]/div/div[2]/div");
        log.info("DropLocationScreenElements:{}", scanDropLocationScreenElements);
        String drop_location = scanDropLocationScreenElements.get("Drop location").toString().trim();
        log.info("Drop Location from UI :{}", drop_location);
        if (scanDropLocationScreenElements.containsKey("Scan Drop Location")
                && scanDropLocationScreenElements.containsKey("Pallet")
                && scanDropLocationScreenElements.containsKey("Drop location")) {
            log.info("Scan Drop Location Screen Elements are displayed");
            StepDetail.addDetail("Scan Drop Location Screen Elements are displayed", true);
        } else {
            StepDetail.addDetail("Scan Drop Location Screen Elements are NOT displayed", false);
            Assert.assertTrue(false);
        }
        enterInputTxt.sendKeys(drop_location);
        enterInputTxt.sendKeys(Keys.ENTER);
    }

	public void clickExitButton() {
	    getWait(20).ignoring(Exception.class).until(elementToBeClickable(exitButton));
	    exitButton.click();		
	}
}
