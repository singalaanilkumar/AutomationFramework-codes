package com.macys.mst.DC2.EndToEnd.pageobjects.handheld;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class LocateContainerPage extends BasePage {
    CommonUtils commonUtils = new CommonUtils();

    public LocateContainerPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "entryBox")
    WebElement scanContainertxtbox;

    @FindBy(xpath = "//*[text()='Locate Container']")
    WebElement locateContainer;

    @FindBy(xpath = "//*[@type='text']")
    WebElement statingLoctionEntrybox;

    @FindBy(xpath = "//SPAN[text()='Exit']")
    WebElement exitButton;

    public void navigateTolocateContainer() {
        getWait(10).until(ExpectedConditions.elementToBeClickable(locateContainer));
        locateContainer.click();
    }

    public void scanContainerBarcode(String containerbarcode) {
        getWait(10).until(ExpectedConditions.elementToBeClickable(scanContainertxtbox));
        scanContainertxtbox.click();
        scanContainertxtbox.sendKeys(containerbarcode);
        scanContainertxtbox.sendKeys(Keys.ENTER);
    }

    public void scanStaingLocation(String location) {
        getWait(10).until(ExpectedConditions.elementToBeClickable(statingLoctionEntrybox));
        statingLoctionEntrybox.click();
        statingLoctionEntrybox.sendKeys(location);
        statingLoctionEntrybox.sendKeys(Keys.ENTER);
    }

    public void clickButton(String button) {
        if (button.equalsIgnoreCase("exit")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(exitButton));
            exitButton.click();
        }
    }
}
