package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.Map;
import java.util.Map.Entry;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.javascript.host.Set;
import com.github.javaparser.utils.Log;
import com.google.api.gax.rpc.PagedCallSettings;
import com.google.rpc.Help.Link;
import com.ibm.icu.impl.Assert;
import com.ibm.icu.impl.UResource.Array;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.HoldAndFlowSQL;
import com.macys.mst.DC2.EndToEnd.db.app.NetworkMapSQL;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.ManifestSteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.NetworkMapUISteps;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
//import com.sun.tools.classfile.Annotation.element_value;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.vavr.collection.LinkedHashMap;
import jnr.ffi.Struct.Boolean;
import net.thucydides.core.annotations.Screenshots;
import net.thucydides.core.model.TakeScreenshots;

public class NetworkMapUIPage extends BasePage {

    CommonUtils commonUtils = new CommonUtils();

    private SeUiContextBase seUiContextBase = new SeUiContextBase();
    private static Logger log = Logger.getLogger(ManifestSteps.class);
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    ValidationUtil validationUtils = new ValidationUtil();


    @FindBy(xpath = "//*[@id='listitemleaf']/div/span[contains(text(),'Add Route')]")
    public WebElement AddRoute;

    @FindBy(xpath = "//*[@id='appRouts']//button/*[contains(text(),'Direct')]")
    public WebElement DirectTab;

    @FindBy(xpath = "//*[@class='input-label-message'][contains(text(),'CFC')]//following::div[2]")
    public WebElement CFC;

    @FindBy(xpath = "//*[@id='appRouts']//*[contains(text(),'TULSA')]")
    public WebElement CFCValue;

    @FindBy(xpath = "//*[@class='input-label-message'][contains(text(),'SDC')]//following::div[2]")
    public WebElement SDC;

    @FindBy(xpath = "//*[@id='appRouts']//*[contains(text(),'HAYWARD')]")
    public WebElement SDCValue;

    @FindBy(xpath = "//*[@id='appRouts']//*[contains(text(),'Date Range')]//following::input")
    public WebElement DateRange;

    @FindBy(xpath = "//*[contains(text(),'Date Range')]//following::input")
    public WebElement ViewRouteDateRange;

    @FindBy(xpath = "//*[@id='appRouts']//button/div[contains(text(),'Add Route')]")
    public WebElement AddRouteButton;

    @FindBy(xpath = "//*[@id='appRouts']//div[contains(text(),'Route added successfully.')]")
    public WebElement AddRouteSuccessMessage;

    @FindBy(xpath = "//*[@class='input-label-message'][contains(text(),'Merge Center')]")
    public WebElement MergeCenterLabel;

    @FindBy(xpath = "//*[@id='appRouts']//button/*[contains(text(),'Merge')]")
    public WebElement MergeTab;

    @FindBy(xpath = "//*[@class='input-label-message'][contains(text(),'Merge Center')]//following::div[2]")
    public WebElement MergeCenter;

    @FindBy(xpath = "//*[@id='appRouts']//*[contains(text(),'NORTH BERGEN')]")
    public WebElement MergeCenterValue;

    @FindBy(xpath = "//*[@class='message']//div[@class='translation-message']")
    public WebElement AlreadyAddRouteMessage;

    @FindBy(xpath = "//div[@class='button-content'][contains(text(),'Close')]")
    public WebElement AlreadyAddRouteMessageCloseButton;

    @FindBy(xpath = "//*[@id='listitemleaf']/div/span[contains(text(),'View Route')]")
    public WebElement ViewRoute;

    @FindBy(xpath = "//*[@id='appRouts']//button/div[contains(text(),'Search')]")
    public WebElement SearchButton;

    @FindBy(xpath = "//*[@id='appRouts']//div//table")
    public WebElement DataTable;

    @FindBy(xpath = "//*[@id='appRouts']//table/tbody/tr[1]//td//*[@title='Copy']")
    public WebElement CopyButton;

    @FindBy(xpath = "//*[@class='button-content'][contains(text(),'Cancel')]")
    public WebElement CancelButtonOnCopy;

    @FindBy(xpath = "//div//h1[contains(text(),'Copy to New Route')]")
    public WebElement CopyToNewRouteLabel;

    @FindBy(xpath = "//div[contains(text(),'Add Route')]")
    public WebElement AddRouteButtonOnCopy;

    @FindBy(xpath = "//div[contains(text(),'Route added successfully.')]")
    public WebElement AddRouteSuccessMessageOnCopy;

