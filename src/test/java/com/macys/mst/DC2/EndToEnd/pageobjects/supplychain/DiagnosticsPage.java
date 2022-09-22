package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.SupportUISteps;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DiagnosticsPage extends BasePage {

    Logger log = Logger.getLogger(DiagnosticsPage.class);

    @FindBy(xpath = "//input[@name='rcptNbr']")
    public WebElement rcptNbr;

    @FindBy(xpath = "//button[@id='Submit']")
    public WebElement Search;

    @FindBy(xpath = "//button[@id='Clear']")
    public WebElement Clear;

    @FindBy(xpath = "//button/span[contains(text(),'CLOSE')]")
    public WebElement Close;

    @FindBy(xpath = "//h5[contains(text(),'SUCCESS')]")
    public WebElement SuccessHeader;

    @FindBy(xpath = "//h6/span[contains(text(),'Appointment Available')]")
    public WebElement SuccessMsg;

    @FindBy(xpath = "//h6[contains(text(),'ERROR')]")
    public WebElement ErrorHeader;

    @FindBy(xpath = "//p[contains(text(),'No Appointment Available')]")
    public WebElement ErrorMsg;

    public void typeIntoInputField(String fieldname, String fieldvalue) {
        if (fieldname.equalsIgnoreCase("rcptNbr")) {
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(rcptNbr));
            rcptNbr.sendKeys(fieldvalue);
        }
    }

    public void clickSearchButton() {
        getWait().until(ExpectedConditions.visibilityOf(Search));
        Search.click();
    }

    public void clickClearButton() {
        getWait().until(ExpectedConditions.visibilityOf(Clear));
        Clear.click();
    }

    public void clickCloseButton() {
        getWait().until(ExpectedConditions.visibilityOf(Close));
        Close.click();
    }

    public void waitForProcessing() {
        try {
            getWait(5).ignoring(Exception.class).until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("//p[text()='Processing... Please wait.']")));
        } catch (Exception e) {
            log.info("Processing "+e.getMessage());
        }
        try {
            getWait(10).ignoring(Exception.class).until(ExpectedConditions
                    .invisibilityOfElementLocated(By.xpath("//p[text()='Processing... Please wait.']")));
        } catch (Exception e) {
            log.info("Processing "+e.getMessage());
        }
    }

    public void validateSuccessRcpt() {
        Assert.assertEquals(SuccessHeader.getText(), "SUCCESS");
        Assert.assertEquals(SuccessMsg.getText(), "Appointment Available");
    }

    public void validateErrorRcpt() {
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(ErrorMsg.getText(), "No Appointment Available");
    }

}
