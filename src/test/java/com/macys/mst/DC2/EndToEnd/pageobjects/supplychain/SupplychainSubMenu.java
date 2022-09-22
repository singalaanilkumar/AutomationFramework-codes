package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SupplychainSubMenu extends BasePage implements SubMenu {

    @FindBy(xpath = "//span[text()='Configuration']")
    public WebElement Configuration;

    @FindBy(xpath = "//span[text()='Location']")
    public WebElement Location;

    @FindBy(xpath = "//span[text()='PO Attributes']")
    public WebElement POAttributes;

    @FindBy(xpath = "//span[text()='Container Inquiry']")
    public WebElement ContainerInquiry;

    @FindBy(xpath = "//span[text()='Inventory Inquiry']")
    public WebElement InventoryInquiry;

    @FindBy(xpath = "//span[text()='Inventory Adjustment Inquiry']")
    public WebElement InventoryAdjustmentInquiry;

    @FindBy(xpath = "//span[text()='Location View']")
    public WebElement LocationView;

    @FindBy(xpath = "//span[text()='Manage Activities']")
    public WebElement ManageActivities;

    @FindBy(xpath = "//span[text()='Dashboard']")
    public WebElement Dashboard;

    @FindBy(xpath = "//span[text()='Workload Planning']")
    public WebElement WorkloadPlanning;

    @FindBy(xpath = "//span[text()='Waves in Progress']")
    public WebElement WavesinProgress;

    @FindBy(xpath = "//span[text()='Swagger Documentation']")
    public WebElement SwaggerDocumentation;

    @FindBy(xpath = "//span[text()='Messaging Support']")
    public WebElement messagingSupport;

    @FindBy(xpath = "//span[text()='MHE Search']")
    public WebElement MheSearch;

    @FindBy(xpath = "//span[text()='Diagnostics']")
    public WebElement Diagnostics;

    @FindBy(xpath = "//span[text()='Container Support']")
    public WebElement ContainerSupport;

    @Override
    public void clickOnGivenSubMenu(String selecetedSubMenu) {
        switch (selecetedSubMenu) {
            case "Configuration":
                getWait().until(ExpectedConditions.elementToBeClickable(Configuration));
                Configuration.click();
                getWait().until(ExpectedConditions.urlContains("configuration"));
                break;
            case "Location":
                getWait().until(ExpectedConditions.elementToBeClickable(Location));
                Location.click();
                getWait().until(ExpectedConditions.urlContains("location"));
                break;
            case "POAttributes":
                getWait().until(ExpectedConditions.elementToBeClickable(POAttributes));
                POAttributes.click();
                getWait().until(ExpectedConditions.urlContains("poattribute"));
                break;
            case "ContainerInquiry":
                getWait().until(ExpectedConditions.elementToBeClickable(ContainerInquiry));
                ContainerInquiry.click();
                getWait().until(ExpectedConditions.urlContains("containerInquiry"));
                break;
            case "InventoryInquiry":
                getWait().until(ExpectedConditions.elementToBeClickable(InventoryInquiry));
                InventoryInquiry.click();
                getWait().until(ExpectedConditions.urlContains("inventoryInquiry"));
                break;
            case "InventoryAdjustmentInquiry":
                getWait().until(ExpectedConditions.elementToBeClickable(InventoryAdjustmentInquiry));
                InventoryAdjustmentInquiry.click();
                getWait().until(ExpectedConditions.urlContains("inventoryAdjustmentInquiry"));
                break;
            case "LocationView":
                getWait().until(ExpectedConditions.elementToBeClickable(LocationView));
                LocationView.click();
                getWait().until(ExpectedConditions.urlContains("locationView"));
                break;
            case "ManageActivities":
                getWait().until(ExpectedConditions.elementToBeClickable(ManageActivities));
                ManageActivities.click();
                getWait().until(ExpectedConditions.urlContains("WSM"));
                break;
            case "Dashboard":
                getWait().until(ExpectedConditions.elementToBeClickable(Dashboard));
                Dashboard.click();
                getWait().until(ExpectedConditions.urlContains("wsmdashboard"));
                break;
            case "WorkloadPlanning":
                getWait().until(ExpectedConditions.elementToBeClickable(WorkloadPlanning));
                WorkloadPlanning.click();
                getWait().until(ExpectedConditions.urlContains("waveui"));
                break;
            case "WavesinProgress":
                getWait().until(ExpectedConditions.elementToBeClickable(WavesinProgress));
                WavesinProgress.click();
                getWait().until(ExpectedConditions.urlContains("wavedashboard"));
                break;
            case "SwaggerDocumentation":
                getWait().until(ExpectedConditions.elementToBeClickable(SwaggerDocumentation));
                SwaggerDocumentation.click();
                getWait().until(ExpectedConditions.urlContains("swagger"));
                break;
            case "MheSearch":
                getWait().until(ExpectedConditions.elementToBeClickable(MheSearch));
                MheSearch.click();
                getWait().until(ExpectedConditions.urlContains("mhe"));
                break;
            case "MessagingSupport":
                getWait().until(ExpectedConditions.elementToBeClickable(messagingSupport));
                messagingSupport.click();
                getWait().until(ExpectedConditions.urlContains("messagingSupport"));
                break;
            case "Diagnostics":
                getWait().until(ExpectedConditions.elementToBeClickable(Diagnostics));
                Diagnostics.click();
                getWait().until(ExpectedConditions.urlContains("diagnostics"));
                break;
            case "ContainerSupport":
                getWait().until(ExpectedConditions.elementToBeClickable(ContainerSupport));
                ContainerSupport.click();
                getWait().until(ExpectedConditions.urlContains("containerSupport"));
                break;
            default:
                Assert.fail("Passed submenu value doesn't match, send correct value");
                break;
        }
    }

}