    @FindBy(xpath = "//div[@class='ui red circular empty label badge  circle-padding']//parent::div//*[contains(text(),'SDC')]//following::div[2]")
    public WebElement SDCOnCopy;

    @FindBy(xpath = "//*[@class='visible menu transition']//div[contains(text(),'HAYWARD')]")
    public WebElement SDCValueOnCopy;

    @FindBy(xpath = "//*[@id='appRouts']//table/tbody/tr[1]//td//*[@title='Edit']")
    public WebElement EditButton;

    @FindBy(xpath = "//div//h1[contains(text(),'Edit Existing Route')]")
    public WebElement EditExistingRouteLabel;

    @FindBy(xpath = "//div[contains(text(),'Save Route')]")
    public WebElement SaveButtonOnPopUp;

    @FindBy(xpath = "//*[@id='appRouts']//table/tbody/tr[1]//td//*[@title='Delete']")
    public WebElement DeleteButton;

    @FindBy(xpath = "//div[contains(text(),'Delete Route')]")
    public WebElement DeleteButtonOnPopUp;

    @FindBy(xpath = "//div[contains(text(),'Route deleted successfully.')]")
    public WebElement DeleteRouteMessage;

    @FindBy(xpath = "//table/thead[@class='p-datatable-thead']/tr/th")
    public WebElement TableHeaders;

    @FindBy(xpath = "//span[text()='Logout']")
    public WebElement LogoutButton;

    public void clickAddRouteLink() {
        getWait().until(ExpectedConditions.visibilityOf(AddRoute));
        AddRoute.click();
        getWait().until(ExpectedConditions.elementToBeClickable(DirectTab));
        log.info("Clicked on the Add Route Link");
    }

    public void clickViewRouteLink() {
        getWait().until(ExpectedConditions.visibilityOf(ViewRoute));
        ViewRoute.click();
        getWait().until(ExpectedConditions.elementToBeClickable(DirectTab));
        log.info("Clicked on the View Route Link");
    }

    public void clickValidateAddRoute() {

        WebElement DirectTabAttribute = driver
                .findElement(By.xpath("//*[@id='appRouts']//*[@class='ui buttons']/child::button[1]"));
        String strValidateDriect = DirectTabAttribute.getAttribute("class");
        assertEquals(strValidateDriect, "ui small button primary switch", "Direct Tab not selected by default");
        getWait().until(ExpectedConditions.elementToBeClickable(CFC));
        log.info("Add Route Tab is selected by default");
    }

    public void clickCopyButton() {
        getWait().until(ExpectedConditions.visibilityOf(DataTable));
        getWait().until(ExpectedConditions.elementToBeClickable(DataTable));
        CopyButton.click();
        getWait().until(ExpectedConditions.visibilityOf(CopyToNewRouteLabel));
        getWait().until(ExpectedConditions.elementToBeClickable(CopyToNewRouteLabel));
        log.info("Clicked on the Copy button");

    }

    public void clickDeleteButton() {
        getWait().until(ExpectedConditions.visibilityOf(DataTable));
        getWait().until(ExpectedConditions.elementToBeClickable(DataTable));
        DeleteButton.click();
        getWait().until(ExpectedConditions.visibilityOf(DeleteButtonOnPopUp));
        getWait().until(ExpectedConditions.elementToBeClickable(DeleteButtonOnPopUp));
        log.info("Clicked on the Delete Button");
    }

    public void clickDeleteButtonOnPopUp() {
        DeleteButtonOnPopUp.click();
        log.info("Clicked on the Delete button on the Pop Up");

    }

    public void clickLogout() {
        getWait().until(ExpectedConditions.elementToBeClickable(LogoutButton));
        getWait(30).ignoring(Exception.class).until(visibilityOf(LogoutButton));
        LogoutButton.click();
        log.info("Logged out from the application");
        CommonUtils.doJbehavereportConsolelogAndAssertion("Logged out from the application", "" , true);
    }

    public void provideSDCOnCopyRoutePage(String strSDC) {
        getWait().until(ExpectedConditions.visibilityOf(CopyToNewRouteLabel));
        getWait().until(ExpectedConditions.elementToBeClickable(CopyToNewRouteLabel));
        getWait().until(ExpectedConditions.visibilityOf(SDCOnCopy));
        getWait().until(ExpectedConditions.elementToBeClickable(SDCOnCopy));
        SDCOnCopy.click();
        // SDCOnCopy.sendKeys("{TAB}");
        getWait(30).ignoring(Exception.class).until(visibilityOf(SDCValueOnCopy));
        WebElement strElement3 = driver.findElement(By
                .xpath("/html/body/div[2]/div/div/div/div/div[1]/div[3]/div[3]/div/div[2]/div[2]/div[contains(text(),'"
                        + strSDC + "')]"));
        strElement3.click();

    }

