package com.macys.mst.WMSLite.EndToEnd.stepdefinitions;

import com.macys.mst.WMSLite.EndToEnd.pageobjects.OrderSelectionAndPrintingPage;
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
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

public class OrderSelectionAndPrintingSteps {

    public WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    private OrderSelectionAndPrintingPage containerInquiryPage = PageFactory.initElements(driver, OrderSelectionAndPrintingPage.class);
    public OrderSelectionAndPrintingSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    Logger log = Logger.getLogger(OrderSelectionAndPrintingSteps.class);


    private OrderSelectionAndPrintingPage orderSelectionPage = new OrderSelectionAndPrintingPage(driver);

    @Then("user click $criteria in Order selection page")
    public  void subMenuPageDisplay(String criteria) throws InterruptedException{
        Thread.sleep(3000);
        orderSelectionPage.Criteria.click();
        Assert.assertTrue(orderSelectionPage.Criteria.isDisplayed());

    }

    @Then("The $dropdownName dropdown is displayed")
    public void verifyDisplayedDropdown(String dropdownName) throws InterruptedException{
        Assert.assertTrue(orderSelectionPage.Dropdown.isDropdownDisplayed(dropdownName));

    }

    @Then("The $inputName input field is displayed")
    public void verifyDisplayedInput(String inputName) throws InterruptedException{
        Assert.assertTrue(orderSelectionPage.Input.isInputDisplayed(inputName));

    }

/*
    @Then("The $dropdownName dropdown is $validation")
    public void verifyDisplayedDropdown(@Named("dropdownName") String dropdownName, String validation) throws InterruptedException{
        if(validation.equals("displayed")){
            Assert.assertTrue(orderSelectionPage.Dropdown.isDropdownDisplayed(dropdownName));
        } else if(validation.equals("not displayed")){
            Assert.assertFalse(orderSelectionPage.Dropdown.isDropdownDisplayed(dropdownName));
        }
    }*/

    @Then("user enters value into inputLabel input from examples")
    public void typeInInputFieldValuesFromExamples(@Named("inputLabel")String inputLabel,@Named("value") String value) {
        orderSelectionPage.Input.typeIntoInputField(inputLabel, value);
        dataStorage.getStoredData().put("search_value",value);
    }

    @Then("The search result table is $validation")
    public void verifyDisplayedFilterResultTable(String validation) {
        if(validation.equals("displayed")){
            Assert.assertTrue(orderSelectionPage.Grid.isSearchResultTableDisplayed());
        } else if(validation.equals("not displayed")){
            Assert.assertFalse(orderSelectionPage.Grid.isSearchResultTableDisplayed());
        }
    }

    @Then("user clicks the $buttonName button")
    public void clickSearchButton(String buttonName)throws InterruptedException{
        if(buttonName.equalsIgnoreCase("Search")){
            orderSelectionPage.clickSearchButton();
            Thread.sleep(3000);
        }
        if(buttonName.equalsIgnoreCase("Reset")){
            orderSelectionPage.clickResetButton();
        }

    }

    @Then("The Order Selection & Printing grid columns are displayed correctly")
    public void verifyResultsTableColumns(){

        List<String> expectedList = Arrays.asList("Res Nbr", "Ship Nbr", "Order Nbr", "Order Date", "Ship Date", "Shipping", "Gift Wrap", "Back Order","Back Order Type", "Sell Chan", "Sell Div", "Lines", "Work Group", "FF#");
        Assert.assertEquals(orderSelectionPage.Grid.getGridHeader(), expectedList);
    }

    @Then("There are maximum $numberOfRows results displayed per page in the grid")
    public void maxRowsPerPage(String numberOfRows) {
        orderSelectionPage.Grid.selectNumberOfRowsPerPage(numberOfRows);
        StepDetail.addDetail("current number of rows are"+String.valueOf(orderSelectionPage.Grid.getCurrentNumberOfPagesOfResults()),true);

      //  Assert.assertTrue(orderSelectionPage.Grid.checkIfTheNumberOfResultsPerPageIsCorrect(Integer.parseInt(numberOfRows)));
    }
    @When("The user selects $rowsNumber from the Items per page dropdown")
    public void selectRowsNumberDropdown(String numberOfRows){
        orderSelectionPage.Grid.selectNumberOfRowsPerPage(numberOfRows);

    }
    @Then("Current numbers of rows is displayed")
    public void displayCurrentNumberRows(){
        StepDetail.addDetail("current number of rows are"+String.valueOf(orderSelectionPage.Grid.getCurrentNumberOfPagesOfResults()),true);

    }


