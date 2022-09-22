package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class WmsLiteSubMenu extends BasePage implements SubMenu {

    @FindBy(xpath = "//span[text()='Order Selection & Printing']")
    public WebElement OrderSelectionPrinting;

    @FindBy(xpath = "//span[text()='Batch Inquiry']")
    public WebElement BatchInquiry;

    @FindBy(xpath = "//span[text()='Batch Detail Inquiry']")
    public WebElement BatchDetailInquiry;

    @FindBy(xpath = "//span[text()='Pick Productivity']")
    public WebElement PickProductivity;

    @FindBy(xpath = "//span[text()='Order Inquiry']")
    public WebElement OrderInquiry;

    @FindBy(xpath = "//span[text()='Order Detail Inquiry']")
    public WebElement OrderDetailInquiry;

    @FindBy(xpath = "//span[text()='Batch Logs']")
    public WebElement BatchLogs;

    @FindBy(xpath = "//span[text()='Unslotted UPCs for Orders']")
    public WebElement UnslottedUPCs;

    SubMenu subMenu;


    @Override
    public void clickOnGivenSubMenu(String selecetedSubMenu) {

        switch (selecetedSubMenu){
            case "OrderSelectionPrinting":
                getWait().until(ExpectedConditions.elementToBeClickable(OrderSelectionPrinting));
             //   OrderSelectionPrinting.click();
            //    boolean b=OrderSelectionPrinting.isDisplayed();
                getWait().until(ExpectedConditions.urlContains("order-selection"));
//
                subMenu = new WmsLiteSubMenu();
                ((WmsLiteSubMenu) subMenu).getWait().until(ExpectedConditions.elementToBeClickable(((WmsLiteSubMenu) subMenu).OrderSelectionPrinting  ));
               try {
                   Thread.sleep(3000);
               }catch (InterruptedException e){

               }

                OrderSelectionPrinting.click();

                break;
            case "BatchInquiry":
                getWait().until(ExpectedConditions.elementToBeClickable(BatchInquiry));
                BatchInquiry.click();
                getWait().until(ExpectedConditions.urlContains("batch-inquiry"));
                break;
            case "BatchDetailInquiry":
                getWait().until(ExpectedConditions.elementToBeClickable(BatchDetailInquiry));
                BatchDetailInquiry.click();
                getWait().until(ExpectedConditions.urlContains("batch-detail-inquiry"));
                break;
            default:
                Assert.fail("Passed submenu value doesn't match, send correct value");
                break;
        }

    }
}
