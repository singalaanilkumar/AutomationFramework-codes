package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PODistroPage extends BasePage {
    Logger log = Logger.getLogger(PODetailsPage.class);

    @FindBy(xpath = "//*[@id='breadcrumbContainer']//*[text()='PO Inquiry']")
    public WebElement poinquirybutton;

    public void navigateToInquiry() {
        scrollToTop();
        getWait().until(ExpectedConditions.elementToBeClickable(poinquirybutton)).click();
    }
}

