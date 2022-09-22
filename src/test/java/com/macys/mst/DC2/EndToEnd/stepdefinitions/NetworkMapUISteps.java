package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateTotePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PrintTicketPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.SortToStorePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.Home;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.AdjustContainer;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.NetworkMapUIPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.RequestUtil;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import com.mysql.cj.jdbc.Driver;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.webdriver.driverproviders.DriverProvider;
import net.sourceforge.htmlunit.corejs.javascript.tools.shell.QuitAction;
import net.thucydides.core.webdriver.DriverServiceHook;

import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.AfterStory;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class NetworkMapUISteps {

    CommonUtils commonUtils = new CommonUtils();
    ValidationUtil validationUtils = new ValidationUtil();
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private RequestUtil requestUtil = new RequestUtil();

    public NetworkMapUISteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    private Home objhome = new Home();
    private NetworkMapUIPage objNetworkMapUIPage = new NetworkMapUIPage();
    public String strCFCLoc;

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("Clear old data $parms")
    public void cleanOldData(ExamplesTable table) {

        for(int j =0 ; j< table.getRowCount();j++) {
            Map<String, String> row = table.getRow(j);
            String GETCallEndpoint = commonUtils.getUrl(row.get("getRoute"));
            String GETQueryParams = row.get("GETQueryParams");
            Map<String, String> processedGetQP = requestUtil.getRandomParamsfromMap(GETQueryParams);
            Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP)
                    .asResponse();
            log.info("Routes: {}", GETResponse.asString());
            CommonUtils.doJbehavereportConsolelogAndAssertion("Get Routes", "Routes:" + GETResponse.asString(), true);
            if (200 == GETResponse.statusCode()) {

                JSONArray routeFetched = new JSONArray(GETResponse.asString());
                for (int i = 0; i < routeFetched.length(); i++) {
                    JsonPath rtfFetchedPath = new JsonPath(routeFetched.getJSONObject(i).toString());

                    String strStartTime = rtfFetchedPath.getString("effectiveTimeStart").substring(0, 10);
                    String strEndTime = rtfFetchedPath.getString("effectiveTimeEnd").substring(0, 10);
                    String strGivenStartTime;
                    String strGivenEndTime;

                    Iterator<Map<String, String>> iterator = table.getRows().iterator();
                    iterator.hasNext();
                    Map<String, String> exRows = iterator.next();
                    strGivenStartTime = exRows.get("StartDate");
                    strGivenEndTime = exRows.get("EndDate");
                    log.info("response time" + strStartTime + strEndTime + "Given time" + strGivenStartTime
                            + strGivenEndTime);

                    if (strStartTime.equals(strGivenStartTime) && strEndTime.equals(strGivenEndTime)) {
                        String routeID = rtfFetchedPath.getString("id");
                        Map<String, String> row1 = table.getRow(0);
                        String GETCallEndpoint1 = commonUtils.getUrl(row1.get("deleteRoute"));
                        String strID = routeID;
                        log.info(strID);
                        Response response = WhmRestCoreAutomationUtils
                                .deleteRequestResponse(GETCallEndpoint1.replace("{id}", strID)).asResponse();
                        log.info("Deleted: {}", response.asString());
                        if (204 == response.getStatusCode()) {
                            log.info("Routes deleted successfully");
                            CommonUtils.doJbehavereportConsolelogAndAssertion("Routes Deleted Successfully", "ID:" + strID, true);
                        } else {
                            Assert.fail("Unable to delete routes");
                        }
                    }

                }

            }

        }
    }

    @Given("user logged in NetworkMap UI and selected Add Route of Network Map")
    public void LoginAndSelectAddRoute() {
        objhome.UIlogin();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Login Successfully", "", true);
        objNetworkMapUIPage.clickAddRouteLink();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Clicked on Add Route Link", "", true);

    }
    @Given("user switched to new NetworkMap UI window and selected Add Route of Network Map")
    public void SelectAddRoute() {
        objNetworkMapUIPage.clickAddRouteLink();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Clicked on Add Route Link", "", true);

    }


    @Given("user logged in NetworkMap UI and selected View Route of Network Map")
    public void loginAndSelectViewRoute() {
        objhome.UIlogin();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Login Successfully", "", true);
        objNetworkMapUIPage.clickViewRouteLink();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Clicked on View Route Link", "", true);

    }

    @When("User select $routeType tab and provide details for Add Route $parms")
    public void whenProvideDetailsForRoute(String routeType, ExamplesTable table) {
        objNetworkMapUIPage.clickValidateAddRoute();
        String strCFC = null;
        String strSDC = null;
        String strDate = null;
        String strMergeCenter = null;
        if (table.getRowCount() == 1) {
            Iterator<Map<String, String>> iterator = table.getRows().iterator();
            iterator.hasNext();
            Map<String, String> exRows = iterator.next();
            strCFC = exRows.get("CFC");
            strSDC = exRows.get("SDC");
            strDate = exRows.get("Date");
            if (routeType.equalsIgnoreCase("Direct")) {

                objNetworkMapUIPage.provideRouteDetails(strCFC, "", strSDC, strDate);
                CommonUtils.doJbehavereportConsolelogAndAssertion("Provied Direct Route details for ", "CFC :- "+ strCFC +"SDC :- " +strSDC+"Date :- "+strDate , true);
            }
            if (routeType.equalsIgnoreCase("Merge")) {
                strMergeCenter = exRows.get("MergeCenter");
                objNetworkMapUIPage.provideRouteDetails(strCFC, strMergeCenter, strSDC, strDate);
                CommonUtils.doJbehavereportConsolelogAndAssertion("Provied Merge Route details for ", "CFC :- "+ strCFC + " Merge Center :- "+strMergeCenter +" SDC :- " +strSDC+" Date :- "+strDate , true);
            }
        }

    }



    @When("User provide the CFC details $parms")
    public void provideCFCDetailsForSearch(ExamplesTable table) {
        String strCFC2 = null;

        if (table.getRowCount() == 1) {
            Iterator<Map<String, String>> iterator = table.getRows().iterator();
            iterator.hasNext();
            Map<String, String> exRows = iterator.next();
            strCFC2 = exRows.get("CFC");

        }
        objNetworkMapUIPage.provideOnlyCFCDetailsForSearch(strCFC2);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Provied Search Route details for ", "CFC :- "+ strCFC2 , true);
        strCFCLoc = strCFC2;
    }

    @When("User provide the CFC and Date details $parms")
    public void provideCFCAndDateDetailsForSearch(ExamplesTable table) {
        String strCFC2 = null;
        String strDate2 = null;

        if (table.getRowCount() == 1) {
            Iterator<Map<String, String>> iterator = table.getRows().iterator();
            iterator.hasNext();
            Map<String, String> exRows = iterator.next();
            strCFC2 = exRows.get("CFC");
            strDate2 = exRows.get("Date");

        }
        objNetworkMapUIPage.provideDetailsForSearch(strCFC2, strDate2);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Provied Search Route details for ", "CFC :- "+ strCFC2 +" Date :- "+strDate2 , true);
    }

    @Then("A new window opens for NetworkMapUI and user switches to new window for validations")
    public void switchToNewWindow(){
        int count=0;
        WebDriverWait wait=new WebDriverWait(driver,10);
        wait.until(driver-> driver.getWindowHandles().size()==2);
        String parentWindow = driver.getWindowHandle();
        Set<String> handles =  driver.getWindowHandles();
        for(String windowHandle  : handles)
        {
            System.out.println("windowHandle :" + windowHandle);
            if(!windowHandle.equals(parentWindow))
            {
                driver.switchTo().window(windowHandle);
                CommonUtils.doJbehavereportConsolelogAndAssertion("New Window opened successfully", "switched to new window with URL = : "  + driver.getCurrentUrl(), true);
                count++;
            }

        }
        if(count==0){
            Assert.fail("No New window opened");
        }
    }


    @When("clicked on Search button")
    public void clickOnSearchButton() {
        objNetworkMapUIPage.clickOnSearchButton();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Clicked on Search ", "Search" , true);
    }

    @When("clicked on Add Route button")
    public void whenClickedOnAddRouteButton() {
        objNetworkMapUIPage.clickAddRouteButton();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Clicked on Add Route Button ", "Add Route" , true);
    }

    @When("click on the copy action for the searched route")
    public void clickOnCopyButton() {
        objNetworkMapUIPage.clickCopyButton();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Clicked on Copy ", "Copy" , true);
    }

    @When("User provide the SDC on the Copy route $parms")
    public void changeSDCOnTheCopyRoutePage(ExamplesTable table) {

        String strSDC1 = null;

        if (table.getRowCount() == 1) {
            Iterator<Map<String, String>> iterator2 = table.getRows().iterator();
            iterator2.hasNext();
            Map<String, String> exRows1 = iterator2.next();
            strSDC1 = exRows1.get("SDC");

        }
        objNetworkMapUIPage.provideSDCOnCopyRoutePage(strSDC1);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Select SDC ", "SDC" , true);
    }

    @When("click on the cancel button")
    public void clickOnCancelButtonOnCopy() {
        objNetworkMapUIPage.clickCancelButtonOnCopy();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Click on the Cancel button on copy ", "Cancel" , true);
    }

    @When("click on the Add route button on the Copy route pop up")
    public void clickOnAddRouteButtonOnCopyRoutePopUP() {
        objNetworkMapUIPage.clickOnAddRouteButtonOnCopy();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Click on Add Route Button on Copy ", "Add Route Button" , true);
    }

    @When("click on the edit button for a route")
    public void clickOnEditRouteButton() {
        objNetworkMapUIPage.clickOnEditButtonOnGrid();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Click on the Edit button on Grid ", "Edit Button" , true);
    }

    @When("edit the SDC option $parms")
    public void clickOnEditSDCOption(ExamplesTable table) {
        String strSDC1 = null;
        if (table.getRowCount() == 1) {
            Iterator<Map<String, String>> iterator2 = table.getRows().iterator();
            iterator2.hasNext();
            Map<String, String> exRows1 = iterator2.next();
            strSDC1 = exRows1.get("SDC");
            objNetworkMapUIPage.provideSDCOnEditRoutePage(strSDC1);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Edit the SDC Value ", "SDC :- " + strSDC1 , true);
        }

    }

    @When("Click on the Save route button")
    public void clickOnSaveRouteButton() {
        objNetworkMapUIPage.clickOnSaveRouteButtonOnPopUp();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Click on Save Route button ", "Save Route" , true);
    }

    @When("click on the delete button for a route")
    public void clickOnDeleteButton() {
        objNetworkMapUIPage.clickDeleteButton();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Click on Delete Button", "Delete Button" , true);
    }

    @When("click on the Delete route button on the pop up")
    public void clickOnDeleteButtonOnPopUp() {
        objNetworkMapUIPage.clickDeleteButtonOnPopUp();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Click on Delete Button on Pop up ", "Delete" , true);
    }

    @Then("Route Added successfully message is displayed")
    public void routeAddedSuccessfullyMessageIsDisplayed() {
        objNetworkMapUIPage.validateAddRouteMessage();
    }

    @Then("Route deleted successfully message is displayed")
    public void routeDeletedMessage() {
        objNetworkMapUIPage.validateRouteDeletedMessageOnPopUp();
    }

    @Then("Route Added successfully message is displayed on pop up")
    public void routeAddedSuccessfullyMessageOnCopy() {
        objNetworkMapUIPage.validateAddRouteSuccessMessageOnCopy();
    }

    @Then("Cannot create new Route message is displayed")
    public void routeCannotBeAddedMessageIsDisplayed() {
        objNetworkMapUIPage.alreadyAddedRouteMessage();
    }

    @Then("All the route for the given CFC should be displayed in the grid and validate it with DB")
    public void searchResultForCFC() throws Exception {
        objNetworkMapUIPage.comparereadWebTableWithDB(strCFCLoc);
    }

    @Then("the copy pop up is closed")
    public void validateCopyPopUPIsClosed() {
        objNetworkMapUIPage.validatePopUpWindowIsClosed();
    }

    @Then("Select logout")
    public void clickLogout() {
        objNetworkMapUIPage.clickLogout();
    }
}