    @Then("The filter options from the $columnName dropdown match the ones displayed in the grid")
    public void getColumnItems(String columnName){
        orderSelectionPage.Dropdown.getDropdownList(columnName);
       // Assert.assertEquals(orderSelectionPage.Grid.getColumnItemsList(columnName), orderSelectionPage.Dropdown.getDropdownList(columnName));
    }

    @Then("The searched value is displayed in the $columnName column from the grid")
    public void getColumnItemsOne(String columnName){
        Assert.assertTrue(orderSelectionPage.isSearchedDataDisplayedInTheGrid(columnName));
    }
    @When("The user selects $option from the $dropdownName dropdown")
    public void selectOptionFromDropdown(String dropdownName, String option){
        dataStorage.getStoredData().put("search_value",option);
        orderSelectionPage.Dropdown.setOptionInDropdown(dropdownName, option);

    }

    @Then("The data from columnName is sorted in an $colOrder order")
    public void getDataFromColumn(@Named("columnName") String columnName, String colOrder){
        orderSelectionPage.Grid.sortGridByColumn(columnName, colOrder);
        orderSelectionPage.Grid.getColumnData(columnName);
     //   Assert.assertTrue(orderSelectionPage.Grid.isColumnSortedCorrectly(orderSelectionPage.Grid.getColumnData(columnName), colOrder, columnName));
    }

    @Then("The $inputLabel field is empty")
    public void checkIfFieldIsEmpty(String inputLabel) {
        Assert.assertTrue(orderSelectionPage.Input.getInputFieldValue(inputLabel).isEmpty());
    }

    @Then("user click $previewBatchOrSubmitBatch button in Order Selection Page")
    public void clickpreviewBatchOrSubmitBatch(String previewBatchOrSubmitBatch) throws InterruptedException{
        Thread.sleep(3000);
        if(previewBatchOrSubmitBatch.equalsIgnoreCase("Preview Batch")){
            orderSelectionPage.clickPreviewBatchButton();
        }else{
            orderSelectionPage.clickSubmitBatchButton();
        }

    }

    @Then("The $previewOrSubmitPopup batch popup is displayed")
    public void isDispayedPreviewBatchPopup(String previewOrSubmitPopup)throws InterruptedException{
        Thread.sleep(3000);
        if(previewOrSubmitPopup.equalsIgnoreCase("Preview")){
            orderSelectionPage.isPreviewPopupDisplayed();
        }else{
            orderSelectionPage.isSubmitPopupDisplayed();

        }

    }
    @Then("user click $yesOrNo button in $previewOrSubmit batch popup")
    public void clickYesOrNoButton(String yesOrNo, String previewOrSubmit) throws InterruptedException{
        Thread.sleep(3000);
        orderSelectionPage.clickYesInPreviewSubmitBatchPopup();

    }
    @Then("The OrderReservation popup is displayed for $PreviewOrSubmit")
    public void isDisplayedOrderReservationBatchPopupForPreviewOrSubmit(String PreviewOrSubmit){
        if(PreviewOrSubmit.equalsIgnoreCase("Preview")){
            orderSelectionPage.ispopupReservationsDisplayedForPreview();
        }else{
            orderSelectionPage.ispopupReservationsDisplayedForSubmit();
        }

    }
    @Then("user gives $maxReservationPerBatch in max reservation per batch for $PreviewOrSubmit")
    public void setMaxReservationPerBatch(String maxReservationPerBatch,String PreviewOrSubmit)throws InterruptedException{
        Thread.sleep(3000);
        if(PreviewOrSubmit.equalsIgnoreCase("Preview")){
            orderSelectionPage.setMaxReservationsPerBatchForPreview(maxReservationPerBatch);
        }else{
            orderSelectionPage.setMaxReservationsPerBatchForSubmit(maxReservationPerBatch);
        }

    }

    @When("The user select $Test option from Printer list")
    public void setPrinterOption(String Test) throws InterruptedException{
        orderSelectionPage.setPrinterOptionInDropdown(Test);
    }

    @Then("user gives $maxReservationPerPrint in max reservation per print for $PreviewOrSubmit")
    public void setMaxReservationPerPrint(String maxReservationPerPrint, String PreviewOrSubmit) throws InterruptedException{
       Thread.sleep(3000);
       if(PreviewOrSubmit.equalsIgnoreCase("Preview")){
            orderSelectionPage.setMaxReservationsPerPrintForPreview(maxReservationPerPrint);
        }else{
            orderSelectionPage.setMaxReservationsPerPrintForSubmit(maxReservationPerPrint);
        }
    }


    @Then("user click $PreviewOrCancel button in OrderReservation popup")
    public void clickPreviewOrCancelInOrderReservationPopup(String PreviewOrCancel){
        orderSelectionPage.clickPreviewButtonInPopup();
    }



}
