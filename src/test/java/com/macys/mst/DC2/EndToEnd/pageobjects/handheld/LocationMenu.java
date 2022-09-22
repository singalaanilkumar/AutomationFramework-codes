package com.macys.mst.DC2.EndToEnd.pageobjects.handheld;


import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LocationMenu extends BasePage {

    @FindBy(linkText = "TULSA")
    public WebElement tulsa;

    @FindBy(linkText = "GOODYEAR")
    public WebElement goodYear;

    @FindBy(linkText = "PORTLAND")
    public WebElement portLand;

    @FindBy(linkText = "MARTINSBURG (WV)")
    public WebElement martinBurg;

    @FindBy(linkText = "COLUMBUS DC")
    public WebElement columbusDC;

    @FindBy(xpath = "//span[text()='Back']")
    public WebElement backButton;

    @FindBy(xpath = "//span[text()='Exit']")
    public WebElement exitButton;

    public void clickGoodyear() {
        goodYear.click();
    }
    public void validateGoodYear() {
        getWait().until(ExpectedConditions.elementToBeClickable(goodYear));
        Assert.assertEquals(true, goodYear.isDisplayed());
        CommonUtils.doJbehavereportConsolelogAndAssertion("Location menu displayed ", "Success", true);
    }

    public void selectGivenLocation(String location) {
        getWait().until(ExpectedConditions.visibilityOf(columbusDC));
        boolean isValid = false;
        if ("TULSA".equalsIgnoreCase(location)) {
            tulsa.click();
        } else if ("PORTLAND".equalsIgnoreCase(location)) {
            portLand.click();
        } else if ("MARTINSBURG".equalsIgnoreCase(location)) {
            martinBurg.click();
        } else if ("GOODYEAR".equalsIgnoreCase(location)) {
            goodYear.click();
        } else if ("COLUMBUSDC".equalsIgnoreCase(location)) {
            columbusDC.click();
        }else {
            Assert.fail("No such option available in Location menu");
        }

    }
}

