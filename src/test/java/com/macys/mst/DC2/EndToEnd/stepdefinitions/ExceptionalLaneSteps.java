package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateTotePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.ContainerInquiryPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.ExceptionalLanePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.LocateContainerPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ExceptionalLaneSteps {
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private CommonUtils commonUtils = new CommonUtils();
    LocateContainerPage locateContainerPage = PageFactory.initElements(driver, LocateContainerPage.class);
    ExceptionalLanePage exceptionalLanePage = PageFactory.initElements(driver, ExceptionalLanePage.class);
    private ContainerInquiryPage containerInquiryPage = PageFactory.initElements(driver, ContainerInquiryPage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    public ExceptionalLaneSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }
    CreateTotePage createTotePage = PageFactory.initElements(driver, CreateTotePage.class);
    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Then("click on Locate Container")
    public void click_locate_Container(){
        locateContainerPage.navigateTolocateContainer();
    }

    @Then("Enter container and assign $location staging location")
    public void assignLocationToContainer(String location){
        ArrayList<String> toteIds = (ArrayList<String>) stepsContext.get(Context.Tote_List.name());
        String containerbarcode="";
        for(String eachTote :toteIds){
            containerbarcode =eachTote;
            break;
        }
        locateContainerPage.scanContainerBarcode(containerbarcode);
        locateContainerPage.scanStaingLocation(location);
        locateContainerPage.clickButton("exit");
    }
   @When("user enter $exceptionID Exception Lane Ids")
    public void enter_Exception_Lane_Id(String location){
       dataStorage.getStoredData().put("exceptionalLane",location);
       locateContainerPage.scanStaingLocation(location);
       CommonUtils.doJbehavereportConsolelogAndAssertion("Exception Lane Entered ",location,true);
   }

    @Then("validated Exception Lane $parm screen details with API")
    public void validate_ExceptionalLane(String parm){
        String LaneId = (String) dataStorage.getStoredData().get("exceptionalLane");
        exceptionalLanePage.waitForexceptionalLaneScreenElemnts();
        Map<String, String> exceptionalLaneUIData = containerInquiryPage.getScreenData("//*[@id='app']/div/div[2]/div/div[3]/div/div");
        exceptionalLaneUIData.remove("Actual Totes");
        Map<String, String> exceptionalLaneActivityDetailsAPI=exceptionalLanePage.getWSMActivitiesforLanes(LaneId);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Exception Lane UI Details Validated for " + LaneId,
                "UI Details " + exceptionalLaneUIData.toString() + " API Details " + exceptionalLaneActivityDetailsAPI.toString(),
                exceptionalLaneUIData.equals(exceptionalLaneActivityDetailsAPI));
        dataStorage.getStoredData().put("ExpecteddToteQty",exceptionalLaneUIData.get("Expected Totes").toString());
        dataStorage.getStoredData().put("ActivityID_ExceptionLane",exceptionalLaneActivityDetailsAPI.get("Activity ID").toString());
    }

    @Then("Enter the Actual qty and validate the activity for $parm lane")
    public void validate_exceptionalLane_activity(String parm){
        String ExceptionalLane_activityId = (String) dataStorage.getStoredData().get("ActivityID_ExceptionLane");
        String actualQty_ToEnter = (String) dataStorage.getStoredData().get("ExpecteddToteQty");
        exceptionalLanePage.enterQty(actualQty_ToEnter);
        exceptionalLanePage.clickButton("Enter");
        exceptionalLanePage.clickButton("OK");
        String laneStatus =exceptionalLanePage.validateExceptionallaneStatus(ExceptionalLane_activityId);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Exception Lane status after working on Lane ",
                laneStatus,  laneStatus.equalsIgnoreCase("Completed"));
    }
}