    public void provideSDCOnEditRoutePage(String strSDC) {

        getWait().until(ExpectedConditions.visibilityOf(EditExistingRouteLabel));
        getWait().until(ExpectedConditions.elementToBeClickable(EditExistingRouteLabel));
        SDCOnCopy.click();
        // SDCOnCopy.sendKeys("{TAB}");
        getWait(30).ignoring(Exception.class).until(visibilityOf(SDCValueOnCopy));
        WebElement strElement3 = driver.findElement(By
                .xpath("/html/body/div[2]/div/div/div/div/div[1]/div[3]/div[3]/div/div[2]/div[2]/div[contains(text(),'"
                        + strSDC + "')]"));
        strElement3.click();

    }

    public void clickOnSaveRouteButtonOnPopUp() {
        SaveButtonOnPopUp.click();
        getWait(30).ignoring(Exception.class).until(visibilityOf(AddRouteSuccessMessageOnCopy));
        getWait().until(ExpectedConditions.elementToBeClickable(AddRouteSuccessMessageOnCopy));
        log.info("Clicked on the Save Route Button on the Pop UP");

    }

    public void validateRouteDeletedMessageOnPopUp() {
        getWait().until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[contains(text(),'Route deleted successfully.')]")));

        boolean successMessage = DeleteRouteMessage.isDisplayed();
        if (successMessage == false) {

            Assert.fail("Route Not Deleted Successfully");
        } else {

            log.info("Route Deleted Successfully");
            CommonUtils.doJbehavereportConsolelogAndAssertion("Delete Message ", "Message :- " + DeleteRouteMessage.getText() , true);
        }

    }

    public void clickCancelButtonOnCopy() {

        getWait().until(ExpectedConditions.visibilityOf(CancelButtonOnCopy));
        getWait().until(ExpectedConditions.elementToBeClickable(CancelButtonOnCopy));
        CancelButtonOnCopy.click();
        getWait().until(ExpectedConditions.visibilityOf(DataTable));
        getWait().until(ExpectedConditions.elementToBeClickable(DataTable));
        log.info("Clicked on the Cancel Button on Copy");
    }

    public void validatePopUpWindowIsClosed() {

        getWait().until(ExpectedConditions.visibilityOf(DataTable));
        getWait().until(ExpectedConditions.elementToBeClickable(DataTable));

        boolean isPresent = driver.findElements(By.xpath("//div//h1[contains(text(),'Copy to New Route')]")).size() > 0;

        if (isPresent == true) {
            Assert.fail("Copy Pop Up Window is not closed");
        } else {
            log.info("Copy Pop Up Window is closed");
            CommonUtils.doJbehavereportConsolelogAndAssertion("POP Up window closed", "" , true);
        }

    }

    public void provideOnlyCFCDetailsForSearch(String strCFC) {

        getWait().until(ExpectedConditions.elementToBeClickable(CFC));
        CFC.click();
        getWait().until(ExpectedConditions.elementToBeClickable(CFCValue));
        WebElement strElement = driver
                .findElement(By.xpath("//*[@id='appRouts']//*[contains(text(),'" + strCFC + "')]"));
        strElement.click();
    }

    public void provideDetailsForSearch(String strCFC, String strDate) {

        getWait().until(ExpectedConditions.elementToBeClickable(CFC));
        CFC.click();
        getWait().until(ExpectedConditions.elementToBeClickable(CFCValue));
        WebElement strElement = driver
                .findElement(By.xpath("//*[@id='appRouts']//*[contains(text(),'" + strCFC + "')]"));
        strElement.click();
        DirectTab.click();
        getWait().until(ExpectedConditions.elementToBeClickable(ViewRouteDateRange));
        getWait().until(ExpectedConditions.visibilityOf(ViewRouteDateRange));
        ViewRouteDateRange.sendKeys(strDate);

    }

    public void clickOnSearchButton() {

        SearchButton.click();
        getWait().until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//*[@id='appRouts']//button/div[contains(text(),'Search')]")));
        getWait().until(ExpectedConditions
                .elementToBeClickable(By.xpath("//*[@id='appRouts']//button/div[contains(text(),'Search')]")));
        log.info("Clicked on the Search Button");

    }

