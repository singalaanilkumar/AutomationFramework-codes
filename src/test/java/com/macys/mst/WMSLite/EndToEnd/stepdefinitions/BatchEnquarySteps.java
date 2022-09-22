package com.macys.mst.WMSLite.EndToEnd.stepdefinitions;

import com.macys.mst.WMSLite.EndToEnd.pageobjects.BatchEnquaryPage;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class BatchEnquarySteps {


    public WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    private BatchEnquaryPage containerInquiryPage = PageFactory.initElements(driver, BatchEnquaryPage.class);
    public BatchEnquarySteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    Logger log = Logger.getLogger(BatchEnquarySteps.class);


    private BatchEnquaryPage BatchEnquaryPage = new BatchEnquaryPage(driver);




/*
    @Then("The $dropdownName dropdown is $validation")
    public void verifyDisplayedDropdown(@Named("dropdownName") String dropdownName, String validation) throws InterruptedException{
        if(validation.equals("displayed")){
            Assert.assertTrue(orderSelectionPage.Dropdown.isDropdownDisplayed(dropdownName));
        } else if(validation.equals("not displayed")){
            Assert.assertFalse(orderSelectionPage.Dropdown.isDropdownDisplayed(dropdownName));
        }
    }*/



// Po deatil inquary
    @Then("user selects row $one from the BatchInquiry table")
    public void selectRow(String one) throws InterruptedException {
        Thread.sleep(3000);
        BatchEnquaryPage.clickCheckbox(one);

    }
    @When("The user selects $waveType in $status from the BatchInquiry page")
    public void selectWaveType(String waveTypeOption, String inputLabel) throws InterruptedException {

        BatchEnquaryPage.selectWaveType(waveTypeOption,inputLabel);
    }

    @Then("user click $Search Button")
    public void clickSearchOrResetButto(String search) throws InterruptedException{

        BatchEnquaryPage.clickSearchOrResetButton(search);
    }
    @When("The user enters value in inputLabel input from examples for po enquary")
    public void typeInInputFieldValuesFromExamplespoDetail(@Named("inputLabel")String inputLabel,@Named("value") String value) throws InterruptedException {
        Thread.sleep(2000);
        BatchEnquaryPage.typeIntoInputFieldID(inputLabel, value);

    }

    @Then("user enters batchvalue in $inputLabel input in Batch Inquiry page")
    public void typeInInputFieldValuesFromExamplespoDetail(String inputLabel) throws InterruptedException {
        Thread.sleep(3000);

        BatchEnquaryPage.typeIntoInputFieldID(inputLabel, dataStorage.getStoredData().get("BatchNumber").toString());

    }

    @Then("user click $buttonName button in BatchInquiry")
    public void clickSubmit(String buttonNmae) throws InterruptedException{
        Thread.sleep(3000);
        BatchEnquaryPage.clickAnyOneOfSixButton(buttonNmae);
    }

    @Then("user click $yesOrNo button in $previewOrSubmit batch popup in BatchInquiry")
    public void clickYesOrNoButton(String yesOrNo, String previewOrSubmit) throws InterruptedException{
        Thread.sleep(3000);
        BatchEnquaryPage.clickYesOrNoInPreviewSubmitBatchPopup(yesOrNo);

    }

    @Then("There are maximum $numberOfRows results displayed per page in BatchDetailInquiry table")
    public void maxRowsPerPage(String numberOfRows) {
        BatchEnquaryPage.selectNumberOfRowsPerPage(numberOfRows);
      //  StepDetail.addDetail("current number of rows are"+String.valueOf(PoDetailEnquaryPage.Grid.getCurrentNumberOfPagesOfResults()),true);

        //  Assert.assertTrue(orderSelectionPage.Grid.checkIfTheNumberOfResultsPerPageIsCorrect(Integer.parseInt(numberOfRows)));
    }
    @Then("user select $Test option from Printer list in BatchInquiry")
    public void setPrinterOption(String Test) throws InterruptedException{
        BatchEnquaryPage.setPrinterOptionInDropdown(Test);
    }

    @Then("user click $PrintOrCancel button in select printer popup")
    public void clickPreviewOrCancelInOrderReservationPopup(){
        BatchEnquaryPage.clickPrintButtonInSelectPrinterPopup();
    }

    @Then("user get Success Batch Number Msg")
    public void clickClose() throws InterruptedException{
        BatchEnquaryPage.clickSuccessDialogCloseAfterPrint();
        if(!dataStorage.getStoredData().get("BatchNumber").toString().isEmpty()) {
            StepDetail.addDetail("Batch Number : " + dataStorage.getStoredData().get("BatchNumber"), true);
        }
    }

}
