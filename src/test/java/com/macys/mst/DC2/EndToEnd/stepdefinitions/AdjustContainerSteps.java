package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateTotePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PrintTicketPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.AdjustContainer;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdjustContainerSteps {
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    CreateTotePage createTotePage = PageFactory.initElements(driver, CreateTotePage.class);
    PrintTicketPage printTicketPage = PageFactory.initElements(driver, PrintTicketPage.class);
    AdjustContainer AdjustContainerPage=PageFactory.initElements(driver, AdjustContainer.class);
    private StepsContext stepsContext;
    public AdjustContainerSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }


    private WebDriverWait getWait(int secs) {
        WebDriverWait wait = new WebDriverWait(driver, secs);
        return wait;
    }

    public void getContainer() {
        Map<String, List<String>> poLinesToteId = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
        if (!poLinesToteId.isEmpty()) {
            List<String> toteIds = poLinesToteId.values().stream().flatMap(List::stream).collect(Collectors.toList());
            String Container=toteIds.get(0);
            dataStorage.getStoredData().put("Containervalue",Container);
        }
    }

    @When("Inventory Adjustment is done for the tote")
    public void selectAdjustContainer() throws Exception
    {
        createTotePage.navigateToCreateTote();
        getWait(30);
        printTicketPage.selectOptionFromMenu("Adjust Container");
        verifyScanContainerScreen();
        getContainer();
        AdjustContainerPage.performAdjustContainer();
    }

    @Then("Adjsut Container-Scan Container ID Screen is displayed")
    public void verifyScanContainerScreen() {
        AdjustContainerPage.validateScanContainerScreen();
    }

}
