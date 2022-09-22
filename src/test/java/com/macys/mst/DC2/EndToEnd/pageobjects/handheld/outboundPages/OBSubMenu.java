package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.outboundPages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.SubMenu;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class OBSubMenu extends BasePage implements SubMenu {

    @FindBy(linkText = "Dock Scan")
    public WebElement dockScan;

    @FindBy(linkText = "Untie")
    public WebElement untie;

    @FindBy(linkText = "Load Inquiry")
    public WebElement loadInquiry;

    @FindBy(xpath = "//span[text()='Back']")
    public WebElement backButton;

    @FindBy(xpath = "//span[text()='Exit']")
    public WebElement exitButton;

    @Override
    public void clickOnGivenSubMenu(String selecetedSubMenu) {

        switch (selecetedSubMenu) {
            case "DockScan":
                dockScan.click();
                getWait().until(ExpectedConditions.urlContains("dockScan/doorScan"));
                break;
            case "Untie":
                untie.click();
                getWait().until(ExpectedConditions.urlContains("dockScan/untie"));
                break;
            case "LoadInquiry":
                loadInquiry.click();
                getWait().until(ExpectedConditions.urlContains("loadInquiry"));
                break;
            default:
                Assert.fail("Passed submenu value doesn't match, send correct value");
                break;
        }
    }

    @Override
    public void back() {
        backButton.click();
    }

    @Override
    public void exit() {
        exitButton.click();
    }
}
