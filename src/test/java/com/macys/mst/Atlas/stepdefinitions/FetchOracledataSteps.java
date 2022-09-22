package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.Atlas.db.app.DBInitilizer;
import com.macys.mst.Atlas.db.app.DBMethods;
import com.macys.mst.Atlas.db.app.SQLQueries;
import com.macys.mst.Atlas.pageobjects.FetchOracleDataPage;
import com.macys.mst.Atlas.utilmethods.CommonUtils;
import com.macys.mst.Atlas.utilmethods.ExpectedDataProperties;
import com.macys.mst.Atlas.utilmethods.RequestUtil;
import com.macys.mst.Atlas.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class FetchOracledataSteps {
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
    private FetchOracleDataPage fetchOracleData = PageFactory.initElements(driver, FetchOracleDataPage.class);
    private ExpectedDataProperties expectedDataProperties = new ExpectedDataProperties();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private RequestUtil requestUtil = new RequestUtil();
    private CommonUtils commonUtils = new CommonUtils();
    private ValidationUtil validationUtils = new ValidationUtil();
    public static String shipment_number = null;

    public FetchOracledataSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("Delete records in db for package_number $packageNo")
    public void deleteRecords(String packageNo){
        fetchOracleData.deleteRecordsFromMPackage(packageNo);
    }

    @Given("Get TestData from DB")
    public void getTestDataInDB() throws ParseException {
        fetchOracleData.getTestData();
    }

    @Given("Update dates for test data in DB for given package no")
    public void updateTestDataInDB() throws ParseException {
        fetchOracleData.updateTestData();
    }

    @Then("User validates ship_via of package in DB")
    public void validateShipVia(){
        fetchOracleData.validateShipViaForPackage();
    }

    @Then("User validates status of package updated to $status in DB")
    public void validateStatus(String statusNo){
        fetchOracleData.validateStatusForPackage(statusNo);
    }
}
