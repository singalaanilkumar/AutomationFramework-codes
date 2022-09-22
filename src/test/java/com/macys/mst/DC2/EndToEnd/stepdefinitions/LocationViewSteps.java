package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.LocationView;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class LocationViewSteps {
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private LocationView LocationViewPage = PageFactory.initElements(driver, LocationView.class);
    private StepsContext stepsContext;
    CreateToteSteps crtToteStp = new CreateToteSteps(stepsContext);
    @Then("user validate $selectedtab tab for $locationvalue location")
    public void LocationView(String selectedtab,String locationvalue)
    {
        LocationViewPage.selectLocationType("6");
        String LocationBarcode = (String) dataStorage.getStoredData().get("stageLocation");
        System.out.println("LstLocationBarcode.get(0) "+ LocationBarcode);
        LocationViewPage.selectAttributeValue(LocationBarcode);
        LocationViewPage.clickSearchButton();
        LocationViewPage.validateLocationHeaderView();
        LocationViewPage.validateLocationView(LocationBarcode);
    }


    @When("user edits $optionNumber in attributes named $dropdown")
    public void userSelectsOptionNoInAttributesDropdown(int optionNumber, String dropdown) {
        LocationViewPage.userClicksButton("EDIT");
        LocationViewPage.userSelectsAttributesValueFromDropdown(optionNumber, dropdown);
    }

    @When("user updates attributes named $dropdown with original values")
    public void userUpdatesOptionNoInAttributesDropdown(String dropdown) {
        LocationViewPage.userClicksButton("EDIT");
        LocationViewPage.userUpdatesAttributesValueFromDropdown(dropdown);
    }

}
