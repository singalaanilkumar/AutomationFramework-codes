package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.SupportUISteps;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ContainerSupportPage extends BasePage {

    Logger log = Logger.getLogger(ContainerSupportPage.class);

    @FindBy(xpath = "//input[@id='IncidentNumber']")
    public static WebElement incidentNbr;

    @FindBy(xpath = "//div[@id='action']")
    public WebElement action;

    @FindBy(xpath = "//li[contains(text(),'Consume Container')]")
    public WebElement consumeContainer;

    @FindBy(xpath = "//li[contains(text(),'Consume & Recreate')]")
    public WebElement consumeRecreate;

    @FindBy(xpath = "//li[contains(text(),'Update Container')]")
    public WebElement updateContainer;

    @FindBy(xpath = "//span[contains(text(),'RETRIEVE')]")
    public WebElement Retrieve;

    @FindBy(xpath = "//div[@id='move-icon']")
    public WebElement Move;

    @FindBy(xpath = "//span[contains(text(),'UPDATE')]")
    public WebElement updateButton;

    @FindBy(xpath = "//input[@id='consumeContainerBarcode']")
    public static WebElement consumeContainerBarcode;

    @FindBy(xpath = "//textarea[@id='consumeContainerBarcode']")
    public WebElement consumeMultipleContainerBarcode;

    @FindBy(xpath="//span/span/input")
    public WebElement multiple;

    @FindBy(xpath = "//input[@id='sourceContainer']")
    public static WebElement sourceContainer;

    @FindBy(xpath = "//input[@id='targetContainer']")
    public static WebElement targetContainer;

    @FindBy(xpath = "//input[@id='containerBarcode']")
    public static WebElement updateContainerBarcode;

    @FindBy(xpath = "//button[@id='Submit']")
    public WebElement Run;

    @FindBy(xpath = "//span[contains(text(),'CLEAR')]")
    public WebElement Clear;

    @FindBy(xpath = "//button/span[contains(text(),'CLOSE')]")
    public WebElement Close;

    @FindBy(xpath = "//h5[contains(text(),'Consume Confirmation')]")
    public WebElement SuccessHeader;

    @FindBy(xpath = "//h6/span[contains(text(),'Inventory has been Consumed.')]")
    public WebElement SuccessMsg;

    @FindBy(xpath = "//h5[contains(text(),'Update Confirmation')]")
    public WebElement SuccessHeaderUpdate;

    @FindBy(xpath = "//h6/span[contains(text(),'Inventory has been Updated.')]")
    public WebElement SuccessMsgUpdate;

    @FindBy(xpath = "//h5[contains(text(),'Consume & Recreate Confirmation')]")
    public WebElement SuccessHeaderCR;

    @FindBy(xpath = "//h6/span[contains(text(),'Inventory has been Recreated.')]")
    public WebElement SuccessMsgCR;

    @FindBy(xpath = "//h6[contains(text(),'ERROR')]")
    public WebElement ErrorHeader;

    @FindBy(xpath = "//p[contains(text(),'Following containers failed: ')]")
    public WebElement ErrorMsg;

    @FindBy(xpath = "//p[contains(text(),'Invalid Barcode :')]")
    public WebElement InvalidErrorMsg;

    @FindBy(xpath = "//p[contains(text(),'Target Container')]")
    public WebElement ErrorMsgCR;

    @FindBy(xpath = "//p[contains(text(),'Unable to get Inventory.')]")
    public WebElement ErrorMsgUpdate;

    @FindBy(xpath = "//p[contains(text(),'container.containerStatusCode is not of a type(s) string')]")
    public WebElement ErrorMsgSchema;

    @FindBy(xpath = "//p[contains(text(),'Wrong Container Status')]")
    public WebElement ErrorMsgUpdateFailure;

    public void typeInputField(WebElement fieldname, String fieldvalue) {
        getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(fieldname));
        fieldname.sendKeys(fieldvalue);

    }

    public void selectAction() {
        getWait().until(ExpectedConditions.visibilityOf(action));
        action.click();
        getWait().until(ExpectedConditions.visibilityOf(consumeContainer));
        consumeContainer.click();
    }

    public void selectConsumeRecreateAction() {
        getWait().until(ExpectedConditions.visibilityOf(action));
        action.click();
        getWait().until(ExpectedConditions.visibilityOf(consumeRecreate));
        consumeRecreate.click();
    }

    public void selectUpdateContainerAction() {
        getWait().until(ExpectedConditions.visibilityOf(action));
        action.click();
        getWait().until(ExpectedConditions.visibilityOf(updateContainer));
        updateContainer.click();
    }

    public void clickSwitchMultiple() {
        multiple.click();
    }

    public void enterMultipleBarcode(String fieldname, String fieldvalue1, String fieldvalue2) {
        if (fieldname.equalsIgnoreCase("consumeContainerBarcode")) {
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(consumeMultipleContainerBarcode));
            consumeMultipleContainerBarcode.sendKeys(fieldvalue1);
            consumeMultipleContainerBarcode.sendKeys(",");
            consumeMultipleContainerBarcode.sendKeys(Keys.ENTER);
            consumeMultipleContainerBarcode.sendKeys(fieldvalue2);
        }
    }

    public void clickRunButton() {
        getWait().until(ExpectedConditions.visibilityOf(Run));
        Run.click();
    }

    public void clickClearButton() {
        getWait().until(ExpectedConditions.visibilityOf(Clear));
        Clear.click();
    }

    public void clickRetrieveButton() {
        getWait().until(ExpectedConditions.visibilityOf(Retrieve));
        Retrieve.click();
    }

    public void clickMoveButton() {
        getWait().until(ExpectedConditions.visibilityOf(Move));
        Move.click();
    }

    public void clickUpdateButton() {
        getWait().until(ExpectedConditions.visibilityOf(updateButton));
        updateButton.click();
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

    public void validateSuccess() {
        Assert.assertEquals(SuccessHeader.getText(), "Consume Confirmation");
        Assert.assertEquals(SuccessMsg.getText(), "Inventory has been Consumed.");
    }

    public void validateError(String errorValue) {
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(ErrorMsg.getText(), "Following containers failed: " +errorValue);
    }

    public void validateSuccessConsumeRecreate() {
        Assert.assertEquals(SuccessHeaderCR.getText(), "Consume & Recreate Confirmation");
        Assert.assertEquals(SuccessMsgCR.getText(), "Inventory has been Recreated.");
    }

    public void validateErrorConsumeRecreate(String errorValue) {
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(ErrorMsgCR.getText(), "Target Container "+errorValue+" Already exists");
    }

    public void validateErrorList(List<String> validConsumedBarcodes) {
        String errorCodes = "";
        for(int i=0; i<validConsumedBarcodes.size(); i++) {
            errorCodes+= validConsumedBarcodes.get(i);
            if(i+1 != validConsumedBarcodes.size())
                errorCodes+= ", ";
        }

        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(ErrorMsg.getText(), "Following containers failed: " +errorCodes);
    }

    public void validateInvalidErrorConsumeRecreate(String errorValue) {
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(InvalidErrorMsg.getText(), "Invalid Barcode : "+errorValue);
    }

    public void validateUpdateContainerSuccess() {
        Assert.assertEquals(SuccessHeaderUpdate.getText(), "Update Confirmation");
        Assert.assertEquals(SuccessMsgUpdate.getText(), "Inventory has been Updated.");
    }

    public void validateInvalidContainerBarcode() {
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(ErrorMsgUpdate.getText(), "Unable to get Inventory.");
    }

    public void validateSchema() {
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(ErrorMsgSchema.getText(), "container.containerStatusCode is not of a type(s) string");
    }

    public void validateWrongPayload() {
        Assert.assertEquals(ErrorHeader.getText(), "ERROR");
        Assert.assertEquals(ErrorMsgUpdateFailure.getText(), "Wrong Container Status");
    }

}
