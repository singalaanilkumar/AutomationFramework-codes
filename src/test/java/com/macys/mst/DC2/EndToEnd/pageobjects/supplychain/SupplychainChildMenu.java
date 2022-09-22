package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SupplychainChildMenu extends BasePage implements ChildMenu {

    @FindBy(xpath = "//span[text()='Create Configurations']")
    public WebElement CreateConfigurations;

    @FindBy(xpath = "//span[text()='Manage Configurations']")
    public WebElement ManageConfigurations;

    @FindBy(xpath = "//span[text()='Location Components']")
    public WebElement LocationComponents;

    @FindBy(xpath = "//span[text()='Location Types']")
    public WebElement LocationTypes;

    @FindBy(xpath = "//span[text()='Location Component Levels']")
    public WebElement LocationComponentLevels;

    @FindBy(xpath = "//span[text()='Location Maintenance']")
    public WebElement LocationMaintenance;


    @Override
    public void clickOnGivenChildMenu(String childMenu) {
        switch (childMenu) {
            case "CreateConfigurations":
                getWait().until(ExpectedConditions.elementToBeClickable(CreateConfigurations));
                CreateConfigurations.click();
                getWait().until(ExpectedConditions.urlContains("createConfiguration"));
                break;
            case "ManageConfigurations":
                getWait().until(ExpectedConditions.elementToBeClickable(ManageConfigurations));
                ManageConfigurations.click();
                getWait().until(ExpectedConditions.urlContains("manageConfigurations"));
                break;
            case "LocationComponents":
                getWait().until(ExpectedConditions.elementToBeClickable(LocationComponents));
                LocationComponents.click();
                getWait().until(ExpectedConditions.urlContains("locationComponents"));
                break;
            case "LocationTypes":
                getWait().until(ExpectedConditions.elementToBeClickable(LocationTypes));
                LocationTypes.click();
                getWait().until(ExpectedConditions.urlContains("locationTypes"));
                break;
            case "LocationComponentLevels":
                getWait().until(ExpectedConditions.elementToBeClickable(LocationComponentLevels));
                LocationComponentLevels.click();
                getWait().until(ExpectedConditions.urlContains("locationComponentLevels"));
                break;
            case "LocationMaintenance":
                getWait().until(ExpectedConditions.elementToBeClickable(LocationMaintenance));
                LocationMaintenance.click();
                getWait().until(ExpectedConditions.urlContains("locationMaintenance"));
                break;
            default:
                Assert.fail("Passed childmenu value doesn't match, send correct value");
                break;
        }
    }

}
