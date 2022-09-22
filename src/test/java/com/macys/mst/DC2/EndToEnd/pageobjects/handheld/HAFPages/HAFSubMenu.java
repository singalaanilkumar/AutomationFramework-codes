package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.HAFPages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.SubMenu;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HAFSubMenu extends BasePage implements SubMenu {

    @FindBy(linkText = "Associate ASN")
    public WebElement associateASN;

    @FindBy(linkText = "Picking")
    public WebElement picking;

    @FindBy(linkText = "PreSort")
    public WebElement preSort;

    @FindBy(linkText = "Container Inquiry")
    public WebElement containerInquiry;

    @FindBy(xpath = "//span[text()='Back']")
    public WebElement backButton;

    @FindBy(linkText = "Manual ASN")
    public WebElement ManualASN;

    @FindBy(xpath = "//span[text()='Exit']")
    public WebElement exitButton;

    @Override
    public void clickOnGivenSubMenu(String selecetedSubMenu) {

        switch (selecetedSubMenu) {
            case "Associate_ASN":
                associateASN.click();
                getWait().until(ExpectedConditions.urlContains("associate-asn"));
                break;
            case "Picking":
                picking.click();
                getWait().until(ExpectedConditions.urlContains("picking"));
                break;
            case "PreSort":
                preSort.click();
                getWait().until(ExpectedConditions.urlContains("presort"));
                break;
            case "Container_Inquiry":
                containerInquiry.click();
                getWait().until(ExpectedConditions.urlContains("container-inquiry"));
                break;
            case "Manual ASN":
                ManualASN.click();
                getWait().until(ExpectedConditions.urlContains("manualasn"));
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
