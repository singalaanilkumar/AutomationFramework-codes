package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.LocateContainerPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LocateContainerSteps {
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private CommonUtils commonUtils = new CommonUtils();
    LocateContainerPage locateContainerPage = PageFactory.initElements(driver, LocateContainerPage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    public LocateContainerSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

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
            dataStorage.getStoredData().put("Tote_locataeContainer", containerbarcode);
            break;
        }
        locateContainerPage.scanContainerBarcode(containerbarcode);
        locateContainerPage.scanStaingLocation(location);
        locateContainerPage.clickButton("exit");
    }
}
