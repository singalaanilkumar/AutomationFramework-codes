package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.Atlas.pageobjects.ApolloUIPage;
import com.macys.mst.Atlas.pageobjects.HandheldPage;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ApolloUISteps {
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    ApolloUIPage apolloUIPage = PageFactory.initElements(driver, ApolloUIPage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;

    public ApolloUISteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("User logs in Apollo")
    public void loginApollo(){
        apolloUIPage.navigateToApollo();
    }

    @Then("User navigates to Talos->$page")
    public void navigateToTalos(String page) throws Exception {
        apolloUIPage.navigateToApolloTalos(page);
    }

    @Then("User navigates to mainmenu $MainMenu and submenu $SubMenu")
    public void navigateToApolloMainMenuSubMenu(String mainMenu, String subMenu) throws Exception {
        apolloUIPage.navigateToMainMenuSubMenu(mainMenu,subMenu);
    }

    @Then("User searches for given package no in Package Detail Inquiry")
    public void searchPackageNo() throws InterruptedException{
        apolloUIPage.searchUsingPackageNo();
    }

    @Then("User enters given package no in Manifest Package")
    public void entersPackageNo() throws InterruptedException{
        apolloUIPage.enterPackageNo();
    }

    @Then("User generates invoice")
    public void downloadInv() throws InterruptedException, IOException, AWTException {
        apolloUIPage.downloadInvoice();
    }

    @Then("User generates shipment label")
    public void downloadShipmentLbl() throws InterruptedException{
        apolloUIPage.downloadShipmentLabel();
    }

    @Then("User Logs out from Apollo")
    public void logout() throws InterruptedException{
        apolloUIPage.logoutFromApollo();
    }

    @Then("User enters estimated weight from DB in weight field")
    public void enterWeight() throws InterruptedException{
        apolloUIPage.enterWeightInWeightField();
    }

    @Then("User user clicks on Manifest button")
    public void clickOnManifest() throws InterruptedException{
        apolloUIPage.clickOnManifestButton();
    }

    @Then("User validates manifest message on page")
    public void validateMessage() throws InterruptedException{
        apolloUIPage.validateManifestMessage();
    }










}
