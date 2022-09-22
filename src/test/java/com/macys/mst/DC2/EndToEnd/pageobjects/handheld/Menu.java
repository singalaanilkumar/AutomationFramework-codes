package com.macys.mst.DC2.EndToEnd.pageobjects.handheld;


import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Menu extends BasePage {
    @FindBy(linkText = "DC2.0 RF Options")
    public WebElement RFOption;

    @FindBy(linkText = "HAF RF Options")
    public WebElement HAFOption;

    @FindBy(linkText = "Outbound")
    public WebElement Outbound;

    @FindBy(xpath = "//span[text()='Back']")
    public WebElement backButton;

    @FindBy(xpath = "//span[text()='Exit']")
    public WebElement exitButton;

    /*the below two elements(ASN & Tote) only for the purpose of detecting their presence
    should not use for performing any action, for that refer the
    corresponding submenu page*/
    @FindBy(linkText = "Associate ASN")
    public WebElement associateASN;

    @FindBy(linkText = "Create Tote")
    public WebElement createTote;

    @FindBy(linkText = "Dock Scan")
    public WebElement dockScan;

    public void validateRFOption() {
        getWait().until(ExpectedConditions.elementToBeClickable(RFOption));
        Assert.assertEquals(true, RFOption.isDisplayed());
    }

    public void validateHAFOption() {
        getWait().until(ExpectedConditions.elementToBeClickable(HAFOption));
        Assert.assertEquals(true, HAFOption.isDisplayed());
    }

    public void validateOutboundOption() {
        getWait().until(ExpectedConditions.elementToBeClickable(Outbound));
        Assert.assertEquals(true, Outbound.isDisplayed());
    }
}
