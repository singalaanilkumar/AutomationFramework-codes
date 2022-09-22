package com.macys.mst.WMSLite.EndToEnd.stepdefinitions;

import com.macys.mst.WMSLite.EndToEnd.pageobjects.BatchEnquaryPage;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.CreateOrderPage;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.OrderInquiryAndDetailInquiryPage;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.concurrent.ConcurrentHashMap;

public class OrderInquiryAndDetailInquirySteps {

    public WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
     public OrderInquiryAndDetailInquirySteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    StepsDataStore dataStorage = StepsDataStore.getInstance();
    Logger log = Logger.getLogger(BatchEnquarySteps.class);

    private OrderInquiryAndDetailInquiryPage InquiryPage = new OrderInquiryAndDetailInquiryPage(driver);


    @Then("user enters value into Label input from examples")
    public void typeInInputFieldValues(@Named("Label")String inputLabel, @Named("value") String value) throws InterruptedException{
        Thread.sleep(3000);
        InquiryPage.typeIntoInputField(inputLabel, value);
        dataStorage.getStoredData().put("search_value",value);
    }
    @Then("user click $buttonName button in $page page")
    public void searchButton(String buttonName, String page) throws ElementClickInterceptedException ,InterruptedException{


       if(buttonName.equalsIgnoreCase("Filter")){
            InquiryPage.filterButton();
        }
        if(buttonName.equalsIgnoreCase("Search")){

            InquiryPage.searchButton(buttonName);
        }
        if(buttonName.equalsIgnoreCase("Export")){
            InquiryPage.clickExportButton();
        }
        if(buttonName.equalsIgnoreCase("Yes")){

            InquiryPage.clickYesOrderInquiryPopup();
            Thread.sleep(20000);
            StepDetail.addDetail("File has been exported",true);
        }


    }


    @Then("user will see the search data in $columnName at Table")
    public void searchDataDisplayed(String columnName)throws InterruptedException{

        Thread.sleep(20000);
        StepDetail.addDetail("Search data "+dataStorage.getStoredData().get("search_value")+" in "+columnName+" is displayed ",InquiryPage.isSearchedDataDisplayedInTheGrid(columnName));

     }


}
