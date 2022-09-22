package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.ContainerSupportPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.DiagnosticsPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.MessagingSupportPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.context.StepsContext;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class SupportUISteps {

    Logger log = Logger.getLogger(SupportUISteps.class);
    public static WebDriver driver = ContainerSupportPage.driver;

    private ContainerSupportPage containerSupport = new ContainerSupportPage ();
    private MessagingSupportPage messagingSupport = new MessagingSupportPage();
    private DiagnosticsPage diagnostics = new DiagnosticsPage ();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private StepsContext stepsContext;

    private RandomUtil randomUtil = new RandomUtil();

    private String targetContainerBarCode = randomUtil.getRandomValue("50-D-18");

    public SupportUISteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    // Diagnostics Page
    @When("User validates Diagnostics screen with a valid rcptNbr")
    public void typeDiagnosticsValue(){
        String rcptNbr = (String) dataStorage.getStoredData().get("rcptNbr");
        diagnostics.typeIntoInputField("rcptNbr", rcptNbr);
        diagnostics.clickSearchButton();
        diagnostics.waitForProcessing();
        diagnostics.validateSuccessRcpt();
        diagnostics.clickCloseButton();
    }

    @Then("User now validates Diagnostics screen with an invalid rcptNbr")
    public void typeDiagnosticsInvalidValue(){
        String rcptNbr = randomUtil.getRandomValue("D-7");
        diagnostics.typeIntoInputField("rcptNbr", rcptNbr);
        diagnostics.clickSearchButton();
        diagnostics.waitForProcessing();
        diagnostics.validateErrorRcpt();
        diagnostics.clickCloseButton();
    }

    @Then("User clears the fields")
    public void clear()  {
        diagnostics.clickClearButton();
    }

    //Container Support page
//    Scenario 1
    @When("User enters incidentNbr and selects action as Consume Container")
    public void typeInInputFieldValues(){
        String incidentNbr = randomUtil.getRandomValue("D-7");
        containerSupport.typeInputField(ContainerSupportPage.incidentNbr, incidentNbr);
        containerSupport.selectAction();

    }

    @Then("User enters a single valid container barcode")
    public void typeBarCode() {
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String barcode = (String) inventoryContainerList.get(0);
        containerSupport.typeInputField(ContainerSupportPage.consumeContainerBarcode, barcode);
        containerSupport.clickRunButton();
        containerSupport.waitForProcessing();
        containerSupport.validateSuccess();
        containerSupport.clickCloseButton();
    }

    @Then("User enters invalid container barcode")
    public void typeInInputFieldInvalidValues(){
        typeInInputFieldValues();
        String consumeContainerInvalidBarcode = randomUtil.getRandomValue("50-D-18");
        containerSupport.typeInputField(ContainerSupportPage.consumeContainerBarcode,consumeContainerInvalidBarcode);
        containerSupport.clickRunButton();
        containerSupport.waitForProcessing();
        containerSupport.validateError(consumeContainerInvalidBarcode);
        containerSupport.clickCloseButton();
    }

    @Then("User clears the form")
    public void clearInputFieldValues()  {
        containerSupport.clickClearButton();
    }


//    Scenario 2 -Multiple
    @When("User selects multiple and enters multiple container valid barcodes")
    public void typeMultipleBarCodes() {
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String consumeContainerBarcode1 = (String) inventoryContainerList.get(0);
        String consumeContainerBarcode2 = (String) inventoryContainerList.get(1);
        containerSupport.clickSwitchMultiple();
        containerSupport.enterMultipleBarcode("consumeContainerBarcode",consumeContainerBarcode1,consumeContainerBarcode2);
        containerSupport.clickRunButton();
        containerSupport.waitForProcessing();
        containerSupport.validateSuccess();
        containerSupport.clickCloseButton();
    }

    @Then("validate error with one valid and one invalid barcode")
    public void typeInvalidMultipleBarCodes() {
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String consumeContainerValidBarcode= (String) inventoryContainerList.get(0);
        String consumeContainerInvalidBarcode = randomUtil.getRandomValue("50-D-18");
        List<String> validInvalidConsumedBarcodes = new ArrayList<>();
        validInvalidConsumedBarcodes.add(consumeContainerValidBarcode);
        validInvalidConsumedBarcodes.add(consumeContainerInvalidBarcode);
        typeInInputFieldValues();
        containerSupport.enterMultipleBarcode("consumeContainerBarcode",consumeContainerValidBarcode,consumeContainerInvalidBarcode);
        containerSupport.clickRunButton();
        containerSupport.waitForProcessing();
        containerSupport.validateErrorList(validInvalidConsumedBarcodes);
        containerSupport.clickCloseButton();
    }

    //scenario-3 multiple but different error scenario
    @Then("validate error with two valid and consumed barcodes")
    public void typevalidConsumedMultipleBarCodes() {
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String consumeContainerBarcode1= (String) inventoryContainerList.get(0);
        String consumeContainerBarcode2= (String) inventoryContainerList.get(1);
        List<String> validConsumedBarcodes = new ArrayList<>();
        validConsumedBarcodes.add(consumeContainerBarcode1);
        validConsumedBarcodes.add(consumeContainerBarcode2);
        typeInInputFieldValues();
        containerSupport.enterMultipleBarcode("consumeContainerBarcode",consumeContainerBarcode1,consumeContainerBarcode2);
        containerSupport.clickRunButton();
        containerSupport.waitForProcessing();
        containerSupport.validateErrorList(validConsumedBarcodes);
        containerSupport.clickCloseButton();
    }

    //Scenario-4
    @When("User enters incidentNbr and selects action as Consume & Recreate")
    public void typeInInputFieldValuesConsumeRecreate(){
        String incidentNbr = randomUtil.getRandomValue("D-7");
        containerSupport.typeInputField(ContainerSupportPage.incidentNbr, incidentNbr);
        containerSupport.selectConsumeRecreateAction();
    }

    @Then("User now enters a source,target container valid barcodes")
    public void consumeRecreateValidValues() {
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String sourceContainerBarCode= (String) inventoryContainerList.get(0);
        containerSupport.typeInputField(ContainerSupportPage.sourceContainer,sourceContainerBarCode);
        containerSupport.typeInputField(ContainerSupportPage.targetContainer,targetContainerBarCode);
        containerSupport.clickRunButton();
        containerSupport.waitForProcessing();
        containerSupport.validateSuccessConsumeRecreate();
        containerSupport.clickCloseButton();
    }

    @Then("validate the invalid barcodes")
    public void consumeRecreateInvalidValues() {
        typeInInputFieldValuesConsumeRecreate();
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String sourceContainerBarCode= (String) inventoryContainerList.get(0);
        containerSupport.typeInputField(ContainerSupportPage.sourceContainer,sourceContainerBarCode);
        containerSupport.typeInputField(ContainerSupportPage.targetContainer,targetContainerBarCode);
        containerSupport.clickRunButton();
        containerSupport.waitForProcessing();
        containerSupport.validateErrorConsumeRecreate(targetContainerBarCode);
        containerSupport.clickCloseButton();
    }

    @Then("User now enters an invalid source and valid target container")
    public void consumeRecreateInvalidSource() {
        typeInInputFieldValuesConsumeRecreate();
        String sourceContainerBarCode = randomUtil.getRandomValue("1-D-19");
        containerSupport.typeInputField(ContainerSupportPage.sourceContainer,sourceContainerBarCode);
        containerSupport.typeInputField(ContainerSupportPage.targetContainer,targetContainerBarCode);
        containerSupport.clickRunButton();
        containerSupport.waitForProcessing();
        containerSupport.validateInvalidErrorConsumeRecreate(sourceContainerBarCode);
        containerSupport.clickCloseButton();
    }
    
    @When("user enters incidentNbr and containerBarcode and selects action")
    public void typeInInputFieldValues(@Named("action")String action){
        String incidentNbr = randomUtil.getRandomValue("D-7");
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String containerBarcode= (String) inventoryContainerList.get(0);
        messagingSupport.typeInputField(MessagingSupportPage.incidentNbr, incidentNbr);
        messagingSupport.typeInputField(MessagingSupportPage.containerBarcode, containerBarcode);
        messagingSupport.selectAction(action);
    }

    @When("user enters incidentNbr and  enters invalid containerBarcode and selects action")
    public void typeInInputFieldValuesForInvalidContainerBarcode(@Named("action")String action){
        String incidentNbr = randomUtil.getRandomValue("D-7");
        String containerBarcode = randomUtil.getRandomValue("D-20");
        messagingSupport.typeInputField(MessagingSupportPage.incidentNbr, incidentNbr);
        messagingSupport.typeInputField(MessagingSupportPage.containerBarcode, containerBarcode);
        messagingSupport.selectAction(action);
    }
    
    @When("user clicks Move button")
    public void clickMoveButton(){
        messagingSupport.clickMoveButton();
    }
    
    @When("user enters invalid payload")
    public void typeInInValidPayload(){
		messagingSupport.inputPayload("{tname: 'PrintTicket',payload: 'INVALID PAYLOAD', clientId: 'SUPPORT' }" );
    }

    public void typeInWrongPayload(){
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String containerBarcode= (String) inventoryContainerList.get(0);
        String invalidPayload = "{container: {barCode: '#replace#',containerType: 'BINBOX',containerStatusCode: 'xyz'}}";
        invalidPayload = invalidPayload.replace("#replace#",containerBarcode);
        messagingSupport.inputPayload(invalidPayload);
    }

    public void typeInvalidPayloadSchemaValidation(){
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String containerBarcode= (String) inventoryContainerList.get(0);
        String invalidPayload = "{container: {barCode: '#replace#',containerType: 'BINBOX',containerStatusCode: 123}}";
        invalidPayload = invalidPayload.replace("#replace#",containerBarcode);
        messagingSupport.inputPayload(invalidPayload);
    }


    @When("user clicks Run button")
    public void clickRunButton(){
        messagingSupport.clickRunButton();
    }
    
    @When("user clicks Clear button")
    public void clickClearButton(){
        messagingSupport.clickClearButton();
    }
    
    @Then("valid success message is displayed for MessagingSupport")
    public void validateSuccessMessage(){
    	messagingSupport.waitForProcessing();
        messagingSupport.validateSuccess();
        messagingSupport.clickCloseButton();
    }
    
    @Then("valid error message is displayed for MessagingSupport")
    public void validateErrorMessage(){
        messagingSupport.validateErrorInvalidPayload();
        messagingSupport.clickCloseButton();
        messagingSupport.clickClearButton();
    }

    @Then("invalid error message is displayed for containerBarcode")
    public void validateInvalidErrorMessage(){
        messagingSupport.waitForProcessing();
        messagingSupport.validateInvalidContainer();
        messagingSupport.clickCloseButton();
        messagingSupport.clickClearButton();
    }

//    Scenario 6:Update Container
@When("User enters incidentNbr and selects action as Update Container")
public void typeInInputFieldValuesUpdateContainer(){
    String incidentNbr = randomUtil.getRandomValue("D-7");
    containerSupport.typeInputField(ContainerSupportPage.incidentNbr, incidentNbr);
    containerSupport.selectUpdateContainerAction();
}

    @Then("User enters container barcode and clicks on retrieve")
    public void updateContainerValidBarcode() {
        List<String> inventoryContainerList = (List) dataStorage.getStoredData().get("inventoryContainerList");
        String containerBarcode= (String) inventoryContainerList.get(0);
        containerSupport.typeInputField(ContainerSupportPage.updateContainerBarcode,containerBarcode);
        containerSupport.clickRetrieveButton();
        containerSupport.waitForProcessing();
    }

    @Then("User clicks Move button and then updates")
    public void updateContainerMove() {
        containerSupport.clickMoveButton();
        containerSupport.clickUpdateButton();
        containerSupport.waitForProcessing();
        containerSupport.validateUpdateContainerSuccess();
        containerSupport.clickCloseButton();
    }

//    Scenario 7:Invalid Container barcode for update
@Then("User clicks on retrieve after entering invalid container barcode")
public void updateContainerInValidBarcode() {
    String containerBarcode = randomUtil.getRandomValue("D-7");
    containerSupport.typeInputField(ContainerSupportPage.updateContainerBarcode,containerBarcode);
    containerSupport.clickRetrieveButton();
    containerSupport.waitForProcessing();
    containerSupport.validateInvalidContainerBarcode();
    containerSupport.clickCloseButton();
}

//Scenario 8: Valid container barcode with invalid payload for schema validation
@Then("User enters invalid payload and clicks update")
public void updateContainerInValidPayload() {
    typeInvalidPayloadSchemaValidation();
    containerSupport.clickUpdateButton();
    containerSupport.waitForProcessing();
    containerSupport.validateSchema();
    containerSupport.clickCloseButton();
}

    //Scenario 9: Valid container barcode with wrong payload for update failure
    @Then("User enters wrong payload and clicks update")
    public void updateContainerEmptyPayload() {
        typeInWrongPayload();
        containerSupport.clickUpdateButton();
        containerSupport.waitForProcessing();
        containerSupport.validateWrongPayload();
        containerSupport.clickCloseButton();
    }
}
