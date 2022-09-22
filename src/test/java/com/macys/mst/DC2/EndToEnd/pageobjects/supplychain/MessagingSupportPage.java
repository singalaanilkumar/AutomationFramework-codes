package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;

public class MessagingSupportPage extends BasePage {

    Logger log = Logger.getLogger(MessagingSupportPage.class);

    @FindBy(xpath = "//input[@id='incidentNbr']")
    public static WebElement incidentNbr;

    @FindBy(xpath = "//input[@id='containerBarcode']")
    public static WebElement containerBarcode;

    @FindBy(xpath = "//div[@id='action']")
    public WebElement action;

    @FindBy(xpath = "//li[contains(text(),'CONTROUTE')]")
    public WebElement contRoute;

    @FindBy(xpath = "//li[contains(text(),'TOTECONT')]")
    public WebElement toteCont;
    
    @FindBy(xpath = "//li[contains(text(),'STOREALLOC')]")
    public WebElement storeAlloc;
    
    @FindBy(xpath = "//span[@id='system-payload']")
    public static WebElement systemPayload;
    
    @FindBy(xpath = "//input[@id='payload-read-only']")
    public static WebElement payloadReadOnly;
    
    @FindBy(xpath = "//span[@id='payload']")
    public WebElement payload;

    @FindBy(xpath = "//button[@id='Submit']")
    public WebElement Run;

    @FindBy(xpath = "//div[@id='move-icon']")
    public WebElement Move;

    @FindBy(xpath = "//span[contains(text(),'CLEAR')]")
    public WebElement Clear;

    @FindBy(xpath = "//button/span[contains(text(),'CLOSE')]")
    public WebElement Close;

    @FindBy(xpath = "//h5[contains(text(),'Messaging Support Confirmation')]")
    public WebElement SuccessHeader;

    @FindBy(xpath = "//h6/span[contains(text(),'Message has been submitted successfully.')]")
    public WebElement SuccessMsg;

    @FindBy(xpath = "//h6[contains(text(),'ERROR')]")
    public WebElement ErrorHeader;

    @FindBy(xpath = "//p[contains(text(),'Error while calling Messaging API to submit the message, please check logs')]")
    public WebElement ErrorMsg;

    @FindBy(xpath = "//p[contains(text(),'payload is not of a type(s) object')]")
    public WebElement InvalidPayloadErrorMsg;

    @FindBy(xpath = "//p[contains(text(),'Payload cannot be generated for this container')]")
    public WebElement InvalidErrorMsg;

    public void typeInputField(WebElement fieldname, String fieldvalue) {
        getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(fieldname));
        fieldname.sendKeys(fieldvalue);
    }

    public void selectAction(String actionSelected) {
        getWait().until(ExpectedConditions.visibilityOf(action));
        action.click();
        WebElement actionItem = null;
    	switch(actionSelected) {
    	  case "CONTROUTE":
    		  actionItem = contRoute;
    		  break;
    	  case "TOTECONT":
    		  actionItem = toteCont;
    		  break;
    	  case "STOREALLOC":
    		  actionItem = storeAlloc;
    		  break;
    	  default:
    		  Assert.fail("Action not declare in Messaging Support Page");
    	}
    	if(actionItem != null) {
            getWait().until(ExpectedConditions.visibilityOf(actionItem));
            actionItem.click();
    	}
    }
    
    public String getSystemPayload() {
        getWait().until(ExpectedConditions.visibilityOf(payload));
        return systemPayload.getAttribute("value");
    }
    
    public String getPayload() {
        getWait().until(ExpectedConditions.visibilityOf(payload));
        return payloadReadOnly.getAttribute("value");
    }
    
    public void inputPayload(String value) {
        getWait().until(ExpectedConditions.visibilityOf(payload));
        payload.click();
        payload.sendKeys(value);
    }

    public void clickRunButton() {
        getWait().until(ExpectedConditions.visibilityOf(Run));
        Run.click();
    }

    public void clickMoveButton() {
        getWait().until(ExpectedConditions.visibilityOf(Move));
        Move.click();
    }

    public void clickClearButton() {
        getWait().until(ExpectedConditions.visibilityOf(Clear));
        Clear.click();
        getWait().until(ExpectedConditions.visibilityOf(action));
    }

    public void clickCloseButton() {
        getWait().until(ExpectedConditions.visibilityOf(Close));
        Close.click();
        getWait().until(ExpectedConditions.visibilityOf(action));
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

    public void validateSuccess() {
    	getWait().until(ExpectedConditions.visibilityOf(SuccessHeader));
        Assert.assertEquals(SuccessHeader.getText(), "Messaging Support Confirmation");
        Assert.assertEquals(SuccessMsg.getText(), "Message has been submitted successfully.");
    }

    public void validateError() {
    	getWait().until(ExpectedConditions.visibilityOf(ErrorHeader));
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(ErrorMsg.getText(), "Error while calling Messaging API to submit the message, please check logs");
    }

    public void validateErrorInvalidPayload() {
        getWait().until(ExpectedConditions.visibilityOf(ErrorHeader));
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(InvalidPayloadErrorMsg.getText(), "payload is not of a type(s) object" );
    }

    public void validateInvalidContainer() {
        getWait().until(ExpectedConditions.visibilityOf(ErrorHeader));
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(InvalidErrorMsg.getText(), "Payload cannot be generated for this container");
    }

}