    public void clickAddRouteButton() {
        AddRouteButton.click();
        getWait().until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//*[@id='appRouts']//button/div[contains(text(),'Add Route')]")));
        getWait().until(ExpectedConditions
                .elementToBeClickable(By.xpath("//*[@id='appRouts']//button/div[contains(text(),'Add Route')]")));
        getWait().until(ExpectedConditions.elementToBeClickable(AddRouteButton));
        log.info("Clicked on Add Route Button");

    }

    public void validateAddRouteMessage() {
        getWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='appRouts']//div[contains(text(),'Route added successfully.')]")));
        getWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='appRouts']//div[contains(text(),'Route added successfully.')]")));

        boolean successMessage = AddRouteSuccessMessage.isDisplayed();
        if (successMessage == false) {

            Assert.fail("Route not added");
        } else {
            Log.info("Route Added Successfully");
            CommonUtils.doJbehavereportConsolelogAndAssertion("Add Route Message ", "Message" + AddRouteSuccessMessage.getText() , true);
        }

    }

    public void clickMergetab() {
        getWait().until(ExpectedConditions.visibilityOf(MergeTab));
        getWait().until(ExpectedConditions.elementToBeClickable(MergeTab));
        MergeTab.click();
        log.info("Clicked on Merge Tab");

    }

    public void provideRouteDetails(String strCFC, String strMergeCenter, String strSDC, String strDateRange) {

        getWait().until(ExpectedConditions.elementToBeClickable(CFC));
        CFC.click();
        getWait().until(ExpectedConditions.elementToBeClickable(CFCValue));
        WebElement strElement = driver
                .findElement(By.xpath("//*[@id='appRouts']//*[contains(text(),'" + strCFC + "')]"));
        strElement.click();
        getWait(5000);

        if (!strMergeCenter.isEmpty()) {
            clickMergetab();
            getWait().until(ExpectedConditions.visibilityOf(MergeCenter));
            getWait().until(ExpectedConditions.elementToBeClickable(MergeCenter));
            MergeCenter.click();
            getWait().until(ExpectedConditions.visibilityOf(MergeCenterValue));
            getWait().until(ExpectedConditions.elementToBeClickable(MergeCenterValue));
            WebElement strElement3 = driver
                    .findElement(By.xpath("//*[@id='appRouts']//*[contains(text(),'" + strMergeCenter + "')]"));
            strElement3.click();
            getWait(5000);
        }
        getWait(30).ignoring(Exception.class).until(visibilityOf(SDC));
        SDC.click();
        getWait().until(ExpectedConditions.elementToBeClickable(SDCValue));
        WebElement strElement2 = driver
                .findElement(By.xpath("//*[@id='appRouts']//*[contains(text(),'" + strSDC + "')]"));
        strElement2.click();
        if (!strMergeCenter.isEmpty()) {
            getWait(30).ignoring(Exception.class).until(visibilityOf(MergeCenter));
        }
        DateRange.sendKeys(strDateRange);
    }

    public void alreadyAddedRouteMessage() {

        getWait(30).ignoring(Exception.class).until(visibilityOf(AlreadyAddRouteMessageCloseButton));
        getWait().until(ExpectedConditions.elementToBeClickable(AlreadyAddRouteMessageCloseButton));
        WebElement messageAlreadyAdded = driver
                .findElement(By.xpath("//*[@class='message']//div[@class='translation-message']"));
        String strMessage = messageAlreadyAdded.getText();
        String strMessage1 = strMessage.substring(0, 24);
        String strMessageExpected = "Cannot create new Route.";
        assertEquals(strMessageExpected, strMessage1);
        AlreadyAddRouteMessageCloseButton.click();

        CommonUtils.doJbehavereportConsolelogAndAssertion("Route Already exist ", "Message :- " + strMessage , true);

    }

    public void clickOnAddRouteButtonOnCopy() {

        getWait().until(ExpectedConditions.visibilityOf(CopyToNewRouteLabel));
        getWait().until(ExpectedConditions.elementToBeClickable(CopyToNewRouteLabel));
        AddRouteButtonOnCopy.click();
        getWait(30).ignoring(Exception.class).until(visibilityOf(AddRouteSuccessMessageOnCopy));
        getWait().until(ExpectedConditions.elementToBeClickable(AddRouteSuccessMessageOnCopy));
        log.info("Clicked on the Add Route Button on Copy page");

    }

    public void clickOnEditButtonOnGrid() {

        getWait().until(ExpectedConditions.visibilityOf(DataTable));
        getWait().until(ExpectedConditions.elementToBeClickable(DataTable));
        EditButton.click();
        getWait().until(ExpectedConditions.visibilityOf(EditExistingRouteLabel));
        getWait().until(ExpectedConditions.elementToBeClickable(EditExistingRouteLabel));
        log.info("Clicked on the Edit Button on Grid");
    }

    public void comparereadWebTableWithDB(String strCFCLoc) throws Exception {

        String schema = "cfnetworkmap";
        String getRoute = NetworkMapSQL.selectNetworkRoute.replace("#CFC_Loc_Desc", strCFCLoc);

        // Read from the Web Table Table
        List<String> strRouteID = new ArrayList<String>();
        getWait().until(ExpectedConditions.visibilityOf(TableHeaders));
        getWait().until(ExpectedConditions.elementToBeClickable(TableHeaders));
        String headerLoc = "//table/thead[@class='p-datatable-thead']/tr/th";
        List<WebElement> allHeadersEle = driver.findElements(By.xpath(headerLoc));
        List<String> allHeaderNames = new ArrayList<String>();
        for (WebElement header : allHeadersEle) {
            String headerName = header.getText();
            if (!headerName.equals("ACTIONS")) {
                allHeaderNames.add(headerName);
            }
        }

        String pageIndex = "//*[@id='appRouts']//*[@class='nav-container']//div[starts-with(@class,'page-btn item-btn')]";
        List<WebElement> pageIndexCount = driver.findElements(By.xpath(pageIndex));
        String nextPage ="//*[@id='appRouts']//*[@class='nav-container']//*[@class='page-btn nav-btn']";
        WebElement clickOnNextpage = driver.findElement(By.xpath(nextPage));

        for(int p=0; p<pageIndexCount.size();p++) {

            // Get total rows count//*[@id="appRouts"]//*[@class='p-datatable-row'][1]
            String rowLoc = "//*[@id='appRouts']//*[@class='p-datatable-row']";
            List<WebElement> allRowsEle = driver.findElements(By.xpath(rowLoc));
            HashMap<String, String> eachRowData = new HashMap<>();
            // Starting from 2 as first row is header.
            for (int i = 1; i <= allRowsEle.size(); i++) {
                // Getting specific row with each iteration
                String specificRowLoc = "//table//tbody/tr[" + i + "]";
                // Locating only cells of specific row.
                List<WebElement> allColumnsEle = driver.findElement(By.xpath(specificRowLoc))
                        .findElements(By.tagName("td"));
                // Creating a map to store key-value pair data. It will be created for each
                // iteration of row
                List<HashMap<String, String>> allTableData = new ArrayList<HashMap<String, String>>();
                // Iterating each cell
                for (int j = 0; j < allColumnsEle.size() - 1; j++) {
                    // Getting cell value
                    String cellValue = allColumnsEle.get(j).getText();
                    // We will put in to map with header name and value with iteration
                    // Get jth index value from allHeaderNames and jth cell value of row
                    eachRowData.put(allHeaderNames.get(j), cellValue);
                }
                // After iterating row completely, add in to list.
                allTableData.add(eachRowData);

                for (Map.Entry<String, String> set : eachRowData.entrySet()) {
                    if (set.getKey().contains("ID")) {
                        strRouteID.add(set.getValue());
                    }
                }

            }

            clickOnNextpage.click();
        }
        log.info("the route id are : - " + strRouteID);

        List<String> GetRoutesDetails = DBMethods.getDBValueInList(getRoute, schema);
        boolean isEqual = strRouteID.equals(GetRoutesDetails);
        log.info("Database The route ids are :- " + GetRoutesDetails);

        if (isEqual == false) {
            Assert.fail("Web Table and Database did not match");
        } else {
            log.info("Data in Web Table Matches with Database");
            CommonUtils.doJbehavereportConsolelogAndAssertion("Data in Web Table Matches with Database", "" , true);
        }

    }

    public void validateAddRouteSuccessMessageOnCopy() {

        boolean successMessage = AddRouteSuccessMessageOnCopy.isDisplayed();
        if (successMessage == false) {

            Assert.fail("Route not added");
        } else {
            log.info("Route copied Successfully");
            CommonUtils.doJbehavereportConsolelogAndAssertion("Add Route Message on Copy page ", "Message :- " + AddRouteSuccessMessageOnCopy.getText() , true);
        }

    }

}
