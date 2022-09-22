package com.macys.mst.WMSLite.EndToEnd.stepdefinitions;

import com.macys.mst.WMSLite.EndToEnd.db.app.SQLQueries;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.BatchDetailEnquaryPage;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.BatchEnquaryPage;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.FetchDBDataPage;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.GridUtils;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.Map;

public class BatchDetailEnquarySteps {


    public WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    private BatchEnquaryPage containerInquiryPage = PageFactory.initElements(driver, BatchEnquaryPage.class);
    public BatchDetailEnquarySteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    Logger log = Logger.getLogger(BatchDetailEnquarySteps.class);
    GridUtils Grid=new GridUtils(driver);


    private BatchDetailEnquaryPage batchDetailEnquaryPage = new BatchDetailEnquaryPage(driver);
    private FetchDBDataPage fetchOracleData = PageFactory.initElements(driver, FetchDBDataPage.class);




// Po deatil inquary
    @Then("user selects row $one from the BatchDetailInquiry table")
    public void selectRow(String one) throws InterruptedException {
        Thread.sleep(3000);
        batchDetailEnquaryPage.clickCheckbox(one);

    }
    @When("The user selects $waveType in $status from the BatchDetailInquiry page")
    public void selectWaveType(String waveTypeOption, String inputLabel) throws InterruptedException {

        batchDetailEnquaryPage.selectWaveType(waveTypeOption,inputLabel);
    }

    @Then("user click $Search Button")
    public void clickSearchOrResetButton(String search) throws InterruptedException{
        batchDetailEnquaryPage.clickSearchOrResetButton(search);
    }


    @Then("user enters $value in $inputLabel input at Batch Detail Enquiry")
    public void typeIntoInputFieldAtBatchDetail(String value, String inputLabel) throws InterruptedException {



        if(value.contains("Batch")){
            value=dataStorage.getStoredData().get("BatchNumber").toString();
          //  dataStorage.getStoredData().put("BatchNumber",value);
            batchDetailEnquaryPage.typeIntoInputFieldAtBatchDetail(value, inputLabel);
        }else if(value.contains("Reservation")){
            value=dataStorage.getStoredData().get("RES_NBR").toString();
            dataStorage.getStoredData().put("RES_NBR",value);
            batchDetailEnquaryPage.typeIntoInputFieldAtBatchDetail(value, inputLabel);
        }else{
            dataStorage.getStoredData().put("RES_NBR",value);
            batchDetailEnquaryPage.typeIntoInputFieldAtBatchDetail(value, inputLabel);
        }

    }

    @Then("user click $buttonName button in BatchDetailInquiry")
    public void clickSubmit(String buttonName) throws InterruptedException{
        Thread.sleep(3000);
        batchDetailEnquaryPage.clickAnyOneOfThreeButton(buttonName);
    }

    @Then("user click $yesOrNo button in $previewOrSubmit batch popup in BatchDetailInquiry")
    public void clickYesOrNoButton(String yesOrNo, String previewOrSubmit) throws InterruptedException{
        Thread.sleep(3000);
        batchDetailEnquaryPage.clickYesOrNoInPreviewSubmitBatchPopup(yesOrNo);

    }

    @Then("There are maximum $numberOfRows results displayed per page in BatchDetailInquiry table")
    public void maxRowsPerPage(String numberOfRows) {
        batchDetailEnquaryPage.selectNumberOfRowsPerPage(numberOfRows);
      //  StepDetail.addDetail("current number of rows are"+String.valueOf(PoDetailEnquaryPage.Grid.getCurrentNumberOfPagesOfResults()),true);

        //  Assert.assertTrue(orderSelectionPage.Grid.checkIfTheNumberOfResultsPerPageIsCorrect(Integer.parseInt(numberOfRows)));
    }
    @When("The user select $Test option from Printer list in BatchDetailInquiry")
    public void setPrinterOption(String Test) throws InterruptedException{
        batchDetailEnquaryPage.setPrinterOptionInDropdown(Test);
    }

    @Then("user click $PrintOrCancel button in select printer popup")
    public void clickPreviewOrCancelInOrderReservationPopup(){
        batchDetailEnquaryPage.clickPrintButtonInSelectPrinterPopup();
    }

    @Then("user get Success Batch Number Msg")
    public void clickClose(){
        batchDetailEnquaryPage.clickSuccessDialogCloseAfterPrint();
        if(!dataStorage.getStoredData().get("BatchNumber").toString().isEmpty()) {
            StepDetail.addDetail("Batch Number : " + dataStorage.getStoredData().get("BatchNumber"), true);
        }
    }

    @Then("user will see $columnName numbers related to batch number")
    public void getCoulumnItemList(String columnName) throws Exception {
     //   batchDetailEnquaryPage.getColumnData(columnName);
      //   batchDetailEnquaryPage.typeIntoInputFieldIDText("Batch#");
        List result = batchDetailEnquaryPage.getColumnDataNew(columnName);
        dataStorage.getStoredData().put("list",result);

        for(int i=0;i<result.size(); i++){

            String poStatusResetSql = "SELECT WAVE_STAT_CODE,STATUS,INPT_PKT_HDR_ID,PKT_NBR,PKT_CTRL_NBR FROM INPT_PKT_HDR pkthdr where PKT_NBR='" + result.get(i) + "'";

            String query = String.format(SQLQueries.GET_WAVE_STATUS, result.get(i));
            List<Map<Object, Object>> dbResults =fetchOracleData.getDbDetailsUsingResNumber(query,"joppaDB");
            // String results=dbResults;
            if (dbResults.size() > 0)
                for (Map<Object, Object> storeAllocMap : dbResults) {
                    StepDetail.addDetail("WAVE_STAT_CODE : "+storeAllocMap.get("WAVE_STAT_CODE").toString()+" STATUS : "+storeAllocMap.get("STATUS").toString() +
                            " INPT_PKT_HDR_ID : "+storeAllocMap.get("INPT_PKT_HDR_ID").toString()+"  PKT_NBR : "+
                            storeAllocMap.get("PKT_NBR").toString(),true);

                    String reservationNumber=storeAllocMap.get("PKT_NBR").toString();
                    dataStorage.getStoredData().put("RES_NBR",reservationNumber);

                }

        }

        System.out.println(result);
        StepDetail.addDetail(result.toString(),true);
    }

    @Then("user click Back to Batch Inquiry button")
    public void clickBackToBatchInquiry() throws InterruptedException{
         Thread.sleep(3000);
        batchDetailEnquaryPage.clickBackToBatchButton();
    }

}
