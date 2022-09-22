package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.Atlas.pageobjects.HandheldPage;
import com.macys.mst.Atlas.utilmethods.CommonUtils;
import com.macys.mst.Atlas.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.concurrent.ConcurrentHashMap;

public class HandheldSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    HandheldPage handheldPage = PageFactory.initElements(driver, HandheldPage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;

    public HandheldSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("User logs in Handheld")
    public void loginToHandheld() throws Exception {
        handheldPage.navigateToHandheld();
    }

    @When("User navigates to outbound->packing->pack and print option and scans package_no")
    public void scanPrinter() throws Exception {
        handheldPage.scanPrinter();
    }

    @When("User navigates to outbound->exceptions->rf manifest package option and scans package_no")
    public void rfManifest() throws Exception {
        handheldPage.rfManifestPackageNumber();
    }

    @Then("User validates manifest request submitted message")
    public void validateManifest() throws Exception {
        handheldPage.validateManifestation();
    }





}
